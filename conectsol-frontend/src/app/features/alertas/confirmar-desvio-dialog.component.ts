import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { ConfirmarDesvioRequest } from '../../core/models/alerta.model';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-confirmar-desvio-dialog',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatDialogModule, MatButtonModule, MatFormFieldModule, MatInputModule],
  templateUrl: './confirmar-desvio-dialog.component.html'
})
export class ConfirmarDesvioDialogComponent {
  form = this.fb.group({
    justificativa: ['', [Validators.required]]
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly dialogRef: MatDialogRef<ConfirmarDesvioDialogComponent, ConfirmarDesvioRequest>,
    private readonly authService: AuthService
  ) {}

  confirmar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.dialogRef.close({
      justificativa: this.form.value.justificativa!,
      confirmadoPor: this.authService.getUsuario()?.nome ?? ''
    });
  }

  cancelar(): void {
    this.dialogRef.close();
  }
}
