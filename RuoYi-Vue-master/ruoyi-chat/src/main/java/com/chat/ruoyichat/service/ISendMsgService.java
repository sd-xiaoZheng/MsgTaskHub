package com.chat.ruoyichat.service;


import com.alibaba.fastjson2.JSONObject;
import com.chat.ruoyichat.domain.dto.BackMsg;
import com.chat.ruoyichat.domain.dto.PhoneInfo;
import com.chat.ruoyichat.domain.sendDto.AccountInfo;
import com.chat.ruoyichat.domain.sendDto.SendMsgObj;
import com.chat.ruoyichat.domain.vo.ReBack2;

import java.text.ParseException;
import java.util.List;

/**
 * 调接口所有接口
 *
 * @author ruoyi
 * @date 2025-03-04
 */
public interface ISendMsgService {

    List getCustomerInfo(String pcNum);

    SendMsgObj getSendMsg(Long taskProjectId, Long userId);

    void getSendMsgSuccess(SendMsgObj sendMsgObj);

    void getSendMsgF(SendMsgObj sendMsgObj);

//    void disable(String account,String reason);
    void disable(String account);

    PhoneInfo getPhoneInfos(int count);

    Integer backMsgs(BackMsg backMsg) throws ParseException;

    PhoneInfo checkBan(Integer count);

    void deleteSys();

    void reBack(JSONObject jsonObject);

    Object accountBinding();

    void reBack2(ReBack2 reBack2);
}
