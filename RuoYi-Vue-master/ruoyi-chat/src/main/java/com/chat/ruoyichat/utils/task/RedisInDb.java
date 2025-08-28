package com.chat.ruoyichat.utils.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chat.ruoyichat.domain.*;
import com.chat.ruoyichat.domain.dto.PhoneInfo;
import com.chat.ruoyichat.domain.sendDto.AccountInfo;
import com.chat.ruoyichat.mapper.*;
import com.chat.ruoyichat.service.impl.SendMsgServiceImpl;
import com.chat.ruoyichat.websocket.WebSocketService;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.system.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author chat
 * @since 2025/3/16
 **/
@Slf4j
@Component
@Configuration
@EnableScheduling
public class RedisInDb {
    //    @Resource
//    private RedisTemplate<String, Integer> redisTemplate;
    @Resource
    private RedisCache redisCache;
    @Autowired
    private TaskContentMapper taskContentMapper;
    @Autowired
    private TaskSendNumMapper taskSendNumMapper;
    @Autowired
    private AccountsMapper accountsMapper;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private CustomerServiceDetailMapper customerServiceDetailMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SendMsgServiceImpl sendMsgServiceImpl;

    private final String isReplyKey = CacheConstants.IS_REPLY_KEY;

    private final String replyNumKey = CacheConstants.REPLY_NUM;
    private final String successNumKey = CacheConstants.SUCCESS_Num;
    private final String sendNumKey = CacheConstants.SEND_NUM;
    private final Random random = new Random();


    /**
     * 看看哪个账号有回复
     */
//    @Scheduled(fixedDelay = 10000)
    @Scheduled(fixedDelay = 20000)
    public void redisInDb() {
//        HashSet<Long> userIdList = WebSocketService.userIdList;
        HashSet<Long> userIdList = new HashSet<>(WebSocketService.userIdList);
//        if (userIdList.isEmpty()) {
//            redisFree(0L);
//            return;
//        }
        if (userIdList.isEmpty()) {
            return;
        }
        List<CustomerServiceDetail> customerServiceDetails = customerServiceDetailMapper.selectCustomerDetailByUserIdAndManager(userIdList);
        if (!customerServiceDetails.isEmpty()) {
            HashSet<Long> managerId = new HashSet<>();
            for (CustomerServiceDetail customerServiceDetail : customerServiceDetails) {
                managerId.add(customerServiceDetail.getUserId());
            }
            List<CustomerServiceDetail> customerServiceDetailManger = customerServiceDetailMapper.selectCustomerDetailByManagerIdSet(managerId);
            for (CustomerServiceDetail customerServiceDetail : customerServiceDetailManger) {
                userIdList.add(customerServiceDetail.getUserId());
            }
        }
        List<Accounts> accounts = accountsMapper.selectAccountsByAccountSet(userIdList);
        for (Accounts account : accounts) {
            if (!account.getAccStatus().equals(0L)) {
                continue;
            }
            String deviceInfo = account.getDeviceInfo();


            PhoneInfo phoneInfo = new PhoneInfo();
//            if (!JSON.isValid(deviceInfo)) {
            phoneInfo.setDeviceInfo(deviceInfo);
            phoneInfo.setId(account.getAccountId().toString());
            phoneInfo.setMyphonenumber(account.getAccount());
            phoneInfo.setUserId(account.getAssignedTo());
            phoneInfo.setCookie(account.getCookie());
            phoneInfo.setUserName(account.getUserName());
            phoneInfo.setDeviceInfo(deviceInfo);
//            }else {
//            JSONObject jsonObject = JSON.parseObject(deviceInfo);
//            phoneInfo.setId(account.getAccountId().toString());
//            phoneInfo.setMyphonenumber(account.getAccount());
//            phoneInfo.setUserId(account.getAssignedTo());
//            phoneInfo.setCookie(account.getCookie());
//            Object string = jsonObject.get("userName");
//            if (Objects.isNull(string)) {
//                string = jsonObject.get("email");
//            }
//            phoneInfo.setUserName(string.toString());
//            phoneInfo.setDeviceInfo(deviceInfo);
//            }
            AccountInfo accountInfo = phoneInfo.getAccountInfo();
            accountInfo.setAccount_sid(account.getUserName());
            accountInfo.setAuth_token(account.getPassword());
            accountInfo.setCurrentNumber("+" + account.getAccount());

            //去重
            if (!redisCache.checkAndLeftPush(isReplyKey, phoneInfo)) {
                redisCache.leftPush(isReplyKey, phoneInfo);
            }
        }
//        Long listLength = redisCache.getListLength(isReplyKey);
//        if (listLength < 500L) {
//            redisFree(listLength);
//        }
    }

