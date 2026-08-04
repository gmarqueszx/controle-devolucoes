import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { RouterLink } from '@angular/router';
import * as L from 'leaflet';
import { CIDADES_BAHIA, CidadeBahia } from '../../core/data/cidades-bahia';
import { Lancamento } from '../../core/models/lancamento.model';
import { LancamentoService } from '../../core/services/lancamento.service';

interface SobraPorCidade {
  cidade: string;
  quantidade: number;
  coordenadas: CidadeBahia | null;
}

const BAHIA_CENTRO: L.LatLngExpression = [-12.6, -41.7];

@Component({
  selector: 'app-localizacao-sobras',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatTableModule,
    MatSortModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './localizacao-sobras.component.html',
  styleUrl: './localizacao-sobras.component.scss'
})
export class LocalizacaoSobrasComponent implements OnInit, AfterViewInit {
  @ViewChild('mapa') mapaRef!: ElementRef<HTMLDivElement>;
  @ViewChild(MatSort) sort!: MatSort;

  displayedColumns = ['cliente', 'cidadeSobra', 'diasParado', 'acoes'];
  dataSource = new MatTableDataSource<Lancamento>([]);
  cidadesResumo: SobraPorCidade[] = [];
  cidadeSelecionada: string | null = null;
  semCidade = 0;

  filtroForm = this.fb.group({
    busca: ''
  });

  private todasSobras: Lancamento[] = [];
  private mapa: L.Map | null = null;
  private marcadores: L.LayerGroup = L.layerGroup();
  private readonly cidadesPorNome = new Map<string, CidadeBahia>(
    CIDADES_BAHIA.map((c) => [this.normalizar(c.nome), c])
  );

  constructor(
    private readonly fb: FormBuilder,
    private readonly lancamentoService: LancamentoService
  ) {}

  ngOnInit(): void {
    this.filtroForm.valueChanges.subscribe(() => this.aplicarFiltros());
    this.carregar();
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
    this.inicializarMapa();
  }

  selecionarCidade(cidade: string): void {
    this.cidadeSelecionada = this.cidadeSelecionada === cidade ? null : cidade;
    this.aplicarFiltros();
  }

  limparSelecao(): void {
    this.cidadeSelecionada = null;
    this.aplicarFiltros();
  }

  private carregar(): void {
    this.lancamentoService.listarSobrasPendentes().subscribe((sobras) => {
      this.todasSobras = sobras.slice().sort((a, b) => (b.diasParado ?? 0) - (a.diasParado ?? 0));
      this.montarResumoPorCidade();
      this.desenharMarcadores();
      this.aplicarFiltros();
    });
  }

  private montarResumoPorCidade(): void {
    const contagem = new Map<string, number>();
    let semCidade = 0;

    for (const sobra of this.todasSobras) {
      const cidade = sobra.cidadeSobra?.trim();
      if (!cidade) {
        semCidade++;
        continue;
      }
      contagem.set(cidade, (contagem.get(cidade) ?? 0) + 1);
    }

    this.semCidade = semCidade;
    this.cidadesResumo = Array.from(contagem.entries())
      .map(([cidade, quantidade]) => ({
        cidade,
        quantidade,
        coordenadas: this.cidadesPorNome.get(this.normalizar(cidade)) ?? null
      }))
      .sort((a, b) => b.quantidade - a.quantidade);
  }

  private aplicarFiltros(): void {
    const { busca } = this.filtroForm.getRawValue();
    const buscaNormalizada = this.normalizar(busca ?? '');
    this.dataSource.data = this.todasSobras.filter((item) => {
      const combinaCidade = !this.cidadeSelecionada || item.cidadeSobra === this.cidadeSelecionada;
      const combinaBusca =
        !buscaNormalizada ||
        this.normalizar(`${item.cliente ?? ''} ${item.cidadeSobra ?? ''}`).includes(buscaNormalizada);
      return combinaCidade && combinaBusca;
    });
  }

  private inicializarMapa(): void {
    if (!this.mapaRef?.nativeElement) {
      return;
    }
    this.mapa = L.map(this.mapaRef.nativeElement, {
      center: BAHIA_CENTRO,
      zoom: 6,
      scrollWheelZoom: false
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap',
      maxZoom: 18
    }).addTo(this.mapa);

    this.marcadores.addTo(this.mapa);
    this.desenharMarcadores();
  }

  private desenharMarcadores(): void {
    if (!this.mapa) {
      return;
    }
    this.marcadores.clearLayers();

    for (const item of this.cidadesResumo) {
      if (!item.coordenadas) {
        continue;
      }
      const tamanho = Math.min(56, 26 + item.quantidade * 4);
      const icone = L.divIcon({
        className: 'marcador-cidade',
        html: `<div class="bolha" style="width:${tamanho}px;height:${tamanho}px;">${item.quantidade}</div>`,
        iconSize: [tamanho, tamanho]
      });

      const marcador = L.marker([item.coordenadas.lat, item.coordenadas.lng], { icon: icone });
      marcador.bindTooltip(`${item.cidade}: ${item.quantidade} sobra(s) pendente(s)`);
      marcador.on('click', () => this.selecionarCidade(item.cidade));
      marcador.addTo(this.marcadores);
    }
  }

  private normalizar(texto: string): string {
    return texto
      .trim()
      .toLowerCase()
      .normalize('NFD')
      .replace(/[̀-ͯ]/g, '');
  }
}
