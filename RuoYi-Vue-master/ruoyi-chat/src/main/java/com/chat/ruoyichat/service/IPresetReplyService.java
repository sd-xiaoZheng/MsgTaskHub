package com.chat.ruoyichat.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.chat.ruoyichat.domain.PresetReply;
import com.chat.ruoyichat.domain.dto.ReplyNum;
import org.springframework.web.multipart.MultipartFile;

/**
 * 预设回复，用于存储预设回复相关信息Service接口
 * 
 * @author ruoyi
 * @date 2025-03-05
 */
public interface IPresetReplyService 
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
     * 批量删除预设回复，用于存储预设回复相关信息
     * 
     * @param replyIds 需要删除的预设回复，用于存储预设回复相关信息主键集合
     * @return 结果
     */
    public int deletePresetReplyByReplyIds(String[] replyIds);

    /**
     * 删除预设回复，用于存储预设回复相关信息信息
     * 
     * @param replyId 预设回复，用于存储预设回复相关信息主键
     * @return 结果
     */
    public int deletePresetReplyByReplyId(String replyId);

    String importPresetReplyList(MultipartFile file, String projectId, Long replyWeight, String title) throws IOException;

    String randomText(PresetReply presetReply);

    String emptyByProject(String projectId);

    ArrayList<ReplyNum> listCount(PresetReply presetReply);

    int deleteByWeight(Long weight,String projectId);
}
