<template>
  <a-layout class="layout">
    <a-layout-header>
      <div class="logo">Calligraphy Art</div>
      <a-menu
        theme="dark"
        mode="horizontal"
        v-model:selectedKeys="selectedKeys"
        :style="{ lineHeight: '64px' }"
      >
        <a-menu-item key="products">
          <router-link to="/products">Products</router-link>
        </a-menu-item>
        <a-menu-item key="admin" v-if="isAdmin">
          <router-link to="/admin/products">Manage Products</router-link>
        </a-menu-item>
        <a-menu-item key="login" v-if="!isLoggedIn">
          <router-link to="/login">Login</router-link>
        </a-menu-item>
        <a-menu-item key="logout" v-if="isLoggedIn" @click="logout">
          Logout
        </a-menu-item>
      </a-menu>
    </a-layout-header>
    
    <a-layout-content style="padding: 0 50px">
      <div :style="{ background: '#fff', padding: '24px', minHeight: '280px' }">
        <router-view></router-view>
      </div>
    </a-layout-content>
    
    <a-layout-footer style="text-align: center">
      Calligraphy Art ©2025 Created by Calligraphy Enthusiasts
    </a-layout-footer>
  </a-layout>
</template>

<script>
import { computed, ref } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';

export default {
  name: 'App',
  setup() {
    const store = useStore();
    const router = useRouter();
    const selectedKeys = ref(['products']);
    
    const isLoggedIn = computed(() => store.getters.isLoggedIn);
    const isAdmin = computed(() => store.getters.isAdmin);
    
    const logout = () => {
      store.commit('logout');
      router.push('/login');
    };
    
    return {
      selectedKeys,
      isLoggedIn,
      isAdmin,
      logout
    };
  }
};
</script>

<style>
.layout {
  min-height: 100vh;
}
.logo {
  float: left;
  width: 120px;
  height: 31px;
  margin: 16px 24px 16px 0;
  color: white;
  font-size: 18px;
  font-weight: bold;
}
</style>
