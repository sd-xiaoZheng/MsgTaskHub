package com.chat.ruoyichat.service;

import java.util.List;
import com.chat.ruoyichat.domain.GenerateTemplate;

/**
 * GenerateTemplateService接口
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public interface IGenerateTemplateService 
{
    /**
     * 查询GenerateTemplate
     * 
     * @param id GenerateTemplate主键
     * @return GenerateTemplate
     */
    public GenerateTemplate selectGenerateTemplateById(Long id);

    /**
     * 查询GenerateTemplate列表
     * 
     * @param generateTemplate GenerateTemplate
     * @return GenerateTemplate集合
     */
    public List<GenerateTemplate> selectGenerateTemplateList(GenerateTemplate generateTemplate);

    /**
     * 新增GenerateTemplate
     * 
     * @param generateTemplate GenerateTemplate
     * @return 结果
     */
    public Long insertGenerateTemplate(GenerateTemplate generateTemplate);

    /**
     * 修改GenerateTemplate
     * 
     * @param generateTemplate GenerateTemplate
     * @return 结果
     */
    public int updateGenerateTemplate(GenerateTemplate generateTemplate);

    /**
     * 批量删除GenerateTemplate
     * 
     * @param ids 需要删除的GenerateTemplate主键集合
     * @return 结果
     */
    public int deleteGenerateTemplateByIds(Long[] ids);

    /**
     * 删除GenerateTemplate信息
     * 
     * @param id GenerateTemplate主键
     * @return 结果
     */
    public int deleteGenerateTemplateById(Long id);
}
