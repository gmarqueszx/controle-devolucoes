import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { Equipe } from '../../core/models/equipe.model';
import { EquipeService } from '../../core/services/equipe.service';
import { EquipeFormComponent } from './equipe-form.component';

@Component({
  selector: 'app-equipe-list',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatTableModule, MatButtonModule, MatIconModule, MatDialogModule],
  templateUrl: './equipe-list.component.html',
  styleUrl: './equipe-list.component.scss'
})
export class EquipeListComponent implements OnInit {
  displayedColumns = ['montador', 'eletricista', 'ativa', 'acoes'];
  equipes: Equipe[] = [];

  constructor(
    private readonly equipeService: EquipeService,
    private readonly dialog: MatDialog,
    private readonly snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.carregar();
  }

  novaEquipe(): void {
    const dialogRef = this.dialog.open(EquipeFormComponent, { data: null });
    dialogRef.afterClosed().subscribe((request) => {
      if (!request) {
        return;
      }
      this.equipeService.criar(request).subscribe({
        next: () => {
          this.snackBar.open('Equipe criada com sucesso', 'Fechar', { duration: 3000 });
          this.carregar();
        },
        error: () => this.snackBar.open('Erro ao criar equipe', 'Fechar', { duration: 3000 })
      });
    });
  }

  editarEquipe(equipe: Equipe): void {
    const dialogRef = this.dialog.open(EquipeFormComponent, { data: equipe });
    dialogRef.afterClosed().subscribe((request) => {
      if (!request) {
        return;
      }
      this.equipeService.atualizar(equipe.id, request).subscribe({
        next: () => {
          this.snackBar.open('Equipe atualizada com sucesso', 'Fechar', { duration: 3000 });
          this.carregar();
        },
        error: () => this.snackBar.open('Erro ao atualizar equipe', 'Fechar', { duration: 3000 })
      });
    });
  }

  excluirEquipe(equipe: Equipe): void {
    this.equipeService.excluir(equipe.id).subscribe({
      next: () => {
        this.snackBar.open('Equipe desativada com sucesso', 'Fechar', { duration: 3000 });
        this.carregar();
      },
      error: () => this.snackBar.open('Erro ao desativar equipe', 'Fechar', { duration: 3000 })
    });
  }

  private carregar(): void {
    this.equipeService.listar().subscribe((equipes) => (this.equipes = equipes));
  }
}
