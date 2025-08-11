import { Component, signal, OnInit, ChangeDetectorRef } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface UserResponse {
  login: string;
  fio: string;
  email: string;
  phone: string;
  status: boolean | string;
  roles?: string[];
}

@Component({
  selector: 'user-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, FormsModule],
  templateUrl: './users.html',
  styleUrl: './users.css'
})
export class User implements OnInit {
  protected readonly title = signal('frontend');
  
  currentPage: number = 1;
  pageSize: number = 10;
  totalPages: number = 1;
  totalElements: number = 0;
  searchParam: string = '';
  users: UserResponse[] = [];
  isLoading: boolean = false;
  errorMessage: string | null = null;

  constructor(
    private http: HttpClient, 
    private cd: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadTotalCount();
    this.loadUsers(this.currentPage, this.pageSize);
  }

  loadTotalCount(): void {
    this.http.get<number>('http://localhost:8094/library/users/counts')
      .subscribe({
        next: (response) => {
          this.totalElements = response;
          this.totalPages = Math.ceil(this.totalElements / this.pageSize);
          this.cd.detectChanges();
        },
        error: (error) => {
          //console.error('Failed to load total count:', error);
          this.errorMessage = 'Ошибка загрузки общего количества пользователей';
          this.cd.detectChanges();
        }
      });
  }

  loadUsers(page: number, size: number): void {
    this.isLoading = true;
    this.errorMessage = null;
    const apiUrl = `http://localhost:8094/library/users/lazy/{page}/{size}?page=${page}&size=${size}`;
    
    this.http.get<UserResponse[]>(apiUrl).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.users = response;
        this.cd.detectChanges();
      },
      error: (error) => {
        //console.error('API Error:', error);
        this.isLoading = false;
        this.errorMessage = 'Ошибка загрузки списка пользователей';
        this.cd.detectChanges();
      }
    });
  }

  getStatusIcon(user: UserResponse): string {
    const isBlocked = typeof user.status === 'string' 
      ? user.status.toLowerCase() === 'true' 
      : user.status;
    return isBlocked 
      ? '<i class="fas fa-lock text-danger" title="Заблокирован"></i>'
      : '<i class="fas fa-lock-open text-success" title="Активен"></i>';
  }

  changePage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      if (this.searchParam.trim()) {
        this.loadSearchResults(page, this.pageSize, this.searchParam);
      } else {
        this.loadUsers(page, this.pageSize);
      }
    }
  }

  getRolesText(user: UserResponse): string {
    return user.roles ? user.roles.join(', ') : 'Нет ролей';
  }

  getPages(): number[] {
    const pages = [];
    const startPage = Math.max(1, this.currentPage - 2);
    const endPage = Math.min(this.totalPages, this.currentPage + 2);
    
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    return pages;
  }

  findUsersbyUI(): void {
    if (this.searchParam.trim().length === 0) {
      this.errorMessage = 'Введите поисковый запрос';
      this.loadUsers(1, this.pageSize);
      return;
    }
    this.currentPage = 1;
    this.loadSearchResults(this.currentPage, this.pageSize, this.searchParam);
  }

  loadSearchResults(page: number, size: number, param: string): void {
    this.isLoading = true;
    this.errorMessage = null;
    const apiUrl = `http://localhost:8094/library/{param}/{page}/{size}?param=${param}&page=${page}&size=${size}`;
    
    this.http.get<{content: UserResponse[], totalElements: number}>(apiUrl).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.users = response.content;
        this.totalElements = response.totalElements;
        this.totalPages = Math.ceil(this.totalElements / this.pageSize);
        this.cd.detectChanges();
      },
      error: (error) => {
        console.error('API Error:', error);
        this.isLoading = false;
        this.errorMessage = 'Ошибка поиска пользователей';
        this.cd.detectChanges();
      }
    });
  }

  openAddUserModal(): void {
  }

  editUser(login: string): void {
    // Реализация редактирования пользователя
    console.log('Редактирование пользователя:', login);
  }

  deleteUser(login: string): void {

  }
}