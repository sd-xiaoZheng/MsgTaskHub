package com.chat.ruoyichat.domain.vo;

import com.ruoyi.common.annotation.Excel;
import lombok.Data;

/**
 * @author chat
 * @since 2025/4/10
 **/
@Data
public class Both2ExcelTemplate {
    @Excel(name = "电话号")
    private String phone;
    @Excel(name = "话术")
    private String Speech;
}
