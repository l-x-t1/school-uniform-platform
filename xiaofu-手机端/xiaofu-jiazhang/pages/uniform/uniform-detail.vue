<template>
  <view class="container">
    <swiper class="swiper" :indicator-dots="uniform.images.length > 1" :autoplay="false" :duration="500">
      <swiper-item v-for="(item, index) in uniform.images" :key="index">
        <image :src="item" mode="aspectFill" class="swiper-image" @click="previewImage(uniform.images, index)"></image>
      </swiper-item>
    </swiper>

    <view class="info-section">
      <view class="purchase-mode" v-if="uniform.saleType === '3'">
        <text class="purchase-mode-label">购买方式</text>
        <view class="sale-type-switch">
          <view
            class="sale-type-pill"
            :class="{ active: purchaseType === '1' }"
            @click="switchPurchaseType('1')"
          >
            统购
          </view>
          <view
            class="sale-type-pill"
            :class="{ active: purchaseType === '2' }"
            @click="switchPurchaseType('2')"
          >
            零售
          </view>
        </view>
      </view>
      <view class="price-row">
        <text class="price">¥{{ displayPrice }}</text>
        <text class="original-price" v-if="displayOriginalPrice">¥{{ displayOriginalPrice }}</text>
      </view>
      <view class="name-row">
        <view class="name-wrap">
          <text class="name">{{ uniform.name }}</text>
          <text class="sub-name">{{ uniform.categoryName }} / {{ uniform.brandName }}</text>
        </view>
        <button class="share-btn" open-type="share">
          <uni-icons type="redo" size="20" color="#666"></uni-icons>
          <text>分享</text>
        </button>
      </view>
      <view class="tags-row">
        <text class="tag">{{ uniform.saleTypeText }}</text>
        <text class="tag">{{ uniform.genderTypeText }}</text>
        <text class="tag">供应商：{{ uniform.supplierName }}</text>
      </view>
      <view class="summary-card">
        <view class="summary-item">
          <text class="summary-label">商品编码</text>
          <text class="summary-value">{{ uniform.goodsCode || '--' }}</text>
        </view>
        <view class="summary-item">
          <text class="summary-label">上架时间</text>
          <text class="summary-value">{{ uniform.publishTime || '--' }}</text>
        </view>
        <view class="summary-item">
          <text class="summary-label">库存合计</text>
          <text class="summary-value">{{ uniform.totalStock }}</text>
        </view>
      </view>
    </view>

    <view class="specs-section section-card">
      <view class="section-header">
        <text class="title">选择尺码</text>
      </view>
      <view class="size-list">
        <view
          v-for="(size, index) in uniform.sizes"
          :key="index"
          class="size-item"
          :class="{ 'size-selected': selectedSizeId === size.id, 'size-disabled': !size.inStock }"
          @click="selectSize(size)"
        >
          <text class="size-code">{{ size.name }}</text>
          <text class="size-stock">{{ size.stockText }}</text>
        </view>
      </view>
      <view class="selected-size-card" v-if="selectedSize">
        <view class="selected-row">
          <text class="selected-label">购买方式</text>
          <text class="selected-value">{{ purchaseTypeText }}</text>
        </view>
        <view class="selected-row">
          <text class="selected-label">当前尺码</text>
          <text class="selected-value">{{ selectedSize.name }}</text>
        </view>
        <view class="selected-row" v-if="showRetailPrice">
          <text class="selected-label">零售价</text>
          <text class="selected-value price-highlight">¥{{ formatPrice(selectedSize.retailPrice) }}</text>
        </view>
        <view class="selected-row" v-if="showGroupPrice">
          <text class="selected-label">统购价</text>
          <text class="selected-value price-highlight">¥{{ formatPrice(selectedSize.groupPrice) }}</text>
        </view>
        <view class="selected-row">
          <text class="selected-label">库存</text>
          <text class="selected-value">{{ selectedSize.stockNum }}</text>
        </view>
        <view class="selected-row">
          <text class="selected-label">适用性别</text>
          <text class="selected-value">{{ selectedSize.genderTypeText }}</text>
        </view>
      </view>
    </view>

    <view class="detail-section section-card">
      <view class="section-title">商品信息</view>
      <view class="detail-content">
        <view class="spec-item" v-for="(spec, index) in uniform.specs" :key="index">
          <text class="spec-label">{{ spec.label }}</text>
          <text class="spec-value">{{ spec.value }}</text>
        </view>
      </view>
    </view>

    <view class="size-table-section section-card" v-if="uniform.sizes.length">
      <view class="section-title">尺码价格表</view>
      <view class="table-header">
        <text class="table-cell">尺码</text>
        <text class="table-cell" v-if="showRetailPrice">零售价</text>
        <text class="table-cell" v-if="showGroupPrice">统购价</text>
        <text class="table-cell">库存</text>
      </view>
      <view class="table-row" v-for="(size, index) in uniform.sizes" :key="index">
        <text class="table-cell">{{ size.name }}</text>
        <text class="table-cell" v-if="showRetailPrice">¥{{ formatPrice(size.retailPrice) }}</text>
        <text class="table-cell" v-if="showGroupPrice">¥{{ formatPrice(size.groupPrice) }}</text>
        <text class="table-cell">{{ size.stockNum }}</text>
      </view>
    </view>

    <view class="report-section section-card" @click="viewReport">
      <view class="report-header">
        <view class="section-title">质检报告</view>
        <view class="report-more">
          <text>查看详情</text>
          <uni-icons type="right" size="14" color="#999"></uni-icons>
        </view>
      </view>
      <view class="report-summary">
        <view class="report-tag report-pass">检测合格</view>
        <text class="report-desc">当前商品已完成质检归档，家长端可查看公开的质检结果与说明。</text>
      </view>
    </view>

    <view class="display-section section-card" v-if="uniform.displayImages.length">
      <view class="section-title">商品展示</view>
      <view class="display-images">
        <image
          v-for="(img, index) in uniform.displayImages"
          :key="index"
          :src="img"
          mode="widthFix"
          class="display-image"
          @click="previewImage(uniform.displayImages, index)"
        ></image>
      </view>
    </view>

    <view class="footer-bar">
      <view class="left-actions">
        <view class="action-btn" @click="contactService">
          <uni-icons type="headphones" size="24" color="#666"></uni-icons>
          <text>客服</text>
        </view>
        <view class="action-btn" @click="navigateTo('/pages/order/order-list')">
          <uni-icons type="list" size="24" color="#666"></uni-icons>
          <text>订单</text>
        </view>
      </view>
      <view class="right-actions">
        <button class="add-cart-btn" @click="addToCart">加入购物车</button>
        <button class="buy-btn" @click="buyNow">立即购买</button>
      </view>
    </view>
  </view>
