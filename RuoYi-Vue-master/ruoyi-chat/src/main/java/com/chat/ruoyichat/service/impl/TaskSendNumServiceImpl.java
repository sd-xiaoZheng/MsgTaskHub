package com.chat.ruoyichat.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.chat.ruoyichat.mapper.TaskSendNumMapper;
import com.chat.ruoyichat.domain.TaskSendNum;
import com.chat.ruoyichat.service.ITaskSendNumService;

/**
 * 任务发送次数Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-03-16
 */
@Service
public class TaskSendNumServiceImpl implements ITaskSendNumService 
{
    @Autowired
    private TaskSendNumMapper taskSendNumMapper;

    /**
     * 查询任务发送次数
     * 
     * @param taskContentId 任务发送次数主键
     * @return 任务发送次数
     */
    @Override
    public TaskSendNum selectTaskSendNumByTaskContentId(String taskContentId)
    {
        return taskSendNumMapper.selectTaskSendNumByTaskContentId(taskContentId);
    }

    /**
     * 查询任务发送次数列表
     * 
     * @param taskSendNum 任务发送次数
     * @return 任务发送次数
     */
    @Override
    public List<TaskSendNum> selectTaskSendNumList(TaskSendNum taskSendNum)
    {
        return taskSendNumMapper.selectTaskSendNumList(taskSendNum);
    }

    /**
     * 新增任务发送次数
     * 
     * @param taskSendNum 任务发送次数
     * @return 结果
     */
    @Override
    public int insertTaskSendNum(TaskSendNum taskSendNum)
    {
        return taskSendNumMapper.insertTaskSendNum(taskSendNum);
    }

    /**
     * 修改任务发送次数
     * 
     * @param taskSendNum 任务发送次数
     * @return 结果
     */
    @Override
    public int updateTaskSendNum(TaskSendNum taskSendNum)
    {
        return taskSendNumMapper.updateTaskSendNum(taskSendNum);
    }

    /**
     * 批量删除任务发送次数
     * 
     * @param taskContentIds 需要删除的任务发送次数主键
     * @return 结果
     */
    @Override
    public int deleteTaskSendNumByTaskContentIds(String[] taskContentIds)
    {
        return taskSendNumMapper.deleteTaskSendNumByTaskContentIds(taskContentIds);
    }

    /**
     * 删除任务发送次数信息
     * 
     * @param taskContentId 任务发送次数主键
     * @return 结果
     */
    @Override
    public int deleteTaskSendNumByTaskContentId(String taskContentId)
    {
        return taskSendNumMapper.deleteTaskSendNumByTaskContentId(taskContentId);
    }
}
