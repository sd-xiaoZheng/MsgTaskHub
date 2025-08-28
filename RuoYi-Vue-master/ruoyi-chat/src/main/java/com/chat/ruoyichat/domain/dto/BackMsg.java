package com.chat.ruoyichat.domain.dto;

import com.chat.ruoyichat.domain.sendDto.AccountInfo;
import com.chat.ruoyichat.domain.sendDto.SendMsgObj;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;

/**
 * @author chat
 * @since 2025/3/18
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BackMsg {
    private String id;
    private String mobileNum;//?
    private String myphonenumber;//?
    private String type;
    private String text;
    private String other;
    private Long userId;
    private String userName;
    private String proxy="";
    private String cookie;
    private String deviceInfo;
    private String isChat;
    private AccountInfo accountInfo=new AccountInfo();
//    private String email;
//    private Long userId;
//    private Long taskProjectId;
    private ArrayList<MsgBoj> receive_result=new ArrayList<>();
}
