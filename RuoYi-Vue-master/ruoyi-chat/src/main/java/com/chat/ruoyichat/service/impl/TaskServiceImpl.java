package com.chat.ruoyichat.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.ServerException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;
import com.chat.ruoyichat.domain.*;
import com.chat.ruoyichat.domain.dto.PhoneContentInfo;
import com.chat.ruoyichat.domain.vo.Both1ExcelTemplate;
import com.chat.ruoyichat.domain.vo.Both2ExcelTemplate;
import com.chat.ruoyichat.domain.vo.CustomerTaskStatusVo;
import com.chat.ruoyichat.mapper.*;
import com.chat.ruoyichat.service.ICustomerServiceDetailService;
import com.chat.ruoyichat.utils.excel.EasyExcelUtil;
import com.chat.ruoyichat.websocket.WebSocketService;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.system.mapper.SysUserMapper;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.chat.ruoyichat.service.ITaskService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.websocket.Session;

import static com.ruoyi.common.utils.PageUtils.startPage;

/**
 * 任务，用于存储任务相关信息Service业务层处理
 *
 * @author ruoyi
 * @date 2025-03-04
 */
@Slf4j
@Service
public class TaskServiceImpl implements ITaskService {
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private CustomerAccountMapper customerAccountMapper;
    @Autowired
    private TaskContentMapper taskContentMapper;
    @Autowired
    private SendContentTemplateContentMapper sendContentTemplateContentMapper;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private CustomerServiceDetailMapper customerServiceDetailMapper;
    @Autowired
    private ICustomerServiceDetailService customerServiceDetailService;
    @Autowired
    private SendContentTemplateContentMapper contentTemplateContentMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 查询任务，用于存储任务相关信息
     *
     * @param taskId 任务，用于存储任务相关信息主键
     * @return 任务，用于存储任务相关信息
     */
    @Override
    public Task selectTaskByTaskId(String taskId) {
        return taskMapper.selectTaskByTaskId(taskId);
    }

    /**
     * 查询任务，用于存储任务相关信息列表
     *
     * @param task 任务，用于存储任务相关信息
     * @return 任务，用于存储任务相关信息
     */
    @Override
    public List<Task> selectTaskList(Task task) {
        Long userId = SecurityUtils.getUserId();
        CustomerServiceDetail customerServiceDetail = customerServiceDetailMapper.selectCustomerDetailByUserId(userId);
        if (customerServiceDetail.getManagerId().equals(-1L)) {
            //组长
            startPage();
            task.setGroupId(customerServiceDetail.getUserId());
            List<Task> tasks = taskMapper.selectTaskList(task);
            //查询所有项目名称
            HashSet<String> projectIdSet = new HashSet<>();
            for (Task item : tasks) {
                projectIdSet.add(item.getProjectId());
            }
            if (!projectIdSet.isEmpty()) {
                ArrayList<Project> projectList = projectMapper.selectProjectByIdList(projectIdSet);
                //创建名称字典 填充名称
                HashMap<String, String> stringStringHashMap = new HashMap<>();
                for (Project project : projectList) {
                    stringStringHashMap.put(project.getProjectId(), project.getProjectName());
                }
                for (Task item : tasks) {
                    item.setProjectName(stringStringHashMap.get(item.getProjectId()));
                }
            }
            return tasks;
        } else {
            TaskContent taskContent = new TaskContent();
            taskContent.setAssignedTo(customerServiceDetail.getUserId());
            taskContent.setIsUse(0L);
            List<TaskContent> taskContents = taskContentMapper.selectTaskContentList(taskContent);
            if (taskContents.isEmpty()) {
                return new ArrayList<>();
            }
            HashSet<String> taskIdIsPaused = new HashSet<>();
            for (TaskContent content : taskContents) {
                if (content.getTaskStatus().equals(0)) {
                    taskIdIsPaused.add(content.getTaskId());
                }
            }

            HashSet<String> taskIds = new HashSet<>();
            for (TaskContent content : taskContents) {
                taskIds.add(content.getTaskId());
            }

            ArrayList<Task> taskArrayList = taskMapper.selectTaskListByTaskIds(taskIds);
            for (Task task1 : taskArrayList) {
                if (taskIdIsPaused.contains(task1.getTaskId())) {
                    task1.setTaskStatus(0L);
                } else {
                    task1.setTaskStatus(1L);
                }
            }
            return taskArrayList;
        }
    }

    /**
     * 新增任务，用于存储任务相关信息
     *
     * @param task 任务，用于存储任务相关信息
     * @return 结果
     */
    @Override
    public int insertTask(Task task) {
        task.setTaskId(IdUtils.fastSimpleUUID());
        task.setCreateTime(DateUtils.getNowDate());
        return taskMapper.insertTask(task);
    }

    /**
     * 修改任务，用于存储任务相关信息
     *
     * @param task 任务，用于存储任务相关信息
     * @return 结果
     */
    @Override
    public int updateTask(Task task) {
        return taskMapper.updateTask(task);
    }

    /**
     * 批量删除任务，用于存储任务相关信息
     *
     * @param taskIds 需要删除的任务，用于存储任务相关信息主键
     * @return 结果
     * 弃用
     */
    @Transactional
    @Override
    public int deleteTaskByTaskIds(String[] taskIds) {
        int i = taskMapper.deleteTaskByTaskIds(taskIds);

        if (i > 0) {
            taskContentMapper.deleteTaskContentByTaskId(taskIds);
        }
        return i;
    }

