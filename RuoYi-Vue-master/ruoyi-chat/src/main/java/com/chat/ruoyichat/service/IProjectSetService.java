package com.chat.ruoyichat.service;

import java.util.List;
import com.chat.ruoyichat.domain.ProjectSet;

/**
 * 项目设置Service接口
 * 
 * @author ruoyi
 * @date 2025-03-14
 */
public interface IProjectSetService 
{
    /**
     * 查询项目设置
     * 
     * @param setId 项目设置主键
     * @return 项目设置
     */
    public ProjectSet selectProjectSetBySetId(Long setId);

    ProjectSet selectProjectSetByProjectId(String projectId);

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
     * 批量删除项目设置
     * 
     * @param setIds 需要删除的项目设置主键集合
     * @return 结果
     */
    public int deleteProjectSetBySetIds(Long[] setIds);

    /**
     * 删除项目设置信息
     * 
     * @param setId 项目设置主键
     * @return 结果
     */
    public int deleteProjectSetBySetId(Long setId);
}
