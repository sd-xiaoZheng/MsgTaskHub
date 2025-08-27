package com.chat.ruoyichat.service.impl;

import java.util.Collections;
import java.util.List;

import com.chat.ruoyichat.domain.CustomerServiceDetail;
import com.chat.ruoyichat.domain.ProjectSet;
import com.chat.ruoyichat.mapper.CustomerServiceDetailMapper;
import com.chat.ruoyichat.mapper.ProjectSetMapper;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.chat.ruoyichat.mapper.ProjectMapper;
import com.chat.ruoyichat.domain.Project;
import com.chat.ruoyichat.service.IProjectService;

/**
 * 项目管理Service业务层处理
 *
 * @author ruoyi
 * @date 2025-03-04
 */
@Service
public class ProjectServiceImpl implements IProjectService {
    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectSetMapper projectSetMapper;
    @Autowired
    private CustomerServiceDetailMapper customerServiceDetailMapper;

    /**
     * 查询项目管理
     *
     * @param projectId 项目管理主键
     * @return 项目管理
     */
    @Override
    public Project selectProjectByProjectId(String projectId) {
        return projectMapper.selectProjectByProjectId(projectId);
    }

    /**
     * 查询项目管理列表
     *
     * @param project 项目管理
     * @return 项目管理
     */
    @Override
    public List<Project> selectProjectList(Project project) {
        return projectMapper.selectProjectList(project);
    }

    /**
     * 查询所有项目管理列表
     *
     * @param project 项目管理
     * @return 项目管理
     */
    @Override
    public List<Project> selectProjectListALl(Project project) {
        CustomerServiceDetail customerServiceDetail = customerServiceDetailMapper.selectCustomerDetailByUserId(SecurityUtils.getUserId());
        if (ObjectUtils.isEmpty(customerServiceDetail)) {
            return projectMapper.selectProjectList(project);
        } else {
            String projectId = customerServiceDetail.getProjectId();
            if (StringUtils.isEmpty(projectId)) {
                return projectMapper.selectProjectList(project);
            } else {
                Project project1 = new Project();
                project1.setProjectId(projectId);
                List<Project> projects = projectMapper.selectProjectList(project1);
                return projects;
            }
        }

    }

    /**
     * 新增项目管理
     *
     * @param project 项目管理
     * @return 结果
     */
    @Override
    public int insertProject(Project project) {
        project.setCreateUser(SecurityUtils.getUsername());
        project.setCreateTime(DateUtils.getNowDate());
        project.setProjectId(IdUtils.fastSimpleUUID());
        projectMapper.insertProject(project);
        ProjectSet set=new ProjectSet();
        set.setProjectId(project.getProjectId());
        set.setAccountControl(0L);
        set.setCustomerRng(0L);
        set.setLeaderSuffix("admin");
        set.setAccountSendNum(20L);
        set.setMaxSend(168L);
        set.setSendCd(5L);
        return projectSetMapper.insertProjectSet(set);
    }

    /**
     * 修改项目管理
     *
     * @param project 项目管理
     * @return 结果
     */
    @Override
    public int updateProject(Project project) {
        project.setUpdateTime(DateUtils.getNowDate());
        project.setUpdateUser(SecurityUtils.getUsername());
        return projectMapper.updateProject(project);
    }

    /**
     * 批量删除项目管理
     *
     * @param projectIds 需要删除的项目管理主键
     * @return 结果
     */
    @Override
    public int deleteProjectByProjectIds(String[] projectIds) {

        return projectMapper.deleteProjectByProjectIds(projectIds);
    }

    /**
     * 删除项目管理信息
     *
     * @param projectId 项目管理主键
     * @return 结果
     */
    @Override
    public int deleteProjectByProjectId(String projectId) {
        ProjectSet projectSet = projectSetMapper.selectProjectSetByProjectId(projectId);
        projectSetMapper.deleteProjectSetBySetId(projectSet.getSetId());
        return projectMapper.deleteProjectByProjectId(projectId);
    }
}
