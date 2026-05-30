import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginSession, SCAEvent } from '../models/models';


@Injectable({ providedIn: 'root' })
export class AuthService {

  private apiUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  
  login(email: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/auth/login`, { email, password });
  }

  
  register(data: { name: string; email: string; phone: string; password: string; role: string}): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/auth/register`, data);
  }

  
  saveSession(userId: number, name: string, email: string, role: string, token: string): void {
    const session: LoginSession = { userId, name, email, role: role as any, token };
    localStorage.setItem('obx_session', JSON.stringify(session));
  }

  
  getSession(): LoginSession | null {
    const data = localStorage.getItem('obx_session');
    if (data) {
      return JSON.parse(data) as LoginSession;
    }
    return null;
  }

  
  isLoggedIn(): boolean {
    return this.getSession() !== null;
  }

  
  getRole(): string {
    const session = this.getSession();
    return session ? session.role : '';
  }

  
  getUserId(): number {
    const session = this.getSession();
    return session ? session.userId : 0;
  }

  
  getToken(): string {
    const session = this.getSession();
    return session ? session.token : '';
  }

 
  getEmail(): string {
    const session = this.getSession();
    return session ? session.email : '';
  }

 
  getName(): string {
    const session = this.getSession();
    return session ? session.name : '';
  }

  
  logout(): void {
    localStorage.removeItem('obx_session');
  }

  
  verifySca(userId: number, method: string, referenceId: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/sca/otp?userId=${userId}&referenceId=${referenceId}`, {});
  }

  
  getScaEvents(): Observable<SCAEvent[]> {
    return this.http.get<SCAEvent[]>(`${this.apiUrl}/sca/events`);
  }
}
