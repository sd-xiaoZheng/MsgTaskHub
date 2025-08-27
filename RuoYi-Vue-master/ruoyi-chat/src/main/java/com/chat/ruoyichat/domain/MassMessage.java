package com.chat.ruoyichat.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 群发消息，用于存储群发消息相关信息对象 mass_message
 * 
 * @author ruoyi
 * @date 2025-03-05
 */
@ToString
public class MassMessage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 群发消息唯一标识，采用VARCHAR类型存储 */
    private String messageId;

    /** 关联项目 */
    @Excel(name = "关联项目")
    private String projectId;

    /** 群发消息内容 */
    @Excel(name = "群发消息内容")
    private String sendContent;

    /** 发送时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发送时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date sendTime;

    /** 使用次数 */
    @Excel(name = "使用次数")
    private Long sendNum;

    /** 最近更新人 */
    @Excel(name = "最近更新人")
    private String updateUser;

    /** 创建人 */
    @Excel(name = "创建人")
    private String createUser;

    public void setMessageId(String messageId) 
    {
        this.messageId = messageId;
    }

    public String getMessageId() 
    {
        return messageId;
    }
    public void setProjectId(String projectId) 
    {
        this.projectId = projectId;
    }

    public String getProjectId() 
    {
        return projectId;
    }
    public void setSendContent(String sendContent) 
    {
        this.sendContent = sendContent;
    }

    public String getSendContent() 
    {
        return sendContent;
    }
    public void setSendTime(Date sendTime) 
    {
        this.sendTime = sendTime;
    }

    public Date getSendTime() 
    {
        return sendTime;
    }
    public void setSendNum(Long sendNum) 
    {
        this.sendNum = sendNum;
    }

    public Long getSendNum() 
    {
        return sendNum;
    }
    public void setUpdateUser(String updateUser) 
    {
        this.updateUser = updateUser;
    }

    public String getUpdateUser() 
    {
        return updateUser;
    }
    public void setCreateUser(String createUser) 
    {
        this.createUser = createUser;
    }

    public String getCreateUser() 
    {
        return createUser;
    }

}
