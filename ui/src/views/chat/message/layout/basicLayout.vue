<template>
  <div class="chat-container" :class="isFullScreen ? 'fullScreen' : ''" ref="chat" v-loading="chatLoading">
    <!-- 左侧联系人列表 -->
    <div class="contact-list">
      <div class="search-box">
        <!-- 搜索框 -->
        <el-input v-model="querySessionParams.custContact" prefix-icon="el-icon-search"
          :placeholder="$t('message.search')" clearable @input="handleSearchInput" />
        <!-- 刷新数据 -->
        <el-button type="primary" style="margin-left: 5px;" icon="el-icon-refresh-right" @click="handleReferData"
          circle>

        </el-button>
      </div>
      <div class="contact-items" v-loading="loadingSession">
        <!-- 会话列表 -->
        <div v-for="contact in sessionContacts" :key="contact.sessionId" class="contact-item"
          :class="{ active: currentContact && (currentContact.sessionId === contact.sessionId), 'is-pinned': contact.isPinned }"
          @click="selectContact(contact)">
          <el-avatar :size="40" :src="LogoImg">
          </el-avatar>
          <div class="contact-info">
            <div class="contact-name">
              {{ contact.custContact }}
              <el-badge v-if="contact.messageCount > 0" :value="contact.messageCount" class="unread-badge" />
            </div>
            <div class="contact-message">
              {{ contact.endText }}
            </div>
            <div class="contact-time">{{ templateTime(contact.latestChatTime) }}</div>
          </div>
          <div class="contact-actions">
            <div class="action-icons">
              <i class="el-icon-top" :class="{ 'is-active': contact.isPinned }" @click.stop="togglePin(contact)"
                :title="$t('message.top')"></i>
              <i class="el-icon-star-off" :class="{ 'is-active': contact.isFavorite }" @click.stop="toggleStar(contact)"
                :title="$t('message.star')" v-if="activeTab !== 'group'"></i>
            </div>
          </div>
        </div>

        <div class="contact-items-loading" ref="contactLoading">
          <!-- 加载中 -->
          <template v-if="reachBootom === 'more'">
            {{ $t('message.reachMore') }}
          </template>
          <template v-if="reachBootom === 'loading'">
            <i class="el-icon-loading"></i>
            {{ $t('message.loading') }}
          </template>
          <template v-if="reachBootom === 'none'">
            {{ $t('message.none') }}
          </template>
        </div>
      </div>
    </div>

    <!-- 中间聊天区域 -->
    <div class="chat-main">
      <div class="chat-header">
        <el-radio-group v-model="activeTab" v-if="sessionCounts">
          <!-- 对话 -->
          <el-radio-button label="personal" class="radio_button">
            <div class="red_point" v-show="unreadPersonal">
              <!-- 红点的显示 -->
            </div>
            {{ $t('message.personal') }}({{ sessionCounts.sessionNum }}/{{
              sessionCounts.sessionUnreadNum }})
          </el-radio-button>
          <!-- 群发 -->
          <el-radio-button label="group" class="radio_button">{{ $t('message.group') }}({{ sessionCounts.groupNum
          }})</el-radio-button>
          <!-- 收藏 -->
          <el-radio-button label="star" class="radio_button">
            <div class="red_point" v-show="unreadStar">
              <!-- 红点的显示 -->
            </div>
            {{ $t('message.star') }}({{ sessionCounts.collectNum }}/{{
              sessionCounts.collectUnreadNum }})
          </el-radio-button>
        </el-radio-group>

        <el-button type="primary" @click="handleControllScreen">
          {{ isFullScreen ? $t('message.exitFullScreen') : $t('message.fullScreen') }}

        </el-button>
      </div>
      <div class="chat-header" v-if="currentContact">
        <span class="chat-title">{{ currentContact.custContact }}</span>
      </div>
      <div v-if="!currentContact" class="empty-state">
        <i class="el-icon-chat-dot-round"></i>
        <p>{{ $t('message.noSessionTips') }}</p>
      </div>
      <div class="message-list" v-loading="recordLoading" ref="messageList" v-show="currentContact">
        <div class="message-load-ref" ref="messageLoad">
          <template v-if="pullDownMessage === 'more'">
            {{ $t('message.pullMore') }}
          </template>
          <template v-if="pullDownMessage === 'none'">
            {{ $t('message.none') }}
          </template>
          <template v-if="pullDownMessage === 'loading'">
            <i class="el-icon-loading"></i>
            {{ $t('message.loading') }}
          </template>
        </div>
        <div v-for="(message, index) in messages" :key="message.chatId" class="message-item"
          :class="message.chatInout === -1 ? 'self' : 'other'">
          <el-avatar :size="36" :src="LogoImg" v-show="message.chatInout === -1">
          </el-avatar>
          <el-avatar :size="36" v-show="message.chatInout === 1" icon="el-icon-user-solid">
          </el-avatar>
          <div class="message-content">
            <div class="message-bubble" v-if="getMessageType(message) === 'text'">
              <!-- 展示图片 -->
              <span>
                {{ message.messageContent }}
              </span>
              <div class="message-actions">
                <i class="el-icon-s-operation" @click="translateMessage(message)" title="翻译"></i>
              </div>
              <div class="translation" v-if="message.showTranslation">
                <template v-if="message.translateLoading">
                  <i class="el-icon-loading" style="margin-right: 5px;"></i>
                  翻译中...
                </template>
                {{ message.translatedContent }}
              </div>
            </div>
            <div class="message-bubble" v-if="getMessageType(message) === 'img'">
              <!-- 展示图片 -->
              <img :src="message.messageContent" alt="" />
            </div>
            <div class="message-tip">
              <div class="message-time">{{ templateTime(message.sendTime) }}</div>
            </div>
          </div>
        </div>
      </div>