    private void redisFree(Long listLength) {
        //redis挺闲
        SysUser sysUser = new SysUser();
        sysUser.setStatus("0");
        List<SysUser> sysUsers = sysUserMapper.selectUserList(sysUser);
        HashSet<Long> userIdSet = new HashSet<>();
        for (SysUser user : sysUsers) {
            userIdSet.add(user.getUserId());
        }
        List<Accounts> accounts1 = accountsMapper.selectAccountsByAccountSet(userIdSet);
        Collections.shuffle(accounts1);
//                redisCache.leftPush(isReplyKey, phoneInfo);
        for (Accounts account : accounts1) {
            String deviceInfo = account.getDeviceInfo();
            JSONObject jsonObject = JSON.parseObject(deviceInfo);
            PhoneInfo phoneInfo = new PhoneInfo();
            phoneInfo.setId(account.getAccountId().toString());
            phoneInfo.setMyphonenumber(account.getAccount());
            phoneInfo.setUserId(account.getAssignedTo());
            phoneInfo.setCookie(account.getCookie());
            Object string = jsonObject.get("userName");
            if (Objects.isNull(string)) {
                string = jsonObject.get("email");
            }
            phoneInfo.setUserName(string.toString());
            phoneInfo.setDeviceInfo(deviceInfo);
//                Long listLengthTemp = redisCache.getListLength(isReplyKey);
            if (listLength < 500L) {
                redisCache.leftPush(isReplyKey, phoneInfo);
                listLength++;
            } else {
                return;
            }
        }
    }

    /**
     * 落库
     */
//    @Scheduled(fixedDelay = 30000)
    @Scheduled(fixedDelay = 120000)
    public void fixedDelayTask() {
        String taskKey = CacheConstants.TASK_NUM;
        String accountNum = CacheConstants.ACCOUNT_NUM;
        HashMap<String, Object> valuesWithPrefix = getValuesWithPrefix(taskKey + "*");
        HashMap<String, Object> valuesWithPrefix1 = getValuesWithPrefix(accountNum + "*");
        Set<Map.Entry<String, Object>> entries = valuesWithPrefix.entrySet();
        ArrayList<TaskContent> taskContents = new ArrayList<>();
        for (Map.Entry<String, Object> entry : entries) {
            String key = entry.getKey().split(":")[1];
            Object value = entry.getValue();
            TaskContent taskContent = new TaskContent();
            taskContent.setTaskContentId(key);
            taskContent.setIsUse((long) (int) value);
            taskContents.add(taskContent);
        }
        if (!taskContents.isEmpty()) {
            taskContentMapper.updateTaskContent2UseBatch(taskContents);
        }
        Set<Map.Entry<String, Object>> entries1 = valuesWithPrefix1.entrySet();
        ArrayList<TaskSendNum> taskSendNumList = new ArrayList<>();
        for (Map.Entry<String, Object> stringObjectEntry : entries1) {
            String key = stringObjectEntry.getKey().split(":")[1];
            Object value = stringObjectEntry.getValue();
            TaskSendNum taskSendNum = new TaskSendNum();
            taskSendNum.setTaskContentId(key);
            taskSendNum.setSendNum(Long.parseLong(value.toString()));
            Long sendNum = taskSendNum.getSendNum();
            //todo
            if (sendNum.equals(-1L)) {
                taskSendNum.setSendNum(0L);
            }
//                redisCache.setOne(accountNum + key, -1);
            taskSendNumList.add(taskSendNum);
        }
        if (!taskSendNumList.isEmpty()) {
            for (TaskSendNum taskSendNum : taskSendNumList) {
                String taskContentId = taskSendNum.getTaskContentId();
                TaskSendNum taskSendNum1 = taskSendNumMapper.selectTaskSendNumByTaskContentId(taskContentId);
                if (Objects.isNull(taskSendNum1)) {
                    taskSendNumMapper.insertTaskSendNum(taskSendNum);
                } else {
                    //应该是设置成redis的 而不是加上redis的数量
//                    taskSendNum.setSendNum(taskSendNum1.getSendNum() + taskSendNum.getSendNum());
                    taskSendNumMapper.updateTaskSendNum(taskSendNum);
                }
            }
        }
    }


