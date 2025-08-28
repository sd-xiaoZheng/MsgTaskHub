package com.chat.ruoyichat.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import com.chat.ruoyichat.domain.TaskContent;
import org.apache.ibatis.annotations.Param;

/**
 * 任务内容，用于存储任务具体内容相关信息Mapper接口
 *
 * @author ruoyi
 * @date 2025-03-04
 */
public interface TaskContentMapper
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
     * 删除任务内容，用于存储任务具体内容相关信息
     *
     * @param taskContentId 任务内容，用于存储任务具体内容相关信息主键
     * @return 结果
     */
    public int deleteTaskContentByTaskContentId(String taskContentId);

    /**
     * 批量删除任务内容，用于存储任务具体内容相关信息
     *
     * @param taskContentIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTaskContentByTaskContentIds(String[] taskContentIds);

    void insertTaskContentBatch(ArrayList<TaskContent> taskContents);

    void updateTaskContent2assignedToBatch(ArrayList<TaskContent> taskContents);

    void deleteTaskContentByTaskId(String[] taskIds);

    ArrayList<TaskContent> selectTaskContentListByTaskIdSet(@Param("taskIdSet") HashSet<String> taskIdSet);

    int updateTaskContent2DeleteBatch(@Param("taskContent")  List<TaskContent> taskContent);

    List<TaskContent> selectDelByTaskId(TaskContent taskContent2);

    void updateTaskContentStartToBatch(List<TaskContent> taskContents);

    void updateTaskContent2UseBatch(ArrayList<TaskContent> taskContents);

    int countTaskContentListByTaskIdSet(@Param("taskIdSet") HashSet<String> strings);

    void updateTaskContent4pausedTask(@Param("taskId") String taskId);

    void updateTaskContent4pausedOnesTask(@Param("taskId")String taskId,@Param("userId")Long userId);

    void updateTaskContent4StartTask(String taskId);

    void updateTaskContent4StartOnesTask(@Param("taskId")String taskId,@Param("userId") Long userId);

    List<TaskContent> selectTaskContentListPages(TaskContent taskContent1);

    List<TaskContent> selectTaskContentListByLimit(@Param("taskId")String taskId, @Param("offset")int offset, @Param("limit")int limit);

    int selectCountByTaskId(@Param("taskId")String taskId);

    int countTaskContentListByTaskIdSetAndUserIdSet(@Param("taskIdSet") HashSet<String> strings, @Param("userIdSet") HashSet<Long> userIdSet);

    List<TaskContent> selectTaskContentList4Export(@Param("userId") HashSet<Long> userId);

    int selectCount(TaskContent taskContentAll);

    void deleteByUserIdSet(@Param("userIdMap") HashSet<Long> userIdMap);

    TaskContent selectTaskContentByCust(String contactValue);

    int selectTaskContentListCount(TaskContent taskContent1);

    List<TaskContent> selectTaskContentListSucc4Export(HashSet<Long> userIdSet);

    List<TaskContent> selectTaskContentListNoSucc4Export(HashSet<Long> userIdSet);
}
