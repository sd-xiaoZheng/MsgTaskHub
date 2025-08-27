package com.chat.ruoyichat.service.impl;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.chat.ruoyichat.domain.SendContentTemplateContent;
import com.chat.ruoyichat.mapper.SendContentTemplateContentMapper;
import com.chat.ruoyichat.utils.excel.EasyExcelUtil;
import com.chat.ruoyichat.utils.tool.ChatUtil;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.chat.ruoyichat.mapper.SendContentTemplateMapper;
import com.chat.ruoyichat.domain.SendContentTemplate;
import com.chat.ruoyichat.service.ISendContentTemplateService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 发送内容模板，用于存储发送内容模板相关信息Service业务层处理
 *
 * @author ruoyi
 * @date 2025-03-05
 */
@Service
public class SendContentTemplateServiceImpl implements ISendContentTemplateService {
    @Autowired
    private SendContentTemplateMapper sendContentTemplateMapper;
    @Autowired
    private SendContentTemplateContentMapper sendContentTemplateContentMapper;

    /**
     * 查询发送内容模板，用于存储发送内容模板相关信息
     *
     * @param templateId 发送内容模板，用于存储发送内容模板相关信息主键
     * @return 发送内容模板，用于存储发送内容模板相关信息
     */
    @Override
    public SendContentTemplate selectSendContentTemplateByTemplateId(String templateId) {
        return sendContentTemplateMapper.selectSendContentTemplateByTemplateId(templateId);
    }

    /**
     * 查询发送内容模板，用于存储发送内容模板相关信息列表
     *
     * @param sendContentTemplate 发送内容模板，用于存储发送内容模板相关信息
     * @return 发送内容模板，用于存储发送内容模板相关信息
     */
    @Override
    public List<SendContentTemplate> selectSendContentTemplateList(SendContentTemplate sendContentTemplate) {
        //todo 如果这里性能变慢可以考虑直接sql 查count 但可能要循环查 做取舍
        List<SendContentTemplate> sendContentTemplates = sendContentTemplateMapper.selectSendContentTemplateList(sendContentTemplate);
        if (sendContentTemplates.isEmpty()) {
            return sendContentTemplates;
        }
        HashSet<String> templateId = new HashSet<>();
        for (SendContentTemplate contentTemplate : sendContentTemplates) {
            templateId.add(contentTemplate.getTemplateId());
        }
        ArrayList<SendContentTemplateContent> ContentList =
                sendContentTemplateContentMapper.selectSendContentTemplateContentByContentIds(templateId);
        HashMap<String, ArrayList<SendContentTemplateContent>> useMap = new HashMap<>();
        HashMap<String, ArrayList<SendContentTemplateContent>> ALlUseMap = new HashMap<>();
        for (SendContentTemplateContent content : ContentList) {
            String templateId1 = content.getTemplateId();
            ArrayList<SendContentTemplateContent> ALlUseMapList = ALlUseMap.get(templateId1);
            if (ObjectUtils.isEmpty(ALlUseMapList)) {
                ALlUseMapList = new ArrayList<>();
            }
            ALlUseMapList.add(content);
            ALlUseMap.put(templateId1, ALlUseMapList);
            Long isUse = content.getIsUse();
            if (isUse.equals(1L)) {
                ArrayList<SendContentTemplateContent> useList = useMap.get(templateId1);
                if (ObjectUtils.isEmpty(useList)) {
                    useList = new ArrayList<>();
                }
                useList.add(content);
                useMap.put(templateId1, useList);
            }
        }
        for (SendContentTemplate contentTemplate : sendContentTemplates) {
            String templateId1 = contentTemplate.getTemplateId();
            ArrayList<SendContentTemplateContent> ALlUseNum = ALlUseMap.get(templateId1);
            ArrayList<SendContentTemplateContent> useNum = useMap.get(templateId1);
            if (ObjectUtils.isEmpty(ALlUseNum) || ALlUseNum.isEmpty()) {
                contentTemplate.setUseNum("0/0");
                continue;
            }
            if (ObjectUtils.isEmpty(useNum) || useNum.isEmpty()) {
                contentTemplate.setUseNum(0 + "/" + ALlUseNum.size());
                continue;
            }
            contentTemplate.setUseNum(useNum.size() + "/" + ALlUseNum.size());
        }

        return sendContentTemplates;
    }

