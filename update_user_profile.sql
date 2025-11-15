-- 为 user 表添加个人资料字段
-- 如果字段已存在，可以忽略相应的 ALTER TABLE 语句

-- 添加真实姓名字段
ALTER TABLE user ADD COLUMN real_name VARCHAR(50) NULL COMMENT '真实姓名';

-- 添加星座字段
ALTER TABLE user ADD COLUMN constellation VARCHAR(20) NULL COMMENT '星座';

-- 添加生日字段
ALTER TABLE user ADD COLUMN birthday DATE NULL COMMENT '生日';

-- 添加兴趣爱好字段
ALTER TABLE user ADD COLUMN interests VARCHAR(500) NULL COMMENT '兴趣爱好';

-- 查看更新后的表结构
DESC user;
