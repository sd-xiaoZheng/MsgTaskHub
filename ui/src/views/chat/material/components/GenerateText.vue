<template>
    <el-dialog title="话术管理" :visible.sync="dialogValue">
        <!-- <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
        <el-form-item label="绑定的中间表id" prop="tlmId">
          <el-input
            v-model="queryParams.tlmId"
            placeholder="请输入绑定的中间表id"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form> -->

        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
                    v-hasPermi="['chat:GenerateText:edit']">修改</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple"
                    @click="handleDelete" v-hasPermi="['chat:GenerateText:remove']">删除</el-button>
            </el-col>
            <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="GenerateTextList" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column label="编号" align="center" prop="id" />
            <el-table-column label="文本" align="center" prop="content" />
            <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                    <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                        v-hasPermi="['chat:GenerateText:edit']">修改</el-button>
                    <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                        v-hasPermi="['chat:GenerateText:remove']">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
            @pagination="getList" />

        <!-- 添加或修改GenerateText对话框 -->
        <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
            <el-form ref="form" :model="form" :rules="rules" label-width="80px">
                <el-form-item label="项编号" prop="tlmId">
                    <el-input v-model="form.tlmId" disabled placeholder="请输入绑定的中间表id" />
                </el-form-item>
                <el-form-item label="文本">
                    <el-input v-model="form.content" style="width:100%;"  :autosize="{ minRows: 5, maxRows: 10 }" type="textarea"
                        placeholder="Please input" /> 
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="submitForm">确 定</el-button>
                <el-button @click="cancel">取 消</el-button>
            </div>
        </el-dialog>
    </el-dialog>
</template>

<script>
import { listGenerateText, getGenerateText, delGenerateText, addGenerateText, updateGenerateText } from "@/api/chat/GenerateText";

export default {
    name: "GenerateText",
    props: ['dialog', 'tlmId'],
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
            // GenerateText表格数据
            GenerateTextList: [],
            // 弹出层标题
            title: "",
            // 是否显示弹出层
            open: false,
            // 查询参数
            queryParams: {
                pageNum: 1,
                pageSize: 10,
                tlmId: null,
                content: null
            },
            // 表单参数
            form: {},
            // 表单校验
            rules: {
            }
        };
    },
    computed: {
        dialogValue:{
            get(){
                return this.dialog;
            },
            set(val){
                this.$emit('update:dialog', val);
            }
        }
    },
    watch: {
        visible: {
            handler(val) {
            },
        },
        tlmId: {
            handler(val) {
                this.queryParams.tlmId = val;
                this.getList();
            },
        }
    },
    created() {

    },
    methods: {
        /** 查询GenerateText列表 */
        getList() {
            this.loading = true;
            listGenerateText(this.queryParams).then(response => {
                this.GenerateTextList = response.rows;
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
                id: null,
                tlmId: null,
                content: null
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
            this.ids = selection.map(item => item.id)
            this.single = selection.length !== 1
            this.multiple = !selection.length
        },
        /** 新增按钮操作 */
        handleAdd() {
            this.reset();
            this.open = true;
            this.title = "添加GenerateText";
        },
        /** 修改按钮操作 */
        handleUpdate(row) {
            this.reset();
            const id = row.id || this.ids
            getGenerateText(id).then(response => {
                this.form = response.data;
                this.open = true;
                this.title = "修改GenerateText";
            });
        },
        /** 提交按钮 */
        submitForm() {
            this.$refs["form"].validate(valid => {
                if (valid) {
                    if (this.form.id != null) {
                        updateGenerateText(this.form).then(response => {
                            this.$modal.msgSuccess("修改成功");
                            this.open = false;
                            this.getList();
                        });
                    } else {
                        addGenerateText(this.form).then(response => {
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
            const ids = row.id || this.ids;
            this.$modal.confirm('是否确认删除GenerateText编号为"' + ids + '"的数据项？').then(function () {
                return delGenerateText(ids);
            }).then(() => {
                this.getList();
                this.$modal.msgSuccess("删除成功");
            }).catch(() => { });
        },
        /** 导出按钮操作 */
        handleExport() {
            this.download('chat/GenerateText/export', {
                ...this.queryParams
            }, `GenerateText_${new Date().getTime()}.xlsx`)
        }
    }
};
</script>