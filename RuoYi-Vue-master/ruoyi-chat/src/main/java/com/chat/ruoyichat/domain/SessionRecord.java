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
 * 会话记录，用于存储会话记录相关信息对象 session_record
 * 
 * @author ruoyi
 * @date 2025-03-04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SessionRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 会话记录唯一标识 */
    private String sessionId;

    /** 客服id */
    @Excel(name = "客服id")
    private Long custId;

    /** 客户账号 */
    @Excel(name = "客户账号")
    private String custContact;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date beginTime;

    /** 最近会话时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "最近会话时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date latestChatTime;

    @Excel(name = "未读消息数量")
    private Long messageCount;

    /** 是否收藏 */
    @Excel(name = "是否收藏")
    private Long isFavorite;

    /** 是否置顶 */
    @Excel(name = "是否置顶")
    private Long isPinned;

    /** 会话类型  0群发  1我的会话 */
    @Excel(name = "会话类型")
    private Integer messageType;

    /** 关联项目 */
    @Excel(name = "关联项目")
    private String projectId;

    /** 最后一句话 */
    @Excel(name = "最后一句话")
    private String endText;

    /** 哪个账号开始的聊天 */
    @Excel(name = "账号")
    private String account;
}
