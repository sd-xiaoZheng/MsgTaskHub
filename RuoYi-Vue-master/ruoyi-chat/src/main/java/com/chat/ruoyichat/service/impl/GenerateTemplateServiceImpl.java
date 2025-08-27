package com.chat.ruoyichat.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.chat.ruoyichat.domain.GenerateMiddle;
import com.chat.ruoyichat.mapper.GenerateMiddleMapper;
import com.chat.ruoyichat.service.IGenerateMiddleService;
import com.ruoyi.common.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.chat.ruoyichat.mapper.GenerateTemplateMapper;
import com.chat.ruoyichat.domain.GenerateTemplate;
import com.chat.ruoyichat.service.IGenerateTemplateService;
import org.springframework.transaction.annotation.Transactional;

/**
 * GenerateTemplateService业务层处理
 *
 * @author ruoyi
 * @date 2025-06-20
 */
@Service
public class GenerateTemplateServiceImpl implements IGenerateTemplateService {
    @Autowired
    private GenerateTemplateMapper generateTemplateMapper;
    @Autowired
    private GenerateMiddleMapper generateMiddleMapper;
    @Autowired
    private IGenerateMiddleService generateMiddleService;

    /**
     * 查询GenerateTemplate
     *
     * @param id GenerateTemplate主键
     * @return GenerateTemplate
     */
    @Override
    public GenerateTemplate selectGenerateTemplateById(Long id) {
        return generateTemplateMapper.selectGenerateTemplateById(id);
    }

    /**
     * 查询GenerateTemplate列表
     *
     * @param generateTemplate GenerateTemplate
     * @return GenerateTemplate
     */
    @Override
    public List<GenerateTemplate> selectGenerateTemplateList(GenerateTemplate generateTemplate) {
        return generateTemplateMapper.selectGenerateTemplateList(generateTemplate);
    }

    /**
     * 新增GenerateTemplate
     *
     * @param generateTemplate GenerateTemplate
     * @return 结果
     */
    @Override
    @Transactional
    public Long insertGenerateTemplate(GenerateTemplate generateTemplate) {
        List<GenerateTemplate> generateTemplates = generateTemplateMapper.selectGenerateTemplateList(generateTemplate);
        if (!generateTemplates.isEmpty()) {
            throw new ServiceException("名字重复");
        }
        generateTemplateMapper.insertGenerateTemplate(generateTemplate);
        Long tlId = generateTemplate.getId();
        ArrayList<GenerateMiddle> generateMiddles = new ArrayList<>();
        generateMiddles.add(new GenerateMiddle(tlId, "招呼", 0L));
        generateMiddles.add(new GenerateMiddle(tlId, "姓名", 1L));
        generateMiddles.add(new GenerateMiddle(tlId, "主题", 2L));
        generateMiddles.add(new GenerateMiddle(tlId, "结尾", 3L));
        generateMiddleMapper.addBatch(generateMiddles);
        return tlId;
    }

    /**
     * 修改GenerateTemplate
     *
     * @param generateTemplate GenerateTemplate
     * @return 结果
     */
    @Override
    public int updateGenerateTemplate(GenerateTemplate generateTemplate) {
        return generateTemplateMapper.updateGenerateTemplate(generateTemplate);
    }

    /**
     * 批量删除GenerateTemplate
     *
     * @param ids 需要删除的GenerateTemplate主键
     * @return 结果
     */
    @Override
    public int deleteGenerateTemplateByIds(Long[] ids) {
        for (Long id : ids) {
            Long[] longs = {id};
            generateMiddleService.deleteGenerateMiddleByIds(longs);
        }
        return generateTemplateMapper.deleteGenerateTemplateByIds(ids);
    }

    /**
     * 删除GenerateTemplate信息
     *
     * @param id GenerateTemplate主键
     * @return 结果
     */
    @Override
    public int deleteGenerateTemplateById(Long id) {
        return generateTemplateMapper.deleteGenerateTemplateById(id);
    }
}
