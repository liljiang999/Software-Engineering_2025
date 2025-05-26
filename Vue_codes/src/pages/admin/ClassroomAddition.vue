<template>
  <div class="classroom-addition container">
    <el-card class="form-card">
      <template #header>
        <div class="card-header">
          <h2>添加教室信息</h2>
          <p>请输入完整教室信息</p>
        </div>
      </template>
      
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="120px" class="input-form">
        <el-form-item label="教室 ID" prop="classroom_id">
          <el-input v-model.number="formData.classroom_id" placeholder="请输入新增教室 ID" />
        </el-form-item>
        <el-form-item label="位置" prop="position">
          <el-input v-model="formData.position" placeholder="请输入新增教室位置" />
        </el-form-item>
        <el-form-item label="容量" prop="capacity">
          <el-input v-model.number="formData.capacity" placeholder="请输入新增教室容量" type="number" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleAddition" :loading="loading">
            添加教室
          </el-button>
          <el-button @click="resetForm">
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { ElMessage, ElNotification } from 'element-plus';
import axios from 'axios';

// 表单数据 - 键名与数据库字段及 v-model 绑定保持一致
const formData = reactive({
  classroom_id: '',
  position: '',
  capacity: ''
});

// 表单验证规则 - 键名与 formData 保持一致
const rules = {
  classroom_id: [
    { required: true, message: '请输入教室ID', trigger: 'blur' },
    { type: 'number', message: '教室ID必须为数字' },
    { 
      validator: (rule, value, callback) => {
        if (value < 1) {
          callback(new Error('教室ID必须大于等于1'));
        } else {
          callback();
        }
      }, 
      trigger: 'blur' 
    }
  ],
  position: [
    { required: true, message: '请输入教室位置', trigger: 'blur' },
    { min: 1, max: 255, message: '长度在1到255个字符之间', trigger: 'blur' }
  ],
  capacity: [
    { required: true, message: '请输入教室容量', trigger: 'blur' },
    { type: 'number', message: '容量必须为数字' },
    { 
      validator: (rule, value, callback) => {
        if (value < 1) {
          callback(new Error('容量必须大于等于1'));
        } else {
          callback();
        }
      },
      trigger: 'blur' 
    }
  ]
};

// 表单引用
const formRef = ref(null);
const loading = ref(false);

// 添加教室API调用
const handleAddition = async () => {
  // 确保 formRef 存在
  if (!formRef.value) return;

  try {
    // 表单验证
    await formRef.value.validate();
    
    loading.value = true;
    
    // 构造请求数据 - 字段名与数据库完全一致
    // 注意：由于 v-model.number, formData 中的 classroom_id 和 capacity 已经是 number 类型或空字符串
    const requestData = {
      classroom_id: formData.classroom_id,
      position: formData.position.trim(),
      capacity: formData.capacity
    };
    
    // API
    const response = await axios.post('/classrooms', requestData);
    
    // 成功处理
    ElNotification({
      title: '成功',
      message: '教室添加成功',
      type: 'success'
    });
    
    // 重置表单
    resetForm();
    
  } catch (error) {
    // 错误处理
    if (error && error.response) {
      const { status, data } = error.response;
      if (status === 409) {
        ElMessage.error('教室ID已存在');
      } else {
        ElMessage.error(data.message || '添加教室失败');
      }
    } else if (error instanceof Error && error.message.includes('validation failed')) {
      // 这是 validate() 失败时的情况，可以不作处理，因为 Element Plus 会自动显示错误信息
    } else {
      ElMessage.error('网络错误或服务器不可用');
      console.error('API调用错误:', error);
    }
  } finally {
    loading.value = false;
  }
};

// 重置表单
const resetForm = () => {
  if (!formRef.value) return;
  formRef.value.resetFields();
  // ElMessage.success('表单已重置'); // 重置操作通常不需要消息提示，以免干扰用户
};
</script>

<style scoped>
/* 样式保持不变 */
.classroom-addition {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-card {
  width: 95%;
  margin: 0 auto 20px auto;
}

.card-header {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.card-header p {
  margin-top: 8px;
  color: #666;
}

.input-form {
  max-width: 500px;
  margin: 0 auto;
}
</style>