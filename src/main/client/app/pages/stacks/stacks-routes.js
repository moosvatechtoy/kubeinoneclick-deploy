const stacksRoutes = [
  {
    path: '/stacks',
    name: 'stacks',
    props: true,
    component: () => import(/* webpackChunkName: "chunk-stacks" */ '@/pages/stacks/stacks.vue'),
    meta: {
      authorities: ['ROLE_USER'],
      breadcrumb: [{ text: 'Provisioners' }],
      title: 'OneClick - Provisioners',
    },
  },
  {
    path: '/stacks/new',
    name: 'stack_creation',
    component: () => import(/* webpackChunkName: "chunk-stacks" */ '@/pages/stacks/stack-creation.vue'),
    props: true,
    meta: {
      authorities: ['ROLE_USER'],
      breadcrumb: [{ text: 'Provisioners', to: { name: 'stacks' } }, { text: 'Provisioner creation' }],
      title: 'OneClick - Provisioner creation',
    },
  },
  {
    path: '/stacks/:stackId',
    component: () => import(/* webpackChunkName: "chunk-stacks" */ '@/pages/stacks/stack.vue'),
    meta: {
      authorities: ['ROLE_USER'],
    },
    children: [
      {
        path: 'edit',
        name: 'stack_edition',
        props: true,
        component: () => import(/* webpackChunkName: "chunk-stacks" */ '@/pages/stacks/stack-edition.vue'),
        meta: {
          authorities: ['ROLE_USER'],
          breadcrumb: [{ text: 'Provisioners', to: { name: 'stacks' } }, { text: 'Provisioner edition' }],
          title: 'OneClick - Provisioner edition',
        },
      },
      {
        path: 'jobs/:jobId',
        name: 'job',
        component: () => import(/* webpackChunkName: "chunk-stacks" */ '@/pages/stacks/job/job.vue'),
        props: true,
        meta: {
          authorities: ['ROLE_USER'],
          breadcrumb: [
            { text: 'Provisioners', to: { name: 'stacks' } },
            { text: 'Provisioner', to: { name: 'stack_edition' } },
            { text: 'Job' },
          ],
          title: 'OneClick - Provisioner job',
        },
      },
    ],
  },
];

export default stacksRoutes;
