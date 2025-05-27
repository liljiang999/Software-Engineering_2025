<template>
  <div class="classroom-schedule">
    <el-card class="filter-card">
      <template #header>
        <div class="card-header">
          <h2>教室课表查询</h2>
        </div>
      </template>

      <div class="filter-form">
        <el-form :model="filterForm">
          <div class="filter-row">
            <el-form-item label="教室" class="filter-item same-size">
              <el-autocomplete
                v-model="filterForm.classroom"
                :fetch-suggestions="queryClassrooms"
                placeholder="请输入教室ID"
                @select="handleClassroomSelect"
              />
            </el-form-item>
            <el-form-item label="学期" class="filter-item same-size">
              <el-select v-model="filterForm.semester" placeholder="请选择学期">
                <el-option label="春夏" value="春夏" />
                <el-option label="秋冬" value="秋冬" />
              </el-select>
            </el-form-item>
            <el-form-item label="学年" class="filter-item same-size">
              <el-select v-model="filterForm.secYear" placeholder="请选择学年">
                <el-option
                  v-for="year in [2024, 2025, 2026]"
                  :key="year"
                  :label="year"
                  :value="year"
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
                    <div class="course-name">课程ID: {{ getCourse(day.value, timeSlot).name }}</div>
                    <div class="teacher">容量: {{ getCourse(day.value, timeSlot).capacity }}/{{ getCourse(day.value, timeSlot).availableCapacity }}</div>
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

const filterForm = reactive({
  classroom: '',
  classroomId: '',
  semester: '春夏',
  secYear: new Date().getFullYear()
});

const loading = ref(false);
const classrooms = ref([]);

const queryClassrooms = async (query, callback) => {
  try {
    const response = await axios.get('/api/classrooms/query', {
      params: { location: query }
    });
    if (response.data) {
      callback(response.data.map(item => ({ 
        value: item.location,
        id: item.id
      })));
    } else {
      callback([]);
    }
  } catch (error) {
    ElMessage.error('获取教室失败');
    console.error('获取教室失败:', error);
    callback([]);
  }
};

const handleClassroomSelect = (item) => {
  filterForm.classroom = item.value;
  filterForm.classroomId = item.id;
};

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

const getCourse = (day, timeSlot) => {
  return scheduleData.value.find(course =>
    course.day === day &&
    course.timeSlot.start <= timeSlot.start &&
    course.timeSlot.end >= timeSlot.end &&
    course.classroom_id === filterForm.classroomId
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
  
  if (!startSlot || !endSlot) return '';
  return `${startSlot.time.split('-')[0]}~${endSlot.time.split('-')[1]}`;
};

const handleQuery = async () => {
  loading.value = true;
  scheduleData.value = [];
  try {
    const response = await axios.get('/api/sections/query', {
      params: {
        classroom_id: filterForm.classroomId,
        semester: filterForm.semester,
        sec_year: filterForm.secYear
      }
    });
    if (response.data) {
      scheduleData.value = response.data.map(item => ({
        id: item.id,
        day: getDayOfWeek(item.secTime), 
        timeSlot: getTimeSlot(item.secTime), 
        name: item.courseId,
        teacher: '待定', 
        classroom_id: item.classroomId,
        capacity: item.capacity,
        availableCapacity: item.availableCapacity,
        semester: item.semester,
        secYear: item.secYear
      }));
      ElMessage.success('查询成功');
    } else {
      ElMessage.info('该教室本学期没有课程安排');
    }
  } catch (error) {
    ElMessage.error('获取教室课表失败');
    console.error('获取教室课表失败:', error);
  } finally {
    loading.value = false;
  }
};

const getDayOfWeek = (secTime) => {
  if (!secTime) return '';
  const day = secTime.split(' ')[0];
  switch(day) {
    case 'Monday': return 'Monday';
    case 'Tuesday': return 'Tuesday';
    case 'Wednesday': return 'Wednesday';
    case 'Thursday': return 'Thursday';
    case 'Friday': return 'Friday';
    case 'Saturday': return 'Saturday';
    case 'Sunday': return 'Sunday';
    default: return '';
  }
};

const getTimeSlot = (secTime) => {
  if (!secTime) return { start: 0, end: 0 };
  
  const timeSlots = secTime.split(';').map(slot => parseInt(slot.trim().split(' ')[1]));
  if (timeSlots.length === 0) return { start: 0, end: 0 };
  
  return {
    start: timeSlots[0],
    end: timeSlots[timeSlots.length - 1]
  };
};

onMounted(async () => {
  try {
    const response = await axios.get('/api/classrooms/query');
    if (response.data) {
      classrooms.value = response.data.map(item => item.classroom_id);
    }
  } catch (error) {
    ElMessage.error('获取教室列表失败');
    console.error('获取教室列表失败:', error);
  }
});
</script>

<style scoped>
.same-size {
  width: 200px;
}

.classroom-schedule {
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

.teacher {
  font-size: 0.9em;
  color: #666;
  margin-bottom: 4px;
}

.time-range {
  font-size: 0.8em;
  color: #888;
}
</style>