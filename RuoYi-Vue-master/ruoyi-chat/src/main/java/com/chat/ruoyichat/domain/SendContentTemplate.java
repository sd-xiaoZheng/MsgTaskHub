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
 * 发送内容模板，用于存储发送内容模板相关信息对象 send_content_template
 *
 * @author ruoyi
 * @date 2025-03-05
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SendContentTemplate extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 发送内容模板唯一标识
     */
    private String templateId;

    /**
     * 关联项目ID
     */
    @Excel(name = "关联项目ID")
    private String projectId;

    /**
     * 模板名称
     */
    @Excel(name = "模板名称")
    private String templateName;

    /**
     * 发送内容ID
     */
    @Excel(name = "发送内容ID")
    private String contentId;

    /**
     * 最近更新人
     */
    @Excel(name = "最近更新人")
    private String updateUser;

    /**
     * 创建人
     */
    @Excel(name = "创建人")
    private String createUser;

    /**
     * 使用量
     */
    @Excel(name = "使用量")
    private String useNum;

    public SendContentTemplate(String projectId, String templateName, String createUser) {
        this.templateId = IdUtils.fastSimpleUUID();
        this.projectId = projectId;
        this.templateName = templateName;
        this.createUser = createUser;
    }

}
