package com.ruoyi.web.controller.chat;

import com.chat.ruoyichat.domain.CustomerServiceDetail;
import com.chat.ruoyichat.domain.TaskContent;
import com.chat.ruoyichat.domain.vo.CustomerTaskStatusVo;
import com.chat.ruoyichat.mapper.CustomerServiceDetailMapper;
import com.chat.ruoyichat.mapper.SessionRecordMapper;
import com.chat.ruoyichat.service.ICustomerServiceDetailService;
import com.chat.ruoyichat.service.ITaskContentService;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.ISysUserService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 客服号详情，用于存储客服号详细信息Controller
 *
 * @author ruoyi
 * @date 2025-03-04
 */
@RestController
@RequestMapping("/chat/CustomerServiceDetail")
public class CustomerServiceDetailController extends BaseController {
    @Autowired
    private ICustomerServiceDetailService customerServiceDetailService;
    @Autowired
    private CustomerServiceDetailMapper customerServiceDetailMapper;
    @Autowired
    private ITaskContentService taskContentService;
    @Autowired
    private SysUserMapper sysUserMapper;

    @GetMapping("/test")
    public TableDataInfo test(CustomerServiceDetail customerServiceDetail) {
//        startPage();
        List<CustomerServiceDetail> list = customerServiceDetailMapper.selectCustomerServiceDetailOrder(customerServiceDetail);
        return getDataTable(list);
    }

    /**
     * 查询客服号详情，用于存储客服号详细信息列表
     */
    @PreAuthorize("@ss.hasPermi('chat:CustomerServiceDetail:list')")
    @GetMapping("/list")
    public TableDataInfo list(CustomerServiceDetail customerServiceDetail) {
//        startPage();
        List<CustomerServiceDetail> list = customerServiceDetailService.selectCustomerServiceDetailList(customerServiceDetail);
        return getDataTable(list);
    }

    /**
     * 导出客服号详情，用于存储客服号详细信息列表
     */
    @PreAuthorize("@ss.hasPermi('chat:CustomerServiceDetail:export')")
    @Log(title = "客服号详情，用于存储客服号详细信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CustomerServiceDetail customerServiceDetail) {
        List<CustomerServiceDetail> list = customerServiceDetailService.selectCustomerServiceDetailList(customerServiceDetail);
        ExcelUtil<CustomerServiceDetail> util = new ExcelUtil<CustomerServiceDetail>(CustomerServiceDetail.class);
        util.exportExcel(response, list, "客服号详情，用于存储客服号详细信息数据");
    }

    /**
     * 获取客服号详情，用于存储客服号详细信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('chat:CustomerServiceDetail:query')")
    @GetMapping(value = "/{customerId}")
    public AjaxResult getInfo(@PathVariable("customerId") String customerId) {
        return success(customerServiceDetailService.selectCustomerServiceDetailByCustomerId(customerId));
    }

    /**
     * 新增客服号详情，用于存储客服号详细信息
     */
    @PreAuthorize("@ss.hasPermi('chat:CustomerServiceDetail:add')")
    @Log(title = "客服号详情，用于存储客服号详细信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CustomerServiceDetail customerServiceDetail) {
        return toAjax(customerServiceDetailService.insertCustomerServiceDetail(customerServiceDetail));
    }

    /**
     * 修改客服号详情，用于存储客服号详细信息
     */
    @PreAuthorize("@ss.hasPermi('chat:CustomerServiceDetail:edit')")
    @Log(title = "客服号详情，用于存储客服号详细信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CustomerServiceDetail customerServiceDetail) {
        return toAjax(customerServiceDetailService.updateCustomerServiceDetail(customerServiceDetail));
    }

    /**
     * 删除客服号详情，用于存储客服号详细信息
     */
    @PreAuthorize("@ss.hasPermi('chat:CustomerServiceDetail:remove')")
    @Log(title = "客服号详情，用于存储客服号详细信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{customerIds}")
    public AjaxResult remove(@PathVariable Long[] customerIds) {
        return toAjax(customerServiceDetailService.deleteCustomerServiceDetailByCustomerIds(customerIds));
    }

