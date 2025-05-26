<template>
  <div class="course-auto-arrange container">
    <el-card class="form-card">
      <template #header>
        <div class="card-header">
          <h2>自动排课</h2>
          <p>排课参数配置</p>
        </div>
      </template>

      <el-form :model="config" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="学期">
              <el-select v-model="config.semester" placeholder="请选择学期">
                <el-option
                  v-for="item in semesterOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="排课优先级">
              <el-select v-model="config.priority" multiple placeholder="请选择优先级">
                <el-option label="教师偏好" value="teacher" />
                <el-option label="教室设备" value="equipment" />
                <el-option label="班级连续性" value="continuity" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="排课时间范围">
          <el-date-picker
            v-model="config.dateRange"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>

        <el-form-item label="每日排课时段">
          <el-time-picker
            v-model="config.dayStart"
            placeholder="开始时间"
            format="HH:mm"
          />
          <span class="time-separator">-</span>
          <el-time-picker
            v-model="config.dayEnd"
            placeholder="结束时间"
            format="HH:mm"
          />
        </el-form-item>

        <el-form-item label="特殊约束">
          <el-checkbox-group v-model="config.constraints">
            <el-checkbox label="avoidConsecutive">避免连堂</el-checkbox>
            <el-checkbox label="teacherGap">教师课间休息</el-checkbox>
            <el-checkbox label="classroomConflict">避免教室冲突</el-checkbox>
            <el-checkbox label="classroomGap">教室课间休息</el-checkbox>
            <el-checkbox label="avoidSingle">避免单节课程</el-checkbox>
            <el-checkbox label="avoidWeekend">避免周末排课</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="待排课程">
          <div class="course-selection-container">
            <div class="selection-actions">
              <el-button type="primary" size="small" @click="toggleAllSelection">
                {{ selectAll ? '取消全选' : '全选' }}
              </el-button>
              <span class="selected-count">已选 {{ selectedCourses.length }} 门课程</span>
            </div>

            <div class="table-container">
              <el-table
                :data="courses"
                style="width: 100%; font-size: 15px;"
                height="300"
                empty-text="暂无课程数据"
                @selection-change="handleSelectionChange"
                ref="courseTable"
                border highlight-current-row
              >
                <el-table-column type="selection" width="55" align="center" />
                <el-table-column prop="id" label="课程ID" width="120" align="center" />
                <el-table-column prop="name" label="课程名称" width="180" align="center" />
                <el-table-column prop="teacherId" label="授课教师ID" width="120" align="center" />
                <el-table-column prop="credit" label="学分" width="80" align="center" />
                <el-table-column prop="category" label="类型" width="100" align="center" />
                <el-table-column prop="hoursPerWeek" label="每周课时" width="100" align="center" />
              </el-table>
            </div>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="generateSchedule" :loading="generating">
            生成排课方案
          </el-button>
          <el-button @click="resetConfig">重置参数</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-dialog
      v-model="showProgressDialog"
      title="排课进度"
      width="30%"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
    >
      <el-progress
        :percentage="progressPercent"
        :status="progressStatus"
        :text-inside="true"
        :stroke-width="20"
      />
      <div class="progress-message">{{ progressMessage }}</div>
      <template #footer>
        <el-button
          v-if="progressStatus === 'success'"
          type="primary"
          @click="showProgressDialog = false"
        >
          完成
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

// 配置数据
const config = reactive({
  semester: '2025-1',
  priority: ['teacher', 'equipment'],
  dateRange: [],
  dayStart: '08:00',
  dayEnd: '18:00',
  constraints: ['avoidConsecutive', 'teacherGap']
})

// 课程数据
const courses = ref([])
const courseSearch = ref('')
const selectAll = ref(false)
const selectedCourses = ref([])
const courseTable = ref(null)

// 学期
const semesterOptions = ref([
  { value: '2025-1', label: '2025-2026学年春夏学期' },
  { value: '2025-2', label: '2025-2026学年秋冬学期' }
])


