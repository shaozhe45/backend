package com.changqing.gov.oauth.utils;

import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LdapAuthentication {
    @Value("${LDAP.FACTORY}")
    private  String FACTORY;
    @Value("${LDAP.BASEDN}")
    private  String BASEDN;
    @Value("${LDAP.HOST}")
    private  String HOST;
    @Value("${LDAP.PORT}")
    private  String PORT;
    @Value("${LDAP.USER}")
    private  String USER;
    @Value("${LDAP.PASSWORD}")
    private  String PASSWORD;
    @Value("${LDAP.AUTHPARAM}")
    private  String AUTHPARAM;
    @Value("${LDAP.URL}")
    private  String URL;
    private  Control[] connCtls = null;
    private LdapContext ctx = null;

    private void LDAP_connect() {

        URL = String.format(URL, HOST, PORT);
        USER = String.format(USER, BASEDN);
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, FACTORY);
        env.put(Context.PROVIDER_URL, URL + BASEDN);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, USER);
        env.put(Context.SECURITY_CREDENTIALS, PASSWORD);
        // 此处若不指定用户名和密码,则自动转换为匿名登录
        try {
            ctx = new InitialLdapContext(env, connCtls);
        } catch (AuthenticationException e) {
            System.out.println("验证失败： " + e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getUserDN(String uid) {
        StringBuilder userDN = new StringBuilder();
        LDAP_connect();
        try {
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration<SearchResult> en = ctx.search("", AUTHPARAM+"=" + uid, constraints);
            if (en == null || !en.hasMoreElements()) {
                System.out.println("未找到该用户");
            }
            // maybe more than one element
            while (en != null && en.hasMoreElements()) {
                SearchResult obj = en.nextElement();
                if (obj != null) {
                    userDN.append(obj.getName());
                    userDN.append("," + BASEDN);
                } else {
                    System.out.println(obj);
                }
            }
        } catch (Exception e) {
            System.out.println("查找用户时产生异常。");
            e.printStackTrace();
        }

        return userDN.toString();
    }

    public boolean authenricate(String UID, String password) {
//        return true;
        boolean valide = false;
        String userDN = getUserDN(UID);

        try {
            ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, userDN);
            ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
            ctx.reconnect(connCtls);
            System.out.println(userDN + " 验证通过");
            valide = true;
        } catch (AuthenticationException e) {
            System.out.println(userDN + " 验证失败");
            System.out.println(e.toString());
        } catch (NamingException e) {
            System.out.println(userDN + " 验证失败");
            valide = false;
        }

        return valide;
    }
}
