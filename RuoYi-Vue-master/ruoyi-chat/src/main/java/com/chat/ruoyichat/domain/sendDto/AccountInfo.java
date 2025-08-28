package com.chat.ruoyichat.domain.sendDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RuoYi-Vue-master
 * @since 2025/7/3
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AccountInfo {
    private String account;
    private String cookie;
    private String deviceInfo;
    private String userName;
    private String password;

    private String account_sid;//userName
    private String auth_token;//password
    private String currentNumber;//手机号
    private String recipient;//收件人
    private String message;//消息
    private String callback_url;//我绑定的接口
    private String mediaUrl;//照片url
}
