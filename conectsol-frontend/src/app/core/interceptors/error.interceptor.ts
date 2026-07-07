import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const authService = inject(AuthService);
  const snackBar = inject(MatSnackBar);

  return next(req).pipe(
    catchError((erro: HttpErrorResponse) => {
      if (erro.status === 401) {
        authService.logout();
        router.navigate(['/login']);
      } else if (erro.status === 403) {
        snackBar.open('Sem permissão para esta ação', 'Fechar', { duration: 4000 });
      } else if (erro.status === 500) {
        snackBar.open('Erro interno. Tente novamente.', 'Fechar', { duration: 4000 });
      }
      return throwError(() => erro);
    })
  );
};
