package com.chat.ruoyichat.service;

import java.util.List;
import com.chat.ruoyichat.domain.TaskSendNum;

/**
 * 任务发送次数Service接口
 * 
 * @author ruoyi
 * @date 2025-03-16
 */
public interface ITaskSendNumService 
{
    /**
     * 查询任务发送次数
     * 
     * @param taskContentId 任务发送次数主键
     * @return 任务发送次数
     */
    public TaskSendNum selectTaskSendNumByTaskContentId(String taskContentId);

    /**
     * 查询任务发送次数列表
     * 
     * @param taskSendNum 任务发送次数
     * @return 任务发送次数集合
     */
    public List<TaskSendNum> selectTaskSendNumList(TaskSendNum taskSendNum);

    /**
     * 新增任务发送次数
     * 
     * @param taskSendNum 任务发送次数
     * @return 结果
     */
    public int insertTaskSendNum(TaskSendNum taskSendNum);

    /**
     * 修改任务发送次数
     * 
     * @param taskSendNum 任务发送次数
     * @return 结果
     */
    public int updateTaskSendNum(TaskSendNum taskSendNum);

    /**
     * 批量删除任务发送次数
     * 
     * @param taskContentIds 需要删除的任务发送次数主键集合
     * @return 结果
     */
    public int deleteTaskSendNumByTaskContentIds(String[] taskContentIds);

    /**
     * 删除任务发送次数信息
     * 
     * @param taskContentId 任务发送次数主键
     * @return 结果
     */
    public int deleteTaskSendNumByTaskContentId(String taskContentId);
}
