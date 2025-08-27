package com.chat.ruoyichat.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * GenerateTemplate对象 generate_template
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public class GenerateTemplate extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 生成模板名称 */
    @Excel(name = "生成模板名称")
    private String generateName;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setGenerateName(String generateName) 
    {
        this.generateName = generateName;
    }

    public String getGenerateName() 
    {
        return generateName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("generateName", getGenerateName())
            .toString();
    }
}
