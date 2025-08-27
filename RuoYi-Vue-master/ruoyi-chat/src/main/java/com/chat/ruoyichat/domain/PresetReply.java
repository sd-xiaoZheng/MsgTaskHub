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

import java.util.ArrayList;

/**
 * 预设回复，用于存储预设回复相关信息对象 preset_reply
 * 
 * @author ruoyi
 * @date 2025-03-05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PresetReply extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 预设回复唯一标识 */
    private String replyId;

    /** 回复权重 */
    @Excel(name = "回复权重")
    private Long replyWeight;

    /** 关联项目 */
    @Excel(name = "关联项目")
    private String projectId;

    /** 预设内容 */
    @Excel(name = "预设内容")
    private String replyContent;

    /** 使用次数 */
    @Excel(name = "使用次数")
    private Long usageCount;

    /** 最近更新人 */
    @Excel(name = "最近更新人")
    private String updateUser;

    /** 预设回复标题 */
    @Excel(name = "预设回复标题")
    private String replyTitle;

    /** 创建人 */
    @Excel(name = "创建人")
    private String createUser;

    public PresetReply(Long replyWeight, String projectId, String replyContent,  String replyTitle) {
        this.replyId = IdUtils.fastSimpleUUID();
        this.replyWeight = replyWeight;
        this.projectId = projectId;
        this.replyContent = replyContent;
        this.replyTitle = replyTitle;
    }
}
