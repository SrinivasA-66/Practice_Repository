import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

import {
  APIProduct,
  APIPlan,
  TPPApp,
  TPPSubscription
} from '../models/models';

@Injectable({ providedIn: 'root' })
export class ProductService {

  private apiUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

 
  getProducts(): Observable<APIProduct[]> {
    return this.http.get<APIProduct[]>(`${this.apiUrl}/products`);
  }

  createProduct(product: Partial<APIProduct>): Observable<APIProduct> {
    return this.http.post<APIProduct>(`${this.apiUrl}/products`, product);
  }

  updateProduct(id: number, product: Partial<APIProduct>): Observable<APIProduct> {
    return this.http.put<APIProduct>(
      `${this.apiUrl}/products/${id}`,
      product
    );
  }

  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/products/${id}`);
  }


  getPlans(): Observable<APIPlan[]> {
    return this.http.get<APIPlan[]>(`${this.apiUrl}/api-plans`);
  }

  getPlansByProduct(productId: number): Observable<APIPlan[]> {
    return this.http.get<APIPlan[]>(
      `${this.apiUrl}/api-plans/product/${productId}`
    );
  }

 
  createPlan(plan: Partial<APIPlan>): Observable<APIPlan> {
    return this.http.post<APIPlan>(
      `${this.apiUrl}/api-plans`,
      plan
    );
  }


  updatePlan(id: number, plan: Partial<APIPlan>): Observable<APIPlan> {
    return this.http.put<APIPlan>(
      `${this.apiUrl}/api-plans/${id}`,
      plan
    );
  }


  deletePlan(id: number): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/api-plans/${id}`
    );
  }

 
  getApps(): Observable<TPPApp[]> {
    return this.http.get<TPPApp[]>(`${this.apiUrl}/apps`);
  }


  getSubscriptions(): Observable<TPPSubscription[]> {
    return this.http.get<TPPSubscription[]>(
      `${this.apiUrl}/subscriptions`
    );
  }

  getSubscriptionsByApp(tppAppId: number): Observable<TPPSubscription[]> {
    return this.http.get<TPPSubscription[]>(
      `${this.apiUrl}/subscriptions/app/${tppAppId}`
    );
  }

  createSubscription(
    sub: Partial<TPPSubscription>
  ): Observable<TPPSubscription> {
    return this.http.post<TPPSubscription>(
      `${this.apiUrl}/subscriptions`,
      sub
    );
  }


  cancelSubscription(subscriptionId: number): Observable<TPPSubscription> {
    return this.http.put<TPPSubscription>(
      `${this.apiUrl}/subscriptions/${subscriptionId}/cancel`,
      {}
    );
  }
}