<!--  底部回复消息模块    -->
      <div class="input-area" v-if="activeTab !== 'group'">
        <div class="toolbar">
          <div class="emoji">
            <div class="emoji_marker" v-show="showEmojiPicker" @click="toggleEmojiPicker">

            </div>
            <emoji-picker class="emoji_picker" v-show="showEmojiPicker" @select="addEmoji" />
            <img src="@/assets/images/emoji.png" style="width: 30px;margin-right: 8px;cursor: pointer;"
              @click="toggleEmojiPicker" alt="">
          </div>

          <!-- <i class="el-icon-bangzhu" @click="toggleEmojiPicker" :title="$t('message.emojiTip')"></i> -->
          <el-popover placement="top-start" title="提示" style="margin-right:10px" width="200" trigger="hover"
            :content="$t('message.AiTipContent')">
            <el-button slot="reference" type="primary" size="small" icon="el-icon-question">
              {{ $t('message.AiTip') }}</el-button>
          </el-popover>
          <!-- 快捷预设回复模块 -->
          <el-button-group class="quick-reply-buttons">
            <el-tooltip class="item" effect="dark" :content="$t('message.butonTips.one')" placement="top-start">
              <el-button size="small" type="primary" @click="handleQuickReply(1)">1</el-button>
            </el-tooltip>
            <el-tooltip class="item" effect="dark" :content="$t('message.butonTips.two')" placement="top-start">
              <el-button size="small" type="primary" @click="handleQuickReply(2)">2</el-button>
            </el-tooltip>
            <el-tooltip class="item" effect="dark" :content="$t('message.butonTips.three')" placement="top-start">
              <el-button size="small" type="primary" @click="handleQuickReply(3)">3</el-button>
            </el-tooltip>
            <el-tooltip class="item" effect="dark" :content="$t('message.butonTips.four')" placement="top-start">
              <el-button size="small" type="primary" @click="handleQuickReply(4)">4</el-button>
            </el-tooltip>
            <el-tooltip class="item" effect="dark" :content="$t('message.butonTips.five')" placement="top-start">
              <el-button size="small" type="primary" @click="handleQuickReply(5)">5</el-button>
            </el-tooltip>
            <el-tooltip class="item" effect="dark" :content="$t('message.butonTips.six')" placement="top-start">
              <el-button size="small" type="primary" @click="handleQuickReply(6)">6</el-button>
            </el-tooltip>
          </el-button-group>
          <!-- <div class="auto-reply-switch">
            <el-switch v-model="enableAutoReply" active-text="AI自动回复" inactive-text=""></el-switch>
          </div> -->
        </div>
        <!-- 发送消息 -->
        <div class="input-box" >
          <el-input v-model="messageInput" type="textarea" :rows="3" :autosize="{ minRows: 3, maxRows: 6 }"
            :placeholder="$t('message.sendInputTip')" @keyup.enter.native="handleKeyup" />
          <div class="word-count" v-if="projectOptions"
            :class="{ 'exceed': messageInput.length > (projectOptions.maxSend || 100) }">
            {{ messageInput.length }}/{{ projectOptions.maxSend || 100 }}
          </div>
        </div>
        <div class="send-box">
          <el-button type="primary" @click="sendMessage">{{ $t('message.send') }}</el-button>
        </div>
      </div>
    </div>


    <!-- 右侧功能区 -->
    <div class="function-panel" v-if="currentContact">
      <div class="panel-header">
        <span>{{ $t('message.sessionTitle') }}</span>
      </div>
      <div class="panel-content">
        <div class="contact-profile">
          <el-avatar :size="80" :src="LogoImg">
          </el-avatar>
          <h3>{{ currentContact.custContact }}</h3>
        </div>
        <div class="quick-actions">
          <!-- <el-button type="text" icon="el-icon-document">聊天记录</el-button>
          <el-button type="text" icon="el-icon-picture">共享文件</el-button>
          <el-button type="text" icon="el-icon-setting">会话设置</el-button> -->
          <el-button type="primary" :plain="!currentContact.isPinned"
            :icon="`el-icon-${currentContact.isPinned ? 'bottom' : 'top'}`" @click="togglePin(currentContact)">{{
              currentContact.isPinned ? $t('message.cancelTop') : $t('message.toTopSession') }}</el-button>
          <el-button type="warning" icon="el-icon-star-off" :plain="!currentContact.isFavorite"
            @click="toggleStar(currentContact)">
            {{ currentContact.isFavorite ? $t('message.cancelStar') : $t('message.toStarSession') }}
          </el-button>
          <!-- <el-button type="danger" icon="el-icon-setting" @click="handleGenerateUI">生成手机界面(Beta)</el-button> -->
        </div>
      </div>
    </div>
    <PhoneLayout :visible="visibledPhone" :contact="currentContact" :messages="messages"
      @close="visibledPhone = false" />

  </div>
