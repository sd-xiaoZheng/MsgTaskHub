package com.chat.ruoyichat.service;

import java.util.List;
import com.ruoyichat.chat.domain.Course;

/**
 * 新手教程Service接口
 * 
 * @author ruoyi
 * @date 2025-03-21
 */
public interface ICourseService 
{
    /**
     * 查询新手教程
     * 
     * @param courseId 新手教程主键
     * @return 新手教程
     */
    public Course selectCourseByCourseId(Long courseId);

    /**
     * 查询新手教程列表
     * 
     * @param course 新手教程
     * @return 新手教程集合
     */
    public List<Course> selectCourseList(Course course);

    /**
     * 新增新手教程
     * 
     * @param course 新手教程
     * @return 结果
     */
    public int insertCourse(Course course);

    /**
     * 修改新手教程
     * 
     * @param course 新手教程
     * @return 结果
     */
    public int updateCourse(Course course);

    /**
     * 批量删除新手教程
     * 
     * @param courseIds 需要删除的新手教程主键集合
     * @return 结果
     */
    public int deleteCourseByCourseIds(Long[] courseIds);

    /**
     * 删除新手教程信息
     * 
     * @param courseId 新手教程主键
     * @return 结果
     */
    public int deleteCourseByCourseId(Long courseId);
}
