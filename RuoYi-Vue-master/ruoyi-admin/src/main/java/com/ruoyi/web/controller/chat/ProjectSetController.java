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
import com.chat.ruoyichat.domain.ProjectSet;
import com.chat.ruoyichat.service.IProjectSetService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 项目设置Controller
 * 
 * @author ruoyi
 * @date 2025-03-14
 */
@RestController
@RequestMapping("/chat/ProjectSet")
public class ProjectSetController extends BaseController
{
    @Autowired
    private IProjectSetService projectSetService;

    /**
     * 查询项目设置列表
     */
    @PreAuthorize("@ss.hasPermi('chat:ProjectSet:list')")
    @GetMapping("/list")
    public TableDataInfo list(ProjectSet projectSet)
    {
        startPage();
        List<ProjectSet> list = projectSetService.selectProjectSetList(projectSet);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('chat:ProjectSet:list')")
    @GetMapping
    public AjaxResult getByProjectId(String projectId)
    {
        ProjectSet projectSet = projectSetService.selectProjectSetByProjectId(projectId);
        return success(projectSet);
    }



    /**
     * 导出项目设置列表
     */
    @PreAuthorize("@ss.hasPermi('chat:ProjectSet:export')")
    @Log(title = "项目设置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ProjectSet projectSet)
    {
        List<ProjectSet> list = projectSetService.selectProjectSetList(projectSet);
        ExcelUtil<ProjectSet> util = new ExcelUtil<ProjectSet>(ProjectSet.class);
        util.exportExcel(response, list, "项目设置数据");
    }

    /**
     * 获取项目设置详细信息
     */
    @PreAuthorize("@ss.hasPermi('chat:ProjectSet:query')")
    @GetMapping(value = "/{setId}")
    public AjaxResult getInfo(@PathVariable("setId") Long setId)
    {
        return success(projectSetService.selectProjectSetBySetId(setId));
    }

    /**
     * 新增项目设置
     */
    @PreAuthorize("@ss.hasPermi('chat:ProjectSet:add')")
    @Log(title = "项目设置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ProjectSet projectSet)
    {
        return toAjax(projectSetService.insertProjectSet(projectSet));
    }

    /**
     * 修改项目设置
     */
    @PreAuthorize("@ss.hasPermi('chat:ProjectSet:edit')")
    @Log(title = "项目设置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ProjectSet projectSet)
    {
        return toAjax(projectSetService.updateProjectSet(projectSet));
    }

    /**
     * 删除项目设置
     */
    @PreAuthorize("@ss.hasPermi('chat:ProjectSet:remove')")
    @Log(title = "项目设置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{setIds}")
    public AjaxResult remove(@PathVariable Long[] setIds)
    {
        return toAjax(projectSetService.deleteProjectSetBySetIds(setIds));
    }
}
