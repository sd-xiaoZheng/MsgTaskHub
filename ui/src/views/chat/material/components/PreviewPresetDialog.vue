<template>
    <el-dialog :title="title" :visible.sync="dialogVisible" width="60%">
        <div class="preview-content">
            <right-toolbar @queryTable="getList" :search="false" style="margin-bottom: 20px;"></right-toolbar>
            <el-table :data="contentList" center max-height="400">
                <el-table-column label="预设标题" width="150" prop="replyTitle"></el-table-column>
                <el-table-column label="回复类型" width="120" align="center" prop="replyWeight">
                    <template slot-scope="scope">
                        <dict-tag :options="dict.type.reply_type" :value="scope.row.replyWeight" />
                    </template>
                </el-table-column>
                <el-table-column label="预设内容" prop="replyContent">
                    <template #default="scope">
                        <!-- 省略号 -->
                        <span v-if="scope.row.replyContent.length > 100">{{ scope.row.replyContent.slice(0, 100)
                            }}...</span>
                        <span v-else>{{ scope.row.replyContent }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="创建时间" prop="createTime"></el-table-column>
                <el-table-column label="操作" width="150">
                    <template slot-scope="scope">
                        <el-button type="text" @click="handlePresetEdit(scope.row)">编辑</el-button>
                        <el-button type="text" class="text-danger" @click="handlePresetDelete(scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum"
                :limit.sync="queryParams.pageSize" @pagination="getList" />
        </div>
        <div slot="footer" class="dialog-footer">
            <el-button @click="cancel">{{ $t('material.preview.close') }}</el-button>
        </div>

        <!-- 编辑对话框 -->
        <el-dialog title="编辑预设回复" :visible.sync="editDialogVisible" width="600px">
            <el-form ref="presetForm" :model="presetForm" :rules="presetRules" label-width="80px">
                <el-form-item :label="$t('material.form.replyTitle')" prop="replyTitle">
                    <el-input v-model="presetForm.replyTitle"
                        :placeholder="$t('material.form.replyTitlePlaceholder')" />
                </el-form-item>
                <el-form-item :label="$t('material.form.replyType')" prop="replyWeight">
                    <el-select v-model="presetForm.replyWeight" :placeholder="$t('material.form.replyTypePlaceholder')"
                        clearable size="small">
                        <el-option v-for="dict in dict.type.reply_type" :key="dict.value" :label="dict.label"
                            :value="parseInt(dict.value)" />
                    </el-select>
                </el-form-item>
                <el-form-item :label="$t('material.form.replyContent')" prop="replyContent">
                    <el-input type="textarea" v-model="presetForm.replyContent" :rows="6"
                        :placeholder="$t('material.form.replyContentPlaceholder')" />
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="presetDialog.visible = false">{{ $t('material.buttons.cancel') }}</el-button>
                <el-button type="primary" @click="submitPreset">{{ $t('material.buttons.confirm') }}</el-button>
            </div>
        </el-dialog>
    </el-dialog>
</template>

<script>
import { listPresetReply, addPresetReply, updatePresetReply, delPresetReply } from '@/api/chat/PresetReply'
import { mapGetters } from 'vuex'
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
        replyWeight: {
            type: [String, Number],
            default: null
        }
    },
    dicts: ['reply_type'],
    computed: {
        ...mapGetters(['projectId'])
    },
    data() {
        return {
            // 预设回复表单验证规则
            presetRules: {
                replyTitle: [
                    { required: true, message: this.$t('material.form.replyTitlePlaceholder'), trigger: 'blur' }
                ],
                replyWeight: [
                    { required: true, message: this.$t('material.form.replyTypePlaceholder'), trigger: 'change' }
                ],
                replyContent: [
                    { required: true, message: this.$t('material.form.replyContentPlaceholder'), trigger: 'blur' }
                ]
            },
            editDialogVisible: false,
            presetForm: {
                replyId: null,
                title: '',
                replyWeight: '',
                projectId: this.projectId,
                replyContent: '',
                replyTitle: ''
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
                replyWeight: null
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
        replyWeight: {
            handler(val) {
                if (val) {
                    this.queryParams.replyWeight = val
                    this.getList()
                }
            },
            immediate: true
        }
    },
    methods: {

        // 提交预设回复
        submitPreset() {
            this.$refs.presetForm.validate(valid => {
                if (valid) {
                    const isEdit = this.presetForm.replyId !== null;
                    this.presetForm.projectId = this.projectId
                    const request = isEdit ? updatePresetReply(this.presetForm) : addPresetReply(this.presetForm);
                    request.then(response => {
                        this.$modal.msgSuccess(isEdit ? '修改成功' : '新增成功');
                        this.editDialogVisible = false;
                        this.getList();
                    }).catch(error => {
                        console.error('操作失败：', error);
                    });
                }
            });
        },
        reset() {
            this.presetForm = {
                replyId: null,
                title: '',
                replyWeight: '',
                projectId: this.projectId,
                replyContent: '',
                replyTitle: ''
            }
        },
        getList() {
            this.loading = true
            listPresetReply(this.queryParams).then(response => {
                this.contentList = response.rows
                this.total = response.total
                this.loading = false
            })
        },

        // 编辑预设回复
        handlePresetEdit(row) {
            this.presetForm = { ...row }
            this.editDialogVisible = true
        },
        handlePresetDelete(row) {
            this.$confirm(this.$t('material.preview.confirmDelete'), this.$t('material.preview.warning'), {
                confirmButtonText: this.$t('material.preview.confirm'),
                cancelButtonText: this.$t('material.preview.cancel'),
                type: 'warning'
            }).then(() => {
                delPresetReply(row.replyId).then(response => {
                    this.$modal.msgSuccess(this.$t('material.preview.deleteSuccess'))
                    this.getList()
                })
            })
        },
        submitEdit() {
            if (this.editForm.replyId) {
                updatePresetReply(this.editForm).then(response => {
                    this.$modal.msgSuccess(this.$t('material.preview.editSuccess'))
                    this.editDialogVisible = false
                    this.getList()
                })
            } else {
                addPresetReply(this.editForm).then(response => {
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