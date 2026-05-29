import {
  HttpInterceptorFn,
  HttpErrorResponse,
  HttpResponse
} from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { catchError, map, throwError } from 'rxjs';


export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  const request = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(request).pipe(
    map(event => {
      if (!(event instanceof HttpResponse)) return event;

      const body: any = event.body;
      if (body && typeof body === 'object'
          && 'data' in body
          && 'accessToken' in body
          && 'generatedAt' in body) {
        return event.clone({ body: body.data });
      }
      return event;
    }),
    catchError((error: HttpErrorResponse) => throwError(() => error))
  );
};
