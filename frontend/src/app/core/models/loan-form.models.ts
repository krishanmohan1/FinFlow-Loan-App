export interface LoanApplicationRequest {
  amount: number;
  loanType: string;
  tenureMonths: number;
  purpose: string;
}

export interface LoanDecisionRequestForm {
  decision: 'APPROVED' | 'REJECTED';
  remarks: string;
  interestRate?: number | null;
  tenureMonths?: number | null;
  sanctionedAmount?: number | null;
}
