import { Routes } from '@angular/router';
import { adminGuard, authGuard } from './core/guards/auth.guard';
import { LoginComponent } from './features/login/login.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { RelatorioEquipesComponent } from './features/relatorio/relatorio-equipes.component';
import { LancamentoListComponent } from './features/lancamentos/lancamento-list.component';
import { LancamentoFormComponent } from './features/lancamentos/lancamento-form.component';
import { AlertaListComponent } from './features/alertas/alerta-list.component';
import { EquipeListComponent } from './features/equipes/equipe-list.component';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'relatorio', component: RelatorioEquipesComponent, canActivate: [authGuard] },
  { path: 'lancamentos', component: LancamentoListComponent, canActivate: [authGuard] },
  { path: 'lancamentos/novo', component: LancamentoFormComponent, canActivate: [authGuard, adminGuard] },
  { path: 'alertas', component: AlertaListComponent, canActivate: [authGuard] },
  { path: 'equipes', component: EquipeListComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: 'dashboard' }
];
