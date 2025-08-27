package com.chat.ruoyichat.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.chat.ruoyichat.domain.Accounts;
import org.apache.ibatis.annotations.Param;

/**
 * 分配给的客服账号Mapper接口
 *
 * @author ruoyi
 * @date 2025-03-14
 */
public interface AccountsMapper {
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

    public List<Accounts> selectAccountsRecycleList(Accounts accounts);

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
     * 删除分配给的客服账号
     *
     * @param accountId 分配给的客服账号主键
     * @return 结果
     */
    public int deleteAccountsByAccountId(Long accountId);

    /**
     * 批量入回收站
     *
     * @param accountIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAccountsByAccountIds(Long[] accountIds);

    /**
     * 批量删除分配给的客服账号
     *
     * @param accountIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAccountsByAccountIdsReal(Long[] accountIds);

    void insertImportBatch(List<Accounts> importAccountList);

    List<Accounts> selectAccountsListNoAssigned(@Param("projectId") String projectId, @Param("num") Integer num);

    void updateAccountsABatchllocation(ArrayList<Accounts> needUpdateAccount);

    void freeAccount(ArrayList<Accounts> banList);

    List<Accounts> selectAccountsListByprojectDel(@Param("project") String project, @Param("batchSize") int batchSize);

    void updateAccountsRecycleAccount(List<Accounts> accounts);

    int recycleAccountBatch(List<Long> list);

    Accounts selectAccountsByAccount(String taskContentId);

    List<Accounts> selectAccountsListNoBan(Accounts accounts);

    List<Accounts> selectAccountsByAccountSet(HashSet<Long> userIdList);

    void emptyAccount(String projectId);

    void emptyAccountNoAssigned(String projectId);

    void emptyAccountNoBan(String projectId);

    void emptyAccountDustbin(String projectId);

    List<Accounts> selectAccountsListIsAssigned(String projectId);

    List<Accounts> isRepeat(List<Accounts> importAccountList);

    int selectAccountsListByprojectDelCount(String projectId);

    List<Accounts> selectAccountsListIsBan(@Param("userIdSet") HashSet<Long> userIdSet);

    int selectAccountsCountByUserID(@Param("userId") Long userId);

    List<Accounts> selectAccountsByAccountSetAndStatus(@Param("longs") HashSet<Long> longs,@Param("status") Long status);

    void Unblocking(@Param("accounts") List<Accounts> accounts,@Param("status") Integer status);

    List<Accounts> selectAccountByIds(@Param("accountIds") Long[] accountIds);

    void resetAccount(@Param("accounts") List<Accounts> accounts);
}
