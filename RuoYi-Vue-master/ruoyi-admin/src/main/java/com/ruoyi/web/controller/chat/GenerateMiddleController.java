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
import com.chat.ruoyichat.domain.GenerateMiddle;
import com.chat.ruoyichat.service.IGenerateMiddleService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * GenerateMiddleController
 *
 * @author ruoyi
 * @date 2025-06-20
 */
@RestController
@RequestMapping("/chat/GenerateMiddle")
public class GenerateMiddleController extends BaseController {
    @Autowired
    private IGenerateMiddleService generateMiddleService;

    /**
     * 查询GenerateMiddle列表
     */
    @PreAuthorize("@ss.hasPermi('chat:GenerateMiddle:list')")
    @GetMapping("/list")
    public TableDataInfo list(GenerateMiddle generateMiddle) {
        List<GenerateMiddle> list = generateMiddleService.selectGenerateMiddleList(generateMiddle);
        return getDataTable(list);
    }

    /**
     * 导出GenerateMiddle列表
     */
    @PreAuthorize("@ss.hasPermi('chat:GenerateMiddle:export')")
    @Log(title = "GenerateMiddle", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GenerateMiddle generateMiddle) {
        List<GenerateMiddle> list = generateMiddleService.selectGenerateMiddleList(generateMiddle);
        ExcelUtil<GenerateMiddle> util = new ExcelUtil<GenerateMiddle>(GenerateMiddle.class);
        util.exportExcel(response, list, "GenerateMiddle数据");
    }

    /**
     * 获取GenerateMiddle详细信息
     */
    @PreAuthorize("@ss.hasPermi('chat:GenerateMiddle:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(generateMiddleService.selectGenerateMiddleById(id));
    }

    /**
     * 新增GenerateMiddle
     */
    @PreAuthorize("@ss.hasPermi('chat:GenerateMiddle:add')")
    @Log(title = "GenerateMiddle", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GenerateMiddle generateMiddle) {
        if (generateMiddleService.insertGenerateMiddle(generateMiddle)
                > 0) {
            return AjaxResult.success(generateMiddle.getId());
        } else {
            return AjaxResult.error();
        }
    }

    /**
     * 修改GenerateMiddle
     */
    @PreAuthorize("@ss.hasPermi('chat:GenerateMiddle:edit')")
    @Log(title = "GenerateMiddle", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GenerateMiddle generateMiddle) {
        return toAjax(generateMiddleService.updateGenerateMiddle(generateMiddle));
    }

    /**
     * 删除GenerateMiddle
     */
    @PreAuthorize("@ss.hasPermi('chat:GenerateMiddle:remove')")
    @Log(title = "GenerateMiddle", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(generateMiddleService.deleteGenerateMiddleByIds(ids));
    }
}
