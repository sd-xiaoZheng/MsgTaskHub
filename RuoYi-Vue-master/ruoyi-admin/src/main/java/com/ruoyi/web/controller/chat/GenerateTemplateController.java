package com.ruoyi.web.controller.chat;

import com.chat.ruoyichat.domain.GenerateTemplate;
import com.chat.ruoyichat.service.IGenerateTemplateService;
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
 * GenerateTemplateController
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
@RestController
@RequestMapping("/chat/GenerateTemplate")
public class GenerateTemplateController extends BaseController
{
    @Autowired
    private IGenerateTemplateService generateTemplateService;

    /**
     * 查询GenerateTemplate列表
     */
    @PreAuthorize("@ss.hasPermi('chat:GenerateTemplate:list')")
    @GetMapping("/list")
    public TableDataInfo list(GenerateTemplate generateTemplate)
    {
        List<GenerateTemplate> list = generateTemplateService.selectGenerateTemplateList(generateTemplate);
        return getDataTable(list);
    }

    /**
     * 导出GenerateTemplate列表
     */
    @PreAuthorize("@ss.hasPermi('chat:GenerateTemplate:export')")
    @Log(title = "GenerateTemplate", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GenerateTemplate generateTemplate)
    {
        List<GenerateTemplate> list = generateTemplateService.selectGenerateTemplateList(generateTemplate);
        ExcelUtil<GenerateTemplate> util = new ExcelUtil<GenerateTemplate>(GenerateTemplate.class);
        util.exportExcel(response, list, "GenerateTemplate数据");
    }

    /**
     * 获取GenerateTemplate详细信息
     */
    @PreAuthorize("@ss.hasPermi('chat:GenerateTemplate:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(generateTemplateService.selectGenerateTemplateById(id));
    }

    /**
     * 新增GenerateTemplate
     */
    @PreAuthorize("@ss.hasPermi('chat:GenerateTemplate:add')")
    @Log(title = "GenerateTemplate", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GenerateTemplate generateTemplate)
    {
        return AjaxResult.success(generateTemplateService.insertGenerateTemplate(generateTemplate));
    }

    /**
     * 修改GenerateTemplate
     */
    @PreAuthorize("@ss.hasPermi('chat:GenerateTemplate:edit')")
    @Log(title = "GenerateTemplate", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GenerateTemplate generateTemplate)
    {
        return toAjax(generateTemplateService.updateGenerateTemplate(generateTemplate));
    }

    /**
     * 删除GenerateTemplate
     */
    @PreAuthorize("@ss.hasPermi('chat:GenerateTemplate:remove')")
    @Log(title = "GenerateTemplate", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(generateTemplateService.deleteGenerateTemplateByIds(ids));
    }
}
