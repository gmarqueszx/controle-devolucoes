import { CommonModule } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { Equipe, EquipeRequest } from '../../core/models/equipe.model';

@Component({
  selector: 'app-equipe-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatDialogModule, MatButtonModule, MatFormFieldModule, MatInputModule],
  templateUrl: './equipe-form.component.html',
  styleUrl: './equipe-form.component.scss'
})
export class EquipeFormComponent {
  form = this.fb.group({
    montador: [this.data?.montador ?? '', [Validators.required]],
    eletricista: [this.data?.eletricista ?? '']
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly dialogRef: MatDialogRef<EquipeFormComponent, EquipeRequest>,
    @Inject(MAT_DIALOG_DATA) public data: Equipe | null
  ) {}

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.dialogRef.close(this.form.getRawValue() as EquipeRequest);
  }

  cancelar(): void {
    this.dialogRef.close();
  }
}
