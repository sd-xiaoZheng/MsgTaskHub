package com.chat.ruoyichat.service.impl;

import com.chat.ruoyichat.domain.Accounts;
import com.chat.ruoyichat.domain.CustomerServiceDetail;
import com.chat.ruoyichat.domain.ProjectSet;
import com.chat.ruoyichat.domain.vo.CountResult;
import com.chat.ruoyichat.mapper.*;
import com.chat.ruoyichat.service.ICustomerServiceDetailService;
import com.chat.ruoyichat.websocket.WebSocketService;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.mapper.SysUserPostMapper;
import com.ruoyi.system.mapper.SysUserRoleMapper;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.system.service.impl.SysUserServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.ruoyi.common.utils.PageUtils.startPage;

/**
 * 客服号详情，用于存储客服号详细信息Service业务层处理
 *
 * @author ruoyi
 * &#064;date  2025-03-04
 */
@Service
public class CustomerServiceDetailServiceImpl implements ICustomerServiceDetailService {
    @Autowired
    private CustomerServiceDetailMapper customerServiceDetailMapper;
    @Autowired
    private SysUserServiceImpl userService;
    @Autowired
    private ISysRoleService roleService;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private AccountsMapper accountsMapper;
    @Autowired
    private ProjectSetMapper projectSetMapper;


    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private SysUserRoleMapper userRoleMapper;
    @Autowired
    private SysUserPostMapper userPostMapper;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private SessionRecordMapper sessionRecordMapper;
    @Autowired
    private TaskContentMapper taskContentMapper;
    @Autowired
    private TaskMapper taskMapper;

    /**
     * 查询客服号详情，用于存储客服号详细信息
     *
     * @param customerId 客服号详情，用于存储客服号详细信息主键
     * @return 客服号详情，用于存储客服号详细信息
     */
    @Override
    public CustomerServiceDetail selectCustomerServiceDetailByCustomerId(String customerId) {
        return customerServiceDetailMapper.selectCustomerServiceDetailByCustomerId(customerId);
    }

