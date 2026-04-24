export type VerificationStatus = 'PENDING' | 'VERIFIED' | 'REJECTED' | string;

export interface LoanDocument {
  id: number;
  username: string;
  loanId: string;
  documentType: string;
  fileName: string;
  filePath: string;
  verificationStatus: VerificationStatus;
  verifiedRemarks?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface DocumentVerificationRequest {
  status: 'VERIFIED' | 'REJECTED';
  remarks: string;
}

export interface DocumentUploadPayload {
  file: File;
  loanId: string;
  documentType: string;
}
