import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, timeout } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User } from '../models/models';


const USER_REQUEST_TIMEOUT_MS = 20000;

@Injectable({ providedIn: 'root' })
export class UserService {

  private api = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.api}/users`)
      .pipe(timeout(USER_REQUEST_TIMEOUT_MS));
  }

  createUser(user: Partial<User>): Observable<User> {
    return this.http.post<User>(`${this.api}/users`, user)
      .pipe(timeout(USER_REQUEST_TIMEOUT_MS));
  }

  updateUser(id: number, user: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.api}/users/${id}`, user)
      .pipe(timeout(USER_REQUEST_TIMEOUT_MS));
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/users/${id}`)
      .pipe(timeout(USER_REQUEST_TIMEOUT_MS));
  }
}
