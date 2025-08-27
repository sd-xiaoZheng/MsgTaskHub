package com.chat.ruoyichat.service;

import java.util.List;
import com.chat.ruoyichat.domain.DeadAccount;

/**
 * 死号，用于存储死号相关信息Service接口
 * 
 * @author ruoyi
 * @date 2025-03-05
 */
public interface IDeadAccountService 
{
    /**
     * 查询死号，用于存储死号相关信息
     * 
     * @param deadId 死号，用于存储死号相关信息主键
     * @return 死号，用于存储死号相关信息
     */
    public DeadAccount selectDeadAccountByDeadId(String deadId);

    /**
     * 查询死号，用于存储死号相关信息列表
     * 
     * @param deadAccount 死号，用于存储死号相关信息
     * @return 死号，用于存储死号相关信息集合
     */
    public List<DeadAccount> selectDeadAccountList(DeadAccount deadAccount);

    /**
     * 新增死号，用于存储死号相关信息
     * 
     * @param deadAccount 死号，用于存储死号相关信息
     * @return 结果
     */
    public int insertDeadAccount(DeadAccount deadAccount);

    /**
     * 修改死号，用于存储死号相关信息
     * 
     * @param deadAccount 死号，用于存储死号相关信息
     * @return 结果
     */
    public int updateDeadAccount(DeadAccount deadAccount);

    /**
     * 批量删除死号，用于存储死号相关信息
     * 
     * @param deadIds 需要删除的死号，用于存储死号相关信息主键集合
     * @return 结果
     */
    public int deleteDeadAccountByDeadIds(String[] deadIds);

    /**
     * 删除死号，用于存储死号相关信息信息
     * 
     * @param deadId 死号，用于存储死号相关信息主键
     * @return 结果
     */
    public int deleteDeadAccountByDeadId(String deadId);
}
