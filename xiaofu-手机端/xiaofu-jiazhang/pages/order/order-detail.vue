<template>
  <view class="container">
    <!-- 订单状态 -->
    <view class="status-section" :class="{'status-success': order.status === 'completed'}">
      <view class="status-icon">
        <uni-icons :type="getStatusIcon(order.status)" size="32" color="#fff"></uni-icons>
      </view>
      <view class="status-info">
        <text class="status-text">{{getStatusText(order.status)}}</text>
        <text class="status-desc" v-if="getStatusDescription(order.status)">{{getStatusDescription(order.status)}}</text>
      </view>
    </view>
    
    <!-- 物流信息 -->
    <view class="logistics-section card-section" v-if="shouldShowLogistics">
      <view class="card-content">
        <view class="logistics-info">
          <view class="logistics-icon">
            <uni-icons type="car" size="24" color="#3B7AFF"></uni-icons>
          </view>
          <view class="logistics-detail">
            <text class="logistics-status">{{logistics.status}}</text>
            <text class="logistics-time">{{logistics.updateTime}}</text>
          </view>
        </view>
        <uni-icons type="right" size="16" color="#CCCCCC" @click="viewLogistics"></uni-icons>
      </view>
    </view>
    
    <!-- 收货地址 -->
    <view class="address-section card-section">
      <view class="section-header">
        <text>收货地址</text>
      </view>
      <view class="card-content">
        <view class="address-content">
          <view class="contact-info">
            <text class="contact-name">{{order.address.name}}</text>
            <text class="contact-phone">{{order.address.phone}}</text>
          </view>
          <text class="address-text">{{order.address.fullAddress}}</text>
        </view>
        <view class="address-tag" v-if="order.address.tag">{{order.address.tag}}</view>
      </view>
    </view>
    
    <!-- 订单信息 -->
    <view class="order-section card-section">
      <view class="section-header">
        <text>订单信息</text>
      </view>
      <view class="card-content">
        <view class="order-detail-row">
          <text class="label">订单编号</text>
          <view class="value-with-action">
            <text class="value">{{order.orderNo}}</text>
            <text class="action-text" @click="copyOrderNo">复制</text>
          </view>
        </view>
        <view class="order-detail-row">
          <text class="label">下单时间</text>
          <text class="value">{{order.createTime}}</text>
        </view>
        <view class="order-detail-row" v-if="order.payTime">
          <text class="label">支付时间</text>
          <text class="value">{{order.payTime}}</text>
        </view>
        <view class="order-detail-row" v-if="order.shipTime">
          <text class="label">发货时间</text>
          <text class="value">{{order.shipTime}}</text>
        </view>
        <view class="order-detail-row" v-if="order.completeTime">
          <text class="label">完成时间</text>
          <text class="value">{{order.completeTime}}</text>
        </view>
        <view class="order-detail-row">
          <text class="label">订单备注</text>
          <text class="value">{{order.remark || '无'}}</text>
        </view>
      </view>
    </view>
    
    <!-- 商品信息 -->
    <view class="products-section card-section">
      <view class="section-header">
        <text>商品信息</text>
      </view>
      <view class="card-content">
        <view class="product-list">
          <view class="product-item" v-for="(product, index) in order.products" :key="index">
            <image class="product-image" :src="product.image" mode="aspectFill"></image>
            <view class="product-info">
              <text class="product-name">{{product.name}}</text>
              <text class="product-spec">{{product.size}} | {{product.color}}</text>
              <view class="product-price-row">
                <text class="product-price">¥{{product.price}}</text>
                <text class="product-quantity">x{{product.quantity}}</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 费用信息 -->
    <view class="payment-section card-section">
      <view class="section-header">
        <text>费用信息</text>
      </view>
      <view class="card-content">
        <view class="payment-detail-row">
          <text class="label">商品总额</text>
          <text class="value">¥{{order.productsAmount}}</text>
        </view>
        <view class="payment-detail-row">
          <text class="label">配送费用</text>
          <text class="value">¥{{order.shippingFee}}</text>
        </view>
        <view class="payment-detail-row" v-if="order.discount">
          <text class="label">优惠金额</text>
          <text class="value discount">-¥{{order.discount}}</text>
        </view>
        <view class="payment-detail-row total">
          <text class="label">实付金额</text>
          <text class="value total-value">¥{{order.totalAmount}}</text>
        </view>
      </view>
    </view>
    
    <!-- 底部操作区域 -->
    <view class="action-footer" v-if="hasActions">
      <button 
        class="footer-btn outline"
        v-if="canCancel(order.status)"
        @click="cancelOrder"
      >取消订单</button>
      <button
        class="footer-btn primary"
        v-if="canPay(order.status)"
        @click="payOrder"
      >立即支付</button>
      <button
        class="footer-btn outline"
        v-if="canConfirm(order.status)"
        @click="confirmReceipt"
      >确认收货</button>
      <button
        class="footer-btn outline"
        v-if="canReturn(order.status)"
        @click="applyReturn"
      >申请退换</button>
      <button
        class="footer-btn outline"
        v-if="canReorder(order.status)"
        @click="reorder"
      >再次购买</button>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      id: null,
      order: {
        id: '1',
        orderNo: 'XF20230715001',
        status: 'shipped',
        createTime: '2023-07-15 14:30:25',
        payTime: '2023-07-15 14:35:12',
        shipTime: '2023-07-17 10:22:48',
        completeTime: null,
        productsAmount: 268.00,
        shippingFee: 0.00,
        discount: 0.00,
        totalAmount: 268.00,
        remark: '男生夏季校服，请发S码',
        address: {
          name: '张三',
          phone: '138****1234',
          tag: '学校',
          fullAddress: '河北省石家庄市裕华区育才街126号石家庄市第一中学'
        },
        products: [
          {
            id: '101',
            name: '2023款夏季校服短袖套装',
            image: 'https://img.alicdn.com/imgextra/i2/2206478235626/O1CN01qJLSGY1qUiksCZB34_!!2206478235626.jpg',
            price: 268.00,
            size: 'M',
            color: '藏青色',
            quantity: 1
          }
        ]
      },
      logistics: {
        status: '已发出，正在配送中',
        updateTime: '2023-07-17 14:35'
      }
    }
  },
  computed: {
    // 是否有底部操作按钮
    hasActions() {
      return this.canCancel(this.order.status) || 
             this.canPay(this.order.status) || 
             this.canConfirm(this.order.status) || 
             this.canReturn(this.order.status) || 
             this.canReorder(this.order.status);
    },
    
    // 是否显示物流信息
    shouldShowLogistics() {
      return ['shipped', 'completed'].includes(this.order.status);
    }
  },
  onLoad(options) {
    if (options.id) {
      this.id = options.id;
      this.fetchOrderDetail();
    }
  },
  methods: {
    // 获取订单详情数据
    fetchOrderDetail() {
      // 模拟API请求，实际开发中应替换为真实API调用
      console.log('获取订单详情，ID:', this.id);
    },
    
    // 获取订单状态图标
    getStatusIcon(status) {
      const iconMap = {
        'unpaid': 'wallet',
        'unshipped': 'box',
        'shipped': 'car',
        'completed': 'checkmarkempty',
        'cancelled': 'closeempty',
        'refunding': 'refresh',
        'refunded': 'refreshempty'
      };
      return iconMap[status] || 'help';
    },
    
    // 获取订单状态文本
    getStatusText(status) {
      const textMap = {
        'unpaid': '待付款',
        'unshipped': '待发货',
        'shipped': '运输中',
        'completed': '已完成',
        'cancelled': '已取消',
        'refunding': '退款中',
        'refunded': '已退款'
      };
      return textMap[status] || '未知状态';
    },
    
    // 获取订单状态描述
    getStatusDescription(status) {
      const descMap = {
        'unpaid': '请在24小时内完成支付',
        'unshipped': '商家正在处理您的订单',
        'shipped': '校服已发出，请注意查收',
        'completed': '感谢您的购买，欢迎再次选购',
        'cancelled': '订单已取消',
        'refunding': '退款申请处理中',
        'refunded': '退款金额已原路返回'
      };
      return descMap[status] || '';
    },
    
    // 获取当前时间
    getCurrentTime() {
      const now = new Date();
      const year = now.getFullYear();
      const month = String(now.getMonth() + 1).padStart(2, '0');
      const day = String(now.getDate()).padStart(2, '0');
      const hour = String(now.getHours()).padStart(2, '0');
      const minute = String(now.getMinutes()).padStart(2, '0');
      const second = String(now.getSeconds()).padStart(2, '0');
      
      return `${year}-${month}-${day} ${hour}:${minute}:${second}`;
    },
    
    // 复制订单号
    copyOrderNo() {
      uni.setClipboardData({
        data: this.order.orderNo,
        success: () => {
          uni.showToast({
            title: '订单号已复制',
            icon: 'success'
          });
        }
      });
    },
    
    // 查看物流
    viewLogistics() {
      uni.navigateTo({
        url: `/pages/order/logistics?id=${this.id}`
      });
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
    },
    
    // 是否可以再次购买
    canReorder(status) {
      return ['completed', 'cancelled'].includes(status);
    },
    
    // 取消订单
    cancelOrder() {
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
              // 更新订单状态
              this.order.status = 'cancelled';
            }, 1000);
          }
        }
      });
    },
    
    // 支付订单
    payOrder() {
      uni.navigateTo({
        url: `/pages/payment/payment?id=${this.id}`
      });
    },
    
    // 确认收货
    confirmReceipt() {
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
              // 更新订单状态
              this.order.status = 'completed';
              this.order.completeTime = this.getCurrentTime();
            }, 1000);
          }
        }
      });
    },
    
    // 申请退换
    applyReturn() {
      uni.navigateTo({
        url: `/pages/order/return-apply?id=${this.id}`
      });
    },
    
    // 再次购买
    reorder() {
      uni.showLoading({ title: '加载中' });
      setTimeout(() => {
        uni.hideLoading();
        uni.navigateTo({
          url: `/pages/uniform/uniform-detail?id=${this.order.products[0].id}`
        });
      }, 500);
    }
  }
}
</script>

