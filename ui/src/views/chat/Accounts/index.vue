<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>账号数据管理</span>
      </div>
      <!-- 统计卡片布局 -->
      <el-row :gutter="20" class="panel-group">
        <el-col :span="6">
          <el-card class="card-panel">
            <div class="card-panel-icon-wrapper icon-people">
              <svg-icon icon-class="peoples" class-name="card-panel-icon" />
            </div>
            <div class="card-panel-description">
              <div class="card-panel-text">总账号数</div>
              <count-to :start-val="0" :end-val="accountAllNum" :duration="2000" class="card-panel-num" />
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="card-panel">
            <div class="card-panel-icon-wrapper icon-message">
              <svg-icon icon-class="validCode" class-name="card-panel-icon" />
            </div>
            <div class="card-panel-description">
              <div class="card-panel-text">正常账号数</div>
              <count-to :start-val="0" :end-val="accountOk" :duration="2000" class="card-panel-num" />
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="card-panel">
            <div class="card-panel-icon-wrapper icon-money">
              <svg-icon icon-class="monitor" class-name="card-panel-icon" />
            </div>
            <div class="card-panel-description">
              <div class="card-panel-text">异常账号数</div>
              <count-to :start-val="0" :end-val="accountBan" :duration="2000" class="card-panel-num" />
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="card-panel">
            <div class="card-panel-icon-wrapper icon-shopping">
              <svg-icon icon-class="user" class-name="card-panel-icon" />
            </div>
            <div class="card-panel-description">
              <div class="card-panel-text">空闲账号数</div>
              <count-to :start-val="0" :end-val="accountNoAssi" :duration="2000" class="card-panel-num" />
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
        <el-form-item label="账号" prop="account">
          <el-input v-model="queryParams.account" placeholder="请输入账号" clearable @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="queryParams.userName" placeholder="请输入用户名" clearable @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="分配人" prop="assignedToName">
          <el-input v-model="queryParams.assignedToName" placeholder="请输入分配人" clearable
            @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="状态" prop="accStatus">
          <el-select v-model="queryParams.accStatus" placeholder="请选择状态">
            <el-option v-for="dict in dict.type.account_status" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-row :gutter="10" class="mb8">
        <!-- <el-col :span="1.5">
          <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
            v-hasPermi="['chat:Accounts:add']">新增</el-button>
        </el-col> -->
        <!-- <el-col :span="1.5">
          <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
            v-hasPermi="['chat:Accounts:edit']">修改</el-button>
        </el-col> -->
        <el-col :span="1.5">
          <el-button type="primary" icon="el-icon-upload2" size="mini" @click="handleImport"
            v-hasPermi="['chat:Accounts:import']">导入</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
            v-hasPermi="['chat:Accounts:remove']">删除</el-button>
        </el-col>
        <!-- <el-col :span="1.5">
          <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
            v-hasPermi="['chat:Accounts:export']">导出</el-button>
        </el-col> -->
        <el-col :span="1.5">
          <el-button plain icon="el-icon-delete-solid" size="mini" @click="handleRecycle"
            v-hasPermi="['chat:Accounts:export']">回收站</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="primary" icon="el-icon-upload2" size="mini" @click="importTxtDialog = true"
            v-hasPermi="['chat:Accounts:import']">导入TXT账号</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-dropdown type="danger" plain size="mini" @click="handleClear(1)" @command="handleClear" split-button>
            清空所有
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="0">清空未分配</el-dropdown-item>
              <el-dropdown-item command="2">清空封号</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </el-col>
        <el-col :span="1.5">
          <el-dropdown type="primary" plain size="mini" @click="handleExport(0)" @command="handleExport" split-button>
            导出所有
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="1">导出可用</el-dropdown-item>
              <el-dropdown-item command="2">导出已分配</el-dropdown-item>
              <el-dropdown-item command="3">导出封禁</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </el-col>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="handleRefer"></right-toolbar>
      </el-row>

      <el-table v-loading="loading" :data="AccountsList" max-height="450" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <!-- <el-table-column label="编号" align="center" prop="accountId" /> -->
        <el-table-column label="账号" align="center" prop="account" />
        <!-- <el-table-column label="cookie" align="center" prop="cookie">
          <template #default="scope">
            <el-popover placement="top" width="100%" trigger="hover" :content="scope.row.cookie">
              <div slot="reference" style="white-space: nowrap;text-overflow: ellipsis;">
                {{ scope.row.cookie }}
              </div>
            </el-popover>
          </template>
