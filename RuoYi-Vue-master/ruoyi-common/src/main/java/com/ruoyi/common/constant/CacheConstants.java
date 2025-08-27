package com.ruoyi.common.constant;

/**
 * 缓存的key 常量
 *
 * @author ruoyi
 */
public class CacheConstants
{
    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 参数管理 cache key
     */
    public static final String SYS_CONFIG_KEY = "sys_config:";

    /**
     * 字典管理 cache key
     */
    public static final String SYS_DICT_KEY = "sys_dict:";

    /**
     * 防重提交 redis key
     */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    /**
     * 限流 redis key
     */
    public static final String RATE_LIMIT_KEY = "rate_limit:";

    /**
     * 登录账户密码错误次数 redis key
     */
    public static final String PWD_ERR_CNT_KEY = "pwd_err_cnt:";


    /**
     * 上传个人模板的模板id
     */
    public static final String TEMPALTE_ONES = "content_template_temp:";


    /**
     * 账号使用情况前缀
     */
    public static final String ACCOUNT_NUM = "account_num:";
    /**
     * 任务使用情况前缀
     */
    public static final String TASK_NUM = "task_num:";
    /**
     * messagekey
     * 聊天的优先级最高 存入redis
     */
    public static final String MESSAGE_KEY = "message_key:";

//    查看哪个账号有回复
    public static final String IS_REPLY_KEY = "is_Reply_Key:";

//  定期把空闲账号推入redis
    public static final String No_Ass_IN_REDIS = "noAssInRedis:";

//  回复数量+1
    public static final String REPLY_NUM = "Surplus_Num:";

//  成功数量+1
    public static final String SUCCESS_Num = "Success_Num:";

//  发送数量+1
    public static final String SEND_NUM = "Send_Num:";

//  缓存脚本获取客服
    public static final String binCsd = "bin_csd:";

//  存储Token
    public static final String Token = "Token:";

}
