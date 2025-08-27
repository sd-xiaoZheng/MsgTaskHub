package com.chat.ruoyichat.service.impl;

import java.util.*;

import com.chat.ruoyichat.domain.CustomerServiceDetail;
import com.chat.ruoyichat.domain.Task;
import com.chat.ruoyichat.domain.vo.DeleteTaskVo;
import com.chat.ruoyichat.mapper.CustomerServiceDetailMapper;
import com.chat.ruoyichat.mapper.SessionRecordMapper;
import com.chat.ruoyichat.mapper.TaskMapper;
import com.chat.ruoyichat.service.ITaskService;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.system.mapper.SysUserMapper;
import io.netty.util.internal.ObjectUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.chat.ruoyichat.mapper.TaskContentMapper;
import com.chat.ruoyichat.domain.TaskContent;
import com.chat.ruoyichat.service.ITaskContentService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 任务内容，用于存储任务具体内容相关信息Service业务层处理
 *
 * @author ruoyi
 * @date 2025-03-04
 */
@Service
public class TaskContentServiceImpl implements ITaskContentService {
    @Autowired
    private TaskContentMapper taskContentMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private CustomerServiceDetailMapper customerServiceDetailMapper;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private SessionRecordMapper sessionRecordMapper;

    /**
     * 查询任务内容，用于存储任务具体内容相关信息
     *
     * @param taskContentId 任务内容，用于存储任务具体内容相关信息主键
     * @return 任务内容，用于存储任务具体内容相关信息
     */
    @Override
    public TaskContent selectTaskContentByTaskContentId(String taskContentId) {
        return taskContentMapper.selectTaskContentByTaskContentId(taskContentId);
    }

    /**
     * 查询任务内容，用于存储任务具体内容相关信息列表
     *
     * @param taskContent 任务内容，用于存储任务具体内容相关信息
     * @return 任务内容，用于存储任务具体内容相关信息
     */
    @Override
    public List<TaskContent> selectTaskContentList(TaskContent taskContent) {
        List<TaskContent> taskContents = taskContentMapper.selectTaskContentList(taskContent);
        //分配给的用户名称
        HashSet<Long> assignedToNameList = new HashSet<>();
        for (TaskContent content : taskContents) {
            Long assignedTo = content.getAssignedTo();
            if (ObjectUtils.isNotEmpty(assignedTo)) {
                assignedToNameList.add(content.getAssignedTo());
            }
        }
        if (!assignedToNameList.isEmpty()) {
            ArrayList<SysUser> sysUsers = sysUserMapper.selectUserByIdList(assignedToNameList);
            //存入map字典id-nikeName
            HashMap<Long, String> integerStringHashMap = new HashMap<>();
            for (SysUser sysUser : sysUsers) {
                integerStringHashMap.put(sysUser.getUserId(), sysUser.getNickName());
            }
            for (TaskContent content : taskContents) {
                content.setAssignedToName(integerStringHashMap.get(content.getAssignedTo()));
            }
        }
        return taskContents;
    }

    /**
     * 新增任务内容，用于存储任务具体内容相关信息
     *
     * @param taskContent 任务内容，用于存储任务具体内容相关信息
     * @return 结果
     */
    @Override
    public int insertTaskContent(TaskContent taskContent) {
        taskContent.setTaskContentId(IdUtils.fastSimpleUUID());
        return taskContentMapper.insertTaskContent(taskContent);
    }

    /**
     * 修改任务内容，用于存储任务具体内容相关信息
     *
     * @param taskContent 任务内容，用于存储任务具体内容相关信息
     * @return 结果
     */
    @Override
    public int updateTaskContent(TaskContent taskContent) {
        return taskContentMapper.updateTaskContent(taskContent);
    }

    /**
     * 批量删除任务内容，用于存储任务具体内容相关信息
     *
     * @param taskContentIds 需要删除的任务内容，用于存储任务具体内容相关信息主键
     * @return 结果
     */
    @Override
    public int deleteTaskContentByTaskContentIds(String[] taskContentIds) {
        return taskContentMapper.deleteTaskContentByTaskContentIds(taskContentIds);
    }

    /**
     * 删除任务内容，用于存储任务具体内容相关信息信息
     *
     * @param taskContentId 任务内容，用于存储任务具体内容相关信息主键
     * @return 结果
     */
    @Override
    public int deleteTaskContentByTaskContentId(String taskContentId) {
        return taskContentMapper.deleteTaskContentByTaskContentId(taskContentId);
    }

    @Override
    public List<TaskContent> selectTaskContentListByTaskId(TaskContent taskContent) {
        return taskContentMapper.selectTaskContentList(taskContent);
    }

