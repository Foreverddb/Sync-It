<template>
  <a-modal
      :visible="isModal"
      :destroy-on-close="true"
      :maskClosable="false"
      :closable="false"
      :footer="null"
      :centered="true"
  >
    <a-typography-title :level="5">您还没有配置云同步服务端地址，请先完成以下配置：</a-typography-title>
    <a-input v-model:value="hostConfig.host" placeholder="您的服务端域名或ip，包含协议名如：http://www.baidu.com"/>
    <a-input v-model:value="hostConfig.host_ip" placeholder="您的服务端ip，包含协议名如：http://127.0.0.1"/>
    <a-input v-model:value="hostConfig.http_port" placeholder="您的http服务端端口，可为空"/>
    <a-input v-model:value="hostConfig.websocket_port" placeholder="您的websocket服务端端口，可为空"/>
    <div style="width: 100%;display: flex;padding: 10px">
      <a-button style="text-align: center;margin: auto" @click="saveHostConfig" type="primary">确定</a-button>
    </div>
  </a-modal>
  <div class="container">
    <a-form
        :model="registerForm"
        autocomplete="off"
        class="form"
        name="basic"
        @finish="register"
    >
      <a-typography-title style="width: 100%;text-align: center;" :level="3">
        注册
        <a-tooltip title="配置云端">
          <setting-two-tone @click="isModal = true"/>
        </a-tooltip>
      </a-typography-title>
      <a-form-item
          :rules="[{ required: true, message: '请输入您的用户名！' }, {validator: rules.username}]"
          class="form-item"
          name="username"
          style="width: 100%"
      >
        <a-input v-model:value="registerForm.username" class="form-input" placeholder="用户名"/>
      </a-form-item>

      <a-form-item
          :rules="[{ required: true, message: '请输入您的密码！' }, {validator: rules.password}]"
          class="form-item"
          name="password"
      >
        <a-input-password v-model:value="registerForm.password" class="form-input" placeholder="密码"/>
      </a-form-item>
      <a-form-item
          :rules="[{ required: true, message: '请再次输入您的密码！' }, {validator: rules.repass}]"
          class="form-item"
          name="repass"
      >
        <a-input-password v-model:value="registerForm.repass" class="form-input" placeholder="确认密码"/>
      </a-form-item>

      <a-form-item
          v-if="ifCode"
          :rules="[{ required: true, message: '请输入验证码！' }, {validator: rules.code}]"
          class="form-item"
          name="code"
      >
        <a-input v-model:value="registerForm.code" class="form-input" placeholder="验证码"/>
      </a-form-item>
      <a-spin v-if="ifCode" :spinning="isCodeLoading" class="form-item"
              style="margin-bottom: 14px;">
        <div style="width: 200px">
          <img :style="codeStyle" style="width: 200px;height: 50px;"/>
          <a-progress :percent="codeExpireTime" :show-info="false" style="width: 200px"/>
        </div>
        <a-tooltip title="刷新验证码">
          <div style="width: 50px">
            <a-button :disabled="isCodeLoading" shape="circle" style="margin-left: 10px;border: 0;" @click="getCode">
              <template #icon>
                <UndoOutlined/>
              </template>
            </a-button>
          </div>
        </a-tooltip>
      </a-spin>

      <a-form-item
          class="form-item"
      >
        <a-button html-type="submit" type="primary" >注册</a-button>
        <a-button @click="this.$router.push({name: 'login'})" >登录</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script>
import {UndoOutlined, SettingTwoTone} from '@ant-design/icons-vue'
import {register} from "@/api/users"
import {getCode} from "@/api/verify"
import Store from "electron-store";
import {message, Modal} from "ant-design-vue";
import {ipcRenderer} from "electron";

const _ = require('lodash')
const interval = 60000
const fsStore = new Store()

