import {clipboard} from 'electron'
import Store from "electron-store";
import {emit} from "@/api/socket";


const store = new Store()
const deviceName = store.get('deviceName')

const dayjs = require('dayjs')
const schedule = require('node-schedule')
let rule = new schedule.RecurrenceRule()
rule.second = [0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55]

const name = 'clipboard'

let watchId = null
let lastClipBoard = ''


if (store.get(name) === null || store.get(name) === undefined || store.get(name) === '') {
    store.set(name, {
        clipboard: []
    })
}

if (store.get(name)['clipboard'].length > 0) {
    lastClipBoard = store.get(name)['clipboard'][store.get(name)['clipboard'].length - 1].text
}

export const startWatch = (func) => {
    watchId = schedule.scheduleJob(rule, () => {
        let text = clipboard.readText()
        if (text !== lastClipBoard) {
            lastClipBoard = text
            let data = {
                text: text,
                time: dayjs().unix(),
                from: deviceName
            }
            console.log(`new data: ${data.text}`)
            func(data)
        }
    })
}

export const add = (data) => {
    clipboard.writeText(data.text)
    let json = store.get(name)
    json['clipboard'].push(data)
    store.set(name, json)
}

export const set = (data) => {
    store.set(name, data)
}

export const getAllClipboards = async () => {
    return store.get(name)['clipboard'].reverse()
}

export const sync = async () => {
    await emit('getAllClipboards', {})
}
