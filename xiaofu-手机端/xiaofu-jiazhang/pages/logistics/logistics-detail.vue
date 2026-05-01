<template>
  <view class="container">
    <!-- 物流状态卡片 -->
    <view class="logistics-status">
      <view class="status-header">
        <image class="courier-logo" :src="logisticsInfo.courierLogo" mode="aspectFit"></image>
        <view class="status-info">
          <text class="courier-name">{{logisticsInfo.courierName}}</text>
          <text class="tracking-number">{{logisticsInfo.trackingNumber}}</text>
        </view>
        <button class="copy-btn" @click="copyTrackingNumber">复制</button>
      </view>
      <view class="status-content">
        <view class="current-status">
          <view class="status-icon" :class="getStatusClass(logisticsInfo.status)">
            <uni-icons :type="getStatusIcon(logisticsInfo.status)" size="20" color="#fff"></uni-icons>
          </view>
          <view class="status-text">
            <text class="main-status">{{getStatusText(logisticsInfo.status)}}</text>
            <text class="sub-status">{{logisticsInfo.statusDesc}}</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 物流轨迹 -->
    <view class="logistics-timeline">
      <view class="timeline-title">
        <text>物流轨迹</text>
      </view>
      <view class="timeline-list">
        <view 
          class="timeline-item" 
          v-for="(item, index) in logisticsInfo.traces" 
          :key="index"
          :class="{'active': index === 0}"
        >
          <view class="timeline-dot"></view>
          <view class="timeline-line" v-if="index !== logisticsInfo.traces.length - 1"></view>
          <view class="timeline-content">
            <view class="timeline-info">
              <text class="timeline-msg">{{item.message}}</text>
              <text class="timeline-time">{{item.time}}</text>
            </view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 订单信息 -->
    <view class="order-info">
      <view class="info-title">
        <text>订单信息</text>
      </view>
      <view class="info-content">
        <view class="info-item">
          <text class="item-label">订单编号</text>
          <text class="item-value">{{logisticsInfo.orderNo}}</text>
        </view>
        <view class="info-item">
          <text class="item-label">发货时间</text>
          <text class="item-value">{{logisticsInfo.shipTime}}</text>
        </view>
        <view class="info-item">
          <text class="item-label">收货地址</text>
          <text class="item-value address-value">{{logisticsInfo.address}}</text>
        </view>
      </view>
    </view>
    
    <!-- 商品信息 -->
    <view class="goods-info">
      <view class="info-title">
        <text>商品信息</text>
      </view>
      <view 
        class="goods-item" 
        v-for="(item, index) in logisticsInfo.goods" 
        :key="index"
      >
        <image class="goods-image" :src="item.image" mode="aspectFill"></image>
        <view class="goods-detail">
          <text class="goods-name">{{item.name}}</text>
          <view class="goods-spec">
            <text class="spec-text">{{item.size}}</text>
            <text class="spec-text">{{item.color}}</text>
          </view>
          <text class="goods-count">x{{item.count}}</text>
        </view>
      </view>
    </view>
    
    <!-- 底部按钮 -->
    <view class="footer-actions">
      <button class="action-btn primary" @click="contactCourier">联系快递员</button>
      <button class="action-btn outline" @click="viewOrder">查看订单</button>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      orderId: '',
      logisticsInfo: {
        courierName: '顺丰速运',
        courierLogo: '/static/images/sf-express.png',
        trackingNumber: 'SF1234567890123',
        status: 'delivering', // delivering, delivered, exception
        statusDesc: '您的订单正在配送中',
        orderNo: 'XF2023101200001',
        shipTime: '2023-10-12 09:30:25',
        address: '河北省石家庄市长安区中山东路123号 张三 13812345678',
        goods: [
          {
            name: '2023秋季校服短袖套装',
            image: '/static/images/uniform-demo1.jpg',
            size: 'XL号',
            color: '藏青色',
            count: 1
          },
          {
            name: '2023秋季校服运动裤',
            image: '/static/images/uniform-demo2.jpg',
            size: 'XL号',
            color: '藏青色',
            count: 2
          }
        ],
        traces: [
          {
            message: '【石家庄市】您的快件已签收，签收人：前台代签，感谢您使用顺丰快递',
            time: '2023-10-15 10:30:25'
          },
          {
            message: '【石家庄市】快件正在派送途中，请您准备签收',
            time: '2023-10-15 08:15:32'
          },
          {
            message: '【石家庄市】快件已到达石家庄长安区营业点',
            time: '2023-10-15 07:23:05'
          },
          {
            message: '【石家庄市】快件在【石家庄转运中心】完成分拣，准备发往下一站',
            time: '2023-10-14 21:35:17'
          },
          {
            message: '【石家庄市】快件已到达【石家庄转运中心】',
            time: '2023-10-14 19:50:43'
          },
          {
            message: '【石家庄市】顺丰速运已收取快件',
            time: '2023-10-14 15:20:11'
          },
          {
            message: '【石家庄市】校服供应商已安排取件',
            time: '2023-10-14 14:30:58'
          },
          {
            message: '【石家庄市】商品已出库',
            time: '2023-10-14 13:25:30'
          },
          {
            message: '【系统通知】您的订单已发货',
            time: '2023-10-14 13:20:00'
          }
        ]
      }
    }
  },
  onLoad(options) {
    if (options.id) {
      this.orderId = options.id;
      // 根据订单id获取物流信息
      this.getLogisticsInfo();
    }
  },
  methods: {
    // 获取物流信息
    getLogisticsInfo() {
      // 这里应该调用接口获取物流信息，现在用模拟数据
      uni.showLoading({
        title: '加载中...'
      });
      
      setTimeout(() => {
        uni.hideLoading();
        // 模拟数据已在data中预设
      }, 500);
    },
    
    // 获取状态样式类
    getStatusClass(status) {
      switch (status) {
        case 'delivering':
          return 'delivering';
        case 'delivered':
          return 'delivered';
        case 'exception':
          return 'exception';
        default:
          return '';
      }
    },
    
    // 获取状态图标
    getStatusIcon(status) {
      switch (status) {
        case 'delivering':
          return 'car';
        case 'delivered':
          return 'checkmarkempty';
        case 'exception':
          return 'info';
        default:
          return 'car';
      }
    },
    
    // 获取状态文本
    getStatusText(status) {
      switch (status) {
        case 'delivering':
          return '配送中';
        case 'delivered':
          return '已签收';
        case 'exception':
          return '异常';
        default:
          return '配送中';
      }
    },
    
    // 复制运单号
    copyTrackingNumber() {
      uni.setClipboardData({
        data: this.logisticsInfo.trackingNumber,
        success: () => {
          uni.showToast({
            title: '复制成功',
            icon: 'success'
          });
        }
      });
    },
    
    // 联系快递员
    contactCourier() {
      uni.showModal({
        title: '联系快递员',
        content: '是否拨打快递员电话 13987654321？',
        confirmText: '拨打',
        success: (res) => {
          if (res.confirm) {
            uni.makePhoneCall({
              phoneNumber: '13987654321'
            });
          }
        }
      });
    },
    
    // 查看订单
    viewOrder() {
      uni.navigateTo({
        url: `/pages/order/order-detail?id=${this.orderId}`
      });
    }
  }
}
</script>

