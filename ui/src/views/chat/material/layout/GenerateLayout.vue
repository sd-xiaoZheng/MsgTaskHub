<template>
    <div class="genLayout">
        <!-- TODO: 润色文件:是否随机首字母大小写,空格替换字符(逗号隔开): _,-,|,+   上传文件 -->
        <div class="genLayout-header">
            <!-- 头部包含：模板名称，自定义模板 -->
            <div style="display: flex; align-items: center;justify-content: flex-start;margin-bottom: 20px;">
                <el-radio-group v-model="model">
                    <el-radio :label="0" border>模板选择</el-radio>
                    <el-radio :label="1" border>创建模板</el-radio>
                </el-radio-group>
                <el-button type="primary" @click="handleOpenTemplate" style="margin-left: 40px;" size="medium"
                    icon="el-icon-setting">
                    模板管理
                </el-button>
                <el-button type="primary" @click="handleOpenRetouch" style="margin-left: 40px;" size="medium"
                    icon="el-icon-s-opportunity">
                    润色
                </el-button>
            </div>
            <el-form inline>
                <el-form-item label="模板" v-if="model === 0">
                    <el-select placholder="请选择模板" v-model="templateId">
                        <el-option v-for="item in templateList" :label="item.generateName" :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="模板名称" v-if="model === 1">
                    <el-input placeholder="请输入模板名称" v-model="generateName">

                    </el-input>
                </el-form-item>

                <el-form-item v-if="model === 1">
                    <el-button type="primary" @click="submitFormGenTemplate">
                        创建
                    </el-button>
                </el-form-item>

            </el-form>
        </div>

        <div class="genlayout-footer">
            <div style="margin-bottom: 10px;">
                表情：
                <el-switch v-model="isEmoji" active-color="#13ce66" inactive-color="#ff4949" :active-value="1"
                    :inactive-value="0">
                </el-switch>
            </div>
            <div style="margin-bottom: 10px;" v-if="isEmoji">
                表情排序(-1为随机)：
                <el-input-number placeholder="请输入排序" :min="-1" :max="9999" v-model="emojiSort">
                </el-input-number>
            </div>

            <div>
                <el-button type="primary" @click="handleOpenGenerateXLS">
                    生成导出XLS
                </el-button>
                <el-button type="warning" @click="handleGenerateText(2)">
                    生成保存模板
                </el-button>
            </div>
        </div>


        <el-divider content-position="center">自定义项</el-divider>
        <!-- 自定义 -->
        <div>
            <template v-if="templateId === null">
                <div class="genlayout-templatenone">
                    <div class="icon">
                        <i class="el-icon-document-remove"></i>
                    </div>
                    <div class="title">
                        请先选择模板
                    </div>
                </div>
            </template>
            <template v-else>
                <div class="genlayout-list">
                    <div class="genlayout-item">
                        <!-- 排序 -->
                        <div class="genlayout-item-order">
                            排序
                        </div>
                        <div class="genlayout-item-name">
                            项名
                        </div>

                        <div class="genlayout-item-text">
                            话术管理
                        </div>

                        <div class="genlayout-item-tool">
                            操作
                        </div>
                    </div>

                    <div class="genlayout-item" v-for="item in middleList" :key="item.id">
                        <!-- 排序 -->
                        <div class="genlayout-item-order">
                            <el-button v-if="!item.sortStaus" @click="item.sortStaus = true" type="text">
                                {{ item.sort }}
                                <i class="el-icon-edit el-icon--right"></i>
                            </el-button>

                            <div v-if="item.sortStaus"
                                style="display: flex;align-items: center;justify-content: center;width: 100%;">
                                <el-input-number style="flex:1;" v-model="item.sort" size="mini"
                                    controls-position="right" :min="1" :max="999">
                                </el-input-number>
                                <el-button type="primary" size="mini" icon="el-icon-check"
                                    @click="handleChangeSort(item)">
                                </el-button>
                            </div>

                        </div>
                        <div class="genlayout-item-name">
                            <el-button v-if="!item.templateNameStaus" type="text"
                                @click="item.templateNameStaus = true">
                                {{ item.templateName }}
                                <i class="el-icon-edit el-icon--right"></i>
                            </el-button>

                            <div v-if="item.templateNameStaus"
                                style="display: flex;align-items: center;justify-content: center;width: 100%;">
                                <el-input v-model="item.templateName" style="flex:1;" size="mini">
                                </el-input>
                                <el-button type="primary" size="mini" icon="el-icon-check"
                                    @click="handleChangeNum(item)">
                                </el-button>
                            </div>
                        </div>

                        <div class="genlayout-item-text">
                            <!-- 话术管理 / 上传话术 -->
                            <template v-if="item.count === -1">
                                <div style="font-size: 14px;">
                                    暂无数据
                                </div>
                            </template>
                            <template v-else>
                                <el-button type="text" @click="handleOpenGenerateText(item)">
                                    话术管理({{ item.count }}条)
                                </el-button>
                            </template>

                            <el-upload v-if="item.flag == void 0" :action="uploadOptions.action"
                                :headers="uploadOptions.headers" :show-file-list="false" :data="{ tlmId: item.id }"
                                class="upload-demo" :on-success="handleUploadSuccess">
                                <el-button size="mini" type="success" icon="el-icon-upload">
                                </el-button>
                            </el-upload>
                        </div>

                        <div class="genlayout-item-tool">

                            <el-button size="mini" type="danger" @click="handleDeleteGenItem(item)"
                                icon="el-icon-delete">
                            </el-button>
                        </div>
                    </div>
                </div>

                <el-button icon="el-icon-plus" style="width: 100%;margin-top: 10px;" @click="handleAddMiddle"
                    type="primary" plain>
                    新增项
                </el-button>
            </template>

        </div>

        <el-divider></el-divider>



        <!-- 话术内容管理 -->
        <GenerateText :dialog.sync="generateTextStatus" :tlmId="currentGenrateMiddleId" />
        <!-- 模板管理 -->
        <GenerateTemplateConfig @change="handleChangheGenerateTemplate" :dialog.sync="templateStatus" />

        <!-- 润色文件弹窗 -->
        <el-dialog :visible.sync="retouchStatus" title="润色" :close-on-click-modal="false">
            <el-form :model="retouchData" :rules="retouchRule" ref="retouchForm">
                <el-form-item label="文件">
                    <el-upload :file-list="retouchFileList" :on-change="handleChangeRetouchFile" :action="retouchUrl"
                        :http-request="handleUploadRetouch" :headers="uploadOptions.headers" :data="retouchData"
                        class="upload-demo" ref="retouchUploadRef" :limit="1" :auto-upload="false">
                        <el-button type="primary">
                            点击上传
                        </el-button>
                    </el-upload>
                </el-form-item>

                <el-form-item label="首字母随机大小写">
                    <el-switch active-color="#13ce66" inactive-color="#ff4949"
                        v-model="retouchData.isUppercase"></el-switch>
                </el-form-item>

                <el-form-item label="空格替换字符(逗号隔开)" prop="salt">
                    <el-input v-model="retouchData.salt" placeholder="请输入符号"></el-input>
                </el-form-item>
            </el-form>

            <template #footer>
                <el-button type="primary" @click="submitFormRetouch">
                    确定
                </el-button>
            </template>
        </el-dialog>

        <el-dialog :visible.sync="generateXlsStatus" title="导出XLS"  :close-on-click-modal="false" :close-on-press-escape="false">
            <el-form ref="xlsFormRef" :model="xlsForm" :rules="xlsFormRule">
                <el-form-item :label="xlsForm.isZip?'单个文件生成数量':'生成数量'" prop="generateNum">
                    <el-input placeholder="请输入数量" v-model="xlsForm.generateNum">
                    </el-input>
                </el-form-item>

                <el-form-item label="导出后缀">
                    <el-radio-group v-model="xlsForm.fileFlag">
                        <el-radio :label="1">
                            .excel
                        </el-radio>
                        <el-radio :label="2">
                            .txt
                        </el-radio>
                        <el-radio :label="3">
                            .csv
                        </el-radio>
                    </el-radio-group>
                </el-form-item>

                <el-form-item label="是否分包">
                    <el-switch v-model="xlsForm.isZip" @change="handleChangeIsZip">
                    </el-switch>
                </el-form-item>

                <el-form-item v-if="xlsForm.isZip" prop="zipFileItem" label="分包数量">
                    <el-input v-model="xlsForm.zipFileItem" placeholder="请输入分包数量">
                    </el-input>
                </el-form-item>
            </el-form>

            <template #footer>
                <el-button type="primary" @click="handleGenerateText(1)">
                    确定
                </el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script>
