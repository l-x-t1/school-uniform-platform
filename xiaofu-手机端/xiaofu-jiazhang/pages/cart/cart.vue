<template>
	<view class="container">
		<view class="content">
			<!-- 空购物车状态 -->
			<view class="empty-cart" v-if="cartList.length === 0">
				<image src="/static/images/empty-cart.png" mode="aspectFit" class="empty-image"></image>
				<text class="empty-text">购物车还是空的</text>
				<button class="go-shopping" @click="goShopping">去选购校服</button>
			</view>
			
			<!-- 购物车列表 -->
			<view class="cart-list" v-else>
				<view class="cart-item" v-for="(item, index) in cartList" :key="index"
					@longpress="enterEditMode"
					@click="handleItemClick(index)">
					<view class="checkbox" @click.stop="toggleSelect(index)" v-if="isEdit">
						<uni-icons :type="item.selected ? 'checkbox-filled' : 'circle'" size="24" :color="item.selected ? '#2979ff' : '#DDDDDD'"></uni-icons>
					</view>
					<image :src="item.image" mode="aspectFill" class="item-image"></image>
					<view class="item-info">
						<view class="item-name">{{item.name}}</view>
						<view class="item-spec">
							<text class="spec-tag">{{item.spec}}</text>
							<text class="spec-tag">{{item.size}}</text>
						</view>
						<view class="item-bottom">
							<text class="price">¥<text class="price-num">{{item.price}}</text></text>
							<view class="quantity-control">
								<view class="minus" :class="{disabled: item.quantity <= 1}" @click.stop="updateQuantity(index, -1)">
									<uni-icons type="minus" size="20" :color="item.quantity <= 1 ? '#CCCCCC' : '#333333'"></uni-icons>
								</view>
								<input type="number" v-model="item.quantity" class="quantity-input" @click.stop />
								<view class="plus" :class="{disabled: item.quantity >= 99}" @click.stop="updateQuantity(index, 1)">
									<uni-icons type="plus" size="20" :color="item.quantity >= 99 ? '#CCCCCC' : '#333333'"></uni-icons>
								</view>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 底部结算栏 -->
		<view class="bottom-bar safe-area-inset-bottom" v-if="cartList.length > 0">
			<template v-if="isEdit">
				<view class="select-all" @click="toggleSelectAll">
					<uni-icons :type="isAllSelected ? 'checkbox-filled' : 'circle'" size="24" :color="isAllSelected ? '#2979ff' : '#DDDDDD'"></uni-icons>
					<text>全选</text>
				</view>
				<view class="edit-btns">
					<button class="btn complete" @click="exitEditMode">完成</button>
					<button class="btn delete" :disabled="selectedCount === 0" @click="handleSubmit">删除</button>
				</view>
			</template>
			<template v-else>
				<view class="price-info">
					<view class="total">
						<text>合计</text>
						<text class="amount">¥<text class="amount-num">{{totalAmount}}</text></text>
					</view>
					<text class="tip">不含运费</text>
				</view>
				<button class="submit-btn" @click="handleSubmit">
					<text>结算</text>
					<text class="count">({{selectedCount}})</text>
				</button>
			</template>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				isEdit: false,
				cartList: [{
					id: 1,
					name: '夏季校服短袖套装',
					spec: '男生款',
					size: 'L码',
					price: '128.00',
					quantity: 1,
					selected: false,
					image: '/static/nanxf.png'
				}, {
					id: 2,
					name: '夏季校服短袖套装',
					spec: '女生款',
					size: 'M码',
					price: '128.00',
					quantity: 1,
					selected: false,
					image: '/static/nvxf.png'
				}]
			}
		},
		onNavigationBarButtonTap(e) {
			if (e.index === 0) {
				this.exitEditMode();
			}
		},
		watch: {
			isEdit: {
				handler(val) {
					if (val) {
						uni.setNavigationBarRightButton({
							text: '取消',
							fontSize: '32rpx',
							width: '100rpx'
						});
					} else {
						uni.setNavigationBarRightButton({
							text: ''
						});
						// 退出编辑模式时取消所有选择
						this.cartList.forEach(item => item.selected = false);
					}
				},
				immediate: true
			}
		},
		computed: {
			isAllSelected() {
				return this.cartList.length > 0 && this.cartList.every(item => item.selected);
			},
			selectedCount() {
				return this.cartList.filter(item => item.selected).length;
			},
			totalAmount() {
				return this.cartList
					.filter(item => item.selected)
					.reduce((sum, item) => sum + parseFloat(item.price) * item.quantity, 0)
					.toFixed(2);
			}
		},
		methods: {
			enterEditMode() {
				if (!this.isEdit) {
					this.isEdit = true;
					uni.showToast({
						title: '进入编辑模式',
						icon: 'none'
					});
				}
			},
			exitEditMode() {
				this.isEdit = false;
				this.cartList.forEach(item => item.selected = false);
			},
			handleItemClick(index) {
				if (!this.isEdit) {
					// 单击进入商品详情
					uni.navigateTo({
						url: `/pages/uniform/uniform-detail?id=${this.cartList[index].id}`
					});
				}
			},
			toggleSelect(index) {
				this.cartList[index].selected = !this.cartList[index].selected;
			},
			toggleSelectAll() {
				const newStatus = !this.isAllSelected;
				this.cartList.forEach(item => item.selected = newStatus);
			},
			updateQuantity(index, delta) {
				const newQuantity = this.cartList[index].quantity + delta;
				if (newQuantity > 0 && newQuantity <= 99) {
					this.cartList[index].quantity = newQuantity;
				}
			},
			handleSubmit() {
				if (this.isEdit) {
					if (this.selectedCount === 0) {
						uni.showToast({
							title: '请选择要删除的商品',
							icon: 'none'
						});
						return;
					}
					// 删除选中商品
					this.cartList = this.cartList.filter(item => !item.selected);
					if (this.cartList.length === 0) {
						this.exitEditMode();
					}
				} else {
					// 结算
					uni.navigateTo({
						url: '/pages/order/create'
					});
				}
			},
			goShopping() {
				uni.switchTab({
					url: '/pages/uniform/uniform-purchase'
				});
			}
		}
	}
