<template>
  <view class="container">
    <view class="order-card">
      <view class="card-title">订单信息</view>
      <view class="info-row">
        <text class="label">订单编号</text>
        <text class="value">{{ order.orderNo || '-' }}</text>
      </view>
      <view class="info-row">
        <text class="label">订单金额</text>
        <text class="value amount">￥{{ order.totalAmount || '0.00' }}</text>
      </view>
      <view class="product" v-if="firstProduct.name">
        <image class="product-image" :src="firstProduct.image" mode="aspectFill"></image>
        <view class="product-info">
          <text class="product-name">{{ firstProduct.name }}</text>
          <text class="product-spec">{{ firstProduct.size || '-' }} | 数量 x{{ firstProduct.quantity || 1 }}</text>
        </view>
      </view>
    </view>

    <view class="form-card">
      <view class="form-item">
        <text class="form-label">申请类型</text>
        <picker :range="applyTypeOptions" range-key="label" @change="onApplyTypeChange">
          <view class="picker-value">
            <text>{{ currentApplyType.label }}</text>
            <uni-icons type="right" size="16" color="#999"></uni-icons>
          </view>
        </picker>
      </view>

      <view class="form-item" v-if="form.applyType === '3'">
        <text class="form-label">换货尺码</text>
        <input class="input" v-model="form.targetSizeName" placeholder="请输入要更换的尺码" />
      </view>

      <view class="form-item">
        <text class="form-label">申请数量</text>
        <input class="input" type="number" v-model="form.applyNum" placeholder="请输入数量" />
      </view>

      <view class="form-item">
        <text class="form-label">申请金额</text>
        <input class="input" type="digit" v-model="form.applyAmount" placeholder="请输入金额" />
      </view>

      <view class="form-item column">
        <text class="form-label">申请原因</text>
        <textarea class="textarea" v-model="form.reason" maxlength="200" placeholder="请填写退换原因" />
        <text class="count">{{ form.reason.length }}/200</text>
      </view>
    </view>

    <view class="notice">
      <text>提交后由学校或供应商在后台审核处理。</text>
    </view>

    <button class="submit-btn" :disabled="submitting" @click="submitApply">
      {{ submitting ? '提交中...' : '提交申请' }}
    </button>
  </view>
</template>

<script>
import config from '@/config'
import { getOrder } from '@/api/order'
import { createAfterSale } from '@/api/afterSale'