<style lang="scss">
.container {
  padding-bottom: 120rpx; // 为底部操作区预留空间
}

// 订单状态区
.status-section {
  display: flex;
  align-items: center;
  padding: 40rpx 30rpx;
  background: linear-gradient(to right, #3B7AFF, #6BA1FF);
  color: #fff;
  
  &.status-success {
    background: linear-gradient(to right, #09BB07, #42C642);
  }
  
  .status-icon {
    width: 80rpx;
    height: 80rpx;
    border-radius: 40rpx;
    background-color: rgba(255, 255, 255, 0.2);
    display: flex;
    justify-content: center;
    align-items: center;
    margin-right: 20rpx;
  }
  
  .status-info {
    flex: 1;
    
    .status-text {
      font-size: 32rpx;
      font-weight: 500;
      margin-bottom: 8rpx;
      display: block;
    }
    
    .status-desc {
      font-size: 24rpx;
      opacity: 0.8;
    }
  }
}

// 卡片通用样式
.card-section {
  margin: 20rpx 30rpx;
  background-color: #fff;
  border-radius: 12rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  
  .section-header {
    padding: 20rpx 30rpx;
    border-bottom: 1rpx solid #f5f5f5;
    font-size: 28rpx;
    font-weight: 500;
    color: #333;
  }
  
  .card-content {
    padding: 30rpx;
  }
}

// 物流信息
.logistics-section {
  .card-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20rpx 30rpx;
  }
  
  .logistics-info {
    display: flex;
    align-items: center;
    
    .logistics-icon {
      width: 60rpx;
      height: 60rpx;
      background-color: rgba(59, 122, 255, 0.1);
      border-radius: 30rpx;
      display: flex;
      justify-content: center;
      align-items: center;
      margin-right: 20rpx;
    }
    
    .logistics-detail {
      .logistics-status {
        font-size: 28rpx;
        color: #333;
        margin-bottom: 8rpx;
        display: block;
      }
      
      .logistics-time {
        font-size: 24rpx;
        color: #999;
      }
    }
  }
}

// 收货地址
.address-section {
  .address-content {
    flex: 1;
    
    .contact-info {
      margin-bottom: 10rpx;
      
      .contact-name {
        font-size: 28rpx;
        font-weight: 500;
        color: #333;
        margin-right: 20rpx;
      }
      
      .contact-phone {
        font-size: 28rpx;
        color: #666;
      }
    }
    
    .address-text {
      font-size: 26rpx;
      color: #666;
      line-height: 1.5;
    }
  }
  
  .address-tag {
    display: inline-block;
    padding: 2rpx 12rpx;
    background-color: rgba(59, 122, 255, 0.1);
    color: #3B7AFF;
    font-size: 24rpx;
    border-radius: 4rpx;
    margin-top: 10rpx;
  }
}

// 订单信息
.order-section {
  .order-detail-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16rpx;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .label {
      font-size: 26rpx;
      color: #666;
    }
    
    .value {
      font-size: 26rpx;
      color: #333;
    }
    
    .value-with-action {
      display: flex;
      align-items: center;
      
      .action-text {
        font-size: 24rpx;
        color: #3B7AFF;
        margin-left: 20rpx;
      }
    }
  }
}

