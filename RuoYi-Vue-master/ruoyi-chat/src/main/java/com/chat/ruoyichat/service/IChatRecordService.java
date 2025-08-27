package com.chat.ruoyichat.service;

import java.util.List;
import com.chat.ruoyichat.domain.ChatRecord;

/**
 * 聊天记录，用于存储聊天记录相关信息Service接口
 * 
 * @author ruoyi
 * @date 2025-03-05
 */
public interface IChatRecordService 
{
    /**
     * 查询聊天记录，用于存储聊天记录相关信息
     * 
     * @param chatId 聊天记录，用于存储聊天记录相关信息主键
     * @return 聊天记录，用于存储聊天记录相关信息
     */
    public ChatRecord selectChatRecordByChatId(String chatId);

    /**
     * 查询聊天记录，用于存储聊天记录相关信息列表
     * 
     * @param chatRecord 聊天记录，用于存储聊天记录相关信息
     * @return 聊天记录，用于存储聊天记录相关信息集合
     */
    public List<ChatRecord> selectChatRecordList(ChatRecord chatRecord);

    /**
     * 新增聊天记录，用于存储聊天记录相关信息
     * 
     * @param chatRecord 聊天记录，用于存储聊天记录相关信息
     * @return 结果
     */
    public int insertChatRecord(ChatRecord chatRecord);

    /**
     * 修改聊天记录，用于存储聊天记录相关信息
     * 
     * @param chatRecord 聊天记录，用于存储聊天记录相关信息
     * @return 结果
     */
    public int updateChatRecord(ChatRecord chatRecord);

    /**
     * 批量删除聊天记录，用于存储聊天记录相关信息
     * 
     * @param chatIds 需要删除的聊天记录，用于存储聊天记录相关信息主键集合
     * @return 结果
     */
    public int deleteChatRecordByChatIds(String[] chatIds);

    /**
     * 删除聊天记录，用于存储聊天记录相关信息信息
     * 
     * @param chatId 聊天记录，用于存储聊天记录相关信息主键
     * @return 结果
     */
    public int deleteChatRecordByChatId(String chatId);
}
