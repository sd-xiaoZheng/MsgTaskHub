package com.chat.ruoyichat.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import com.chat.ruoyichat.domain.TaskSendNum;
import org.apache.ibatis.annotations.Param;

/**
 * 任务发送次数Mapper接口
 * 
 * @author ruoyi
 * @date 2025-03-16
 */
public interface TaskSendNumMapper 
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
     * 删除任务发送次数
     * 
     * @param taskContentId 任务发送次数主键
     * @return 结果
     */
    public int deleteTaskSendNumByTaskContentId(String taskContentId);

    /**
     * 批量删除任务发送次数
     * 
     * @param taskContentIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTaskSendNumByTaskContentIds(String[] taskContentIds);

    List<TaskSendNum> selectTaskSendNumListByIds(@Param("taskIdSet")  HashSet<String> taskIdSet);

    void updateTaskSendNumBatch(ArrayList<TaskSendNum> taskSendNumList);

    void deleteTaskSendNumByaccountSet(@Param("accountSet") HashSet<String> accountSet);
}
