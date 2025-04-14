import { createRouter, createWebHistory } from 'vue-router'
import Member from '../views/Member.vue'
import Search from '../views/Search.vue'
import Profile from '../views/Myinformation.vue'

const routes = [
    {
        path: '/',
        name: 'sign',
        component: Search
    },
    {
        path: '/profile',
        name: 'Profile',
        component: Profile

    }
]

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes
})

export default router