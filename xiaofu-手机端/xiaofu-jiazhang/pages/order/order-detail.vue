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
        class="footer-btn outline danger"
        v-if="canDelete(order.status)"
        @click="deleteOrder"
      >删除订单</button>
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
        v-if="canViewAfterSale()"
        @click="viewAfterSale"
      >查看售后</button>
      <button
        class="footer-btn primary"
        v-if="canComment(order)"
        @click="commentOrder"
      >评价</button>
      <button
        class="footer-btn outline"
        v-if="canViewComment(order)"
        @click="viewComment"
      >查看评价</button>
      <button
        class="footer-btn outline"
        v-if="canReorder(order.status)"
        @click="reorder"
      >再次购买</button>
    </view>
  </view>
</template>

<script>
import config from '@/config'
import { getOrder, cancelOrder as cancelOrderApi, confirmOrder } from '@/api/order'
import { listAfterSale } from '@/api/afterSale'

export default {
  data() {
    return {
      id: null,
      order: {
        id: '',
        orderNo: '',
        status: 'unpaid',
        createTime: '',
        payTime: '',
        shipTime: '',
        completeTime: null,
        productsAmount: '0.00',
        shippingFee: '0.00',
        discount: '0.00',
        totalAmount: '0.00',
        remark: '',
        address: {
          name: '家长',
          phone: '',
          tag: '',
          fullAddress: '暂无收货地址'
        },
        products: []
      },
      logistics: {
        status: '暂无物流信息',
        updateTime: ''
      },
      afterSaleInfo: null
    }
  },
  computed: {
    // 是否有底部操作按钮
    hasActions() {
      return this.canCancel(this.order.status) || 
             this.canPay(this.order.status) || 
             this.canConfirm(this.order.status) || 
             this.canReturn(this.order.status) ||
             this.canViewAfterSale() ||
             this.canComment(this.order) ||
             this.canViewComment(this.order) ||
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
    isLocalOnlyOrder(orderId = this.id) {
      return String(orderId || '').startsWith('DEV');
    },

    normalizeOrder(data = {}) {
      const products = data.products || data.items || data.orderItems || data.xfptOrderItemList || [];
      const address = data.address || {};
      const commentedOrders = uni.getStorageSync('commentedOrders') || {};
      const localCommented = !!commentedOrders[data.id || data.orderId || this.id]?.commented;
      const fullAddress =
        address.fullAddress ||
        data.fullAddress ||
        `${address.province || data.province || ''}${address.city || data.city || ''}${address.district || data.district || ''}${address.detailAddress || address.address || data.detailAddress || data.address || ''}`;
      return {
        ...this.order,
        id: data.id || data.orderId || this.id,
        orderNo: data.orderNo || data.orderSn || data.orderCode || this.id,
        status: this.normalizeStatus(data.status),
        commented: !!(data.commented || data.commentFlag || localCommented),
        commentFlag: !!(data.commentFlag || localCommented),
        createTime: data.createTime || '',
        payTime: data.payTime || '',
        shipTime: data.shipTime || data.deliveryTime || '',
        completeTime: data.completeTime || data.finishTime || null,
        productsAmount: data.productsAmount || data.totalGoodsPrice || data.goodsAmount || data.totalAmount || '0.00',
        shippingFee: data.shippingFee || data.freightAmount || '0.00',
        discount: data.discount || '0.00',
        totalAmount: data.totalAmount || data.payAmount || '0.00',
        remark: data.remark || '',
        address: {
          name: address.name || address.receiver || data.receiver || '家长',
          phone: address.phone || address.mobile || data.phone || data.mobile || '',
          tag: address.tag || address.label || '默认',
          fullAddress: fullAddress || '暂无收货地址'
        },
        products: products.map((item) => ({
          id: item.id || item.goodsId || item.uniformId,
          orderItemId: item.orderItemId || item.id,
          goodsId: item.goodsId || item.uniformId || item.productId || item.id,
          name: item.name || item.goodsName || item.uniformName || item.productName || '校服',
          image: item.image || item.mainImage || item.productImage || item.coverImage || '/static/qjxf.png',
          price: item.price || item.unitPrice || item.salePrice || '0.00',
          size: item.size || item.sizeName || '-',
          color: item.color || item.colorName || item.spec || '默认款',
          quantity: item.quantity || item.buyNum || item.count || 1,
          supplierId: item.supplierId
        }))
      };
    },

    normalizeStatus(status) {
      const map = {
        0: 'unpaid',
        1: 'unshipped',
        2: 'shipped',
        3: 'completed',
        4: 'cancelled',
        6: 'afterSale',
        unpaid: 'unpaid',
        unshipped: 'unshipped',
        shipped: 'shipped',
        completed: 'completed',
        cancelled: 'cancelled',
        afterSale: 'afterSale'
      };
      return map[status] || status || 'unpaid';
    },

    getLocalOrder() {
      const localOrders = uni.getStorageSync('devOrders') || {};
      return localOrders[this.id];
    },

    // 获取订单详情数据
    fetchOrderDetail() {
      const localOrder = this.getLocalOrder();
      if (config.enableMockFallback && this.isLocalOnlyOrder() && localOrder) {
        this.order = this.normalizeOrder(localOrder);
        this.loadAfterSaleInfo();
        return;
      }
      getOrder(this.id)
        .then((res) => {
          this.order = this.normalizeOrder(res.data || res);
          this.loadAfterSaleInfo();
        })
        .catch((error) => {
          uni.showToast({
            title: error.message || '订单详情加载失败',
            icon: 'none'
          });
        });
    },
    
    // 获取订单状态图标
    getStatusIcon(status) {
      const iconMap = {
        'unpaid': 'wallet',
        'unshipped': 'box',
        'shipped': 'car',
        'afterSale': 'reload',
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
        'afterSale': '售后中',
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
        'afterSale': '售后申请已提交，请查看售后处理进度',
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
        url: `/pages/logistics/logistics-detail?id=${this.id}`
      });
    },
    
    // 是否可以取消订单
    canCancel(status) {
      return status === 'unpaid';
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
      return ['unshipped', 'shipped', 'completed'].includes(status) && !this.afterSaleInfo;
    },

    canViewAfterSale() {
      return !!(this.afterSaleInfo && this.afterSaleInfo.id);
    },

    canDelete(status) {
      return status === 'cancelled';
    },
    
    // 是否可以再次购买
    canReorder(status) {
      return ['completed', 'cancelled'].includes(status);
    },

    canComment(order) {
      return order.status === 'completed' && !order.commentFlag && !order.commented;
    },

    canViewComment(order) {
      return order.status === 'completed' && !!(order.commentFlag || order.commented);
    },
    
    // 取消订单
    cancelOrder() {
      uni.showModal({
        title: '确认取消',
        content: '确定要取消此订单吗？',
        success: (res) => {
          if (res.confirm) {
            uni.showLoading({ title: '处理中' });
            cancelOrderApi(this.id)
              .catch((error) => {
                if (!config.enableMockFallback) return Promise.reject(error);
              })
              .then(() => {
                uni.hideLoading();
                this.order.status = 'cancelled';
                this.saveLocalOrder();
                uni.showToast({
                  title: '订单已取消',
                  icon: 'success'
                });
              })
              .catch((error) => {
                uni.hideLoading();
                uni.showToast({
                  title: error.message || '取消失败',
                  icon: 'none'
                });
              });
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
            uni.showLoading({ title: '处理中' });
            confirmOrder(this.id)
              .catch((error) => {
                if (!config.enableMockFallback) return Promise.reject(error);
              })
              .then(() => {
                uni.hideLoading();
                this.order.status = 'completed';
                this.order.completeTime = this.getCurrentTime();
                this.saveLocalOrder();
                uni.showToast({
                  title: '已确认收货',
                  icon: 'success'
                });
              })
              .catch((error) => {
                uni.hideLoading();
                uni.showToast({
                  title: error.message || '确认失败',
                  icon: 'none'
                });
              });
          }
        }
      });
    },
    
    // 申请退换
    applyReturn() {
      const options = ['退货退款', '换货', '仅退款'];
      const typeMap = ['2', '3', '1'];
      uni.showActionSheet({
        itemList: options,
        success: (res) => {
          const applyType = typeMap[res.tapIndex] || '2';
          const query = [
            `id=${this.id}`,
            `orderNo=${encodeURIComponent(this.order.orderNo || '')}`,
            `amount=${encodeURIComponent(this.order.payAmount || this.order.totalAmount || '0.00')}`,
            `applyType=${applyType}`
          ].join('&');
          uni.navigateTo({
            url: `/pages/order/return-apply?${query}`
          });
        }
      });
    },

    async loadAfterSaleInfo() {
      try {
        const res = await listAfterSale({
          orderId: this.id,
          pageNum: 1,
          pageSize: 20
        });
        const rows = res.rows || [];
        if (!rows.length) {
          this.afterSaleInfo = null;
          return;
        }
        const latest = rows.sort((a, b) => String(b.createTime || '').localeCompare(String(a.createTime || '')))[0];
        this.afterSaleInfo = latest;
        if (['0', '1', '3'].includes(String(latest.status || '0'))) {
          this.order.status = 'afterSale';
        }
      } catch (error) {
        this.afterSaleInfo = null;
      }
    },

    viewAfterSale() {
      if (!this.afterSaleInfo || !this.afterSaleInfo.id) return;
      const query = [
        `id=${this.id}`,
        `afterSaleId=${this.afterSaleInfo.id}`,
        `afterSaleNo=${encodeURIComponent(this.afterSaleInfo.afterSaleNo || '')}`
      ].join('&');
      uni.navigateTo({
        url: `/pages/order/after-sale-detail?${query}`
      });
    },

    commentOrder() {
      uni.setStorageSync('pendingCommentOrder', this.order);
      uni.navigateTo({
        url: `/pages/order/comment-apply?id=${this.id}&orderNo=${encodeURIComponent(this.order.orderNo || '')}`
      });
    },

    viewComment() {
      uni.navigateTo({
        url: `/pages/order/comment-detail?id=${this.id}&orderNo=${encodeURIComponent(this.order.orderNo || '')}`
      });
    },
    
    // 再次购买
    reorder() {
      if (!this.order.products || !this.order.products.length) {
        uni.showToast({
          title: '暂无可购买商品',
          icon: 'none'
        });
        return;
      }
      uni.showLoading({ title: '加载中' });
      setTimeout(() => {
        uni.hideLoading();
        uni.navigateTo({
          url: `/pages/uniform/uniform-detail?id=${this.order.products[0].id}`
        });
      }, 500);
    },

    saveLocalOrder() {
      const localOrders = uni.getStorageSync('devOrders') || {};
      if (this.isLocalOnlyOrder() && localOrders[this.id]) {
        localOrders[this.id] = this.order;
        uni.setStorageSync('devOrders', localOrders);
      }
    },

    removeLocalOrder() {
      const localOrders = uni.getStorageSync('devOrders') || {};
      if (this.isLocalOnlyOrder() && localOrders[this.id]) {
        delete localOrders[this.id];
        uni.setStorageSync('devOrders', localOrders);
      }
    },

    deleteOrder() {
      uni.showModal({
        title: '删除订单',
        content: '删除后无法恢复，确定删除这个已取消订单吗？',
        success: (res) => {
          if (!res.confirm) return;
          uni.showLoading({ title: '删除中' });
          import('@/api/order').then(({ deleteOrder: deleteOrderApi }) => {
            deleteOrderApi(this.id)
              .then(() => {
                uni.hideLoading();
                this.removeLocalOrder();
                uni.showToast({ title: '已删除', icon: 'success' });
                setTimeout(() => {
                  uni.navigateBack();
                }, 500);
              })
              .catch((error) => {
                uni.hideLoading();
                uni.showToast({
                  title: error.message || '删除失败',
                  icon: 'none'
                });
              });
          });
        }
      });
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

    &.danger {
      background-color: #fff5f5;
      color: #f04438;
      border: 1rpx solid #ffd6d6;

      &::after {
        border: none;
      }
    }
  }
}
</style> 
