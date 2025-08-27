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
import com.chat.ruoyichat.domain.TaskSendNum;
import com.chat.ruoyichat.service.ITaskSendNumService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 任务发送次数Controller
 * 
 * @author ruoyi
 * @date 2025-03-16
 */
@RestController
@RequestMapping("/chat/TaskSendNum")
public class TaskSendNumController extends BaseController
{
    @Autowired
    private ITaskSendNumService taskSendNumService;

    /**
     * 查询任务发送次数列表
     */
    @PreAuthorize("@ss.hasPermi('chat:TaskSendNum:list')")
    @GetMapping("/list")
    public TableDataInfo list(TaskSendNum taskSendNum)
    {
        startPage();
        List<TaskSendNum> list = taskSendNumService.selectTaskSendNumList(taskSendNum);
        return getDataTable(list);
    }

    /**
     * 导出任务发送次数列表
     */
    @PreAuthorize("@ss.hasPermi('chat:TaskSendNum:export')")
    @Log(title = "任务发送次数", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TaskSendNum taskSendNum)
    {
        List<TaskSendNum> list = taskSendNumService.selectTaskSendNumList(taskSendNum);
        ExcelUtil<TaskSendNum> util = new ExcelUtil<TaskSendNum>(TaskSendNum.class);
        util.exportExcel(response, list, "任务发送次数数据");
    }

    /**
     * 获取任务发送次数详细信息
     */
    @PreAuthorize("@ss.hasPermi('chat:TaskSendNum:query')")
    @GetMapping(value = "/{taskContentId}")
    public AjaxResult getInfo(@PathVariable("taskContentId") String taskContentId)
    {
        return success(taskSendNumService.selectTaskSendNumByTaskContentId(taskContentId));
    }

    /**
     * 新增任务发送次数
     */
    @PreAuthorize("@ss.hasPermi('chat:TaskSendNum:add')")
    @Log(title = "任务发送次数", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TaskSendNum taskSendNum)
    {
        return toAjax(taskSendNumService.insertTaskSendNum(taskSendNum));
    }

    /**
     * 修改任务发送次数
     */
    @PreAuthorize("@ss.hasPermi('chat:TaskSendNum:edit')")
    @Log(title = "任务发送次数", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TaskSendNum taskSendNum)
    {
        return toAjax(taskSendNumService.updateTaskSendNum(taskSendNum));
    }

    /**
     * 删除任务发送次数
     */
    @PreAuthorize("@ss.hasPermi('chat:TaskSendNum:remove')")
    @Log(title = "任务发送次数", businessType = BusinessType.DELETE)
	@DeleteMapping("/{taskContentIds}")
    public AjaxResult remove(@PathVariable String[] taskContentIds)
    {
        return toAjax(taskSendNumService.deleteTaskSendNumByTaskContentIds(taskContentIds));
    }
}
