const usersRoutes = [
  {
    path: '/users',
    name: 'users',
    component: () => import(/* webpackChunkName: "chunk-users" */ '@/pages/users/users.vue'),
    meta: {
      authorities: ['ROLE_ADMIN'],
      breadcrumb: [{ text: 'Users' }],
      title: 'OneClick - Users',
    },
  },
];

export default usersRoutes;
