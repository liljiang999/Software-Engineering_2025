<template>
  <div class="teacher-schedule">
    <el-card class="filter-card">
      <template #header>
        <div class="card-header">
          <h2>教师课表查询</h2>
        </div>
      </template>

      <div class="filter-form">
        <el-form :model="filterForm">
          <div class="filter-row">
            <el-form-item label="教师" class="filter-item">
              <el-input v-model="currentTeacher" disabled />
            </el-form-item>
            <el-form-item label="周数" class="filter-item">
              <el-select v-model="filterForm.week" placeholder="请选择周数">
                <el-option
                  v-for="week in 16"
                  :key="week"
                  :label="`第 ${week} 周`"
                  :value="week"
                />
              </el-select>
            </el-form-item>
            <el-button type="primary" @click="handleQuery" :loading="loading">
              查询
            </el-button>
          </div>
        </el-form>
      </div>
    </el-card>

    <el-card class="schedule-card">
      <div class="schedule-container">
        <table class="schedule-table">
          <thead>
            <tr>
              <th>时间</th>
              <th v-for="day in weekDays" :key="day.value">{{ day.label }}</th>
            </tr>
          </thead>
          <tbody>
            <template v-for="(timeSlot, index) in timeSlots" :key="timeSlot.time">
              <tr>
                <td class="time-slot">{{ timeSlot.time }}</td>
                <td
                  v-for="day in weekDays"
                  :key="day.value"
                  :rowspan="getCourseRowspan(day.value, timeSlot)"
                  :class="{
                    'course-cell': true,
                    'occupied': isCourseStart(day.value, timeSlot),
                    'empty': !isCourseStart(day.value, timeSlot)
                  }"
                  v-show="isCourseStart(day.value, timeSlot) || !getCourse(day.value, timeSlot)"
                >
                  <div v-if="isCourseStart(day.value, timeSlot)" class="course-info">
                    <div class="course-name">{{ getCourse(day.value, timeSlot).name }}</div>
                    <div class="classroom">教室: {{ getCourse(day.value, timeSlot).classroom_id }}</div>
                    <div class="time-range">{{ getCourseTimeRange(getCourse(day.value, timeSlot)) }}</div>
                  </div>
                </td>
              </tr>
            </template>
          </tbody>
        </table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import axios from 'axios';

// 实际应从用户认证信息获取
const currentTeacher = ref('张老师');
const currentTeacherId = ref('111'); 
const filterForm = reactive({
  week: 1
});

const loading = ref(false);

const weekDays = ref([
  { value: 'Monday', label: '周一' },
  { value: 'Tuesday', label: '周二' },
  { value: 'Wednesday', label: '周三' },
  { value: 'Thursday', label: '周四' },
  { value: 'Friday', label: '周五' },
  { value: 'Saturday', label: '周六' },
  { value: 'Sunday', label: '周日' }
]);

const timeSlots = ref([
  { time: '08:00-08:45', start: 1, end: 2 },
  { time: '08:50-09:35', start: 3, end: 4 },
  { time: '10:00-10:45', start: 5, end: 6 },
  { time: '10:50-11:35', start: 7, end: 8 },
  { time: '11:40-12:25', start: 9, end: 10 },
  { time: '13:25-14:10', start: 11, end: 12 },
  { time: '14:15-15:00', start: 13, end: 14 },
  { time: '15:05-15:50', start: 15, end: 16 },
  { time: '16:15-17:00', start: 17, end: 18 },
  { time: '17:05-17:50', start: 19, end: 20 },
  { time: '18:50-19:35', start: 21, end: 22 },
  { time: '19:40-20:25', start: 23, end: 24 },
  { time: '20:30-21:15', start: 25, end: 26 }
]);

const scheduleData = ref([]);

// 获取课程
const getCourse = (day, timeSlot) => {
  return scheduleData.value.find(course =>
    course.day === day &&
    course.timeSlot.start <= timeSlot.start &&
    course.timeSlot.end >= timeSlot.end &&
    course.week === filterForm.week
  );
};


const isCourseStart = (day, timeSlot) => {
  const course = getCourse(day, timeSlot);
  if (!course) return false;
  return course.timeSlot.start === timeSlot.start;
};


const getCourseRowspan = (day, timeSlot) => {
  const course = getCourse(day, timeSlot);
  if (!course) return 1;


  const startIndex = timeSlots.value.findIndex(slot => slot.start === course.timeSlot.start);
  const endIndex = timeSlots.value.findIndex(slot => slot.end === course.timeSlot.end);

  return (endIndex - startIndex) + 1;
};


