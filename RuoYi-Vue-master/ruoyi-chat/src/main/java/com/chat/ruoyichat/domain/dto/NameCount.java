package com.chat.ruoyichat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sms
 * @since 2025/6/24
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NameCount {
    private Long name;//用户名称
    private Long count;//当前发送数量
}
