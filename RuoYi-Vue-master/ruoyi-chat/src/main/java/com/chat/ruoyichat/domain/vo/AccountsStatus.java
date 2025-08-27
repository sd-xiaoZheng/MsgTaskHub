package com.chat.ruoyichat.domain.vo;

import com.chat.ruoyichat.domain.Accounts;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountsStatus {
    private Integer status;
    private ArrayList<Accounts> accounts;
}