import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, MatToolbarModule, MatIconModule, MatButtonModule],
  template: `
    <mat-toolbar color="primary">
      <span>Conectsol</span>
      <span class="spacer"></span>
      <span class="usuario" *ngIf="usuario">{{ usuario.nome }} ({{ usuario.perfil }})</span>
      <button mat-icon-button (click)="sair()" aria-label="Sair" title="Sair">
        <mat-icon>logout</mat-icon>
      </button>
    </mat-toolbar>
  `,
  styles: [
    `
      .spacer {
        flex: 1 1 auto;
      }
      .usuario {
        margin-right: 12px;
        font-size: 0.9rem;
      }
    `
  ]
})
export class HeaderComponent {
  usuario = this.authService.getUsuario();

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}

  sair(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
