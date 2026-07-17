import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { RelatorioColaborador, RelatorioEquipe } from '../../core/models/relatorio.model';
import { RelatorioService } from '../../core/services/relatorio.service';

@Component({
  selector: 'app-relatorio-equipes',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatTableModule,
    MatSortModule,
    MatTabsModule
  ],
  templateUrl: './relatorio-equipes.component.html',
  styleUrl: './relatorio-equipes.component.scss'
})
export class RelatorioEquipesComponent implements AfterViewInit {
  displayedColumns = [
    'montador',
    'eletricista',
    'alto',
    'medio',
    'leve',
    'totalAlertas',
    'pontos',
    'sistemas',
    'indice'
  ];
  displayedColumnsColaboradores = ['nome', 'alto', 'medio', 'leve', 'totalAlertas', 'pontos'];
  dataSource = new MatTableDataSource<RelatorioEquipe>([]);
  colaboradores: RelatorioColaborador[] = [];

  @ViewChild(MatSort) sort!: MatSort;

  periodoForm = this.fb.group({
    start: this.inicioPeriodoPadrao(),
    end: new Date()
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly relatorioService: RelatorioService
  ) {}

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
    this.carregarRelatorio();
  }

  aoMudarPeriodo(): void {
    this.carregarRelatorio();
  }

  classeLinha(item: { alto: number; medio: number }): string {
    if (item.alto > 0) {
      return 'linha-alto';
    }
    if (item.medio > 0) {
      return 'linha-medio';
    }
    return '';
  }

  private carregarRelatorio(): void {
    const inicio = this.periodoForm.controls.start.value;
    const fim = this.periodoForm.controls.end.value;
    if (!inicio || !fim) {
      return;
    }

    this.relatorioService
      .relatorioEquipes(this.paraIso(inicio), this.paraIso(fim))
      .subscribe((relatorio) => (this.dataSource.data = relatorio));

    this.relatorioService
      .relatorioColaboradores(this.paraIso(inicio), this.paraIso(fim))
      .subscribe((colaboradores) => {
        this.colaboradores = colaboradores.slice().sort((a, b) => b.pontos - a.pontos);
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
