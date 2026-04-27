import { Component, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '@core/services/auth.service';

@Component({
  selector: 'ff-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  protected readonly auth = inject(AuthService);
  protected readonly navOpen = signal(false);

  constructor() {
    this.auth.bootstrapSession().subscribe();
  }

  protected closeNav(): void {
    this.navOpen.set(false);
  }

  protected toggleNav(): void {
    this.navOpen.update((current) => !current);
  }
}