import { listGenerateTemplate, addGenerateTemplate, updateGenerateTemplate } from '@/api/chat/GenerateTemplate'
import { getToken } from "@/utils/auth";
import { listGenerateMiddle, delGenerateMiddle, updateGenerateMiddle, addGenerateMiddle } from '@/api/chat/GenerateMiddle'
import { genGenerateText } from '@/api/chat/GenerateText'
import { Loading } from 'element-ui';
import GenerateText from '../components/GenerateText.vue';
import GenerateTemplateConfig from '../components/GenerateTemplateConfig.vue';
import request from '@/utils/request';
export default {
    components: {
        GenerateText, GenerateTemplateConfig
    },
    data() {
        var vilidateGenerateNum = (rule, value, callbacK) => { 
            if (value <= 0) {
                callbacK(new Error("请输入大于0的数字"))
            } else if (value > 0 && value <= 200000) {
                callbacK()
            } else {
                callbacK(new Error("请输入1到200000的数字"))
            }
        }
        var vilidateZipFileItem = (rule, value, callbacK) => {
            if (value <= 0) {
                callbacK(new Error("请输入大于0的数字"))
            } else if (value > 0 && value <= 1000) {
                callbacK()
            } else {
                callbacK(new Error("请输入1到1000的数字"))
            }
        }
        return {
            // 模板管理状态
            templateStatus: false,
            // 模板选择 0 ，自定义 1
            model: 0,
            // 模板id
            templateId: null,
            templateName: "",
            templateList: [],
            middleList: [],
            uploadOptions: {
                action: process.env.VUE_APP_BASE_API + "/chat/GenerateText/uploadText",
                headers: {
                    Authorization: "Bearer " + getToken()
                },
            },
            generateName: "", 
            // 弹窗状态，查看话术管理的
            generateTextStatus: false,
            // 当前话模板项的id
            currentGenrateMiddleId: null,
            // 表情的参数  1添加, 0不添加
            isEmoji: 0,
            // 表情排序 
            emojiSort: 9999,
            // 润色弹窗
            retouchStatus: false,
            retouchData: {
                // 首字母随机大小写
                isUppercase: false,
                // 空格替换字符(逗号隔开)
                salt: "_,.,-,`,|"
            },
            retouchRule: {
                salt: [
                    { required: true, message: '请输入符号', trigger: 'blur' },
                ]
            },
            // 上传链接
            retouchUrl: "/chat/GenerateText/embellish",
            retouchLoading: null,
            retouchFileList: [],
            generateXlsStatus: false,
            xlsForm: {
                generateNum: 1000,
                zipFileItem: 10,
                isZip: true,
                fileFlag: 1 //1excle 2txt 3csv
            },
            xlsFormRule: {
                generateNum: [
                    { required: true, message: '请输入生成数量', trigger: 'blur' },
                    { validator: vilidateGenerateNum, trigger: 'blur' },
                ],
                zipFileItem: [
                    { required: true, message: '请输入生成数量', trigger: 'blur' },
                    { validator: vilidateZipFileItem, trigger: 'blur' },
                ]
            },
        }
    },
    mounted() {
        // 获取模板
        this.getTemplateList()
    },
    watch: {
        templateId: {
            handler(v) {
                if (v !== null) {
                    this.templateName = this.templateList.find(item => item.id === v).generateName
                    this.getTemplateMiddle(v)
                }
                else
                    this.middleList = []
            }
        },
        model: {
            handler(v) {
                if (v !== 0)
                    this.templateId = null
            }
        }
    },
    methods: {
        handleChangeIsZip(){
            console.log("开关值",this.xlsForm)
            if(this.xlsForm.isZip){
                this.xlsForm.zipFileItem = 10
            }
            else{
                this.xlsForm.zipFileItem = null
            }
        },
        handleOpenGenerateXLS() {
            this.generateXlsStatus = true
        },
        // 打开模板管理弹窗
        handleOpenTemplate() {
            this.templateStatus = true
        },
        // 在模板管理中增删改查了
        handleChangheGenerateTemplate() {
            this.getTemplateList()
            this.templateId = null
        },
        handleUploadSuccess(res) {
            if (res.code === 200) {
                this.$message.success("上传成功")
                this.getTemplateMiddle(this.templateId)
            }
        },
        handleOpenGenerateText(item) {
            this.generateTextStatus = true
            this.currentGenrateMiddleId = item.id
        },
        /**
         * 生成话术
         * @param type 1导出excel 2导入库
         */
        handleGenerateText(type) {
            if (this.templateId === null) {
                this.$message.error("请选择模板")
                return
            } 
            if (this.middleList.length === 0) {
                this.$message.error("请添加自定义项")
                return
            }

            const data = {
                generateTemplateId: this.templateId, 
                toExcel: type,
                isEmoji: this.isEmoji,
                emojiSort: this.emojiSort,
                ...this.xlsForm
            }
            if (data.toExcel === 1) {
                this.$refs.xlsFormRef.validate((valid) => {
                    if (valid) {
                        if(this.xlsForm.isZip &&( this.xlsForm.zipFileItem * this.xlsForm.generateNum >200000)){
                            this.$message.error("生成数量总数不能超过200000")
                            return
                        }
                        const loading = Loading.service({
                            text: "正在生成中..."
                        })
                        request({
                            url: "/chat/GenerateText/generate",
                            method: "post",
                            data,
                            responseType: "blob"
                        }).then(res => {
                            // const blob = new Blob([res], { type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8" });
                            const blob = new Blob([res]); 
                            const link = URL.createObjectURL(blob) 
                            const a = document.createElement("a")
                            // 生成姓名
                            const name = (() => {
                                let time = new Date()
                                let month = time.getMonth() + 1
                                let day = time.getDate()
                                let hour = time.getHours()
                                let minute = time.getMinutes()
                                return `${this.templateName}${month}${day}${hour}${minute}-${data.generateNum}${data.zipFileItem?'_'+data.zipFileItem:""}`
                            })()
                            const type = (() => {
                                if(data.isZip)
                                    return ".zip"
                                else if(data.fileFlag === 1)
                                    return ".xlsx"
                                else if(data.fileFlag === 2)
                                    return ".txt"
                                else
                                    return ".csv"
                            })()
                            a.href = link
                            a.download = name + type
                            a.click()
                            loading.close()
                            URL.revokeObjectURL(link)
                            this.generateXlsStatus = false
                            this.xlsForm.generateNum = 1000
                            this.xlsForm.zipFileItem = 10
                            this.xlsForm.isZip = true
                            this.xlsForm.fileFlag = 1
                        })
                    }
                })
            } else {
                this.$prompt('请输入生成数量', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    inputPattern: /^([1-9][0-9]{0,4}|1[0-9]{5}|200000)$/,
                    inputErrorMessage: '请输入正整数(最大值为20w)'
                }).then(({ value }) => {
                    const loading = Loading.service({
                        text: "正在生成中..."
                    })
                    data.generateNum = value
                    genGenerateText(data).then(res => {
                        this.$message.success("生成成功")
                        loading.close()
                    })
                }).catch(() => {
                });

            }
        },
        submitFormGenTemplate() {
            // 新增模板
            addGenerateTemplate({ generateName: this.generateName }).then(res => {
                if (res.code === 200) {
                    this.templateId = res.data
                    this.$message.success("创建成功")
                    this.getTemplateList()
                }
            })
        },
        getTemplateList() {
            listGenerateTemplate({ pageNum: 1, pageSize: 0 }).then(res => {
                this.templateList = res.rows
            })
        },
        getTemplateMiddle(tlId) {
            listGenerateMiddle({ pageNum: 1, pageSize: 0, tlId }).then(res => {
                res.rows.forEach(element => {
                    element.sortStaus = false
                    element.templateNameStaus = false
                });
                this.middleList = res.rows
            })
        },
        handleChangeSort(row) {
            this.submitFormMiddle(row, "sortStaus", false)
        },
        handleChangeNum(row) {
            this.submitFormMiddle(row, "templateNameStaus", false)
        },
        submitFormMiddle(row, key, value) {
            // 修改
            if (row.id != null) {
                updateGenerateMiddle(row).then(res => {
                    if (res.code === 200) {
                        row[key] = value
                    }
                })
            } else {
                // 新增
                addGenerateMiddle(row).then(res => {
                    if (res.code === 200) {
                        row[key] = value
                        row.id = res.data
                        row.flag = void 0
                    }
                })
            }
        },
        // 删除模板项
        handleDeleteGenItem(item) {
            if (item.id === null) {
                for (let i = 0; i < this.middleList.length; i++) {
                    if (this.middleList[i].flag === item.flag) {
                        this.middleList.splice(i, 1)
                        break
                    }
                }
                return
            }
            this.$confirm("是否永久删除此项？", "提示", {
                type: "warning",
            }).then(() => {
                delGenerateMiddle(item.id).then(res => {
                    if (res.code === 200)
                        this.getTemplateMiddle(this.templateId)
                })
            })

        },
        handleAddMiddle() {
            if (this.templateId) {
                //flag  随机id代表还没添加   void 0才是真的新增了 a.splice(1,1)
                // 随机
                const symbol = Symbol("flag")
                this.middleList.push({
                    id: null, templateName: "", templateNameStaus: true, sortStaus: true, sort: 0, tlId: this.templateId, count: -1, flag: symbol
                })
            } else {
                this.$message.warning("请先选择模板或自定义模板")
            }
        },
        // 打开润色文件弹窗
        handleOpenRetouch() {
            this.retouchStatus = true
        },
        submitFormRetouch() {
            this.$refs.retouchForm.validate(v => {
                if (v) {
                    if (this.retouchFileList.length === 0) {
                        this.$message.warning("请先上传文件")
                        return
                    }
                    this.retouchLoading = Loading.service({
                        text: "正在润色中..."
                    })
                    this.$refs.retouchUploadRef.submit()
                }
            })
        },
        handleChangeRetouchFile(file, fileList) {
            this.retouchFileList = fileList
        },
        handleUploadRetouch(e) {
            const formdata = new FormData()
            formdata.append("file", e.file)
            formdata.append("isUppercase", e.data.isUppercase)
            formdata.append("salt", e.data.salt)
            request({
                url: e.action,
                data: formdata,
                responseType: "blob",
                method: "post",
                headers: {
                    "content-type": "multipart/ form - data; boundary=----WebKitFormBoundarytYCYxpegSXifkJLx",
                    ...e.headers
                },
            }).then(res => {
                this.retouchLoading.close()
                this.retouchStatus = false
                this.retouchData.isUppercase = false
                this.retouchData.salt = "_,.,-,`,|"
                this.$refs.retouchUploadRef.clearFiles()
                this.retouchFileList = []
                const blob = new Blob([res], { type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8" });
                const link = URL.createObjectURL(blob)
                const a = document.createElement("a")
                a.href = link
                a.download = "润色文件.xlsx"
                a.click()
            })
        },
    }
}
</script>


<style lang="scss">
.genlayout-templatenone {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    color: #4e4e4e;
    font-size: 12px;

    .icon {
        font-size: 50px;
    }
}

.genlayout-list {
    .genlayout-item {
        display: grid;
        grid-template-columns: 150px 200px 1fr 250px;
        padding: 3px 0px;
        transition: all 0.3s;
        color: rgb(105, 105, 105);

        >div {
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .genlayout-item-text {
            column-gap: 10px;
        }

        .genlayout-item-tool {
            flex-direction: column;
            row-gap: 5px;
        }

        &:hover:not(:first-child) {
            background: #f7f7f7;
        }

        &:first-child {
            background: #f3f3f3;
            color: #000;

            >div {
                font-size: 15px;
                font-weight: bold;
            }
        }
    }
}
</style>