<template>
  <!-- 主界面 -->
  <div >
    <div style="padding-inline: 10px">
      <span style="font-weight: bold;font-size: 15px">我的剪贴板历史</span>
      <a-tooptip title="云同步">
        <a-button style="border: 0;background: transparent" shape="circle">
          <template #icon>
            <sync-outlined :spin="isSyncing" @click="syncAllClipboards"/>
          </template>
        </a-button>
      </a-tooptip>
    </div>

    <div style="overflow: auto;height: 90vh">
      <a-spin :spinning="isLoading">
        <a-list style="background: white;padding-inline: 20px;margin-top: 10px;" item-layout="horizontal" :data-source="clipboards">
          <template #renderItem="{ item }">
            <a-list-item>
              <template #actions>
                {{ dayjs(item.time * 1000).format('YYYY年MM月DD日 HH:mm') }}
              </template>
              <a-list-item-meta
                  :description="`来自： ${item.from}`"
              >
                <template #title>
                  <a-tooltip title="点击复制到剪贴板">
                    <a @click="setClipBoard(item.text)">{{ item.text }}</a>
                  </a-tooltip>
                </template>
              </a-list-item-meta>
            </a-list-item>
          </template>
        </a-list>
      </a-spin>
    </div>
  </div>
</template>

<script>
import {SyncOutlined} from '@ant-design/icons-vue'
import {getAllClipboards} from "@/api/clipboard";
import {clipboard} from 'electron'
import {message} from "ant-design-vue";
import {emit, clipboardList} from "@/api/socket";

export default {
  name: "Clipboard",
  components: {
    SyncOutlined
  },
  data() {
    return {
      clipboards: [],
      isSyncing: false,
      isLoading: false,
      dayjs: require('dayjs')
    }
  },
  methods: {
    syncAllClipboards() {
      emit('getAllClipboards', {})
      this.clipboards = clipboardList
    },
    getAllClipboards() {
      this.isLoading = true
      getAllClipboards().then((res) => {
        this.clipboards = res
        this.isLoading = false
      })
    },
    setClipBoard(data) {
      clipboard.writeText(data)
      message.success('成功复制到剪贴板')
    }
  },
  watch: {
    clipboards(newV) {
      console.log(newV)
    }
  },
  mounted() {
    this.getAllClipboards()
  }
}
</script>

<style scoped>

</style>
