package com.jet.cloud.jetsndcxqyapply.web;


import com.jet.cloud.jetsndcxqyapply.entity.Authority.TbSysMenuFunc;
import com.jet.cloud.jetsndcxqyapply.entity.SystemUser;
import com.jet.cloud.jetsndcxqyapply.mapper.SystemUserMapper;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.shiro.web.filter.mgt.DefaultFilter.roles;


public class MyShiroRealm extends AuthorizingRealm {

    private static final Logger logger = LoggerFactory.getLogger(MyShiroRealm.class);


    @Autowired
    SystemUserMapper systemUserMapper;

    /**
     * 权限认证，为当前登录的Subject授予角色和权限
     *
     * @see 经测试：本例中该方法的调用时机为需授权资源被访问时（有@RequiresPermissions()注解的接口）
     * @see 经测试：如果连续访问同一个URL（比如刷新），该方法不会被重复调用，Shiro有一个时间间隔（也就是cache时间，在ehcache-shiro.xml中配置），
     * 超过这个时间间隔再刷新页面，该方法会被执行
     */
    @SuppressWarnings("JavadocReference")
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //获取当前登录输入的用户名，等价于(String) principalCollection.fromRealm(getName()).iterator().next();
        String loginId = (String) super.getAvailablePrincipal(principalCollection);
        SystemUser user = systemUserMapper.getUserByUserId(loginId);
        if (user != null) {
            //权限信息对象info,用来存放查出的用户的所有的角色（role）及权限（permission）
            String roleId = user.getRoleId();
            List<TbSysMenuFunc> tbSysMenuFunc = systemUserMapper.getTbSysMenuFunc(roleId);
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            Set<String> set = new HashSet<>();
            for (TbSysMenuFunc s : tbSysMenuFunc) {
                if (s.getUrl() != null && !"".equals(s.getUrl())) {
                    set.add(s.getUrl());
                }
            }
            info.setStringPermissions(set);
            return info;
        }
        /*
         * 返回null的话，就会导致任何用户访问被拦截的请求时，都会自动跳转到unauthorizedUrl指定的地址
         * 本项目中，该配置在ShiroConfiguration类中的shiroFilterFactoryBean.setUnauthorizedUrl("/403");语句
         */
        return null;
    }


    /**
     * 登录认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken authenticationToken) throws AuthenticationException {
        //UsernamePasswordToken对象用来存放提交的登录信息
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        SystemUser sysUser = systemUserMapper.getUserByUserId(token.getUsername());
        if (sysUser != null) {
            // 若存在，将此用户存放到登录认证info中，无需自己做密码对比，Shiro会为我们进行密码对比校验
            return new SimpleAuthenticationInfo(sysUser.getUserId(), sysUser.getUserPwd(), getName());
        }
        return null;
    }
}
