package com.chat.ruoyichat.mapper;

import java.util.List;
import com.chat.ruoyichat.domain.SendContentTemplate;
import org.apache.ibatis.annotations.Param;

/**
 * 发送内容模板，用于存储发送内容模板相关信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-03-05
 */
public interface SendContentTemplateMapper 
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
     * 删除发送内容模板，用于存储发送内容模板相关信息
     * 
     * @param templateId 发送内容模板，用于存储发送内容模板相关信息主键
     * @return 结果
     */
    public int deleteSendContentTemplateByTemplateId(String templateId);

    /**
     * 批量删除发送内容模板，用于存储发送内容模板相关信息
     * 
     * @param templateIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSendContentTemplateByTemplateIds(String[] templateIds);

    void resetByTemplateId(String templateId);

    int emptyByProject(String projectId);

    void deleteByTlNameProjectId(@Param("templateName") String templateName, @Param("projectId") String projectId);
}
