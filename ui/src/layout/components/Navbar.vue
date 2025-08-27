<template>
  <div class="navbar" :class="settings.sideTheme">
    <hamburger id="hamburger-container" :is-active="sidebar.opened" class="hamburger-container"
      @toggleClick="toggleSideBar" />

    <!-- <breadcrumb id="breadcrumb-container" class="breadcrumb-container" v-if="!topNav" /> -->
    <top-nav id="topmenu-container" class="topmenu-container" v-if="topNav" />

    <div class="right-menu">
      <template>
        <el-tooltip :content="$t('navbar.switchLannguage')" effect="dark" placement="bottom">
          <el-dropdown class="right-menu-item hover-effect" trigger="click" @command="handleLanguageChange">
            <div class="language-menu">
              <i class="el-icon-message" />
              <span style="margin-left: 5px; font-size: 14px;">{{ $i18n.locale === 'zh' ? '中文' : 'English' }}</span>
            </div>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="zh" :class="{ 'selected-language': $i18n.locale === 'zh' }">
                <span>中文</span>
              </el-dropdown-item>
              <el-dropdown-item command="en" :class="{ 'selected-language': $i18n.locale === 'en' }">
                <span>English</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </el-tooltip>
        <el-tooltip :content="$t('navbar.switchProject')" effect="dark" placement="bottom">
          <el-dropdown class="right-menu-item hover-effect" trigger="click" @command="handleSocialSelect">
            <div class="social-menu">
              <i class="el-icon-share" />
              <span style="margin-left: 5px; font-size: 14px;">{{ selectedPlatform || $t('navbar.project') }}</span>
            </div>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item :command="item.projectId" v-for="item in projectList">
                <i class="el-icon-message" style="margin-right: 10px;" />
                <span :class="{ 'selected-platform': selectedPlatform === item.projectName }">{{ item.projectName
                  }}</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </el-tooltip>
      </template>
      <el-button icon="el-icon-bell" size="small" type="primary" @click="showNoticeDialog">
        {{ $t('navbar.notice') }}
      </el-button>
      <el-dropdown class="avatar-container right-menu-item hover-effect" trigger="click">
        <div class="avatar-wrapper">
          <!-- <img :src="avatar" class="user-avatar"> -->
          <span style="font-size: 15px;">{{ name }} 
            (
            {{ isManage === -1 ? (isAdmin?$t('navbar.role.admin'):$t('navbar.role.operation')): (isManage === 0 ? $t('navbar.role.serviceStaff') :
    $t('navbar.role.teamLeader')) }}
            )
          </span>
          <i class="el-icon-caret-bottom" />
        </div>
        <el-dropdown-menu slot="dropdown">
          <router-link to="/user/profile" v-if="isManage === -1 ">
            <el-dropdown-item>{{ $t('navbar.profile') }}</el-dropdown-item>
          </router-link>
          <el-dropdown-item @click.native="setting = true" v-hasRole="['admin']">
            <span>{{ $t('navbar.layoutSettings') }}</span>
          </el-dropdown-item>
          <el-dropdown-item divided @click.native="logout">
            <span>{{ $t('navbar.logout') }}</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div> 
    <el-dialog title="消息列表" :visible.sync="noticeDialogVisible" width="600px" custom-class="notice-dialog">
      <el-table :data="noticeList" style="width: 100%" @row-click="handleNoticeClick">
        <el-table-column prop="noticeTitle" :label="$t('notice.title')" />
        <el-table-column prop="createTime" :label="$t('notice.publishTime')" width="180" />
      </el-table>
    </el-dialog>

    <el-dialog title="公告详情" :visible.sync="noticeDetailVisible" width="60%">
      <template v-if="currentNotice">
        <h3>{{ currentNotice.noticeTitle }}</h3>
        <div class="notice-time">{{ $t('notice.publishTime') }}{{ currentNotice.createTime }}</div>
        <div class="notice-content">
           {{ currentNotice.noticeContent }}</div>
      </template>
    </el-dialog>

  </div>
</template>

