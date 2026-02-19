import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Filme, Sala, SessaoRequest } from '../app/core/models/cinema.models';

@Injectable({ providedIn: 'root' })
export class SessaoService {
  private http = inject(HttpClient);
  private readonly API = 'http://localhost:8080';

  private getHeaders() {
    const token = localStorage.getItem('token');
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  listarFilmes(): Observable<Filme[]> {
    return this.http.get<Filme[]>(`${this.API}/filmes`, { headers: this.getHeaders() });
  }

  listarSalas(): Observable<Sala[]> {
    return this.http.get<Sala[]>(`${this.API}/salas`, { headers: this.getHeaders() });
  }

  salvarSessao(dados: SessaoRequest): Observable<any> {
    return this.http.post(`${this.API}/sessoes`, dados, { headers: this.getHeaders() });
  }
}