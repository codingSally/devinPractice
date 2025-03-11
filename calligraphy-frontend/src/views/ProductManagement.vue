<template>
  <div class="product-management">
    <h1 class="gradient-title">Product Management</h1>
    
    <a-tabs v-model:activeKey="activeTab">
      <a-tab-pane key="list" tab="Product List">
        <a-button type="primary" @click="showAddProductModal" class="add-product-btn">
          <span style="font-weight: 500;">Add New Product</span>
        </a-button>
        
        <a-table
          :columns="columns"
          :data-source="products"
          :loading="loading"
          :pagination="{ pageSize: 10 }"
          rowKey="productId"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'image'">
              <img
                :src="getCartoonImage(record.imagePath)"
                alt="Product"
                style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px;"
              />
            </template>
            
            <template v-if="column.key === 'price'">
              ${{ record.price }}
            </template>
            
            <template v-if="column.key === 'action'">
              <a-space>
                <a-button type="primary" size="small" @click="editProduct(record)">
                  Edit
                </a-button>
                <a-popconfirm
                  title="Are you sure you want to delete this product?"
                  ok-text="Yes"
                  cancel-text="No"
                  @confirm="deleteProduct(record.productId)"
                >
                  <a-button type="danger" size="small">
                    Delete
                  </a-button>
                </a-popconfirm>
                <a-button type="primary" size="small" @click="showCopyModal(record)" style="background: #722ed1; border-color: #722ed1;">
                  Copy
                </a-button>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-tab-pane>
    </a-tabs>
    
    <!-- Add/Edit Product Modal -->
    <a-modal
      :title="isEditing ? 'Edit Product' : (isCopying ? 'Copy Product' : 'Add New Product')"
      :visible="modalVisible"
      @cancel="closeModal"
      :footer="null"
      width="700px"
    >
      <a-form
        :model="formState"
        :rules="rules"
        ref="formRef"
        layout="vertical"
        @finish="submitForm"
      >
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item name="productName" label="Product Name" required>
              <a-input v-model:value="formState.productName" />
            </a-form-item>
          </a-col>
          
          <a-col :span="12">
            <a-form-item name="category" label="Category" required>
              <a-select v-model:value="formState.category">
                <a-select-option value="brushes">Brushes</a-select-option>
                <a-select-option value="ink">Ink</a-select-option>
                <a-select-option value="paper">Paper</a-select-option>
                <a-select-option value="calligraphy works">Calligraphy Works</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item name="price" label="Price" required>
              <a-input-number
                v-model:value="formState.price"
                :min="0"
                :precision="2"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          
          <a-col :span="12">
            <a-form-item name="inventory" label="Inventory" required>
              <a-input-number
                v-model:value="formState.inventory"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-form-item name="description" label="Description">
          <a-textarea
            v-model:value="formState.description"
            :rows="4"
            placeholder="Enter product description"
          />
        </a-form-item>
        
        <a-form-item name="imagePath" label="Image URL">
          <a-input v-model:value="formState.imagePath" placeholder="Enter image URL" />
        </a-form-item>
        
        <a-form-item>
          <a-button type="primary" html-type="submit" :loading="submitting">
            {{ isEditing ? 'Update' : 'Create' }}
          </a-button>
          <a-button style="margin-left: 8px" @click="closeModal">
            Cancel
          </a-button>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { getCartoonImage } from '../utils/imageUtils';
import { message } from 'ant-design-vue';

