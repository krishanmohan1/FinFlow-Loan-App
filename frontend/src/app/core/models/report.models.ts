import { LoanDocument } from './document.models';
import { LoanApplication } from './loan.models';
import { UserProfile } from './auth.models';

export interface AdminReport {
  allLoans: LoanApplication[];
  pendingLoans: LoanApplication[];
  approvedLoans?: LoanApplication[];
  rejectedLoans: LoanApplication[];
  underReviewLoans: LoanApplication[];
  offeredLoans?: LoanApplication[];
  activeLoans?: LoanApplication[];
  allDocuments: LoanDocument[];
  allUsers: UserProfile[];
  generatedAt: string;
}
