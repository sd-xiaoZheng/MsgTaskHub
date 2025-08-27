package com.chat.ruoyichat.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * GenerateText对象 generate_text
 *
 * @author ruoyi
 * @date 2025-06-20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerateText extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    private Long id;

    /**
     * 绑定的中间表id
     */
    @Excel(name = "绑定的中间表id")
    private Long tlmId;

    /**
     * 文本
     */
    @Excel(name = "文本")
    private String content;

    public GenerateText(Long tlmId, String str) {
        this.tlmId = tlmId;
        this.content = str;
    }
}