<style lang="scss">
.container {
  padding-bottom: 120rpx; // 为底部按钮预留空间
}

// 物流状态卡片
.logistics-status {
  background-color: #fff;
  margin-bottom: 20rpx;
  border-radius: 12rpx;
  overflow: hidden;
  
  .status-header {
    display: flex;
    align-items: center;
    padding: 30rpx;
    border-bottom: 1rpx solid #f5f5f5;
    
    .courier-logo {
      width: 80rpx;
      height: 80rpx;
      border-radius: 8rpx;
      margin-right: 20rpx;
    }
    
    .status-info {
      flex: 1;
      
      .courier-name {
        font-size: 30rpx;
        color: #333;
        font-weight: 500;
        margin-bottom: 8rpx;
        display: block;
      }
      
      .tracking-number {
        font-size: 26rpx;
        color: #666;
      }
    }
    
    .copy-btn {
      padding: 0 20rpx;
      height: 56rpx;
      line-height: 56rpx;
      background-color: #f5f5f5;
      color: #666;
      font-size: 26rpx;
      border-radius: 28rpx;
      
      &::after {
        border: none;
      }
    }
  }
  
  .status-content {
    padding: 30rpx;
    
    .current-status {
      display: flex;
      align-items: center;
      
      .status-icon {
        width: 60rpx;
        height: 60rpx;
        border-radius: 30rpx;
        display: flex;
        justify-content: center;
        align-items: center;
        margin-right: 20rpx;
        
        &.delivering {
          background-color: #3B7AFF;
        }
        
        &.delivered {
          background-color: #2ecc71;
        }
        
        &.exception {
          background-color: #ff5a5f;
        }
      }
      
      .status-text {
        .main-status {
          font-size: 32rpx;
          color: #333;
          font-weight: 500;
          margin-bottom: 6rpx;
          display: block;
        }
        
        .sub-status {
          font-size: 26rpx;
          color: #666;
        }
      }
    }
  }
}

