<template>
    <!-- 项目导入对话框 -->
    <el-dialog title="账号txt导入" :close-on-press-escape="false" :show-close="false" :close-on-click-modal="false" :visible.sync="dialogValue" width="400px">
        <el-upload ref="upload" :limit="1" accept=".txt" :headers="upload.headers" :action="upload.url"
            :data="uploadForm" :disabled="upload.isUploading" :on-progress="handleFileUploadProgress"
            :on-success="handleFileSuccess" :auto-upload="false" drag>
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <div class="el-upload__tip text-center" slot="tip">
                <span>仅允许导入txt格式文件。</span>
                <!-- <el-link type="primary" :underline="false" style="font-size: 12px; vertical-align: baseline"
            @click="importTemplate">下载模板</el-link> -->
            </div>
        </el-upload>
        <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click="submitFileForm">确 定</el-button>
            <el-button @click="$emit('close')">取 消</el-button>
        </div>
    </el-dialog>
</template>

<script>
import { getToken } from "@/utils/auth";

export default {
    props: {
        uploadForm: {
            type: Object,
            default: () => { }
        },
        dialogValue: {
            type: Boolean,
            default: false
        }
    },

    data() {
        return {
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
                url: process.env.VUE_APP_BASE_API + "/chat/Accounts/importAccountTxt"
            },
        }
    },
    methods: {
        // 文件上传中处理
        handleFileUploadProgress(event, file, fileList) {
            this.upload.isUploading = true;
        },
        // 提交上传文件
        submitFileForm() {
            this.$refs.upload.submit();
        },
        // 文件上传成功处理
        handleFileSuccess(response, file, fileList) {
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
            this.$emit('success')
        },
    }
}
</script>

<style lang="scss"></style>