    @Override
    public List<SendContentTemplate> selectSendContentTemplateListTemp(SendContentTemplate sendContentTemplate) {
        List<SendContentTemplate> sendContentTemplates = sendContentTemplateMapper.selectSendContentTemplateList(sendContentTemplate);
        if (sendContentTemplates.isEmpty()) {
            return sendContentTemplates;
        }
        // 获取所有templateId
        Set<String> templateIds = sendContentTemplates.stream()
                .map(SendContentTemplate::getTemplateId)
                .collect(Collectors.toSet());
        // 优化1：直接查询统计数量，而不是获取全部数据
        List<Map<String, Object>> countResults = sendContentTemplateContentMapper.countContentByTemplateIds(templateIds);

        // 转换为Map方便查找
        Map<String, ContentCount> countMap = countResults.stream()
                .collect(Collectors.toMap(
                        m -> (String) m.get("templateId"),
                        m -> new ContentCount(
                                ((Number) m.get("totalCount")).longValue(),
                                ((Number) m.get("usedCount")).longValue()
                        )
                ));

        // 设置统计结果
        for (SendContentTemplate contentTemplate : sendContentTemplates) {
            ContentCount counts = countMap.get(contentTemplate.getTemplateId());
            if (counts == null) {
                contentTemplate.setUseNum("0/0");
            } else {
                contentTemplate.setUseNum(counts.usedCount + "/" + counts.totalCount);
            }
        }
        return sendContentTemplates;
    }

    /**
     * 新增发送内容模板，用于存储发送内容模板相关信息
     *
     * @param sendContentTemplate 发送内容模板，用于存储发送内容模板相关信息
     * @return 结果
     */
    @Override
    public int insertSendContentTemplate(SendContentTemplate sendContentTemplate) {
        sendContentTemplate.setTemplateId(IdUtils.fastSimpleUUID());
        sendContentTemplate.setCreateTime(DateUtils.getNowDate());
        return sendContentTemplateMapper.insertSendContentTemplate(sendContentTemplate);
    }

    /**
     * 修改发送内容模板，用于存储发送内容模板相关信息
     *
     * @param sendContentTemplate 发送内容模板，用于存储发送内容模板相关信息
     * @return 结果
     */
    @Override
    public int updateSendContentTemplate(SendContentTemplate sendContentTemplate) {
        sendContentTemplate.setUpdateTime(DateUtils.getNowDate());
        sendContentTemplate.setUpdateUser(SecurityUtils.getUsername());
        return sendContentTemplateMapper.updateSendContentTemplate(sendContentTemplate);
    }

    /**
     * 批量删除发送内容模板，用于存储发送内容模板相关信息
     *
     * @param templateIds 需要删除的发送内容模板，用于存储发送内容模板相关信息主键
     * @return 结果
     */
    @Override
    public int deleteSendContentTemplateByTemplateIds(String[] templateIds) {
        return sendContentTemplateMapper.deleteSendContentTemplateByTemplateIds(templateIds);
    }

