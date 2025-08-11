import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'book-root',
  imports: [RouterOutlet],
  standalone: true,
  templateUrl: './books.html',
  styleUrl: './books.css'
})
export class Book {
  protected readonly title = signal('frontend');
}
