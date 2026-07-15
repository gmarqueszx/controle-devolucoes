import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatNativeDateModule } from '@angular/material/core';
import { NgxChartsModule } from '@swimlane/ngx-charts';
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
  stats: DashboardStats | null = null;
  topEquipes: SerieBarra[] = [];
  tendencia: SerieLinha[] = [];

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
