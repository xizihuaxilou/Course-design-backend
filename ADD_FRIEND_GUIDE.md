# 添加好友功能使用说明

## 一、数据库准备

### 1. 执行 SQL 创建好友关系表

在 MySQL 中执行以下 SQL(已保存在 `friend_relation.sql`):

```sql
CREATE TABLE IF NOT EXISTS friend_relation (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id INT NOT NULL COMMENT '用户ID',
    friend_id INT NOT NULL COMMENT '好友ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    UNIQUE KEY uk_user_friend (user_id, friend_id) COMMENT '用户-好友唯一索引',
    INDEX idx_user_id (user_id) COMMENT '用户ID索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='好友关系表';
```

执行方式:

```bash
# 方式1: 命令行
mysql -u root -p chatdemo < friend_relation.sql

# 方式2: MySQL客户端
USE chatdemo;
SOURCE E:/VsCodeProject/chatdemo/friend_relation.sql;
```

## 二、后端接口说明

### 1. 添加好友

- **接口**: `POST /chat/friend/add`
- **请求体**:

```json
{
  "userId": 1, // 当前用户ID
  "friendId": 2 // 要添加的好友ID
}
```

- **响应**:

```json
{
  "message": "添加好友成功",
  "code": "200",
  "data": null
}
```

- **说明**: 会自动双向添加好友关系(A 加 B 为好友,B 也会有 A)

### 2. 获取好友列表

- **接口**: `GET /chat/friend/list/{userId}`
- **参数**: userId - 用户 ID
- **响应**:

```json
[
  {
    "id": "2",
    "username": "zhangsan"
  },
  {
    "id": "3",
    "username": "lisi"
  }
]
```

### 3. 删除好友

- **接口**: `DELETE /chat/friend/delete`
- **请求体**:

```json
{
  "userId": 1,
  "friendId": 2
}
```

- **响应**:

```json
{
  "message": "删除好友成功",
  "code": "200",
  "data": null
}
```

## 三、前端使用方法

### 1. 在组件中添加好友

```vue
<script setup lang="ts">
import { useChatStore } from "@/store/chat";
import { ElMessage } from "element-plus";

const chatStore = useChatStore();

// 添加好友
async function handleAddFriend(friendId: number) {
  try {
    const message = await chatStore.addFriend({ id: friendId });
    ElMessage.success(message || "添加成功");
  } catch (error: any) {
    ElMessage.error(error.message || "添加失败");
  }
}
</script>

<template>
  <el-button @click="handleAddFriend(2)">添加好友</el-button>
</template>
```

### 2. 好友列表会自动刷新

登录后 `initChatData()` 会自动从数据库加载好友列表,每次添加好友后也会自动刷新。

## 四、数据流程

```
前端组件
  ↓ 调用 chatStore.addFriend({ id: 2 })
  ↓
Pinia Store (chat.ts)
  ↓ 调用 addFriendApi({ userId: 1, friendId: 2 })
  ↓
后端接口 POST /chat/friend/add
  ↓ FriendMapper.addFriend()
  ↓
数据库 INSERT INTO friend_relation
  ↓ 双向插入: (1,2) 和 (2,1)
  ↓
返回成功
  ↓
前端重新加载好友列表 initChatData()
  ↓ GET /chat/friend/list/1
  ↓
显示最新好友列表
```

## 五、注意事项

1. **用户 ID 必须真实存在**: 添加好友时,friendId 必须是 user 表中已存在的用户 ID
2. **不能重复添加**: 表中有唯一索引,重复添加会返回"已经是好友了"
3. **双向关系**: 添加好友会自动建立双向关系
4. **持久化存储**: 所有好友关系都存储在数据库,不再使用 localStorage
5. **自动刷新**: 登录时和添加好友后都会自动从数据库加载最新列表

## 六、测试步骤

1. 启动后端服务
2. 执行 SQL 创建表
3. 启动前端服务
4. 登录用户(假设 userId=1)
5. 调用添加好友接口,添加 userId=2 的用户为好友
6. 刷新页面,验证好友列表是否包含新添加的好友
7. 用 userId=2 登录,验证其好友列表是否包含 userId=1

## 七、后续可扩展功能

- [ ] 好友申请审批机制
- [ ] 好友备注功能
- [ ] 好友分组
- [ ] 黑名单功能
- [ ] 好友推荐
