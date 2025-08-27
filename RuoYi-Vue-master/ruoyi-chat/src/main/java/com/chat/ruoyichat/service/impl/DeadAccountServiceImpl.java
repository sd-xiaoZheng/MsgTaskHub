package com.chat.ruoyichat.service.impl;

import java.util.List;

import com.ruoyi.common.utils.uuid.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.chat.ruoyichat.mapper.DeadAccountMapper;
import com.chat.ruoyichat.domain.DeadAccount;
import com.chat.ruoyichat.service.IDeadAccountService;

/**
 * 死号，用于存储死号相关信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-03-05
 */
@Service
public class DeadAccountServiceImpl implements IDeadAccountService 
{
    @Autowired
    private DeadAccountMapper deadAccountMapper;

    /**
     * 查询死号，用于存储死号相关信息
     * 
     * @param deadId 死号，用于存储死号相关信息主键
     * @return 死号，用于存储死号相关信息
     */
    @Override
    public DeadAccount selectDeadAccountByDeadId(String deadId)
    {
        return deadAccountMapper.selectDeadAccountByDeadId(deadId);
    }

    /**
     * 查询死号，用于存储死号相关信息列表
     * 
     * @param deadAccount 死号，用于存储死号相关信息
     * @return 死号，用于存储死号相关信息
     */
    @Override
    public List<DeadAccount> selectDeadAccountList(DeadAccount deadAccount)
    {
        return deadAccountMapper.selectDeadAccountList(deadAccount);
    }

    /**
     * 新增死号，用于存储死号相关信息
     * 
     * @param deadAccount 死号，用于存储死号相关信息
     * @return 结果
     */
    @Override
    public int insertDeadAccount(DeadAccount deadAccount)
    {
        deadAccount.setDeadId(IdUtils.fastSimpleUUID());
        return deadAccountMapper.insertDeadAccount(deadAccount);
    }

    /**
     * 修改死号，用于存储死号相关信息
     * 
     * @param deadAccount 死号，用于存储死号相关信息
     * @return 结果
     */
    @Override
    public int updateDeadAccount(DeadAccount deadAccount)
    {
        return deadAccountMapper.updateDeadAccount(deadAccount);
    }

    /**
     * 批量删除死号，用于存储死号相关信息
     * 
     * @param deadIds 需要删除的死号，用于存储死号相关信息主键
     * @return 结果
     */
    @Override
    public int deleteDeadAccountByDeadIds(String[] deadIds)
    {
        return deadAccountMapper.deleteDeadAccountByDeadIds(deadIds);
    }

    /**
     * 删除死号，用于存储死号相关信息信息
     * 
     * @param deadId 死号，用于存储死号相关信息主键
     * @return 结果
     */
    @Override
    public int deleteDeadAccountByDeadId(String deadId)
    {
        return deadAccountMapper.deleteDeadAccountByDeadId(deadId);
    }
}
