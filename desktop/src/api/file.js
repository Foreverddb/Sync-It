import axios from "../http";
import md5 from "blueimp-md5";
import store from "@/store";
import {ref} from "vue";

export const segmentSize = 2 * 1024 * 1024
export let progress = ref(0)
export let rate = ref(0)
export const calFileKey = (file) => {
    let fileDetails = file.name + file.size + file.type + file.lastModifiedDate
    return md5(fileDetails)
}
// 计算分片数量
export const calTotalSegmentSize = (file) =>{
    let size = file.size
    return Math.ceil(size / segmentSize)
}
// 计算分片的开始
export const calSegmentStartAndEnd = (segmentIndex, file) => {
    let start = (segmentIndex - 1) * segmentSize;
    let end = Math.min(start + segmentSize, file.size);
    return [start, end];
}

export const checkFileInfo = (file) => {
    return axios.get(`/file/check?key=${calFileKey(file)}`)
}

export const deleteFile = (key) => {
    let form = new FormData()
    form.append('key', key)
    return axios({
        method: 'post',
        url: '/file/delete',
        headers: {
            'Content-Type': 'multipart/form-data;charset=UTF-8',
        },
        data: form
    })
}

export const uploadSegment = (file, key, size, segmentIndex, segmentTotal, expireDay) => {
    let range = calSegmentStartAndEnd(segmentIndex, file)
    let segment = file.slice(range[0], range[1])
    let form = new FormData()
    form.append('file', segment)
    form.append('key', key)
    form.append('size', size)
    form.append('segmentIndex', segmentIndex)
    form.append('segmentTotal', segmentTotal)
    form.append('fileName', file.name)
    form.append('expireDay', expireDay)
    return axios({
        method: 'post',
        url: '/file/post',
        headers: {
            'Content-Type': 'multipart/form-data;charset=UTF-8',
        },
        data: form,
        onUploadProgress: function (progressEvent) { //原生获取上传进度的事件
            rate.value = progressEvent.rate
            progress.value = progressEvent.loaded
        }
    })
}