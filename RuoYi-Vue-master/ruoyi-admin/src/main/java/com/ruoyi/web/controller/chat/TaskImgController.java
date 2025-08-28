package com.ruoyi.web.controller.chat;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
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
import com.chat.ruoyichat.domain.TaskImg;
import com.chat.ruoyichat.service.ITaskImgService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * TaskImgController
 *
 * @author ruoyi
 * @date 2025-07-07
 */
@RestController
@RequestMapping("/system/taskImg")
public class TaskImgController extends BaseController {
    @Autowired
    private ITaskImgService taskImgService;

    /**
     * 查询TaskImg列表
     */
    @PreAuthorize("@ss.hasPermi('system:taskImg:list')")
    @GetMapping("/list")
    public TableDataInfo list(TaskImg taskImg) {
        startPage();
        List<TaskImg> list = taskImgService.selectTaskImgList(taskImg);
        return getDataTable(list);
    }

    /**
     * 获取头像
     */
    @GetMapping("/getAvatar")
    public AjaxResult getAvatar(Long imgAct) {
        TaskImg taskImg = taskImgService.getAvatar(imgAct);
        if (Objects.isNull(taskImg)){
            return AjaxResult.error("未设置");
        }
        return AjaxResult.success(taskImg);
    }

    /**
     * 导出TaskImg列表
     */
    @PreAuthorize("@ss.hasPermi('system:taskImg:export')")
    @Log(title = "TaskImg", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TaskImg taskImg) {
        List<TaskImg> list = taskImgService.selectTaskImgList(taskImg);
        ExcelUtil<TaskImg> util = new ExcelUtil<TaskImg>(TaskImg.class);
        util.exportExcel(response, list, "TaskImg数据");
    }

    /**
     * 获取TaskImg详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:taskImg:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(taskImgService.selectTaskImgById(id));
    }

    /**
     * 新增TaskImg
     */
    @PreAuthorize("@ss.hasPermi('system:taskImg:add')")
    @Log(title = "TaskImg", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TaskImg taskImg) {
        return toAjax(taskImgService.insertTaskImg(taskImg));
    }

    /**
     * 修改TaskImg
     */
    @PreAuthorize("@ss.hasPermi('system:taskImg:edit')")
    @Log(title = "TaskImg", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TaskImg taskImg) {
        return toAjax(taskImgService.updateTaskImg(taskImg));
    }

    /**
     * 删除TaskImg
     */
    @PreAuthorize("@ss.hasPermi('system:taskImg:remove')")
    @Log(title = "TaskImg", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(taskImgService.deleteTaskImgByIds(ids));
    }

    /**
     * 导入图片
     */
    @Log(title = "导入图片", businessType = BusinessType.IMPORT)
    @PostMapping("/importImgName")
    public AjaxResult importImgName(MultipartFile file) throws IOException {
        TaskImg taskImg = taskImgService.importImgName(file);
        return AjaxResult.success(taskImg);
    }

    /**
     * 修改各种头像
     * ModelAttribute
     */
    @Log(title = "updateAvatar", businessType = BusinessType.IMPORT)
    @PostMapping("/updateAvatar")
    public AjaxResult updateAvatar(MultipartFile file,Long imgAct) throws IOException {
        TaskImg taskImg = taskImgService.updateAvatar(file,imgAct);
        return AjaxResult.success(taskImg);
    }
}














