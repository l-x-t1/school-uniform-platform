<template>
  <view class="container">
    <view class="card">
      <view class="section-title">选择评价商品</view>
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
      <view class="section-title">商品评分</view>
      <uni-rate :value="form.score" :size="28" @change="onScoreChange" />
    </view>

    <view class="card">
      <view class="section-title">评价内容</view>
      <textarea
        v-model="form.content"
        class="textarea"
        maxlength="200"
        placeholder="说说商品质量、尺码、穿着体验吧"
      />
      <view class="count">{{ form.content.length }}/200</view>
    </view>

    <view class="submit-bar">
      <button class="submit-btn" :disabled="isSubmitting || !selectedProduct" @click="submitReview">
        {{ isSubmitting ? '提交中...' : '提交评价' }}
      </button>
    </view>
  </view>
</template>

<script>
import { getOrder } from '@/api/order'
import { createComment } from '@/api/comment'

export default {
  data() {
    return {
      orderId: '',
      isSubmitting: false,
      order: null,
      products: [],
      selectedProductId: null,
      form: {
        score: 5,
        content: ''
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
    },
    onScoreChange(event) {
      this.form.score = Number(event.value || 5)
    },
    submitReview() {
      if (!this.order || !this.selectedProduct) {
        uni.showToast({
          title: '请选择商品',
          icon: 'none'
        })
        return
      }
      if (!this.form.content.trim()) {
        uni.showToast({
          title: '请填写评价内容',
          icon: 'none'
        })
        return
      }

      this.isSubmitting = true
      uni.showLoading({ title: '提交中...' })
      createComment(this.order, this.selectedProduct, this.form)
        .then(() => {
          uni.hideLoading()
          uni.showToast({
            title: '评价成功',
            icon: 'success'
          })
          setTimeout(() => {
            uni.redirectTo({
              url: '/pages/order/order-list?status=reviewed'
            })
          }, 800)
        })
        .catch((error) => {
          uni.hideLoading()
          uni.showToast({
            title: error.message || '评价提交失败',
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
