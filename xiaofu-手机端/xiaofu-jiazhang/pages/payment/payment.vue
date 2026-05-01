<template>
  <view class="container">
    <!-- 订单信息 -->
    <view class="order-info">
      <view class="title-bar">
        <text class="title">订单信息</text>
      </view>
      <view class="info-content">
        <view class="order-number">
          <text class="label">订单编号</text>
          <text class="value">{{orderInfo.orderNo}}</text>
        </view>
        <view class="order-school">
          <text class="label">所属学校</text>
          <text class="value">{{orderInfo.school}}</text>
        </view>
        <view class="order-student">
          <text class="label">学生信息</text>
          <text class="value">{{orderInfo.studentName}} {{orderInfo.studentClass}}</text>
        </view>
      </view>
    </view>
    
    <!-- 商品清单 -->
    <view class="goods-list">
      <view class="title-bar">
        <text class="title">商品清单</text>
      </view>
      <view 
        class="goods-item" 
        v-for="(item, index) in orderInfo.items" 
        :key="index"
      >
        <image class="goods-image" :src="item.image" mode="aspectFill"></image>
        <view class="goods-info">
          <text class="goods-name">{{item.name}}</text>
          <view class="goods-spec">
            <text class="spec-text">{{item.size}}</text>
            <text class="spec-text">{{item.color}}</text>
          </view>
          <view class="goods-price-row">
            <text class="goods-price">¥{{item.price}}</text>
            <text class="goods-count">x{{item.count}}</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 支付方式 -->
    <view class="payment-methods">
      <view class="title-bar">
        <text class="title">支付方式</text>
      </view>
      <view class="methods-list">
        <view 
          class="method-item" 
          v-for="(method, index) in paymentMethods" 
          :key="index"
          :class="{'selected': selectedMethod === method.id}"
          @click="selectPaymentMethod(method.id)"
        >
          <view class="method-icon" :class="method.id">
            <uni-icons :type="method.icon" size="24" :color="method.color"></uni-icons>
          </view>
          <view class="method-info">
            <text class="method-name">{{method.name}}</text>
            <text class="method-desc">{{method.desc}}</text>
          </view>
          <uni-icons 
            type="circle" 
            size="24" 
            color="#ccc" 
            v-if="selectedMethod !== method.id"
          ></uni-icons>
          <uni-icons 
            type="checkbox-filled" 
            size="24" 
            color="#3B7AFF" 
            v-else
          ></uni-icons>
        </view>
      </view>
    </view>
    
    <!-- 价格明细 -->
    <view class="price-detail">
      <view class="title-bar">
        <text class="title">价格明细</text>
      </view>
      <view class="price-item">
        <text class="label">商品总额</text>
        <text class="value">¥{{orderInfo.totalGoodsPrice}}</text>
      </view>
      <view class="price-item">
        <text class="label">运费</text>
        <text class="value">¥{{orderInfo.shippingFee}}</text>
      </view>
      <view class="price-item discount" v-if="orderInfo.discount > 0">
        <text class="label">优惠</text>
        <text class="value">-¥{{orderInfo.discount}}</text>
      </view>
      <view class="price-total">
        <text class="label">应付金额</text>
        <text class="value">¥{{orderInfo.totalAmount}}</text>
      </view>
    </view>
    
    <!-- 底部支付栏 -->
    <view class="payment-bar">
      <view class="total-amount">
        <text class="label">实付款：</text>
        <text class="amount">¥{{orderInfo.totalAmount}}</text>
      </view>
      <button class="pay-btn" @click="confirmPay">确认支付</button>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      orderId: '',
      selectedMethod: 'wxpay',
      orderInfo: {
        orderNo: 'SJZ20230912001',
        school: '石家庄市第一中学',
        studentName: '张三',
        studentClass: '高一(3)班',
        totalGoodsPrice: 338.00,
        shippingFee: 0.00,
        discount: 20.00,
        totalAmount: 318.00,
        items: [
          {
            id: '1',
            name: '2023秋季校服套装',
            size: 'L码',
            color: '藏青色',
            price: 169.00,
            count: 1,
            image: '/static/images/uniform-1.png'
          },
          {
            id: '2',
            name: '2023冬季加厚校服外套',
            size: 'L码',
            color: '藏青色',
            price: 169.00,
            count: 1,
            image: '/static/images/uniform-2.png'
          }
        ]
      },
      paymentMethods: [
        {
          id: 'wxpay',
          name: '微信支付',
          desc: '推荐使用微信支付',
          icon: 'weixin',
          color: '#09BB07'
        },
        {
          id: 'alipay',
          name: '支付宝',
          desc: '支付宝安全支付',
          icon: 'wallet',
          color: '#1296DB'
        }
      ]
    }
  },
  onLoad(options) {
    if (options.id) {
      this.orderId = options.id;
      // 在实际项目中，这里应该根据订单ID获取订单信息
      // this.getOrderInfo(this.orderId);
    }
  },
  methods: {
    // 选择支付方式
    selectPaymentMethod(methodId) {
      this.selectedMethod = methodId;
    },
    
    // 确认支付
    confirmPay() {
      uni.showLoading({
        title: '支付处理中...'
      });
      
      // 模拟支付过程
      setTimeout(() => {
        uni.hideLoading();
        
        // 这里应该调用实际的支付接口
        if (this.selectedMethod === 'wxpay') {
          // 调用微信支付
          this.wxPay();
        } else if (this.selectedMethod === 'alipay') {
          // 调用支付宝支付
          this.aliPay();
        }
      }, 1000);
    },
    
    // 微信支付
    wxPay() {
      // 在实际项目中，这里应该调用微信支付API
      uni.showModal({
        title: '支付提示',
        content: '确认使用微信支付¥' + this.orderInfo.totalAmount + '?',
        success: (res) => {
          if (res.confirm) {
            // 模拟支付成功
            this.paymentSuccess();
          }
        }
      });
    },
    
    // 支付宝支付
    aliPay() {
      // 在实际项目中，这里应该调用支付宝支付API
      uni.showModal({
        title: '支付提示',
        content: '确认使用支付宝支付¥' + this.orderInfo.totalAmount + '?',
        success: (res) => {
          if (res.confirm) {
            // 模拟支付成功
            this.paymentSuccess();
          }
        }
      });
    },
    
    // 支付成功处理
    paymentSuccess() {
      uni.showToast({
        title: '支付成功',
        icon: 'success',
        duration: 2000
      });
      
      // 延迟跳转到订单详情页
      setTimeout(() => {
        uni.redirectTo({
          url: '/pages/order/order-detail?id=' + this.orderId + '&from=pay'
        });
      }, 2000);
    }
  }
}
</script>

