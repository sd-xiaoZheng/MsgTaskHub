package com.chat.ruoyichat.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import com.chat.ruoyichat.domain.CustomerServiceDetail;
import com.ruoyi.common.core.domain.entity.SysUser;

/**
 * 客服号详情，用于存储客服号详细信息Service接口
 * 
 * @author ruoyi
 * @date 2025-03-04
 */
public interface ICustomerServiceDetailService 
{
    /**
     * 查询客服号详情，用于存储客服号详细信息
     * 
     * @param customerId 客服号详情，用于存储客服号详细信息主键
     * @return 客服号详情，用于存储客服号详细信息
     */
    public CustomerServiceDetail selectCustomerServiceDetailByCustomerId(String customerId);

    /**
     * 查询客服号详情，用于存储客服号详细信息列表
     * 
     * @param customerServiceDetail 客服号详情，用于存储客服号详细信息
     * @return 客服号详情，用于存储客服号详细信息集合
     */
    public List<CustomerServiceDetail> selectCustomerServiceDetailList(CustomerServiceDetail customerServiceDetail);

    /**
     * 新增客服号详情，用于存储客服号详细信息
     * 
     * @param customerServiceDetail 客服号详情，用于存储客服号详细信息
     * @return 结果
     */
    public int insertCustomerServiceDetail(CustomerServiceDetail customerServiceDetail);

    /**
     * 修改客服号详情，用于存储客服号详细信息
     * 
     * @param customerServiceDetail 客服号详情，用于存储客服号详细信息
     * @return 结果
     */
    public int updateCustomerServiceDetail(CustomerServiceDetail customerServiceDetail);

    /**
     * 批量删除客服号详情，用于存储客服号详细信息
     * 
     * @param customerIds 需要删除的客服号详情，用于存储客服号详细信息主键集合
     * @return 结果
     */
    public int deleteCustomerServiceDetailByCustomerIds(Long[] customerIds);

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    public int deleteUserByIds(Long[] userIds);


    /**
     * 删除客服号详情，用于存储客服号详细信息信息
     * 
     * @param customerId 客服号详情，用于存储客服号详细信息主键
     * @return 结果
     */
    public int deleteCustomerServiceDetailByCustomerId(String customerId);

    List<CustomerServiceDetail> selectCustomerDetailByUserIdList(HashSet<Long> userIds);

    CustomerServiceDetail selectCustomerDetailByUserId(Long userId);

    List<CustomerServiceDetail> selectCustomerDetailByManagerId(Long managerId);

    /**
     * 批量生成用户
     *
     * @param user
     * @param maxLoad
     * @return
     */
    ArrayList<CustomerServiceDetail> addBatch(SysUser user, Long maxLoad);

    List<CustomerServiceDetail> leaderAllList(CustomerServiceDetail customerServiceDetail);

    HashMap<Long, Long> getSuccessMap(HashSet<Long> userIdSet);

}
