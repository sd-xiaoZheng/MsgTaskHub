package com.chat.ruoyichat.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * GenerateMiddle对象 generate_middle
 *
 * @author ruoyi
 * @date 2025-06-20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerateMiddle extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    private Long id;

    /**
     * 绑定的generateId
     */
    @Excel(name = "绑定的generateId")
    private Long tlId;

    /**
     * 排序
     */
    @Excel(name = "绑定的generateId")
    private Long sort;

    /**
     * 名称
     */
    @Excel(name = "名称")
    private String templateName;

    //Text有多少数量
    private Long count;

    public GenerateMiddle(Long id, String templateName, Long sort) {
        this.tlId = id;
        this.templateName = templateName;
        this.sort = sort;
    }
}
