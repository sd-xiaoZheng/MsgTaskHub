package com.chat.ruoyichat.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chat.ruoyichat.domain.*;
import com.chat.ruoyichat.domain.dto.AccountStatus;
import com.chat.ruoyichat.mapper.*;
import com.chat.ruoyichat.service.IAccountsService;
import com.chat.ruoyichat.utils.excel.EasyExcelUtil;
import com.chat.ruoyichat.websocket.WebSocketService;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.mapper.SysUserMapper;
import lombok.Synchronized;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.websocket.Session;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

import static com.ruoyi.common.utils.PageUtils.startPage;

/**
 * 分配给的客服账号Service业务层处理
 *
 * @author ruoyi
 * @date 2025-03-14
 */
@Service
public class AccountsServiceImpl implements IAccountsService {
    @Autowired
    private AccountsMapper accountsMapper;
    @Autowired
    private CustomerServiceDetailServiceImpl customerServiceDetailServiceImpl;
    @Autowired
    private CustomerServiceDetailMapper customerServiceDetailMapper;
    @Autowired
    private ProjectSetMapper projectSetMapper;
    @Autowired
    private TaskContentMapper taskContentMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private TaskSendNumMapper taskSendNumMapper;

    /**
     * 查询分配给的客服账号
     *
     * @param accountId 分配给的客服账号主键
     * @return 分配给的客服账号
     */
    @Override
    public Accounts selectAccountsByAccountId(Long accountId) {
        return accountsMapper.selectAccountsByAccountId(accountId);
    }

    /**
     * 查询分配给的客服账号列表
     *
     * @param accounts 分配给的客服账号
     * @return 分配给的客服账号
     */
    @Override
    public List<Accounts> selectAccountsList(Accounts accounts) {
        Long assignedTo = accounts.getAssignedTo();
        if (Objects.nonNull(assignedTo) && assignedTo.equals(1L)) {
            return new ArrayList<>();
        }
        if (ObjectUtils.isEmpty(assignedTo)) {
            startPage();
            List<Accounts> accounts1 = accountsMapper.selectAccountsList(accounts);
            getsendNum(accounts1);
            return accounts1;
        }
        CustomerServiceDetail customerServiceDetail = customerServiceDetailMapper.selectCustomerDetailByUserId(assignedTo);
        Long managerId = customerServiceDetail.getManagerId();
        HashSet<Long> longs = new HashSet<>();
        if (ObjectUtils.isEmpty(managerId) || managerId.equals(-1L)) {
            CustomerServiceDetail customerServiceDetail1 = new CustomerServiceDetail();
            customerServiceDetail1.setManagerId(customerServiceDetail.getUserId());
            List<CustomerServiceDetail> customerServiceDetails = customerServiceDetailMapper.selectCustomerServiceDetailList(customerServiceDetail1);
            for (CustomerServiceDetail serviceDetail : customerServiceDetails) {
                longs.add(serviceDetail.getUserId());
            }
        } else {
            startPage();
            List<Accounts> accounts1 = accountsMapper.selectAccountsList(accounts);
            getsendNum(accounts1);
            return accounts1;
        }
        startPage();
//        List<Accounts> accounts1 = accountsMapper.selectAccountsByAccountSet(longs);
        List<Accounts> accounts1 = new ArrayList<>();
        if (!longs.isEmpty()) {
            accounts1 = accountsMapper.selectAccountsByAccountSetAndStatus(longs, accounts.getAccStatus());
            getsendNum(accounts1);
        } else {
            return accounts1;
        }
        return accounts1;
//        List<Accounts> accounts1 = accountsMapper.selectAccountsByAccountSetAndStatus(longs,accounts.getAccStatus());
//        ArrayList<Accounts> accounts2 = new ArrayList<>();
//        ArrayList<Accounts> accounts4 = new ArrayList<>();
//        Long accStatus = accounts.getAccStatus();
//        if (ObjectUtils.isEmpty(accStatus)) {
//            getsendNum(accounts1);
//            return accounts1;
//        }
//        for (Accounts accounts3 : accounts1) {
//            if (accStatus.equals(-1L)) {
//                accounts2.add(accounts3);
//            } else {
//                accounts4.add(accounts3);
//            }
//        }
//        if (accStatus.equals(-1L)) {
//            getsendNum(accounts2);
//            return accounts2;
//        } else {
//            getsendNum(accounts4);
//            return accounts4;
//        }
    }

