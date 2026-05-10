<template>
  <view class="container">
    <!-- 轮播图 -->
    <swiper class="swiper" :indicator-dots="true" :autoplay="false" :duration="500">
      <swiper-item v-for="(item, index) in uniform.images" :key="index">
        <image :src="item" mode="aspectFill" class="swiper-image"></image>
      </swiper-item>
    </swiper>
    
    <!-- 基本信息 -->
    <view class="info-section">
      <view class="price-row">
        <text class="price">¥{{uniform.price}}</text>
        <text class="original-price" v-if="uniform.originalPrice">¥{{uniform.originalPrice}}</text>
      </view>
      <view class="name-row">
        <text class="name">{{uniform.name}}</text>
        <button class="share-btn" open-type="share">
          <uni-icons type="redo" size="20" color="#666"></uni-icons>
          <text>分享</text>
        </button>
      </view>
      <view class="tags-row">
        <text class="tag" v-for="(tag, index) in uniform.tags" :key="index">{{tag}}</text>
      </view>
    </view>
    
    <!-- 选择尺码 -->
    <view class="specs-section section-card">
      <view class="section-header">
        <text class="title">选择尺码</text>
        <text class="size-guide" @click="showSizeGuide">尺码对照表</text>
      </view>
      <view class="size-list">
        <view 
          v-for="(size, index) in uniform.sizes" 
          :key="index"
          class="size-item"
          :class="{'size-selected': selectedSize === size.name, 'size-disabled': !size.inStock}"
          @click="selectSize(size)"
        >
          {{size.name}}
        </view>
      </view>
    </view>
    
    <!-- 校服详情 -->
    <view class="detail-section section-card">
      <view class="section-title">校服详情</view>
      <view class="detail-content">
        <view class="spec-item" v-for="(spec, index) in uniform.specs" :key="index">
          <text class="spec-label">{{spec.label}}</text>
          <text class="spec-value">{{spec.value}}</text>
        </view>
      </view>
    </view>
    
    <!-- 质检报告 -->
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
        <text class="report-desc">该校服已通过国家纺织品质量检测中心检测，符合国家校服质量标准。</text>
      </view>
    </view>
    
    <!-- 穿着展示 -->
    <view class="display-section section-card">
      <view class="section-title">穿着展示</view>
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
    
    <!-- 底部操作栏 -->
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
import { addCart } from '@/api/cart'

