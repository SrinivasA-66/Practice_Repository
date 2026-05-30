import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';


@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent implements OnInit, OnDestroy {

  session: any = null;
  isCollapsed = false;

  private readonly COLLAPSE_BREAKPOINT = 992;

  tppMenu = [
    { label: 'Dashboard',      route: '/developer/dashboard',     icon: 'fas fa-tachometer-alt' },
    { label: 'Register TPP',   route: '/developer/tpp-register',  icon: 'fas fa-building' },
    { label: 'My Apps',        route: '/developer/apps',          icon: 'fas fa-cube' },
    { label: 'Register App',   route: '/developer/apps/new',      icon: 'fas fa-plus-circle' },
    { label: 'Subscribe Plan', route: '/developer/subscribe',     icon: 'fas fa-plug' }
  ];

  customerMenu = [
    { label: 'My Apps',        route: '/customer/apps',              icon: 'fas fa-store' },
    { label: 'My Consents',    route: '/customer/consents',          icon: 'fas fa-handshake' },
    { label: 'My Accounts',    route: '/customer/accounts',          icon: 'fas fa-wallet' },
    { label: 'Payments',       route: '/customer/payment-initiate',  icon: 'fas fa-paper-plane' },
    { label: 'Funds Check',    route: '/customer/funds-check',       icon: 'fas fa-search-dollar' },
    { label: 'SCA Verify',     route: '/customer/sca',               icon: 'fas fa-shield-alt' }
  ];

  opsMenu = [
    { label: 'API Health',     route: '/operations/health',      icon: 'fas fa-heartbeat' },
    { label: 'Throttle Log',   route: '/operations/throttle-log', icon: 'fas fa-tachometer-alt' },
    { label: 'Incidents',      route: '/operations/incidents',   icon: 'fas fa-exclamation-triangle' },
    { label: 'Consent Registry', route: '/compliance/consents',  icon: 'fas fa-file-contract' },
    { label: 'SCA Stats',      route: '/compliance/sca',         icon: 'fas fa-chart-pie' },
    { label: 'Audit Trail',    route: '/compliance/audit',       icon: 'fas fa-search' }
  ];

  adminMenu = [
    { label: 'API Products',   route: '/admin/products',    icon: 'fas fa-boxes' },
    { label: 'Plan Config',    route: '/admin/plans',       icon: 'fas fa-sliders-h' },
    { label: 'User Mgmt',      route: '/admin/users',       icon: 'fas fa-users' },
    { label: 'TPP Mgmt',       route: '/admin/tpp',         icon: 'fas fa-building' }
  ];

  constructor(private authService: AuthService) {
    this.session = this.authService.getSession();
  }

  ngOnInit(): void {
    this.applyResponsiveCollapse(window.innerWidth);
  }

  ngOnDestroy(): void {
    document.body.classList.remove('sidebar-collapsed');
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: UIEvent): void {
    const w = (event.target as Window).innerWidth;
    this.applyResponsiveCollapse(w);
  }

  
  private applyResponsiveCollapse(viewportWidth: number): void {
    if (viewportWidth < this.COLLAPSE_BREAKPOINT) {
      this.setCollapsed(true);
    }
  }

  toggleCollapse(): void {
    this.setCollapsed(!this.isCollapsed);
  }

  private setCollapsed(collapsed: boolean): void {
    this.isCollapsed = collapsed;
    document.body.classList.toggle('sidebar-collapsed', collapsed);
  }

  logout(): void {
    this.authService.logout();
    window.location.href = '/login';
  }
}
