import { CommonModule } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { Equipe, EquipeRequest, FuncaoEquipe } from '../../core/models/equipe.model';

@Component({
  selector: 'app-equipe-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatIconModule
  ],
  templateUrl: './equipe-form.component.html',
  styleUrl: './equipe-form.component.scss'
})
export class EquipeFormComponent {
  funcoes: { valor: FuncaoEquipe; rotulo: string }[] = [
    { valor: 'MONTADOR', rotulo: 'Montador' },
    { valor: 'ELETRICISTA', rotulo: 'Eletricista' },
    { valor: 'AJUDANTE', rotulo: 'Ajudante' }
  ];

  form = this.fb.group({
    membros: this.fb.array(
      this.data?.membros?.length
        ? this.data.membros.map((m) => this.criarMembroGroup(m.nome, m.funcao))
        : [this.criarMembroGroup('', 'MONTADOR')]
    )
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly dialogRef: MatDialogRef<EquipeFormComponent, EquipeRequest>,
    @Inject(MAT_DIALOG_DATA) public data: Equipe | null
  ) {}

  get membrosArray(): FormArray {
    return this.form.controls.membros;
  }

  get temMontador(): boolean {
    return this.membrosArray.controls.some((grupo) => grupo.value.funcao === 'MONTADOR' && grupo.value.nome);
  }

  adicionarMembro(): void {
    this.membrosArray.push(this.criarMembroGroup('', 'MONTADOR'));
  }

  removerMembro(index: number): void {
    if (this.membrosArray.length > 1) {
      this.membrosArray.removeAt(index);
    }
  }

  salvar(): void {
    if (this.form.invalid || !this.temMontador) {
      this.form.markAllAsTouched();
      return;
    }
    this.dialogRef.close({ membros: this.form.getRawValue().membros as Equipe['membros'] });
  }

  cancelar(): void {
    this.dialogRef.close();
  }

  private criarMembroGroup(nome: string, funcao: FuncaoEquipe) {
    return this.fb.group({
      nome: [nome, [Validators.required]],
      funcao: [funcao, [Validators.required]]
    });
  }
}
