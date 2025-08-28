package com.ruoyi.web.controller.chat;

import com.alibaba.fastjson2.JSONObject;
import com.chat.ruoyichat.domain.dto.BackMsg;
import com.chat.ruoyichat.domain.dto.PhoneInfo;
import com.chat.ruoyichat.domain.sendDto.AccountInfo;
import com.chat.ruoyichat.domain.sendDto.SendMsgObj;
import com.chat.ruoyichat.domain.vo.ReBack2;
import com.chat.ruoyichat.service.ISendMsgService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
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
    //TODO 每个项目一个接口类 然后抽出类中相似的


    //TODO 获取用户通过组名称
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
    public AjaxResult getSendMsgSuccess(@RequestBody SendMsgObj sendMsgObj) {
        sendMsgService.getSendMsgSuccess(sendMsgObj);
        log.info("消息发送成功接口调用成功");
        return AjaxResult.success();
    }

    //     * 消息发送失败
    @Log(title = "消息发送失败", businessType = BusinessType.INSERT)
    @PostMapping("/Msg/upsendmessagestatus/f")
    public AjaxResult getSendMsgF(@RequestBody SendMsgObj sendMsgObj) {
        sendMsgService.getSendMsgF(sendMsgObj);
        log.info("消息发送失败接口调用成功");
        return AjaxResult.success();
    }

    /**
     * 封号
     */
    @PostMapping("/PhoneInfo/disableOrRecoverAccount/disable")
    public AjaxResult disable(@RequestBody HashMap map) {
        //{
        //    "myphonenumber":myphonenumber,
        //    "taskProjectId":30321,
        //     "reason":""
        //}
        String account = map.get("myphonenumber").toString();
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
        PhoneInfo phoneInfos = sendMsgService.getPhoneInfos(count);
        log.info("查看是否有消息回复调用成功");
        return AjaxResult.success(phoneInfos);
    }

    /**
     * 收到回复
     */
    @PostMapping("/Msg/backMsgs")
    public AjaxResult backMsgs(@RequestBody BackMsg backMsg) throws ParseException {
        Integer i = sendMsgService.backMsgs(backMsg);
        log.info("收到回复调用成功");
        return AjaxResult.success("操作成功，重复消息数量:" + i);
    }

    /**
     * 检查未分配账号是否封禁
     */
    @GetMapping("/PhoneInfo/checkBan")
    public AjaxResult checkBan(Integer count) {
        PhoneInfo phoneInfos = sendMsgService.checkBan(count);
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
     * 收到回复回调
     */
    @PostMapping("/Msg/reBack")
    public AjaxResult reBack(@RequestParam HashMap<String, String> paramMap) {
        log.info("reBack有消息回复:" + paramMap);
        JSONObject jsonObject = new JSONObject(paramMap);
        sendMsgService.reBack(jsonObject);
        log.info("reBack接口调用成功");
        return AjaxResult.success();
    }


    /**
     * 收到回复回调网管
     */
    @PostMapping("/Msg/reBack2")
    public AjaxResult reBack2(@RequestBody ReBack2 reBack2) {
        log.info("reBack有消息回复:" + reBack2);
        sendMsgService.reBack2(reBack2);
        log.info("reBack接口调用成功");
        return AjaxResult.success();
    }

    /**
     * 获取账号去绑定
     */
    @PostMapping("/Msg/accountBinding")
    public AjaxResult accountBinding() {
        Object accountInfo = sendMsgService.accountBinding();
        return AjaxResult.success(accountInfo);
    }
}


















