//package com.kkb.spring.test.v20;
//
//
//import com.kkb.spring.po.User;
//import com.kkb.spring.service.UserService;
//import com.kkb.springframework.factory.support.DefaultListableBeanFactory;
//import com.kkb.springframework.io.ClasspathResource;
//import com.kkb.springframework.io.Resource;
//import com.kkb.springframework.reader.XmlBeanDefinitionReader;
//import com.kkb.springframework.utils.DocumentUtils;
//import org.dom4j.Document;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.io.InputStream;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * 写测试代码的是A同学
// * 写业务功能的是B同学(写的代码是以jar包的方式依赖过去的)
// * 使用面向过程的思维去编码
// */
//public class TestSpringV03 {
//
//    private DefaultListableBeanFactory beanFactory;
//
//    @Before
//    public void before(){
//        // 加载并注册BeanDefinition
//        String location = "beans.xml";
//        // 获取流对象
//        // 手动指定策略
//        // 如果不指定具体策略，让系统根据路径自己去判断策略的话，使用ResourcePattern
//        Resource resource = new ClasspathResource(location);
//        InputStream inputStream = resource.getResource();
//
//        // 按照spring配置文件的语义去完成解析工作
//        beanFactory = new DefaultListableBeanFactory();
//        XmlBeanDefinitionReader bdReader = new XmlBeanDefinitionReader(beanFactory);
//        bdReader.loadBeanDefinitions(inputStream);
//        //bdReader.loadBeanDefinitions(location);
//    }
//
//    @Test
//    public void test(){
//        UserService userService = (UserService) beanFactory.getBean("userService");
//
//        Map<String,Object> map = new HashMap<>();
//        map.put("username","千年老亚瑟");
//        List<User> users = userService.queryUsers(map);
//        System.out.println(users);
//    }
//
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
