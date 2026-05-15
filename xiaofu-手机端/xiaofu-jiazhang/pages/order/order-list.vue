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
        @click="goToOrderDetail(order.detailOrderId || order.id)"
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
            <view class="product-item" v-for="(product, pIndex) in getDisplayProducts(order.products)" :key="pIndex">
              <image class="product-image" :src="product.image" mode="aspectFill"></image>
              <view class="product-info">
                <text class="product-name">{{product.name}}</text>
                <text class="product-spec">{{product.size}} | {{product.color}}</text>
                <view
                  v-if="order.status === 'reviewed' && order.commentContent"
                  class="product-comment"
                >
                  <text class="comment-label">评价：</text>
                  <text class="comment-content">{{order.commentContent}}</text>
                </view>
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
            <text>共{{order.productCount || getTotalProducts(order.products)}}件商品</text>
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
              v-if="canReview(order.status)"
              @click.stop="reviewGoods(order.id)"
            >{{ getReviewActionText(order) }}</button>
            <button
              class="action-btn outline"
              v-if="canViewReviewDetail(order.status)"
              @click.stop="viewReviewDetail(order)"
            >评价详情</button>
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
        <image class="empty-image" src="/static/uni.png" mode="aspectFit"></image>
        <text class="empty-text">暂无相关订单</text>
        <button class="go-shop-btn" @click="goToShop">去购买校服</button>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { listOrders, cancelOrder as cancelOrderApi, confirmOrder, getOrder } from '@/api/order'
import { listComments } from '@/api/comment'
import { listAfterSales } from '@/api/after-sale'

