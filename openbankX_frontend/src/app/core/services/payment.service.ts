import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, timeout } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PaymentInitiation, PaymentInitiationRequest } from '../models/models';

const PAYMENT_REQUEST_TIMEOUT_MS = 20000;

@Injectable({ providedIn: 'root' })
export class PaymentService {

  private apiUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  initiatePayment(payload: PaymentInitiationRequest): Observable<PaymentInitiation> {
    return this.http
      .post<PaymentInitiation>(
        `${this.apiUrl}/pisp/payments`,
        payload,
        { headers: this.tppHeaders(payload.tppAppId) }
      )
      .pipe(timeout(PAYMENT_REQUEST_TIMEOUT_MS));
  }

  executePayment(paymentId: number, tppAppId?: number | null): Observable<PaymentInitiation> {
    return this.http
      .post<PaymentInitiation>(
        `${this.apiUrl}/pisp/payments/${paymentId}/execute`,
        {},
        { headers: this.tppHeaders(tppAppId) }
      )
      .pipe(timeout(PAYMENT_REQUEST_TIMEOUT_MS));
  }

  private tppHeaders(tppAppId?: number | null): HttpHeaders | undefined {
    if (!tppAppId) return undefined;
    return new HttpHeaders({ 'X-TPP-App-Id': String(tppAppId) });
  }
}