    /**
     * 查询客服号详情，用于存储客服号详细信息列表
     *
     * @param customerServiceDetail 客服号详情，用于存储客服号详细信息
     * @return 客服号详情，用于存储客服号详细信息
     */
    @Override
    public List<CustomerServiceDetail> selectCustomerServiceDetailList(CustomerServiceDetail customerServiceDetail) {
        List<CustomerServiceDetail> customerServiceDetails = customerServiceDetailMapper.selectCustomerServiceDetailList(customerServiceDetail);
        if (customerServiceDetails.isEmpty()) {
            return customerServiceDetails;
        }
        //出查询是否删除
        HashSet<Long> userIdSet4User = new HashSet<>();
        for (CustomerServiceDetail serviceDetail : customerServiceDetails) {
            userIdSet4User.add(serviceDetail.getUserId());
        }
        ArrayList<SysUser> sysUsers = sysUserMapper.selectUserByIdList(userIdSet4User);
        HashMap<Long, SysUser> userNoDel = new HashMap<>();
        for (SysUser sysUser : sysUsers) {
            String delFlag = sysUser.getDelFlag();
            if (delFlag.equals("0")) {
                userNoDel.put(sysUser.getUserId(), sysUser);
            }
        }

        HashMap<Long, List<CustomerServiceDetail>> groupMap = new HashMap<>();
        HashSet<Long> userIdSet = new HashSet<>();
        for (CustomerServiceDetail serviceDetail : customerServiceDetails) {
            SysUser sysUser = userNoDel.get(serviceDetail.getUserId());
            if (Objects.isNull(sysUser)) {
                continue;
            }
            Long managerId = serviceDetail.getManagerId();
            if (ObjectUtils.isEmpty(managerId) || managerId.equals(-1L)) {
                continue;
            }
            userIdSet.add(serviceDetail.getUserId());
            List<CustomerServiceDetail> customerServiceDetails1 = groupMap.get(managerId);
            if (ObjectUtils.isEmpty(customerServiceDetails1) || customerServiceDetails1.isEmpty()) {
                customerServiceDetails1 = new ArrayList<>();
            }
            customerServiceDetails1.add(serviceDetail);
            groupMap.put(managerId, customerServiceDetails1);
        }
        HashMap<Long, Integer> accountCount = new HashMap<>();//todo 小表驱动大表
        if (!userIdSet.isEmpty()) {
            List<Accounts> accounts1 = accountsMapper.selectAccountsByAccountSet(userIdSet);
            for (Accounts accounts : accounts1) {
                Integer i = accountCount.get(accounts.getAssignedTo());
                if (ObjectUtils.isEmpty(i)) {
                    accountCount.put(accounts.getAssignedTo(), 1);
                } else {
                    accountCount.put(accounts.getAssignedTo(), i + 1);
                }
            }
        }

        HashMap<Long, Long> successMap = getSuccessMap(userIdSet);
        ArrayList<CustomerServiceDetail> groupRsp = new ArrayList<>();
        for (CustomerServiceDetail serviceDetail : customerServiceDetails) {
            SysUser sysUser = userNoDel.get(serviceDetail.getUserId());
            if (Objects.isNull(sysUser)) {
                continue;
            }
            Long userId = serviceDetail.getUserId();
            List<CustomerServiceDetail> customerServiceDetails1 = groupMap.get(userId);
            if (ObjectUtils.isNotEmpty(customerServiceDetails1) && !customerServiceDetails1.isEmpty()) {
                long allSuccessNum = 0L;
                long allReplyNum = 0L;
                for (CustomerServiceDetail detail : customerServiceDetails1) {
                    Long userId1 = detail.getUserId();
                    Long sendNum = detail.getSendNum();
                    Long surplusNum = detail.getSurplusNum();
                    Long replyNum = detail.getReplyNum();
                    Long successNum = successMap.getOrDefault(detail.getUserId(), 0L);
//                    Long successNum = detail.getSuccessNum();
                    Long banNum = detail.getBanNum();
                    Integer accountNum = accountCount.get(userId1);
                    if (ObjectUtils.isEmpty(accountNum)) {
                        accountNum = 0;
                    }
                    detail.setAccountNum(accountNum);
                    if (successNum == 0L) {
                        detail.setReplyRate(0d);
                    } else {
                        detail.setSuccessNum(successNum);
                        double replyRate = (double) replyNum / successNum * 100;
                        // 格式化回复率，保留两位小数
                        double v = Double.parseDouble(String.format("%.2f", replyRate));
                        detail.setReplyRate(v);
                    }
                    Integer accountNum1 = serviceDetail.getAccountNum();
                    if (ObjectUtils.isEmpty(accountNum1)) {
                        serviceDetail.setAccountNum(accountNum);
                    } else {
                        serviceDetail.setAccountNum(accountNum + accountNum1);
                    }
                    serviceDetail.setSendNum(sendNum + serviceDetail.getSendNum());
                    serviceDetail.setSurplusNum(surplusNum + serviceDetail.getSurplusNum());
                    serviceDetail.setReplyNum(replyNum + serviceDetail.getReplyNum());
                    serviceDetail.setSuccessNum(serviceDetail.getSuccessNum() + successMap.getOrDefault(detail.getUserId(), 0L));
                    serviceDetail.setBanNum(banNum + serviceDetail.getBanNum());
                    allSuccessNum += successNum;
                    allReplyNum += replyNum;
                }

                if (allSuccessNum == 0L) {
                    serviceDetail.setReplyRate(0d);
                } else {
                    double replyRate = (double) allReplyNum / allSuccessNum * 100;
                    // 格式化回复率，保留两位小数
                    double v = Double.parseDouble(String.format("%.2f", replyRate));
                    serviceDetail.setReplyRate(v);
                }
                serviceDetail.setChildren(customerServiceDetails1);
                groupRsp.add(serviceDetail);
            }
        }
//        List<CustomerServiceDetail> groupRsp = customerServiceDetailMapper.selectCustomerServiceDetailOrder(customerServiceDetail);
        return groupRsp;
    }

    @Override
    public HashMap<Long, Long> getSuccessMap(HashSet<Long> userIdSet) {
        HashMap<Long, Long> resultMap = new HashMap<>();
        if (!userIdSet.isEmpty()) {
            List<CountResult> countResults = sessionRecordMapper.selectCountByUserIdSet(userIdSet);
            for (CountResult result : countResults) {
                resultMap.put(result.getItem(), result.getCount());
            }
            return resultMap;
        }
        return resultMap;
    }