export default {
  data() {
    return {
      currentStatus: 'all',
      userComments: [],
      isLoading: false,
      isLoadingMore: false,
      isRefreshing: false,
      page: 1,
      hasMore: true,
      orderList: [],
      orderStatus: [
        { status: 'all', name: '全部' },
        { status: 'unpaid', name: '待付款' },
        { status: 'unshipped', name: '待发货' },
        { status: 'shipped', name: '待收货' },
        { status: 'completed', name: '已完成' },
        { status: 'reviewed', name: '已评价' },
        { status: 'afterSale', name: '退换货' },
        { status: 'cancelled', name: '已取消' }
      ]
    }
  },
  onLoad(options) {
    const allowedStatus = ['all', 'unpaid', 'unshipped', 'shipped', 'completed', 'reviewed', 'afterSale', 'cancelled']
    if (options.status && allowedStatus.includes(options.status)) {
      this.currentStatus = options.status
    }
  },
  onShow() {
    this.page = 1
    this.hasMore = true
    this.orderList = []
    this.loadOrders()
  },
  methods: {
    getDisplayProducts(products = []) {
      return Array.isArray(products) ? products.slice(0, 2) : []
    },

    normalizeProduct(product = {}) {
      return {
        id: product.id || product.productId || product.uniformId,
        uniformId: product.uniformId || product.goodsId || product.productId || product.id,
        supplierId: product.supplierId,
        name: product.name || product.goodsName || product.productName || product.uniformName || '校服',
        image: product.image || product.mainImage || product.productImage || product.coverImage || '/static/qjxf.png',
        price: product.price || product.salePrice || product.unitPrice || '0.00',
        quantity: product.quantity || product.buyNum || product.num || 1,
        size: product.size || product.sizeName || '-',
        color: product.color || product.colorName || '-'
      }
    },

    normalizeOrder(item = {}) {
      const products = item.products || item.items || item.orderItems || item.xfptOrderItemList || []
      return {
        ...item,
        id: item.id || item.orderId,
        detailOrderId: item.detailOrderId || item.id || item.orderId,
        orderNo: item.orderNo || item.orderSn || item.orderCode,
        status: this.normalizeStatus(item.status),
        createTime: item.createTime,
        totalAmount: item.totalAmount || item.payAmount || item.amount || '0.00',
        products: products.map((product) => this.normalizeProduct(product)),
        productCount: this.getTotalProducts(products)
      }
    },

    enrichOrderWithDetail(order, detail) {
      if (!detail) return order
      const normalizedDetail = this.normalizeOrder(detail)
      return {
        ...order,
        products: normalizedDetail.products,
        productCount: normalizedDetail.productCount || order.productCount || 0,
        totalAmount: normalizedDetail.totalAmount || order.totalAmount
      }
    },

    normalizeStatus(status) {
      const statusMap = {
        0: 'unpaid',
        1: 'unshipped',
        2: 'shipped',
        3: 'completed',
        4: 'cancelled',
        unpaid: 'unpaid',
        unshipped: 'unshipped',
        shipped: 'shipped',
        completed: 'completed',
        cancelled: 'cancelled',
        reviewed: 'reviewed',
        afterSale: 'afterSale'
      }
      return statusMap[status] || status
    },

    getQueryParams() {
      return {
        pageNum: this.page,
        pageSize: 10,
        status: this.currentStatus === 'all' ? undefined : this.currentStatus
      }
    },

    finishLoading() {
      this.isLoading = false
      this.isLoadingMore = false
      this.isRefreshing = false
    },

    isSpecialListStatus() {
      return ['reviewed', 'afterSale'].includes(this.currentStatus)
    },

    async loadOrderMap(orderIds = []) {
      const uniqueIds = [...new Set(orderIds.filter(Boolean).map((item) => String(item)))]
      const pairs = await Promise.all(
        uniqueIds.map((orderId) =>
          getOrder(orderId)
            .then((res) => [String(orderId), res.data || res])
            .catch(() => [String(orderId), null])
        )
      )
      return pairs.reduce((map, [orderId, order]) => {
        map[orderId] = order
        return map
      }, {})
    },

    findOrderProduct(order, record = {}) {
      const products = order?.products || []
      return (
        products.find((item) => String(item.id) === String(record.orderItemId)) ||
        products.find((item) => String(item.uniformId) === String(record.goodsId)) ||
        products[0] ||
        null
      )
    },

    buildReviewedOrder(record, orderMap) {
      const order = orderMap[String(record.orderId)]
      const product = this.findOrderProduct(order, record)
      const normalizedProduct = product
        ? this.normalizeProduct(product)
        : this.normalizeProduct({
            id: record.orderItemId,
            goodsId: record.goodsId,
            name: record.goodsName,
            quantity: 1
          })

      return {
        id: record.id,
        detailOrderId: record.orderId,
        orderNo: record.orderNo,
        status: 'reviewed',
        createTime: record.createTime,
        totalAmount: order?.totalAmount || normalizedProduct.price || '0.00',
        score: record.score,
        commentContent: record.content,
        goodsId: record.goodsId,
        orderItemId: record.orderItemId,
        hasUserComment: true,
        productCount: Number(normalizedProduct.quantity || 1),
        products: [normalizedProduct]
      }
    },

    async ensureUserCommentsLoaded(force = false) {
      if (!force && this.userComments.length) {
        return this.userComments
      }
      const res = await listComments({
        pageNum: 1,
        pageSize: 500
      })
      this.userComments = res.rows || res.data || res.list || []
      return this.userComments
    },

    attachReviewState(orders = []) {
      const commentMap = new Map()
      this.userComments.forEach((item) => {
        if (item.orderId) {
          commentMap.set(String(item.orderId), true)
        }
      })
      return orders.map((order) => ({
        ...order,
        hasUserComment: commentMap.has(String(order.detailOrderId || order.id))
      }))
    },

    getAfterSaleDisplayStatus(status) {
      const statusMap = {
        '0': 'afterSalePending',
        '1': 'afterSaleApproved',
        '2': 'afterSaleRejected',
        '3': 'afterSaleProcessing',
        '4': 'afterSaleCompleted',
        '5': 'afterSaleClosed'
      }
      return statusMap[String(status)] || 'afterSale'
    },

    buildAfterSaleOrder(record, orderMap) {
      const order = orderMap[String(record.orderId)]
      const product = this.findOrderProduct(order, record)
      const normalizedProduct = product
        ? this.normalizeProduct(product)
        : this.normalizeProduct({
            id: record.orderItemId,
            name: record.orderNo,
            quantity: record.applyNum || 1
          })

      return {
        id: record.id,
        detailOrderId: record.orderId,
        orderNo: record.orderNo,
        status: this.getAfterSaleDisplayStatus(record.status),
        createTime: record.createTime,
        totalAmount: record.applyAmount || order?.totalAmount || '0.00',
        applyType: record.applyType,
        reason: record.reason,
        productCount: Number(record.applyNum || normalizedProduct.quantity || 1),
        products: [
          {
            ...normalizedProduct,
            quantity: record.applyNum || normalizedProduct.quantity || 1
          }
        ]
      }
    },

    async loadReviewedOrders() {
      const res = await listComments({
        pageNum: this.page,
        pageSize: 10
      })
      const rows = res.rows || res.data || res.list || []
      const orderMap = await this.loadOrderMap(rows.map((item) => item.orderId))
      return {
        ...res,
        rows: rows.map((item) => this.buildReviewedOrder(item, orderMap))
      }
    },

    async loadAfterSaleOrders() {
      const res = await listAfterSales({
        pageNum: this.page,
        pageSize: 10
      })
      const rows = res.rows || res.data || res.list || []
      const orderMap = await this.loadOrderMap(rows.map((item) => item.orderId))
      return {
        ...res,
        rows: rows.map((item) => this.buildAfterSaleOrder(item, orderMap))
      }
    },

    switchStatus(status) {
      if (this.currentStatus === status) return
      this.currentStatus = status
      this.page = 1
      this.hasMore = true
      this.orderList = []
      this.loadOrders()
    },

    loadOrders() {
      if (!this.hasMore || this.isLoading) return
      this.isLoading = true

      const loader = this.currentStatus === 'reviewed'
        ? this.loadReviewedOrders()
        : this.currentStatus === 'afterSale'
          ? this.loadAfterSaleOrders()
          : Promise.all([
              listOrders(this.getQueryParams()),
              this.ensureUserCommentsLoaded(this.page === 1)
            ]).then(async ([res]) => {
              const rows = (res.rows || res.data || res.list || []).map((item) => this.normalizeOrder(item))
              const orderMap = await this.loadOrderMap(rows.map((item) => item.detailOrderId || item.id))
              return {
                ...res,
                rows: this.attachReviewState(
                  rows.map((item) => this.enrichOrderWithDetail(item, orderMap[String(item.detailOrderId || item.id)]))
                )
              }
            })

      loader
        .then((res) => {
          const list = res.rows || []
          this.orderList = this.page === 1 ? list : [...this.orderList, ...list]
          this.hasMore = res.total ? this.orderList.length < res.total : list.length >= 10
          if (this.hasMore) this.page += 1
          this.finishLoading()
        })
        .catch((error) => {
          this.finishLoading()
          uni.showToast({
            title: error.message || '订单加载失败',
            icon: 'none'
          })
        })
    },

    loadMore() {
      if (!this.hasMore || this.isLoadingMore || this.isLoading) return
      this.isLoadingMore = true
      this.loadOrders()
    },

    onRefresh() {
      this.isRefreshing = true
      this.page = 1
      this.hasMore = true
      this.orderList = []
      this.loadOrders()
    },

    goToOrderDetail(orderId) {
      uni.navigateTo({
        url: `/pages/order/order-detail?id=${orderId}`
      })
    },

    goToShop() {
      uni.navigateTo({
        url: '/pages/uniform/uniform-list'
      })
    },

    cancelOrder(orderId) {
      uni.showModal({
        title: '确认取消',
        content: '确定要取消此订单吗？',
        success: (res) => {
          if (res.confirm) {
            uni.showLoading({ title: '处理中' })
            cancelOrderApi(orderId)
              .then(() => {
                uni.hideLoading()
                uni.showToast({
                  title: '订单已取消',
                  icon: 'success'
                })
                this.page = 1
                this.hasMore = true
                this.orderList = []
                this.loadOrders()
              })
              .catch((error) => {
                uni.hideLoading()
                uni.showToast({
                  title: error.message || '取消失败',
                  icon: 'none'
                })
              })
          }
        }
      })
    },

    payOrder(orderId) {
      uni.navigateTo({
        url: `/pages/payment/payment?id=${orderId}`
      })
    },

    confirmReceipt(orderId) {
      uni.showModal({
        title: '确认收货',
        content: '确认已收到商品吗？',
        success: (res) => {
          if (res.confirm) {
            uni.showLoading({ title: '处理中' })
            confirmOrder(orderId)
              .then(() => {
                uni.hideLoading()
                uni.showToast({
                  title: '已确认收货',
                  icon: 'success'
                })
                this.page = 1
                this.hasMore = true
                this.orderList = []
                this.loadOrders()
              })
              .catch((error) => {
                uni.hideLoading()
                uni.showToast({
                  title: error.message || '确认失败',
                  icon: 'none'
                })
              })
          }
        }
      })
    },

    applyReturn(orderId) {
      uni.navigateTo({
        url: `/pages/order/after-sale-form?orderId=${orderId}`
      })
    },

    reviewGoods(orderId) {
      uni.navigateTo({
        url: `/pages/order/review-form?orderId=${orderId}`
      })
    },

    viewReviewDetail(order) {
      const product = (order.products || [])[0] || {}
      const goodsId = product.uniformId || product.id || order.goodsId || ''
      const orderItemId = product.id || order.orderItemId || ''
      uni.navigateTo({
        url: `/pages/order/review-detail?orderId=${order.detailOrderId || order.id}&goodsId=${goodsId}&orderItemId=${orderItemId}`
      })
    },

    getStatusText(status) {
      const statusMap = {
        unpaid: '待付款',
        unshipped: '待发货',
        shipped: '待收货',
        completed: '已完成',
        cancelled: '已取消',
        reviewed: '已评价',
        afterSale: '退换货',
        afterSalePending: '待审核',
        afterSaleApproved: '已通过',
        afterSaleRejected: '已驳回',
        afterSaleProcessing: '处理中',
        afterSaleCompleted: '已完成',
        afterSaleClosed: '已关闭'
      }
      return statusMap[status] || '未知状态'
    },

    getStatusClass(status) {
      const statusClassMap = {
        unpaid: 'status-warning',
        unshipped: 'status-primary',
        shipped: 'status-info',
        completed: 'status-success',
        cancelled: 'status-default',
        reviewed: 'status-success',
        afterSale: 'status-warning',
        afterSalePending: 'status-warning',
        afterSaleApproved: 'status-primary',
        afterSaleRejected: 'status-default',
        afterSaleProcessing: 'status-info',
        afterSaleCompleted: 'status-success',
        afterSaleClosed: 'status-default'
      }
      return statusClassMap[status] || ''
    },

    getTotalProducts(products = []) {
      return products.reduce((total, product) => total + Number(product.quantity || 0), 0)
    },

    canCancel(status) {
      return ['unpaid', 'unshipped'].includes(status)
    },

    canPay(status) {
      return status === 'unpaid'
    },

    canConfirm(status) {
      return status === 'shipped'
    },

    canReview(status) {
      return status === 'completed'
    },

    canViewReviewDetail(status) {
      return status === 'reviewed'
    },

    getReviewActionText(order = {}) {
      return order.hasUserComment ? '追加评价' : '评价商品'
    },

    canReturn(status) {
      return ['unshipped', 'shipped', 'completed'].includes(status)
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

            .product-comment {
              display: flex;
              margin-top: 12rpx;
              font-size: 24rpx;
              line-height: 1.5;

              .comment-label {
                color: #666;
                flex-shrink: 0;
              }

              .comment-content {
                color: #333;
                word-break: break-all;
              }
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
