package com.chat.ruoyichat.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author RuoYi-Vue-master
 * @since 2025/6/20
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerateSetting {
    private Long generateTemplateId;//模板id
    private Integer isEmoji;//是否添加表情1天加 0不添加
    private Integer emojiSort;//表情顺序 默认 9999 -1随机
    private Integer generateNum;//生成数量
    private Integer toExcel;//导出到excel 1导出excel 2导入库
    private Integer fileFlag;//1excle 2txt 3csv
    private Integer zipFileItem; //压缩包中文件数量
}
