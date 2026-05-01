<template>
	<view class="container">
		<!-- 发布时间 -->
		<!-- <view class="publish-time">{{currentTime}}</view> -->
		
		<!-- 列表内容 -->
		<view class="content-wrapper">
			<view class="process-list">
				<view class="process-item" v-for="(item, index) in filteredList" :key="index" @click="viewDetail(item)">
					<view class="item-card">
						<text class="item-title">{{item.title}}</text>
						<view class="item-info">
							<text class="info-label">会议时间：</text>
							<text class="info-value">{{item.time}}</text>
						</view>
					</view>
					<uni-icons type="right" size="16" color="#CCCCCC"></uni-icons>
				</view>
			</view>
			
			<!-- 空状态 -->
			<view class="empty-state" v-if="filteredList.length === 0">
				<image src="/static/images/empty.png" mode="aspectFit" class="empty-image"></image>
				<text class="empty-text">暂无相关公告</text>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				title: '',
				type: '',
				currentTime: this.formatDateTime(new Date()),
				processList: [
					{
						id: 1,
						type: 'meeting',
						title: '石家庄市第二中学2024级新生校服采购学校领导班子会议',
						time: '2024-06-27 00:00',
						status: 'completed'
					},
					{
						id: 2,
						type: 'meeting',
						title: '2024级新生校服采购工作安排会议',
						time: '2024-06-28 09:30',
						status: 'completed'
					}
				]
			}
		},
		computed: {
			filteredList() {
				return this.processList.filter(item => item.type === this.type);
			}
		},
		onLoad(options) {
			this.title = options.title || '学校班子会议纪要公布';
			this.type = options.type || '';
			// 设置导航栏标题
			uni.setNavigationBarTitle({
				title: this.title
			});
		},
		methods: {
			viewDetail(item) {
				uni.navigateTo({
					url: '/pages/process/process-detail?id=' + item.id + '&title=' + item.title
				});
			},
			
			formatDateTime(date) {
				const year = date.getFullYear();
				const month = String(date.getMonth() + 1).padStart(2, '0');
				const day = String(date.getDate()).padStart(2, '0');
				const hours = String(date.getHours()).padStart(2, '0');
				const minutes = String(date.getMinutes()).padStart(2, '0');
				return year + '-' + month + '-' + day + ' ' + hours + ':' + minutes;
			}
		}
	}
</script>

<style lang="scss">
	.container {
		min-height: 100vh;
		background-color: #F5F6F8;
	}
	
	.publish-time {
		padding: 24rpx 30rpx;
		font-size: 26rpx;
		color: #999;
		text-align: center;
		background-color: #fff;
	}
	
	.content-wrapper {
		padding: 20rpx 30rpx;
		
		.process-list {
			.process-item {
				margin-bottom: 20rpx;
				background-color: #fff;
				border-radius: 16rpx;
				padding: 30rpx;
				display: flex;
				justify-content: space-between;
				align-items: center;
				
				.item-card {
					flex: 1;
					margin-right: 20rpx;
					
					.item-title {
						font-size: 32rpx;
						color: #333;
						font-weight: 400;
						margin-bottom: 20rpx;
						display: block;
						line-height: 1.4;
					}
					
					.item-info {
						display: flex;
						align-items: center;
						
						.info-label {
							font-size: 28rpx;
							color: #666;
						}
						
						.info-value {
							font-size: 28rpx;
							color: #666;
						}
					}
				}
				
				&:active {
					opacity: 0.8;
				}
			}
		}
		
		.empty-state {
			padding: 120rpx 0;
			display: flex;
			flex-direction: column;
			align-items: center;
			
			.empty-image {
				width: 240rpx;
				height: 240rpx;
				margin-bottom: 20rpx;
			}
			
			.empty-text {
				font-size: 28rpx;
				color: #999;
			}
		}
	}
</style> 