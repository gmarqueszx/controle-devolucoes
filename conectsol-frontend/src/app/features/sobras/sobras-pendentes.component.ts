import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { RouterLink } from '@angular/router';
import { Equipe } from '../../core/models/equipe.model';
import { Lancamento } from '../../core/models/lancamento.model';
import { EquipeService } from '../../core/services/equipe.service';
import { LancamentoService } from '../../core/services/lancamento.service';
import { EquipeNomePipe } from '../../shared/pipes/equipe-nome.pipe';

@Component({
  selector: 'app-sobras-pendentes',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatTableModule,
    MatSortModule,
    MatButtonModule,
    MatIconModule,
    EquipeNomePipe
  ],
  templateUrl: './sobras-pendentes.component.html',
  styleUrl: './sobras-pendentes.component.scss'
})
export class SobrasPendentesComponent implements OnInit, AfterViewInit {
  displayedColumns = ['cliente', 'montador', 'cidadeSobra', 'dataLancamento', 'diasParado', 'localizacaoSobra', 'acoes'];
  dataSource = new MatTableDataSource<Lancamento>([]);
  equipes: Equipe[] = [];

  @ViewChild(MatSort) sort!: MatSort;

  filtroForm = this.fb.group({
    equipeId: null as number | null,
    busca: ''
  });

  private todasSobras: Lancamento[] = [];

  constructor(
    private readonly fb: FormBuilder,
    private readonly lancamentoService: LancamentoService,
    private readonly equipeService: EquipeService
  ) {}

  ngOnInit(): void {
    this.equipeService.listar().subscribe((equipes) => (this.equipes = equipes));
    this.filtroForm.valueChanges.subscribe(() => this.aplicarFiltros());
    this.carregar();
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
  }

  private carregar(): void {
    this.lancamentoService.listarSobrasPendentes().subscribe((sobras) => {
      this.todasSobras = sobras.slice().sort((a, b) => (b.diasParado ?? 0) - (a.diasParado ?? 0));
      this.aplicarFiltros();
    });
  }

  private aplicarFiltros(): void {
    const { equipeId, busca } = this.filtroForm.getRawValue();
    const buscaNormalizada = this.normalizar(busca ?? '');
    this.dataSource.data = this.todasSobras.filter((item) => {
      const combinaEquipe = !equipeId || item.equipeId === equipeId;
      const combinaBusca =
        !buscaNormalizada ||
        this.normalizar(
          `${item.cliente ?? ''} ${item.montador ?? ''} ${item.cidadeSobra ?? ''} ${item.localizacaoSobra ?? ''}`
        ).includes(buscaNormalizada);
      return combinaEquipe && combinaBusca;
    });
  }

  private normalizar(texto: string): string {
    return texto
      .trim()
      .toLowerCase()
      .normalize('NFD')
      .replace(/[̀-ͯ]/g, '');
  }
}
