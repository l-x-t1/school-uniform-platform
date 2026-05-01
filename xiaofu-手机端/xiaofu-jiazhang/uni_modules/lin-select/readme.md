# lin-select

## **使用说明**

### **==注意：需要依赖 [uni-easyinput](https://ext.dcloud.net.cn/plugin?name=uni-easyinput) ，请先导入[uni-easyinput](https://ext.dcloud.net.cn/plugin?name=uni-easyinput)后再使用；==**

```html
//将插件导入到Hubilder之后,直接通过标签使用<lin-select />
<template>
  <!-- 普通用法 -->

  <lin-select
    :list="productList1"
    value-key="value"
    name-key="name"
    max-height="180"
    placeholder="请输入商品名称"
    @input="input2"
    v-model="mytext1"
    @confirm="confirm"
  />

  <!-- 远程加载数据 -->
  <lin-select
    :list="productList2"
    :loading="loading"
    loading-text="数据加载中"
    value-key="value"
    name-key="name"
    max-height="180"
    placeholder="请输入商品名称"
    @input="input2"
    v-model="mytext2"
  />
</template>
```

```javascript
<script>
export default {
  data() {
    return {
      mytext1: 1,
      mytext2: '',
      productList1: [{ "name": "特选痩肉", value: 0 }, { "name": "特选键子肉", value: 1 }, { "name": "特选梅肉", value: 2 }],
      productList2: [],
    }
  },
  onLoad() {

  },
  methods: {

    confirm(val) {
      console.log(val, 'confirm')
    },

    input1(val) {
      console.log(val, 666)
    },

    // 远程加载数据
    input2(val) {
      setTimeout(() => {
        this.productList2 = [
          { "name": "远程加载分割猪肉及附件", value: 0 },
          { "name": "远程加载良种白条猪肉", value: 1 },
          { "name": "远程加载土猪白条猪肉", value: 2 }
        ]
      }, 1000)
    }
  }
}
</script>
```

# **Props**

| 参数         | 说明              | 类型             | 是否必填 |
| :----------- | :---------------- | :--------------- | :------- |
| list         | 数据源数组        | Array            | 必填     |
| value-key    | 取值的 key        | string           | 必填     |
| name-key     | 显示的 key        | string           | 必填     |
| max-height   | 列表最大高度      | string \| number | 否       |
| @input       | 输入框 input 事件 | function         | 否       |
| @confirm     | 点击选项事件      | function         | 否       |
| v-model      | 绑定的字段        | -                | 否       |
| loading      | 是否正在加载      | Boolean          | 否       |
| loading-text | 远程加载中的文案  | string           | 否       |

## **联系作者**

如使用上有问题可以留言或者联系我哈，我会一直更新的；
