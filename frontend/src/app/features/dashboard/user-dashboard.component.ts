import { CurrencyPipe, DatePipe } from "@angular/common";
import { Component, OnInit, computed, inject, signal } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { finalize, forkJoin } from "rxjs";
import { ProfileUpdateRequest, UserProfile } from "@core/models/auth.models";
import { LoanDocument } from "@core/models/document.models";
import { LoanApplication, LoanOfferResponseRequest } from "@core/models/loan.models";
import { LoanApplicationRequest } from "@core/models/loan-form.models";
import { SelectOption } from "@core/models/ui.models";
import { AuthService } from "@core/services/auth.service";
import { DocumentService } from "@core/services/document.service";
import { LoanService } from "@core/services/loan.service";
import { resolveApiError } from "@core/utils/api-error.util";
import { StatusPillComponent } from "@shared/components/status-pill.component";

@Component({
  selector: "ff-user-dashboard",
  standalone: true,
  imports: [ReactiveFormsModule, CurrencyPipe, DatePipe, StatusPillComponent],
  templateUrl: "./user-dashboard.component.html",
  styleUrl: "./user-dashboard.component.css",
})
export class UserDashboardComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly loansApi = inject(LoanService);
  private readonly documentsApi = inject(DocumentService);

  protected readonly loanTypeOptions: SelectOption[] = [
    {
      value: "HOME",
      label: "Home loan",
      hint: "Property purchase, construction, or home improvement",
    },
    {
      value: "PERSONAL",
      label: "Personal loan",
      hint: "Medical, family, or planned lifestyle expenses",
    },
    {
      value: "EDUCATION",
      label: "Education loan",
      hint: "Tuition, accommodation, and study support",
    },
    {
      value: "BUSINESS",
      label: "Business loan",
      hint: "Working capital, expansion, or equipment",
    },
    {
      value: "AUTO",
      label: "Auto loan",
      hint: "Car, bike, or commercial vehicle financing",
    },
    {
      value: "GOLD",
      label: "Gold loan",
      hint: "Short-term secured credit against gold assets",
    },
  ];

  protected readonly documentTypeOptions: SelectOption[] = [
    { value: "AADHAAR", label: "Aadhaar card" },
    { value: "PAN", label: "PAN card" },
    { value: "SALARY_SLIP", label: "Salary slip" },
    { value: "BANK_STATEMENT", label: "Bank statement" },
    { value: "PROPERTY_PROOF", label: "Property proof" },
  ];

  protected readonly username = this.auth.username;
  protected readonly profile = signal<UserProfile | null>(null);
  protected readonly loans = signal<LoanApplication[]>([]);
  protected readonly documents = signal<LoanDocument[]>([]);
  protected readonly isLoading = signal(true);
  protected readonly isSavingProfile = signal(false);
  protected readonly isSubmittingLoan = signal(false);
  protected readonly isUploadingDocument = signal(false);
  protected readonly isRespondingToOffer = signal(false);
  protected readonly message = signal("");
  protected readonly error = signal("");
  protected readonly selectedFile = signal<File | null>(null);
  protected readonly activeStep = signal<1 | 2 | 3 | 4>(1);

  protected readonly pendingLoans = computed(
    () => this.loans().filter((loan) => loan.status === "PENDING").length,
  );
  protected readonly activeLoans = computed(
    () => this.loans().filter((loan) => loan.status === "ACTIVE").length,
  );
  protected readonly documentsPending = computed(
    () =>
      this.documents().filter((doc) => doc.verificationStatus === "PENDING")
        .length,
  );
  protected readonly currentStage = computed(() => {
    if (this.loans().length === 0) {
      return 1;
    }
    if (this.documents().length === 0) {
      return 2;
    }
    const hasOfferOrActiveLoan = this.loans().some(
      (loan) => loan.status === "OFFER_MADE" || loan.status === "ACTIVE",
    );
    if (hasOfferOrActiveLoan) {
      return 4;
    }
    return 3;
  });
  protected readonly flowSteps = [
    {
      id: 1,
      title: "Account created",
      detail: "You can now work inside your private FinFlow workspace.",
    },
    {
      id: 2,
      title: "Loan submitted",
      detail: "Your application is now moving through the lending pipeline.",
    },
    {
      id: 3,
      title: "Documents under review",
      detail: "Upload supporting files so the admin team can verify them.",
    },
    {
      id: 4,
      title: "Decision available",
      detail:
        "Track offers, active loans, rejected cases, and admin remarks.",
    },
  ] as const;
  protected readonly availableLoanIds = computed(() =>
    this.loans()
      .filter((loan) => typeof loan.id === "number")
      .map((loan) => ({
        id: String(loan.id),
        label: `#${loan.id} - ${loan.loanType} - ${loan.amount.toLocaleString("en-IN", { maximumFractionDigits: 0 })}`,
      })),
  );
  protected readonly recentLoans = computed(() => this.loans().slice(0, 5));
  protected readonly nextRecommendedStep = computed<1 | 2 | 3 | 4>(() => {
    if (this.loans().length === 0) {
      return 2;
    }
    if (this.documents().length === 0) {
      return 3;
    }
    return 4;
  });
  protected readonly profileFields = computed(() => {
    const profile = this.profile();
    return [
      {
        label: "Username",
        value: profile?.username || this.username() || "Not available",
      },
      {
        label: "Role",
        value: profile?.role === "ADMIN" ? "Admin" : "Applicant",
      },
      {
        label: "Profile status",
        value: this.loans().length > 0 ? "Active borrower" : "Ready to apply",
      },
    ];
  });
  protected readonly requiredDocuments = computed(() => {
    const selectedType = this.loanForm.controls.loanType.value;
    const common = ["Aadhaar card", "PAN card"];
    if (selectedType === "HOME") {
      return [...common, "Bank statement", "Property proof"];
    }
    if (selectedType === "EDUCATION") {
      return [...common, "Bank statement", "Admission or fee proof"];
    }
    if (selectedType === "BUSINESS") {
      return [...common, "Bank statement", "Business proof"];
    }
    if (selectedType === "AUTO") {
      return [...common, "Bank statement", "Vehicle quotation"];
    }
    if (selectedType === "GOLD") {
      return [...common, "Gold ownership proof"];
    }
    return [...common, "Salary slip", "Bank statement"];
  });
  protected readonly selectedLoanTypeHint = computed(
    () =>
      this.loanTypeOptions.find(
        (option) => option.value === this.loanForm.controls.loanType.value,
      )?.hint ?? "",
  );
  protected readonly documentProgress = computed(() => {
    const uploaded = new Set(
      this.documents().map((document) => document.documentType.toUpperCase()),
    );
    return this.requiredDocuments().map((item) => {
      const normalized = this.normalizeDocumentChecklistItem(item);
      return {
        label: item,
        done: uploaded.has(normalized),
      };
    });
  });
  protected readonly latestLoan = computed(() => this.loans()[0] ?? null);
  protected readonly offeredLoans = computed(() =>
    this.loans().filter((loan) => loan.status === "OFFER_MADE"),
  );
  protected readonly timelineEntries = computed(() => {
    const entries: Array<{
      title: string;
      detail: string;
      state: "done" | "active" | "upcoming";
    }> = [];
    const profile = this.profile();
    if (profile) {
      entries.push({
        title: "Borrower profile confirmed",
        detail: `${profile.fullName} is ready for application review.`,
        state: "done",
      });
    }
    const loan = this.latestLoan();
    if (loan) {
      entries.push({
        title: `Loan #${loan.id} submitted`,
        detail: `${loan.loanType} request for INR ${loan.amount.toLocaleString("en-IN")} is ${loan.status?.toLowerCase().replace(/_/g, " ") ?? "pending"}.`,
        state: "done",
      });
    } else {
      entries.push({
        title: "Loan application pending",
        detail: "Choose a product, amount, and tenure to create your first request.",
        state: "active",
      });
    }

    if (this.documents().length > 0) {
      entries.push({
        title: "Supporting documents uploaded",
        detail: `${this.documents().length} document(s) are available for admin verification.`,
        state: "done",
      });
    } else {
      entries.push({
        title: "Document upload pending",
        detail: "Upload borrower proofs against the selected loan to keep the case moving.",
        state: loan ? "active" : "upcoming",
      });
    }

    const offered = this.loans().find((item) => item.status === "OFFER_MADE");
    const activeLoan = this.loans().find((item) => item.status === "ACTIVE");
    if (activeLoan) {
      entries.push({
        title: "Decision published",
        detail: `Loan #${activeLoan.id} is now active. Review EMI, due date, and repayment details below.`,
        state: "done",
      });
    } else if (offered) {
      entries.push({
        title: "Offer ready for your approval",
        detail: `Loan #${offered.id} has a sanctioned offer waiting for your response.`,
        state: "active",
      });
    } else {
      entries.push({
        title: "Decision in progress",
        detail: "Track admin remarks and status updates once review starts.",
        state: this.documents().length > 0 ? "active" : "upcoming",
      });
    }

    return entries;
  });

  protected readonly loanForm = this.fb.nonNullable.group({
    amount: [50000, [Validators.required, Validators.min(1000)]],
    loanType: ["HOME", [Validators.required]],
    tenureMonths: [
      120,
      [Validators.required, Validators.min(6), Validators.max(360)],
    ],
    purpose: [
      "Buying my first home near work and family support.",
      [
        Validators.required,
        Validators.minLength(10),
        Validators.maxLength(255),
      ],
    ],
  });

  protected readonly documentForm = this.fb.nonNullable.group({
    loanId: ["", [Validators.required]],
    documentType: ["AADHAAR", [Validators.required]],
  });

  protected readonly profileForm = this.fb.nonNullable.group({
    fullName: [
      "",
      [Validators.required, Validators.minLength(3), Validators.maxLength(80)],
    ],
    email: [
      "",
      [Validators.required, Validators.email, Validators.maxLength(120)],
    ],
    phoneNumber: [
      "",
      [Validators.required, Validators.pattern(/^[6-9][0-9]{9}$/)],
    ],
    dateOfBirth: ["", [Validators.required]],
    addressLine1: [
      "",
      [
        Validators.required,
        Validators.minLength(10),
        Validators.maxLength(120),
      ],
    ],
    city: [
      "",
      [Validators.required, Validators.minLength(2), Validators.maxLength(60)],
    ],
    state: [
      "",
      [Validators.required, Validators.minLength(2), Validators.maxLength(60)],
    ],
    postalCode: [
      "",
      [Validators.required, Validators.pattern(/^[1-9][0-9]{5}$/)],
    ],
    occupation: [
      "",
      [Validators.required, Validators.minLength(2), Validators.maxLength(60)],
    ],
    annualIncome: [0, [Validators.required, Validators.min(0)]],
  });

  ngOnInit(): void {
    this.loadWorkspace();
  }

  loadWorkspace(): void {
    this.isLoading.set(true);
    forkJoin({
      profile: this.auth.getCurrentProfile(),
      loans: this.loansApi.getLoans(),
      documents: this.documentsApi.mine(),
    })
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        next: ({ profile, loans, documents }) => {
          this.profile.set(profile);
          this.profileForm.reset({
            fullName: profile.fullName,
            email: profile.email,
            phoneNumber: profile.phoneNumber,
            dateOfBirth: profile.dateOfBirth,
            addressLine1: profile.addressLine1,
            city: profile.city,
            state: profile.state,
            postalCode: profile.postalCode,
            occupation: profile.occupation,
            annualIncome: profile.annualIncome,
          });
          this.loans.set(loans);
          this.documents.set(documents);
          this.syncActiveStepWithWorkspace(loans, documents);
        },
        error: (error) =>
          this.error.set(
            resolveApiError(error, "Could not load your workspace."),
          ),
      });
  }

  setActiveStep(step: 1 | 2 | 3 | 4): void {
    this.activeStep.set(step);
    this.message.set("");
    this.error.set("");
  }

  goToNextStep(): void {
    this.activeStep.update((current) => (current < 4 ? ((current + 1) as 1 | 2 | 3 | 4) : current));
  }

  goToPreviousStep(): void {
    this.activeStep.update((current) => (current > 1 ? ((current - 1) as 1 | 2 | 3 | 4) : current));
  }

  submitLoan(): void {
    if (this.loanForm.invalid) {
      this.loanForm.markAllAsTouched();
      return;
    }

    this.message.set("");
    this.error.set("");
    this.isSubmittingLoan.set(true);
    this.loansApi
      .apply(this.loanForm.getRawValue() as LoanApplicationRequest)
      .pipe(finalize(() => this.isSubmittingLoan.set(false)))
      .subscribe({
        next: (loan) => {
          this.message.set(
            `Loan #${loan.id} submitted and routed through RabbitMQ.`,
          );
          if (loan.id) {
            this.documentForm.controls.loanId.setValue(String(loan.id));
          }
          this.loanForm.reset({
            amount: 50000,
            loanType: "HOME",
            tenureMonths: 120,
            purpose: "Buying my first home near work and family support.",
          });
          this.activeStep.set(3);
          this.loadWorkspace();
        },
        error: (error) =>
          this.error.set(resolveApiError(error, "Loan submission failed.")),
      });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedFile.set(input.files?.[0] ?? null);
  }

  selectLoanForDocument(loan: LoanApplication): void {
    if (!loan.id) {
      return;
    }
    this.documentForm.controls.loanId.setValue(String(loan.id));
    this.activeStep.set(3);
    this.message.set(
      `Loan #${loan.id} selected for your next document upload.`,
    );
    this.error.set("");
  }

  isStepComplete(stepId: number): boolean {
    return this.currentStage() > stepId;
  }

  isStepActive(stepId: number): boolean {
    return this.currentStage() === stepId;
  }

  amountError(): string {
    const control = this.loanForm.controls.amount;
    if (!control.touched && !control.dirty) {
      return "";
    }
    if (control.hasError("required")) {
      return "Loan amount is required.";
    }
    if (control.hasError("min")) {
      return "Loan amount must be at least 1,000.";
    }
    return "";
  }

  purposeError(): string {
    const control = this.loanForm.controls.purpose;
    if (!control.touched && !control.dirty) {
      return "";
    }
    if (control.hasError("required")) {
      return "Purpose is required.";
    }
    if (control.hasError("minlength") || control.hasError("maxlength")) {
      return "Purpose must be between 10 and 255 characters.";
    }
    return "";
  }

  tenureError(): string {
    const control = this.loanForm.controls.tenureMonths;
    if (!control.touched && !control.dirty) {
      return "";
    }
    if (control.hasError("required")) {
      return "Tenure is required.";
    }
    if (control.hasError("min") || control.hasError("max")) {
      return "Tenure must be between 6 and 360 months.";
    }
    return "";
  }

  loanIdError(): string {
    const control = this.documentForm.controls.loanId;
    if (!control.touched && !control.dirty) {
      return "";
    }
    if (control.hasError("required")) {
      return "Choose or enter a loan ID.";
    }
    return "";
  }

  profileFieldError(
    field:
      | "fullName"
      | "email"
      | "phoneNumber"
      | "dateOfBirth"
      | "addressLine1"
      | "city"
      | "state"
      | "postalCode"
      | "occupation"
      | "annualIncome",
  ): string {
    const control = this.profileForm.controls[field];
    if (!control.touched && !control.dirty) {
      return "";
    }
    if (control.hasError("required")) {
      return "This field is required.";
    }
    if (control.hasError("email")) {
      return "Enter a valid email address.";
    }
    if (control.hasError("pattern")) {
      if (field === "phoneNumber") {
        return "Enter a valid 10-digit Indian mobile number.";
      }
      if (field === "postalCode") {
        return "Enter a valid 6-digit PIN code.";
      }
    }
    if (control.hasError("minlength") || control.hasError("maxlength")) {
      return "Please enter a value within the allowed length.";
    }
    if (control.hasError("min")) {
      return "Value cannot be negative.";
    }
    return "";
  }

  uploadDocument(): void {
    const file = this.selectedFile();
    if (this.documentForm.invalid || !file) {
      this.documentForm.markAllAsTouched();
      this.error.set("Choose a file and provide loan/document details.");
      return;
    }

    this.message.set("");
    this.error.set("");
    this.isUploadingDocument.set(true);
    this.documentsApi
      .upload({ ...this.documentForm.getRawValue(), file })
      .pipe(finalize(() => this.isUploadingDocument.set(false)))
      .subscribe({
        next: (document) => {
          this.message.set(
            `Document #${document.id} uploaded for loan ${document.loanId}. Your application is now in the review pipeline and you can track the status in the next step.`,
          );
          this.documentForm.reset({
            loanId: this.documentForm.controls.loanId.value,
            documentType: "AADHAAR",
          });
          this.selectedFile.set(null);
          this.activeStep.set(4);
          this.loadWorkspace();
        },
        error: (error) =>
          this.error.set(resolveApiError(error, "Document upload failed.")),
      });
  }

  saveProfile(): void {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      this.error.set(
        "Please correct the borrower profile fields before saving.",
      );
      return;
    }

    this.message.set("");
    this.error.set("");
    this.isSavingProfile.set(true);
    this.auth
      .updateCurrentProfile(
        this.profileForm.getRawValue() as ProfileUpdateRequest,
      )
      .pipe(finalize(() => this.isSavingProfile.set(false)))
      .subscribe({
        next: (profile) => {
          this.profile.set(profile);
          this.message.set(
            "Your borrower profile has been updated successfully.",
          );
        },
        error: (error) =>
          this.error.set(resolveApiError(error, "Profile update failed.")),
      });
  }

  withdrawLoan(loan: LoanApplication): void {
    if (!loan.id) {
      return;
    }
    this.message.set("");
    this.error.set("");
    this.loansApi.withdraw(loan.id).subscribe({
      next: () => {
        this.message.set(`Loan #${loan.id} was withdrawn successfully.`);
        this.loadWorkspace();
      },
      error: (error) =>
        this.error.set(resolveApiError(error, "Loan withdrawal failed.")),
    });
  }

  respondToOffer(loan: LoanApplication, borrowerDecision: "ACCEPTED" | "DECLINED"): void {
    if (!loan.id) {
      return;
    }
    const request: LoanOfferResponseRequest = {
      borrowerDecision,
      borrowerRemarks:
        borrowerDecision === "ACCEPTED"
          ? "Borrower accepted the offer from the workspace."
          : "Borrower declined the offer from the workspace.",
    };

    this.message.set("");
    this.error.set("");
    this.isRespondingToOffer.set(true);
    this.loansApi.respondToOffer(loan.id, request)
      .pipe(finalize(() => this.isRespondingToOffer.set(false)))
      .subscribe({
        next: (updatedLoan) => {
          this.message.set(
            borrowerDecision === "ACCEPTED"
              ? `Loan #${updatedLoan.id} accepted successfully. Your repayment plan is now active.`
              : `Loan #${updatedLoan.id} was declined from your side and marked accordingly.`,
          );
          this.loadWorkspace();
        },
        error: (error) =>
          this.error.set(resolveApiError(error, "Unable to submit your loan offer response.")),
      });
  }

  private syncActiveStepWithWorkspace(
    loans: LoanApplication[],
    documents: LoanDocument[],
  ): void {
    const current = this.activeStep();
    if (current === 1 && loans.length === 0) {
      return;
    }
    if (loans.length === 0) {
      this.activeStep.set(2);
      return;
    }
    if (documents.length === 0) {
      this.activeStep.set(3);
      return;
    }
    if (loans.some((loan) => loan.status === "OFFER_MADE" || loan.status === "ACTIVE")) {
      this.activeStep.set(4);
      return;
    }
    this.activeStep.set(4);
  }

  private normalizeDocumentChecklistItem(label: string): string {
    const normalized = label.toUpperCase();
    if (normalized.includes("AADHAAR")) {
      return "AADHAAR";
    }
    if (normalized.includes("PAN")) {
      return "PAN";
    }
    if (normalized.includes("SALARY")) {
      return "SALARY_SLIP";
    }
    if (normalized.includes("BANK")) {
      return "BANK_STATEMENT";
    }
    if (normalized.includes("PROPERTY")) {
      return "PROPERTY_PROOF";
    }
    return normalized.replace(/[^A-Z]/g, "_");
  }
}
