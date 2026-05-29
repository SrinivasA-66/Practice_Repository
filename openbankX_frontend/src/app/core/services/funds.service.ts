import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, timeout } from 'rxjs';
import { environment } from '../../../environments/environment';
import { FundsCheck } from '../models/models';

const FUNDS_REQUEST_TIMEOUT_MS = 20000;

export interface FundsCheckRequest {
  tppAppId: number;
  accountId: number;
  amount: number;
  currency?: string;
}


@Injectable({ providedIn: 'root' })
export class FundsService {

  private apiUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  checkFunds(req: FundsCheckRequest): Observable<FundsCheck> {
    const body = {
      tppApp:    { tppAppId: req.tppAppId },
      accountRef:{ accountId: req.accountId },
      amount:    req.amount,
      currency:  req.currency || 'GBP'
    };
    const headers = new HttpHeaders({ 'X-TPP-App-Id': String(req.tppAppId) });
    return this.http
      .post<FundsCheck>(`${this.apiUrl}/cbpii/funds-check`, body, { headers })
      .pipe(timeout(FUNDS_REQUEST_TIMEOUT_MS));
  }
}
