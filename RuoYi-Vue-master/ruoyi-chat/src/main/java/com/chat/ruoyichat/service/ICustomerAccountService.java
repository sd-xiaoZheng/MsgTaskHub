package com.chat.ruoyichat.service;

import java.util.List;
import com.chat.ruoyichat.domain.CustomerAccount;

/**
 * 客户账号，用于存储客户账号相关信息Service接口
 * 
 * @author ruoyi
 * @date 2025-03-04
 */
public interface ICustomerAccountService 
{
    /**
     * 查询客户账号，用于存储客户账号相关信息
     * 
     * @param accountId 客户账号，用于存储客户账号相关信息主键
     * @return 客户账号，用于存储客户账号相关信息
     */
    public CustomerAccount selectCustomerAccountByAccountId(String accountId);

    /**
     * 查询客户账号，用于存储客户账号相关信息列表
     * 
     * @param customerAccount 客户账号，用于存储客户账号相关信息
     * @return 客户账号，用于存储客户账号相关信息集合
     */
    public List<CustomerAccount> selectCustomerAccountList(CustomerAccount customerAccount);

    /**
     * 新增客户账号，用于存储客户账号相关信息
     * 
     * @param customerAccount 客户账号，用于存储客户账号相关信息
     * @return 结果
     */
    public int insertCustomerAccount(CustomerAccount customerAccount);

    /**
     * 修改客户账号，用于存储客户账号相关信息
     * 
     * @param customerAccount 客户账号，用于存储客户账号相关信息
     * @return 结果
     */
    public int updateCustomerAccount(CustomerAccount customerAccount);

    /**
     * 批量删除客户账号，用于存储客户账号相关信息
     * 
     * @param accountIds 需要删除的客户账号，用于存储客户账号相关信息主键集合
     * @return 结果
     */
    public int deleteCustomerAccountByAccountIds(String[] accountIds);

    /**
     * 删除客户账号，用于存储客户账号相关信息信息
     * 
     * @param accountId 客户账号，用于存储客户账号相关信息主键
     * @return 结果
     */
    public int deleteCustomerAccountByAccountId(String accountId);
}
