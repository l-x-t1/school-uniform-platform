<template>
  <view class="container">
    <!-- 状态选项卡 -->
    <scroll-view scroll-x class="status-tabs" :show-scrollbar="false">
      <view 
        class="tab-item" 
        v-for="(item, index) in orderStatus" 
        :key="index"
        :class="{'active': currentStatus === item.status}"
        @click="switchStatus(item.status)"
      >
        <text class="tab-text">{{item.name}}</text>
      </view>
    </scroll-view>
    
    <!-- 订单列表 -->
    <scroll-view 
      scroll-y 
      class="order-list"
      @scrolltolower="loadMore"
      refresher-enabled
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onRefresh"
    >
      <!-- 订单项 -->
      <view 
        class="order-item" 
        v-for="(order, index) in orderList" 
        :key="index"
        @click="goToOrderDetail(order.id)"
      >
        <view class="order-header">
          <view class="order-number">
            <text>订单号：{{order.orderNo}}</text>
          </view>
          <view class="order-status">
            <text :class="getStatusClass(order.status)">{{getStatusText(order.status)}}</text>
          </view>
        </view>
        
        <view class="order-content">
          <view class="product-list">
            <view class="product-item" v-for="(product, pIndex) in order.products" :key="pIndex">
              <image class="product-image" :src="product.image" mode="aspectFill"></image>
              <view class="product-info">
                <text class="product-name">{{product.name}}</text>
                <text class="product-spec">{{product.size}} | {{product.color}}</text>
              </view>
              <view class="product-price">
                <text class="price">¥{{product.price}}</text>
                <text class="quantity">x{{product.quantity}}</text>
              </view>
            </view>
          </view>
        </view>
        
        <view class="order-footer">
          <view class="order-total">
            <text>共{{getTotalProducts(order.products)}}件商品</text>
            <view class="total-price">
              <text>合计：</text>
              <text class="price">¥{{order.totalAmount}}</text>
            </view>
          </view>
          <view class="order-actions">
            <button 
              class="action-btn outline"
              v-if="canCancel(order.status)"
              @click.stop="cancelOrder(order.id)"
            >取消订单</button>
            <button
              class="action-btn primary"
              v-if="canPay(order.status)"
              @click.stop="payOrder(order.id)"
            >立即支付</button>
            <button
              class="action-btn outline"
              v-if="canConfirm(order.status)"
              @click.stop="confirmReceipt(order.id)"
            >确认收货</button>
            <button
              class="action-btn outline"
              v-if="canReturn(order.status)"
              @click.stop="applyReturn(order.id)"
            >申请退换</button>
          </view>
        </view>
      </view>
      
      <!-- 加载状态 -->
      <view class="loading-more" v-if="isLoadingMore">
        <uni-icons type="spinner-cycle" size="20" color="#999"></uni-icons>
        <text class="loading-text">加载中...</text>
      </view>
      
      <!-- 无数据提示 -->
      <view class="empty-state" v-if="orderList.length === 0 && !isLoading">
        <image class="empty-image" src="/static/images/empty-order.png" mode="aspectFit"></image>
        <text class="empty-text">暂无相关订单</text>
        <button class="go-shop-btn" @click="goToShop">去购买校服</button>
      </view>
    </scroll-view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      currentStatus: 'unpaid', // 当前选中的状态
      isLoading: false, // 是否正在加载
      isLoadingMore: false, // 是否正在加载更多
      isRefreshing: false, // 是否正在刷新
      page: 1, // 当前页码
      orderList: [], // 订单列表
      orderStatus: [
        { status: 'unpaid', name: '待付款' },
        { status: 'unshipped', name: '待发货' },
        { status: 'shipped', name: '已发货' },
        { status: 'completed', name: '已完成' },
        { status: 'cancelled', name: '已取消' }
      ]
    }
  },
  onLoad() {
    this.loadOrders();
  },
  methods: {
    // 切换订单状态
    switchStatus(status) {
      if (this.currentStatus === status) return;
      this.currentStatus = status;
      this.page = 1;
      this.orderList = [];
      this.loadOrders();
    },
    
    // 加载订单数据
    loadOrders() {
      this.isLoading = true;
      // 模拟API请求
      setTimeout(() => {
        // 模拟数据，实际应从API获取
        const mockOrders = this.getMockOrders();
        this.orderList = this.filterOrdersByStatus(mockOrders, this.currentStatus);
        this.isLoading = false;
      }, 800);
    },
    
    // 加载更多订单
    loadMore() {
      if (this.isLoadingMore) return;
      this.isLoadingMore = true;
      
      // 模拟加载更多
      setTimeout(() => {
        this.page += 1;
        // 模拟没有更多数据
        if (this.page > 2) {
          uni.showToast({
            title: '没有更多订单了',
            icon: 'none'
          });
          this.isLoadingMore = false;
          return;
        }
        
        // 模拟加载更多数据
        const moreOrders = this.getMockOrders(this.page);
        const filteredOrders = this.filterOrdersByStatus(moreOrders, this.currentStatus);
        this.orderList = [...this.orderList, ...filteredOrders];
        this.isLoadingMore = false;
      }, 1000);
    },
    
    // 下拉刷新
    onRefresh() {
      this.isRefreshing = true;
      this.page = 1;
      
      // 模拟刷新
      setTimeout(() => {
        this.orderList = [];
        this.loadOrders();
        this.isRefreshing = false;
      }, 1000);
    },
    
    // 跳转到订单详情
    goToOrderDetail(orderId) {
      uni.navigateTo({
        url: `/pages/order/order-detail?id=${orderId}`
      });
    },
    
    // 跳转到商城
    goToShop() {
      uni.switchTab({
        url: '/pages/uniform/uniform-list'
      });
    },
    
    // 取消订单
    cancelOrder(orderId) {
      uni.showModal({
        title: '确认取消',
        content: '确定要取消此订单吗？',
        success: (res) => {
          if (res.confirm) {
            // 模拟取消订单
            uni.showLoading({ title: '处理中' });
            setTimeout(() => {
              uni.hideLoading();
              uni.showToast({
                title: '订单已取消',
                icon: 'success'
              });
              // 重新加载订单数据
              this.loadOrders();
            }, 1000);
          }
        }
      });
    },
    
    // 支付订单
    payOrder(orderId) {
      uni.navigateTo({
        url: `/pages/payment/payment?id=${orderId}`
      });
    },
    
    // 确认收货
    confirmReceipt(orderId) {
      uni.showModal({
        title: '确认收货',
        content: '确认已收到商品吗？',
        success: (res) => {
          if (res.confirm) {
            // 模拟确认收货
            uni.showLoading({ title: '处理中' });
            setTimeout(() => {
              uni.hideLoading();
              uni.showToast({
                title: '已确认收货',
                icon: 'success'
              });
              // 重新加载订单数据
              this.loadOrders();
            }, 1000);
          }
        }
      });
    },
    
    // 申请退换
    applyReturn(orderId) {
      uni.navigateTo({
        url: `/pages/order/return-apply?id=${orderId}`
      });
    },
    
    // 获取模拟订单数据
    getMockOrders(page = 1) {
      const baseOrders = [
        {
          id: '100001',
          orderNo: 'SJZ20230715001',
          status: 'unpaid',
          createTime: '2023-07-15 10:30:45',
          totalAmount: '268.00',
          products: [
            {
              id: '1',
              name: '2023秋季校服套装',
              image: '/static/qjxf.png',
              price: '268.00',
              quantity: 1,
              size: 'L',
              color: '深蓝色'
            }
          ]
        },
        {
          id: '100002',
          orderNo: 'SJZ20230713002',
          status: 'unshipped',
          createTime: '2023-07-13 15:22:36',
          totalAmount: '369.00',
          products: [
            {
              id: '2',
              name: '2023秋季校服外套',
              image: '/static/qjxf.png',
              price: '369.00',
              quantity: 1,
              size: 'XL',
              color: '黑色'
            }
          ]
        },
        {
          id: '100003',
          orderNo: 'SJZ20230710003',
          status: 'shipped',
          createTime: '2023-07-10 08:15:20',
          totalAmount: '536.00',
          products: [
            {
              id: '3',
              name: '2023秋季校服短袖套装',
              image: '/static/qjxf.png',
              price: '268.00',
              quantity: 2,
              size: 'M',
              color: '白色'
            }
          ]
        },
        {
          id: '100004',
          orderNo: 'SJZ20230705004',
          status: 'completed',
          createTime: '2023-07-05 16:42:18',
          totalAmount: '536.00',
          products: [
            {
              id: '1',
              name: '2023秋季校服套装',
              image: '/static/qjxf.png',
              price: '268.00',
              quantity: 2,
              size: 'M',
              color: '深蓝色'
            }
          ]
        },
        {
          id: '100005',
          orderNo: 'SJZ20230701005',
          status: 'cancelled',
          createTime: '2023-07-01 09:05:55',
          totalAmount: '268.00',
          products: [
            {
              id: '2',
              name: '2023秋季校服外套',
              image: '/static/qjxf.png',
              price: '268.00',
              quantity: 1,
              size: 'L',
              color: '黑色'
            }
          ]
        }
      ];
      
      // 第二页返回不同数据
      if (page === 2) {
        return [
          {
            id: '100006',
            orderNo: 'SJZ20230628006',
            status: 'completed',
            createTime: '2023-06-28 14:30:22',
            totalAmount: '369.00',
            products: [
              {
                id: '3',
                name: '2023夏季校服短袖套装',
                image: 'https://cdn.uviewui.com/uview/common/logo.png',
                price: '369.00',
                quantity: 1,
                size: 'S',
                color: '白色'
              }
            ]
          },
          {
            id: '100007',
            orderNo: 'SJZ20230625007',
            status: 'completed',
            createTime: '2023-06-25 11:18:40',
            totalAmount: '536.00',
            products: [
              {
                id: '1',
                name: '2023秋季校服套装',
                image: 'https://cdn.uviewui.com/uview/common/logo.png',
                price: '268.00',
                quantity: 2,
                size: 'L',
                color: '深蓝色'
              }
            ]
          }
        ];
      }
      
      return baseOrders;
    },
    
    // 根据状态筛选订单
    filterOrdersByStatus(orders, status) {
      if (status === 'all') {
        return orders;
      }
      return orders.filter(order => order.status === status);
    },
    
    // 获取订单状态文本
    getStatusText(status) {
      const statusMap = {
        'unpaid': '待付款',
        'unshipped': '待发货',
        'shipped': '已发货',
        'completed': '已完成',
        'cancelled': '已取消'
      };
      return statusMap[status] || '未知状态';
    },
    
    // 获取订单状态样式
    getStatusClass(status) {
      const statusClassMap = {
        'unpaid': 'status-warning',
        'unshipped': 'status-primary',
        'shipped': 'status-info',
        'completed': 'status-success',
        'cancelled': 'status-default'
      };
      return statusClassMap[status] || '';
    },
    
    // 计算订单总商品数量
    getTotalProducts(products) {
      return products.reduce((total, product) => total + product.quantity, 0);
    },
    
    // 是否可以取消订单
    canCancel(status) {
      return ['unpaid', 'unshipped'].includes(status);
    },
    
    // 是否可以支付
    canPay(status) {
      return status === 'unpaid';
    },
    
    // 是否可以确认收货
    canConfirm(status) {
      return status === 'shipped';
    },
    
    // 是否可以申请退换
    canReturn(status) {
      return ['unshipped', 'shipped', 'completed'].includes(status);
    }
  }
}
</script>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  background-color: #f5f5f5;
  display: flex;
  flex-direction: column;
}

