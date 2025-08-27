package com.chat.ruoyichat.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.chat.ruoyichat.mapper.ProjectSetMapper;
import com.chat.ruoyichat.domain.ProjectSet;
import com.chat.ruoyichat.service.IProjectSetService;

/**
 * 项目设置Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-03-14
 */
@Service
public class ProjectSetServiceImpl implements IProjectSetService 
{
    @Autowired
    private ProjectSetMapper projectSetMapper;

    /**
     * 查询项目设置
     * 
     * @param setId 项目设置主键
     * @return 项目设置
     */
    @Override
    public ProjectSet selectProjectSetBySetId(Long setId)
    {
        return projectSetMapper.selectProjectSetBySetId(setId);
    }
    @Override
    public ProjectSet selectProjectSetByProjectId(String projectId)
    {
        return projectSetMapper.selectProjectSetByProjectId(projectId);
    }

    /**
     * 查询项目设置列表
     * 
     * @param projectSet 项目设置
     * @return 项目设置
     */
    @Override
    public List<ProjectSet> selectProjectSetList(ProjectSet projectSet)
    {
        return projectSetMapper.selectProjectSetList(projectSet);
    }

    /**
     * 新增项目设置
     * 
     * @param projectSet 项目设置
     * @return 结果
     */
    @Override
    public int insertProjectSet(ProjectSet projectSet)
    {
        return projectSetMapper.insertProjectSet(projectSet);
    }

    /**
     * 修改项目设置
     * 
     * @param projectSet 项目设置
     * @return 结果
     */
    @Override
    public int updateProjectSet(ProjectSet projectSet)
    {
        return projectSetMapper.updateProjectSet(projectSet);
    }

    /**
     * 批量删除项目设置
     * 
     * @param setIds 需要删除的项目设置主键
     * @return 结果
     */
    @Override
    public int deleteProjectSetBySetIds(Long[] setIds)
    {
        return projectSetMapper.deleteProjectSetBySetIds(setIds);
    }

    /**
     * 删除项目设置信息
     * 
     * @param setId 项目设置主键
     * @return 结果
     */
    @Override
    public int deleteProjectSetBySetId(Long setId)
    {
        return projectSetMapper.deleteProjectSetBySetId(setId);
    }
}
