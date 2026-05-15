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
      <button class="pay-btn" :disabled="isSubmitting" @click="confirmPay">
        {{ isSubmitting ? '支付处理中...' : '确认支付' }}
      </button>
    </view>
  </view>
</template>

<script>
import { getOrder, payOrder } from '@/api/order'

export default {
  data() {
    return {
      orderId: '',
      isSubmitting: false,
      selectedMethod: 'wxpay',
      orderInfo: {
        orderNo: '',
        school: '',
        studentName: '',
        studentClass: '',
        totalGoodsPrice: '0.00',
        shippingFee: '0.00',
        discount: '0.00',
        totalAmount: '0.00',
        items: []
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
          id: 'offline',
          name: '线下确认',
          desc: '用于联调和学校线下收款场景',
          icon: 'wallet',
          color: '#1296DB'
        }
      ]
    }
  },
  onLoad(options) {
    if (options.id) {
      this.orderId = options.id;
      this.getOrderInfo();
    }
  },
  methods: {
    normalizeOrder(data = {}) {
      const products = data.products || data.items || data.orderItems || data.xfptOrderItemList || [];
      const student = data.student || {};
      const gradeName = data.gradeName || student.grade || student.gradeName || '';
      const className = data.className || data.studentClass || student.className || '';
      return {
        orderNo: data.orderNo || data.orderSn || data.orderCode || this.orderId,
        school: data.school || data.schoolName || student.school || student.schoolName || '石家庄市示例学校',
        studentName: data.studentName || student.name || student.studentName || '学生',
        studentClass: [gradeName, className].filter(Boolean).join(' '),
        totalGoodsPrice: data.productsAmount || data.totalGoodsPrice || data.goodsAmount || data.totalAmount || '0.00',
        shippingFee: data.shippingFee || data.freightAmount || '0.00',
        discount: data.discount || '0.00',
        totalAmount: data.totalAmount || data.payAmount || '0.00',
        items: products.map((item) => ({
          id: item.id || item.uniformId,
          name: item.name || item.goodsName || item.uniformName || item.productName || '校服',
          size: item.size || item.sizeName || '-',
          color: item.color || item.colorName || item.spec || '默认款',
          price: item.price || item.unitPrice || item.salePrice || '0.00',
          count: item.count || item.quantity || item.buyNum || 1,
          image: item.image || item.mainImage || item.productImage || item.coverImage || '/static/qjxf.png'
        }))
      };
    },
    getOrderInfo() {
      getOrder(this.orderId)
        .then((res) => {
          this.orderInfo = this.normalizeOrder(res.data || res);
        })
        .catch((error) => {
          uni.showToast({
            title: error.message || '订单信息加载失败',
            icon: 'none'
          });
        });
    },
    selectPaymentMethod(methodId) {
      this.selectedMethod = methodId;
    },
    confirmPay() {
      uni.showModal({
        title: '确认支付',
        content: `确认支付¥${this.orderInfo.totalAmount}？`,
        success: (res) => {
          if (!res.confirm) return;
          this.submitPay();
        }
      });
    },
    submitPay() {
      if (this.isSubmitting) return;
      this.isSubmitting = true;
      uni.showLoading({
        title: '支付处理中...'
      });

      payOrder(this.orderId, {
        payType: this.selectedMethod
      })
        .then(() => {
          this.paymentSuccess();
        })
        .catch((error) => {
          uni.hideLoading();
          uni.showToast({
            title: error.message || '支付失败',
            icon: 'none'
          });
        })
        .finally(() => {
          this.isSubmitting = false;
        });
    },
    paymentSuccess() {
      uni.hideLoading();
      uni.showToast({
        title: '支付成功',
        icon: 'success',
        duration: 1200
      });

      setTimeout(() => {
        uni.redirectTo({
          url: '/pages/order/order-list?status=unshipped'
        });
      }, 1200);
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
