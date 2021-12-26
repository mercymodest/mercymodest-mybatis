package com.mercymodest.mybatis.statementhandler;

import com.mercymodest.mybatis.statementhandler.mapper.user.UserMapper;
import lombok.SneakyThrows;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Mybatis 参数处理器 测试
 *
 * @author ZGH.MercyModest
 * @version V1.0.0
 * @create 2021/12/25
 */
public class MybatisParameterHandlerTest {
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

    @Disabled
    @Test
    public void testMultiParameterHandlerTest() {
        try (SqlSession sqlSession = sessionFactory.openSession(true)) {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            final Integer id = 1;
            final String newUserName = LocalDateTime.now().toString();
            userMapper.updateUsername(id, newUserName);
        }
    }

    @Disabled
    @Test
    public void testMybatisStatementHandler() {
        try (SqlSession sqlSession = sessionFactory.openSession(true)) {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            final Integer id = 1;
            System.out.println(userMapper.selectById(id));
        }
    }
}
