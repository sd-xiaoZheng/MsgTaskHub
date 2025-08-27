<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!-- 左侧教程导航 -->
      <el-col :span="4">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>教程目录</span>


            <el-button style="float: right;" type="primary" icon="el-icon-plus" size="mini" @click="handleAdd"
                v-hasPermi="['chat:Course:add']">新增</el-button>
          </div>
          <el-menu :default-active="activeIndex" class="tutorial-menu" @select="handleSelect">
            <el-menu-item v-for="(item, index) in tutorialList" :key="index" :index="String(index)">
              {{ item.title }}
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>

      <!-- 右侧内容区 -->
      <el-col :span="18">
        <el-card class="tutorial-content">
          <div class="content-header">
            <h2>{{ currentTutorial.title }}</h2>
            <!-- 管理员操作按钮 -->
            <div class="admin-actions" v-hasRole="['project','admin','admin001']"> 
              <el-button type="success" plain icon="el-icon-edit" size="mini" @click="handleUpdate"
                v-hasPermi="['chat:Course:edit']">修改</el-button>
              <el-button type="danger" plain icon="el-icon-delete" size="mini" @click="handleDelete"
                v-hasPermi="['chat:Course:remove']">删除</el-button>
            </div>
          </div>

          <!-- 教程内容区域 -->
          <div class="tutorial-sections">
            <div v-for="(section, index) in currentTutorial.sections" :key="index" class="tutorial-section">
              <!-- <h3>{{ section.title }}</h3>  -->
              <div class="section-content" v-html="section.content"></div>
              <div class="section-image" v-if="section.image">
                <img :src="section.image" :alt="section.title">
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 新增/修改教程对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="780px" 
    :close-on-press-escape="false" :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="教程标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入教程标题" />
        </el-form-item>
        <el-form-item label="教程内容" prop="content">
          <editor v-model="form.content" :min-height="192" />
        </el-form-item>
        <el-form-item label="排序" prop="orderBy">
          <el-input-number v-model="form.orderBy" :min="0" :max="999" />
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
import { listCourse, getCourse, addCourse, updateCourse, delCourse } from '@/api/chat/Course';

export default {
  name: "Tutorial",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 是否显示弹出层
      open: false,
      // 弹出层标题
      title: "", 
      // 当前激活的教程索引
      activeIndex: "0",
      // 当前显示的教程
      currentTutorial: {},
      // 教程列表数据
      tutorialList: [],
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        title: [
          { required: true, message: "教程标题不能为空", trigger: "blur" }
        ],
        content: [
          { required: true, message: "教程内容不能为空", trigger: "blur" }
        ],
        orderBy: [
          { required: true, message: "排序不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    // 获取教程列表
    getList() {
      this.loading = true;
      listCourse().then(response => {
        this.tutorialList = response.rows.map(item => ({
          title: item.title,
          sections: [{
            title: item.title,
            content: item.content
          }],
          courseId: item.courseId,
          orderBy: item.orderBy
        }));
        if (this.tutorialList.length > 0) {
          this.currentTutorial = this.tutorialList[0];
        }
        this.loading = false;
      });
    },
    // 检查是否为管理员
    checkAdmin() {
      return this.$store.getters.roles.includes('admin');
    },
    // 选择教程
    handleSelect(index) {
      this.currentTutorial = this.tutorialList[parseInt(index)];
    },
    // 新增按钮操作
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加教程";
    },
    // 修改按钮操作
    handleUpdate() {
      this.reset();
      const tutorial = this.currentTutorial;
      this.form = {
        courseId: tutorial.courseId,
        title: tutorial.title,
        content: tutorial.sections[0].content,
        orderBy: tutorial.orderBy
      };
      this.open = true;
      this.title = "修改教程";
    },
    // 删除按钮操作
    handleDelete() {
      this.$modal.confirm('是否确认删除该教程？').then(() => {
        delCourse(this.currentTutorial.courseId).then(response => {
          this.$modal.msgSuccess("删除成功");
          this.getList();
        });
      }).catch(() => { });
    },
    // 表单重置
    reset() {
      this.form = {
        title: undefined,
        content: undefined,
        orderBy: 0
      };
      this.resetForm("form");
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 提交按钮
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.courseId) {
            updateCourse(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addCourse(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    }
  }
};
</script>

<style lang="scss">
.tutorial-content {
  min-height: 500px;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.tutorial-section {
  margin-bottom: 30px;
}

.section-content {
  line-height: 1.6;
  margin: 15px 0;

  & img {
    width: 350px;
  }
}


.section-image {
  margin: 20px 0;
  text-align: center;
}

.section-image img {
  max-width: 100%;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.tutorial-menu {
  border-right: none;
}
</style>