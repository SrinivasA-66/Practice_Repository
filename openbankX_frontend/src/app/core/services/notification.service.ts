import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Notification } from '../models/models';


@Injectable({ providedIn: 'root' })
export class NotificationService {

  private apiUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  getNotifications(recipientId: string, role?: string): Observable<Notification[]> {
    let url = `${this.apiUrl}/notifications?recipientId=${recipientId}`;
    if (role) {
      url += `&role=${role}`;
    }
    return this.http.get<Notification[]>(url);
  }

  markAsRead(id: number): Observable<Notification> {
    return this.http.put<Notification>(`${this.apiUrl}/notifications/${id}`, { status: 'READ' });
  }

  markAllRead(recipientId: string, role?: string): Observable<void> {
    let url = `${this.apiUrl}/notifications/mark-all-read?recipientId=${recipientId}`;
    if (role) {
      url += `&role=${role}`;
    }
    return this.http.put<void>(url, {});
  }
}

