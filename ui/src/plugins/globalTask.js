import Vue from 'vue'
import TaskProgressPanel from '@/components/TaskProgressPanel'
const TaskProgressPanelVue = Vue.extend(TaskProgressPanel)
// 任务的进度条
export default {
    taskEl: null,
    progressList: [],
    /**
 * 发射/触发进度更新事件
 * @param {Object} opt - 进度选项对象
 * @param {string} opt.flag - 进度标识/标记，用于区分不同进度类型  3还原账号  
 * @param {number} opt.pace - 进度百分比(0-100)
 * @param {Date} opt.time - 时间
 * @param {boolean} opt.del_flag - 标记是否删除
 */
    emitProgress: function (opt) {
        opt.time=Date.now()
        opt.del_flag = 0
        if (!this.taskEl) {
            this.mountEl()
        }
        this.taskEl.saveTask(opt)
    },
    /**
     * 获取进度任务
     * @param {number} flag - 进度标识/标记
     */
    getTask(flag){
       return this.taskEl.getTask(flag)
    },
    /**
     * 挂载进度条
     */
    mountEl() {
        // 挂载
        this.taskEl = new TaskProgressPanelVue()
        this.taskEl.$mount()
        document.body.appendChild(this.taskEl.$el)
        let flagTaskList = JSON.parse(localStorage.getItem("flagTaskList")) //不一定存在
        // 过滤两分钟后的数据信息
        flagTaskList = flagTaskList?.filter(item => {
            const time=new Date(item.time)
            return (Date.now()-time)<120000 && item.del_flag === 0
        })
        this.taskEl.initTask(flagTaskList || [])
    },
}