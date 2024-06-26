package edu.hubu.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Const {
    // JWT令牌
    public static final String JWT_BLACK_LIST = "jwt:blacklist:";
    // 用户角色，jwt解码后存于response
    public static final String ATTR_USER_ID = "user:id:";
    public static final String ATTR_Client = "client:";
    // 邮箱验证码
    public static final String VERIFY_EMAIL_LIMIT = "verify:email:limit:";
    public static final String VERIFY_EMAIL_DATA = "verify:email:data:";
    // 请求频率限制
    public static final String FLOW_LIMIT_COUNT = "flow:count:";
    public static final String FLOW_LIMIT_BLOCK = "flow:block:";
    // 过滤优先级
    public static final int ORDER_CORS = -102;
    public static final int ORDER_LIMIT = -101;
    // 论坛相关
    public static final String FORUM_WEATHER_CACHE = "weather:cache:";
    public static final String FORUM_IMAGE_COUNTER = "forum:image:";
    public static final String FORUM_TOPIC_CREATE_COUNTER = "forum:create:topic:";
    public static final String FORUM_TOPIC_CREATE_COMMENT = "forum:create:comment:";
    public static final String FORUM_TOPIC_PREVIEW_CACHE = "forum:preview:";
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_NORMAL = "user";
    // 用户黑名单
    public static final String USER_BLACK_LIST = "user:blacklist:";
    public static final String Attr_USER_ROLE = "userRole:";
    private static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
