import axios from '../http'

export function login(data) {
    return axios.post('/users/login', data)
}

export function register(data) {
    return axios.post('/users/register', data)
}
