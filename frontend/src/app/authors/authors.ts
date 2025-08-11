import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'author-root',
  imports: [RouterOutlet],
  standalone: true,
  templateUrl: './authors.html',
  styleUrl: './authors.css'
})
export class Author {
  protected readonly title = signal('frontend');
}
