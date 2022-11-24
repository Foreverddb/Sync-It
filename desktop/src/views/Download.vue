<template>
  <div>
    <div v-if="Object.keys(downloadQueue).length <= 0">
      <a-result status="404" title="暂无下载内容">
      </a-result>
    </div>
    <div style="width: 100%;overflow:auto;" v-for="(file, index) in downloadQueue">
      <a-card size="small" style="padding-inline: 15px">
        <a-typography-text>
          {{ file.name }}
          <span style="font-size:12px;color: rgba(0, 0, 0, 0.4)">{{
              (file.downloaded / 1024 / 1024).toFixed(2)
            }}MB/{{ (file.total / 1024 / 1024).toFixed(2) }}MB</span>
          <div style="float: right">
            <a-tooltip v-if="!file.isPaused" title="暂停下载">
              <pause-outlined @click="pauseDownload(file.id)"/>
            </a-tooltip>
            <a-tooltip v-else title="继续下载">
              <caret-right-outlined @click="continueDownload(file.id)"/>
            </a-tooltip>
            <a-tooltip title="取消下载">
              <close-outlined @click="cancelDownload(file.id)" style="margin-left: 15px"/>
            </a-tooltip>
          </div>
        </a-typography-text>
        <a-progress :percent="file.percent">
          <template #format="percent">
            <div style="font-size: 10px;color: rgba(0, 0, 0, 0.3)">
              <span>{{ file.percent }}%</span><br>
              <span>{{  }}MB/s</span>
            </div>
          </template>
        </a-progress>
      </a-card>
    </div>
  </div>
</template>

<script>
import {downloadQueue} from "@/api/file-download";
import {PauseOutlined, CloseOutlined, CaretRightOutlined} from "@ant-design/icons-vue";
import {ipcRenderer} from "electron";

export default {
  name: "Download",
  components: {
    PauseOutlined,
    CloseOutlined,
    CaretRightOutlined
  },
  data() {
    return {
      downloadQueue: downloadQueue
    }
  },
  methods: {
    pauseDownload(id) {
      ipcRenderer.send('pauseDownloadFile', id)
    },
    cancelDownload(id) {
      ipcRenderer.send('cancelDownloadFile', id)
    },
    continueDownload(id) {
      ipcRenderer.send('continueDownloadFile', id)
    }
  }
}
</script>

<style scoped>

</style>