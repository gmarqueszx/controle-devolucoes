import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Alerta, AlertaRequest } from '../models/alerta.model';

@Injectable({ providedIn: 'root' })
export class AlertaService {
  private readonly apiUrl = `${environment.apiUrl}/alertas`;

  constructor(private readonly http: HttpClient) {}

  listar(de: string, ate: string, nivel?: string, equipeId?: number): Observable<Alerta[]> {
    let params = new HttpParams().set('de', de).set('ate', ate);
    if (nivel) {
      params = params.set('nivel', nivel);
    }
    if (equipeId) {
      params = params.set('equipeId', equipeId);
    }
    return this.http.get<Alerta[]>(this.apiUrl, { params });
  }

  criar(request: AlertaRequest): Observable<Alerta> {
    return this.http.post<Alerta>(this.apiUrl, request);
  }

  atualizarStatus(id: number, status: Alerta['status']): Observable<Alerta> {
    return this.http.put<Alerta>(`${this.apiUrl}/${id}/status`, { status });
  }
}
