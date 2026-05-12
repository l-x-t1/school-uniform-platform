# 家长端前后端协作接口草案

本文档用于家长端前端和若依后端同学对齐接口。前端已经统一通过 `utils/request.js` 调接口，请求地址在 `config/index.js` 里配置。

## 通用约定

- 本地后端地址：`http://localhost:8080`
- token 存储 key：`token`
- 请求头：`Authorization: Bearer <token>`
- 分页参数：`pageNum`、`pageSize`
- 若依标准返回：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {}
}
```

- 分页列表建议返回：

```json
{
  "code": 200,
  "msg": "查询成功",
  "rows": [],
  "total": 0
}
```

## 登录认证

### 发送短信验证码

`POST /school/mobile/sms-code`

请求：

```json
{
  "phone": "13800138000",
  "scene": "parent_login"
}
```

### 手机号验证码登录

`POST /school/mobile/login`

请求：

```json
{
  "phone": "13800138000",
  "code": "123456"
}
```

返回：

```json
{
  "code": 200,
  "data": {
    "token": "xxx",
    "simId": "家长或学生绑定主键",
    "isImprove": true,
    "user": {
      "userId": 1,
      "nickName": "家长用户",
      "phone": "13800138000",
      "userType": "parent"
    }
  }
}
```

### 微信手机号登录

`POST /school/mobile/wechat-phone-login`

请求：

```json
{
  "code": "微信手机号 code",
  "encryptedData": "",
  "iv": ""
}
```

返回结构同手机号验证码登录。

## 校服商品

### 校服列表

`GET /school/uniform/list`

查询参数：

```text
pageNum=1
pageSize=10
keyword=
category=
grade=
season=
minPrice=
maxPrice=
```

每条数据建议字段：

```json
{
  "id": 1,
  "name": "秋季校服套装",
  "price": 268,
  "salesCount": 120,
  "image": "/profile/upload/xxx.png",
  "season": "autumn",
  "grade": "primary"
}
```

前端也兼容这些别名：`uniformId`、`productId`、`uniformName`、`productName`、`salePrice`、`coverImage`、`mainImage`、`imgUrl`。

### 校服详情

`GET /school/uniform/{id}`

后续详情页建议补：商品基础信息、SKU 尺码库存、质检报告、适用学校/年级、购买说明。

## 订单

### 创建订单

`POST /school/order`

请求：

```json
{
  "source": "buyNow",
  "studentId": 1,
  "addressId": 1,
  "remark": "配送备注",
  "items": [
    {
      "uniformId": 1,
      "skuId": 10,
      "size": "L",
      "quantity": 1
    }
  ]
}
```

返回：

```json
{
  "code": 200,
  "data": {
    "id": 100001,
    "orderId": 100001,
    "orderNo": "SJZ20260502001"
  }
}
```

### 订单列表

`GET /school/order/list`

查询参数：

```text
pageNum=1
pageSize=10
status=unpaid
```

前端状态值：

```text
unpaid      待付款
unshipped   待发货
shipped     已发货
completed   已完成
cancelled   已取消
```

后端也可以先返回数字，前端目前兼容：

```text
0 unpaid
1 unshipped
2 shipped
3 completed
4 cancelled
```

每条数据建议字段：

```json
{
  "id": 100001,
  "orderNo": "SJZ20260502001",
  "status": "unpaid",
  "createTime": "2026-05-02 10:30:00",
  "totalAmount": "268.00",
  "products": [
    {
      "id": 1,
      "name": "秋季校服套装",
      "image": "/profile/upload/xxx.png",
      "price": "268.00",
      "quantity": 1,
      "size": "L",
      "color": "深蓝色"
    }
  ]
}
```

### 取消订单

`PUT /school/order/cancel/{id}`

### 确认收货

`PUT /school/order/confirm/{id}`

### 支付订单

`POST /school/order/pay/{id}`

请求：

```json
{
  "payType": "wxpay"
}
```

返回：

```json
{
  "code": 200,
  "data": {
    "paid": true,
    "payTime": "2026-05-02 12:00:00"
  }
}
```

说明：正式接微信支付时，这个接口可以返回微信支付所需参数；前端再调用 `uni.requestPayment`。

## 购物车

### 购物车列表

`GET /school/cart/list`

每条数据建议字段：

```json
{
  "id": 1,
  "uniformId": 1,
  "skuId": 10,
  "name": "秋季校服套装",
  "spec": "男生款",
  "size": "L",
  "price": "268.00",
  "quantity": 1,
  "image": "/profile/upload/xxx.png"
}
```

### 加入购物车

`POST /school/cart`

请求：

```json
{
  "uniformId": 1,
  "skuId": 10,
  "size": "L",
  "quantity": 1
}
```

### 修改购物车数量

`PUT /school/cart`

请求：

```json
{
  "id": 1,
  "cartId": 1,
  "quantity": 2
}
```

### 删除购物车

`DELETE /school/cart/{ids}`

示例：

```text
DELETE /school/cart/1,2,3
```

## 学生

### 当前家长默认学生

`GET /school/student/default`

返回：

```json
{
  "code": 200,
  "data": {
    "id": 1,
    "studentId": 1,
    "name": "张同学",
    "studentName": "张同学",
    "schoolName": "石家庄市示例学校",
    "gradeName": "六年级",
    "className": "1班"
  }
}
```

### 当前家长学生列表

`GET /school/student/my-list`

查询参数：

```text
simId=家长绑定标识
```

### 学生详情

`GET /school/student/{id}`

### 新增学生

`POST /school/student`

请求字段建议：

```json
{
  "name": "张小明",
  "sex": "0",
  "studentId": "20260001",
  "school": "石家庄市示例学校",
  "grade": "六年级",
  "className": "1班",
  "topSize": "165/84A",
  "bottomSize": "L",
  "shoesSize": "39码"
}
```

### 修改学生

`PUT /school/student`

### 删除学生

`DELETE /school/student/{id}`

## 收货地址

### 地址列表

`GET /school/address/list`

返回字段建议：

```json
{
  "code": 200,
  "rows": [
    {
      "id": 1,
      "name": "张先生",
      "phone": "13812345678",
      "province": "河北省",
      "city": "石家庄市",
      "district": "长安区",
      "detailAddress": "和平路123号",
      "isDefault": true,
      "tag": "家"
    }
  ],
  "total": 1
}
```

### 地址详情

`GET /school/address/{id}`

### 新增地址

`POST /school/address`

### 修改地址

`PUT /school/address`

### 删除地址

`DELETE /school/address/{id}`

### 设置默认地址

`PUT /school/address/default/{id}`

## 前端开发备注

- 后端未完成时，开发环境会启用 mock fallback，不影响页面继续开发。
- 生产环境需要把 `config/index.js` 的 `baseUrl` 改成正式接口域名，并关闭 mock fallback。
- 新页面不要再直接写散落的 `uni.request`，优先在 `api/` 目录新增模块，再由页面调用。

## 你负责前端时和若依后端怎么配合

1. 先确认若依后端本地地址，一般是 `http://localhost:8080`。如果后端同学改了端口，你只需要改 `config/index.js` 的 `baseUrl`。
2. 登录接口能通以后，用浏览器开发者工具看请求头里有没有 `Authorization: Bearer <token>`。
3. 每接一个页面，先让后端同学给接口路径、请求参数、返回 JSON 示例。你把对应方法写到 `api/xxx.js`，页面只调用 `api` 方法。
4. 若依分页接口通常返回 `rows` 和 `total`，所以列表页优先按这个格式接。
5. 若依上传文件通常是 `/common/upload`，返回文件地址后，前端保存到对应表单字段。
6. 遇到跨域问题，不是页面代码问题，通常让后端在若依里配置 CORS，或者 HBuilderX H5 代理。
7. 后端字段名和页面字段名不一致时，不要在模板里到处改，优先在页面的 `normalizeXxx` 方法里做字段适配。
8. 你不需要熟悉若依全部功能，前端最常用的是：接口地址、token、字典数据、分页格式、上传返回格式、状态码和错误信息。
