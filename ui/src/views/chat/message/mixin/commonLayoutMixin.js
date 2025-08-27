
import { randomText } from '@/api/chat/PresetReply'
import { myMassMessage, mySession, favoriteSession, topSession, myFavoriteSession, readSession, sessionUnread } from '@/api/chat/SessionRecord'
import { listChatRecord } from '@/api/chat/ChatRecord'
import { mapGetters } from 'vuex'
import WebSocketClient from '@/api/chat/websocket'
import translate from 'i18n-jsautotranslate'
import { Picker as EmojiPicker } from 'emoji-mart-vue'
import 'emoji-mart-vue/css/emoji-mart.css'
import PhoneLayout from '../components/PhoneLayout.vue'
import logo from '@/assets/logo/logo.jpg'
export default {
  name: 'BasicMessageLayout',
  components: {
    EmojiPicker, PhoneLayout
  },
  data() {
    return {
      // 显示手机截图
      visibledPhone: false,
      LogoImg: logo,
      chatLoading: false,
      /**
       * @param {string} activeTab  --当前的会话类型  personal 个人会话  group 群聊会话  star 收藏会话
       */
      activeTab: 'personal',
      showEmojiPicker: false,
      messageInput: '',
      /**
       * @param {object} currentContact
       * @param {string} currentContact.sessionId    --会话id 
       * @param {string} currentContact.custContact  --联系人名称
       * @param {string} currentContact.endText  --最后的消息
       * @param {string} currentContact.latestChatTime  --最后的对话时间
       * @param {string} currentContact.isPinned  --是否置顶
       * @param {string} currentContact.isFavorite  --是否收藏
       */
      currentContact: null,
      // 开启翻译
      showTranslation: false,
      enableAutoReply: false,
      // 会话列表
      sessionContacts: [
      ],
      // 消息聊天记录列表
      messages: [],
      querySessionParams: {
        pageNum: 1,
        pageSize: 20,
        projectId: null,
        custId: null,
        reasonable: false,
      },
      isFullScreen: false, //0 不是全屏，1是全屏
      loadingSession: false,
      reachBootom: "more",
      queryChatRecordParams: {
        pageNum: 1,
        pageSize: 20,
        sessionId: null,
        custId: null,
        reasonable: false,
      },
      recordLoading: false,//消息界面的聊天记录加载
      pullDownMessage: 'more',//下拉加载消息数据的状态
      pullDownTopHeight: "",//下拉加载固定当前滚动条
      searchTimer: null,//搜索定时器
      userSessionId: null,
      sessionCounts: null,
      unreadPersonal: false,//会话是否有未读
      unreadStar: false,//收藏会话是否有未读
    }
  },
  computed: {
    ...mapGetters(['userId', 'projectId', 'projectOptions', 'messageContactData']),
  },
  created() {
    this.userSessionId = this.$route.query.userId

    if (this.userSessionId) {
      this.querySessionParams.custId = this.userSessionId;
      this.querySessionParams.projectId = this.projectId;
      this.getSessionList();
      this.getCountSession();
      this.$websocket.close();
      this.$websocket.connect(this.userSessionId);
      // this.initWebsocket(); // 初始化WebSocket连接
    } else if (this.userId) {
      this.userSessionId = this.userId
      this.querySessionParams.custId = this.userSessionId;
      this.querySessionParams.projectId = this.projectId;
      this.getSessionList();
      // this.initWebsocket(); // 初始化WebSocket连接
      this.getCountSession()
    }
    this.$nextTick(() => {
      // 监听上拉的加载的会话
      const sessionObserver = new IntersectionObserver(entries => {
        console.log(entries)
        if (entries[0].intersectionRatio <= 0) return
        if (this.reachBootom == "more" && !this.loadingSession) {
          this.reachBootom = "loading";
          this.querySessionParams.pageNum++;
          this.getSessionList();
        }
      }, {
        rootMargin: "0px 0px 500px 0px"
      })
      // 开始监听
      sessionObserver.observe(this.$refs.contactLoading)

      // 历史记录的监听
      const messageObserver = new IntersectionObserver(entries => {
        if (entries[0].intersectionRatio <= 0) return
        if (this.pullDownMessage == "more" && !this.recordLoading) {
          // 加载首先需要获取scrollTop
          this.$nextTick(() => {
            this.pullDownTopHeight = this.$refs.messageList.scrollHeight
          })
          this.pullDownMessage = "loading";
          this.queryChatRecordParams.pageNum++;
          this.getChatRecordList();
        }
      })
      // 开始监听
      messageObserver.observe(this.$refs.messageLoad)
    })

  },
  beforeDestroy() {
    // 组件销毁前关闭WebSocket连接
    this.$websocket.close()
    this.$websocket.connect(this.userId)
  },
  watch: {
    messageContactData: {
      handler(res) {
        // 处理接收到的消息
        const messageData = res.data; //消息的数据 
        // 分析消息的类型
        if (res.type === 'receive') {
          this.getCountSession()
          // 判断消息的类型
          // 如果是个人并且这条消息是收藏的，那么给收藏消息添加未读红点的显示
          // 消息红点的显示，首先如果在当前的对话类型，那么当前的类型不需要显示红点，如果不是当前的类型，那么消息过来的那个会话判断类型后需要显示红点
          if (this.activeTab === 'personal' && messageData.isFavorite === 1) {
            this.unreadStar = true
          } else if (this.activeTab === 'group') {
            if (messageData.isFavorite === 1) {
              this.unreadStar = true
            } else {
              this.unreadPersonal = true
            }
          } else if (this.activeTab === 'star') {
            if (messageData.isFavorite !== 1) {
              this.unreadPersonal = true
            }
          }
          // 接收到的消息
          // 首先判断是不是当前的会话
          if (this.currentContact && this.currentContact.sessionId == messageData.sessionId) {
            // 是当前的会话
            // 直接添加到消息列表中
            this.currentContact.endText = messageData.text
            this.currentContact.latestChatTime = messageData.time
            this.messages.push({
              messageContent: messageData.text,
              sendTime: messageData.time,
              chatInout: 1,
              chatId: messageData.realId,
              success: 1
            })
            // 需要直接已读当前的会话
            readSession(messageData.sessionId).then(res => {
            })

            this.$nextTick(() => {
              this.scrollToBottom();
            });
          } else {
            if (this.activeTab !== 'group') {
              // 
              this.sessionContacts.forEach(item => {
                if (item.sessionId === messageData.sessionId) {
                  item.endText = messageData.text
                  item.messageCount++
                  item.latestChatTime = messageData.time
                  item.messageType = 1
                }
              })
            } else {
              // 如果是群发，那么需要删除当前的会话，搬到收藏或者是会话里面去
              for (let i in this.sessionContacts) {
                if (this.sessionContacts[i].sessionId === messageData.sessionId) {
                  this.sessionContacts.splice(i, 1)
                }
              }
            }
          }
        } else if (res.type == 'result') {
          // 代表发送的消息，返回的消息的状态 
          if (this.currentContact.sessionId == messageData.sessionId) {
            // 是当前的会话，并且还是发送的消息，直接更新这条消息的状态还有消息的chatId
            // 直接添加到消息列表中
            if (messageData.success == 1) {
              this.currentContact.endText = messageData.text
              this.currentContact.latestChatTime = messageData.time
            }
            this.messages.forEach(item => {
              // 首先第一次返回,需要判断假id,如果都有那么就设置真id
              if (item.flagId && messageData.flagId) {
                item.chatId = messageData.realId
                item.sendTime = messageData.time
                item.sessionId = messageData.sessionId
                if (messageData.success === 0) {
                  item.success = messageData.success
                } else if (messageData.success && item.chatId == messageData.realId) {
                  item.success = messageData.success
                }
              } else if (messageData.success && item.chatId == messageData.realId) {
                item.success = messageData.success
              }
            })
          }
        }
      },
      deep: true
    },
    userId: {
      handler(v) {
        if (!this.userSessionId) {
          this.userSessionId = v;
        }
        // this.initWebsocket();
        this.getCountSession()
      }
    },
    projectId: {
      handler(v) {
        this.querySessionParams.projectId = v;
        this.querySessionParams.pageNum = 1
        this.getSessionList();
      }
    },
    activeTab: {
      handler(v) {
        // 监听当前点击的哪个会话
        switch (v) {
          case 'personal':
            this.unreadPersonal = false
            break;
          case 'star':
            this.unreadStar = false
            break;
        }
        this.querySessionParams.pageNum = 1
        this.getSessionList()
      }
    }
  },
  methods: {
    // 转换消息，
    getMessageType(item) {
      if (item.messageContent.indexOf('av-media') != -1) {
        return "img"
      } else {
        return "text"
      }
    },
    // 刷新数据
    handleReferData() {
      this.querySessionParams.pageNum = 1
      this.getSessionList()
      this.getChatRecordList()
    },
    // 生成手机界面
    handleGenerateUI() {
      this.visibledPhone = true
    },
    // 获取三个会话类型的全部数量统计
    getCountSession() {
      sessionUnread(this.userSessionId).then(res => {
        this.sessionCounts = res.data
      })
    },
    toggleEmojiPicker() {
      this.showEmojiPicker = !this.showEmojiPicker
    },
    addEmoji(emoji) {
      this.messageInput += emoji.native
    },
    templateTime(strtime) {
      // 时间格式化
      const time = new Date(strtime)
      const today = new Date()
      // 判断是不是今天
      // 判断是否是今天
      if (time.toDateString() === today.toDateString()) {
        // 如果是今天,返回时分秒
        return time.toLocaleString([], {
          hour: '2-digit',
          minute: '2-digit',
          hour12: false
        });
      } else {
        // 如果不是今天,返回年月日时分
        return time.toLocaleString([], {
          // year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit',
          hour12: false
        });
      }
    },
    handleSearchInput() {
      // 清除之前的定时器
      if (this.searchTimer) {
        clearTimeout(this.searchTimer)
      }
      // 设置新的定时器，300ms后执行搜索
      this.searchTimer = setTimeout(() => {
        this.querySessionParams.pageNum = 1
        this.getSessionList()
      }, 300)
    },
    genMessageBox(text) {
      // 生成传输的消息盒子
      // 生成UUID作为消息标识
      const uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        const r = Math.random() * 16 | 0;
        const v = c === 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
      });
      return {
        flag: 1,
        type: 'chat',
        data: {
          sessionId: this.currentContact.sessionId,
          text,
          time: new Date(),
          flagId: uuid
        }
      }
    },
    // 获取聊天记录
    getChatRecordList() {
      if (this.queryChatRecordParams.sessionId) {
        listChatRecord(this.queryChatRecordParams).then(res => {
          this.recordLoading = false
          if (res.code === 200) {
            if (res.rows.length === this.queryChatRecordParams.pageSize) {
              this.pullDownMessage = "more"
            } else {
              this.pullDownMessage = "none"
            }
            if (this.queryChatRecordParams.pageNum === 1) {

              this.messages = res.rows // 重新赋值
              this.$nextTick(() => {
                this.scrollToBottom();
              });
            } else {
              // 分页加载，需要滚动到之前固定的位置去
              // 插入头部的数据
              this.messages.unshift(...res.rows)
              // this.messages = this.messages.concat(res.rows.sort((a, b) => {
              //   return new Date(a.sendTime) - new Date(b.sendTime)
              // }))
              this.$nextTick(() => {
                const height = this.$refs.messageList.scrollHeight
                this.$refs.messageList.scrollTop = height - this.pullDownTopHeight
              });

            }

          }
        })
      }
    },
    // 链接websocket
    initWebsocket() {
      console.log("初始化websocket")
      // 初始化websocket的数据
      this.wsClient = new WebSocketClient(this.userSessionId);
      // 连接WebSocket
      this.wsClient.connect();
      // 注册消息处理回调
      this.wsClient.onMessage(res => {
        // 处理接收到的消息
        const messageData = res.data; //消息的数据 
        // 分析消息的类型
        if (res.type === 'receive') {
          this.getCountSession()
          // 判断消息的类型
          // 如果是个人并且这条消息是收藏的，那么给收藏消息添加未读红点的显示
          // 消息红点的显示，首先如果在当前的对话类型，那么当前的类型不需要显示红点，如果不是当前的类型，那么消息过来的那个会话判断类型后需要显示红点
          if (this.activeTab === 'personal' && messageData.isFavorite === 1) {
            this.unreadStar = true
          } else if (this.activeTab === 'group') {
            if (messageData.isFavorite === 1) {
              this.unreadStar = true
            } else {
              this.unreadPersonal = true
            }
          } else if (this.activeTab === 'star') {
            if (messageData.isFavorite !== 1) {
              this.unreadPersonal = true
            }
          }
          // 接收到的消息
          // 首先判断是不是当前的会话
          if (this.currentContact && this.currentContact.sessionId == messageData.sessionId) {
            // 是当前的会话
            // 直接添加到消息列表中
            this.currentContact.endText = messageData.text
            this.currentContact.latestChatTime = messageData.time
            this.messages.push({
              messageContent: messageData.text,
              sendTime: messageData.time,
              chatInout: 1,
              chatId: messageData.realId,
              success: 1
            })
            // 需要直接已读当前的会话
            readSession(messageData.sessionId).then(res => {
            })

            this.$nextTick(() => {
              this.scrollToBottom();
            });
          } else {
            if (this.activeTab !== 'group') {
              // 
              this.sessionContacts.forEach(item => {
                if (item.sessionId === messageData.sessionId) {
                  item.endText = messageData.text
                  item.messageCount++
                  item.latestChatTime = messageData.time
                  item.messageType = 1
                }
              })
            } else {
              // 如果是群发，那么需要删除当前的会话，搬到收藏或者是会话里面去
              for (let i in this.sessionContacts) {
                if (this.sessionContacts[i].sessionId === messageData.sessionId) {
                  this.sessionContacts.splice(i, 1)
                }
              }
            }
          }
        } else if (res.type == 'result') {
          // 代表发送的消息，返回的消息的状态 
          if (this.currentContact.sessionId == messageData.sessionId) {
            // 是当前的会话，并且还是发送的消息，直接更新这条消息的状态还有消息的chatId
            // 直接添加到消息列表中
            if (messageData.success == 1) {
              this.currentContact.endText = messageData.text
              this.currentContact.latestChatTime = messageData.time
            }
            this.messages.forEach(item => {
              // 首先第一次返回,需要判断假id,如果都有那么就设置真id
              if (item.flagId && messageData.flagId) {
                item.chatId = messageData.realId
                item.sendTime = messageData.time
                item.sessionId = messageData.sessionId
                if (messageData.success === 0) {
                  item.success = messageData.success
                } else if (messageData.success && item.chatId == messageData.realId) {
                  item.success = messageData.success
                }
              } else if (messageData.success && item.chatId == messageData.realId) {
                item.success = messageData.success
              }
            })
          }
        }

      });
    },
    // 随机生成回复信息
    handleQuickReply(replyWeight) {
      // 加载等待界面
      this.chatLoading = true
      randomText({ replyWeight, projectId: this.projectId }).then(response => {
        if (response.code === 200) {
          // 随机从记录挑一条出来
          this.messageInput = response.msg
        } else {
          // this.$message.error(this.$t('message.presetEmpty'));
          // this.$message.error(response.msg);
        }
        this.chatLoading = false
      })
    },
    // 全屏当天聊天的界面
    handleControllScreen() {
      // 是否全屏
      if (this.isFullScreen) {
        document.exitFullscreen();
        this.isFullScreen = false;
      } else {
        document.body.requestFullscreen();
        this.isFullScreen = true;
      }
    },
    // 获取会话的列表
    getSessionList() {
      if (this.querySessionParams.pageNum === 1) {
        this.loadingSession = true
      }
      this.loadingSession = true;
      if (this.activeTab === 'personal') {
        mySession(this.querySessionParams).then(response => {
          this.loadingSession = false;
          if (response.code === 200) {
            // 更新统计数量
            this.getCountSession();
            if (this.querySessionParams.pageNum === 1)
              this.sessionContacts = response.rows || []
            else
              this.sessionContacts = [...this.sessionContacts, ...response.rows];

            if (response.rows.length === this.querySessionParams.pageSize)
              this.reachBootom = "more";
            else
              this.reachBootom = "none";
          } else {
            this.$message.error(response.msg);
          }
        })
      } else if (this.activeTab === 'group') {
        myMassMessage(this.querySessionParams).then(response => {
          this.loadingSession = false;
          if (response.code === 200) {
            if (this.querySessionParams.pageNum === 1)
              this.sessionContacts = response.rows || [];
            else
              this.sessionContacts = [...this.sessionContacts, ...response.rows];

            if (response.rows.length === this.querySessionParams.pageSize)
              this.reachBootom = "more";
            else
              this.reachBootom = "none";
          } else {
            this.$message.error(response.msg);
          }
        })
      } else {
        // 收藏会话
        myFavoriteSession(this.querySessionParams).then(response => {
          this.loadingSession = false;
          if (response.code === 200) {
            if (this.querySessionParams.pageNum === 1)
              this.sessionContacts = response.rows || [];
            else
              this.sessionContacts = [...this.sessionContacts, ...response.rows];

            if (response.rows.length === this.querySessionParams.pageSize)
              this.reachBootom = "more";
            else
              this.reachBootom = "none";
          } else {
            this.$message.error(response.msg);
          }
        })
      }
    },
    selectContact(contact) {
      // 选择当前的会话,顺便清除掉当前的未读数量
      contact.messageCount = 0
      this.currentContact = contact
      this.queryChatRecordParams.pageNum = 1
      this.queryChatRecordParams.sessionId = contact.sessionId
      this.recordLoading = true
      this.getChatRecordList()
    },
    handleKeyup(e) {
      e.preventDefault()
      if (!e.shiftKey) {
        this.messageInput = this.messageInput.endsWith("\n") ? this.messageInput.slice(0, -1) : this.messageInput
        this.sendMessage()
        return false;
      }
    },
    scrollToBottom() {
      const messageList = this.$refs.messageList
      messageList.scrollTop = messageList.scrollHeight
    },
    togglePin(contact) {
      topSession(contact.sessionId).then(response => {
        if (response.code === 200) {
          this.$message.success(response.msg);
          contact.isPinned = !contact.isPinned - 0

          // 重新排序  置顶和最后对话时间的排序
          this.sessionContacts.sort((a, b) => {
            if (a.isPinned && !b.isPinned) return -1;
            if (!a.isPinned && b.isPinned) return 1;
            return new Date(b.latestChatTime) - new Date(a.latestChatTime);
          });
          this.sessionContacts.sort((a, b) => {
            if (a.isPinned && !b.isPinned) return -1;
            if (!a.isPinned && b.isPinned) return 1;
            return new Date(b.latestChatTime) - new Date(a.latestChatTime);
          });
        } else {
          this.$message.error(response.msg);
        }
      })
    },
    toggleStar(contact) {
      favoriteSession(contact.sessionId).then(response => {
        if (response.code === 200) {
          contact.isFavorite = !contact.isFavorite - 0
          this.$message.success(response.msg);
        } else {
          this.$message.error(response.msg);
        }
      })
    },
    async translateMessage(message) {
      if (!message.translatedContent) {
        // 这里添加调用翻译API的逻辑
        var obj = {
          from: 'english',
          to: 'chinese_simplified',
          texts: [message.messageContent]
        }
        translate.request.translateText(obj, res => {
          //打印翻译结果
          this.$set(message, 'translatedContent', res.text.join(""))
          this.$set(message, 'translateLoading', false)
          // if (res.result == 1) {
          //   this.$set(message, 'translateLoading', false)
          //   this.$set(message, 'translatedContent', res.text.join(""))
          //   console.log(message)
          // }
          // console.log(res);
        });
        this.$set(message, 'translateLoading', true); // 触发Vue的响应式更新
      }
      this.$set(message, 'showTranslation', !message.showTranslation);
    },
    async sendMessage() {
      // 判断是否选择会话
      if (!this.currentContact) {
        this.$message.warning(this.$t('message.pleaseSelSession'));
        return;
      }
      if (!this.messageInput.trim()) return;


      const lastSendTime = localStorage.getItem('lastMessageSendTime');
      const currentTime = Date.now();
      const cooldownTime = (this.projectOptions.sendCd || 0) * 1000; // 转换为毫秒 
      const maxSend = this.projectOptions.maxSend || 1000; // 获取最大字数限制

      // 判断发送的冷却时间
      if (lastSendTime && currentTime - parseInt(lastSendTime) < cooldownTime) {
        const remainingTime = Math.ceil((cooldownTime - (currentTime - parseInt(lastSendTime))) / 1000);
        this.$message.warning(this.$t('message.sendTooFrequent', { remainingTime }));
        return;
      }

      // 判断字数是否超出限制
      if (this.messageInput.length > maxSend) {
        this.$message.warning(this.$t('message.maxSend', { maxSend }));
        return;
      }

      const sendMsg = this.genMessageBox(this.messageInput);
      const newMessage = {
        messageContent: this.messageInput,
        sendTime: new Date(),
        success: 0,
        chatInout: -1,
        flagId: sendMsg.data.flagId,
        sessionId: sendMsg.data.flagId + "99999",
        chatId: sendMsg.data.flagId
      };

      // this.wsClient.send(sendMsg);
      this.$websocket.send(sendMsg);
      this.messages.push(newMessage);
      this.messageInput = '';
      // 更新最后发送时间
      localStorage.setItem('lastMessageSendTime', Date.now().toString());

      // 发送消息

      this.$nextTick(() => {
        this.scrollToBottom();
      });

      // 如果开启了AI自动回复，则生成回复
      if (this.enableAutoReply) {
        await this.autoReply(newMessage);
      }
    },
    async autoReply(message) {
      // 这里添加调用AI自动回复API的逻辑
      setTimeout(() => {
        this.messages.push({
          content: '这是一条AI自动回复的消息',
          time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
          isSelf: false,
          avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
        });
        this.$nextTick(() => {
          this.scrollToBottom();
        });
      }, 1000);
    },
  }
}