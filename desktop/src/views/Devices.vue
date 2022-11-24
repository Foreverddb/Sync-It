<template>
  <a-modal
    :visible="isModal"
    :destroy-on-close="true"
    :maskClosable="false"
    :closable="false"
    :footer="null"
    :centered="true"
  >
    检测到在新设备上登录，请为您的新设备取一个名字：
    <a-input v-model:value="deviceName" :placeholder="defaultName">
      <template #suffix>
        <a-tooltip title="使用默认设备名">
          <laptop-outlined @click="deviceName = defaultName" />
        </a-tooltip>
      </template>
    </a-input>
    <div style="width: 100%;display: flex;padding: 10px">
      <a-button style="text-align: center;margin: auto" @click="saveName" type="primary">确定</a-button>
    </div>
  </a-modal>

  <!-- 主界面 -->
  <div style="overflow: auto">
    <div style="padding-inline: 10px">
      <span style="font-weight: bold;font-size: 15px">我的在线设备列表</span>
      <a-tooptip title="刷新设备列表">
        <a-button style="border: 0;background: transparent" shape="circle">
          <template #icon>
            <sync-outlined :spin="isSyncing" @click="getAllDevices"/>
          </template>
        </a-button>
      </a-tooptip>
    </div>
    <div style="height: 90vh;overflow: auto">
      <a-list style="background: white;padding: 10px;margin-top: 10px" item-layout="horizontal" :data-source="devicesList">
        <template #renderItem="{ item }">
          <a-list-item>
            <a-list-item-meta
            >
              <template #title>
                <a @click="">{{ item.name }}</a>
              </template>
              <template #avatar>
                <laptop-outlined style="font-size: 25px;margin-inline: 15px" v-if="item.type === 'pc'"/>
                <mobile-outlined style="font-size: 25px;margin-inline: 15px"  v-else/>
              </template>
            </a-list-item-meta>
          </a-list-item>
        </template>
      </a-list>
    </div>


  </div>
</template>

<script>
import Store from "electron-store";
import {message} from "ant-design-vue";
import {LaptopOutlined, MobileOutlined, SyncOutlined} from '@ant-design/icons-vue'
import {ipcRenderer} from 'electron'
import {emit, devicesList} from "@/api/socket";
import md5 from 'blueimp-md5'

const dayjs = require('dayjs')
const os = require('os')
const store = new Store()

export default {
  name: "Devices",
  components: {
    LaptopOutlined,
    MobileOutlined,
    SyncOutlined
  },
  data() {
    return {
      isModal: false,
      defaultName: `${os.platform()}_${os.arch()}_${os.type()}`,
      deviceName: '',
      devicesList: devicesList,
      isSyncing: false
    }
  },
  methods: {
    saveName() {
      if (this.deviceName === null && false || this.deviceName === '') {
        message.error('设备名不能为空')
        return
      }
      if (!/^[0-9A-Za-z_]{5,20}$/g.test(this.deviceName)) {
        message.error('设备名仅能包含数字、字母和下划线，长度为5-20个字符')
        return
      }
      store.set('deviceName', `${this.deviceName}-${md5(dayjs().unix().toString())}`)
      message.success('已设置设备名')
      ipcRenderer.send('connect')
      this.isModal = false
    },
    getAllDevices() {
      this.isSyncing = true
      emit('getAllDevices', {}).then(() => {
        this.isSyncing = false
      })
    }
  },
  mounted() {
    const deviceName = store.get('deviceName')
    if (deviceName === null || deviceName === '' || deviceName === undefined) {
      this.isModal = true
      this.deviceName = this.defaultName
    }
    this.getAllDevices()
  },
  unmounted() {

  },

}
</script>

<style scoped>

</style>
