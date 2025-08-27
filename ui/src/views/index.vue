<template>
  <div class="app-container home">  
    <!-- <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :md="8">
        <el-card class="data-card">
          <div class="data-header">
            <i class="el-icon-message"></i>
            <span>今日消息总量</span>
          </div>
          <div class="data-content">
            <count-to :startVal="0" :endVal="2451" :duration="2000" class="data-number" />
            <div class="data-label">条</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="8">
        <el-card class="data-card">
          <div class="data-header">
            <i class="el-icon-s-custom"></i>
            <span>在线客服数量</span>
          </div>
          <div class="data-content">
            <count-to :startVal="0" :endVal="45" :duration="2000" class="data-number" />
            <div class="data-label">人</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="8">
        <el-card class="data-card">
          <div class="data-header">
            <i class="el-icon-data-line"></i>
            <span>平均响应时间</span>
          </div>
          <div class="data-content">
            <count-to :startVal="0" :endVal="15" :duration="2000" class="data-number" />
            <div class="data-label">秒</div>
          </div>
        </el-card>
      </el-col>
    </el-row> -->
  </div>
</template>

<script>
import CountTo from 'vue-count-to'

export default {
  name: 'Index',
  components: {
    CountTo
  }
}
</script>

<style lang="scss" scoped>
.home {
  padding: 20px;
  background-color: #f5f7fa;

  .feature-item {
    text-align: center;
    padding: 20px;
    margin-bottom: 20px;
    background: #fff;
    border-radius: 4px;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    }

    i {
      font-size: 40px;
      color: #409EFF;
      margin-bottom: 15px;
    }

    h4 {
      margin: 15px 0;
      font-size: 18px;
      color: #303133;
    }

    p {
      color: #606266;
      font-size: 14px;
      line-height: 1.6;
    }
  }

  .data-card {
    margin-bottom: 20px;

    .data-header {
      display: flex;
      align-items: center;
      margin-bottom: 20px;

      i {
        font-size: 24px;
        color: #409EFF;
        margin-right: 10px;
      }

      span {
        font-size: 16px;
        color: #606266;
      }
    }

    .data-content {
      text-align: center;

      .data-number {
        font-size: 36px;
        color: #303133;
        font-weight: bold;
      }

      .data-label {
        margin-top: 10px;
        color: #909399;
        font-size: 14px;
      }
    }
  }
}
</style>

