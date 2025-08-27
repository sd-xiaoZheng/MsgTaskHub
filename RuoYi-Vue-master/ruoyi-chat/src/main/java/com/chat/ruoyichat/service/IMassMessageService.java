package com.chat.ruoyichat.service;

import java.util.List;
import com.chat.ruoyichat.domain.MassMessage;

/**
 * 群发消息，用于存储群发消息相关信息Service接口
 * 
 * @author ruoyi
 * @date 2025-03-05
 */
public interface IMassMessageService 
{
    /**
     * 查询群发消息，用于存储群发消息相关信息
     * 
     * @param messageId 群发消息，用于存储群发消息相关信息主键
     * @return 群发消息，用于存储群发消息相关信息
     */
    public MassMessage selectMassMessageByMessageId(String messageId);

    /**
     * 查询群发消息，用于存储群发消息相关信息列表
     * 
     * @param massMessage 群发消息，用于存储群发消息相关信息
     * @return 群发消息，用于存储群发消息相关信息集合
     */
    public List<MassMessage> selectMassMessageList(MassMessage massMessage);

    /**
     * 新增群发消息，用于存储群发消息相关信息
     * 
     * @param massMessage 群发消息，用于存储群发消息相关信息
     * @return 结果
     */
    public int insertMassMessage(MassMessage massMessage);

    /**
     * 修改群发消息，用于存储群发消息相关信息
     * 
     * @param massMessage 群发消息，用于存储群发消息相关信息
     * @return 结果
     */
    public int updateMassMessage(MassMessage massMessage);

    /**
     * 批量删除群发消息，用于存储群发消息相关信息
     * 
     * @param messageIds 需要删除的群发消息，用于存储群发消息相关信息主键集合
     * @return 结果
     */
    public int deleteMassMessageByMessageIds(String[] messageIds);

    /**
     * 删除群发消息，用于存储群发消息相关信息信息
     * 
     * @param messageId 群发消息，用于存储群发消息相关信息主键
     * @return 结果
     */
    public int deleteMassMessageByMessageId(String messageId);
}
