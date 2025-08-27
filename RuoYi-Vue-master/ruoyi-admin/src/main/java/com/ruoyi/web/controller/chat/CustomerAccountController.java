package com.ruoyi.web.controller.chat;

import com.chat.ruoyichat.domain.CustomerAccount;
import com.chat.ruoyichat.service.ICustomerAccountService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 客户账号，用于存储客户账号相关信息Controller
 *
 * @author ruoyi
 * @date 2025-03-04
 */
@RestController
@RequestMapping("/chat/CustomerAccount")
public class CustomerAccountController extends BaseController
{
    @Autowired
    private ICustomerAccountService customerAccountService;

    /**
     * 查询客户账号，用于存储客户账号相关信息列表
     */
    @PreAuthorize("@ss.hasPermi('chat:CustomerAccount:list')")
    @GetMapping("/list")
    public TableDataInfo list(CustomerAccount customerAccount)
    {
        startPage();
        List<CustomerAccount> list = customerAccountService.selectCustomerAccountList(customerAccount);
        return getDataTable(list);
    }

    /**
     * 导出客户账号，用于存储客户账号相关信息列表
     */
    @PreAuthorize("@ss.hasPermi('chat:CustomerAccount:export')")
    @Log(title = "客户账号，用于存储客户账号相关信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CustomerAccount customerAccount)
    {
        List<CustomerAccount> list = customerAccountService.selectCustomerAccountList(customerAccount);
        ExcelUtil<CustomerAccount> util = new ExcelUtil<CustomerAccount>(CustomerAccount.class);
        util.exportExcel(response, list, "客户账号，用于存储客户账号相关信息数据");
    }

    /**
     * 获取客户账号，用于存储客户账号相关信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('chat:CustomerAccount:query')")
    @GetMapping(value = "/{accountId}")
    public AjaxResult getInfo(@PathVariable("accountId") String accountId)
    {
        return success(customerAccountService.selectCustomerAccountByAccountId(accountId));
    }

    /**
     * 新增客户账号，用于存储客户账号相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:CustomerAccount:add')")
    @Log(title = "客户账号，用于存储客户账号相关信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CustomerAccount customerAccount)
    {
        return toAjax(customerAccountService.insertCustomerAccount(customerAccount));
    }

    /**
     * 修改客户账号，用于存储客户账号相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:CustomerAccount:edit')")
    @Log(title = "客户账号，用于存储客户账号相关信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CustomerAccount customerAccount)
    {
        return toAjax(customerAccountService.updateCustomerAccount(customerAccount));
    }

    /**
     * 删除客户账号，用于存储客户账号相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:CustomerAccount:remove')")
    @Log(title = "客户账号，用于存储客户账号相关信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{accountIds}")
    public AjaxResult remove(@PathVariable String[] accountIds)
    {
        return toAjax(customerAccountService.deleteCustomerAccountByAccountIds(accountIds));
    }
}
