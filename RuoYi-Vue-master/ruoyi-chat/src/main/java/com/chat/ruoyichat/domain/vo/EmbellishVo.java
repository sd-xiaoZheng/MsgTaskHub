package com.chat.ruoyichat.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author sms
 * @since 2025/6/24
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmbellishVo {
    private Boolean isUppercase;//是否大小写
    private String salt;//字符逗号隔开
    private MultipartFile file;
}
