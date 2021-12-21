package com.mercymodest.mybatis.caching.mybatisspring;

import com.mercymodest.mybatis.caching.entity.user.User;
import com.mercymodest.mybatis.caching.mapper.user.UserMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

/**
 * Mybatis-Spring 测试
 *
 * @author ZGH.MercyModest
 * @version V1.0.0
 * @create 2021/12/21
 */
public class MybatisSpringTest {

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
}
