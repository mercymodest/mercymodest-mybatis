package com.mercymodest.mybatis.executor;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import com.mercymodest.mybatis.executor.entity.user.User;
import com.mercymodest.mybatis.executor.mapper.user.UserMapper;
import lombok.SneakyThrows;
import org.apache.ibatis.executor.*;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.jdbc.JdbcTransaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author ZGH.MercyModest
 * @version V1.0.0
 * @create 2021/12/06
 */
public class MybatisExecutorTest {


    /**
     * {@link  SqlSessionFactory}
     */
    private static SqlSessionFactory sessionFactory;

    /**
     * {@link  Connection}
     */
    private static Connection connection;

    /**
     * {@link  JdbcTransaction}
     */
    private static JdbcTransaction jdbcTransaction;

    /**
     * {@link  Configuration}
     */
    private static Configuration configuration;

    @SneakyThrows
    @BeforeAll
    public static void beforeAll() {
        final String mybatisConfigFile = "mybatis-config.xml";
        sessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream(mybatisConfigFile));
        configuration = sessionFactory.getConfiguration();
        connection = sessionFactory.openSession().getConnection();
        jdbcTransaction = new JdbcTransaction(connection);
    }

    @SneakyThrows
    @AfterAll
    public static void afterAll() {
        if (Objects.nonNull(jdbcTransaction)) {
            jdbcTransaction.close();
        }
        if (Objects.nonNull(connection)) {
            connection.close();
        }
    }


    @SneakyThrows
    @Disabled
    @Test
    public void testEnvironment() {
        final String mybatisConfigFile = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(mybatisConfigFile);
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sessionFactory.openSession(true);
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        List<User> userList = generateUser(2);
        if (CollectionUtil.isNotEmpty(userList)) {
            userList.forEach(userMapper::insertUser);
        }
    }


    /**
     * 生成指定数量{@code count} 的 {@link User}
     *
     * @param count {@code count} 需要生成的用户数量
     * @return {@code List<User> }
     */
    private List<User> generateUser(Integer count) {
        if (Objects.isNull(count) || count <= 0) {
            count = 5;
        }
        List<User> userList = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            userList.add(new User()
                    .setUsername(RandomUtil.randomString(2))
                    .setGender(RandomUtil.randomInt(0, 2))
                    .setMotto(RandomUtil.randomString(50)));
        }
        return userList;
    }

    /**
     * Mybatis 简单执行器
     */
    @SneakyThrows
    @Disabled
    @Test
    public void testSimpleExecutor() {
        final String statementId = "com.mercymodest.myabtis.executor.mapper.user.UserMapper.selectById";
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

    /**
     * Mybatis  重用执行器
     */
    @SneakyThrows
    @Disabled
    @Test
    public void testReuseExecutor() {
        final String statementId = "com.mercymodest.myabtis.executor.mapper.user.UserMapper.selectById";
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

    /**
     * Mybatis  批处理执行器
     */
    @SneakyThrows
    @Disabled
    @Test
    public void testBatchExecutor() {
        final String statementId = "com.mercymodest.myabtis.executor.mapper.user.UserMapper.updateUsername";
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

    /**
     * Mybatis 缓存执行器
     */
    @SneakyThrows
    @Disabled
    @Test
    public void testCachingExecutor() {
        final String statementId = "com.mercymodest.myabtis.executor.mapper.user.UserMapper.selectById";
        final Integer parameter = 1;
        MappedStatement mappedStatement = configuration.getMappedStatement(statementId);
        SimpleExecutor simpleExecutor = new SimpleExecutor(configuration, jdbcTransaction);
        Executor cachingExecutor = new CachingExecutor(simpleExecutor);
        cachingExecutor.query(mappedStatement, parameter, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER);
        // 提交之后,二级缓存才会更新
        cachingExecutor.commit(true);
        cachingExecutor.query(mappedStatement, parameter, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER);
    }

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
            final String statementId = "com.mercymodest.myabtis.executor.mapper.user.UserMapper.userList";
            List<Object> userList = sqlSession.selectList(statementId);
            Optional.ofNullable(userList)
                    .ifPresent(list -> list.forEach(System.out::println));
        }
    }

}
