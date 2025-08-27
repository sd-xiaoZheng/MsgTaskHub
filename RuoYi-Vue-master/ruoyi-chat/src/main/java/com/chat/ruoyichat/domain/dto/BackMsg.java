package com.chat.ruoyichat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;

/**
 * @author chat
 * @since 2025/3/18
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BackMsg {
    private String email;
    private Long userId;
    private Long taskProjectId;
    private ArrayList<MsgBoj> data=new ArrayList<>();
}
