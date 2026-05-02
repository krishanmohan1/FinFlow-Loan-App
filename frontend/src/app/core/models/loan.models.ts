export type LoanStatus =
  | 'PENDING'
  | 'UNDER_REVIEW'
  | 'OFFER_MADE'
  | 'ACTIVE'
  | 'REJECTED'
  | 'WITHDRAWN'
  | 'OFFER_DECLINED'
  | string;

export type BorrowerDecision = 'PENDING' | 'ACCEPTED' | 'DECLINED' | string;

export interface LoanApplication {
  id?: number;
  username?: string;
  amount: number;
  status?: LoanStatus;
  loanType: string;
  tenureMonths: number;
  purpose: string;
  appliedAt?: string;
  remarks?: string;
  sanctionedAmount?: number;
  interestRate?: number;
  processingFee?: number;
  gstAmount?: number;
  monthlyEmi?: number;
  firstEmiDate?: string;
  borrowerDecision?: BorrowerDecision;
  borrowerDecisionAt?: string;
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
  OFFER_MADE?: number;
  ACTIVE?: number;
  OFFER_DECLINED?: number;
  WITHDRAWN?: number;
  [key: string]: number | undefined;
}

export interface LoanOfferResponseRequest {
  borrowerDecision: 'ACCEPTED' | 'DECLINED';
  borrowerRemarks?: string;
}
