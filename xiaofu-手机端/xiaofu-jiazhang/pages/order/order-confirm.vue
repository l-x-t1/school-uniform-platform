<template>
  <view class="container">
    <view class="student-card section-card" @click="goBindStudent">
      <view>
        <text class="card-title">购买学生</text>
        <text class="student-name">{{ student.name }}</text>
        <text class="student-desc">{{ student.school }} {{ student.grade }} {{ student.className }}</text>
      </view>
      <uni-icons type="right" size="16" color="#999"></uni-icons>
    </view>

    <view class="address-card section-card" @click="goAddress">
      <view>
        <text class="card-title">收货信息</text>
        <text class="receiver">{{ address.receiver }} {{ address.phone }}</text>
        <text class="address-text">{{ address.fullAddress }}</text>
      </view>
      <uni-icons type="right" size="16" color="#999"></uni-icons>
    </view>

    <view class="goods-card section-card">
      <view class="section-title">商品清单</view>
      <view class="goods-item" v-for="item in items" :key="(item.uniformId || item.id) + '-' + item.size">
        <image class="goods-image" :src="item.image" mode="aspectFill"></image>
        <view class="goods-info">
          <text class="goods-name">{{ item.name }}</text>
          <text class="goods-spec">{{ item.spec || item.color }} {{ item.size }}</text>
        </view>
        <view class="goods-price">
          <text class="price">¥{{ item.price }}</text>
          <text class="quantity">x{{ item.quantity }}</text>
        </view>
      </view>
    </view>

    <view class="remark-card section-card">
      <text class="section-title">订单备注</text>
      <input class="remark-input" v-model="remark" placeholder="选填，可填写配送或尺码备注" />
    </view>

    <view class="summary-card section-card">
      <view class="summary-row">
        <text>商品金额</text>
        <text>¥{{ goodsAmount }}</text>
      </view>
      <view class="summary-row">
        <text>运费</text>
        <text>¥{{ freightAmount }}</text>
      </view>
      <view class="summary-row total">
        <text>实付款</text>
        <text>¥{{ payAmount }}</text>
      </view>
    </view>

    <view class="bottom-bar">
      <view class="pay-total">
        <text>合计：</text>
        <text class="pay-price">¥{{ payAmount }}</text>
      </view>
      <button class="submit-btn" :disabled="submitting || items.length === 0" @click="submitOrder">提交订单</button>
    </view>
  </view>
</template>

<script>
import { createOrder } from '@/api/order'
import { getDefaultStudent } from '@/api/student'
import { listAddress } from '@/api/address'

