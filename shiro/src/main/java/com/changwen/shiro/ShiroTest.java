package com.changwen.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.Test;

import java.util.Arrays;

/**
 * <b>function:</b>
 *
 * @author liucw on 2016/3/20.
 */
public class ShiroTest {
    /**
     * Apache Shiro（日语“堡垒（Castle）”的意思）是一个强大易用的Java安全框架，提供了认证、授权、加密和会话管理功能，可为任何应用提供安全保障 - 从命令行应用、移动应用到大型网络及企业应用。
     Shiro为解决下列问题（我喜欢称它们为应用安全的四要素）提供了保护应用的API：
     认证(Authentication) - 用户身份识别，常被称为用户“登录”；
     授权(Authorization) - 访问控制；
     密码加密(Cryptography) - 保护或隐藏数据防止被偷窥；
     会话管理（Session Management) - 每用户相关的时间敏感的状态。
     Shiro还支持一些辅助特性，如Web应用安全、单元测试和多线程，它们的存在强化了上面提到的四个要素。
     */
    public  Subject login(String configFile,String userName,String password){
        // 读取配置文件，初始化SecurityManager工厂
        Factory<SecurityManager> factory=new IniSecurityManagerFactory(configFile);
        // 获取securityManager实例
        SecurityManager securityManager=factory.getInstance();
        // 把securityManager实例绑定到SecurityUtils
        SecurityUtils.setSecurityManager(securityManager);
        // 得到当前执行的用户
        Subject currentUser=SecurityUtils.getSubject();
        // 创建token令牌，用户名/密码
        UsernamePasswordToken token=new UsernamePasswordToken(userName, password);
        try{
            // 身份认证
            currentUser.login(token);
            System.out.println("身份认证成功！");
        }catch(AuthenticationException e){
            e.printStackTrace();
            System.out.println("身份认证失败！");
        }
        return currentUser;
    }

    @Test
    public void testHelloWorld() {
        Subject currentUser= login("classpath:shiroHelloWorld.ini", "java1234", "123456");

        // 退出
        currentUser.logout();
    }

    /**
     * Subject 认证主体包含两个信息：
     *   Principals：身份，可以是用户名，邮件，手机号码等等，用来标识一个登录主体身份；
     *   Credentials：凭证，常见有密码，数字证书等等；
     *
     * Realm&JDBC Reaml
     *  Realm：意思是域，Shiro 从 Realm 中获取验证数据；
     *  Realm 有很多种类，例如常见的 jdbc realm，jndi realm
     */
    @Test
    public void testJdbcRealm() {
        Subject currentUser= login("classpath:jdbc_realm.ini", "changwen", "123456");

        // 退出
        currentUser.logout();
    }


    /**************************权限认证(授权)*******************************/
    /**
     * 一、权限认证核心要素
     * 权限认证，也就是访问控制，即在应用中控制谁能访问哪些资源。
     * 在权限认证中，最核心的三个要素是：权限，角色和用户；
     *   1、权限，即操作资源的权利，比如访问某个页面，以及对某个模块的数据的添加，修改，删除，查看的权利；
     *   2、角色，是权限的集合，一中角色可以包含多种权限；
     *   3、用户，在 Shiro 中，代表访问系统的用户，即 Subject；
     */
    /**
     * 二、授权
     * 1，编程式授权
     *   1.1 基于角色的访问控制
     *   1.2 基于权限的访问控制
     * 2，注解式授权
     * 3，Jsp 标签授权
     */
    //1.1 基于角色的访问控制
    @Test
    public void testHasRole() {
        Subject currentUser= login("classpath:shiro_role.ini", "java1234", "123456");
        // Subject currentUser=ShiroUtil.login("classpath:shiro_role.ini", "jack", "123");

        //hasRole(String roleName)判断roleName是否有这个权限
        System.out.println(currentUser.hasRole("role1")?"有role1这个角色":"没有role1这个角色");

        //hasRoles
        boolean []results=currentUser.hasRoles(Arrays.asList("role1", "role2", "role3"));

        System.out.println(results[0]?"有role1这个角色":"没有role1这个角色");
        System.out.println(results[1]?"有role2这个角色":"没有role2这个角色");
        System.out.println(results[2]?"有role3这个角色":"没有role3这个角色");
        //hasAllRoles
        System.out.println(currentUser.hasAllRoles(Arrays.asList("role1","role2"))?"role1,role2这两个角色都有":"role1,role2这个两个角色不全有");

        currentUser.logout();
    }

    @Test
    public void testCheckRole() {
        Subject currentUser=login("classpath:shiro_role.ini", "java1234", "123456");
        // Subject currentUser=ShiroUtil.login("classpath:shiro_role.ini", "jack", "123");
        currentUser.checkRole("role1");
        currentUser.checkRoles(Arrays.asList("role1","role2"));
        //currentUser.checkRoles("role1","role2","role3");

        currentUser.logout();
    }

    /***************************
     *1.2 基于权限的访问控制
     */
    @Test
    public void testIsPermitted() {
        Subject currentUser= login("classpath:shiro_permission.ini", "java1234", "123456");
        // Subject currentUser=ShiroUtil.login("classpath:shiro_permission.ini", "jack", "123");
        System.out.println(currentUser.isPermitted("user:select")?"有user:select这个权限":"没有user:select这个权限");
        System.out.println(currentUser.isPermitted("user:update")?"有user:update这个权限":"没有user:update这个权限");
        boolean results[]=currentUser.isPermitted("user:select","user:update","user:delete");
        System.out.println(results[0]?"有user:select这个权限":"没有user:select这个权限");
        System.out.println(results[1]?"有user:update这个权限":"没有user:update这个权限");
        System.out.println(results[2]?"有user:delete这个权限":"没有user:delete这个权限");
        System.out.println(currentUser.isPermittedAll("user:select","user:update")?"有user:select,update这两个权限":"user:select,update这两个权限不全有");

        currentUser.logout();
    }
    @Test
    public void testCheckPermitted() {
        Subject currentUser= login("classpath:shiro_permission.ini", "java1234", "123456");
        // Subject currentUser=ShiroUtil.login("classpath:shiro_permission.ini", "jack", "123");
        currentUser.checkPermission("user:select");
        currentUser.checkPermissions("user:select","user:update","user:delete");
        currentUser.logout();
    }

}
