const stacksRoutes = [
  {
    path: '/clusters',
    name: 'stacks',
    props: true,
    component: () => import(/* webpackChunkName: "chunk-stacks" */ '@/pages/stacks/stacks.vue'),
    meta: {
      authorities: ['ROLE_USER'],
      breadcrumb: [{ text: 'Clusters' }],
      title: 'OneClick - Clusters',
    },
  },
  {
    path: '/clusters/new',
    name: 'stack_creation',
    component: () => import(/* webpackChunkName: "chunk-stacks" */ '@/pages/stacks/stack-creation.vue'),
    props: true,
    meta: {
      authorities: ['ROLE_USER'],
      breadcrumb: [{ text: 'Clusters', to: { name: 'stacks' } }, { text: 'Cluster creation' }],
      title: 'OneClick - Cluster creation',
    },
  },
  {
    path: '/clusters/:stackId',
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
          breadcrumb: [{ text: 'Clusters', to: { name: 'stacks' } }, { text: 'Cluster edition' }],
          title: 'OneClick - Cluster edition',
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
            { text: 'Clusters', to: { name: 'stacks' } },
            { text: 'Cluster', to: { name: 'stack_edition' } },
            { text: 'Job' },
          ],
          title: 'OneClick - Cluster job',
        },
      },
    ],
  },
];

export default stacksRoutes;
