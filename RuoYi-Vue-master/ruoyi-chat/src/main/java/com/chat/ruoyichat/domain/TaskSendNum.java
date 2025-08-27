package com.chat.ruoyichat.domain;

import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 任务发送次数对象 task_send_num
 * 
 * @author ruoyi
 * @date 2025-03-16
 */
@ToString
public class TaskSendNum extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 任务内容的唯一标识符 */
    private String taskContentId;

    /** 单个任务的发送次数 */
    @Excel(name = "单个任务的发送次数")
    private Long sendNum;

    public void setTaskContentId(String taskContentId) 
    {
        this.taskContentId = taskContentId;
    }

    public String getTaskContentId() 
    {
        return taskContentId;
    }
    public void setSendNum(Long sendNum) 
    {
        this.sendNum = sendNum;
    }

    public Long getSendNum() 
    {
        return sendNum;
    }

}
