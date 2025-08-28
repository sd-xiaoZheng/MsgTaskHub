package com.chat.ruoyichat.service.impl;

import java.io.IOException;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.chat.ruoyichat.domain.CustomerServiceDetail;
import com.chat.ruoyichat.mapper.CustomerServiceDetailMapper;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.chat.ruoyichat.mapper.TaskImgMapper;
import com.chat.ruoyichat.domain.TaskImg;
import com.chat.ruoyichat.service.ITaskImgService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * TaskImgService业务层处理
 *
 * @author ruoyi
 * @date 2025-07-07
 */
@Service
public class TaskImgServiceImpl implements ITaskImgService {
    @Autowired
    private TaskImgMapper taskImgMapper;
    @Autowired
    private CustomerServiceDetailMapper customerServiceDetailMapper;

    /**
     * 查询TaskImg
     *
     * @param id TaskImg主键
     * @return TaskImg
     */
    @Override
    public TaskImg selectTaskImgById(Long id) {
        return taskImgMapper.selectTaskImgById(id);
    }

    /**
     * 查询TaskImg列表
     *
     * @param taskImg TaskImg
     * @return TaskImg
     */
    @Override
    public List<TaskImg> selectTaskImgList(TaskImg taskImg) {
        return taskImgMapper.selectTaskImgList(taskImg);
    }

    /**
     * 新增TaskImg
     *
     * @param taskImg TaskImg
     * @return 结果
     */
    @Override
    public int insertTaskImg(TaskImg taskImg) {
        return taskImgMapper.insertTaskImg(taskImg);
    }

    /**
     * 修改TaskImg
     *
     * @param taskImg TaskImg
     * @return 结果
     */
    @Override
    public int updateTaskImg(TaskImg taskImg) {
        return taskImgMapper.updateTaskImg(taskImg);
    }

    /**
     * 批量删除TaskImg
     *
     * @param ids 需要删除的TaskImg主键
     * @return 结果
     */
    @Override
    public int deleteTaskImgByIds(Long[] ids) {
        return taskImgMapper.deleteTaskImgByIds(ids);
    }

    /**
     * 删除TaskImg信息
     *
     * @param id TaskImg主键
     * @return 结果
     */
    @Override
    public int deleteTaskImgById(Long id) {
        return taskImgMapper.deleteTaskImgById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskImg importImgName(MultipartFile file) throws IOException {
        //最多10张照片吧
        if (!file.isEmpty()) {
            String articleImg = FileUploadUtils.upload(RuoYiConfig.getUploadPath(), file);//图片路径
            if (StringUtils.isEmpty(articleImg)) {
                throw new ServerException("照片上传为空");
            }
            Long userId = SecurityUtils.getUserId();
            CustomerServiceDetail customerServiceDetail = customerServiceDetailMapper.selectCustomerDetailByUserId(userId);
//            if(Objects.isNull(customerServiceDetail)||Objects.isNull(customerServiceDetail.getMaxLoad())){
////                taskImgMapper.deleteTaskImgByuserId(userId);//上传前删除
//                taskImgMapper.deleteTaskImgByNoProjectId(userId);//上传前删除
//                TaskImg taskImg = new TaskImg();
//                taskImg.setUserId(userId);
//                taskImg.setPath(articleImg);
//                taskImgMapper.insertTaskImg(taskImg);
//                return taskImg;
//            }
            String projectId1 = customerServiceDetail.getProjectId();
            ArrayList<TaskImg> taskImgList = taskImgMapper.selectTaskImgsByUserId(userId);
            if (taskImgList.size() > 9) {
                throw new ServerException("最多上传10张照片（You can upload up to 10 picture at most.）");
            }

            TaskImg taskImg = new TaskImg();
            taskImg.setUserId(userId);
            taskImg.setProjectId(projectId1);
            taskImg.setPath(articleImg);
            taskImgMapper.insertTaskImg(taskImg);
            return taskImg;
        }
        return null;
    }

    @Override
    public TaskImg getAvatar(Long imgAct) {
        //获取聊天头像
        return taskImgMapper.selectTaskImgAvatarByAct(imgAct);
    }

    @Override
    public TaskImg updateAvatar(MultipartFile file, Long imgAct) throws IOException {
        Long userId = SecurityUtils.getUserId();//获取uiserId
        String articleImg = FileUploadUtils.upload(RuoYiConfig.getUploadPath(), file);//图片路径
        if (StringUtils.isEmpty(articleImg)) {
            throw new ServerException("照片上传为空");
        }

        CustomerServiceDetail customerServiceDetail = customerServiceDetailMapper.selectCustomerDetailByUserId(userId);
        if(Objects.isNull(customerServiceDetail)||Objects.isNull(customerServiceDetail.getMaxLoad())){
            taskImgMapper.deleteTaskImgByuserIdAndAct(userId,imgAct);//上传前删除
            TaskImg taskImg = new TaskImg();
            taskImg.setUserId(userId);
            taskImg.setPath(articleImg);
            taskImg.setImgAct(imgAct);
            taskImgMapper.insertTaskImg(taskImg);
            return taskImg;
        }
        return null;
    }
}
















