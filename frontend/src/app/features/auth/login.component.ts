import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '@core/services/auth.service';
import { resolveApiError } from '@core/utils/api-error.util';

@Component({
  selector: 'ff-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './auth.component.css'
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly isSubmitting = signal(false);
  protected readonly error = signal('');
  protected readonly mode = signal<'USER' | 'ADMIN'>('USER');

  protected readonly form = this.fb.nonNullable.group({
    username: ['', [Validators.required, Validators.maxLength(30)]],
    password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(64)]]
  });

  useApplicantMode(): void {
    this.mode.set('USER');
    this.form.patchValue({ username: '', password: '' });
    this.error.set('');
  }

  useAdminMode(): void {
    this.mode.set('ADMIN');
    this.form.patchValue({ username: 'admin', password: 'admin123' });
    this.error.set('');
  }

  usernameError(): string {
    const control = this.form.controls.username;
    if (!control.touched && !control.dirty) {
      return '';
    }
    if (control.hasError('required')) {
      return 'Username is required.';
    }
    if (control.hasError('maxlength')) {
      return 'Username must be 30 characters or fewer.';
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

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.error.set('');

    this.auth.login(this.form.getRawValue()).subscribe({
      next: (response) => void this.router.navigate([response.role === 'ADMIN' ? '/admin' : '/dashboard']),
      error: (error) => {
        this.error.set(resolveApiError(error, 'Login failed. Please verify your credentials.'));
        this.isSubmitting.set(false);
      }
    });
  }
}