const generating = ref(false)
const showProgressDialog = ref(false)
const progressPercent = ref(0)
const progressStatus = ref('')
const progressMessage = ref('')

// 计算属性 - 过滤课程
const filteredCourses = computed(() => {
  if (!courseSearch.value) return courses.value
  //补全筛选方法
})

// 全选/取消全选
const toggleAllSelection = () => {
  selectAll.value = !selectAll.value
  if (courseTable.value) {
    courses.value.forEach(row => {
      courseTable.value.toggleRowSelection(row, selectAll.value)
    })
  }
}


const handleSelectionChange = (selection) => {
  selectedCourses.value = selection
  selectAll.value = selection.length === courses.value.length && courses.value.length > 0
}


const handleSearchClear = () => {
  courseSearch.value = ''
}

// 生成排课方案
const generateSchedule = async () => {
  if (selectedCourses.value.length === 0) {
    ElMessage.warning('请至少选择一门课程进行排课')
    return
  }

  generating.value = true
  showProgressDialog.value = true
  progressPercent.value = 0
  progressStatus.value = ''
  progressMessage.value = '正在准备排课数据...'

  try {
    const scheduleConfig = {
      semester: config.semester,
      priority: config.priority,
      dateRange: config.dateRange,
      dayStart: config.dayStart,
      dayEnd: config.dayEnd,
      constraints: config.constraints,
      courses: selectedCourses.value.map(course => course.id) // 仅发送课程ID列表
    }

    const response = await axios.post('/api/schedules/generate', scheduleConfig)

    if (response.status === 200) {
      progressPercent.value = 100
      progressStatus.value = 'success'
      progressMessage.value = '排课方案生成成功'
      ElMessage.success('排课方案生成成功')
    } else {
      progressPercent.value = 100
      progressStatus.value = 'error'
      progressMessage.value = '排课方案生成失败'
      ElMessage.error('排课方案生成失败')
    }
  } catch (error) {
    progressPercent.value = 100
    progressStatus.value = 'error'
    progressMessage.value = '排课发生错误'
    ElMessage.error('排课发生错误')
    console.error('生成排课方案失败:', error)
  } finally {
    generating.value = false
  }
}

// 重置配置
const resetConfig = () => {
  Object.assign(config, {
    semester: '2025-1',
    priority: ['teacher', 'equipment'],
    dateRange: [],
    dayStart: '08:00',
    dayEnd: '18:00',
    constraints: ['avoidConsecutive', 'teacherGap']
  })
  if (courseTable.value) {
    courseTable.value.clearSelection()
  }
  selectedCourses.value = []
  selectAll.value = false
  courseSearch.value = ''
}

onMounted(async () => {
  try {
        // 测试数据
      courses.value = [
      {
        id: 210,
        name: "Course 41",
        teacherId: 213,
        credit: 3,
        category: "普通",
        hoursPerWeek: 5,
        description: "Description 14"
      },
      {
        id: 211,
        name: "Course 42",
        teacherId: 214,
        credit: 4,
        category: "必修",
        hoursPerWeek: 6,
        description: "Description 15"
      }
    ];
    
    // 实际API调用
    // const response = await axios.get('/api/courses');
    // courses.value = response.data;
  } catch (error) {
    ElMessage.error('获取课程数据失败');
    console.error('获取课程数据失败:', error);
  }
})
</script>

<style scoped>
.course-auto-arrange {
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

.card-header h2 {
  margin-bottom: 5px;
}

.card-header p {
  margin: 0;
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.time-separator {
  margin: 0 10px;
  color: var(--el-text-color-secondary);
}

.course-selection-container {
  width: 100%;
}

.selection-actions {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.selected-count {
  margin-left: 15px;
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.progress-message {
  margin-top: 10px;
  text-align: center;
  color: var(--el-text-color-secondary);
}
</style>