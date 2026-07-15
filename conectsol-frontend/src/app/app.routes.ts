import { Routes } from '@angular/router';
import { adminGuard, authGuard } from './core/guards/auth.guard';
import { LoginComponent } from './features/login/login.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { RelatorioEquipesComponent } from './features/relatorio/relatorio-equipes.component';
import { LancamentoListComponent } from './features/lancamentos/lancamento-list.component';
import { LancamentoFormComponent } from './features/lancamentos/lancamento-form.component';
import { AlertaListComponent } from './features/alertas/alerta-list.component';
import { EquipeListComponent } from './features/equipes/equipe-list.component';
import { SobrasPendentesComponent } from './features/sobras/sobras-pendentes.component';
import { LayoutComponent } from './shared/components/layout.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'relatorio', component: RelatorioEquipesComponent },
      { path: 'lancamentos', component: LancamentoListComponent },
      { path: 'lancamentos/novo', component: LancamentoFormComponent, canActivate: [adminGuard] },
      { path: 'lancamentos/:id/editar', component: LancamentoFormComponent, canActivate: [adminGuard] },
      { path: 'alertas', component: AlertaListComponent },
      { path: 'equipes', component: EquipeListComponent },
      { path: 'sobras', component: SobrasPendentesComponent }
    ]
  },
  { path: '**', redirectTo: 'dashboard' }
];
