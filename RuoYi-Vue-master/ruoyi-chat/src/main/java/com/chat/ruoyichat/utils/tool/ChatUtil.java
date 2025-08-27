package com.chat.ruoyichat.utils.tool;

import com.chat.ruoyichat.domain.CustomerServiceDetail;
import com.chat.ruoyichat.service.impl.CustomerServiceDetailServiceImpl;
import com.ruoyi.common.utils.SecurityUtils;
import org.apache.commons.lang3.ObjectUtils;

/**
 * @author chat
 * @since 2025/3/11
 **/
public class ChatUtil {

    public static String getProjectIdByLoginUserId() {
        Long userId = SecurityUtils.getUserId();
        CustomerServiceDetailServiceImpl customerServiceDetailService = new CustomerServiceDetailServiceImpl();
        CustomerServiceDetail customerServiceDetail = customerServiceDetailService.selectCustomerDetailByUserId(userId);
        if (ObjectUtils.isEmpty(customerServiceDetail)) {
            return null;
        }else {
            return customerServiceDetail.getProjectId();
        }
    }
}
