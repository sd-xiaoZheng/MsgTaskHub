<!-- AblePro Chat布局界面 -->
<template>
    <div class="chat-container">
        <div class="chat-contactPanel">
            <div class="chat-contactPanel-header">
                <div class="chat-contactPanel-header-title">联系人</div>
                <!-- 群聊，我的会话，收藏的选择 -->
                <el-dropdown class="chat-contactPanel-header-dropdown" @command="handleChangeSession">
                    <el-button type="text">
                        群聊选择
                        <i class="el-icon-arrow-down el-icon--right"></i>
                    </el-button>
                    <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item command="personal">消息</el-dropdown-item>
                        <el-dropdown-item command="group">群发</el-dropdown-item>
                        <el-dropdown-item command="star">关注</el-dropdown-item>
                        <el-dropdown-item>群组</el-dropdown-item>
                    </el-dropdown-menu>
                </el-dropdown>
            </div>
            <div class="chat-contactPanel-search">
                <el-input type="text" prefix-icon="el-icon-search" placeholder="请输入关键词"></el-input>
            </div>
            <div class="chat-contactPanel-list">
                <!-- 联系人列表 -->
                <div class="chat-contactPanel-item"
                    :class="{ active: currentContact && (currentContact.sessionId === item.sessionId), 'isTop': item.isPinned === 1 }"
                    v-for="(item, index) in sessionContacts" :key="item.sessionId" @click="selectContact(item)">
                    <div class="chat-contactPanel-item-avatar">
                        <img src="@/assets/images/avatar.png" alt />
                    </div>
                    <div class="chat-contactPanel-item-info">
                        <div class="chat-contactPanel-item-info-name">{{ item.custContact }}</div>
                        <div class="chat-contactPanel-item-info-message">
                            {{ item.endText }}
                        </div>
                    </div>
                    <div class="chat-contactPanel-item-other">
                        <div class="chat-contactPanel-item-time">
                            {{ templateTime(item.latestChatTime) }}
                        </div>
                        <div class="chat-contactPanel-item-unread">
                            <el-badge type="primary" v-if="item.messageCount > 0" :value="item.messageCount" />
                        </div>
                    </div>
                </div>

                <div class="contact-items-empty" v-if="sessionContacts.length === 0">
                    <img src="@/assets/images/empty.png" alt="" />
                    消息为空
                </div>

                <div class="contact-items-loading" ref="contactLoading">
                    <!-- 加载中 -->
                    <!-- <template v-if="reachBootom === 'more'">
                        {{ $t('message.reachMore') }}
                    </template> -->
                    <template v-if="reachBootom === 'loading'">
                        <i class="el-icon-loading"></i>
                        {{ $t('message.loading') }}
                    </template>
                    <!-- <template v-if="reachBootom === 'none'">
                        {{ $t('message.none') }}
                    </template> -->
                </div>
            </div>
        </div>

        <!-- 消息记录的面板 -->
        <div class="chat-containerPanel" v-if="currentContact">
            <div class="chat-containerPanel-header">
                <!-- 头部 -->
                <div class="chat-containerPanel-header-listMenu">
                    <i class="el-icon el-icon-s-unfold"></i>
                </div>
                <div class="chat-containerPanel-header-info">
                    <div class="chat-containerPanel-header-info-avatar">
                        <img src="@/assets/images/avatar.png" alt />
                    </div>
                    <div class="chat-containerPanel-header-info-name">
                        {{ currentContact.custContact }}
                    </div>
                </div>
                <div class="chat-containerPanel-header-fun">
                    <div>
                        <img src="@/assets/images/top.png" alt="" />
                    </div>
                    <div>
                        <img src="@/assets/images/star.png" alt="" />
                    </div>
                    <div>
                        <i class="el-icon el-icon-full-screen"></i>
                    </div>
                    <div>
                        <i class="el-icon el-icon-info"></i>
                    </div>
                </div>
            </div>
            <!-- 消息记录面板 -->
            <div class="chat-containerPanel-main" v-loading="recordLoading" ref="messageList">
                <div class="chat-containerPanel-main-load" ref="messageLoad">
                    <template v-if="pullDownMessage === 'more'">
                        {{ $t('message.pullMore') }}
                    </template>
                    <!-- <template v-if="pullDownMessage === 'none'">
                        {{ $t('message.none') }}
                    </template> -->
                    <template v-if="pullDownMessage === 'loading'">
                        <i class="el-icon-loading" style="margin-right: 8px;"></i>
                        {{ $t('message.loading') }}
                    </template>
                </div>
                <div class="chat-containerPanel-main-item" :class="{ mySelf: item.chatInout === -1 }"
                    v-for="(item, index) in messages" :key="item.chatId">
                    <div class="chat-containerPanel-main-item-avatar" v-if="item.chatInout === 1">
                        <!-- 头像 -->
                        <img v-if="(index == 0 || (messages[index - 1].chatInout != item.chatInout))"
                            src="@/assets/images/avatar.png" alt />
                    </div>
                    <div class="chat-containerPanel-main-item-content">
                        <div class="chat-containerPanel-main-item-content-info"
                            v-if="(index == 0 || (messages[index - 1].chatInout != item.chatInout))">
                            <div>{{ currentContact.custContact }}</div>
                            <div>{{ templateTime(item.sendTime) }}</div>
                        </div>
                        <!-- 消息内容 -->
                        <div class="chat-containerPanel-main-item-content-text" v-if="getMessageType(item) === 'text'">
                            {{ item.messageContent }}
                        </div>

                        <!-- 消息内容 -->
                        <div class="chat-containerPanel-main-item-content-text" v-if="getMessageType(item) === 'img'">
                            <img :src="item.messageContent" alt="" />
                        </div>
                    </div>
                </div>
            </div>

            <!-- 底部发消息的工具栏 -->
            <div class="chat-containerPanel-footer">
                <div class="chat-containerPanel-footer-input">
                    <el-input type="textarea" placeholder="请输入消息" :rows="1"></el-input>
                </div>

                <div class="chat-containerPanel-footer-toolbar">
                    <div class="chat-containerPanel-footer-emoji">
                        <div class="emoji_marker" v-show="showEmojiPicker" @click="toggleEmojiPicker"></div>
                        <emoji-picker class="emoji_picker" v-show="showEmojiPicker" @select="addEmoji" />
                        <img src="@/assets/images/emoji.png" @click="toggleEmojiPicker" alt />
                    </div>
                    <!-- 回复列表 -->
                    <!-- <el-button-group class="chat-containerPanel-footer-buttons">
                        <el-tooltip
                            class="item"
                            effect="dark"
                            :content="$t('message.butonTips.one')"
                            placement="top-start"
                        >
                            <el-button size="small" type="primary" @click="handleQuickReply(1)">1</el-button>
                        </el-tooltip>
                        <el-tooltip
                            class="item"
                            effect="dark"
                            :content="$t('message.butonTips.two')"
                            placement="top-start"
                        >
                            <el-button size="small" type="primary" @click="handleQuickReply(2)">2</el-button>
                        </el-tooltip>
                        <el-tooltip
                            class="item"
                            effect="dark"
                            :content="$t('message.butonTips.three')"
                            placement="top-start"
                        >
                            <el-button size="small" type="primary" @click="handleQuickReply(3)">3</el-button>
                        </el-tooltip>
                        <el-tooltip
                            class="item"
                            effect="dark"
                            :content="$t('message.butonTips.four')"
                            placement="top-start"
                        >
                            <el-button size="small" type="primary" @click="handleQuickReply(4)">4</el-button>
                        </el-tooltip>
                        <el-tooltip
                            class="item"
                            effect="dark"
                            :content="$t('message.butonTips.five')"
                            placement="top-start"
                        >
                            <el-button size="small" type="primary" @click="handleQuickReply(5)">5</el-button>
                        </el-tooltip>
                        <el-tooltip
                            class="item"
                            effect="dark"
                            :content="$t('message.butonTips.six')"
                            placement="top-start"
                        >
                            <el-button size="small" type="primary" @click="handleQuickReply(6)">6</el-button>
                        </el-tooltip>
                    </el-button-group> -->
                    <!-- 发送按钮 -->
                    <div class="chat-contanerPanel-footer-send">
                        <i class="el-icon-s-promotion"></i>
                    </div>
                </div>
            </div>
        </div>
        <div class="chat-containerPanel-empty" v-else>
            <img src="@/assets/images/select.png" style="width: 200px;margin-bottom: 20px;" alt="" />
            <p style="font-size: 15px;color: #444444;">
                请选择联系人
            </p>
        </div>
        <!-- 消息面板 -->
        <div class="chat-infoPanel">

        </div>
    </div>
