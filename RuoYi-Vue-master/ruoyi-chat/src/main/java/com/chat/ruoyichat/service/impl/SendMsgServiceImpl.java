package com.chat.ruoyichat.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chat.ruoyichat.domain.*;
import com.chat.ruoyichat.domain.dto.*;
import com.chat.ruoyichat.domain.sendDto.SendMsgObj;
import com.chat.ruoyichat.domain.sendDto.UserSimpleInfo;
import com.chat.ruoyichat.mapper.*;
import com.chat.ruoyichat.service.ISendMsgService;
import com.chat.ruoyichat.websocket.WebSocketService;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.system.mapper.SysUserMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.websocket.Session;
import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author chat
 * @since 2025/3/15
 **/
@Slf4j
@Service
public class SendMsgServiceImpl implements ISendMsgService {

    private static final Object Lock = new Object();
    @Autowired
    private CustomerServiceDetailMapper customerServiceDetailMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private TaskContentMapper taskContentMapper;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private TaskSendNumMapper taskSendNumMapper;
    @Autowired
    private AccountsMapper accountsMapper;
    @Autowired
    private SessionRecordMapper sessionRecordMapper;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private ChatRecordMapper chatRecordMapper;
    @Autowired
    private ProjectSetMapper projectSetMapper;
    private final String noAssInRedis = CacheConstants.No_Ass_IN_REDIS;
    private final String replyNumKey = CacheConstants.REPLY_NUM;
    private final String successNumKey = CacheConstants.SUCCESS_Num;
    private final String sendNumKey = CacheConstants.SEND_NUM;
    private final String binCsd = CacheConstants.binCsd;
    private final String isReplyKey = CacheConstants.IS_REPLY_KEY;

    /**
     * 获取用户信息
     *
     * @param pcNum
     * @return
     */
    @Override
    public List getCustomerInfo(String pcNum) {
        Object csdObj = redisCache.getCacheObject(binCsd);
        if (Objects.nonNull(csdObj)) {
            JSONArray objects = JSON.parseArray(csdObj.toString());
            objects.forEach(obj -> {
                JSONObject obj1 = (JSONObject) obj;
                obj1.remove("@type");
            });
            String strObj = objects.toString();
            return JSON.parseObject(strObj, List.class);
        }

        List<CustomerServiceDetail> customerServiceDetails = null;
        ArrayList<UserSimpleInfo> userSimpleInfoArrayList = new ArrayList<UserSimpleInfo>();
        if (pcNum.equals("A1")) {
            customerServiceDetails = customerServiceDetailMapper.selectCustomerServiceDetailList4getCustomerInfo();
        } else {
            SysUser sysUser = sysUserMapper.selectUserByUserName(pcNum);
            if (ObjectUtils.isEmpty(sysUser)) {
                throw new ServiceException("没有该小组");
            }
            String status = sysUser.getStatus();
            if (status.equals("1")) {
                throw new ServiceException("没有该小组");
            }
            Long userId = sysUser.getUserId();
            customerServiceDetails = customerServiceDetailMapper.selectCustomerDetailByManagerId(userId);
        }
        //不给组长
        //UserSimpleInfo leaderInfo = new UserSimpleInfo(userId,sysUser.getUserName(),true);
        //userSimpleInfoArrayList.add(leaderInfo);
        for (CustomerServiceDetail customerServiceDetail : customerServiceDetails) {
            Long userId2 = customerServiceDetail.getUserId();
            String userName = customerServiceDetail.getUserName();
            UserSimpleInfo userSimpleInfo = new UserSimpleInfo(userId2, userName, false);
            Long managerId = customerServiceDetail.getManagerId();
            if (managerId.equals(-1L)) {
                userSimpleInfo.setAdmin(true);
            }
            userSimpleInfoArrayList.add(userSimpleInfo);
        }

//        Collections.sort(list, Comparator.comparingLong(CustomerServiceDetail::getTimestamp).reversed());
        List<UserSimpleInfo> collect = userSimpleInfoArrayList.stream()
                .sorted(Comparator.comparingLong(UserSimpleInfo::getUserId))
                .collect(Collectors.toList());
        //六十秒更新redis
        redisCache.setCacheObject(binCsd, collect, 60, TimeUnit.SECONDS);
        return collect;
    }


