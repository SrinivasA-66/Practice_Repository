import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MonitoringService } from '../../core/services/monitoring.service';
import { Incident } from '../../core/models/models';

@Component({
  selector: 'app-incident-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './incident-management.component.html',
  styleUrl: './incident-management.component.css'
})
export class IncidentManagementComponent implements OnInit {
  incidents: Incident[] = [];
  isLoading = true;
  showForm = false;
  errorMessage = '';
  categories = ['OUTAGE', 'LATENCY', 'SECURITY'];
  newIncident = { category: '', description: '' };

  constructor(private monitoringService: MonitoringService) {}

  ngOnInit(): void {
    this.monitoringService.getIncidents().subscribe({
      next: (d) => { this.incidents = d || []; this.isLoading = false; },
      error: () => { this.errorMessage = 'Unable to load incidents.'; this.isLoading = false; }
    });
  }

  createIncident(): void {
    this.errorMessage = '';
    if (!this.newIncident.category) {
      this.errorMessage = 'Please choose a category.';
      return;
    }
    if (!this.newIncident.description || this.newIncident.description.trim().length < 10) {
      this.errorMessage = 'Description must be at least 10 characters.';
      return;
    }
    this.monitoringService.createIncident(this.newIncident as any).subscribe({
      next: (d) => {
        this.incidents.unshift(d);
        this.showForm = false;
        this.newIncident = { category: '', description: '' };
      },
      error: () => { this.errorMessage = 'Failed to create incident.'; }
    });
  }

  getCatIcon(cat: string): string {
    return cat === 'OUTAGE' ? 'fas fa-power-off' : cat === 'LATENCY' ? 'fas fa-clock' : 'fas fa-shield-alt';
  }
  getCatColor(cat: string): string {
    return cat === 'OUTAGE' ? 'linear-gradient(135deg,#ef4444,#dc2626)' : cat === 'LATENCY' ? 'linear-gradient(135deg,#f59e0b,#d97706)' : 'linear-gradient(135deg,#667eea,#764ba2)';
  }
}
