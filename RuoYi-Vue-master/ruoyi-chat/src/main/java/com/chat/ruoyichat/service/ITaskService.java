package com.chat.ruoyichat.service;

import java.io.IOException;
import java.util.List;

import com.chat.ruoyichat.domain.CustomerAccount;
import com.chat.ruoyichat.domain.Task;
import com.chat.ruoyichat.domain.vo.Both1ExcelTemplate;
import com.chat.ruoyichat.domain.vo.Both2ExcelTemplate;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

/**
 * 任务，用于存储任务相关信息Service接口
 *
 * @author ruoyi
 * @date 2025-03-04
 */
public interface ITaskService {
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
     * 批量删除任务，用于存储任务相关信息
     *
     * @param taskIds 需要删除的任务，用于存储任务相关信息主键集合
     * @return 结果
     */
    public int deleteTaskByTaskIds(String[] taskIds);

    /**
     * 删除任务，用于存储任务相关信息信息
     *
     * @param taskId 任务，用于存储任务相关信息主键
     * @return 结果
     */
    public int deleteTaskByTaskId(String taskId);

    String importTaskList(MultipartFile file, String operName, String projectId, String templateId,String taskName,String isBoth) throws IOException;

    String assigned(String taskId);

    Object taskStatusByTaskId();

    void suspendTask(String taskId);

    void StartTask(String taskId);

    List<Both1ExcelTemplate> download1Template(Integer both);

    List<Both2ExcelTemplate> download2Template(Integer both);
}
