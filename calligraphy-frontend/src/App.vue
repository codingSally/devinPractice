<template>
  <div class="app-container">
    <!-- Top navigation bar (JD style) -->
    <div class="top-nav">
      <div class="container">
        <div class="top-nav-left">
          <a href="/">Home</a>
          <a href="#">Location</a>
        </div>
        <div class="top-nav-right">
          <template v-if="!isLoggedIn">
            <a @click="$router.push('/login')">Hello, Sign In</a>
            <a @click="$router.push('/login')">Register</a>
          </template>
          <template v-else>
            <a>Welcome, {{ username }}</a>
            <a @click="logout">Sign Out</a>
          </template>
          <a @click="$router.push('/orders')">My Orders</a>
          <a v-if="isAdmin" @click="$router.push('/admin/products')">Product Management</a>
        </div>
      </div>
    </div>
    
    <!-- Main header with logo, search and cart (JD style) -->
    <div class="main-header">
      <div class="container">
        <div class="logo" @click="$router.push('/')">Calligraphy Art</div>
        <div class="search-box">
          <input type="text" placeholder="Search calligraphy products" v-model="searchQuery" @keyup.enter="search" />
          <button @click="search">Search</button>
        </div>
        <div class="shopping-cart" @click="$router.push('/cart')">
          <i class="cart-icon">🛒</i>
          <span>Cart</span>
        </div>
      </div>
    </div>
    
    <!-- Category navigation (JD style) -->
    <div class="category-nav">
      <div class="container">
        <ul class="category-list">
          <li><router-link to="/products?category=brushes">Brushes</router-link></li>
          <li><router-link to="/products?category=ink">Ink</router-link></li>
          <li><router-link to="/products?category=paper">Rice Paper</router-link></li>
          <li><router-link to="/products?category=calligraphy works">Calligraphy Works</router-link></li>
          <li><router-link to="/products?category=accessories">Accessories</router-link></li>
          <li><router-link to="/products?category=sets">Calligraphy Sets</router-link></li>
          <li><router-link to="/products">All Products</router-link></li>
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
            <h4>Shopping Guide</h4>
            <a href="#">Shopping Process</a>
            <a href="#">Membership</a>
            <a href="#">FAQ</a>
          </div>
          <div class="footer-section">
            <h4>Delivery</h4>
            <a href="#">Store Pickup</a>
            <a href="#">Express Delivery</a>
            <a href="#">Delivery Service</a>
          </div>
          <div class="footer-section">
            <h4>Payment</h4>
            <a href="#">Cash on Delivery</a>
            <a href="#">Online Payment</a>
            <a href="#">Installment</a>
          </div>
          <div class="footer-section">
            <h4>After-Sales</h4>
            <a href="#">Return Policy</a>
            <a href="#">Price Protection</a>
            <a href="#">Refund Instructions</a>
          </div>
        </div>
        <div class="footer-bottom">
          <p>Calligraphy Art ©2025 All Rights Reserved</p>
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
        return store.state.user.username || 'User';
      }
      return 'User';
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
  --light-bg: #f5f5f5;
  --border-color: #e3e4e5;
  --hover-color: #c81623;
  --card-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  --hover-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
  --border-radius: 2px;
  --transition-speed: 0.2s;
  --container-width: 1190px;
  --header-height: 100px;
  --nav-height: 40px;
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
  height: var(--header-height);
  display: flex;
  align-items: center;
  box-shadow: 0 1px 3px rgba(0,0,0,0.08);
}

.main-header .container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 10px;
}

.logo {
  width: 190px;
  height: 60px;
  background: var(--primary-color);
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
