<template>
  <div class="product-detail-page">
    <div class="container">
      <a-spin :spinning="loading">
        <!-- Breadcrumb navigation -->
        <div class="breadcrumb">
          <a @click="$router.push('/')">Home</a> &gt;
          <a @click="$router.push('/products')">All Products</a> &gt;
          <a @click="$router.push(`/products?category=${product.category}`)">{{ getCategoryName(product.category) }}</a> &gt;
          <span>{{ product.productName }}</span>
        </div>
        
        <div class="product-container" v-if="!error">
          <div class="product-left">
            <div class="product-image">
              <img :src="getCartoonImage(product.imagePath)" :alt="product.productName" />
            </div>
            <div class="product-thumbnails">
              <div class="thumbnail active">
                <img :src="getCartoonImage(product.imagePath)" :alt="product.productName" />
              </div>
            </div>
          </div>
          
          <div class="product-right">
            <h1 class="product-title">{{ product.productName }}</h1>
            <div class="product-shop">Calligraphy Art Store</div>
            
            <div class="product-price-section">
              <div class="price-label">Price</div>
              <div class="product-price">¥{{ product.price }}</div>
            </div>
            
            <div class="product-promotion">
              <div class="promotion-label">Promotions</div>
              <div class="promotion-content">
                <span class="promotion-tag">Limited Time Offer</span>
                <span class="promotion-desc">Free bookmark with purchase</span>
              </div>
            </div>
            
            <div class="product-delivery">
              <div class="delivery-label">Delivery</div>
              <div class="delivery-content">
                <div class="delivery-address">Between 3rd and 4th Ring Road, Chaoyang District, Beijing</div>
                <div class="delivery-time">In stock, free shipping on orders over ¥99</div>
              </div>
            </div>
            
            <div class="product-quantity">
              <div class="quantity-label">Quantity</div>
              <div class="quantity-content">
                <a-input-number v-model:value="quantity" :min="1" :max="product.inventory" />
                <span class="inventory">{{ product.inventory }} in stock</span>
              </div>
            </div>
            
            <div class="product-actions">
              <button class="buy-now-btn">Buy Now</button>
              <button class="add-to-cart-btn">Add to Cart</button>
              <button class="chat-btn" @click="showChatModal">
                <i class="chat-icon">💬</i>
                Customer Service
              </button>
            </div>
            
            <div class="product-services">
              <div class="service-item">
                <i class="service-icon">✓</i>
                <span>Authentic Guarantee</span>
              </div>
              <div class="service-item">
                <i class="service-icon">✓</i>
                <span>Fast Refund</span>
              </div>
              <div class="service-item">
                <i class="service-icon">✓</i>
                <span>7-Day Return</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="product-detail-tabs">
          <a-tabs default-active-key="1">
            <a-tab-pane key="1" tab="Product Description">
              <div class="product-description">
                <h3>Product Details</h3>
                <div class="description-content">
                  {{ product.description }}
                </div>
              </div>
            </a-tab-pane>
            <a-tab-pane key="2" tab="Specifications">
              <div class="product-specs">
                <table class="specs-table">
                  <tr>
                    <th>Brand</th>
                    <td>Calligraphy Art</td>
                  </tr>
                  <tr>
                    <th>Product Name</th>
                    <td>{{ product.productName }}</td>
                  </tr>
                  <tr>
                    <th>Category</th>
                    <td>{{ getCategoryName(product.category) }}</td>
                  </tr>
                </table>
              </div>
            </a-tab-pane>
          </a-tabs>
        </div>
        
        <a-result
          v-if="error"
          status="404"
          title="404"
          sub-title="Sorry, the product you are looking for does not exist."
        >
          <template #extra>
            <a-button type="primary" @click="goBack">
              Return to Product List
            </a-button>
          </template>
        </a-result>
      </a-spin>
    </div>
    
    <!-- Custom Chat Modal -->
    <div v-show="chatModalVisible" class="custom-modal-overlay">
      <div class="custom-modal">
        <div class="custom-modal-header">
          <h3>Customer Service</h3>
          <button class="close-button" @click="closeChatModal">×</button>
        </div>
        <div class="chat-container">
          <div class="chat-messages">
            <div v-for="(item, index) in chatMessages" :key="index" 
                 :class="['message', item.sender === 'user' ? 'user-message' : 'bot-message']">
              <div class="avatar" :style="{ backgroundColor: item.sender === 'user' ? '#e1251b' : '#52c41a' }">
                {{ item.sender === 'user' ? 'Me' : 'CS' }}
              </div>
              <div class="message-content">{{ item.content }}</div>
            </div>
          </div>
          
          <div class="chat-input">
            <input
              v-model="userMessage"
              placeholder="Enter your question..."
              @keyup.enter="sendMessage"
              class="chat-input-field"
            />
            <button class="send-button" @click="sendMessage" :disabled="sendingMessage">
              {{ sendingMessage ? 'Sending...' : 'Send' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, nextTick } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import { getCartoonImage } from '../utils/imageUtils';

export default {
  name: 'ProductDetail',
  setup() {
    const route = useRoute();
    const router = useRouter();
    const product = ref({});
    const loading = ref(false);
    const error = ref(false);
    const chatModalVisible = ref(false);
    const chatMessages = ref([]);
    const userMessage = ref('');
    const sendingMessage = ref(false);
    const quantity = ref(1);
    
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
    
    const getCategoryName = (category) => {
      const categoryMap = {
        'brushes': 'Brushes',
        'ink': 'Ink',
        'paper': 'Rice Paper',
        'calligraphy works': 'Calligraphy Works',
        'accessories': 'Accessories',
        'sets': 'Calligraphy Sets'
      };
      return categoryMap[category] || category;
    };
    
    const goBack = () => {
      router.push('/products');
    };
    
    const showChatModal = () => {
      console.log('showChatModal called');
      
      // Add initial welcome message
      if (chatMessages.value.length === 0) {
        chatMessages.value.push({
          sender: 'bot',
          content: `Welcome! How can I help you with the ${product.value.productName}?`
        });
      }
      
      // Set modal visible
      chatModalVisible.value = true;
      console.log('chatModalVisible set to:', chatModalVisible.value);
      
      // Force a re-render and check
      nextTick(() => {
        console.log('After nextTick, chatModalVisible:', chatModalVisible.value);
        const modalElement = document.querySelector('.custom-modal-overlay');
        console.log('Modal element exists:', !!modalElement);
        
        if (modalElement) {
          modalElement.style.display = 'flex';
        }
      });
    };

    const closeChatModal = () => {
      console.log('Closing chat modal');
      chatModalVisible.value = false;
      
      // Force update DOM
      nextTick(() => {
        const modalElement = document.querySelector('.custom-modal-overlay');
        if (modalElement) {
          modalElement.style.display = 'none';
        }
      });
    };

    const sendMessage = async () => {
      if (!userMessage.value.trim()) return;
      
      // Add user message to chat
      const message = userMessage.value;
      chatMessages.value.push({
        sender: 'user',
        content: message
      });
      userMessage.value = '';
      
      // Show typing indicator
      sendingMessage.value = true;
      
      try {
        // Call backend chat API
        const response = await axios.post(`/api/chat/product/${route.params.id}`, {
          message: message
        });
        
        // Add bot response to chat
        chatMessages.value.push({
          sender: 'bot',
          content: response.data.message
        });
      } catch (error) {
        console.error('Error calling chat API:', error);
        chatMessages.value.push({
          sender: 'bot',
          content: 'Sorry, I encountered an error. Please try again later.'
        });
      } finally {
        sendingMessage.value = false;
      }
    };
    
    onMounted(() => {
      fetchProduct();
    });
    
    return {
      product,
      loading,
      error,
      goBack,
      chatModalVisible,
      chatMessages,
      userMessage,
      sendingMessage,
      quantity,
      showChatModal,
      closeChatModal,
      sendMessage,
      getCartoonImage,
      getCategoryName
    };
  }
};
</script>

<style scoped>
.product-detail-page {
  background-color: var(--light-bg);
  padding: 20px 0;
}

.container {
  width: 100%;
  max-width: var(--container-width);
  margin: 0 auto;
  padding: 0 15px;
}

.breadcrumb {
  margin-bottom: 15px;
  font-size: 12px;
  color: #666;
}

.breadcrumb a {
  color: #666;
  text-decoration: none;
  margin: 0 5px;
  cursor: pointer;
}

.breadcrumb a:hover {
  color: var(--primary-color);
}

.breadcrumb span {
  color: #999;
  margin: 0 5px;
}

.product-container {
  display: flex;
  background-color: white;
  border-radius: var(--border-radius);
  box-shadow: var(--card-shadow);
  margin-bottom: 20px;
  padding: 20px;
}

.product-left {
  width: 400px;
  margin-right: 20px;
}

.product-image {
  width: 100%;
  height: 400px;
  overflow: hidden;
  margin-bottom: 10px;
  border: 1px solid #eee;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.product-thumbnails {
  display: flex;
  gap: 10px;
}

.thumbnail {
  width: 60px;
  height: 60px;
  border: 1px solid #ddd;
  cursor: pointer;
  overflow: hidden;
}

.thumbnail.active {
  border: 2px solid var(--primary-color);
}

.thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-right {
  flex: 1;
  padding: 0 20px;
}

.product-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 10px;
  color: #333;
  line-height: 1.4;
}

