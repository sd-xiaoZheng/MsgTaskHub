package com.chat.ruoyichat.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import com.chat.ruoyichat.domain.GenerateMiddle;
import com.chat.ruoyichat.domain.dto.NameCount;

/**
 * GenerateMiddleMapper接口
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public interface GenerateMiddleMapper 
{
    /**
     * 查询GenerateMiddle
     * 
     * @param id GenerateMiddle主键
     * @return GenerateMiddle
     */
    public GenerateMiddle selectGenerateMiddleById(Long id);

    /**
     * 查询GenerateMiddle列表
     * 
     * @param generateMiddle GenerateMiddle
     * @return GenerateMiddle集合
     */
    public List<GenerateMiddle> selectGenerateMiddleList(GenerateMiddle generateMiddle);

    /**
     * 新增GenerateMiddle
     * 
     * @param generateMiddle GenerateMiddle
     * @return 结果
     */
    public int insertGenerateMiddle(GenerateMiddle generateMiddle);

    /**
     * 修改GenerateMiddle
     * 
     * @param generateMiddle GenerateMiddle
     * @return 结果
     */
    public int updateGenerateMiddle(GenerateMiddle generateMiddle);

    /**
     * 删除GenerateMiddle
     * 
     * @param id GenerateMiddle主键
     * @return 结果
     */
    public int deleteGenerateMiddleById(Long id);

    /**
     * 批量删除GenerateMiddle
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGenerateMiddleByIds(Long[] ids);

    void addBatch(ArrayList<GenerateMiddle> generateMiddles);
}
