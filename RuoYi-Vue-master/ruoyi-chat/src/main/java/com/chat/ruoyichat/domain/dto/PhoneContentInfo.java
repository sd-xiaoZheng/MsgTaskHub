package com.chat.ruoyichat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author chat
 * @since 2025/3/13
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PhoneContentInfo {
    private String phone;
    private String content;
    private String title;

    public PhoneContentInfo(String phone, String content) {
        this.phone = phone;
        this.content = content;
    }
}