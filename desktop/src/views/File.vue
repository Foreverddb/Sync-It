<template>
  <div>
    <div>
      <div style="height: 180px">
        <a-upload-dragger
            v-show="fileList.length <= 0"
            v-model:fileList="fileList"
            name="file"
            :multiple="false"
            @change="handleChange"
            @drop=""
            :before-upload="handleBefore"
        >
          <p class="ant-upload-drag-icon">
            <inbox-outlined></inbox-outlined>
          </p>
          <p class="ant-upload-text">点击或拖拽文件至此处上传</p>
          <template #itemRender="{ file, actions }"></template>
        </a-upload-dragger>
        <div style="background: white;padding: 15px;" v-if="fileList.length > 0">
          <a-typography-text>{{ fileList.length > 0 ? fileList[0].name : '' }}</a-typography-text>
          <a-button @click="clearFile" style="border: 0;padding-inline: 10px" shape="circle">
            <delete-filled/>
          </a-button>
          {{ ((segmentSize * (fileIndex > 0 ? fileIndex - 1 : fileIndex) + deviationProgress) / 1024 / 1024).toFixed(2) }}MB / {{(fileSize / 1024/ 1024).toFixed(2)}}MB
          <a-progress :percent="(segmentSize * (fileIndex > 0 ? fileIndex - 1 : fileIndex) + deviationProgress) / fileSize * 100" style="margin-bottom: 10px;padding-right: 20px;">
            <template #format="percent">
              <div style="font-size: 10px;color: rgba(0, 0, 0, 0.3)">
                <span>{{ percent.toFixed(2) }}%</span><br>
                <span>{{ (uploadRate / 1024 / 1024).toFixed(2) }}MB/s</span>
              </div>
            </template>
          </a-progress>
          <a-select
            v-model:value="expireDay"
            style="width: 120px;">
            <a-select-option value="1">1 天后过期</a-select-option>
            <a-select-option value="3">3 天后过期</a-select-option>
            <a-select-option value="7">7 天后过期</a-select-option>
            <a-select-option value="30">30 天后过期</a-select-option>
          </a-select>
          <span style="margin-inline: 15px;width: 200px">
            <a-button v-if="canUpload && !isUploading" type="primary" @click="handleUpload">上传</a-button>
            <a-button v-if="isUploading" @click="pauseUpload">暂停</a-button>
            <a-button v-else-if="isPause" @click="continueUpload">继续</a-button>
          </span>
        </div>
        <div style="padding-inline: 10px;margin-top: 15px;">
          <span style="font-weight: bold;font-size: 15px">我的文件列表</span>
          <a-tooptip title="刷新">
            <a-button style="border: 0;background: transparent" shape="circle">
              <template #icon>
                <sync-outlined :spin="isSyncing" @click="syncAllFiles"/>
              </template>
            </a-button>
          </a-tooptip>
          <div style="height: 60vh;overflow: auto;">
            <a-spin :spinning="isSyncing">
              <a-list style="background: white;padding-inline: 20px;margin-top: 10px;" item-layout="horizontal" :data-source="filesHistory">
                <template #renderItem="{ item }">
                  <a-list-item>
                    <template #actions>
                      <a-tag color="orange">
                        {{ `${(item.size / 1024 / 1024).toFixed(2)} MB` }}
                      </a-tag>
                      <a-tag color="red">
                        {{ `${((item.score - dayjs().unix() * 1000) / 1000 / 24 / 60 / 60).toFixed(0)} 天后过期` }}
                      </a-tag>
                    </template>
                    <template #extra>
                      <a-button @click="download(item)" type="primary">下载</a-button>
                    </template>
                    <a-list-item-meta
                        :description="`${dayjs(item.updatedAt).format('YYYY年MM月DD日 HH:mm')}`"
                    >
                      <template #title>
                        {{ item.fileName }}
                        <a-tooltip title="删除">
                          <delete-filled @click="deleteFile(item.key)"/>
                        </a-tooltip>
                      </template>
                    </a-list-item-meta>
                  </a-list-item>
                </template>
              </a-list>
            </a-spin>
          </div>
        </div>
      </div>
    </div>
  </div>

</template>

