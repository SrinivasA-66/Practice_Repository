import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  
  {
    path: 'login',
    loadComponent: () => import('./login/login.component').then(m => m.LoginComponent)
  },


  {
    path: 'register',
    loadComponent: () => import('./register/register.component').then(m => m.RegisterComponent)
  },

  {
    path: 'developer',
    loadComponent: () => import('./developer-portal/developer-portal.component').then(m => m.DeveloperPortalComponent),
    canActivate: [authGuard],
    data: { role: 'TPP' },
    children: [
      { path: 'dashboard', loadComponent: () => import('./developer-portal/dashboard/dashboard.component').then(m => m.DashboardComponent) },
      { path: 'apps', loadComponent: () => import('./developer-portal/app-list/app-list.component').then(m => m.AppListComponent) },
      { path: 'apps/new', loadComponent: () => import('./developer-portal/app-registration/app-registration.component').then(m => m.AppRegistrationComponent) },
      { path: 'apps/:id/keys', loadComponent: () => import('./developer-portal/keys-upload/keys-upload.component').then(m => m.KeysUploadComponent) },
      { path: 'tpp-register', loadComponent: () => import('./developer-portal/tpp-registration/tpp-registration.component').then(m => m.TppRegistrationComponent) },
      { path: 'subscribe', loadComponent: () => import('./developer-portal/subscribe-plan/subscribe-plan.component').then(m => m.SubscriptionPlanComponent) },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  },

  {
    path: 'customer',
    loadComponent: () => import('./customer-portal/customer-portal.component').then(m => m.CustomerPortalComponent),
    canActivate: [authGuard],
    data: { role: 'CUSTOMER' },
    children: [
      { path: 'apps', loadComponent: () => import('./customer-portal/app-browser/app-browser.component').then(m => m.AppBrowserComponent) },
      { path: 'consents', loadComponent: () => import('./customer-portal/my-consents/my-consents.component').then(m => m.MyConsentsComponent) },
      { path: 'consents/:id', loadComponent: () => import('./customer-portal/consent-detail/consent-detail.component').then(m => m.ConsentDetailComponent) },

      { path: 'consent-review', redirectTo: 'consents', pathMatch: 'full' },
      { path: 'sca', loadComponent: () => import('./customer-portal/sca-verify/sca-verify.component').then(m => m.ScaVerifyComponent) },
      { path: 'accounts', loadComponent: () => import('./customer-portal/my-accounts/my-accounts.component').then(m => m.MyAccountsComponent) },
      { path: 'payment-initiate', loadComponent: () => import('./customer-portal/initiate-payment/initiate-payment.component').then(m => m.InitiatePaymentComponent) },
      { path: 'funds-check', loadComponent: () => import('./customer-portal/funds-check/funds-check.component').then(m => m.FundsCheckComponent) },
      { path: '', redirectTo: 'apps', pathMatch: 'full' }
    ]
  },

  {
    path: 'operations',
    loadComponent: () => import('./operations-portal/operations-portal.component').then(m => m.OperationsPortalComponent),
    canActivate: [authGuard],
    data: { role: 'OPERATIONS' },
    children: [
      { path: 'health', loadComponent: () => import('./operations-portal/api-health/api-health.component').then(m => m.ApiHealthComponent) },
      { path: 'throttle-log', loadComponent: () => import('./operations-portal/throttle-log/throttle-log.component').then(m => m.ThrottleLogComponent) },
      { path: 'incidents', loadComponent: () => import('./operations-portal/incident-management/incident-management.component').then(m => m.IncidentManagementComponent) },
      { path: '', redirectTo: 'health', pathMatch: 'full' }
    ]
  },

  
  {
    path: 'compliance',
    loadComponent: () => import('./compliance-portal/compliance-portal.component').then(m => m.CompliancePortalComponent),
    canActivate: [authGuard],
    data: { role: 'OPERATIONS' },
    children: [
      { path: 'consents', loadComponent: () => import('./compliance-portal/consent-registry/consent-registry.component').then(m => m.ConsentRegistryComponent) },
      { path: 'sca', loadComponent: () => import('./compliance-portal/sca-statistics/sca-statistics.component').then(m => m.ScaStatisticsComponent) },
      { path: 'audit', loadComponent: () => import('./compliance-portal/audit-trail/audit-trail.component').then(m => m.AuditTrailComponent) },
      { path: '', redirectTo: 'consents', pathMatch: 'full' }
    ]
  },

  {
    path: 'admin',
    loadComponent: () => import('./admin-portal/admin-portal.component').then(m => m.AdminPortalComponent),
    canActivate: [authGuard],
    data: { role: 'ADMIN' },
    children: [
      { path: 'products', loadComponent: () => import('./admin-portal/product-management/product-management.component').then(m => m.ProductManagementComponent) },
      { path: 'plans', loadComponent: () => import('./admin-portal/plan-configuration/plan-configuration.component').then(m => m.PlanConfigurationComponent) },
      { path: 'users', loadComponent: () => import('./admin-portal/user-management/user-management.component').then(m => m.UserManagementComponent) },
      { path: 'tpp', loadComponent: () => import('./admin-portal/tpp-management/tpp-management.component').then(m => m.TppManagementComponent) },
      { path: '', redirectTo: 'products', pathMatch: 'full' }
    ]
  },


  {
    path: 'notifications',
    loadComponent: () => import('./shared/notification-list/notification-list.component').then(m => m.NotificationListComponent),
    canActivate: [authGuard],
    data: { role: '' }
  },


  {
    path: 'error',
    loadComponent: () => import('./shared/error-page/error-page.component').then(m => m.ErrorPageComponent)
  },


  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login' }
];
