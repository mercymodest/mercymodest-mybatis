package com.mercymodest.mybatis.executor.entity.user;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author ZGH.MercyModest
 * @version V1.0.0
 * @create 2021/12/06
 */
@Data
@Accessors(chain = true)
public class User implements Serializable {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 性别 :0:女 1:男 2:未知
     */
    private Integer gender;

    /**
     * 座右铭
     */
    private String motto;
}