<script>
import { mapGetters, mapState } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import TopNav from '@/components/TopNav'
import Hamburger from '@/components/Hamburger'
import Screenfull from '@/components/Screenfull'
import SizeSelect from '@/components/SizeSelect'
import Search from '@/components/HeaderSearch'
import RuoYiGit from '@/components/RuoYi/Git'
import RuoYiDoc from '@/components/RuoYi/Doc'
import { listProjectAll } from '@/api/chat/Project'
import { myNotice } from '@/api/system/notice'
import Cookies from 'js-cookie'
import { getProjectSetByProjectId } from '@/api/chat/ProjectSet'
export default {
  components: {
    Breadcrumb,
    TopNav,
    Hamburger,
    Screenfull,
    SizeSelect,
    Search,
    RuoYiGit,
    RuoYiDoc
  },
  data() {
    return {
      selectedPlatform: '',
      noticeDialogVisible: false,
      noticeList: [],
      noticeDetailVisible: false,
      currentNotice: null
    }
  },
  mounted() {
    listProjectAll().then(res => {
      console.log(res)
      if (res.code == 200) {
        this.$store.dispatch('app/setProjectList', res.rows)
        if (res.rows.length > 0) {
          // 选择第一个项目，同时获取第一个项目的配置信息
          this.$store.dispatch('app/setProjectId', res.rows[0].projectId);
          this.selectedPlatform = res.rows[0].projectName;
          getProjectSetByProjectId(res.rows[0].projectId).then(res => {
            if (res.code == 200) {
              // 设置项目的全局配置信息
              this.$store.dispatch('app/setProjectOptions', res.data);
            }
          })
        }
      }
    })
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'avatar','isAdmin',
      'device', 'name', 'projectList', 'isManage'
    ]),
    ...mapState({
      settings: state => state.settings
    }),
    setting: {
      get() {
        return this.$store.state.settings.showSettings
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'showSettings',
          value: val
        })
      }
    },
    topNav: {
      get() {
        return this.$store.state.settings.topNav
      }
    }
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    handleLanguageChange(lang) {
      this.$i18n.locale = lang
      Cookies.set('language', lang)
      this.$message({
        message: lang === 'zh' ? '切换语言成功' : 'Language changed successfully',
        type: 'success'
      })
    },
    showNoticeDialog() {
      this.noticeDialogVisible = true
      this.getNoticeList()
    },
    getNoticeList() {
      myNotice().then(response => {
        this.noticeList = response.rows
      })
    },
    handleNoticeClick(notice) {
      this.currentNotice = notice
      this.noticeDetailVisible = true
    },
    async logout() {
      this.$confirm('确定注销并退出系统吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('LogOut').then(() => {
          location.href = '/index';
        })
      }).catch(() => { });
    },
    handleSocialSelect(command) {
      this.$store.dispatch('app/setProjectId', command);
      getProjectSetByProjectId(command).then(res => {
        if (res.code == 200) {
          // 设置项目的全局配置信息
          this.$store.dispatch('app/setProjectOptions', res.data);
        }
      })
      this.projectList.filter(item => {
        if (item.projectId == command) {
          this.selectedPlatform = item.projectName;
        }
      })
      this.$message({
        message: `已选择${this.selectedPlatform}平台`,
        type: 'success'
      });
    }
  }
}
</script>

<style lang="scss" scoped>
.notice-dialog {
  .el-dialog__body {
    padding: 20px;
  }

  .el-table {
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  }
}

.notice-time {
  color: #666;
  font-size: 14px;
  margin: 10px 0;
}

.notice-content {
  margin-top: 20px;
  line-height: 1.8;
  color: #333;
  font-size: 14px;
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
}

.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;

  &.theme-dark {
    background: #111827;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);

    .social-menu {
      >span {
        color: #e6e6e6 !important;
      }

      >i {
        color: #e6e6e6 !important;
      }
    }

    .avatar-wrapper {
      >span {
        color: #e6e6e6 !important;
      }

      .el-icon-caret-bottom {
        color: #e6e6e6 !important;
      }

    }
  }

  &.theme-light {
    background: #fff;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);

    .right-menu-item {
      color: #3f3f3f !important;
    }

    .social-menu {
      >span {
        color: #3f3f3f !important;
      }

      >i {
        color: #3f3f3f !important;
      }
    }

    .avatar-container {
      margin-right: 30px;

      .avatar-wrapper {
        >span {
          color: #3f3f3f !important;
        }

        .el-icon-caret-bottom {
          color: #3f3f3f;
        }
      }
    }
  }


  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: all 0.3s;
    -webkit-tap-highlight-color: transparent;

    &:hover {
      background: rgba(37, 100, 235, 0.1);
      color: #2564eb;
    }
  }

  .breadcrumb-container {
    float: left;
  }

  .topmenu-container {
    position: absolute;
    left: 50px;
  }

  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  .right-menu {
    float: right;
    height: 100%;
    line-height: 50px;
    display: flex;
    align-items: center;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          background: rgba(37, 100, 235, 0.1);
          color: #2564eb;
        }
      }
    }

    .avatar-container {
      margin-right: 30px;

      .avatar-wrapper {
        height: 100%;
        display: flex;
        align-items: center;
        padding: 0 8px;
        transition: all 0.3s;

        &:hover {
          background: rgba(37, 100, 235, 0.1);
        }

        .user-avatar {
          cursor: pointer;
          width: 32px;
          height: 32px;
          border-radius: 50%;
          margin-right: 10px;
          border: 2px solid #2564eb;
          transition: all 0.3s;

          &:hover {
            transform: scale(1.1);
          }
        }

        span {
          color: #5a5e66;
          transition: all 0.3s;
        }

        .el-icon-caret-bottom {
          cursor: pointer;
          font-size: 15px;
          color: #5a5e66;
          transition: all 0.3s;
        }

        &:hover {

          span,
          .el-icon-caret-bottom {
            color: #2564eb;
          }
        }
      }
    }
  }
}

.social-menu,
.language-menu {
  height: 100%;
  padding: 0 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  transition: all 0.3s;

  &:hover {
    background: rgba(37, 100, 235, 0.1);
    color: #2564eb;
  }
}

.selected-language {
  color: #2564eb;
  font-weight: 500;
}

.selected-platform {
  color: #2564eb;
  font-weight: 500;
}

.notice-dialog {
  .el-table {
    cursor: pointer;

    .el-table__row:hover {
      background-color: rgba(37, 100, 235, 0.1);
    }
  }
}

.notice-time {
  color: #666;
  font-size: 14px;
  margin: 10px 0;
}

.notice-content {
  margin-top: 20px;
  line-height: 1.6;
}
</style>
