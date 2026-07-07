import { CommonModule } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { Lancamento } from '../../core/models/lancamento.model';

@Component({
  selector: 'app-lancamento-detalhe',
  standalone: true,
  imports: [CommonModule, MatDialogModule],
  templateUrl: './lancamento-detalhe.component.html',
  styleUrl: './lancamento-detalhe.component.scss'
})
export class LancamentoDetalheComponent {
  constructor(@Inject(MAT_DIALOG_DATA) public lancamento: Lancamento) {}
}
