package com.chat.ruoyichat.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author chat
 * @since 2025/3/11
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DeleteTaskVo {
    private String taskId;
    private Long userId;
}
