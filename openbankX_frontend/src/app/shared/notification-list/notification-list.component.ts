import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../core/services/notification.service';
import { AuthService } from '../../core/services/auth.service';
import { Notification } from '../../core/models/models';

@Component({
  selector: 'app-notification-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notification-list.component.html',
  styleUrl: './notification-list.component.css'
})
export class NotificationListComponent implements OnInit {
  notifications: Notification[] = [];
  selectedNotification: Notification | null = null;
  isLoading = true;
  errorMessage = '';

  constructor(private notificationService: NotificationService, private authService: AuthService) {}

  ngOnInit(): void {
    const userId = this.authService.getUserId();
    const role = this.authService.getRole();
    this.notificationService.getNotifications(userId.toString(), role).subscribe({
      next: (d) => { this.notifications = d || []; this.isLoading = false; },
      error: () => { this.errorMessage = 'Unable to load notifications.'; this.isLoading = false; }
    });
  }

  openDetail(n: Notification): void { this.selectedNotification = n; }
  closeDetail(): void { this.selectedNotification = null; }

  toggleRead(n: Notification, event: Event): void {
    event.stopPropagation();
    const newStatus = n.status === 'READ' ? 'UNREAD' : 'READ';
    this.notificationService.markAsRead(n.notificationId).subscribe({
      next: () => { n.status = newStatus as any; },
      error: () => { n.status = newStatus as any; }
    });
  }

  markAllRead(): void {
    const userId = this.authService.getUserId();
    const role = this.authService.getRole();
    this.notificationService.markAllRead(userId.toString(), role).subscribe({
      next: () => { this.notifications.forEach(n => n.status = 'READ'); },
      error: () => { this.notifications.forEach(n => n.status = 'READ'); }
    });
  }

  getCategoryIcon(cat: string): string {
    switch (cat) {
      case 'CONSENT': return 'fas fa-handshake';
      case 'SECURITY': return 'fas fa-shield-alt';
      case 'USAGE': return 'fas fa-chart-bar';
      case 'INCIDENT': return 'fas fa-exclamation-triangle';
      default: return 'fas fa-bell';
    }
  }

  getCategoryColor(cat: string): string {
    switch (cat) {
      case 'CONSENT': return '#667eea';
      case 'SECURITY': return '#ef4444';
      case 'USAGE': return '#10b981';
      case 'INCIDENT': return '#f59e0b';
      default: return '#6b7280';
    }
  }

  get unreadCount(): number {
    return this.notifications.filter(n => n.status === 'UNREAD').length;
  }
}
