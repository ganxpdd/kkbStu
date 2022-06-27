package com.kkb.spring.test.v21;


import com.kkb.spring.dao.UserDaoImpl;
import com.kkb.spring.ioc.BeanDefinition;
import com.kkb.spring.ioc.PropertyValue;
import com.kkb.spring.ioc.RuntimeBeanReference;
import com.kkb.spring.ioc.TypedStringValue;
import com.kkb.spring.po.User;
import com.kkb.spring.service.UserService;
import com.kkb.spring.service.UserServiceImpl;
import org.apache.commons.dbcp.BasicDataSource;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 写测试代码的是A同学
 * 写业务功能的是B同学(写的代码是以jar包的方式依赖过去的)
 * 使用【面向过程】的思维去编码
 */
public class TestSpringV02{

    // 存储最终解析出来的BeanDefinition
    private Map<String,BeanDefinition> beanDefinitions = new HashMap<>();

    // 存储单例Bean实例的集合
    private Map<String,Object> singletonObjects = new HashMap<>();

    // XML解析工作
    @Before
    public void before(){
        // 解决方法：配置文件（注解）+反射
        // 需要配置的内容：类的全路径、属性名称和属性值
        // 配置文件：
        // <bean class="类的全路径" scope="singleton、prototype">
        //      <property name="属性名称" value="属性值"/>
        // </bean>

        // 解析流程（只需要解析一次，就将所有的bean标签封装到对应的对象中）：
        // Dom4j解析  ---->  BeanDefinition(封装bean标签的信息)   PropertyValue（封装property标签的信息）
        // Map集合（K：bean的名称、V：BeanDefinition对象）

        // 资源路径
        String location = "beans.xml";

        // 加载
        InputStream inputStream = getResourceAsStream(location);

        // 创建Document
        Document document = getDocument(inputStream);

        // 按照Spring语义解析Document
        parseBeanDefinitions(document.getRootElement());
    }

    // 测试人员
    @Test
    public void test(){
        Map<String,Object> map = new HashMap<>();
        map.put("username","千年老亚瑟");

        // 调用程序员A的代码
//        UserService userService = getUserService();
//        UserService userService = (UserService) getObject("userService");
        UserService userService = (UserService) getBean("userService");

        List<User> users = userService.queryUsers(map);
        System.out.println(users);
    }


    private InputStream getResourceAsStream(String location) {
        return this.getClass().getClassLoader().getResourceAsStream(location);
    }

    /**
     *
     * @param rootElement <beans></beans>
     */
    private void parseBeanDefinitions(Element rootElement) {
        List<Element> elements = rootElement.elements();
        for (Element element : elements) {
            String name = element.getName();
            if (name.equals("bean")){
                parseDefaultElement(element);
            }else{
                parseCustomElement(element);
            }
        }
    }

    private void parseCustomElement(Element element) {

    }

    /**
     *
     * @param element <bean></bean>
     */
    private void parseDefaultElement(Element element) {
        String id = element.attributeValue("id");
        String clazz = element.attributeValue("class");
        // 获取Class类对象
        Class clazzType = resolveType(clazz);

        String initMethod = element.attributeValue("init-method");
        String scope = element.attributeValue("scope");
        // 保证scope不能为空
        scope = scope == null || scope.equals("") ? "singleton":scope ;

        // 保证id不能为空
        id = id == null || id.equals("") ? clazzType.getSimpleName() : id;

        BeanDefinition bd = new BeanDefinition(clazz,id);
        bd.setScope(scope);
        bd.setInitMethod(initMethod);

        List<Element> elements = element.elements("property");
        for (Element propertyElement : elements) {
            bd.addPropertyValue(parsePropertyElement(propertyElement));
        }

        this.beanDefinitions.put(id,bd);

    }