export default {
  data() {
    return {
      id: null,
      selectedSize: '',
      uniform: {
        id: '1',
        name: '2023秋季校服套装（男）',
        price: '268.00',
        originalPrice: '298.00',
        tags: ['纯棉面料', '环保染料', '舒适透气'],
        images: [
          '/static/nanxf.png',  
        ],
        sizes: [
          { name: 'S', inStock: true },
          { name: 'M', inStock: true },
          { name: 'L', inStock: true },
          { name: 'XL', inStock: true },
          { name: 'XXL', inStock: false }
        ],
        specs: [
          { label: '面料成分', value: '100%纯棉' },
          { label: '颜色', value: '藏青色' },
          { label: '适用季节', value: '秋季' },
          { label: '洗涤说明', value: '30℃以下水温手洗，不可漂白' }
        ],
        displayImages: [
          '/static/nanxf.png',  // 请确保这些图片存在
          '/static/nanxf.png'
        ]
      }
    }
  },
  onLoad(options) {
    if (options.id) {
      this.id = options.id;
    }
    this.initData();
  },
  methods: {
    normalizeUniform(data = {}) {
      const images = data.images || data.imageList || [data.image || data.coverImage || data.mainImage || '/static/nanxf.png'];
      const sizes = data.sizes || data.skuList || data.skus || this.uniform.sizes;
      return {
        ...this.uniform,
        id: data.id || data.uniformId || data.productId || this.id || this.uniform.id,
        name: data.name || data.uniformName || data.productName || this.uniform.name,
        price: data.price || data.salePrice || data.minPrice || this.uniform.price,
        originalPrice: data.originalPrice || data.marketPrice || this.uniform.originalPrice,
        tags: data.tags || data.tagList || this.uniform.tags,
        images,
        sizes: sizes.map((item) => ({
          name: item.name || item.size || item.sizeName,
          inStock: item.inStock !== undefined ? item.inStock : (item.stock === undefined ? true : item.stock > 0),
          skuId: item.skuId || item.id
        })),
        specs: data.specs || data.specList || this.uniform.specs,
        displayImages: data.displayImages || data.detailImages || images
      };
    },

    initData() {
      if (!this.id) return;

      getUniform(this.id)
        .then((res) => {
          this.uniform = this.normalizeUniform(res.data || res);
        })
        .catch((error) => {
          if (config.enableMockFallback) return;
          uni.showToast({
            title: error.message || '加载失败，请重试',
            icon: 'none'
          });
        });
    },
    
    selectSize(size) {
      if (!size.inStock) return;
      this.selectedSize = size.name;
    },
    
    showSizeGuide() {
      uni.navigateTo({
        url: '/pages/uniform/size-guide'
      });
    },
    
    viewReport() {
      uni.navigateTo({
        url: `/pages/uniform/quality-report?id=${this.uniform.id}`
      });
    },
    
    previewImage(images, current) {
      uni.previewImage({
        current: current,
        urls: images
      });
    },
    
    contactService() {
      uni.showToast({
        title: '正在连接客服...',
        icon: 'none'
      });
      // 实际开发中接入客服系统
    },
    
    navigateTo(url) {
      uni.navigateTo({ url });
    },

    buildOrderItem() {
      const selectedSku = this.uniform.sizes.find((item) => item.name === this.selectedSize) || {};
      return {
        id: this.uniform.id,
        uniformId: this.uniform.id,
        skuId: selectedSku.skuId,
        name: this.uniform.name,
        image: this.uniform.images[0],
        price: this.uniform.price,
        quantity: 1,
        size: this.selectedSize,
        color: '默认款',
        spec: this.selectedSize
      };
    },

    syncLocalCartItem(item) {
      const cartList = uni.getStorageSync('cartList') || [];
      const existed = cartList.find((cart) => String(cart.uniformId) === String(item.uniformId) && cart.size === item.size);
      if (existed) {
        existed.quantity = Number(existed.quantity || 1) + Number(item.quantity || 1);
        existed.selected = true;
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

      const item = this.buildOrderItem();
      addCart({
        uniformId: item.uniformId,
        skuId: item.skuId,
        size: item.size,
        quantity: item.quantity
      })
        .catch((error) => {
          if (!config.enableMockFallback) return Promise.reject(error);
          this.syncLocalCartItem(item);
          return { localOnly: true };
        })
        .then((res) => {
          if (!res || !res.localOnly) {
            this.syncLocalCartItem(item);
          }
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
  padding-bottom: 120rpx;
  background-color: #f5f5f5; // 添加背景色
}

.swiper {
  width: 100vw;  // 确保宽度正确
  height: 20vh;
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
      font-size: 40rpx;
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
    margin-bottom: 16rpx;
    
    .name {
      flex: 1;
      font-size: 32rpx;
      color: #333;
      font-weight: 500;
      line-height: 1.4;
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
    
    .tag {
      padding: 4rpx 12rpx;
      font-size: 22rpx;
      color: #3B7AFF;
      background-color: rgba(59, 122, 255, 0.1);
      border-radius: 4rpx;
      margin-right: 16rpx;
      margin-bottom: 8rpx;
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
  font-weight: 500;
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
    font-weight: 500;
  }
  
  .size-guide {
    font-size: 24rpx;
    color: #3B7AFF;
  }
}

.size-list {
  display: flex;
  flex-wrap: wrap;
  
  .size-item {
    padding: 12rpx 30rpx;
    border: 1rpx solid #ddd;
    border-radius: 6rpx;
    margin-right: 20rpx;
    margin-bottom: 20rpx;
    font-size: 28rpx;
    color: #333;
    
    &.size-selected {
      color: #3B7AFF;
      border-color: #3B7AFF;
      background-color: rgba(59, 122, 255, 0.05);
    }
    
    &.size-disabled {
      color: #ccc;
      border-color: #eee;
      background-color: #f8f8f8;
    }
  }
}

.detail-content {
  .spec-item {
    display: flex;
    padding: 16rpx 0;
    border-bottom: 1rpx solid #f0f0f0;
    
    &:last-child {
      border-bottom: none;
    }
    
    .spec-label {
      width: 200rpx;
      font-size: 26rpx;
      color: #999;
    }
    
    .spec-value {
      flex: 1;
      font-size: 26rpx;
      color: #333;
    }
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
  height: 100rpx;
  background-color: #fff;
  padding-bottom: env(safe-area-inset-bottom); // 适配iPhone X等机型
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
      height: 100rpx;
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
    
    .add-cart-btn, .buy-btn {
      width: 240rpx;
      height: 72rpx;
      line-height: 72rpx;
      border-radius: 36rpx;
      font-size: 28rpx;
      padding: 0;
    }
    
    .add-cart-btn {
      background-color: rgba(59, 122, 255, 0.1);
      color: #3B7AFF;
      
      &::after {
        border: none;
      }
    }
    
    .buy-btn {
      background-color: #3B7AFF;
      color: #fff;
      
      &::after {
        border: none;
      }
    }
  }
}
</style> 