    /**
     * 删除用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/removeUser/{userIds}")
    public AjaxResult removeUser(@PathVariable Long[] userIds) {
        if (ArrayUtils.contains(userIds, getUserId())) {
            return error("当前用户不能删除");
        }
        return toAjax(customerServiceDetailService.deleteUserByIds(userIds));
    }


    /**
     * 通过任务id查询客服任务执行状态
     * 弃用
     */
    @PreAuthorize("@ss.hasPermi('chat:CustomerServiceDetail:list')")
    @GetMapping("/taskStatusByTaskId")
    public TableDataInfo taskStatusByTaskId(TaskContent taskContent) {
        List<TaskContent> taskContents = taskContentService.selectTaskContentListByTaskId(taskContent);
        //通过taskContents拿出所有的用户id 根据用户id查用户详情
        HashSet<Long> userID = new HashSet<>();
        for (TaskContent content : taskContents) {
            userID.add(content.getAssignedTo());
        }
        startPage();
        ArrayList<SysUser> sysUsers = sysUserMapper.selectUserByIdList(userID);
        //总返回
        CustomerTaskStatusVo customerTaskStatusVo = new CustomerTaskStatusVo();
        customerTaskStatusVo.setStatusName("1正常，2停用");
        List<CustomerServiceDetail> customerServiceDetails =
                customerServiceDetailService.selectCustomerDetailByUserIdList(userID);
        HashMap<Long, CustomerServiceDetail> longDetailsMap = new HashMap<>();
        for (CustomerServiceDetail customerServiceDetail : customerServiceDetails) {
            longDetailsMap.put(customerServiceDetail.getUserId(), customerServiceDetail);
        }
        //组装每个详情
        ArrayList<CustomerTaskStatusVo> customerTaskStatusVoList = customerTaskStatusVo.getCustomerTaskStatusVoList();

        for (SysUser sysUser : sysUsers) {
            CustomerTaskStatusVo customerTaskStatusVo1 = new CustomerTaskStatusVo();
            customerTaskStatusVo1.setStatus(sysUser.getStatus());
            customerTaskStatusVo1.setNickName(sysUser.getNickName());
            String status = sysUser.getStatus();
            if (status.equals("1")) {
                customerTaskStatusVo1.setStatusName("正常");
            } else if (status.equals("2")) {
                customerTaskStatusVo1.setStatusName("停用");
            }
            customerTaskStatusVo1.setCreateTime(sysUser.getCreateTime());
            CustomerServiceDetail customerServiceDetail = longDetailsMap.get(sysUser.getUserId());
            Long sendNum1 = customerServiceDetail.getSendNum();
            Long maxLoad1 = customerServiceDetail.getMaxLoad();
            Long surplusNum1 = customerServiceDetail.getSurplusNum();
            Long replyNum1 = customerServiceDetail.getReplyNum();
            Long successNum1 = customerServiceDetail.getSuccessNum();

//            customerTaskStatusVo1.setSendNum(sendNum1);
            customerTaskStatusVo1.setMaxLoad(maxLoad1);
            customerTaskStatusVo1.setSurplusNum(surplusNum1);
            customerTaskStatusVo1.setReplyNum(replyNum1);
            customerTaskStatusVo1.setSuccessNum(successNum1);
            if (surplusNum1.equals(0L)) {
                customerTaskStatusVo1.setReplyRate(0d);
            } else {
                double replyRate = (double) replyNum1 / successNum1 * 100;
                // 格式化回复率，保留两位小数
                double v = Double.parseDouble(String.format("%.2f", replyRate));
                customerTaskStatusVo.setReplyRate(v);
            }

            customerTaskStatusVoList.add(customerTaskStatusVo1);

        }
        customerTaskStatusVo.setCustomerTaskStatusVoList(customerTaskStatusVoList);
        return getDataTable(customerTaskStatusVoList);
    }


