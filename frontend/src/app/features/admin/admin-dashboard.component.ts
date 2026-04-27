import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize, forkJoin } from 'rxjs';
import { StaffRegistrationRequest, UserProfile } from '@core/models/auth.models';
import { LoanDocument } from '@core/models/document.models';
import { LoanApplication, LoanDecisionRequest } from '@core/models/loan.models';
import { AdminReport } from '@core/models/report.models';
import { AdminService } from '@core/services/admin.service';
import { resolveApiError } from '@core/utils/api-error.util';
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
  protected readonly isActing = signal(false);
  protected readonly isCreatingStaff = signal(false);
  protected readonly message = signal('');
  protected readonly error = signal('');

  protected readonly pendingLoans = computed(() => this.loans().filter((loan) => loan.status === 'PENDING').length);
  protected readonly verifiedDocuments = computed(() => this.documents().filter((doc) => doc.verificationStatus === 'VERIFIED').length);
  protected readonly activeUsers = computed(() => this.users().filter((user) => user.active).length);
  protected readonly rejectedDocuments = computed(() => this.documents().filter((doc) => doc.verificationStatus === 'REJECTED').length);

  protected readonly decisionForm = this.fb.nonNullable.group({
    loanId: [1, [Validators.required, Validators.min(1)]],
    decision: ['APPROVED' as 'APPROVED' | 'REJECTED', [Validators.required]],
    remarks: ['Approved after verification and financial review.', [Validators.maxLength(500)]],
    interestRate: [8.5, [Validators.min(0.1)]],
    tenureMonths: [60, [Validators.min(1), Validators.max(600)]],
    sanctionedAmount: [48000, [Validators.min(0.1)]]
  });

  protected readonly documentForm = this.fb.nonNullable.group({
    documentId: [1, [Validators.required, Validators.min(1)]],
    status: ['VERIFIED' as 'VERIFIED' | 'REJECTED', [Validators.required]],
    remarks: ['Document looks valid and matches applicant details.', [Validators.maxLength(500)]]
  });

  protected readonly userForm = this.fb.nonNullable.group({
    userId: [1, [Validators.required, Validators.min(1)]],
    role: ['USER' as 'USER' | 'ADMIN', [Validators.required]],
    active: [true]
  });

  protected readonly staffForm = this.fb.nonNullable.group({
    fullName: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(80)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(120)]],
    phoneNumber: ['', [Validators.required, Validators.pattern(/^[6-9][0-9]{9}$/)]],
    dateOfBirth: ['1990-01-01', [Validators.required]],
    addressLine1: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(120)]],
    city: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(60)]],
    state: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(60)]],
    postalCode: ['', [Validators.required, Validators.pattern(/^[1-9][0-9]{5}$/)]],
    occupation: ['Credit Operations', [Validators.required, Validators.minLength(2), Validators.maxLength(60)]],
    annualIncome: [800000, [Validators.required, Validators.min(0)]],
    username: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(30), Validators.pattern(/^[A-Za-z][A-Za-z0-9_]{3,29}$/)]],
    password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(64)]]
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
        error: (error) => this.error.set(resolveApiError(error, 'Admin data failed to load.'))
      });
  }

  selectLoan(loan: LoanApplication): void {
    if (!loan.id) {
      return;
    }
    this.decisionForm.patchValue({
      loanId: loan.id,
      decision: loan.status === 'REJECTED' ? 'REJECTED' : 'APPROVED',
      remarks: loan.remarks || 'Approved after verification and financial review.',
      sanctionedAmount: loan.amount,
      interestRate: 8.5,
      tenureMonths: 60
    });
    this.message.set(`Loan #${loan.id} loaded into the decision workspace.`);
    this.error.set('');
  }

  selectDocument(document: LoanDocument): void {
    this.documentForm.patchValue({
      documentId: document.id,
      status: document.verificationStatus === 'REJECTED' ? 'REJECTED' : 'VERIFIED',
      remarks: document.verifiedRemarks || 'Document looks valid and matches applicant details.'
    });
    this.message.set(`Document #${document.id} loaded into the verification workspace.`);
    this.error.set('');
  }

  selectUser(user: UserProfile): void {
    this.userForm.patchValue({
      userId: user.id,
      role: user.role,
      active: user.active
    });
    this.message.set(`User #${user.id} loaded into the user controls workspace.`);
    this.error.set('');
  }

  decisionRemarksError(): string {
    const control = this.decisionForm.controls.remarks;
    if (!control.touched && !control.dirty) {
      return '';
    }
    if (control.hasError('maxlength')) {
      return 'Remarks must be 500 characters or fewer.';
    }
    return '';
  }

  documentRemarksError(): string {
    const control = this.documentForm.controls.remarks;
    if (!control.touched && !control.dirty) {
      return '';
    }
    if (control.hasError('maxlength')) {
      return 'Remarks must be 500 characters or fewer.';
    }
    return '';
  }

  decideLoan(): void {
    if (this.decisionForm.invalid) {
      this.decisionForm.markAllAsTouched();
      return;
    }

    const { loanId, ...request } = this.decisionForm.getRawValue();
    const payload: LoanDecisionRequest = {
      decision: request.decision,
      remarks: request.remarks,
      interestRate: request.interestRate ?? undefined,
      tenureMonths: request.tenureMonths ?? undefined,
      sanctionedAmount: request.sanctionedAmount ?? undefined
    };
    this.runAction(
      this.adminApi.decideLoan(loanId, payload),
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

  createStaffAccount(): void {
    if (this.staffForm.invalid) {
      this.staffForm.markAllAsTouched();
      this.error.set('Please complete the internal staff onboarding form before creating the account.');
      return;
    }

    this.message.set('');
    this.error.set('');
    this.isCreatingStaff.set(true);
    this.adminApi.registerStaff(this.staffForm.getRawValue() as StaffRegistrationRequest)
      .pipe(finalize(() => this.isCreatingStaff.set(false)))
      .subscribe({
        next: (user) => {
          this.message.set(`Admin account for ${user.username} is ready. The new staff user can now sign in from the login page.`);
          this.staffForm.reset({
            fullName: '',
            email: '',
            phoneNumber: '',
            dateOfBirth: '1990-01-01',
            addressLine1: '',
            city: '',
            state: '',
            postalCode: '',
            occupation: 'Credit Operations',
            annualIncome: 800000,
            username: '',
            password: ''
          });
          this.loadAdminData();
        },
        error: (error) => this.error.set(resolveApiError(error, 'Internal staff onboarding failed.'))
      });
  }

  staffFieldError(
    field:
      | 'fullName'
      | 'email'
      | 'phoneNumber'
      | 'dateOfBirth'
      | 'addressLine1'
      | 'city'
      | 'state'
      | 'postalCode'
      | 'occupation'
      | 'annualIncome'
      | 'username'
      | 'password'
  ): string {
    const control = this.staffForm.controls[field];
    if (!control.touched && !control.dirty) {
      return '';
    }
    if (control.hasError('required')) {
      return 'This field is required.';
    }
    if (control.hasError('email')) {
      return 'Enter a valid email address.';
    }
    if (control.hasError('pattern')) {
      if (field === 'phoneNumber') {
        return 'Enter a valid 10-digit Indian mobile number.';
      }
      if (field === 'postalCode') {
        return 'Enter a valid 6-digit PIN code.';
      }
      if (field === 'username') {
        return 'Start with a letter and use only letters, numbers, and underscores.';
      }
    }
    if (control.hasError('minlength') || control.hasError('maxlength')) {
      return 'Please enter a value within the allowed length.';
    }
    if (control.hasError('min')) {
      return 'Value cannot be negative.';
    }
    return '';
  }

  private runAction<T>(request$: import('rxjs').Observable<T>, successMessage: string): void {
    this.message.set('');
    this.error.set('');
    this.isActing.set(true);
    request$.pipe(finalize(() => this.isActing.set(false))).subscribe({
      next: () => {
        this.message.set(successMessage);
        this.loadAdminData();
      },
      error: (error) => this.error.set(resolveApiError(error, 'Admin action failed.'))
    });
  }
}
