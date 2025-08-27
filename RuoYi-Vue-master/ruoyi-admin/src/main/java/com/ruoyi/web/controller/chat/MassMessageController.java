package com.ruoyi.web.controller.chat;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.utils.SecurityUtils;
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
import com.chat.ruoyichat.domain.MassMessage;
import com.chat.ruoyichat.service.IMassMessageService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 群发消息，用于存储群发消息相关信息Controller
 *
 * @author ruoyi
 * @date 2025-03-05
 */
@RestController
@RequestMapping("/chat/MassMessage")
public class MassMessageController extends BaseController {
    @Autowired
    private IMassMessageService massMessageService;

    /**
     * 查询群发消息，用于存储群发消息相关信息列表
     */
    @PreAuthorize("@ss.hasPermi('chat:MassMessage:list')")
    @GetMapping("/list")
    public TableDataInfo list(MassMessage massMessage) {
        startPage();
        List<MassMessage> list = massMessageService.selectMassMessageList(massMessage);
        return getDataTable(list);
    }

    /**
     * 导出群发消息，用于存储群发消息相关信息列表
     */
    @PreAuthorize("@ss.hasPermi('chat:MassMessage:export')")
    @Log(title = "群发消息，用于存储群发消息相关信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MassMessage massMessage) {
        List<MassMessage> list = massMessageService.selectMassMessageList(massMessage);
        ExcelUtil<MassMessage> util = new ExcelUtil<MassMessage>(MassMessage.class);
        util.exportExcel(response, list, "群发消息，用于存储群发消息相关信息数据");
    }

    /**
     * 获取群发消息，用于存储群发消息相关信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('chat:MassMessage:query')")
    @GetMapping(value = "/{messageId}")
    public AjaxResult getInfo(@PathVariable("messageId") String messageId) {
        return success(massMessageService.selectMassMessageByMessageId(messageId));
    }

    /**
     * 新增群发消息，用于存储群发消息相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:MassMessage:add')")
    @Log(title = "群发消息，用于存储群发消息相关信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MassMessage massMessage) {
        return toAjax(massMessageService.insertMassMessage(massMessage));
    }

    /**
     * 修改群发消息，用于存储群发消息相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:MassMessage:edit')")
    @Log(title = "群发消息，用于存储群发消息相关信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MassMessage massMessage) {
        return toAjax(massMessageService.updateMassMessage(massMessage));
    }

    /**
     * 删除群发消息，用于存储群发消息相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:MassMessage:remove')")
    @Log(title = "群发消息，用于存储群发消息相关信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{messageIds}")
    public AjaxResult remove(@PathVariable String[] messageIds) {
        return toAjax(massMessageService.deleteMassMessageByMessageIds(messageIds));
    }
}
