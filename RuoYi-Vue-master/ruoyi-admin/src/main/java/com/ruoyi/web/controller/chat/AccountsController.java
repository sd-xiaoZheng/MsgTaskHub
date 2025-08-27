package com.ruoyi.web.controller.chat;

import com.alibaba.fastjson.JSONObject;
import com.chat.ruoyichat.domain.Accounts;
import com.chat.ruoyichat.domain.dto.AccountStatus;
import com.chat.ruoyichat.domain.vo.AccountsStatus;
import com.chat.ruoyichat.service.IAccountsService;
import com.chat.ruoyichat.websocket.WebSocketService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 分配给的客服账号Controller
 *
 * @author ruoyi
 * @date 2025-03-14
 */
@RestController
@RequestMapping("/chat/Accounts")
public class AccountsController extends BaseController {
    @Autowired
    private IAccountsService accountsService;

    /**
     * 查询分配给客服的账号列表
     */
    @PreAuthorize("@ss.hasPermi('chat:Accounts:list')")
    @GetMapping("/list")
    public TableDataInfo list(Accounts accounts) {
        List<Accounts> list = accountsService.selectAccountsList(accounts);
        return getDataTable(list);
    }

    /**
     * 查询回收站
     */
    @PreAuthorize("@ss.hasPermi('chat:Accounts:list')")
    @GetMapping("/recycleList")
    public TableDataInfo recycleList(Accounts accounts) {
        startPage();
        List<Accounts> list = accountsService.selectAccountsRecycleList(accounts);
        return getDataTable(list);
    }


//    /**
//     * 导出分配给的客服账号列表
//     */
//    @PreAuthorize("@ss.hasPermi('chat:Accounts:export')")
//    @Log(title = "分配给的客服账号", businessType = BusinessType.EXPORT)
//    @PostMapping("/export")
//    public void export(HttpServletResponse response, Accounts accounts) {
//        List<Accounts> list = accountsService.selectAccountsList(accounts);
//        ExcelUtil<Accounts> util = new ExcelUtil<Accounts>(Accounts.class);
//        util.exportExcel(response, list, "分配给的客服账号数据");
//    }

    /**
     * 获取分配给的客服账号详细信息
     */
    @PreAuthorize("@ss.hasPermi('chat:Accounts:query')")
    @GetMapping(value = "/{accountId}")
    public AjaxResult getInfo(@PathVariable("accountId") Long accountId) {
        return success(accountsService.selectAccountsByAccountId(accountId));
    }

    /**
     * 新增分配给的客服账号
     */
    @PreAuthorize("@ss.hasPermi('chat:Accounts:add')")
    @Log(title = "分配给的客服账号", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Accounts accounts) {
        return toAjax(accountsService.insertAccounts(accounts));
    }

    /**
     * 修改分配给的客服账号
     */
    @PreAuthorize("@ss.hasPermi('chat:Accounts:edit')")
    @Log(title = "分配给的客服账号", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Accounts accounts) {
        return toAjax(accountsService.updateAccounts(accounts));
    }

    /**
     * 假删除分配给的客服账号
     */
    @PreAuthorize("@ss.hasPermi('chat:Accounts:remove')")
    @Log(title = "分配给的客服账号", businessType = BusinessType.DELETE)
    @DeleteMapping("/{accountIds}")
    public AjaxResult remove(@PathVariable Long[] accountIds) {
        return toAjax(accountsService.deleteAccountsByAccountIds(accountIds));
    }

    /**
     * 真删除分配给的客服账号
     */
    @PreAuthorize("@ss.hasPermi('chat:Accounts:remove')")
    @Log(title = "分配给的客服账号", businessType = BusinessType.DELETE)
    @DeleteMapping("/deleteAccountsByAccountIdsReal/{accountIds}")
    public AjaxResult deleteAccountsByAccountIdsReal(@PathVariable Long[] accountIds) {
        return toAjax(accountsService.deleteAccountsByAccountIdsReal(accountIds));
    }

    /**
     * 导入账号
     */
    @Log(title = "导入账号", businessType = BusinessType.IMPORT)
    @PostMapping("/importAccount")
    public AjaxResult importAccount(MultipartFile file, String projectId) throws Exception {
        ArrayList<Accounts> accounts = accountsService.importAccount(file, projectId);
        if (accounts.isEmpty()) {
            return AjaxResult.success("导入成功");
        } else {
            return AjaxResult.success("该重复项不允许导入", accounts);
        }
    }


    /**
     * 导入账号txt
     */
    @Log(title = "导入账号txt", businessType = BusinessType.IMPORT)
    @PostMapping("/importAccountTxt")
    public AjaxResult importAccountTxt(MultipartFile file, String projectId) throws Exception {
        ArrayList<Accounts> accounts = accountsService.importAccountTxt(file, projectId);
        if (accounts.isEmpty()) {
            return AjaxResult.success("导入成功");
        } else {
            return AjaxResult.success("该重复项不允许导入", accounts);
        }
    }


