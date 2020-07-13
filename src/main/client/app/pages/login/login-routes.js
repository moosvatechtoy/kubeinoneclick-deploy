import AppLoginForm from '@/pages/login/login.vue';

const loginRoutes = [
  {
    path: '/login',
    name: 'login',
    component: AppLoginForm,
    meta: {
      layout: 'none',
      title: 'OneClick - Login',
    },
  },
];

export default loginRoutes;
