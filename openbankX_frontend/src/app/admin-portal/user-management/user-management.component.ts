import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../core/services/user.service';
import { User } from '../../core/models/models';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user-management.component.html',
  styleUrl: './user-management.component.css'
})
export class UserManagementComponent implements OnInit {

  users: User[] = [];
  isLoading = true;
  isSaving = false;
  showForm = false;
  editMode = false;
  successMessage = '';
  errorMessage = '';

  roles = ['CUSTOMER', 'TPP', 'OPERATIONS', 'ADMIN'];

  form: any = this.blankForm();

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  private blankForm() {
    return {
      userId: null,
      name: '',
      email: '',
      phone: '',
      password: '',
      role: '',
      status: 'ACTIVE'
    };
  }

  loadUsers(): void {
    this.isLoading = true;
    this.userService.getUsers().subscribe({
      next: (d) => {
        this.users = d || [];
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = this.describeError(err, 'load users');
      }
    });
  }

  private describeError(err: any, action: string): string {

    if (err?.name === 'TimeoutError') {
      return `Backend did not respond within 20s while trying to ${action}. ` +
             `Make sure the Spring backend has been restarted after the latest code change.`;
    }
    if (err?.status === 0) return 'Cannot reach the backend. Make sure it is running on port 8081.';
    if (err?.status === 401) return 'Your session has expired. Please sign in again.';
    if (err?.status === 403) return 'You need ADMIN access to ' + action + '.';
    if (err?.status === 404 || err?.status === 405) {
      return `This action is not available on the running backend yet — please restart the Spring app so it picks up the new /api/v1/users endpoint.`;
    }
    const serverMsg = err?.error?.message || err?.error?.error;
    if (serverMsg) return serverMsg;
    return `Failed to ${action} (HTTP ${err?.status || '?'}).`;
  }

  openCreate(): void {
    this.editMode = false;
    this.form = this.blankForm();
    this.showForm = true;
    this.successMessage = '';
    this.errorMessage = '';
  }

  edit(u: User): void {
    this.editMode = true;
    this.form = { ...u };
    this.showForm = true;
    this.successMessage = '';
    this.errorMessage = '';
  }

  save(): void {
    if (!this.form.name || this.form.name.trim().length < 2) {
      this.errorMessage = 'Name is required (min 2 characters).';
      return;
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.form.email || '')) {
      this.errorMessage = 'Please enter a valid email.';
      return;
    }
    if (!/^[6-9][0-9]{9}$/.test(this.form.phone || '')) {
      this.errorMessage = 'Phone must be exactly 10 digits and start with 6, 7, 8 or 9.';
      return;
    }
    if (!this.editMode) {
      const p = this.form.password || '';
      if (p.length < 8 || !/[A-Za-z]/.test(p) || !/\d/.test(p)) {
        this.errorMessage = 'Password must be at least 8 characters and include letters and numbers.';
        return;
      }
    }
    if (!this.form.role) {
      this.errorMessage = 'Role is required.';
      return;
    }

    this.isSaving = true;
    this.errorMessage = '';

    const wasEdit = this.editMode;
    const userName = this.form.name;
    const req = this.editMode
      ? this.userService.updateUser(this.form.userId, this.form)
      : this.userService.createUser(this.form);

    req.subscribe({
      next: (savedUser: any) => {
        this.isSaving = false;
        this.showForm = false;
        this.successMessage = wasEdit
          ? `User "${userName}" updated successfully.`
          : `User "${userName}" created successfully.`;
        setTimeout(() => (this.successMessage = ''), 4000);

        if (wasEdit && savedUser) {
          const idx = this.users.findIndex(x => x.userId === savedUser.userId);
          if (idx > -1) this.users[idx] = savedUser;
        } else if (savedUser?.userId) {
          this.users.unshift(savedUser);
        } else {
          this.loadUsers();
        }
      },
      error: (err) => {
        this.isSaving = false;
        this.errorMessage = this.describeError(err, wasEdit ? 'update user' : 'create user');
      }
    });
  }

  toggleStatus(u: User): void {
    const newStatus = u.status === 'ACTIVE' ? 'LOCKED' : 'ACTIVE';
    const prev = u.status;
    u.status = newStatus;

    this.userService.updateUser(u.userId, { status: newStatus }).subscribe({
      next: (updated) => {
        u.status = updated?.status ?? newStatus;
        this.successMessage = `"${u.name}" is now ${u.status}.`;
        setTimeout(() => (this.successMessage = ''), 3000);
      },
      error: (err) => {
        u.status = prev;
        this.errorMessage = this.describeError(err, newStatus === 'LOCKED' ? 'lock user' : 'unlock user');
      }
    });
  }

  delete(id: number): void {
    const target = this.users.find(u => u.userId === id);
    const label = target ? `"${target.name}"` : `user #${id}`;
    if (!confirm(`Delete ${label}? This cannot be undone.`)) return;

    this.userService.deleteUser(id).subscribe({
      next: () => {
        this.users = this.users.filter(u => u.userId !== id);
        this.successMessage = `${label} deleted.`;
        setTimeout(() => (this.successMessage = ''), 3000);
      },
      error: (err) => {
        this.errorMessage = this.describeError(err, 'delete user');
      }
    });
  }

  cancel(): void {
    this.showForm = false;
    this.errorMessage = '';
  }
}
