<template>
  <div class="app-container">
    <!-- 顶部操作栏 -->
    <el-row :gutter="20" class="mb-4">
      <el-col :span="24">
        <el-card shadow="never">
          <el-tabs v-model="activeTab" @tab-click="handleTabClick">
            <!-- 批量群发内容操作 -->
            <el-tab-pane :label="$t('material.tabs.massContent')" name="massContent">
              <div class="tab-content">
                <el-button type="success" @click="handleMassImport">{{ $t('material.buttons.import') }}</el-button>
                <!-- <el-button type="success" @click="handleMassExport">{{ $t('material.buttons.export') }}</el-button> -->
                <!-- <el-button type="danger" @click="handleMassDelete">{{ $t('material.buttons.delete') }}</el-button> -->
                <el-button type="danger" @click="handleMassEmpty">{{ $t('material.buttons.empty') }}</el-button>
              </div>
            </el-tab-pane>
            <!-- 预设回复操作 -->
            <el-tab-pane :label="$t('material.tabs.presetContent')" name="presetContent">
              <div class="tab-content">
                <el-button type="success" @click="handlePresetImport">{{ $t('material.buttons.importPrset') }}</el-button>
                <!-- <el-button type="success" @click="handlePresetExport">{{ $t('material.buttons.exportPreset') }}</el-button> -->
                <!-- <el-button type="danger" @click="handlePresetDelete">{{ $t('material.buttons.deletePreset') }}</el-button> -->
                <el-button type="danger" @click="handlePresetEmpty">{{ $t('material.buttons.empty') }}</el-button>
              </div>
            </el-tab-pane>
            <!-- 批量生成内容操作 -->
            <el-tab-pane :label="$t('material.tabs.generateContent')" name="generateContent">
              <div class="tab-content">
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>

    <!-- 内容展示区域 -->
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card shadow="never">
          <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch && activeTab != 'generateContent'" >
            <template v-if="activeTab === 'massContent'">
              <el-form-item :label="$t('material.form.templateName')" prop="templateName">
                <el-input v-model="queryParams.templateName" :placeholder="$t('material.form.templateNamePlaceholder')" clearable size="small"
                  @keyup.enter.native="handleQuery" />
              </el-form-item>
            </template>
            <template v-if="activeTab === 'presetContent'"> 
              <el-form-item :label="$t('material.form.replyType')" prop="replyWeight">
                <el-select v-model="queryParams.replyWeight" :placeholder="$t('material.form.replyTypePlaceholder')" clearable size="small">
                  <el-option v-for="dict in dict.type.reply_type" :key="dict.value" :label="dict.label"
                    :value="parseInt(dict.value)" />
                </el-select>
              </el-form-item>
            </template>
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ $t('material.buttons.search') }}</el-button>
              <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ $t('material.buttons.reset') }}</el-button>
            </el-form-item>
          </el-form>
          <right-toolbar  v-show="activeTab != 'generateContent'"  @queryTable="getList" style="margin-bottom: 20px;" :search="false"></right-toolbar>
          <!-- 批量群发内容列表 -->
          <div v-show="activeTab === 'massContent'">
            <el-table v-loading="loading" :data="massContentList" > 
              <el-table-column :label="$t('material.table.contentTitle')" prop="templateName"></el-table-column>
              <el-table-column :label="$t('material.table.contentPreview')" prop="content">
                <template slot-scope="scope">
                  <el-button type="text" @click="handlePreviewContent(scope.row)">{{ $t('material.buttons.preview') }}</el-button>
                </template>
              </el-table-column>
              <el-table-column :label="$t('material.table.total')" prop="total"></el-table-column>
              <el-table-column :label="$t('material.table.used')" prop="use"></el-table-column>
              <el-table-column :label="$t('material.table.remaining')" prop="remain"></el-table-column>
              <el-table-column :label="$t('material.table.createTime')" prop="createTime"></el-table-column>
              <el-table-column :label="$t('material.table.operations')" width="150">
                <template slot-scope="scope">
                  <el-button type="text" class="text-reset"
                    @click="handleResetData(scope.row.templateId)">{{ $t('material.buttons.resetData') }}</el-button>
                  <el-button type="text" @click="handleMassEdit(scope.row)">{{ $t('material.buttons.edit') }}</el-button>
                  <el-button type="text" class="text-danger" @click="handleMassDelete(scope.row)">{{ $t('material.buttons.delete') }}</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <!-- 预设回复内容列表 -->
          <div v-show="activeTab === 'presetContent'">
            <el-table :data="presetContentList" center >
              <el-table-column label="回复类型" align="center" prop="replyWeight">
                <template slot-scope="scope">
                  <dict-tag :options="dict.type.reply_type" :value="scope.row.replyWeight" />
                </template>
              </el-table-column> 
              <el-table-column :label="$t('material.table.contentPreview')" prop="content">
                <template slot-scope="scope">
                  <el-button type="text" @click="handlePreviewPreset(scope.row)">{{ $t('material.buttons.preview') }}</el-button>
                </template>
              </el-table-column>
              <el-table-column label="预设总数" prop="num"></el-table-column>
              <el-table-column label="操作" width="150">
                <template slot-scope="scope">
                  <el-button type="text" class="text-danger" @click="handlePresetDelete(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <!-- 批量生成内容列表 -->
          <div v-show="activeTab === 'generateContent'"> 
            <generate-layout>

            </generate-layout>
          </div>

          <!-- 分页 -->
          <pagination v-show="total > 0 && activeTab != 'generateContent'" :total="total" :page.sync="queryParams.pageNum"
            :limit.sync="queryParams.pageSize" @pagination="getList" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 预览弹窗 -->
    <PreviewDialog :title="previewTitle" :templateId="previewId" :visible.sync="previewVisible"
      :content-list="previewContentList" />

    <PreviewPresetDialog :title="presetTitle" :replyWeight="presetReplyWeight" :visible.sync="presetVisible" :content-list="presetContentList"  />

      <!-- 导入 -->
    <ImportDialog :visible="import_status" :type="activeTab" @success="handleSuccessImport" @cancelImport="handleCancelImport" />
 

    <!-- 编辑弹窗 -->
    <el-dialog :title="$t('material.dialog.editTitle')" :visible.sync="editDialogVisible" width="500px">
      <el-form ref="editForm" :model="editForm" :rules="editRules" label-width="80px">

        <el-form-item :label="$t('material.dialog.titleLabel')" prop="templateName">
          <el-input v-model="editForm.templateName" :placeholder="$t('material.dialog.titlePlaceholder')" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">{{ $t('material.buttons.cancel') }}</el-button>
        <el-button type="primary" @click="submitEdit">{{ $t('material.buttons.confirm') }}</el-button>
      </div>
    </el-dialog> 
  </div>
