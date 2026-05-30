import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuditTrail, ComplianceReport } from '../models/models';


@Injectable({ providedIn: 'root' })
export class AuditService {

  private apiUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  getAuditTrails(): Observable<AuditTrail[]> {
    return this.http.get<AuditTrail[]>(`${this.apiUrl}/audit-trails`);
  }

  getComplianceReports(): Observable<ComplianceReport[]> {
    return this.http.get<ComplianceReport[]>(`${this.apiUrl}/compliance/reports`);
  }

  generateReport(scope: string): Observable<ComplianceReport> {
    return this.http.post<ComplianceReport>(
      `${this.apiUrl}/compliance/reports?scope=${encodeURIComponent(scope)}&metrics=auto-generated`,
      {}
    );
  }
}
