# Mybatis源码学习Mybatis多级缓存

## Mybatis一级缓存命中条件

> - 最终SQL相同，SQL参数相同
> - 同一 `SqlSession`
> - 同一 MappedStatement id
> - `RowBounds`最终返回行相同

## Mybatis一级缓存失效场景

> - 未手动情况缓存(提交，回滚)
> - 未调用 `flushCache=true` 的查询
> - 未执行`UPDATE`语句
> - `LocalCacheScope` 不是 `STATEMENT`

### 图示梗概

tips: 图片来源： [源码阅读网](http://www.coderead.cn/home/index.html)

![image-20211220231418927](https://img.mercymodest.com/public/image-20211220231418927.png)

## Mybatis一级缓存源码执行梗概

tips: 图片来源： [源码阅读网](http://www.coderead.cn/home/index.html)

![image-20211220232234468](https://img.mercymodest.com/public/image-20211220232234468.png)

### 示例代码

```java
    /**
     * 测试 Mybatis 一级缓存
     */
    @Disabled
    @Test
    public void testMybatisFirstLevelCachingByPrototype() {
        try (SqlSession sqlSession = sessionFactory.openSession(true)) {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            User user = userMapper.selectById(1);
            User user2 = userMapper.selectById(user.getId());
            System.out.println(user == user2 ? "命中缓存" : "未命中缓存");
        }
    }
```

#### cacheKey

![image-20211220233720316](https://img.mercymodest.com/public/image-20211220233720316.png)

1. ![image-20211220233829099](https://img.mercymodest.com/public/image-20211220233829099.png)
2. ![image-20211220234045067](https://img.mercymodest.com/public/image-20211220234045067.png)

## 关于Mybatis集成Spring之后的Mybatis一级缓存失效问题

### Mybatis一级缓存失效验证

#### spring-mybatis.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context" xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context https://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/cache http://www.springframework.org/schema/cache/spring-cache.xsd http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd">
    <!--properties-->
    <context:property-placeholder location="jdbc-config.properties" file-encoding="utf-8"/>
    <!--datasource-->
    <bean name="datasource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="url" value="${local.mysql.url}"/>
        <property name="username" value="${local.mysql.username}"/>
        <property name="password" value="${local.mysql.password}"/>
        <property name="driverClassName" value="${local.mysql.driver}"/>
    </bean>
    <!--SqlSessionFactoryBean-->
    <bean name="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="datasource"/>
    </bean>
    <!--DatasourceTransactionManager-->
    <bean name="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="datasource"/>
    </bean>
    <tx:annotation-driven/>
    <!--mapper scanner-->
    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <property name="basePackage" value="com.mercymodest.mybatis.caching"/>
    </bean>
</beans>
```

#### 测试代码

```java
    /***
     *  测试 Mybatis 集成 Spring 之后 一级缓存失效问题
     */
    @Disabled
    @Test
    public void testMybatisWithSpring() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-mybatis.xml");
        UserMapper userMapper = applicationContext.getBean(UserMapper.class);
        final Integer id = 1;
        User user = userMapper.selectById(id);
        User user2 = userMapper.selectById(id);
        System.out.println(user == user2 ? "走了一级缓存" : "没走一级缓存");
    }
```

##### 运行结果

![image-20211221235618624](https://img.mercymodest.com/public/image-20211221235618624.png)

### 失效原因探究

tips: 图片来源： [源码阅读网](http://www.coderead.cn/home/index.html)

![image-20211222001608206](https://img.mercymodest.com/public/image-20211222001608206.png)

#### 从日志中找寻答案

![image-20211222001525298](https://img.mercymodest.com/public/image-20211222001525298.png)

##### 如上图所示日志,默认情况下，即使我们使用 同一`mapper` 调用同一方法，Mybatis都会开启一个新的会话(`SqlSession`)进行`SQL`处理

![image-20211222001731241](https://img.mercymodest.com/public/image-20211222001731241.png)

![image-20211222001811934](https://img.mercymodest.com/public/image-20211222001811934.png)

#### 从源码中尝试寻找答案

> **我们都了解，Mybatis 的执行过程中 `Sqlsession`:`Executor`:`StatementHandler` 的比例是 1:1:n**

##### 第一次查询

![image-20211222000934983](https://img.mercymodest.com/public/image-20211222000934983.png)

##### 第二次查询

![image-20211222001327565](https://img.mercymodest.com/public/image-20211222001327565.png)

由此可得，两次的查询的SqlSession是不一致的.

## 当我们在同一个事务中执行查询,则可以保证一级缓存的命中

### 测试代码

```java
   /***
     *  测试 Mybatis 集成 Spring 之后 一级缓存失效问题
     */
    @Disabled
    @Test
    public void testMybatisWithSpring() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-mybatis.xml");
        UserMapper userMapper = applicationContext.getBean(UserMapper.class);
        final Integer id = 1;
        // 手动开启事务
        DataSourceTransactionManager dataSourceTransactionManager = (DataSourceTransactionManager) applicationContext.getBean(TransactionManager.class);
        dataSourceTransactionManager.getTransaction(new DefaultTransactionAttribute());
        User user = userMapper.selectById(id);
        User user2 = userMapper.selectById(id);
        System.out.println(user == user2 ? "走了一级缓存" : "没走一级缓存");
    }
```

#### 执行结果示例

![image-20211222002620721](https://img.mercymodest.com/public/image-20211222002620721.png)

## Mybatis二级缓存

### Mybatis 二级缓存命中条件

> - **会话提交后**
>
>   tips: 图片来源： [源码阅读网](http://www.coderead.cn/home/index.html)
>
>   ![image-20211224235233599](https://img.mercymodest.com/public/image-20211224235233599.png)
>
> - `SQL` 语句相同,参数相同
>
> - 相同的`StatementId`
>
> - `RowBands`相同

### Mybatis 二级缓存结构

tips: 图片来源： [源码阅读网](http://www.coderead.cn/home/index.html)

![image-20211224235407168](https://img.mercymodest.com/public/image-20211224235407168.png)

![image-20211225000703616](https://img.mercymodest.com/public/image-20211225000703616.png)

> `会话`:`事务缓存管理器`:`暂存区`:`缓存区` = `1:1:n:1`

![image-20211224235654400](https://img.mercymodest.com/public/image-20211224235654400.png)

1. ![image-20211224235945631](https://img.mercymodest.com/public/image-20211224235945631.png)

2. ![image-20211225000045011](https://img.mercymodest.com/public/image-20211225000045011.png)
3. ![image-20211225000122832](https://img.mercymodest.com/public/image-20211225000122832.png)
4. ![image-20211225000308215](https://img.mercymodest.com/public/image-20211225000308215.png)

### 二级缓存暂存区内容刷新至二级缓存缓存区逻辑梗概

![image-20211225000519686](https://img.mercymodest.com/public/image-20211225000519686.png)

![image-20211225000603155](https://img.mercymodest.com/public/image-20211225000603155.png)

