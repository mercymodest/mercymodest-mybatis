package com.mercymodest.mybatis.executor;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import com.mercymodest.myabtis.executor.entity.user.User;
import com.mercymodest.myabtis.executor.mapper.user.UserMapper;
import lombok.SneakyThrows;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author ZGH.MercyModest
 * @version V1.0.0
 * @create 2021/12/06
 */
public class MybatisExecutorTest {


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
}