export default {
  data() {
    return {
      orderId: '',
      submitting: false,
      order: {},
      applyTypeOptions: [
        { label: '仅退款', value: '1' },
        { label: '退货退款', value: '2' },
        { label: '换货', value: '3' }
      ],
      form: {
        applyType: '2',
        targetSizeName: '',
        applyNum: 1,
        applyAmount: '',
        reason: ''
      }
    }
  },
  computed: {
    currentApplyType() {
      return this.applyTypeOptions.find((item) => item.value === this.form.applyType) || this.applyTypeOptions[1]
    },
    firstProduct() {
      const products = this.order.products || this.order.items || this.order.orderItems || []
      return products[0] || {}
    }
  },
  onLoad(options) {
    this.orderId = options.id || options.orderId || ''
    this.order = {
      id: this.orderId,
      orderNo: options.orderNo ? decodeURIComponent(options.orderNo) : '',
      totalAmount: options.amount ? decodeURIComponent(options.amount) : '0.00',
      payAmount: options.amount ? decodeURIComponent(options.amount) : '0.00'
    }
    this.form.applyAmount = this.order.payAmount || this.order.totalAmount || '0.00'
    if (this.orderId) {
      this.loadOrder()
    }
  },
  methods: {
    loadOrder() {
      getOrder(this.orderId)
        .then((res) => {
          const detail = res.data || res || {}
          this.order = {
            ...this.order,
            ...detail,
            orderNo: detail.orderNo || this.order.orderNo || '',
            totalAmount: detail.totalAmount || detail.payAmount || this.order.totalAmount || '0.00',
            payAmount: detail.payAmount || detail.totalAmount || this.order.payAmount || '0.00'
          }
          if (!this.form.applyAmount) {
            this.form.applyAmount = this.order.payAmount || this.order.totalAmount || '0.00'
          }
        })
        .catch((error) => {
          const localOrders = uni.getStorageSync('devOrders') || {}
          if (config.enableMockFallback && localOrders[this.orderId]) {
            this.order = localOrders[this.orderId]
            this.form.applyAmount = this.order.payAmount || this.order.totalAmount || '0.00'
            return
          }
          uni.showToast({
            title: error.message || '订单加载失败',
            icon: 'none'
          })
        })
    },
    onApplyTypeChange(event) {
      const index = Number(event.detail.value)
      this.form.applyType = this.applyTypeOptions[index].value
    },
    validateForm() {
      if (!this.orderId) {
        return '缺少订单信息'
      }
      if (!this.form.applyNum || Number(this.form.applyNum) <= 0) {
        return '请输入申请数量'
      }
      if (this.form.applyType !== '3' && (!this.form.applyAmount || Number(this.form.applyAmount) < 0)) {
        return '请输入申请金额'
      }
      if (this.form.applyType === '3' && !this.form.targetSizeName.trim()) {
        return '请输入换货尺码'
      }
      if (!this.form.reason.trim()) {
        return '请填写申请原因'
      }
      return ''
    },
    buildPayload() {
      const product = this.firstProduct
      return {
        orderId: Number(this.order.id || this.orderId),
        orderItemId: product.orderItemId || product.id,
        orderNo: this.order.orderNo || '',
        orderType: this.order.orderType || '2',
        schoolId: this.order.schoolId,
        supplierId: this.order.supplierId,
        userId: this.order.userId,
        userName: this.order.userName,
        studentName: this.order.studentName,
        applyType: this.form.applyType,
        targetSizeName: this.form.applyType === '3' ? this.form.targetSizeName.trim() : '',
        reason: this.form.reason.trim(),
        applyNum: Number(this.form.applyNum),
        applyAmount: this.form.applyType === '3' ? 0 : Number(this.form.applyAmount || 0),
        deptId: this.order.deptId || this.order.schoolId
      }
    },
    submitApply() {
      const message = this.validateForm()
      if (message) {
        uni.showToast({ title: message, icon: 'none' })
        return
      }

      this.submitting = true
      uni.showLoading({ title: '提交中' })
      createAfterSale(this.buildPayload())
        .then(() => {
          uni.hideLoading()
          uni.showToast({ title: '申请已提交', icon: 'success' })
          setTimeout(() => {
            uni.navigateBack()
          }, 800)
        })
        .catch((error) => {
          uni.hideLoading()
          uni.showToast({
            title: error.message || '提交失败',
            icon: 'none'
          })
        })
        .finally(() => {
          this.submitting = false
        })
    }
  }
}
</script>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  padding: 24rpx;
  background: #f5f7fb;
  box-sizing: border-box;
}

.order-card,
.form-card {
  background: #fff;
  border-radius: 12rpx;
  padding: 28rpx;
  margin-bottom: 24rpx;
}

.card-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #222;
  margin-bottom: 20rpx;
}

.info-row,
.form-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 76rpx;
  border-bottom: 1rpx solid #f1f1f1;
}

.info-row:last-child,
.form-item:last-child {
  border-bottom: none;
}

.label,
.form-label {
  font-size: 28rpx;
  color: #555;
}

.value {
  max-width: 470rpx;
  font-size: 28rpx;
  color: #222;
  text-align: right;
  word-break: break-all;
}

.amount {
  color: #f04438;
  font-weight: 600;
}

.product {
  display: flex;
  margin-top: 24rpx;
  padding-top: 24rpx;
  border-top: 1rpx solid #f1f1f1;
}

.product-image {
  width: 132rpx;
  height: 132rpx;
  border-radius: 8rpx;
  background: #f2f2f2;
  margin-right: 20rpx;
}

.product-info {
  flex: 1;
  min-width: 0;
}

.product-name {
  display: block;
  font-size: 28rpx;
  color: #222;
  line-height: 1.4;
}

.product-spec {
  display: block;
  margin-top: 12rpx;
  font-size: 24rpx;
  color: #888;
}

.picker-value {
  display: flex;
  align-items: center;
  font-size: 28rpx;
  color: #222;
}

.input {
  flex: 1;
  height: 76rpx;
  font-size: 28rpx;
  color: #222;
  text-align: right;
}

.column {
  align-items: flex-start;
  flex-direction: column;
  padding: 22rpx 0;
}

.textarea {
  width: 100%;
  height: 180rpx;
  margin-top: 18rpx;
  padding: 20rpx;
  box-sizing: border-box;
  background: #f8f9fb;
  border-radius: 8rpx;
  font-size: 28rpx;
  color: #222;
}

.count {
  align-self: flex-end;
  margin-top: 8rpx;
  font-size: 24rpx;
  color: #999;
}

.notice {
  padding: 0 8rpx 24rpx;
  font-size: 24rpx;
  color: #888;
  line-height: 1.5;
}

.submit-btn {
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 44rpx;
  background: #3B7AFF;
  color: #fff;
  font-size: 30rpx;
}

.submit-btn[disabled] {
  background: #a9c4ff;
  color: #fff;
}
</style>