    /**
     * 新增客服号详情，用于存储客服号详细信息
     *
     * @param customerServiceDetail 客服号详情，用于存储客服号详细信息
     * @return 结果
     */
    @Override
    public int insertCustomerServiceDetail(CustomerServiceDetail customerServiceDetail) {
        customerServiceDetail.setCreateTime(DateUtils.getNowDate());
        customerServiceDetail.setCustomerId(IdUtils.fastSimpleUUID());
        return customerServiceDetailMapper.insertCustomerServiceDetail(customerServiceDetail);
    }

    /**
     * 修改客服号详情，用于存储客服号详细信息
     *
     * @param customerServiceDetail 客服号详情，用于存储客服号详细信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateCustomerServiceDetail(CustomerServiceDetail customerServiceDetail) {
        Long userId = customerServiceDetail.getUserId();
        CustomerServiceDetail customerServiceDetail2 = customerServiceDetailMapper.selectCustomerDetailByUserId(userId);
        Long surplusNum = customerServiceDetail.getSurplusNum();
        Long maxLoad1 = customerServiceDetail.getMaxLoad();
        if (surplusNum > maxLoad1) {
            throw new ServiceException("最大数量小于已分配");
        }
        customerServiceDetail.setUpdateTime(DateUtils.getNowDate());
        Long managerId = customerServiceDetail.getManagerId();
        CustomerServiceDetail customerServiceDetail1 = customerServiceDetailMapper.selectCustomerDetailByUserId(managerId);

        Long maxLoad = customerServiceDetail1.getMaxLoad();
        Long maxLoad2 = customerServiceDetail2.getMaxLoad();

        customerServiceDetail1.setMaxLoad((maxLoad1 - maxLoad2) + maxLoad);
        customerServiceDetailMapper.updateCustomerServiceDetail(customerServiceDetail1);
        return customerServiceDetailMapper.updateCustomerServiceDetail(customerServiceDetail);
    }

    /**
     * 批量删除客服号详情，用于存储客服号详细信息
     *
     * @param userIds 需要删除的客服号详情，用于存储客服号详细信息主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteCustomerServiceDetailByCustomerIds(Long[] userIds) {
        HashSet<Long> userIdMap = new HashSet<>();
        Collections.addAll(userIdMap, userIds);

        List<CustomerServiceDetail> customerServiceDetails = customerServiceDetailMapper.selectCustomerDetailByUserIdList(userIdMap);
        HashSet<Long> groupLeaderId = new HashSet<>();
        HashSet<Long> groupId = new HashSet<>();

        for (CustomerServiceDetail customerServiceDetail : customerServiceDetails) {
            Long managerId = customerServiceDetail.getManagerId();
            if (managerId.equals(-1L)) {
                groupLeaderId.add(customerServiceDetail.getUserId());
            } else {
                groupId.add(customerServiceDetail.getUserId());
            }
        }
        if (!groupLeaderId.isEmpty() && !groupId.isEmpty()) {
            throw new ServiceException("组长组员不能同时选择");
        }

        int i = 0;
        if (!groupId.isEmpty()) {//只有组员
            if (userIds.length > 0) {
                HashSet<Long> userIdSet = new HashSet<>(Arrays.asList(userIds));
                List<CustomerServiceDetail> customerServiceDetails1 = customerServiceDetailMapper.selectCustomerDetailByUserIdList(userIdSet);
                HashMap<Long, Long> subNum = new HashMap<>();
                HashSet<Long> managerUserIdSet = new HashSet<>();
                for (CustomerServiceDetail customerServiceDetail : customerServiceDetails1) {
                    Long managerId = customerServiceDetail.getManagerId();
                    managerUserIdSet.add(managerId);
                    Long max = subNum.getOrDefault(managerId, 0L);
                    max += customerServiceDetail.getMaxLoad();
                    subNum.put(managerId, max);
                }

                List<CustomerServiceDetail> managers = customerServiceDetailMapper.selectCustomerDetailByUserIdList(managerUserIdSet);
                for (CustomerServiceDetail manager : managers) {
                    Long max = subNum.get(manager.getUserId());
                    manager.setMaxLoad(manager.getMaxLoad() - max);
                }
                customerServiceDetailMapper.updateCustomerServiceDetail2MaxLoadBatch(managers);
                i = customerServiceDetailMapper.deleteCustomerServiceDetailByuserIds(userIds);
                inRecycleBin(userIdMap);//账号入回收站
                //删除角色同时删除任务
                deleteTask(userIdMap);
            }
        } else if (!groupLeaderId.isEmpty()) {
            ArrayList<Long> userId = new ArrayList<>();
            for (Long s : groupLeaderId) {
                userId.add(s);
                List<CustomerServiceDetail> customerServiceDetails1 = customerServiceDetailMapper.selectCustomerDetailByManagerId(s);
                for (CustomerServiceDetail customerServiceDetail : customerServiceDetails1) {
                    Long userId1 = customerServiceDetail.getUserId();
                    userId.add(userId1);
                }
            }
            Object[] array = userId.toArray();
            Long[] customerId = new Long[array.length];
            for (int j = 0; j < customerId.length; j++) {
                customerId[j] = Long.parseLong(array[j].toString());
            }
            if (customerId.length > 0) {
                i = customerServiceDetailMapper.deleteCustomerServiceDetailByuserIds(customerId);
                userIdMap.clear();
                Collections.addAll(userIdMap, customerId);
                inRecycleBin(userIdMap);
                deleteTask(userIdMap);
            }
        } else {
            return 0;
        }
        return i;
    }

    private void deleteTask(HashSet<Long> userIdMap) {
        taskContentMapper.deleteByUserIdSet(userIdMap);
        taskMapper.deleteTaskByUserIdSet(userIdMap);
    }

    /**
     * 删除客服号详情，用于存储客服号详细信息信息
     *
     * @param customerId 客服号详情，用于存储客服号详细信息主键
     * @return 结果
     */
    @Override
    public int deleteCustomerServiceDetailByCustomerId(String customerId) {
        return customerServiceDetailMapper.deleteCustomerServiceDetailByCustomerId(customerId);
    }

