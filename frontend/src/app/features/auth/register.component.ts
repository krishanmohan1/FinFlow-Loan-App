import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '@core/services/auth.service';
import { resolveApiError } from '@core/utils/api-error.util';

@Component({
  selector: 'ff-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './auth.component.css'
})
export class RegisterComponent {
  private static readonly usernamePattern = /^[A-Za-z][A-Za-z0-9_]{3,29}$/;
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly isSubmitting = signal(false);
  protected readonly error = signal('');
  protected readonly success = signal('');
  protected readonly step = signal(1);

  protected readonly form = this.fb.nonNullable.group({
    fullName: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(80)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(120)]],
    phoneNumber: ['', [Validators.required, Validators.pattern(/^[6-9][0-9]{9}$/)]],
    dateOfBirth: ['', [Validators.required]],
    addressLine1: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(120)]],
    city: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(60)]],
    state: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(60)]],
    postalCode: ['', [Validators.required, Validators.pattern(/^[1-9][0-9]{5}$/)]],
    occupation: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(60)]],
    annualIncome: [600000, [Validators.required, Validators.min(0)]],
    username: [
      `user${Math.floor(Math.random() * 9000) + 1000}`,
      [Validators.required, Validators.minLength(4), Validators.maxLength(30), Validators.pattern(RegisterComponent.usernamePattern)]
    ],
    password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(64)]]
  });

  protected readonly personalFields = ['fullName', 'email', 'phoneNumber', 'dateOfBirth', 'occupation', 'annualIncome'] as const;
  protected readonly addressFields = ['addressLine1', 'city', 'state', 'postalCode'] as const;
  protected readonly accessFields = ['username', 'password'] as const;

  useFreshUsername(): void {
    this.form.controls.username.setValue(`user${Date.now().toString().slice(-5)}`);
    this.error.set('');
  }

  nextStep(): void {
    if (this.step() === 1 && !this.isStepValid(this.personalFields)) {
      this.form.markAllAsTouched();
      this.error.set('Please complete the borrower profile details before continuing.');
      return;
    }
    if (this.step() === 2 && !this.isStepValid(this.addressFields)) {
      this.form.markAllAsTouched();
      this.error.set('Please complete the address details before continuing.');
      return;
    }
    this.error.set('');
    this.step.update((current) => Math.min(current + 1, 3));
  }

  previousStep(): void {
    this.error.set('');
    this.step.update((current) => Math.max(current - 1, 1));
  }

  usernameError(): string {
    const control = this.form.controls.username;
    if (!control.touched && !control.dirty) {
      return '';
    }
    if (control.hasError('required')) {
      return 'Username is required.';
    }
    if (control.hasError('minlength') || control.hasError('maxlength')) {
      return 'Username must be between 4 and 30 characters.';
    }
    if (control.hasError('pattern')) {
      return 'Start with a letter and use only letters, numbers, and underscores.';
    }
    return '';
  }

  passwordError(): string {
    const control = this.form.controls.password;
    if (!control.touched && !control.dirty) {
      return '';
    }
    if (control.hasError('required')) {
      return 'Password is required.';
    }
    if (control.hasError('minlength') || control.hasError('maxlength')) {
      return 'Password must be between 8 and 64 characters.';
    }
    return '';
  }

  fieldError(
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
  ): string {
    const control = this.form.controls[field];
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
    }
    if (control.hasError('minlength') || control.hasError('maxlength')) {
      return 'Please enter a value within the allowed length.';
    }
    if (control.hasError('min')) {
      return 'Value cannot be negative.';
    }
    return '';
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.error.set('Please correct the highlighted fields before creating your account.');
      return;
    }

    this.isSubmitting.set(true);
    this.error.set('');
    this.success.set('');

    this.auth.register(this.form.getRawValue()).subscribe({
      next: () => {
        this.success.set('Account created successfully. Opening your workspace...');
        setTimeout(() => void this.router.navigate(['/dashboard']), 450);
      },
      error: (error) => {
        const backendMessage = resolveApiError(error, 'Registration failed. Please try again.');
        this.error.set(
          backendMessage.includes('Username already taken')
            ? 'That username already exists. Click "Suggest username" or choose a different name.'
            : backendMessage
        );
        this.isSubmitting.set(false);
      }
    });
  }

  private isStepValid(fields: readonly string[]): boolean {
    return fields.every((field) => {
      const control = this.form.get(field);
      return Boolean(control && control.valid);
    });
  }
}