</template>

<script>
import commonLayoutMixin from "../mixin/commonLayoutMixin"
export default {
    mixins: [commonLayoutMixin],
    methods: {
        handleChangeSession(e) {
            this.activeTab = e;
        }
    }
}
</script>

<style lang="scss" scoped>
$borderColor: #e4e4e4;
$hover: #f4f4f4;

.chat-container {
    display: flex;
    align-items: center;
    justify-content: center;
    column-gap: 30px;
    height: calc(100vh - 150px);

    >div {
        background-color: white;
        border: 1px solid #e7e7e7;
        box-shadow: 0px 0px 8px #f1f1f1;
        border-radius: 8px;
        height: inherit;
    }

    .chat-contactPanel {
        width: 300px;
        display: flex;
        flex-direction: column;
        padding: 25px 0px;

        .chat-contactPanel-header {
            display: flex;
            justify-content: space-between;
            padding: 0 25px;
            box-sizing: border-box;
            margin-bottom: 25px;

            .chat-contactPanel-header-title {
                font-size: 20px;
                font-weight: bold;
            }
        }

        .chat-contactPanel-search {
            padding: 0px 25px;
            box-sizing: border-box;
            margin-bottom: 13px;
        }

        .chat-contactPanel-list {
            flex: 1;
            overflow-y: auto;

            .contact-items-empty {
                display: flex;
                align-items: center;
                justify-content: center;
                flex-direction: column;
                font-size: 12px;
                color: #7c7c7c;

                & img {
                    width: 120px;
                    margin-bottom: 10px;
                }
            }

            .chat-contactPanel-item {
                padding: 15px;
                box-sizing: border-box;
                display: flex;
                border-bottom: 1px solid #e4e4e4;
                cursor: pointer;
                user-select: none; //禁止选中文字

                &.isTop {
                    position: relative;

                    &::before {
                        content: "置顶";
                        position: absolute;
                        top: 0;
                        right: 0;
                        background: #4680ff;
                        color: white;
                        padding: 2px 8px;
                        font-size: 12px;
                        border-radius: 0 0 0 8px;
                    }
                }

                &.active {
                    background-color: $hover;
                }

                &:hover {
                    background-color: $hover;
                }

                .chat-contactPanel-item-avatar {
                    width: 55px;
                    height: 55px;

                    >img {
                        width: 55px;
                        height: 55px;
                        border-radius: 50%;
                        -webkit-user-drag: none; //禁止拖拽图片
                    }

                    overflow: hidden;
                }

                .chat-contactPanel-item-info {
                    flex: 1;
                    display: flex;
                    flex-direction: column;

                    .chat-contactPanel-item-info-name {
                        font-size: 15px;
                        font-weight: bold;
                        margin-top: 5px;
                    }

                    .chat-contactPanel-item-info-message {
                        font-size: 12px;
                        color: #888888;
                        margin-top: auto;
                        margin-bottom: 10px;
                        white-space: nowrap;
                        overflow: hidden;
                        text-overflow: ellipsis;
                        max-width: 120px;
                    }
                }

                .chat-contactPanel-item-other {
                    display: flex;
                    flex-direction: column;
                    align-items: flex-end;
                    justify-content: space-around;

                    .chat-contactPanel-item-time {
                        font-size: 13px;
                        color: #888888;
                    }

                    .chat-contactPanel-item-unread {
                        font-size: 13px;
                        color: #888888;

                        .unread-badge {}
                    }
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
    .chat-containerPanel-empty{
        display: flex;
        align-items: center;
        justify-content: center;
        flex-direction: column;
        flex:1;
    }
    .chat-containerPanel {
        box-sizing: border-box;
        display: flex;
        flex-direction: column;
        flex: 1;

        .chat-containerPanel-header {
            display: flex;
            align-items: center;
            padding: 16px;
            border-bottom: 1px solid $borderColor;

            .hover-link {
                padding: 10px;
                border-radius: 10px;
                transition: background 0.2s ease-in-out;
                cursor: pointer;

                &:hover {
                    background: $hover;
                }
            }

            .chat-containerPanel-header-listMenu {
                @extend .hover-link;
                margin-right: 15px;
                cursor: pointer;

                >i {
                    font-size: 20px;
                }
            }

            .chat-containerPanel-header-info {
                display: flex;
                align-items: center;

                .chat-containerPanel-header-info-avatar {
                    margin-right: 15px;
                    width: 40px;
                    height: 40px;

                    & img {
                        width: inherit;
                        height: inherit;
                        border-radius: 50%;
                    }
                }

                .chat-containerPanel-header-info-name {
                    font-size: 15px;
                }
            }

            .chat-containerPanel-header-fun {
                display: flex;
                margin-left: auto;
                column-gap: 10px;

                >i {
                    @extend .hover-link;
                }

                >div {
                    @extend .hover-link;
                    width: 40px;
                    height: 40px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                }

                & img {
                    width: 17px;
                }
            }
        }

        .chat-containerPanel-main {
            padding: 0px 16px;
            padding-top: 20px;
            box-sizing: border-box;
            flex: 1;
            overflow-y: auto;

            .chat-containerPanel-main-load {
                display: flex;
                align-items: center;
                margin: 10px 0px;
                justify-content: center;
                font-size: 13px;
                color: #909399;
            }

            .mySelf {
                flex-direction: row-reverse;

                .chat-containerPanel-main-item-content {
                    align-items: flex-end !important;
                }

                .chat-containerPanel-main-item-content-text {
                    background: #4680ff;
                    color: white !important;
                    border-radius: 10px 0px 10px 10px !important;
                    & img{
                        width: 180px;
                    }
                }
            }

            .chat-containerPanel-main-item {
                display: flex;
                margin-bottom: 7px;

                .chat-containerPanel-main-item-avatar {
                    width: 50px;
                    margin-right: 10px;

                    >img {
                        width: inherit;
                        border-radius: 50%;
                    }
                }

                .chat-containerPanel-main-item-content {
                    display: flex;
                    flex-direction: column;
                    align-items: flex-start;
                    max-width: 60%;

                    .chat-containerPanel-main-item-content-info {
                        display: flex;
                        align-items: center;
                        column-gap: 10px;
                        font-size: 13px;
                        color: #494949;
                        margin-bottom: 5px;
                    }

                    .chat-containerPanel-main-item-content-text {
                        border: 1px solid $borderColor;
                        padding: 10px 15px;
                        border-radius: 0px 10px 10px 10px;
                        color: #1f1f1f;
                    }
                }
            }
        }

        .chat-containerPanel-footer {
            border-top: 1px solid $borderColor;
            padding: 16px;

            .chat-containerPanel-footer-input {
                margin-bottom: 10px;
                padding-bottom: 10px;
                border-bottom: 1px solid rgb(194, 194, 194);

                ::v-deep .el-textarea__inner {
                    padding: 10px 15px;
                    box-sizing: border-box;
                    border-radius: 5px;
                    margin-bottom: 10px;
                    border: none;
                    height: 65px;

                    &:hover {
                        background: #f5f5f5;
                    }

                    &:focus {
                        background: #f5f5f5;
                    }
                }
            }

            .chat-containerPanel-footer-toolbar {
                display: flex;
                column-gap: 10px;
                align-items: center;

                .hover-link {
                    padding: 10px;
                    box-sizing: border-box;
                    border-radius: 10px;
                    transition: all 0.3s;
                    cursor: pointer;

                    &:hover {
                        background: $hover;
                    }
                }

                .chat-containerPanel-footer-emoji {
                    position: relative;
                    width: 40px;
                    height: 40px;

                    & img {
                        @extend .hover-link;
                        width: inherit;
                        height: inherit;
                    }

                    .emoji_marker {
                        position: fixed;
                        top: 0px;
                        left: 0px;
                        width: 100vw;
                        height: 100vh;
                        z-index: 9;
                    }

                    .emoji_picker {
                        position: absolute;
                        bottom: 40px;
                        left: -40px;
                        z-index: 10;
                    }
                }

                .chat-containerPanel-footer-buttons {
                    @extend.hover-link;
                }

                .chat-contanerPanel-footer-send {
                    @extend.hover-link;
                    margin-left: auto;

                    &:hover {
                        background-color: #4681ff3f !important;
                    }

                    >i {
                        color: #4680ff;
                        font-size: 20px;
                    }
                }
            }
        }
    }
}
</style>