    @Override
    public List<CustomerServiceDetail> selectCustomerDetailByUserIdList(HashSet<Long> userIds) {
        return customerServiceDetailMapper.selectCustomerDetailByUserIdList(userIds);
    }

    @Override
    public CustomerServiceDetail selectCustomerDetailByUserId(Long userId) {
        return customerServiceDetailMapper.selectCustomerDetailByUserId(userId);
    }

    @Override
    public List<CustomerServiceDetail> selectCustomerDetailByManagerId(Long managerId) {
        return customerServiceDetailMapper.selectCustomerDetailByManagerId(managerId);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArrayList<CustomerServiceDetail> addBatch(SysUser user, Long maxLoad) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SysUser sysUser1 = new SysUser();
        sysUser1.setUserName(user.getUserName());
        List<SysUser> sysUsers = sysUserMapper.selectUserList(sysUser1);
        if (!sysUsers.isEmpty()) {
            throw new ServiceException("前缀已被占用，请更换前缀");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword("rbs999.");
        }
        long timestamp = System.currentTimeMillis();
        Long userId = user.getUserId();
        Long[] role = {2L};
        Integer genNum = user.getGenNum();
        ArrayList<CustomerServiceDetail> CustomerServiceDetailList = new ArrayList<>();
        ProjectSet projectSet = projectSetMapper.selectProjectSetByProjectId(user.getProjectId());
        Long accountSendNum = projectSet.getAccountSendNum();
        Long customerRng = user.getCustomerRng();// 0顺序 1随机
        String leaderSuffix = user.getLeaderSuffix();
//        String leaderSuffix = projectSet.getLeaderSuffix();
        if (user.getIsManage().equals(1)) {
            //此处生成一个管理号
            SysUser adminUser = new SysUser();
            BeanUtils.copyBeanProp(adminUser, user);
            String adminName = null;
            if (StringUtils.isEmpty(leaderSuffix)) {
                leaderSuffix = projectSet.getLeaderSuffix();
                if (StringUtils.isEmpty(leaderSuffix)) {
                    adminName = user.getUserName() + "999";
                } else {
                    adminName = user.getUserName() + leaderSuffix;
                }
            } else {
                adminName = user.getUserName() + leaderSuffix;
            }
            adminUser.setUserName(adminName);
            adminUser.setNickName(adminName);
            adminUser.setRoleIds(role);
            adminUser.setPostIds(user.getPostIds());
            roleService.checkRoleDataScope(adminUser.getRoleIds());
            adminUser.setCreateBy(SecurityUtils.getUsername());
            adminUser.setPassword(SecurityUtils.encryptPassword(adminUser.getPassword()));
            userService.insertGroupLeader(adminUser, timestamp);
            timestamp++;
            userId = adminUser.getUserId();
            CustomerServiceDetail customerServiceDetailTemp =
                    new CustomerServiceDetail(user.getProjectId(), genNum * maxLoad, -1L, userId);
            customerServiceDetailTemp.setUserName(adminUser.getUserName());
            customerServiceDetailTemp.setIsEnough((int) (long) projectSet.getAccountSendNum());
            customerServiceDetailMapper.insertCustomerServiceDetail(customerServiceDetailTemp);
            CustomerServiceDetailList.add(customerServiceDetailTemp);
        } else {
            CustomerServiceDetail customerServiceDetail = customerServiceDetailMapper.selectCustomerDetailByUserId(userId);
            Long maxLoad1 = customerServiceDetail.getMaxLoad();
            customerServiceDetail.setMaxLoad(maxLoad1 + (genNum * maxLoad));
            customerServiceDetailMapper.updateCustomerServiceDetail(customerServiceDetail);
        }

        //批量生成客服号
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String pwdEncode = passwordEncoder.encode(user.getPassword());
        ArrayList<SysUser> sysUserList = new ArrayList<>();
        ArrayList<CustomerServiceDetail> CustomerServiceDetail = new ArrayList<>();

        for (int i = 0; i < genNum; i++) {
            StringBuilder nameNum = null;
            if (customerRng.equals(0L)) {
                //顺序
                int nameNumTemp = i + 1;
                nameNum = new StringBuilder(String.valueOf(nameNumTemp));
                // 根据数字的位数进行补零操作
                while (nameNum.length() < 3) {
                    nameNum.insert(0, "0");
                }
            } else {
                //随机
                StringBuilder code = new StringBuilder();
                Random random = new Random();
                for (int j = 0; j < 4; j++) {
                    int index = random.nextInt(characters.length());
                    code.append(characters.charAt(index));
                }
                nameNum = code;
            }
            SysUser customerUser = new SysUser();
            CustomerServiceDetail customerServiceDetailTemp = new CustomerServiceDetail(user.getProjectId(), maxLoad, userId, timestamp);
            BeanUtils.copyBeanProp(customerUser, user);
            customerUser.setUserId(timestamp);
            customerUser.setUserName(user.getUserName() + nameNum);
            customerUser.setNickName(user.getUserName() + nameNum);
            customerUser.setRoleIds(role);

            roleService.checkRoleDataScope(customerUser.getRoleIds());
            customerUser.setPassword(pwdEncode);
            customerUser.setCreateBy(SecurityUtils.getUsername());
            //设置客服的名称
            customerServiceDetailTemp.setUserName(customerUser.getUserName());
            sysUserList.add(customerUser);
            CustomerServiceDetail.add(customerServiceDetailTemp);
            timestamp++;
        }
        int allocation = 1;//TODO 后期查策略是直接分配到手里还是组长自己去分配，现在先直接分配到手里
        if (allocation == 1) {
            allocation2User(CustomerServiceDetail, user.getProjectId(), (int) ((maxLoad * genNum) / accountSendNum) + genNum);
        }
        userService.insertUserBatch(sysUserList);
        customerServiceDetailMapper.insertCustomerServiceDetailBatch(CustomerServiceDetail);

        CustomerServiceDetailList.addAll(CustomerServiceDetail);
        return CustomerServiceDetailList;
    }

