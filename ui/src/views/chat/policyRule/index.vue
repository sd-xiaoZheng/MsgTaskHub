<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>项目策略配置</span>
        <el-button type="danger" icon="el-icon-delete" size="mini" style="float: right; margin-left: 10px;"
                   @click="handleClearSystem">清空系统数据</el-button>
      </div>

      <el-form :model="strategyForm" :rules="rules" ref="strategyForm" label-width="150px">
        <el-form-item label="所属项目" prop="projectId">
          <el-select v-model="strategyForm.projectId" :disabled="true" placeholder="请选择项目">
            <el-option
              v-for="item in projectList"
              :key="item.projectId"
              :label="item.projectName"
              :value="item.projectId"
            />
          </el-select>
        </el-form-item>

        <!-- <el-form-item label="客服后缀生成方式" prop="customerRng">
          <el-radio-group v-model="strategyForm.customerRng">
            <el-radio :label="0">顺序生成</el-radio>
            <el-radio :label="1">随机生成</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="管理员后缀" prop="leaderSuffix">
          <el-input v-model="strategyForm.leaderSuffix" placeholder="请输入管理员后缀" />
        </el-form-item> -->

        <el-form-item label="单个账号发送数量" prop="accountSendNum">
          <el-input-number v-model="strategyForm.accountSendNum" :min="1" />
        </el-form-item>

        <el-form-item label="客服查看账号权限" prop="accountControl">
          <el-radio-group v-model="strategyForm.accountControl">
            <el-radio :label="0">不可查看</el-radio>
            <el-radio :label="1">可以查看</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="用户自定义上传话术" prop="selfTemplate">
          <el-radio-group v-model="strategyForm.selfTemplate">
            <el-radio :label="-1">不可以</el-radio>
            <el-radio :label="1">可以</el-radio>
          </el-radio-group>
        </el-form-item>


        <!-- TODO:动态脚本的时间 -->

        <!-- <el-form-item label="检测任务正则表达式" prop="regular">
          <el-input
            v-model="strategyForm.regular"
            type="textarea"
            :rows="3"
            placeholder="请输入正则表达式，输入-1表示关闭检测"
          />
        </el-form-item> -->

        <el-form-item label="最大发送字数" prop="maxSend">
          <el-input-number v-model="strategyForm.maxSend" :min="1" />
        </el-form-item>
        <el-form-item label="发送冷却时间(秒)" prop="sendCd">
          <el-input-number v-model="strategyForm.sendCd" :min="1" />
        </el-form-item>
        <el-form-item label="账号发送间隔(秒)" prop="gapTime">
          <el-input-number v-model="strategyForm.gapTime" :min="1" />
        </el-form-item>
        <div style="margin-bottom: 20px;">
          到
          <el-input-number v-model="strategyForm.gapLongTime" :min="1" />
          次,
          间隔
          <el-input-number v-model="strategyForm.gapValue" :min="1" />
          秒左右
        </div>
        <el-form-item>
          <el-button type="primary" @click="handleSaveStrategy">保存配置</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import {getProjectSetByProjectId,updateProjectSet} from '@/api/chat/ProjectSet'
import { deleteSys } from '@/api/chat/Project'
export default {
  name: 'PolicyRule',
  computed: {
    ...mapGetters([
      'projectList','projectId'
    ])
  },
  watch:{
    projectId: {
      handler(v) {
        this.getProjectOptions()
      },
      // immediate: true
    }
  },
  data() {
    return {
      strategyForm: {
        projectId: null,
        // customerRng: 0,
        // leaderSuffix: '',
        accountSendNum: 100,
        accountControl: 0,
        regular: '',
        selfTemplate:-1,
        maxSend: 1000,
        /**
 * 账号执行任务间隔时间 s
 */
gapTime:0,

/**
 * 发送中长间隔时间 s
 */
gapLongTime:0,

/**
 * 达到账号发送间隔的条数阈值
 */
gapValue:0
      },
      rules: {
        projectId: [
          { required: true, message: '请选择项目', trigger: 'change' }
        ],
        // leaderSuffix: [
        //   { required: true, message: '请输入管理员后缀', trigger: 'blur' }
        // ],
        accountSendNum: [
          { required: true, message: '请输入发送数量限制', trigger: 'blur' }
        ],
        maxSend: [
          { required: true, message: '请输入最大发送字数', trigger: 'blur' }
        ],sendCd: [
          { required: true, message: '请输入发送冷却时间', trigger: 'blur' }
        ],gapTime:[
        { required: true, message: '请输入账号发送间隔', trigger: 'blur' }
        ]
      }
    }
  },
  created(){
    if(this.projectId){
      this.getProjectOptions()
    }
  },
  methods: {
    getProjectOptions(){
      getProjectSetByProjectId(this.projectId).then(res=>{
        if(res.code === 200){
          this.strategyForm=res.data
        }
      })
    },
    handleSaveStrategy() {
      this.$refs.strategyForm.validate((valid) => {
        if (valid) {
          updateProjectSet(this.strategyForm).then(res=>{
            if(res.code === 200){
              this.$modal.msgSuccess('保存成功')
              this.getProjectOptions()
            }
          })
          // TODO: 调用保存策略的API
        }
      })
    },
    resetForm() {
      this.$refs.strategyForm.resetFields()
    },
    handleClearSystem() {
      this.$confirm(`请输入"<span style="color: red;user-select:none">确定删除</span>"进行永久清除所有数据并且无法恢复, 是否继续?`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        dangerouslyUseHTMLString: true,
        showInput: true,
        inputPlaceholder: '请输入"确定删除"以确认操作',
        inputPattern: /^确定删除$/,
        inputErrorMessage: '请输入正确的确认文本',
        closeOnClickModal: false,
        closeOnPressEscape: false,
        beforeClose: (action, instance, done) => {
          if (action === 'confirm' && instance.inputValue !== '确定删除') {
            instance.$refs.input.$el.querySelector('input').style.borderColor = '#ff4949';
            return;
          }
          if (action === 'cancel') {
            done();
          } else if (action === 'confirm' && instance.inputValue === '确定删除') {
            done();
          }
        }
      }).then(({ value }) => {
        if (value === '确定删除') {
          deleteSys().then(res => {
            if (res.code === 200) {
              this.$message({
                type: 'success',
                message: '系统数据已清空'
              });
            }
          });
        }
      }).catch(() => {});
    }
  }
}
</script>

<style scoped>
.box-card {
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.el-form {
  max-width: 800px;
  margin: 0 auto;
}

.el-input, .el-select {
  width: 100%;
}
</style>
