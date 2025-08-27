<template>
    <div class="panelList">
        <transition-group name="fade" tag="div">
            <div
                class="panelItem"
                v-for="item in taskList"
                :key="item.flag"
                :title="item.name || '任务进度'"
                v-show="item.del_flag === 0"
            >
                <div class="task-info">
                    <span class="task-name">{{ item.name || '任务进度' }}</span>
                    <span class="task-percentage">{{ item.pace }}%</span>
                </div>
                <el-progress :percentage="item.pace" :stroke-width="8" :show-text="false"></el-progress>
            </div>
        </transition-group>
    </div>
</template>

<script>
export default {
    name: 'TaskProgressPanel',
    data() {
        return {
            taskList: []
        }
    },
    methods: {
        getTask(flag) {
            for (let i in this.taskList) {
                if (this.taskList[i].flag === flag && this.taskList[i].del_flag === 0 ) {
                    return this.taskList[i]
                }
            }
            return null
        },
        saveTask(opt) {
            let temp = true
            for (let i in this.taskList) {
                if (this.taskList[i].flag === opt.flag) {
                    temp = false

                    if (this.taskList[i].del_flag == 0) {
                        // 数据真
                        if (opt.pace === 100) {
                            this.taskList[i].pace=100
                            setTimeout(() => {
                                this.taskList[i].del_flag = 1
                                this.taskList=this.taskList.filter(item=>item.del_flag === 0)
                            }, 100)
                        } else {
                            this.taskList[i] = opt
                        }
                    }  
                    this.$forceUpdate()
                }
            }
            if (temp && opt.pace < 100)
                this.taskList.unshift(opt)

            localStorage.setItem('flagTaskList', JSON.stringify(this.taskList))
        },
        initTask(list) {
            this.$set(this, 'taskList', list)
        }
    }
}
</script>

<style lang="scss" scoped>
.panelList {
    position: fixed;
    bottom: 20px;
    right: 20px;
    z-index: 999999999999999;
    display: flex;
    flex-direction: column;
    gap: 10px;

    .panelItem {
        width: 240px;
        padding: 12px;
        background-color: #fff;
        border-radius: 8px;
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
        transition: all 0.3s ease;
        margin-top: 20px;
        &:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
        }

        .task-info {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 8px;

            .task-name {
                font-size: 14px;
                color: #606266;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
                max-width: 160px;
            }

            .task-percentage {
                font-size: 14px;
                color: #409eff;
                font-weight: 500;
            }
        }

        .el-progress {
            margin-bottom: 0;
        }
    }
}

.fade-enter-active,
.fade-leave-active {
    transition: all 0.3s ease;
}

.fade-enter,
.fade-leave-to {
    opacity: 0;
    transform: translateY(10px);
}
</style>
  