    /**
     * 删除发送内容模板，用于存储发送内容模板相关信息信息
     *
     * @param templateId 发送内容模板，用于存储发送内容模板相关信息主键
     * @return 结果
     */
    @Override
    public int deleteSendContentTemplateByTemplateId(String templateId) {
        return sendContentTemplateMapper.deleteSendContentTemplateByTemplateId(templateId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String importContentList(MultipartFile file, String operName, String projectId, String templateName) throws IOException {
        List<Map<Integer, String>> maps = EasyExcelUtil.syncRead(file.getInputStream(), 0, 0);
        ArrayList<String> contentStrList = maps
                .stream().
                flatMap(map -> map.values().stream())
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        if (contentStrList.isEmpty()) {
            return "空文件";
        }
        //如果不传项目id则使用登陆人的所属项目
        if (ObjectUtils.isEmpty(projectId)) {
            projectId = ChatUtil.getProjectIdByLoginUserId();
            if (ObjectUtils.isEmpty(projectId)) {
                throw new ServiceException("导入失败,当前用户不属于任何项目");
            }
        }
        //生成一个发送模板
        SendContentTemplate sendContentTemplate = new SendContentTemplate(projectId, templateName, operName);
        //填入模板关联内容
        ArrayList<SendContentTemplateContent> contentList = new ArrayList<>();
        for (String s : contentStrList) {
            contentList.add(new SendContentTemplateContent(sendContentTemplate.getTemplateId(), s));
        }
        sendContentTemplateContentMapper.insertSendContentTemplateContentBatch(contentList);
        sendContentTemplateMapper.insertSendContentTemplate(sendContentTemplate);
        return "导入成功";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String importPrivate(MultipartFile file, String templateName) throws IOException {
        String projectId = ChatUtil.getProjectIdByLoginUserId();
        if (ObjectUtils.isEmpty(projectId)) {
            throw new ServiceException("导入失败,当前用户不支持导入");
        }
        SendContentTemplate sendContentTemplate1 = new SendContentTemplate();
        sendContentTemplate1.setTemplateName(templateName);
        sendContentTemplate1.setProjectId(projectId);
        List<SendContentTemplate> sendContentTemplates = sendContentTemplateMapper.selectSendContentTemplateList(sendContentTemplate1);
        if (!sendContentTemplates.isEmpty()){
            return "名字重复";
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        List<Map<Integer, String>> maps = EasyExcelUtil.syncRead(file.getInputStream(), 0, 0);
        ArrayList<String> contentStrList = maps
                .stream().
                flatMap(map -> map.values().stream())
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        if (contentStrList.isEmpty()) {
            return "空文件";
        }

        //生成一个发送模板
        SendContentTemplate sendContentTemplate = new SendContentTemplate(projectId, templateName, loginUser.getUsername());
        //填入模板关联内容
        ArrayList<SendContentTemplateContent> contentList = new ArrayList<>();
        for (String s : contentStrList) {
            contentList.add(new SendContentTemplateContent(sendContentTemplate.getTemplateId(), s));
        }
        sendContentTemplateContentMapper.insertSendContentTemplateContentBatch(contentList);
        sendContentTemplateMapper.insertSendContentTemplate(sendContentTemplate);
        return "导入成功";
    }

    @Override
    public void resetByTemplateId(String templateId) {
        sendContentTemplateMapper.resetByTemplateId(templateId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String emptyByProject(String projectId) {
        SendContentTemplate sendContentTemplate = new SendContentTemplate();
        sendContentTemplate.setProjectId(projectId);
        List<SendContentTemplate> sendContentTemplates = sendContentTemplateMapper.selectSendContentTemplateList(sendContentTemplate);
        HashSet<String> templateId = new HashSet<>();
        for (SendContentTemplate contentTemplate : sendContentTemplates) {
            templateId.add(contentTemplate.getTemplateId());
        }
        int i = sendContentTemplateContentMapper.emptyByTemplateId(templateId);
        sendContentTemplateMapper.emptyByProject(projectId);
        return "已清空" + i + "条";
    }

    @Override
    public String deleteByTemplate(String templateId) {
        HashSet<String> templateIdSet = new HashSet<>();
        templateIdSet.add(templateId);
        sendContentTemplateContentMapper.emptyByTemplateId(templateIdSet);
        sendContentTemplateMapper.deleteSendContentTemplateByTemplateId(templateId);
        return "删除完成";
    }




    //辅助类
    private static class ContentCount {
        long totalCount;
        long usedCount;

        ContentCount(long totalCount, long usedCount) {
            this.totalCount = totalCount;
            this.usedCount = usedCount;
        }
    }

}
