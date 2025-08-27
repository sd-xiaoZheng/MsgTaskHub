package com.chat.ruoyichat.service.impl;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.utils.uuid.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.chat.ruoyichat.mapper.SessionRecordMapper;
import com.chat.ruoyichat.domain.SessionRecord;
import com.chat.ruoyichat.service.ISessionRecordService;

/**
 * 会话记录，用于存储会话记录相关信息Service业务层处理
 *
 * @author ruoyi
 * @date 2025-03-04
 */
@Service
public class SessionRecordServiceImpl implements ISessionRecordService {
    @Autowired
    private SessionRecordMapper sessionRecordMapper;

    /**
     * 查询会话记录，用于存储会话记录相关信息
     *
     * @param sessionId 会话记录，用于存储会话记录相关信息主键
     * @return 会话记录，用于存储会话记录相关信息
     */
    @Override
    public SessionRecord selectSessionRecordBySessionId(String sessionId) {
        return sessionRecordMapper.selectSessionRecordBySessionId(sessionId);
    }

    //   查询群发的消息
    @Override
    public List<SessionRecord> selectSessionRecordByMassMessage(SessionRecord sessionRecord) {
//        设置类型为群发
        return sessionRecordMapper.selectSessionRecordByMassMessage(sessionRecord);
    }


    //   查询我的会话
    @Override
    public List<SessionRecord> selectMySessionRecord(SessionRecord sessionRecord) {
//        设置类型为群发
        sessionRecord.setMessageType(1);
        return sessionRecordMapper.selectSessionRecordList(sessionRecord);
    }

    /**
     * 查询会话记录，用于存储会话记录相关信息列表
     *
     * @param sessionRecord 会话记录，用于存储会话记录相关信息
     * @return 会话记录，用于存储会话记录相关信息
     */
    @Override
    public List<SessionRecord> selectSessionRecordList(SessionRecord sessionRecord) {
        return sessionRecordMapper.selectSessionRecordList(sessionRecord);
    }

    /**
     * 新增会话记录，用于存储会话记录相关信息
     *
     * @param sessionRecord 会话记录，用于存储会话记录相关信息
     * @return 结果
     */
    @Override
    public int insertSessionRecord(SessionRecord sessionRecord) {
        sessionRecord.setSessionId(IdUtils.fastSimpleUUID());
        return sessionRecordMapper.insertSessionRecord(sessionRecord);
    }

    /**
     * 修改会话记录，用于存储会话记录相关信息
     *
     * @param sessionRecord 会话记录，用于存储会话记录相关信息
     * @return 结果
     */
    @Override
    public int updateSessionRecord(SessionRecord sessionRecord) {
        return sessionRecordMapper.updateSessionRecord(sessionRecord);
    }

    /**
     * 批量删除会话记录，用于存储会话记录相关信息
     *
     * @param sessionIds 需要删除的会话记录，用于存储会话记录相关信息主键
     * @return 结果
     */
    @Override
    public int deleteSessionRecordBySessionIds(String[] sessionIds) {
        return sessionRecordMapper.deleteSessionRecordBySessionIds(sessionIds);
    }

    /**
     * 删除会话记录，用于存储会话记录相关信息信息
     *
     * @param sessionId 会话记录，用于存储会话记录相关信息主键
     * @return 结果
     */
    @Override
    public int deleteSessionRecordBySessionId(String sessionId) {
        return sessionRecordMapper.deleteSessionRecordBySessionId(sessionId);
    }

    @Override
    public JSONObject sessionUnread(Long userId) {
        SessionRecord sessionRecord = new SessionRecord();
        sessionRecord.setCustId(userId);
        List<SessionRecord> sessionRecords = sessionRecordMapper.selectSessionRecordList(sessionRecord);
        int sessionUnreadNum = 0;
        int sessionNum = 0;
        int groupNum = 0;
        int collectNum = 0;
        int collectUnreadNum = 0;
        for (SessionRecord record : sessionRecords) {
            Integer messageType = record.getMessageType();
            Long messageCount = record.getMessageCount();
            if (messageType>-1) {
                groupNum++;
            }
            if (messageType.equals(1)){
                sessionNum++;
                if (messageCount>0){
                    sessionUnreadNum+=messageCount;
                }
            }

            
            Long isFavorite = record.getIsFavorite();
            if (isFavorite.equals(1L)) {
                collectUnreadNum += (int) (long) record.getMessageCount();
                collectNum++;
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sessionUnreadNum", sessionUnreadNum);
        jsonObject.put("sessionNum", sessionNum);
        jsonObject.put("groupNum", groupNum);
        jsonObject.put("collectNum", collectNum);
        jsonObject.put("collectUnreadNum", collectUnreadNum);
        return jsonObject;
    }
}
