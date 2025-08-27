package com.chat.ruoyichat.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.chat.ruoyichat.utils.excel.EasyExcelUtil;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.chat.ruoyichat.mapper.SendContentTemplateContentMapper;
import com.chat.ruoyichat.domain.SendContentTemplateContent;
import com.chat.ruoyichat.service.ISendContentTemplateContentService;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * 发送内容模板内容，用于存储发送内容模板的具体内容相关信息Service业务层处理
 *
 * @author ruoyi
 * @date 2025-03-05
 */
@Service
public class SendContentTemplateContentServiceImpl implements ISendContentTemplateContentService {
    @Autowired
    private SendContentTemplateContentMapper sendContentTemplateContentMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 查询发送内容模板内容，用于存储发送内容模板的具体内容相关信息
     *
     * @param contentId 发送内容模板内容，用于存储发送内容模板的具体内容相关信息主键
     * @return 发送内容模板内容，用于存储发送内容模板的具体内容相关信息
     */
    @Override
    public SendContentTemplateContent selectSendContentTemplateContentByContentId(String contentId) {
        return sendContentTemplateContentMapper.selectSendContentTemplateContentByContentId(contentId);
    }

    /**
     * 查询发送内容模板内容，用于存储发送内容模板的具体内容相关信息列表
     *
     * @param sendContentTemplateContent 发送内容模板内容，用于存储发送内容模板的具体内容相关信息
     * @return 发送内容模板内容，用于存储发送内容模板的具体内容相关信息
     */
    @Override
    public List<SendContentTemplateContent> selectSendContentTemplateContentList(SendContentTemplateContent sendContentTemplateContent) {
        return sendContentTemplateContentMapper.selectSendContentTemplateContentList(sendContentTemplateContent);
    }

    /**
     * 新增发送内容模板内容，用于存储发送内容模板的具体内容相关信息
     *
     * @param sendContentTemplateContent 发送内容模板内容，用于存储发送内容模板的具体内容相关信息
     * @return 结果
     */
    @Override
    public int insertSendContentTemplateContent(SendContentTemplateContent sendContentTemplateContent) {
        sendContentTemplateContent.setContentId(IdUtils.fastSimpleUUID());
        return sendContentTemplateContentMapper.insertSendContentTemplateContent(sendContentTemplateContent);
    }

    /**
     * 修改发送内容模板内容，用于存储发送内容模板的具体内容相关信息
     *
     * @param sendContentTemplateContent 发送内容模板内容，用于存储发送内容模板的具体内容相关信息
     * @return 结果
     */
    @Override
    public int updateSendContentTemplateContent(SendContentTemplateContent sendContentTemplateContent) {
        return sendContentTemplateContentMapper.updateSendContentTemplateContent(sendContentTemplateContent);
    }

    /**
     * 批量删除发送内容模板内容，用于存储发送内容模板的具体内容相关信息
     *
     * @param contentIds 需要删除的发送内容模板内容，用于存储发送内容模板的具体内容相关信息主键
     * @return 结果
     */
    @Override
    public int deleteSendContentTemplateContentByContentIds(String[] contentIds) {
        return sendContentTemplateContentMapper.deleteSendContentTemplateContentByContentIds(contentIds);
    }

    /**
     * 删除发送内容模板内容，用于存储发送内容模板的具体内容相关信息信息
     *
     * @param contentId 发送内容模板内容，用于存储发送内容模板的具体内容相关信息主键
     * @return 结果
     */
    @Override
    public int deleteSendContentTemplateContentByContentId(String contentId) {
        return sendContentTemplateContentMapper.deleteSendContentTemplateContentByContentId(contentId);
    }

    @Override
    public String importContentTemplateTemp(MultipartFile file) throws IOException {
        List<Map<Integer, String>> maps = EasyExcelUtil.syncRead(file.getInputStream(), 0, 0);
        ArrayList<String> contentStrList = maps
                .stream().
                flatMap(map -> map.values().stream())
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        if (contentStrList.isEmpty()) {
            return "空文件";
        }
        String redisKey = CacheConstants.TEMPALTE_ONES + SecurityUtils.getUserId();
        //如何一次性存入redis并设置过期时间为5分钟
        saveListToRedisWithExpiration(redisKey, contentStrList, 5, TimeUnit.MINUTES);
        return redisKey;
    }



    private void saveListToRedisWithExpiration(String key, List<String> list, long timeout, TimeUnit unit) {
        // 清空已有的列表（可选）
        redisTemplate.delete(key);
        // 一次性将列表元素添加到 Redis 的列表中
        if (!list.isEmpty()) {
            for (Object item : list) {
                redisTemplate.opsForList().rightPush(key, item);
            }
        }
        // 设置过期时间
        redisTemplate.expire(key, timeout, unit);
    }
}
