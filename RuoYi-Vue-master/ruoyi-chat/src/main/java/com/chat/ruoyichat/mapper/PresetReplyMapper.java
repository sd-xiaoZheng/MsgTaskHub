package com.chat.ruoyichat.mapper;

import java.util.ArrayList;
import java.util.List;
import com.chat.ruoyichat.domain.PresetReply;
import com.chat.ruoyichat.domain.dto.ReplyNum;
import org.apache.ibatis.annotations.Param;

/**
 * 预设回复，用于存储预设回复相关信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-03-05
 */
public interface PresetReplyMapper 
{
    /**
     * 查询预设回复，用于存储预设回复相关信息
     * 
     * @param replyId 预设回复，用于存储预设回复相关信息主键
     * @return 预设回复，用于存储预设回复相关信息
     */
    public PresetReply selectPresetReplyByReplyId(String replyId);

    /**
     * 查询预设回复，用于存储预设回复相关信息列表
     * 
     * @param presetReply 预设回复，用于存储预设回复相关信息
     * @return 预设回复，用于存储预设回复相关信息集合
     */
    public List<PresetReply> selectPresetReplyList(PresetReply presetReply);

    /**
     * 新增预设回复，用于存储预设回复相关信息
     * 
     * @param presetReply 预设回复，用于存储预设回复相关信息
     * @return 结果
     */
    public int insertPresetReply(PresetReply presetReply);

    /**
     * 修改预设回复，用于存储预设回复相关信息
     * 
     * @param presetReply 预设回复，用于存储预设回复相关信息
     * @return 结果
     */
    public int updatePresetReply(PresetReply presetReply);

    /**
     * 删除预设回复，用于存储预设回复相关信息
     * 
     * @param replyId 预设回复，用于存储预设回复相关信息主键
     * @return 结果
     */
    public int deletePresetReplyByReplyId(String replyId);

    /**
     * 批量删除预设回复，用于存储预设回复相关信息
     * 
     * @param replyIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePresetReplyByReplyIds(String[] replyIds);

    void insertPresetReplyBath(ArrayList<PresetReply> presetReplies);

    PresetReply randomText(@Param("projectId") String projectId,@Param("replyWeight") Long replyWeight);

    int emptyByProject(String projectId);

    ArrayList<ReplyNum> selectCountByreplyWeight(PresetReply presetReply);

    int deletePresetReplyByReply(PresetReply presetReply);

}