    private void getsendNum(List<Accounts> accounts1) {
        HashSet<String> accountSet = new HashSet<>();
        for (Accounts accounts2 : accounts1) {
            accountSet.add(accounts2.getAccount());
            if (!JSON.isValid(accounts2.getDeviceInfo())) {
                continue;
            }
            JSONObject jsonObject = JSON.parseObject(accounts2.getDeviceInfo());
            Object o = jsonObject.get("virtualPhoneNumber");
            if (Objects.nonNull(o)) {
                JSONObject jsonObject2 = JSON.parseObject(o.toString());
                Object o1 = jsonObject2.get("number");
                accounts2.setNumber(o1.toString());
            } else {
                accounts2.setNumber(jsonObject.get("phone").toString());
            }
        }
        HashMap<String, Long> sendNumMap = new HashMap<>();
        List<TaskSendNum> taskSendNums = taskSendNumMapper.selectTaskSendNumListByIds(accountSet);
        for (TaskSendNum taskSendNum : taskSendNums) {
            sendNumMap.put(taskSendNum.getTaskContentId(), taskSendNum.getSendNum());
        }
        for (Accounts accounts2 : accounts1) {
            Long l = sendNumMap.get(accounts2.getAccount());
            if (Objects.nonNull(l)) {
                accounts2.setSendNum(l);
            } else {
                accounts2.setSendNum(0L);
            }
        }
    }

    @Override
    public List<Accounts> selectAccountsRecycleList(Accounts accounts) {
        return accountsMapper.selectAccountsRecycleList(accounts);
    }


    /**
     * 新增分配给的客服账号
     *
     * @param accounts 分配给的客服账号
     * @return 结果
     */
    @Override
    public int insertAccounts(Accounts accounts) {
        return accountsMapper.insertAccounts(accounts);
    }

    /**
     * 修改分配给的客服账号
     *
     * @param accounts 分配给的客服账号
     * @return 结果
     */
    @Override
    public int updateAccounts(Accounts accounts) {
        return accountsMapper.updateAccounts(accounts);
    }

    /**
     * 批量假删除分配给的客服账号
     *
     * @param accountIds 需要删除的分配给的客服账号主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteAccountsByAccountIds(Long[] accountIds) {
        Accounts accounts = accountsMapper.selectAccountsByAccountId(accountIds[0]);
        Long assignedTo = accounts.getAssignedTo();
        if (!ObjectUtils.isEmpty(assignedTo)) {
            CustomerServiceDetail customerServiceDetail = customerServiceDetailMapper.selectCustomerDetailByUserId(assignedTo);
            if (!customerServiceDetail.getManagerId().equals(-1L)) {
                customerServiceDetail.setIsEnough(0);
            }
            customerServiceDetailMapper.updateCustomerServiceDetail(customerServiceDetail);
        }
        int i = accountsMapper.deleteAccountsByAccountIds(accountIds);
        //删除的同时删除他的使用信息
        return i;
    }

    /**
     * 批量删除分配给的客服账号
     *
     * @param accountIds 需要删除的分配给的客服账号主键
     * @return 结果
     */
    @Override
    public int deleteAccountsByAccountIdsReal(Long[] accountIds) {
        return accountsMapper.deleteAccountsByAccountIdsReal(accountIds);
    }

    /**
     * 删除分配给的客服账号信息
     *
     * @param accountId 分配给的客服账号主键
     * @return 结果
     */
    @Override
    public int deleteAccountsByAccountId(Long accountId) {
        return accountsMapper.deleteAccountsByAccountId(accountId);
    }

