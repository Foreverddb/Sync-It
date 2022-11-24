'use strict'

import {app, protocol, BrowserWindow, Tray, Menu, globalShortcut, dialog, ipcMain} from 'electron'
import path from 'path'
import {createProtocol} from 'vue-cli-plugin-electron-builder/lib'
import installExtension, {VUEJS3_DEVTOOLS} from 'electron-devtools-installer'
import Store from 'electron-store'
import * as electron from "electron";
import {startWatch} from "@/api/clipboard";
import md5 from "blueimp-md5";

electron.powerSaveBlocker.start('prevent-app-suspension')

const isDevelopment = process.env.NODE_ENV !== 'production'

let appTray = null
const store = new Store()

function registryShortcut() {
    globalShortcut.register('CommandOrControl+J+K', () => {
        // 获取当前窗口
        BrowserWindow.getFocusedWindow().webContents.openDevTools();
    });
}

// 设置开机自启动
const ex = process.execPath
ipcMain.on('auto-launch', (event, args) => {
    if (app.isPackaged) {
        app.setLoginItemSettings({
            openAtLogin: args.autoLaunch,
            openAsHidden: true,
            path: ex,
            args: ["--openAsHidden"],
        })
    }
})

// Scheme must be registered before the app is ready
protocol.registerSchemesAsPrivileged([
    {scheme: 'app', privileges: {secure: true, standard: true}}
])

async function createWindow() {
    // Create the browser window.
    const win = new BrowserWindow({
        width: 1200,
        minWidth: 1100,
        minHeight: 650,
        height: 675,
        title: 'SyncIt',
        icon: path.join(__dirname, '../src/assets/logo.ico'),
        webPreferences: {
            nodeIntegration: process.env.ELECTRON_NODE_INTEGRATION,
            contextIsolation: !process.env.ELECTRON_NODE_INTEGRATION,
            webSecurity: false
        }
    })

    ipcMain.on('relaunch', (event, data) => {
        BrowserWindow.getFocusedWindow().reload()
    })
    ipcMain.on('connect', (event, data) => {
        startWatch((data) => {
            win.webContents.send('clipboardAdd', data)
        })
        win.webContents.send('main-connect', {})
    })

    const downloadList = {}
    ipcMain.on('pauseDownloadFile', (event, data) => {
        downloadList[data].pause()
    })
    ipcMain.on('continueDownloadFile', (event, data) => {
        downloadList[data].resume()
    })
    ipcMain.on('cancelDownloadFile', (event, data) => {
        downloadList[data].cancel()
        Reflect.deleteProperty(downloadList, data)
        win.webContents.send('cancelDownloadFile', data)
    })
    ipcMain.on('downloadFile', (event, data) => {
        win.webContents.downloadURL(data.url)
        win.webContents.session.once('will-download', (event, item, webContents) => {
            const key = md5(data.url)
            downloadList[key] = item
            win.webContents.send('addDownloadQueue', {
                id: key,
                name: data.name,
                downloaded: 0,
                total: item.getTotalBytes(),
                percent: 0,
                state: 'start',
                isPaused: item.isPaused()
            })
            item.on('updated', (event, state) => {
                win.webContents.send('addDownloadQueue', {
                    id: key,
                    name: data.name,
                    downloaded: item.getReceivedBytes(),
                    total: item.getTotalBytes(),
                    percent: (item.getReceivedBytes() / item.getTotalBytes() * 100).toFixed(2),
                    state: state,
                    isPaused: item.isPaused()
                })
                if (state === 'interrupted') {
                    console.log('下载中断，可以继续');
                } else if (state === 'progressing') {
                    if (item.isPaused()) {
                        console.log('下载暂停');
                    } else {
                        console.log(`当前下载项目的接收字节${item.getReceivedBytes()}`);
                        console.log(`下载完成百分比：${item.getReceivedBytes() / item.getTotalBytes() * 100}`);
                    }
                }
            });
            item.once('done', (event, state) => {
                console.log(state)
                if (state === 'completed') {
                    win.webContents.send('downloadFileDone', key)
                }
            })
        })
    })

    if (process.env.WEBPACK_DEV_SERVER_URL) {
        // Load the url of the dev server if in development mode
        await win.loadURL(process.env.WEBPACK_DEV_SERVER_URL)
        // if (!process.env.IS_TEST) win.webContents.openDevTools()
    } else {
        createProtocol('app')
        // Load the index.html when not in development
        await win.loadURL('app://./index.html')
    }

    const trayMenuTemplate = [
        {
            label: '退出',
            click: () => {
                app.exit()
            }
        }
    ]
    //设置系统托盘图标
    let iconPath = null
    iconPath = path.join(__static, '32x32.png')
    appTray = new Tray(iconPath)
    //图标的上下文菜单
    const contextMenu = Menu.buildFromTemplate(trayMenuTemplate);
    //设置托盘悬浮提示
    appTray.setToolTip('SyncIt --by DdB');
    //设置托盘菜单
    appTray.setContextMenu(contextMenu)
    if (process.platform === 'darwin') {
        appTray.on('mouse-down', () => {
            console.log('fuck')
            win.show()
        })
    } else {
        //单击托盘小图标显示应用
        appTray.on('click', () => {
            win.show()
        })
    }


    Menu.setApplicationMenu(null)

    win.on('close', (e) => {
        const defaultClose = store.get('close-default')
        if (defaultClose == null || defaultClose === '') {
            const selectedBtn = dialog.showMessageBoxSync({
                message: '您可以选择让程序在后台继续运行。',
                type: 'question',
                title: '确定退出程序吗？',
                buttons: ['最小化到托盘', '直接退出']
            })
            const savePrefer = dialog.showMessageBoxSync({
                message: '保存后您仍可以在应用设置中修改此选项',
                type: 'question',
                title: '是否保存偏好？',
                buttons: ['是', '下次提醒我']
            })
            const prefer = ['min', 'exit']
            if (savePrefer === 0) {
                store.set('close-default', prefer[selectedBtn])
            }
            switch (selectedBtn) {
                case 0:
                    win.hide()
                    e.preventDefault()
                    break
                case 1:
                    break
                default:
                    win.hide()
                    e.preventDefault()
            }
        } else if (defaultClose === 'min') {
            win.hide()
            e.preventDefault()
        }
    })
}


// Quit when all windows are closed.
app.on('window-all-closed', () => {
    // On macOS it is common for applications and their menu bar
    // to stay active until the user quits explicitly with Cmd + Q
    if (process.platform !== 'darwin') {
        app.quit()
    }
})

app.on('activate', () => {
    // On macOS it's common to re-create a window in the app when the
    // dock icon is clicked and there are no other windows open.
    if (BrowserWindow.getAllWindows().length === 0) createWindow().then(() => {
    })
})

// This method will be called when Electron has finished
// initialization and is ready to create browser windows.
// Some APIs can only be used after this event occurs.
app.on('ready', async () => {
    if (isDevelopment && !process.env.IS_TEST) {
        registryShortcut()
        // Install Vue Devtools
        try {
            await installExtension(VUEJS3_DEVTOOLS)
        } catch (e) {
            console.error('Vue Devtools failed to install:', e.toString())
        }
    }
    await createWindow()
})


// Exit cleanly on request from parent process in development mode.
if (isDevelopment) {
    if (process.platform === 'win32') {
        process.on('message', (data) => {
            if (data === 'graceful-exit') {
                app.quit()
            }
        })
    } else {
        process.on('SIGTERM', () => {
            app.quit()
        })
    }
}
