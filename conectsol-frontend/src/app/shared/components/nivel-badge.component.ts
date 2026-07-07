import { Component, Input } from '@angular/core';
import { NivelLabelPipe } from '../pipes/nivel-label.pipe';

@Component({
  selector: 'app-nivel-badge',
  standalone: true,
  imports: [NivelLabelPipe],
  template: `<span class="nivel-badge" [class]="'nivel-' + nivel?.toLowerCase()">{{ nivel | nivelLabel }}</span>`,
  styles: [
    `
      .nivel-badge {
        display: inline-block;
        padding: 2px 10px;
        border-radius: 12px;
        font-size: 0.8rem;
        font-weight: 600;
        color: #fff;
      }
      .nivel-alto {
        background-color: #d32f2f;
      }
      .nivel-medio {
        background-color: #f57c00;
      }
      .nivel-leve {
        background-color: #fbc02d;
        color: #333;
      }
    `
  ]
})
export class NivelBadgeComponent {
  @Input() nivel: 'ALTO' | 'MEDIO' | 'LEVE' | null | undefined;
}
