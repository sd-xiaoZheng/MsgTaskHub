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
import com.chat.ruoyichat.domain.SendContentTemplateContent;
import com.chat.ruoyichat.service.ISendContentTemplateContentService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 发送内容模板内容，用于存储发送内容模板的具体内容相关信息Controller
 * 
 * @author ruoyi
 * @date 2025-03-05
 */
@RestController
@RequestMapping("/chat/SendContentTemplateContent")
public class SendContentTemplateContentController extends BaseController
{
    @Autowired
    private ISendContentTemplateContentService sendContentTemplateContentService;

    /**
     * 查询发送内容模板内容，用于存储发送内容模板的具体内容相关信息列表
     */
    @PreAuthorize("@ss.hasPermi('chat:SendContentTemplateContent:list')")
    @GetMapping("/list")
    public TableDataInfo list(SendContentTemplateContent sendContentTemplateContent)
    {
        startPage();
        List<SendContentTemplateContent> list = sendContentTemplateContentService.selectSendContentTemplateContentList(sendContentTemplateContent);
        return getDataTable(list);
    }

    /**
     * 导出发送内容模板内容，用于存储发送内容模板的具体内容相关信息列表
     */
    @PreAuthorize("@ss.hasPermi('chat:SendContentTemplateContent:export')")
    @Log(title = "发送内容模板内容，用于存储发送内容模板的具体内容相关信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SendContentTemplateContent sendContentTemplateContent)
    {
        List<SendContentTemplateContent> list = sendContentTemplateContentService.selectSendContentTemplateContentList(sendContentTemplateContent);
        ExcelUtil<SendContentTemplateContent> util = new ExcelUtil<SendContentTemplateContent>(SendContentTemplateContent.class);
        util.exportExcel(response, list, "发送内容模板内容，用于存储发送内容模板的具体内容相关信息数据");
    }

    /**
     * 获取发送内容模板内容，用于存储发送内容模板的具体内容相关信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('chat:SendContentTemplateContent:query')")
    @GetMapping(value = "/{contentId}")
    public AjaxResult getInfo(@PathVariable("contentId") String contentId)
    {
        return success(sendContentTemplateContentService.selectSendContentTemplateContentByContentId(contentId));
    }

    /**
     * 新增发送内容模板内容，用于存储发送内容模板的具体内容相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:SendContentTemplateContent:add')")
    @Log(title = "发送内容模板内容，用于存储发送内容模板的具体内容相关信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SendContentTemplateContent sendContentTemplateContent)
    {
        return toAjax(sendContentTemplateContentService.insertSendContentTemplateContent(sendContentTemplateContent));
    }

    /**
     * 修改发送内容模板内容，用于存储发送内容模板的具体内容相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:SendContentTemplateContent:edit')")
    @Log(title = "发送内容模板内容，用于存储发送内容模板的具体内容相关信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SendContentTemplateContent sendContentTemplateContent)
    {
        return toAjax(sendContentTemplateContentService.updateSendContentTemplateContent(sendContentTemplateContent));
    }

    /**
     * 删除发送内容模板内容，用于存储发送内容模板的具体内容相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:SendContentTemplateContent:remove')")
    @Log(title = "发送内容模板内容，用于存储发送内容模板的具体内容相关信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{contentIds}")
    public AjaxResult remove(@PathVariable String[] contentIds)
    {
        return toAjax(sendContentTemplateContentService.deleteSendContentTemplateContentByContentIds(contentIds));
    }

    /**
     * 上传自己模板存入内存
     */
    @Log(title = "导入模板", businessType = BusinessType.IMPORT)
    @PostMapping("/importContentTemplateTemp")
    public AjaxResult importContentTemplateTemp(MultipartFile file) throws Exception
    {
        String redisKey= sendContentTemplateContentService.importContentTemplateTemp(file);
        return AjaxResult.success(redisKey);
    }
}
