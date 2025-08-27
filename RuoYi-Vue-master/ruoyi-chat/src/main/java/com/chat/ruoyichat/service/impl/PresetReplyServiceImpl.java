package com.chat.ruoyichat.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.chat.ruoyichat.domain.dto.ReplyNum;
import com.chat.ruoyichat.utils.excel.EasyExcelUtil;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.chat.ruoyichat.mapper.PresetReplyMapper;
import com.chat.ruoyichat.domain.PresetReply;
import com.chat.ruoyichat.service.IPresetReplyService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 预设回复，用于存储预设回复相关信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-03-05
 */
@Service
public class PresetReplyServiceImpl implements IPresetReplyService 
{
    @Autowired
    private PresetReplyMapper presetReplyMapper;

    /**
     * 查询预设回复，用于存储预设回复相关信息
     * 
     * @param replyId 预设回复，用于存储预设回复相关信息主键
     * @return 预设回复，用于存储预设回复相关信息
     */
    @Override
    public PresetReply selectPresetReplyByReplyId(String replyId)
    {
        return presetReplyMapper.selectPresetReplyByReplyId(replyId);
    }

    /**
     * 查询预设回复，用于存储预设回复相关信息列表
     * 
     * @param presetReply 预设回复，用于存储预设回复相关信息
     * @return 预设回复，用于存储预设回复相关信息
     */
    @Override
    public List<PresetReply> selectPresetReplyList(PresetReply presetReply)
    {
        return presetReplyMapper.selectPresetReplyList(presetReply);
    }


    @Override
    public ArrayList<ReplyNum> listCount(PresetReply presetReply) {
        ArrayList<ReplyNum> replyNumList=presetReplyMapper.selectCountByreplyWeight(presetReply);
        if (replyNumList.isEmpty()){
            return null;
        }
        return replyNumList;
    }

    @Override
    public int deleteByWeight(Long weight,String projectId) {
        PresetReply presetReply = new PresetReply();
        presetReply.setReplyWeight(weight);
        presetReply.setProjectId(projectId);
        return presetReplyMapper.deletePresetReplyByReply(presetReply);
    }

    /**
     * 新增预设回复，用于存储预设回复相关信息
     * 
     * @param presetReply 预设回复，用于存储预设回复相关信息
     * @return 结果
     */
    @Override
    public int insertPresetReply(PresetReply presetReply)
    {
        presetReply.setCreateTime(DateUtils.getNowDate());
        presetReply.setReplyId(IdUtils.fastSimpleUUID());
        return presetReplyMapper.insertPresetReply(presetReply);
    }

    /**
     * 修改预设回复，用于存储预设回复相关信息
     * 
     * @param presetReply 预设回复，用于存储预设回复相关信息
     * @return 结果
     */
    @Override
    public int updatePresetReply(PresetReply presetReply)
    {
        presetReply.setUpdateTime(DateUtils.getNowDate());
        return presetReplyMapper.updatePresetReply(presetReply);
    }

    /**
     * 批量删除预设回复，用于存储预设回复相关信息
     * 
     * @param replyIds 需要删除的预设回复，用于存储预设回复相关信息主键
     * @return 结果
     */
    @Override
    public int deletePresetReplyByReplyIds(String[] replyIds)
    {
        return presetReplyMapper.deletePresetReplyByReplyIds(replyIds);
    }

    /**
     * 删除预设回复，用于存储预设回复相关信息信息
     * 
     * @param replyId 预设回复，用于存储预设回复相关信息主键
     * @return 结果
     */
    @Override
    public int deletePresetReplyByReplyId(String replyId)
    {
        return presetReplyMapper.deletePresetReplyByReplyId(replyId);
    }

    @Override
    public String importPresetReplyList(MultipartFile file, String projectId, Long replyWeight, String title) throws IOException {
        List<Map<Integer, String>> maps = EasyExcelUtil.syncRead(file.getInputStream(), 0, 0);
//        Map<Integer, String> remove = maps.remove(0);//拿到表头
        ArrayList<String> PresetReplyStrList = maps.stream().flatMap(map -> map.values().stream())
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        ArrayList<PresetReply> presetReplies = new ArrayList<>();
        for (String str : PresetReplyStrList) {
            PresetReply presetReply = new PresetReply(replyWeight, projectId, str, title);
            presetReplies.add(presetReply);
        }
        presetReplyMapper.insertPresetReplyBath(presetReplies);
        return "导入成功";
    }

    @Override
    public String randomText(PresetReply presetReply) {
        String projectId = presetReply.getProjectId();
        Long replyWeight = presetReply.getReplyWeight();
        PresetReply reply = presetReplyMapper.randomText(projectId,replyWeight);
        if (ObjectUtils.isEmpty(reply)) {
            throw new ServiceException("数据为空");
        }
        return reply.getReplyContent();
    }

    @Override
    public String emptyByProject(String projectId) {
        int i = presetReplyMapper.emptyByProject(projectId);
        return "已清空"+i+"条";
    }

}
