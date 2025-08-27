// import { Message } from 'element-ui'
var origin = location.origin.startsWith("https")?"wss://":"ws://"
const websocketURl=origin+location.host+process.env.VUE_APP_BASE_API+"/ruoyichat/chat/"
// const websocketURl="http://192.168.102.199:8080/ruoyichat/chat/"
class WebSocketClient {
  constructor(options = {}) {
    this.url = websocketURl
    this.options = {
      heartbeatInterval: 8000, // 心跳间隔，默认8秒
      reconnectInterval: 5000,  // 重连间隔，默认5秒
      messageTimeout: 1800,     // 消息响应超时时间，默认1.8秒
      ...options
    }
    this.ws = null
    this.isConnected = false 
    this.reconnectCount = 0
    this.heartbeatTimer = null
    this.reconnectTimer = null
    this.messageCallbacks = []
    this.sendMsgs = []
    this.pendingMessages = new Map() // 存储待确认的消息
  }

  // 连接WebSocket
  connect(userId = null) {
    try {
      if(userId){
        this.url = websocketURl+userId
      }
      console.log("websocketURl:"+this.url)
      console.log("websocketURl:"+websocketURl)
      this.ws = new WebSocket(this.url)
      console.log("绑定websocket")
      this.bindEvents()
    } catch (error) {
      console.error('WebSocket连接失败:', error)
      this.reconnect()
    }
  }

  // 绑定WebSocket事件
  bindEvents() {
    this.ws.onopen = () => {
      console.log('WebSocket连接成功')
      this.isConnected = true
      this.reconnectCount = 0
      this.startHeartbeat()

      // 处理消息的积压
      if (this.sendMsgs.length > 0) {
        this.sendMsgs.forEach(msg => this.send(msg))
        this.sendMsgs = []
      }
    }

    this.ws.onclose = (e) => {
      console.log('WebSocket连接关闭',e)
      this.isConnected = false
      this.stopHeartbeat()
      this.reconnect()
    }

    this.ws.onerror = (error) => {
      console.error('WebSocket错误:', error)
      this.isConnected = false
      this.stopHeartbeat()
      this.reconnect()
    }

    this.ws.onmessage = (event) => {
      const message = event.data
      if (message === 'pong') {
        // 心跳响应
        return
      }
      // 处理接收到的消息
      this.handleMessage(message)
    }
  }

  // 开始心跳检测
  startHeartbeat() {
    if (this.heartbeatTimer) {
      clearInterval(this.heartbeatTimer)
      this.heartbeatTimer = null
    }
    this.heartbeatTimer = setInterval(() => {
      if (this.isConnected) {
        this.send('ping')
      }
    }, this.options.heartbeatInterval)
  }

  // 停止心跳检测
  stopHeartbeat() {
    if (this.heartbeatTimer) {
      clearInterval(this.heartbeatTimer)
      this.heartbeatTimer = null
    }
  }

  // 重连机制
  reconnect() {
    if (this.reconnectTimer) {
      return
    } 

    this.reconnectTimer = setTimeout(() => {
      console.log(`WebSocket尝试第${this.reconnectCount + 1}次重连`)
      this.reconnectCount++
      this.connect()
      this.reconnectTimer = null
    }, this.options.reconnectInterval)
  }

  // 发送消息
  send(message) {
    if (!this.isConnected) {
      // WebSocket未连接，需要重新进行链接
      this.reconnectCount = 0
      localStorage.removeItem("lastMessageSendTime")
      this.reconnect()
      this.sendMsgs.push(message)
      return
    }

    try {
      const data = typeof message === 'string' ? message : JSON.stringify(message)
      
      // 如果不是心跳消息，则添加到待确认队列
      if (message !== 'ping' && typeof message === 'object') {
        const messageId = message.data.flagId
        if (messageId) {
          // 设置消息确认超时
          const timeoutId = setTimeout(() => {
            if (this.pendingMessages.has(messageId)) {
              console.log(`消息 ${messageId} 发送超时，准备重新发送`)
              this.pendingMessages.delete(messageId)
              this.isConnected = false // 标记连接断开
              this.reconnectCount = 0
              this.reconnect()
              this.sendMsgs.push(message) // 将消息加入重发队列
            }
          }, this.options.messageTimeout)

          this.pendingMessages.set(messageId, {
            message,
            timeoutId
          })
        }
      }

      this.ws.send(data)
    } catch (error) {
      console.error('发送消息失败:', error)
      // 发送失败时，将消息加入重发队列
      if (typeof message === 'object') {
        this.sendMsgs.push(message)
      }
    }
  }

  // 注册消息处理回调
  onMessage(callback) {
    if (typeof callback === 'function') {
      this.messageCallbacks.push(callback)
    }
  }

  // 处理接收到的消息
  handleMessage(message) {
    try {
      const data = typeof message === 'string' ? JSON.parse(message) : message
      
      // 如果是消息发送结果的响应，清除对应的超时检查
      if (data.type === 'result' && data.data.flagId) {
        const messageId = data.data.flagId
        const pendingMessage = this.pendingMessages.get(messageId)
        if (pendingMessage) {
          clearTimeout(pendingMessage.timeoutId)
          this.pendingMessages.delete(messageId)
        }
      }

      this.messageCallbacks.forEach(callback => callback(data))
    } catch (error) {
      console.error('消息处理失败:', error)
    }
  }

  // 关闭连接
  close() {
    this.stopHeartbeat()
    // 清除所有待确认消息的定时器
    this.pendingMessages.forEach(({ timeoutId }) => {
      clearTimeout(timeoutId)
    })
    this.pendingMessages.clear()
    
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
      this.reconnectTimer = null
    }
    if (this.ws) {
      this.ws.close()
    }
  }
}

export default WebSocketClient