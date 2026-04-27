import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiRoutes } from '@core/config/api.config';
import { DocumentVerificationRequest, LoanDocument } from '@core/models/document.models';
import { AdminReport } from '@core/models/report.models';
import { LoanApplication, LoanDecisionRequest, LoanStatusCounts } from '@core/models/loan.models';
import { StaffRegistrationRequest, UserProfile, UserUpdateRequest } from '@core/models/auth.models';
import { HttpApiService } from './http-api.service';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private readonly api = inject(HttpApiService);

  loans(): Observable<LoanApplication[]> {
    return this.api.get<LoanApplication[]>(ApiRoutes.admin.loans);
  }

  documents(): Observable<LoanDocument[]> {
    return this.api.get<LoanDocument[]>(ApiRoutes.admin.documents);
  }

  users(): Observable<UserProfile[]> {
    return this.api.get<UserProfile[]>(ApiRoutes.admin.users);
  }

  registerStaff(request: StaffRegistrationRequest): Observable<UserProfile> {
    return this.api.post<UserProfile>(ApiRoutes.admin.staff, request);
  }

  report(): Observable<AdminReport> {
    return this.api.get<AdminReport>(ApiRoutes.admin.reports);
  }

  counts(): Observable<LoanStatusCounts> {
    return this.api.get<LoanStatusCounts>(ApiRoutes.admin.counts);
  }

  decideLoan(id: number, request: LoanDecisionRequest): Observable<LoanApplication> {
    return this.api.post<LoanApplication>(ApiRoutes.admin.decision(id), request);
  }

  markUnderReview(id: number): Observable<LoanApplication> {
    return this.api.put<LoanApplication>(ApiRoutes.admin.review(id), {});
  }

  verifyDocument(id: number, request: DocumentVerificationRequest): Observable<LoanDocument> {
    return this.api.put<LoanDocument>(ApiRoutes.admin.verifyDocument(id), request);
  }

  updateUser(id: number, request: UserUpdateRequest): Observable<UserProfile> {
    return this.api.put<UserProfile>(ApiRoutes.admin.userById(id), request);
  }
}
