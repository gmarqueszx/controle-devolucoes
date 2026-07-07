import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
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
import { MatTableModule } from '@angular/material/table';
import { RouterLink } from '@angular/router';
import { Equipe } from '../../core/models/equipe.model';
import { Lancamento } from '../../core/models/lancamento.model';
import { AuthService } from '../../core/services/auth.service';
import { EquipeService } from '../../core/services/equipe.service';
import { LancamentoService } from '../../core/services/lancamento.service';
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
    MatButtonModule,
    MatIconModule,
    MatDialogModule
  ],
  templateUrl: './lancamento-list.component.html',
  styleUrl: './lancamento-list.component.scss'
})
export class LancamentoListComponent implements OnInit {
  displayedColumns = [
    'dataLancamento',
    'montador',
    'eletricista',
    'cliente',
    'sistemas',
    'telhado',
    'strings',
    'aproveitamento',
    'acoes'
  ];
  lancamentos: Lancamento[] = [];
  equipes: Equipe[] = [];

  filtroForm = this.fb.group({
    start: this.primeiroDiaDoMes(),
    end: new Date(),
    equipeId: null as number | null
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
    this.carregar();
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
      .subscribe((lancamentos) => (this.lancamentos = lancamentos));
  }

  private primeiroDiaDoMes(): Date {
    const hoje = new Date();
    return new Date(hoje.getFullYear(), hoje.getMonth(), 1);
  }

  private paraIso(data: Date): string {
    return data.toISOString().substring(0, 10);
  }
}
