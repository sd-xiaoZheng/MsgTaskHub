package com.chat.ruoyichat.mapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.chat.ruoyichat.domain.SessionRecord;
import com.chat.ruoyichat.domain.vo.CountResult;
import org.apache.ibatis.annotations.Param;

/**
 * 会话记录，用于存储会话记录相关信息Mapper接口
 *
 * @author ruoyi
 * @date 2025-03-04
 */
public interface SessionRecordMapper {
    /**
     * 查询会话记录，用于存储会话记录相关信息
     *
     * @param sessionId 会话记录，用于存储会话记录相关信息主键
     * @return 会话记录，用于存储会话记录相关信息
     */
    public SessionRecord selectSessionRecordBySessionId(String sessionId);

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
     * 删除会话记录，用于存储会话记录相关信息
     *
     * @param sessionId 会话记录，用于存储会话记录相关信息主键
     * @return 结果
     */
    public int deleteSessionRecordBySessionId(String sessionId);

    /**
     * 批量删除会话记录，用于存储会话记录相关信息
     *
     * @param sessionIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSessionRecordBySessionIds(String[] sessionIds);

    SessionRecord selectSessionRecordByPhoneNum(String recipientList);

    List<SessionRecord> selectSessionRecordByMassMessage(SessionRecord sessionRecord);

    int selectSessionRecordCountByUserId(@Param("userId") Long userId);

    List<CountResult> selectCountByUserIdSet(@Param("userIdSet") HashSet<Long> userIdSet);

    SessionRecord selectSessionRecordAccount(String account);
}
