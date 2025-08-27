package com.chat.ruoyichat.domain.vo;

import com.ruoyi.common.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author chat
 * @since 2025/3/8
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CustomerTaskStatusVo {
    private Long userId;
    //用户名称吧
    private String nickName;
    //1正常 2停用
    private String status;
    //1正常 2停用
    private String statusName;
    //创建时间
    private Date createTime;

    //总发送数量
    private Long maxLoad;
//    //总发送数量
    private Long sendNum;
    //上传数量
    private Long surplusNum;
    //回复数量
    private Long replyNum;
    //成功数量
    private Long successNum;
    //回复率
    private Double replyRate;

    //小组剩余可上传数量
    private Long excessNum;
    //客服数量
    private Integer customerNum;

    private ArrayList<CustomerTaskStatusVo> CustomerTaskStatusVoList = new ArrayList<>();
}
