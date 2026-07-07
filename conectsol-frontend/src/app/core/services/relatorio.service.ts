import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { DashboardStats, RelatorioEquipe, TendenciaMensal } from '../models/relatorio.model';

@Injectable({ providedIn: 'root' })
export class RelatorioService {
  private readonly apiUrl = `${environment.apiUrl}/relatorio`;
  private readonly dashboardUrl = `${environment.apiUrl}/dashboard`;

  constructor(private readonly http: HttpClient) {}

  relatorioEquipes(de: string, ate: string): Observable<RelatorioEquipe[]> {
    const params = new HttpParams().set('de', de).set('ate', ate);
    return this.http.get<RelatorioEquipe[]>(`${this.apiUrl}/equipes`, { params });
  }

  tendencia(meses = 6): Observable<TendenciaMensal[]> {
    const params = new HttpParams().set('meses', meses);
    return this.http.get<TendenciaMensal[]>(`${this.apiUrl}/tendencia`, { params });
  }

  dashboardStats(): Observable<DashboardStats> {
    return this.http.get<DashboardStats>(`${this.dashboardUrl}/stats`);
  }
}