</el-table-column> -->
        <el-table-column label="设备信息" align="center" prop="deviceInfo">
          <template #default="scope">
            <el-button style="margin-bottom: 10px;" type="primary" size="mini"
              @click="handleCopyDeviiceInfo(scope.row.deviceInfo)">
              复制
            </el-button>
            <el-popover placement="top" width="90%" trigger="hover" :content="scope.row.deviceInfo">
              <div slot="reference" style="white-space: nowrap;text-overflow: ellipsis;">
                {{ scope.row.deviceInfo }}
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <!-- <el-table-column label="用户名" align="center" prop="userName" /> -->
        <el-table-column label="密码" align="center" prop="password" />
        <el-table-column label="分配人" align="center" prop="assignedToName" />
        <el-table-column label="上传人" align="center" prop="upUser" />
        <el-table-column label="发送数量" align="center" prop="sendNum" />
        <el-table-column label="创建时间" align="center" width="150" prop="createTime">
        </el-table-column>
        <el-table-column label="使用时间" align="center" width="150" prop="startTime">
        </el-table-column>
        <el-table-column label="封禁时间" align="center" width="150" prop="banTime">
        </el-table-column>
        <el-table-column label="封禁原因" align="center" width="150" prop="reason">
        </el-table-column>
        <el-table-column label="国家" align="center" width="150" prop="country">
        </el-table-column>
        <el-table-column label="状态" align="center" prop="accStatus">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.account_status" :value="scope.row.accStatus" />
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
              v-hasPermi="['chat:Accounts:edit']">修改</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
              v-hasPermi="['chat:Accounts:remove']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
        @pagination="getList" />

      <!-- 添加或修改分配给的客服账号对话框 -->
      <el-dialog :title="title" :visible.sync="open" width="500px">
        <el-form ref="form" :model="form" :rules="rules" label-width="80px">
          <el-form-item label="账号" prop="account">
            <el-input v-model="form.account" placeholder="请输入账号" />
          </el-form-item>
          <el-form-item label="cookie" prop="cookie">
            <el-input v-model="form.cookie" type="textarea" placeholder="请输入内容" />
          </el-form-item>
          <el-form-item label="设备信息" prop="deviceInfo">
            <el-input v-model="form.deviceInfo" type="textarea" placeholder="请输入内容" />
          </el-form-item>
          <el-form-item label="用户名" prop="userName">
            <el-input v-model="form.userName" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" placeholder="请输入密码" />
          </el-form-item>
          <el-form-item label="分配人" prop="assignedTo">
            <el-input v-model="form.assignedTo" placeholder="请输入分配人" />
          </el-form-item>
          <el-form-item label="所属项目" prop="projectId">
            <el-input v-model="form.projectId" placeholder="请输入所属项目" />
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </el-dialog>
    </el-card>

    <!-- 账号txt导入对话 -->
    <importAccountTxt :dialogValue="importTxtDialog" :uploadForm="uploadForm" @success="handleSuccessImportAccountTxt" @close="importTxtDialog=false" />

    <!-- 项目导入对话框 -->
    <el-dialog :title="upload.title" :visible.sync="upload.open" width="400px">
      <el-upload ref="upload" :limit="1" accept=".xlsx, .xls" :headers="upload.headers" :action="upload.url"
        :data="uploadForm" :disabled="upload.isUploading" :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess" :auto-upload="false" drag>
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip text-center" slot="tip">
          <span>仅允许导入xls、xlsx格式文件。</span>
          <!-- <el-link type="primary" :underline="false" style="font-size: 12px; vertical-align: baseline"
            @click="importTemplate">下载模板</el-link> -->
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 回收站弹窗 -->
    <el-dialog title="回收站" :visible.sync="recycleVisible" width="80%">
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <div class="block">
            <el-form :model="queryRecycleParams" inline>
              <el-form-item label="分配人" prop="assignedToName">
          <el-input v-model="queryRecycleParams.assignedToName" placeholder="请输入分配人" clearable
            @keyup.enter.native="handleQuery" />
        </el-form-item>
              <el-form-item>
                <el-date-picker value-format="yyyy-MM-dd hh:mm" v-model="queryRecycleParams.time" type="datetimerange"
                  range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期">
                </el-date-picker>
              </el-form-item>
              <el-form-item>
                <el-button size="small" icon="el-icon-search" type="primary"
                  @click="handleSearchRecycle()">搜索</el-button>
              </el-form-item>

            </el-form>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" icon="el-icon-plus" size="mini" :loading="recycleAllStatus" @click="handleRecycleData"
            v-hasPermi="['chat:Accounts:add']">恢复</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="success" icon="el-icon-plus" size="mini" @click="handleRecycleAll" :loading="recycleAllStatus"
            v-hasPermi="['chat:Accounts:edit']">还原所有可用账号</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="danger" icon="el-icon-delete" size="mini" :loading="recycleAllStatus" @click="handleClear(3)"
            v-hasPermi="['chat:Accounts:edit']">清空回收站</el-button>
        </el-col>
        <right-toolbar :search="false" @queryTable="getRecycleList"></right-toolbar>
      </el-row>
      <el-table v-loading="recycleLoading" :data="recycleList" @selection-change="handleSelectionRecycleChange"
        max-height="450">
        <el-table-column type="selection" width="55" align="center" />
        <!-- <el-table-column label="编号" align="center" prop="accountId" /> -->
        <el-table-column label="账号" align="center" prop="account" />
        <!-- <el-table-column label="cookie" align="center" prop="cookie">
          <template #default="scope">
            <el-popover placement="top" width="100%" trigger="hover" :content="scope.row.cookie">
              <div slot="reference" style="white-space: nowrap;text-overflow: ellipsis;">
                {{ scope.row.cookie }}
              </div>
            </el-popover>
          </template>
        </el-table-column> -->
        <el-table-column label="设备信息" align="center" prop="deviceInfo">
          <template #default="scope">
            <el-button style="margin-bottom: 10px;" type="primary" size="mini"
              @click="handleCopyDeviiceInfo(scope.row.deviceInfo)">
              复制
            </el-button>
            <el-popover placement="top" width="90%" trigger="hover" :content="scope.row.deviceInfo">
              <div slot="reference" style="white-space: nowrap;text-overflow: ellipsis;">
                {{ scope.row.deviceInfo }}
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column label="用户名" align="center" prop="userName" />
        <el-table-column label="密码" align="center" prop="password" />
        <el-table-column label="分配人" align="center" prop="assignedToName" />
        <el-table-column label="上传人" align="center" prop="upUser" />
        <el-table-column label="发送数量" align="center" prop="sendNum" />
        <el-table-column label="创建时间" align="center" width="150" prop="createTime">
        </el-table-column>
        <el-table-column label="使用时间" align="center" width="150" prop="startTime">
        </el-table-column>
        <el-table-column label="封禁时间" align="center" width="150" prop="banTime">
        </el-table-column>
        <el-table-column label="封禁原因" align="center" width="150" prop="reason">
        </el-table-column>
        <el-table-column label="状态" align="center" prop="accStatus">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.account_status" :value="scope.row.accStatus" />
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleRecycleData(scope.row)"
              v-hasPermi="['chat:Accounts:edit']">恢复</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleRealDelete(scope.row)"
              v-hasPermi="['chat:Accounts:remove']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="queryRecycleParams.total > 0" :total="queryRecycleParams.total"
        :page.sync="queryRecycleParams.pageNum" :limit.sync="queryRecycleParams.pageSize"
        @pagination="getRecycleList" />
    </el-dialog>
  </div>
