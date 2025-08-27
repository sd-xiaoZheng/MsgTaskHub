package com.chat.ruoyichat.service;

import java.util.List;
import com.chat.ruoyichat.domain.Project;

/**
 * 项目管理Service接口
 * 
 * @author ruoyi
 * @date 2025-03-04
 */
public interface IProjectService 
{
    /**
     * 查询项目管理
     * 
     * @param projectId 项目管理主键
     * @return 项目管理
     */
    public Project selectProjectByProjectId(String projectId);

    /**
     * 查询项目管理列表
     * 
     * @param project 项目管理
     * @return 项目管理集合
     */
    public List<Project> selectProjectList(Project project);

    /**
     * 查询项目管理列表所有
     *
     * @param project 项目管理
     * @return 项目管理集合
     */
    List<Project> selectProjectListALl(Project project);
    /**
     * 新增项目管理
     * 
     * @param project 项目管理
     * @return 结果
     */
    public int insertProject(Project project);

    /**
     * 修改项目管理
     * 
     * @param project 项目管理
     * @return 结果
     */
    public int updateProject(Project project);

    /**
     * 批量删除项目管理
     * 
     * @param projectIds 需要删除的项目管理主键集合
     * @return 结果
     */
    public int deleteProjectByProjectIds(String[] projectIds);

    /**
     * 删除项目管理信息
     * 
     * @param projectId 项目管理主键
     * @return 结果
     */
    public int deleteProjectByProjectId(String projectId);
}
