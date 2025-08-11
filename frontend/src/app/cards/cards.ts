import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'card-root',
  imports: [RouterOutlet],
  standalone: true,
  templateUrl: './cards.html',
  styleUrl: './cards.css'
})
export class Card {
  protected readonly title = signal('frontend');
}
