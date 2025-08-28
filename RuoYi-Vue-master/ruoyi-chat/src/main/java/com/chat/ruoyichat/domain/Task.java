package com.chat.ruoyichat.domain;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.utils.uuid.IdUtils;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 任务，用于存储任务相关信息对象 task
 *
 * @author ruoyi
 * @date 2025-03-04
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class Task extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 任务的唯一标识符
     */
    private String taskId;

    /**
     * 任务名称
     */
    @Excel(name = "任务名称")
    private String taskName;

    /**
     * 关联的项目ID
     */
    @Excel(name = "关联的项目ID")
    private String projectId;


    /**
     * 关联的项目名称
     */
    @Excel(name = "关联的项目名称")
    private String projectName;


    /**
     * 类型
     */
    @Excel(name = "类型")
    private Long taskType;

    /**
     * 任务的状态，如0暂停、1进行中、2已完成、-1失败 3待分配
     */
    @Excel(name = "状态")
    private Long taskStatus;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;


    /**
     * 组长id
     */
    @Excel(name = "组长id")
    private Long groupId;


    /**
     * 上传数量
     */
    @Excel(name = "上传数量")
    private Long taskNum;

    /**
     * 删除标志 1删除
     */
    @Excel(name = "删除标志")
    private Long delFlag;

    /**
     * 是否是图片1是图片任务
     */
    @Excel(name = "删除标志")
    private Long isImg;


    public Task(String projectId, String taskName,Long taskType,Long groupId,Long taskNum) {
        this.taskName = taskName;
        this.taskId = IdUtils.fastSimpleUUID();
        this.projectId = projectId;
        this.taskType = taskType;
        this.taskStatus = 3L;
        this.startTime = null;
        this.endTime = null;
        this.groupId = groupId;
        this.taskNum=taskNum;
    }
    public Task(String projectId, String taskName,Long taskType,Long groupId,Long taskNum,Long taskStatus) {
        this.taskName = taskName;
        this.taskId = IdUtils.fastSimpleUUID();
        this.projectId = projectId;
        this.taskType = taskType;
        this.taskStatus = taskStatus;
        this.startTime = new Date();
        this.endTime = null;
        this.groupId = groupId;
        this.taskNum=taskNum;
    }
}
