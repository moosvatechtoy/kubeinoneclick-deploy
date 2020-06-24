const modulesRoutes = [
  {
    path: '/modules',
    name: 'modules',
    component: () => import(/* webpackChunkName: "chunk-modules" */ '@/pages/modules/modules.vue'),
    meta: {
      authorities: ['ROLE_USER'],
      breadcrumb: [{ text: 'Configurations' }],
      title: 'OneClick - Configurations',
    },
  },
  {
    path: '/modules/import',
    name: 'module_import',
    component: () => import(/* webpackChunkName: "chunk-modules" */ '@/pages/modules/module-import.vue'),
    meta: {
      authorities: ['ROLE_USER'],
      breadcrumb: [{ text: 'Configurations', to: { name: 'modules' } }, { text: 'Configuration import' }],
      title: 'OneClick - Configurations import',
    },
  },
  {
    path: '/modules/:moduleId',
    name: 'module',
    component: () => import(/* webpackChunkName: "chunk-modules" */ '@/pages/modules/module.vue'),
    props: true,
    meta: {
      authorities: ['ROLE_USER'],
      breadcrumb: [{ text: 'Configurations', to: { name: 'modules' } }, { text: 'Configuration edition' }],
      title: 'OneClick - Configuration edition',
    },
  },
  {
    path: '/modules/:moduleId/description',
    name: 'module_description',
    component: () => import(/* webpackChunkName: "chunk-modules" */ '@/pages/modules/module-description.vue'),
    props: true,
    meta: {
      authorities: ['ROLE_USER'],
      breadcrumb: [{ text: 'Configurations', to: { name: 'modules' } }, { text: 'Configuration details' }],
      title: 'OneClick - Configuration description',
    },
  },
];

export default modulesRoutes;
