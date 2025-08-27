package com.chat.ruoyichat.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 分配给的客服账号对象 accounts
 *
 * @author ruoyi
 * @date 2025-03-14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Accounts extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 账号的唯一标识符
     */
    private Long accountId;

    /**
     * 账号
     */
    @Excel(name = "account")
    @ExcelProperty("account")
    private String account;

    /**
     * cookie
     */
    @Excel(name = "Cookie")
    @ExcelProperty("Cookie")
    private String Cookie;

    /**
     * 设备信息
     */
    @Excel(name = "deviceInfo")
    @ExcelProperty("deviceInfo")
    private String deviceInfo;

    /**
     * 用户名
     */
    @Excel(name = "userName")
    @ExcelProperty("userName")
    private String userName;
    /**
     * 国家
     */
    @Excel(name = "country")
    @ExcelProperty("country")
    private String country;

    /**
     * 密码
     */
    @Excel(name = "password")
    @ExcelProperty("password")
    private String password;

    /**
     * 帐号状态0正常 -1封禁 1回收
     */
//    @Excel(name = "帐号状态",readConverterExp = "0=正常,-1=封禁,1=回收站")
    private Long accStatus;
//    private String accStatusName;
    /**
     * 分配人
     */
    private Long assignedTo;
//    @Excel(name = "分配人")
    private String assignedToName;


    private Integer delFlag;

    /**
     * 所属项目
     */
    private String projectId;
    /**
     * 所属项目
     */
//    @Excel(name = "所属项目")
    private String projectName;
    /**
     * 上传人
     */
//    @Excel(name = "上传人")
    private String upUser;

//    @Excel(name = "创建时间",dateFormat = "yyyy-MM-dd HH:mm:ss")
//    @DateTimeFormat(pattern="MM-dd HH:mm:ss")
    @JsonFormat(pattern = "MM-dd HH:mm:ss")
    private Date createTime;


    @JsonFormat(pattern = "YYYY-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(pattern = "YYYY-MM-dd HH:mm:ss")
    private Date endTime;

//    @Excel(name = "封禁时间",dateFormat = "yyyy-MM-dd HH:mm:ss")
//    @DateTimeFormat(pattern="MM-dd HH:mm:ss")
    @JsonFormat(pattern = "MM-dd HH:mm:ss")
    private Date banTime;

    private Long sendNum;

    //封号原因
    private String reason;
    //允许客服查看账号
    private String number;
}