</template>

<script>
import config from '@/config'
import { getUniform } from '@/api/uniform'
import { upsertCart, listCart } from '@/api/cart'

const DEFAULT_IMAGE = '/static/qjxf.png';

export default {
  data() {
    return {
      id: null,
      baseUrl: getApp().globalData.baseUrl,
      selectedSizeId: null,
      purchaseType: '1',
      uniform: {
        id: '',
        name: '',
        goodsCode: '',
        categoryName: '',
        brandName: '',
        supplierName: '',
        publishTime: '',
        saleTypeText: '商品信息',
        genderTypeText: '通用',
        totalStock: 0,
        images: [DEFAULT_IMAGE],
        displayImages: [DEFAULT_IMAGE],
        sizes: [],
        specs: [],
        detailContent: ''
      }
    }
  },
  computed: {
    selectedSize() {
      return this.uniform.sizes.find((item) => String(item.id) === String(this.selectedSizeId)) || null;
    },
    purchaseTypeText() {
      return this.purchaseType === '2' ? '零售购买' : '统购购买';
    },
    showGroupPrice() {
      return (this.uniform.saleType === '1' || this.uniform.saleType === '3') && !!this.uniform.noticeId;
    },
    showRetailPrice() {
      return this.uniform.saleType === '2' || this.uniform.saleType === '3';
    },
    displayPrice() {
      if (this.selectedSize) {
        return this.formatPrice(this.getSelectedUnitPrice(this.selectedSize));
      }
      if (!this.uniform.sizes.length) {
        return '0.00';
      }
      const minPrice = this.uniform.sizes.reduce((min, item) => {
        const current = this.getSelectedUnitPrice(item);
        return min === null || current < min ? current : min;
      }, null);
      return this.formatPrice(minPrice || 0);
    },
    displayOriginalPrice() {
      if (!this.selectedSize) {
        return '';
      }
      if (this.uniform.saleType !== '3') {
        return '';
      }
      if (this.purchaseType === '1' && this.showRetailPrice) {
        return this.formatPrice(this.selectedSize.retailPrice || 0);
      }
      if (this.purchaseType === '2' && this.showGroupPrice) {
        return this.formatPrice(this.selectedSize.groupPrice || 0);
      }
      return '';
    }
  },
  onLoad(options) {
    if (options.id) {
      this.id = options.id;
    }
    this.initData();
  },
  methods: {
    resolveImage(url) {
      if (!url) {
        return DEFAULT_IMAGE;
      }
      if (/^https?:\/\//i.test(url)) {
        return url;
      }
      if (url.indexOf('/filestatics/') === 0) {
        return `http://127.0.0.1:9300${url}`;
      }
      if (url.indexOf('/profile/upload/') === 0) {
        return url.replace('/profile/upload/', 'http://127.0.0.1:9300/filestatics/upload/');
      }
      const baseUrl = (this.baseUrl || config.baseUrl || '').replace(/\/$/, '');
      return `${baseUrl}${url.startsWith('/') ? '' : '/'}${url}`;
    },

    safeParseJson(text, fallback = null) {
      if (!text) {
        return fallback;
      }
      if (typeof text === 'object') {
        return text;
      }
      try {
        return JSON.parse(text);
      } catch (error) {
        return fallback;
      }
    },

    parseImageList(value) {
      if (!value) {
        return [];
      }
      if (Array.isArray(value)) {
        return value.map((item) => `${item || ''}`.trim()).filter(Boolean);
      }
      if (typeof value === 'object') {
        return Object.values(value).map((item) => `${item || ''}`.trim()).filter(Boolean);
      }
      const text = `${value}`.trim();
      if (!text) {
        return [];
      }
      const parsed = this.safeParseJson(text, null);
      if (Array.isArray(parsed)) {
        return parsed.map((item) => `${item || ''}`.trim()).filter(Boolean);
      }
      return text.split(',').map((item) => item.trim()).filter(Boolean);
    },

    formatPrice(value) {
      const num = Number(value || 0);
      return Number.isNaN(num) ? '0.00' : num.toFixed(2);
    },

    getSaleTypeText(saleType) {
      const map = {
        '1': '统购商品',
        '2': '零售商品',
        '3': '统购零售'
      };
      return map[String(saleType || '')] || '商品信息';
    },

    getDefaultPurchaseType(saleType, noticeId) {
      if (String(saleType) === '2') {
        return '2';
      }
      if (String(saleType) === '3' && !noticeId) {
        return '2';
      }
      return '1';
    },

    getSelectedUnitPrice(size) {
      if (!size) {
        return 0;
      }
      if (this.purchaseType === '2') {
        return Number(size.retailPrice || 0);
      }
      return Number(size.groupPrice || 0);
    },

    getGenderTypeText(genderType) {
      const map = {
        '0': '通用',
        '1': '男款',
        '2': '女款'
      };
      return map[String(genderType || '0')] || '通用';
    },

    buildSpecs(data, sizes) {
      return [
        { label: '商品编码', value: data.goodsCode || '--' },
        { label: '品类名称', value: data.categoryName || '--' },
        { label: '品牌名称', value: data.brandName || '--' },
        { label: '供应商', value: data.supplierName || '--' },
        { label: '销售类型', value: this.getSaleTypeText(data.saleType) },
        { label: '上架时间', value: data.publishTime || '--' },
        { label: '尺码数量', value: `${sizes.length} 个` },
        { label: '商品说明', value: data.detailContent || '暂无商品说明' }
      ];
    },

    normalizeUniform(data = {}) {
      const imageList = this.parseImageList(data.detailImagesJson).map((item) => this.resolveImage(item));
      const mainImage = this.resolveImage(data.image || data.coverImage || data.mainImage);
      const rawSizes = Array.isArray(data.xfptGoodsSizeList) ? data.xfptGoodsSizeList : [];
      const sizes = rawSizes
        .filter((item) => String(item.status || '1') === '1')
        .sort((a, b) => Number(a.sortNum || 0) - Number(b.sortNum || 0))
        .map((item) => {
          const stockNum = Number(item.stockNum || 0);
          return {
            id: item.id,
            name: item.sizeCode || '--',
            sizeCode: item.sizeCode || '--',
            sortNum: Number(item.sortNum || 0),
            genderType: item.genderType || '0',
            genderTypeText: this.getGenderTypeText(item.genderType),
            retailPrice: Number(item.retailPrice || 0),
            groupPrice: Number(item.groupPrice || 0),
            stockNum,
            stockText: stockNum > 0 ? `库存 ${stockNum}` : '缺货',
            inStock: stockNum > 0,
            skuId: item.id,
            measureJson: item.measureJson || ''
          };
        });

      const totalStock = sizes.reduce((sum, item) => sum + Number(item.stockNum || 0), 0);
      const genderTypeText = sizes.length
        ? this.getGenderTypeText(sizes[0].genderType)
        : '通用';

      return {
        id: data.id || this.id || '',
        name: data.goodsName || data.name || '校服商品',
        goodsCode: data.goodsCode || '',
        categoryName: data.categoryName || '未分类',
        brandName: data.brandName || '未设置品牌',
        supplierId: data.supplierId || '',
        supplierName: data.supplierName || '未设置供应商',
        schoolId: data.schoolId || data.shcoolId || data.deptId || '',
        shcoolId: data.shcoolId || data.schoolId || data.deptId || '',
        schoolName: data.schoolName || '',
        deptId: data.deptId || data.schoolId || data.shcoolId || '',
        noticeId: data.noticeId || null,
        purchaseStartTime: data.purchaseStartTime || '',
        purchaseEndTime: data.purchaseEndTime || '',
        publishTime: data.publishTime || '',
        saleType: String(data.saleType || ''),
        saleTypeText: this.getSaleTypeText(data.saleType),
        genderTypeText,
        totalStock,
        images: imageList.length ? [mainImage, ...imageList.filter((item) => item !== mainImage)] : [mainImage],
        displayImages: imageList,
        sizes,
        specs: this.buildSpecs(data, sizes),
        detailContent: data.detailContent || ''
      };
    },

    initData() {
      if (!this.id) return;

      getUniform(this.id)
        .then((res) => {
          this.uniform = this.normalizeUniform(res.data || res);
          this.purchaseType = this.getDefaultPurchaseType(this.uniform.saleType, this.uniform.noticeId);
          if (this.uniform.sizes.length) {
            const defaultSize = this.uniform.sizes.find((item) => item.inStock) || this.uniform.sizes[0];
            this.selectedSizeId = defaultSize.id;
          }
        })
        .catch((error) => {
          uni.showToast({
            title: error.message || '加载失败，请重试',
            icon: 'none'
          });
        });
    },

    selectSize(size) {
      if (!size.inStock) return;
      this.selectedSizeId = size.id;
    },

    switchPurchaseType(type) {
      if (this.uniform.saleType !== '3') {
        return;
      }
      if (type === '1' && !this.uniform.noticeId) {
        return uni.showToast({
          title: '当前不在统购时间内',
          icon: 'none'
        });
      }
      this.purchaseType = type;
    },

    viewReport() {
      uni.navigateTo({
        url: `/pages/uniform/quality-report?id=${this.uniform.id}`
      });
    },

    previewImage(images, current) {
      uni.previewImage({
        current,
        urls: images
      });
    },

    contactService() {
      uni.showToast({
        title: '正在连接客服...',
        icon: 'none'
      });
    },

    navigateTo(url) {
      uni.navigateTo({ url });
    },

    validatePurchaseAvailable() {
      if (this.purchaseType === '1' && !this.uniform.noticeId) {
        uni.showToast({
          title: '当前不在统购时间内',
          icon: 'none'
        });
        return false;
      }
      return true;
    },

    buildOrderItem() {
      const selectedSku = this.selectedSize || {};
      const price = this.getSelectedUnitPrice(selectedSku);
      const selectedStudent = uni.getStorageSync('selectedStudent') || {};
      return {
        id: this.uniform.id,
        uniformId: this.uniform.id,
        goodsId: this.uniform.id,
        skuId: selectedSku.skuId,
        sizeId: selectedSku.skuId,
        name: this.uniform.name,
        goodsName: this.uniform.name,
        goodsCode: this.uniform.goodsCode || '',
        image: this.uniform.images[0],
        mainImage: this.uniform.images[0],
        price: this.formatPrice(price),
        unitPrice: this.formatPrice(price),
        quantity: 1,
        buyNum: 1,
        size: selectedSku.name || '',
        sizeName: selectedSku.name || '',
        color: selectedSku.genderTypeText || '通用',
        spec: selectedSku.name || '',
        studentId: selectedStudent.id || selectedStudent.studentId || 0,
        schoolId: this.uniform.schoolId || this.uniform.shcoolId || selectedStudent.schoolId || selectedStudent.shcoolId || selectedStudent.deptId || 0,
        schoolName: this.uniform.schoolName || selectedStudent.school || selectedStudent.schoolName || '',
        deptId: this.uniform.deptId || this.uniform.schoolId || this.uniform.shcoolId || selectedStudent.schoolId || selectedStudent.shcoolId || selectedStudent.deptId || 0,
        supplierId: this.uniform.supplierId || null,
        supplierName: this.uniform.supplierName || '',
        selected: true,
        checkedFlag: '1',
        noticeId: this.purchaseType === '1' ? this.uniform.noticeId : null,
        saleType: this.purchaseType,
        orderType: this.purchaseType
      };
    },

    syncLocalCartItem(item) {
      const cartList = uni.getStorageSync('cartList') || [];
      const existed = cartList.find((cart) =>
        String(cart.uniformId || cart.goodsId) === String(item.uniformId) &&
        String(cart.skuId || cart.sizeId || '') === String(item.skuId || '') &&
        String(cart.orderType || '1') === String(item.orderType || '1')
      );
      if (existed) {
        existed.quantity = Number(existed.quantity || 1) + Number(item.quantity || 1);
        existed.buyNum = existed.quantity;
        existed.price = item.price;
        existed.unitPrice = item.price;
        existed.selected = true;
        existed.checkedFlag = '1';
      } else {
        cartList.push({ ...item, selected: true });
      }
      uni.setStorageSync('cartList', cartList);
    },

    addToCart() {
      if (!this.selectedSize) {
        return uni.showToast({
          title: '请选择尺码',
          icon: 'none'
        });
      }
      if (!this.validatePurchaseAvailable()) return;

      const item = this.buildOrderItem();
      upsertCart(item)
        .catch((error) => {
          if (!config.enableMockFallback) return Promise.reject(error);
          this.syncLocalCartItem(item);
          return { localOnly: true };
        })
        .then((res) => {
          if (!res || !res.localOnly) {
            return listCart().then((cartRes) => {
              const rows = cartRes.rows || [];
              uni.setStorageSync('cartList', rows);
            }).catch(() => {
              this.syncLocalCartItem(item);
            });
          }
        })
        .then(() => {
          uni.showToast({
            title: '已加入购物车',
            icon: 'success'
          });
        })
        .catch((error) => {
          uni.showToast({
            title: error.message || '加入购物车失败',
            icon: 'none'
          });
        });
    },

    buyNow() {
      if (!this.selectedSize) {
        return uni.showToast({
          title: '请选择尺码',
          icon: 'none'
        });
      }
      if (!this.validatePurchaseAvailable()) return;
      uni.setStorageSync('confirmOrderItems', [this.buildOrderItem()]);
      uni.navigateTo({
        url: '/pages/order/order-confirm?source=buyNow'
      });
    }
  }
}
</script>

