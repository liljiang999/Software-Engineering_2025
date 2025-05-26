<template>
  <div class="classroom-browse-admin container">
    <el-card class="form-card">
      <template #header>
        <div class="card-header">
          <h2>浏览教室信息（管理员端）</h2>
          <p>请输入筛选教室信息（不能全为空）</p>
        </div>
      </template>

      <el-form :model="formData" label-width="120px" class="input-form">
        <el-form-item label="教室 ID">
          <el-input v-model="formData.classroom_id" placeholder="请输入教室 ID" />
        </el-form-item>
        <el-form-item label="位置">
          <el-input v-model="formData.classroom_location" placeholder="请输入教室位置" />
        </el-form-item>
        <el-form-item label="容量">
          <el-input v-model.number="formData.classroom_capacity" placeholder="请输入教室容量" type="number" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery" :loading="loading">
            开始筛选
          </el-button>
          <el-button @click="resetForm">
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="classroom-card">
      <template #header>
        <div class="card-header">
          <h2 v-if="classroomList.length > 0">
            共查询到 {{ classroomList.length }} 间教室
          </h2>
          <h2 v-else-if="hasSearched">
            未查询到符合条件的教室
          </h2>
          <h2 v-else>
            请输入查询条件
          </h2>
        </div>
      </template>

      <el-table :data="classroomList" style="font-size: 15px;" empty-text="暂无教室数据">
        <el-table-column prop="classroom_id" label="教室 ID" />
        <el-table-column prop="classroom_location" label="位置" />
        <el-table-column prop="classroom_capacity" label="容量" />
      </el-table>
    </el-card>
  </div>
</template>


<script setup>
import { ref, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import axios from 'axios';

const formData = reactive({
  classroom_id: '',
  classroom_location: '',
  classroom_capacity: ''
});

const loading = ref(false);
const classroomList = ref([]);
const hasSearched = ref(false); // 标记是否进行过搜索

// 获取教室信息
const handleQuery = async () => {
  loading.value = true;
  hasSearched.value = true;
  classroomList.value = []; // 清空查询结果

  try {
    // 构建查询参数，只包含非空字段
    const queryParams = {};
    for (const key in formData) {
      if (formData[key] !== '') {
        queryParams[key] = formData[key];
      }
    }

    // 如果所有字段都为空，给出提示
    if (Object.keys(queryParams).length === 0) {
      ElMessage.warning('请输入至少一个查询条件');
      loading.value = false;
      return;
    }

    const response = await axios.get('/classrooms/query', {// 发送查询请求
      params: queryParams
    });

    if (response.data.success) {
      classroomList.value = response.data.data; // 后端返回 { success: true, data: [...] }
      if (classroomList.value.length === 0) {
        ElMessage.info('未查询到符合条件的教室');
      }
    } else {
      ElMessage.error(response.data.message || '获取教室信息失败');
    }
  } catch (error) {
    ElMessage.error('网络错误或服务器不可用');
    console.error('获取教室信息失败:', error);
  } finally {
    loading.value = false;
  }
};

// 重置筛选条件
const resetForm = () => {
  Object.keys(formData).forEach(key => {
    formData[key] = '';
  });
  classroomList.value = [];
  hasSearched.value = false;
  ElMessage.success('表单已重置');
};
</script>

<style scoped>
.classroom-browse-admin {
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
  margin: 0px auto 20px auto;
}

.card-header {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.input-form {
  max-width: 500px;
  margin: 0 auto;
}

.submit-area {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

.classroom-card {
  width: 95%;
  margin: 0px auto 20px auto;
}
</style>