</script>

<style lang="scss">
	.container {
		background-color: #f8f8f8;
		min-height: 100vh;
		display: flex;
		flex-direction: column;
		padding-bottom: calc(120rpx + env(safe-area-inset-bottom) + 100rpx); /* 为tabbar预留空间 */
	}
	
	.content {
		flex: 1;
	}
	
	.empty-cart {
		display: flex;
		flex-direction: column;
		align-items: center;
		padding-top: 200rpx;
		
		.empty-image {
			width: 280rpx;
			height: 280rpx;
			margin-bottom: 40rpx;
		}
		
		.empty-text {
			font-size: 30rpx;
			color: #999999;
			margin-bottom: 40rpx;
		}
		
		.go-shopping {
			width: 280rpx;
			height: 88rpx;
			line-height: 88rpx;
			background: linear-gradient(90deg, #2979ff, #5c9dff);
			color: #FFFFFF;
			font-size: 30rpx;
			border-radius: 44rpx;
			box-shadow: 0 10rpx 20rpx rgba(41, 121, 255, 0.2);
		}
	}
	
	.cart-list {
		padding: 24rpx;
		
		.cart-item {
			background-color: #FFFFFF;
			border-radius: 20rpx;
			margin-bottom: 24rpx;
			padding: 32rpx;
			display: flex;
			align-items: center;
			box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
			
			.checkbox {
				margin-right: 24rpx;
				display: flex;
				align-items: center;
			}
			
			.item-image {
				width: 180rpx;
				height: 180rpx;
				border-radius: 12rpx;
				margin-right: 24rpx;
				background-color: #f5f5f5;
			}
			
			.item-info {
				flex: 1;
				
				.item-name {
					font-size: 30rpx;
					color: #333333;
					margin-bottom: 16rpx;
					font-weight: 500;
				}
				
				.item-spec {
					margin-bottom: 24rpx;
					
					.spec-tag {
						display: inline-block;
						padding: 4rpx 16rpx;
						background-color: #f5f5f5;
						color: #666666;
						font-size: 24rpx;
						border-radius: 8rpx;
						margin-right: 16rpx;
					}
				}
				
				.item-bottom {
					display: flex;
					justify-content: space-between;
					align-items: center;
					
					.price {
						font-size: 26rpx;
						color: #ff5555;
						
						.price-num {
							font-size: 36rpx;
							font-weight: 600;
						}
					}
					
					.quantity-control {
						display: flex;
						align-items: center;
						border: 2rpx solid #eeeeee;
						border-radius: 12rpx;
						overflow: hidden;
						
						.minus, .plus {
							width: 64rpx;
							height: 64rpx;
							display: flex;
							align-items: center;
							justify-content: center;
							background-color: #fafafa;
							
							&.disabled {
								background-color: #f5f5f5;
							}
						}
						
						.quantity-input {
							width: 88rpx;
							height: 64rpx;
							text-align: center;
							font-size: 28rpx;
							background-color: #ffffff;
						}
					}
				}
			}
		}
	}
	
	.bottom-bar {
		position: fixed;
		left: 0;
		right: 0;
		bottom: calc(100rpx + env(safe-area-inset-bottom)); /* 为tabbar预留空间 */
		height: 120rpx;
		background-color: #FFFFFF;
		display: flex;
		align-items: center;
		padding: 0 32rpx;
		box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.05);
		
		.select-all {
			display: flex;
			align-items: center;
			flex: 1;
			
			text {
				font-size: 28rpx;
				color: #333333;
				margin-left: 12rpx;
			}
		}
		
		.edit-btns {
			display: flex;
			gap: 20rpx;
			
			.btn {
				min-width: 160rpx;
				height: 80rpx;
				line-height: 80rpx;
				border-radius: 40rpx;
				color: #FFFFFF;
				font-size: 30rpx;
				margin: 0;
				padding: 0 40rpx;
				
				&.complete {
					background: #999999;
					box-shadow: 0 10rpx 20rpx rgba(153, 153, 153, 0.2);
				}
				
				&.delete {
					background: linear-gradient(90deg, #ff5555, #ff7777);
					box-shadow: 0 10rpx 20rpx rgba(255, 85, 85, 0.2);
					
					&[disabled] {
						opacity: 0.5;
						background: #cccccc;
						box-shadow: none;
					}
				}
			}
		}
		
		.price-info {
			flex: 1;
			
			.total {
				display: flex;
				align-items: baseline;
				
				text {
					font-size: 28rpx;
					color: #333333;
				}
				
				.amount {
					margin-left: 12rpx;
					font-size: 28rpx;
					color: #ff5555;
					
					.amount-num {
						font-size: 40rpx;
						font-weight: 600;
					}
				}
			}
			
			.tip {
				font-size: 24rpx;
				color: #999999;
				margin-top: 4rpx;
			}
		}
		
		.submit-btn {
			min-width: 220rpx;
			height: 80rpx;
			line-height: 80rpx;
			background: linear-gradient(90deg, #2979ff, #5c9dff);
			border-radius: 40rpx;
			color: #FFFFFF;
			font-size: 30rpx;
			margin: 0;
			padding: 0 40rpx;
			display: flex;
			align-items: center;
			justify-content: center;
			box-shadow: 0 10rpx 20rpx rgba(41, 121, 255, 0.2);
			
			.count {
				margin-left: 4rpx;
			}
		}
	}
</style> 