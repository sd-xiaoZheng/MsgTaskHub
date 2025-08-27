<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="${comment}" prop="setId">
        <el-input
          v-model="queryParams.setId"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="客服后缀是否随机 0顺序 1随机" prop="customerRng">
        <el-input
          v-model="queryParams.customerRng"
          placeholder="请输入客服后缀是否随机 0顺序 1随机"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="管理员后缀" prop="leaderSuffix">
        <el-input
          v-model="queryParams.leaderSuffix"
          placeholder="请输入管理员后缀"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="账号发送数量" prop="accountSendNum">
        <el-input
          v-model="queryParams.accountSendNum"
          placeholder="请输入账号发送数量"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="客服是否产看账号权限" prop="accountControl">
        <el-input
          v-model="queryParams.accountControl"
          placeholder="请输入客服是否产看账号权限"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label=".-1不验证/正则表达式" prop="regular">
        <el-input
          v-model="queryParams.regular"
          placeholder="请输入.-1不验证/正则表达式"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="最大发送字数" prop="maxSend">
        <el-input
          v-model="queryParams.maxSend"
          placeholder="请输入最大发送字数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="所属项目" prop="projectId">
        <el-input
          v-model="queryParams.projectId"
          placeholder="请输入所属项目"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="预留0" prop="arg0">
        <el-input
          v-model="queryParams.arg0"
          placeholder="请输入预留0"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="预留1" prop="arg1">
        <el-input
          v-model="queryParams.arg1"
          placeholder="请输入预留1"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="预留2" prop="arg2">
        <el-input
          v-model="queryParams.arg2"
          placeholder="请输入预留2"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="预留3" prop="arg3">
        <el-input
          v-model="queryParams.arg3"
          placeholder="请输入预留3"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="预留4" prop="arg4">
        <el-input
          v-model="queryParams.arg4"
          placeholder="请输入预留4"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['chat:ProjectSet:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['chat:ProjectSet:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['chat:ProjectSet:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['chat:ProjectSet:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="ProjectSetList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="${comment}" align="center" prop="setId" />
      <el-table-column label="客服后缀是否随机 0顺序 1随机" align="center" prop="customerRng" />
      <el-table-column label="管理员后缀" align="center" prop="leaderSuffix" />
      <el-table-column label="账号发送数量" align="center" prop="accountSendNum" />
      <el-table-column label="客服是否产看账号权限" align="center" prop="accountControl" />
      <el-table-column label=".-1不验证/正则表达式" align="center" prop="regular" />
      <el-table-column label="最大发送字数" align="center" prop="maxSend" />
      <el-table-column label="所属项目" align="center" prop="projectId" />
      <el-table-column label="预留0" align="center" prop="arg0" />
      <el-table-column label="预留1" align="center" prop="arg1" />
      <el-table-column label="预留2" align="center" prop="arg2" />
      <el-table-column label="预留3" align="center" prop="arg3" />
      <el-table-column label="预留4" align="center" prop="arg4" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['chat:ProjectSet:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['chat:ProjectSet:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改项目设置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" >
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="客服后缀是否随机 0顺序 1随机" prop="customerRng">
          <el-input v-model="form.customerRng" placeholder="请输入客服后缀是否随机 0顺序 1随机" />
        </el-form-item>
        <el-form-item label="管理员后缀" prop="leaderSuffix">
          <el-input v-model="form.leaderSuffix" placeholder="请输入管理员后缀" />
        </el-form-item>
        <el-form-item label="账号发送数量" prop="accountSendNum">
          <el-input v-model="form.accountSendNum" placeholder="请输入账号发送数量" />
        </el-form-item>
        <el-form-item label="客服是否产看账号权限" prop="accountControl">
          <el-input v-model="form.accountControl" placeholder="请输入客服是否产看账号权限" />
        </el-form-item>
        <el-form-item label=".-1不验证/正则表达式" prop="regular">
          <el-input v-model="form.regular" placeholder="请输入.-1不验证/正则表达式" />
        </el-form-item>
        <el-form-item label="最大发送字数" prop="maxSend">
          <el-input v-model="form.maxSend" placeholder="请输入最大发送字数" />
        </el-form-item>
        <el-form-item label="所属项目" prop="projectId">
          <el-input v-model="form.projectId" placeholder="请输入所属项目" />
        </el-form-item>
        <el-form-item label="预留0" prop="arg0">
          <el-input v-model="form.arg0" placeholder="请输入预留0" />
        </el-form-item>
        <el-form-item label="预留1" prop="arg1">
          <el-input v-model="form.arg1" placeholder="请输入预留1" />
        </el-form-item>
        <el-form-item label="预留2" prop="arg2">
          <el-input v-model="form.arg2" placeholder="请输入预留2" />
        </el-form-item>
        <el-form-item label="预留3" prop="arg3">
          <el-input v-model="form.arg3" placeholder="请输入预留3" />
        </el-form-item>
        <el-form-item label="预留4" prop="arg4">
          <el-input v-model="form.arg4" placeholder="请输入预留4" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listProjectSet, getProjectSet, delProjectSet, addProjectSet, updateProjectSet } from "@/api/chat/ProjectSet";

export default {
  name: "ProjectSet",
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
      // 项目设置表格数据
      ProjectSetList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        setId: null,
        customerRng: null,
        leaderSuffix: null,
        accountSendNum: null,
        accountControl: null,
        regular: null,
        maxSend: null,
        projectId: null,
        arg0: null,
        arg1: null,
        arg2: null,
        arg3: null,
        arg4: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询项目设置列表 */
    getList() {
      this.loading = true;
      listProjectSet(this.queryParams).then(response => {
        this.ProjectSetList = response.rows;
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
        setId: null,
        customerRng: null,
        leaderSuffix: null,
        accountSendNum: null,
        accountControl: null,
        regular: null,
        maxSend: null,
        projectId: null,
        arg0: null,
        arg1: null,
        arg2: null,
        arg3: null,
        arg4: null
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
      this.ids = selection.map(item => item.setId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加项目设置";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const setId = row.setId || this.ids
      getProjectSet(setId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改项目设置";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.setId != null) {
            updateProjectSet(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addProjectSet(this.form).then(response => {
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
      const setIds = row.setId || this.ids;
      this.$modal.confirm('是否确认删除项目设置编号为"' + setIds + '"的数据项？').then(function() {
        return delProjectSet(setIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('chat/ProjectSet/export', {
        ...this.queryParams
      }, `ProjectSet_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
