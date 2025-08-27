package com.chat.ruoyichat.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 聊天记录，用于存储聊天记录相关信息对象 chat_record
 * 
 * @author ruoyi
 * @date 2025-03-05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 聊天记录唯一标识 */
    private Long chatId;

    /** 1表示接受，-1表示发出 */
    @Excel(name = "1表示接受，-1表示发出")
    private Long chatInout;

    /** 聊天内容 */
    @Excel(name = "聊天内容")
    private String messageContent;

    /** 发送时间 */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @Excel(name = "发送时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date sendTime;

    /** 会话记录表id */
    @Excel(name = "会话记录表id")
    private String sessionId;


    /**1成功 0等待 -1失败 */
    @Excel(name = "1成功 0等待 -1失败")
    private Long success;

    /**去重 */
    private String id;
}
