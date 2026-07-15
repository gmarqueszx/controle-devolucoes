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
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { Alerta } from '../../core/models/alerta.model';
import { Equipe } from '../../core/models/equipe.model';
import { MediaCaboUso } from '../../core/models/lancamento.model';
import { AlertaService } from '../../core/services/alerta.service';
import { AuthService } from '../../core/services/auth.service';
import { EquipeService } from '../../core/services/equipe.service';
import { LancamentoService } from '../../core/services/lancamento.service';
import { EquipeNomePipe } from '../../shared/pipes/equipe-nome.pipe';
import { NivelBadgeComponent } from '../../shared/components/nivel-badge.component';
import { AlertaFormComponent } from './alerta-form.component';
import { ConfirmarDesvioDialogComponent } from './confirmar-desvio-dialog.component';

@Component({
  selector: 'app-alerta-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatTableModule,
    MatPaginatorModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    NivelBadgeComponent,
    EquipeNomePipe
  ],
  templateUrl: './alerta-list.component.html',
  styleUrl: './alerta-list.component.scss'
})
export class AlertaListComponent implements OnInit, AfterViewInit {
  displayedColumns = ['dataAlerta', 'cliente', 'montador', 'eletricista', 'descricao', 'nivel', 'status', 'acoes'];
  dataSource = new MatTableDataSource<Alerta>([]);
  equipes: Equipe[] = [];
  medias: MediaCaboUso[] = [];

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  filtroForm = this.fb.group({
    start: this.inicioPeriodoPadrao(),
    end: new Date(),
    nivel: null as string | null,
    equipeId: null as number | null
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly alertaService: AlertaService,
    private readonly equipeService: EquipeService,
    private readonly lancamentoService: LancamentoService,
    private readonly authService: AuthService,
    private readonly dialog: MatDialog,
    private readonly snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.equipeService.listar().subscribe((equipes) => (this.equipes = equipes));
    this.lancamentoService.consultarMediasUso().subscribe((medias) => (this.medias = medias));
    this.carregar();
  }

  get isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  recalcularAlertas(): void {
    this.lancamentoService.recalcularAlertas().subscribe({
      next: () => {
        this.snackBar.open('Alertas recalculados com sucesso', 'Fechar', { duration: 3000 });
        this.carregar();
      },
      error: () => this.snackBar.open('Erro ao recalcular alertas', 'Fechar', { duration: 3000 })
    });
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
  }

  aoMudarFiltro(): void {
    this.carregar();
  }

  novoAlerta(): void {
    const dialogRef = this.dialog.open(AlertaFormComponent, { data: { equipes: this.equipes } });
    dialogRef.afterClosed().subscribe((request) => {
      if (!request) {
        return;
      }
      this.alertaService.criar(request).subscribe({
        next: (alerta) => {
          const mensagem = alerta ? 'Alerta registrado com sucesso' : 'Registro ignorado (sobra de galpão)';
          this.snackBar.open(mensagem, 'Fechar', { duration: 3000 });
          this.carregar();
        },
        error: () => this.snackBar.open('Erro ao registrar alerta', 'Fechar', { duration: 3000 })
      });
    });
  }

  atualizarStatus(alerta: Alerta, status: Alerta['status']): void {
    this.alertaService.atualizarStatus(alerta.id, status).subscribe({
      next: () => {
        this.snackBar.open('Status atualizado com sucesso', 'Fechar', { duration: 3000 });
        this.carregar();
      },
      error: () => this.snackBar.open('Erro ao atualizar status', 'Fechar', { duration: 3000 })
    });
  }

  podeConfirmarDesvio(alerta: Alerta): boolean {
    return alerta.nivel === 'MEDIO' && alerta.origem === 'CABO_ACIMA_MEDIA';
  }

  confirmarDesvio(alerta: Alerta): void {
    const dialogRef = this.dialog.open(ConfirmarDesvioDialogComponent);
    dialogRef.afterClosed().subscribe((request) => {
      if (!request) {
        return;
      }
      this.alertaService.confirmarDesvio(alerta.id, request).subscribe({
        next: () => {
          this.snackBar.open('Desvio confirmado, alerta marcado como Alto', 'Fechar', { duration: 3000 });
          this.carregar();
        },
        error: () => this.snackBar.open('Erro ao confirmar desvio', 'Fechar', { duration: 3000 })
      });
    });
  }

  private carregar(): void {
    const { start, end, nivel, equipeId } = this.filtroForm.getRawValue();
    if (!start || !end) {
      return;
    }
    this.alertaService
      .listar(this.paraIso(start), this.paraIso(end), nivel ?? undefined, equipeId ?? undefined)
      .subscribe((alertas) => (this.dataSource.data = alertas));
  }

  private inicioPeriodoPadrao(): Date {
    const hoje = new Date();
    return new Date(hoje.getFullYear(), hoje.getMonth() - 2, 1);
  }

  private paraIso(data: Date): string {
    return data.toISOString().substring(0, 10);
  }
}
