import { Pipe, PipeTransform } from '@angular/core';

const ROTULOS: Record<string, string> = {
  ALTO: 'Alto',
  MEDIO: 'Médio',
  LEVE: 'Leve'
};

@Pipe({
  name: 'nivelLabel',
  standalone: true
})
export class NivelLabelPipe implements PipeTransform {
  transform(nivel: string | null | undefined): string {
    if (!nivel) {
      return '';
    }
    return ROTULOS[nivel] ?? nivel;
  }
}
