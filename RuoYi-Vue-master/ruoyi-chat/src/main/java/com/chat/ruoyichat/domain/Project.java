package com.chat.ruoyichat.domain;

import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 项目管理对象 project
 * 
 * @author ruoyi
 * @date 2025-03-04
 */
@ToString
public class Project extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 项目id */
    private String projectId;

    /** 项目名称 */
    @Excel(name = "项目名称")
    private String projectName;

    /** 项目描述 */
    @Excel(name = "项目描述")
    private String description;

    /** 项目信息最后更新人 */
    @Excel(name = "项目信息最后更新人")
    private String updateUser;

    /** 创建人 */
    @Excel(name = "创建人")
    private String createUser;

    /** 状态 */
    @Excel(name = "状态")
    private Long projectStatus;

    public void setProjectId(String projectId) 
    {
        this.projectId = projectId;
    }

    public String getProjectId() 
    {
        return projectId;
    }
    public void setProjectName(String projectName) 
    {
        this.projectName = projectName;
    }

    public String getProjectName() 
    {
        return projectName;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
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
    public void setProjectStatus(Long projectStatus) 
    {
        this.projectStatus = projectStatus;
    }

    public Long getProjectStatus() 
    {
        return projectStatus;
    }

}
