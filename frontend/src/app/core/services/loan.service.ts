import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiRoutes } from '@core/config/api.config';
import { LoanApplication } from '@core/models/loan.models';
import { LoanApplicationRequest } from '@core/models/loan-form.models';
import { HttpApiService } from './http-api.service';

@Injectable({ providedIn: 'root' })
export class LoanService {
  private readonly api = inject(HttpApiService);

  apply(loan: LoanApplicationRequest): Observable<LoanApplication> {
    return this.api.post<LoanApplication>(ApiRoutes.loans.apply, loan);
  }

  getLoans(): Observable<LoanApplication[]> {
    return this.api.get<LoanApplication[]>(ApiRoutes.loans.all);
  }

  getLoan(id: number): Observable<LoanApplication> {
    return this.api.get<LoanApplication>(ApiRoutes.loans.byId(id));
  }

  withdraw(id: number): Observable<LoanApplication> {
    return this.api.put<LoanApplication>(ApiRoutes.loans.withdraw(id), {});
  }
}
