package com.mercymodest.mybatis.jdbc;

import cn.hutool.core.collection.CollectionUtil;
import com.mercymodest.mybatis.executor.entity.user.User;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC 代码示例
 *
 * @author ZGH.MercyModest
 * @version V1.0.0
 * @create 2021/12/18
 */
public class JdbcCodeTest {

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
}
