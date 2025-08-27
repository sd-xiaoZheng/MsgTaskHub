package com.ruoyi.web.controller.chat;

import com.chat.ruoyichat.domain.GenerateTemplate;
import com.chat.ruoyichat.domain.GenerateText;
import com.chat.ruoyichat.domain.vo.ContentGenerate;
import com.chat.ruoyichat.domain.vo.EmbellishVo;
import com.chat.ruoyichat.domain.vo.GenerateSetting;
import com.chat.ruoyichat.service.IGenerateTemplateService;
import com.chat.ruoyichat.service.IGenerateTextService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.ruoyi.common.utils.poi.ExcelUtil.*;


/**
 * GenerateTextController
 *
 * @author ruoyi
 * @date 2025-06-20
 */
@RestController
@RequestMapping("/chat/GenerateText")
public class GenerateTextController extends BaseController {
    @Autowired
    private IGenerateTextService generateTextService;
    @Autowired
    private IGenerateTemplateService generateTemplateService;

    /**
     * 查询GenerateText列表
     */
    @PreAuthorize("@ss.hasPermi('chat:GenerateText:list')")
    @GetMapping("/list")
    public TableDataInfo list(GenerateText generateText) {
        startPage();
        List<GenerateText> list = generateTextService.selectGenerateTextList(generateText);
        return getDataTable(list);
    }

    /**
     * 导出GenerateText列表
     */
    @PreAuthorize("@ss.hasPermi('chat:GenerateText:export')")
    @Log(title = "GenerateText", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GenerateText generateText) {
        List<GenerateText> list = generateTextService.selectGenerateTextList(generateText);
        ExcelUtil<GenerateText> util = new ExcelUtil<GenerateText>(GenerateText.class);
        util.exportExcel(response, list, "GenerateText数据");
    }

    /**
     * 获取GenerateText详细信息
     */
    @PreAuthorize("@ss.hasPermi('chat:GenerateText:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(generateTextService.selectGenerateTextById(id));
    }

    /**
     * 新增GenerateText
     */
    @PreAuthorize("@ss.hasPermi('chat:GenerateText:add')")
    @Log(title = "GenerateText", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GenerateText generateText) {
        return toAjax(generateTextService.insertGenerateText(generateText));
    }

    /**
     * 修改GenerateText
     */
    @PreAuthorize("@ss.hasPermi('chat:GenerateText:edit')")
    @Log(title = "GenerateText", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GenerateText generateText) {
        return toAjax(generateTextService.updateGenerateText(generateText));
    }

    /**
     * 删除GenerateText
     */
    @PreAuthorize("@ss.hasPermi('chat:GenerateText:remove')")
    @Log(title = "GenerateText", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(generateTextService.deleteGenerateTextByIds(ids));
    }

    /**
     * 上传内容
     */
    @Log(title = "上传内容", businessType = BusinessType.DELETE)
    @PostMapping("/uploadText")
    public AjaxResult uploadText(ContentGenerate contentGenerate) throws IOException {
        generateTextService.uploadText(contentGenerate);
        return AjaxResult.success();
    }

    /**
     * 润色
     */
    @Log(title = "润色", businessType = BusinessType.DELETE)
    @PostMapping("/embellish")
    public void embellish(HttpServletResponse response, EmbellishVo embellishVo) throws IOException {
        List<String> embellish = generateTextService.embellish(embellishVo);
        ExcelUtil.exportStringListNoHeader(response, embellish, "Text");
    }


    /**
     * 生成内容
     */
    @Log(title = "生成内容", businessType = BusinessType.DELETE)
    @PostMapping("/generate")
    public void generate(HttpServletResponse response, @RequestBody GenerateSetting generateSetting) throws IOException {
        List<String> embellish = generateTextService.generate(generateSetting);
        GenerateTemplate generateTemplate = generateTemplateService.selectGenerateTemplateById(generateSetting.getGenerateTemplateId());
        String format = new SimpleDateFormat("MMddHHmm").format(new Date());
        if (!Objects.isNull(embellish)) {
            //备注-月- 日- 时- 分-数量.xls .zip
            Integer fileFlag = generateSetting.getFileFlag();
            String fileName;
            if (Objects.isNull(generateSetting.getZipFileItem())) {
                fileName = generateTemplate.getGenerateName() + format + "-" + generateSetting.getGenerateNum();
            } else {
                int i = generateSetting.getGenerateNum() * generateSetting.getZipFileItem();
                fileName = generateTemplate.getGenerateName() + format + "-" + i;
            }
            Integer zipFileItem = generateSetting.getZipFileItem();//几个文件一个zip
            if (Objects.nonNull(zipFileItem)) {
                exportFilesByZipNum(response, embellish, fileName, zipFileItem, fileFlag);
            } else {
                switch (fileFlag) {
                    case 1: {
                        ExcelUtil.exportStringListNoHeader(response, embellish, fileName);
                        break;
                    }
                    case 2: {
                        ExcelUtil.exportTxt(response, embellish, fileName);
                        break;
                    }
                    case 3: {
                        ExcelUtil.exportCsv(response, embellish, fileName);
                        break;
                    }
                    default:
                        break;
                }
            }
        }
    }
}

