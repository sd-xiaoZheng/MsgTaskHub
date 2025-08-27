<!-- 手机界面 -->
<template>
    <div>
        <div class="phone-overlay" v-if="visible" @click.self="$emit('close')">
            <div>
                <div class="phone-container">
                    <div class="phone-header">
                        <div class="status-bar">
                            <span>20:00</span>
                            <div class="status-icons">
                                <i class="el-icon-connection"></i>
                                <i class="el-icon-wifi"></i>
                                <i class="el-icon-battery"></i>
                            </div>
                        </div>
                        <div class="chat-header">
                            <i class="el-icon-arrow-left" @click="$emit('close')"></i>
                            <span class="contact-name">User</span>
                            <i class="el-icon-more"></i>
                        </div>
                    </div>
                    <div class="message-list">
                        <div v-for="(message, index) in messages" :key="index"
                            :class="['message-item', message.chatInout === -1 ? 'sent' : 'received']">
                            <!-- <div class="avatar">
                                <el-avatar icon="el-icon-user-solid"></el-avatar>
                            </div> -->
                            <div class="message-content">
                                <div class="message-bubble">{{ message.messageContent }}</div>
                                <div class="message-time">{{ message.sendTime }}</div>
                            </div>
                        </div>
                    </div>

                    <div class="input-area">
                        <div class="input-box">
                            <i class="el-icon-mic"></i>
                            <div>
                                Input Message...
                            </div>
                            <!-- <input type="text" placeholder=""> -->
                            <i class="el-icon-plus"></i>
                        </div>

                    </div>
                </div>

                <div class="download-button">
                    <el-button type="primary" icon="el-icon-download" @click="downloadImage">保存聊天截图</el-button>
                    <el-button type="info" icon="el-icon-close" @click="$emit('close')">关闭页面</el-button>
                </div>
            </div>

        </div>
    </div>

</template>

<script>
import html2canvas from 'html2canvas'

export default {
    name: 'Phone',
    props: {
        visible: {
            type: Boolean,
            default: false
        },
        messages: {
            default: []
        },
        contact: {
            type: Object,
            default: () => ({})
        }
    },
    data() {
        return {
        }
    },
    methods: {
        downloadImage() {
            const element = this.$el.querySelector('.phone-container')
            html2canvas(element).then(canvas => {
                const link = document.createElement('a')
                link.download = 'chat-screenshot.png'
                link.href = canvas.toDataURL()
                link.click()
            })
        }
    }
}
</script>

<style lang="scss" scoped>
.phone-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 9999;
}

.phone-container {
    height: calc(100vh - 100px);
    aspect-ratio: 390/844; //Iphone 12x 比例缩放
    background-color: #f7f7f7;
    // background-color: #f30000;
    // border-radius: 20px;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    // box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2);
}

.phone-header {
    background-color: #fff;

    .status-bar {
        height: 24px;
        background-color: #f7f7f7;
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0 16px;
        font-size: 12px;

        .status-icons {
            display: flex;
            gap: 8px;
        }
    }

    .chat-header {
        height: 44px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0 16px;
        border-bottom: 1px solid #eee;

        .contact-name {
            font-size: 16px;
            font-weight: 500;
        }

        i {
            font-size: 20px;
            color: #666;
            cursor: pointer;
        }
    }
}

.message-list {
    flex: 1;
    overflow-y: auto;
    padding: 16px;

    .message-item {
        display: flex;
        margin-bottom: 16px;

        &.received {
            .message-bubble {
                background-color: #fff;
                border-radius: 0 15px 15px 20px;
            }
        }

        &.sent {
            flex-direction: row-reverse;

            .message-bubble {
                background-color: #46a3ee;
                border-radius: 15px 0 20px 15px;
            }

            .message-time {
                text-align: right;
            }
        }

        .avatar {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            overflow: hidden;
            flex-shrink: 0;

            img {
                width: 100%;
                height: 100%;
                object-fit: cover;
            }
        }

        .message-content {
            margin: 0 12px;
            max-width: 70%;

            .message-bubble {
                padding: 12px;
                font-size: 14px;
                line-height: 1.4;
                word-break: break-word;
            }

            .message-time {
                font-size: 12px;
                color: #999;
                margin-top: 4px;
            }
        }
    }
}

.input-area {
    background-color: #f7f7f7;
    padding: 12px;
    border-top: 1px solid #eee;

    .input-box {
        background-color: #fff;
        border-radius: 20px;
        display: flex;
        align-items: center;
        padding: 8px 12px;

        i {
            font-size: 20px;
            color: #666;
            cursor: pointer;
        }

        >div {
            color: #999;
            flex: 1;
            margin: 0 12px;
            font-size: 14px;
        }

    }


}

.download-button {
    margin-top: 10px;
    text-align: center;
}
</style>