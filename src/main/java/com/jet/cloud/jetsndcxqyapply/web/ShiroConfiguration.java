package com.jet.cloud.jetsndcxqyapply.web;


import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.Filter;
import java.util.*;

/**
 * shiro配置类
 */
@Configuration
public class ShiroConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(ShiroConfiguration.class);

    @Autowired
    private ShiroKickOutSessionProperties shiroSessionProperties;

    @Autowired(required = false)
    private Collection<SessionListener> listeners;

    @Value("${server.servlet.session.timeout}")
    private Long sessoinTimeOut;

    @Bean
    public EhCacheManager getEhCacheManager() {
        EhCacheManager em = new EhCacheManager();
        em.setCacheManagerConfigFile("classpath:ehcache-shiro.xml");
        return em;
    }

    @Bean(name = "myShiroRealm")
    public MyShiroRealm myShiroRealm(EhCacheManager cacheManager) {
        MyShiroRealm realm = new MyShiroRealm();
        realm.setCacheManager(cacheManager);
        return realm;
    }

    /**
     * 加载shiroFilter权限控制规则
     */
    private void loadShiroFilterChain(ShiroFilterFactoryBean shiroFilterFactoryBean) {
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();

        filterChainDefinitionMap.put("/admin/login", "anon");
        filterChainDefinitionMap.put("/user/*", "anon");
        filterChainDefinitionMap.put("/verify/verificationCode", "anon");
        filterChainDefinitionMap.put("/public/**", "anon");
        filterChainDefinitionMap.put("/favicon.ico", "anon");
        filterChainDefinitionMap.put("/getCheckCode", "anon");
        filterChainDefinitionMap.put("/mail/checkVerify", "anon");
        filterChainDefinitionMap.put("/file/**", "anon");
        filterChainDefinitionMap.put("/districtCommittee/getSysPoints", "anon");
        filterChainDefinitionMap.put("/**", "authc,kickout");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
    }

    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultWebSecurityManager securityManager, KickoutSessionControlFilter kickoutSessionControlFilter) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new MShiroFilterFactoryBean();
        // 必须设置 SecurityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
        shiroFilterFactoryBean.setLoginUrl("/admin/login");
        // 登录成功后要跳转的连接
        shiroFilterFactoryBean.setSuccessUrl("/admin/business");
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");
        Map<String, Filter> filtersMap = new HashMap<>();
        filtersMap.put("kickout", kickoutSessionControlFilter);
        shiroFilterFactoryBean.setFilters(filtersMap);
        loadShiroFilterChain(shiroFilterFactoryBean);
        return shiroFilterFactoryBean;
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
        daap.setProxyTargetClass(true);
        return daap;
    }

    @Bean
    public DefaultWebSecurityManager getDefaultWebSecurityManager(MyShiroRealm myShiroRealm, DefaultSessionManager sessionManager) {
        DefaultWebSecurityManager dwsm = new DefaultWebSecurityManager();
        dwsm.setRealm(myShiroRealm);
//      <!-- 用户授权/认证信息Cache, 采用EhCache 缓存 -->
        dwsm.setCacheManager(getEhCacheManager());
        dwsm.setSessionManager(sessionManager);
        return dwsm;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
        aasa.setSecurityManager(securityManager);
        return aasa;
    }


    @Bean
    public KickoutSessionControlFilter getKickOutFilterFactoryBean(EhCacheManager cacheManager, DefaultSessionManager sessionManager) {
        KickoutSessionControlFilter kickoutSessionControlFilter = new KickoutSessionControlFilter();
        kickoutSessionControlFilter.setCacheManager(cacheManager);
        kickoutSessionControlFilter.setKickoutAfter(shiroSessionProperties.isKickoutAfter());
        kickoutSessionControlFilter.setMaxSession(shiroSessionProperties.getMaxSession());
        kickoutSessionControlFilter.setKickoutUrl(shiroSessionProperties.getKickoutUrl());
        return kickoutSessionControlFilter;
    }

    @Bean
    @ConditionalOnMissingBean(Cookie.class)
    public Cookie rememberMeCookie() {
        SimpleCookie rememberMeCookie = new SimpleCookie("shiroId");
        rememberMeCookie.setHttpOnly(true);
        rememberMeCookie.setMaxAge(-1);
        return rememberMeCookie;
    }

    @Bean
    public DefaultSessionManager sessionManager(EhCacheManager cacheManager, SessionDAO sessionDAO, Cookie cookie) {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        SessionListener sessionListener = new MySessionListener();
        listeners = new ArrayList<>();
        listeners.add(sessionListener);
        sessionManager.setCacheManager(cacheManager);
        sessionManager.setGlobalSessionTimeout(sessoinTimeOut);//设置session过期时间
        sessionManager.setSessionDAO(sessionDAO);
        sessionManager.setSessionListeners(listeners);
        sessionManager.setSessionIdCookieEnabled(true);
        sessionManager.setSessionIdCookie(cookie);
        return sessionManager;
    }

    @Bean
    @ConditionalOnMissingBean
    public SessionDAO sessionDAO(EhCacheManager ehCacheManager) {
        EnterpriseCacheSessionDAO sessionDAO = new EnterpriseCacheSessionDAO();
        sessionDAO.setActiveSessionsCacheName("shiroCache");
        Class<? extends SessionIdGenerator> idGenerator = JavaUuidSessionIdGenerator.class;
        if (idGenerator != null) {
            SessionIdGenerator sessionIdGenerator = BeanUtils.instantiate(idGenerator);
            sessionDAO.setSessionIdGenerator(sessionIdGenerator);
        }
        sessionDAO.setCacheManager(ehCacheManager);
        return sessionDAO;
    }

    @Bean
    public SimpleMappingExceptionResolver simpleMappingExceptionResolver() {
        SimpleMappingExceptionResolver exceptionResolver = new SimpleMappingExceptionResolver();
        Properties exceptionMappings = new Properties();
        exceptionMappings.put("org.apache.shiro.authz.UnauthorizedException", "redirect:/403");
        exceptionResolver.setExceptionMappings(exceptionMappings);
        return exceptionResolver;
    }
}