</template>

<script>
import * as api from '@/api/chat/Accounts'
import { getToken } from "@/utils/auth";
import CountTo from 'vue-count-to'
import { mapGetters } from 'vuex'
import importAccountTxt from './components/importAccountTxt.vue';
export default {
  name: "Accounts",
  dicts: ['account_status'],
  components: {
    CountTo,importAccountTxt
  },
  data() {
    return {
      queryRecycleParams: {
        pageNum: 1,
        pageSize: 50,
        total: 0,
        projectId: null,
        time: null,
        startTime: null,
        endTime: null,
        assignedToName:""
      },
      recycleList: [],
      recycleVisible: false,
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      recycleIds: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 分配给的客服账号表格数据
      AccountsList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 50,
        accountId: null,
        account: null,
        cookie: null,
        deviceInfo: null,
        userName: null,
        password: null,
        accStatus: null,
        assignedToName: null,
        platform: null,
        projectId: null
      },

      // 表单参数
      form: {},
      // 表单校验
      rules: {
        account: [
          { required: true, message: "账号不能为空", trigger: "blur" }
        ],
      },
      // 账号导入参数
      upload: {
        // 是否显示弹出层（账号导入）
        open: false,
        // 弹出层标题（账号导入数据）
        title: "账号导入数据",
        // 是否禁用上传
        isUploading: false,
        // 设置上传的请求头部
        headers: { Authorization: "Bearer " + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/chat/Accounts/importAccount"
      },
      uploadForm: {
        projectId: null
      },
      // 封禁数量
      accountBan: 0,
      // 正常数量
      accountOk: 0,
      accountNoAssi: 0,
      accountAllNum: 0,
      recycleLoading: false,
      recycleAllStatus:false,//是否正在还原所有账号
      importTxtDialog:false,
    };
  },
  created() {
    if (this.projectId) {
      this.uploadForm.projectId = this.projectId
      this.queryParams.projectId = this.projectId
      this.queryRecycleParams.projectId = this.projectId
      this.getList();
      this.initStatus()
    }
  },
  mounted(){

  },
  computed: {
    ...mapGetters(['projectId'])
  },
  watch: {
    projectId: {
      handler(v) {
        this.queryParams.projectId = v
        this.queryParams.pageNum = 1
        this.queryRecycleParams.projectId = v
        this.queryRecycleParams.pageNum = 1
        this.uploadForm.projectId = v
        this.initStatus()
        this.getList()
      },
    }
  },
  methods: {
    handleSuccessImportAccountTxt(){
      this.importTxtDialog = false
      this.initStatus()
      this.getList()
    },
    handleSearchRecycle() {
      this.queryRecycleParams.pageNum = 1
      if(this.queryRecycleParams.time){
      let time = this.queryRecycleParams.time
      this.queryRecycleParams.startTime = time[0] + ""
      this.queryRecycleParams.endTime = time[1] + ""
      }

      this.getRecycleList()
    },
    // 刷新
    handleRefer() {
      this.getList()
      this.initStatus()
    },
    // 获取账号统计结果
    initStatus() {
      api.accountStatus(this.projectId).then(res => {
        if (res.code === 200) {
          this.accountBan = res.data.accountBan
          this.accountOk = res.data.accountOk
          this.accountNoAssi = res.data.accountNoAssi
          this.accountAllNum = res.data.accountAllNum
        }
      })
    },
    // 复制设备的信息
    handleCopyDeviiceInfo(data) {
      var copyData = data; //每一行的某个值，如选中的当前行的url
      var oInput = document.createElement('input'); //创建一个隐藏input（重要！）
      oInput.value = copyData; //赋值
      document.body.appendChild(oInput);
      oInput.select(); // 选择对象
      document.execCommand('Copy'); // 执行浏览器复制命令
      oInput.className = 'oInput';
      oInput.style.display = 'none';
      this.$modal.msgSuccess('已复制');
    },
    // 一键清空全部账号数据
    handleClear(type = 1) {
      type = parseInt(type)
      let tips;
      switch (type) {
        case 0:
          tips = '未分配的'
          break;
        case 2:
          tips = '封禁的'
          break;
        case 3:
          tips = '回收站的'
          break;
      }
      this.$prompt(`请输入"<span style="color: red">确认删除</span>"以确认永久清除所有${tips || ''}数据`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        dangerouslyUseHTMLString: true,
        inputPattern: /^确认删除$/,
        inputErrorMessage: '输入内容必须为"确认删除"'
      }).then(({ value }) => {
        return this.$confirm('确认永久清除所有数据?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'danger',
          beforeClose: (action, instance, done) => {
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '执行中...';
              api.emptyAccount({ projectId: this.projectId, isAll: type }).then(res => {
                done();
                instance.confirmButtonLoading = false;
                if (res.code === 200) {
                  this.$message({
                    type: 'success',
                    message: '清空成功'
                  })
                  this.initStatus()
                  if (type === 3) {
                    this.queryRecycleParams.pageNum = 1
                    this.getRecycleList()
                  }else {
                    this.queryParams.pageNum = 1
                    this.getList()
                  }
                } else {
                  this.$message({
                    type: 'error',
                    message: res.msg
                  })
                }
              })
            } else {
              done();
            }
          }
        })
      }).then(res => {
      })
    },
    // 恢复可用数据
    handleRecycleAll() {
      this.$confirm("是否恢复所有数据", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        // 清空回收站
        this.$globalTask.emitProgress({flag:3,pace:0,name:"还原可用账号"})
        this.recycleAllStatus=true
        api.recycleAccount(this.projectId).then(res => {
          this.recycleAllStatus=false
          if (res.code === 200) {
            this.$message({
              type: "success",
              message: "恢复成功"
            });
            this.getRecycleList()
            this.getList()
          }
        });
      });
    },
    getRecycleList() {
      this.recycleLoading = true
      api.listRecycleAccounts(this.queryRecycleParams).then(res => {
        this.recycleLoading = false
        this.recycleList = res.rows
        this.queryRecycleParams.total = res.total
      })
    },
    handleRecycle() {
      // 回收站功能
      this.getRecycleList()
     const flagTask = this.$globalTask.getTask(3)
     this.recycleAllStatus = flagTask ? true : false
      this.recycleVisible = true;
    },
    /** 导入按钮操作 */
    handleImport() {
      this.upload.title = "账号导入";
      this.upload.open = true;
    },

    // 文件上传中处理
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true;
    },
    // 文件上传成功处理
    handleFileSuccess(response, file, fileList) {
      this.upload.open = false;
      this.upload.isUploading = false;
      this.$refs.upload.clearFiles();
      if (response.data) {
        // 代表有重复数据
        let msg = "<ol>";
        response.data.forEach(item => {
          msg += "<li>" + item.account + "</li>"
        })
        msg += "</ol>"
        response.msg = "导入成功，以下账号已存在数据：" + msg
      } else {
        response.msg = "导入成功"
      }
      this.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>", "导入结果", { dangerouslyUseHTMLString: true });
      this.initStatus();
      this.getList();
    },
    // 提交上传文件
    submitFileForm() {
      this.$refs.upload.submit();
    },
    /** 查询分配给的客服账号列表 */
    getList() {
      this.loading = true;
      api.listAccounts(this.queryParams).then(response => {
        this.AccountsList = response.rows;
        this.total = response.total;
        // 计算各类型账号数量
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        accountId: null,
        account: null,
        cookie: null,
        deviceInfo: null,
        userName: null,
        password: null,
        accStatus: null,
        assignedTo: null,
        platform: null,
        projectId: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.accountId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    // 多选框选中数据
    handleSelectionRecycleChange(selection) {
      this.recycleIds = selection.map(item => item.accountId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },

    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加分配给的客服账号";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const accountId = row.accountId || this.ids
      api.getAccounts(accountId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改分配给的客服账号";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.accountId != null) {
            api.updateAccounts(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            api.addAccounts(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },

    /** 恢复按钮操作 */
    handleRecycleData(row) {
      const accountIds = row.accountId || this.recycleIds;
      this.$modal.confirm('是否恢复该数据？').then(function () {
        return api.recycleAccountBatch(accountIds);
      }).then(() => {
        this.getList();
        this.getRecycleList();
        this.$modal.msgSuccess("恢复成功");
      }).catch(() => { });
    },
    // 真删除操作
    handleRealDelete(row) {
      const accountIds = row.accountId || this.ids;
      this.$modal.confirm('是否确认永久删除该数据？').then(function () {
        return api.deleteAccountsByAccountIdsReal(accountIds);
      }).then(() => {
        this.getRecycleList();
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const accountIds = row.accountId || this.ids;
      this.$modal.confirm('是否确认删除账号编号为"' + accountIds + '"的数据项？').then(function () {
        return api.delAccounts(accountIds);
      }).then((res) => {
        this.getRecycleList();
        this.getList();
        if (res.code === 200) {
          this.$modal.msgSuccess("删除成功");
        }
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport(accStatus) {
      /**
       * accStatus
       * 1：未分配 2：已分配  3：封禁 0：全部
       *
       */
      accStatus = parseInt(accStatus)
      if (accStatus === 1) {
        // 导出未分配需要输入数量
        this.$prompt('请输入导出数量', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputPattern: /^\d+$/,
          inputErrorMessage: '请输入数字'
        }).then(({ value }) => {
          this.download('chat/Accounts/export', {
            projectId: this.projectId, accStatus, exportNum: value
          }, `Accounts_${new Date().getTime()}.xlsx`)
        }).catch(() => { })
      } else {
        this.download('chat/Accounts/export', {
          projectId: this.projectId, accStatus
        }, `Accounts_${new Date().getTime()}.xlsx`)
      }
    }
  }
};
</script>
<style lang="scss" scoped>
.panel-group {
  margin-top: 18px;
  margin-bottom: 30px;

  .card-panel {
    font-size: 12px;
    position: relative;
    overflow: hidden;
    color: #666;
    background: #fff;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    border-radius: 12px;
    border: none;
    transition: all 0.3s ease;
    transition: all 0.3s ease-in-out;

    &:hover {
      transform: translateY(-2px);
      transform: translateY(-4px);
    }

    .card-panel-icon-wrapper {
      color: #fff;
    }

    .icon-people {
      background: linear-gradient(135deg, #7367F0 0%, #5E50EE 100%);
    }

    .icon-message {
      background: linear-gradient(135deg, #36a3f7 0%, #1a7fd1 100%);
    }

    .icon-money {
      background: linear-gradient(135deg, #f4516c 0%, #d62f4d 100%);
    }

    .icon-shopping {
      // background: linear-gradient(135deg, #b6bf34 0%, #9b9029 100%);
      background: linear-gradient(135deg, #34bfa3 0%, #299b85 100%);
    }

    .card-panel-icon-wrapper {
      float: left;
      margin: 14px 0 0 14px;
      padding: 16px;
      transition: all 0.38s ease-out;
      border-radius: 8px;

      .card-panel-icon {
        float: left;
        font-size: 40px;
        color: #fff;
      }
    }

    .card-panel-description {
      float: right;
      font-weight: bold;
      margin: 26px;
      margin-left: 0px;

      .card-panel-text {
        line-height: 18px;
        color: #666;
        color: #909399;
        margin-bottom: 12px;
      }

      .card-panel-num {
        font-size: 28px;
        color: #333;
        color: #303133;
      }
    }
  }
}
</style>
