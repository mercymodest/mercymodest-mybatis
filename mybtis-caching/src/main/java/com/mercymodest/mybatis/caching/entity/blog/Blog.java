package com.mercymodest.mybatis.caching.entity.blog;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Blog
 *
 * @author ZGH.MercyModest
 * @version V1.0.0
 * @create 2021/12/24
 */
@Data
@Accessors(chain = true)
public class Blog {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 博文名称
     */
    private String blogName;

    /**
     * 博文创建时间
     */
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBlogName() {
        return blogName;
    }

    public void setBlogName(String blogName) {
        this.blogName = blogName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}

