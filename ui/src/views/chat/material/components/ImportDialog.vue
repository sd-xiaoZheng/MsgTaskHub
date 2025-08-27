<template>
    <el-dialog :title="$t('importDialog.title')" :visible.sync="visible" width="500px" :close-on-click-modal="false"
        :show-close="false">
        <!-- 群发消息任务的表单 -->
        <el-form ref="form" v-if="type === 'massContent'" :model="form" :rules="rules" label-width="80px">
            <el-form-item :label="$t('importDialog.templateTitle')" prop="templateName">
                <el-input v-model="form.templateName" :placeholder="$t('importDialog.inputTemplateTitle')" />
            </el-form-item>
            <el-form-item :label="$t('importDialog.file')" prop="file">
                <el-upload ref="upload" :limit="1" :data="form" :headers="headers" accept=".xlsx,.xls"
                    :auto-upload="false" :action="uploadUrl" :on-success="handleUploadSuccess"
                    :on-error="handleUploadError">
                    <el-button slot="trigger" size="small" type="primary">{{ $t('importDialog.selectFile')
                        }}</el-button>
                    <div slot="tip" class="el-upload__tip">{{ $t('importDialog.fileTypeLimit') }}</div>
                </el-upload>
            </el-form-item>
        </el-form>

        <!-- 预设回复的表单 -->
        <el-form ref="presetForm" v-if="type === 'presetContent'" :model="presetForm" :rules="presetRules"
            label-width="80px">
            <el-form-item :label="$t('material.form.replyTitle')" prop="title">
                <el-input v-model="presetForm.title" :placeholder="$t('importDialog.inputTemplateTitle')" />
            </el-form-item>
            <el-form-item :label="$t('material.form.replyType')" prop="replyWeight">
                <el-select v-model="presetForm.replyWeight" :placeholder="$t('material.form.replyTypePlaceholder')"
                    clearable size="small">
                    <el-option v-for="dict in dict.type.reply_type" :key="dict.value" :label="dict.label"
                        :value="parseInt(dict.value)" />
                </el-select>
            </el-form-item>
            <el-form-item :label="$t('importDialog.file')">
                <el-upload ref="presetUpload" :limit="1" :data="presetForm" :headers="headers" accept=".xlsx,.xls"
                    :auto-upload="false" :action="uploadPresetUrl" :on-success="handleUploadSuccess"
                    :on-error="handleUploadError">
                    <el-button slot="trigger" size="small" type="primary">{{ $t('importDialog.selectFile')
                        }}</el-button>
                    <div slot="tip" class="el-upload__tip">{{ $t('importDialog.fileTypeLimit') }}</div>
                </el-upload>
            </el-form-item>
        </el-form>

        <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click="submitForm">{{ $t('importDialog.upload') }}</el-button>
            <el-button @click="cancel">{{ $t('importDialog.cancel') }}</el-button>
        </div>
    </el-dialog>
</template>

<script>
import { mapGetters } from 'vuex'
import { getToken } from "@/utils/auth";
export default {
    name: 'ImportDialog',
    props: ["visible", "type"],
    dicts: ['reply_type'],
    data() {
        return {
            title: '导入模板',
            projectOptions: [],
            headers: {
                Authorization: "Bearer " + getToken()
            },
            uploadUrl: process.env.VUE_APP_BASE_API + "/chat/SendContentTemplate/import",
            uploadPresetUrl: process.env.VUE_APP_BASE_API + "/chat/PresetReply/import",
            form: {
                projectId: undefined,
                templateName: '',
            },presetForm: {
                projectId: undefined,
                title: '',
                replyWeight:""
            },
            rules: {
                templateName: [
                    { required: true, message: '请输入模板标题', trigger: 'blur' }
                ],
            },
            presetRules: {
                title: [
                    { required: true, message: '请输入预设标题', trigger: 'blur' }
                ],  replyWeight: [
                    { required: true, message: '请选择回复类型', trigger: 'blur' }
                ],
            }
        }
    },
    computed: {
        ...mapGetters([
            'projectId', 'projectList'])
    },
    created() {
    },
    methods: {
        handleUploadSuccess(response, file, fileList) {
            if (response.code === 200) {
                this.$modal.msgSuccess('上传成功');
                this.resetForm();
                this.$emit('success');
            } else {
                this.$modal.msgError(response.msg);
            }
        },
        handleUploadError(err) {
            this.$modal.msgError('上传失败，请重试');
            if(this.type === 'massContent')
                this.$refs.upload.clearFiles()
            else
                this.$refs.presetUpload.clearFiles()
            console.error('文件上传失败:', err);
        },
        submitForm() {
            if (this.type === 'massContent') {
                this.form.projectId = this.projectId
                this.$refs['form'].validate(valid => {
                    if (valid) {
                        this.$refs['upload'].submit()
                    }
                })
            } else {
                this.presetForm.projectId = this.projectId
                this.$refs['presetForm'].validate(valid => {
                    if (valid) {
                        this.$refs['presetUpload'].submit()
                    }
                })
            }

        },
        cancel() {
            this.resetForm()
            this.$emit('cancelImport')
        },
        resetForm() {
            this.form = {
                projectId: null,
                templateName: '',
            }
            if (this.$refs.form) {
                this.$refs.form.resetFields()
            }
            if (this.$refs.upload) {
                this.$refs.upload.clearFiles()
            }

            if (this.$refs.presetForm) {
                this.$refs.presetForm.resetFields()
            }

            if (this.$refs.presetUpload) {
                this.$refs.presetUpload.clearFiles()
            }
        }
    }
}
</script>
