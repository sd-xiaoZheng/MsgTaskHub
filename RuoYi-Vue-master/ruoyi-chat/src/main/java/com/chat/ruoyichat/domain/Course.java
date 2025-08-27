package com.ruoyichat.chat.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 新手教程对象 course
 * 
 * @author ruoyi
 * @date 2025-03-21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Course extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 公告ID */
    private Long courseId;

    /** 公告标题 */
    @Excel(name = "公告标题")
    private String title;

    /** 公告内容 */
    @Excel(name = "公告内容")
    private String content;

    /** 排序 */
    @Excel(name = "排序")
    private Long orderBy;
}
