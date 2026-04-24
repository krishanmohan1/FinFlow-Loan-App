import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL, ApiRoutes } from '@core/config/api.config';
import { DocumentUploadPayload, LoanDocument } from '@core/models/document.models';
import { HttpClient } from '@angular/common/http';
import { HttpApiService } from './http-api.service';

@Injectable({ providedIn: 'root' })
export class DocumentService {
  private readonly http = inject(HttpClient);
  private readonly api = inject(HttpApiService);

  upload(payload: DocumentUploadPayload): Observable<LoanDocument> {
    const formData = new FormData();
    formData.append('file', payload.file);
    formData.append('loanId', payload.loanId);
    formData.append('documentType', payload.documentType);

    // Multipart upload must not set Content-Type manually; the browser adds the boundary.
    return this.http.post<LoanDocument>(`${API_BASE_URL}${ApiRoutes.documents.upload}`, formData);
  }

  mine(): Observable<LoanDocument[]> {
    return this.api.get<LoanDocument[]>(ApiRoutes.documents.mine);
  }

  byLoan(loanId: string): Observable<LoanDocument[]> {
    return this.api.get<LoanDocument[]>(ApiRoutes.documents.byLoan(loanId));
  }
}
