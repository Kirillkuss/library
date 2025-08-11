import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'record-root',
  imports: [RouterOutlet],
  standalone: true,
  templateUrl: './records.html',
  styleUrl: './records.css'
})
export class Record {
  protected readonly title = signal('frontend');
}