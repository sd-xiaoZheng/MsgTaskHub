package com.chat.ruoyichat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author chat
 * @since 2025/3/26
 **/
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ReplyNum {
    private Integer replyWeight;
    private Integer num;
}
