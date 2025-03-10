import { createApp } from 'vue';
import { createStore } from 'vuex';
import { createRouter, createWebHistory } from 'vue-router';
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/antd.css';
import './assets/global.css';
import App from './App.vue';
import Login from './views/Login.vue';
import ProductList from './views/ProductList.vue';
import ProductDetail from './views/ProductDetail.vue';
import ProductManagement from './views/ProductManagement.vue';

// Create Vuex store
const store = createStore({
  state() {
    return {
      token: localStorage.getItem('token') || '',
      user: JSON.parse(localStorage.getItem('user')) || null
    };
  },
  mutations: {
    setToken(state, token) {
      state.token = token;
      localStorage.setItem('token', token);
    },
    setUser(state, user) {
      state.user = user;
      localStorage.setItem('user', JSON.stringify(user));
    },
    logout(state) {
      state.token = '';
      state.user = null;
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    }
  },
  getters: {
    isLoggedIn: state => !!state.token,
    isAdmin: state => state.user && state.user.role === 'admin'
  }
});

// Create Vue Router
const routes = [
  { path: '/', redirect: '/products' },
  { path: '/login', component: Login },
  { path: '/products', component: ProductList },
  { path: '/products/:id', component: ProductDetail },
  { 
    path: '/admin/products', 
    component: ProductManagement,
    meta: { requiresAuth: true, requiresAdmin: true }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// Navigation guard
router.beforeEach((to, from, next) => {
  const isLoggedIn = store.getters.isLoggedIn;
  const isAdmin = store.getters.isAdmin;
  
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!isLoggedIn) {
      next('/login');
    } else if (to.matched.some(record => record.meta.requiresAdmin) && !isAdmin) {
      next('/products');
    } else {
      next();
    }
  } else {
    next();
  }
});

// Create and mount Vue app
const app = createApp(App);
app.use(store);
app.use(router);
app.use(Antd);
app.mount('#app');
