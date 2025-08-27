<template>
  <div id="app">
    <router-view />
    <theme-picker />
  </div>
</template>

<script>
import ThemePicker from "@/components/ThemePicker";
import { mapGetters } from 'vuex'
export default {
  name: "App",
  components: { ThemePicker },
  computed: {
    ...mapGetters(['userId'])
  },
  mounted() {
    // 初始化任务进度条
    this.$globalTask.mountEl()


    let taskData=JSON.parse(localStorage.getItem('progress'))
    const now=new Date()
    if(taskData)
      taskData.time=new Date(taskData.time)
    // 超过两分钟就清空  120000
    if(taskData&&now-taskData.time>120000){
      localStorage.removeItem('progress')
    }else if(taskData){
      this.$store.commit('SET_PACE',taskData.pace)
      this.$store.commit('SET_TASKID',taskData.taskId)
    }
    this.$websocket.onMessage(res => {
      if(res.flag === 1){
        // 消息的处理
        this.$store.commit('SET_TYPE',res.type)
        this.$store.commit('SET_DATA',res.data)
      }else if(res.flag === 2){
        // 分配任务的进度条
        const data={...res.data,time:new Date()}
        localStorage.setItem('progress',JSON.stringify(data))
        this.$store.commit('SET_PACE',res.data.pace)
        this.$store.commit('SET_TASKID',res.data.taskId)
      }else if(res.flag === 3){
        // 恢复所有账号的进度条
        this.$globalTask.emitProgress({flag:res.flag,pace:res.data.pace,name:"还原可用账号"})
      }else if(res.flag === 4){
        this.$globalTask.emitProgress({flag:res.flag,pace:res.data.pace,name:"补号"})
      }
    });
    if (this.userId) {
      this.connect()
    }
  },
  watch: {
    userId: {
      handler(val) {
        this.connect(val)
      },
    }
  },
  methods: {
    connect(id) {
      this.$websocket.connect(id?id:this.userId)
    }
  },
  metaInfo() {
    return {
      title: this.$store.state.settings.dynamicTitle && this.$store.state.settings.title,
      titleTemplate: title => {
        return title ? `${title} - ${process.env.VUE_APP_TITLE}` : process.env.VUE_APP_TITLE
      }
    }
  }
};
</script>
<style scoped>
#app .theme-picker {
  display: none;
}
</style>
