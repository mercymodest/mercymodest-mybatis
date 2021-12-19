package com.mercymodest.myabtis.executor.mapper.user;

import com.mercymodest.myabtis.executor.entity.user.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * @author ZGH.MercyModest
 * @version V1.0.0
 * @create 2021/12/06
 */
public interface UserMapper {


    @Insert(value = " INSERT INTO tb_user(username, gender, motto) VALUES (#{user.username} ,#{user.gender} ,#{user.motto} )")
    int insertUser(@Param("user") User user);
}
