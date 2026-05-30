import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AccountRef, TransactionRef } from '../models/models';

@Injectable({ providedIn: 'root' })
export class AccountService {

  private apiUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

 
  getAccounts(userId: number, tppAppId?: number | null): Observable<AccountRef[]> {
    return this.http.get<AccountRef[]>(
      `${this.apiUrl}/aisp/accounts/user/${userId}`,
      { headers: this.tppHeaders(tppAppId) }
    );
  }

  getTransactions(accountId: number, tppAppId?: number | null): Observable<TransactionRef[]> {
    return this.http.get<TransactionRef[]>(
      `${this.apiUrl}/aisp/accounts/${accountId}/transactions`,
      { headers: this.tppHeaders(tppAppId) }
    );
  }

  private tppHeaders(tppAppId?: number | null): HttpHeaders | undefined {
    if (!tppAppId) return undefined;
    return new HttpHeaders({ 'X-TPP-App-Id': String(tppAppId) });
  }
}
