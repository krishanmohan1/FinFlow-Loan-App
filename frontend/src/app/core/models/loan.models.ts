export type LoanStatus = 'PENDING' | 'UNDER_REVIEW' | 'APPROVED' | 'REJECTED' | string;

export interface LoanApplication {
  id?: number;
  username?: string;
  amount: number;
  status?: LoanStatus;
  loanType: string;
  purpose: string;
  appliedAt?: string;
  remarks?: string;
}

export interface LoanDecisionRequest {
  decision: 'APPROVED' | 'REJECTED';
  remarks: string;
  interestRate?: number;
  tenureMonths?: number;
  sanctionedAmount?: number;
}

export interface LoanStatusUpdateRequest {
  status: LoanStatus;
  remarks: string;
}

export interface LoanStatusCounts {
  PENDING?: number;
  APPROVED?: number;
  REJECTED?: number;
  UNDER_REVIEW?: number;
  [key: string]: number | undefined;
}
