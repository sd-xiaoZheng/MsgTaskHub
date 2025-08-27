package com.chat.ruoyichat.service.impl;

import java.util.List;

import com.chat.ruoyichat.service.ICourseService;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.chat.ruoyichat.mapper.CourseMapper;
import com.ruoyichat.chat.domain.Course;


/**
 * 新手教程Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-03-21
 */
@Service
public class CourseServiceImpl implements ICourseService
{
    @Autowired
    private CourseMapper courseMapper;

    /**
     * 查询新手教程
     * 
     * @param courseId 新手教程主键
     * @return 新手教程
     */
    @Override
    public Course selectCourseByCourseId(Long courseId)
    {
        return courseMapper.selectCourseByCourseId(courseId);
    }

    /**
     * 查询新手教程列表
     * 
     * @param course 新手教程
     * @return 新手教程
     */
    @Override
    public List<Course> selectCourseList(Course course)
    {
        return courseMapper.selectCourseList(course);
    }

    /**
     * 新增新手教程
     * 
     * @param course 新手教程
     * @return 结果
     */
    @Override
    public int insertCourse(Course course)
    {
        course.setCreateTime(DateUtils.getNowDate());
        return courseMapper.insertCourse(course);
    }

    /**
     * 修改新手教程
     * 
     * @param course 新手教程
     * @return 结果
     */
    @Override
    public int updateCourse(Course course)
    {
        course.setUpdateTime(DateUtils.getNowDate());
        return courseMapper.updateCourse(course);
    }

    /**
     * 批量删除新手教程
     * 
     * @param courseIds 需要删除的新手教程主键
     * @return 结果
     */
    @Override
    public int deleteCourseByCourseIds(Long[] courseIds)
    {
        return courseMapper.deleteCourseByCourseIds(courseIds);
    }

    /**
     * 删除新手教程信息
     * 
     * @param courseId 新手教程主键
     * @return 结果
     */
    @Override
    public int deleteCourseByCourseId(Long courseId)
    {
        return courseMapper.deleteCourseByCourseId(courseId);
    }
}