    @Override
    public ArrayList<Accounts> importAccount(MultipartFile file, String projectId) throws IOException {
        List<Accounts> ts = EasyExcelUtil.syncReadModel(file.getInputStream(), Accounts.class, 0, 1);
//        projectId = getProjectIdByLoginUserId();
        List<Accounts> importAccountList = ts.stream()
                .peek(accounts -> {
                    accounts.setProjectId(projectId);
                    accounts.setUpUser(SecurityUtils.getUsername());
                })
                .collect(Collectors.toList());
        importAccountList.forEach(accounts -> {
            accounts.setCreateTime(new Date());
            String deviceInfo = accounts.getDeviceInfo();
            if (JSON.isValid(deviceInfo)) {
                JSONObject jsonObject = JSON.parseObject(deviceInfo);
                Object o = jsonObject.get("token");
                if (Objects.nonNull(o)) {
                    accounts.setCookie(o.toString());
                }
            }
//            else {
//                throw new ServiceException("deviceInfo格式错误");
//            }
        });
        List<Accounts> repeatAccount = accountsMapper.isRepeat(importAccountList);
        if (!repeatAccount.isEmpty()) {
            return new ArrayList<>(repeatAccount);
        }
        accountsMapper.insertImportBatch(importAccountList);

        //导入时推入redis 扫描是否封号
//        ArrayList<Accounts> repeat = new ArrayList<>();
//        for (Accounts account : importAccountList) {
//            Long accountId = account.getAccountId();
//            if(ObjectUtils.isEmpty(accountId)) {
//                repeat.add(account);
//                continue;
//            }
//            String deviceInfo = account.getDeviceInfo();
//            JSONObject jsonObject = JSON.parseObject(deviceInfo);
//            PhoneInfo phoneInfo = new PhoneInfo();
//            Long userId = account.getAssignedTo();
//            phoneInfo.setId(accountId.toString());
//            phoneInfo.setMyphonenumber(account.getAccount());
//            phoneInfo.setUserId(account.getAssignedTo());
//            phoneInfo.setCookie(account.getCookie());
//            phoneInfo.setUserName(jsonObject.get("userName").toString());
//            phoneInfo.setDeviceInfo(deviceInfo);
//            redisCache.leftPush(isReplyKey, phoneInfo);
//        }
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Accounts> importAccountTxt(MultipartFile file, String projectId) throws IOException {
        ArrayList<JSONObject> accountTxt = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
//                accountTxt.add(line);
                line = line.replace("'", "\"");
                // 将字符串转换为JSON对象
                line = line.replace("True", "true").replace("False", "false");
                JSONObject jsonObject = JSON.parseObject(line);
                // 打印JSON对象
                accountTxt.add(jsonObject);
            }
        }
        List<Accounts> importAccountList = new ArrayList<>();
        Date date = new Date();
        String username = SecurityUtils.getUsername();
        for (JSONObject jsonObject : accountTxt) {
            Accounts accounts = new Accounts();
            accounts.setAccount(jsonObject.getString("email"));
            accounts.setCookie(jsonObject.getString("token"));
            accounts.setDeviceInfo(jsonObject.toString());
            accounts.setCountry(jsonObject.getString("name"));
            accounts.setUserName(jsonObject.getString("email"));
            accounts.setPassword(jsonObject.getString("password"));
            accounts.setProjectId(projectId);
            accounts.setCreateTime(date);
            accounts.setUpUser(username);
            importAccountList.add(accounts);
        }
        List<Accounts> repeatAccount = accountsMapper.isRepeat(importAccountList);
        if (!repeatAccount.isEmpty()) {
            return new ArrayList<>(repeatAccount);
        }
        accountsMapper.insertImportBatch(importAccountList);
//        List<Accounts> ts = EasyExcelUtil.syncReadModel(file.getInputStream(), Accounts.class, 0, 1);
////        projectId = getProjectIdByLoginUserId();
//        List<Accounts> importAccountList = ts.stream()
//                .peek(accounts -> {
//                    accounts.setProjectId(projectId);
//                    accounts.setUpUser(SecurityUtils.getUsername());
//                })
//                .collect(Collectors.toList());
//        importAccountList.forEach(accounts -> accounts.setCreateTime(new Date()));
//        List<Accounts> repeatAccount = accountsMapper.isRepeat(importAccountList);
//        if (!repeatAccount.isEmpty()) {
//            return new ArrayList<>(repeatAccount);
//        }
//        accountsMapper.insertImportBatch(importAccountList);