// 状态选项卡
.status-tabs {
  background-color: #fff;
  white-space: nowrap;
  height: 80rpx;
  border-bottom: 1rpx solid #f0f0f0;
  
  .tab-item {
    display: inline-block;
    padding: 0 30rpx;
    height: 80rpx;
    line-height: 80rpx;
    position: relative;
    
    &.active {
      .tab-text {
        color: #3B7AFF;
        font-weight: 500;
      }
      
      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translateX(-50%);
        width: 40rpx;
        height: 4rpx;
        background-color: #3B7AFF;
        border-radius: 2rpx;
      }
    }
    
    .tab-text {
      font-size: 28rpx;
      color: #666;
    }
  }
}

// 订单列表
.order-list {
  flex: 1;
  padding: 20rpx;
  width: 100vw;
  box-sizing: border-box;
  .order-item {
    background-color: #fff;
    border-radius: 12rpx;
    margin-bottom: 20rpx;
    overflow: hidden;
    
    .order-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20rpx 30rpx;
      border-bottom: 1rpx solid #f5f5f5;
      
      .order-number {
        font-size: 26rpx;
        color: #666;
      }
      
      .order-status {
        font-size: 26rpx;
        
        .status-warning {
          color: #ff9800;
        }
        
        .status-primary {
          color: #3B7AFF;
        }
        
        .status-info {
          color: #2196f3;
        }
        
        .status-success {
          color: #4caf50;
        }
        
        .status-default {
          color: #999;
        }
      }
    }
    
    .order-content {
      padding: 0 30rpx;
      
      .product-list {
        .product-item {
          display: flex;
          align-items: center;
          padding: 20rpx 0;
          border-bottom: 1rpx solid #f5f5f5;
          
          &:last-child {
            border-bottom: none;
          }
          
          .product-image {
            width: 120rpx;
            height: 120rpx;
            border-radius: 8rpx;
            margin-right: 20rpx;
          }
          
          .product-info {
            flex: 1;
            overflow: hidden;
            
            .product-name {
              font-size: 28rpx;
              color: #333;
              margin-bottom: 10rpx;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }
            
            .product-spec {
              font-size: 24rpx;
              color: #999;
            }
          }
          
          .product-price {
            display: flex;
            flex-direction: column;
            align-items: flex-end;
            
            .price {
              font-size: 28rpx;
              color: #333;
              font-weight: 500;
            }
            
            .quantity {
              font-size: 24rpx;
              color: #999;
              margin-top: 8rpx;
            }
          }
        }
      }
    }
    
    .order-footer {
      padding: 20rpx 30rpx;
      border-top: 1rpx solid #f0f0f0;
      
      .order-total {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20rpx;
        font-size: 26rpx;
        color: #666;
        
        .total-price {
          display: flex;
          align-items: center;
          
          .price {
            font-size: 30rpx;
            font-weight: 500;
            color: #f44;
          }
        }
      }
      
      .order-actions {
        display: flex;
        justify-content: flex-end;
        
        .action-btn {
          margin: 0;
          margin-left: 20rpx;
          padding: 0 30rpx;
          font-size: 26rpx;
          height: 60rpx;
          line-height: 60rpx;
          border-radius: 30rpx;
          
          &.outline {
            background-color: #fff;
            color: #666;
            border: 1rpx solid #ddd;
            
            &::after {
              border: none;
            }
          }
          
          &.primary {
            background-color: #3B7AFF;
            color: #fff;
            
            &::after {
              border: none;
            }
          }
        }
      }
    }
  }
  
  // 加载更多
  .loading-more {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 30rpx 0;
    
    .loading-text {
      font-size: 24rpx;
      color: #999;
      margin-left: 10rpx;
    }
  }
  
  // 空状态
  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 100rpx 0;
    
    .empty-image {
      width: 200rpx;
      height: 200rpx;
      margin-bottom: 20rpx;
    }
    
    .empty-text {
      font-size: 28rpx;
      color: #999;
      margin-bottom: 30rpx;
    }
    
    .go-shop-btn {
      background-color: #3B7AFF;
      color: #fff;
      font-size: 28rpx;
      padding: 0 40rpx;
      height: 80rpx;
      line-height: 80rpx;
      border-radius: 40rpx;
      
      &::after {
        border: none;
      }
    }
  }
}
</style> 