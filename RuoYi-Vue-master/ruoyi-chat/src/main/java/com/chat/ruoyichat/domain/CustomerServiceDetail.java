package com.chat.ruoyichat.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.utils.uuid.IdUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * 客服号详情，用于存储客服号详细信息对象 customer_service_detail
 *
 * @author ruoyi
 * @date 2025-03-04
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class CustomerServiceDetail extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 客服号唯一标识
     */
    private String customerId;

    /**
     * 关联的项目ID
     */
    @Excel(name = "关联的项目ID")
    private String projectId;
    private String projectName;
    private Integer status;
//    账号库存是否充足 （默认不充足0 ，1充足)
    private Integer isEnough;

    /**
     * 最多分配数量
     */
    @Excel(name = "最多分配数量")
    private Long maxLoad;

    /**
     * 管理员用户ID
     */
    @Excel(name = "管理员用户ID")
    private Long managerId;

    /**
     * 总发送
     */
    @Excel(name = "总发送")
    private Long sendNum;

    /**
     * 分配数量
     */
    @Excel(name = "分配数量")
    private Long surplusNum;

    /**
     * 回复数量
     */
    @Excel(name = "回复数量")
    private Long replyNum;

    /**
     * 成功数量
     */
    @Excel(name = "成功数量")
    private Long successNum;

    /**
     * 总封禁账号数量
     */
    @Excel(name = "总封禁账号数量")
    private Long banNum;

    /**
     * 账号数量
     */
    @Excel(name = "账号数量")
    private Integer accountNum;

    /**
     * 用户id
     */
    @Excel(name = "客服的用户id")
    private Long userId;
    private String userName;
    private List<CustomerServiceDetail> children;

    //回复率
    private Double replyRate;

    //并发控制
    private Integer version;


    public CustomerServiceDetail(String projectId, Long maxLoad, Long managerId, Long userId) {
        this.customerId = IdUtils.fastSimpleUUID();
        this.projectId = projectId;
        this.maxLoad = maxLoad;
        this.managerId = managerId;
        this.sendNum = 0L;
        this.successNum = 0L;
        this.banNum = 0L;
        this.userId = userId;
    }
}
