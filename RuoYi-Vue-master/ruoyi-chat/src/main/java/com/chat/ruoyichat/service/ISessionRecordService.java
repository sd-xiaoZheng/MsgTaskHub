package com.chat.ruoyichat.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.chat.ruoyichat.domain.SessionRecord;

/**
 * 会话记录，用于存储会话记录相关信息Service接口
 * 
 * @author ruoyi
 * @date 2025-03-04
 */
public interface ISessionRecordService 
{
    /**
     * 查询会话记录，用于存储会话记录相关信息
     * 
     * @param sessionId 会话记录，用于存储会话记录相关信息主键
     * @return 会话记录，用于存储会话记录相关信息
     */
    public SessionRecord selectSessionRecordBySessionId(String sessionId);

    //   查询群发的消息
    List<SessionRecord>  selectSessionRecordByMassMessage(SessionRecord sessionRecord);

    //   查询我的会话
    List<SessionRecord>  selectMySessionRecord(SessionRecord sessionRecord);

    /**
     * 查询会话记录，用于存储会话记录相关信息列表
     * 
     * @param sessionRecord 会话记录，用于存储会话记录相关信息
     * @return 会话记录，用于存储会话记录相关信息集合
     */
    public List<SessionRecord> selectSessionRecordList(SessionRecord sessionRecord);

    /**
     * 新增会话记录，用于存储会话记录相关信息
     * 
     * @param sessionRecord 会话记录，用于存储会话记录相关信息
     * @return 结果
     */
    public int insertSessionRecord(SessionRecord sessionRecord);

    /**
     * 修改会话记录，用于存储会话记录相关信息
     * 
     * @param sessionRecord 会话记录，用于存储会话记录相关信息
     * @return 结果
     */
    public int updateSessionRecord(SessionRecord sessionRecord);

    /**
     * 批量删除会话记录，用于存储会话记录相关信息
     * 
     * @param sessionIds 需要删除的会话记录，用于存储会话记录相关信息主键集合
     * @return 结果
     */
    public int deleteSessionRecordBySessionIds(String[] sessionIds);

    /**
     * 删除会话记录，用于存储会话记录相关信息信息
     * 
     * @param sessionId 会话记录，用于存储会话记录相关信息主键
     * @return 结果
     */
    public int deleteSessionRecordBySessionId(String sessionId);

    JSONObject sessionUnread(Long userId);

}
