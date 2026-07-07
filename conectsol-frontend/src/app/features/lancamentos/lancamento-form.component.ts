import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { Observable, map, startWith } from 'rxjs';
import { Equipe } from '../../core/models/equipe.model';
import { AuthService } from '../../core/services/auth.service';
import { EquipeService } from '../../core/services/equipe.service';
import { LancamentoService } from '../../core/services/lancamento.service';

const TELHADOS = ['ZINCO', 'ETERNIT', 'QUADRADINHA', 'CASCA DE OVO', 'SOLO', 'FIBROCIMENTO', 'COLONIAL'];

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
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    MatCheckboxModule
  ],
  templateUrl: './lancamento-form.component.html',
  styleUrl: './lancamento-form.component.scss'
})
export class LancamentoFormComponent implements OnInit {
  equipes: Equipe[] = [];
  equipesFiltradas$: Observable<Equipe[]> = new Observable<Equipe[]>();
  telhados = TELHADOS;

  form = this.fb.group({
    equipe: [null as Equipe | string | null, [Validators.required, this.equipeValidator()]],
    dataLancamento: [new Date(), [Validators.required]],
    cliente: [''],
    sistemas: [1, [Validators.required, Validators.min(1)]],
    observacoes: [''],
    criadoPor: [''],

    retornou: [false],
    tipoSistema: ['PROJETO'],
    telhado: ['', [Validators.required]],
    placas: [0, [Validators.required, Validators.min(1)]],
    inversores: this.fb.array([this.criarInversorGroup()]),

    caboSolarVermDevolvido: [null as number | null],
    caboSolarPretoDevolvido: [null as number | null],
    caboHeprDevolvido: [null as number | null],

    qtdMateriaisEnviados: [null as number | null],
    qtdMateriaisDivergentes: [null as number | null],
    fotoSobrasGrupo: [false],
    ajusteFino: [null as number | null]
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

  get inversoresArray(): FormArray {
    return this.form.controls.inversores;
  }

  adicionarInversor(): void {
    this.inversoresArray.push(this.criarInversorGroup());
  }

  removerInversor(index: number): void {
    if (this.inversoresArray.length > 1) {
      this.inversoresArray.removeAt(index);
    }
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
        criadoPor: valores.criadoPor || this.authService.getUsuario()?.nome || '',
        retornou: valores.retornou ?? undefined,
        tipoSistema: valores.tipoSistema ?? undefined,
        telhado: valores.telhado ?? undefined,
        placas: valores.placas ?? undefined,
        inversores: valores.inversores.map((i) => ({ kw: i.kw!, quantidade: i.quantidade! })),
        caboSolarVermDevolvido: valores.caboSolarVermDevolvido ?? undefined,
        caboSolarPretoDevolvido: valores.caboSolarPretoDevolvido ?? undefined,
        caboHeprDevolvido: valores.caboHeprDevolvido ?? undefined,
        qtdMateriaisEnviados: valores.qtdMateriaisEnviados ?? undefined,
        qtdMateriaisDivergentes: valores.qtdMateriaisDivergentes ?? undefined,
        fotoSobrasGrupo: valores.fotoSobrasGrupo ?? undefined,
        ajusteFino: valores.ajusteFino ?? undefined
      })
      .subscribe({
        next: () => {
          this.snackBar.open('Lançamento criado com sucesso', 'Fechar', { duration: 3000 });
          this.router.navigate(['/lancamentos']);
        },
        error: () => this.snackBar.open('Erro ao criar lançamento', 'Fechar', { duration: 3000 })
      });
  }

  private criarInversorGroup() {
    return this.fb.group({
      kw: [null as number | null, [Validators.required, Validators.min(0.1)]],
      quantidade: [1, [Validators.required, Validators.min(1)]]
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
