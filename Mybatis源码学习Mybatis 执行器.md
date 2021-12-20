# [Mybatis源码学习](https://www.mercymodest.com):Mybatis 执行器

## JDBC 回顾

### JDBC 执行过程

![image-20211218105648485](https://img.mercymodest.com/public/image-20211218105648485.png)

#### 示例代码

```java
 /**
     * {@code  JDBC} 原生代码示例
     */
    @Disabled
    @Test
    public void testJdbc() throws Exception {
        // 数据库连接信息
        final String url = "jdbc:mysql://127.0.0.1:3306/test?serverTimezone=Asia/Shanghai&useSSL=false&useUnicode=true&characterEncoding=UTF-8";
        final String username = "root";
        final String password = "123456";

        final String querySql = "SELECT id,username,gender,motto FROM tb_user";

        // step1: 加载 JDBC 驱动
        final String jdbcClassName = "com.mysql.cj.jdbc.Driver";

        Class.forName(jdbcClassName);
        try (
                // step2: 获取连接
                Connection connection = DriverManager.getConnection(url, username, password);
                // step3: 创建 Statement
                Statement statement = connection.createStatement();
                // step4: 获取 ResultSet
                ResultSet resultSet = statement.executeQuery(querySql)
        ) {
            // step 5: 解析 ResultSet
            List<User> userList = new ArrayList<>(1 << 2);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("username");
                int gender = resultSet.getInt("gender");
                String motto = resultSet.getString("motto");
                User user = new User()
                        .setId(id)
                        .setUsername(name)
                        .setGender(gender)
                        .setMotto(motto);
                userList.add(user);
            }
            if (CollectionUtil.isNotEmpty(userList)) {
                userList.forEach(System.out::println);
            }
        }
    }
```

##### 结果示例

![image-20211218111608471](https://img.mercymodest.com/public/image-20211218111608471.png)

### 关于  JDBC Statement

![image-20211218112219945](https://img.mercymodest.com/public/image-20211218112219945.png)

> - Statement 
>
>   - 基本功能: 执行静态 SQL
>
>   - 传输相关: 批处理，这是加载的行数
>
>     ![image-20211218112610337](https://img.mercymodest.com/public/image-20211218112610337.png)
>
> - PreparedStatement
>
>   - 防止 SQL 注入
>
>   - 设置预编译参数
>
>     ![image-20211218112659631](https://img.mercymodest.com/public/image-20211218112659631.png)
>
> - CallableStatement
>
>   - 设置出参，读取出参
>
>     ![image-20211218112635131](https://img.mercymodest.com/public/image-20211218112635131.png)

#### Mybatis执行器 与 JDBC Statement 的对应关系

tips: 图片来源： [源码阅读网](http://www.coderead.cn/home/index.html)

![image-20211218112901085](https://img.mercymodest.com/public/image-20211218112901085.png)

## Mybatis 执行过程

> - 接口代理
> - SQL 会话
> - 执行器
> - JDBC 处理器
>
> tips: 图片来源： [源码阅读网](http://www.coderead.cn/home/index.html)
>
> ![image-20211218113243118](https://img.mercymodest.com/public/image-20211218113243118.png)

tips: 图片来源： [源码阅读网](http://www.coderead.cn/home/index.html)

![image-20211218113451360](https://img.mercymodest.com/public/image-20211218113451360.png)

## Mybatis执行器

tips: 图片来源： [源码阅读网](http://www.coderead.cn/home/index.html)

![image-20211219190327188](https://img.mercymodest.com/public/image-20211219190327188.png)

### `SimpleExecutor`

示例代码

![image-20211219190120148](https://img.mercymodest.com/public/image-20211219190120148.png)

![image-20211219190042135](https://img.mercymodest.com/public/image-20211219190042135.png)

```java
    /**
     * Mybatis 简单执行器
     */
    @SneakyThrows
    @Disabled
    @Test
    public void testSimpleExecutor() {
        final String statementId = "com.mercymodest.mybatis.executor.mapper.user.UserMapper.selectById";
        final Integer parameter = 1;
        MappedStatement mappedStatement = configuration.getMappedStatement(statementId);
        SimpleExecutor simpleExecutor = new SimpleExecutor(configuration, jdbcTransaction);
        List<Object> userList = simpleExecutor.doQuery(mappedStatement, parameter, RowBounds.DEFAULT, SimpleExecutor.NO_RESULT_HANDLER, mappedStatement.getBoundSql(parameter));
        Optional.ofNullable(userList)
                .ifPresent(list -> list.forEach(System.out::println));
        System.out.println("============== the separator ==============");
        userList = simpleExecutor.doQuery(mappedStatement, parameter, RowBounds.DEFAULT, SimpleExecutor.NO_RESULT_HANDLER, mappedStatement.getBoundSql(parameter));
        Optional.ofNullable(userList)
                .ifPresent(list -> list.forEach(System.out::println));
    }
```

#### 执行结果示例

![image-20211219184647746](https://img.mercymodest.com/public/image-20211219184647746.png)

### `ReuseExecutor`

示例代码

![image-20211219190120148](https://img.mercymodest.com/public/image-20211219190120148.png)

![image-20211219190042135](https://img.mercymodest.com/public/image-20211219190042135.png)

```java
 /**
     * Mybatis  重用执行器
     */
    @SneakyThrows
    @Disabled
    @Test
    public void testReuseExecutor() {
        final String statementId = "com.mercymodest.mybatis.executor.mapper.user.UserMapper.selectById";
        final Integer parameter = 1;
        MappedStatement mappedStatement = configuration.getMappedStatement(statementId);
        ReuseExecutor reuseExecutor = new ReuseExecutor(configuration, jdbcTransaction);
        List<Object> userList = reuseExecutor.doQuery(mappedStatement, parameter, RowBounds.DEFAULT, SimpleExecutor.NO_RESULT_HANDLER, mappedStatement.getBoundSql(parameter));
        Optional.ofNullable(userList)
                .ifPresent(list -> list.forEach(System.out::println));
        System.out.println("============== the separator ==============");
        userList = reuseExecutor.doQuery(mappedStatement, parameter, RowBounds.DEFAULT, SimpleExecutor.NO_RESULT_HANDLER, mappedStatement.getBoundSql(parameter));
        Optional.ofNullable(userList)
                .ifPresent(list -> list.forEach(System.out::println));
    }
```

#### 执行结果示例

![image-20211219184951589](https://img.mercymodest.com/public/image-20211219184951589.png)

### `ReuseExecutor` 代码梗概



![image-20211220224411911](https://img.mercymodest.com/public/image-20211220224411911.png)

![image-20211220224500047](https://img.mercymodest.com/public/image-20211220224500047.png)

## `BatchExecutor`

示例代码

![image-20211219190120148](https://img.mercymodest.com/public/image-20211219190120148.png)

![image-20211219190025085](https://img.mercymodest.com/public/image-20211219190025085.png)

```java
 /**
     * Mybatis  批处理执行器
     */
    @SneakyThrows
    @Disabled
    @Test
    public void testBatchExecutor() {
        final String statementId = "com.mercymodest.mybatis.executor.mapper.user.UserMapper.updateUsername";
        MappedStatement mappedStatement = configuration.getMappedStatement(statementId);
        BatchExecutor batchExecutor = new BatchExecutor(configuration, jdbcTransaction);
        User user = new User()
                .setId(1)
                .setUsername("mercymodest");
        batchExecutor.doUpdate(mappedStatement, user);
        user = new User()
                .setId(2)
                .setUsername("modest");
        batchExecutor.doUpdate(mappedStatement, user);
        batchExecutor.flushStatements(false);
    }
```

#### 执行结果示例

![image-20211219185937740](https://img.mercymodest.com/public/image-20211219185937740.png)

### `CachingExecutor`

开启 Mybatis 二级缓存

![image-20211219191509980](https://img.mercymodest.com/public/image-20211219191509980.png)

示例代码

![image-20211219190042135](https://img.mercymodest.com/public/image-20211219190042135.png)

```java
 /**
     * Mybatis 缓存执行器
     */
    @SneakyThrows
    @Disabled
    @Test
    public void testCachingExecutor() {
        final String statementId = "com.mercymodest.mybatis.executor.mapper.user.UserMapper.selectById";
        final Integer parameter = 1;
        MappedStatement mappedStatement = configuration.getMappedStatement(statementId);
        SimpleExecutor simpleExecutor = new SimpleExecutor(configuration, jdbcTransaction);
        Executor cachingExecutor = new CachingExecutor(simpleExecutor);
        cachingExecutor.query(mappedStatement, parameter, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER);
        // 提交之后,二级缓存才会更新
        cachingExecutor.commit(true);
        cachingExecutor.query(mappedStatement, parameter, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER);
    }

```

#### 执行结果示例

![image-20211219191538055](https://img.mercymodest.com/public/image-20211219191538055.png)

### `BatchExecutor`代码梗概

![image-20211220224637744](https://img.mercymodest.com/public/image-20211220224637744.png)

- 最终的SQL相同
- MappedStatement 相同
- 必须是连续的 (保证SQL的执行顺序)

![image-20211220224754627](https://img.mercymodest.com/public/image-20211220224754627.png)

## Mybatis执行过程简单探究

> 以<span style="color:red">`SqlSession#selectList(java.lang.String)`</span>为切入口，简单探究 Mybatis 执行过程

### 测试代码

```java
  /**
     * 测试 Mybatis 执行流程
     * <pre>
     *       1. {@link  SqlSession#selectList(String)}
     *       2. {@link  CachingExecutor#query(MappedStatement, Object, RowBounds, ResultHandler)}
     *       3. {@link  BaseExecutor#query(MappedStatement, Object, RowBounds, ResultHandler)}
     *       4. {@link  SimpleExecutor#doQuery(MappedStatement, Object, RowBounds, ResultHandler, BoundSql)}
     * </pre>
     */
    @Disabled
    @Test
    public void testMybatisExecutionSteps() {
        try (SqlSession sqlSession = sessionFactory.openSession()) {
            final String statementId = "com.mercymodest.mybatis.executor.mapper.user.UserMapper.userList";
            List<Object> userList = sqlSession.selectList(statementId);
            Optional.ofNullable(userList)
                    .ifPresent(list -> list.forEach(System.out::println));
        }
    }
```

### 我们先简要看一下 `SqlSession` 的整体结构

![image-20211219220532501](https://img.mercymodest.com/public/image-20211219220532501.png)

### Debug 整体流程梗概

#### 版本小提示

> JDK:
>
>  ![image-20211219220643668](https://img.mercymodest.com/public/image-20211219220643668.png)
>
> Mybatis verison:  **3.5.1**
>
> ![image-20211219220710719](https://img.mercymodest.com/public/image-20211219220710719.png)

#### 大致流程梗概

1. 进入 `org.apache.ibatis.session.defaults.DefaultSqlSession#selectList(java.lang.String, java.lang.Object, org.apache.ibatis.session.RowBounds)`

   ![image-20211219221008286](https://img.mercymodest.com/public/image-20211219221008286.png)

2. 进入 `org.apache.ibatis.executor.CachingExecutor#query(org.apache.ibatis.mapping.MappedStatement, java.lang.Object, org.apache.ibatis.session.RowBounds, org.apache.ibatis.session.ResultHandler, org.apache.ibatis.cache.CacheKey, org.apache.ibatis.mapping.BoundSql)`

   ![image-20211219221246750](https://img.mercymodest.com/public/image-20211219221246750.png)

3. 进入 `org.apache.ibatis.executor.BaseExecutor#query(org.apache.ibatis.mapping.MappedStatement, java.lang.Object, org.apache.ibatis.session.RowBounds, org.apache.ibatis.session.ResultHandler, org.apache.ibatis.cache.CacheKey, org.apache.ibatis.mapping.BoundSql)`

   ![image-20211219221631501](https://img.mercymodest.com/public/image-20211219221631501.png)

   1. ![image-20211219221732949](https://img.mercymodest.com/public/image-20211219221732949.png)

4. 进入 `org.apache.ibatis.executor.SimpleExecutor#doQuery`

   ![image-20211219221824560](https://img.mercymodest.com/public/image-20211219221824560.png)

#### 执行结果示例

![image-20211219221955632](https://img.mercymodest.com/public/image-20211219221955632.png)

## Mybatis执行器梗概

tips: 图片来源： [源码阅读网](http://www.coderead.cn/home/index.html)

![image-20211220225100228](https://img.mercymodest.com/public/image-20211220225100228.png)
