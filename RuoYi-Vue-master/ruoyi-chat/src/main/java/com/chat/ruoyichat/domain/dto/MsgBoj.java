package com.chat.ruoyichat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author chat
 * @since 2025/3/18
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MsgBoj {
    private Long id;
    private String to;//发送的账号
    private String from;//回复人手机号
    private String contact_name;//回复人名称
    private String read;//是否阅读
    private String body;//消息
    private String date_sent;//时间
    private Long date;//时间
}
