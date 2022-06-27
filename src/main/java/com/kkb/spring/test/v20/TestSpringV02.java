package com.kkb.spring.test.v20;


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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 写测试代码的是A同学
 * 写业务功能的是B同学(写的代码是以jar包的方式依赖过去的)
 * 使用面向过程的思维去编码
 */
public class TestSpringV02<pd, viod> {

    /**
     * 专门用来存储BeanDefinition
     */
    private Map<String, BeanDefinition> beanDefinitions = new HashMap<>();

    /**
     * 存储单例Bean
     */
    private Map<String, Object> singletonObjects = new HashMap<>();

    @Before
    public void before() {
        // 加载并注册BeanDefinition
        String location = "beans.xml";
        // 获取流对象
        InputStream inputStream = getResourceAsStream(location);
        // 获取Document对象
        Document document = getDocument(inputStream);
        // 按照spring配置文件的语义去完成解析工作
        loadBeanDefinitions(document.getRootElement());
    }

    /**
     * A同学根本不想关心UserServiceImpl对象是怎么new的，他只是想测试查询功能。
     * IoC要做的事情，就是让使用对象的同学只需要找工厂去要对应的对象即可，不需要自己创建。
     * IoC是将创建对象的权利，由程序员这边，反转给spring容器去创建
     */
    @Test
    public void test() {
//        UserService userService = getUserService();
//        UserService userService = (UserService) getObject("userService");
        UserService userService = (UserService) getBean("userService");

        Map<String, Object> map = new HashMap<>();
        map.put("username", "詹哥");
        List<User> users = userService.queryUsers(map);
        System.out.println(users.get(0));
    }

    /**
     * @param rootElement <beans></beans>
     */
    private void loadBeanDefinitions(Element rootElement) {
        List<Element> elements = rootElement.elements();
        for (Element element : elements) {
            String name = element.getName();
            if (name.equals("bean")) {
                parseDefaultElement(element);
            } else {
                parseCustomElement(element);
            }
        }
    }

    private void parseCustomElement(Element element) {

    }

