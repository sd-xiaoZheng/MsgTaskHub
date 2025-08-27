package com.chat.ruoyichat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author chat
 * @since 2025/3/17
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PhoneInfo implements Serializable {
    private String id;
    private String myphonenumber;
    private Long userId;
    private String cookie;
    private String userName;
    private String deviceInfo;
}