</template>

<script>
import commonLayoutMixin from "../mixin/commonLayoutMixin"
export default {
  mixins: [commonLayoutMixin],
}
</script>

<style lang="scss" scoped>
.quick-reply-dialog {
  ::v-deep .el-dialog__body {
    padding: 20px;
  }

  .reply-list {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
    padding: 8px;

    .reply-item {
      background-color: #f5f7fa;
      border-radius: 8px;
      padding: 16px;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        background-color: #ecf5ff;
        transform: translateY(-2px);
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
      }

      .reply-content {
        font-size: 14px;
        color: #303133;
        margin-bottom: 8px;
        line-height: 1.4;
      }

      .reply-desc {
        font-size: 12px;
        color: #909399;
      }
    }
  }
}

.fullScreen {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw !important;
  height: 100% !important;
  z-index: 1002;
}

.chat-container {
  display: flex;
  height: calc(100vh - 100px);
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

  .contact-list {
    width: 250px;
    border-right: 1px solid #ebeef5;
    display: flex;
    flex-direction: column;

    .search-box {
      padding: 16px;
      border-bottom: 1px solid #ebeef5;
      display: flex;
      align-items: center;
    }

    .contact-items {
      flex: 1;
      overflow-y: auto;
      padding-bottom: 20px;
      box-sizing: border-box;

      .contact-item {
        padding: 12px 16px;
        display: flex;
        align-items: center;
        cursor: pointer;
        transition: background-color 0.3s;

        &:hover {
          background-color: #f5f7fa;
        }

        &.active {
          .contact-name {
            color: #f7f7f7 !important;
          }

          .contact-message {
            color: #f7f7f7 !important;
          }

          .contact-time {
            color: #f7f7f7 !important;
          }
        }

        .contact-info {
          flex: 1;
          margin-left: 12px;
          overflow: hidden;

          .contact-name {
            font-size: 14px;
            color: #303133;
            margin-bottom: 4px;
          }

          .contact-message {
            font-size: 12px;
            color: #909399;
            white-space: nowrap;
            width: 100%;
            overflow: hidden;
            text-overflow: ellipsis;
          }
        }

        .contact-time {
          font-size: 12px;
          color: #909399;
          margin-top: 3px;
        }

        .action-icons {
          display: flex;
          align-items: center;
          justify-content: flex-end;
        }
      }

      .contact-items-loading {
        display: flex;
        align-items: center;
        justify-content: center;
        margin-top: 20px;
        font-size: 13px;
        color: #303133;

        >i {
          margin-right: 10px;
        }
      }
    }
  }

  .chat-main {
    flex: 1;
    display: flex;
    flex-direction: column;

    .chat-header {
      padding: 16px;
      border-bottom: 1px solid #ebeef5;
      display: flex;
      justify-content: space-between;
      align-items: center;

      .radio_button {
        position: relative;

        .red_point {
          position: absolute;
          width: 8px;
          height: 8px;
          background: rgb(245, 44, 44);
          border-radius: 50%;
          top: 3px;
          right: 3px;
          z-index: 1000;
        }
      }

      .chat-title {
        font-size: 16px;
        font-weight: bold;
        color: #303133;
        white-space: nowrap;
      }
    }

    .empty-state {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      color: #909399;

      i {
        font-size: 48px;
        margin-bottom: 16px;
      }

      p {
        font-size: 16px;
        margin: 0;
      }
    }

    .message-list {
      flex: 1;
      padding: 16px;
      overflow-y: auto;

      .message-load-ref {
        display: flex;
        align-items: center;
        margin: 10px 0px;
        justify-content: center;
        font-size: 13px;
        color: #909399;
      }

      .message-item {
        display: flex;
        margin-bottom: 16px;

        &.self {
          flex-direction: row-reverse;

          .message-content {
            margin-right: 12px;
            margin-left: 0;

            .message-bubble {
              background-color: #409eff;
              color: #fff;
              border-radius: 8px 2px 8px 8px;

            }
          }
        }

        &.other .message-content {
          margin-left: 12px;

          .message-bubble {
            background-color: #f4f4f5;
            color: #303133;
            border-radius: 2px 8px 8px 8px;
            position: relative;

            .message-actions {
              position: absolute;
              right: -30px;
              top: 50%;
              transform: translateY(-50%);
              opacity: 0;
              transition: opacity 0.3s;

              i {
                cursor: pointer;
                color: #909399;
                font-size: 16px;
                margin-left: 8px;

                &:hover {
                  color: #409eff;
                }
              }
            }

            &:hover .message-actions {
              opacity: 1;
            }

            .translation {
              margin-top: 8px;
              padding-top: 8px;
              border-top: 1px solid #e4e7ed;
              color: #606266;
              font-size: 13px;
            }
          }
        }

        .message-content {
          max-width: 60%;

          .message-bubble {
            padding: 10px 16px;
            word-break: break-word;
            & img {
              width: 250px;
            }
          }

          .message-tip {
            display: flex;
            align-items: center;
            justify-content: right;
            margin-top: 5px;

          }

          .message-time {
            font-size: 12px;
            color: #909399;
            margin-top: 4px;
            text-align: right;
          }

          .message-load {
            font-size: 12px;
            color: #909399;
            display: flex;
            align-items: center;
            margin-left: 15px;
            text-align: right;
          }

          .message-error {
            font-size: 12px;
            color: #f50909;
            display: flex;
            align-items: center;
            margin-left: 15px;
            text-align: right;
          }
        }
      }
    }

    .input-area {
      border-top: 1px solid #ebeef5;

      .toolbar {
        padding: 8px 16px;
        border-bottom: 1px solid #ebeef5;
        display: flex;
        align-items: center;

        .emoji {
          position: relative;

          .emoji_picker {
            position: absolute;
            bottom: 40px;
            left: -40px;
            z-index: 10;
          }

          .emoji_marker {
            position: fixed;
            top: 0px;
            left: 0px;
            width: 100vw;
            height: 100vh;
            z-index: 9;
          }
        }

        i {
          font-size: 20px;
          color: #606266;
          margin-right: 16px;
          cursor: pointer;
          transition: color 0.3s;

          &:hover {
            color: #409eff;
          }
        }

        .auto-reply-switch {
          margin-left: auto;
        }
      }

      .input-box {
        padding: 16px;
        position: relative;
        margin-bottom: 10px;

        ::v-deep .el-textarea__inner {
          resize: none;
          min-height: 80px;
          max-height: 150px;
          overflow-y: auto;
          transition: all 0.3s;
        }

        .word-count {
          position: absolute;
          bottom: 0px;
          right: 24px;
          font-size: 12px;
          color: #909399;

          &.exceed {
            color: #f56c6c;
          }
        }
      }

      .send-box {
        padding: 0 16px 16px;
        text-align: right;
      }
    }
  }

  .function-panel {
    width: 200px;
    border-left: 1px solid #ebeef5;

    .panel-header {
      padding: 16px;
      border-bottom: 1px solid #ebeef5;
      font-size: 16px;
      font-weight: bold;
      color: #303133;
    }

    .panel-content {
      padding: 16px;

      .contact-profile {
        text-align: center;
        padding: 16px 0;

        h3 {
          margin: 12px 0;
          color: #303133;
        }
      }

      .quick-actions {
        display: flex;
        flex-direction: column;
        gap: 8px;

        .el-button {
          // justify-content: flex-start;
          // padding: 8px 0;
          margin-left: 0px;

          i {
            margin-right: 8px;
          }
        }
      }
    }
  }
}
</style>
