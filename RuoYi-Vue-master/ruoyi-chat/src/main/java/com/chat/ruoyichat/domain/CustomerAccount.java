package com.chat.ruoyichat.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.utils.uuid.IdUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 客户账号，用于存储客户账号相关信息对象 customer_account
 * 
 * @author ruoyi
 * @date 2025-03-04
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CustomerAccount extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 账号的唯一标识符 */
    private String accountId;

    /** 账号类型 */
    @Excel(name = "账号类型")
    private Long accountType;

    /** 账号 */
    @Excel(name = "账号")
    private String accountNumber;

    /** 关联的项目ID  */
    @Excel(name = "关联的项目ID ")
    private String projectId;

    /** 状态 */
    @Excel(name = "状态")
    private Long accountStatus;

    /** 账号分配给的客服号ID */
    @Excel(name = "账号分配给的客服号ID")
    private String assignedId;

    /** 客服号名称 */
    @Excel(name = "客服号名称")
    private String assignedName;

    /** 最近发送时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "最近发送时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date lastSendTime;

    /** 总发送数量 */
    @Excel(name = "总发送数量")
    private Long totalSendCount;

    /** 总回复数量 */
    @Excel(name = "总回复数量")
    private Long totalReplyCount;

    /** 上传者 */
    @Excel(name = "上传者")
    private String uploader;

    public CustomerAccount(Long accountType, String accountNumber, String projectId, String uploader) {
        this.accountId = IdUtils.fastSimpleUUID();
        this.accountType = accountType;
        this.accountNumber = accountNumber;
        this.projectId = projectId;
        this.accountStatus = 2L;
        this.assignedId = "";
        this.assignedName = "";
        this.lastSendTime = null;
        this.totalSendCount = 0L;
        this.totalReplyCount = 0L;
        this.uploader = uploader;
    }

    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }

    public String getAccountId() 
    {
        return accountId;
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
    public void setAccountStatus(Long accountStatus) 
    {
        this.accountStatus = accountStatus;
    }

    public Long getAccountStatus() 
    {
        return accountStatus;
    }
    public void setAssignedId(String assignedId) 
    {
        this.assignedId = assignedId;
    }

    public String getAssignedId() 
    {
        return assignedId;
    }
    public void setAssignedName(String assignedName) 
    {
        this.assignedName = assignedName;
    }

    public String getAssignedName() 
    {
        return assignedName;
    }
    public void setLastSendTime(Date lastSendTime) 
    {
        this.lastSendTime = lastSendTime;
    }

    public Date getLastSendTime() 
    {
        return lastSendTime;
    }
    public void setTotalSendCount(Long totalSendCount) 
    {
        this.totalSendCount = totalSendCount;
    }

    public Long getTotalSendCount() 
    {
        return totalSendCount;
    }
    public void setTotalReplyCount(Long totalReplyCount) 
    {
        this.totalReplyCount = totalReplyCount;
    }

    public Long getTotalReplyCount() 
    {
        return totalReplyCount;
    }
    public void setUploader(String uploader) 
    {
        this.uploader = uploader;
    }

    public String getUploader() 
    {
        return uploader;
    }

}
