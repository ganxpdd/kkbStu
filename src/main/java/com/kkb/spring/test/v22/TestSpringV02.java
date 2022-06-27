package com.kkb.spring.test.v22;


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
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 面向过程思维
 * 把大象装冰箱，总共分几步
 */
public class TestSpringV02 {

    //存储BeanDefinition的容器
    private Map<String, BeanDefinition> beanDefinitions = new HashMap<>();

    // 存储单例Bean的容器
    private Map<String, Object> singletonObjects = new HashMap<>();

    @Before
    public void before(){
        // 进行BeanDefinition的注册

        String location = "beans.xml";

        InputStream inputStream = getInputStream(location);

        Document document = getDocument(inputStream);

        // 按照spring的配置语义进行解析
        loadBeanDefinitions(document.getRootElement());
    }

    // 测试人员
    @Test
    public void test(){

        // 调用开发人员的代码，获得Service对象
//        UserService userService = getUserService();
        UserService userService = (UserService) getBean("userService");

        // 以下代码才是测试人员需要的代码

        Map<String,Object> map = new HashMap<>();
        map.put("username","詹哥");
        List<User> users = userService.queryUsers(map);
        System.out.println(users);
    }

    /**
     *
     * @param rootElement <beans></beans>
     */
    private void loadBeanDefinitions(Element rootElement) {
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

    /**
     *
     * @param element <bean></bean>
     */
    private void parseDefaultElement(Element element) {
        String id = element.attributeValue("id");


        String clazzName = element.attributeValue("class");
        Class clazzType = resolveClassType(clazzName);
        String scope = element.attributeValue("scope");
        scope = scope == "" || scope == null ? "singleton" : scope;
        String initMethod = element.attributeValue("init-method");

        id = id == "" || id == null ? clazzType.getSimpleName() : id;


        BeanDefinition bd = new BeanDefinition(clazzName,id);
        bd.setScope(scope);
        bd.setInitMethod(initMethod);

        List<Element> elements = element.elements();
        parsePropertyElements(bd,elements);

        this.beanDefinitions.put(id,bd);
    }

    private Class resolveClassType(String clazzName) {
        try {
            return Class.forName(clazzName);
        }catch (Exception e){
            e.printStackTrace();
        }

        return  null;

    }

    private void parsePropertyElements(BeanDefinition bd, List<Element> elements) {
        for (Element element : elements) {
            parsePropertyElement(bd,element);
        }
    }

    private void parsePropertyElement(BeanDefinition bd, Element element) {
        String name = element.attributeValue("name");
        String value = element.attributeValue("value");
        String ref = element.attributeValue("ref");

        if (value!= "" && ref!= "" && value != null && ref != null ){
            return ;
        }
        if (value != "" && value != null){
            TypedStringValue typedStringValue = new TypedStringValue(value);
            Class targetType = resolveTargetType(bd.getClazzType(),name);
            typedStringValue.setTargetType(targetType);
            PropertyValue pv = new PropertyValue(name,typedStringValue);

            bd.addPropertyValue(pv);
        }else if(ref != "" && ref != null){
            RuntimeBeanReference reference = new RuntimeBeanReference(ref);
            PropertyValue pv = new PropertyValue(name,reference);
            bd.addPropertyValue(pv);
        }
    }

    private Class resolveTargetType(Class<?> clazzType, String name) {
        try {
            Field field = clazzType.getDeclaredField(name);
            return field.getType();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private void parseCustomElement(Element element) {

    }

    private Document getDocument(InputStream inputStream) {
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(inputStream);
            return document;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private InputStream getInputStream(String location) {
        return this.getClass().getClassLoader().getResourceAsStream(location);
    }



    // 刚入职的小程序员A
    private UserService getUserService() {
        UserServiceImpl userService = new UserServiceImpl();
        // 第一步：发现了userService不能正常使用，需要注入userDao
        UserDaoImpl userDao = new UserDaoImpl();
        // 第二步：发现了userDao不能正常使用，需要注入dataSource
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://39.106.92.113:3306/kkb2?characterEncoding=utf8");
        dataSource.setUsername("root");
        dataSource.setPassword("huike0826");

        userDao.setDataSource(dataSource);
        userService.setUserDao(userDao);

        //IoC：控制反转，创建对象的角色，进行了反转，由调用者去创建，变成了其他人去创建，调用者直接得到成品。

        return userService;
    }

    // 已经工作2年的小程序员B
    private Object getObject(String name) {
        if(name.equals("userService")){
            UserServiceImpl userService = new UserServiceImpl();
            // 第一步：发现了userService不能正常使用，需要注入userDao
            UserDaoImpl userDao = new UserDaoImpl();
            // 第二步：发现了userDao不能正常使用，需要注入dataSource
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setDriverClassName("com.mysql.jdbc.Driver");
            dataSource.setUrl("jdbc:mysql://39.106.92.113:3306/kkb2?characterEncoding=utf8");
            dataSource.setUsername("root");
            dataSource.setPassword("huike0826");

            userDao.setDataSource(dataSource);
            userService.setUserDao(userDao);

            //IoC：控制反转，创建对象的角色，进行了反转，由调用者去创建，变成了其他人去创建，调用者直接得到成品。

            return userService;
        }
        //else if ()

        // 思考该方法存在的问题：
        // 1、违反开闭原则
        // 2、扩展性不好
        // 3、累的职责太重

        return null;
    }

    // 已经工作5年的开发组长C
    private Object getBean(String name) {
        // 面向对象编程中，有两类对象：
        // 1、数据对象（V2)
        // 2、业务对象


        // 一旦遇到了扩展性的问题，首先想到的解决方案：
        // 设计经验：【配置 + 反射 + 容器】

        // 配置（创建一个类的对象，就对应一个bean标签）：
        // <bean id="bean的唯一标识" class="类的全路径">
        //    <property name="属性名称"  value="属性值"/>
        //    <property name="属性名称"  ref="属性值"/>
        // </bean>

        // 容器（Map）：存储的是bean标签对应的数据
        // Key：bean的唯一标识，bean的名称，beanname
        // Value：BeanDefinition（对应一个bean标签的所有信息）

        // BeanDefinition
        // id\class\。。。
        // List<PropertyValue>

        // PropertyValue
        //  String name
        //  Object value(value和ref的标签属性值)
        //     value标签  ---  TypedStringValue
        //     ref标签  ---  RuntimeBeanReference

        // 反射：
        // Class instanceClass = Class.forName("类的全路径");
        // Object bean = instanceClass.newInstance();
        // Field field = instanceClass.getDeclearField(属性名称）;
        // field.set(bean,属性值)；

        // 思考：每次getBean都会创建新的Bean对象吗？比如说同样的userService，会创建多个对象吗？
        // 结论：需要有一个容器（Map）去缓存已经创建出来的Bean对象（单例Bean对象）
        // 存储Bean对象的容器（缓存思维）：
        // Key：bean的唯一标识，beanname
        // Value：bean的对象


        // 程序处理流程：
        // 注意：配置信息需要一次性进行加载。而getBean需要每次都获取类的信息

        // 拆分为两大流程：
        // 1、BeanDefinition的注册流程
        //   1.1、定位
        //   1.2、加载
        //   1.3、解析XML配置信息
        //   1.4、注册BeanDefinition到对应容器中
        // 2、getBean的创建bean流程
        //   2.1、先去缓存容器中查找
        //   2.2、如果存在，则直接返回
        //   2.3、如果不存在，则再去存储BeanDefinition的容器中获取对应的BeanDefinition
        //   2.4、如果是单例（singleton）
        //      2.4.1、创建Bean实例（细分流程，到时候分解）
        //      2.4.2、将创建出来的Bean实例放入缓存容器中
        //   2.5、如果是多例（prototype）
        //      2.5.1、创建Bean实例（细分流程，到时候分解）

        Object bean = this.singletonObjects.get(name);

        if (bean != null) {
            return bean;
        }

        BeanDefinition bd = this.beanDefinitions.get(name);
        if (bd == null){
            return null;
        }
        if ("singleton".equals(bd.getScope())){
            bean = createBean(bd);
            this.singletonObjects.put(name,bean);
        }else if ("prototype".equals(bd.getScope())){
            bean = createBean(bd);
        }

        return bean;
    }

    private Object createBean(BeanDefinition bd) {
        // 1、bean的实例化（new）
        Object bean = createInstance(bd);
        // 2、bean的依赖注入（属性填充，setter）
        populateBean(bd,bean);
        // 3、bean的初始化（init方法）
        initializingBean(bd,bean);

        return bean;
    }

    private void initializingBean(BeanDefinition bd, Object bean) {
        // TODO 可以针对目标对象进行Aware
        // TODO 比如BeanFactoryAware

        invokeInitMethod(bd,bean);

    }

    private void invokeInitMethod(BeanDefinition bd, Object bean) {
        // TODO 针对实现了InitializingBean接口的类调用afterPropertiesSet方法

        try {
            String initMethod = bd.getInitMethod();
            if (!"".equals(initMethod) && initMethod != null){
                Class<?> clazzType = bd.getClazzType();
                Method method = clazzType.getDeclaredMethod(initMethod);
                method.invoke(bean);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void populateBean(BeanDefinition bd, Object bean) {
        List<PropertyValue> propertyValues = bd.getPropertyValues();

        for (PropertyValue pv : propertyValues) {
            String name = pv.getName();
            // 注意：该value不是最终需要注入的值
            Object value = pv.getValue();

            Object valueToUse = resolveValue(value);

            setProperty(bean,name,valueToUse);
        }
    }

    private void setProperty(Object bean, String name, Object valueToUse) {
        try {
            Class<?> aClass = bean.getClass();
            Field field = aClass.getDeclaredField(name);
            field.setAccessible(true);
            field.set(bean,valueToUse);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 根据具体的类型，转成TypedStringValue或者RuntimeBeanReference进行处理
     * @param value
     * @return
     */
    private Object resolveValue(Object value) {
        if (value instanceof TypedStringValue){
            TypedStringValue typedStringValue = (TypedStringValue) value;
            Object valueToUse = typedStringValue.getValue();

            Class targetType = typedStringValue.getTargetType();
            if (targetType != null){
                valueToUse = handleType(valueToUse,targetType);
            }
            return valueToUse;
        }else if (value instanceof RuntimeBeanReference){
            RuntimeBeanReference reference = (RuntimeBeanReference) value;
            String ref = reference.getRef();

            // TODO 出现循环依赖的地方
            return getBean(ref);
        }

        return null;
    }

    private Object handleType(Object valueToUse, Class targetType) {
        if (targetType == Integer.class){
            return Integer.parseInt(valueToUse.toString());
        }else if (targetType == String.class){
            return valueToUse.toString();
        }
        // .....
        return null;
    }

    private Object createInstance(BeanDefinition bd) {
        try {
            // TODO 通过bean工厂去创建

            // TODO 通过静态工厂去创建

            Class<?> clazzType = bd.getClazzType();
            // TODO 思考如何使用有参构造来创建实例
            // 默认使用无参构造来创建
            Constructor<?> constructor = clazzType.getDeclaredConstructor();
            return constructor.newInstance();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
