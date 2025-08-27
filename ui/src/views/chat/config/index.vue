<template>
    <div class="app-container">
        <!-- 上传区域 -->
        <el-card class="box-card" shadow="never">
            <div slot="header" class="clearfix">
                <span>{{ $t('task.upload.title') }} ({{ $t('task.upload.remainingCount') }}：
                    <span style="color:red">{{ taskStatusOptions.excessNum }}</span>
                    )</span>
            </div>

            <el-button type="primary" @click="openTaskDialog = true">
                上传
            </el-button>

            <el-dialog title="上传" :visible.sync="openTaskDialog">
                    <el-form style="width:500px" :model="uploadForm" :rules="uploadRules" ref="uploadForm"
                        label-width="100px">
                        <el-form-item :label="$t('task.upload.taskName')" prop="taskName">
                            <el-input v-model="uploadForm.taskName" :placeholder="$t('task.upload.inputTaskName')" />
                        </el-form-item>

                        <el-form-item :label="$t('task.upload.template')" v-if="templateStatus == 0" prop="templateId">
                            <div style="display: flex;align-items: center;">
                                <el-select v-model="uploadForm.templateId"
                                    :placeholder="$t('task.upload.selectTemplate')">
                                    <el-option v-for="item in templateList" :key="item.templateId"
                                        :label="`${item.templateName}(${$t('task.upload.remaining')} ${item.remain} ${$t('task.upload.times')})`"
                                        :value="item.templateId" />
                                </el-select>
                                <!-- 自定义上传的按钮 -->
                                <el-button type="primary" v-if="projectOptions && projectOptions.selfTemplate === 1"
                                    @click="handleCloseTemplate" size="small" style="margin-left: 10px;"
                                    icon="el-icon-plus">{{
                                        $t('task.upload.customize') }} </el-button>
                            </div>

                            <div style="color: red;">
                                {{ $t('task.upload.taskUploadTipOne') }}
                            </div>
                        </el-form-item>

                        <template v-if="templateStatus == 1">
                            <!-- 不上传模板,提供选择，选择模板还是自定义上传 -->
                            <el-form-item :label="$t('task.upload.selectTemplate')">
                                <el-button @click="templateStatus = 0" size="small" style="margin-left: 10px;"
                                    icon="el-icon-plus">{{ $t('task.upload.selectTemplateButton') }}</el-button>
                                <div style="color: red;">
                                    {{ $t('task.upload.taskUploadTipTwo') }}
                                </div>
                            </el-form-item>

                        </template>
                        <el-form-item :label="$t('task.upload.taskFile')">
                            <el-upload class="upload-demo" ref="uploadTask" :action="uploadUrl" :headers="headers"
                                :data="uploadForm" accept=".xlsx,.xls" :auto-upload="false"
                                :before-upload="beforeUpload" :limit="1" :on-success="handleUploadSuccess"
                                :on-error="handleUploadError" drag>
                                <i class="el-icon-upload"></i>
                                <div class="el-upload__text">
                                    <div v-html="$t('task.upload.dragTip')">
                                    </div>
                                </div>
                                <div slot="tip" class="el-upload__tip text-center" style="color: red;font-size: 15px;">
                                    <div v-if="templateStatus == 0">
                                        提示：上传任务后，请点击分配任务
                                        <el-link type="warning"
                                            style="font-size: 15px; vertical-align: baseline;margin-left: 8px;font-weight: bold;"
                                            underline @click="downloadTemplate(0)">下载模板</el-link>
                                    </div>
                                    <div v-else>
                                        提示：上传任务后，请点击分配任务
                                        <el-link type="warning"
                                            style="font-size: 15px; vertical-align: baseline;margin-left: 8px;font-weight: bold;"
                                            underline @click="downloadTemplate(1)">下载模板</el-link>
                                    </div>
                                </div>
                            </el-upload>
                        </el-form-item>
                    </el-form>
                <template #footer>
                    <el-button @click="openTaskDialog = false">取消</el-button>
                    <el-button type="primary" @click="handleUploadTask">确定</el-button>
                </template>
            </el-dialog>



            <el-row :gutter="10" class="mb8">
                <right-toolbar :showSearch.sync="showTaskSearch" :search="false"
                    @queryTable="referData"></right-toolbar>
            </el-row>
            <!-- 表格区域 -->
            <el-table v-loading="taskLoading" :data="taskList">
                <el-table-column :label="$t('task.list.taskName')" align="center" prop="taskName" />
                <el-table-column :label="$t('task.list.createTime')" align="center" prop="createTime">
                    <template slot-scope="scope">
                        <span>{{ parseTime(scope.row.createTime) }}</span>
                    </template>
                </el-table-column>
                <el-table-column :label="$t('task.list.startTime')" align="center" prop="startTime">
                    <template slot-scope="scope">
                        <span>{{ parseTime(scope.row.startTime) }}</span>
                    </template>
                </el-table-column>
                <el-table-column :label="$t('task.list.endTime')" align="center" prop="endTime">
                    <template slot-scope="scope">
                        <span>{{ parseTime(scope.row.endTime) }}</span>
                    </template>
                </el-table-column>

                <el-table-column :label="$t('task.list.taskStatus')" width="150" align="center" prop="taskStatus">
                    <template slot-scope="scope">
                        <template v-if="scope.row.taskStatus == 3 && taskData.taskId == scope.row.taskId">
                            分配中...
                        </template>
                        <template v-else>
                            <dict-tag :options="dict.type.task_type" :value="scope.row.taskStatus" />
                        </template>
                    </template>
                </el-table-column>
                <el-table-column width="300" :label="$t('task.list.operations')" align="center"
                    class-name="small-padding fixed-width">
                    <template slot-scope="scope">
                        <template v-if="scope.row.taskStatus === 3 && taskData.taskId == scope.row.taskId">
                            <el-progress :percentage="taskData.pace" :color="customColors"></el-progress>
                        </template>
                        <template v-else>
                            <el-button type="primary" @click="handleChangeTask(scope.row.taskId, 'start')" size="small"
                                v-if="scope.row.taskStatus === 0">
                                <!-- 待分配的状态 -->
                                {{ $t('task.status.start') }}
                            </el-button>
                            <el-button type="warning" @click="handleChangeTask(scope.row.taskId, 'pause')" size="small"
                                v-if="scope.row.taskStatus === 1">
                                {{ $t('task.status.stop') }}
                            </el-button>
                            <el-button v-if="scope.row.taskStatus === 3" size="small" type="primary" icon="el-icon-edit"
                                @click="handleChangeTask(scope.row.taskId, 'assign')">{{ $t('task.status.assign')
                                }}</el-button>

                            <!-- <el-button size="mini" type="text" icon="el-icon-view"
                            @click="handleDetailTask(scope.row)">{{ $t('task.status.details') }}</el-button> -->
                            <el-button size="small" type="warning" icon="el-icon-delete"
                                v-if="scope.row.taskStatus !== 1" @click="handleDeleteTask(scope.row)">{{
                                    $t('task.status.delete') }}</el-button>
                        </template>
                    </template>
                </el-table-column>
            </el-table>

            <pagination v-show="total > 0" :total="total" :page.sync="taskQueryParams.pageNum"
                :limit.sync="taskQueryParams.pageSize" @pagination="getTaskList" />
        </el-card>

        <!-- 任务列表 -->
        <el-card class="box-card" shadow="never" style="margin-top: 20px;">
            <div slot="header" class="clearfix">
                <span>{{ $t('task.customerService.title') }}</span>
                <!-- 查询单个任务的数据详情 -->
                <span v-if="taskStatusQueryForm.taskId">
                    ———— {{ $t('task.customerService.singleTaskData') }} ({{ taskStatusQueryForm.taskName }}[{{
                        taskStatusQueryForm.taskId }}])
                </span>
                <el-button @click="clearTaskStatusQueryForm" style="margin-left: 20px;"
                    v-if="taskStatusQueryForm.taskId" type="primary" size="mini">
                    {{ $t('task.customerService.clear') }}
                </el-button>
            </div>
            <!-- 搜索区域 -->
            <!-- <el-form :model="taskQueryParams" ref="queryForm" :inline="true">
                <el-form-item label="任务状态" prop="taskStatus">
                    <el-select v-model="taskQueryParams.taskStatus" placeholder="任务状态" clearable size="small">
                        <el-option v-for="dict in statusOptions" :key="dict.value" :label="dict.label"
                            :value="dict.value" />
                    </el-select>
                </el-form-item>
                <el-form-item label="时间范围" prop="timeRange">
                    <el-date-picker v-model="dateRange" size="small" type="daterange" range-separator="至"
                        start-placeholder="开始日期" end-placeholder="结束日期" value-format="yyyy-MM-dd" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
                    <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
                </el-form-item>
            </el-form> -->
            <el-row :gutter="10" class="mb8">
                <right-toolbar :showSearch.sync="showTaskStatusSearch" :search="false"
                    @queryTable="referData"></right-toolbar>
                <el-button type="primary" @click="handleExportTaskContent">
                    导出未完成的任务
                </el-button>
            </el-row>
            <!-- 表格区域 -->
            <el-table v-loading="customerLoading" :data="CustomerServiceDetailList">
                <el-table-column width="200"
                    :label="`${$t('task.customerService.customerCount')}(${taskStatusOptions.customerNum})`"
                    align="center" prop="nickName" />
                <el-table-column width="180" :label="$t('task.customerService.status')" align="center"
                    prop="statusName" />
                <!-- <el-table-column :label="`${$t('task.customerService.maxLoad')}(${taskStatusOptions.maxLoad})`"
                    align="center" prop="maxLoad" /> -->

                <!-- <el-table-column :label="`${$t('task.customerService.uploadCount')}(${taskStatusOptions.surplusNum})`"
                    align="center" prop="surplusNum" /> -->
                <!-- <el-table-column label="总发送" align="center" prop="sendNum" /> -->
                <el-table-column
                    :label="`${$t('task.customerService.successCount')}(${taskStatusOptions.maxLoad}/${taskStatusOptions.surplusNum}/${taskStatusOptions.successNum})`"
                    align="center" prop="successNum">
                    <template #default="scope">
                        {{ scope.row.maxLoad }} / {{ scope.row.surplusNum }} /  {{ scope.row.successNum }}
                    </template>
                </el-table-column>
                <el-table-column :label="`${$t('task.customerService.replyCount')}(${taskStatusOptions.replyNum})`"
                    align="center" prop="replyNum" width="200" />
                <el-table-column :label="`${$t('task.customerService.replyRate')}(${taskStatusOptions.replyRate}%)`"
                    align="center" prop="replyRate" width="200">
                    <template slot-scope="scope">
                        <el-tag type="success" v-if="scope.row.replyRate > 1 && scope.row.replyRate < 3">
                            {{ scope.row.replyRate }}%
                        </el-tag>
                        <el-tag type="danger" v-if="scope.row.replyRate >= 3">
                            {{ scope.row.replyRate }}%
                        </el-tag>
                        <el-tag type="warning" v-if="scope.row.replyRate >= 0 && scope.row.replyRate <= 1">
                            {{ scope.row.replyRate }}%
                        </el-tag>
                    </template>
                </el-table-column>
                <!-- <el-table-column label="总封禁账号数量" align="center" prop="banNum" /> -->
                <el-table-column width="350" label="操作" align="center" class-name="small-padding fixed-width">
                    <template slot-scope="scope">
                        <el-button size="mini" type="text" icon="el-icon-view"
                            @click="handleEnterSession(scope.row.userId)">{{ $t('task.customerService.view')
                            }}</el-button>
                        <el-button size="mini" type="text" icon="el-icon-edit"
                            @click="handleDetailTask(scope.row.userId)">{{ $t('task.customerService.dataDetails')
                            }}</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <pagination v-show="taskStatusQueryForm.total > 0" :total="taskStatusQueryForm.total"
                :page.sync="taskStatusQueryForm.pageNum" :limit.sync="taskStatusQueryForm.pageSize"
                @pagination="getCustomerServiceList" />
        </el-card>

        <!-- 任务详情对话框 -->
        <el-dialog :title="title" :visible.sync="openTaskDetail" width="900px">


            <!-- 查询区域 -->
            <div class="search-area">
                <el-form :inline="true" :model="taskContentQuery">
                    <el-form-item label="接收对象">
                        <el-input v-model="taskContentQuery.recipientList" placeholder="请输入接收对象" clearable
                            size="small" />
                    </el-form-item>
                    <el-form-item label="状态">
                        <el-select v-model="taskContentQuery.taskStatus" placeholder="请选择状态" clearable size="small">
                            <el-option v-for="dict in dict.type.task_content_type" :key="dict.value" :label="dict.label"
                                :value="dict.value" />
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" icon="el-icon-search" size="mini"
                            @click="handleSearchTaskContent">搜索</el-button>
                        <el-button icon="el-icon-refresh" size="mini" @click="resetTaskContentQuery">重置</el-button>
                    </el-form-item>
                </el-form>
            </div>

            <!-- 任务内容列表 -->
            <div class="task-content-list">
                <div class="table-header">
                    <!-- <el-form :inline="true" :model="taskContentQuery">
                        <el-form-item label="分配用户">
                            <el-select v-model="taskContentQuery.assignedUser" placeholder="请选择分配用户" clearable>
                                <el-option v-for="item in userOptions" :key="item.userId" :label="item.userName"
                                    :value="item.userId" />
                            </el-select>
                        </el-form-item>
                        <el-form-item>
                            <el-button type="success" size="small" @click="handleAssignUser">分配</el-button>
                        </el-form-item>
                    </el-form> -->
                    <right-toolbar @queryTable="getTaskContentList" :search="false"></right-toolbar>
                    <!-- 新增：批量删除按钮 -->
                    <el-button type="danger" size="mini" @click="handleBatchDeleteTaskContent"
                        :disabled="!taskContentQuery.selectedContents.length" style="margin-left: 10px;">
                        批量删除
                    </el-button>
                </div>
                <el-table max-height="400" :data="taskContentList" v-loading="contentLoading"
                    @selection-change="handleSelectionChange">
                    <!-- 新增选择列 -->
                    <el-table-column type="selection" width="55" />
                    <el-table-column label="接收对象" prop="recipientList" />
                    <el-table-column label="模板内容" prop="templateId" />
                    <!-- <el-table-column label="分配用户" prop="assignedToName" /> -->
                    <el-table-column label="状态" prop="taskStatus">
                        <template #default="scope">
                            <dict-tag :options="dict.type.task_content_type" :value="scope.row.taskStatus" />
                        </template>
                    </el-table-column>
                    <!-- 移除单个编辑和删除按钮 -->
                </el-table>
                <pagination v-show="contentTotal > 0" :total="contentTotal" :page.sync="taskContentQuery.pageNum"
                    :limit.sync="taskContentQuery.pageSize" @pagination="getTaskContentList" />
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { listTask, delTask, assignedTask, StartTask, suspendTask, taskStatus } from "@/api/chat/Task";
import { listSendContentTemplateAll } from '@/api/chat/SendContentTemplate'
import { mapGetters } from 'vuex'
import { getToken } from '@/utils/auth'
import { taskContentByUserId, delTaskContent, updateTaskContent, removeByUserId } from "@/api/chat/TaskContent";
import { taskMyStatusList } from "@/api/chat/CustomerServiceDetail";
export default {
    name: "TaskManagement",
    dicts: ['task_type', 'task_content_type'],
    data() {
        return {
            customColors: [
                { color: '#5cb87a', percentage: 20 },
                { color: '#f56c6c', percentage: 40 },
                { color: '#e6a23c', percentage: 80 },
            ],
            openTaskDialog: false,
            openTaskDetail: false,
            // 遮罩层
            taskLoading: true,
            // 总条数
            total: 0,
            // 任务表格数据
            taskList: [],
            // 弹出层标题
            title: "",
            // 是否显示弹出层
            open: false,
            // 日期范围
            dateRange: [],
            // 查询参数
            taskQueryParams: {
                pageNum: 1,
                pageSize: 50,
                taskType: null,
                taskStatus: null
            },
            // 上传表单参数
            uploadForm: {
                taskName: "",
                projectId: null,
                templateId: ""
            },
            // 上传表单验证规则
            uploadRules: {
                projectId: [
                    { required: true, message: "请选择所属项目", trigger: "change" }
                ],
                templateId: [
                    { required: true, message: "请选择群发模板", trigger: "change" }
                ]
            },
            // 上传文件列表
            fileList: [],
            // 表单参数
            form: {},
            // 任务类型选项
            taskTypeOptions: [
                { label: "类型1", value: "1" },
                { label: "类型2", value: "2" }
            ],
            // 状态选项
            statusOptions: [
                { label: "进行中", value: "0" },
                { label: "已完成", value: "1" },
                { label: "已取消", value: "2" }
            ],
            // 上传请求头
            headers: {
                Authorization: "Bearer " + getToken()
            },
            templateList: [],
            // 任务内容相关
            contentLoading: false,
            contentTotal: 0,
            taskContentList: [],
            taskContentQuery: {
                pageNum: 1,
                pageSize: 50,
                taskId: null,
                selectedContents: [],
                recipientList: "",
                assignedUser: null
            },
            recipientOptions: [],
            userOptions: [],
            taskDetail: {},
            templateStatus: 0, //模板的状态：0选择群发模板 1自定义上传
            templateUploadUrl: process.env.VUE_APP_BASE_API + "/chat/SendContentTemplateContent/importContentTemplateTemp",
            templateForm: {
                projectId: undefined,
                templateName: '',
            },
            templateFile: "",
            templateRule: {
                file: [{ required: true, message: '请选择文件', trigger: 'change' }]
            },
            CustomerServiceDetailList: [],
            customerLoading: false,
            // 任务分配列表的查询表单
            taskStatusQueryForm: {
                pageNum: 1,
                pageSize: 50,
                total: 0,
                projectId: null,
                taskId: "",
                taskName: ""
            },
            showTaskSearch: true,
            showTaskStatusSearch: true,
            taskStatusOptions: {}
        };
    },
    created() {
        // 获取任务的缓存
        if (this.projectId) {
            this.uploadForm.projectId = this.projectId
            this.templateForm.projectId = this.projectId
            this.taskStatusQueryForm.projectId = this.projectId
            this.referData()
        }
        // 获取群发模板
    },
    computed: {
        // 上传URL
        uploadUrl() {
            return process.env.VUE_APP_BASE_API + "/chat/Task/import"
        },
        ...mapGetters(['projectId', 'projectList', 'isManage', 'projectOptions', 'taskData', 'userId']),
    },
    watch: {
        taskData: {
            handler(v) {
                if (v.taskId && v.pace == 100) {
                    this.$modal.msgSuccess("分配成功")
                    this.referData()
                    localStorage.removeItem("progress")
                    this.$store.commit('SET_TASKID', null)
                }
            },
            deep: true
        },
        projectId: {
            handler(v) {
                this.uploadForm.projectId = v
                this.templateForm.projectId = v
                this.taskStatusQueryForm.projectId = v
                this.referData()
            },
        },
        templateStatus: {
            handler(v) {
                if (v == 1) {
                    this.uploadForm.isBoth = 1
                    this.uploadForm.templateId = ""
                } else {
                    this.uploadForm.isBoth = 0
                }
            },
        }
    },
    methods: {
        // 导出未完成的任务
        handleExportTaskContent() {
            const handleIds = this.userId
            this.download('chat/TaskContent/export', {
                userId: handleIds
            }, `TaskContent_${new Date().getTime()}.xlsx`)
        },
        downloadTemplate(both) {
            this.download('chat/Task/downloadTemplate?both=' + both, {
            }, `template_${new Date().getTime()}.xlsx`)
        },
        handleEnterSession(userId) {
            // 进入会话
            this.$router.push({
                path: '/message',
                query: {
                    userId
                }
            })
        },
        handleCloseTemplate() {
            // 关闭选择模板
            this.templateStatus = 1
        },
        // 刷新所有数据
        async referData() {
            this.customerLoading = true;
            this.taskLoading = true;
            const res = await Promise.all([
                listSendContentTemplateAll({ projectId: this.projectId }),
                taskMyStatusList(this.taskStatusQueryForm),
                listTask({ projectId: this.projectId }),
                taskStatus()
            ])
            /**
             * 获取群发模板
             *
             */
            if (res[0].code == 200) {
                this.templateList = res[0].rows.map(item => {
                    let use = item.useNum.split("/")[0]
                    let total = item.useNum.split("/")[1]
                    item.use = use
                    item.total = total
                    // 剩余使用量
                    item.remain = total - use
                    return item
                });
            }
            /**
             * 获取任务分配列表
             */
            this.customerLoading = false;
            this.CustomerServiceDetailList = res[1].rows
            this.taskStatusQueryForm.total = res[1].total
            /**
             * 获取任务列表
             */

            this.total = res[2].total;
            this.taskLoading = false;
            this.taskList = res[2].rows

            /**
             * 获取任务状态
             */
            this.taskStatusOptions = res[3].data
        },
        handleGetTaskStatus() {
            taskStatus().then(res => {
                this.taskStatusOptions = res.data
            })
        },
        getCustomerServiceList() {
            this.customerLoading = true;
            taskMyStatusList(this.taskStatusQueryForm).then(response => {
                this.CustomerServiceDetailList = response.rows;
                this.taskStatusQueryForm.total = response.total;
                this.customerLoading = false;
            });
        },
        // 查询单个任务下的客服分配数据
        handleDetailTaskStatus(row) {
            this.taskStatusQueryForm.taskId = row.taskId
            this.taskStatusQueryForm.taskName = row.taskName
            this.taskStatusQueryForm.pageNum = 1
            this.taskStatusQueryForm.pageSize = 50
            this.getCustomerServiceList()
        },
        clearTaskStatusQueryForm() {
            this.taskStatusQueryForm.taskId = null
            this.taskStatusQueryForm.taskName = null
            this.taskStatusQueryForm.pageNum = 1
            this.taskStatusQueryForm.pageSize = 50
            this.getCustomerServiceList()
        },
        handleSearchTaskContent() {
            this.taskContentQuery.pageNum = 1;
            this.getTaskContentList();
        },
        /** 重置任务内容查询条件 */
        resetTaskContentQuery() {
            this.taskContentQuery.pageNum = 1;
            this.taskContentQuery.taskStatus = null,
                this.taskContentQuery.recipientList = null,
                this.taskContentQuery.selectedContents = []
            this.getTaskContentList();
        },
        /** 查询任务列表 */
        getTaskList() {
            this.taskLoading = true;
            listTask({ projectId: this.projectId }).then(response => {
                this.taskList = response.rows;
                this.total = response.total;
                this.taskLoading = false;
            });
        },
        /** 搜索按钮操作 */
        handleQuery() {
            this.taskQueryParams.pageNum = 1;
            this.referData()
        },
        /** 重置按钮操作 */
        resetQuery() {
            this.dateRange = [];
            this.resetForm("queryForm");
            this.handleQuery();
        },
        handleUploadTask() {
            this.$refs.uploadForm.validate(valid => {
                if (valid) {
                    this.$refs.uploadTask.submit()
                }
            });
        },
        /** 上传前的处理 */
        beforeUpload(file) {
            // 验证表单字段
            let temp = true;
            // 需要选择群发模板，所以需要检验模板是否选择
            if (this.templateStatus == 0) {
                this.$refs.uploadForm.validate(valid => {
                    if (!valid) {
                        this.$modal.msgError('请先完善表单信息');
                        temp = false;
                    }
                });
            }
            return temp;
        },
        handleFileChange(file) {
            this.templateFile = file.raw
        },
        handleFileRemove() {
            this.form.file = null
        },
        /** 上传成功处理 */
        handleUploadTemplateSuccess(response, file, fileList) {
            if (response.code == 200) {
                this.$modal.msgSuccess("话术上传成功，请继续上传任务");
                this.uploadForm.templateId = "";
            } else {
                this.$modal.msgError(response.msg);
            }

            this.referData()
        },
        /** 上传失败处理 */
        handleUploadTemplateError(error, file, fileList) {
            this.$modal.msgError("上传失败");
        },
        /** 上传成功处理 */
        handleUploadSuccess(response, file, fileList) {
            this.$refs['uploadTask'].clearFiles();
            if (response.code == 200) {
                this.$modal.msgSuccess("上传成功");
                this.openTaskDialog = false
            } else {
                this.$modal.msgError(response.msg);
            }
            this.referData()
        },
        /** 上传失败处理 */
        handleUploadError(error, file, fileList) {
            this.$refs['uploadTask'].clearFiles();
            this.$modal.msgError("上传失败");
        },
        /** 修改按钮操作 */
        handleUpdateTask(row) {
            // 实现修改逻辑
        },
        handleChangeTask(id, type) {
            switch (type) {
                case 'start':
                    // 启动任务
                    StartTask(id).then(res => {
                        if (res.code == 200) {
                            this.$modal.msgSuccess("启动成功");
                            this.referData()
                        }
                    })
                    return;
                case 'pause':
                    // 暂停任务
                    this.$modal.confirm('是否暂停该任务').then(() => {
                        return suspendTask(id);
                    }).then(res => {
                        if (res.code == 200) {
                            this.$modal.msgSuccess("暂停成功");
                            this.referData()
                        }
                    }).catch(() => { });
                    return;
                case 'assign':
                    // 分配任务
                    this.$modal.confirm('是否确认分配该任务？分配后不能删除').then(() => {
                        const progress = {
                            pace: 0, taskId: id, time: new Date()
                        }
                        localStorage.setItem('progress', JSON.stringify(progress))
                        this.$store.commit('SET_PACE', progress.pace)
                        this.$store.commit('SET_TASKID', progress.taskId)
                        return assignedTask(id);
                    }).then(res => {
                    }).catch(() => { });
                    return;
            }
        },
        /** 删除按钮操作 */
        handleDeleteTask(row) {
            this.$modal.confirm('是否确认删除该任务？').then(() => {
                if (this.isManage == 0) {
                    // 客服的执行删除逻辑
                    return removeByUserId(row.taskId);
                } else {
                    // 组长和管理员的执行删除逻辑
                    return delTask(row.taskId);
                }
            }).then(() => {
                this.referData()
                this.$modal.msgSuccess("删除成功");
            }).catch(() => { });
        },
        /** 获取任务内容列表 */
        getTaskContentList() {
            this.contentLoading = true;
            taskContentByUserId(this.taskContentQuery).then(response => {
                this.taskContentList = response.rows;
                this.contentTotal = response.total;
                this.contentLoading = false;
            });
        },
        /** 处理选择变更 */
        handleSelectionChange(selection) {
            this.taskContentQuery.selectedContents = selection.map(s => s.taskContentId);
        },

        /** 分配用户 */
        // handleAssignUser() {
        //     if (this.taskContentQuery.selectedContents.length === 0) {
        //         this.$modal.msgError("请选择要分配的内容");
        //         return;
        //     }
        //     if (!this.taskContentQuery.assignedUser) {
        //         this.$modal.msgError("请选择要分配的用户");
        //         return;
        //     }
        //     const contentIds = this.taskContentQuery.selectedContents.map(item => item.taskContentId);
        //     updateTaskContent({
        //         taskContentIds: contentIds,
        //         assignedUser: this.taskContentQuery.assignedUser
        //     }).then(() => {
        //         this.$modal.msgSuccess("分配成功");
        //         this.getTaskContentList();
        //     });
        // },
        // 新增：批量删除任务内容方法
        handleBatchDeleteTaskContent() {
            if (!this.taskContentQuery.selectedContents.length) {
                this.$modal.msgError("请先选择要删除的任务内容");
                return;
            }
            this.$modal.confirm("是否确认删除选中的任务内容？").then(() => {
                return delTaskContent(this.taskContentQuery.selectedContents);
            }).then(() => {
                this.taskContentQuery.selectedContents = [];
                this.taskContentQuery.pageNum = 1
                this.getTaskContentList();
                this.$modal.msgSuccess("删除成功");
            }).catch(() => { });
        },
        /** 编辑任务内容 */
        handleEditContent(row) {
            // 实现编辑逻辑
        },
        /** 删除任务内容 */
        handleDeleteContent(row) {
            this.$modal.confirm('是否确认删除该任务内容？').then(() => {
                return delTaskContent(row.taskContentId);
            }).then(() => {
                this.getTaskContentList();
                this.$modal.msgSuccess("删除成功");
            }).catch(() => { });
        },
        /** 查看详细按钮操作 */
        handleDetailTask(id) {
            this.title = "任务详情";
            // this.taskDetail = row;
            this.taskContentQuery.assignedTo = id;
            this.openTaskDetail = true;
            this.getTaskContentList();
        }
    }
};
</script>

<style scoped>
.app-container {
    padding: 20px;
    background-color: transparent;
}

.box-card {
    margin-bottom: 20px;
    border-radius: 8px;
    /* box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05); */
    transition: all 0.3s ease;
}


.upload-demo {
    margin-bottom: 20px;
}

.el-form-item {
    margin-bottom: 22px;
}

/*
.el-button--primary {
    background-color: #409EFF;
    border-color: #409EFF;
    transition: all 0.3s ease;
}

.el-button--primary:hover {
    background-color: #66b1ff;
    border-color: #66b1ff;
} */

.el-table {
    border-radius: 8px;
    overflow: hidden;
}

.el-table th {
    background-color: #f5f7fa !important;
}

.el-pagination {
    margin-top: 20px;
    text-align: right;
}

.el-dialog {
    border-radius: 8px;
}

.el-dialog__header {
    padding: 20px;
    border-bottom: 1px solid #ebeef5;
}

.el-dialog__body {
    padding: 30px 20px;
}

.el-dialog__footer {
    padding: 20px;
    border-top: 1px solid #ebeef5;
}
</style>
