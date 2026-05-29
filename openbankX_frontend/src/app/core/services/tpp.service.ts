import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { TPP, TPPApp, AppStats } from '../models/models';


@Injectable({ providedIn: 'root' })
export class TppService {

  private apiUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}


  registerTpp(tpp: Partial<TPP>): Observable<TPP> {
    return this.http.post<TPP>(`${this.apiUrl}/tpps`, tpp);
  }

  getTpps(): Observable<TPP[]> {
    return this.http.get<TPP[]>(`${this.apiUrl}/tpps`);
  }

  
  getMyTpps(ownerEmail: string): Observable<TPP[]> {
    const email = (ownerEmail || '').trim().toLowerCase();
    if (!email) return this.getTpps().pipe(map(() => []));
    return this.getTpps().pipe(
      map(tpps => (tpps || []).filter(t => {
        const owner   = (t.ownerEmail   || '').trim().toLowerCase();
        const contact = (t.contactInfo  || '').trim().toLowerCase();
        return owner === email || contact === email;
      }))
    );
  }

  getTppById(id: number): Observable<TPP> {
    return this.http.get<TPP>(`${this.apiUrl}/tpps/${id}`);
  }

  updateTppStatus(id: number, status: string): Observable<TPP> {
    return this.http.put<TPP>(`${this.apiUrl}/tpps/${id}`, { status });
  }



  getApps(): Observable<TPPApp[]> {
    return this.http.get<TPPApp[]>(`${this.apiUrl}/apps`);
  }

 
  getMyApps(ownerEmail: string): Observable<TPPApp[]> {
    const email = (ownerEmail || '').trim().toLowerCase();
    if (!email) return this.getApps().pipe(map(() => []));
    return this.getApps().pipe(
      map(apps => (apps || []).filter(a => {
        const owner   = (a.tpp?.ownerEmail   || '').trim().toLowerCase();
        const contact = (a.tpp?.contactInfo  || '').trim().toLowerCase();
        return owner === email || contact === email;
      }))
    );
  }

  getAppById(id: number): Observable<TPPApp> {
    return this.http.get<TPPApp>(`${this.apiUrl}/apps/${id}`);
  }

  createApp(app: Partial<TPPApp>): Observable<TPPApp> {
    return this.http.post<TPPApp>(`${this.apiUrl}/apps`, app);
  }

  updateApp(id: number, app: Partial<TPPApp>): Observable<TPPApp> {
    return this.http.put<TPPApp>(`${this.apiUrl}/apps/${id}`, app);
  }

  updateAppStatus(id: number, status: string): Observable<TPPApp> {
    return this.http.put<TPPApp>(`${this.apiUrl}/apps/${id}`, { status });
  }

  
  approveApp(id: number): Observable<TPPApp> {
    return this.http.put<TPPApp>(`${this.apiUrl}/apps/${id}/approve`, {});
  }

 
  rejectApp(id: number): Observable<TPPApp> {
    return this.http.put<TPPApp>(`${this.apiUrl}/apps/${id}/reject`, {});
  }

  getAvailableApps(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/apps/available`);
  }

  getAppStats(appId: number): Observable<AppStats> {
    return this.http.get<AppStats>(`${this.apiUrl}/apps/${appId}/stats`);
  }
}
