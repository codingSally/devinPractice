<template>
  <div class="product-list">
    <h1 style="background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; display: inline-block;">Calligraphy Products</h1>
    
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
          <a-button type="primary" @click="applyFilters" style="background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%); border: none; box-shadow: 0 2px 6px rgba(24, 144, 255, 0.3); border-radius: 18px;">Apply Filters</a-button>
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
                :src="getCartoonImage(product.imagePath)"
                style="height: 200px; object-fit: cover; border-radius: 8px 8px 0 0;"
              />
            </template>
            <a-card-meta :title="product.productName">
              <template #description>
                <div>
                  <p>{{ truncateDescription(product.description) }}</p>
                  <p class="price">${{ product.price }}</p>
               <a-button type="primary" class="view-details-btn" @click="viewProductDetails(product.productId)">
                    <span style="font-weight: 500;">View Details</span>
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
import { getCartoonImage } from '../utils/imageUtils';

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
      viewProductDetails,
      getCartoonImage
    };
  }
};
</script>

<style scoped>
.product-list {
  padding: 20px 0;
  animation: fadeIn 0.5s ease-out;
}

.filters {
  margin-bottom: 24px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-left: 4px solid #1890ff;
  animation: slideUp 0.5s ease-out;
}

.price {
  font-weight: bold;
  color: #1890ff;
  margin: 12px 0;
  font-size: 20px;
  background: linear-gradient(135deg, rgba(24, 144, 255, 0.1), transparent);
  display: inline-block;
  padding: 6px 12px;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.pagination {
  margin-top: 32px;
  text-align: center;
  padding: 16px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  animation: fadeIn 0.5s ease-out;
}

.empty-state {
  margin: 48px 0;
  text-align: center;
  padding: 40px;
  background: white;
  border-radius: var(--border-radius);
  box-shadow: var(--card-shadow);
}

/* Card styling */
:deep(.ant-card) {
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: none;
  animation: slideUp 0.5s ease-out;
  transform: translateY(0);
}

:deep(.ant-card:hover) {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

:deep(.ant-card-meta-title) {
  font-size: 18px;
  margin-bottom: 8px;
  color: #333333;
  font-weight: 600;
}

:deep(.ant-card-meta-description) {
  color: rgba(0, 0, 0, 0.65);
}

.view-details-btn {
  background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%) !important;
  border: none !important;
  height: 36px;
  border-radius: 18px !important;
  padding: 0 20px !important;
  box-shadow: 0 2px 6px rgba(24, 144, 255, 0.3) !important;
  transition: all 0.3s !important;
  width: 100%;
  margin-top: 8px;
}

.view-details-btn:hover {
  transform: translateY(-2px) !important;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.5) !important;
  opacity: 0.9;
}

/* Gradient text styling */
.gradient-text {
  background: var(--primary-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-fill-color: transparent;
  display: inline-block;
}
</style>
