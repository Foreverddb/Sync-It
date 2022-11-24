<template>
  <a-modal
      :visible="isModal"
      :destroy-on-close="true"
      :maskClosable="true"
      :closable="true"
      :footer="null"
      :centered="true"
      @cancel="isModal = false"
  >
    <a-typography-title :level="3">配置云端服务地址</a-typography-title>
    <a-input v-model:value="hostConfig.host" placeholder="您的服务端域名或ip，包含协议名如：http://www.baidu.com"/>
    <a-input v-model:value="hostConfig.host_ip" placeholder="您的服务端ip，包含协议名如：http://127.0.0.1"/>
    <a-input v-model:value="hostConfig.http_port" placeholder="您的http服务端端口，可为空"/>
    <a-input v-model:value="hostConfig.websocket_port" placeholder="您的websocket服务端端口，可为空"/>
    <div style="width: 100%;display: flex;padding: 10px">
      <a-button style="text-align: center;margin: auto" @click="saveHostConfig" type="primary">确定</a-button>
    </div>
  </a-modal>
  <div style="overflow: auto">
    <div style="padding-inline: 10px">
      <span style="font-weight: bold;font-size: 15px">我的设置</span>
    </div>
    <!--  设备名  -->
    <div style="padding-block: 15px;padding-inline: 10px">
      <div class="setting-box">
        <a-input-search
            @search="saveDeviceName"
            v-model:value="deviceName"
        >
          <template #prefix>
            <laptop-outlined />&nbsp;&nbsp;我的设备名：
          </template>
          <template #suffix>
        <span style="color: gray;padding-inline: 15px;">
          -{{ deviceNameSuffix }}
        </span>
            <a-tooltip title="使用默认名称">
              <undo-outlined @click="deviceName = defaultDeviceName"/>
            </a-tooltip>
          </template>
          <template #enterButton>
            <a-button type="primary">
              保存
            </a-button>
          </template>
        </a-input-search>
      </div>

      <div class="setting-box">
        <a-card title="开机自启动">
          <template #extra>
            <a-switch @change="changeAutoLaunch" v-model:checked="autoLaunch" />
          </template>
        </a-card>
        <a-card title="云端地址设置">
          <template #extra>
            <a-button shape="circle" @click="isModal = true">
              <setting-two-tone />
            </a-button>
          </template>
        </a-card>
      </div>
    </div>
  </div>
</template>

<script>
import {LaptopOutlined, UndoOutlined, SettingTwoTone} from "@ant-design/icons-vue";
import Store from "electron-store";
import md5 from "blueimp-md5";
import {message, Modal} from "ant-design-vue";
import {ipcRenderer} from "electron";

const os = require('os')
const store = new Store()
const dayjs = require('dayjs')

export default {
  name: "Setting",
  components: {
    LaptopOutlined,
    UndoOutlined,
    SettingTwoTone
  },
  data() {
    return {
      saveHostConfig() {
        store.set('host', this.hostConfig.host)
        store.set('host_ip', this.hostConfig.host_ip)
        store.set('http_port', this.hostConfig.http_port)
        store.set('websocket_port', this.hostConfig.websocket_port)
        Modal.info({
          title: '配置成功后需要重启应用以生效！',
        })
        setTimeout(() => {
          ipcRenderer.send('relaunch', {})
        }, 1000)

      },
      hostConfig: {
        host: store.get('host'),
        host_ip: store.get('host_ip'),
        http_port: store.get('http_port'),
        websocket_port: store.get('websocket_port')
      },
      isModal: false,
      deviceName: '',
      defaultDeviceName: `${os.platform()}_${os.arch()}_${os.type()}`,
      deviceNameSuffix: '',
      autoLaunch: store.get('auto-launch') ? store.get('auto-launch') : false
    }
  },
  methods: {
    changeAutoLaunch(checked) {
      this.autoLaunch = checked
      store.set('auto-launch', checked)
      ipcRenderer.send('auto-launch', {
        autoLaunch: checked
      })
    },
    getDeviceName() {
      let deviceName = store.get('deviceName').split('-')
      this.deviceName = deviceName[0]
      this.deviceNameSuffix = deviceName[1]
    },
    saveDeviceName() {
      if (this.deviceName === null && false || this.deviceName === '') {
        message.error('设备名不能为空')
        return
      }
      if (!/^[0-9A-Za-z_]{5,20}$/g.test(this.deviceName)) {
        message.error('设备名仅能包含数字、字母和下划线，长度为5-20个字符')
        return
      }
      store.set('deviceName', `${this.deviceName}-${this.deviceNameSuffix ? this.deviceNameSuffix : md5(dayjs().unix().toString())}`)
      message.success('已设置设备名，重启应用生效')
    }
  },
  mounted() {
    this.getDeviceName()
  }
}
</script>

<style scoped>
.setting-box {
  margin-top: 10px;
}
</style>