    /**
     * 发送消息逻辑
     *
     * @param taskProjectId
     * @param userId
     * @return
     */
    @Override
//    @Transactional(rollbackFor = Exception.class)
    public SendMsgObj getSendMsg(Long taskProjectId, Long userId) {
        //tn
        //应该先检测是否在聊天redis中
        String key = CacheConstants.MESSAGE_KEY;

        Boolean b = redisCache.hasKey(key + userId);
        if (b) {
            Object o = redisCache.rightPop(key + userId);
            JSONObject jsonObject1 = JSON.parseObject(o.toString());
            String strObj = jsonObject1.toString();
            SendMsgObj sendMsgObj = JSON.parseObject(strObj, SendMsgObj.class);
            CustomerServiceDetail customerServiceDetail = customerServiceDetailMapper.selectCustomerDetailByUserId(userId);
            ProjectSet projectSet = projectSetMapper.selectProjectSetByProjectId(customerServiceDetail.getProjectId());
            if (projectSet.getIsSendNum().equals(1L)) {
                //输入框是否算发送条数
                redisCache.leftPush(sendNumKey, customerServiceDetail);
            }
            return sendMsgObj;
        }
        SysUser sysUser = sysUserMapper.selectUserById(userId);
        if (ObjectUtils.isEmpty(sysUser)) {
            throw new ServiceException("暂无该用户");
        }
        if (sysUser.getStatus().equals("1")) {
            throw new ServiceException("员工已经离职");
        }
        TaskContent taskContent = new TaskContent();
        taskContent.setAssignedTo(userId);
        taskContent.setTaskStatus(2);
        taskContent.setIsUse(0L);
        //获取出员工所有任务
//        startPage();
        List<TaskContent> taskContents = taskContentMapper.selectTaskContentListPages(taskContent);
//        List<TaskContent> taskContents = taskContentMapper.selectTaskContentList(taskContent);
        if (taskContents.isEmpty()) {
            throw new ServiceException("员工没有进行中的任务");
        }
        String taskKey = CacheConstants.TASK_NUM;
        String accountNum = CacheConstants.ACCOUNT_NUM;
        CustomerServiceDetail customerServiceDetail = customerServiceDetailMapper.selectCustomerDetailByUserId(userId);
//        CustomerServiceDetail leader = customerServiceDetailMapper.selectCustomerDetailByUserId(customerServiceDetail.getManagerId());
        ProjectSet projectSet = projectSetMapper.selectProjectSetByProjectId(customerServiceDetail.getProjectId());
//        Integer maxLoad = leader.getIsEnough();//TODO 可以优化 存入redis
        int maxLoad = (int) (long) projectSet.getAccountSendNum();

        //查出员工手里所有正常的账号
        Accounts accounts = new Accounts();
        accounts.setAssignedTo(userId);
        accounts.setAccStatus(0L);
        List<Accounts> accounts1 = accountsMapper.selectAccountsListNoBan(accounts);
        if (accounts1.isEmpty()) {
            throw new ServiceException("员工账号不足");
        }
        //账号字典;
        HashMap<Long, Accounts> accountMap = new HashMap<>();
        ArrayList<Accounts> accounts3 = new ArrayList<>();
        for (Accounts accounts2 : accounts1) {
            //分拣出没有cd的
            if (!redisCache.hasKey(accounts2.getAccount())) {
                accountMap.put(accounts2.getAccountId(), accounts2);
                accounts3.add(accounts2);
            }
        }
        if (accountMap.isEmpty()) {
            throw new ServiceException("所有账号发送冷却中");
        }
        //所有的账号
        HashSet<String> taskIds = new HashSet<>();
        for (Accounts content : accounts3) {
            taskIds.add(content.getAccount());
        }
        //查账号的发送情况 (没法送过的没有 定时任务没有扫到(说明还在redis中)的没有)
        List<TaskSendNum> taskSendNums = taskSendNumMapper.selectTaskSendNumListByIds(taskIds);
        //这是使用发送的账号
        Accounts useAccount = null;
        if (!taskSendNums.isEmpty()) {
            HashMap<String, Long> sendNumMap = new HashMap<>();
            for (TaskSendNum taskSendNum : taskSendNums) {
                sendNumMap.put(taskSendNum.getTaskContentId(), taskSendNum.getSendNum());
            }

            for (Accounts content : accounts3) {
                String accountId = content.getAccount();
                Long sendNum = sendNumMap.get(accountId);
                if (ObjectUtils.isEmpty(sendNum)) {
                    //可能是第一次发
                    //进来这里说明账号发送数量还没有存过库
                    //进redis查数量
                    Integer expire = redisCache.getCacheObject(accountNum + accountId);
                    if (ObjectUtils.isEmpty(expire)) {
                        //直接发送
                        //第一次发送
                        content.setStartTime(new Date());
                        accountsMapper.updateAccounts(content);
                        useAccount = content;
                        redisCache.setOne(accountNum + accountId, 1);
//                        redisCache.addOne(accountNum + accountId);//？？？
                        break;
                    }
                    if (expire < maxLoad) {
                        //也可以发
                        useAccount = content;
                        redisCache.addOne(accountNum + accountId);
                        break;
                    }
                } else {
                    //进来这里说明数据库中有这个账号的发送数量记录
                    if (sendNum < maxLoad) {
                        //没有到达阈值
                        //拿redis
                        Integer expire = redisCache.getCacheObject(accountNum + accountId);
                        if (ObjectUtils.isEmpty(expire)) {
                            //这里可能有bug 就是redis中没有是正好被定时任务扫走了，不过几率特别小
                            useAccount = content;
                            redisCache.setOne(accountNum + accountId, (int) (sendNum + 1));
                            break;
                        }
                        if (expire < maxLoad) {
                            useAccount = content;
                            redisCache.addOne(accountNum + accountId);
                            break;
                        }
                    } else {
                        //满了的情况
                        Integer expire = redisCache.getCacheObject(accountNum + accountId);
//                        Integer expire = (Integer) redisTemplateObj.opsForValue().get(accountNum + taskContentId);
                        if (!ObjectUtils.isEmpty(expire)) {
                            redisCache.deleteObject(accountNum + accountId);
                        }
//                        if (expire < 0) {
//                            //TODO到这里了就说明这个号有一次发送失败了 是否是封禁了发送失败？如果是就不需要这里了 这里会重试
//                            useAccount = content;
//                            redisCache.addOne(accountNum + accountId);
////                            redisTemplateObj.opsForValue().increment(accountNum + taskContentId);
//                        }//没有就是满了 进行下一个
                    }
                }
            }
        } else {
            useAccount = accounts3.get(0);
            String accountId = useAccount.getAccount();
            redisCache.addOne(accountNum + accountId);
//            redisTemplateObj.opsForValue().increment(accountNum + taskContentId);
        }
        //拿出可使用账号
        Accounts accounts2 = null;
        if (useAccount != null) {
            accounts2 = accountMap.get(useAccount.getAccountId());
        } else {
            throw new ServiceException("用户无可用账号");
        }
        //拿未发送任务
        TaskContent task = null;
        Collections.shuffle(taskContents);//乱序
        for (TaskContent content : taskContents) {
            String taskContentId = content.getTaskContentId();
            Object i = redisCache.getCacheObject(taskKey + taskContentId);
            if (ObjectUtils.isEmpty(i)) {
                redisCache.setOne(taskKey + taskContentId, 1);
                task = content;
                task.setIsUse(1L);
                taskContentMapper.updateTaskContent(task);
                break;
            }
        }
        if (ObjectUtils.isEmpty(task)) {
            throw new ServiceException("任务全部发送完毕", 1001);
        }
//        task.setTaskStatus(1);
//            customerServiceDetail.setSendNum(sendNum + 1);
        //组装消息
        SendMsgObj sendMsgObj = new SendMsgObj();
        sendMsgObj.setId(task.getTaskContentId());


        //通过json拿邮箱
        if (!JSON.isValid(accounts2.getDeviceInfo())) {
            sendMsgObj.setMobileNum(accounts2.getAccount());
            sendMsgObj.setMyphonenumber(accounts2.getAccount());
        } else {
            HashMap<String, Object> map = JSON.parseObject(accounts2.getDeviceInfo(), HashMap.class);
            String email = map.get("email").toString();
            sendMsgObj.setMobileNum(email);
            sendMsgObj.setMyphonenumber(email);
        }

        sendMsgObj.setType("text");
        sendMsgObj.setText(task.getTemplateId());
        sendMsgObj.setOther(task.getRecipientList());
        sendMsgObj.setUserId(userId);
        sendMsgObj.setUserName(sysUser.getUserName());
        sendMsgObj.setCookie(accounts2.getCookie());
        sendMsgObj.setDeviceInfo(accounts2.getDeviceInfo());
        sendMsgObj.setPassword(accounts2.getPassword());
        Random random = new Random();
        Integer gapTime = projectSet.getGapTime();//发送间隔
        Integer gapValue = projectSet.getGapValue();//达到6条等待时间次数
        int randomNumber = random.nextInt((gapTime / 2) + 1) + gapTime;
//        redisCache.setCacheObject(email, 1, 1, TimeUnit.MINUTES);
//        redisCache.setCacheObject(email, 1, randomNumber, TimeUnit.MINUTES);

        //customerServiceDetail就一个逻辑+1发送次数+1
//        redisCache.leftPush(sendNumKey, customerServiceDetail);

        //在这里判断是否到了发送等待间隔次数
        Integer expire = redisCache.getCacheObject(accountNum + accounts2.getAccount());

        if (!JSON.isValid(accounts2.getDeviceInfo())) {

            if (expire % gapValue == 0) {
                redisCache.setCacheObject(accounts2.getAccount(), 1, projectSet.getGapLongTime(), TimeUnit.SECONDS);
            } else {
                redisCache.setCacheObject(accounts2.getAccount(), 1, randomNumber, TimeUnit.SECONDS);
            }
        } else {
            HashMap<String, Object> map = JSON.parseObject(accounts2.getDeviceInfo(), HashMap.class);
            String email = map.get("email").toString();
            if (expire % gapValue == 0) {
                redisCache.setCacheObject(email, 1, projectSet.getGapLongTime(), TimeUnit.SECONDS);
            } else {
                redisCache.setCacheObject(email, 1, randomNumber, TimeUnit.SECONDS);
            }
        }

//CAS customerServiceDetail 的 SendNum
//        int rows = 0;
//        int retryCount = 5; // 重试次数
//        boolean flag = true;
//        while (rows == 0 && retryCount > 0 && flag) {//去批量修改数据库
//            customerServiceDetail = customerServiceDetailMapper.selectCustomerDetailByUserId(userId);
//            Long sendNum = customerServiceDetail.getSendNum();
//            Long surplusNum = customerServiceDetail.getSurplusNum();
//            if (sendNum < surplusNum) {
//                customerServiceDetail.setSendNum(sendNum + 1);
//                rows = customerServiceDetailMapper.updateCustomerServiceDetail(customerServiceDetail);
//            } else {
//                flag = false;
//            }
//            retryCount--;
//        }
//        if (rows == 0 && flag) {
//            Long sendNum = customerServiceDetail.getSendNum();
//            customerServiceDetail.setSendNum(sendNum + 1);
//            customerServiceDetailMapper.updateCustomerServiceDetail(customerServiceDetail);
////            throw new ServiceException("更新客服发送数量失败，请稍后重试");
//        }
        //客服总发+1
        //        sendMsgObj.setProxy();
        //拿出来手里的账号 如果满了再在redis里找是否有-1 有就可以发一条，没有就满了
        //如果不满在redis里拿 如果有记录用记录 如果没有就用且存redis
        //发一条 先 redis +1
        //如果成功不管 如果不成功查redis是否存在 如果不存在加 -1
        //定时任务 如果满了就存库

        //胖哥要发图片的接口
        return sendMsgObj;
    }


