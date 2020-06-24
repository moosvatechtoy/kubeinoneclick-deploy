<template>
  <b-navbar
    toggleable="lg"
    type="light"
  >
    <b-navbar-brand :to="{ name: 'home' }">
      <img
        class="img-responsive logo"
        src="@/assets/images/PK-Red.png"
        alt="#"
      >
      <span class="product-title">OneClick</span>
    </b-navbar-brand>

    <b-navbar-nav class="ml-auto">
      <app-top-bar-user />
    </b-navbar-nav>
  </b-navbar>
</template>

<script>
  import AppTopBarUser from '@/shared/components/topbar/top-bar-user.vue';

  export default {
    name: 'AppTopBar',
    components: {
      AppTopBarUser,
    },
  };
</script>

<style scoped>
  .navbar {
    background: #fff;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
    position: fixed;
    padding: 0;
    width: 100%;
    z-index: 2;
  }

  .logo {
    height: 50px;
    margin: 0 0 0 15px;
  }

  .product-title {
    font-weight: 600;
    font-size: 23px;
    line-height: 35px;
    color: #014361;
  }
</style>
