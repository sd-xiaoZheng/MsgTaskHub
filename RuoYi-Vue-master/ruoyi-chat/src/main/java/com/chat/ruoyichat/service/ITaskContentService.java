package com.chat.ruoyichat.service;

import java.util.HashSet;
import java.util.List;
import com.chat.ruoyichat.domain.TaskContent;
import com.chat.ruoyichat.domain.vo.DeleteTaskVo;

/**
 * 任务内容，用于存储任务具体内容相关信息Service接口
 * 
 * @author ruoyi
 * @date 2025-03-04
 */
public interface ITaskContentService 
{
    /**
     * 查询任务内容，用于存储任务具体内容相关信息
     * 
     * @param taskContentId 任务内容，用于存储任务具体内容相关信息主键
     * @return 任务内容，用于存储任务具体内容相关信息
     */
    public TaskContent selectTaskContentByTaskContentId(String taskContentId);

    /**
     * 查询任务内容，用于存储任务具体内容相关信息列表
     * 
     * @param taskContent 任务内容，用于存储任务具体内容相关信息
     * @return 任务内容，用于存储任务具体内容相关信息集合
     */
    public List<TaskContent> selectTaskContentList(TaskContent taskContent);

    /**
     * 新增任务内容，用于存储任务具体内容相关信息
     * 
     * @param taskContent 任务内容，用于存储任务具体内容相关信息
     * @return 结果
     */
    public int insertTaskContent(TaskContent taskContent);

    /**
     * 修改任务内容，用于存储任务具体内容相关信息
     * 
     * @param taskContent 任务内容，用于存储任务具体内容相关信息
     * @return 结果
     */
    public int updateTaskContent(TaskContent taskContent);

    /**
     * 批量删除任务内容，用于存储任务具体内容相关信息
     * 
     * @param taskContentIds 需要删除的任务内容，用于存储任务具体内容相关信息主键集合
     * @return 结果
     */
    public int deleteTaskContentByTaskContentIds(String[] taskContentIds);

    /**
     * 删除任务内容，用于存储任务具体内容相关信息信息
     * 
     * @param taskContentId 任务内容，用于存储任务具体内容相关信息主键
     * @return 结果
     */
    public int deleteTaskContentByTaskContentId(String taskContentId);

    List<TaskContent> selectTaskContentListByTaskId(TaskContent taskContent);

    List<TaskContent> taskContentByUserId(TaskContent taskContent);

    int removeByUserId(DeleteTaskVo userId);

    List<TaskContent> selectTaskContentList4Export(Long userId);
}
