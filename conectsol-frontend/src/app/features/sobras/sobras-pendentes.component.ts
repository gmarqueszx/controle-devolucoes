import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { RouterLink } from '@angular/router';
import { Lancamento } from '../../core/models/lancamento.model';
import { LancamentoService } from '../../core/services/lancamento.service';

@Component({
  selector: 'app-sobras-pendentes',
  standalone: true,
  imports: [CommonModule, RouterLink, MatCardModule, MatTableModule, MatButtonModule, MatIconModule],
  templateUrl: './sobras-pendentes.component.html',
  styleUrl: './sobras-pendentes.component.scss'
})
export class SobrasPendentesComponent implements OnInit {
  displayedColumns = ['cliente', 'montador', 'dataLancamento', 'diasParado', 'localizacaoSobra', 'acoes'];
  sobras: Lancamento[] = [];

  constructor(private readonly lancamentoService: LancamentoService) {}

  ngOnInit(): void {
    this.carregar();
  }

  private carregar(): void {
    this.lancamentoService.listarSobrasPendentes().subscribe((sobras) => {
      this.sobras = sobras.slice().sort((a, b) => (b.diasParado ?? 0) - (a.diasParado ?? 0));
    });
  }
}
