import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize, forkJoin } from 'rxjs';
import { UserProfile } from '@core/models/auth.models';
import { LoanDocument } from '@core/models/document.models';
import { LoanApplication } from '@core/models/loan.models';
import { AdminReport } from '@core/models/report.models';
import { AdminService } from '@core/services/admin.service';
import { StatusPillComponent } from '@shared/components/status-pill.component';

@Component({
  selector: 'ff-admin-dashboard',
  standalone: true,
  imports: [ReactiveFormsModule, CurrencyPipe, DatePipe, StatusPillComponent],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css'
})
export class AdminDashboardComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly adminApi = inject(AdminService);

  protected readonly loans = signal<LoanApplication[]>([]);
  protected readonly documents = signal<LoanDocument[]>([]);
  protected readonly users = signal<UserProfile[]>([]);
  protected readonly report = signal<AdminReport | null>(null);
  protected readonly isLoading = signal(true);
  protected readonly message = signal('');
  protected readonly error = signal('');

  protected readonly pendingLoans = computed(() => this.loans().filter((loan) => loan.status === 'PENDING').length);
  protected readonly verifiedDocuments = computed(() => this.documents().filter((doc) => doc.verificationStatus === 'VERIFIED').length);
  protected readonly activeUsers = computed(() => this.users().filter((user) => user.active).length);

  protected readonly decisionForm = this.fb.nonNullable.group({
    loanId: [1, [Validators.required, Validators.min(1)]],
    decision: ['APPROVED' as 'APPROVED' | 'REJECTED', [Validators.required]],
    remarks: ['Approved after verification', [Validators.required]],
    interestRate: [8.5, [Validators.min(0)]],
    tenureMonths: [60, [Validators.min(1)]],
    sanctionedAmount: [48000, [Validators.min(0)]]
  });

  protected readonly documentForm = this.fb.nonNullable.group({
    documentId: [1, [Validators.required, Validators.min(1)]],
    status: ['VERIFIED' as 'VERIFIED' | 'REJECTED', [Validators.required]],
    remarks: ['Document looks valid', [Validators.required]]
  });

  protected readonly userForm = this.fb.nonNullable.group({
    userId: [1, [Validators.required, Validators.min(1)]],
    role: ['USER' as 'USER' | 'ADMIN', [Validators.required]],
    active: [true]
  });

  ngOnInit(): void {
    this.loadAdminData();
  }

  loadAdminData(): void {
    this.isLoading.set(true);
    forkJoin({
      loans: this.adminApi.loans(),
      documents: this.adminApi.documents(),
      users: this.adminApi.users(),
      report: this.adminApi.report()
    })
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        next: ({ loans, documents, users, report }) => {
          this.loans.set(loans);
          this.documents.set(documents);
          this.users.set(users);
          this.report.set(report);
        },
        error: () => this.error.set('Admin data failed to load. Confirm all backend services are UP in Eureka.')
      });
  }

  decideLoan(): void {
    if (this.decisionForm.invalid) {
      this.decisionForm.markAllAsTouched();
      return;
    }

    const { loanId, ...request } = this.decisionForm.getRawValue();
    this.runAction(
      this.adminApi.decideLoan(loanId, request),
      `Loan #${loanId} decision submitted.`
    );
  }

  markUnderReview(loanId: number | undefined): void {
    if (!loanId) return;
    this.runAction(this.adminApi.markUnderReview(loanId), `Loan #${loanId} moved under review.`);
  }

  verifyDocument(): void {
    if (this.documentForm.invalid) {
      this.documentForm.markAllAsTouched();
      return;
    }

    const { documentId, ...request } = this.documentForm.getRawValue();
    this.runAction(
      this.adminApi.verifyDocument(documentId, request),
      `Document #${documentId} verification updated.`
    );
  }

  updateUser(): void {
    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched();
      return;
    }

    const { userId, ...request } = this.userForm.getRawValue();
    this.runAction(
      this.adminApi.updateUser(userId, request),
      `User #${userId} profile updated.`
    );
  }

  private runAction<T>(request$: import('rxjs').Observable<T>, successMessage: string): void {
    this.message.set('');
    this.error.set('');
    request$.subscribe({
      next: () => {
        this.message.set(successMessage);
        this.loadAdminData();
      },
      error: (error) => this.error.set(error?.error?.error ?? 'Admin action failed.')
    });
  }
}
