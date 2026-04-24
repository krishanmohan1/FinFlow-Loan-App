import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '@core/services/auth.service';
import { HealthService } from '@core/services/health.service';
import { ServiceHealth } from '@core/models/health.models';

@Component({
  selector: 'ff-landing',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.css'
})
export class LandingComponent {
  protected readonly auth = inject(AuthService);
  private readonly health = inject(HealthService);

  protected healthChecks: ServiceHealth[] = [];

  constructor() {
    this.health.checkServices().subscribe((checks) => {
      this.healthChecks = checks;
    });
  }
}
