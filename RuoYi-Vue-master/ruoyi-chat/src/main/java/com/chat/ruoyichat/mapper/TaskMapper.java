package com.chat.ruoyichat.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import com.chat.ruoyichat.domain.Task;
import org.apache.ibatis.annotations.Param;

/**
 * 任务，用于存储任务相关信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-03-04
 */
public interface TaskMapper 
{
    /**
     * 查询任务，用于存储任务相关信息
     * 
     * @param taskId 任务，用于存储任务相关信息主键
     * @return 任务，用于存储任务相关信息
     */
    public Task selectTaskByTaskId(String taskId);

    /**
     * 查询任务，用于存储任务相关信息列表
     * 
     * @param task 任务，用于存储任务相关信息
     * @return 任务，用于存储任务相关信息集合
     */
    public List<Task> selectTaskList(Task task);

    /**
     * 新增任务，用于存储任务相关信息
     * 
     * @param task 任务，用于存储任务相关信息
     * @return 结果
     */
    public int insertTask(Task task);

    /**
     * 修改任务，用于存储任务相关信息
     * 
     * @param task 任务，用于存储任务相关信息
     * @return 结果
     */
    public int updateTask(Task task);

    /**
     * 删除任务，用于存储任务相关信息
     * 
     * @param taskId 任务，用于存储任务相关信息主键
     * @return 结果
     */
    public int deleteTaskByTaskId(String taskId);

    /**
     * 批量删除任务，用于存储任务相关信息
     * 
     * @param taskIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTaskByTaskIds(String[] taskIds);

    ArrayList<Task> selectTaskListByTaskIds(HashSet<String> taskIds);

    List<Task> selectByEndTimeLess3Min();

    void updateTask2Start(HashSet<String> taskIdSet);

    void deleteTaskByUserIdSet(@Param("userIdMap") HashSet<Long> userIdMap);
}
