import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize, forkJoin } from 'rxjs';
import { LoanDocument } from '@core/models/document.models';
import { LoanApplication } from '@core/models/loan.models';
import { AuthService } from '@core/services/auth.service';
import { DocumentService } from '@core/services/document.service';
import { LoanService } from '@core/services/loan.service';
import { StatusPillComponent } from '@shared/components/status-pill.component';

@Component({
  selector: 'ff-user-dashboard',
  standalone: true,
  imports: [ReactiveFormsModule, CurrencyPipe, DatePipe, StatusPillComponent],
  templateUrl: './user-dashboard.component.html',
  styleUrl: './user-dashboard.component.css'
})
export class UserDashboardComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly loansApi = inject(LoanService);
  private readonly documentsApi = inject(DocumentService);

  protected readonly username = this.auth.username;
  protected readonly loans = signal<LoanApplication[]>([]);
  protected readonly documents = signal<LoanDocument[]>([]);
  protected readonly isLoading = signal(true);
  protected readonly message = signal('');
  protected readonly error = signal('');
  protected readonly selectedFile = signal<File | null>(null);

  protected readonly pendingLoans = computed(() => this.loans().filter((loan) => loan.status === 'PENDING').length);
  protected readonly approvedLoans = computed(() => this.loans().filter((loan) => loan.status === 'APPROVED').length);
  protected readonly documentsPending = computed(() => this.documents().filter((doc) => doc.verificationStatus === 'PENDING').length);

  protected readonly loanForm = this.fb.nonNullable.group({
    amount: [50000, [Validators.required, Validators.min(1000)]],
    loanType: ['HOME', [Validators.required]],
    purpose: ['Buying house', [Validators.required, Validators.minLength(5)]]
  });

  protected readonly documentForm = this.fb.nonNullable.group({
    loanId: ['', [Validators.required]],
    documentType: ['AADHAAR', [Validators.required]]
  });

  ngOnInit(): void {
    this.loadWorkspace();
  }

  loadWorkspace(): void {
    this.isLoading.set(true);
    forkJoin({
      loans: this.loansApi.getLoans(),
      documents: this.documentsApi.mine()
    })
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        next: ({ loans, documents }) => {
          this.loans.set(loans);
          this.documents.set(documents);
        },
        error: () => this.error.set('Could not load your workspace. Please ensure backend containers are running.')
      });
  }

  submitLoan(): void {
    if (this.loanForm.invalid) {
      this.loanForm.markAllAsTouched();
      return;
    }

    this.message.set('');
    this.error.set('');
    this.loansApi.apply(this.loanForm.getRawValue()).subscribe({
      next: (loan) => {
        this.message.set(`Loan #${loan.id} submitted and routed through RabbitMQ.`);
        this.loanForm.reset({ amount: 50000, loanType: 'HOME', purpose: 'Buying house' });
        this.loadWorkspace();
      },
      error: (error) => this.error.set(error?.error?.error ?? 'Loan submission failed.')
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedFile.set(input.files?.[0] ?? null);
  }

  uploadDocument(): void {
    const file = this.selectedFile();
    if (this.documentForm.invalid || !file) {
      this.documentForm.markAllAsTouched();
      this.error.set('Choose a file and provide loan/document details.');
      return;
    }

    this.message.set('');
    this.error.set('');
    this.documentsApi.upload({ ...this.documentForm.getRawValue(), file }).subscribe({
      next: (document) => {
        this.message.set(`Document #${document.id} uploaded for loan ${document.loanId}.`);
        this.documentForm.reset({ loanId: '', documentType: 'AADHAAR' });
        this.selectedFile.set(null);
        this.loadWorkspace();
      },
      error: (error) => this.error.set(error?.error?.error ?? 'Document upload failed.')
    });
  }
}