    /**
     * 删除任务，用于存储任务相关信息信息
     *
     * @param taskId 任务，用于存储任务相关信息主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTaskByTaskId(String taskId) {
        TaskContent taskContent = new TaskContent();
        taskContent.setTaskId(taskId);
        List<TaskContent> taskContents = taskContentMapper.selectTaskContentList(taskContent);
        if (taskContents.isEmpty()) {
            return taskMapper.deleteTaskByTaskId(taskId);
        }
        String[] ids = taskContents.stream()
                .filter(task -> task.getIsUse() == 0)
                .map(TaskContent::getTaskContentId)
                .toArray(String[]::new);
        if (ids.length > 0) {
            taskContentMapper.deleteTaskContentByTaskContentIds(ids);
        }
        //所有id
        HashSet<Long> userIdSet = new HashSet<>();
        for (TaskContent content : taskContents) {
            if (ObjectUtils.isNotEmpty(content.getAssignedTo())) {
                userIdSet.add(content.getAssignedTo());
            }
        }
        if (!userIdSet.isEmpty()) {
            List<CustomerServiceDetail> customerServiceDetails = customerServiceDetailMapper.selectCustomerDetailByUserIdListNoStatus(userIdSet);
            //按人分入字典
            HashMap<Long, ArrayList<TaskContent>> longTaskContentHashMap = new HashMap<>();
            for (TaskContent content : taskContents) {
                ArrayList<TaskContent> taskContents1 = longTaskContentHashMap.get(content.getAssignedTo());
                if (ObjectUtils.isEmpty(taskContents1)) {
                    taskContents1 = new ArrayList<>();
                }
                if (content.getIsUse().equals(0L)) {
                    taskContents1.add(content);
                    longTaskContentHashMap.put(content.getAssignedTo(), taskContents1);
                }
            }
            for (CustomerServiceDetail customerServiceDetail : customerServiceDetails) {
                ArrayList<TaskContent> taskContents1 = longTaskContentHashMap.getOrDefault(customerServiceDetail.getUserId(), new ArrayList<>());
                customerServiceDetail.setSurplusNum(customerServiceDetail.getSurplusNum() - taskContents1.size());
            }
            //修改个人详情
            customerServiceDetailMapper.updateCustomerServiceDetail2SurplusNumBatch(customerServiceDetails);
        }
        return taskMapper.deleteTaskByTaskId(taskId);
    }


    /**
     * 导入任务
     *
     * @param file
     * @param operName
     * @param projectId
     * @return
     * @throws IOException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String importTaskList(MultipartFile file, String operName, String projectId, String templateId, String taskName, String isBoth) throws IOException {
        List<Map<Integer, String>> maps = EasyExcelUtil.syncRead(file.getInputStream(), 0, 0);
        Map<Integer, String> remove = maps.remove(0);//拿到表头
        ArrayList<String> phoneStrList = maps.stream().flatMap(map -> map.values().stream()).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        if (phoneStrList.isEmpty()) {
            throw new ServiceException("空文件(empty file)");
        }

        if (StringUtils.isNotEmpty(isBoth) && isBoth.equals("2")) {
            //走这里就是上传图片，只需要上传手机号就可以了，图片另外上传
            int size = remove.size();
            if (size > 1) {
                throw new ServerException("上传文件格式错误");
            }
        }

        //如果任务名称没有就随机
        if (ObjectUtils.isEmpty(taskName)) {
            taskName = SecurityUtils.getUsername() + "_" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        }

        int size = maps.size();
        //查询当前用户是否是组长
        Long userId = SecurityUtils.getUserId();
        CustomerServiceDetail customerServiceDetail = customerServiceDetailService.selectCustomerDetailByUserId(userId);
        Long managerId = customerServiceDetail.getManagerId();

        if (managerId.equals(-1L)) {
            Task task2 = new Task();
            task2.setGroupId(userId);
            List<Task> tasksList = taskMapper.selectTaskList(task2);
            for (Task task : tasksList) {
                if (!task.getTaskStatus().equals(2L)) {
                    throw new ServiceException("上传失败,当前有未完成任务(Upload failed. There are unfinished tasks at present.)");
                }
            }
            List<CustomerServiceDetail> customerServiceDetails = customerServiceDetailService.selectCustomerDetailByManagerId(customerServiceDetail.getUserId());
//            int surplus = 0;
            int maxLoad = 0;
            int surplusNum = 0;
            for (CustomerServiceDetail serviceDetail : customerServiceDetails) {
//                surplus += serviceDetail.getSurplusNum();
                maxLoad += serviceDetail.getMaxLoad();
                surplusNum += serviceDetail.getSurplusNum();
            }

            //查询组内所有任务总数
            Task task1 = new Task();
            task1.setGroupId(userId);
            task1.setDelFlag(0L);
            List<Task> tasks = taskMapper.selectTaskList(task1);
            int taskMax = 0;
            if (tasks.size() > 0) {
                HashSet<String> taskIdSet = new HashSet<>();
                for (Task task : tasks) {
                    taskIdSet.add(task.getTaskId());
                }
                HashSet<Long> userIdSet = new HashSet<>();
                for (CustomerServiceDetail csd : customerServiceDetails) {
                    userIdSet.add(csd.getUserId());
                }
                taskMax = taskContentMapper.countTaskContentListByTaskIdSetAndUserIdSet(taskIdSet, userIdSet);
            }
            size += taskMax;
            if (size > maxLoad - surplusNum) {
                throw new ServiceException("导入失败，上传任务数量大于组内总发量(Import failed. The number of tasks exceeds the limit)");
            }
            //导入客户号码
//            readExcel4SMS(phoneStrList, operName, projectId);
            //如果任务是超管创建 则等待分配 如果是客服管理员就认定是管理员的
            //生成任务
            Task task = new Task(projectId, taskName, -1L, userId, (long) maps.size());


            //获取发送模板内容
            //生成任务内容
            ArrayList<TaskContent> taskContents = new ArrayList<>();

            if (StringUtils.isNotEmpty(isBoth) && isBoth.equals("1")) {//手机+话术
                List<PhoneContentInfo> phoneContentInfos = readExcel(file);
                if (ObjectUtils.isEmpty(phoneContentInfos)) {
                    throw new ServiceException("请检查文件格式：手机号 话术(Please check the file format: Mobile phone number, Script.)");
                }
                for (PhoneContentInfo phoneContentInfo : phoneContentInfos) {
                    String phone = phoneContentInfo.getPhone();
                    if (phone.contains("号")) {
                        continue;
                    }
                    String content = phoneContentInfo.getContent();
                    taskContents.add(new TaskContent(task.getTaskId(), phone, content, null, 0));
                }
            } else if (StringUtils.isNotEmpty(isBoth) && isBoth.equals("0")) {
                //一定不是手机号+话术 只允许有手机号
                if (remove.size() > 1) {
                    throw new ServiceException("只允许有一列数据（联系号）(Only one column of data (contact number) is allowed.)");
                }
                List<SendContentTemplateContent> ReplyStr = new ArrayList<>();
                if (StringUtils.isEmpty(templateId)) {
                    List<Object> range = redisTemplate.opsForList().range(CacheConstants.TEMPALTE_ONES + SecurityUtils.getUserId(), 0, -1);
                    if (ObjectUtils.isNotEmpty(range)) {
                        ArrayList<String> collect = range.stream()
                                .filter(Objects::nonNull)
                                .map(Object::toString)
                                .collect(Collectors.toCollection(ArrayList::new));
                        addContentTextByString(phoneStrList, taskContents, task, collect);
                        redisTemplate.delete(CacheConstants.TEMPALTE_ONES + SecurityUtils.getUserId());
                    } else {
                        throw new ServiceException("请选择或者上传话术模板(Please select or upload a template of your speech.)");
                    }
                } else {
                    SendContentTemplateContent sendContentTemplateContent = new SendContentTemplateContent();
                    sendContentTemplateContent.setTemplateId(templateId);
                    sendContentTemplateContent.setIsUse(0L);
                    ReplyStr = sendContentTemplateContentMapper.selectSendContentTemplateContentList(sendContentTemplateContent);
                    if (ReplyStr.isEmpty()) {
                        throw new ServiceException("模板剩余使用量为0,请重置或导入新模板(The remaining usage amount of the template is 0. Please reset or import a new template.)");
                    }
                    if (ReplyStr.size() < phoneStrList.size()) {
                        sendContentTemplateContentMapper.updateSendContentTemplateContentReset(templateId);
                        //重置模板所有为0 再查询赋值
                        ReplyStr = sendContentTemplateContentMapper.selectSendContentTemplateContentList(sendContentTemplateContent);
                    }
                    ArrayList<SendContentTemplateContent> sendContentTemplateContents = addContentText(phoneStrList, taskContents, task, ReplyStr);

                    contentTemplateContentMapper.updateSendContentTemplateContent2IsUseBatch(sendContentTemplateContents);
                }
            } else if (StringUtils.isNotEmpty(isBoth) && isBoth.equals("2")) {
                for (String phoneNum : phoneStrList) {
                    taskContents.add(new TaskContent(task.getTaskId(), phoneNum, null, customerServiceDetail.getUserId(), 0));
                }
                task.setIsImg(1L);
            }
            taskContentMapper.insertTaskContentBatch(taskContents);
            taskMapper.insertTask(task);
        } else {
            Task taskIsEnd = new Task();
            taskIsEnd.setTaskType(customerServiceDetail.getUserId());
            List<Task> tasksList = taskMapper.selectTaskList(taskIsEnd);
            for (Task task : tasksList) {
                if (!task.getTaskStatus().equals(2L)) {
                    throw new ServiceException("上传失败,当前有未完成任务(Upload failed. There are unfinished tasks at present.)");
                }
            }
//            TaskContent taskContent = new TaskContent();
//            taskContent.setAssignedTo(customerServiceDetail.getUserId());
//            List<TaskContent> taskContents1 = taskContentMapper.selectTaskContentList(taskContent);
//            if (!taskContents1.isEmpty()) {
//                for (TaskContent content : taskContents1) {
//                    if (!content.getTaskStatus().equals(1) || !content.getTaskStatus().equals(-1)) {
//                        throw new ServiceException("上传失败,当前有未完成任务");
//                    }
//                }
//            }
            Long surplusNum = customerServiceDetail.getSurplusNum();
            Long maxLoad = customerServiceDetail.getMaxLoad();
            //查询项目总剩余
            int taskAllMax = 0;
            HashSet<String> taskIdSet = new HashSet<>();
            Long managerId2 = customerServiceDetail.getManagerId();
            Task task2 = new Task();
            task2.setGroupId(managerId2);
            List<Task> tasks1 = taskMapper.selectTaskList(task2);
            for (Task task : tasks1) {
                taskIdSet.add(task.getTaskId());
            }
            if (!taskIdSet.isEmpty()) {
                int taskContentListTaskAll = taskContentMapper.countTaskContentListByTaskIdSet(taskIdSet);
                taskAllMax = taskContentListTaskAll;//项目中已经上传的
            }
            List<CustomerServiceDetail> customerServiceDetails = customerServiceDetailMapper.selectCustomerDetailByManagerId(customerServiceDetail.getManagerId());
            int groupMax = 0;
            for (CustomerServiceDetail serviceDetail : customerServiceDetails) {
                groupMax += serviceDetail.getMaxLoad();
            }
            taskAllMax += size;
            if (taskAllMax > groupMax) {
                throw new ServiceException("导入失败，上传任务数量大于组内剩余发送量或组内有未分配任务(Import failed. The number of tasks exceeds the limit or there are unassigned tasks within the group.)");
            }
            //查询个人总剩余

            Long userId1 = SecurityUtils.getUserId();
            int taskMax = 0;
            Task taskTemp = new Task();
            taskTemp.setTaskType(userId1);
            List<Task> tasks = taskMapper.selectTaskList(taskTemp);
            if (!tasks.isEmpty()) {
                taskIdSet.clear();
                for (Task task : tasks) {
                    taskIdSet.add(task.getTaskId());
                }
                taskMax = taskContentMapper.countTaskContentListByTaskIdSet(taskIdSet);
            }
            size += taskMax;

            if (size > maxLoad - surplusNum) {
                throw new ServiceException("导入失败，上传任务数量大于个人剩余发送量(Import failed. The number of tasks exceeds the limit.)");
            }
            //导入客户号码
//            readExcel4SMS(phoneStrList, operName, projectId);
            //如果任务是超管创建 则等待分配 如果是客服管理员就认定是管理员的
            //生成任务
            Long managerId1 = customerServiceDetail.getManagerId();
            Task task = new Task(projectId, taskName, customerServiceDetail.getUserId(), managerId1, (long) maps.size(), 0L);

            //获取发送模板内容
            //生成任务内容
            ArrayList<TaskContent> taskContents = new ArrayList<>();
            if (StringUtils.isNotEmpty(isBoth) && isBoth.equals("1")) {
                List<PhoneContentInfo> phoneContentInfos = readExcel(file);
                if (ObjectUtils.isEmpty(phoneContentInfos)) {
                    throw new ServiceException("请检查文件格式：手机号 话术(Please check the file format: Mobile phone number, Script.)");
                }
                for (PhoneContentInfo phoneContentInfo : phoneContentInfos) {
                    String phone = phoneContentInfo.getPhone();
                    if (phone.contains("号")) {
                        continue;
                    }
                    String content = phoneContentInfo.getContent();
                    taskContents.add(new TaskContent(task.getTaskId(), phone, content, customerServiceDetail.getUserId(), 0));
                }
            } else if (StringUtils.isNotEmpty(isBoth) && isBoth.equals("0")) {
                //一定不是手机号+话术 只允许有手机号
                if (remove.size() > 1) {
                    throw new ServiceException("只允许有一列数据（联系号）(Only one column of data (contact number) is allowed.)");
                }
                List<SendContentTemplateContent> ReplyStr = new ArrayList<>();
                if (StringUtils.isEmpty(templateId)) {
                    List<Object> range = redisTemplate.opsForList().range(CacheConstants.TEMPALTE_ONES + SecurityUtils.getUserId(), 0, -1);
                    if (ObjectUtils.isNotEmpty(range)) {
                        ArrayList<String> collect = range.stream()
                                .filter(Objects::nonNull)
                                .map(Object::toString)
                                .collect(Collectors.toCollection(ArrayList::new));
                        addContentTextByStringWithOne(phoneStrList, taskContents, task, collect, customerServiceDetail);
                        redisTemplate.delete(CacheConstants.TEMPALTE_ONES + SecurityUtils.getUserId());
                    } else {
                        throw new ServiceException("请选择或者上传话术模板(Please select or upload a template of your speech.)");
                    }
                } else {
                    SendContentTemplateContent sendContentTemplateContent = new SendContentTemplateContent();
                    sendContentTemplateContent.setTemplateId(templateId);
                    sendContentTemplateContent.setIsUse(0L);
                    ReplyStr = sendContentTemplateContentMapper.selectSendContentTemplateContentList(sendContentTemplateContent);
                    if (ReplyStr.isEmpty()) {
                        throw new ServiceException("模板剩余使用量为0,请重置或导入新模板(The remaining usage amount of the template is 0. Please reset or import a new template.)");
                    }
                    if (ReplyStr.size() < phoneStrList.size()) {
                        sendContentTemplateContentMapper.updateSendContentTemplateContentReset(templateId);
                        //重置模板所有为0 再查询赋值
                        ReplyStr = sendContentTemplateContentMapper.selectSendContentTemplateContentList(sendContentTemplateContent);
                    }
                    ArrayList<SendContentTemplateContent> sendContentTemplateContents = addContentTextWithOne(phoneStrList, taskContents, task, ReplyStr, customerServiceDetail);
                    contentTemplateContentMapper.updateSendContentTemplateContent2IsUseBatch(sendContentTemplateContents);
                }
            } else if (StringUtils.isNotEmpty(isBoth) && isBoth.equals("2")) {
                //到这里是发图片 只上传手机号就好
                for (String phoneNum : phoneStrList) {
                    taskContents.add(new TaskContent(task.getTaskId(), phoneNum, null, customerServiceDetail.getUserId(), 0));
                }
                task.setIsImg(1L);
            }
//            ArrayList<TaskContent> taskContents = new ArrayList<>();
//            for (String s : phoneStrList) {
//                taskContents.add(new TaskContent(task.getTaskId(), s, ReplyStr.get(random.nextInt(ReplyStr.size())).getTemplateContent(), customerServiceDetail.getUserId(), 2));
//            }
            CustomerServiceDetail customerServiceDetail1 = new CustomerServiceDetail();
            customerServiceDetail1.setMaxLoad(customerServiceDetail.getMaxLoad());
            customerServiceDetail1.setCustomerId(customerServiceDetail.getCustomerId());
            customerServiceDetail1.setSurplusNum(surplusNum + (long) taskContents.size());
            //刚上传任务就算为发送成功
            customerServiceDetail1.setSendNum(customerServiceDetail1.getSurplusNum());
//            if (customerServiceDetail.getMaxLoad()<customerServiceDetail1.getSurplusNum()){
//                throw new ServiceException("上传失败，请刷新重试");
//            }
            int i = customerServiceDetailMapper.updateSurplusNumByCustomerId(customerServiceDetail1);
            if (i < 1) {
                throw new ServiceException("上传失败，请刷新重试(Upload failed. Please refresh and try again.)");
            }
            taskContentMapper.insertTaskContentBatch(taskContents);
            taskMapper.insertTask(task);
        }
        return "导入成功(success)";
    }

    private ArrayList<SendContentTemplateContent> addContentText(ArrayList<String> phoneStrList, ArrayList<TaskContent> taskContents, Task task, List<SendContentTemplateContent> ReplyStr) {
        ArrayList<SendContentTemplateContent> sendContentTemplateContents = new ArrayList<>();
        for (int i = 0, j = 0; i < phoneStrList.size(); i++) {
            if (j >= ReplyStr.size()) {
                j = 0;
            }
            SendContentTemplateContent sendContentTemplateContent = ReplyStr.get(j);
            sendContentTemplateContent.setIsUse(1L);
            taskContents.add(new TaskContent(task.getTaskId(), phoneStrList.get(i), sendContentTemplateContent.getTemplateContent(), null, 2));
            sendContentTemplateContents.add(sendContentTemplateContent);
            j++;
        }
        return sendContentTemplateContents;
    }

    private ArrayList<SendContentTemplateContent> addContentTextWithOne(ArrayList<String> phoneStrList, ArrayList<TaskContent> taskContents, Task task, List<SendContentTemplateContent> ReplyStr, CustomerServiceDetail customerServiceDetail) {
        ArrayList<SendContentTemplateContent> sendContentTemplateContents = new ArrayList<>();
        for (int i = 0, j = 0; i < phoneStrList.size(); i++) {
            if (j >= ReplyStr.size()) {
                j = 0;
            }
            SendContentTemplateContent sendContentTemplateContent = ReplyStr.get(j);
            sendContentTemplateContent.setIsUse(1L);
            taskContents.add(new TaskContent(task.getTaskId(), phoneStrList.get(i), sendContentTemplateContent.getTemplateContent(), customerServiceDetail.getUserId(), 0));
            sendContentTemplateContents.add(sendContentTemplateContent);
            j++;
        }
        return sendContentTemplateContents;
    }

    private void addContentTextByString(ArrayList<String> phoneStrList, ArrayList<TaskContent> taskContents, Task task, ArrayList<String> collect) {
        for (int i = 0, j = 0; i < phoneStrList.size(); i++) {
            if (j >= collect.size()) {
                j = 0;
            }
            String str = collect.get(j);
            taskContents.add(new TaskContent(task.getTaskId(), phoneStrList.get(i), str, null, 0));
            j++;
        }
    }

    private void addContentTextByStringWithOne(ArrayList<String> phoneStrList, ArrayList<TaskContent> taskContents, Task task, ArrayList<String> collect, CustomerServiceDetail customerServiceDetail) {
        for (int i = 0, j = 0; i < phoneStrList.size(); i++) {
            if (j >= collect.size()) {
                j = 0;
            }
            String str = collect.get(j);
            taskContents.add(new TaskContent(task.getTaskId(), phoneStrList.get(i), str, customerServiceDetail.getUserId(), 0));
            j++;
        }
    }


    /**
     * 分配任务
     *
     * @param taskId 将哪个任务分配
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String assigned(String taskId) {
        if (ObjectUtils.isEmpty(taskId)) {
            throw new ServiceException("请选择任务");
        }
        Long userId = SecurityUtils.getUserId();

        CustomerServiceDetail customerServiceDetailTemp = new CustomerServiceDetail();
        customerServiceDetailTemp.setManagerId(userId);
        //获取每个客服的可分配数量一个个进行分配满
        List<CustomerServiceDetail> CustomerServiceDetailList = customerServiceDetailMapper.selectCustomerServiceDetailList(customerServiceDetailTemp);
        if (CustomerServiceDetailList.isEmpty()) {
            return "组长权限可分配任务";
        }
//        TaskContent taskContent = new TaskContent();
//        taskContent.setTaskId(taskId);
        // 初始加载任务数量
        int taskNum = taskContentMapper.selectCountByTaskId(taskId);
        int batchSize = taskNum > 10 ? taskNum / 10 : taskNum;

//        if (taskNum > 10) {
//            batchSize = taskNum / 10;
//        } else {
//            batchSize = taskNum;
//        }
        int taskIndex = 0;
        int taskContentNum = 0;
        int pace = 10;
        List<TaskContent> taskContents;
        ArrayList<TaskContent> taskContentsUpdate = new ArrayList<>();
        List<CustomerServiceDetail> needUpdate = new ArrayList<>();

        // 首次加载任务
        taskContents = taskContentMapper.selectTaskContentListByLimit(taskId, taskIndex, batchSize);
        if (Objects.isNull(taskContents) || taskContents.isEmpty()) {
            return "分配失败，任务为空";
        }
        taskContentNum += taskContents.size();


        loop:
        for (CustomerServiceDetail customer : CustomerServiceDetailList) {
            Long surplusNum = customer.getSurplusNum();
            Long maxLoad = customer.getMaxLoad();
            if (surplusNum >= maxLoad) {
                continue;
            }
            long excess = maxLoad - surplusNum;
            long surplusNumTemp = 0L;
            for (int i = 0; i < excess; i++) {
                // 检查当前任务列表是否还有任务
                if (taskIndex >= taskContentNum) {
                    taskContentMapper.updateTaskContent2assignedToBatch(taskContentsUpdate);
                    taskContentsUpdate.clear();
                    // 如果任务不足，清空当前任务列表并重新加载
                    taskContents.clear();
                    taskContents = taskContentMapper.selectTaskContentListByLimit(taskId, taskIndex, batchSize);
                    if (Objects.isNull(taskContents) || taskContents.isEmpty()) {
                        // 如果加载后仍然没有任务，保存当前状态并退出
                        customer.setSurplusNum(surplusNumTemp + surplusNum);//todo
                        needUpdate.add(customer);
                        break loop;
                    }
                    //进度条+10
                    HashMap<Long, Session> userSessionMap = WebSocketService.userSessionMap;
                    Session session = userSessionMap.get(userId);
                    JSONObject jsonObject = new JSONObject();
                    JSONObject data = new JSONObject();
                    jsonObject.put("flag", 2);
                    data.put("pace", pace);
                    data.put("taskId", taskId);
                    jsonObject.put("data", data);
                    WebSocketService.SendMessage(session, jsonObject.toString());
                    taskContentNum += taskContents.size();
                    pace += 10;
                }
                TaskContent currentTask = taskContents.get(taskIndex % batchSize);
                currentTask.setAssignedTo(customer.getUserId());
                currentTask.setTaskStatus(2);
                taskContentsUpdate.add(currentTask);
                surplusNumTemp++;
                taskIndex++;
            }
            customer.setSurplusNum(surplusNumTemp + surplusNum);
            needUpdate.add(customer);
        }
        // 更新数据库
        if (!needUpdate.isEmpty()) {
            customerServiceDetailMapper.updateCustomerServiceDetail2SurplusNumBatch(needUpdate);
        } else {
            return "分配失败，客服任务已经满载";
        }
        if (!taskContentsUpdate.isEmpty()) {
            taskContentMapper.updateTaskContent2assignedToBatch(taskContentsUpdate);
        }
        // 修改任务状态
        Task task = new Task();
        task.setTaskId(taskId);
        task.setTaskStatus(1L);
        task.setStartTime(new Date());
        taskMapper.updateTask(task);


        HashMap<Long, Session> userSessionMap = WebSocketService.userSessionMap;
        Session session = userSessionMap.get(userId);
        JSONObject jsonObject = new JSONObject();
        JSONObject data = new JSONObject();
        jsonObject.put("flag", 2);
        data.put("pace", 100);
        data.put("taskId", taskId);
        jsonObject.put("data", data);
        WebSocketService.SendMessage(session, jsonObject.toString());
        return "任务分配成功";
        //todo
//        taskService.assAsync();
//        List<TaskContent> taskContents = taskContentMapper.selectTaskContentList(taskContent);
//        ArrayList<TaskContent> taskContentsUpdate = new ArrayList<>();
//        ArrayList<CustomerServiceDetail> needUpdate = new ArrayList<>();
//        if (taskContents != null) {
//            int taskSize = taskContents.size();
//            int taskIndex = 0;
//            loop:
//            for (CustomerServiceDetail customer : CustomerServiceDetailList) {
//                int surplusNum = (int) (long) customer.getSurplusNum();
//                //根据每个人的最大承载量为每个人分配任务
//                Long maxLoad = customer.getMaxLoad();
//                if (surplusNum >= maxLoad) {
//                    continue;
//                }
//                //计算剩余分配数量
//                long excess = maxLoad - surplusNum;
//                int surplusNumTemp = 0;
////                int forNum = Integer.parseInt(maxLoad.toString());
//                for (int i = 0; i < excess; i++) {
//                    if (taskIndex >= taskSize) {
//                        customer.setSurplusNum(surplusNumTemp + customer.getSurplusNum());
//                        needUpdate.add(customer);
//                        break loop;
//                    }
//                    TaskContent currentTask = taskContents.get(taskIndex);
//                    currentTask.setAssignedTo(customer.getUserId());
//                    currentTask.setTaskStatus(2);
//                    taskContentsUpdate.add(currentTask);
//                    surplusNumTemp++;
//                    taskIndex++;
//                }
//                customer.setSurplusNum(surplusNumTemp + customer.getSurplusNum());
//                needUpdate.add(customer);
//            }
//            if (needUpdate.isEmpty() && taskContentsUpdate.isEmpty()) {
//                return "分配失败，客服任务已经满载";
//            } else {
//                customerServiceDetailMapper.updateCustomerServiceDetail2SurplusNumBatch(needUpdate);
//                taskContentMapper.updateTaskContent2assignedToBatch(taskContentsUpdate);
//            }
//            //修改任务状态
//            Task task = new Task();
//            task.setTaskId(taskId);
//            task.setTaskStatus(1L);
//            task.setStartTime(new Date());
//            taskMapper.updateTask(task);
//        } else {
//            return "分配失败，任务为空";
//        }
//        return "分配成功";
    }


    @Async
    public void assAsync() {
        System.out.println("run is running in thread: " + Thread.currentThread().getName());
    }

    @Override
    public Object taskStatusByTaskId() {
        Long userId = SecurityUtils.getUserId();
        long sendNum = 0L;
        long maxLoad = 0L;
        long surplusNum = 0L;
        long replyNum = 0L;
        long successNum = 0L;
        CustomerServiceDetail customerServiceDetail = customerServiceDetailMapper.selectCustomerDetailByUserId(userId);
        if (ObjectUtils.isEmpty(customerServiceDetail)) {
            throw new ServiceException("禁止访问");
        }
        if (!customerServiceDetail.getManagerId().equals(-1L)) {
            //不是组长
            Long sendNum1 = customerServiceDetail.getSendNum();
            Long maxLoad1 = customerServiceDetail.getMaxLoad();
            Long surplusNum1 = customerServiceDetail.getSurplusNum();
            Long replyNum1 = customerServiceDetail.getReplyNum();
            //查询成功的session
            HashSet<Long> userIdSet = new HashSet<>();
            userIdSet.add(customerServiceDetail.getUserId());
            HashMap<Long, Long> successMap = customerServiceDetailService.getSuccessMap(userIdSet);
//            Long successNum1 = customerServiceDetail.getSuccessNum();
            Long successNum1 = successMap.getOrDefault(customerServiceDetail.getUserId(), 0L);
            sendNum += sendNum1;
            maxLoad += maxLoad1;
            surplusNum += surplusNum1;
            replyNum += replyNum1;
            successNum += successNum1;
            Task task = new Task();
            task.setGroupId(customerServiceDetail.getManagerId());
            List<Task> tasks = taskMapper.selectTaskList(task);
            int taskAllSize = 0;
            for (Task task1 : tasks) {
                taskAllSize += task1.getTaskNum();
            }
            CustomerTaskStatusVo customerTaskStatusVo = new CustomerTaskStatusVo();
            customerTaskStatusVo.setMaxLoad(maxLoad);
            customerTaskStatusVo.setSurplusNum(surplusNum);
            customerTaskStatusVo.setReplyNum(replyNum);
            customerTaskStatusVo.setSuccessNum(successNum);
            customerTaskStatusVo.setCustomerNum(1);
            long l1 = maxLoad - surplusNum;
            customerTaskStatusVo.setExcessNum(l1);

            if (successNum == 0L) {
                customerTaskStatusVo.setReplyRate(0d);
            } else {
                double replyRate = (double) replyNum / successNum * 100;
                // 格式化回复率，保留两位小数
                double v = Double.parseDouble(String.format("%.2f", replyRate));
                customerTaskStatusVo.setReplyRate(v);
            }
            return customerTaskStatusVo;
        } else {
            HashSet<Long> userIdSet = new HashSet<>();
//            int count = getTaskContentbyGroupId(userId);
            List<CustomerServiceDetail> customerServiceDetails = customerServiceDetailMapper.selectCustomerDetailByManagerId(userId);
            for (CustomerServiceDetail customerServiceDetail1 : customerServiceDetails) {
                Long sendNum1 = customerServiceDetail1.getSendNum();
                Long maxLoad1 = customerServiceDetail1.getMaxLoad();
                Long surplusNum1 = customerServiceDetail1.getSurplusNum();
                Long replyNum1 = customerServiceDetail1.getReplyNum();
                Long successNum1 = customerServiceDetail1.getSuccessNum();
                sendNum += sendNum1;
                maxLoad += maxLoad1;
                surplusNum += surplusNum1;
                replyNum += replyNum1;
//                successNum += successNum1;
                userIdSet.add(customerServiceDetail1.getUserId());
            }
            HashMap<Long, Long> successMap = customerServiceDetailService.getSuccessMap(userIdSet);
            Set<Map.Entry<Long, Long>> entries = successMap.entrySet();
            for (Map.Entry<Long, Long> entry : entries) {
                if (entry.getValue().equals(0L)) {
                    continue;
                }
                successNum += entry.getValue();
            }
            CustomerTaskStatusVo customerTaskStatusVo = new CustomerTaskStatusVo();
            customerTaskStatusVo.setSendNum(sendNum);
            customerTaskStatusVo.setMaxLoad(maxLoad);
            customerTaskStatusVo.setSurplusNum(surplusNum);
            customerTaskStatusVo.setReplyNum(replyNum);
            customerTaskStatusVo.setSuccessNum(successNum);
            customerTaskStatusVo.setCustomerNum(customerServiceDetails.size());
            customerTaskStatusVo.setExcessNum(maxLoad - surplusNum);

            if (successNum == 0L) {
                customerTaskStatusVo.setReplyRate(0d);
            } else {
                double replyRate = (double) replyNum / successNum * 100;
                // 格式化回复率，保留两位小数
                double v = Double.parseDouble(String.format("%.2f", replyRate));
                customerTaskStatusVo.setReplyRate(v);
            }
            return customerTaskStatusVo;
        }
    }

    //根据组长id查询组内已经上传的任务
    private int getTaskContentbyGroupId(Long userId) {
        Task task = new Task();
        task.setGroupId(userId);
        List<Task> tasks = taskMapper.selectTaskList(task);
        HashSet<String> strings = new HashSet<>();
        for (Task task1 : tasks) {
            strings.add(task1.getTaskId());
        }
        if (strings.isEmpty()) {
            return 0;
        }
        List<CustomerServiceDetail> customerServiceDetails = customerServiceDetailMapper.selectCustomerDetailByManagerId(userId);
        HashSet<Long> userIdSet = new HashSet<>();
        for (CustomerServiceDetail customerServiceDetail : customerServiceDetails) {
            userIdSet.add(customerServiceDetail.getUserId());
        }
        return taskContentMapper.countTaskContentListByTaskIdSetAndUserIdSet(strings, userIdSet);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void suspendTask(String taskId) {
        Long userId = SecurityUtils.getUserId();
        CustomerServiceDetail customerServiceDetail = customerServiceDetailService.selectCustomerDetailByUserId(userId);
        if (customerServiceDetail.getManagerId().equals(-1L)) {
            //组长暂停
            Task task = new Task();
            task.setTaskId(taskId);
            task.setTaskStatus(0L);
            int i = taskMapper.updateTask(task);
            taskContentMapper.updateTaskContent4pausedTask(taskId);
            if (i <= 0) {
                throw new ServiceException("未知错误，请联系管理员");
            }
        } else {
            //个人暂停
            TaskContent taskContentAll = new TaskContent();
            taskContentAll.setTaskId(taskId);
            int allNum = taskContentMapper.selectCount(taskContentAll);

            TaskContent taskContentOne = new TaskContent();
            taskContentOne.setTaskId(taskId);
            taskContentOne.setAssignedTo(customerServiceDetail.getUserId());
            int OneNum = taskContentMapper.selectCount(taskContentOne);
            if (OneNum >= allNum) {
                //改变任务状态
                Task task = new Task();
                task.setTaskId(taskId);
                task.setTaskStatus(0L);
                int i = taskMapper.updateTask(task);
            }
            taskContentMapper.updateTaskContent4pausedOnesTask(taskId, customerServiceDetail.getUserId());
        }
    }

    @Override
    @Transactional
    public void StartTask(String taskId) {
        CustomerServiceDetail customerServiceDetail = customerServiceDetailMapper.selectCustomerDetailByUserId(SecurityUtils.getUserId());
        if (customerServiceDetail.getManagerId().equals(-1L)) {
            //是组长 开始全组
            Task task = new Task();
            task.setTaskId(taskId);
            task.setTaskStatus(1L);
            int i = taskMapper.updateTask(task);
            taskContentMapper.updateTaskContent4StartTask(taskId);
//            TaskContent taskContent = new TaskContent();
//            taskContent.setTaskId(taskId);
//            List<TaskContent> taskContents = taskContentMapper.selectTaskContentList(taskContent);
//            for (TaskContent content : taskContents) {
//                content.setTaskStatus(2);
//            }
//            taskContentMapper.updateTaskContentStartToBatch(taskContents);
            if (i <= 0) {
                throw new ServiceException("未知错误，请联系管理员");
            }
        } else {
            //开始个人
//            Task task = new Task();
//            task.setTaskId(taskId);
//            task.setTaskStatus(1L);
//            int i = taskMapper.updateTask(task);
            TaskContent taskContentAll = new TaskContent();
            taskContentAll.setTaskId(taskId);
            int allNum = taskContentMapper.selectCount(taskContentAll);

            TaskContent taskContentOne = new TaskContent();
            taskContentOne.setTaskId(taskId);
            taskContentOne.setAssignedTo(customerServiceDetail.getUserId());
            int OneNum = taskContentMapper.selectCount(taskContentOne);
            if (OneNum >= allNum) {
                //改变任务状态
                Task task = new Task();
                task.setTaskId(taskId);
                task.setTaskStatus(1L);
                int i = taskMapper.updateTask(task);
            }
            taskContentMapper.updateTaskContent4StartOnesTask(taskId, customerServiceDetail.getUserId());
//            TaskContent taskContent = new TaskContent();
//            taskContent.setAssignedTo(customerServiceDetail.getUserId());
//            List<TaskContent> taskContents = taskContentMapper.selectTaskContentList(taskContent);
//            for (TaskContent content : taskContents) {
//                content.setTaskStatus(2);
//            }
//            taskContentMapper.updateTaskContentStartToBatch(taskContents);
        }
    }

    @Override
    public List<Both1ExcelTemplate> download1Template(Integer both) {
        return Collections.emptyList();
    }

    @Override
    public List<Both2ExcelTemplate> download2Template(Integer both) {
        return Collections.emptyList();
    }

    /**
     * 导入手机号
     *
     * @param phoneStrList 手机号集合
     * @return
     */
    public boolean readExcel4SMS(ArrayList<String> phoneStrList, String operName, String projectId) {
        ArrayList<Long> phones = new ArrayList<>();
        try {
            for (String s : phoneStrList) {
                if (StringUtils.isEmpty(s)) {
                    continue;
                }
                System.out.println(s);
                phones.add(Long.parseLong(s));
            }
//            for (int i = 0; i < phoneStrList.size(); i++) {
//                System.out.println(phoneStrList.get(i));
//                phones.add(Long.parseLong(phoneStrList.get(i)));
//                i++;
//            }
        } catch (NumberFormatException e) {
            log.error("账号只能填入手机号", e);
            throw new ServiceException("账号只能填入手机号");
        } catch (NullPointerException e) {
            log.error("请将手机号填入第一列(A列)", e);
            throw new ServiceException("请将手机号填入第一列(A列)");
        }
        ArrayList<CustomerAccount> customerAccounts = new ArrayList<>();
        for (Long phone : phones) {
            customerAccounts.add(new CustomerAccount(1L, phone.toString(), projectId, operName));
        }
        customerAccountMapper.insertCustomerAccountBatch(customerAccounts);
        return true;
    }

