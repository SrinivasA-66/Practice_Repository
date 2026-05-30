import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Consent, ConsentEvent } from '../models/models';

@Injectable({ providedIn: 'root' })
export class ConsentService {

  private apiUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}


  createConsent(consent: {
    user: { userId: number };
    tppApp: { tppAppId: number };
    scopeJSON: string;
  }): Observable<Consent> {
    return this.http.post<Consent>(
      `${this.apiUrl}/consents`,
      consent
    );
  }

  getConsents(): Observable<Consent[]> {
    return this.http.get<Consent[]>(`${this.apiUrl}/consents`);
  }

 
  getConsentsByUser(userId: number): Observable<Consent[]> {
    return this.http.get<Consent[]>(
      `${this.apiUrl}/consents/user/${userId}`
    );
  }


  getConsentById(id: number): Observable<Consent> {
    return this.http.get<Consent>(`${this.apiUrl}/consents/${id}`);
  }


  revokeConsent(id: number): Observable<Consent> {
    return this.http.put<Consent>(
      `${this.apiUrl}/consents/${id}/revoke`,
      {}
    );
  }


  updateScopes(id: number, scopes: string[]): Observable<Consent> {
    return this.http.put<Consent>(
      `${this.apiUrl}/consents/${id}/scopes`,
      { scopeJSON: JSON.stringify(scopes) }
    );
  }


  activateAfterSca(id: number, method: string = 'OTP'): Observable<Consent> {
    return this.http.put<Consent>(
      `${this.apiUrl}/consents/${id}/activate`,
      { method }
    );
  }


  getConsentEvents(consentId: number): Observable<ConsentEvent[]> {
    return this.http.get<ConsentEvent[]>(
      `${this.apiUrl}/consents/${consentId}/events`
    );
  }
}