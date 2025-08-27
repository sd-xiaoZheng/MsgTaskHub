package com.ruoyi.web.controller.chat;

import com.chat.ruoyichat.domain.Accounts;
import com.chat.ruoyichat.domain.dto.BackMsg;
import com.chat.ruoyichat.domain.dto.CallBackObj;
import com.chat.ruoyichat.domain.dto.PhoneInfo;
import com.chat.ruoyichat.domain.sendDto.SendMsgObj;
import com.chat.ruoyichat.service.ISendMsgService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author chat
 * @since 2025/3/15
 **/
@Slf4j
@RestController
@RequestMapping
public class SendMsgController {
    @Autowired
    private ISendMsgService sendMsgService;

    /**
     * 获取客服账号  500我报错
     */
//    @Log(title = "项目账号状态", businessType = BusinessType.INSERT)
    @PostMapping("/Msg/getUserIds/30321")
    public AjaxResult accountStatus(@RequestBody HashMap map) {
        Object param = map.get("pcNum");
        String pcNum = param.toString();
        List customerList = sendMsgService.getCustomerInfo(pcNum);
        log.info("获取用户信息接口调用成功");
        return AjaxResult.success(customerList);
    }


    /**
     * 发消息
     */
//    @Log(title = "项目账号状态", businessType = BusinessType.INSERT)
    @PostMapping("/PhoneInfo/getTaskMsg")//3.28修改接口140服务器
//    @PostMapping("/PhoneInfo/getSendMsg")
    public AjaxResult getTaskMsg(@RequestBody HashMap map) {
        Long taskProjectId = Long.parseLong(map.get("taskProjectId").toString());
        Long userId = Long.parseLong(map.get("userId").toString());
        SendMsgObj customerList = sendMsgService.getSendMsg(taskProjectId, userId);
        log.info("发消息接口调用成功");
        return AjaxResult.success(customerList);
    }

    @PostMapping("/PhoneInfo/getSendMsg")
    public AjaxResult getSendMsg(@RequestBody HashMap map) {
        Long taskProjectId = Long.parseLong(map.get("taskProjectId").toString());
        Long userId = Long.parseLong(map.get("userId").toString());
        SendMsgObj customerList = sendMsgService.getSendMsg(taskProjectId, userId);
        log.info("发消息接口调用成功");
        return AjaxResult.success(customerList);
    }

    /**
     * 消息发送成功
     */
//    @Log(title = "消息发送成功", businessType = BusinessType.INSERT)
    @PostMapping("/Msg/upsendmessagestatus/success")
    public AjaxResult getSendMsgSuccess(@RequestBody HashMap map) {
        String taskContentId = map.get("id").toString();
        String account = map.get("mobileNum").toString();
        sendMsgService.getSendMsgSuccess(taskContentId, account);
        log.info("消息发送成功接口调用成功");
        return AjaxResult.success();
    }

//    /**
//     * 消息发送成功 TODO TODO
//     */

    /// /    @Log(title = "消息发送成功", businessType = BusinessType.INSERT)
//    @PostMapping("/Msg/upsendmessagestatus/f")
//    public AjaxResult getSendMsgF(@RequestBody HashMap map) {
//        String taskContentId = map.get("id").toString();
//        String account = map.get("mobileNum").toString();
//        sendMsgService.getSendMsgSuccess(taskContentId, account);
//        log.info("消息发送成功接口调用成功");
//        return AjaxResult.success();
//    }


//    /**
//     * 消息发送失败
//     *
    @Log(title = "消息发送失败", businessType = BusinessType.INSERT)
    @PostMapping("/Msg/upsendmessagestatus/f")
    public AjaxResult getSendMsgF(@RequestBody HashMap map) {//整合失败原因
        String taskContentId = map.get("id").toString();
        String account = map.get("mobileNum").toString();
        sendMsgService.getSendMsgF(taskContentId, account);
        log.info("消息发送失败接口调用成功");
        return AjaxResult.success();
    }


    //    /**
//     * 消息发送失败
//     */Msg/upsendmessagestatus/fail
//    @Log(title = "消息发送失败", businessType = BusinessType.INSERT)
    @PostMapping("/Msg/upsendmessagestatus/fail")
    public AjaxResult getSendMsgFail(@RequestBody HashMap map) {
        String taskContentId = map.get("id").toString();
        String account = map.get("mobileNum").toString();
        sendMsgService.getSendMsgF(taskContentId, account);
        log.info("消息发送失败接口调用成功");
        return AjaxResult.success();
    }

