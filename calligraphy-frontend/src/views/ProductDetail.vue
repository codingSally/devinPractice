<template>
  <div class="product-detail" style="animation: fadeIn 0.5s ease-out; padding: 20px 0; background: linear-gradient(to bottom, rgba(24, 144, 255, 0.05), transparent 300px);">
    <a-spin :spinning="loading">
      <a-page-header
        :title="product.productName"
        @back="goBack"
        style="background: linear-gradient(135deg, rgba(24, 144, 255, 0.05), transparent); border-radius: 8px; margin-bottom: 20px;"
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
            <h1 style="background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; display: inline-block;">{{ product.productName }}</h1>
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
                <span style="font-weight: 500;">💬 Add to Chat</span>
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
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  border: 4px solid white;
}

.product-image:hover {
  transform: scale(1.02);
  box-shadow: var(--hover-shadow);
}

.product-info {
  padding: 0 24px;
}

.product-info h1 {
  font-size: 28px;
  margin-bottom: 16px;
  border-bottom: 2px solid #1890ff;
  padding-bottom: 8px;
  font-weight: 600;
}

:deep(.ant-tag) {
  font-size: 14px;
  padding: 4px 12px;
  border-radius: 16px;
  background: var(--primary-gradient);
  color: white;
  border: none;
}

.price-section {
  margin: 24px 0;
  padding: 20px;
  background: linear-gradient(to right, rgba(24, 144, 255, 0.05), white);
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-left: 4px solid #1890ff;
  animation: slideUp 0.5s ease-out;
}

.price {
  color: #1890ff;
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 8px;
  background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  display: inline-block;
}

.description-section {
  margin-bottom: 32px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  animation: slideUp 0.5s ease-out;
  border-top: 4px solid #36cfc9;
}

.description-section h3 {
  font-size: 20px;
  margin-bottom: 16px;
  color: #333333;
  background: linear-gradient(135deg, rgba(24, 144, 255, 0.1), transparent);
  display: inline-block;
  padding: 6px 12px;
  border-radius: 4px;
}

.actions {
  margin-top: 32px;
}

.primary-button {
  background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
  color: white;
  border: none;
  border-radius: 24px;
  padding: 10px 24px;
  font-size: 16px;
  cursor: pointer;
  height: 48px;
  line-height: 28px;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
  transition: all 0.3s;
  width: 100%;
  max-width: 200px;
}

.primary-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(24, 144, 255, 0.5);
  opacity: 0.9;
}

.chat-container {
  display: flex;
  flex-direction: column;
  height: 400px;
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
  background: var(--primary-gradient);
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
  background: #e6f7ff;
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
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.send-button {
  padding: 0 20px;
  background: var(--primary-gradient);
  color: white;
  border: none;
  border-radius: 24px;
  cursor: pointer;
  height: 40px;
  box-shadow: 0 2px 6px rgba(24, 144, 255, 0.3);
  transition: all var(--transition-speed);
}

.send-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.5);
}

.send-button:disabled {
  background: #d9d9d9;
  box-shadow: none;
}
</style>
