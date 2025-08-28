package com.ruoyi.web.controller.chat;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
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
import com.chat.ruoyichat.domain.SessionRecord;
import com.chat.ruoyichat.service.ISessionRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 会话记录，用于存储会话记录相关信息Controller
 *
 * @author ruoyi
 * @date 2025-03-04
 */
@RestController
@RequestMapping("/chat/SessionRecord")
public class SessionRecordController extends BaseController
{
    @Autowired
    private ISessionRecordService sessionRecordService;

    /**
     * 查询会话记录，用于存储会话记录相关信息列表
     */
    @PreAuthorize("@ss.hasPermi('chat:SessionRecord:list')")
    @GetMapping("/list")
    public TableDataInfo list(SessionRecord sessionRecord)
    {
        startPage();
        List<SessionRecord> list = sessionRecordService.selectSessionRecordList(sessionRecord);
        return getDataTable(list);
    }

    @PostMapping("/read/{sessionId}")
    public AjaxResult readSession(@PathVariable String sessionId)
    {
        SessionRecord sessionRecord = new SessionRecord();
        sessionRecord.setSessionId(sessionId);
        sessionRecord.setMessageCount(0L);
        sessionRecordService.updateSessionRecord(sessionRecord);
        return success();
    }

    @GetMapping("/myMassMessage")
    public TableDataInfo myMassMessage(SessionRecord sessionRecord)
    {
        startPage();
        List<SessionRecord> list = sessionRecordService.selectSessionRecordByMassMessage(sessionRecord);
        return getDataTable(list);
    }

    @GetMapping("/myFavoriteSession")
    public TableDataInfo myFavoriteSession(SessionRecord sessionRecord)
    {
        sessionRecord.setIsFavorite(1L);
        startPage();
        List<SessionRecord> list = sessionRecordService.selectSessionRecordByMassMessage(sessionRecord);
        return getDataTable(list);
    }

    @PostMapping("/topSession/{sessionId}")
    public AjaxResult topSession(@PathVariable String sessionId)
    {
        SessionRecord sessionRecord = sessionRecordService.selectSessionRecordBySessionId(sessionId);
        Long isPinned = sessionRecord.getIsPinned();
        SessionRecord update=new SessionRecord();
        update.setSessionId(sessionId);
        update.setIsPinned(isPinned.equals(0L)?1L:0L);
        sessionRecordService.updateSessionRecord(update);
        if(isPinned.equals(0L)){
//            之前就没置顶，那么需要提示置顶成功
            return success("置顶成功");
        }else{
//            之前就置顶了，那么需要提示取消置顶成功
            return success("取消置顶成功");
        }
    }

    @PostMapping("/favoriteSession/{sessionId}")
    public AjaxResult favoriteSession(@PathVariable String sessionId)
    {
        SessionRecord sessionRecord = sessionRecordService.selectSessionRecordBySessionId(sessionId);
        Long isFavorite = sessionRecord.getIsFavorite();
        SessionRecord update=new SessionRecord();
        update.setSessionId(sessionId);
        update.setIsFavorite(isFavorite.equals(0L)?1L:0L);
        sessionRecordService.updateSessionRecord(update);
        if(isFavorite.equals(0L)){
//            之前就没置顶，那么需要提示置顶成功
            return success("收藏成功");
        }else{
//            之前就置顶了，那么需要提示取消置顶成功
            return success("取消收藏成功");
        }
    }

    @GetMapping("/mySession")
    public TableDataInfo mySession(SessionRecord sessionRecord)
    {
        sessionRecord.setMessageType(1);
        startPage();
        List<SessionRecord> list = sessionRecordService.selectMySessionRecord(sessionRecord);
        return getDataTable(list);
    }

    /**
     * 导出会话记录，用于存储会话记录相关信息列表
     */
    @PreAuthorize("@ss.hasPermi('chat:SessionRecord:export')")
    @Log(title = "会话记录，用于存储会话记录相关信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SessionRecord sessionRecord)
    {
        List<SessionRecord> list = sessionRecordService.selectSessionRecordList(sessionRecord);
        ExcelUtil<SessionRecord> util = new ExcelUtil<SessionRecord>(SessionRecord.class);
        util.exportExcel(response, list, "会话记录，用于存储会话记录相关信息数据");
    }

    /**
     * 获取会话记录，用于存储会话记录相关信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('chat:SessionRecord:query')")
    @GetMapping(value = "/{sessionId}")
    public AjaxResult getInfo(@PathVariable("sessionId") String sessionId)
    {
        return success(sessionRecordService.selectSessionRecordBySessionId(sessionId));
    }

    /**
     * 新增会话记录，用于存储会话记录相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:SessionRecord:add')")
    @Log(title = "会话记录，用于存储会话记录相关信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SessionRecord sessionRecord)
    {
        return toAjax(sessionRecordService.insertSessionRecord(sessionRecord));
    }

    /**
     * 修改会话记录，用于存储会话记录相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:SessionRecord:edit')")
    @Log(title = "会话记录，用于存储会话记录相关信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SessionRecord sessionRecord)
    {
        return toAjax(sessionRecordService.updateSessionRecord(sessionRecord));
    }

    /**
     * 删除会话记录，用于存储会话记录相关信息
     */
    @PreAuthorize("@ss.hasPermi('chat:SessionRecord:remove')")
    @Log(title = "会话记录，用于存储会话记录相关信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{sessionIds}")
    public AjaxResult remove(@PathVariable String[] sessionIds)
    {
        return toAjax(sessionRecordService.deleteSessionRecordBySessionIds(sessionIds));
    }

    /**
     * 按userid获取会话中未读数量
     */
    @GetMapping("/sessionUnread")
    public AjaxResult sessionUnread( Long userId)
    {
        JSONObject unreadNum = sessionRecordService.sessionUnread(userId);
        return AjaxResult.success(unreadNum);
    }

    /**
     * 对ai会话的打分
     * @param sessionRecord 包含会话id（sessionId）和分数（score）
     * @return {@code AjaxResult}
     */
    // todo 对ai会话的打分
    @Log(title = "对ai会话的打分", businessType = BusinessType.UPDATE)
    @PutMapping("/aiscore")
    public AjaxResult aiscore(SessionRecord sessionRecord) {
        sessionRecordService.aiscore(sessionRecord);
        return AjaxResult.success("ai会话打分保存成功");
    }

    // todo 准备给ai提供打分相关数据（未定）
}
