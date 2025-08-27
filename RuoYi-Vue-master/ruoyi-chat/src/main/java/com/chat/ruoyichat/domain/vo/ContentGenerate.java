package com.chat.ruoyichat.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author RuoYi-Vue-master
 * @since 2025/6/20
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentGenerate {
        private Long tlmId;
        private MultipartFile file;
}