package com.chat.ruoyichat.service.impl;

import java.util.List;
import java.util.Objects;

import com.chat.ruoyichat.domain.SessionRecord;
import com.chat.ruoyichat.mapper.CustomerServiceDetailMapper;
import com.chat.ruoyichat.mapper.SessionRecordMapper;
import com.ruoyi.common.utils.uuid.IdUtils;
import io.netty.util.internal.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.chat.ruoyichat.mapper.ChatRecordMapper;
import com.chat.ruoyichat.domain.ChatRecord;
import com.chat.ruoyichat.service.IChatRecordService;
import org.springframework.util.ObjectUtils;

/**
 * 聊天记录，用于存储聊天记录相关信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-03-05
 */
@Service
public class ChatRecordServiceImpl implements IChatRecordService 
{
    @Autowired
    private ChatRecordMapper chatRecordMapper;
    @Autowired
    private SessionRecordMapper sessionRecordMapper;
    @Autowired
    private CustomerServiceDetailMapper customerServiceDetailMapper;
    /**
     * 查询聊天记录，用于存储聊天记录相关信息
     * 
     * @param chatId 聊天记录，用于存储聊天记录相关信息主键
     * @return 聊天记录，用于存储聊天记录相关信息
     */
    @Override
    public ChatRecord selectChatRecordByChatId(String chatId)
    {
        return chatRecordMapper.selectChatRecordByChatId(chatId);
    }

    /**
     * 查询聊天记录，用于存储聊天记录相关信息列表
     * 
     * @param chatRecord 聊天记录，用于存储聊天记录相关信息
     * @return 聊天记录，用于存储聊天记录相关信息
     */
    @Override
    public List<ChatRecord> selectChatRecordList(ChatRecord chatRecord)
    {
//        如果session不为空,已读当前会话
        if(!ObjectUtils.isEmpty(chatRecord.getSessionId())){
            SessionRecord session =new SessionRecord();
            session.setSessionId(chatRecord.getSessionId());
            session.setMessageCount(0L);
            sessionRecordMapper.updateSessionRecord(session);
        }
        List<ChatRecord> chatRecords = chatRecordMapper.selectChatRecordList(chatRecord);
        return chatRecords;
    }

    /**
     * 新增聊天记录，用于存储聊天记录相关信息
     * 
     * @param chatRecord 聊天记录，用于存储聊天记录相关信息
     * @return 结果
     */
    @Override
    public int insertChatRecord(ChatRecord chatRecord)
    {
        return chatRecordMapper.insertChatRecord(chatRecord);
    }

    /**
     * 修改聊天记录，用于存储聊天记录相关信息
     * 
     * @param chatRecord 聊天记录，用于存储聊天记录相关信息
     * @return 结果
     */
    @Override
    public int updateChatRecord(ChatRecord chatRecord)
    {
        return chatRecordMapper.updateChatRecord(chatRecord);
    }

    /**
     * 批量删除聊天记录，用于存储聊天记录相关信息
     * 
     * @param chatIds 需要删除的聊天记录，用于存储聊天记录相关信息主键
     * @return 结果
     */
    @Override
    public int deleteChatRecordByChatIds(String[] chatIds)
    {
        return chatRecordMapper.deleteChatRecordByChatIds(chatIds);
    }

    /**
     * 删除聊天记录，用于存储聊天记录相关信息信息
     * 
     * @param chatId 聊天记录，用于存储聊天记录相关信息主键
     * @return 结果
     */
    @Override
    public int deleteChatRecordByChatId(String chatId)
    {
        return chatRecordMapper.deleteChatRecordByChatId(chatId);
    }

    /**
     * 对ai单句回复的打分的入库
     * <p>
     * 1. 检查组长是否开启ai回复功能<br>
     * 2. 检查该字段是否是ai回复的<br>
     * 3. 检查是否已经打过分<br>
     * 4. 然后分数入库
     *
     * @param chatRecord 包含chat_id（聊天记录唯一标识）与score（评分）
     */
    @Override
    public void aiscore(ChatRecord chatRecord) {
        Long chatId = chatRecord.getChatId();
        Integer score = chatRecord.getAiScore();
        // todo 具体业务逻辑
    }
}
