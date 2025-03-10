<template>
  <div class="product-management">
    <h1>Product Management</h1>
    
    <a-tabs v-model:activeKey="activeTab">
      <a-tab-pane key="list" tab="Product List">
        <a-button type="primary" @click="showAddProductModal" style="margin-bottom: 16px">
          Add New Product
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
                :src="record.imagePath || 'https://via.placeholder.com/50x50?text=No+Image'"
                alt="Product"
                style="width: 50px; height: 50px; object-fit: cover;"
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
              </a-space>
            </template>
          </template>
        </a-table>
      </a-tab-pane>
    </a-tabs>
    
    <!-- Add/Edit Product Modal -->
    <a-modal
      :title="isEditing ? 'Edit Product' : 'Add New Product'"
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
        width: 150
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
      resetForm();
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
      formState,
      rules,
      formRef,
      submitting,
      showAddProductModal,
      editProduct,
      deleteProduct,
      submitForm,
      closeModal
    };
  }
};
</script>

<style scoped>
.product-management {
  padding: 20px 0;
}
</style>
