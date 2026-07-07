import { CommonModule } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { AlertaRequest } from '../../core/models/alerta.model';
import { Equipe } from '../../core/models/equipe.model';

export interface AlertaFormDialogData {
  equipes: Equipe[];
}

@Component({
  selector: 'app-alerta-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule
  ],
  templateUrl: './alerta-form.component.html'
})
export class AlertaFormComponent {
  form = this.fb.group({
    equipeId: [null as number | null, [Validators.required]],
    dataAlerta: [new Date(), [Validators.required]],
    descricao: [''],
    statusOriginal: ['']
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly dialogRef: MatDialogRef<AlertaFormComponent, AlertaRequest>,
    @Inject(MAT_DIALOG_DATA) public data: AlertaFormDialogData
  ) {}

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const valores = this.form.getRawValue();
    this.dialogRef.close({
      equipeId: valores.equipeId!,
      dataAlerta: valores.dataAlerta!.toISOString().substring(0, 10),
      descricao: valores.descricao ?? '',
      statusOriginal: valores.statusOriginal ?? ''
    });
  }

  cancelar(): void {
    this.dialogRef.close();
  }
}
