package com.chat.ruoyichat.service.impl;

import com.chat.ruoyichat.domain.*;
import com.chat.ruoyichat.domain.vo.ContentGenerate;
import com.chat.ruoyichat.domain.vo.EmbellishVo;
import com.chat.ruoyichat.domain.vo.GenerateSetting;
import com.chat.ruoyichat.mapper.*;
import com.chat.ruoyichat.service.IGenerateTextService;
import com.chat.ruoyichat.utils.excel.EasyExcelUtil;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.rmi.ServerException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * GenerateTextService业务层处理
 *
 * @author ruoyi
 * @date 2025-06-20
 */
@Service
public class GenerateTextServiceImpl implements IGenerateTextService {
    @Autowired
    private GenerateTextMapper generateTextMapper;
    @Autowired
    private GenerateTemplateMapper generateTemplateMapper;
    @Autowired
    private GenerateMiddleMapper generateMiddleMapper;
    @Autowired
    private SendContentTemplateMapper sendContentTemplateMapper;
    @Autowired
    private SendContentTemplateContentMapper sendContentTemplateContentMapper;
    @Autowired
    private CustomerServiceDetailMapper customerServiceDetailMapper;

    private static final Random RANDOM = new Random();
    private static final float CAPITALIZE_PROBABILITY = 0.3f;
    private static final float SPACE_REPLACE_PROBABILITY = 0.3f;

    /**
     * 查询GenerateText
     *
     * @param id GenerateText主键
     * @return GenerateText
     */
    @Override
    public GenerateText selectGenerateTextById(Long id) {
        return generateTextMapper.selectGenerateTextById(id);
    }

    /**
     * 查询GenerateText列表
     *
     * @param generateText GenerateText
     * @return GenerateText
     */
    @Override
    public List<GenerateText> selectGenerateTextList(GenerateText generateText) {
        return generateTextMapper.selectGenerateTextList(generateText);
    }

    /**
     * 新增GenerateText
     *
     * @param generateText GenerateText
     * @return 结果
     */
    @Override
    public int insertGenerateText(GenerateText generateText) {
        return generateTextMapper.insertGenerateText(generateText);
    }

    /**
     * 修改GenerateText
     *
     * @param generateText GenerateText
     * @return 结果
     */
    @Override
    public int updateGenerateText(GenerateText generateText) {
        return generateTextMapper.updateGenerateText(generateText);
    }

    /**
     * 批量删除GenerateText
     *
     * @param ids 需要删除的GenerateText主键
     * @return 结果
     */
    @Override
    public int deleteGenerateTextByIds(Long[] ids) {
        return generateTextMapper.deleteGenerateTextByIds(ids);
    }

    /**
     * 删除GenerateText信息
     *
     * @param id GenerateText主键
     * @return 结果
     */
    @Override
    public int deleteGenerateTextById(Long id) {
        return generateTextMapper.deleteGenerateTextById(id);
    }

    @Override
    public void uploadText(ContentGenerate contentGenerate) throws IOException {
        MultipartFile file = contentGenerate.getFile();
        Long tlmId = contentGenerate.getTlmId();
        String fileSuffix = getFileSuffix(file);
        ArrayList<String> contentStrList = new ArrayList<>();
        //判断是那种文件 并获取
        if (!StringUtils.isEmpty(fileSuffix)) {
            getStrings(fileSuffix, file, contentStrList);
        }

        if (contentStrList.isEmpty()) {
            throw new ServerException("空文件");
        }

        ArrayList<GenerateText> generateTexts = new ArrayList<>();
        for (String str : contentStrList) {
            GenerateText generateText = new GenerateText(tlmId, str);
            generateTexts.add(generateText);
        }
        generateTextMapper.deleteGenerateTextByMiddleId(tlmId);
        generateTextMapper.addBatch(generateTexts);
    }

