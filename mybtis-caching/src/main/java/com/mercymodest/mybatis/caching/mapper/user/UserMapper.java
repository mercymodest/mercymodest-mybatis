package com.mercymodest.mybatis.caching.mapper.user;

import com.mercymodest.mybatis.caching.entity.user.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author ZGH.MercyModest
 * @version V1.0.0
 * @create 2021/12/06
 */
@CacheNamespace
public interface UserMapper {

    /**
     * 新增 {@code  user}
     *
     * @param user {@link  User}
     * @return {@code  int}  受影响行
     */
    @Insert(value = " INSERT INTO tb_user(username, gender, motto) VALUES (#{user.username} ,#{user.gender} ,#{user.motto} )")
    int insertUser(@Param("user") User user);

    /**
     * 通过 {@code  id} 查找 {@code  User}
     *
     * @param id {@code id}
     * @return {@code  user}
     */
    @Select(value = "SELECT id,username,gender,motto FROM tb_user WHERE id =#{id} ")
    User selectById(Integer id);

    /**
     * 通过 {@code  id} 更新 {@code  username}
     *
     * @param id       {@code  id}
     * @param username {@code  username}
     * @return {@code int} 受影响行
     */
    @Update(value = "UPDATE tb_user SET username=#{username}  WHERE id=#{id} ")
    int updateUsername(@Param("id") Integer id, @Param("username") String username);

    /**
     * 查询 {@link  User} List 限制上限 200 条数据
     *
     * @return {@code  List<User> }
     */
    @Select(value = "SELECT id,username,gender,motto FROM tb_user LIMIT 200")
    List<User> userList();
}
