const modulesRoutes = [
  {
    path: '/modules',
    name: 'modules',
    component: () => import(/* webpackChunkName: "chunk-modules" */ '@/pages/modules/modules.vue'),
    meta: {
      authorities: ['ROLE_USER'],
      breadcrumb: [{ text: 'Templates' }],
      title: 'OneClick - Templates',
    },
  },
  {
    path: '/modules/import',
    name: 'module_import',
    component: () => import(/* webpackChunkName: "chunk-modules" */ '@/pages/modules/module-import.vue'),
    meta: {
      authorities: ['ROLE_USER'],
      breadcrumb: [{ text: 'Templates', to: { name: 'modules' } }, { text: 'Template import' }],
      title: 'OneClick - Templates import',
    },
  },
  {
    path: '/modules/:moduleId',
    name: 'module',
    component: () => import(/* webpackChunkName: "chunk-modules" */ '@/pages/modules/module.vue'),
    props: true,
    meta: {
      authorities: ['ROLE_USER'],
      breadcrumb: [{ text: 'Templates', to: { name: 'modules' } }, { text: 'Template edition' }],
      title: 'OneClick - Template edition',
    },
  },
  {
    path: '/modules/:moduleId/description',
    name: 'module_description',
    component: () => import(/* webpackChunkName: "chunk-modules" */ '@/pages/modules/module-description.vue'),
    props: true,
    meta: {
      authorities: ['ROLE_USER'],
      breadcrumb: [{ text: 'Templates', to: { name: 'modules' } }, { text: 'Template details' }],
      title: 'OneClick - Template description',
    },
  },
];

export default modulesRoutes;
