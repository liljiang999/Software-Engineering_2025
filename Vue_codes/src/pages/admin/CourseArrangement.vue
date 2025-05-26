<template>
  <div class="course-modify container">
    <el-card class="form-card">
      <template #header>
        <div class="card-header">
          <h2>调整课程安排</h2>
          <p>请输入筛选条件查找要调整的课程信息</p>
        </div>
      </template>

      <el-form :model="filterForm" label-width="120px" class="input-form">
        <el-form-item label="开课ID">
          <el-input v-model="filterForm.section_id" placeholder="请输入开课ID" />
        </el-form-item>
        <el-form-item label="课程ID">
          <el-input v-model="filterForm.course_id" placeholder="请输入课程ID" />
        </el-form-item>
        <el-form-item label="教室ID">
          <el-input v-model="filterForm.classroom_id" placeholder="请输入教室ID" />
        </el-form-item>
        <el-form-item label="容量">
          <el-input v-model.number="filterForm.capacity" placeholder="请输入容量" type="number" />
        </el-form-item>
        <el-form-item label="学期">
          <el-input v-model="filterForm.semester" placeholder="请输入学期" />
        </el-form-item>
        <el-form-item label="开课年份">
          <el-input v-model.number="filterForm.sec_year" placeholder="请输入开课年份" type="number" />
        </el-form-item>
        <el-form-item label="开课时间">
          <el-input v-model="filterForm.sec_time" placeholder="请输入开课时间" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleFilter" :loading="filterLoading">
            查询课程
          </el-button>
          <el-button @click="resetFilterForm">
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="course-card">
      <template #header>
        <div class="card-header">
          <h2 v-if="courseList.length > 0">
            共查询到 {{ courseList.length }} 门课程
          </h2>
          <h2 v-else-if="hasSearched">
            未查询到符合条件的课程
          </h2>
          <h2 v-else>
            请输入查询条件
          </h2>
        </div>
      </template>

      <el-table :data="courseList" style="font-size: 15px;" empty-text="暂无课程数据">
        <el-table-column prop="section_id" label="开课ID" />
        <el-table-column prop="course_id" label="课程ID" />
        <el-table-column prop="classroom_id" label="教室ID" />
        <el-table-column prop="capacity" label="容量" />
        <el-table-column prop="semester" label="学期" />
        <el-table-column prop="sec_year" label="开课年份" />
        <el-table-column prop="sec_time" label="开课时间" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button type="primary" size="small" @click="openEditDialog(scope.row)">
              修改
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="editDialogVisible"
      :title="`修改课程 - ${editFormData.section_id}`"
      width="600px"
    >
      <el-form :model="editFormData" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开课ID">
              <el-input v-model="editFormData.section_id" :disabled="true" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程ID">
              <el-input v-model="editFormData.course_id" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="教室ID">
              <el-input v-model="editFormData.classroom_id" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="容量">
              <el-input-number v-model="editFormData.capacity" :min="1" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学期">
              <el-input v-model="editFormData.semester" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开课年份">
              <el-input v-model="editFormData.sec_year" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="开课时间">
          <el-input v-model="editFormData.sec_time" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEdit" :loading="editLoading">保存修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
  import { ref, reactive } from 'vue'
  import { ElMessage } from 'element-plus'
  import axios from 'axios'

  const filterForm = reactive({
    section_id: '',
    course_id: '',
    classroom_id: '',
    capacity: '',
    semester: '',
    sec_year: '',
    sec_time: ''
  })

  const filterLoading = ref(false)
  const hasSearched = ref(false)
  const courseList = ref([])

  // 编辑弹窗相关
  const editDialogVisible = ref(false)
  const editFormData = reactive({
    section_id: '',
    course_id: '',
    classroom_id: '',
    capacity: null,
    semester: '',
    sec_year: null,
    sec_time: ''
  })
  const editLoading = ref(false)

  // 获取课程信息 (用于筛选)
  const handleFilter = async () => {
    filterLoading.value = true
    hasSearched.value = true
    courseList.value = []

    try {
      const queryParams = {}
      for (const key in filterForm) {
        if (filterForm[key] !== '') {
          queryParams[key] = filterForm[key]
        }
      }

      const response = await axios.get('/api/sections/query', { // 后端查询课程安排的接口 /sections/query
        params: queryParams
      })

      if (response.data) {
        // 字段映射：将后端返回的字段名转换为前端所需的字段名
        courseList.value = response.data.map(item => ({
          section_id: item.id,
          course_id: item.courseId,
          classroom_id: item.classroomId,
          capacity: item.capacity,
          semester: item.semester,
          sec_year: item.secYear,
          sec_time: item.secTime
          // availableCapacity 不用管
        }))
        if (courseList.value.length === 0) {
          ElMessage.info('未查询到符合条件的课程安排')
        }
      } else {
        ElMessage.error('获取课程安排失败')
      }
    } catch (error) {
      ElMessage.error('网络错误或服务器不可用')
      console.error('筛选课程安排失败:', error)
    } finally {
      filterLoading.value = false
    }
  }

  // 重置筛选条件
  const resetFilterForm = () => {
    Object.keys(filterForm).forEach(key => {
      filterForm[key] = ''
    })
    courseList.value = []
    hasSearched.value = false
    ElMessage.success('筛选条件已重置')
  }

  // 打开编辑弹窗
  const openEditDialog = (row) => {
    Object.assign(editFormData, { ...row })
    editDialogVisible.value = true
  }

  // 保存编辑信息
  const saveEdit = async () => {
    editLoading.value = true
    try {
      const response = await axios.put(`/api/sections/${editFormData.section_id}`, {
        course_id: editFormData.course_id,
        classroom_id: editFormData.classroom_id,
        capacity: editFormData.capacity,
        semester: editFormData.semester,
        sec_year: editFormData.sec_year,
        sec_time: editFormData.sec_time
      })

      if (response.status === 200) {
        ElMessage.success(`开课 ID ${editFormData.section_id} 信息修改成功`)
        editDialogVisible.value = false
        
        const index = courseList.value.findIndex(
          item => item.section_id === editFormData.section_id
        )
        if (index!== -1) {
          courseList.value[index] = { ...editFormData }
        }
      } else {
        ElMessage.error(`修改开课 ID ${editFormData.section_id} 信息失败`)
      }
    } catch (error) {
      ElMessage.error('网络错误或服务器不可用')
      console.error('修改课程安排信息失败:', error)
    } finally {
      editLoading.value = false
    }
  }
</script>

<style scoped>
  .course-modify {
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

  .course-card {
    width: 95%;
    margin: 0px auto 20px auto;
  }
</style>