import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { MatAutocompleteModule, MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
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
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, map, startWith } from 'rxjs';
import { Equipe } from '../../core/models/equipe.model';
import { AuthService } from '../../core/services/auth.service';
import { EquipeService } from '../../core/services/equipe.service';
import { LancamentoService } from '../../core/services/lancamento.service';

const QTD_MATERIAIS_POR_TIPO_SISTEMA: Record<string, number> = {
  PROJETO: 33,
  'AMPLIAÇÃO': 15
};

type CampoNome = 'montador' | 'eletricista' | 'ajudante';

interface OpcaoNome {
  valor: string;
  ehNovo: boolean;
}

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
  montadoresOpcoes$: Observable<OpcaoNome[]> = new Observable<OpcaoNome[]>();
  eletricistasOpcoes$: Observable<OpcaoNome[]> = new Observable<OpcaoNome[]>();
  ajudantesOpcoes$: Observable<OpcaoNome[]> = new Observable<OpcaoNome[]>();
  lancamentoId: number | null = null;

  private conhecidos: Record<CampoNome, string[]> = { montador: [], eletricista: [], ajudante: [] };
  private confirmados: Record<CampoNome, string | null> = { montador: null, eletricista: null, ajudante: null };

  get modoEdicao(): boolean {
    return this.lancamentoId !== null;
  }

  form = this.fb.group({
    montador: ['', [Validators.required, this.nomeConfirmadoValidator('montador')]],
    eletricista: ['', [this.nomeConfirmadoValidator('eletricista')]],
    ajudante: ['', [this.nomeConfirmadoValidator('ajudante')]],
    dataLancamento: [new Date(), [Validators.required]],
    cliente: [''],
    sistemas: [1, [Validators.required, Validators.min(1)]],
    observacoes: [''],
    criadoPor: [''],

    retornou: [false],
    tipoSistema: ['PROJETO'],
    solo: [false],
    placas: [0, [Validators.required, Validators.min(1)]],
    inversores: this.fb.array([this.criarInversorGroup()]),

    caboSolarVermDevolvido: [null as number | null],
    caboSolarPretoDevolvido: [null as number | null],
    caboHeprDevolvido: [null as number | null],

    qtdMateriaisEnviados: [{ value: null as number | null, disabled: false }],
    qtdMateriaisDivergentes: [null as number | null],
    fotoSobrasGrupo: [false],
    ajusteFinoVerm: [null as number | null],
    ajusteFinoPreto: [null as number | null],
    ajusteFinoHepr: [null as number | null],
    localizacaoSobra: ['']
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly equipeService: EquipeService,
    private readonly lancamentoService: LancamentoService,
    private readonly authService: AuthService,
    private readonly snackBar: MatSnackBar,
    private readonly router: Router,
    private readonly route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.equipeService.listar().subscribe((equipes) => {
      this.equipes = equipes;
      this.conhecidos.montador = this.nomesUnicos(equipes.map((e) => e.montador));
      this.conhecidos.eletricista = this.nomesUnicos(equipes.map((e) => e.eletricista));
      this.conhecidos.ajudante = this.nomesUnicos(equipes.map((e) => e.ajudante));

      const idParam = this.route.snapshot.paramMap.get('id');
      if (idParam) {
        this.lancamentoId = Number(idParam);
        this.carregarParaEdicao(this.lancamentoId);
      }
    });

    this.montadoresOpcoes$ = this.form.controls.montador.valueChanges.pipe(
      startWith(''),
      map((valor) => this.montarOpcoes('montador', valor))
    );
    this.eletricistasOpcoes$ = this.form.controls.eletricista.valueChanges.pipe(
      startWith(''),
      map((valor) => this.montarOpcoes('eletricista', valor))
    );
    this.ajudantesOpcoes$ = this.form.controls.ajudante.valueChanges.pipe(
      startWith(''),
      map((valor) => this.montarOpcoes('ajudante', valor))
    );

    this.form.controls.tipoSistema.valueChanges.subscribe((tipoSistema) => this.aoMudarTipoSistema(tipoSistema));
    this.atualizarEstadoQtdMaterial(this.form.controls.tipoSistema.value);
  }

  get inversoresArray(): FormArray {
    return this.form.controls.inversores;
  }

  adicionarInversor(): void {
    this.inversoresArray.push(this.criarInversorGroup());
  }

  removerInversor(index: number): void {
    const minimo = this.form.controls.tipoSistema.value === 'AMPLIAÇÃO' ? 0 : 1;
    if (this.inversoresArray.length > minimo) {
      this.inversoresArray.removeAt(index);
    }
  }

  onNomeSelecionado(campo: CampoNome, evento: MatAutocompleteSelectedEvent): void {
    const valor = evento.option.value as string;
    this.confirmados[campo] = valor;
    this.form.controls[campo].updateValueAndValidity();
  }

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const valores = this.form.getRawValue();

    const request = {
      montador: valores.montador!.trim(),
      eletricista: valores.eletricista?.trim() || undefined,
      ajudante: valores.ajudante?.trim() || undefined,
      dataLancamento: this.paraIso(valores.dataLancamento!),
      cliente: valores.cliente ?? '',
      sistemas: valores.sistemas!,
      observacoes: valores.observacoes ?? '',
      criadoPor: valores.criadoPor || this.authService.getUsuario()?.nome || '',
      retornou: valores.retornou ?? undefined,
      tipoSistema: valores.tipoSistema ?? undefined,
      solo: valores.solo ?? undefined,
      placas: valores.placas ?? undefined,
      inversores: valores.inversores.map((i) => ({ kw: i.kw!, quantidade: i.quantidade! })),
      caboSolarVermDevolvido: valores.caboSolarVermDevolvido ?? undefined,
      caboSolarPretoDevolvido: valores.caboSolarPretoDevolvido ?? undefined,
      caboHeprDevolvido: valores.caboHeprDevolvido ?? undefined,
      qtdMateriaisEnviados: valores.qtdMateriaisEnviados ?? undefined,
      qtdMateriaisDivergentes: valores.qtdMateriaisDivergentes ?? undefined,
      fotoSobrasGrupo: valores.fotoSobrasGrupo ?? undefined,
      ajusteFinoVerm: valores.ajusteFinoVerm ?? undefined,
      ajusteFinoPreto: valores.ajusteFinoPreto ?? undefined,
      ajusteFinoHepr: valores.ajusteFinoHepr ?? undefined,
      localizacaoSobra: valores.localizacaoSobra ?? undefined
    };

    const operacao = this.modoEdicao
      ? this.lancamentoService.atualizar(this.lancamentoId!, request)
      : this.lancamentoService.criar(request);

    operacao.subscribe({
      next: () => {
        const mensagem = this.modoEdicao ? 'Lançamento atualizado com sucesso' : 'Lançamento criado com sucesso';
        this.snackBar.open(mensagem, 'Fechar', { duration: 3000 });
        this.router.navigate(['/lancamentos']);
      },
      error: () => {
        const mensagem = this.modoEdicao ? 'Erro ao atualizar lançamento' : 'Erro ao criar lançamento';
        this.snackBar.open(mensagem, 'Fechar', { duration: 3000 });
      }
    });
  }

  private carregarParaEdicao(id: number): void {
    this.lancamentoService.buscarPorId(id).subscribe((lancamento) => {
      this.inversoresArray.clear();
      (lancamento.inversores.length ? lancamento.inversores : [{ kw: null, quantidade: 1 }]).forEach(() => {
        this.inversoresArray.push(this.criarInversorGroup());
      });

      this.confirmados.montador = lancamento.montador || null;
      this.confirmados.eletricista = lancamento.eletricista || null;
      this.confirmados.ajudante = lancamento.ajudante || null;

      this.form.patchValue({
        montador: lancamento.montador,
        eletricista: lancamento.eletricista,
        ajudante: lancamento.ajudante,
        dataLancamento: new Date(lancamento.dataLancamento),
        cliente: lancamento.cliente,
        sistemas: lancamento.sistemas,
        observacoes: lancamento.observacoes,
        criadoPor: lancamento.criadoPor,
        retornou: lancamento.retornou ?? false,
        tipoSistema: lancamento.tipoSistema ?? 'PROJETO',
        solo: lancamento.solo ?? false,
        placas: lancamento.placas ?? 0,
        caboSolarVermDevolvido: lancamento.caboSolarVermDevolvido,
        caboSolarPretoDevolvido: lancamento.caboSolarPretoDevolvido,
        caboHeprDevolvido: lancamento.caboHeprDevolvido,
        qtdMateriaisEnviados: lancamento.qtdMateriaisEnviados,
        qtdMateriaisDivergentes: lancamento.qtdMateriaisDivergentes,
        fotoSobrasGrupo: lancamento.fotoSobrasGrupo ?? false,
        ajusteFinoVerm: lancamento.ajusteFinoVerm,
        ajusteFinoPreto: lancamento.ajusteFinoPreto,
        ajusteFinoHepr: lancamento.ajusteFinoHepr,
        localizacaoSobra: lancamento.localizacaoSobra ?? ''
      });

      this.inversoresArray.controls.forEach((grupo, i) => {
        const inversor = lancamento.inversores[i];
        if (inversor) {
          grupo.patchValue({ kw: inversor.kw, quantidade: inversor.quantidade });
        }
      });

      this.atualizarEstadoQtdMaterial(this.form.controls.tipoSistema.value);
    });
  }

  private aoMudarTipoSistema(tipoSistema: string | null): void {
    this.atualizarEstadoQtdMaterial(tipoSistema);
    if (tipoSistema !== 'AMPLIAÇÃO' && this.inversoresArray.length === 0) {
      this.adicionarInversor();
    }
  }

  private atualizarEstadoQtdMaterial(tipoSistema: string | null): void {
    const qtd = QTD_MATERIAIS_POR_TIPO_SISTEMA[(tipoSistema ?? '').toUpperCase().trim()];
    if (qtd !== undefined) {
      this.form.controls.qtdMateriaisEnviados.setValue(qtd, { emitEvent: false });
      this.form.controls.qtdMateriaisEnviados.disable({ emitEvent: false });
    } else {
      this.form.controls.qtdMateriaisEnviados.enable({ emitEvent: false });
    }
  }

  private criarInversorGroup() {
    return this.fb.group({
      kw: [null as number | null, [Validators.required, Validators.min(0.1)]],
      quantidade: [1, [Validators.required, Validators.min(1)]]
    });
  }

  private nomesUnicos(nomes: (string | null | undefined)[]): string[] {
    const unicos = new Set(nomes.filter((n): n is string => !!n && n.trim().length > 0));
    return Array.from(unicos).sort((a, b) => a.localeCompare(b));
  }

  private montarOpcoes(campo: CampoNome, valor: string | null): OpcaoNome[] {
    const texto = (valor ?? '').trim();
    const textoLower = texto.toLowerCase();
    const conhecidos = this.conhecidos[campo];
    const opcoes: OpcaoNome[] = conhecidos
      .filter((nome) => nome.toLowerCase().includes(textoLower))
      .map((nome) => ({ valor: nome, ehNovo: false }));

    const existeExato = conhecidos.some((nome) => nome.toLowerCase() === textoLower);
    if (texto && !existeExato) {
      opcoes.push({ valor: texto, ehNovo: true });
    }
    return opcoes;
  }

  private nomeConfirmadoValidator(campo: CampoNome) {
    return (control: AbstractControl): ValidationErrors | null => {
      const valor = (control.value ?? '').trim();
      if (!valor) {
        return null;
      }
      return this.confirmados[campo] === valor ? null : { naoConfirmado: true };
    };
  }

  private paraIso(data: Date): string {
    return data.toISOString().substring(0, 10);
  }
}
