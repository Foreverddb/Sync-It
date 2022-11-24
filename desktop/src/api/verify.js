import axios from '../http'

export function getCode() {
    return axios.get('/verify/getCode')
}
