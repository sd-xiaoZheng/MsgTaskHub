package com.chat.ruoyichat.service;

import java.io.IOException;
import java.util.List;
import com.chat.ruoyichat.domain.SendContentTemplate;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

/**
 * 发送内容模板，用于存储发送内容模板相关信息Service接口
 * 
 * @author ruoyi
 * @date 2025-03-05
 */
public interface ISendContentTemplateService 
{
    /**
     * 查询发送内容模板，用于存储发送内容模板相关信息
     * 
     * @param templateId 发送内容模板，用于存储发送内容模板相关信息主键
     * @return 发送内容模板，用于存储发送内容模板相关信息
     */
    public SendContentTemplate selectSendContentTemplateByTemplateId(String templateId);

    /**
     * 查询发送内容模板，用于存储发送内容模板相关信息列表
     * 
     * @param sendContentTemplate 发送内容模板，用于存储发送内容模板相关信息
     * @return 发送内容模板，用于存储发送内容模板相关信息集合
     */
    public List<SendContentTemplate> selectSendContentTemplateList(SendContentTemplate sendContentTemplate);

    /**
     * 新增发送内容模板，用于存储发送内容模板相关信息
     * 
     * @param sendContentTemplate 发送内容模板，用于存储发送内容模板相关信息
     * @return 结果
     */
    public int insertSendContentTemplate(SendContentTemplate sendContentTemplate);

    /**
     * 修改发送内容模板，用于存储发送内容模板相关信息
     * 
     * @param sendContentTemplate 发送内容模板，用于存储发送内容模板相关信息
     * @return 结果
     */
    public int updateSendContentTemplate(SendContentTemplate sendContentTemplate);

    /**
     * 批量删除发送内容模板，用于存储发送内容模板相关信息
     * 
     * @param templateIds 需要删除的发送内容模板，用于存储发送内容模板相关信息主键集合
     * @return 结果
     */
    public int deleteSendContentTemplateByTemplateIds(String[] templateIds);

    /**
     * 删除发送内容模板，用于存储发送内容模板相关信息信息
     * 
     * @param templateId 发送内容模板，用于存储发送内容模板相关信息主键
     * @return 结果
     */
    public int deleteSendContentTemplateByTemplateId(String templateId);

    String importContentList(MultipartFile file, String operName, String projectId,String templateName) throws IOException;

    String importPrivate(MultipartFile file, String templateName) throws IOException;

    void resetByTemplateId(String templateId);

    String emptyByProject(String projectId);

    String deleteByTemplate(@Param("templateId") String templateId);

    List<SendContentTemplate> selectSendContentTemplateListTemp(SendContentTemplate sendContentTemplate);
}
