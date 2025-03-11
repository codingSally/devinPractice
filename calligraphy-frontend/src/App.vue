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
    
    <a-layout-content>
      <div class="content-container">
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
:root {
  --primary-color: #1890ff;
  --primary-gradient: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
  --secondary-color: #722ed1;
  --accent-color: #f5222d;
  --text-color: #333333;
  --light-bg: #f8f9fa;
  --card-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  --hover-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  --border-radius: 8px;
  --transition-speed: 0.3s;
}

.layout {
  min-height: 100vh;
  background-color: var(--light-bg);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.logo {
  float: left;
  width: 200px;
  height: 36px;
  margin: 14px 24px 14px 0;
  background: var(--primary-gradient);
  border-radius: var(--border-radius);
  color: white;
  font-size: 22px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--card-shadow);
  transition: all var(--transition-speed);
}

.logo:hover {
  transform: translateY(-2px);
  box-shadow: var(--hover-shadow);
}

.ant-layout-header {
  background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%) !important;
  padding: 0 50px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  position: sticky;
  top: 0;
  z-index: 1000;
}

/* Import animations from global.css */
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.ant-layout-content {
  padding: 24px 50px;
}

.content-container {
  background: white;
  padding: 24px;
  min-height: 280px;
  border-radius: var(--border-radius);
  box-shadow: var(--card-shadow);
}

.ant-layout-footer {
  text-align: center;
  background: #001529;
  color: white;
  padding: 24px 50px;
}

.ant-menu-dark {
  background: transparent !important;
}

.ant-menu-item a {
  color: white !important;
  font-weight: 500;
  transition: all var(--transition-speed);
}

.ant-menu-item-selected a {
  color: white !important;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
}
</style>
