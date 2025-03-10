<template>
  <div class="product-list">
    <h1>Calligraphy Products</h1>
    
    <div class="filters">
      <a-row :gutter="16">
        <a-col :span="6">
          <a-select
            v-model:value="categoryFilter"
            placeholder="Filter by Category"
            style="width: 100%"
            @change="applyFilters"
          >
            <a-select-option value="">All Categories</a-select-option>
            <a-select-option value="brushes">Brushes</a-select-option>
            <a-select-option value="ink">Ink</a-select-option>
            <a-select-option value="paper">Paper</a-select-option>
            <a-select-option value="calligraphy works">Calligraphy Works</a-select-option>
          </a-select>
        </a-col>
        
        <a-col :span="12">
          <a-row :gutter="8">
            <a-col :span="11">
              <a-input-number
                v-model:value="minPrice"
                placeholder="Min Price"
                style="width: 100%"
                :min="0"
              />
            </a-col>
            <a-col :span="2" style="text-align: center">-</a-col>
            <a-col :span="11">
              <a-input-number
                v-model:value="maxPrice"
                placeholder="Max Price"
                style="width: 100%"
                :min="0"
              />
            </a-col>
          </a-row>
        </a-col>
        
        <a-col :span="6">
          <a-button type="primary" @click="applyFilters">Apply Filters</a-button>
          <a-button style="margin-left: 8px" @click="resetFilters">Reset</a-button>
        </a-col>
      </a-row>
    </div>
    
    <a-spin :spinning="loading">
      <div v-if="products.length === 0 && !loading" class="empty-state">
        <a-empty description="No products found" />
      </div>
      
      <a-row :gutter="[16, 16]" v-else>
        <a-col :xs="24" :sm="12" :md="8" :lg="6" v-for="product in products" :key="product.productId">
          <a-card hoverable>
            <template #cover>
              <img
                alt="product image"
                :src="product.imagePath || 'https://via.placeholder.com/300x200?text=Calligraphy+Product'"
                style="height: 200px; object-fit: cover;"
              />
            </template>
            <a-card-meta :title="product.productName">
              <template #description>
                <div>
                  <p>{{ truncateDescription(product.description) }}</p>
                  <p class="price">${{ product.price }}</p>
                  <a-button type="primary" @click="viewProductDetails(product.productId)">
                    View Details
                  </a-button>
                </div>
              </template>
            </a-card-meta>
          </a-card>
        </a-col>
      </a-row>
      
      <div class="pagination">
        <a-pagination
          v-model:current="currentPage"
          :total="totalProducts"
          :pageSize="pageSize"
          @change="handlePageChange"
        />
      </div>
    </a-spin>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

export default {
  name: 'ProductList',
  setup() {
    const router = useRouter();
    const products = ref([]);
    const loading = ref(false);
    const currentPage = ref(1);
    const pageSize = ref(8);
    const totalProducts = ref(0);
    
    // Filters
    const categoryFilter = ref('');
    const minPrice = ref(null);
    const maxPrice = ref(null);
    
    const fetchProducts = async () => {
      loading.value = true;
      try {
        let url = '/api/products';
        
        // Apply category filter
        if (categoryFilter.value) {
          url = `/api/products/category/${categoryFilter.value}`;
        }
        
        // Apply price filter
        if (minPrice.value !== null && maxPrice.value !== null) {
          url = `/api/products/price?min=${minPrice.value}&max=${maxPrice.value}`;
        }
        
        const response = await axios.get(url);
        products.value = response.data;
        totalProducts.value = response.data.length;
      } catch (error) {
        console.error('Error fetching products:', error);
      } finally {
        loading.value = false;
      }
    };
    
    const applyFilters = () => {
      currentPage.value = 1;
      fetchProducts();
    };
    
    const resetFilters = () => {
      categoryFilter.value = '';
      minPrice.value = null;
      maxPrice.value = null;
      currentPage.value = 1;
      fetchProducts();
    };
    
    const handlePageChange = (page) => {
      currentPage.value = page;
      // In a real app, we would fetch the specific page from the backend
      // For now, we're just using client-side pagination
    };
    
    const truncateDescription = (description) => {
      if (!description) return '';
      return description.length > 100 ? description.substring(0, 100) + '...' : description;
    };
    
    const viewProductDetails = (productId) => {
      router.push(`/products/${productId}`);
    };
    
    onMounted(() => {
      fetchProducts();
    });
    
    return {
      products,
      loading,
      currentPage,
      pageSize,
      totalProducts,
      categoryFilter,
      minPrice,
      maxPrice,
      applyFilters,
      resetFilters,
      handlePageChange,
      truncateDescription,
      viewProductDetails
    };
  }
};
</script>

<style scoped>
.product-list {
  padding: 20px 0;
}

.filters {
  margin-bottom: 24px;
  padding: 16px;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.price {
  font-weight: bold;
  color: #1890ff;
  margin: 8px 0;
}

.pagination {
  margin-top: 24px;
  text-align: center;
}

.empty-state {
  margin: 48px 0;
  text-align: center;
}
</style>
