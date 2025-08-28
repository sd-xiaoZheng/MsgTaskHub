package com.chat.ruoyichat.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import com.chat.ruoyichat.domain.CustomerServiceDetail;

/**
 * 客服号详情，用于存储客服号详细信息Mapper接口
 *
 * @author ruoyi
 * @date 2025-03-04
 */
public interface CustomerServiceDetailMapper
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
    public List<CustomerServiceDetail> selectCustomerServiceDetailOrder(CustomerServiceDetail customerServiceDetail);

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
     * 删除客服号详情，用于存储客服号详细信息
     *
     * @param customerId 客服号详情，用于存储客服号详细信息主键
     * @return 结果
     */
    public int deleteCustomerServiceDetailByCustomerId(String customerId);

    /**
     * 批量删除客服号详情，用于存储客服号详细信息
     *
     * @param customerIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCustomerServiceDetailByuserIds(Long[] customerIds);

    CustomerServiceDetail selectCustomerDetailByUserId(Long userId);

    void updateCustomerServiceDetail2SurplusNumBatch(List<CustomerServiceDetail> needUpdate);

    List<CustomerServiceDetail> selectCustomerDetailByUserIdList(HashSet<Long> userIds);

    List<CustomerServiceDetail> selectCustomerDetailByManagerId(Long managerId);

    void insertCustomerServiceDetailBatch(ArrayList<CustomerServiceDetail> customerServiceDetail);

    List<CustomerServiceDetail> selectCustomerServiceDetailList4getCustomerInfo();

    List<CustomerServiceDetail> selectCustomerServiceDetailByCustomerIds(String[] customerIds);

    void updateCustomerServiceemptyAccount(String projectId);

    List<CustomerServiceDetail> selectCustomerDetailByUserIdAndManager(HashSet<Long> userIdList);

    List<CustomerServiceDetail> selectCustomerDetailByManagerIdSet(HashSet<Long> managerId);

    void updateCustomerServiceDetail2ReplyNumKeyBatch(ArrayList<CustomerServiceDetail> customerServiceDetails);

    void updateCustomerServiceDetail2SuccessNumKeyBatch(ArrayList<CustomerServiceDetail> customerServiceDetails);

    void updateCustomerServiceDetail2SendNumKeyBatch(ArrayList<CustomerServiceDetail> customerServiceDetails);

    void updateCustomerServiceDetail2MaxLoadBatch(List<CustomerServiceDetail> managers);

    void updateCustomerServiceDetailAddBanNum(Long assignedTo);

    int updateSurplusNumByCustomerId(CustomerServiceDetail customerServiceDetail1);

    List<CustomerServiceDetail> selectCustomerDetailByUserIdListNoStatus(HashSet<Long> userIdSet);

    void deleteSysAll();

    void updateCustomerServiceDetailSub(Long userId);
}
