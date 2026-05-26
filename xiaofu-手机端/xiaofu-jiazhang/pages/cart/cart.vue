<template>
	<view class="container">
		<view class="content">
			<!-- 空购物车状态 -->
			<view class="empty-cart" v-if="cartList.length === 0">
				<image src="/static/uni.png" mode="aspectFit" class="empty-image"></image>
				<text class="empty-text">购物车还是空的</text>
				<button class="go-shopping" @click="goShopping">去选购校服</button>
			</view>
			
			<!-- 购物车列表 -->
			<view class="cart-list" v-else>
				<view class="cart-item" v-for="(item, index) in cartList" :key="index"
					@longpress="enterEditMode"
					@click="handleItemClick(index)">
					<view class="checkbox" @click.stop="toggleSelect(index)">
						<uni-icons :type="item.selected ? 'checkbox-filled' : 'circle'" size="24" :color="item.selected ? '#2979ff' : '#DDDDDD'"></uni-icons>
					</view>
					<image :src="item.image" mode="aspectFill" class="item-image"></image>
					<view class="item-info">
						<view class="item-name">{{item.name}}</view>
						<view class="item-remove" @click.stop="removeItem(index)">
							<uni-icons type="trash" size="16" color="#ff6b6b"></uni-icons>
							<text>取消</text>
						</view>
						<view class="item-spec">
							<text class="spec-tag">{{item.orderTypeText}}</text>
							<text class="spec-tag" v-if="item.spec">{{item.spec}}</text>
							<text class="spec-tag" v-if="item.size">{{item.size}}</text>
						</view>
						<view class="item-meta">
							<text class="meta-text" v-if="item.studentName">{{item.studentName}}</text>
							<text class="meta-text" v-if="item.goodsCode">{{item.goodsCode}}</text>
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
	import config from '@/config'
	import { listCart, updateCart, deleteCart } from '@/api/cart'

	export default {
		data() {
			return {
				isEdit: false,
				cartList: []
			}
		},
		onShow() {
			this.loadCart();
		},
		onNavigationBarButtonTap(e) {
			if (e.index === 0) {
				this.exitEditMode();
			}
		},
		watch: {
			isEdit: {
				handler(val) {
					if (typeof uni.setNavigationBarRightButton !== 'function') {
						return;
					}
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
					.reduce((sum, item) => sum + parseFloat(item.price || 0) * Number(item.quantity || 1), 0)
					.toFixed(2);
			}
		},
		methods: {
			normalizeCartItem(item) {
				return {
					id: item.id || item.cartId || item.uniformId,
					userId: item.userId,
					studentId: item.studentId,
					uniformId: item.uniformId || item.goodsId || item.productId || item.id,
					goodsId: item.goodsId || item.uniformId || item.id,
					skuId: item.skuId || item.sizeId,
					sizeId: item.sizeId || item.skuId,
					name: item.name || item.goodsName || item.uniformName || item.productName || '校服',
					spec: item.spec || item.color || '',
					size: item.size || item.sizeName || '-',
					sizeName: item.sizeName || item.size || '-',
					price: item.price || item.salePrice || item.unitPrice || '0.00',
					unitPrice: item.unitPrice || item.price || '0.00',
					quantity: Number(item.quantity || item.buyNum || item.num || 1),
					buyNum: Number(item.buyNum || item.quantity || item.num || 1),
					selected: item.selected !== undefined ? item.selected : String(item.checkedFlag || '1') === '1',
					checkedFlag: item.checkedFlag || (item.selected === false ? '0' : '1'),
					orderType: String(item.orderType || item.saleType || '1'),
					orderTypeText: String(item.orderType || item.saleType || '1') === '2' ? '零售购买' : '统购购买',
					noticeId: item.noticeId || null,
					image: item.image || item.mainImage || item.productImage || item.coverImage || '/static/qjxf.png',
					mainImage: item.mainImage || item.image || item.productImage || item.coverImage || '/static/qjxf.png',
					studentName: item.studentName || '',
					goodsCode: item.goodsCode || ''
				};
			},
			loadCart() {
				listCart()
					.then((res) => {
						const rows = res.rows || res.data || res.list || [];
						if (rows.length) {
							this.cartList = rows.map(this.normalizeCartItem);
							this.saveLocalCart();
							return;
						}
						const localCart = uni.getStorageSync('cartList') || [];
						this.cartList = localCart.length ? localCart.map(this.normalizeCartItem) : [];
					})
					.catch((error) => {
						if (config.enableMockFallback) {
							const localCart = uni.getStorageSync('cartList') || [];
							this.cartList = localCart.length ? localCart.map(this.normalizeCartItem) : [];
							return;
						}
						uni.showToast({
							title: error.message || '购物车加载失败',
							icon: 'none'
						});
					});
			},
			saveLocalCart() {
				uni.setStorageSync('cartList', this.cartList);
			},
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
				this.cartList.forEach(item => {
					item.selected = true;
					item.checkedFlag = '1';
				});
				Promise.allSettled(this.cartList.map((item) => updateCart({
					id: item.id,
					selected: true,
					checkedFlag: '1'
				}))).catch(() => {
					if (config.enableMockFallback) this.saveLocalCart();
				});
				this.saveLocalCart();
			},
			handleItemClick(index) {
				if (!this.isEdit) {
					// 单击进入商品详情
					uni.navigateTo({
						url: `/pages/uniform/uniform-detail?id=${this.cartList[index].uniformId || this.cartList[index].id}`
					});
				}
			},
			toggleSelect(index) {
				this.cartList[index].selected = !this.cartList[index].selected;
				this.cartList[index].checkedFlag = this.cartList[index].selected ? '1' : '0';
				const item = this.cartList[index];
				updateCart({
					id: item.id,
					selected: item.selected,
					checkedFlag: item.checkedFlag
				}).catch(() => {
					if (config.enableMockFallback) this.saveLocalCart();
				});
				this.saveLocalCart();
			},
			toggleSelectAll() {
				const newStatus = !this.isAllSelected;
				this.cartList.forEach(item => {
					item.selected = newStatus;
					item.checkedFlag = newStatus ? '1' : '0';
				});
				Promise.allSettled(this.cartList.map((item) => updateCart({
					id: item.id,
					selected: item.selected,
					checkedFlag: item.checkedFlag
				}))).catch(() => {
					if (config.enableMockFallback) this.saveLocalCart();
				});
				this.saveLocalCart();
			},
			updateQuantity(index, delta) {
				const newQuantity = this.cartList[index].quantity + delta;
				if (newQuantity > 0 && newQuantity <= 99) {
					this.cartList[index].quantity = newQuantity;
					this.cartList[index].buyNum = newQuantity;
					const item = this.cartList[index];
					updateCart({
						id: item.id,
						quantity: item.quantity,
						buyNum: item.quantity,
						uniformId: item.uniformId,
						goodsId: item.goodsId,
						skuId: item.skuId,
						sizeId: item.sizeId,
						name: item.name,
						sizeName: item.sizeName,
						image: item.image,
						price: item.price,
						orderType: item.orderType,
						selected: item.selected,
						checkedFlag: item.checkedFlag,
						studentId: item.studentId,
						noticeId: item.noticeId
					}).catch(() => {
						if (config.enableMockFallback) this.saveLocalCart();
					});
					this.saveLocalCart();
				}
			},
			removeItem(index) {
				const item = this.cartList[index];
				uni.showModal({
					title: '移除商品',
					content: `确定将“${item.name}”从购物车中移除吗？`,
					success: ({ confirm }) => {
						if (!confirm) return;
						this.cartList.splice(index, 1);
						this.saveLocalCart();
						deleteCart(item.id).catch(() => {
							if (config.enableMockFallback) this.saveLocalCart();
						});
						uni.showToast({
							title: '已移除',
							icon: 'success'
						});
						if (this.cartList.length === 0) {
							this.isEdit = false;
						}
					}
				});
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
					const ids = this.cartList.filter(item => item.selected).map(item => item.id).join(',');
					this.cartList = this.cartList.filter(item => !item.selected);
					this.saveLocalCart();
					deleteCart(ids).catch(() => {
						if (config.enableMockFallback) this.saveLocalCart();
					});
					if (this.cartList.length === 0) {
						this.exitEditMode();
					}
				} else {
					if (this.selectedCount === 0) {
						uni.showToast({
							title: '请选择要结算的商品',
							icon: 'none'
						});
						return;
					}
					uni.setStorageSync('confirmOrderItems', this.cartList.filter(item => item.selected).map((item) => ({
						...item,
						price: item.price,
						unitPrice: item.unitPrice || item.price,
						quantity: Number(item.quantity || 1),
						buyNum: Number(item.quantity || 1),
						orderType: item.orderType || '1',
						saleType: item.orderType || '1'
					})));
					uni.navigateTo({
						url: '/pages/order/order-confirm?source=cart'
					});
				}
			},
			goShopping() {
				uni.navigateTo({
					url: '/pages/uniform/uniform-list'
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
					margin-bottom: 12rpx;
					font-weight: 500;
					padding-right: 120rpx;
				}

				.item-remove {
					display: inline-flex;
					align-items: center;
					padding: 6rpx 16rpx;
					border-radius: 999rpx;
					background: rgba(255, 107, 107, 0.08);
					margin-bottom: 16rpx;

					text {
						font-size: 22rpx;
						color: #ff6b6b;
						margin-left: 6rpx;
					}
				}
				
				.item-spec {
					margin-bottom: 12rpx;
					
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

				.item-meta {
					display: flex;
					flex-wrap: wrap;
					gap: 12rpx;
					margin-bottom: 20rpx;

					.meta-text {
						font-size: 22rpx;
						color: #97a0af;
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
