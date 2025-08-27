package com.chat.ruoyichat.service;


import com.alibaba.fastjson2.JSONObject;
import com.chat.ruoyichat.domain.Accounts;
import com.chat.ruoyichat.domain.dto.BackMsg;
import com.chat.ruoyichat.domain.dto.CallBackItem;
import com.chat.ruoyichat.domain.dto.CallBackObj;
import com.chat.ruoyichat.domain.dto.PhoneInfo;
import com.chat.ruoyichat.domain.sendDto.SendMsgObj;

import java.text.ParseException;
import java.util.ArrayList;
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

    void getSendMsgSuccess(String taskContentId,String account);

    void getSendMsgF(String taskContentId, String account);

//    void disable(String account,String reason);
    void disable(String account);

    List<PhoneInfo> getPhoneInfos(int count);

    Integer backMsgs(BackMsg backMsg) throws ParseException;

    List<PhoneInfo> checkBan(Integer count);

    void deleteSys();

    void setToken(String userName, String token);

    String getToken(String userName);

    void reBack(CallBackObj jsonObject);

    ArrayList<Long> uploadAccount(Accounts accounts);
}
