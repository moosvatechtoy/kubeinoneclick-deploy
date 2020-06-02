import AppDashboard from '@/pages/dashboard/dashboard.vue';

const dashboardRoutes = [
  {
    path: '/dashboard',
    name: 'dashboard',
    component: AppDashboard,
    meta: {
      authorities: ['ROLE_USER'],
      breadcrumb: [{ text: 'Dashboard' }],
      title: 'OneClick - Dashboard',
    },
  },
];

export default dashboardRoutes;
