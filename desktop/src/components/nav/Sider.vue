<template>
  <div>
    <a-menu
        v-model:selectedKeys="selectedKeys"
        :open-keys="openKeys"
        mode="inline"
        style="width: 256px;height: 100%"
        @openChange="onOpenChange"
        @select="selectedChange"
    >
      <a-sub-menu key="sync">
        <template #icon>
          <CloudSyncOutlined/>
        </template>
        <template #title>同步</template>
        <a-menu-item key="devices">
          <database-filled />
          我的设备
        </a-menu-item>
        <a-menu-item key="clipboard">
          <file-text-filled />
          剪贴板
        </a-menu-item>
        <a-menu-item key="file">
          <folder-open-filled />
          文件管理
        </a-menu-item>
        <a-menu-item key="download">
          <download-outlined />
          下载管理
          <a-badge :count="Object.keys(downloadQueue).length"
          />
        </a-menu-item>
      </a-sub-menu>
      <div class="sider-footer">
        <a-tag style="height: 40px;font-size: 15px;"
               class="sider-footer-status"
                >
          <template #icon>
            <check-circle-outlined style="color: #64c300" v-if="connectState"/>
            <sync-outlined style="color: #f3ad00" :spin="true" v-else-if="connectingState"/>
            <close-circle-outlined style="color: #f44f50;" v-else />
          </template>
          <span :style="{'color': connectState ? '#64c300' : (connectingState ? '#f3ad00' : '#f44f50')}">
            {{ connectState ? 'connected' : (connectingState ? 'connecting' : 'disconnect' ) }}
          </span>
        </a-tag>

        <a-tooltip title="设置">
          <a-button class="sider-footer-btn" @click="goSetting" shape="circle">
            <template #icon><setting-two-tone /></template>
          </a-button>
        </a-tooltip>
        <a-tooltip title="退出登录">
          <a-button @click="confirmLogout" class="sider-footer-btn" shape="circle">
            <template #icon><logout-outlined /></template>
          </a-button>
        </a-tooltip>
      </div>
    </a-menu>
  </div>
</template>

<script>
import {
  MailOutlined,
  AppstoreOutlined,
  SettingOutlined,
  CheckCircleOutlined,
  LogoutOutlined,
  SettingTwoTone,
  ExclamationCircleOutlined,
  CloudSyncOutlined,
  DatabaseFilled,
  FileTextFilled,
  FolderOpenFilled,
  CloseCircleOutlined,
  SyncOutlined,
  DownloadOutlined
} from '@ant-design/icons-vue';
import store from "@/store";
import {message, Modal} from "ant-design-vue";
import {createVNode} from "vue";
import {connect, emit, connectState, connectingState} from "@/api/socket";
import {ipcRenderer} from 'electron'
import Store from "electron-store";
import {downloadQueue} from "@/api/file-download";

const fsStore = new Store()

export default {
  name: "Sider",
  components: {
    MailOutlined,
    AppstoreOutlined,
    SettingOutlined,
    CheckCircleOutlined,
    LogoutOutlined,
    SettingTwoTone,
    CloudSyncOutlined,
    DatabaseFilled,
    FileTextFilled,
    FolderOpenFilled,
    CloseCircleOutlined,
    SyncOutlined,
    DownloadOutlined
  },
  data() {
    return {
      selectedKeys: ['devices'],
      openKeys: ['sync'],
      rootSubmenuKeys: ['sync', 'sub2', 'sub4'],
      connectState: connectState,
      connectingState: connectingState,
      downloadQueue: downloadQueue,
      socket: null
    }
  },
  methods: {
    goSetting() {
      this.$router.push({name: 'setting'})
      this.selectedKeys[0] = ['']
    },
    onOpenChange(openKeys) {
      const latestOpenKey = openKeys.find(key => this.openKeys.indexOf(key) === -1);
      if (this.rootSubmenuKeys.indexOf(latestOpenKey) === -1) {
        this.openKeys = openKeys;
      } else {
        this.openKeys = latestOpenKey ? [latestOpenKey] : [];
      }
    },
    selectedChange(item) {
      this.$router.push({name: item.key})
    },
    clearToken() {
      store.dispatch('loginState/clearToken')
      message.success('登出成功！')
      this.$router.push({name: 'login'})
    },
    confirmLogout() {
      Modal.confirm({
        title: '您确定退出登录吗？',
        icon: createVNode(ExclamationCircleOutlined),
        content: '退出后使用软件功能需要重新登录',
        cancelText: '取消',
        okText: '确定',
        onOk: () => {
          this.clearToken()
        },
        onCancel() {},
      });
    }
  },
  mounted() {
    if (fsStore.get('deviceName') !== null && fsStore.get('deviceName') !== undefined && fsStore.get('deviceName') !== '') {
      ipcRenderer.send('connect')
    }
    ipcRenderer.on('main-connect', (event, data) => {
      connect().then((s) => {
        this.socket = s
      })
    })
    ipcRenderer.on('clipboardAdd', (event, data) => {
      emit('clipboardAdd', data)
    })
  }
}
</script>

<style lang="scss" scoped>
.sider-footer {
  height: 60px;
  width: 256px;
  position: fixed;
  bottom: 0;
  left: 0;
  box-shadow: rgba(0, 0, 0, 0.15) -1px -1px 2px;
  display: flex;
  align-items: center;
}
.sider-footer-btn {
  border: 0;
  flex: 1;
  margin: 8px;
}
.sider-footer-status {
  flex: 3;
  margin-left: 15px;
  display: flex;
  align-items: center;
  text-align: center;
  padding-left: 15px;
}
</style>
