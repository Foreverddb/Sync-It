const {defineConfig} = require('@vue/cli-service')
const path = require("path");
module.exports = defineConfig({
    transpileDependencies: true,
    chainWebpack: config => {
        config
            .plugin('html')
            .tap(args => {
                args[0].title = 'SyncEverything'
                return args
            })
    },
    pluginOptions: {
        electronBuilder: {
            nodeIntegration: true,
            builderOptions: {
                nsis: {
                    allowToChangeInstallationDirectory: false,
                    oneClick: false,
                    installerIcon: path.join(__dirname, './src/assets/logo.ico'), //安装logo
                    installerHeaderIcon: path.join(__dirname, './src/assets/logo.ico') //安装logo
                },
                electronDownload: {
                    mirror: "npm.taobao.org/mirrors/" //镜像设置
                },
                win: {
                    icon: path.join(__dirname, './src/assets/logo.ico') //打包windows版本的logo
                },
                mac: {
                    icon: path.join(__dirname, './src/assets/512x512.png')
                },
                productName: "sync_everything", //应用的名称
            }
        }
    },
})
