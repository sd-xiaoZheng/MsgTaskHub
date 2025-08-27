package com.chat.ruoyichat.service;

import java.io.IOException;
import java.util.List;
import com.chat.ruoyichat.domain.SendContentTemplateContent;
import org.springframework.web.multipart.MultipartFile;

/**
 * 发送内容模板内容，用于存储发送内容模板的具体内容相关信息Service接口
 * 
 * @author ruoyi
 * @date 2025-03-05
 */
public interface ISendContentTemplateContentService 
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
     * 批量删除发送内容模板内容，用于存储发送内容模板的具体内容相关信息
     * 
     * @param contentIds 需要删除的发送内容模板内容，用于存储发送内容模板的具体内容相关信息主键集合
     * @return 结果
     */
    public int deleteSendContentTemplateContentByContentIds(String[] contentIds);

    /**
     * 删除发送内容模板内容，用于存储发送内容模板的具体内容相关信息信息
     * 
     * @param contentId 发送内容模板内容，用于存储发送内容模板的具体内容相关信息主键
     * @return 结果
     */
    public int deleteSendContentTemplateContentByContentId(String contentId);

    String importContentTemplateTemp(MultipartFile file) throws IOException;

}
