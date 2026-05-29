import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuditService } from '../../core/services/audit.service';
import { AuditTrail } from '../../core/models/models';

@Component({
  selector: 'app-audit-trail',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './audit-trail.component.html',
  styleUrl: './audit-trail.component.css'
})
export class AuditTrailComponent implements OnInit, OnDestroy {
  trails: AuditTrail[] = [];
  searchTerm = '';
  actorFilter = '';
  isLoading = true;
  errorMessage = '';
  actorTypes = ['USER', 'TPP', 'SYSTEM'];

  private pollHandle: any;

  constructor(private auditService: AuditService) {}

  ngOnInit(): void {
    this.fetch();
    
    this.pollHandle = setInterval(() => this.fetch(true), 8000);
  }

  ngOnDestroy(): void {
    if (this.pollHandle) clearInterval(this.pollHandle);
  }

  private fetch(silent: boolean = false): void {
    if (!silent) this.isLoading = true;
    this.auditService.getAuditTrails().subscribe({
      next: (d) => {
        
        this.trails = (d || []).sort((a, b) =>
          new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime()
        );
        this.isLoading = false;
      },
      error: () => {
        if (!silent) this.errorMessage = 'Unable to load audit trails.';
        this.isLoading = false;
      }
    });
  }

  get filteredTrails(): AuditTrail[] {
    return this.trails.filter(t => {
      const matchSearch = !this.searchTerm ||
        t.action.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        t.resource.toLowerCase().includes(this.searchTerm.toLowerCase());
      const matchActor = !this.actorFilter || t.actorType === this.actorFilter;
      return matchSearch && matchActor;
    });
  }
}
