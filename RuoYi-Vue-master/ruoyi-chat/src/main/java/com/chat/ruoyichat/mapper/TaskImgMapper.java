package com.chat.ruoyichat.mapper;

import java.util.ArrayList;
import java.util.List;
import com.chat.ruoyichat.domain.TaskImg;
import org.apache.ibatis.annotations.Param;

/**
 * TaskImgMapper接口
 *
 * @author ruoyi
 * @date 2025-07-07
 */
public interface TaskImgMapper
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
     * 删除TaskImg
     *
     * @param id TaskImg主键
     * @return 结果
     */
    public int deleteTaskImgById(Long id);

    /**
     * 批量删除TaskImg
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTaskImgByIds(Long[] ids);

    void deleteTaskImgByuserId(@Param("userId") Long userId);

    TaskImg selectTaskImgByUserId(Long userId);

    ArrayList<TaskImg> selectTaskImgsByUserId(Long userId);

    void deleteTaskImgByNoProjectId(Long userId);

    TaskImg selectTaskImgAvatar();

    TaskImg randOneByUserId(Long userId);

    void deleteTaskImgByuserIdAndAct(@Param("userId")Long userId,@Param("imgAct") Long imgAct);

    TaskImg selectTaskImgAvatarByAct(@Param("act")Long act);
}
