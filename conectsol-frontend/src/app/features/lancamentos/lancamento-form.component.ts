import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { Observable, map, startWith } from 'rxjs';
import { Equipe } from '../../core/models/equipe.model';
import { AuthService } from '../../core/services/auth.service';
import { EquipeService } from '../../core/services/equipe.service';
import { LancamentoService } from '../../core/services/lancamento.service';

@Component({
  selector: 'app-lancamento-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatAutocompleteModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule
  ],
  templateUrl: './lancamento-form.component.html',
  styleUrl: './lancamento-form.component.scss'
})
export class LancamentoFormComponent implements OnInit {
  equipes: Equipe[] = [];
  equipesFiltradas$: Observable<Equipe[]> = new Observable<Equipe[]>();

  form = this.fb.group({
    equipe: [null as Equipe | string | null, [Validators.required, this.equipeValidator()]],
    dataLancamento: [new Date(), [Validators.required]],
    cliente: [''],
    sistemas: [1, [Validators.required, Validators.min(1)]],
    observacoes: ['']
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly equipeService: EquipeService,
    private readonly lancamentoService: LancamentoService,
    private readonly authService: AuthService,
    private readonly snackBar: MatSnackBar,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.equipeService.listar().subscribe((equipes) => {
      this.equipes = equipes;
    });

    this.equipesFiltradas$ = this.form.controls.equipe.valueChanges.pipe(
      startWith(''),
      map((valor) => this.filtrarEquipes(valor))
    );
  }

  exibirEquipe = (equipe: Equipe | string | null): string => {
    if (!equipe) {
      return '';
    }
    return typeof equipe === 'string' ? equipe : equipe.montador;
  };

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const valores = this.form.getRawValue();
    const equipe = valores.equipe as Equipe;

    this.lancamentoService
      .criar({
        equipeId: equipe.id,
        dataLancamento: this.paraIso(valores.dataLancamento!),
        cliente: valores.cliente ?? '',
        sistemas: valores.sistemas!,
        observacoes: valores.observacoes ?? '',
        criadoPor: this.authService.getUsuario()?.nome ?? ''
      })
      .subscribe({
        next: () => {
          this.snackBar.open('Lançamento criado com sucesso', 'Fechar', { duration: 3000 });
          this.router.navigate(['/lancamentos']);
        },
        error: () => this.snackBar.open('Erro ao criar lançamento', 'Fechar', { duration: 3000 })
      });
  }

  private filtrarEquipes(valor: Equipe | string | null): Equipe[] {
    const texto = (typeof valor === 'string' ? valor : valor?.montador ?? '').toLowerCase();
    return this.equipes.filter((equipe) => equipe.montador.toLowerCase().includes(texto));
  }

  private equipeValidator() {
    return (control: { value: Equipe | string | null }) => {
      return control.value && typeof control.value === 'object' ? null : { equipeInvalida: true };
    };
  }

  private paraIso(data: Date): string {
    return data.toISOString().substring(0, 10);
  }
}
