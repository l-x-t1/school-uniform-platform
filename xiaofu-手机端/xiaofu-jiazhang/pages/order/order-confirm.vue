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
          <text class="goods-spec">{{ currentOrderTypeText }} {{ item.spec || item.color }} {{ item.size }}</text>
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
import config from '@/config'
import { deleteCart } from '@/api/cart'
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
        .reduce((sum, item) => sum + Number(item.unitPrice || item.price || 0) * Number(item.quantity || 1), 0)
        .toFixed(2);
    },
    payAmount() {
      return (Number(this.goodsAmount) + Number(this.freightAmount)).toFixed(2);
    },
    currentOrderType() {
      return this.items[0]?.orderType || this.items[0]?.saleType || '1';
    },
    currentOrderTypeText() {
      return String(this.currentOrderType) === '2' ? '零售购买' : '统购购买';
    }
  },
  onLoad(options) {
    this.source = options.source || '';
    this.items = (uni.getStorageSync('confirmOrderItems') || []).map((item) => ({
      ...item,
      quantity: Number(item.quantity || 1),
      unitPrice: item.unitPrice || item.price || '0.00',
      price: item.price || item.unitPrice || '0.00',
      buyNum: Number(item.buyNum || item.quantity || 1),
      orderType: item.orderType || item.saleType || '1',
      saleType: item.orderType || item.saleType || '1'
    }));
    this.loadDefaultStudent();
    this.loadDefaultAddress();
  },
  onShow() {
    this.syncStudentFromStorage();
    const selectedAddress = uni.getStorageSync('selectedAddress');
    if (selectedAddress) {
      this.address = this.normalizeAddress(selectedAddress);
    }
  },
  methods: {
    normalizeStudent(item = {}) {
      return {
        id: item.id || item.studentId || '',
        studentId: item.studentId || item.id || '',
        name: item.name || item.studentName || '待绑定学生',
        schoolId: item.schoolId || item.shcoolId || item.deptId || item.sysDepts?.deptId || '',
        shcoolId: item.shcoolId || item.schoolId || item.deptId || item.sysDepts?.deptId || '',
        deptId: item.deptId || item.schoolId || item.shcoolId || item.sysDepts?.deptId || '',
        school: item.school || item.schoolName || item.sysDepts?.deptName || '请先完善学生信息',
        schoolName: item.schoolName || item.school || item.sysDepts?.deptName || '请先完善学生信息',
        grade: item.grade || item.gradeName || '',
        gradeName: item.gradeName || item.grade || '',
        className: item.className || item.class || '',
        isPlaceholder: !(item.id || item.studentId)
      };
    },
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
    applyStudent(student, writeBack = false) {
      if (!student || !student.id) {
        return false;
      }
      this.student = this.normalizeStudent(student);
      if (writeBack) {
        uni.setStorageSync('selectedStudent', this.student);
      }
      return true;
    },
    resolveCurrentStudent() {
      const rawSelectedStudent = uni.getStorageSync('selectedStudent') || {};
      const selectedStudent = rawSelectedStudent && (rawSelectedStudent.id || rawSelectedStudent.studentId)
        ? this.normalizeStudent(rawSelectedStudent)
        : null;
      const studentList = (uni.getStorageSync('studentList') || []).map((item) => this.normalizeStudent(item));
      const itemStudentId = this.items.find((item) => item.studentId || item.userStudentId)?.studentId
        || this.items.find((item) => item.studentId || item.userStudentId)?.userStudentId
        || '';

      if (itemStudentId) {
        const matchedItemStudent = studentList.find((item) => String(item.id || item.studentId || '') === String(itemStudentId));
        if (matchedItemStudent) {
          return matchedItemStudent;
        }
        if (selectedStudent && String(selectedStudent.id || selectedStudent.studentId || '') === String(itemStudentId)) {
          return selectedStudent;
        }
      }

      if (selectedStudent) {
        const matchedSelectedStudent = studentList.find((item) =>
          String(item.id || item.studentId || '') === String(selectedStudent.id || selectedStudent.studentId || '')
        );
        return matchedSelectedStudent || selectedStudent;
      }

      return studentList[0] || null;
    },
    syncStudentFromStorage(writeBack = false) {
      const student = this.resolveCurrentStudent();
      return this.applyStudent(student, writeBack);
    },
    loadDefaultStudent() {
      if (this.syncStudentFromStorage()) {
        return;
      }
      getDefaultStudent()
        .then((res) => {
          const data = res.data || res;
          if (!data || (!data.id && !data.studentId && config.enableMockFallback)) {
            this.useLocalStudent();
            return;
          }
          this.applyStudent(data, true);
        })
        .catch(() => {
          this.useLocalStudent();
        });
    },
    useLocalStudent() {
      if (!config.enableMockFallback) return;
      this.syncStudentFromStorage(true);
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
            return;
          }
          this.useLocalAddress();
        })
        .catch(() => {
          this.useLocalAddress();
        });
    },
    useLocalAddress() {
      if (!config.enableMockFallback) return;
      const local = uni.getStorageSync('addressList') || [];
      const defaultAddress = local.find((item) => item.isDefault) || local[0];
      if (defaultAddress) {
        this.address = this.normalizeAddress(defaultAddress);
        uni.setStorageSync('selectedAddress', this.address);
      }
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

      const orderTypes = [...new Set(this.items.map((item) => String(item.orderType || item.saleType || '1')))];
      if (orderTypes.length > 1) {
        uni.showToast({
          title: '统购和零售商品请分开提交订单',
          icon: 'none'
        });
        return;
      }

      const schoolIds = [...new Set(this.items.map((item) => String(item.schoolId || item.deptId || '')))];
      if (schoolIds.length > 1) {
        uni.showToast({
          title: '不同学校商品请分开提交订单',
          icon: 'none'
        });
        return;
      }

      const supplierIds = [...new Set(this.items.map((item) => String(item.supplierId || '')))];
      if (supplierIds.length > 1) {
        uni.showToast({
          title: '不同供应商商品请分开提交订单',
          icon: 'none'
        });
        return;
      }

      if (this.items.some((item) => !item.supplierId)) {
        uni.showToast({
          title: '商品供应商信息缺失，请重新选择商品后再提交',
          icon: 'none'
        });
        return;
      }

      this.submitting = true;
      uni.showLoading({ title: '提交中...' });

      const payload = {
        source: this.source,
        studentId: this.student.id,
        addressId: this.address.id,
        receiveType: '2',
        freightAmount: this.freightAmount,
        remark: this.remark,
        orderType: this.currentOrderType,
        items: this.items.map((item) => ({
          id: item.id,
          uniformId: item.uniformId || item.goodsId || item.id,
          goodsId: item.goodsId || item.uniformId || item.id,
          goodsCode: item.goodsCode || '',
          skuId: item.skuId || item.sizeId,
          sizeId: item.sizeId || item.skuId,
          size: item.size,
          sizeName: item.sizeName || item.size,
          quantity: Number(item.quantity || 1),
          buyNum: Number(item.quantity || 1),
          price: item.price || item.unitPrice || '0.00',
          unitPrice: item.unitPrice || item.price || '0.00',
          image: item.image || item.mainImage || '',
          mainImage: item.mainImage || item.image || '',
          name: item.name,
          goodsName: item.name || item.goodsName,
          supplierId: item.supplierId || null,
          supplierName: item.supplierName || '',
          schoolId: item.schoolId || null,
          schoolName: item.schoolName || this.student.school,
          deptId: item.deptId || item.schoolId || null,
          noticeId: item.noticeId || null,
          orderType: item.orderType || item.saleType || '1',
          saleType: item.orderType || item.saleType || '1'
        }))
      };

      createOrder(payload)
        .catch((error) => {
          if (!config.enableMockFallback) return Promise.reject(error);
          const devOrderId = `DEV${Date.now()}`;
          return {
            data: {
              id: devOrderId,
              orderId: devOrderId
            }
          };
        })
        .then(async (res) => {
          const data = res.data || res;
          const orderId = data.id || data.orderId;
          if (!orderId) {
            throw new Error('订单创建成功，但未获取到订单编号，请稍后在订单列表中查看');
          }
          const localOrder = {
            id: orderId,
            orderNo: data.orderNo || `XF${Date.now()}`,
            status: 'unpaid',
            createTime: this.getCurrentTime(),
            orderType: this.currentOrderType,
            orderTypeText: this.currentOrderTypeText,
            payStatus: '0',
            orderStatus: '0',
            schoolId: payload.items[0]?.schoolId || '',
            schoolName: payload.items[0]?.schoolName || this.student.school,
            supplierId: payload.items[0]?.supplierId || null,
            supplierName: payload.items[0]?.supplierName || '',
            studentId: this.student.id,
            studentName: this.student.name,
            gradeName: this.student.grade,
            className: this.student.className,
            addressId: this.address.id,
            receiveType: '2',
            receiverName: this.address.receiver,
            receiverPhone: this.address.phone,
            receiverAddress: this.address.fullAddress,
            productsAmount: this.goodsAmount,
            shippingFee: this.freightAmount,
            discount: '0.00',
            totalAmount: this.payAmount,
            payAmount: this.payAmount,
            refundAmount: '0.00',
            remark: this.remark,
            student: this.student,
            address: {
              name: this.address.receiver,
              phone: this.address.phone,
              tag: this.address.tag || '默认',
              fullAddress: this.address.fullAddress
            },
            products: this.items.map((item) => ({
              id: item.id,
              uniformId: item.uniformId || item.goodsId || item.id,
              goodsId: item.goodsId || item.uniformId || item.id,
              skuId: item.skuId || item.sizeId,
              sizeId: item.sizeId || item.skuId,
              name: item.name,
              goodsName: item.name || item.goodsName,
              image: item.image || item.mainImage,
              mainImage: item.mainImage || item.image,
              price: item.price || item.unitPrice,
              unitPrice: item.unitPrice || item.price,
              size: item.size,
              sizeName: item.sizeName || item.size,
              color: item.color || item.spec || '默认款',
              quantity: Number(item.quantity || 1),
              buyNum: Number(item.quantity || 1),
              orderType: item.orderType || item.saleType || '1',
              saleType: item.orderType || item.saleType || '1',
              itemAmount: (Number(item.unitPrice || item.price || 0) * Number(item.quantity || 1)).toFixed(2)
            }))
          };
          localOrder.items = localOrder.products;
          localOrder.orderItems = localOrder.products;
          localOrder.xfptOrderItemList = localOrder.products;
          const localOrders = uni.getStorageSync('devOrders') || {};
          localOrders[orderId] = localOrder;
          uni.setStorageSync('devOrders', localOrders);
          uni.hideLoading();
          if (this.source === 'cart') {
            const localCart = uni.getStorageSync('cartList') || [];
            const orderedCartIds = [...new Set(this.items.map((item) => item.id).filter((id) => id !== undefined && id !== null && id !== ''))];
            const orderedKeys = this.items.map((item) => `${item.uniformId || item.id}-${item.size}-${item.orderType || item.saleType || '1'}`);
            if (orderedCartIds.length) {
              try {
                await deleteCart(orderedCartIds.join(','));
              } catch (error) {
                console.warn('delete cart after order failed', error);
              }
            }
            uni.setStorageSync('cartList', localCart.filter((item) => {
              if (orderedCartIds.length && orderedCartIds.includes(item.id)) {
                return false;
              }
              return !orderedKeys.includes(`${item.uniformId || item.id}-${item.size}-${item.orderType || item.saleType || '1'}`);
            }));
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
    },
    getCurrentTime() {
      const now = new Date();
      const pad = (num) => String(num).padStart(2, '0');
      return `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())} ${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`;
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