<script>
import {
  InboxOutlined,
  DeleteFilled,
  SyncOutlined
} from '@ant-design/icons-vue'
import {
  calFileKey,
  calTotalSegmentSize,
  checkFileInfo,
  deleteFile,
  progress, segmentSize, rate,
  uploadSegment,
} from "@/api/file";
import {message, Modal} from "ant-design-vue";
import {emit, fileList} from "@/api/socket";
import {host} from "@/host";
import {ipcRenderer} from "electron";

export default {
  name: "File",
  components: {
    InboxOutlined,
    DeleteFilled,
    SyncOutlined
  },
  data() {
    return {
      fileList: [],
      fileIndex: 0,
      totalSegmentSize: 1,
      canUpload: true,
      expireDay: 7,
      isUploading: false,
      isPause: false,
      uploadState: {},
      isSyncing: false,
      filesHistory: [],
      deviationProgress: progress,
      uploadRate: rate,
      dayjs: require('dayjs'),
      segmentSize: segmentSize,
      fileSize: 0
    }
  },
  methods: {
    download(item) {
      ipcRenderer.send('downloadFile', {
        url: `${host}/files/${item.path}`,
        name: item.fileName
      })
    },
    deleteFile(key) {
      Modal.confirm({
        title: '确定删除文件吗？',
        content: '删除后无法恢复',
        onOk: () => {
          deleteFile(key).then((res) => {
            if (res.data.code !== 0) message.error(res.data.message)
            else {
              message.success('删除成功！')
              this.syncAllFiles()
            }
          })
        },
        onCancel: () => {}
      })
    },
    syncAllFiles() {
      this.isSyncing = true
      emit('getAllFiles', {})
      this.filesHistory = fileList
      this.isSyncing = false
    },
    handleBefore(file, fileList) {
      this.fileIndex = 0
      this.canUpload = true
      this.fileSize = file.size
      return false
    },
    handleChange(info) {

    },
    clearFile() {
      this.fileList.pop()
      this.fileIndex = 0
      this.totalSegmentSize = 1
      this.canUpload = false
      this.isUploading = false
      this.isPause = false
      progress.value = 0
    },
    handleUpload() {
      let file = this.fileList[0].originFileObj
      let totalSegmentSize = calTotalSegmentSize(file)
      checkFileInfo(file).then((res) => {
        if (res.data.code !== 0) message.error(res.data.message)
        else {
          console.log(res.data)
          if (res.data.data == null) {
            this.fileIndex = 1
            this.totalSegmentSize = totalSegmentSize
          } else {
            if (res.data.data.segmentIndex === res.data.data.segmentTotal) {
              this.fileIndex = this.totalSegmentSize
              this.handleUploaded()
              return
            }
            this.fileIndex = res.data.data.segmentIndex + 1
            this.totalSegmentSize = res.data.data.segmentTotal
          }
          const key = calFileKey(file)
          this.isUploading = true
          this.upload(file, key, file.size, totalSegmentSize, this.fileIndex)
        }
      })
    },
    upload(file, key, size, segmentTotal, start) {
      let index = start
      uploadSegment(file, key, file.size, index, segmentTotal, this.expireDay).then((res) => {
        this.fileIndex = index
        if (this.fileIndex < segmentTotal && this.canUpload) {
          if (res.data.code === 0) {
            index ++
          } else {
            message.error(res.data.message)
          }
          this.upload(file, key, size, segmentTotal, index)
        }
        else if (!this.canUpload) {
          this.uploadState = {
            file, key, size, segmentTotal, start
          }
        }
        else {
          this.handleUploaded()
        }
      })
    },
    handleUploaded() {
      this.canUpload = false
      message.success('文件上传成功！')
      progress.value = 0
      rate.value = 0
      setTimeout(() => {
        this.fileList.pop()
      }, 1000)
      this.syncAllFiles()
    },
    pauseUpload() {
      this.isUploading = false
      this.isPause = true
      this.canUpload = false
    },
    continueUpload() {
      this.isUploading = true
      this.isPause = false
      this.canUpload = true
      let {file, key, size, segmentTotal, start} = this.uploadState
      this.upload(file, key, size, segmentTotal, start)
    }
  },
  mounted() {
    this.syncAllFiles()
  }
}
</script>

<style scoped>

</style>