    //删除不在线的websocket
    @Scheduled(cron = "0 0 12 * * ?")
    public void refreshOnLineWebsocket() {
        Collection<String> keys = redisCache.keys(CacheConstants.LOGIN_TOKEN_KEY + "*");
        HashSet<Long> userIdSet = new HashSet<>();//所有在线用户 去重
        for (String key : keys) {
            LoginUser user = redisCache.getCacheObject(key);
            userIdSet.add(user.getUserId());
        }
        HashSet<Long> userIdList = new HashSet<>(WebSocketService.userIdList);
        for (Long l : userIdList) {
            if (!userIdSet.contains(l)) {
                WebSocketService.userIdList.remove(l);
                WebSocketService.userSessionMap.remove(l);
            }
        }
    }


    //超时的处理
//    @Scheduled(fixedDelay = 180000)
    public void reFlashTask() {
        Task task = new Task();
        task.setTaskStatus(2L);//已完成
        List<Task> tasks = taskMapper.selectTaskList(task);
//        HashMap<Long, Long> userSuccessNum = new HashMap<>();
        for (Task taskItem : tasks) {
            String taskId = taskItem.getTaskId();
            TaskContent taskContent = new TaskContent();
            taskContent.setTaskId(taskId);
            taskContent.setIsUse(0L);
            List<TaskContent> taskContents = taskContentMapper.selectTaskContentList(taskContent);
            if (taskContents.isEmpty()) {
                taskContent.setIsUse(1L);
                taskContent.setTaskStatus(2);
                List<TaskContent> taskContents2Update = taskContentMapper.selectTaskContentList(taskContent);
                for (TaskContent content : taskContents2Update) {
//                    Long assignedTo = content.getAssignedTo();
//                    if (!userSuccessNum.containsKey(assignedTo)) {
//                        userSuccessNum.put(assignedTo, 0L);
//                    }else {
//                        userSuccessNum.put(assignedTo, userSuccessNum.get(assignedTo) + 1L);
//                    }
                    content.setTaskStatus(1);
//                    sendMsgServiceImpl.getSendMsgSuccess(content.getTaskContentId(), content.getRecipientList());
                }
                if (!taskContents2Update.isEmpty()) {
                    taskContentMapper.updateTaskContentStartToBatch(taskContents2Update);
                }
            }
        }
    }


    /**
     * 闲账号扫封
     */
//    @Scheduled(fixedDelay = 10000)
//    @Scheduled(fixedDelay = 3600000)
//    public void noAssigendInRedis() {
//        Accounts accountsQuery = new Accounts();
//        accountsQuery.setAccStatus(0L);
////        List<Accounts> accounts = accountsMapper.selectAccountsList(accountsQuery);
//        List<Accounts> accounts1 = accountsMapper.selectAccountsListNoAssigned(null);
//        for (Accounts account : accounts1) {
//            String deviceInfo = account.getDeviceInfo();
//            JSONObject jsonObject = JSON.parseObject(deviceInfo);
//            PhoneInfo phoneInfo = new PhoneInfo();
//            Long userId = account.getAssignedTo();
//            phoneInfo.setId(account.getAccountId().toString());
//            phoneInfo.setMyphonenumber(account.getAccount());
//            phoneInfo.setUserId(account.getAssignedTo());
//            phoneInfo.setCookie(account.getCookie());
//            phoneInfo.setUserName(jsonObject.get("userName").toString());
//            phoneInfo.setDeviceInfo(deviceInfo);