    //发送成功逻辑
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void getSendMsgSuccess(String taskContentId, String account) {
        //taskContentId：可能是任务id 可能是聊天记录id
        TaskContent taskContent = taskContentMapper.selectTaskContentByTaskContentId(taskContentId);
        if (Objects.isNull(taskContent)) {
            ChatRecord chatRecord = chatRecordMapper.selectChatRecordByChatId(taskContentId);
            SessionRecord sessionRecord = sessionRecordMapper.selectSessionRecordBySessionId(chatRecord.getSessionId());
            //不是第一次会话 对话中
//            SessionRecord sessionRecord = sessionRecordMapper.selectSessionRecordBySessionId(chatRecord1.getSessionId());
//            SessionRecord sessionRecord1 = sessionRecords.get(0);
//            ChatRecord chatRecord = chatRecordMapper.selectChatRecordEndBySessionId(sessionRecord1.getSessionId());
//            if (!ObjectUtils.isEmpty(chatRecord)) {
//                chatRecord.setSuccess(1L);
//                chatRecordMapper.updateChatRecord(chatRecord);
//            } else {
            String messageContent = chatRecord.getMessageContent();
            Date date = new Date();
            sessionRecord.setLatestChatTime(date);
            sessionRecord.setEndText(messageContent);
            sessionRecord.setMessageType(1);
            sessionRecordMapper.updateSessionRecord(sessionRecord);
//            }
            Long userId = sessionRecord.getCustId();
//            推送给前端websocket
//            封装成前端消息对象
            HashMap<Long, Session> userSessionMap = WebSocketService.userSessionMap;
            Session session = userSessionMap.get(userId);
            JSONObject jsonObject = assembleStatusMsg(sessionRecord, chatRecord, 1L);
            WebSocketService.SendMessage(session, jsonObject.toString());
            return;
        }

        //是否是第一次会话
        SessionRecord sessionRecord = new SessionRecord();
        sessionRecord.setCustId(taskContent.getAssignedTo());
        sessionRecord.setCustContact(taskContent.getRecipientList());
        List<SessionRecord> sessionRecords = sessionRecordMapper.selectSessionRecordList(sessionRecord);
//        ChatRecord chatRecord1 = chatRecordMapper.selectChatRecordByChatId(taskContentId);
        if (sessionRecords.isEmpty()) {
            //第一次会话
            String recipientList = taskContent.getRecipientList();
            String templateId = taskContent.getTemplateId();
            //第一次会话
            //获取关联项目
            String taskId = taskContent.getTaskId();
            Task task = taskMapper.selectTaskByTaskId(taskId);
            String projectId = task.getProjectId();
            //如果是第一次会话
            //创建一个会话
            SessionRecord sessionRecord1 = new SessionRecord();
            //创建一条聊天记录
            sessionRecord1.setSessionId(IdUtils.fastSimpleUUID());
            sessionRecord1.setCustId(taskContent.getAssignedTo());
            sessionRecord1.setCustContact(recipientList);
            Date date = new Date();
            sessionRecord1.setBeginTime(date);
            sessionRecord1.setLatestChatTime(date);
            sessionRecord1.setMessageCount(0L);
            sessionRecord1.setMessageType(0);
            sessionRecord1.setProjectId(projectId);
            sessionRecord1.setEndText(templateId);
            sessionRecord1.setAccount(account);
            //修改回复次数
            //修改成功次数
            Long assignedTo = taskContent.getAssignedTo();

            int i = sessionRecordMapper.insertSessionRecord(sessionRecord1);
            if (i != 0) {
                taskContent.setTaskStatus(1);
                taskContentMapper.updateTaskContent(taskContent);
                CustomerServiceDetail customerServiceDetail = customerServiceDetailMapper.selectCustomerDetailByUserId(assignedTo);
                redisCache.leftPush(successNumKey, customerServiceDetail);
            }
            ChatRecord chatRecord = new ChatRecord();
            chatRecord.setChatInout(-1L);
            chatRecord.setMessageContent(templateId);
            chatRecord.setSendTime(date);
            chatRecord.setSessionId(sessionRecord1.getSessionId());
            chatRecord.setSuccess(1L);
            chatRecordMapper.insertChatRecord(chatRecord);
            //CAS customerServiceDetail 的 SendNum
            //TODO
//            int rows = 0;
//            int retryCount = 10; // 重试次数
//            while (rows == 0 && retryCount > 0) {
//                CustomerServiceDetail customerServiceDetail = customerServiceDetailMapper.selectCustomerDetailByUserId(assignedTo);
//                Long successNum = customerServiceDetail.getSuccessNum();
////            Long sendNum = customerServiceDetail.getSendNum();
//                customerServiceDetail.setSuccessNum(successNum + 1);
//                rows = customerServiceDetailMapper.updateCustomerServiceDetail(customerServiceDetail);
//                retryCount--;
//            }


            //判断是否还有没使用的任务 如果没有 就停止总任务
            TaskContent taskContent1 = new TaskContent();
            taskContent1.setTaskId(taskContent.getTaskId());
            taskContent1.setIsUse(0L);
//            startPage();//todo
            List<TaskContent> taskContents = taskContentMapper.selectTaskContentListPages(taskContent1);
//            List<TaskContent> taskContents = taskContentMapper.selectTaskContentList(taskContent1);
            //就一个逻辑 成功数量+1
            if (taskContents.isEmpty()) {
                //说明任务执行完毕
                Task task1 = new Task();
                task1.setTaskId(taskContent.getTaskId());
                task1.setTaskStatus(2L);
                task1.setEndTime(date);
                taskMapper.updateTask(task1);
                redisCache.deleteObject(successNumKey);
//                polErupt(customerServiceDetail);
            }
        }
    }


