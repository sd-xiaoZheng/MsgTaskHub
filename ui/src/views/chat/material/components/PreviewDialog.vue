<template>
  <el-dialog :title="title" :visible.sync="dialogVisible" width="60%" >
    <div class="preview-content">
      <div class="operation-bar" style="margin-bottom: 10px;">
        <el-button type="primary" size="small" @click="handleAdd">{{ $t('material.preview.addContent') }}</el-button>
      </div>
      <right-toolbar @queryTable="getList" :search="false" style="margin-bottom: 20px;" ></right-toolbar>
      <el-table max-height="400" :data="contentList" v-loading="loading" style="width: 100%">
        <el-table-column :label="$t('material.preview.content')" prop="templateContent" show-overflow-tooltip>
        </el-table-column>
        <el-table-column :label="$t('material.preview.status')" prop="templateContent" show-overflow-tooltip>
          <template #default="scope">
            <!-- isUse是否已经使用了 -->
            <el-tag v-if="scope.row.isUse === 1" type="success">{{ $t('material.preview.used') }}</el-tag>
            <el-tag v-else type="danger">{{ $t('material.preview.unused') }}</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column :label="$t('material.preview.operation')" width="150" align="center">
          <template slot-scope="scope">
            <el-button type="text" @click="handleEdit(scope.row)">{{ $t('material.preview.edit') }}</el-button>
            <el-button type="text" class="text-danger" @click="handleDelete(scope.row)">{{ $t('material.preview.delete') }}</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button @click="cancel">{{ $t('material.preview.close') }}</el-button>
    </div>

    <!-- 编辑对话框 -->
    <el-dialog :title="$t('material.preview.editContent')" :visible.sync="editDialogVisible" width="50%">
      <el-form :model="editForm" ref="editForm" label-width="80px">
        <el-form-item :label="$t('material.preview.content')" prop="templateContent">
          <el-input type="textarea" v-model="editForm.templateContent" :rows="8" :placeholder="$t('material.preview.inputContent')"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">{{ $t('material.preview.cancel') }}</el-button>
        <el-button type="primary" @click="submitEdit">{{ $t('material.preview.confirm') }}</el-button>
      </div>
    </el-dialog>
  </el-dialog>
</template>

<script>
import { listSendContentTemplateContent,addSendContentTemplateContent, updateSendContentTemplateContent, delSendContentTemplateContent } from '@/api/chat/SendContentTemplateContent'

export default {
  name: 'PreviewDialog',
  components: {
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: '内容预览'
    },
    templateId: {
      type: [String, Number],
      default: null
    }
  },
  data() {
    return {
      editDialogVisible: false,
      editForm: {
        contentId: null,
        templateId: null,
        content: ''
      },
      // 遮罩层
      loading: true,
      // 总条数
      total: 0,
      // 表格数据
      contentList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 50,
        templateId: null
      }
    }
  },
  computed: {
    dialogVisible: {
      get() {
        return this.visible
      },
      set(val) {
        this.$emit('update:visible', val)
      }
    }
  },
  watch: {
    templateId: {
      handler(val) {
        if (val) {
          this.queryParams.templateId = val
          this.getList()
        }
      },
      immediate: true
    }
  },
  methods: {

    handleAdd() {
      this.editForm = {
        contentId: null,
        templateId: this.templateId,
        templateContent: ''
      }
      this.editDialogVisible = true
    },
    getList() {
      this.loading = true
      listSendContentTemplateContent(this.queryParams).then(response => {
        this.contentList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    handleEdit(row) {
      this.editForm = {
        contentId: row.contentId,
        templateId: this.templateId,
        templateContent: row.templateContent
      }
      this.editDialogVisible = true
    },
    handleDelete(row) {
      this.$confirm(this.$t('material.preview.confirmDelete'), this.$t('material.preview.warning'), {
        confirmButtonText: this.$t('material.preview.confirm'),
        cancelButtonText: this.$t('material.preview.cancel'),
        type: 'warning'
      }).then(() => {
        delSendContentTemplateContent(row.contentId).then(response => {
          this.$modal.msgSuccess(this.$t('material.preview.deleteSuccess'))
          this.getList()
        })
      })
    },
    submitEdit() {
      if (this.editForm.contentId) {
        updateSendContentTemplateContent(this.editForm).then(response => {
          this.$modal.msgSuccess(this.$t('material.preview.editSuccess'))
          this.editDialogVisible = false
          this.getList()
        })
      } else {
        addSendContentTemplateContent(this.editForm).then(response => {
          this.$modal.msgSuccess(this.$t('material.preview.addSuccess'))
          this.editDialogVisible = false
          this.getList()
        })
      }
    },
    cancel() {
      this.dialogVisible = false
      this.$emit('cancel')
    }
  }
}
</script>


<style lang="scss" scoped>
.preview-content {
  // max-height: 500px;
  overflow-y: auto;
  padding: 10px;
}
</style>