    /**
     * 封号
     */
//    @Log(title = "封号", businessType = BusinessType.INSERT)
    @PostMapping("/PhoneInfo/disableOrRecoverAccount/disable")
    public AjaxResult disable(@RequestBody HashMap map) {
        //{
        //    "myphonenumber":myphonenumber,
        //    "taskProjectId":30321,
        //     "reason":""
        //}
        String account = map.get("myphonenumber").toString();
//        String reason = map.get("reason").toString();
//        sendMsgService.disable(account,reason);
        sendMsgService.disable(account);
        log.info("封号调用成功");
        return AjaxResult.success();
    }


    /**
     * 根据手机号查看是否有消息回复
     */
//    @Log(title = "拿手机号 为了查看是否有消息回复", businessType = BusinessType.INSERT)
    @GetMapping("/PhoneInfo/getPhoneInfos")
    public AjaxResult getPhoneInfos(Integer count) {
        List<PhoneInfo> phoneInfos = sendMsgService.getPhoneInfos(count);
        log.info("查看是否有消息回复调用成功");
        return AjaxResult.success(phoneInfos);
    }


    /**
     * 收到回复
     */
//    @Log(title = "有消息回复", businessType = BusinessType.INSERT)
    @PostMapping("/Msg/backTaskMsgs")
//    @PostMapping("/Msg/backMsgs")
    public AjaxResult backTaskMsgs(@RequestBody BackMsg backMsg) throws ParseException {
        Integer i = sendMsgService.backMsgs(backMsg);
        log.info("收到回复调用成功");
        return AjaxResult.success("操作成功，重复消息数量:" + i);
    }

    @PostMapping("/Msg/backMsgs")
    public AjaxResult backMsgs(@RequestBody BackMsg backMsg) throws ParseException {
        Integer i = sendMsgService.backMsgs(backMsg);
        log.info("收到回复调用成功");
        return AjaxResult.success("操作成功，重复消息数量:" + i);
    }

    /**
     * 检查未分配账号是否封禁
     */
//    @Log(title = "检查未分配账号是否封禁", businessType = BusinessType.INSERT)
    @GetMapping("/PhoneInfo/checkBan")
    public AjaxResult checkBan(Integer count) {
        List<PhoneInfo> phoneInfos = sendMsgService.checkBan(count);
        log.info("检查未分配账号是否封禁调用成功");
        return AjaxResult.success(phoneInfos);
    }

    @Log(title = "清空系统数据", businessType = BusinessType.INSERT)
    @PostMapping("/warn/deleteSys")
    public AjaxResult deleteSys() {
        sendMsgService.deleteSys();
        return AjaxResult.success();
    }


    /**
     * token处理
     */
    @GetMapping("/PhoneInfo/setToken")
    public AjaxResult setToken(String userName, String token) {
        sendMsgService.setToken(userName, token);
        return AjaxResult.success();
    }

    /**
     * token处理
     */
    @GetMapping("/PhoneInfo/getToken")
    public AjaxResult getToken(String userName) {
        String token = sendMsgService.getToken(userName);
        return AjaxResult.success(token);
    }


    /**
     * 收到回复回调
     */
    @PostMapping("/Msg/reBack")
    public AjaxResult reBack(@RequestBody CallBackObj paramMap) {
        log.info("reBack有消息回复:" + paramMap);
//        JSONObject jsonObject = new JSONObject(paramMap);
        sendMsgService.reBack(paramMap);
        log.info("reBack接口调用成功");
        return AjaxResult.success();
    }

    /**
     * 上传手机号
     */
    @PostMapping("/Msg/uploadAccount")
    public AjaxResult uploadAccount(@RequestBody Accounts accounts) {
        //脚本上报设备号，我需要保存该账号 并且返回该账号与那个客服绑定
        log.info("accounts有消息回复:" + accounts);
        ArrayList<Long> boundUser = sendMsgService.uploadAccount(accounts);
        log.info("accounts接口调用成功");
        return AjaxResult.success(boundUser);
    }
}

























