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
            :src="getCartoonImage(product.imagePath)"
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
              <button class="primary-button" @click="showChatModal" type="button">
                Add to Chat
              </button>
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
              <div class="avatar" :style="{ backgroundColor: item.sender === 'user' ? '#1890ff' : '#52c41a' }">
                {{ item.sender === 'user' ? 'U' : 'CS' }}
              </div>
              <div class="message-content">{{ item.content }}</div>
            </div>
          </div>
          
          <div class="chat-input">
            <input
              v-model="userMessage"
              placeholder="Ask about this product..."
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
      showChatModal,
      closeChatModal,
      sendMessage,
      getCartoonImage
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

.primary-button {
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 2px;
  padding: 8px 16px;
  font-size: 16px;
  cursor: pointer;
  height: 40px;
  line-height: 24px;
}

.primary-button:hover {
  background-color: #40a9ff;
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
  padding: 8px;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.message {
  display: flex;
  margin-bottom: 8px;
  align-items: flex-start;
}

.message-content {
  margin-left: 8px;
  padding: 8px 12px;
  border-radius: 4px;
  background-color: white;
  max-width: 80%;
}

.user-message .message-content {
  background-color: #e6f7ff;
}

.bot-message .message-content {
  background-color: #f6ffed;
}

.chat-input {
  display: flex;
  gap: 8px;
}

/* Custom Modal Styles */
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
}

.custom-modal {
  width: 500px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.custom-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.custom-modal-header h3 {
  margin: 0;
  color: rgba(0, 0, 0, 0.85);
  font-weight: 500;
}

.close-button {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: rgba(0, 0, 0, 0.45);
}

.close-button:hover {
  color: rgba(0, 0, 0, 0.85);
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 16px;
  padding: 16px;
  background-color: #f5f5f5;
  border-radius: 4px;
  height: 300px;
}

.avatar {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  color: white;
  font-weight: bold;
}

.chat-input-field {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  outline: none;
}

.chat-input-field:focus {
  border-color: #40a9ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.send-button {
  padding: 8px 16px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.send-button:hover {
  background-color: #40a9ff;
}

.send-button:disabled {
  background-color: #d9d9d9;
  cursor: not-allowed;
}
</style>