    //读取号码+话术
    public static List<PhoneContentInfo> readExcel(MultipartFile file) throws IOException {
        List<PhoneContentInfo> messageInfoList = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = getWorkbook(file, inputStream);
//            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0); // 假设读取第一个工作表

            for (Row row : sheet) {
                if (row.getLastCellNum() == 2) { // 确保每行至少有两列
                    String phone = getCellValueAsString(row.getCell(0));
                    String content = getCellValueAsString(row.getCell(1));
                    if (StringUtils.isNotEmpty(content) && StringUtils.isNotEmpty(phone)) {
                        PhoneContentInfo messageInfo = new PhoneContentInfo(phone, content);
                        messageInfoList.add(messageInfo);
                    }
                } else if (row.getLastCellNum() == 3) {
                    String phone = getCellValueAsString(row.getCell(0));
                    String content = getCellValueAsString(row.getCell(1));
                    String title = getCellValueAsString(row.getCell(2));
                    PhoneContentInfo messageInfo = new PhoneContentInfo(phone, content, title);
                    messageInfoList.add(messageInfo);
                }
            }
            workbook.close();
        }
        return messageInfoList;
    }


    private static Workbook getWorkbook(MultipartFile file, InputStream inputStream) throws IOException {
        String contentType = file.getContentType();
        if ("application/vnd.ms-excel".equals(contentType)) {
            return new HSSFWorkbook(inputStream);
        } else if ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType)) {
            return new XSSFWorkbook(inputStream);
        } else {
            throw new IllegalArgumentException("不支持的文件格式，请使用 .xls 或 .xlsx 文件");
        }
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                // 以字符串形式获取数值
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
}