    /// /            Long listLength = redisCache.getListLength(isReplyKey);
    /// /            if (listLength < 4000L) {
    /// /                redisCache.leftPush(isReplyKey, phoneInfo);
    /// /            }
//            //去重
//            if (!redisCache.checkAndLeftPush(noAssInRedis, phoneInfo)) {
//                redisCache.leftPush(noAssInRedis, phoneInfo);
//            }
//        }
//    }


    //        @Scheduled(fixedDelay = 150000)//2.5min
    public void syncTaskStatus() {
        List<Task> tasks = taskMapper.selectByEndTimeLess3Min();
        if (tasks.isEmpty()) {
            return;
        }
        ArrayList<CustomerServiceDetail> needUpdate = new ArrayList<>();
        for (Task task : tasks) {
            TaskContent taskContent = new TaskContent();
            taskContent.setTaskId(task.getTaskId());
            List<TaskContent> taskContents = taskContentMapper.selectTaskContentList(taskContent);
            HashSet<Long> userId = new HashSet<>();
            for (TaskContent content : taskContents) {
                userId.add(content.getAssignedTo());
            }
            List<CustomerServiceDetail> customerServiceDetails = customerServiceDetailMapper.selectCustomerDetailByUserIdList(userId);
            for (CustomerServiceDetail customerServiceDetail : customerServiceDetails) {
                Long surplusNum = customerServiceDetail.getSurplusNum();
                Long sendNum = customerServiceDetail.getSendNum();
                if (surplusNum < sendNum) {
                    customerServiceDetail.setSendNum(surplusNum);
                    needUpdate.add(customerServiceDetail);
                }
            }
            if (!needUpdate.isEmpty()) {
                for (CustomerServiceDetail customerServiceDetail : needUpdate) {
                    customerServiceDetailMapper.updateCustomerServiceDetail(customerServiceDetail);
                }
            }
        }
    }


