<template>
    <div>
        <div class="simple-overlay" v-if="visible" @click.self="$emit('close')">
            <div>
                <div class="simple-container">
                    <div class="simple-header">
                        <!-- <div class="status-bar">
                            <span>20:00</span>
                            <div class="status-icons">
                                <i class="el-icon-connection"></i>
                                <i class="el-icon-wifi"></i>
                                <i class="el-icon-battery"></i>
                            </div>
                        </div> -->
                        <div class="status-bar">
                           
                        </div>
                        <div class="chat-header">
                            <i class="el-icon-arrow-left"  @click="$emit('close')"></i>
                            <span class="contact-name">{{ contact.custContact }}</span>
                            <i class="el-icon-more"></i>
                        </div>
                    </div>
                    <div class="message-list">
                        <div v-for="(message, index) in messages" :key="index"
                            :class="['message-item', message.chatInout === -1 ? 'sent' : 'received']">
                            <div class="avatar" v-show="index == 0 || (messages[index-1].chatInout != message.chatInout)">
                                <img src="/img/a3.jpg"  />
                            </div>
                            <div class="message-content">
                                <div class="message-bubble">{{ message.messageContent }}</div>
                                <div class="message-time">{{ message.sendTime }}</div>
                            </div>
                        </div>
                    </div>
                    <div class="input-area">
                        <div class="input-box">
                            <i class="el-icon-picture"></i>
                            <div class="input-placeholder">Input Message...</div>
                            <i class="el-icon-link"></i>
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
    name: 'SimplePhone',
    props: {
        visible: {
            type: Boolean,
            default: false
        },
        messages: {
            type: Array,
            default: () => []
        },
        contact: {
            type: Object,
            default: () => ({})
        }
    },
    methods: {
        downloadImage() {
            const element = this.$el.querySelector('.simple-container')
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
.simple-overlay {
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

.simple-container {
    height: calc(100vh - 200px);
    aspect-ratio: 390/844;
    background-color: #fafafa;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    // border-radius: 12px;
}

.simple-header {
    background-color: #fff;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);

    .status-bar {
        height: 24px;
        background-color: #ffffff;
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
        background-color: white;
        .contact-name {
            font-size: 16px;
            font-weight: 500;
            position: relative;
            &::after{
                content: '';
                position: absolute;
                width: 8px;
                height: 8px;
                top: 50%;
                transform: translateY(-50%);
                left: -15px;
                border-radius: 50%;
                background: #23ff91;
            }
        }

        i {
            font-size: 17px;
            color: #c5c5c5;
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
        // align-items: flex-start; 
        flex-direction: column;
        &.received {
            .message-bubble {
                background-color: rgb(59,65,74);
                border-radius: 0 12px 12px 12px;
                color: #ececec;
            }
        }

        &.sent {
            // flex-direction: row-reverse;
            align-items: flex-end;
            .message-bubble {
                background-color: white;
                border-radius: 12px 0 12px 12px;
                color: #333333;
            }

            .message-time {
                text-align: right;
            }
        }

        .avatar {
            margin: 0 8px;
            >img{
                width:40px;
                border-radius:50%;
                height:40px;
            }
        }

        .message-content {
            max-width: 70%;

            .message-bubble {
                padding: 13px 17px;
                font-size: 14px;
                line-height: 1.4;
                word-break: break-word;
            }

            .message-time {
                font-size: 12px;
                color: #999;
                margin-top: 4px;
                padding: 0 4px;
            }
        }
    }
}

.input-area {
    background-color: #fff;
    padding:0px 12px;
    // border-top: 1px solid #eee;
    .input-box {
        // border-radius: 24px;
        display: flex;
        align-items: center;
        height: 60px;
        i {
            font-size: 20px;
            color: #666;
            cursor: pointer;
        }

        .input-placeholder {
            flex: 1;
            margin: 0 12px;
            color: #999;
            font-size: 14px;
        }
    }
}

.download-button {
    margin-top: 16px;
    text-align: center;
}
</style>