const modulesRoutes = [
  {
    path: '/modules',
    name: 'modules',
    component: () => import(/* webpackChunkName: "chunk-modules" */ '@/pages/modules/modules.vue'),
    meta: {
      authorities: ['ROLE_USER'],
      breadcrumb: [{ text: 'Modules' }],
      title: 'OneClick - Modules',
    },
  },
  {
    path: '/modules/import',
    name: 'module_import',
    component: () => import(/* webpackChunkName: "chunk-modules" */ '@/pages/modules/module-import.vue'),
    meta: {
      authorities: ['ROLE_USER'],
      breadcrumb: [{ text: 'Modules', to: { name: 'modules' } }, { text: 'Module import' }],
      title: 'OneClick - Modules import',
    },
  },
  {
    path: '/modules/:moduleId',
    name: 'module',
    component: () => import(/* webpackChunkName: "chunk-modules" */ '@/pages/modules/module.vue'),
    props: true,
    meta: {
      authorities: ['ROLE_USER'],
      breadcrumb: [{ text: 'Modules', to: { name: 'modules' } }, { text: 'Module edition' }],
      title: 'OneClick - Module edition',
    },
  },
  {
    path: '/modules/:moduleId/description',
    name: 'module_description',
    component: () => import(/* webpackChunkName: "chunk-modules" */ '@/pages/modules/module-description.vue'),
    props: true,
    meta: {
      authorities: ['ROLE_USER'],
      breadcrumb: [{ text: 'Modules', to: { name: 'modules' } }, { text: 'Module description' }],
      title: 'OneClick - Module description',
    },
  },
];

export default modulesRoutes;
