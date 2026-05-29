import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Incident, APILog } from '../models/models';


@Injectable({ providedIn: 'root' })
export class MonitoringService {

  private apiUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

 

  getIncidents(): Observable<Incident[]> {
    return this.http.get<Incident[]>(`${this.apiUrl}/incidents`);
  }

  createIncident(incident: Partial<Incident>): Observable<Incident> {
    return this.http.post<Incident>(`${this.apiUrl}/incidents`, incident);
  }

  closeIncident(id: number): Observable<Incident> {
    return this.http.put<Incident>(`${this.apiUrl}/incidents/${id}/close`, {});
  }


  getGatewayLogs(): Observable<APILog[]> {
    return this.http.get<APILog[]>(`${this.apiUrl}/logs`);
  }

  getLogsByApp(appId: number): Observable<APILog[]> {
    return this.http.get<APILog[]>(`${this.apiUrl}/logs/app/${appId}`);
  }
}

