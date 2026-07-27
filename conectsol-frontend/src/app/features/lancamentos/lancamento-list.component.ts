import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { RouterLink } from '@angular/router';
import { Equipe } from '../../core/models/equipe.model';
import { Lancamento } from '../../core/models/lancamento.model';
import { AuthService } from '../../core/services/auth.service';
import { EquipeService } from '../../core/services/equipe.service';
import { LancamentoService } from '../../core/services/lancamento.service';
import { EquipeNomePipe } from '../../shared/pipes/equipe-nome.pipe';
import { LancamentoDetalheComponent } from './lancamento-detalhe.component';

@Component({
  selector: 'app-lancamento-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatTableModule,
    MatSortModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    EquipeNomePipe
  ],
  templateUrl: './lancamento-list.component.html',
  styleUrl: './lancamento-list.component.scss'
})
export class LancamentoListComponent implements OnInit, AfterViewInit {
  displayedColumns = [
    'dataLancamento',
    'montador',
    'eletricista',
    'cliente',
    'sistemas',
    'solo',
    'strings',
    'aproveitamento',
    'acoes'
  ];
  dataSource = new MatTableDataSource<Lancamento>([]);
  equipes: Equipe[] = [];

  @ViewChild(MatSort) sort!: MatSort;

  filtroForm = this.fb.group({
    start: this.inicioPeriodoPadrao(),
    end: new Date(),
    equipeId: null as number | null,
    busca: ''
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly lancamentoService: LancamentoService,
    private readonly equipeService: EquipeService,
    private readonly authService: AuthService,
    private readonly snackBar: MatSnackBar,
    private readonly dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.equipeService.listar().subscribe((equipes) => (this.equipes = equipes));
    this.dataSource.filterPredicate = (item, filtro) => {
      const alvo = this.normalizar(
        `${item.cliente ?? ''} ${item.montador ?? ''} ${item.eletricista ?? ''} ${item.ajudante ?? ''}`
      );
      return alvo.includes(filtro);
    };
    this.filtroForm.get('busca')!.valueChanges.subscribe((busca) => {
      this.dataSource.filter = this.normalizar(busca ?? '');
    });
    this.carregar();
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
  }

  get isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  aoMudarFiltro(): void {
    this.carregar();
  }

  verDetalhes(lancamento: Lancamento): void {
    this.dialog.open(LancamentoDetalheComponent, { data: lancamento, width: '520px' });
  }

  excluir(lancamento: Lancamento): void {
    this.lancamentoService.excluir(lancamento.id).subscribe({
      next: () => {
        this.snackBar.open('Lançamento excluído com sucesso', 'Fechar', { duration: 3000 });
        this.carregar();
      },
      error: () => this.snackBar.open('Erro ao excluir lançamento', 'Fechar', { duration: 3000 })
    });
  }

  private carregar(): void {
    const { start, end, equipeId } = this.filtroForm.getRawValue();
    if (!start || !end) {
      return;
    }
    this.lancamentoService
      .listar(this.paraIso(start), this.paraIso(end), equipeId ?? undefined)
      .subscribe((lancamentos) => (this.dataSource.data = lancamentos));
  }

  private inicioPeriodoPadrao(): Date {
    const hoje = new Date();
    return new Date(hoje.getFullYear(), hoje.getMonth() - 2, 1);
  }

  private paraIso(data: Date): string {
    return data.toISOString().substring(0, 10);
  }

  private normalizar(texto: string): string {
    return texto
      .trim()
      .toLowerCase()
      .normalize('NFD')
      .replace(/[̀-ͯ]/g, '');
  }
}