// 物流轨迹
.logistics-timeline {
  background-color: #fff;
  margin-bottom: 20rpx;
  border-radius: 12rpx;
  overflow: hidden;
  
  .timeline-title {
    padding: 30rpx;
    border-bottom: 1rpx solid #f5f5f5;
    
    text {
      font-size: 30rpx;
      color: #333;
      font-weight: 500;
    }
  }
  
  .timeline-list {
    padding: 0 30rpx;
    
    .timeline-item {
      position: relative;
      padding: 30rpx 0 30rpx 40rpx;
      
      .timeline-dot {
        position: absolute;
        left: 0;
        top: 40rpx;
        width: 16rpx;
        height: 16rpx;
        border-radius: 50%;
        background-color: #ccc;
        z-index: 1;
      }
      
      .timeline-line {
        position: absolute;
        left: 7rpx;
        top: 48rpx;
        width: 2rpx;
        height: calc(100% - 18rpx);
        background-color: #e0e0e0;
      }
      
      .timeline-content {
        .timeline-info {
          .timeline-msg {
            font-size: 28rpx;
            color: #666;
            display: block;
            line-height: 1.5;
            margin-bottom: 8rpx;
          }
          
          .timeline-time {
            font-size: 24rpx;
            color: #999;
          }
        }
      }
      
      &.active {
        .timeline-dot {
          background-color: #3B7AFF;
          width: 20rpx;
          height: 20rpx;
          left: -2rpx;
        }
        
        .timeline-msg {
          color: #333 !important;
          font-weight: 500;
        }
      }
    }
  }
}

// 订单信息
.order-info {
  background-color: #fff;
  margin-bottom: 20rpx;
  border-radius: 12rpx;
  overflow: hidden;
  
  .info-title {
    padding: 30rpx;
    border-bottom: 1rpx solid #f5f5f5;
    
    text {
      font-size: 30rpx;
      color: #333;
      font-weight: 500;
    }
  }
  
  .info-content {
    padding: 0 30rpx;
    
    .info-item {
      display: flex;
      padding: 24rpx 0;
      border-bottom: 1rpx solid #f5f5f5;
      
      &:last-child {
        border-bottom: none;
      }
      
      .item-label {
        width: 150rpx;
        font-size: 28rpx;
        color: #666;
      }
      
      .item-value {
        flex: 1;
        font-size: 28rpx;
        color: #333;
      }
      
      .address-value {
        line-height: 1.5;
      }
    }
  }
}

// 商品信息
.goods-info {
  background-color: #fff;
  margin-bottom: 20rpx;
  border-radius: 12rpx;
  overflow: hidden;
  
  .info-title {
    padding: 30rpx;
    border-bottom: 1rpx solid #f5f5f5;
    
    text {
      font-size: 30rpx;
      color: #333;
      font-weight: 500;
    }
  }
  
  .goods-item {
    display: flex;
    padding: 30rpx;
    border-bottom: 1rpx solid #f5f5f5;
    
    &:last-child {
      border-bottom: none;
    }
    
    .goods-image {
      width: 120rpx;
      height: 120rpx;
      border-radius: 8rpx;
      margin-right: 20rpx;
    }
    
    .goods-detail {
      flex: 1;
      
      .goods-name {
        font-size: 28rpx;
        color: #333;
        margin-bottom: 12rpx;
        display: block;
        line-height: 1.4;
      }
      
      .goods-spec {
        margin-bottom: 12rpx;
        
        .spec-text {
          display: inline-block;
          font-size: 24rpx;
          color: #666;
          background-color: #f5f5f5;
          padding: 4rpx 12rpx;
          border-radius: 4rpx;
          margin-right: 12rpx;
        }
      }
      
      .goods-count {
        font-size: 24rpx;
        color: #999;
      }
    }
  }
}

// 底部按钮
.footer-actions {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: space-between;
  padding: 20rpx 30rpx;
  background-color: #fff;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
  
  .action-btn {
    flex: 1;
    height: 90rpx;
    line-height: 90rpx;
    border-radius: 45rpx;
    font-size: 30rpx;
    margin: 0 10rpx;
    
    &::after {
      border: none;
    }
    
    &.primary {
      background-color: #3B7AFF;
      color: #fff;
    }
    
    &.outline {
      background-color: #fff;
      color: #3B7AFF;
      border: 1rpx solid #3B7AFF;
    }
  }
}
</style> 