        //导入时推入redis 扫描是否封号
//        ArrayList<Accounts> repeat = new ArrayList<>();
//        for (Accounts account : importAccountList) {
//            Long accountId = account.getAccountId();
//            if(ObjectUtils.isEmpty(accountId)) {
//                repeat.add(account);
//                continue;
//            }
//            String deviceInfo = account.getDeviceInfo();
//            JSONObject jsonObject = JSON.parseObject(deviceInfo);
//            PhoneInfo phoneInfo = new PhoneInfo();
//            Long userId = account.getAssignedTo();
//            phoneInfo.setId(accountId.toString());
//            phoneInfo.setMyphonenumber(account.getAccount());
//            phoneInfo.setUserId(account.getAssignedTo());
//            phoneInfo.setCookie(account.getCookie());
//            phoneInfo.setUserName(jsonObject.get("userName").toString());
//            phoneInfo.setDeviceInfo(deviceInfo);
//            redisCache.leftPush(isReplyKey, phoneInfo);
//        }
        return new ArrayList<>();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized String fillAccount(Long userId, Integer num) {
        CustomerServiceDetail customerServiceDetail = customerServiceDetailMapper.selectCustomerDetailByUserId(userId);
//        int banNum = fillBan(customerServiceDetail, num);
//        int banNum = fillBan(customerServiceDetail, 0);
        if (num.equals(-1)) {
            //总数-封禁的号拿的数量-正常号*10 / 10 kpbl111.
            Long maxLoad = customerServiceDetail.getMaxLoad();
            HashSet<Long> userIdSet = new HashSet<>();
            userIdSet.add(userId);
//            taskContentMapper.countTaskContentListByTaskIdSet(userIdSet);
            List<Accounts> accounts = accountsMapper.selectAccountsListIsBan(userIdSet);
            HashSet<String> accountSet = new HashSet<>();
            for (Accounts account : accounts) {
                accountSet.add(account.getAccount());
            }
            int banSendNum = 0;
            if (!accountSet.isEmpty()) {
                List<TaskSendNum> taskSendNums = taskSendNumMapper.selectTaskSendNumListByIds(accountSet);
                for (TaskSendNum taskSendNum : taskSendNums) {
                    banSendNum += taskSendNum.getSendNum();
                }
            }
            int onBanNum = accountsMapper.selectAccountsCountByUserID(userId);
            CustomerServiceDetail customerServiceDetail1 = customerServiceDetailMapper.selectCustomerDetailByUserId(customerServiceDetail.getManagerId());
            Integer isEnough = customerServiceDetail1.getIsEnough();
            int factor = (int) (maxLoad - banSendNum - onBanNum * isEnough);
            if (factor <= 0) {
                return 0 + ";" + 0;
            }
            num = factor % isEnough == 0 ? factor / isEnough : factor / isEnough + 1;
        }
        int enoughNum = fillNotEnough(customerServiceDetail, num);
        customerServiceDetailMapper.updateCustomerServiceDetail(customerServiceDetail);
        return 0 + ";" + enoughNum;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String dimission(Long userId) {
        //先删除任务
        TaskContent taskContent = new TaskContent();
        taskContent.setAssignedTo(userId);
        List<TaskContent> taskContents = taskContentMapper.selectTaskContentList(taskContent);
        if (!taskContents.isEmpty()) {
            taskContentMapper.updateTaskContent2DeleteBatch(taskContents);
        }
        //号入回收站
        Accounts accounts = new Accounts();
        accounts.setAssignedTo(userId);
        List<Accounts> accounts1 = accountsMapper.selectAccountsList(accounts);
        if (!accounts1.isEmpty()) {
            Long[] accountIds = accounts1.stream().map(Accounts::getAccountId).toArray(Long[]::new);
            accountsMapper.deleteAccountsByAccountIds(accountIds);
        }
        //改user状态
        SysUser sysUser = sysUserMapper.selectUserById(userId);
        sysUser.setStatus("1");
        int i = sysUserMapper.updateUser(sysUser);
        if (i > 0) {
            return "操作成功";
        } else {
            throw new ServiceException("未知错误 请联系管理员");
        }

    }

    @Override
    public String recycleAccount(String projectId) {
        Long userId = SecurityUtils.getUserId();
        int allAccount = accountsMapper.selectAccountsListByprojectDelCount(projectId);
        if (allAccount == 0) {
            return "回收站为空";
        }
        int batchSize = allAccount > 10 ? allAccount / 10 : allAccount;
        int pace = 0;
        int recycleNum = 0;
        while (true) {
            List<Accounts> accounts = accountsMapper.selectAccountsListByprojectDel(projectId, batchSize);
            if (!accounts.isEmpty()) {//分批10
                accountsMapper.updateAccountsRecycleAccount(accounts);
                HashMap<Long, Session> userSessionMap = WebSocketService.userSessionMap;
                Session session = userSessionMap.get(userId);
                JSONObject jsonObject = new JSONObject();
                JSONObject data = new JSONObject();
                jsonObject.put("flag", 3);
                data.put("pace", pace);
                data.put("userId", userId);
                jsonObject.put("data", data);
                WebSocketService.SendMessage(session, jsonObject.toString());
                recycleNum += accounts.size();
                pace += 10;
            } else {
                return "已回收：" + recycleNum + "个账号";
            }
        }
    }

    @Override
    public AccountStatus accountStatus(String projectId) {
        AccountStatus accountStatus = new AccountStatus();
        Accounts accounts = new Accounts();
        accounts.setProjectId(projectId);
        List<Accounts> accountsProjectAll = accountsMapper.selectAccountsList(accounts);
        //总
        accountStatus.setAccountAllNum(accountsProjectAll.size());
        //正常
        ArrayList<Accounts> accountOkList = accountsProjectAll.stream().filter(Accounts -> Accounts.getAccStatus().equals(0L)).collect(Collectors.toCollection(ArrayList::new));
        accountStatus.setAccountOk(accountOkList.size());
        //已封禁
        ArrayList<Accounts> accountBanList = accountsProjectAll.stream().filter(Accounts -> Accounts.getAccStatus().equals(-1L)).collect(Collectors.toCollection(ArrayList::new));
        accountStatus.setAccountBan(accountBanList.size());
        //未分配
        ArrayList<Accounts> accountOkNoAssiList = accountOkList.stream().filter(Accounts -> ObjectUtils.isEmpty(Accounts.getAssignedTo())).collect(Collectors.toCollection(ArrayList::new));
        accountStatus.setAccountNoAssi(accountOkNoAssiList.size());

        return accountStatus;
    }

    @Override
    public String recycleAccountBatch(Long[] accountIds) {
        if (ObjectUtils.isEmpty(accountIds) || accountIds.length == 0) {
            return "请勾选账号";
        }
        List<Long> list = Arrays.asList(accountIds);
        int i = accountsMapper.recycleAccountBatch(list);
        return "恢复成功";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void emptyAccount(String projectId, Long isAll) {
        //清空账号 1所有 0未分配 2封禁 3清空回收站
        if (isAll.equals(1L)) {
            accountsMapper.emptyAccount(projectId);
            //项目中所有人都设置为不足
            customerServiceDetailMapper.updateCustomerServiceemptyAccount(projectId);
        } else if (isAll.equals(0L)) {
            accountsMapper.emptyAccountNoAssigned(projectId);
        } else if (isAll.equals(2L)) {
            accountsMapper.emptyAccountNoBan(projectId);
        } else if (isAll.equals(3L)) {
            accountsMapper.emptyAccountDustbin(projectId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String supplement(Long[] customerIds, Integer num) {
        HashSet<Long> userIdMap = new HashSet<>();
        Collections.addAll(userIdMap, customerIds);

        List<CustomerServiceDetail> customerServiceDetails = customerServiceDetailMapper.selectCustomerDetailByUserIdList(userIdMap);
//        ArrayList<Long> groupLeaderId = new ArrayList<>();
//        ArrayList<Long> groupId = new ArrayList<>();
        //为什么不用set集合：为了实现顺序补号
        HashSet<Long> groupLeaderId = new HashSet<>();
        HashSet<Long> groupId = new HashSet<>();

        for (CustomerServiceDetail customerServiceDetail : customerServiceDetails) {
            Long managerId = customerServiceDetail.getManagerId();
            if (managerId.equals(-1L)) {
                groupLeaderId.add(customerServiceDetail.getUserId());
//                Long userId = customerServiceDetail.getUserId();
//                if (!groupLeaderId.contains(userId)) {
//                    groupLeaderId.add(userId);
//                }
            } else {
//                Long userId = customerServiceDetail.getUserId();
//                if (!groupId.contains(userId)) {
//                    groupId.add(userId);
//                }
                groupId.add(customerServiceDetail.getUserId());
            }
        }
        if (!groupLeaderId.isEmpty() && !groupId.isEmpty()) {
            throw new ServiceException("组长组员不能同时选择");
        }

        int banNum = 0;
        int enoughNum = 0;
        if (!groupId.isEmpty()) {//只有组员
            if (customerIds.length == 0) {
                return 0 + ";" + 0;
            }
            for (Long customerId : customerIds) {
                String res = fillAccount(customerId, num);
                String[] split = res.split(";");
                banNum += Integer.parseInt(split[0]);
                enoughNum += Integer.parseInt(split[1]);
            }
            return banNum + ";" + enoughNum;
        } else {
            //组长
            ArrayList<Long> userId = new ArrayList<>();
            for (Long s : groupLeaderId) {
//                userId.add(s);
                List<CustomerServiceDetail> customerServiceDetails1 = customerServiceDetailMapper.selectCustomerDetailByManagerId(s);
                for (CustomerServiceDetail customerServiceDetail : customerServiceDetails1) {
                    Long userId1 = customerServiceDetail.getUserId();
                    userId.add(userId1);
                }
            }
            if (!userId.isEmpty()) {
                for (Long user : userId) {
                    String res = fillAccount(user, num);
                    String[] split = res.split(";");
                    banNum += Integer.parseInt(split[0]);
                    enoughNum += Integer.parseInt(split[1]);
                }
                return banNum + ";" + enoughNum;
            } else {
                return 0 + ";" + 0;
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Accounts> selectAccountsList4Export(Accounts accounts, Integer exportNum) {
        Long accStatus = accounts.getAccStatus();
        String projectId = accounts.getProjectId();
        //1可用 2已分配 3封禁
        if (accStatus.equals(1L)) {
            List<Accounts> accounts1 = accountsMapper.selectAccountsListNoAssigned(projectId, exportNum);
//            if (exportNum>=accounts1.size()) {
            formatAccount(accounts1);
            deleteReal(accounts1);
            return accounts1;
//            }else {
//                List<Accounts> arrayList = new ArrayList<>();
//                for (int i = 0; i < exportNum; i++) {
//                    arrayList.add(accounts1.get(i));
//                }
//                formatAccount(arrayList);
//                deleteReal(arrayList);
//                return arrayList;
//            }

        } else if (accStatus.equals(2L)) {
            List<Accounts> accounts1 = accountsMapper.selectAccountsListIsAssigned(projectId);
            formatAccount(accounts1);
            return accounts1;
        } else if (accStatus.equals(3L)) {
            Accounts accounts1 = new Accounts();
            accounts1.setProjectId(projectId);
            accounts1.setAccStatus(-1L);
            List<Accounts> accounts2 = accountsMapper.selectAccountsList(accounts1);
            formatAccount(accounts2);
            return accounts2;
        } else if (accStatus.equals(0L)) {
            Accounts accounts2 = new Accounts();
            accounts2.setProjectId(projectId);
            List<Accounts> accounts1 = accountsMapper.selectAccountsList(accounts2);
            formatAccount(accounts1);
            return accounts1;
        }
//        accountsMapper
        return Collections.emptyList();
    }

    @Override
    public void Unblocking(List<Accounts> accounts, Integer status) {
        accountsMapper.Unblocking(accounts, status);
    }

    @Override
    public void resetAccount(Long[] accountIds) {
        List<Accounts> accounts = accountsMapper.selectAccountByIds(accountIds);
        HashSet<String> accountSet = new HashSet<>();
        accounts.forEach(a -> accountSet.add(a.getAccount()));
        accountsMapper.resetAccount(accounts);
        taskSendNumMapper.deleteTaskSendNumByaccountSet(accountSet);
    }


    private void deleteReal(List<Accounts> accounts1) {
        Long[] accountId = accounts1.stream()
                .map(Accounts::getAccountId)
                .toArray(Long[]::new);
//        accountsMapper.deleteAccountsByAccountIdsReal(accountId);
        if (!ObjectUtils.isEmpty(accountId)) {
            accountsMapper.deleteAccountsByAccountIds(accountId);
        }
    }

    private void formatAccount(List<Accounts> accounts) {
        List<SysUser> sysUsers = sysUserMapper.selectUserList(new SysUser());
        HashMap<Long, String> userMap = new HashMap<>();
        HashMap<String, String> projectMap = new HashMap<>();
        for (SysUser sysUser : sysUsers) {
            userMap.put(sysUser.getUserId(), sysUser.getUserName());
        }
        List<Project> projects = projectMapper.selectProjectList(new Project());
        for (Project project : projects) {
            projectMap.put(project.getProjectId(), project.getProjectName());
        }
        for (Accounts accounts2 : accounts) {
            Long assignedTo = accounts2.getAssignedTo();
            String s = userMap.get(assignedTo);
            accounts2.setAssignedToName(s);
            String projectName = projectMap.get(accounts2.getProjectId());
            accounts2.setProjectName(projectName);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public int fillNotEnough(CustomerServiceDetail customerServiceDetail, Integer num) {
        Long managerId = customerServiceDetail.getManagerId();
        if (ObjectUtils.isEmpty(managerId)) {
            return 0;
//            throw new ServiceException("该用户不需要补充账号");
        } else if (managerId.equals(-1L)) {
            return 0;
//            throw new ServiceException("该用户不需要补充账号");
        }
        Long userId = customerServiceDetail.getUserId();
        Long maxLoad = customerServiceDetail.getMaxLoad();
        Long successNum = customerServiceDetail.getSuccessNum();
        String projectId = customerServiceDetail.getProjectId();
        Accounts accounts = new Accounts();
        accounts.setAssignedTo(userId);
        List<Accounts> accountList = accountsMapper.selectAccountsList(accounts);
        int nowNum = accountList.size();
        ProjectSet projectSet = projectSetMapper.selectProjectSetByProjectId(projectId);
        Long accountSendNum = projectSet.getAccountSendNum();
        long accountNum = 0;
        if (maxLoad % accountSendNum == 0) {
            accountNum = maxLoad / accountSendNum;
        } else {
            accountNum = maxLoad / accountSendNum + 1;
        }
        if (num > 0) {
            int i = allocation2User4NotEnough(customerServiceDetail, num);
            if ((i + nowNum) >= accountNum) {
                customerServiceDetail.setIsEnough(1);
                customerServiceDetailMapper.updateCustomerServiceDetail(customerServiceDetail);
            }
            return i;
        } else {
            if (accountNum > nowNum) {
                int i = allocation2User4NotEnough(customerServiceDetail, (int) (accountNum - nowNum));
                customerServiceDetail.setIsEnough(1);
                customerServiceDetailMapper.updateCustomerServiceDetail(customerServiceDetail);
                return i;
            } else {
                customerServiceDetail.setIsEnough(1);
                customerServiceDetailMapper.updateCustomerServiceDetail(customerServiceDetail);
            }
        }
        return 0;
    }

    @Transactional(rollbackFor = Exception.class)
    public int fillBan(CustomerServiceDetail customerServiceDetail, Integer num) {
        Long userId = customerServiceDetail.getUserId();
        String projectId = customerServiceDetail.getProjectId();
        Accounts accounts = new Accounts();
        accounts.setAssignedTo(userId);
        List<Accounts> accounts1 = accountsMapper.selectAccountsList(accounts);
//        HashSet<String> accountSendNum = new HashSet<>();
//        for (Accounts accounts2 : accounts1) {
//            String account = accounts2.getAccount();
//            accountSendNum.add(account);
//        }
        //这里注释的是如果封号的发过消息了就不补充了
//        List<TaskSendNum> taskSendNums = taskSendNumMapper.selectTaskSendNumListByIds(accountSendNum);
//        accountSendNum.clear();
//        for (TaskSendNum taskSendNum : taskSendNums) {
//            accountSendNum.add(taskSendNum.getTaskContentId());
//        }
        //ban 正常
        ArrayList<Accounts> banList = new ArrayList<>();
        if (num.equals(0)) {
            for (Accounts accounts2 : accounts1) {
                Long accStatus = accounts2.getAccStatus();
//            if (accStatus.equals(-1L) && !accountSendNum.contains(accounts2.getAccount())) {
                if (accStatus.equals(-1L)) {
                    banList.add(accounts2);
                }
            }
        }
        //4.9 补充指定个数//todo拿出指定个数

        //根据封禁的数量去补充几个账号 3.27弃用

        //这里释放掉手里被封禁的
//        if (!freeList.isEmpty()) {
//            accountsMapper.freeAccount(freeList);
//        }
//        return freeList.size();
        return allocation2User4BanNum(customerServiceDetail, num, banList);
    }


    public String getProjectIdByLoginUserId() {
        Long userId = SecurityUtils.getUserId();
        CustomerServiceDetail customerServiceDetail = customerServiceDetailServiceImpl.selectCustomerDetailByUserId(userId);
        if (ObjectUtils.isEmpty(customerServiceDetail)) {
            return null;
        } else {
            return customerServiceDetail.getProjectId();
        }
    }


//    @Transactional(rollbackFor = Exception.class)
//    public ArrayList<Accounts> allocation2User4Ban(CustomerServiceDetail customerServiceDetail, ArrayList<Accounts> banList) {
//        List<Accounts> accounts1 = accountsMapper.selectAccountsListNoAssigned(customerServiceDetail.getProjectId());
//        int currentIndex = 0;
//        ArrayList<Accounts> needUpdateAccount = new ArrayList<>();
//        ArrayList<Accounts> freeList = new ArrayList<>();
//        for (int i = 0; i < banList.size(); ) {
//            if (currentIndex >= accounts1.size()) {
//                //能补多少补多少
//                break;
//                //提示不足

    /// /                throw new ServiceException("账号剩余数量不足");
//            }
//            Accounts account = accounts1.get(currentIndex);
//            account.setAssignedTo(customerServiceDetail.getUserId());
//            currentIndex++;
//            needUpdateAccount.add(account);
//            freeList.add(banList.remove(i));
//        }
//        if (!needUpdateAccount.isEmpty()) {
//            accountsMapper.updateAccountsABatchllocation(needUpdateAccount);
//        }
//        return freeList;
//    }
    @Transactional(rollbackFor = Exception.class)
    public int allocation2User4BanNum(CustomerServiceDetail customerServiceDetail, Integer num, ArrayList<Accounts> banList) {
        List<Accounts> accounts1 = accountsMapper.selectAccountsListNoAssigned(customerServiceDetail.getProjectId(), num);
        if (accounts1.isEmpty()) {
            throw new ServiceException("账号不足，请上传账号");
        }
        int currentIndex = 0;
        if (num.equals(0)) {
            num = banList.size();
        }

        ArrayList<Accounts> needUpdateAccount = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            if (currentIndex >= num) {
                //能补多少补多少
                break;
                //提示不足
//                throw new ServiceException("账号剩余数量不足");
            }
            Accounts account = accounts1.get(currentIndex);
            account.setAssignedTo(customerServiceDetail.getUserId());
            currentIndex++;
            needUpdateAccount.add(account);
        }
        if (!needUpdateAccount.isEmpty()) {
            accountsMapper.updateAccountsABatchllocation(needUpdateAccount);
        }
        return num;
    }


    // 同步方法，使用 synchronized 关键字修饰
    @Transactional(rollbackFor = Exception.class)
    @Synchronized
    public int allocation2User4NotEnough(CustomerServiceDetail customerServiceDetail, Integer need) {
        // 这里是具体的分配逻辑，你可以根据实际需求实现
        //todo 如果此时项目单账号最大分配量设置值与生成此客服时设置值不一样 则会分配不对
        String projectId = customerServiceDetail.getProjectId();
        List<Accounts> accounts1 = accountsMapper.selectAccountsListNoAssigned(projectId, need);//todo拿出指定个数
        ProjectSet projectSet = projectSetMapper.selectProjectSetByProjectId(projectId);
        int currentIndex = 0;
        ArrayList<Accounts> needUpdateAccount = new ArrayList<>();

        for (int i = 0; i < need; i++) {
            if (currentIndex >= accounts1.size()) {
                // 如果 accounts1 列表元素不够分配，停止分配
                if (!customerServiceDetail.getManagerId().equals(-1L)) {
                    customerServiceDetail.setIsEnough(0);
                }
//                customerServiceDetail.setIsEnough(0);
                if (!needUpdateAccount.isEmpty()) {
                    accountsMapper.updateAccountsABatchllocation(needUpdateAccount);
                }
                return needUpdateAccount.size();
            }
            Accounts account = accounts1.get(currentIndex);
            account.setAssignedTo(customerServiceDetail.getUserId());
            currentIndex++;
            needUpdateAccount.add(account);
        }
        if (!needUpdateAccount.isEmpty()) {
            accountsMapper.updateAccountsABatchllocation(needUpdateAccount);
        }
        return needUpdateAccount.size();
    }

    public void inRecycleBin(HashSet<Long> userIdSet) {
        List<Accounts> accounts1 = accountsMapper.selectAccountsByAccountSet(userIdSet);
        if (!accounts1.isEmpty()) {
            Long[] accountIds = accounts1.stream().map(Accounts::getAccountId).toArray(Long[]::new);
            accountsMapper.deleteAccountsByAccountIds(accountIds);
        }
    }
}