    private void parseDefaultElement(Element beanElement) {
        try {
            if (beanElement == null) {
                return;
            }
            // 获取id属性
            String id = beanElement.attributeValue("id");

            // 获取name属性
            String name = beanElement.attributeValue("name");

            // 获取class属性
            String clazzName = beanElement.attributeValue("class");
            if (clazzName == null || "".equals(clazzName)) {
                return;
            }

            // 获取init-method属性
            String initMethod = beanElement.attributeValue("init-method");
            // 获取scope属性
            String scope = beanElement.attributeValue("scope");
            scope = scope != null && !scope.equals("") ? scope : "singleton";

            // 获取beanName
            String beanName = id == null ? name : id;
            Class<?> clazzType = Class.forName(clazzName);
            beanName = beanName == null ? clazzType.getSimpleName() : beanName;
            // 创建BeanDefinition对象
            // 此次可以使用构建者模式进行优化
            BeanDefinition beanDefinition = new BeanDefinition(clazzName, beanName);
            beanDefinition.setInitMethod(initMethod);
            beanDefinition.setScope(scope);
            // 获取property子标签集合
            List<Element> propertyElements = beanElement.elements();
            for (Element propertyElement : propertyElements) {
                parsePropertyElement(beanDefinition, propertyElement);
            }

            // 注册BeanDefinition信息
            this.beanDefinitions.put(beanName, beanDefinition);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void parsePropertyElement(BeanDefinition beanDefination, Element propertyElement) {
        if (propertyElement == null)
            return;

        // 获取name属性
        String name = propertyElement.attributeValue("name");
        // 获取value属性
        String value = propertyElement.attributeValue("value");
        // 获取ref属性
        String ref = propertyElement.attributeValue("ref");

        // 如果value和ref都有值，则返回
        if (value != null && !value.equals("") && ref != null && !ref.equals("")) {
            return;
        }

        /**
         * PropertyValue就封装着一个property标签的信息
         */
        PropertyValue pv = null;

        if (value != null && !value.equals("")) {
            // 因为spring配置文件中的value是String类型，而对象中的属性值是各种各样的，所以需要存储类型
            TypedStringValue typeStringValue = new TypedStringValue(value);

            Class<?> targetType = getTypeByFieldName(beanDefination.getClazzName(), name);
            typeStringValue.setTargetType(targetType);

            pv = new PropertyValue(name, typeStringValue);
            beanDefination.addPropertyValue(pv);
        } else if (ref != null && !ref.equals("")) {

            RuntimeBeanReference reference = new RuntimeBeanReference(ref);
            pv = new PropertyValue(name, reference);
            beanDefination.addPropertyValue(pv);
        } else {
            return;
        }
    }

    private Class<?> getTypeByFieldName(String beanClassName, String name) {
        try {
            Class<?> clazz = Class.forName(beanClassName);
            Field field = clazz.getDeclaredField(name);
            return field.getType();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Class resoleType(String className) {
        try {
            return Class.forName(className);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    private Document getDocument(InputStream inputStream) {
        try {
            SAXReader saxReader = new SAXReader();
            return saxReader.read(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private InputStream getResourceAsStream(String location) {
        return this.getClass().getClassLoader().getResourceAsStream(location);
    }


    // B同学(8K)
    private UserService getUserService() {
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

    // C同学（开闭原则：对修改关闭）(15K)
    private Object getObject(String name) {

        if (name.equals("userService")) {
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
        } else if ("".equals("")) {
            //......
        }
        return null;
    }

    // D同学(25K)
    // spring是面向Bean编码的框架（BOP）
    private Object getBean(String name) {
        // 通过XML配置文件将要创建的Bean的信息配置出来
        // <bean name="bean的唯一名称" class="bean的全路径">
        //   <property name="要依赖注入的属性名称" value="要注入的简单属性值"/>
        //   <property name="要依赖注入的属性名称" ref="要注入的引用属性值"/>
        // </bean>

        // 反射
        // Class class = Class.forName("bean的全路径");
        // Object bean = class.newInstance();//相对于new
        // Field field = class.getDeclearField("要依赖注入的属性名称");
        // field.set(bean,要注入的属性值);


        // 步骤：
        // 1、先查询缓存（Map集合）
        Object bean = this.singletonObjects.get(name);
        // 2、有则直接返回
        if (bean != null) {
            return bean;
        }
        // 3、要获取该beanName对于的BeanDefinition
        BeanDefinition bd = this.beanDefinitions.get(name);
        // 4、没有BeanDefinition的信息，则返回null
        if (bd == null) {
            return null;
        }
        // 5、走创建Bean的子流程(单例、多例)
//        if("singleton".equals(bd.getScope())){
        if (bd.isSingleton()) {
            bean = createBean(bd);
            // 6、将创建完成的Bean放入Map集合中
            this.singletonObjects.put(name, bean);
//        }else if ("prototype".equals(bd.getScope())){
        } else if (bd.isPrototype()) {
            bean = createBean(bd);
        }

        return bean;
    }

    private Object createBean(BeanDefinition bd) {
        try {
            // 1、实例化（new）
            Object bean = createBeanInstance(bd);
            // 2、依赖注入（setter）
            polulateBean(bean, bd);
            // 3、初始化
            initilizeBean(bean, bd);
            return bean;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void initilizeBean(Object bean, BeanDefinition bd) throws Exception {
        // TODO Aware(BeanFactoryAware\BeanNameAware)  RequestMappingHandlerMapping(BeanFactory)

        // TODO BeanPostProcessor类的方法被调用（初始化前方法、初始化后方法）
        // 调用初始化方法（init-method\InitializingBean接口的afterPropertiesSet）
        invokeInitMethod(bean, bd);

        // TODO BeanPostProcessor类的方法被调用（初始化前方法、初始化后方法,AOP代理对象的产生）

    }

    private void invokeInitMethod(Object bean, BeanDefinition bd) throws Exception {
        // TODO InitializingBean接口的afterPropertiesSet
        String initMethod = bd.getInitMethod();
        if (initMethod == null || "".equals(initMethod)) {
            return;
        }
        Class<?> clazzType = bd.getClazzType();
        Method method = clazzType.getMethod(initMethod);
        method.invoke(bean);
    }

    private void polulateBean(Object bean, BeanDefinition bd) throws Exception {
        List<PropertyValue> propertyValues = bd.getPropertyValues();
        for (PropertyValue pv : propertyValues) {
            String name = pv.getName();
            // 此处取出来的属性值是RuntimeBeanReference和TypedStringValue
            Object value = pv.getValue();

            // 解析出来真正可用的value值
            Object valueToUse = resolveValue(bd, value);

            // 通过属性去进行依赖注入
            // TODO 思考题：spring到底是通过给属性赋值的方式进行依赖注入还是调用setter方法的方式（内省技术）

            setProperty(bean, name, valueToUse);
        }
    }

    private void setProperty(Object bean, String name, Object valueToUse) throws Exception {
        Class<?> aClass = bean.getClass();
        Field field = aClass.getDeclaredField(name);
        field.setAccessible(true);
        field.set(bean, valueToUse);
    }

    private Object resolveValue(BeanDefinition bd, Object value) {
        if (value instanceof RuntimeBeanReference) {
            RuntimeBeanReference reference = (RuntimeBeanReference) value;
            String ref = reference.getRef();

            // TODO 此处可能会发生循环依赖问题
            return getBean(ref);
        } else if (value instanceof TypedStringValue) {
            TypedStringValue typedStringValue = (TypedStringValue) value;
            String stringValue = typedStringValue.getValue();

            Class<?> targetType = typedStringValue.getTargetType();
            if (targetType != null) {
                // 要根据目标类型进行转换
                if (targetType == Integer.class) {
                    return Integer.parseInt(stringValue);
                } else if (targetType == String.class) {
                    return stringValue;
                }// ......
            }

            return stringValue;

        }// Map\ Set\Array\List

        return null;
    }

    private Object createBeanInstance(BeanDefinition bd) throws Exception {
        // TODO 静态工厂
        // TODO 实例工厂
        Class<?> clazzType = bd.getClazzType();
        // 默认获取的是无参构造（如果想通过有参构造的话，需要怎么做）
        // TODO <constructor-arg>
        Constructor<?> constructor = clazzType.getConstructor();

        // 通过构造器去创建实例
        Object bean = constructor.newInstance();

        return bean;

    }


}


























