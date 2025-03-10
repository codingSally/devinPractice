<template>
  <div class="product-detail">
    <a-spin :spinning="loading">
      <a-page-header
        :title="product.productName"
        @back="goBack"
      />
      
      <a-row :gutter="24">
        <a-col :span="12">
          <img
            :src="product.imagePath || 'https://via.placeholder.com/600x400?text=Calligraphy+Product'"
            alt="Product Image"
            class="product-image"
          />
        </a-col>
        
        <a-col :span="12">
          <div class="product-info">
            <h1>{{ product.productName }}</h1>
            <a-tag color="blue">{{ product.category }}</a-tag>
            
            <div class="price-section">
              <h2 class="price">${{ product.price }}</h2>
              <p>In Stock: {{ product.inventory }} items</p>
            </div>
            
            <div class="description-section">
              <h3>Description</h3>
              <p>{{ product.description }}</p>
            </div>
            
            <div class="actions">
              <a-button type="primary" size="large">
                Add to Cart
              </a-button>
            </div>
          </div>
        </a-col>
      </a-row>
    </a-spin>
    
    <a-result
      v-if="error"
      status="404"
      title="404"
      sub-title="Sorry, the product you visited does not exist."
    >
      <template #extra>
        <a-button type="primary" @click="goBack">
          Back to Products
        </a-button>
      </template>
    </a-result>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

export default {
  name: 'ProductDetail',
  setup() {
    const route = useRoute();
    const router = useRouter();
    const product = ref({});
    const loading = ref(false);
    const error = ref(false);
    
    const fetchProduct = async () => {
      const productId = route.params.id;
      loading.value = true;
      error.value = false;
      
      try {
        const response = await axios.get(`/api/products/${productId}`);
        product.value = response.data;
      } catch (err) {
        console.error('Error fetching product details:', err);
        error.value = true;
      } finally {
        loading.value = false;
      }
    };
    
    const goBack = () => {
      router.push('/products');
    };
    
    onMounted(() => {
      fetchProduct();
    });
    
    return {
      product,
      loading,
      error,
      goBack
    };
  }
};
</script>

<style scoped>
.product-detail {
  padding: 20px 0;
}

.product-image {
  width: 100%;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.product-info {
  padding: 0 16px;
}

.price-section {
  margin: 24px 0;
  padding: 16px;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.price {
  color: #1890ff;
  font-size: 24px;
  margin-bottom: 8px;
}

.description-section {
  margin-bottom: 24px;
}

.actions {
  margin-top: 32px;
}
</style>
