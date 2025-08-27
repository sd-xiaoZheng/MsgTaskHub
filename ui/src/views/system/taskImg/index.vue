<template>
  <div class="app-container">
    <!-- <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">  
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form> -->
    <el-card>

      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
            v-hasPermi="['system:taskImg:add']">上传</el-button>
        </el-col>
        <!-- <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['system:taskImg:edit']">修改</el-button>
      </el-col> -->
        <el-col :span="1.5">
          <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
            v-hasPermi="['system:taskImg:remove']">删除</el-button>
        </el-col>
        <!-- <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['system:taskImg:export']">导出</el-button>
      </el-col> -->
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table v-loading="loading" :data="taskImgList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <!-- <el-table-column label="id" align="center" prop="id" /> -->
        <!-- <el-table-column label="用户id" align="center" prop="userId" /> -->
        <el-table-column label="图片地址" align="center" prop="path">
          <template #default="scope">
            <el-image style="width: 80px; height: 80px" :src="imgUrl + scope.row.path" :preview-src-list="[imgUrl + scope.row.path]">
            </el-image> 
          </template>
        </el-table-column>
        <!-- <el-table-column label="项目id" align="center" prop="projectId" /> -->
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <!-- <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['system:taskImg:edit']">修改</el-button> -->
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
              v-hasPermi="['system:taskImg:remove']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
        @pagination="getList" />
    </el-card>

    <!-- 添加或修改TaskImg对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-upload :action="uploadOption.uploadUrl" ref="uploadImg" :headers="uploadOption.headers"
        list-type="picture-card" :on-preview="handlePictureCardPreview" :on-success="handleUploadSuccess"
        accept=".jpg,.png,.jpeg,.webp" :on-remove="handleRemove" :limit="1" :auto-upload="false">
        <i class="el-icon-plus"></i>
      </el-upload>
      <!-- <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="图片">
          <el-upload :action="uploadOption.uploadUrl" ref="uploadImg" :headers="uploadOption.headers" list-type="picture-card"
            :on-preview="handlePictureCardPreview" :on-success="handleUploadSuccess" :on-remove="handleRemove" :limit="1" :auto-upload="false" >
            <i class="el-icon-plus"></i>
          </el-upload>
        </el-form-item> -->
      <!-- <el-form-item label="用户id" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户id" />
        </el-form-item>
        <el-form-item label="保存地址" prop="path">
          <el-input v-model="form.path" placeholder="请输入保存地址" />
        </el-form-item>
        <el-form-item label="项目id" prop="projectId">
          <el-input v-model="form.projectId" placeholder="请输入项目id" />
        </el-form-item> -->
      <!-- </el-form> -->
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listTaskImg, getTaskImg, delTaskImg, addTaskImg, updateTaskImg } from "@/api/system/taskImg";
import { getToken } from "@/utils/auth";
export default {
  name: "TaskImg",
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
      // TaskImg表格数据
      taskImgList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        path: null,
        projectId: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      dialogImageUrl: '',
      dialogVisible: false,
      uploadOption: {
        uploadUrl: process.env.VUE_APP_BASE_API + "/system/taskImg/importImgName", // 上传的图片服务器地址
        headers: {
          Authorization: "Bearer " + getToken()
        },
      }
    };
  },
  created() {
    this.getList();
  },
  computed:{
    imgUrl(){
      return process.env.VUE_APP_BASE_API 
    }
  },
  methods: {
    handleRemove(file, fileList) {
      console.log(file, fileList);
    },
    handleUploadSuccess(res) {
      this.$refs.uploadImg.clearFiles()
      if (res.code == 200) {
        this.open = false
        this.$message.success("图片上传成功")
        this.getList()
      } else {
        this.$message.error("图片上传失败")
      }
    },
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url;
      this.dialogVisible = true;
    },
    /** 查询TaskImg列表 */
    getList() {
      this.loading = true;
      listTaskImg(this.queryParams).then(response => {
        this.taskImgList = response.rows;
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
        userId: null,
        path: null,
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
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加图片";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getTaskImg(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改TaskImg";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs.uploadImg.submit()
      // this.$refs["form"].validate(valid => {
      //   if (valid) {
      //     if (this.form.id != null) {
      //       updateTaskImg(this.form).then(response => {
      //         this.$modal.msgSuccess("修改成功");
      //         this.open = false;
      //         this.getList();
      //       });
      //     } else {
      //       this.$refs.uploadImg.submit()
      //       // addTaskImg(this.form).then(response => {
      //       //   this.$modal.msgSuccess("新增成功");
      //       //   this.open = false;
      //       //   this.getList();
      //       // });
      //     }
      //   }
      // });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除TaskImg编号为"' + ids + '"的数据项？').then(function () {
        return delTaskImg(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/taskImg/export', {
        ...this.queryParams
      }, `taskImg_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>

<style> 
</style>
