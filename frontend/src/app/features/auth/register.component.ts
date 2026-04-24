import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '@core/services/auth.service';

@Component({
  selector: 'ff-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './auth.component.css'
})
export class RegisterComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly isSubmitting = signal(false);
  protected readonly error = signal('');
  protected readonly success = signal('');

  protected readonly form = this.fb.nonNullable.group({
    username: [`user${Math.floor(Math.random() * 9000) + 1000}`, [Validators.required, Validators.minLength(3)]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  useFreshUsername(): void {
    this.form.controls.username.setValue(`user${Date.now().toString().slice(-5)}`);
    this.error.set('');
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.error.set('Please enter a username and a password with at least 6 characters.');
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
        const backendMessage = error?.error?.error as string | undefined;
        this.error.set(
          backendMessage?.includes('Username already taken')
            ? 'That username already exists. Click "Suggest username" or choose a different name.'
            : backendMessage ?? 'Registration failed. Confirm the backend is running and try again.'
        );
        this.isSubmitting.set(false);
      }
    });
  }
}
