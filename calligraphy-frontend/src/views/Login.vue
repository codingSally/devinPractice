<template>
  <div class="login-container">
    <a-card title="Login" style="width: 400px">
      <a-form
        :model="formState"
        name="login"
        @finish="onFinish"
        @finishFailed="onFinishFailed"
        autocomplete="off"
      >
        <a-form-item
          label="Username"
          name="username"
          :rules="[{ required: true, message: 'Please input your username!' }]"
        >
          <a-input v-model:value="formState.username" />
        </a-form-item>

        <a-form-item
          label="Password"
          name="password"
          :rules="[{ required: true, message: 'Please input your password!' }]"
        >
          <a-input-password v-model:value="formState.password" />
        </a-form-item>

        <a-form-item>
          <a-button type="primary" html-type="submit" :loading="loading" block>
            Login
          </a-button>
        </a-form-item>
      </a-form>
      
      <a-alert v-if="error" type="error" :message="error" show-icon />
    </a-card>
  </div>
</template>

<script>
import { reactive, ref } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';
import axios from 'axios';

export default {
  name: 'Login',
  setup() {
    const store = useStore();
    const router = useRouter();
    const loading = ref(false);
    const error = ref('');
    
    const formState = reactive({
      username: '',
      password: '',
    });

    const onFinish = async (values) => {
      loading.value = true;
      error.value = '';
      
      try {
        const response = await axios.post('http://localhost:8080/api/auth/login', {
          username: values.username,
          password: values.password,
        });
        
        const { token, username, role } = response.data;
        
        store.commit('setToken', token);
        store.commit('setUser', { username, role });
        
        if (role === 'admin') {
          router.push('/admin/products');
        } else {
          router.push('/products');
        }
      } catch (err) {
        error.value = 'Invalid username or password';
        console.error('Login error:', err);
      } finally {
        loading.value = false;
      }
    };

    const onFinishFailed = (errorInfo) => {
      console.log('Failed:', errorInfo);
    };

    return {
      formState,
      loading,
      error,
      onFinish,
      onFinishFailed,
    };
  },
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 70vh;
}
</style>
