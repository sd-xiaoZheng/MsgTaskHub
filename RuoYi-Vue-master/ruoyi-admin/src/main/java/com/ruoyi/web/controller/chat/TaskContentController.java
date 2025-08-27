package com.ruoyi.web.controller.chat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;

import com.alibaba.fastjson.JSONObject;
import com.chat.ruoyichat.domain.Accounts;
import com.chat.ruoyichat.domain.vo.DeleteTaskVo;
import com.chat.ruoyichat.websocket.WebSocketService;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.chat.ruoyichat.domain.TaskContent;
import com.chat.ruoyichat.service.ITaskContentService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 任务内容，用于存储任务具体内容相关信息Controller
 *
 * @author ruoyi
 * @date 2025-03-04
 */
@RestController
@RequestMapping("/chat/TaskContent")
public class TaskContentController extends BaseController {
    @Autowired
    private ITaskContentService taskContentService;

    /**
     * 查询任务内容，用于存储任务具体内容相关信息列表
     */
    @PreAuthorize("@ss.hasPermi('chat:TaskContent:list')")
    @GetMapping("/list")
    public TableDataInfo list(TaskContent taskContent) {
        startPage();
        List<TaskContent> list = taskContentService.selectTaskContentList(taskContent);
        return getDataTable(list);
    }

    /**
     * 导出任务内容，用于存储任务具体内容相关信息列表
     */
    @PreAuthorize("@ss.hasPermi('chat:TaskContent:export')")
    @Log(title = "任务内容，用于存储任务具体内容相关信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Long userId,Long taskId) {
        List<TaskContent> list = taskContentService.selectTaskContentList4Export(userId);
        ExcelUtil<TaskContent> util = new ExcelUtil<TaskContent>(TaskContent.class);
        util.exportExcel(response, list, "任务内容，用于存储任务具体内容相关信息数据");
    }

    /**
     * 获取任务内容，用于存储任务具体内容相关信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('chat:TaskContent:query')")
    @GetMapping(value = "/{taskContentId}")
    public AjaxResult getInfo(@PathVariable("taskContentId") String taskContentId) {
        return success(taskContentService.selectTaskContentByTaskContentId(taskContentId));
    }

    /**
     * 新增任务内容，用于存储任务具体内容相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:TaskContent:add')")
    @Log(title = "任务内容，用于存储任务具体内容相关信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TaskContent taskContent) {
        return toAjax(taskContentService.insertTaskContent(taskContent));
    }

    /**
     * 修改任务内容，用于存储任务具体内容相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:TaskContent:edit')")
    @Log(title = "任务内容，用于存储任务具体内容相关信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TaskContent taskContent) {
        return toAjax(taskContentService.updateTaskContent(taskContent));
    }

    /**
     * 删除任务内容，用于存储任务具体内容相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:TaskContent:remove')")
    @Log(title = "任务内容，用于存储任务具体内容相关信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{taskContentIds}")
    public AjaxResult remove(@PathVariable String[] taskContentIds) {
        return toAjax(taskContentService.deleteTaskContentByTaskContentIds(taskContentIds));
    }


    /**
     * 根据客服id查询任务详情
     */
    @PreAuthorize("@ss.hasPermi('chat:TaskContent:list')")
    @GetMapping("/taskContentByUserId")
    public TableDataInfo taskContentByUserId(TaskContent taskContent) {
        startPage();
        List<TaskContent> list = taskContentService.taskContentByUserId(taskContent);
        return getDataTable(list);
    }


    /**
     * 删除任务内容，用于存储任务具体内容相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:TaskContent:remove')")
    @Log(title = "任务内容，用于存储任务具体内容相关信息", businessType = BusinessType.DELETE)
    @PostMapping("/removeByUserId")
    public AjaxResult removeByUserId(@RequestBody DeleteTaskVo deleteTaskVo) {
        return toAjax(taskContentService.removeByUserId(deleteTaskVo));
    }


//    @Log(title = "导出不成功任务", businessType = BusinessType.EXPORT)
//    @PostMapping("/export")
//    public void export(HttpServletResponse response, Accounts accounts, Integer exportNum) {
//        List<Accounts> list = accountsService.selectAccountsList4Export(accounts,exportNum);
//        ExcelUtil<Accounts> util = new ExcelUtil<Accounts>(Accounts.class);
//        util.exportExcel(response, list, "账号数据");
//    }
}
