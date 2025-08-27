package com.chat.ruoyichat.service;

import java.util.List;
import com.chat.ruoyichat.domain.GenerateMiddle;

/**
 * GenerateMiddleService接口
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public interface IGenerateMiddleService 
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
     * 批量删除GenerateMiddle
     * 
     * @param ids 需要删除的GenerateMiddle主键集合
     * @return 结果
     */
    public int deleteGenerateMiddleByIds(Long[] ids);

    /**
     * 删除GenerateMiddle信息
     * 
     * @param id GenerateMiddle主键
     * @return 结果
     */
    public int deleteGenerateMiddleById(Long id);
}
