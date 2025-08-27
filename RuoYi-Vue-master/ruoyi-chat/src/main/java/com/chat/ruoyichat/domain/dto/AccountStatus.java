package com.chat.ruoyichat.domain.dto;

/**
 * @author chat
 * @since 2025/3/15
 **/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AccountStatus {
    //总
    private Integer accountAllNum;
    //可使用
    private Integer accountOk;
    //已封禁
    private Integer accountBan;
    //未分配
    private Integer accountNoAssi;
}
