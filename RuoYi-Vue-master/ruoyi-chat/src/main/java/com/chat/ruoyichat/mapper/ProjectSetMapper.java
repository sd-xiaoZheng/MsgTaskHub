package com.chat.ruoyichat.mapper;

import java.util.List;
import com.chat.ruoyichat.domain.ProjectSet;

/**
 * 项目设置Mapper接口
 * 
 * @author ruoyi
 * @date 2025-03-14
 */
public interface ProjectSetMapper 
{
    /**
     * 查询项目设置
     * 
     * @param setId 项目设置主键
     * @return 项目设置
     */
    public ProjectSet selectProjectSetBySetId(Long setId);

    /**
     * 查询项目设置列表
     * 
     * @param projectSet 项目设置
     * @return 项目设置集合
     */
    public List<ProjectSet> selectProjectSetList(ProjectSet projectSet);

    /**
     * 新增项目设置
     * 
     * @param projectSet 项目设置
     * @return 结果
     */
    public int insertProjectSet(ProjectSet projectSet);

    /**
     * 修改项目设置
     * 
     * @param projectSet 项目设置
     * @return 结果
     */
    public int updateProjectSet(ProjectSet projectSet);

    /**
     * 删除项目设置
     * 
     * @param setId 项目设置主键
     * @return 结果
     */
    public int deleteProjectSetBySetId(Long setId);

    /**
     * 批量删除项目设置
     * 
     * @param setIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteProjectSetBySetIds(Long[] setIds);

    ProjectSet selectProjectSetByProjectId(String projectId);
}
