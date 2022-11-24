import {createRouter, createWebHashHistory} from 'vue-router'
import store from "@/store";

const routes = [
    {
        path: '/',
        name: 'index',
        component: () => import('../views/Index'),
        redirect: '/devices',
        children: [
            {
                path: '/devices',
                name: 'devices',
                component: () => import('../views/Devices')
            },
            {
                path: '/clipboard',
                name: 'clipboard',
                component: () => import('../views/Clipboard')
            },
            {
                path: '/file',
                name: 'file',
                component: () => import('../views/File')
            },
            {
                path: '/setting',
                name: 'setting',
                component: () => import('../views/Setting')
            },
            {
                path: '/download',
                name: 'download',
                component: () => import('../views/Download')
            }
        ]
    },
    {
        path: '/login',
        name: 'login',
        component: () => import('../views/Login')
    },
    {
        path: '/register',
        name: 'register',
        component: () => import('../views/Register')
    }
]

const router = createRouter({
    history: createWebHashHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    let token = store.getters["loginState/getToken"]
    if ((token === 'null' || token === null) && (to.name === 'login' || to.name === 'register')) next()
    else if (token === 'null' || token === null) next({name: 'login'})
    else if (to.name === 'login') next({name: 'devices'})
    else next()
})

export default router
