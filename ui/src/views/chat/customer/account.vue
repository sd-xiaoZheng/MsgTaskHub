<template>
  <div class="app-container">
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
        <el-form-item label="账号" prop="account">
          <el-input v-model="queryParams.account" placeholder="请输入账号" clearable @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="queryParams.userName" placeholder="请输入用户名" clearable @keyup.enter.native="handleQuery" />
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
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
      <el-table v-loading="loading" :data="AccountsList" max-height="450" @selection-change="handleSelectionChange">
        <!-- <el-table-column type="selection" width="55" align="center" /> -->
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
        <el-table-column label="发送数量" align="center" prop="sendNum" /> 
        <el-table-column label="创建时间" align="center"width="150"  prop="createTime">
        </el-table-column> 
        <el-table-column label="使用时间" align="center" width="150"  prop="startTime">
        </el-table-column> 
        <el-table-column label="封禁时间" align="center" width="150" prop="banTime">
        </el-table-column>
        <el-table-column label="封禁原因" align="center" width="150" prop="reason">
        </el-table-column>
        <el-table-column label="分配人" align="center" prop="assignedToName" />
        <el-table-column label="状态" align="center" prop="accStatus">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.account_status" :value="scope.row.accStatus" />
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width"  fixed="right">
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
      <el-dialog :title="title" :visible.sync="open" width="500px" >
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
          <el-form-item label="所属平台0SMS 1抖音" prop="platform">
            <el-input v-model="form.platform" placeholder="请输入所属平台0SMS 1抖音" />
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
  </div>
</template>

<script>
import { listAccounts,listRecycleAccounts, getAccounts, delAccounts, addAccounts, updateAccounts } from "@/api/chat/Accounts";
import { getToken } from "@/utils/auth";
import {mapGetters } from 'vuex'
export default {
  name: "Accounts",
  dicts: ['account_status'],
  props:['userId','projectId'],
  data() {
    return {
      queryRecycleParams:{
        pageNum:1,
        pageSize:50,
        total:0,
        projectId:null
      },
      recycleList: [],
      recycleVisible: false,
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
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
        assignedTo: null,
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
    };
  },
  activated() {
  },
  created() {
    this.queryParams.assignedTo=this.userId
    this.queryParams.projectId=this.projectId
    this.getList();
  }, 
  watch:{
    
  },
  methods: {
    
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
    // 清空数据
    handleClear() {
      this.$confirm("此操作将清空回收站, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        // 清空回收站
        this.clearRecycle();
      });
    },
    getRecycleList(){
      listRecycleAccounts(this.queryRecycleParams).then(res=>{
        this.recycleList=res.rows
      })
    },
    // 导入功能
    handleRecycle() {
      // 回收站功能
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
      this.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>", "导入结果", { dangerouslyUseHTMLString: true });
      this.getList();
    },
    // 提交上传文件
    submitFileForm() {
      this.$refs.upload.submit();
    },
    /** 查询分配给的客服账号列表 */
    getList() {
      this.loading = true;
      listAccounts(this.queryParams).then(response => {
        this.AccountsList = response.rows;
        this.total = response.total;
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
      getAccounts(accountId).then(response => {
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
            updateAccounts(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addAccounts(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const accountIds = row.accountId || this.ids;
      this.$modal.confirm('是否确认删除分配给的客服账号编号为"' + accountIds + '"的数据项？').then(function () {
        return delAccounts(accountIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('chat/Accounts/export', {
        ...this.queryParams
      }, `Accounts_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
<style lang="scss" scoped>
.box-card {
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
</style>