<template>
  <div :class="classObj" class="app-wrapper" :style="{'--current-color': theme}">
    <div v-if="device==='mobile'&&sidebar.opened" class="drawer-bg" @click="handleClickOutside"/>
    <sidebar v-if="!sidebar.hide" class="sidebar-container"/>
    <div :class="{hasTagsView:needTagsView,sidebarHide:sidebar.hide}" class="main-container">
      <div :class="{'fixed-header':fixedHeader}">
        <navbar/>
        <!-- 公告通知滚动 -->
        <div class="notice-banner" :class="sideTheme" v-if="latestNotice">
          <i class="el-icon-bell notice-icon"></i>
          <div class="notice-scrollbar" style="flex: 1;overflow: hidden;">
            <div class="notice-content">
              <span class="notice-text">{{ latestNotice.noticeTitle }}：{{ latestNotice.noticeContent }}</span>
            </div>
          </div>
        </div>
        <!-- <tags-view v-if="needTagsView"/> -->
      </div>
      <app-main/>
      <right-panel>
        <settings/>
      </right-panel>
    </div>
  </div>
</template>

<script>
import RightPanel from '@/components/RightPanel'
import { AppMain, Navbar, Settings, Sidebar, TagsView } from './components'
import ResizeMixin from './mixin/ResizeHandler'
import { mapState } from 'vuex'
import variables from '@/assets/styles/variables.scss'
import { myNotice } from '@/api/system/notice'
export default {
  name: 'Layout',
  components: {
    AppMain,
    Navbar,
    RightPanel,
    Settings,
    Sidebar,
    TagsView
  },
  mixins: [ResizeMixin],
  data() {
    return {
      latestNotice: null
    }
  },
  mounted() {
    this.getLatestNotice()
  },
  computed: {
    ...mapState({
      theme: state => state.settings.theme,
      sideTheme: state => state.settings.sideTheme,
      sidebar: state => state.app.sidebar,
      device: state => state.app.device,
      needTagsView: state => state.settings.tagsView,
      fixedHeader: state => state.settings.fixedHeader
    }),
    classObj() {
      return {
        hideSidebar: !this.sidebar.opened,
        openSidebar: this.sidebar.opened,
        withoutAnimation: this.sidebar.withoutAnimation,
        mobile: this.device === 'mobile'
      }
    },
    variables() {
      return variables;
    }
  },
  methods: {
    handleClickOutside() {
      this.$store.dispatch('app/closeSideBar', { withoutAnimation: false })
    },
    getLatestNotice() {
      myNotice().then(response => {
        if (response.rows && response.rows.length > 0) {
          this.latestNotice = response.rows[0]
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
  @import "~@/assets/styles/mixin.scss";
  @import "~@/assets/styles/variables.scss";

  .app-wrapper {
    @include clearfix;
    position: relative;
    height: 100%;
    width: 100%;

    &.mobile.openSidebar {
      position: fixed;
      top: 0;
    }
  }

  .drawer-bg {
    background: #000;
    opacity: 0.3;
    width: 100%;
    top: 0;
    height: 100%;
    position: absolute;
    z-index: 999;
  }

  .fixed-header {
    position: fixed;
    top: 0;
    right: 0;
    z-index: 9;
    width: calc(100% - #{$base-sidebar-width});
    transition: width 0.28s;
  }

  .hideSidebar .fixed-header {
    width: calc(100% - 54px);
  }
 
  .sidebarHide .fixed-header {
    width: 100%;
  }

  .mobile .fixed-header {
    width: 100%;
  }

  .notice-banner { 
    padding: 10px 20px;
    margin: 0;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.12);
    transition: all 0.3s;
    display: flex;
    align-items: center;
    .notice-content {
      display: flex;
      align-items: center;
      animation: scrollText 20s linear infinite;
    }

    .notice-icon {
      font-size: 20px;
      // color: #409eff;
      color: #b0d3f7;
      margin-right: 10px;
      animation: shake 2s infinite;
    }

    .notice-text {
      // color: #606266;
      color: #ededed;
      font-size: 14px;
      white-space: nowrap;
    }
  }

  @keyframes scrollText {
    0% {
      transform: translateX(100%);
    }
    100% {
      transform: translateX(-100%);
    }
  }

  @keyframes shake {
    0%, 100% {
      transform: translateX(0);
    }
    25% {
      transform: translateX(-3px);
    }
    75% {
      transform: translateX(3px);
    }
  }
</style>
