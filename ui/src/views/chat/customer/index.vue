<template>
  <div class="app-container">
    <el-card> 

      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" icon="el-icon-download" size="mini" @click="handleGenerate"
            v-hasPermi="['chat:CustomerServiceDetail:export']">批量生成客服</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="primary" plain icon="el-icon-download" size="mini" :disabled="multiple" :loading="supplyAccountStatus"
            @click="handleSupplyAccount">补号</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="primary" icon="el-icon-download" size="mini" :disabled="single"
            @click="handleExportTaskContent">导出未完成任务</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
            v-hasPermi="['chat:CustomerServiceDetail:remove']">删除</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
            v-hasPermi="['chat:CustomerServiceDetail:edit']">修改</el-button>
        </el-col>

        <right-toolbar :showSearch.sync="showSearch" :search="false" @queryTable="getList"></right-toolbar>
      </el-row>
      <el-table row-key="userId" :row-class-name="tableRowClassName" v-loading="loading"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"  
        :data="CustomerServiceDetailList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <!-- <el-table-column label="客服编号" align="center" prop="customerId" /> -->
        <!-- <el-table-column label="所属项目" align="center" prop="projectName" /> -->
        <el-table-column label="客服账号" align="center" prop="userName" />
        <el-table-column label="最大分配" align="center" prop="maxLoad" />
        <!-- <el-table-column label="状态" align="center" prop="customerStatus" /> -->
        <el-table-column label="身份" align="center" prop="managerId">
          <!-- -1代表是组长 -->
          <template slot-scope="scope">
            <el-tag type="warning" v-if="scope.row.managerId === -1">
              组长
            </el-tag>
            <el-tag type="primary" v-else>
              客服
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="status">
          <template slot-scope="scope">
            <el-tag type="success" v-if="scope.row.status === 0">
              启用
            </el-tag>
            <el-tag type="danger" v-else>
              离职
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="分配 / 发送 / 成功" align="center">
          <template #default="scope">
            {{ scope.row.surplusNum + ' / ' + scope.row.sendNum + ' / ' + scope.row.successNum }}
          </template>
        </el-table-column>
        <el-table-column label="回复数量" align="center" prop="replyNum" />
        <el-table-column label="回复率( % )" align="center" prop="replyRate">
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
        <el-table-column label="账号数量" align="center" prop="banNum">
          <template #default="scope">
            {{ scope.row.accountNum + '/' + scope.row.banNum }}
          </template>
        </el-table-column>
        <el-table-column label="账号库存" align="center" prop="isEnough">
          <template #default="scope">
            <template v-if="scope.row.managerId !== -1">
              <el-tag type="success" v-if="scope.row.isEnough === 1">
                充足
              </el-tag>
              <el-tag type="danger" v-else>
                不充足
              </el-tag>
            </template>

          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <!-- <el-button size="mini" type="text" icon="el-icon-edit" v-if="scope.row.managerId !== -1"
            :disabled="supplyAccountStatus"
              @click="handleInputNumSupplyAccount(scope.row)" v-hasPermi="['chat:CustomerServiceDetail:edit']">补号</el-button> -->
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleViewAccount(scope.row.userId)"
              v-hasPermi="['chat:CustomerServiceDetail:edit']">详情</el-button>
            <el-dropdown size="mini" @command="(command) => handleCommand(command, scope.row)"
              v-hasPermi="['chat:CustomerServiceDetail:edit']">
              <el-button size="mini" type="text" icon="el-icon-d-arrow-right">更多</el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item :command="handleUpdate" icon="el-icon-edit"
                  v-hasPermi="['chat:CustomerServiceDetail:edit']">修改</el-dropdown-item>
                <el-dropdown-item :command="handleDimission" v-if="scope.row.managerId !== -1" icon="el-icon-s-release"
                  v-hasPermi="['chat:CustomerServiceDetail:edit']">离职</el-dropdown-item>
                <el-dropdown-item :command="handleDelete" icon="el-icon-delete"
                  v-hasPermi="['chat:CustomerServiceDetail:remove']">删除</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <!-- <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
        @pagination="getList" /> -->

        <el-dialog title="补号" :visible.sync="supplyAccountDialog" width="500px" :close-on-click-modal="false" :close-on-press-escape="false">
          <el-radio v-model="supplyRadio" label="auto">一键补号</el-radio>
  <el-radio v-model="supplyRadio" label="input">输入补号</el-radio>
  <el-form ref="supplyFormRef" :model="supplyForm" v-show="supplyRadio === 'input'" :rules="supplyFormRules">
    <el-form-item label="补号数量">
      <el-input v-model="supplyForm.supplyNum" type="number" placeholder="请输入补号数量" />
    </el-form-item>
  </el-form>

  <template #footer>
    <el-button type="primary" @click="submitSupplyAccount">确定</el-button>
  </template>
         </el-dialog>

      <!-- 添加或修改客服号详情，用于存储客服号详细信息对话框 -->
      <el-dialog :title="title" :visible.sync="open" width="500px">
        <el-form ref="form" :model="form" :rules="rules">
          <el-form-item label="登录账号" prop="userName">
            <el-input v-model="form.userName" disabled placeholder="请输入登录账号" />
          </el-form-item>
          <!-- <el-form-item label="登录密码" prop="password">
            <el-input v-model="form.password" placeholder="请输入登录密码" />
          </el-form-item> -->
          <el-form-item label="分配数量" prop="maxLoad">
            <el-input v-model="form.maxLoad" :disabled="form.managerId === -1" placeholder="请输入分配数量" />
          </el-form-item>
          <el-form-item label="已分配数量" prop="surplusNum">
            <el-input v-model="form.surplusNum" disabled placeholder="请输入已分配数量" />
          </el-form-item>
          <!-- <el-form-item label="管理员用户ID" prop="managerId">
            <el-input v-model="form.managerId" placeholder="请输入管理员用户ID" />
          </el-form-item>
          <el-form-item label="总发送" prop="sendNum">
            <el-input v-model="form.sendNum" placeholder="请输入总发送" />
          </el-form-item>
          <el-form-item label="成功数量" prop="successNum">
            <el-input v-model="form.successNum" placeholder="请输入成功数量" />
          </el-form-item>
          <el-form-item label="总封禁账号数量" prop="banNum">
            <el-input v-model="form.banNum" placeholder="请输入总封禁账号数量" />
          </el-form-item> -->
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </el-dialog>

      <el-dialog title="批量生成客服" :visible.sync="generateDialogVisible" width="500px">
        <el-form ref="generateFormRef" :model="generateForm" :rules="generateRules" label-width="100px">
          <el-form-item label="账号前缀" prop="userName">
            <el-input v-model="generateForm.userName" placeholder="请输入账号前缀">
              <template slot="append">
                <el-tooltip content="生成随机数字" placement="top">
                  <el-button icon="el-icon-coin" size="small" @click="generateRandomPrefix('number')"></el-button>
                </el-tooltip>
                <el-tooltip content="生成随机字母" placement="top">
                  <el-button icon="el-icon-collection" size="small" @click="generateRandomPrefix('letter')"></el-button>
                </el-tooltip>
                <el-tooltip content="生成随机混合" placement="top">
                  <el-button icon="el-icon-magic-stick" size="small" @click="generateRandomPrefix('mixed')"></el-button>
                </el-tooltip>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="登录密码" prop="password">
            <el-input type="password" v-model="generateForm.password" placeholder="请输入登录密码" />
          </el-form-item>
          <el-form-item label="客服后缀" prop="customerRng">
            <el-radio-group v-model="generateForm.customerRng">
              <el-radio :label="0">顺序生成</el-radio>
              <el-radio :label="1">随机生成</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="生成组长" prop="isManage">
            <el-switch v-model="generateForm.isManage" :active-value="1" :inactive-value="0" active-text="生成"
              inactive-text="不生成" />
          </el-form-item>
          <el-form-item label="组长后缀" prop="leaderSuffix" v-if="generateForm.isManage === 1">
            <el-input v-model="generateForm.leaderSuffix" placeholder="请输入组长后缀" />
          </el-form-item>
          <el-form-item label="指定组长" prop="userId" v-if="generateForm.isManage === 0">
            <el-select v-model="generateForm.userId" placeholder="请选择组长">
              <el-option v-for="item in leaderList" :label="item.userName" :value="item.userId">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="生成数量" prop="genNum">
            <el-input-number v-model="generateForm.genNum" :min="1" :max="1000" />
          </el-form-item>
          <el-form-item label="最大分配量" prop="maxLoad">
            <el-input-number v-model="generateForm.maxLoad" :min="1" :max="10000" />
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" :loading="loadingSubmit" @click="submitGenerate">确 定</el-button>
          <el-button @click="generateDialogVisible = false">取 消</el-button>
        </div>
      </el-dialog>

      <!-- 新增：生成客服列表展示弹窗 -->
      <el-dialog title="生成的客服列表" :visible.sync="generateListDialogVisible" :show-close="false"
        :close-on-press-escape="false" :close-on-click-modal="false" width="600px">
        <el-table :data="generatedCustomerList" max-height="450px">
          <el-table-column prop="customerId" label="客服编号" />
          <el-table-column prop="userName" label="客服号名称" />
          <el-table-column prop="maxLoad" label="最大分配量" />
          <el-table-column prop="projectId" label="所属项目" />
          <!-- 如有其它字段，可扩展 -->
        </el-table>
        <div slot="footer" class="dialog-footer">
          <el-button type="success" id="equSN" :data-clipboard-text="copyData" @click="handleCopyData">一键复制</el-button>
        </div>
      </el-dialog>


      <!-- 客服的账号数据 -->
      <el-dialog title="分配的账号数据" :visible.sync="accountDialogVisible" width="90%">
        <account v-if="accountDialogVisible" :userId="currentAccountUserId" :projectId="projectId" />
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
import { listCustomerServiceDetail, leaderAllList, getCustomerServiceDetail, delCustomerServiceDetail, addCustomerServiceDetail, updateCustomerServiceDetail, addBatch } from "@/api/chat/CustomerServiceDetail";
import { mapGetters } from "vuex";
import { supplementFillAccount, dimission } from "@/api/chat/Accounts";
import account from "./account.vue";
import Clipboard from 'clipboard'
export default {
  name: "CustomerServiceDetail",
  components: {
    account
  },
  data() {
    return {
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
      // 客服号详情，用于存储客服号详细信息表格数据
      CustomerServiceDetailList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 50,
        projectId: null,
        maxLoad: null,
        customerStatus: null,
        managerId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      generateForm: {
        userName: "",
        password: "",
        isManage: 1,  // 修改为0（不生成组长）或1（生成组长）
        genNum: 20,
        customerRng: 0,
        leaderSuffix: '999',
        maxLoad: 1000,
        roleIds: [],
        postIds: [],
        projectId: null
      },
      generateRules: {
        userName: [
          { required: true, message: '请输入账号前缀', trigger: 'blur' }
        ],
        genNum: [
          { required: true, message: '请输入生成数量', trigger: 'blur' }
        ],
        maxLoad: [
          { required: true, message: '请输入最大分配量', trigger: 'blur' }
        ],
        userId: [
          { required: true, message: '请选择组长', trigger: 'change' }
        ]
      },
      generateDialogVisible: false,
      generateListDialogVisible: false,
      generatedCustomerList: [],
      copyData: "",
      leaderList: [],
      currentAccountUserId: null,
      accountDialogVisible: false,
      loadingSubmit:false,//生成客服时候的加载状态
      supplyAccountStatus:false,//是否正在补号
      supplyAccountDialog:false,//补号弹窗
      supplyRadio:'auto', //补号类型
      supplyForm:{
        supplyNum:1
      },
      supplyFormRules:{
        supplyNum:[{
          required: true,
          message: '请输入补号数量',
          trigger: 'blur'
        }]
      }
    };
  },
  mounted(){
    // 获取补号是否有任务
    const flagTask=this.$globalTask.getTask(4)
    this.supplyAccountStatus=flagTask ? true :false
  },
  created() {
    // 获取全部组长的信息

    if (this.projectId) {
      this.generateForm.projectId = this.projectId
      this.queryParams.projectId = this.projectId
      this.getList()
      this.getLeaderList()
    }
  },
  computed: {
    ...mapGetters(['projectId'])
  },
  watch: {
    projectId: {
      handler(v) {
        this.generateForm.projectId = v
        this.queryParams.projectId = v
        this.getList()
        this.getLeaderList()
      },
    }
  },

  methods: {
    rowClassName({ row }) {
      return row.children?.length ? 'is-parent' : 'is-child';
    },
    getLeaderList() {
      // 获取当前项目的所有组长
      leaderAllList({ projectId: this.projectId }).then(res => {
        this.leaderList = res.data
      })
    },
    tableRowClassName({ row, rowIndex }) {
      if (row.managerId === -1) {
        return 'manager-row';
      }
      // return 'is-child';
      return '';
    },
    handleCopyData() {
      // 复制数据 generatedCustomerList  //临时数据
      var clipboard = new Clipboard('#equSN')
      clipboard.on('success', e => {
        this.$message.success('已成功复制到粘贴板')
        //  释放内存
        clipboard.destroy()
        this.generateListDialogVisible = false
      })
      clipboard.on('error', e => {
        // 不支持复制
        this.$message.warning('该浏览器不支持复制')
        // 释放内存
        clipboard.destroy()
      })
    },
    handleGenerate() {
      this.generateDialogVisible = true;
    },
    submitGenerate() {
      this.$refs["generateFormRef"].validate(valid => {
        if (valid) {
          this.loadingSubmit = true
          this.generateForm.projectId = this.projectId
          addBatch(this.generateForm).then(response => {
            this.loadingSubmit = false
            // 假设返回数据在response.data.generatedList 
            this.generatedCustomerList = response.data || [];
            // 生成复制的数据
            let transToDataList = ""
            this.generatedCustomerList.forEach(item => {
              if (this.generateForm.isManage == 0 || this.generatedCustomerList[0].userName != item.userName) {
                transToDataList += `账号：${item.userName}，数量：${item.maxLoad}条\n`
              }
            })
            this.copyData = `${transToDataList}\n${this.generateForm.isManage == 1 ? '管理号：' + response.data[0].userName + '\n' : ''}密码：${this.generateForm.password ? this.generateForm.password : 'rbs999.'} \n总数：${this.generateForm.maxLoad * this.generateForm.genNum}\n地址：http://${window.location.hostname}`
            this.$modal.msgSuccess("批量生成成功");
            this.generateDialogVisible = false;
            this.generateListDialogVisible = true;
            this.getList();
            this.getLeaderList()
          });
        }
      });
    },
    /** 查询客服号详情，用于存储客服号详细信息列表 */
    getList() {
      this.loading = true;
      listCustomerServiceDetail(this.queryParams).then(response => {
        this.CustomerServiceDetailList = response.rows;
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
        customerId: null,
        projectId: null,
        userName: null,
        maxLoad: null,
        customerStatus: null,
        createTime: null,
        updateTime: null,
        managerId: null,
        sendNum: null,
        successNum: null,
        banNum: null
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
      this.ids = selection.map(item => item.userId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加客服号";
    },
    // 离职操作
    handleDimission(row) {
      this.$confirm('此操作将离职当前客服, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        dimission(row.userId).then(() => {
          this.$message.success("操作成功");
          this.getList();
        });
      })
    },
    // 下拉执行代码
    handleCommand(common, row) {
      common(row)
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const customerId = row.customerId || this.ids
      getCustomerServiceDetail(customerId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改客服号";
      });
    },
    // 导出未完成的任务
    handleExportTaskContent(){
      const handleIds = this.ids[0]
      this.download('chat/TaskContent/export', {
        userId: handleIds
      }, `TaskContent_${new Date().getTime()}.xlsx`)
    },
    // 模拟进度条
    generateProgress(){
      this.supplyAccountStatus=true
// 创建一个模拟进度条的定时器
let timer = setInterval(() => {
  if(this.supplyAccountStatus){

  const task = this.$globalTask.getTask(4) //有可能会没任务的
  // 获取当前进度
  let currentPace = task?.pace || 0;
  // 随机增长步长(1-3之间的随机整数)
  const increment = Math.floor(Math.random() * 3) + 1;
  // 新进度(取整)
  let newPace = Math.floor(currentPace + increment);
  
  // 当进度达到90-98之间随机整数值时停止
  const endPace = Math.floor(Math.random() * 9) + 90;
  if(newPace >= endPace) {
    clearInterval(timer);
    newPace = endPace;
  }
  // 如果当前进度已经是100%则停止
  if(currentPace >= 100) {
    newPace=100
    clearInterval(timer);
  }
  // 发送进度更新
  this.$globalTask.emitProgress({
    flag: 4,
    pace: newPace,
    name: "补号"
  });
}else{
  clearInterval(timer);
}
}, 1000); // 每1000ms更新一次进度
    },
    handleInputNumSupplyAccount(row){
      const handleIds = row.userId || this.ids
      // 输入指定补号的数量
      this.$prompt('请输入补号的数量', '补号', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValue: '',
        inputValidator: (value) => {
          // 验证输入是否为纯数字
          if (!/^\d+$/.test(value)) {
            return '请输入有效的数字';
          }
          return true;
        }
      }).then(({ value }) => {
        this.generateProgress() 
        supplementFillAccount(handleIds, value).then(res => {
          this.supplyAccountStatus=false
          if (res.code === 200) {
            this.$message.success(res.msg)
          }
          this.getList()
        })
      }) 
    },
    handleSupplyAccount() {
      this.supplyAccountDialog=true
      this.supp=1
      this.supplyRadio='auto'
      // const handleIds = row.userId || this.ids
      // this.$confirm('此操作将补全选择客服的账号数据, 是否继续?', '提示', {
      //   confirmButtonText: '确定',
      //   cancelButtonText: '取消',
      //   type: 'warning'
      // }).then(() => {
      //   supplementFillAccount(handleIds).then(res => {
      //     if (res.code === 200) {
      //       this.$message.success(res.msg)
      //     }
      //     this.getList()
      //   }) 
      // })
    },
    submitSupplyAccount(){
      this.$refs.supplyFormRef.validate(valid => {
        if (valid) {
          const handleIds = this.ids
          if(this.supplyRadio === 'auto'){
            this.supplyForm.supplyNum = -1
          }
        this.generateProgress() 
        this.supplyAccountDialog = false

          supplementFillAccount(handleIds, this.supplyForm.supplyNum).then(res => {
          this.supplyAccountStatus=false
          this.supplyForm.supplyNum = 1
          this.supplyRadio = 'auto'
            if (res.code === 200) {
              this.$message.success(res.msg)
            }
            this.getList()
          })
        }
      })
    },
    handleViewAccount(userId) {
      // 点击详情查看当前客服的账号数据
      this.currentAccountUserId = userId
      this.accountDialogVisible = true
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.customerId != null) {
            updateCustomerServiceDetail(this.form).then(response => {
              if (response.code === 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
              }
              this.getList();
            });
          } else {
            addCustomerServiceDetail(this.form).then(response => {
              if (response.code === 200) {
                this.$modal.msgSuccess("新增成功");
                this.open = false;
              }
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const customerIds = row.userId || this.ids;
      this.$modal.confirm('是否确认删除客服号详情，用于存储客服号详细信息编号为"' + customerIds + '"的数据项？').then(function () {
        return delCustomerServiceDetail(customerIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('chat/CustomerServiceDetail/export', {
        ...this.queryParams
      }, `CustomerServiceDetail_${new Date().getTime()}.xlsx`)
    },
    generateRandomPrefix(type) {
      const length = Math.floor(Math.random() * 3) + 4; // 4-6位随机长度
      const numbers = '0123456789';
      const letters = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
      let chars = '';

      switch (type) {
        case 'number':
          chars = numbers;
          break;
        case 'letter':
          chars = letters;
          break;
        case 'mixed':
          chars = numbers + letters;
          break;
      }

      let result = '';
      for (let i = 0; i < length; i++) {
        result += chars.charAt(Math.floor(Math.random() * chars.length));
      }

      this.generateForm.userName = result;
    },
  }
};
</script>

<style lang="scss">
.el-table .manager-row {
  background: rgb(241, 243, 245) !important;
}
.el-table .el-table__row.is-child .el-checkbox {
  display: none !important;
}
</style>