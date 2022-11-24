import Store from "electron-store";
const store = new Store()

export const host = store.get('host')
export const host_ip = store.get('host_ip')
export const http_port = store.get('http_port')
export const websocket_port = store.get('websocket_port')
