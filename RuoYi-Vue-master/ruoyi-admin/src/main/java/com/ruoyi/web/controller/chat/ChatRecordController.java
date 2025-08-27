package com.ruoyi.web.controller.chat;

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
import com.chat.ruoyichat.domain.ChatRecord;
import com.chat.ruoyichat.service.IChatRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 聊天记录，用于存储聊天记录相关信息Controller
 * 
 * @author ruoyi
 * @date 2025-03-05
 */
@RestController
@RequestMapping("/chat/ChatRecord")
public class ChatRecordController extends BaseController
{
    @Autowired
    private IChatRecordService chatRecordService;

    /**
     * 查询聊天记录，用于存储聊天记录相关信息列表
     */
    @PreAuthorize("@ss.hasPermi('chat:ChatRecord:list')")
    @GetMapping("/list")
    public TableDataInfo list(ChatRecord chatRecord)
    {
//        startPage();
        List<ChatRecord> list = chatRecordService.selectChatRecordList(chatRecord);
        return getDataTable(list);
    }

    /**
     * 导出聊天记录，用于存储聊天记录相关信息列表
     */
    @PreAuthorize("@ss.hasPermi('chat:ChatRecord:export')")
    @Log(title = "聊天记录，用于存储聊天记录相关信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ChatRecord chatRecord)
    {
        List<ChatRecord> list = chatRecordService.selectChatRecordList(chatRecord);
        ExcelUtil<ChatRecord> util = new ExcelUtil<ChatRecord>(ChatRecord.class);
        util.exportExcel(response, list, "聊天记录，用于存储聊天记录相关信息数据");
    }

    /**
     * 获取聊天记录，用于存储聊天记录相关信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('chat:ChatRecord:query')")
    @GetMapping(value = "/{chatId}")
    public AjaxResult getInfo(@PathVariable("chatId") String chatId)
    {
        return success(chatRecordService.selectChatRecordByChatId(chatId));
    }

    /**
     * 新增聊天记录，用于存储聊天记录相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:ChatRecord:add')")
    @Log(title = "聊天记录，用于存储聊天记录相关信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ChatRecord chatRecord)
    {
        return toAjax(chatRecordService.insertChatRecord(chatRecord));
    }

    /**
     * 修改聊天记录，用于存储聊天记录相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:ChatRecord:edit')")
    @Log(title = "聊天记录，用于存储聊天记录相关信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ChatRecord chatRecord)
    {
        return toAjax(chatRecordService.updateChatRecord(chatRecord));
    }

    /**
     * 删除聊天记录，用于存储聊天记录相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:ChatRecord:remove')")
    @Log(title = "聊天记录，用于存储聊天记录相关信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{chatIds}")
    public AjaxResult remove(@PathVariable String[] chatIds)
    {
        return toAjax(chatRecordService.deleteChatRecordByChatIds(chatIds));
    }
}
