<template>
	<view class="container">
		
		<!-- 年级选择区域 -->
		<view class="grade-section">
			<text class="grade-tip">请选择您所在的年级</text>
			<view class="grade-picker" @click="showGradePicker">
				<text class="selected-grade">{{selectedGrade}}</text>
				<uni-icons type="bottom" size="14" color="#ffffff"></uni-icons>
			</view>
			<!-- <view class="guide-link">
				<text>统购指引</text>
				<uni-icons type="help" size="16" color="#FFFFFF"></uni-icons>
			</view> -->
		</view>
		
		<!-- 统购卡片列表 -->
		<view class="content-wrapper">
			<view class="purchase-list">
				<view class="purchase-card" v-for="(item, index) in purchaseList" :key="index">
					<!-- 公告区域 -->
					<view class="notice-section">
						<view class="notice-card" @click="viewNotice(item)">
							<text class="notice-title">{{item.title}}</text>
							<uni-icons type="right" size="16" color="#CCCCCC"></uni-icons>
						</view>
						
						<!-- 公告信息 -->
						<view class="notice-info">
							<view class="info-item">
								<text class="label">适用年级：</text>
								<text class="value">{{item.grades}}</text>
							</view>
							<view class="info-item">
								<text class="label">预售商品：</text>
								<text class="value">{{item.presaleInfo}}</text>
							</view>
							<view class="info-item">
								<text class="label">截止日期：</text>
								<text class="value warning">{{item.deadline}}</text>
								<uni-icons type="clock" size="14" color="#FF9900"></uni-icons>
							</view>
						</view>
					</view>
					
					<!-- 校服选购区域 -->
					<view class="uniform-section">
						<view class="uniform-grid">
							<!-- 男生校服 -->
							<view class="uniform-card" @click="viewMaleUniform(item)">
								<image :src="item.maleUniform.image" mode="aspectFill" class="uniform-image"></image>
								<view class="uniform-tag male">男生专区</view>
								<view class="uniform-price">
									<text class="price-symbol">¥</text>
									<text class="price-value">{{item.maleUniform.price}}</text>
								</view>
							</view>
							
							<!-- 女生校服 -->
							<view class="uniform-card" @click="viewFemaleUniform(item)">
								<image :src="item.femaleUniform.image" mode="aspectFill" class="uniform-image"></image>
								<view class="uniform-tag female">女生专区</view>
								<view class="uniform-price">
									<text class="price-symbol">¥</text>
									<text class="price-value">{{item.femaleUniform.price}}</text>
								</view>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 年级选择弹出层 -->
		<uni-popup ref="gradePicker" type="bottom">
			<view class="grade-popup">
				<view class="popup-header">
					<text>选择年级</text>
					<view class="close-btn" @click="closeGradePicker">
						<uni-icons type="close" size="20" color="#666666"></uni-icons>
					</view>
				</view>
				<view class="grade-list">
					<view 
						class="grade-item" 
						v-for="(grade, index) in gradeList" 
						:key="index"
						:class="{'active': selectedGrade === grade}"
						@click="selectGrade(grade)"
					>
						<text>{{grade}}</text>
						<uni-icons v-if="selectedGrade === grade" type="checkmarkempty" size="20" color="#409EFF"></uni-icons>
					</view>
				</view>
			</view>
		</uni-popup>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				selectedGrade: '六年级',
				gradeList: ['幼儿园', '一年级', '二年级', '三年级', '四年级', '五年级', '六年级', '初中部', '高中部'],
				purchaseList: [{
					id: 1,
					title: '北京市阳光学校夏季校服订购公告2022年11...',
					grades: '幼儿园，一年级，二年级，三年级，四...',
					presaleInfo: '送达日期以订单实物流进度为准',
					deadline: '还有1天截止付款',
					maleUniform: {
						image: '/static/nanxf.png',
						price: '128.00'
					},
					femaleUniform: {
						image: '/static/nvxf.png',
						price: '128.00'
					}
				}]
			}
		},
		methods: {
			showGradePicker() {
				this.$refs.gradePicker.open();
			},
			closeGradePicker() {
				this.$refs.gradePicker.close();
			},
			selectGrade(grade) {
				this.selectedGrade = grade;
				this.closeGradePicker();
				this.refreshPurchaseList();
			},
			refreshPurchaseList() {
				console.log('刷新列表，选中年级：', this.selectedGrade);
			},
			viewNotice(item) {
				uni.navigateTo({
					url: '/pages/notice/notice-detail?id=' + item.id
				});
			},
			viewMaleUniform(item) {
				uni.navigateTo({
					url: '/pages/uniform/uniform-detail?type=male&id=' + item.id
				});
			},
			viewFemaleUniform(item) {
				uni.navigateTo({
					url: '/pages/uniform/uniform-detail?type=female&id=' + item.id
				});
			}
		}
	}
