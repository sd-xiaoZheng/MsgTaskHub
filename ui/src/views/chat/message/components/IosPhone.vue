<template>
    <div>
        <div class="ios-overlay" v-if="visible" @click.self="$emit('close')">
            <div>

                <div class="ios-container">
                    <div class="ios-header">
                        <div class="ios-status-bar">
                            <span>20:00</span>
                            <div class="status-icons">
                                <i class="el-icon-connection"></i>
                                <i class="el-icon-wifi"></i>
                                <i class="el-icon-battery"></i>
                            </div>
                        </div>
                        <div class="ios-chat-header">
                            <i class="el-icon-arrow-left" @click="$emit('close')"></i>
                            <span class="ios-contact-name">{{ contact.custContact }}</span>
                            <i class="el-icon-more"></i>
                        </div>
                    </div>

                    <div class="ios-message-list">
                        <div v-for="(message, index) in messages" :key="index"
                            :class="['ios-message-item', message.chatInout === -1 ? 'ios-sent' : 'ios-received']">
                            <div class="ios-avatar">
                                <el-avatar icon="el-icon-user-solid" size="medium"></el-avatar>
                                <!-- <img src="@/assets/logo/logo.jpg" alt="avatar"> -->
                            </div>
                            <div class="ios-message-content">
                                <div class="ios-message-bubble">{{ message.messageContent }}</div>
                                <div class="ios-message-time">{{ message.sendTime }}</div>
                            </div>
                        </div>
                    </div>

                    <div class="ios-input-area">
                        <div class="ios-input-box">
                            <i class="el-icon-mic"></i>
                            <div>Input Message...</div>
                            <i class="el-icon-plus"></i>
                        </div>
                    </div>
                </div>

                <div class="ios-download-button">
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
    name: 'IosPhone',
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
    methods: {
        downloadImage() {
            const element = this.$el.querySelector('.ios-container')
            html2canvas(element).then(canvas => {
                const link = document.createElement('a')
                link.download = 'ios-chat-screenshot.png'
                link.href = canvas.toDataURL()
                link.click()
            })
        }
    }
}
</script>

<style lang="scss" scoped>
.ios-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.7);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 9999;
}

.ios-container {
    height: calc(100vh - 100px);
    aspect-ratio: 390/844; //Iphone 12x 比例缩放
    background-color: #1c1c1e;
    // border-radius: 40px;
    overflow: hidden;
    display: flex;
    flex-direction: column;
}

.ios-header {
    background-color: #2c2c2e;
    padding-top: 8px;

    .ios-status-bar {
        height: 24px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0 16px;
        font-size: 12px;
        color: #ffffff;

        .ios-status-icons {
            display: flex;
            gap: 8px;
            color: #ffffff;
        }
    }

    .ios-chat-header {
        height: 44px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0 16px;
        border-bottom: 1px solid #3a3a3c;

        .ios-contact-name {
            font-size: 16px;
            font-weight: 500;
            color: #ffffff;
        }

        i {
            font-size: 20px;
            color: #ffffff;
            cursor: pointer;
        }
    }
}

.ios-message-list {
    flex: 1;
    overflow-y: auto;
    padding: 16px;

    .ios-message-item {
        display: flex;
        margin-bottom: 16px;

        &.ios-received {
            .ios-message-bubble {
                background-color: #2c2c2e;
                border-radius: 20px 20px 20px 4px;
                color: #ffffff;
            }
        }

        &.ios-sent {
            flex-direction: row-reverse;

            .ios-message-bubble {
                background-color: #0a84ff;
                border-radius: 20px 20px 4px 20px;
                color: #ffffff;
            }

            .ios-message-time {
                text-align: right;
                color: #8e8e93;
            }
        }

        .ios-avatar {
            // width: 36px;
            // height: 36px;
            // border-radius: 50%;
            // overflow: hidden;
            // flex-shrink: 0;

            // img {
            //     width: 100%;
            //     height: 100%;
            //     object-fit: cover;
            // }
        }

        .ios-message-content {
            margin: 0 12px;
            max-width: 70%;

            .ios-message-bubble {
                padding: 12px;
                font-size: 14px;
                line-height: 1.4;
                word-break: break-word;
            }

            .ios-message-time {
                font-size: 12px;
                color: #8e8e93;
                margin-top: 4px;
            }
        }
    }
}

.ios-input-area {
    background-color: #2c2c2e;
    padding: 12px;
    border-top: 1px solid #3a3a3c;

    .ios-input-box {
        background-color: #3a3a3c;
        border-radius: 20px;
        display: flex;
        align-items: center;
        padding: 8px 12px;

        i {
            font-size: 20px;
            color: #ffffff;
            cursor: pointer;
        }

        >div {
            color: #8e8e93;
            flex: 1;
            margin: 0 12px;
            font-size: 14px;
        }
    }
}

.ios-download-button {
    margin-top: 16px;
    text-align: center;

    .el-button {
        border-radius: 20px;
        padding: 12px 24px;
        font-weight: 500;
    }
}
</style>