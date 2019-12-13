package com.jet.cloud.jetsndcxqyapply.common;

import org.springframework.util.StringUtils;

//字符串为null判断工具类
public class StringHelper {
    public static String nvl(Object s, String s1) {
        String ss = "";
        if (s instanceof String) {
            ss = (String) s;
        } else {
            if (s != null) {
                ss = String.valueOf(s);
            }
        }
        if (StringUtils.isEmpty(ss))
            return s1;
        return ss;
    }
}
