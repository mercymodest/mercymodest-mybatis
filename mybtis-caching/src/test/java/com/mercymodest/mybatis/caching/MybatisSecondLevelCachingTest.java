package com.mercymodest.mybatis.caching;

import com.mercymodest.mybatis.caching.mapper.user.UserMapper;
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
import java.util.Objects;

/**
 * Mybatis 二级缓存测试
 *
 * @author ZGH.MercyModest
 * @version V1.0.0
 * @create 2021/12/24
 */
public class MybatisSecondLevelCachingTest {
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

    /**
     * 测试 Mybatis 二级缓存
     */
    @Disabled
    @Test
    public void testMybatisSecondLevelCaching() {
        try (SqlSession sqlSession = sessionFactory.openSession(true)) {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            final Integer id = 1;
            System.out.println(userMapper.selectById(id));
            System.out.println(userMapper.selectById(id));
        }
    }

}
