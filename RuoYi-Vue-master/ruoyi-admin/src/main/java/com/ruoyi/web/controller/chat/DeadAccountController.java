package com.chat.ruoyichat.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.chat.ruoyichat.domain.DeadAccount;
import com.chat.ruoyichat.service.IDeadAccountService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 死号，用于存储死号相关信息Controller
 * 
 * @author ruoyi
 * @date 2025-03-05
 */
@RestController
@RequestMapping("/chat/DeadAccount")
public class DeadAccountController extends BaseController
{
    @Autowired
    private IDeadAccountService deadAccountService;

    /**
     * 查询死号，用于存储死号相关信息列表
     */
    @PreAuthorize("@ss.hasPermi('chat:DeadAccount:list')")
    @GetMapping("/list")
    public TableDataInfo list(DeadAccount deadAccount)
    {
        startPage();
        List<DeadAccount> list = deadAccountService.selectDeadAccountList(deadAccount);
        return getDataTable(list);
    }

    /**
     * 导出死号，用于存储死号相关信息列表
     */
    @PreAuthorize("@ss.hasPermi('chat:DeadAccount:export')")
    @Log(title = "死号，用于存储死号相关信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DeadAccount deadAccount)
    {
        List<DeadAccount> list = deadAccountService.selectDeadAccountList(deadAccount);
        ExcelUtil<DeadAccount> util = new ExcelUtil<DeadAccount>(DeadAccount.class);
        util.exportExcel(response, list, "死号，用于存储死号相关信息数据");
    }

    /**
     * 获取死号，用于存储死号相关信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('chat:DeadAccount:query')")
    @GetMapping(value = "/{deadId}")
    public AjaxResult getInfo(@PathVariable("deadId") String deadId)
    {
        return success(deadAccountService.selectDeadAccountByDeadId(deadId));
    }

    /**
     * 新增死号，用于存储死号相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:DeadAccount:add')")
    @Log(title = "死号，用于存储死号相关信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DeadAccount deadAccount)
    {
        return toAjax(deadAccountService.insertDeadAccount(deadAccount));
    }

    /**
     * 修改死号，用于存储死号相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:DeadAccount:edit')")
    @Log(title = "死号，用于存储死号相关信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DeadAccount deadAccount)
    {
        return toAjax(deadAccountService.updateDeadAccount(deadAccount));
    }

    /**
     * 删除死号，用于存储死号相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:DeadAccount:remove')")
    @Log(title = "死号，用于存储死号相关信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{deadIds}")
    public AjaxResult remove(@PathVariable String[] deadIds)
    {
        return toAjax(deadAccountService.deleteDeadAccountByDeadIds(deadIds));
    }
}