const getCourseTimeRange = (course) => {
  if (!course) return '';
  const startSlot = timeSlots.value.find(slot => slot.start === course.timeSlot.start);
  const endSlot = timeSlots.value.find(slot => slot.end === course.timeSlot.end);

  return `${startSlot.time.split('-')[0]}~${endSlot.time.split('-')[1]}`;
};

const handleQuery = async () => {
  loading.value = true;
  scheduleData.value = [];
  try {
    const response = await axios.get(`/schedules/teacher/${currentTeacherId.value}`, {
      params: {
        week: filterForm.week
      }
    });
    if (response.data) {
      scheduleData.value = response.data.map(item => ({
        id: item.section_id, 
        day: getDayOfWeek(item.sec_time), 
        timeSlot: getTimeSlot(item.sec_time), 
        name: item.course_id, 
        classroom_id: item.classroom_id,
        week: item.week 
      }));
      ElMessage.success('查询成功');
    } else {
      ElMessage.info('该教师本周没有课程安排');
    }
  } catch (error) {
    ElMessage.error('获取教师课表失败');
    console.error('获取教师课表失败:', error);
  } finally {
    loading.value = false;
  }
};

// 辅助函数：根据后端返回的 sec_time 格式解析出星期几 
const getDayOfWeek = (secTime) => {
  // 示例：如果 secTime 是 "周一 10:00 - 11:35"
  if (secTime && secTime.startsWith('周一')) return 'Monday';
  if (secTime && secTime.startsWith('周二')) return 'Tuesday';
  if (secTime && secTime.startsWith('周三')) return 'Wednesday';
  if (secTime && secTime.startsWith('周四')) return 'Thursday';
  if (secTime && secTime.startsWith('周五')) return 'Friday';
  if (secTime && secTime.startsWith('周六')) return 'Saturday';
  if (secTime && secTime.startsWith('周日')) return 'Sunday';
  return '';
};

// 辅助函数：根据后端返回的 sec_time 格式解析出开始和结束时间段 (需要根据你的实际格式和时间段定义调整)
const getTimeSlot = (secTime) => {
  // 示例：如果 secTime 是 "周一 08:50 - 09:35"
  if (secTime && secTime.includes('08:00')) return { start: 1, end: 2 };
  if (secTime && secTime.includes('08:50')) return { start: 3, end: 4 };
  if (secTime && secTime.includes('10:00')) return { start: 5, end: 6 };
  if (secTime && secTime.includes('10:50')) return { start: 7, end: 8 };
  if (secTime && secTime.includes('11:40')) return { start: 9, end: 10 };
  if (secTime && secTime.includes('13:25')) return { start: 11, end: 12 };
  if (secTime && secTime.includes('14:15')) return { start: 13, end: 14 };
  if (secTime && secTime.includes('15:05')) return { start: 15, end: 16 };
  if (secTime && secTime.includes('16:15')) return { start: 17, end: 18 };
  if (secTime && secTime.includes('17:05')) return { start: 19, end: 20 };
  if (secTime && secTime.includes('18:50')) return { start: 21, end: 22 };
  if (secTime && secTime.includes('19:40')) return { start: 23, end: 24 };
  if (secTime && secTime.includes('20:30')) return { start: 25, end: 26 };
  return { start: 0, end: 0 }; // 默认值
};

onMounted(() => {
  // currentTeacher 和 currentTeacherId 从用户的登录信息或全局状态中获取
});
</script>

<style scoped>
.teacher-schedule {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.filter-card {
  margin-bottom: 20px;
}

.card-header {
  text-align: center;
}

.filter-form {
  display: flex;
  justify-content: center;
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 20px;
}

.filter-item {
  margin-bottom: 0;
  min-width: 200px;
}

.schedule-container {
  overflow-x: auto;
}

.schedule-table {
  width: 100%;
  border-collapse: collapse;
}

.schedule-table th,
.schedule-table td {
  border: 1px solid #e0e0e0;
  padding: 8px;
  text-align: center;
}

.schedule-table th {
  background-color: #f5f5f5;
}

.time-slot {
  background-color: #f5f5f5;
  width: 100px;
}

.course-cell {
  height: 80px;
  vertical-align: middle;
}

.empty {
  background-color: #f9f9f9;
}

.occupied {
  background-color: #e3f2fd;
}

.course-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
  height: 100%;
}

.course-name {
  font-weight: bold;
  margin-bottom: 4px;
}

.classroom {
  font-size: 0.9em;
  color: #666;
  margin-bottom: 4px;
}

.time-range {
  font-size: 0.8em;
  color: #888;
}
</style>