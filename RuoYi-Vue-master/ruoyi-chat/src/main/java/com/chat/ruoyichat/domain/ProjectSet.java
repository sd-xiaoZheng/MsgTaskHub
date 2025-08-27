package com.chat.ruoyichat.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 项目设置对象 project_set
 *
 * @author ruoyi
 * @date 2025-03-14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProjectSet extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    private Long setId;

    /**
     * 客服后缀是否随机 0顺序 1随机
     */
    private Long customerRng;

    /**
     * 管理员后缀
     */
    private String leaderSuffix;

    /**
     * 账号发送数量
     */
    private Long accountSendNum;

    /**
     * 客服是否产看账号权限
     */
    private Long accountControl;

    /**
     * .-1不验证/正则表达式
     */
    private String regular;

    /**
     * 最大发送字数
     */
    private Long maxSend;
    /**
     * 冷却时间
     */
    private Long sendCd;
    /**
     * 是否允许客服导入任务自定义话术 1是 -1否
     */
    private Long selfTemplate;

    /**
     * 所属项目
     */
    private String projectId;

    /**
     * 账号执行任务间隔时间 s
     */
    private Integer gapTime;

    /**
     * 发送中长间隔时间 s
     */
    private Integer gapLongTime;

    /**
     * 达到账号发送间隔的条数阈值
     */
    private Integer gapValue;

    /**
     * 获取在线客服回复消息的时间
     */
    private Long backMsgTime;

    /**
     * 输入框是否算回复数量1是 -1否
     */
    private Long isSendNum;
}
