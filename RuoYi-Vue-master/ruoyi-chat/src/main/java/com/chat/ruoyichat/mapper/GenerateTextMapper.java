package com.chat.ruoyichat.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import com.chat.ruoyichat.domain.GenerateText;
import com.chat.ruoyichat.domain.dto.NameCount;
import org.apache.ibatis.annotations.Param;

/**
 * GenerateTextMapper接口
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public interface GenerateTextMapper 
{
    /**
     * 查询GenerateText
     * 
     * @param id GenerateText主键
     * @return GenerateText
     */
    public GenerateText selectGenerateTextById(Long id);

    /**
     * 查询GenerateText列表
     * 
     * @param generateText GenerateText
     * @return GenerateText集合
     */
    public List<GenerateText> selectGenerateTextList(GenerateText generateText);

    /**
     * 新增GenerateText
     * 
     * @param generateText GenerateText
     * @return 结果
     */
    public int insertGenerateText(GenerateText generateText);

    /**
     * 修改GenerateText
     * 
     * @param generateText GenerateText
     * @return 结果
     */
    public int updateGenerateText(GenerateText generateText);

    /**
     * 删除GenerateText
     * 
     * @param id GenerateText主键
     * @return 结果
     */
    public int deleteGenerateTextById(Long id);

    /**
     * 批量删除GenerateText
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGenerateTextByIds(Long[] ids);

    void addBatch(ArrayList<GenerateText> generateTexts);

    void deleteGenerateTextByMiddleId(@Param("tlmId") Long tlmId);

    List<NameCount> selectGenerateMiddleByTlmIdSet(HashSet<Long> tlmIdSet);
}
