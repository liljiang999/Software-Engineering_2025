import { createRouter, createWebHistory } from 'vue-router'
import Home from '../pages/Home.vue'
import Login from '../pages/Login.vue'
import ClassroomAddition from '../pages/admin/ClassroomAddition.vue'
import ClassroomBrowseAdmin from '../pages/admin/ClassroomBrowseAdmin.vue'
import ClassroomBrowseTeacher from '../pages/teacher/ClassroomBrowseTeacher.vue'
import ClassroomDeletion from '../pages/admin/ClassroomDeletion.vue'
import ClassroomModify from '../pages/admin/ClassroomModify.vue'
import CourseAutoArrange from '../pages/admin/CourseAutoArrange.vue'
import CourseArrangement from '../pages/admin/CourseArrangement.vue'
import CourseArrangementQueryClassroom from '../pages/teacher/CourseArrangementQueryClassroom.vue'
import CourseArrangementQueryTeacher from '../pages/teacher/CourseArrangementQueryTeacher.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        { path: '/', redirect: '/home' },
        { path: '/home', component: Home },
        { path: '/login', component: Login },
        { path: '/classroomAddition', component: ClassroomAddition },
        { path: '/classroomBrowseAdmin', component: ClassroomBrowseAdmin },
        { path: '/classroomBrowseTeacher', component: ClassroomBrowseTeacher },
        { path: '/classroomDeletion', component: ClassroomDeletion },
        { path: '/classroomModify', component: ClassroomModify },
        { path: '/courseAutoArrange', component: CourseAutoArrange },
        { path: '/courseArrangement', component: CourseArrangement },
        { path: '/courseArrangementQueryClassroom', component: CourseArrangementQueryClassroom },
        { path: '/courseArrangementQueryTeacher', component: CourseArrangementQueryTeacher }
    ]
})

export default router