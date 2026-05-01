## 基于最新版uni-data-select改造的可输入自定义选项的下拉输入文本框选择器
## 实现了与elementUI中<el-select>同样的功能
##使用案例：
```
<template>
	<view class="content">
		 <view class="title">下拉输入文本框</view>
		 <niceui-data-select-input
		        v-model="value"
		        :localdata="range"
		        @change="change"
		      ></niceui-data-select-input>
	</view>
</template>

<script>
	import NiceUIDataSelectInput from '@/uni_modules/niceui-data-select-input/components/niceui-data-select-input/niceui-data-select-input.vue'
	export default {
		components:{
			NiceUIDataSelectInput
		},
		data() {
			return {
				value: '',
				range: [
				  { value: 0, text: "篮球" },
				  { value: 1, text: "足球" },
				  { value: 2, text: "游泳" },
				],
				checkItems:[]
			}
		},
		onLoad() {

		},
		methods: {
			change(e) {
			        console.log("e:", e);
			      },
		}
	}
</script>

<style>
	.content {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}
	.title {
		font-size: 36rpx;
		color: #333;
		margin-top: 60rpx;
		margin-bottom: 50rpx;
	}
</style>

```

##使用教程与uni-data-select一样，可点击下面查看文档进行使用。
## DataSelect 下拉框选择器
> **组件名：uni-data-select**
> 代码块： `uDataSelect`

当选项过多时，使用下拉菜单展示并选择内容

### [查看文档](https://uniapp.dcloud.io/component/uniui/uni-data-select)
#### 如使用过程中有任何问题，或者您对uni-ui有一些好的建议，欢迎加入 uni-ui 交流群：871950839 
