const settingsRoutes = [
  {
    path: '/settings',
    name: 'settings',
    component: () => import(/* webpackChunkName: "chunk-settings" */ '@/pages/settings/settings.vue'),
    meta: {
      authorities: ['ROLE_ADMIN'],
      breadcrumb: [{ text: 'Settings' }],
      title: 'OneClick - Settings',
    },
  },
];

export default settingsRoutes;
