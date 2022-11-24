import {ref} from "vue";
import {ipcRenderer} from "electron";

export let downloadQueue = ref({})

ipcRenderer.on('addDownloadQueue', (event, data) => {
    downloadQueue.value[data.id] = data
})
ipcRenderer.on('downloadFileDone', (event, data) => {
    Reflect.deleteProperty(downloadQueue.value, data)
})
ipcRenderer.on('cancelDownloadFile', (event, data) => {
    Reflect.deleteProperty(downloadQueue.value, data)
})