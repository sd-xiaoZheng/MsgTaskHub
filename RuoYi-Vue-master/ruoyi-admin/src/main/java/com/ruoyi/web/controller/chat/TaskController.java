package com.ruoyi.web.controller.chat;

import com.chat.ruoyichat.domain.Task;
import com.chat.ruoyichat.domain.vo.Both1ExcelTemplate;
import com.chat.ruoyichat.domain.vo.Both2ExcelTemplate;
import com.chat.ruoyichat.service.ITaskService;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * 任务，用于存储任务相关信息Controller
 *
 * @author ruoyi
 * @date 2025-03-04
 */
@RestController
@RequestMapping("/chat/Task")
public class TaskController extends BaseController {
    @Autowired
    private ITaskService taskService;
    public static final HashMap<Long, Object> userLock = new HashMap<>();

    /**
     * 查询任务，用于存储任务相关信息列表
     */
    @PreAuthorize("@ss.hasPermi('chat:Task:list')")
    @GetMapping("/list")
    public TableDataInfo list(Task task) {
        List<Task> list = taskService.selectTaskList(task);
        return getDataTable(list);
    }

    /**
     * 导出任务，用于存储任务相关信息列表
     */
    @PreAuthorize("@ss.hasPermi('chat:Task:export')")
    @Log(title = "任务，用于存储任务相关信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Task task) {
        List<Task> list = taskService.selectTaskList(task);
        ExcelUtil<Task> util = new ExcelUtil<Task>(Task.class);
        util.exportExcel(response, list, "任务，用于存储任务相关信息数据");
    }

    /**
     * 获取任务，用于存储任务相关信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('chat:Task:query')")
    @GetMapping(value = "/{taskId}")
    public AjaxResult getInfo(@PathVariable("taskId") String taskId) {
        return success(taskService.selectTaskByTaskId(taskId));
    }

    /**
     * 新增任务，用于存储任务相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:Task:add')")
    @Log(title = "任务，用于存储任务相关信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Task task) {
        return toAjax(taskService.insertTask(task));
    }

    /**
     * 修改任务，用于存储任务相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:Task:edit')")
    @Log(title = "任务，用于存储任务相关信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Task task) {
        return toAjax(taskService.updateTask(task));
    }

    /**
     * 删除任务，用于存储任务相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:Task:remove')")
    @Log(title = "任务，用于存储任务相关信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{taskId}")
    public AjaxResult remove(@PathVariable String taskId) {
        return toAjax(taskService.deleteTaskByTaskId(taskId));
    }


    /**
     * 任务导入
     */
    @Log(title = "导入任务", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public AjaxResult importData(MultipartFile file, String projectId, String templateId, String taskName, String isBoth) throws Exception {
        String operName = SecurityUtils.getUsername();
        Long userId = SecurityUtils.getUserId();
        boolean b;
        String errorMsg;
        try {
            synchronized (userLock) {
                b = userLock.containsKey(userId);
                if (!b) {
                    userLock.put(userId, 1);
                } else {
                    return AjaxResult.error();
                }
            }
            if (b) {
                synchronized (this) {
                    String message = taskService.importTaskList(file, operName, projectId, templateId, taskName, isBoth);
                    userLock.remove(userId);
                    return AjaxResult.success(message);
                }
            } else {
                String message = taskService.importTaskList(file, operName, projectId, templateId, taskName, isBoth);
                userLock.remove(userId);
                return AjaxResult.success(message);
            }
        } catch (Exception e) {
            errorMsg = e.getMessage();
            logger.error(e.getMessage());
        }
        userLock.remove(userId);
        return AjaxResult.error(errorMsg);
    }


    @Log(title = "下载模板", businessType = BusinessType.EXPORT)
    @PostMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response, Integer both) {
        if (both.equals(0)) {
            List<Both1ExcelTemplate> list = taskService.download1Template(both);
            ExcelUtil<Both1ExcelTemplate> util = new ExcelUtil<>(Both1ExcelTemplate.class);
            util.exportExcel(response, list, "上传模板");
        } else {
            List<Both2ExcelTemplate> list = taskService.download2Template(both);
            ExcelUtil<Both2ExcelTemplate> util = new ExcelUtil<>(Both2ExcelTemplate.class);
            util.exportExcel(response, list, "上传模板");
        }
    }


    /**
     * 分配任务
     */
    @GetMapping("/assigned")
    public AjaxResult assigned(String taskId) {
        return AjaxResult.success(taskService.assigned(taskId));
    }


    /**
     * 组长查询整组任务状态
     */
    @GetMapping("/taskStatus")
    public AjaxResult taskStatus() {
        return AjaxResult.success("全组状态", taskService.taskStatusByTaskId());
    }

    /**
     * 暂停任务
     */
    @GetMapping("/suspendTask")
    public AjaxResult suspendTask(String taskId) {
        taskService.suspendTask(taskId);
        return AjaxResult.success("已暂停");
    }

    /**
     * 开始任务
     */
    @GetMapping("/StartTask")
    public AjaxResult StartTask(String taskId) {
        taskService.StartTask(taskId);
        return AjaxResult.success("已开始");
    }
}
