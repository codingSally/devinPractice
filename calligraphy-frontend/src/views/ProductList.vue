<template>
  <div class="product-list-page">
    <!-- Banner carousel (JD style) -->
    <div class="banner-carousel">
      <img src="../images/banner1.jpg" alt="Banner" class="banner-image" />
    </div>
    
    <!-- Filters section -->
    <div class="filters-section">
      <div class="container">
        <div class="filters">
          <a-row :gutter="16">
            <a-col :span="6">
              <a-select
                v-model:value="categoryFilter"
                placeholder="Category Filter"
                style="width: 100%"
                @change="applyFilters"
              >
                <a-select-option value="">All Categories</a-select-option>
                <a-select-option value="brushes">Brushes</a-select-option>
                <a-select-option value="ink">Ink</a-select-option>
                <a-select-option value="paper">Rice Paper</a-select-option>
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
      </div>
    </div>
    
    <!-- Product grid (JD style) -->
    <div class="product-grid-section">
      <div class="container">
        <a-spin :spinning="loading">
          <div v-if="products.length === 0 && !loading" class="empty-state">
            <a-empty description="No products found" />
          </div>
          
          <div class="product-grid" v-else>
            <div class="product-item" v-for="product in products" :key="product.productId" @click="viewProductDetails(product.productId)">
              <div class="product-image">
                <img :src="getCartoonImage(product.imagePath)" :alt="product.productName" />
              </div>
              <div class="product-price">¥{{ product.price }}</div>
              <div class="product-name">{{ product.productName }}</div>
              <div class="product-shop">Calligraphy Art Store</div>
              <div class="product-actions">
                <button class="add-to-cart-btn">Add to Cart</button>
              </div>
            </div>
          </div>
          
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
    </div>
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
.product-list-page {
  background-color: var(--light-bg);
}

.banner-carousel {
  width: 100%;
  height: 340px;
  overflow: hidden;
  margin-bottom: 20px;
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.filters-section {
  background-color: white;
  padding: 15px 0;
  margin-bottom: 20px;
  box-shadow: var(--card-shadow);
}

.filters {
  padding: 10px;
}

.product-grid-section {
  margin-bottom: 30px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 15px;
}

.product-item {
  background-color: white;
  border-radius: var(--border-radius);
  overflow: hidden;
  transition: all var(--transition-speed);
  cursor: pointer;
  padding: 10px;
  position: relative;
}

.product-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--hover-shadow);
}

.product-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
  margin-bottom: 10px;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.product-item:hover .product-image img {
  transform: scale(1.05);
}

.product-price {
  color: var(--primary-color);
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 5px;
}

.product-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
  height: 40px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-shop {
  font-size: 12px;
  color: #999;
  margin-bottom: 10px;
}

.product-actions {
  display: none;
  position: absolute;
  bottom: 10px;
  left: 10px;
  right: 10px;
}

.product-item:hover .product-actions {
  display: block;
}

.add-to-cart-btn {
  width: 100%;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: var(--border-radius);
  padding: 6px 0;
  cursor: pointer;
  font-size: 14px;
  transition: background-color var(--transition-speed);
}

.add-to-cart-btn:hover {
  background-color: var(--hover-color);
}

.pagination {
  margin-top: 20px;
  text-align: center;
}

.empty-state {
  background-color: white;
  padding: 40px;
  border-radius: var(--border-radius);
  text-align: center;
}
</style>