    @Override
    public List<CustomerServiceDetail> leaderAllList(CustomerServiceDetail customerServiceDetail) {
        customerServiceDetail.setManagerId(-1L);
        String projectId = customerServiceDetail.getProjectId();
        if (StringUtils.isEmpty(projectId)) {
            customerServiceDetail.setProjectId(getProjectIdByLoginUserId());
        }
        return customerServiceDetailMapper.selectCustomerServiceDetailList(customerServiceDetail);
    }

    public String getProjectIdByLoginUserId() {
        Long userId = SecurityUtils.getUserId();
        CustomerServiceDetail customerServiceDetail = this.selectCustomerDetailByUserId(userId);
        if (ObjectUtils.isEmpty(customerServiceDetail)) {
            return null;
        } else {
            return customerServiceDetail.getProjectId();
        }
    }

    // 同步方法，使用 synchronized 关键字修饰
    @Transactional(rollbackFor = Exception.class)
    public synchronized void allocation2User(ArrayList<CustomerServiceDetail> detail, String projectId, Integer num) {
        // 这里是具体的分配逻辑，你可以根据实际需求实现
        List<Accounts> accounts1 = accountsMapper.selectAccountsListNoAssigned(projectId, num);//todo 不能把全部账号都拿出来
        ProjectSet projectSet = projectSetMapper.selectProjectSetByProjectId(projectId);
        Long accountSendNum = projectSet.getAccountSendNum();
        int currentIndex = 0;
        ArrayList<Accounts> needUpdateAccount = new ArrayList<>();
        loop:
        for (CustomerServiceDetail customerServiceDetail : detail) {
            Long maxLoad = customerServiceDetail.getMaxLoad();
            long accountNum = 0;
            if (maxLoad % accountSendNum == 0) {
                accountNum = maxLoad / accountSendNum;
            } else {
                accountNum = maxLoad / accountSendNum + 1;
            }
            for (int i = 0; i < accountNum; i++) {
                if (currentIndex >= accounts1.size()) {
                    // 如果 accounts1 列表元素不够分配，停止分配
                    customerServiceDetail.setIsEnough(0);
                    break loop;
                }
                Accounts account = accounts1.get(currentIndex);
                account.setAssignedTo(customerServiceDetail.getUserId());
                currentIndex++;
                needUpdateAccount.add(account);
            }
            customerServiceDetail.setIsEnough(1);
        }
        if (!needUpdateAccount.isEmpty()) {
            accountsMapper.updateAccountsABatchllocation(needUpdateAccount);
        }
    }

