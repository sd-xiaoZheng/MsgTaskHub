package com.chat.ruoyichat.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.chat.ruoyichat.domain.Accounts;
import com.chat.ruoyichat.domain.dto.AccountStatus;
import org.springframework.web.multipart.MultipartFile;

/**
 * 分配给的客服账号Service接口
 *
 * @author ruoyi
 * @date 2025-03-14
 */
public interface IAccountsService {
    /**
     * 查询分配给的客服账号
     *
     * @param accountId 分配给的客服账号主键
     * @return 分配给的客服账号
     */
    public Accounts selectAccountsByAccountId(Long accountId);

    /**
     * 查询分配给的客服账号列表
     *
     * @param accounts 分配给的客服账号
     * @return 分配给的客服账号集合
     */
    public List<Accounts> selectAccountsList(Accounts accounts);

    List<Accounts> selectAccountsRecycleList(Accounts accounts);

    /**
     * 新增分配给的客服账号
     *
     * @param accounts 分配给的客服账号
     * @return 结果
     */
    public int insertAccounts(Accounts accounts);

    /**
     * 修改分配给的客服账号
     *
     * @param accounts 分配给的客服账号
     * @return 结果
     */
    public int updateAccounts(Accounts accounts);

    /**
     * 批量假删除分配给的客服账号
     *
     * @param accountIds 需要删除的分配给的客服账号主键集合
     * @return 结果
     */
    public int deleteAccountsByAccountIds(Long[] accountIds);

    /**
     * 批量删除分配给的客服账号
     *
     * @param accountIds 需要删除的分配给的客服账号主键集合
     * @return 结果
     */
    public int deleteAccountsByAccountIdsReal(Long[] accountIds);

    /**
     * 删除分配给的客服账号信息
     *
     * @param accountId 分配给的客服账号主键
     * @return 结果
     */
    public int deleteAccountsByAccountId(Long accountId);

    ArrayList<Accounts> importAccount(MultipartFile file, String projectId) throws IOException;

    ArrayList<Accounts> importAccountTxt(MultipartFile file, String projectId) throws IOException;

    String fillAccount(Long userId, Integer num);

    String dimission(Long userId);

    String recycleAccount(String projectId);

    AccountStatus accountStatus(String projectId);

    String recycleAccountBatch(Long[] accountIds);

    void emptyAccount(String projectId, Long isAll);

    String supplement(Long[] customerIds, Integer num);

    List<Accounts> selectAccountsList4Export(Accounts accounts, Integer exportNum);

    void Unblocking(List<Accounts> accounts, Integer status);

    void resetAccount(Long[] accountIds);
}
