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
  @ViewChild('areaExportavel') areaExportavel!: ElementRef<HTMLElement>;

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
      const elemento = this.areaExportavel.nativeElement;
      const canvas = await html2canvas(elemento, { scale: 2, backgroundColor: '#ffffff' });
      const imagem = canvas.toDataURL('image/png');

      const pdf = new jsPDF({ orientation: 'portrait', unit: 'mm', format: 'a4' });
      const margem = 10;
      const larguraUtil = pdf.internal.pageSize.getWidth() - margem * 2;
      const alturaUtilPagina = pdf.internal.pageSize.getHeight() - margem * 2;
      const alturaImagem = (canvas.height * larguraUtil) / canvas.width;

      let alturaRestante = alturaImagem;
      let deslocamentoY = 0;
      pdf.addImage(imagem, 'PNG', margem, margem, larguraUtil, alturaImagem);
      alturaRestante -= alturaUtilPagina;

      while (alturaRestante > 0) {
        deslocamentoY += alturaUtilPagina;
        pdf.addPage();
        pdf.addImage(imagem, 'PNG', margem, margem - deslocamentoY, larguraUtil, alturaImagem);
        alturaRestante -= alturaUtilPagina;
      }

      const data = new Date().toISOString().substring(0, 10);
      pdf.save(`relatorio-conectsol-${data}.pdf`);
    } finally {
      this.exportandoPdf = false;
    }
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