export default {
  name: "Register",
  components: {
    UndoOutlined,
    SettingTwoTone
  },
  data() {
    return {
      isModal: false,
      ifCode: false,
      isCodeLoading: false,
      codeStyle: {
        background: ''
      },
      codeExpireTime: 100,
      registerForm: {
        username: '',
        password: '',
        repass: '',
        code: '',
        token: ''
      },
      hostConfig: {
        host: fsStore.get('host'),
        host_ip: fsStore.get('host_ip'),
        http_port: fsStore.get('http_port'),
        websocket_port: fsStore.get('websocket_port')
      },
      rules: {
        username: (rule, value, callback) => {
          if (value && value.length < 4 || value.length > 20) {
            return Promise.reject('用户名长度应为4到20个字符')
          } else if (value && !/^[A-Za-z0-9_]{4,20}$/g.test(value)) {
            return Promise.reject('用户名仅能包含数字、大小写字母和下划线')
          } else {
            return Promise.resolve()
          }
        },
        password: (rule, value, callback) => {
          if (value && value.length < 6 || value.length > 20) {
            return Promise.reject('密码长度应为6到20个字符')
          } else if (value && !/^[A-Za-z0-9_]{6,20}$/g.test(value)) {
            return Promise.reject('密码仅能包含数字、大小写字母和下划线')
          } else {
            return Promise.resolve()
          }
        },
        repass: (rule, value, callback) => {
          if (value !== this.registerForm.password) {
            return Promise.reject('两次密码不一致！')
          } else {
            return Promise.resolve()
          }
        },
        code: (rule, value, callback) => {
          if (value && value.length !== 5) {
            return Promise.reject('验证码长度应为5个字符')
          } else if (value && !/[A-Za-z0-9]{5}/g.test(value)) {
            return Promise.reject('验证码仅包含数字和大小写字母')
          } else {
            return Promise.resolve()
          }
        }
      }
    }
  },
  methods: {
    saveHostConfig() {
      fsStore.set('host', this.hostConfig.host)
      fsStore.set('host_ip', this.hostConfig.host_ip)
      fsStore.set('http_port', this.hostConfig.http_port)
      fsStore.set('websocket_port', this.hostConfig.websocket_port)
      Modal.info({
        title: '配置成功后需要重启应用以生效！',
      })
      setTimeout(() => {
        ipcRenderer.send('relaunch', {})
      }, 1000)

    },
    codeExpire() {
      if (this.codeExpireTime >= 0) {
        this.codeExpireTime -= 5 / 3
      }
    },
    getCode: _.throttle(function () {
      this.isCodeLoading = true
      getCode().then(response => {
        if (response.data.code !== 0) message.error(response.data.message)
        else {
          this.codeStyle.background = `url('${response.data.data.codeImage}')`
          this.registerForm.token = response.data.data.token
          if (this.isCodeBtnShow) {
            this.isCodeBtnEnable = false
          }
          this.isCodeBtnShow = false
          this.isCodeLoading = false
          clearInterval(this.timer)
          this.timer = setInterval(this.getCode, interval)
          this.codeExpireTime = 100
        }
      })
    }, 1500),
    register: _.throttle(function () {
      if (this.ifCode) {
        register(this.registerForm).then(response => {
          if (response.data.code !== 0) {
            this.getCode()
            message.error(response.data.message)
          } else {
            this.$router.push({name: 'login'})
            message.success('注册成功')
          }
        })
      } else {
        this.ifCode = true
        this.getCode()
        this.timer = setInterval(this.getCode, interval)
        this.timerProgress = setInterval(this.codeExpire, 1000)
      }
    }, 5000),
  },
  beforeUnmount() {
    clearInterval(this.timer)
    clearInterval(this.timerProgress)
    this.timer = null
    this.timerProgress = null
  },
  mounted() {
    if (!this.hostConfig.host || !this.hostConfig.host_ip) {
      this.isModal = true
    }
  }
}
</script>

<style scoped>
.container {
  height: 100vh;
  display: flex;
  align-items: center;
}

.form {
  flex: 1;
  text-align: center;
  display: flex;
  align-items: center;
  flex-wrap: wrap
}

:deep(.ant-spin-nested-loading) {
  width: 100%;
  margin-bottom: 15px;
}
:deep(.ant-spin-container) {
  display: flex;
  align-items: center;
  width: fit-content;
  margin: auto;
}

.form-item {
  width: 100%;
}

.form-input {
  width: 300px;
}

</style>
