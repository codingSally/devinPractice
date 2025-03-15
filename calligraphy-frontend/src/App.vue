<template>
  <div class="app-container">
    <!-- Top navigation bar (JD style) -->
    <div class="top-nav">
      <div class="container">
        <div class="top-nav-left">
          <a href="/">首页</a>
          <a href="#">位置</a>
        </div>
        <div class="top-nav-right">
          <template v-if="!isLoggedIn">
            <a @click="$router.push('/login')">你好，请登录</a>
            <a @click="$router.push('/login')">免费注册</a>
          </template>
          <template v-else>
            <a>欢迎，{{ username }}</a>
            <a @click="logout">退出登录</a>
          </template>
          <a @click="$router.push('/orders')">我的订单</a>
          <a v-if="isAdmin" @click="$router.push('/admin/products')">商品管理</a>
        </div>
      </div>
    </div>
    
    <!-- Main header with logo, search and cart (JD style) -->
    <div class="main-header">
      <div class="container">
        <div class="logo" @click="$router.push('/')">书法艺术</div>
        <div class="search-box">
          <input type="text" placeholder="搜索书法产品" v-model="searchQuery" @keyup.enter="search" />
          <button @click="search">搜索</button>
        </div>
        <div class="shopping-cart" @click="$router.push('/cart')">
          <i class="cart-icon">🛒</i>
          <span>购物车</span>
        </div>
      </div>
    </div>
    
    <!-- Category navigation (JD style) -->
    <div class="category-nav">
      <div class="container">
        <ul class="category-list">
          <li><router-link to="/products?category=brushes">毛笔</router-link></li>
          <li><router-link to="/products?category=ink">墨水</router-link></li>
          <li><router-link to="/products?category=paper">宣纸</router-link></li>
          <li><router-link to="/products?category=calligraphy works">书法作品</router-link></li>
          <li><router-link to="/products?category=accessories">书法配件</router-link></li>
          <li><router-link to="/products?category=sets">书法套装</router-link></li>
          <li><router-link to="/products">全部商品</router-link></li>
        </ul>
      </div>
    </div>
    
    <!-- Main content -->
    <div class="content-container">
      <router-view></router-view>
    </div>
    
    <!-- Footer (JD style) -->
    <div class="footer">
      <div class="container">
        <div class="footer-links">
          <div class="footer-section">
            <h4>购物指南</h4>
            <a href="#">购物流程</a>
            <a href="#">会员介绍</a>
            <a href="#">常见问题</a>
          </div>
          <div class="footer-section">
            <h4>配送方式</h4>
            <a href="#">上门自提</a>
            <a href="#">211限时达</a>
            <a href="#">配送服务查询</a>
          </div>
          <div class="footer-section">
            <h4>支付方式</h4>
            <a href="#">货到付款</a>
            <a href="#">在线支付</a>
            <a href="#">分期付款</a>
          </div>
          <div class="footer-section">
            <h4>售后服务</h4>
            <a href="#">售后政策</a>
            <a href="#">价格保护</a>
            <a href="#">退款说明</a>
          </div>
        </div>
        <div class="footer-bottom">
          <p>书法艺术 ©2025 版权所有</p>
        </div>
      </div>
    </div>
  </div>
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
    const searchQuery = ref('');
    
    const isLoggedIn = computed(() => store.getters.isLoggedIn);
    const isAdmin = computed(() => store.getters.isAdmin);
    const username = computed(() => {
      if (store.state.user) {
        return store.state.user.username || '用户';
      }
      return '用户';
    });
    
    const logout = () => {
      store.commit('logout');
      router.push('/login');
    };
    
    const search = () => {
      if (searchQuery.value.trim()) {
        router.push(`/products?search=${encodeURIComponent(searchQuery.value.trim())}`);
      }
    };
    
    return {
      searchQuery,
      isLoggedIn,
      isAdmin,
      username,
      logout,
      search
    };
  }
};
</script>

<style>
:root {
  --primary-color: #e1251b; /* JD red */
  --secondary-color: #f10215;
  --accent-color: #fff;
  --text-color: #333;
  --light-bg: #f6f6f6;
  --border-color: #e3e4e5;
  --hover-color: #c81623;
  --card-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  --hover-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
  --border-radius: 2px;
  --transition-speed: 0.3s;
  --container-width: 1190px;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: "Microsoft YaHei", Arial, sans-serif;
  background-color: var(--light-bg);
  color: var(--text-color);
  line-height: 1.5;
}

.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.container {
  width: 100%;
  max-width: var(--container-width);
  margin: 0 auto;
  padding: 0 15px;
}

/* Top navigation bar */
.top-nav {
  background-color: #e3e4e5;
  color: #999;
  font-size: 12px;
  height: 30px;
  line-height: 30px;
  border-bottom: 1px solid #ddd;
}

.top-nav .container {
  display: flex;
  justify-content: space-between;
}

.top-nav a {
  color: #999;
  text-decoration: none;
  margin-right: 10px;
  cursor: pointer;
}

.top-nav a:hover {
  color: var(--primary-color);
}

.top-nav-right a {
  margin-left: 10px;
  margin-right: 0;
}

/* Main header */
.main-header {
  background-color: white;
  height: 100px;
  display: flex;
  align-items: center;
}

.main-header .container {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  width: 190px;
  height: 60px;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
  color: white;
  font-size: 24px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--border-radius);
  cursor: pointer;
}

.search-box {
  display: flex;
  width: 550px;
  height: 36px;
}

.search-box input {
  flex: 1;
  height: 100%;
  padding: 0 10px;
  border: 2px solid var(--primary-color);
  border-right: none;
  border-radius: var(--border-radius) 0 0 var(--border-radius);
  outline: none;
  font-size: 14px;
}

.search-box button {
  width: 80px;
  height: 100%;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 0 var(--border-radius) var(--border-radius) 0;
  cursor: pointer;
  font-size: 16px;
}

.shopping-cart {
  display: flex;
  align-items: center;
  height: 36px;
  padding: 0 15px;
  background-color: #f9f9f9;
  border: 1px solid #e3e4e5;
  border-radius: var(--border-radius);
  cursor: pointer;
}

.shopping-cart:hover {
  background-color: #f0f0f0;
}

.cart-icon {
  margin-right: 5px;
  font-size: 16px;
}

/* Category navigation */
.category-nav {
  background-color: var(--primary-color);
  height: 40px;
  line-height: 40px;
}

.category-list {
  display: flex;
  list-style: none;
}

.category-list li {
  margin-right: 20px;
}

.category-list a {
  color: white;
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
}

.category-list a:hover {
  color: #ffccc7;
}

/* Content container */
.content-container {
  flex: 1;
  padding: 20px 0;
  background-color: var(--light-bg);
}

/* Footer */
.footer {
  background-color: #eaeaea;
  padding: 30px 0;
  margin-top: 20px;
}

.footer-links {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.footer-section {
  display: flex;
  flex-direction: column;
}

.footer-section h4 {
  margin-bottom: 10px;
  font-size: 14px;
  color: #666;
}

.footer-section a {
  color: #999;
  text-decoration: none;
  font-size: 12px;
  margin-bottom: 5px;
}

.footer-section a:hover {
  color: var(--primary-color);
}

.footer-bottom {
  text-align: center;
  padding-top: 15px;
  border-top: 1px solid #ddd;
  color: #999;
  font-size: 12px;
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
</style>
