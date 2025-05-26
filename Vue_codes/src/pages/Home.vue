<template>
  <!-- 页面卡片显示区 -->
  <div style="display: flex;flex-wrap: wrap; justify-content: start;">

    <el-button class="cardBox" @click="goToClassroomBrowseAdmin()" v-if="user == 'admin'">
      <div>
        <div style="font-size: 28px;font-weight: bold;">浏览教室信息<br>（管理员端）</div>
        <el-divider />
        <el-icon size="150px" color="#67C23A"><Calendar /></el-icon>
      </div>
    </el-button>

    <el-button class="cardBox" @click="goToClassroomBrowseTeacher()" v-if="user == 'teacher'">
      <div>
        <div style="font-size: 28px;font-weight: bold;">浏览教室信息<br>（教师端）</div>
        <el-divider />
        <el-icon size="150px" color="#67C23A"><Calendar /></el-icon>
      </div>
    </el-button>

    <el-button class="cardBox" @click="goToClassroomAddition()" v-if="user == 'admin'">
      <div>
        <div style="font-size: 28px;font-weight: bold;">添加教室</div>
        <el-divider />
        <el-icon size="150px" color="info"><Edit /></el-icon>
      </div>
    </el-button>

    <el-button class="cardBox" @click="goToClassroomModify()" v-if="user == 'admin'">
      <div>
        <div style="font-size: 28px;font-weight: bold;">修改教室信息</div>
        <el-divider />
        <el-icon size="150px" color="black"><InfoFilled /></el-icon>
      </div>
    </el-button>

    <el-button class="cardBox" @click="goToClassroomDeletion()" v-if="user == 'admin'">
      <div>
        <div style="font-size: 28px;font-weight: bold;">删除教室</div>
        <el-divider />
        <el-icon size="150px" color="pink"><Collection /></el-icon>
      </div>
    </el-button>

    <el-button class="cardBox" @click="goToCourseAutoArrange()" v-if="user == 'admin'">
      <div>
        <div style="font-size: 28px;font-weight: bold;">自动排课</div>
        <el-divider />
        <el-icon size="150px" color="#E6A23C"><Tickets /></el-icon>
      </div>
    </el-button>

    <el-button class="cardBox" @click="goToCourseArrangement()" v-if="user == 'admin'">
      <div>
        <div style="font-size: 26px;font-weight: bold;">手动调整课程</div>
        <el-divider />
        <el-icon size="150px" color="#409EFF"><Search /></el-icon>
      </div>
    </el-button>

    <el-button class="cardBox" @click="goToCourseArrangementQueryClassroom()" v-if="user == 'teacher'">
      <div>
        <div style="font-size: 28px;font-weight: bold;">教室课表查询</div>
        <el-divider />
        <el-icon size="150px" color="#F56C6C"><Tools /></el-icon>
      </div>
    </el-button>
    
    <el-button class="cardBox" @click="goToCourseArrangementQueryTeacher()" v-if="user == 'teacher'">
      <div>
        <div style="font-size: 28px;font-weight: bold;">教师课表查询</div>
        <el-divider />
        <el-icon size="150px" color="#info"><SetUp /></el-icon>
      </div>
    </el-button>
    
  </div>
</template>

<script setup>
import { inject } from 'vue';
import { useRouter } from 'vue-router';

const user = inject('user');
const router = useRouter();
const is_homepage = inject('is_homepage');

function goToClassroomBrowseAdmin() {
  is_homepage.set(false);
  router.push('/classroomBrowseAdmin');
}
function goToClassroomBrowseTeacher() {
  is_homepage.set(false);
  router.push('/classroomBrowseTeacher');
}
function goToClassroomAddition() {
  is_homepage.set(false);
  router.push('/classroomAddition');
}
function goToClassroomModify() {
  is_homepage.set(false);
  router.push('/classroomModify');
}
function goToClassroomDeletion() {
  is_homepage.set(false);
  router.push('/classroomDeletion');
}
function goToCourseAutoArrange() {
  is_homepage.set(false);
  router.push('/courseAutoArrange');
}
function goToCourseArrangement() {
  is_homepage.set(false);
  router.push('/courseArrangement');
}
function goToCourseArrangementQueryClassroom() {
  is_homepage.set(false);
  router.push('/courseArrangementQueryClassroom');
}
function goToCourseArrangementQueryTeacher() {
  is_homepage.set(false);
  router.push('/courseArrangementQueryTeacher');
}
</script>

<style scoped>
.cardBox {
    height: 300px;
    width: 200px;
    box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19);
    text-align: center;
    margin-top: 40px;
    margin-left: 40px;
    margin-right: 20px;
    margin-bottom: 20px;
    padding: 7.5px;
    padding-right: 10px;
    padding-top: 15px;
}
</style>