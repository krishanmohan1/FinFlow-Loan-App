import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiRoutes } from '@core/config/api.config';
import { LoanApplication } from '@core/models/loan.models';
import { HttpApiService } from './http-api.service';

@Injectable({ providedIn: 'root' })
export class LoanService {
  private readonly api = inject(HttpApiService);

  apply(loan: LoanApplication): Observable<LoanApplication> {
    return this.api.post<LoanApplication>(ApiRoutes.loans.apply, loan);
  }

  getLoans(): Observable<LoanApplication[]> {
    return this.api.get<LoanApplication[]>(ApiRoutes.loans.all);
  }

  getLoan(id: number): Observable<LoanApplication> {
    return this.api.get<LoanApplication>(ApiRoutes.loans.byId(id));
  }
}
