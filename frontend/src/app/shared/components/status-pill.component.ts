import { Component, Input } from '@angular/core';

@Component({
  selector: 'ff-status-pill',
  standalone: true,
  template: `<span class="pill" [class]="statusClass">{{ label }}</span>`,
  styles: `
    .pill {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      min-width: 92px;
      padding: 7px 10px;
      border-radius: 999px;
      font-size: 0.72rem;
      font-weight: 800;
      letter-spacing: 0.08em;
      text-transform: uppercase;
    }
    .pending { color: #6a4a00; background: #fff2c2; }
    .approved, .verified { color: #064d34; background: #c8f8de; }
    .rejected { color: #7b1621; background: #ffd1d6; }
    .review { color: #193c83; background: #dbe8ff; }
    .default { color: #344054; background: #edf1f5; }
  `
})
export class StatusPillComponent {
  @Input({ required: true }) status = '';

  get label(): string {
    return this.status || 'UNKNOWN';
  }

  get statusClass(): string {
    const value = this.status.toUpperCase();
    if (value === 'PENDING') return 'pending';
    if (value === 'APPROVED') return 'approved';
    if (value === 'VERIFIED') return 'verified';
    if (value === 'REJECTED') return 'rejected';
    if (value === 'UNDER_REVIEW') return 'review';
    return 'default';
  }
}
