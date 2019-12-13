package com.jet.cloud.jetsndcxqyapply.web;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: zhangkaifeng.
 * @CreateTime: 2017/5/26 16:15
 * @Description:
 */

public class MySessionListener implements SessionListener {

    private static final Logger logger = LoggerFactory.getLogger(MySessionListener.class);

    @Override
    public void onStart(Session session) {
        logger.info("会话创建:{}", session.getId());
    }

    @Override
    public void onStop(Session session) {
        logger.info("会话停止:{}", session.getId());
    }

    @Override
    public void onExpiration(Session session) {
        logger.info("会话过期:{}", session.getId());
    }
}
