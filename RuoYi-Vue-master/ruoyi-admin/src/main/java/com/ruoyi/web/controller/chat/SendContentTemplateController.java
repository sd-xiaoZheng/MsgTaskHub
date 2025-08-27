package com.ruoyi.web.controller.chat;

import com.chat.ruoyichat.domain.SendContentTemplate;
import com.chat.ruoyichat.service.ISendContentTemplateService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 发送内容模板Controller
 * 
 * @author ruoyi
 * @date 2025-03-05
 */
@RestController
@RequestMapping("/chat/SendContentTemplate")
public class SendContentTemplateController extends BaseController
{
    @Autowired
    private ISendContentTemplateService sendContentTemplateService;

    /**
     * 查询发送内容模板，用于存储发送内容模板相关信息列表
     */
    @PreAuthorize("@ss.hasPermi('chat:SendContentTemplate:list')")
    @GetMapping("/list")
    public TableDataInfo list(SendContentTemplate sendContentTemplate)
    {
        startPage();
        List<SendContentTemplate> list = sendContentTemplateService.selectSendContentTemplateList(sendContentTemplate);
        return getDataTable(list);
    }

    /**
     * 导出发送内容模板，用于存储发送内容模板相关信息列表
     */
    @PreAuthorize("@ss.hasPermi('chat:SendContentTemplate:export')")
    @Log(title = "发送内容模板，用于存储发送内容模板相关信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SendContentTemplate sendContentTemplate)
    {
        List<SendContentTemplate> list = sendContentTemplateService.selectSendContentTemplateList(sendContentTemplate);
        ExcelUtil<SendContentTemplate> util = new ExcelUtil<SendContentTemplate>(SendContentTemplate.class);
        util.exportExcel(response, list, "发送内容模板，用于存储发送内容模板相关信息数据");
    }

    /**
     * 获取发送内容模板，用于存储发送内容模板相关信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('chat:SendContentTemplate:query')")
    @GetMapping(value = "/{templateId}")
    public AjaxResult getInfo(@PathVariable("templateId") String templateId)
    {
        return success(sendContentTemplateService.selectSendContentTemplateByTemplateId(templateId));
    }

    /**
     * 新增发送内容模板，用于存储发送内容模板相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:SendContentTemplate:add')")
    @Log(title = "发送内容模板，用于存储发送内容模板相关信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SendContentTemplate sendContentTemplate)
    {
        return toAjax(sendContentTemplateService.insertSendContentTemplate(sendContentTemplate));
    }

    /**
     * 修改发送内容模板，用于存储发送内容模板相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:SendContentTemplate:edit')")
    @Log(title = "发送内容模板，用于存储发送内容模板相关信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SendContentTemplate sendContentTemplate)
    {
        return toAjax(sendContentTemplateService.updateSendContentTemplate(sendContentTemplate));
    }

    /**
     * 删除发送内容模板，用于存储发送内容模板相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:SendContentTemplate:remove')")
    @Log(title = "发送内容模板，用于存储发送内容模板相关信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{templateIds}")
    public AjaxResult remove(@PathVariable String[] templateIds)
    {
        return toAjax(sendContentTemplateService.deleteSendContentTemplateByTemplateIds(templateIds));
    }

    /**
     * 任务模板
     */
    @Log(title = "导入模板", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public AjaxResult importData(MultipartFile file, String projectId,String templateName) throws Exception
    {
        String operName = SecurityUtils.getUsername();
        String message = sendContentTemplateService.importContentList(file, operName,projectId,templateName);
        return AjaxResult.success(message);
    }


    /**
     * 查询所有发送内容模板
     */
    @PreAuthorize("@ss.hasPermi('chat:SendContentTemplate:list')")
    @GetMapping("/listAll")
    public TableDataInfo listAll(SendContentTemplate sendContentTemplate)
    {
        List<SendContentTemplate> list = sendContentTemplateService.selectSendContentTemplateListTemp(sendContentTemplate);
//        List<SendContentTemplate> list = sendContentTemplateService.selectSendContentTemplateList(sendContentTemplate);
        return getDataTable(list);
    }


    /**
     * 导入模板
     */
    @Log(title = "导入模板", businessType = BusinessType.IMPORT)
    @PostMapping("/importPrivate")
    public AjaxResult importPrivate(MultipartFile file,String templateName) throws Exception
    {
        String templateId= sendContentTemplateService.importPrivate(file,templateName);
        return AjaxResult.success(templateId);
    }

    /**
     * 重置模板次数
     */
    @PreAuthorize("@ss.hasPermi('chat:SendContentTemplate:edit')")
    @GetMapping("/resetByTemplateId")
    public AjaxResult resetByTemplateId(String templateId)
    {
        sendContentTemplateService.resetByTemplateId(templateId);
        return AjaxResult.success("重置成功");
    }


    /**
     * 清空群发模板
     */
    @Log(title = "清空群发模板", businessType = BusinessType.DELETE)
    @PostMapping("/empty")
    public AjaxResult empty(String projectId){
        String message = sendContentTemplateService.emptyByProject(projectId);
        return AjaxResult.success(message);
    }

    /**
     * 按模板删除
     */
    @Log(title = "清空群发模板", businessType = BusinessType.DELETE)
    @PostMapping("/deleteByTemplate")
    public AjaxResult deleteByTemplate(String templateId){
        String message = sendContentTemplateService.deleteByTemplate(templateId);
        return AjaxResult.success(message);
    }
}
