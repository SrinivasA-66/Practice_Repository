import { Component, Input, OnInit, OnDestroy, HostListener, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { NotificationService } from '../../core/services/notification.service';
import { Notification } from '../../core/models/models';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit, OnDestroy {

  @Input() pageTitle: string = 'Dashboard';

  notifications: Notification[] = [];
  showNotifications = false;
  showProfile = false;
  unreadCount = 0;
  session: any = null;
  initials = 'U';

  private refreshInterval: any;

  constructor(
    private authService: AuthService,
    private notificationService: NotificationService,
    private router: Router,
    private elRef: ElementRef
  ) {}

  ngOnInit(): void {
    this.session = this.authService.getSession();
    this.initials = this.computeInitials(this.session?.name);
    this.loadNotifications();
    this.refreshInterval = setInterval(() => this.loadNotifications(), 30000);
  }

  ngOnDestroy(): void {
    if (this.refreshInterval) {
      clearInterval(this.refreshInterval);
    }
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (!this.elRef.nativeElement.contains(event.target)) {
      this.showNotifications = false;
      this.showProfile = false;
    }
  }

  private computeInitials(name: string | undefined): string {
    if (!name) return 'U';
    const parts = name.trim().split(/\s+/);
    if (parts.length === 1) return parts[0].charAt(0).toUpperCase();
    return (parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
  }

  loadNotifications(): void {
    const userId = this.authService.getUserId();
    const role = this.authService.getRole();
    if (userId) {
      this.notificationService.getNotifications(userId.toString(), role).subscribe({
        next: (data: Notification[]) => {
          this.notifications = data || [];
          this.unreadCount = this.notifications.filter(n => n.status === 'UNREAD').length;
        },
        error: () => {
          this.notifications = [];
          this.unreadCount = 0;
        }
      });
    }
  }

  toggleNotifications(): void {
    this.showNotifications = !this.showNotifications;
    if (this.showNotifications) {
      this.showProfile = false;
      this.loadNotifications();
    }
  }

  toggleProfile(): void {
    this.showProfile = !this.showProfile;
    if (this.showProfile) {
      this.showNotifications = false;
    }
  }

  goToNotifications(): void {
    this.showProfile = false;
    this.router.navigate(['/notifications']);
  }

  logout(): void {
    this.authService.logout();
    this.showProfile = false;
    this.router.navigate(['/login']);
  }

  markRead(notif: Notification): void {
    if (notif.status === 'UNREAD') {
      this.notificationService.markAsRead(notif.notificationId).subscribe({
        next: () => {
          notif.status = 'READ';
          this.unreadCount = this.notifications.filter(n => n.status === 'UNREAD').length;
        },
        error: () => {
          notif.status = 'READ';
          this.unreadCount = this.notifications.filter(n => n.status === 'UNREAD').length;
        }
      });
    }
  }

  markAllAsRead(): void {
    const userId = this.authService.getUserId();
    const role = this.authService.getRole();
    this.notificationService.markAllRead(userId.toString(), role).subscribe({
      next: () => {
        this.notifications.forEach(n => n.status = 'READ');
        this.unreadCount = 0;
      },
      error: () => {
        this.notifications.forEach(n => n.status = 'READ');
        this.unreadCount = 0;
      }
    });
  }

  getCategoryIcon(category: string): string {
    switch (category) {
      case 'CONSENT':  return 'fas fa-handshake';
      case 'SECURITY': return 'fas fa-shield-alt';
      case 'USAGE':    return 'fas fa-chart-bar';
      case 'INCIDENT': return 'fas fa-exclamation-triangle';
      default:         return 'fas fa-bell';
    }
  }

  getCategoryColor(category: string): string {
    switch (category) {
      case 'CONSENT':  return '#1e40af';
      case 'SECURITY': return '#dc2626';
      case 'USAGE':    return '#d97706';
      case 'INCIDENT': return '#dc2626';
      default:         return '#1e40af';
    }
  }

  getCategoryBg(category: string): string {
    switch (category) {
      case 'CONSENT':  return '#dbeafe';
      case 'SECURITY': return '#fee2e2';
      case 'USAGE':    return '#fef3c7';
      case 'INCIDENT': return '#fee2e2';
      default:         return '#dbeafe';
    }
  }
}
