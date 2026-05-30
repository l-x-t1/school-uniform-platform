<template>
  <view class="container">
    <view class="card">
      <view class="section-title">选择退换商品</view>
      <view
        v-for="(product, index) in products"
        :key="index"
        class="product-card"
        :class="{ active: selectedProductId === product.id }"
        @click="selectProduct(product.id)"
      >
        <image class="product-image" :src="product.image" mode="aspectFill"></image>
        <view class="product-info">
          <text class="product-name">{{ product.name }}</text>
          <text class="product-spec">{{ product.size }} | {{ product.color }}</text>
          <text class="product-price">¥{{ product.price }} x{{ product.quantity }}</text>
        </view>
      </view>
    </view>

    <view class="card">
      <view class="section-title">申请类型</view>
      <view class="type-row">
        <view
          v-for="item in applyTypeOptions"
          :key="item.value"
          class="type-item"
          :class="{ active: form.applyType === item.value }"
          @click="form.applyType = item.value"
        >
          {{ item.label }}
        </view>
      </view>
    </view>

    <view class="card">
      <view class="section-title">申请数量</view>
      <input v-model="form.applyNum" class="input" type="number" placeholder="请输入申请数量" />
    </view>

    <view v-if="form.applyType === '3'" class="card">
      <view class="section-title">换货目标尺码</view>
      <input v-model="form.targetSizeName" class="input" placeholder="请输入目标尺码，如 170" />
    </view>

    <view class="card">
      <view class="section-title">退货物流单号</view>
      <input v-model="form.returnTrackingNo" class="input" maxlength="100" placeholder="请填写退货物流单号" />
    </view>

    <view class="card">
      <view class="section-title">申请原因</view>
      <textarea
        v-model="form.reason"
        class="textarea"
        maxlength="200"
        placeholder="请填写退换货原因"
      />
      <view class="count">{{ form.reason.length }}/200</view>
    </view>

    <view class="submit-bar">
      <button class="submit-btn" :disabled="isSubmitting || !selectedProduct" @click="submitAfterSale">
        {{ isSubmitting ? '提交中...' : '提交申请' }}
      </button>
    </view>
  </view>
</template>

<script>
import { getOrder } from '@/api/order'
import { createAfterSale } from '@/api/after-sale'

export default {
  data() {
    return {
      orderId: '',
      isSubmitting: false,
      order: null,
      products: [],
      selectedProductId: null,
      applyTypeOptions: [
        { value: '2', label: '退货退款' },
        { value: '3', label: '换货' }
      ],
      form: {
        applyType: '2',
        applyNum: '1',
        targetSizeName: '',
        returnTrackingNo: '',
        reason: ''
      }
    }
  },
  computed: {
    selectedProduct() {
      return this.products.find((item) => String(item.id) === String(this.selectedProductId)) || null
    }
  },
  onLoad(options) {
    this.orderId = options.orderId || ''
    if (this.orderId) {
      this.fetchOrder()
    }
  },
  methods: {
    fetchOrder() {
      uni.showLoading({ title: '加载中...' })
      getOrder(this.orderId)
        .then((res) => {
          const order = res.data || res
          this.order = order
          this.products = order.products || []
          if (this.products.length) {
            this.selectedProductId = this.products[0].id
            this.form.applyNum = String(this.products[0].quantity || 1)
          }
          uni.hideLoading()
        })
        .catch((error) => {
          uni.hideLoading()
          uni.showToast({
            title: error.message || '订单信息加载失败',
            icon: 'none'
          })
        })
    },
    selectProduct(productId) {
      this.selectedProductId = productId
      const product = this.products.find((item) => String(item.id) === String(productId))
      if (product) {
        this.form.applyNum = String(product.quantity || 1)
      }
    },
    submitAfterSale() {
      if (!this.order || !this.selectedProduct) {
        uni.showToast({
          title: '请选择商品',
          icon: 'none'
        })
        return
      }
      if (!this.form.reason.trim()) {
        uni.showToast({
          title: '请填写申请原因',
          icon: 'none'
        })
        return
      }
      if (!this.form.returnTrackingNo.trim()) {
        uni.showToast({
          title: '请填写退货物流单号',
          icon: 'none'
        })
        return
      }

      const maxNum = Number(this.selectedProduct.quantity || 1)
      const applyNum = Number(this.form.applyNum || 1)
      if (!applyNum || applyNum < 1 || applyNum > maxNum) {
        uni.showToast({
          title: `申请数量需在1-${maxNum}之间`,
          icon: 'none'
        })
        return
      }

      this.isSubmitting = true
      uni.showLoading({ title: '提交中...' })
      createAfterSale(this.order, this.selectedProduct, {
        ...this.form,
        applyNum
      })
        .then(() => {
          uni.hideLoading()
          uni.showToast({
            title: '申请已提交',
            icon: 'success'
          })
          setTimeout(() => {
            uni.redirectTo({
              url: '/pages/order/order-list?status=afterSale'
            })
          }, 800)
        })
        .catch((error) => {
          uni.hideLoading()
          uni.showToast({
            title: error.message || '提交申请失败',
            icon: 'none'
          })
        })
        .finally(() => {
          this.isSubmitting = false
        })
    }
  }
}
</script>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  padding: 24rpx 24rpx 160rpx;
  background: #f5f6f8;
  box-sizing: border-box;
}

.card {
  background: #fff;
  border-radius: 16rpx;
  padding: 28rpx;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #222;
  margin-bottom: 20rpx;
}

.product-card {
  display: flex;
  align-items: center;
  padding: 20rpx;
  border: 2rpx solid #eef1f5;
  border-radius: 14rpx;
  margin-bottom: 16rpx;

  &.active {
    border-color: #30a3ff;
    background: #f3f9ff;
  }

  &:last-child {
    margin-bottom: 0;
  }
}

.product-image {
  width: 132rpx;
  height: 132rpx;
  border-radius: 12rpx;
  margin-right: 18rpx;
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
  margin-bottom: 10rpx;
}

.product-spec,
.product-price {
  display: block;
  font-size: 24rpx;
  color: #777;
}

.type-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.type-item {
  padding: 14rpx 28rpx;
  border-radius: 999rpx;
  border: 2rpx solid #d9dee8;
  color: #666;
  font-size: 26rpx;

  &.active {
    color: #30a3ff;
    border-color: #30a3ff;
    background: #f3f9ff;
  }
}

.input {
  height: 80rpx;
  font-size: 28rpx;
  color: #333;
}

.textarea {
  width: 100%;
  min-height: 220rpx;
  font-size: 28rpx;
  color: #333;
  line-height: 1.6;
}

.count {
  text-align: right;
  font-size: 22rpx;
  color: #999;
  margin-top: 12rpx;
}

.submit-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 20rpx 24rpx calc(20rpx + env(safe-area-inset-bottom));
  background: #fff;
  box-shadow: 0 -6rpx 18rpx rgba(0, 0, 0, 0.06);
}

.submit-btn {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 44rpx;
  background: #30a3ff;
  color: #fff;
  font-size: 30rpx;

  &[disabled] {
    background: #b8dfff;
  }
}
</style>
