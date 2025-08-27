package com.chat.ruoyichat.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.chat.ruoyichat.domain.dto.NameCount;
import com.chat.ruoyichat.mapper.GenerateTextMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.chat.ruoyichat.mapper.GenerateMiddleMapper;
import com.chat.ruoyichat.domain.GenerateMiddle;
import com.chat.ruoyichat.service.IGenerateMiddleService;
import org.springframework.transaction.annotation.Transactional;

/**
 * GenerateMiddleService业务层处理
 *
 * @author ruoyi
 * @date 2025-06-20
 */
@Service
public class GenerateMiddleServiceImpl implements IGenerateMiddleService {
    @Autowired
    private GenerateMiddleMapper generateMiddleMapper;
    @Autowired
    private GenerateTextMapper generateTextMapper;

    /**
     * 查询GenerateMiddle
     *
     * @param id GenerateMiddle主键
     * @return GenerateMiddle
     */
    @Override
    public GenerateMiddle selectGenerateMiddleById(Long id) {
        return generateMiddleMapper.selectGenerateMiddleById(id);
    }

    /**
     * 查询GenerateMiddle列表
     *
     * @param generateMiddle GenerateMiddle
     * @return GenerateMiddle
     */
    @Override
    public List<GenerateMiddle> selectGenerateMiddleList(GenerateMiddle generateMiddle) {
        List<GenerateMiddle> generateMiddles = generateMiddleMapper.selectGenerateMiddleList(generateMiddle);
        if (generateMiddles.isEmpty()){
            return generateMiddles;
        }
        HashSet<Long> tlmIdSet = new HashSet<>();
        for (GenerateMiddle middle : generateMiddles) {
            tlmIdSet.add(middle.getId());
        }
        HashMap<Long, Long> nameCountMap = new HashMap<>();
        List<NameCount> isFile = generateTextMapper.selectGenerateMiddleByTlmIdSet(tlmIdSet);
        for (NameCount nameCount : isFile) {
            nameCountMap.put(nameCount.getName(), nameCount.getCount());
        }
        for (GenerateMiddle middle : generateMiddles) {
            middle.setCount(nameCountMap.getOrDefault(middle.getId(), -1L));
        }
        return generateMiddles;
    }

    /**
     * 新增GenerateMiddle
     * ·
     *
     * @param generateMiddle GenerateMiddle
     * @return 结果
     */
    @Override
    public int insertGenerateMiddle(GenerateMiddle generateMiddle) {
        return generateMiddleMapper.insertGenerateMiddle(generateMiddle);
    }

    /**
     * 修改GenerateMiddle
     *
     * @param generateMiddle GenerateMiddle
     * @return 结果
     */
    @Override
    public int updateGenerateMiddle(GenerateMiddle generateMiddle) {
        return generateMiddleMapper.updateGenerateMiddle(generateMiddle);
    }

    /**
     * 批量删除GenerateMiddle
     *
     * @param ids 需要删除的GenerateMiddle主键
     * @return 结果
     */
    @Override
    public int deleteGenerateMiddleByIds(Long[] ids) {
        generateTextMapper.deleteGenerateTextByMiddleId(ids[0]);
        return generateMiddleMapper.deleteGenerateMiddleByIds(ids);
    }

    /**
     * 删除GenerateMiddle信息
     *
     * @param id GenerateMiddle主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteGenerateMiddleById(Long id) {
        return generateMiddleMapper.deleteGenerateMiddleById(id);
    }
}
