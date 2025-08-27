package com.chat.ruoyichat.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.chat.ruoyichat.mapper.MassMessageMapper;
import com.chat.ruoyichat.domain.MassMessage;
import com.chat.ruoyichat.service.IMassMessageService;

/**
 * 群发消息，用于存储群发消息相关信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-03-05
 */
@Service
public class MassMessageServiceImpl implements IMassMessageService 
{
    @Autowired
    private MassMessageMapper massMessageMapper;

    /**
     * 查询群发消息，用于存储群发消息相关信息
     * 
     * @param messageId 群发消息，用于存储群发消息相关信息主键
     * @return 群发消息，用于存储群发消息相关信息
     */
    @Override
    public MassMessage selectMassMessageByMessageId(String messageId)
    {
        return massMessageMapper.selectMassMessageByMessageId(messageId);
    }

    /**
     * 查询群发消息，用于存储群发消息相关信息列表
     * 
     * @param massMessage 群发消息，用于存储群发消息相关信息
     * @return 群发消息，用于存储群发消息相关信息
     */
    @Override
    public List<MassMessage> selectMassMessageList(MassMessage massMessage)
    {
        return massMessageMapper.selectMassMessageList(massMessage);
    }

    /**
     * 新增群发消息，用于存储群发消息相关信息
     * 
     * @param massMessage 群发消息，用于存储群发消息相关信息
     * @return 结果
     */
    @Override
    public int insertMassMessage(MassMessage massMessage)
    {
        massMessage.setMessageId(IdUtils.fastSimpleUUID());
        massMessage.setCreateTime(DateUtils.getNowDate());
        return massMessageMapper.insertMassMessage(massMessage);
    }

    /**
     * 修改群发消息，用于存储群发消息相关信息
     * 
     * @param massMessage 群发消息，用于存储群发消息相关信息
     * @return 结果
     */
    @Override
    public int updateMassMessage(MassMessage massMessage)
    {
        massMessage.setUpdateTime(DateUtils.getNowDate());
        return massMessageMapper.updateMassMessage(massMessage);
    }

    /**
     * 批量删除群发消息，用于存储群发消息相关信息
     * 
     * @param messageIds 需要删除的群发消息，用于存储群发消息相关信息主键
     * @return 结果
     */
    @Override
    public int deleteMassMessageByMessageIds(String[] messageIds)
    {
        return massMessageMapper.deleteMassMessageByMessageIds(messageIds);
    }

    /**
     * 删除群发消息，用于存储群发消息相关信息信息
     * 
     * @param messageId 群发消息，用于存储群发消息相关信息主键
     * @return 结果
     */
    @Override
    public int deleteMassMessageByMessageId(String messageId)
    {
        return massMessageMapper.deleteMassMessageByMessageId(messageId);
    }
}
