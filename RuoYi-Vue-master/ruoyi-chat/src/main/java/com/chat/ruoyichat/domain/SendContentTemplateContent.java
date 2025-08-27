package com.chat.ruoyichat.domain;

import com.ruoyi.common.utils.uuid.IdUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 发送内容模板内容，用于存储发送内容模板的具体内容相关信息对象 send_content_template_content
 * 
 * @author ruoyi
 * @date 2025-03-05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SendContentTemplateContent extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 发送内容模板内容的唯一标识符 */
    private String contentId;

    /** 关联模板ID */
    @Excel(name = "关联模板ID")
    private String templateId;

    /** 内容 */
    @Excel(name = "内容")
    private String templateContent;



    /** 是否使用过 */
    @Excel(name = "是否使用过")
    private Long isUse;


    public SendContentTemplateContent(String templateId, String templateContent) {
        this.contentId = IdUtils.fastSimpleUUID();
        this.templateId = templateId;
        this.templateContent = templateContent;
        this.isUse = 0L;
    }
}