export default {
  data() {
    return {
      source: '',
      submitting: false,
      remark: '',
      items: [],
      student: {
        id: '',
        name: '待绑定学生',
        school: '请先完善学生信息',
        grade: '',
        className: '',
        isPlaceholder: true
      },
      address: {
        id: '',
        receiver: '家长',
        phone: uni.getStorageSync('phone') || '',
        fullAddress: '请完善收货地址',
        isPlaceholder: true
      },
      freightAmount: '0.00'
    }
  },
  computed: {
    goodsAmount() {
      return this.items
        .reduce((sum, item) => sum + Number(item.price || 0) * Number(item.quantity || 1), 0)
        .toFixed(2);
    },
    payAmount() {
      return (Number(this.goodsAmount) + Number(this.freightAmount)).toFixed(2);
    }
  },
  onLoad(options) {
    this.source = options.source || '';
    this.items = (uni.getStorageSync('confirmOrderItems') || []).map((item) => ({
      ...item,
      quantity: Number(item.quantity || 1)
    }));
    this.loadDefaultStudent();
    this.loadDefaultAddress();
  },
  onShow() {
    const selectedAddress = uni.getStorageSync('selectedAddress');
    if (selectedAddress) {
      this.address = this.normalizeAddress(selectedAddress);
    }
  },
  methods: {
    normalizeAddress(item = {}) {
      return {
        id: item.id || item.addressId || '',
        receiver: item.receiver || item.name || item.userName || '家长',
        phone: item.phone || item.mobile || '',
        fullAddress: item.fullAddress || `${item.province || ''}${item.city || ''}${item.district || ''}${item.detailAddress || item.address || ''}` || '请完善收货地址',
        tag: item.tag || item.label || '',
        isPlaceholder: false
      };
    },
    loadDefaultStudent() {
      getDefaultStudent()
        .then((res) => {
          const data = res.data || res;
          if (!data || (!data.id && !data.studentId)) {
            return;
          }
          this.student = {
            id: data.id || data.studentId,
            name: data.name || data.studentName || this.student.name,
            school: data.school || data.schoolName || this.student.school,
            grade: data.grade || data.gradeName || '',
            className: data.className || data.class || '',
            isPlaceholder: false
          };
          uni.setStorageSync('selectedStudent', this.student);
        });
    },
    loadDefaultAddress() {
      const selectedAddress = uni.getStorageSync('selectedAddress');
      if (selectedAddress) {
        this.address = this.normalizeAddress(selectedAddress);
        return;
      }

      listAddress()
        .then((res) => {
          const rows = res.rows || res.data || res.list || [];
          const defaultAddress = rows.find((item) => item.isDefault || item.defaultFlag === 1) || rows[0];
          if (defaultAddress) {
            this.address = this.normalizeAddress(defaultAddress);
            uni.setStorageSync('selectedAddress', this.address);
          }
        });
    },
    goBindStudent() {
      uni.navigateTo({
        url: '/pages/user/student-info'
      });
    },
    goAddress() {
      uni.navigateTo({
        url: '/pages/user/address-list?choose=1'
      });
    },
    submitOrder() {
      if (!this.items.length) {
        uni.showToast({
          title: '请选择商品',
          icon: 'none'
        });
        return;
      }

      if (!this.student.id || this.student.isPlaceholder) {
        uni.showToast({
          title: '请先绑定购买学生',
          icon: 'none'
        });
        setTimeout(() => {
          this.goBindStudent();
        }, 500);
        return;
      }

      if (!this.address.id || this.address.isPlaceholder || !this.address.phone || this.address.fullAddress === '请完善收货地址') {
        uni.showToast({
          title: '请先完善收货地址',
          icon: 'none'
        });
        setTimeout(() => {
          this.goAddress();
        }, 500);
        return;
      }

      this.submitting = true;
      uni.showLoading({ title: '提交中...' });

      const payload = {
        source: this.source,
        studentId: this.student.id,
        addressId: this.address.id,
        remark: this.remark,
        items: this.items.map((item) => ({
          uniformId: item.uniformId || item.id,
          skuId: item.skuId,
          size: item.size,
          quantity: item.quantity
        }))
      };

      createOrder(payload)
        .then((res) => {
          const data = res.data || res;
          const orderId = data.id || data.orderId;
          uni.hideLoading();
          if (this.source === 'cart') {
            const localCart = uni.getStorageSync('cartList') || [];
            const orderedKeys = this.items.map((item) => `${item.uniformId || item.id}-${item.size}`);
            uni.setStorageSync('cartList', localCart.filter((item) => !orderedKeys.includes(`${item.uniformId || item.id}-${item.size}`)));
          }
          uni.removeStorageSync('confirmOrderItems');
          uni.showToast({
            title: '订单已提交',
            icon: 'success'
          });
          setTimeout(() => {
            uni.redirectTo({
              url: `/pages/payment/payment?id=${orderId}`
            });
          }, 600);
        })
        .catch((error) => {
          uni.hideLoading();
          uni.showToast({
            title: error.message || '提交订单失败',
            icon: 'none'
          });
        })
        .finally(() => {
          this.submitting = false;
        });
    }
  }
}
</script>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  padding: 20rpx 24rpx 150rpx;
  background-color: #f5f6f8;
  box-sizing: border-box;
}

.section-card {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 28rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 6rpx 18rpx rgba(20, 40, 80, 0.04);
}

.student-card,
.address-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-title,
.section-title {
  display: block;
  font-size: 26rpx;
  color: #777;
  margin-bottom: 14rpx;
}

.student-name,
.receiver {
  display: block;
  font-size: 32rpx;
  color: #222;
  font-weight: 600;
  margin-bottom: 10rpx;
}

.student-desc,
.address-text {
  display: block;
  font-size: 26rpx;
  color: #666;
  line-height: 1.5;
}

.goods-item {
  display: flex;
  padding: 22rpx 0;
  border-bottom: 1rpx solid #f0f0f0;

  &:last-child {
    border-bottom: none;
  }
}

.goods-image {
  width: 150rpx;
  height: 150rpx;
  border-radius: 12rpx;
  background-color: #f3f3f3;
  margin-right: 22rpx;
}

.goods-info {
  flex: 1;
  min-width: 0;
}

.goods-name {
  display: block;
  font-size: 30rpx;
  color: #222;
  line-height: 1.4;
  margin-bottom: 14rpx;
}

.goods-spec {
  display: block;
  font-size: 24rpx;
  color: #777;
}

.goods-price {
  width: 120rpx;
  text-align: right;
}

.price {
  display: block;
  font-size: 28rpx;
  color: #e64340;
  font-weight: 600;
}

.quantity {
  display: block;
  font-size: 24rpx;
  color: #888;
  margin-top: 12rpx;
}

.remark-card {
  display: flex;
  align-items: center;
}

.remark-input {
  flex: 1;
  height: 72rpx;
  font-size: 28rpx;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  font-size: 28rpx;
  color: #555;
  margin-bottom: 18rpx;

  &:last-child {
    margin-bottom: 0;
  }

  &.total {
    color: #222;
    font-weight: 600;
  }
}

.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18rpx 24rpx calc(18rpx + env(safe-area-inset-bottom));
  background-color: #fff;
  box-shadow: 0 -6rpx 20rpx rgba(0, 0, 0, 0.06);
}

.pay-total {
  font-size: 28rpx;
  color: #333;
}

.pay-price {
  color: #e64340;
  font-size: 38rpx;
  font-weight: 700;
}

.submit-btn {
  width: 240rpx;
  height: 82rpx;
  line-height: 82rpx;
  border-radius: 42rpx;
  background-color: #30a3ff;
  color: #fff;
  font-size: 30rpx;

  &[disabled] {
    background-color: #b8dfff;
  }
}
</style>
