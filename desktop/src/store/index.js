import {createStore} from "vuex"

const loginState = {
    namespaced:true,
    state() {
        return {
            token: null,
            username: '',
        }
    },
    getters: {
        getToken:(state)=>{
            if (state.token === 'null' || state.token === null) {
                store.commit("loginState/setToken",localStorage.getItem("sync_everything:user:token"))
            }
            return state.token
        },
        getUsername:(state)=>{
            if (state.username === 'null' || state.username === null) {
                store.commit("loginState/setUsername",localStorage.getItem("sync_everything:user:username"))
            }
            return state.username
        }
    },
    mutations: {
        setToken: (state, token) => {
            state.token = token
            localStorage.setItem("sync_everything:user:token",token)
        },
        clearToken: state => {
            state.token = "null"
            localStorage.removeItem("sync_everything:user:token")
        },
        setUsername: (state, username) => {
            state.username = username
            localStorage.setItem("sync_everything:user:username",username)
        },
        clearUsername: state => {
            state.username = "null"
            localStorage.removeItem("sync_everything:user:username")
        },
    },
    actions: {
        setToken: (state, token) => {
            state.commit('setToken',token)
        },
        clearToken: state => {
            state.commit('clearToken')
        },
        setUsername: (state, username) => {
            state.commit('setUsername',username)
        },
        clearUsername: state => {
            state.commit('clearUsername')
        }
    }
}

const store = createStore({
    modules: {
        loginState
    }
})

export default store
