import { CommonModule } from '@angular/common';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatNativeDateModule } from '@angular/material/core';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';
import { RelatorioService } from '../../core/services/relatorio.service';
import { DashboardStats, TendenciaMensal } from '../../core/models/relatorio.model';

interface SerieBarra {
  name: string;
  value: number;
}

interface SerieLinha {
  name: string;
  series: SerieBarra[];
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    NgxChartsModule
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  @ViewChild('blocoKpis') blocoKpis?: ElementRef<HTMLElement>;
  @ViewChild('blocoRanking') blocoRanking?: ElementRef<HTMLElement>;
  @ViewChild('graficoTendencia', { read: ElementRef }) graficoTendencia?: ElementRef<HTMLElement>;

  stats: DashboardStats | null = null;
  topEquipes: SerieBarra[] = [];
  tendencia: SerieLinha[] = [];
  exportandoPdf = false;

  periodoForm = this.fb.group({
    start: this.inicioPeriodoPadrao(),
    end: new Date()
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly relatorioService: RelatorioService
  ) {}

  ngOnInit(): void {
    this.carregarStats();
    this.carregarTendencia();
    this.carregarTopEquipes();
  }

  aoMudarPeriodo(): void {
    this.carregarStats();
    this.carregarTopEquipes();
  }

  larguraBarra(valor: number): number {
    const maiorValor = this.topEquipes[0]?.value ?? 0;
    if (maiorValor <= 0) {
      return 0;
    }
    return Math.max((valor / maiorValor) * 100, 18);
  }

  async exportarPdf(): Promise<void> {
    if (this.exportandoPdf) {
      return;
    }
    this.exportandoPdf = true;
    try {
      const pdf = new jsPDF({ orientation: 'portrait', unit: 'mm', format: 'a4' });
      const margem = 10;
      const alturaPagina = pdf.internal.pageSize.getHeight();
      const larguraUtil = pdf.internal.pageSize.getWidth() - margem * 2;
      let cursorY = margem;

      pdf.setFontSize(14);
      pdf.text('Conectsol — Relatório de Sobras e Alertas', margem, cursorY);
      cursorY += 7;

      pdf.setFontSize(10);
      pdf.setTextColor(100);
      const inicio = this.periodoForm.controls.start.value;
      const fim = this.periodoForm.controls.end.value;
      pdf.text(`Período: ${this.formatarDataBr(inicio)} a ${this.formatarDataBr(fim)}`, margem, cursorY);
      pdf.setTextColor(0);
      cursorY += 8;

      if (this.blocoKpis) {
        cursorY = await this.adicionarBlocoHtml(pdf, this.blocoKpis.nativeElement, margem, cursorY, larguraUtil, alturaPagina);
        cursorY += 6;
      }

      if (this.blocoRanking) {
        pdf.setFontSize(12);
        pdf.text('Top 10 equipes por pontos de alerta', margem, cursorY);
        cursorY += 6;
        cursorY = await this.adicionarBlocoHtml(pdf, this.blocoRanking.nativeElement, margem, cursorY, larguraUtil, alturaPagina);
        cursorY += 6;
      }

      const svgTendencia = this.graficoTendencia?.nativeElement.querySelector('svg');
      if (svgTendencia) {
        if (cursorY + 10 > alturaPagina - margem) {
          pdf.addPage();
          cursorY = margem;
        }
        pdf.setFontSize(12);
        pdf.text('Tendência dos últimos 6 meses', margem, cursorY);
        cursorY += 6;
        cursorY = await this.adicionarSvgComoImagem(pdf, svgTendencia, margem, cursorY, larguraUtil, alturaPagina);
      }

      const data = new Date().toISOString().substring(0, 10);
      pdf.save(`relatorio-conectsol-${data}.pdf`);
    } finally {
      this.exportandoPdf = false;
    }
  }

  private async adicionarBlocoHtml(
    pdf: jsPDF,
    elemento: HTMLElement,
    margemX: number,
    y: number,
    larguraUtil: number,
    alturaPagina: number
  ): Promise<number> {
    const canvas = await html2canvas(elemento, { scale: 1.5, backgroundColor: '#ffffff' });
    const alturaImagem = (canvas.height * larguraUtil) / canvas.width;

    let cursorY = y;
    if (cursorY + alturaImagem > alturaPagina - margemX) {
      pdf.addPage();
      cursorY = margemX;
    }
    pdf.addImage(canvas.toDataURL('image/jpeg', 0.92), 'JPEG', margemX, cursorY, larguraUtil, alturaImagem);
    return cursorY + alturaImagem;
  }

  // html2canvas nao rasteriza bem SVG complexo (o grafico de tendencia, gerado pelo ngx-charts/d3, sai em
  // branco), entao esse SVG e rasterizado a parte: clona-lo com o estilo computado de cada elemento gravado
  // como atributo inline (necessario porque o SVG serializado sozinho nao enxerga o CSS global da pagina) e
  // desenhado num canvas dedicado do tamanho dele mesmo — sem tentar alinhar coordenadas com o canvas do
  // html2canvas, que e a fonte da instabilidade de posicionamento.
  private async adicionarSvgComoImagem(
    pdf: jsPDF,
    svgOriginal: SVGSVGElement,
    margemX: number,
    y: number,
    larguraUtil: number,
    alturaPagina: number
  ): Promise<number> {
    const rect = svgOriginal.getBoundingClientRect();
    if (rect.width === 0 || rect.height === 0) {
      return y;
    }

    const propriedades = [
      'fill',
      'fill-opacity',
      'stroke',
      'stroke-width',
      'stroke-opacity',
      'stroke-dasharray',
      'stop-color',
      'stop-opacity',
      'font-family',
      'font-size',
      'font-weight',
      'opacity',
      'color',
      'text-anchor'
    ];

    const clone = svgOriginal.cloneNode(true) as SVGSVGElement;
    const originais = svgOriginal.querySelectorAll('*');
    const clones = clone.querySelectorAll('*');
    originais.forEach((original, indice) => {
      const estiloComputado = getComputedStyle(original);
      const estiloInline = propriedades
        .map((propriedade) => `${propriedade}:${estiloComputado.getPropertyValue(propriedade)}`)
        .join(';');
      clones[indice]?.setAttribute('style', estiloInline);
    });
    clone.setAttribute('width', `${rect.width}`);
    clone.setAttribute('height', `${rect.height}`);

    const svgString = new XMLSerializer().serializeToString(clone);
    const svgDataUrl = 'data:image/svg+xml;base64,' + btoa(unescape(encodeURIComponent(svgString)));

    const escala = 2;
    const canvas = document.createElement('canvas');
    canvas.width = rect.width * escala;
    canvas.height = rect.height * escala;
    const ctx = canvas.getContext('2d');
    if (!ctx) {
      return y;
    }
    ctx.fillStyle = '#ffffff';
    ctx.fillRect(0, 0, canvas.width, canvas.height);

    const imagem = new Image();
    await new Promise<void>((resolve) => {
      imagem.onload = () => resolve();
      imagem.onerror = () => resolve();
      imagem.src = svgDataUrl;
    });
    ctx.drawImage(imagem, 0, 0, canvas.width, canvas.height);

    const alturaImagem = (canvas.height * larguraUtil) / canvas.width;
    let cursorY = y;
    if (cursorY + alturaImagem > alturaPagina - margemX) {
      pdf.addPage();
      cursorY = margemX;
    }
    pdf.addImage(canvas.toDataURL('image/jpeg', 0.92), 'JPEG', margemX, cursorY, larguraUtil, alturaImagem);
    return cursorY + alturaImagem;
  }

  private formatarDataBr(data: Date | null): string {
    if (!data) {
      return '—';
    }
    return data.toLocaleDateString('pt-BR');
  }

  private carregarStats(): void {
    const inicio = this.periodoForm.controls.start.value;
    const fim = this.periodoForm.controls.end.value;
    if (!inicio || !fim) {
      return;
    }
    this.relatorioService
      .dashboardStats(this.paraIso(inicio), this.paraIso(fim))
      .subscribe((stats) => (this.stats = stats));
  }

  private carregarTendencia(): void {
    this.relatorioService.tendencia(6).subscribe((tendencia: TendenciaMensal[]) => {
      this.tendencia = [
        { name: 'Alto', series: tendencia.map((t) => ({ name: t.mes, value: t.alto })) },
        { name: 'Médio', series: tendencia.map((t) => ({ name: t.mes, value: t.medio })) },
        { name: 'Leve', series: tendencia.map((t) => ({ name: t.mes, value: t.leve })) }
      ];
    });
  }

  private carregarTopEquipes(): void {
    const inicio = this.periodoForm.controls.start.value;
    const fim = this.periodoForm.controls.end.value;
    if (!inicio || !fim) {
      return;
    }

    this.relatorioService.relatorioEquipes(this.paraIso(inicio), this.paraIso(fim)).subscribe((relatorio) => {
      this.topEquipes = relatorio
        .slice()
        .sort((a, b) => b.pontos - a.pontos)
        .slice(0, 10)
        .map((r) => ({
          name: [r.montador, r.eletricista, r.ajudante].filter(Boolean).join(' / '),
          value: r.pontos
        }));
    });
  }

  private inicioPeriodoPadrao(): Date {
    const hoje = new Date();
    return new Date(hoje.getFullYear(), hoje.getMonth() - 2, 1);
  }

  private paraIso(data: Date): string {
    return data.toISOString().substring(0, 10);
  }
}
