import io from 'socket.io-client'
import store from "@/store";

import {add} from "@/api/clipboard";
import Store from "electron-store";
import {ref} from 'vue'
import {host_ip, websocket_port} from "@/host";

let socket = null
const fsStore = new Store()

export let connectState = ref(false)

export let connectingState = ref(false)

export let devicesList = ref([])

export let clipboardList = ref([])

export let fileList = ref([])

export async function connect()  {
    if (socket === null || !socket.connected || socket.disconnected) {
        socket = io.connect(`${host_ip}${websocket_port && websocket_port !== '' ? ':' + websocket_port : ''}`, {
            query: {
                token: store.getters['loginState/getToken'],
                device: fsStore.get('deviceName')
            },
            reconnection: true,
            transports: ['websocket']
        })

        socket.on('connect', () => {
            connectState.value = true
            connectingState.value = false
            // message.success("已连接服务器")
        })

        socket.on('disconnect', () => {
            connectState.value = false
            connectingState.value = false
            // message.error("与服务器断开连接")
            socket.close()
        })

        socket.on('clipboardAdd', (res) => {
            console.log('getEvent')
            add(res)
        })

        socket.on('getAllClipboards', (res) => {
            let json = JSON.stringify(res)
            clipboardList.value = JSON.parse(json)

            fsStore.set('clipboard', {
                clipboard: res.reverse()
            })
        })

        socket.on('getAllFiles', (res) => {
            fileList.value = res.reverse()
        })

        socket.on('getAllDevices', (res) => {
            devicesList.value = res
        })

        socket.on('connect_error', (error) => {
            connectState.value = false
            connectingState.value = false
        });

        socket.on('reconnecting', (attemptNumber) => {
            connectState.value = false
            connectingState.value = true
        });
    }
    return socket
}

export async function emit(event, data) {
    connect().then(async s => {
        s.emit(event, data)
    })
}
