import { Routes } from '@angular/router';
import { Book } from './books/books';
import { User } from './users/users';
import { Author } from './authors/authors';
import { Card } from './cards/cards';
import { Record } from './records/records';
import { AuthGuard } from '../guard';

export const routes: Routes = [
  { 
    path: '', 
    canActivate: [AuthGuard],
    children: [
      { path: 'users', component: User },
      { path: 'books', component: Book },
      { path: 'authors', component: Author },
      { path: 'cards', component: Card },
      { path: 'records', component: Record },
      { path: '', redirectTo: 'users', pathMatch: 'full' }
    ],
  },
  { path: '**', redirectTo: '' }
];