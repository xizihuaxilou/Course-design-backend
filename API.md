# ChatDemo 接口文档

> 基于 `chatdemo` 后端源码（Spring Boot 3.5.4 + MyBatis）。默认服务地址为 `http://localhost:8081`，所有接口均为 RESTful JSON，除特别说明外均返回 HTTP 200。

## 通用说明

- **鉴权**：当前接口未实现 Token/Session 校验，前端只需在请求体中传参即可。
- **数据格式**：请求与响应均为 `application/json`。
- **错误处理**：后端直接抛出异常或返回 `null`，无统一 `code/message` 包装，需要前端自行兼容 `null/空数组` 场景。
- **数据库**：接口直接访问 `chatdemo` 数据库中的表（`user`、`singlemessage`、`groupdata`、`group_message_data` 等）。

## 用户模块 `/user`

| 接口          | 方法                                   | 描述     | 请求体  | 响应 |
| ------------- | -------------------------------------- | -------- | ------- | ---- |
| `/user/login` | **GET**（源码如此，带 `@RequestBody`） | 用户登录 | ```json |

{
"username": "alice",
"password": "123456"
}
```|`LogUser` 对象：

```json
{
  "id": "1",
  "username": "alice",
  "password": "123456"
}
```

> **注意**：Spring 默认 GET 不支持 Body，建议将后端改为 `@PostMapping`。现阶段可在 Postman 之类工具通过 GET + raw body 访问，或临时将方法改为 POST。 |
> | `/user/register` | POST | 用户注册 | 同上 | `null`（插入成功无返回体） |

## 单聊模块 `/chat/single`

| 接口                          | 方法 | 描述                                     | 请求/路径参数     | 响应示例 |
| ----------------------------- | ---- | ---------------------------------------- | ----------------- | -------- |
| `/chat/single/name_list/{id}` | GET  | 获取除自身外的所有用户（可视为好友列表） | `id`：当前用户 ID | ```json  |

[
{ "id": "2", "username": "bob", "password": "***" },
{ "id": "3", "username": "carol" }
]

````|
| `/chat/single/send` | POST | 发送私聊消息 | Body：
```json
{
  "sendId": 1,
  "receiveId": 2,
  "content": "你好"
}
``` | `null`（仅入库） |
| `/chat/single/list/{sendId}/{receiveId}` | GET | 查询两人之间的聊天记录 | `sendId`、`receiveId` | ```json
[
  { "sendId": 1, "receiveId": 2, "content": "hi" },
  { "sendId": 2, "receiveId": 1, "content": "你好" }
]
``` |

## 群聊模块 `/chat/group`

| 接口 | 方法 | 描述 | 请求/路径参数 | 响应示例 |
| --- | --- | --- | --- | --- |
| `/chat/group/list_name/{id}` | GET | 查询用户所在群列表（当前实现未按用户过滤，直接返回 `groupdata` 中全部 group_name） | `id`：当前用户 ID | ```json
["前端交流群", "产品讨论组"]
``` |
| `/chat/group/create` | POST | 创建群聊并批量写入成员 | Body：
```json
{
  "groupName": "前端交流群",
  "memberIds": [1, 2, 3]
}
``` | `null` |
| `/chat/group/send` | POST | 发送群聊消息 | Body：
```json
{
  "sendId": 1,
  "groupName": "前端交流群",
  "content": "大家好"
}
``` | `null` |
| `/chat/group/list/{sendId}/{groupName}` | GET | 查询某群历史消息 | `sendId`：当前用户 ID（仅作路径占位）<br>`groupName`：群名 | ```json
[
  { "sendId": 1, "groupName": "前端交流群", "content": "hi" },
  { "sendId": 2, "groupName": "前端交流群", "content": "hello" }
]
``` |

## 其他说明

- **返回值为空**：`send`/`create` 等写操作统一返回 `null`，前端应检测 HTTP 状态码或捕获异常即可。
- **字段差异**：单聊接口使用 `receiveId`，与旧前端的 `receiverId` 命名不同；请务必同步修改前端类型和接口调用。
- **群聊列表**：`/chat/group/list_name/{id}` 当前未根据用户 ID 过滤，如需基于成员过滤需调整 Mapper。
- **登录 token**：后端未实现 token 签发，前端可直接保存返回的用户信息并跳转页面。
````
