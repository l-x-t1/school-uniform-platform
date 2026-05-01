<template>
	<view class="container">
		<!-- 内容区域 -->
		<view class="content-wrapper">
			<!-- 标题区域 -->
			<view class="header">
				<text class="main-title">石家庄市第二中学2024级新生校服采购{{title}}</text>
				<view class="info-row">
					<text class="time">会议时间：2024-06-27 00:00</text>
				</view>
			</view>
			
			<!-- 正文内容 -->
			<view class="content">
				<view class="section">
					<view class="section-title">一、会议背景</view>
					<view class="section-content">
						<text class="text-paragraph">根据《石家庄市教育局关于规范中小学校服采购工作的指导意见》（石教〔2023〕25号）要求，为确保2024级新生校服采购工作规范有序开展，学校召开专题会议研究部署相关工作。</text>
					</view>
				</view>
				
				<view class="section">
					<view class="section-title">二、参会人员</view>
					<view class="section-content">
						<text class="text-paragraph">校长、分管副校长、教导主任、年级主任、家委会代表等共12人</text>
					</view>
				</view>
				
				<view class="section">
					<view class="section-title">三、会议决议</view>
					<view class="section-content">
						<text class="text-paragraph">1. 成立校服采购工作领导小组，由校长担任组长，分管副校长担任副组长；</text>
						<text class="text-paragraph">2. 确定采购工作时间节点，要求在7月底前完成全部采购流程；</text>
						<text class="text-paragraph">3. 采购过程坚持"公开、公平、公正"原则，确保家长充分参与；</text>
						<text class="text-paragraph">4. 严格执行"阳光采购"要求，所有环节接受社会监督。</text>
					</view>
				</view>
				
				<view class="section">
					<view class="section-title">四、工作要求</view>
					<view class="section-content">
						<text class="text-paragraph">1. 严格执行上级要求，确保采购工作合规有序；</text>
						<text class="text-paragraph">2. 广泛征求家长意见，充分尊重家长选择权；</text>
						<text class="text-paragraph">3. 严把质量关，确保校服质量符合国家标准；</text>
						<text class="text-paragraph">4. 做好信息公开，主动接受社会监督。</text>
					</view>
				</view>
			</view>
			
			<!-- 底部签章区 -->
			<view class="footer">
				<text class="sign">石家庄市第二中学</text>
				<text class="date">2024年6月27日</text>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				title: '',
				step: ''
			}
		},
		onLoad(options) {
			// 获取页面参数
			this.title = options.title || '';
			this.step = options.step || '';
			// 设置导航栏标题
			uni.setNavigationBarTitle({
				title: '会议纪要'
			});
		}
	}
</script>

<style lang="scss">
	.container {
		min-height: 100vh;
		background-color: #fff;
	}
	
	.content-wrapper {
		padding: 30rpx;
		background-color: #fff;
		min-height: 100vh;
		
		.header {
			padding-bottom: 30rpx;
			border-bottom: 1rpx solid #f0f0f0;
			
			.main-title {
				font-size: 36rpx;
				font-weight: 600;
				color: #333;
				line-height: 1.4;
				margin-bottom: 20rpx;
			}
			
			.info-row {
				display: flex;
				align-items: center;
				
				.time {
					font-size: 26rpx;
					color: #999;
				}
			}
		}
		
		.content {
			padding: 30rpx 0;
			
			.section {
				margin-bottom: 40rpx;
				
				.section-title {
					font-size: 32rpx;
					font-weight: 600;
					color: #333;
					margin-bottom: 20rpx;
				}
				
				.section-content {
					.text-paragraph {
						font-size: 28rpx;
						color: #666;
						line-height: 1.6;
						margin-bottom: 16rpx;
						display: block;
						
						&:last-child {
							margin-bottom: 0;
						}
					}
				}
			}
		}
		
		.footer {
			padding-top: 60rpx;
			text-align: right;
			
			.sign {
				font-size: 28rpx;
				color: #333;
				font-weight: 500;
				display: block;
				margin-bottom: 10rpx;
			}
			
			.date {
				font-size: 26rpx;
				color: #666;
			}
		}
	}
</style> 