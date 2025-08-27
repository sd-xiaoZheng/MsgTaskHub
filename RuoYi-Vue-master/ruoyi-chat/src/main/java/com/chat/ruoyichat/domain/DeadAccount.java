package com.chat.ruoyichat.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 死号，用于存储死号相关信息对象 dead_account
 * 
 * @author ruoyi
 * @date 2025-03-05
 */
@ToString
public class DeadAccount extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 死号记录唯一标识 */
    private String deadId;

    /** 类型 */
    @Excel(name = "类型")
    private Long accountType;

    /** 死号的具体号码或地址 */
    @Excel(name = "死号的具体号码或地址")
    private String accountNumber;

    /** 关联项目 */
    @Excel(name = "关联项目")
    private String projectId;

    /** 死号时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "死号时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date deadTime;

    /** 死号原因 */
    @Excel(name = "死号原因")
    private String reason;

    public void setDeadId(String deadId) 
    {
        this.deadId = deadId;
    }

    public String getDeadId() 
    {
        return deadId;
    }
    public void setAccountType(Long accountType) 
    {
        this.accountType = accountType;
    }

    public Long getAccountType() 
    {
        return accountType;
    }
    public void setAccountNumber(String accountNumber) 
    {
        this.accountNumber = accountNumber;
    }

    public String getAccountNumber() 
    {
        return accountNumber;
    }
    public void setProjectId(String projectId) 
    {
        this.projectId = projectId;
    }

    public String getProjectId() 
    {
        return projectId;
    }
    public void setDeadTime(Date deadTime) 
    {
        this.deadTime = deadTime;
    }

    public Date getDeadTime() 
    {
        return deadTime;
    }
    public void setReason(String reason) 
    {
        this.reason = reason;
    }

    public String getReason() 
    {
        return reason;
    }

}