    private static JSONObject assembleStatusMsg(SessionRecord sessionRecord, ChatRecord chatRecord1, Long status) {
        JSONObject allObj = new JSONObject();
        Message message = new Message();
        message.setSessionId(sessionRecord.getSessionId());
        message.setTime(new Date());
        message.setSuccess(status);
        message.setRealId(chatRecord1.getChatId());
        message.setText(chatRecord1.getMessageContent());
        allObj.put("type", "result");
        allObj.put("data", message);
        allObj.put("flag", 1);
        return allObj;
    }

    private static JSONObject assembleBackMsg(SessionRecord sessionRecord, ChatRecord chatRecord1) {
        JSONObject allObj = new JSONObject();
        Message message = new Message();
        message.setSessionId(sessionRecord.getSessionId());
        message.setTime(chatRecord1.getSendTime());
        message.setSuccess(1L);
        message.setRealId(chatRecord1.getChatId());
        message.setText(chatRecord1.getMessageContent());
        message.setIsFavorite(sessionRecord.getIsFavorite());
        allObj.put("type", "receive");
        allObj.put("data", message);
        allObj.put("flag", 1);
        return allObj;
    }


    /**
     * 消息发送失败
     *
     * @param taskContentId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void getSendMsgF(String taskContentId, String account) {
        //先查是否是聊天记录表
//        ChatRecord chatRecord1 = chatRecordMapper.selectChatRecordByChatId(taskContentId);
        TaskContent taskContent = taskContentMapper.selectTaskContentByTaskContentId(taskContentId);
        SessionRecord sessionRecord = new SessionRecord();
        sessionRecord.setCustId(taskContent.getAssignedTo());
        sessionRecord.setCustContact(taskContent.getRecipientList());
        List<SessionRecord> sessionRecords = sessionRecordMapper.selectSessionRecordList(sessionRecord);
        if (!sessionRecords.isEmpty()) {
            //有聊天记录
            //进来这里说明taskContentId是聊天记录id 正在聊天
            SessionRecord sessionRecord1 = sessionRecords.get(0);
            Long userId = sessionRecord1.getCustId();
            ChatRecord chatRecord = chatRecordMapper.selectChatRecordEndBySessionId(sessionRecord1.getSessionId());
            //如果是就修改状态
            chatRecord.setSuccess(-1L);
            chatRecordMapper.updateChatRecord(chatRecord);
            HashMap<Long, Session> userSessionMap = WebSocketService.userSessionMap;
            Session session = userSessionMap.get(userId);
            JSONObject jsonObject = assembleStatusMsg(sessionRecord1, chatRecord, -1L);
            WebSocketService.SendMessage(session, jsonObject.toString());
            return;
        }
        //进来这里说明taskContentId是任务id
        //如果不是 说明是第一次发消息
        //只修改状态 0允许下次继续发送这条 1不允许重试
        taskContent.setIsUse(1L);
        taskContent.setTaskStatus(-1);
        taskContentMapper.updateTaskContent(taskContent);
        String accountNum = CacheConstants.ACCOUNT_NUM;

        //判断是否还有没使用的任务 如果没有 就停止总任务
        TaskContent taskContent1 = new TaskContent();
        taskContent1.setTaskId(taskContent.getTaskId());
        taskContent1.setIsUse(0L);
//        startPage();
        List<TaskContent> taskContents = taskContentMapper.selectTaskContentListPages(taskContent1);
//        List<TaskContent> taskContents = taskContentMapper.selectTaskContentList(taskContent1);
        if (taskContents.isEmpty()) {
            Task task1 = new Task();
            task1.setTaskId(taskContent.getTaskId());
            task1.setTaskStatus(2L);
            task1.setEndTime(new Date());
            taskMapper.updateTask(task1);
//            Long assignedTo = taskContent.getAssignedTo();
//            CustomerServiceDetail customerServiceDetail = customerServiceDetailMapper.selectCustomerDetailByUserId(assignedTo);
//            polErupt(customerServiceDetail);
        }
//        CustomerServiceDetail customerServiceDetail = customerServiceDetailMapper.selectCustomerDetailByUserId(taskContent.getAssignedTo());
//        Long sendNum = customerServiceDetail.getSendNum();
//        customerServiceDetail.setSendNum(sendNum + 1);
//        customerServiceDetailMapper.updateCustomerServiceDetail(customerServiceDetail);
        TaskSendNum taskSendNum = taskSendNumMapper.selectTaskSendNumByTaskContentId(account);
        //数据库没有 redis没有不管
        //数据库没有 redis有 如果大于零 -1
        Integer expire = redisCache.getCacheObject(accountNum + account);
        if (ObjectUtils.isEmpty(taskSendNum)) {
            if (!ObjectUtils.isEmpty(expire) && expire > 0) {
                redisCache.subOne(accountNum + account);
            }
        } else {//数据库有
            // redis没有 -1存redis
            // redis有 如果大于零 -1
            if (ObjectUtils.isEmpty(expire)) {
                redisCache.setOne(accountNum + account, (int) (taskSendNum.getSendNum() - 1L));
            } else if (expire > 0) {
                redisCache.subOne(accountNum + account);
            }
        }
    }

    /**
     * 账号封禁
     *
     * @param account
     */
    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void disable(String account,String reason) {
    public void disable(String account) {
        //修改帐号状态
        //删除redis
        String accountNum = CacheConstants.ACCOUNT_NUM;
        redisCache.deleteObject(accountNum + account);
        synchronized (Lock) {
            Accounts accounts = accountsMapper.selectAccountsByAccount(account);
            Long accStatus = accounts.getAccStatus();
            if (accStatus.equals(-1L)) {
                return;
            }
            //修改个人的状态
            accounts.setAccStatus(-1L);
            accounts.setBanTime(new Date());
            int i = accountsMapper.updateAccounts(accounts);
            if (i == 0) {
                return;
            }
            Long assignedTo = accounts.getAssignedTo();
            if (!ObjectUtils.isEmpty(assignedTo)) {
//              synchronized (Lock) {
                customerServiceDetailMapper.updateCustomerServiceDetailAddBanNum(assignedTo);
//              CustomerServiceDetail customerServiceDetail = customerServiceDetailMapper.selectCustomerDetailByUserId(assignedTo);
//              Long banNum = customerServiceDetail.getBanNum();
//              customerServiceDetail.setBanNum(banNum + 1);
//              customerServiceDetail.setSurplusNum(null);
//              customerServiceDetailMapper.updateCustomerServiceDetail(customerServiceDetail);
            }
        }
    }

    /**
     * 查看哪个账号有回复
     *
     * @param count
     * @return
     */
    @Override
    public List<PhoneInfo> getPhoneInfos(int count) {
        ArrayList<PhoneInfo> phoneInfos = new ArrayList<>();
        Boolean b = redisCache.hasKey(isReplyKey);
        if (b) {
            List<Object> objects = redisCache.dequeue(isReplyKey, count);
            for (Object object : objects) {
                JSONObject jsonObject = JSON.parseObject(object.toString());
                jsonObject.remove("@type");
                phoneInfos.add(JSON.toJavaObject(jsonObject, PhoneInfo.class));
            }
        }
        return phoneInfos;
    }

    /**
     * 回复消息
     *
     * @param backMsg
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer backMsgs(BackMsg backMsg) throws ParseException {
        Long userId = backMsg.getUserId();
        ArrayList<MsgBoj> data = backMsg.getData();
        int repeatNum = 0;
        data.sort(Comparator.comparing(MsgBoj::getDate));
        // 定义解析输入字符串的格式
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // 明确设置输入时间为UTC时区

        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        outputFormat.setTimeZone(TimeZone.getTimeZone("GMT+8")); // 设置为东八区
        for (MsgBoj datum : data) {
            String contactValue = datum.getContact_value();
            if (!isNumericByRegex(contactValue)) {
                //过滤掉不是纯数字的手机号的消息 这里应该解决了没有发消息收到了系统消息的问题
                continue;
            }
            /** 如果需要转时区 就用这里
             Instant instant = Instant.parse(datum.getDate());  // 直接解析 UTC 时间
             Date localDate = Date.from(instant);
             **/
            //OO时间直接落库 不做处理
            Date dateZ = inputFormat.parse(datum.getDate());
            String beijingTime = outputFormat.format(dateZ);
            Date date = outputFormat.parse(beijingTime);
            Long id = datum.getId();
            ChatRecord chatRecord1 = new ChatRecord();
            if (Objects.isNull(id)) {
                chatRecord1.setSendTime(date);
                chatRecord1.setMessageContent(datum.getMessage());
                chatRecord1.setChatInout(1L);
            } else {
                chatRecord1.setId(id.toString());
            }

            //先去重
            List<ChatRecord> chatRecords2 = chatRecordMapper.selectChatRecordList(chatRecord1);
            if (!chatRecords2.isEmpty()) {
                repeatNum += chatRecords2.size();
                continue;
            }
//            accounts.add(account);

            SessionRecord sessionRecord = new SessionRecord();
            sessionRecord.setCustContact(datum.getContact_value());
            sessionRecord.setCustId(userId);//一个手机 一个用户id  确定一个对话
            List<SessionRecord> sessionRecordOne = sessionRecordMapper.selectSessionRecordList(sessionRecord);
            if (ObjectUtils.isEmpty(sessionRecordOne)) {
                //这里因为可能没有调用发送成功或者没有状态，但是确实发出去了，他回信了就直接给他重新走一遍发送成功
                TaskContent taskContent = taskContentMapper.selectTaskContentByCust(datum.getContact_value());
                if (Objects.nonNull(taskContent) && taskContent.getIsUse().equals(1L)) {
                    //如果有这个任务并且被使用 需要重新走发送成功逻辑
                    getSendMsgSuccess(taskContent.getTaskContentId(), datum.getUsername());
                } else {
                    log.info("应该是删除的任务，没有被记录成功，结果发成功了也回了消息");
                    continue;
                }
            }
            List<SessionRecord> reSessionRecordOne = sessionRecordMapper.selectSessionRecordList(sessionRecord);
            if (!ObjectUtils.isEmpty(reSessionRecordOne)) {
                //添加聊天记录
                for (SessionRecord sessionRecord1 : reSessionRecordOne) {
//                    SessionRecord sessionRecord1 = reSessionRecordOne.get(0);

                    //修改会话状态
                    sessionRecord1.setLatestChatTime(date);
                    sessionRecord1.setMessageCount(sessionRecord1.getMessageCount() + 1);
                    sessionRecord1.setMessageType(1);
                    sessionRecord1.setEndText(datum.getMessage());
                    sessionRecordMapper.updateSessionRecord(sessionRecord1);

                    //根据sessionId找chatrecord 如果有chat_inout 有1就回复率+1
                    ChatRecord chatRecord2 = new ChatRecord();
                    chatRecord2.setSessionId(sessionRecord1.getSessionId());
                    chatRecord2.setChatInout(1L);
                    List<ChatRecord> chatRecords = chatRecordMapper.selectChatRecordList(chatRecord2);
                    if (chatRecords.isEmpty()) {
                        CustomerServiceDetail customerServiceDetail = customerServiceDetailMapper.selectCustomerDetailByUserId(userId);
                        //就一个逻辑 回复数量+1
                        redisCache.leftPush(replyNumKey, customerServiceDetail);
                    }

                    ChatRecord chatRecord = new ChatRecord();
                    if (Objects.nonNull(id)) {
                        chatRecord.setId(id.toString());
                    }
                    chatRecord.setChatInout(1L);
                    chatRecord.setMessageContent(datum.getMessage());
                    chatRecord.setSendTime(date);
                    chatRecord.setSessionId(sessionRecord1.getSessionId());
                    chatRecord.setSuccess(1L);
                    try {
                        chatRecordMapper.insertChatRecord(chatRecord);
                    } catch (Exception e) {
                        log.info("是否消息-时间唯一索引错误了");
                        log.info(e.getMessage());
                    }

                    //推redis
                    //推送websocket
                    //组装消息
                    JSONObject jsonObject = assembleBackMsg(sessionRecord1, chatRecord);
                    HashMap<Long, Session> userSessionMap = WebSocketService.userSessionMap;
                    Session session = userSessionMap.get(userId);
                    WebSocketService.SendMessage(session, jsonObject.toString());
                }
//                    int rows = 0;
//                    int retryCount = 5; // 重试次数
//                    //就一个逻辑 回复数量+1
//                    while (rows == 0 && retryCount > 0) {
//                        CustomerServiceDetail customerServiceDetail = customerServiceDetailMapper.selectCustomerDetailByUserId(userId);
//                        customerServiceDetail.setReplyNum(customerServiceDetail.getReplyNum() + 1);
//                        rows = customerServiceDetailMapper.updateCustomerServiceDetail(customerServiceDetail);
//                        if (rows != 0) {
//                            ChatRecord chatRecord = new ChatRecord();
////                chatRecord.setChatId(datum.getId().toString());
//                            chatRecord.setChatInout(1L);
//                            chatRecord.setMessageContent(datum.getMessage());
//
//                            chatRecord.setSendTime(date);
//                            chatRecord.setSessionId(sessionRecord1.getSessionId());
//                            chatRecord.setSuccess(1L);
//                            chatRecord.setId(id);
//                            chatRecordMapper.insertChatRecord(chatRecord);
//                            //修改会话状态
//                            sessionRecord1.setLatestChatTime(date);
//                            sessionRecord1.setMessageCount(sessionRecord1.getMessageCount() + 1);
//                            sessionRecord1.setMessageType(1);
//                            sessionRecord1.setEndText(datum.getMessage());
//                            sessionRecordMapper.updateSessionRecord(sessionRecord1);
//
//                            //推redis
//                            //推送websocket
//                            //组装消息
//                            JSONObject jsonObject = assembleBackMsg(sessionRecord1, chatRecord);
//                            HashMap<Long, Session> userSessionMap = WebSocketService.userSessionMap;
//                            Session session = userSessionMap.get(userId);
//                            WebSocketService.SendMessage(session, jsonObject.toString());
//                        }
//                        retryCount--;
//                    }
//                ChatRecord chatRecord = new ChatRecord();
////                chatRecord.setChatId(datum.getId().toString());
//                chatRecord.setChatInout(1L);
//                chatRecord.setMessageContent(datum.getMessage());
//
//                chatRecord.setSendTime(date);
//                chatRecord.setSessionId(sessionRecord1.getSessionId());
//                chatRecord.setSuccess(1L);
//                chatRecord.setId(id);
//                chatRecordMapper.insertChatRecord(chatRecord);
//                //修改会话状态
//                sessionRecord1.setLatestChatTime(date);
//                sessionRecord1.setMessageCount(sessionRecord1.getMessageCount() + 1);
//                sessionRecord1.setMessageType(1);
//                sessionRecord1.setEndText(datum.getMessage());
//                sessionRecordMapper.updateSessionRecord(sessionRecord1);
//                //推送websocket
//                //组装消息
//                JSONObject jsonObject = assembleBackMsg(sessionRecord1, chatRecord);
//                HashMap<Long, Session> userSessionMap = WebSocketService.userSessionMap;
//                Session session = userSessionMap.get(userId);
//                WebSocketService.SendMessage(session, jsonObject.toString());
            }
        }
        return repeatNum;
    }

    @Override
    public List<PhoneInfo> checkBan(Integer count) {
        List<Object> objects = redisCache.dequeue(noAssInRedis, count);
        ArrayList<PhoneInfo> phoneInfos = new ArrayList<>();
        for (Object object : objects) {
            JSONObject jsonObject = JSON.parseObject(object.toString());
            jsonObject.remove("@type");
            phoneInfos.add(JSON.toJavaObject(jsonObject, PhoneInfo.class));
        }
        return phoneInfos;
    }

    @Override
    public void deleteSys() {
        /**
         * 清空系统数据
         * task_send_Num
         * task_content
         * task
         * sys_user
         * sys_user_role
         * session_record
         * send content template content
         * send content template
         * preset reply
         * customer service detail
         * customer account
         * chat record
         * accounts
         */
        customerServiceDetailMapper.deleteSysAll();
    }

    @Override
    public void setToken(String userName, String token) {
        String tokenKey = CacheConstants.Token;
        redisCache.setCacheObject(tokenKey + userName, token, 48, TimeUnit.HOURS);
    }

    @Override
    public String getToken(String userName) {
        String tokenKey = CacheConstants.Token;
        Object cacheObject = redisCache.getCacheObject(tokenKey + userName);
        if (Objects.isNull(cacheObject)) {
            return null;
        } else {
            return cacheObject.toString();
        }
    }

    @Override
    public void reBack(CallBackObj jsonList) {
        jsonList.getMsgMap().sort(Comparator.comparingLong(CallBackItem::getTimestamp));
        String to = jsonList.getTo();
        String from = jsonList.getFrom();
        for (CallBackItem jsonResult : jsonList.getMsgMap()) {
            String body = jsonResult.getBody();//消息
            String id = jsonResult.getId();
            Date date = new Date(jsonResult.getTimestamp());
            Long userId = 0L;

            ChatRecord chatRecord1 = new ChatRecord();
            chatRecord1.setId(id);
            //先去重
            List<ChatRecord> chatRecords2 = chatRecordMapper.selectChatRecordList(chatRecord1);
            if (!chatRecords2.isEmpty()) {
                continue;
            }

            SessionRecord sessionRecord = new SessionRecord();
            sessionRecord.setCustContact(from);
//        sessionRecord.setCustId(userId);//一个手机 一个用户id  确定一个对话
            sessionRecord.setAccount(to);
            List<SessionRecord> sessionRecordOne = sessionRecordMapper.selectSessionRecordList(sessionRecord);
            if (!ObjectUtils.isEmpty(sessionRecordOne)) {
                //添加聊天记录
                for (SessionRecord record : sessionRecordOne) {
                    //修改会话状态
                    userId = record.getCustId();
                    record.setLatestChatTime(date);
                    record.setMessageCount(record.getMessageCount() + 1);
                    record.setMessageType(1);
                    record.setEndText(body);
                    sessionRecordMapper.updateSessionRecord(record);
                    //根据sessionId找chatrecord 如果有chat_inout 有1就回复率+1
                    ChatRecord chatRecord2 = new ChatRecord();
                    chatRecord2.setSessionId(record.getSessionId());
                    chatRecord2.setChatInout(1L);
                    List<ChatRecord> chatRecords = chatRecordMapper.selectChatRecordList(chatRecord2);
                    if (chatRecords.isEmpty()) {
                        CustomerServiceDetail customerServiceDetail = customerServiceDetailMapper.selectCustomerDetailByUserId(userId);
                        //就一个逻辑 回复数量+1
                        redisCache.leftPush(replyNumKey, customerServiceDetail);
                    }
                    ChatRecord chatRecord = new ChatRecord();
                    chatRecord.setId(id);
                    chatRecord.setChatInout(1L);
                    chatRecord.setMessageContent(body);
                    chatRecord.setSendTime(date);
                    chatRecord.setSessionId(record.getSessionId());
                    chatRecord.setSuccess(1L);
                    try {
                        chatRecordMapper.insertChatRecord(chatRecord);
                    } catch (Exception e) {
                        log.info("是否消息-时间唯一索引错误了");
                        log.info(e.getMessage());
                    }
                    //推redis
                    //推送websocket
                    //组装消息
                    JSONObject jsonObject = assembleBackMsg(record, chatRecord);
                    HashMap<Long, Session> userSessionMap = WebSocketService.userSessionMap;
                    Session session = userSessionMap.get(userId);
                    WebSocketService.SendMessage(session, jsonObject.toString());
                }
//                customerServiceDetailMapper.updateCustomerServiceDetailSub(userId);
            }
        }
    }

    @Override
    public ArrayList<Long> uploadAccount(Accounts accounts) {
        //todo 这里的所属项目需要后期接口传递
        String account = accounts.getAccount();
        Accounts accounts1 = accountsMapper.selectAccountsByAccount(account);
        if (Objects.nonNull(accounts1)) {
            Long assignedTo = accounts1.getAssignedTo();
            if (Objects.isNull(assignedTo)) {
                throw new ServiceException("该账号未绑定");
            }
            return new ArrayList<>(Arrays.asList(assignedTo));
        }else {
            accounts.setProjectId("28e9dd50bf484269ad4ad4af0094d247");
            accounts.setCreateTime(new Date());
            accountsMapper.insertAccounts(accounts);
            throw new ServiceException("首次上报成功",201);
        }
    }

    public boolean isNumericByRegex(String str) {
        return str.matches("^\\d+$");
    }


    public static String convertImageToBase64(File file) {
        try (FileInputStream imageInFile = new FileInputStream(file)) {
            byte[] imageData = new byte[(int) file.length()];
            imageInFile.read(imageData);
            // 把字节数组转换为Base64编码字符串
            return Base64.getEncoder().encodeToString(imageData);
        } catch (Exception e) {
            System.err.println("Error reading the file: " + e.getMessage());
            return null;
        }
    }
    //补齐并发发送数量的问题 兜底
//    private void polErupt(CustomerServiceDetail customerServiceDetail) {
//        List<CustomerServiceDetail> customerServiceDetails = customerServiceDetailMapper.selectCustomerDetailByManagerId(customerServiceDetail.getManagerId());
//        for (CustomerServiceDetail serviceDetail : customerServiceDetails) {
//            Long surplusNum = serviceDetail.getSurplusNum();
//            serviceDetail.setSendNum(surplusNum);
//        }
//        customerServiceDetailMapper.updateCustomerServiceDetail2SurplusNumBatch(customerServiceDetails);
//    }

}