.product-shop {
  font-size: 12px;
  color: #999;
  margin-bottom: 15px;
}

.product-price-section {
  display: flex;
  align-items: center;
  background-color: #f7f7f7;
  padding: 15px;
  margin-bottom: 15px;
}

.price-label {
  width: 60px;
  color: #999;
  font-size: 14px;
}

.product-price {
  font-size: 24px;
  font-weight: bold;
  color: var(--primary-color);
}

.product-promotion {
  display: flex;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.promotion-label {
  width: 60px;
  color: #999;
  font-size: 14px;
}

.promotion-content {
  flex: 1;
}

.promotion-tag {
  display: inline-block;
  background-color: var(--primary-color);
  color: white;
  font-size: 12px;
  padding: 2px 5px;
  margin-right: 10px;
}

.promotion-desc {
  font-size: 14px;
  color: #333;
}

.product-delivery {
  display: flex;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.delivery-label {
  width: 60px;
  color: #999;
  font-size: 14px;
}

.delivery-content {
  flex: 1;
}

.delivery-address {
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
}

.delivery-time {
  font-size: 12px;
  color: #999;
}

.product-quantity {
  display: flex;
  margin-bottom: 20px;
}

.quantity-label {
  width: 60px;
  color: #999;
  font-size: 14px;
  line-height: 32px;
}

.quantity-content {
  display: flex;
  align-items: center;
}

.inventory {
  margin-left: 10px;
  font-size: 12px;
  color: #999;
}

.product-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.buy-now-btn {
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: var(--border-radius);
  padding: 10px 30px;
  font-size: 16px;
  cursor: pointer;
  transition: all var(--transition-speed);
}

.buy-now-btn:hover {
  background-color: var(--hover-color);
}

.add-to-cart-btn {
  background-color: #ff9700;
  color: white;
  border: none;
  border-radius: var(--border-radius);
  padding: 10px 30px;
  font-size: 16px;
  cursor: pointer;
  transition: all var(--transition-speed);
}

.add-to-cart-btn:hover {
  background-color: #e68a00;
}

.chat-btn {
  display: flex;
  align-items: center;
  background-color: white;
  color: #666;
  border: 1px solid #ddd;
  border-radius: var(--border-radius);
  padding: 10px 15px;
  font-size: 14px;
  cursor: pointer;
  transition: all var(--transition-speed);
}

.chat-btn:hover {
  color: var(--primary-color);
  border-color: var(--primary-color);
}

.chat-icon {
  margin-right: 5px;
  font-style: normal;
}

.product-services {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.service-item {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #999;
}

.service-icon {
  color: var(--primary-color);
  margin-right: 5px;
  font-style: normal;
}

.product-detail-tabs {
  background-color: white;
  border-radius: var(--border-radius);
  box-shadow: var(--card-shadow);
  padding: 20px;
}

.product-description {
  padding: 20px 0;
}

.product-description h3 {
  font-size: 18px;
  margin-bottom: 15px;
  color: #333;
}

.description-content {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.product-specs {
  padding: 20px 0;
}

.specs-table {
  width: 100%;
  border-collapse: collapse;
}

.specs-table th, .specs-table td {
  padding: 10px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.specs-table th {
  width: 100px;
  color: #999;
  font-weight: normal;
}

.specs-table td {
  color: #333;
}

/* Chat modal styling */
.custom-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  backdrop-filter: blur(5px);
}

.custom-modal {
  width: 500px;
  background-color: white;
  border-radius: var(--border-radius);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  animation: modalFadeIn 0.3s ease-out;
}

@keyframes modalFadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.custom-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background-color: var(--primary-color);
  color: white;
}

.custom-modal-header h3 {
  color: white;
  margin: 0;
  font-weight: 500;
}

.close-button {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: white;
  transition: all var(--transition-speed);
}

.close-button:hover {
  transform: rotate(90deg);
}

.chat-container {
  display: flex;
  flex-direction: column;
  height: 400px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: var(--border-radius);
  height: 300px;
}

.message {
  display: flex;
  margin-bottom: 16px;
  align-items: flex-start;
  animation: messageFadeIn 0.3s ease-out;
}

@keyframes messageFadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.avatar {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  color: white;
  font-weight: bold;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.message-content {
  margin-left: 8px;
  padding: 12px 16px;
  border-radius: 18px;
  max-width: 80%;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.user-message .message-content {
  background: #fff1f0;
  border-top-right-radius: 4px;
}

.bot-message .message-content {
  background: #f6ffed;
  border-top-left-radius: 4px;
}

.chat-input {
  display: flex;
  gap: 8px;
  padding: 0 16px 16px;
}

.chat-input-field {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #d9d9d9;
  border-radius: 24px;
  outline: none;
  transition: all var(--transition-speed);
}

.chat-input-field:focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(225, 37, 27, 0.2);
}

.send-button {
  padding: 0 20px;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 24px;
  cursor: pointer;
  height: 40px;
  box-shadow: 0 2px 6px rgba(225, 37, 27, 0.3);
  transition: all var(--transition-speed);
}

.send-button:hover {
  background-color: var(--hover-color);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(225, 37, 27, 0.5);
}

.send-button:disabled {
  background: #d9d9d9;
  box-shadow: none;
}
</style>