</template>

<script>
import GenerateLayout from './layout/GenerateLayout.vue'
import ImportDialog from './components/ImportDialog.vue'
import PreviewDialog from './components/PreviewDialog.vue'
import PreviewPresetDialog from './components/PreviewPresetDialog.vue'
import { mapGetters } from 'vuex'
import { listSendContentTemplate, resetByTemplateId, addSendContentTemplate, updateSendContentTemplate, deleteByTemplate,emptySendContentTemplate } from '@/api/chat/SendContentTemplate'
import { listCountPreset,emptyPresetReply, addPresetReply, updatePresetReply, deleteByWeight } from '@/api/chat/PresetReply'

export default {
  name: 'Material',
  components: { ImportDialog, PreviewDialog,PreviewPresetDialog,GenerateLayout },
  dicts: ['reply_type'],
  computed: {
    ...mapGetters([
      'projectId', 'projectList'
    ])
  },
  data() {
    return {
      // 遮罩层
      loading: false,
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
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 50,
        templateName: "",
        replyWeight: null,
        replyTitle: "",
        createTime: undefined
      },
      // 激活的标签页
      activeTab: 'massContent',
      import_status: false, 
      // 总条数
      total: 0,
      // 批量群发内容列表
      massContentList: [],
      // 预设回复内容列表
      presetContentList: [], 
      // 预览弹窗
      previewVisible: false,
      previewTitle: '',
      previewId: "",
      previewContent: '',
      previewContentList: [], // 新增预览内容列表
      // 预设回复的预览
      presetTitle:"",
      presetVisible:false,
      presetContentList:[],
      presetReplyWeight:"", 
      // 编辑弹窗
      editDialogVisible: false,
      // 编辑的表单数据
      editForm: {
        templateId: '',
        templateName: '',
        projectId: ''
      },
      // 表单校验规则
      editRules: {
        templateName: [
          { required: true, message: this.$t('material.form.templateNamePlaceholder'), trigger: 'blur' }
        ],
        projectId: [
          { required: true, message: this.$t('material.form.selectProject'), trigger: 'change' }
        ]
      },  
     
    }
  },
  watch: {
    projectId: {
      handler(newVal) {
        this.queryParams.projectId = newVal 
        if (newVal) {
          this.getList()
        }
      },
      immediate: true
    }
  },
  methods: {
    // 清空内容
    handleMassEmpty(){
      this.$confirm("是否清空所有数据？", "提示", {
        confirmButtonText:"确定",
        cancelButtonText: "取消",
        type: 'warning'
      }).then(async () => {
        try {
          const response = await emptySendContentTemplate(this.projectId);
          if (response.code === 200) {
            this.$message.success("清空成功");
            this.getList();
          } else {
            this.$message.error(response.msg);
          }
        } catch (error) {
          console.error('清空内容失败：', error);
        }
      }).catch(() => {
        // 用户取消操作
      });
    },
     // 清空内容
     handlePresetEmpty(){
      this.$confirm("是否清空所有数据？", "提示", {
        confirmButtonText:"确定",
        cancelButtonText: "取消",
        type: 'warning'
      }).then(async () => {
        try {
          const response = await emptyPresetReply(this.projectId);
          if (response.code === 200) {
            this.$message.success("清空成功");
            this.getList();
          } else {
            this.$message.error(response.msg);
          }
        } catch (error) {
          console.error('清空内容失败：', error);
        }
      }).catch(() => {
        // 用户取消操作
      });
    },
    // tab切换事件
    handleTabClick(tab) {
      this.queryParams.page = 1
      this.getList()
    },
    async getPresetList() {
      try {
        this.loading = true;
        const response = await listCountPreset(this.queryParams);
        this.presetContentList = response.data || [];
        this.total = 0;
      } catch (error) {
        console.error('获取预设回复列表失败：', error);
      } finally {
        this.loading = false;
      }
    }, 
    // 删除预设回复
    handlePresetDelete(row) { 
      const data={weight:row.replyWeight,projectId:this.projectId}
      this.$modal.confirm('是否确认删除当前预设类型？').then(() => {
        return deleteByWeight(data);
      }).then((res) => {
        if(res.code === 200){
          this.$modal.msgSuccess('删除成功');
        }
        this.getList();
      }).catch(() => { });
    },
    // 搜索按钮操作
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    // 重置按钮操作
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 获取模板列表
    async getTemplateList() {
      if (!this.queryParams.projectId) return;
      try {
        this.loading = true;
        const response = await listSendContentTemplate(this.queryParams);
        if (response.rows) {
          this.massContentList = response.rows.map(item => {
            let use = item.useNum.split("/")[0]
            let total = item.useNum.split("/")[1]
            item.use = use
            item.total = total
            // 剩余使用量
            item.remain = total - use
            return item
          });
        } else
          this.massContentList = response.rows || [];

        this.total = response.total;
      } catch (error) {
        console.error('获取模板列表失败：', error);
      } finally {
        this.loading = false;
      }
    },
    // 获取列表数据
    async getList() {
      this.queryParams.projectId = this.projectId;
      this.loading = true;
      try {
        switch (this.activeTab) {
          case 'massContent':
            // 获取批量群发内容列表
            this.getTemplateList();
            break;

          case 'presetContent':
            // 获取预设回复内容列表
            this.getPresetList();
            break;

          case 'generateContent':
            // 获取批量生成内容列表
            
            break;
        }
      } catch (error) {
        console.error('获取列表数据失败：', error);
      } finally {
      }
    },
    // 批量群发内容相关方法
    handleMassImport() { 
      this.import_status = true
    },
    handlePresetImport(){ 
      this.import_status = true
    },
    // 一键重置数据 
    handleResetData(id) {
      // TODO询问
      this.$confirm('是否确认重置数据？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 重置数据
        resetByTemplateId(id).then(res => {
          this.handleQuery()
          if (res.code == 200) {
            this.$modal.msgSuccess('重置成功')
          } else {
            this.$modal.msgError(res.msg)
          }
        })
      }).catch(() => {
        // 取消操作
      })
    },
    handleMassEdit(row) {
      this.editForm.templateId = row.templateId
      this.editForm.templateName = row.templateName
      this.editForm.projectId = row.projectId
      this.editDialogVisible = true
    },
    submitEdit() {
      this.$refs.editForm.validate(valid => {
        if (valid) {
          try {
            if (this.editForm.templateId) {
              updateSendContentTemplate(this.editForm).then(response => {
                this.$modal.msgSuccess('修改成功')
                this.editDialogVisible = false
                this.getList()
              })
            } else {
              addSendContentTemplate(this.editForm).then(response => {
                this.$modal.msgSuccess('新增成功')
                this.editDialogVisible = false
                this.getList()
              })
            }
          } catch (error) {
            console.error('操作失败：', error)
          }
        }
      })
    },
    handleMassDelete(row) {
      const templateIds = row.templateId  
      if (!templateIds) return

      this.$modal.confirm('是否确认删除当前数据项？').then(() => {
        return deleteByTemplate(templateIds)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      }).catch(() => { })
    }, 

    handlePresetExport() {
      this.$modal.msgSuccess('导出预设功能待实现')
    }, 

    // 批量生成内容相关方法
    handleGenerate() {
      this.$modal.msgSuccess('生成内容功能待实现')
    },
    handleGenerateExport() {
      this.$modal.msgSuccess('导出内容功能待实现')
    },
    handleGenerateEdit(row) {
      this.$modal.msgSuccess('编辑功能待实现')
    },
    handleGenerateDelete(row) {
      this.$modal.msgSuccess('删除功能待实现')
    },
    handleGenerateSelectionChange(selection) {
      this.selectedIds = selection.map(item => item.id)
    },
    handlePreviewPreset(row){ 
      this.presetTitle =this.dict.type.reply_type.filter(item=>item.value == row.replyWeight).map(item=>item.label)[0]
      this.presetReplyWeight = row.replyWeight 
      this.presetVisible = true
    },
    // 预览内容
     handlePreviewContent(row) {
      this.previewTitle = row.templateName
      this.previewId = null
      this.previewId = row.templateId
      this.previewVisible = true
    },
    handleCancelImport() {
      this.import_status = false
    },
    handleSuccessImport() {
      this.handleCancelImport()
      this.getList()
    },
  }
}
</script>

<style lang="scss" scoped>
.app-container {
  padding: 0px;
  .mb-4 {
    margin-bottom: 1rem;
  }

  .tab-content {
    margin-top: 1rem;
  }

  .text-danger {
    color: #F56C6C;
  }

  .text-reset {
    color: #ffc640;
  }

  .preview-content {
    max-height: 500px;
    overflow-y: auto;
    padding: 20px;
    line-height: 1.6;
    font-size: 14px;
    color: #333;
    background-color: #f8f9fa;
    border-radius: 4px;
  }
}
</style>
