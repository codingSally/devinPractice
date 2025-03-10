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
              <a-button type="primary" size="large" @click="showChatModal">
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
    
    <!-- Chat Modal -->
    <a-modal
      title="Customer Service"
      :visible="chatModalVisible"
      @cancel="closeChatModal"
      :footer="null"
      width="500px"
    >
      <div class="chat-container">
        <a-list
          class="chat-messages"
          :data-source="chatMessages"
          :split="false"
        >
          <template #renderItem="{ item }">
            <a-list-item>
              <div :class="['message', item.sender === 'user' ? 'user-message' : 'bot-message']">
                <a-avatar :style="{ backgroundColor: item.sender === 'user' ? '#1890ff' : '#52c41a' }">
                  {{ item.sender === 'user' ? 'U' : 'CS' }}
                </a-avatar>
                <div class="message-content">{{ item.content }}</div>
              </div>
            </a-list-item>
          </template>
        </a-list>
        
        <div class="chat-input">
          <a-input
            v-model:value="userMessage"
            placeholder="Ask about this product..."
            @pressEnter="sendMessage"
          />
          <a-button type="primary" @click="sendMessage" :loading="sendingMessage">Send</a-button>
        </div>
      </div>
    </a-modal>
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
      chatModalVisible.value = true;
      // Add initial welcome message
      if (chatMessages.value.length === 0) {
        chatMessages.value.push({
          sender: 'bot',
          content: `Welcome! How can I help you with the ${product.value.productName}?`
        });
      }
    };

    const closeChatModal = () => {
      chatModalVisible.value = false;
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
        // Call LLM API with product context
        const response = await callLlmApi(message, product.value);
        
        // Add bot response to chat
        chatMessages.value.push({
          sender: 'bot',
          content: response
        });
      } catch (error) {
        console.error('Error calling LLM API:', error);
        chatMessages.value.push({
          sender: 'bot',
          content: 'Sorry, I encountered an error. Please try again later.'
        });
      } finally {
        sendingMessage.value = false;
      }
    };
    
    const callLlmApi = async (message, productInfo) => {
      // Using Hugging Face Inference API as an example
      // You would need to replace this with your preferred LLM API
      try {
        const prompt = `
          You are a helpful customer service assistant for a calligraphy art store.
          
          Product Information:
          - Name: ${productInfo.productName}
          - Price: $${productInfo.price}
          - Category: ${productInfo.category}
          - Description: ${productInfo.description}
          - Inventory: ${productInfo.inventory} items in stock
          
          Customer Question: ${message}
          
          Provide a helpful, concise response about this product. If asked about price, inventory, or features, use the product information provided.
        `;
        
        // For demo purposes, simulate API call with a timeout
        // In production, replace with actual API call
        return new Promise((resolve) => {
          setTimeout(() => {
            // Generate a simple response based on the question
            let response = "I'm sorry, I don't have specific information about that.";
            
            if (message.toLowerCase().includes('price')) {
              response = `The price of ${productInfo.productName} is $${productInfo.price}.`;
            } else if (message.toLowerCase().includes('inventory') || message.toLowerCase().includes('stock')) {
              response = `We currently have ${productInfo.inventory} ${productInfo.productName} items in stock.`;
            } else if (message.toLowerCase().includes('description') || message.toLowerCase().includes('what is') || message.toLowerCase().includes('tell me about')) {
              response = `${productInfo.productName} is ${productInfo.description}`;
            } else if (message.toLowerCase().includes('category')) {
              response = `${productInfo.productName} belongs to the ${productInfo.category} category.`;
            } else if (message.toLowerCase().includes('hello') || message.toLowerCase().includes('hi')) {
              response = `Hello! How can I help you with the ${productInfo.productName} today?`;
            }
            
            resolve(response);
          }, 1000);
        });
        
        /* Uncomment and replace with your actual API key for production use
        const response = await axios.post('https://api-inference.huggingface.co/models/mistralai/Mistral-7B-Instruct-v0.2', {
          inputs: prompt,
          parameters: {
            max_new_tokens: 250,
            temperature: 0.7,
            top_p: 0.95,
            do_sample: true
          }
        }, {
          headers: {
            'Authorization': 'Bearer YOUR_HUGGINGFACE_API_KEY',
            'Content-Type': 'application/json'
          }
        });
        
        return response.data[0].generated_text || "I'm sorry, I couldn't generate a response.";
        */
      } catch (error) {
        console.error('Error calling LLM API:', error);
        return "I'm sorry, I'm having trouble connecting to my knowledge base right now.";
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
      sendMessage
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
</style>