    private static void getStrings(String fileSuffix, MultipartFile file, ArrayList<String> contentStrList) throws IOException {
        if (fileSuffix.equals("txt")) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    contentStrList.add(line);
                }
            } catch (Exception e) {
                throw new RuntimeException("读取txt文件失败", e);
            }
        } else {
            List<Map<Integer, String>> maps = EasyExcelUtil.syncRead(file.getInputStream(), 0, 0);
            maps.stream().flatMap(map -> map.values().stream()).filter(StringUtils::isNotEmpty).forEach(contentStrList::add);
        }
    }

    @Override
    public List<String> embellish(EmbellishVo embellishVo) throws IOException {
        String fileSuffix = getFileSuffix(embellishVo.getFile());
        ArrayList<String> contentStrList = new ArrayList<>();
        //判断是那种文件
        if (!StringUtils.isEmpty(fileSuffix)) {
            getStrings(fileSuffix, embellishVo.getFile(), contentStrList);
        }
        return replaceSpacesRandomly(contentStrList, embellishVo);
    }

    @Override
    public List<String> generate(GenerateSetting generateSetting) {
        Long generateTemplateId = generateSetting.getGenerateTemplateId();
        int generateNum = 0;
        if (Objects.isNull(generateSetting.getZipFileItem())) {
            generateNum = generateSetting.getGenerateNum();
        } else {
            generateNum = generateSetting.getGenerateNum() * generateSetting.getZipFileItem();
        }
        Integer emojiSort = generateSetting.getEmojiSort();
        GenerateTemplate generateTemplate = generateTemplateMapper.selectGenerateTemplateById(generateTemplateId);
        if (Objects.isNull(generateTemplate)) {
            throw new ServiceException("无模板");
        }
        //把话术取出进集合 顺便吧表情排序进去
        boolean emojiSortBl = true; //是否添加过表情
        ArrayList<List<GenerateText>> arrayLists = new ArrayList<>();
        GenerateMiddle generateMiddle = new GenerateMiddle();
        generateMiddle.setTlId(generateTemplateId);
        List<GenerateMiddle> generateMiddles = generateMiddleMapper.selectGenerateMiddleList(generateMiddle);
        for (GenerateMiddle middle : generateMiddles) {
            if (emojiSort != -1 && emojiSortBl) {
                if (emojiSort <= middle.getSort()) {
                    if (generateSetting.getIsEmoji().equals(1)) {
                        GenerateText generateText = new GenerateText();
                        generateText.setTlmId(0L);
                        List<GenerateText> generateTexts = generateTextMapper.selectGenerateTextList(generateText);
                        if (!generateTexts.isEmpty()) {
                            arrayLists.add(generateTexts);
                        }
                    }
                    emojiSortBl = false;
                }
            }
            GenerateText generateText = new GenerateText();
            generateText.setTlmId(middle.getId());
            List<GenerateText> generateTexts = generateTextMapper.selectGenerateTextList(generateText);
            if (!generateTexts.isEmpty()) {
                arrayLists.add(generateTexts);
            }
        }
        if (emojiSort != -1 && emojiSortBl) {
            //需要插入表情了把表情查出来
            if (generateSetting.getIsEmoji().equals(1)) {
                GenerateText generateText = new GenerateText();
                generateText.setTlmId(0L);
                List<GenerateText> generateTexts = generateTextMapper.selectGenerateTextList(generateText);
                if (!generateTexts.isEmpty()) {
                    arrayLists.add(generateTexts);
                }
            }
        }

        //组装话术 要考虑表情的位置
        List<GenerateText> generateEmojis = new ArrayList<>();
        if (emojiSort == -1) {
            GenerateText generateText = new GenerateText();
            generateText.setTlmId(0L);
            generateEmojis = generateTextMapper.selectGenerateTextList(generateText);
        }
        int emojiSize = generateEmojis.size();
        Random random = new Random();
        ArrayList<String> text = new ArrayList<>();
        for (int l = 0; l < generateNum; l++) {
            StringBuilder combinedMessage = new StringBuilder();
            emojiSortBl = true;
            for (List<GenerateText> arrayList : arrayLists) {
                if (arrayList.isEmpty()) {
                    continue;
                }
                int size = arrayList.size();
                int randomIndex = random.nextInt(size);
                if (emojiSort == -1 && emojiSortBl) {
                    if (randomIndex < size / 2) {
                        int randomEmojiIndex = random.nextInt(emojiSize);
                        combinedMessage.append(generateEmojis.get(randomEmojiIndex).getContent()).append(" ");
                        emojiSortBl = false;
                    }
                }
                combinedMessage.append(arrayList.get(randomIndex).getContent()).append(" ");
            }
            text.add(combinedMessage.toString());
        }
        if (generateSetting.getToExcel().equals(1)) {
            //导出excel
            return text;
        } else {
            String projectId;
            Long userId = SecurityUtils.getUserId();
            CustomerServiceDetail customerServiceDetail = customerServiceDetailMapper.selectCustomerDetailByUserId(userId);
            if (Objects.nonNull(customerServiceDetail) && !StringUtils.isEmpty(customerServiceDetail.getProjectId())) {
                projectId = customerServiceDetail.getProjectId();
            } else {
                projectId = "28e9dd50bf484269ad4ad4af0094d247";//TODO 先写死目前项目只是用sms
            }
            String format = new SimpleDateFormat("MMddHHmm").format(new Date());
            String templateName = generateTemplate.getGenerateName() + format + "-" + generateSetting.getGenerateNum();
            //把相同的直接删除
            sendContentTemplateMapper.deleteByTlNameProjectId(templateName, projectId);
            SendContentTemplate sendContentTemplate = new SendContentTemplate(
                    projectId,
                    templateName,
                    SecurityUtils.getLoginUser().getUsername());
            //直接入库
            //填入模板关联内容
            ArrayList<SendContentTemplateContent> contentList = new ArrayList<>();
            for (String s : text) {
                contentList.add(new SendContentTemplateContent(sendContentTemplate.getTemplateId(), s));
            }
            sendContentTemplateContentMapper.insertSendContentTemplateContentBatch(contentList);
            sendContentTemplateMapper.insertSendContentTemplate(sendContentTemplate);
            return null;
        }
    }

    public static String getFileSuffix(MultipartFile file) {
        if (file == null || file.getOriginalFilename() == null) {
            return "";
        }
        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        // 从文件名中提取后缀（考虑点号可能出现在文件名中间的情况）
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < originalFilename.length() - 1) {
            return originalFilename.substring(dotIndex + 1).toLowerCase();
        }
        return ""; // 无后缀或格式异常
    }

    //润色
    public List<String> replaceSpacesRandomly(List<String> inputStrings, EmbellishVo embellishVo) {
        String[] split;
        String salt = embellishVo.getSalt();
        if (salt.contains(",")) {
            split = salt.split(",");
        } else {
            split = new String[]{salt};
        }
        List<String> resultList = new ArrayList<>();
        for (String str : inputStrings) {
            resultList.add(formatText(str, split, embellishVo.getIsUppercase()));
        }
        return resultList;
    }


    //润色
    public static String formatText(String input, String[] split, Boolean isUppercase) {
        StringBuilder result = new StringBuilder();
        boolean isStartOfWord = true;
        boolean isFirstWord = true;
        int length = input.length();

        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);

            if (Character.isWhitespace(c)) {
                // 空格或换行符，触发新单词
                if (RANDOM.nextFloat() < SPACE_REPLACE_PROBABILITY) {
                    result.append(split[RANDOM.nextInt(split.length)]);
                } else {
                    result.append(' ');
                }
                isStartOfWord = true;
            } else if (Character.isLetter(c)) {
                if (isStartOfWord && isUppercase) {
                    if (isFirstWord) {
                        result.append(Character.toUpperCase(c));
                        isFirstWord = false;
                    } else {
                        if (RANDOM.nextFloat() < CAPITALIZE_PROBABILITY) {
                            result.append(Character.toUpperCase(c));
                        } else {
                            result.append(Character.toLowerCase(c));
                        }
                    }
                    isStartOfWord = false;
                } else {
                    result.append(Character.toLowerCase(c));
                }
            } else {
                result.append(c);
                isStartOfWord = true;
            }
        }
        return result.toString();
    }

}