    /**
     * 补号 弃用
     */
    @Log(title = "补充账号", businessType = BusinessType.INSERT)
    @PostMapping("/fillAccount")
    public AjaxResult fillAccount(Long userId, Integer num) throws Exception {
        String message = accountsService.fillAccount(userId, num);
        String[] split = message.split(":");
        return AjaxResult.success("已补" + split[1]);
//        return AjaxResult.success("已补封禁：" + split[0] + "；已补不足" + split[1]);
    }

    /**
     * 批量补号 ， 组长客服不能同时选择
     */
    @Log(title = "批量补号", businessType = BusinessType.DELETE)
    @PostMapping("/supplement/{customerIds}")
    public AjaxResult supplement(@PathVariable Long[] customerIds, Integer num) {
        String message = accountsService.supplement(customerIds, num);
        String[] split = message.split(";");
        Long userId = SecurityUtils.getUserId();
        HashMap<Long, Session> userSessionMap = WebSocketService.userSessionMap;
        Session session = userSessionMap.get(userId);
        JSONObject jsonObject = new JSONObject();
        JSONObject data = new JSONObject();
        jsonObject.put("flag", 4);
        data.put("pace", 100);
        data.put("userId", userId);
        jsonObject.put("data", data);
        WebSocketService.SendMessage(session, jsonObject.toString());
        return AjaxResult.success("已补" + split[1]);
    }


    /**
     * 离职
     */
    @Log(title = "离职", businessType = BusinessType.INSERT)
    @PostMapping("/dimission")
    public AjaxResult dimission(Long userId) throws Exception {
        String message = accountsService.dimission(userId);
        return AjaxResult.success(message);
    }


    /**
     * 回收账号
     */
    @Log(title = "回收账号", businessType = BusinessType.INSERT)
    @PostMapping("/recycleAccount")
    public AjaxResult recycleAccount(String projectId) {
        Long userId = SecurityUtils.getUserId();
        String message = accountsService.recycleAccount(projectId);
        HashMap<Long, Session> userSessionMap = WebSocketService.userSessionMap;
        Session session = userSessionMap.get(userId);
        JSONObject jsonObject = new JSONObject();
        JSONObject data = new JSONObject();
        jsonObject.put("flag", 3);
        data.put("pace", 100);
        data.put("userId", userId);
        jsonObject.put("data", data);
        WebSocketService.SendMessage(session, jsonObject.toString());
        return AjaxResult.success(message);
    }

    /**
     * 项目账号状态
     */
    @Log(title = "项目账号状态", businessType = BusinessType.INSERT)
    @PostMapping("/accountStatus")
    public AjaxResult accountStatus(String projectId) {
        AccountStatus accountStatus = accountsService.accountStatus(projectId);
        return AjaxResult.success(accountStatus);
    }

    @Log(title = "批量勾选回收账号", businessType = BusinessType.INSERT)
    @GetMapping("/recycleAccountBatch/{accountIds}")
    public AjaxResult recycleAccountBatch(@PathVariable Long[] accountIds) {
        String message = accountsService.recycleAccountBatch(accountIds);
        return AjaxResult.success(message);
    }

    /**
     * 清空账号
     */
    @Log(title = "清空账号", businessType = BusinessType.INSERT)
    @PostMapping("/emptyAccount")
    public AjaxResult emptyAccount(String projectId, Long isAll) {
        accountsService.emptyAccount(projectId, isAll);
        return AjaxResult.success();
    }


    /**
     * 手动设置封不封号
     */
    @Log(title = "手动设置封不封号", businessType = BusinessType.INSERT)
    @PostMapping("/Unblocking")
    public AjaxResult Unblocking(@RequestBody AccountsStatus accountsStatus) {
        Integer status = accountsStatus.getStatus();
        ArrayList<Accounts> accountId = accountsStatus.getAccounts();
        accountsService.Unblocking(accountId, status);
        return AjaxResult.success();
    }

    @Log(title = "导出账号", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Accounts accounts, Integer exportNum) {
        List<Accounts> list = accountsService.selectAccountsList4Export(accounts, exportNum);
        ExcelUtil<Accounts> util = new ExcelUtil<Accounts>(Accounts.class);
        util.exportExcel(response, list, "账号数据");
    }

    @Log(title = "重置帐号数据", businessType = BusinessType.UPDATE)
    @PostMapping("/resetAccount/{accountIds}")
    public AjaxResult resetAccount(@PathVariable Long[] accountIds) {
        accountsService.resetAccount(accountIds);
        return AjaxResult.success();
    }
}
