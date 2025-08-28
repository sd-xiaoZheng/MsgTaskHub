package com.chat.ruoyichat.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * TaskImg对象 task_img
 * 
 * @author ruoyi
 * @date 2025-07-07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskImg extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 用户id */
    @Excel(name = "用户id")
    private Long userId;

    /** 保存地址 */
    @Excel(name = "保存地址")
    private String path;

    /** 项目id */
    @Excel(name = "项目id")
    private String projectId;

    /** 项目id */
    @Excel(name = "图片作用")
    private Long imgAct;
}