// 商品信息
.products-section {
  .product-list {
    .product-item {
      display: flex;
      margin-bottom: 30rpx;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      .product-image {
        width: 160rpx;
        height: 160rpx;
        border-radius: 8rpx;
        margin-right: 20rpx;
      }
      
      .product-info {
        flex: 1;
        
        .product-name {
          font-size: 28rpx;
          color: #333;
          margin-bottom: 10rpx;
          line-height: 1.4;
          display: -webkit-box;
          -webkit-box-orient: vertical;
          -webkit-line-clamp: 2;
          overflow: hidden;
        }
        
        .product-spec {
          font-size: 24rpx;
          color: #999;
          margin-bottom: 20rpx;
        }
        
        .product-price-row {
          display: flex;
          justify-content: space-between;
          align-items: center;
          
          .product-price {
            font-size: 28rpx;
            color: #333;
            font-weight: 500;
          }
          
          .product-quantity {
            font-size: 24rpx;
            color: #999;
          }
        }
      }
    }
  }
}

// 费用信息
.payment-section {
  .payment-detail-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16rpx;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .label {
      font-size: 26rpx;
      color: #666;
    }
    
    .value {
      font-size: 26rpx;
      color: #333;
      
      &.discount {
        color: #f44;
      }
    }
    
    &.total {
      margin-top: 20rpx;
      padding-top: 20rpx;
      border-top: 1rpx dashed #eee;
      
      .label, .value {
        font-weight: 500;
      }
      
      .total-value {
        color: #f44;
        font-size: 32rpx;
      }
    }
  }
}

// 底部操作区
.action-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  background-color: #fff;
  padding: 20rpx 30rpx;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
  
  .footer-btn {
    margin: 0;
    margin-left: 20rpx;
    padding: 0 30rpx;
    font-size: 28rpx;
    height: 80rpx;
    line-height: 80rpx;
    border-radius: 40rpx;
    
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
</style> 