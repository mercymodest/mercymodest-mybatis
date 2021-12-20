# Mybatis源码学习Mybatis多级缓存

## Mybatis一级缓存命中条件

> - 最终SQL相同，SQL参数相同
> - 同一 `SqlSession`
> - 同一 MappedStatement id
> - `RowBounds`最终返回行相同

## Mybatis一级缓存失效场景

> - 未手动情况缓存(提交，回滚)
> - 未调用 `flushCache=true` 的查询
> - 未执行`UPDATE`语句
> - `LocalCacheScope` 不是 `STATEMENT`

### 图示梗概

tips: 图片来源： [源码阅读网](http://www.coderead.cn/home/index.html)

![image-20211220231418927](https://img.mercymodest.com/public/image-20211220231418927.png)

## Mybatis一级缓存源码执行梗概

tips: 图片来源： [源码阅读网](http://www.coderead.cn/home/index.html)

![image-20211220232234468](https://img.mercymodest.com/public/image-20211220232234468.png)

### 示例代码

```java
    /**
     * 测试 Mybatis 一级缓存
     */
    @Disabled
    @Test
    public void testMybatisFirstLevelCachingByPrototype() {
        try (SqlSession sqlSession = sessionFactory.openSession(true)) {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            User user = userMapper.selectById(1);
            User user2 = userMapper.selectById(user.getId());
            System.out.println(user == user2 ? "命中缓存" : "未命中缓存");
        }
    }
```

#### cacheKey

![image-20211220233720316](https://img.mercymodest.com/public/image-20211220233720316.png)

1. ![image-20211220233829099](https://img.mercymodest.com/public/image-20211220233829099.png)
2. ![image-20211220234045067](https://img.mercymodest.com/public/image-20211220234045067.png)