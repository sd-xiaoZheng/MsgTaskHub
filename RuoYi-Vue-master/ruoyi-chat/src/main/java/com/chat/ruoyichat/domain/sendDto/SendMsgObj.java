package com.chat.ruoyichat.domain.sendDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author chat
 * @since 2025/3/15
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SendMsgObj {
    /**
     *         "id": 2774149,
     *         "mobileNum": "esmeraldalapointe3636@hotmail.com",
     *         "myphonenumber": "esmeraldalapointe3636@hotmail.com",
     *         "type": "text",
     *         "text": "Sorry to intrude! I'm Cody  I'd Iike to discuss a position that couId be a great match for your profiIe. Can we speak for a few minutes now?",
     *         "other": "12134531988",
     *         "userId": 2577,
     *         "userName": "esmeraldalapointe363",
     *         "proxy": "79dc8131fb8aec4b.na.roxlabs.vip:4600:user-miertnweb2024-region-us-sessid-eJUqcj6a-sesstime-30-keep-true:miertnweb2024",
     *         "cookie": "cookie",
     *         "deviceInfo": "{\"email\":
     */
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
}