    private Class resolveType(String clazz) {
        try {
            return Class.forName(clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private PropertyValue parsePropertyElement(Element propertyElement) {
        String name = propertyElement.attributeValue("name");
        String value = propertyElement.attributeValue("value");
        String ref = propertyElement.attributeValue("ref");

        if (value != null && ref != null){
            return null;
        }
        PropertyValue pv = null;
        if (value != null){
            TypedStringValue typedStringValue = new TypedStringValue(value);
            pv = new PropertyValue(name,typedStringValue);
        }else if (ref != null){
            RuntimeBeanReference beanReference = new RuntimeBeanReference(ref);
            pv = new PropertyValue(name,beanReference);
        }

        return pv;
    }

    private Document getDocument(InputStream inputStream) {
        try {
            SAXReader saxReader = new SAXReader();
            return saxReader.read(inputStream);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }


    // 开发人员A
    public UserService getUserService(){
        UserServiceImpl userService = new UserServiceImpl();
        // 第一步：发现了userService不能正常使用，需要注入userDao
        UserDaoImpl userDao = new UserDaoImpl();
        // 第二步：发现了userDao不能正常使用，需要注入dataSource
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://39.105.204.66:3306/kkb?characterEncoding=utf8");
        dataSource.setUsername("root");
        dataSource.setPassword("root");

        userDao.setDataSource(dataSource);
        userService.setUserDao(userDao);

        return userService;
    }

    // 程序员B（五年老程序）
    public Object getObject(String name){
        if(name.equals("userService")){
            UserServiceImpl userService = new UserServiceImpl();
            // 第一步：发现了userService不能正常使用，需要注入userDao
            UserDaoImpl userDao = new UserDaoImpl();
            // 第二步：发现了userDao不能正常使用，需要注入dataSource
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setDriverClassName("com.mysql.jdbc.Driver");
            dataSource.setUrl("jdbc:mysql://39.105.204.66:3306/kkb?characterEncoding=utf8");
            dataSource.setUsername("root");
            dataSource.setPassword("root");

            userDao.setDataSource(dataSource);
            userService.setUserDao(userDao);

            return userService;
        }

        return null;
    }

    // 程序C（技术组长）
    private Object getBean(String name){

        // 分析：程序员B写的代码，扩展性很不好


        // getBean()流程（针对单例模式的Bean进行存储，针对多例模式或者叫原型模式的Bean不进行缓存存储）
        // Map集合（K：bean的名称、V：单例bean实例）----缓存

        // 1、先查询缓存中有没有
        Object bean = this.singletonObjects.get(name);
        // 2、有则直接返回
        if (bean != null) {
            return bean;
        }

        // 3、没有则查询对应的BeanDefinition准备创建对象
        BeanDefinition bd = this.beanDefinitions.get(name);

        if (bd == null) {
            return null;
        }

        // 4、判断是单例还是多例（在bean标签中可以通过一个标识去区别）
        /*if ("singleton".equals(bd.getScope())){

        }else if("prototype".equals(bd.getScope())){

        }*/
        if (bd.isSingleton()){
            // 5、触发创建单例Bean实例的流程
            bean = createBean(bd);
            // 6、将单例Bean实例放入Map集合中
            this.singletonObjects.put(name,bean);
        }else if(bd.isPrototype()){
            bean = createBean(bd);
        }


        return bean;
    }

    private Object createBean(BeanDefinition bd) {
        // 1、Bean的实例化（new对象）
        Object bean = createBeanInstance(bd);
        // 2、属性填充/依赖注入（setter）
        populateBean(bean,bd);
        // 3、Bean的初始化（调用初始化方法）
        initializingBean(bean,bd);
        return bean;
    }

    private void initializingBean(Object bean, BeanDefinition bd) {
        // TODO Aware接口的处理（在类创建成功之后，去通过Aware接口装饰一个类）

        // TODO BeanPostProcessor接口方法的处理

        // 处理初始化方法
        invokeInitMethod(bean,bd);
    }

    private void invokeInitMethod(Object bean, BeanDefinition bd) {
        // TODO InitializingBean接口的afterProertiesSet方法的处理

        // 调用自定义初始化方法
        try {
            String initMethod = bd.getInitMethod();
            if (initMethod == null || "".equals(initMethod)){
                return;
            }
            Class<?> clazzType = bd.getClazzType();
            Method method = clazzType.getDeclaredMethod(initMethod);
            method.invoke(bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 依赖注入
     * @param bean
     * @param bd
     */
    private void populateBean(Object bean, BeanDefinition bd) {
        List<PropertyValue> propertyValues = bd.getPropertyValues();

        for (PropertyValue pv : propertyValues) {
            String name = pv.getName();//属性名称
            Object value = pv.getValue();//需要处理的属性值
            Object valueToUse = resolveValue(value);// 处理属性值的

            setProperty(bean,name,valueToUse);
        }
    }

    private void setProperty(Object bean, String name, Object valueToUse) {
        try {
            Class<?> aClass = bean.getClass();
            Field field = aClass.getDeclaredField(name);
            field.setAccessible(true);
            field.set(bean,valueToUse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object resolveValue(Object value) {
        Object valueToUse = null;
        if (value instanceof TypedStringValue){
            TypedStringValue typedStringValue = (TypedStringValue) value;

            Class<?> targetType = typedStringValue.getTargetType();
            String stringValue = typedStringValue.getValue();
            valueToUse = stringValue;
            if (targetType != null){
                valueToUse = handleType(targetType,stringValue);
            }
        }else if (value instanceof RuntimeBeanReference){
            RuntimeBeanReference beanReference = (RuntimeBeanReference) value;
            String ref = beanReference.getRef();

            // 注意：此处可能出现循环依赖问题
            valueToUse = getBean(ref);

        }
        return valueToUse;
    }

    private Object handleType(Class<?> targetType, String stringValue) {
        // TODO 后面是有设计模式进行优化
        if (targetType == Integer.class){
            return Integer.parseInt(stringValue);
        }else if (targetType == String.class){
            return stringValue;
        }//....
        return null;
    }

    private Object createBeanInstance(BeanDefinition bd) {
        // 通过实例工厂去创建Bean

        // 通过静态工厂去创建Bean

        try {
            Class<?> clazzType = bd.getClazzType();
            Constructor<?> constructor = clazzType.getDeclaredConstructor();
            return constructor.newInstance();
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
}