export default {
  name: 'ProductManagement',
  setup() {
    const store = useStore();
    const router = useRouter();
    const formRef = ref(null);
    
    // Check if user is admin
    const isAdmin = computed(() => store.getters.isAdmin);
    if (!isAdmin.value) {
      router.push('/login');
    }
    
    const activeTab = ref('list');
    const products = ref([]);
    const loading = ref(false);
    const modalVisible = ref(false);
    const isEditing = ref(false);
    const isCopying = ref(false);
    const submitting = ref(false);
    
    const formState = reactive({
      productId: null,
      productName: '',
      description: '',
      price: 0,
      inventory: 0,
      imagePath: '',
      category: 'brushes',
      popularity: 0
    });
    
    const rules = {
      productName: [{ required: true, message: 'Please enter product name' }],
      category: [{ required: true, message: 'Please select a category' }],
      price: [{ required: true, message: 'Please enter price' }],
      inventory: [{ required: true, message: 'Please enter inventory' }]
    };
    
    const columns = [
      {
        title: 'Image',
        key: 'image',
        width: 80
      },
      {
        title: 'Name',
        dataIndex: 'productName',
        key: 'productName'
      },
      {
        title: 'Category',
        dataIndex: 'category',
        key: 'category'
      },
      {
        title: 'Price',
        dataIndex: 'price',
        key: 'price'
      },
      {
        title: 'Inventory',
        dataIndex: 'inventory',
        key: 'inventory'
      },
      {
        title: 'Actions',
        key: 'action',
        width: 220
      }
    ];
    
    const fetchProducts = async () => {
      loading.value = true;
      try {
        const token = store.state.token;
        const response = await axios.get('/api/products', {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        products.value = response.data;
      } catch (error) {
        console.error('Error fetching products:', error);
      } finally {
        loading.value = false;
      }
    };
    
    const showAddProductModal = () => {
      isEditing.value = false;
      resetForm();
      modalVisible.value = true;
    };
    
    const editProduct = (product) => {
      isEditing.value = true;
      Object.keys(formState).forEach(key => {
        formState[key] = product[key];
      });
      modalVisible.value = true;
    };
    
    const deleteProduct = async (productId) => {
      try {
        const token = store.state.token;
        await axios.delete(`/api/products/${productId}`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        fetchProducts();
      } catch (error) {
        console.error('Error deleting product:', error);
      }
    };
    
    const submitForm = async () => {
      submitting.value = true;
      try {
        const token = store.state.token;
        const headers = {
          'Authorization': `Bearer ${token}`
        };
        
        // Check for duplicate product name if adding or copying
        if (!isEditing.value) {
          const isDuplicate = products.value.some(
            product => product.productName.toLowerCase() === formState.productName.toLowerCase()
          );
          
          if (isDuplicate) {
            message.error('A product with this name already exists. Please use a different name.');
            submitting.value = false;
            return;
          }
        }
        
        if (isEditing.value) {
          await axios.put(`/api/products/${formState.productId}`, formState, { headers });
        } else {
          await axios.post('/api/products', formState, { headers });
        }
        
        closeModal();
        fetchProducts();
      } catch (error) {
        console.error('Error saving product:', error);
      } finally {
        submitting.value = false;
      }
    };
    
    const closeModal = () => {
      modalVisible.value = false;
      isEditing.value = false;
      isCopying.value = false;
      resetForm();
    };
    
    const showCopyModal = (product) => {
      isCopying.value = true;
      // Pre-fill the form with product details but clear the productId
      Object.keys(formState).forEach(key => {
        if (key === 'productId') {
          formState[key] = null; // Clear productId for new product
        } else {
          formState[key] = product[key];
        }
      });
      // Slightly modify the product name to indicate it's a copy
      formState.productName = `${product.productName} (Copy)`;
      modalVisible.value = true;
    };
    
    const resetForm = () => {
      formState.productId = null;
      formState.productName = '';
      formState.description = '';
      formState.price = 0;
      formState.inventory = 0;
      formState.imagePath = '';
      formState.category = 'brushes';
      formState.popularity = 0;
      
      if (formRef.value) {
        formRef.value.resetFields();
      }
    };
    
    onMounted(() => {
      fetchProducts();
    });
    
    return {
      activeTab,
      products,
      loading,
      columns,
      modalVisible,
      isEditing,
      isCopying,
      formState,
      rules,
      formRef,
      submitting,
      showAddProductModal,
      editProduct,
      deleteProduct,
      submitForm,
      closeModal,
      showCopyModal,
      getCartoonImage
    };
  }
};
</script>

<style scoped>
.product-management {
  padding: 20px 0;
  animation: fadeIn 0.5s ease-out;
}

.gradient-title {
  font-size: 28px;
  margin-bottom: 24px;
  background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-fill-color: transparent;
  border-bottom: 2px solid #1890ff;
  padding-bottom: 8px;
  display: inline-block;
}

/* Gradient text styling */
.gradient-text {
  background: var(--primary-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-fill-color: transparent;
  display: inline-block;
  border-bottom: 2px solid var(--primary-color);
  padding-bottom: 8px;
}

:deep(.ant-table) {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  animation: slideUp 0.5s ease-out;
  border: none;
}

:deep(.ant-table-container) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.ant-table-thead > tr > th) {
  background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%) !important;
  color: white !important;
  font-weight: 600;
  border: none !important;
  padding: 12px 16px;
}

:deep(.ant-table-tbody > tr:hover > td) {
  background: #f0f5ff;
}

:deep(.ant-table-tbody > tr > td) {
  transition: all var(--transition-speed);
}

:deep(.ant-btn) {
  border-radius: 4px;
  transition: all var(--transition-speed);
}

:deep(.ant-btn:hover) {
  transform: translateY(-2px);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.add-product-btn {
  margin-bottom: 16px;
  background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%) !important;
  border: none !important;
  box-shadow: 0 2px 6px rgba(24, 144, 255, 0.3) !important;
  border-radius: 4px !important;
  height: 40px;
  padding: 0 20px !important;
  transition: all 0.3s !important;
}

.add-product-btn:hover {
  transform: translateY(-2px) !important;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.5) !important;
}

:deep(.ant-btn-primary:hover) {
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.5);
  transform: translateY(-2px);
}

:deep(.ant-btn-danger) {
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
  border: none;
  color: white;
  box-shadow: 0 2px 6px rgba(255, 77, 79, 0.3);
  transition: all 0.3s;
}

:deep(.ant-btn-danger:hover) {
  box-shadow: 0 4px 12px rgba(255, 77, 79, 0.5);
}

:deep(.ant-modal-content) {
  border-radius: var(--border-radius);
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

:deep(.ant-modal-header) {
  background: var(--primary-gradient);
  border-bottom: none;
}

:deep(.ant-modal-title) {
  color: white;
  font-weight: 500;
}

:deep(.ant-modal-close) {
  color: white;
}

:deep(.ant-modal-footer) {
  border-top: none;
  padding: 16px 24px 24px;
}

:deep(.ant-form-item-label > label) {
  font-weight: 500;
  color: var(--text-color);
}

:deep(.ant-input), :deep(.ant-input-number), :deep(.ant-select-selector) {
  border-radius: 4px;
  transition: all var(--transition-speed);
}

:deep(.ant-input:focus), :deep(.ant-input-number:focus), :deep(.ant-select-selector:focus) {
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

:deep(.ant-tabs-tab) {
  transition: all var(--transition-speed);
}

:deep(.ant-tabs-tab:hover) {
  color: var(--primary-color);
}

:deep(.ant-tabs-tab-active) {
  background: linear-gradient(to right, rgba(24, 144, 255, 0.1), transparent);
}

:deep(.ant-tabs-ink-bar) {
  background: var(--primary-gradient);
}
</style>
