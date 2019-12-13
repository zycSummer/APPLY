package com.jet.cloud.jetsndcxqyapply.common;

import org.apache.shiro.crypto.hash.SimpleHash;

public class Demo {
    public static void main(String[] args) {
        String password = "admin";//明码
        String algorithmName = "MD5";//加密算法
        Object source = password;//要加密的密码

        Object salt = "admin";//盐值，一般都是用户名或者userid，要保证唯一
        int hashIterations = 1024;//加密次数

        SimpleHash simpleHash = new SimpleHash(algorithmName, source, salt, hashIterations);
        System.out.println(simpleHash);//打印出经过盐值、加密次数、md5后的密码
    }
}
