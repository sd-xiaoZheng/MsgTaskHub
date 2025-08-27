package com.chat.ruoyichat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CallBackObj {
    private String from;
    private String to;
    private ArrayList<CallBackItem> msgMap;
}