    public HashMap<String, Object> getValuesWithPrefix(String redisKey) {
        HashMap<String, Object> keyValueMap = new HashMap<>();
        // 定义扫描选项，匹配以 abc: 开头的键
        ScanOptions options = ScanOptions.scanOptions().match(redisKey).build();
        // 使用 scan 方法扫描匹配的键
//        try (Cursor<byte[]> cursor = redisTemplate.getConnectionFactory().getConnection().scan(options)) {
        try (Cursor<byte[]> cursor = redisCache.getPrefix(options)) {
            while (cursor.hasNext()) {
                String key = new String(cursor.next());
                // 获取键对应的值
                Object value = redisCache.getCacheObject(key);
//                Object value = redisTemplate.opsForValue().get(key);
                if (value != null) {
                    keyValueMap.put(key, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 删除获取到的键
        for (String key : keyValueMap.keySet()) {
            redisCache.deleteObject(key);
//            redisTemplate.delete(key);
        }
        return keyValueMap;
    }

    //
    @Scheduled(fixedDelay = 20000)
    public void ReplyNumAddOne() {
        Boolean b = redisCache.hasKey(replyNumKey);
        if (b) {
            List<Object> allListElements = redisCache.dequeue(replyNumKey, 100);
            HashMap<Long, Long> idCountMap = new HashMap<>();
            ArrayList<CustomerServiceDetail> customerServiceDetails = new ArrayList<>();
            if (allListElements.isEmpty()) {
                return;
            }
            for (Object allListElement : allListElements) {
                JSONObject jsonObject1 = JSON.parseObject(allListElement.toString());
                String strObj = jsonObject1.toString();
                CustomerServiceDetail csd = JSON.parseObject(strObj, CustomerServiceDetail.class);
                Long count = idCountMap.getOrDefault(csd.getUserId(), 0L);
                idCountMap.put(csd.getUserId(), count + 1L);
            }
            Set<Map.Entry<Long, Long>> entries = idCountMap.entrySet();
            for (Map.Entry<Long, Long> entry : entries) {
                Long key = entry.getKey();
                Long value = entry.getValue();
                CustomerServiceDetail customerServiceDetail = new CustomerServiceDetail();
                customerServiceDetail.setUserId(key);
                customerServiceDetail.setReplyNum(value);
                customerServiceDetails.add(customerServiceDetail);
            }
            customerServiceDetailMapper.updateCustomerServiceDetail2ReplyNumKeyBatch(customerServiceDetails);
            log.info("成功更新 {} 用户的回复次数", customerServiceDetails.size());
        }
    }

    @Scheduled(fixedDelay = 15000)
    public void SuccessNumAddOne() {
        Boolean b = redisCache.hasKey(successNumKey);
        if (b) {
            List<Object> allListElements = redisCache.dequeue(successNumKey, 100);
            HashMap<Long, Long> idCountMap = new HashMap<>();
            ArrayList<CustomerServiceDetail> customerServiceDetails = new ArrayList<>();
            if (allListElements.isEmpty()) {
                return;
            }
            for (Object allListElement : allListElements) {
                JSONObject jsonObject1 = JSON.parseObject(allListElement.toString());
                String strObj = jsonObject1.toString();
                CustomerServiceDetail csd = JSON.parseObject(strObj, CustomerServiceDetail.class);
                Long count = idCountMap.getOrDefault(csd.getUserId(), 0L);
                idCountMap.put(csd.getUserId(), count + 1L);
            }
            Set<Map.Entry<Long, Long>> entries = idCountMap.entrySet();
            for (Map.Entry<Long, Long> entry : entries) {
                Long key = entry.getKey();
                Long value = entry.getValue();
                CustomerServiceDetail customerServiceDetail = new CustomerServiceDetail();
                customerServiceDetail.setUserId(key);
                customerServiceDetail.setSuccessNum(value);
                customerServiceDetails.add(customerServiceDetail);
            }
            customerServiceDetailMapper.updateCustomerServiceDetail2SuccessNumKeyBatch(customerServiceDetails);
            log.info("成功更新 {} 用户的发送成功次数", customerServiceDetails.size());
        }
    }

    @Scheduled(fixedDelay = 10000)
    public void SendNumAddOne() {
        Boolean b = redisCache.hasKey(sendNumKey);
        if (b) {
            List<Object> allListElements = redisCache.dequeue(sendNumKey, 100);
            HashMap<Long, Long> idCountMap = new HashMap<>();
            HashMap<Long, CustomerServiceDetail> idCsdMap = new HashMap<>();
            ArrayList<CustomerServiceDetail> customerServiceDetails = new ArrayList<>();
            if (allListElements.isEmpty()) {
                return;
            }
            for (Object allListElement : allListElements) {
                JSONObject jsonObject1 = JSON.parseObject(allListElement.toString());
                String strObj = jsonObject1.toString();
                CustomerServiceDetail csd = JSON.parseObject(strObj, CustomerServiceDetail.class);
                Long count = idCountMap.getOrDefault(csd.getUserId(), 0L);
                idCountMap.put(csd.getUserId(), count + 1L);
            }
            Set<Map.Entry<Long, Long>> entries = idCountMap.entrySet();
            for (Map.Entry<Long, Long> entry : entries) {
                Long key = entry.getKey();
                Long value = entry.getValue();
                CustomerServiceDetail customerServiceDetail = new CustomerServiceDetail();
                customerServiceDetail.setUserId(key);
                customerServiceDetail.setSendNum(value);
                customerServiceDetails.add(customerServiceDetail);
            }
            customerServiceDetailMapper.updateCustomerServiceDetail2SendNumKeyBatch(customerServiceDetails);
            log.info("成功更新 {} 用户的总发送次数", customerServiceDetails.size());
        }
    }

    // 返回 5000-15000 之间的随机数（单位：毫秒）
    public long getRandomDelay() {
        return 5000 + random.nextInt(10000); // 5s + [0,15s) = 5s-15s
    }
}
