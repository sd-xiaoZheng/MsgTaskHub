package com.chat.ruoyichat.websocket;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chat.ruoyichat.ApplicationHelper;
import com.chat.ruoyichat.domain.Accounts;
import com.chat.ruoyichat.domain.ChatRecord;
import com.chat.ruoyichat.domain.SessionRecord;
import com.chat.ruoyichat.domain.dto.Message;
import com.chat.ruoyichat.domain.sendDto.SendMsgObj;
import com.chat.ruoyichat.mapper.AccountsMapper;
import com.chat.ruoyichat.mapper.ChatRecordMapper;
import com.chat.ruoyichat.mapper.SessionRecordMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.system.mapper.SysUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Scope("prototype")
@ServerEndpoint(value = "/ruoyichat/chat/{id}")
public class WebSocketService {

    private static Logger log = LoggerFactory.getLogger(WebSocketService.class);
    private final RedisCache redisCache = (RedisCache)ApplicationHelper.getBean("redisCache");
    private final ChatRecordMapper chatRecordMapper = (ChatRecordMapper) ApplicationHelper.getBean("chatRecordMapper");
    private final SessionRecordMapper sessionRecordMapper =  (SessionRecordMapper) ApplicationHelper.getBean("sessionRecordMapper");
    private final AccountsMapper accountsMapper = (AccountsMapper) ApplicationHelper.getBean("accountsMapper");
    private final SysUserMapper sysUserMapper = (SysUserMapper) ApplicationHelper.getBean("sysUserMapper");


    private String key = CacheConstants.MESSAGE_KEY;

    private static final AtomicInteger OnlineCount = new AtomicInteger(0);
    // concurrent包的线程安全Set，用来存放每个客户端对应的Session对象。
    public static CopyOnWriteArraySet<Session> SessionSet = new CopyOnWriteArraySet<Session>();
    public static HashSet<Long> userIdList = new HashSet<>();
    public static HashMap<Long, Session> userSessionMap = new HashMap<>();

    /**
     * 建立连接
     *
     * @param session
     * @param user
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("id") String user) {
        SessionSet.add(session);
        userIdList.add(Long.parseLong(user));
        userSessionMap.put(Long.parseLong(user), session);

        int cnt = OnlineCount.incrementAndGet(); // 在线数加1
        log.info("有连接加入，当前连接数为：{}", cnt);
        System.out.println("session:" + session);
        System.out.println("user:" + user);
    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose(Session session,@PathParam("id") String user) {
        SessionSet.remove(session);
        userSessionMap.remove(Long.parseLong(user), session);
        String userId = getUserId(session);
        userIdList.remove(Long.parseLong(userId));

        int cnt = OnlineCount.decrementAndGet();
        log.info("有连接关闭，当前连接数为：{}", cnt);
        System.out.println("session:" + session);
        System.out.println("user:" + user);
    }

    /**
     * 接收到消息
     *
     * @param text 发送来的消息
     */
    @OnMessage
//    @Transactional(rollbackFor = Exception.class)
    public void onMessage(String text, Session session, @PathParam("id") String user) throws IOException {
        if (text.equals("ping")) {
            SendMessage(session, "pong");
            return;
        }
        JSONObject jsonObject = JSON.parseObject(text);
        Object o = jsonObject.get("flag");
        String flag = o.toString();

        switch (flag) {
            case "1":
                sendMessage(text, session);
                break;
            default:
                log.info("websocket没有该Flag");
                break;
        }
    }


    /**
     * 出现错误
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误：{}，Session ID： {}", error.getMessage(), session.getId());
        error.printStackTrace();
    }

    /**
     * 发送消息，实践表明，每次浏览器刷新，session会发生变化。
     *
     * @param session
     * @param message
     */
    public static void SendMessage(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (NullPointerException nullPointerException){
            log.error("员工不在线");
        } catch (IOException e) {
            log.error("发送消息出错：{}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 群发消息
     *
     * @param message
     * @throws IOException
     */
    public static void BroadCastInfo(String message) throws IOException {
        for (Session session : SessionSet) {
            if (session.isOpen()) {
                SendMessage(session, message);
            }
        }
    }

    /**
     * 指定Session发送消息
     *
     * @param sessionId
     * @param message
     * @throws IOException
     */
    public static void SendMessage(String message, String sessionId) throws IOException {
        Session session = null;
        for (Session s : SessionSet) {
            if (s.getId().equals(sessionId)) {
                session = s;
                break;
            }
        }
        if (session != null) {
            SendMessage(session, message);
        } else {
            log.warn("没有找到你指定ID的会话：{}", sessionId);
        }
    }

    /**
     * 客服发送消息
     * @param messageJson 消息json
     * @param session     会话
     * @throws IOException
     */
//    @Transactional(rollbackFor = Exception.class)
    public void sendMessage(String messageJson, Session session) {
        String userId = getUserId(session);
        SysUser sysUser = sysUserMapper.selectUserById(Long.parseLong(userId));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        //组装一条发送消息
        //获得会话id
        JSONObject jsonObject = JSON.parseObject(messageJson);
        Object o = jsonObject.get("data");
        Message megData = JSONObject.parseObject(o.toString(), Message.class);
//        Message megData = (Message) o;
        String sessionId = megData.getSessionId();
        String message = megData.getText();


//        LoginUser sysUser = SecurityUtils.getLoginUser();
        SessionRecord sessionRecord = sessionRecordMapper.selectSessionRecordBySessionId(sessionId);
        String phone = sessionRecord.getCustContact();
        String account = sessionRecord.getAccount();
        Accounts accounts = accountsMapper.selectAccountsByAccount(account);
        String email = accounts.getAccount();
        sessionRecord.setMessageType(1);
        sessionRecordMapper.updateSessionRecord(sessionRecord);
        //聊天记录入库
        Date date = new Date();
        ChatRecord chatRecord = new ChatRecord();
//        chatRecord.setChatId(IdUtils.fastSimpleUUID());
        chatRecord.setChatInout(-1L);
        chatRecord.setMessageContent(message);
        chatRecord.setSendTime(date);
        chatRecord.setSessionId(sessionId);
        chatRecord.setSuccess(1L);//TODO是否是不能成功？ 再议
        chatRecordMapper.insertChatRecord(chatRecord);
        megData.setRealId(chatRecord.getChatId());
        jsonObject.put("data", megData);
        jsonObject.put("type","result");
        SendMessage(session, jsonObject.toString());
        //组装发送消息
        SendMsgObj sendMsgObj = new SendMsgObj();
        sendMsgObj.setId(chatRecord.getChatId().toString());
        sendMsgObj.setMobileNum(email);
        sendMsgObj.setMyphonenumber(email);
        sendMsgObj.setType("chat");
        sendMsgObj.setText(message);
        sendMsgObj.setOther(phone);
        sendMsgObj.setUserId(sysUser.getUserId());
        sendMsgObj.setUserName(sysUser.getUserName());
        sendMsgObj.setCookie(accounts.getCookie());
        sendMsgObj.setDeviceInfo(accounts.getDeviceInfo());
        sendMsgObj.setIsChat("1");
        sendMsgObj.setPassword(accounts.getPassword());
        redisCache.leftPush(key+sysUser.getUserId(), sendMsgObj);
        //websocket 返回
    }

    public String getUserId(Session session) {
        URI requestURI = session.getRequestURI();
        String url = requestURI.toString();
        String[] split = url.split("/");
        return split[split.length - 1];
    }
}