    /**
     * 查询用户任务状态
     */
    @PreAuthorize("@ss.hasPermi('chat:CustomerServiceDetail:list')")
    @GetMapping("/taskStatusByUserId")
    public TableDataInfo taskStatusByUserId() {
        Long userId = SecurityUtils.getUserId();
        CustomerServiceDetail customerServiceDetail =
                customerServiceDetailService.selectCustomerDetailByUserId(userId);
        if (ObjectUtils.isEmpty(customerServiceDetail)) {
            return getDataTable(new ArrayList<>());
        }
        Long managerId = customerServiceDetail.getManagerId();
        ArrayList<CustomerTaskStatusVo> customerTaskStatusVos = new ArrayList<>();
        if (managerId.equals(-1L)) {
            //是组长查所有组员
            HashSet<Long> userIdMap = new HashSet<>();
            HashMap<Long, CustomerServiceDetail> longCustomerMap = new HashMap<>();
            startPage();
            List<CustomerServiceDetail> customerServiceDetails =
                    customerServiceDetailMapper.selectCustomerDetailByManagerId(userId);
            PageInfo pages = new PageInfo(customerServiceDetails);
            long total = pages.getTotal();

            for (CustomerServiceDetail serviceDetail : customerServiceDetails) {
                Long userId1 = serviceDetail.getUserId();
                longCustomerMap.put(userId1, serviceDetail);
                userIdMap.add(userId1);
            }
            if (userIdMap.isEmpty()) {
                return getDataTable(customerTaskStatusVos);
            }
            ArrayList<SysUser> sysUsers = sysUserMapper.selectUserByIdList(userIdMap);
            HashSet<Long> userIdSet = new HashSet<>();
            for (SysUser sysUser : sysUsers) {
                CustomerTaskStatusVo customerTaskStatusVo = new CustomerTaskStatusVo();
                customerTaskStatusVo.setUserId(sysUser.getUserId());
                customerTaskStatusVo.setNickName(sysUser.getNickName());
                customerTaskStatusVo.setStatus(sysUser.getStatus());
                String status = sysUser.getStatus();
                if (status.equals("0")) {
                    customerTaskStatusVo.setStatusName("正常");
                } else if (status.equals("1")) {
                    customerTaskStatusVo.setStatusName("停用");
                }
                customerTaskStatusVo.setCreateTime(sysUser.getCreateTime());

                CustomerServiceDetail customerServiceDetail1 = longCustomerMap.get(sysUser.getUserId());
                Long sendNum = customerServiceDetail1.getSendNum();
                Long maxLoad = customerServiceDetail1.getMaxLoad();
                Long surplusNum = customerServiceDetail1.getSurplusNum();
                Long replyNum = customerServiceDetail1.getReplyNum();
                Long successNum = customerServiceDetail1.getSuccessNum();

                customerTaskStatusVo.setSendNum(sendNum);
                customerTaskStatusVo.setMaxLoad(maxLoad);
                customerTaskStatusVo.setSurplusNum(surplusNum);
                customerTaskStatusVo.setReplyNum(replyNum);
                customerTaskStatusVo.setSuccessNum(successNum);

                customerTaskStatusVos.add(customerTaskStatusVo);
                userIdSet.add(customerTaskStatusVo.getUserId());
            }
            HashMap<Long, Long> userCount = customerServiceDetailService.getSuccessMap(userIdSet);
            for (CustomerTaskStatusVo customerTaskStatusVo : customerTaskStatusVos) {
                if (Objects.nonNull(userCount) && !userCount.isEmpty()) {
                    Long l = userCount.get(customerTaskStatusVo.getUserId());
                    if (Objects.nonNull(l)) {
                        customerTaskStatusVo.setSuccessNum(l);
                    } else {
                        customerTaskStatusVo.setSuccessNum(0L);
                    }
                } else {
                    customerTaskStatusVo.setSuccessNum(0L);
//                    continue;
                }

                if (customerTaskStatusVo.getReplyNum().equals(0L)) {
                    customerTaskStatusVo.setReplyRate(0d);
                } else {
                    if (customerTaskStatusVo.getSuccessNum().equals(0L)) {
                        customerTaskStatusVo.setReplyRate(0d);
                    } else {
                        double replyRate = (double) customerTaskStatusVo.getReplyNum() / customerTaskStatusVo.getSuccessNum() * 100;
                        // 格式化回复率，保留两位小数
                        double v = Double.parseDouble(String.format("%.2f", replyRate));
                        customerTaskStatusVo.setReplyRate(v);
                    }
                }
//                customerTaskStatusVo.setSuccessNum(userCount.get(customerTaskStatusVo.getUserId()));
            }
            TableDataInfo dataTable = getDataTable(customerTaskStatusVos);
            dataTable.setTotal(total);
            return dataTable;
        } else {
            //不是组长查自己
            Long userId1 = customerServiceDetail.getUserId();
            HashSet<Long> userIdSet2getSuccess = new HashSet<>();
            userIdSet2getSuccess.add(userId1);
            HashMap<Long, Long> userCount = customerServiceDetailService.getSuccessMap(userIdSet2getSuccess);
            SysUser sysUser = sysUserMapper.selectUserById(userId1);
            CustomerTaskStatusVo customerTaskStatusVo = new CustomerTaskStatusVo();
            customerTaskStatusVo.setUserId(userId1);
            customerTaskStatusVo.setNickName(sysUser.getNickName());
            customerTaskStatusVo.setStatus(sysUser.getStatus());
            customerTaskStatusVo.setCreateTime(sysUser.getCreateTime());
            String status = sysUser.getStatus();
            if (status.equals("0")) {
                customerTaskStatusVo.setStatusName("正常");
            } else if (status.equals("1")) {
                customerTaskStatusVo.setStatusName("停用");
            }

            Long sendNum = customerServiceDetail.getSendNum();
            Long maxLoad = customerServiceDetail.getMaxLoad();
            Long surplusNum = customerServiceDetail.getSurplusNum();
            Long replyNum = customerServiceDetail.getReplyNum();
            Long successNum = 0L;
            if (Objects.nonNull(userCount) && !userCount.isEmpty()) {
                successNum = userCount.get(userId1);
            }


            customerTaskStatusVo.setSendNum(sendNum);
            customerTaskStatusVo.setMaxLoad(maxLoad);
            customerTaskStatusVo.setSurplusNum(surplusNum);
            customerTaskStatusVo.setReplyNum(replyNum);
            customerTaskStatusVo.setSuccessNum(successNum);

            if (successNum.equals(0L)) {
                customerTaskStatusVo.setReplyRate(0d);
            } else {
                if (successNum.equals(0L)) {
                    customerTaskStatusVo.setReplyRate(0d);
                } else {
                    double replyRate = (double) replyNum / successNum * 100;
                    // 格式化回复率，保留两位小数
                    double v = Double.parseDouble(String.format("%.2f", replyRate));
                    customerTaskStatusVo.setReplyRate(v);
                }
            }
            if (Objects.nonNull(userCount) && !userCount.isEmpty()) {
                customerTaskStatusVo.setSuccessNum(userCount.get(customerTaskStatusVo.getUserId()));
            } else {
                customerTaskStatusVo.setSuccessNum(0L);
            }
            customerTaskStatusVos.add(customerTaskStatusVo);
            return getDataTable(customerTaskStatusVos);
        }
    }

    /**
     * 批量生成客服
     */
    @PreAuthorize("@ss.hasPermi('chat:CustomerServiceDetail:add')")
    @Log(title = "批量添加客服", businessType = BusinessType.INSERT)
    @PostMapping("/addBatch")
    public AjaxResult addBatch(@Validated @RequestBody SysUser user) {
        return AjaxResult.success(customerServiceDetailService.addBatch(user, user.getMaxLoad()));
    }


    /**
     * 查询客服号详情，用于存储客服号详细信息列表
     */
    @PreAuthorize("@ss.hasPermi('chat:CustomerServiceDetail:list')")
    @GetMapping("/leaderAllList")
    public AjaxResult leaderAllList(CustomerServiceDetail customerServiceDetail) {
        List<CustomerServiceDetail> list = customerServiceDetailService.leaderAllList(customerServiceDetail);
        return AjaxResult.success(list);
    }
}