    @Override
    public List<TaskContent> taskContentByUserId(TaskContent taskContent) {
        return taskContentMapper.selectTaskContentList(taskContent);
    }


    /**
     * 删除个人为执行任务
     *
     * @param deleteTaskVo
     * @return 4.1弃用 组员也可以删除整组
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removeByUserId(DeleteTaskVo deleteTaskVo) {
        Long userId = SecurityUtils.getUserId();
        TaskContent taskContent = new TaskContent();
        taskContent.setAssignedTo(userId);
//        taskContent.setTaskStatus(0);
        taskContent.setDelFlag(0L);
        taskContent.setTaskId(deleteTaskVo.getTaskId());
        List<TaskContent> taskContents = taskContentMapper.selectTaskContentList(taskContent);
        if (taskContents.isEmpty()) {
            return 0;
        }
        int surplusNumDel=0;
        for (TaskContent content : taskContents) {
            if (content.getIsUse().equals(0L)){
                surplusNumDel++;
            }
        }
        int i = taskContentMapper.updateTaskContent2DeleteBatch(taskContents);
        //删除完 同步自己的状态
        CustomerServiceDetail customerServiceDetail = customerServiceDetailMapper.selectCustomerDetailByUserId(userId);
        Long surplusNum = customerServiceDetail.getSurplusNum();
        customerServiceDetail.setSurplusNum(surplusNum - surplusNumDel);
        customerServiceDetailMapper.updateCustomerServiceDetail(customerServiceDetail);
        TaskContent taskContent1 = new TaskContent();
        taskContent1.setTaskId(deleteTaskVo.getTaskId());
        List<TaskContent> taskContents1 = taskContentMapper.selectTaskContentList(taskContent1);
        if (taskContents1.isEmpty()){
            taskMapper.deleteTaskByTaskId(deleteTaskVo.getTaskId());
        }
        //删除后检测任务是否已经完成
//        Task task = taskMapper.selectTaskByTaskId(deleteTaskVo.getTaskId());
//        TaskContent taskContent1 = new TaskContent();
//        taskContent1.setTaskId(deleteTaskVo.getTaskId());
//        taskContent1.setTaskStatus(1);
//        //所有成功的
//        List<TaskContent> taskContents1 = taskContentMapper.selectTaskContentList(taskContent1);
//        //已经删除的
//        TaskContent taskContent2 = new TaskContent();
//        taskContent2.setTaskId(deleteTaskVo.getTaskId());
//        taskContent2.setDelFlag(1L);
//        List<TaskContent> taskContents2 = taskContentMapper.selectDelByTaskId(taskContent2);
//        if ((taskContents1.size() + taskContents.size() + taskContents2.size()) >= task.getTaskNum()) {
//            task.setTaskStatus(2L);
//            task.setEndTime(new Date());
//            taskMapper.updateTask(task);
//        }
        return i;
    }

    @Override
    public List<TaskContent> selectTaskContentList4Export(Long userId) {
        List<TaskContent> taskContents;
        HashSet<Long> userIdSet = new HashSet<>();
        List<CustomerServiceDetail> customerServiceDetails = customerServiceDetailMapper.selectCustomerDetailByManagerId(userId);
        if (customerServiceDetails.isEmpty()) {
            //说明是组员
            userIdSet.add(userId);
            taskContents = taskContentMapper.selectTaskContentList4Export(userIdSet);
        }else {
            for (CustomerServiceDetail customerServiceDetail : customerServiceDetails) {
                userIdSet.add(customerServiceDetail.getUserId());
            }
            taskContents = taskContentMapper.selectTaskContentList4Export(userIdSet);
        }

        //分配给的用户名称
        HashSet<Long> assignedToNameList = new HashSet<>();
        for (TaskContent content : taskContents) {
            Long assignedTo = content.getAssignedTo();
            if (ObjectUtils.isNotEmpty(assignedTo)) {
                assignedToNameList.add(content.getAssignedTo());
            }
        }
        if (!assignedToNameList.isEmpty()) {
            ArrayList<SysUser> sysUsers = sysUserMapper.selectUserByIdList(assignedToNameList);
            //存入map字典id-nikeName
            HashMap<Long, String> integerStringHashMap = new HashMap<>();
            for (SysUser sysUser : sysUsers) {
                integerStringHashMap.put(sysUser.getUserId(), sysUser.getNickName());
            }
            for (TaskContent content : taskContents) {
                content.setAssignedToName(integerStringHashMap.get(content.getAssignedTo()));
            }
        }
        return taskContents;
    }
}
