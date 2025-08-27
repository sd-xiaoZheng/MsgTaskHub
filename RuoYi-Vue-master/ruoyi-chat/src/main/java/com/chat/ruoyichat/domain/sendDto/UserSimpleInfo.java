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
public class UserSimpleInfo {
    private Long userId;
    private String userName;
    private boolean admin;

    public UserSimpleInfo(Long userId2, String userName) {
        this.userId = userId2;
        this.userName = userName;
    }
}
