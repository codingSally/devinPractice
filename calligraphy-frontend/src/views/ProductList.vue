<template>
  <div class="product-list-page">
    
    <!-- Filters section (JD.com style) -->
    <div class="filters-section">
      <div class="container">
        <div class="filters">
          <div class="filters-row">
            <div class="filter-group">
              <div class="filter-label">Category:</div>
              <a-select
                v-model:value="categoryFilter"
                placeholder="All Categories"
                style="width: 180px"
                @change="applyFilters"
                class="jd-select"
              >
                <a-select-option value="">All Categories</a-select-option>
                <a-select-option value="brushes">Brushes</a-select-option>
                <a-select-option value="ink">Ink</a-select-option>
                <a-select-option value="paper">Rice Paper</a-select-option>
                <a-select-option value="calligraphy works">Calligraphy Works</a-select-option>
                <a-select-option value="accessories">Accessories</a-select-option>
                <a-select-option value="sets">Calligraphy Sets</a-select-option>
              </a-select>
            </div>
            
            <div class="filter-group">
              <div class="filter-label">Price:</div>
              <div class="price-range">
                <a-input-number
                  v-model:value="minPrice"
                  placeholder="¥"
                  :min="0"
                  class="price-input"
                />
                <span class="price-separator">-</span>
                <a-input-number
                  v-model:value="maxPrice"
                  placeholder="¥"
                  :min="0"
                  class="price-input"
                />
                <a-button type="primary" class="price-btn" @click="applyFilters">Apply</a-button>
              </div>
            </div>
            
            <div class="filter-actions">
              <a-button type="primary" class="filter-btn" @click="applyFilters">Apply Filters</a-button>
              <a-button class="reset-btn" @click="resetFilters">Reset</a-button>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Product grid (JD style) -->
    <div class="product-grid-section">
      <div class="container">
        <a-spin :spinning="loading">
          <div v-if="products.length === 0 && !loading" class="empty-state">
            <img src="/images/products/empty/jd_style_empty_state.svg" alt="No products found" class="empty-state-image" />
            <div class="empty-state-text">
              <h3>No products found</h3>
              <p>Try adjusting your search or filter to find what you are looking for.</p>
              <a-button type="primary" class="browse-btn" @click="resetFilters">Browse Popular Products</a-button>
            </div>
            <div class="empty-state-recommendations">
              <h4>You might be interested in</h4>
              <div class="recommendation-items">
                <div class="recommendation-item" v-for="i in 4" :key="i" @click="resetFilters">
                  <div class="rec-image">
                    <img :src="`/images/products/product${i}.jpg`" :alt="`Recommendation ${i}`" />
                  </div>
                  <div class="rec-name">Recommended Product {{i}}</div>
                  <div class="rec-price">¥{{99 + (i * 10)}}</div>
                </div>
              </div>
            </div>
          </div>
          
          <div class="product-grid" v-else>
            <div class="product-item" v-for="product in products" :key="product.productId" @click="viewProductDetails(product.productId)">
              <div class="product-image">
                <img :src="getCartoonImage(product.imagePath)" :alt="product.productName" />
              </div>
              <div class="product-price">¥{{ product.price }}</div>
              <div class="product-name">{{ product.productName }}</div>
              <div class="product-shop">Calligraphy Art Store</div>
              <div class="product-services">
                <span class="service-tag">Free Shipping</span>
                <span class="service-tag">Authentic</span>
              </div>
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
  padding: 10px 0;
  margin-bottom: 20px;
  box-shadow: var(--card-shadow);
  border-radius: 4px;
}

.filters {
  padding: 10px 15px;
}

.filters-row {
  display: flex;
  align-items: center;
  flex-wrap: nowrap;
  gap: 20px;
}

.filter-group {
  display: flex;
  align-items: center;
}

.filter-label {
  margin-right: 10px;
  color: #666;
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
}

.jd-select {
  border-radius: 2px;
}

.price-range {
  display: flex;
  align-items: center;
}

.price-input {
  width: 80px;
}

.price-separator {
  margin: 0 8px;
  color: #999;
}

.price-btn {
  margin-left: 8px;
  background-color: var(--primary-color);
  border-color: var(--primary-color);
  height: 32px;
  padding: 0 12px;
}

.filter-actions {
  display: flex;
  margin-left: auto;
}

.filter-btn {
  background-color: var(--primary-color);
  border-color: var(--primary-color);
}

.reset-btn {
  margin-left: 10px;
}

.product-grid-section {
  margin-bottom: 30px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 15px;
  margin-top: 20px;
}

.product-item {
  background-color: white;
  border-radius: var(--border-radius);
  overflow: hidden;
  transition: all var(--transition-speed);
  cursor: pointer;
  padding: 15px;
  position: relative;
  box-shadow: var(--card-shadow);
  border: 1px solid transparent;
}

.product-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--hover-shadow);
  border: 1px solid var(--primary-color);
}

.product-image {
  width: 100%;
  height: 220px;
  overflow: hidden;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: contain;
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
  margin-bottom: 5px;
}

.product-services {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  margin-bottom: 10px;
}

.service-tag {
  font-size: 10px;
  color: #999;
  background-color: #f5f5f5;
  border: 1px solid #eee;
  border-radius: 2px;
  padding: 1px 4px;
  display: inline-block;
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
  padding: 40px 30px;
  border-radius: var(--border-radius);
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: var(--card-shadow);
}

.empty-state-image {
  width: 200px;
  height: auto;
  margin-bottom: 25px;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.empty-state-text {
  margin-bottom: 30px;
  max-width: 500px;
}

.empty-state-text h3 {
  font-size: 18px;
  color: #333;
  margin-bottom: 10px;
}

.empty-state-text p {
  font-size: 14px;
  color: #666;
  margin-bottom: 20px;
}

.browse-btn {
  background-color: var(--primary-color);
  border-color: var(--primary-color);
}

.empty-state-recommendations {
  width: 100%;
  margin-top: 30px;
  border-top: 1px solid #eee;
  padding-top: 20px;
}

.empty-state-recommendations h4 {
  font-size: 16px;
  color: #333;
  margin-bottom: 15px;
  text-align: left;
}

.recommendation-items {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
}

.recommendation-item {
  background-color: white;
  padding: 12px;
  border-radius: var(--border-radius);
  cursor: pointer;
  transition: all var(--transition-speed);
  box-shadow: var(--card-shadow);
}

.recommendation-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--hover-shadow);
}

.rec-image {
  width: 100%;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10px;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.rec-image img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  opacity: 0.8;
}

.rec-name {
  font-size: 12px;
  color: #333;
  margin-bottom: 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.rec-price {
  font-size: 14px;
  color: var(--primary-color);
  font-weight: bold;
}
</style>