<style lang="scss">
.container {
  padding-bottom: 140rpx;
  background-color: #f5f5f5;
}

.swiper {
  width: 100vw;
  height: 520rpx;
  background-color: #fff;

  .swiper-image {
    width: 100%;
    height: 100%;
  }
}

.info-section {
  padding: 30rpx;
  background-color: #fff;
  margin-bottom: 20rpx;

  .price-row {
    margin-bottom: 16rpx;

    .price {
      font-size: 48rpx;
      color: #e74c3c;
      font-weight: 600;
      margin-right: 16rpx;
    }

    .original-price {
      font-size: 28rpx;
      color: #999;
      text-decoration: line-through;
    }
  }

  .name-row {
    display: flex;
    justify-content: space-between;
    margin-bottom: 20rpx;

    .name-wrap {
      flex: 1;
    }

    .name {
      display: block;
      font-size: 34rpx;
      color: #333;
      font-weight: 600;
      line-height: 1.4;
      margin-bottom: 10rpx;
    }

    .sub-name {
      font-size: 24rpx;
      color: #8792a7;
    }

    .share-btn {
      background: none;
      padding: 0;
      display: flex;
      flex-direction: column;
      align-items: center;
      margin: 0 0 0 20rpx;
      line-height: 1.2;
      width: 80rpx;

      text {
        font-size: 22rpx;
        color: #666;
        margin-top: 4rpx;
      }

      &::after {
        border: none;
      }
    }
  }

  .tags-row {
    display: flex;
    flex-wrap: wrap;
    margin-bottom: 24rpx;

    .tag {
      padding: 8rpx 16rpx;
      font-size: 22rpx;
      color: #3b7aff;
      background-color: rgba(59, 122, 255, 0.1);
      border-radius: 999rpx;
      margin-right: 16rpx;
      margin-bottom: 12rpx;
    }
  }

  .purchase-mode {
    margin-bottom: 18rpx;

    .purchase-mode-label {
      display: block;
      font-size: 24rpx;
      color: #7c879b;
      margin-bottom: 12rpx;
      font-weight: 500;
    }
  }

  .sale-type-switch {
    display: inline-flex;
    padding: 8rpx;
    background: #f3f6fb;
    border-radius: 999rpx;
    gap: 10rpx;
  }

  .sale-type-pill {
    min-width: 128rpx;
    height: 58rpx;
    line-height: 58rpx;
    padding: 0 22rpx;
    text-align: center;
    border-radius: 999rpx;
    font-size: 24rpx;
    color: #6f7f98;
    font-weight: 500;
    background: transparent;

    &.active {
      background: #3b7aff;
      color: #fff;
      box-shadow: 0 8rpx 20rpx rgba(59, 122, 255, 0.24);
    }
  }

  .summary-card {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 16rpx;
    padding: 24rpx;
    border-radius: 24rpx;
    background: linear-gradient(135deg, #f7faff 0%, #f0f5ff 100%);

    .summary-item {
      display: flex;
      flex-direction: column;
    }

    .summary-label {
      font-size: 22rpx;
      color: #8b95a9;
      margin-bottom: 8rpx;
    }

    .summary-value {
      font-size: 26rpx;
      color: #2c3550;
      font-weight: 500;
    }
  }
}

.section-card {
  margin-bottom: 20rpx;
  padding: 30rpx;
  background-color: #fff;
}

.section-title {
  font-size: 30rpx;
  color: #333;
  font-weight: 600;
  margin-bottom: 20rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;

  .title {
    font-size: 30rpx;
    color: #333;
    font-weight: 600;
  }

  .size-guide {
    font-size: 24rpx;
    color: #3b7aff;
  }
}

.size-list {
  display: flex;
  flex-wrap: wrap;
  gap: 18rpx;

  .size-item {
    width: 150rpx;
    padding: 18rpx 0;
    border: 1rpx solid #dfe5ef;
    border-radius: 18rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    background-color: #fff;

    .size-code {
      font-size: 30rpx;
      color: #2d3a52;
      font-weight: 600;
      margin-bottom: 8rpx;
    }

    .size-stock {
      font-size: 22rpx;
      color: #8d97a8;
    }

    &.size-selected {
      border-color: #3b7aff;
      background-color: rgba(59, 122, 255, 0.08);

      .size-code,
      .size-stock {
        color: #3b7aff;
      }
    }

    &.size-disabled {
      border-color: #eceff4;
      background-color: #f7f8fb;

      .size-code,
      .size-stock {
        color: #c2c8d3;
      }
    }
  }
}

.selected-size-card {
  margin-top: 24rpx;
  padding: 24rpx;
  border-radius: 20rpx;
  background-color: #f8fbff;

  .selected-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10rpx 0;
  }

  .selected-label {
    font-size: 24rpx;
    color: #7c879b;
  }

  .selected-value {
    font-size: 26rpx;
    color: #2d3a52;
    font-weight: 500;
  }

  .price-highlight {
    color: #e74c3c;
  }
}

