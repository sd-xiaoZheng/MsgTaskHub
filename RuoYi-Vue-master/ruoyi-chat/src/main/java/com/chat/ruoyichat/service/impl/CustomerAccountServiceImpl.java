package com.chat.ruoyichat.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.chat.ruoyichat.mapper.CustomerAccountMapper;
import com.chat.ruoyichat.domain.CustomerAccount;
import com.chat.ruoyichat.service.ICustomerAccountService;

/**
 * 客户账号，用于存储客户账号相关信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-03-04
 */
@Service
public class CustomerAccountServiceImpl implements ICustomerAccountService 
{
    @Autowired
    private CustomerAccountMapper customerAccountMapper;

    /**
     * 查询客户账号，用于存储客户账号相关信息
     * 
     * @param accountId 客户账号，用于存储客户账号相关信息主键
     * @return 客户账号，用于存储客户账号相关信息
     */
    @Override
    public CustomerAccount selectCustomerAccountByAccountId(String accountId)
    {
        return customerAccountMapper.selectCustomerAccountByAccountId(accountId);
    }

    /**
     * 查询客户账号，用于存储客户账号相关信息列表
     * 
     * @param customerAccount 客户账号，用于存储客户账号相关信息
     * @return 客户账号，用于存储客户账号相关信息
     */
    @Override
    public List<CustomerAccount> selectCustomerAccountList(CustomerAccount customerAccount)
    {
        return customerAccountMapper.selectCustomerAccountList(customerAccount);
    }

    /**
     * 新增客户账号，用于存储客户账号相关信息
     * 
     * @param customerAccount 客户账号，用于存储客户账号相关信息
     * @return 结果
     */
    @Override
    public int insertCustomerAccount(CustomerAccount customerAccount)
    {
        customerAccount.setCreateTime(DateUtils.getNowDate());
        customerAccount.setAccountId(IdUtils.fastSimpleUUID());
        return customerAccountMapper.insertCustomerAccount(customerAccount);
    }

    /**
     * 修改客户账号，用于存储客户账号相关信息
     * 
     * @param customerAccount 客户账号，用于存储客户账号相关信息
     * @return 结果
     */
    @Override
    public int updateCustomerAccount(CustomerAccount customerAccount)
    {
        customerAccount.setUpdateTime(DateUtils.getNowDate());
        return customerAccountMapper.updateCustomerAccount(customerAccount);
    }

    /**
     * 批量删除客户账号，用于存储客户账号相关信息
     * 
     * @param accountIds 需要删除的客户账号，用于存储客户账号相关信息主键
     * @return 结果
     */
    @Override
    public int deleteCustomerAccountByAccountIds(String[] accountIds)
    {
        return customerAccountMapper.deleteCustomerAccountByAccountIds(accountIds);
    }

    /**
     * 删除客户账号，用于存储客户账号相关信息信息
     * 
     * @param accountId 客户账号，用于存储客户账号相关信息主键
     * @return 结果
     */
    @Override
    public int deleteCustomerAccountByAccountId(String accountId)
    {
        return customerAccountMapper.deleteCustomerAccountByAccountId(accountId);
    }
}
