package com.chat.ruoyichat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * @author chat
 * @since 2025/3/17
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Message {
    private String sessionId;
    private String text;
    private Date time;
    private Long success;//1成功 0失败
    private String flagId;
    private Long realId;
    private Long isFavorite;
}
