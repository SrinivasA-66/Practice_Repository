import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { MonitoringService } from '../../core/services/monitoring.service';
import { APILog } from '../../core/models/models';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-api-health',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './api-health.component.html',
  styleUrl: './api-health.component.css'
})
export class ApiHealthComponent implements OnInit {

  errorMessage = '';
  isLoading = true;
  logs: APILog[] = [];
  allLogs: APILog[] = [];

  private readonly SLOW_THRESHOLD_MS = 3000;
  private readonly apiUrl = environment.apiBaseUrl;

  metrics = [
    { label: 'Total Requests', value: '—', icon: 'fas fa-bolt',                 gradient: '#0a2540' },
    { label: 'Avg Latency',    value: '—', icon: 'fas fa-clock',                gradient: '#1e40af' },
    { label: 'Error Rate',     value: '—', icon: 'fas fa-bug',                  gradient: '#dc2626' },
    { label: 'Incidents',      value: '0', icon: 'fas fa-exclamation-triangle', gradient: '#c79a2a' }
  ];

  constructor(
    private monitoringService: MonitoringService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.isLoading = true;

    this.monitoringService.getGatewayLogs().subscribe({
      next: (logs) => {
        this.allLogs = (logs || [])
          .filter(l => this.isTppApiEndpoint(l.endpoint))
          .sort((a, b) =>
            new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime()
          );


        this.logs = this.allLogs.filter(l =>
          l.statusCode < 400 &&
          l.statusCode !== 429 &&
          (l.latencyMs ?? 0) <= this.SLOW_THRESHOLD_MS
        );

        this.calculateMetrics();

        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.errorMessage = 'Unable to load API logs.';
      }
    });

    this.monitoringService.getIncidents().subscribe({
      next: (incidents) => {
        if (incidents) {
          this.metrics[3].value = incidents.length.toString();
        }
      }
    });
  }


  simulateError(): void {
    this.http.get(`${this.apiUrl}/aisp/accounts/INVALID-999`).subscribe({
      next: () => {
        this.ngOnInit();
      },
      error: () => {
        setTimeout(() => this.ngOnInit(), 500);
      }
    });
  }

  resolveAppName(log: APILog): string {
    return log.tppApp?.appName
      || (log as any).appName
      || (log.authClient?.tppApp?.appName)
      || '';
  }

  private isTppApiEndpoint(uri?: string): boolean {
    if (!uri) return false;
    return uri.startsWith('/api/v1/aisp/')
        || uri.startsWith('/api/v1/pisp/')
        || uri.startsWith('/api/v1/cbpii/');
  }

  private calculateMetrics(): void {
    if (this.allLogs.length === 0) return;

    this.metrics[0].value = this.allLogs.length.toString();

    const avgLatency = this.allLogs
      .reduce((sum, l) => sum + (l.latencyMs || 0), 0) / this.allLogs.length;
    this.metrics[1].value = Math.round(avgLatency) + 'ms';

    const errors = this.allLogs.filter(l => l.statusCode >= 400).length;
    this.metrics[2].value = ((errors / this.allLogs.length) * 100).toFixed(1) + '%';
  }
}