</script>

<style lang="scss">
	.container {
		background-color: #F5F6F8;
		min-height: 100vh;
		display: flex;
		flex-direction: column;
	}
	
	.nav-bar {
		background-color: #FFFFFF;
		height: 88rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		z-index: 100;
		border-bottom: 1rpx solid #EEEEEE;
		
		.nav-title {
			font-size: 34rpx;
			color: #333333;
			font-weight: 500;
		}
	}
	
	.grade-section {
		background-color: #409eff;
		padding: 40rpx 30rpx;
		position: fixed;
		top: 76rpx;
		left: 0;
		right: 0;
		z-index: 100;
		
		.grade-tip {
			font-size: 26rpx;
			color: rgba(255, 255, 255, 0.8);
			margin-bottom: 20rpx;
		}
		
		.grade-picker {
			display: flex;
			align-items: center;
			
			.selected-grade {
				font-size: 32rpx;
				color: #FFFFFF;
				margin-right: 10rpx;
			}

			.uni-icons {
				color: #FFFFFF;
			}
		}
	}
	
	.content-wrapper {
		margin-top: 180rpx;
		flex: 1;
	}

	.purchase-list {
		padding: 20rpx;
		
		.purchase-card {
			background-color: #FFFFFF;
			margin-bottom: 20rpx;
			
			.notice-section {
				padding: 30rpx;
				
				.notice-card {
					display: flex;
					justify-content: space-between;
					align-items: center;
					
					.notice-title {
						font-size: 32rpx;
						color: #333333;
						flex: 1;
						margin-right: 20rpx;
					}
				}
				
				.notice-info {
					margin-top: 20rpx;
					
					.info-item {
						display: flex;
						align-items: center;
						margin-bottom: 16rpx;
						
						.label {
							font-size: 28rpx;
							color: #666666;
							width: 140rpx;
						}
						
						.value {
							font-size: 28rpx;
							color: #333333;
							flex: 1;
							
							&.warning {
								color: #FF9900;
							}
						}
					}
				}
			}
			
			.uniform-section {
				padding: 0 30rpx 30rpx;
				
				.uniform-grid {
					display: flex;
					justify-content: space-between;
					
					.uniform-card {
						width: 320rpx;
						background-color: #FFFFFF;
						overflow: hidden;
						position: relative;
						border-radius: 20rpx;
						overflow: hidden;
						.uniform-image {
							width: 100%;
							height: 320rpx;
						}
						
						.uniform-tag {
							position: absolute;
							left: 0;
							bottom: 80rpx;
							padding: 8rpx 20rpx;
							font-size: 24rpx;
							color: #FFFFFF;
							border-radius: 0 20rpx 20rpx 0;
							
							&.male {
								background-color: #409EFF;
							}
							
							&.female {
								background-color: #FF6B9A;
							}
						}
						
						.uniform-price {
							height: 80rpx;
							display: flex;
							align-items: center;
							padding: 0 20rpx;
							
							.price-symbol {
								font-size: 24rpx;
								color: #FF5555;
							}
							
							.price-value {
								font-size: 36rpx;
								color: #FF5555;
								font-weight: 500;
							}
						}
					}
				}
			}
		}
	}

	.grade-popup {
		background-color: #FFFFFF;
		border-radius: 24rpx 24rpx 0 0;
		
		.popup-header {
			display: flex;
			justify-content: center;
			align-items: center;
			height: 100rpx;
			position: relative;
			border-bottom: 1rpx solid #F0F0F0;
			
			text {
				font-size: 32rpx;
				color: #333333;
				font-weight: 500;
			}
			
			.close-btn {
				position: absolute;
				right: 30rpx;
				top: 50%;
				transform: translateY(-50%);
				padding: 10rpx;
			}
		}
		
		.grade-list {
			padding: 20rpx 0;
			max-height: 60vh;
			overflow-y: auto;
			
			.grade-item {
				display: flex;
				justify-content: space-between;
				align-items: center;
				padding: 30rpx;
				
				text {
					font-size: 30rpx;
					color: #333333;
				}
				
				&.active {
					background-color: #F8F8F8;
					
					text {
						color: #409EFF;
						font-weight: 500;
					}
				}
			}
		}
	}
</style> 