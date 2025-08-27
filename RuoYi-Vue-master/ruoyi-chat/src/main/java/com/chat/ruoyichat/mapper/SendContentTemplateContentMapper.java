package com.chat.ruoyichat.mapper;

import java.util.*;

import com.chat.ruoyichat.domain.SendContentTemplate;
import com.chat.ruoyichat.domain.SendContentTemplateContent;
import org.apache.ibatis.annotations.Param;

/**
 * 发送内容模板内容，用于存储发送内容模板的具体内容相关信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-03-05
 */
public interface SendContentTemplateContentMapper 
{
    /**
     * 查询发送内容模板内容，用于存储发送内容模板的具体内容相关信息
     * 
     * @param contentId 发送内容模板内容，用于存储发送内容模板的具体内容相关信息主键
     * @return 发送内容模板内容，用于存储发送内容模板的具体内容相关信息
     */
    public SendContentTemplateContent selectSendContentTemplateContentByContentId(String contentId);

    /**
     * 查询发送内容模板内容，用于存储发送内容模板的具体内容相关信息列表
     * 
     * @param sendContentTemplateContent 发送内容模板内容，用于存储发送内容模板的具体内容相关信息
     * @return 发送内容模板内容，用于存储发送内容模板的具体内容相关信息集合
     */
    public List<SendContentTemplateContent> selectSendContentTemplateContentList(SendContentTemplateContent sendContentTemplateContent);

    /**
     * 新增发送内容模板内容，用于存储发送内容模板的具体内容相关信息
     * 
     * @param sendContentTemplateContent 发送内容模板内容，用于存储发送内容模板的具体内容相关信息
     * @return 结果
     */
    public int insertSendContentTemplateContent(SendContentTemplateContent sendContentTemplateContent);

    /**
     * 修改发送内容模板内容，用于存储发送内容模板的具体内容相关信息
     * 
     * @param sendContentTemplateContent 发送内容模板内容，用于存储发送内容模板的具体内容相关信息
     * @return 结果
     */
    public int updateSendContentTemplateContent(SendContentTemplateContent sendContentTemplateContent);

    /**
     * 删除发送内容模板内容，用于存储发送内容模板的具体内容相关信息
     * 
     * @param contentId 发送内容模板内容，用于存储发送内容模板的具体内容相关信息主键
     * @return 结果
     */
    public int deleteSendContentTemplateContentByContentId(String contentId);

    /**
     * 批量删除发送内容模板内容，用于存储发送内容模板的具体内容相关信息
     * 
     * @param contentIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSendContentTemplateContentByContentIds(String[] contentIds);

    void insertSendContentTemplateContentBatch(ArrayList<SendContentTemplateContent> contentList);

    void updateSendContentTemplateContent2IsUseBatch(ArrayList<SendContentTemplateContent> sendContentTemplateContents);

    void updateSendContentTemplateContentReset(String templateId);

    ArrayList<SendContentTemplateContent> selectSendContentTemplateContentByContentIds(HashSet<String> templateId);


    int emptyByTemplateId(HashSet<String> templateId);

    List<Map<String, Object>> countContentByTemplateIds(Set<String> templateIds);
}
