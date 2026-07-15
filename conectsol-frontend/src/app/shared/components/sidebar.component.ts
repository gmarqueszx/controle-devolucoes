import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink, RouterLinkActive } from '@angular/router';

interface ItemMenu {
  label: string;
  path: string;
  icon: string;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, MatListModule, MatIconModule, RouterLink, RouterLinkActive],
  template: `
    <mat-nav-list>
      <a
        mat-list-item
        *ngFor="let item of itens"
        [routerLink]="item.path"
        routerLinkActive="ativo"
      >
        <mat-icon matListItemIcon>{{ item.icon }}</mat-icon>
        <span matListItemTitle>{{ item.label }}</span>
      </a>
    </mat-nav-list>
  `,
  styles: [
    `
      .ativo {
        background-color: rgba(0, 0, 0, 0.06);
        font-weight: 600;
      }
    `
  ]
})
export class SidebarComponent {
  itens: ItemMenu[] = [
    { label: 'Dashboard', path: '/dashboard', icon: 'dashboard' },
    { label: 'Relatório', path: '/relatorio', icon: 'bar_chart' },
    { label: 'Lançamentos', path: '/lancamentos', icon: 'assignment' },
    { label: 'Alertas', path: '/alertas', icon: 'warning' },
    { label: 'Sobras pendentes', path: '/sobras', icon: 'inventory_2' },
    { label: 'Equipes', path: '/equipes', icon: 'groups' }
  ];
}
