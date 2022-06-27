/*
package com.kkb.spring.test.v21;


import com.kkb.spring.po.User;
import com.kkb.spring.service.UserService;
import com.kkb.springframework.factory.support.DefaultListableBeanFactory;
import com.kkb.springframework.io.ClasspathResource;
import com.kkb.springframework.io.Resource;
import com.kkb.springframework.reader.BeanDefinitionReader;
import com.kkb.springframework.reader.XmlBeanDefinitionReader;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestSpringV03 {

    private DefaultListableBeanFactory beanFactory;

    // XML解析工作
    @Before
    public void before(){

        // 资源路径
        String location = "beans.xml";

        //TODO 策略模式去处理资源读取的问题（classpath、URL、文件系统）
        // 策略模式有两种使用方式：
        // 一种是我自己知道策略，我把我的策略交给你，去执行（计算器，我们自己去输入到底是加法还是减法）
        // 还有一种是我自己不知道策略，你帮我按照我的条件去执行策略（旅行社，）---看springmvc的直播回访
        // 策略模式可以解决if语句扩展性和阅读性不友好的问题。


        beanFactory = new DefaultListableBeanFactory();

        BeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

        // 最终的BeanDefinition要注册到BeanDefinitionRegistry中
        beanDefinitionReader.loadBeanDefinitions(location);

    }

    // 测试人员
    @Test
    public void test(){
        Map<String,Object> map = new HashMap<>();
        map.put("username","千年老亚瑟");

        // 调用程序员A的代码
        UserService userService = (UserService) beanFactory.getBean("userService");

        List<User> users = userService.queryUsers(map);
        System.out.println(users);
    }

}*/
