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
import com.chat.ruoyichat.domain.PresetReply;
import com.chat.ruoyichat.service.IPresetReplyService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 预设回复，用于存储预设回复相关信息Controller
 * 
 * @author ruoyi
 * @date 2025-03-05
 */
@RestController
@RequestMapping("/chat/PresetReply")
public class PresetReplyController extends BaseController
{
    @Autowired
    private IPresetReplyService presetReplyService;

    /**
     * 查询预设回复，用于存储预设回复相关信息列表
     */
    @PreAuthorize("@ss.hasPermi('chat:PresetReply:list')")
    @GetMapping("/list")
    public TableDataInfo list(PresetReply presetReply)
    {
        startPage();
        List<PresetReply> list = presetReplyService.selectPresetReplyList(presetReply);
        return getDataTable(list);
    }


    /**
     * 查询预设回复 根据权重查6条
     */
    @PreAuthorize("@ss.hasPermi('chat:PresetReply:list')")
    @GetMapping("/listCount")
    public AjaxResult listCount(PresetReply presetReply)
    {
        return AjaxResult.success(presetReplyService.listCount(presetReply));
    }

    /**
     * 查询预设回复，用于存储预设回复相关信息列表
     */
    @PreAuthorize("@ss.hasPermi('chat:PresetReply:list')")
    @GetMapping("/randomText")
    public AjaxResult randomText(PresetReply presetReply)
    {
        String text = presetReplyService.randomText(presetReply);
        return AjaxResult.success(text);
    }

    /**
     * 导出预设回复，用于存储预设回复相关信息列表
     */
    @PreAuthorize("@ss.hasPermi('chat:PresetReply:export')")
    @Log(title = "预设回复，用于存储预设回复相关信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PresetReply presetReply)
    {
        List<PresetReply> list = presetReplyService.selectPresetReplyList(presetReply);
        ExcelUtil<PresetReply> util = new ExcelUtil<PresetReply>(PresetReply.class);
        util.exportExcel(response, list, "预设回复，用于存储预设回复相关信息数据");
    }

    /**
     * 获取预设回复，用于存储预设回复相关信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('chat:PresetReply:query')")
    @GetMapping(value = "/{replyId}")
    public AjaxResult getInfo(@PathVariable("replyId") String replyId)
    {
        return success(presetReplyService.selectPresetReplyByReplyId(replyId));
    }

    /**
     * 新增预设回复，用于存储预设回复相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:PresetReply:add')")
    @Log(title = "预设回复，用于存储预设回复相关信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PresetReply presetReply)
    {
        return toAjax(presetReplyService.insertPresetReply(presetReply));
    }

    /**
     * 修改预设回复，用于存储预设回复相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:PresetReply:edit')")
    @Log(title = "预设回复，用于存储预设回复相关信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PresetReply presetReply)
    {
        return toAjax(presetReplyService.updatePresetReply(presetReply));
    }

    /**
     * 删除预设回复，用于存储预设回复相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:PresetReply:remove')")
    @Log(title = "预设回复，用于存储预设回复相关信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{replyIds}")
    public AjaxResult remove(@PathVariable String[] replyIds)
    {
        return toAjax(presetReplyService.deletePresetReplyByReplyIds(replyIds));
    }


    /**
     * 删除预设回复，用于存储预设回复相关信息
     */
    @Log(title = "预设回复", businessType = BusinessType.DELETE)
    @PostMapping("/deleteByWeight")
    public AjaxResult deleteByWeight(Long weight,String projectId)
    {
        return toAjax(presetReplyService.deleteByWeight(weight,projectId));
    }


    /**
     * 预设回复导入
     */
    @Log(title = "导入任务", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public AjaxResult importData(MultipartFile file, String projectId, Long replyWeight, String title) throws Exception {
        String message = presetReplyService.importPresetReplyList(file, projectId, replyWeight, title);
        return AjaxResult.success(message);
    }


    /**
     * 清空预设回复
     */
    @Log(title = "清空预设回复", businessType = BusinessType.DELETE)
    @PostMapping("/empty")
    public AjaxResult empty(String projectId){
        String message = presetReplyService.emptyByProject(projectId);
        return AjaxResult.success(message);
    }
}
