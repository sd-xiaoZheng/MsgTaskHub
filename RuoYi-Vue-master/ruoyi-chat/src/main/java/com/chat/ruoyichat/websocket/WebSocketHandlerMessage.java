package com.chat.ruoyichat.websocket;

import com.chat.ruoyichat.mapper.AccountsMapper;
import com.chat.ruoyichat.mapper.ChatRecordMapper;
import com.chat.ruoyichat.mapper.SessionRecordMapper;
import com.chat.ruoyichat.utils.SpringContext;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.system.mapper.SysUserMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
public class WebSocketHandlerMessage implements WebSocketHandler {

    public RedisCache getRedisCache() {
        return SpringContext.getBean(RedisCache.class);
    }

    public ChatRecordMapper getChatRecordMapper() {
        return SpringContext.getBean(ChatRecordMapper.class);
    }

    public SessionRecordMapper getSessionRecordMapper() {
        return SpringContext.getBean(SessionRecordMapper.class);
    }

    public AccountsMapper getAccountsMapper() {
        return SpringContext.getBean(AccountsMapper.class);
    }

    public SysUserMapper getSysUserMapper() {
        return SpringContext.getBean(SysUserMapper.class);
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}