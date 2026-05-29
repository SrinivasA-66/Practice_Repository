import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { SCAEvent } from '../../core/models/models';

@Component({
  selector: 'app-sca-statistics',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sca-statistics.component.html',
  styleUrl: './sca-statistics.component.css'
})
export class ScaStatisticsComponent implements OnInit {
  events: SCAEvent[] = [];
  isLoading = true;
  errorMessage = '';
  stats = [
    { label: 'Total SCA', value: '0', icon: 'fas fa-shield-alt', gradient: 'linear-gradient(135deg,#667eea,#764ba2)' },
    { label: 'Pass Rate', value: '0%', icon: 'fas fa-check', gradient: 'linear-gradient(135deg,#10b981,#059669)' },
    { label: 'Failures', value: '0', icon: 'fas fa-times', gradient: 'linear-gradient(135deg,#ef4444,#dc2626)' }
  ];

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.authService.getScaEvents().subscribe({
      next: (d) => {
        this.events = d || [];
        this.stats[0].value = this.events.length.toString();
        const passes = this.events.filter(e => e.result === 'PASS').length;
        this.stats[1].value = this.events.length > 0 ? Math.round(passes/this.events.length*100) + '%' : '0%';
        this.stats[2].value = (this.events.length - passes).toString();
        this.isLoading = false;
      },
      error: () => { this.errorMessage = 'Unable to load SCA events.'; this.isLoading = false; }
    });
  }
}