.detail-content {
  .spec-item {
    display: flex;
    padding: 18rpx 0;
    border-bottom: 1rpx solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }

    .spec-label {
      width: 180rpx;
      font-size: 25rpx;
      color: #98a1b2;
      flex-shrink: 0;
    }

    .spec-value {
      flex: 1;
      font-size: 26rpx;
      color: #333;
      line-height: 1.6;
      word-break: break-all;
    }
  }
}

.size-table-section {
  .table-header,
  .table-row {
    display: grid;
    grid-template-columns: 1.1fr 1fr 1fr 0.9fr;
    align-items: center;
  }

  .table-header {
    padding: 18rpx 0;
    border-bottom: 1rpx solid #edf1f6;
    color: #8b95a9;
    font-size: 24rpx;
  }

  .table-row {
    padding: 20rpx 0;
    border-bottom: 1rpx solid #f4f6fa;
    font-size: 25rpx;
    color: #2d3a52;

    &:last-child {
      border-bottom: none;
    }
  }

  .table-cell {
    text-align: center;
  }
}

.report-section {
  .report-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;

    .report-more {
      display: flex;
      align-items: center;
      font-size: 24rpx;
      color: #999;
    }
  }

  .report-summary {
    display: flex;
    align-items: flex-start;

    .report-tag {
      padding: 6rpx 16rpx;
      font-size: 24rpx;
      border-radius: 6rpx;
      margin-right: 20rpx;

      &.report-pass {
        background-color: rgba(46, 204, 113, 0.1);
        color: #2ecc71;
      }
    }

    .report-desc {
      flex: 1;
      font-size: 26rpx;
      color: #666;
      line-height: 1.6;
    }
  }
}

.display-images {
  .display-image {
    width: 100%;
    margin-bottom: 20rpx;
    border-radius: 12rpx;

    &:last-child {
      margin-bottom: 0;
    }
  }
}

.footer-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  height: 110rpx;
  background-color: #fff;
  padding-bottom: env(safe-area-inset-bottom);
  display: flex;
  border-top: 1rpx solid #f0f0f0;
  z-index: 999;

  .left-actions {
    display: flex;

    .action-btn {
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      width: 120rpx;
      height: 110rpx;
      font-size: 22rpx;
      color: #666;
    }
  }

  .right-actions {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-around;
    padding-right: 20rpx;

    .add-cart-btn,
    .buy-btn {
      width: 240rpx;
      height: 76rpx;
      line-height: 76rpx;
      border-radius: 38rpx;
      font-size: 28rpx;
      padding: 0;
    }

    .add-cart-btn {
      background-color: rgba(59, 122, 255, 0.1);
      color: #3b7aff;

      &::after {
        border: none;
      }
    }

    .buy-btn {
      background-color: #3b7aff;
      color: #fff;

      &::after {
        border: none;
      }
    }
  }
}
</style>
