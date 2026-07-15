import { Pipe, PipeTransform } from '@angular/core';

interface EquipeComoNomes {
  montador?: string | null;
  eletricista?: string | null;
  ajudante?: string | null;
}

@Pipe({
  name: 'equipeNome',
  standalone: true
})
export class EquipeNomePipe implements PipeTransform {
  transform(equipe: EquipeComoNomes | null | undefined): string {
    if (!equipe) {
      return '';
    }
    return [equipe.montador, equipe.eletricista, equipe.ajudante].filter((nome) => !!nome).join(' / ');
  }
}