<style lang="scss">
.container {
  padding-bottom: 180rpx;
}

// 通用标题栏样式
.title-bar {
  padding: 30rpx;
  border-bottom: 1rpx solid #f5f5f5;
  
  .title {
    font-size: 32rpx;
    font-weight: 500;
    color: #333;
  }
}

// 订单信息
.order-info {
  background-color: #fff;
  margin-bottom: 20rpx;
  border-radius: 12rpx;
  overflow: hidden;
  
  .info-content {
    padding: 20rpx 30rpx;
    
    .order-number, .order-school, .order-student {
      display: flex;
      justify-content: space-between;
      margin-bottom: 20rpx;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      .label {
        font-size: 28rpx;
        color: #666;
      }
      
      .value {
        font-size: 28rpx;
        color: #333;
      }
    }
  }
}

// 商品清单
.goods-list {
  background-color: #fff;
  margin-bottom: 20rpx;
  border-radius: 12rpx;
  overflow: hidden;
  
  .goods-item {
    display: flex;
    padding: 30rpx;
    border-bottom: 1rpx solid #f5f5f5;
    
    &:last-child {
      border-bottom: none;
    }
    
    .goods-image {
      width: 160rpx;
      height: 160rpx;
      border-radius: 8rpx;
      background-color: #f9f9f9;
      margin-right: 20rpx;
    }
    
    .goods-info {
      flex: 1;
      display: flex;
      flex-direction: column;
      
      .goods-name {
        font-size: 28rpx;
        color: #333;
        margin-bottom: 12rpx;
      }
      
      .goods-spec {
        display: flex;
        margin-bottom: 20rpx;
        
        .spec-text {
          font-size: 24rpx;
          color: #999;
          background-color: #f5f5f5;
          padding: 4rpx 12rpx;
          border-radius: 4rpx;
          margin-right: 10rpx;
        }
      }
      
      .goods-price-row {
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        .goods-price {
          font-size: 30rpx;
          color: #ff5a5f;
          font-weight: 500;
        }
        
        .goods-count {
          font-size: 26rpx;
          color: #666;
        }
      }
    }
  }
}

// 支付方式
.payment-methods {
  background-color: #fff;
  margin-bottom: 20rpx;
  border-radius: 12rpx;
  overflow: hidden;
  
  .methods-list {
    padding: 0 30rpx;
    
    .method-item {
      display: flex;
      align-items: center;
      padding: 30rpx 0;
      border-bottom: 1rpx solid #f5f5f5;
      
      &:last-child {
        border-bottom: none;
      }
      
      &.selected {
        .method-name {
          color: #3B7AFF;
        }
      }
      
      .method-icon {
        width: 60rpx;
        height: 60rpx;
        border-radius: 30rpx;
        display: flex;
        justify-content: center;
        align-items: center;
        margin-right: 20rpx;
        
        &.wxpay {
          background-color: rgba(9, 187, 7, 0.1);
        }
        
        &.alipay {
          background-color: rgba(18, 150, 219, 0.1);
        }
      }
      
      .method-info {
        flex: 1;
        
        .method-name {
          font-size: 28rpx;
          color: #333;
          margin-bottom: 6rpx;
        }
        
        .method-desc {
          font-size: 24rpx;
          color: #999;
        }
      }
    }
  }
}

// 价格明细
.price-detail {
  background-color: #fff;
  margin-bottom: 20rpx;
  border-radius: 12rpx;
  overflow: hidden;
  
  .price-item {
    display: flex;
    justify-content: space-between;
    padding: 20rpx 30rpx;
    
    .label {
      font-size: 28rpx;
      color: #666;
    }
    
    .value {
      font-size: 28rpx;
      color: #333;
    }
    
    &.discount {
      .value {
        color: #ff5a5f;
      }
    }
  }
  
  .price-total {
    display: flex;
    justify-content: space-between;
    padding: 30rpx;
    border-top: 1rpx solid #f5f5f5;
    
    .label {
      font-size: 30rpx;
      color: #333;
      font-weight: 500;
    }
    
    .value {
      font-size: 30rpx;
      color: #ff5a5f;
      font-weight: bold;
    }
  }
}

// 底部支付栏
.payment-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #fff;
  display: flex;
  align-items: center;
  padding: 20rpx 30rpx;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
  
  .total-amount {
    flex: 1;
    
    .label {
      font-size: 28rpx;
      color: #333;
    }
    
    .amount {
      font-size: 32rpx;
      color: #ff5a5f;
      font-weight: bold;
    }
  }
  
  .pay-btn {
    width: 240rpx;
    height: 90rpx;
    line-height: 90rpx;
    background-color: #3B7AFF;
    color: #fff;
    font-size: 30rpx;
    border-radius: 45rpx;
    
    &::after {
      border: none;
    }
  }
}
</style> 