    public void inRecycleBin(HashSet<Long> userIdSet) {
        List<Accounts> accounts1 = accountsMapper.selectAccountsByAccountSet(userIdSet);
        if (!accounts1.isEmpty()) {
            Long[] accountIds = accounts1.stream().map(Accounts::getAccountId).toArray(Long[]::new);
            accountsMapper.deleteAccountsByAccountIds(accountIds);
        }
    }

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteUserByIds(Long[] userIds) {
        for (Long userId : userIds) {
            checkUserAllowed(new SysUser(userId));
            checkUserDataScope(userId);
        }
        HashSet<Long> userIdMap = new HashSet<>();
        Collections.addAll(userIdMap, userIds);
        inRecycleBin(userIdMap);
        deleteTask(userIdMap);
        for (Long userId : userIds) {
            WebSocketService.userSessionMap.remove(userId);
        }
        // 删除用户与角色关联
        userRoleMapper.deleteUserRole(userIds);
        // 删除用户与岗位关联
        userPostMapper.deleteUserPost(userIds);
        return userMapper.deleteUserByIds(userIds);
    }

    /**
     * 校验用户是否允许操作
     *
     * @param user 用户信息
     */
    public void checkUserAllowed(SysUser user) {
        if (StringUtils.isNotNull(user.getUserId()) && user.isAdmin()) {
            throw new ServiceException("不允许操作超级管理员用户");
        }
    }

    /**
     * 校验用户是否有数据权限
     *
     * @param userId 用户id
     */
    public void checkUserDataScope(Long userId) {
        if (!SysUser.isAdmin(SecurityUtils.getUserId())) {
            SysUser user = new SysUser();
            user.setUserId(userId);
            List<SysUser> users = sysUserService.selectUserList(user);
            if (StringUtils.isEmpty(users)) {
                throw new ServiceException("没有权限访问用户数据！");
            }
        }
    }

}
