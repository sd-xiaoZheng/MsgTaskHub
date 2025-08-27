package com.chat.ruoyichat.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.utils.uuid.IdUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 任务内容，用于存储任务具体内容相关信息对象 task_content
 * 
 * @author ruoyi
 * @date 2025-03-04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TaskContent extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 任务内容编号 */
    private String taskContentId;

    /** 关联的任务ID */
//    @Excel(name = "关联的任务ID")
    private String taskId;

    /** 任务的接收对象列表，如电话号码、邮箱等 */
    @Excel(name = "号码")
    private String recipientList;

    /** 任务使用的发送内容模板ID */
    @Excel(name = "发送内容")
    private String templateId;

    /** -1失败 0准备 1成功  2进行中 */
    @Excel(name = "任务状态",readConverterExp = "1=成功,2=未使用,0=未使用,-1=失败")
    private Integer taskStatus;

    /** 分配客服ID */
//    @Excel(name = "分配客服ID")
    private Long assignedTo;


    /** 分配客服名称 */
    @Excel(name = "客服名称")
    private String assignedToName;

    /** 删除标志 */
//    @Excel(name = "删除标志")
    private Long delFlag;

    /** 主题 */
//    @Excel(name = "主题")
    private String title;


    /** 是否使用过 0能 1不能*/
//    @Excel(name = "是否使用过")
    private Long isUse;


    public TaskContent(String taskId, String recipientList, String templateId) {
        this.taskContentId = IdUtils.fastSimpleUUID();
        this.taskId = taskId;
        this.recipientList = recipientList;
        this.templateId = templateId;
    }
    public TaskContent(String taskId, String recipientList, String templateId,Long assignedTo,Integer taskStatus) {
        this.taskContentId = IdUtils.fastSimpleUUID();
        this.taskId = taskId;
        this.recipientList = recipientList;
        this.templateId = templateId;
        this.assignedTo = assignedTo;
        this.taskStatus=taskStatus;
    }
}
