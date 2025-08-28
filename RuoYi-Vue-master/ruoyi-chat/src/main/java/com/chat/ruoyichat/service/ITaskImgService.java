package com.chat.ruoyichat.service;

import java.io.IOException;
import java.util.List;
import com.chat.ruoyichat.domain.TaskImg;
import org.springframework.web.multipart.MultipartFile;

/**
 * TaskImgService接口
 *
 * @author ruoyi
 * @date 2025-07-07
 */
public interface ITaskImgService
{
    /**
     * 查询TaskImg
     *
     * @param id TaskImg主键
     * @return TaskImg
     */
    public TaskImg selectTaskImgById(Long id);

    /**
     * 查询TaskImg列表
     *
     * @param taskImg TaskImg
     * @return TaskImg集合
     */
    public List<TaskImg> selectTaskImgList(TaskImg taskImg);

    /**
     * 新增TaskImg
     *
     * @param taskImg TaskImg
     * @return 结果
     */
    public int insertTaskImg(TaskImg taskImg);

    /**
     * 修改TaskImg
     *
     * @param taskImg TaskImg
     * @return 结果
     */
    public int updateTaskImg(TaskImg taskImg);

    /**
     * 批量删除TaskImg
     *
     * @param ids 需要删除的TaskImg主键集合
     * @return 结果
     */
    public int deleteTaskImgByIds(Long[] ids);

    /**
     * 删除TaskImg信息
     *
     * @param id TaskImg主键
     * @return 结果
     */
    public int deleteTaskImgById(Long id);

    TaskImg importImgName(MultipartFile file) throws IOException;

    TaskImg getAvatar(Long imgAct);

    TaskImg updateAvatar(MultipartFile file, Long imgAct) throws IOException;
}
