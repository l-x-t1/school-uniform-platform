<template>
  <view class="container">
    <!-- 顶部提示 -->
    <view class="guide-header">
      <view class="header-icon">
        <uni-icons type="info-filled" size="24" color="#3B7AFF"></uni-icons>
      </view>
      <view class="header-text">
        <text class="header-title">测量指南</text>
        <text class="header-desc">正确测量可以帮助您选择合适的校服尺码</text>
      </view>
    </view>
    
    <!-- 测量工具准备 -->
    <view class="section tool-section">
      <view class="section-title">
        <text class="title-text">测量前准备</text>
      </view>
      <view class="tools-list">
        <view class="tool-item">
          <image class="tool-image" src="/static/images/tape.png" mode="aspectFit"></image>
          <text class="tool-name">软尺</text>
        </view>
        <view class="tool-item">
          <image class="tool-image" src="/static/images/standing.png" mode="aspectFit"></image>
          <text class="tool-name">自然站立</text>
        </view>
        <view class="tool-item">
          <image class="tool-image" src="/static/images/light-clothes.png" mode="aspectFit"></image>
          <text class="tool-name">穿薄衣服</text>
        </view>
      </view>
      <view class="tips-box">
        <text class="tips-title">测量小贴士：</text>
        <text class="tips-content">建议孩子穿着较薄的贴身衣物进行测量，测量时保持自然站立，不要绷紧或者过度放松。</text>
      </view>
    </view>
    
    <!-- 测量步骤指南 -->
    <view class="section measure-section">
      <view class="section-title">
        <text class="title-text">测量步骤</text>
      </view>
      
      <!-- 身高测量 -->
      <view class="measure-item">
        <view class="measure-header">
          <view class="measure-number">1</view>
          <text class="measure-title">身高测量</text>
        </view>
        <view class="measure-content">
          <image class="measure-image" src="/static/images/height-measure.png" mode="widthFix"></image>
          <view class="measure-desc">
            <text class="desc-title">测量方法：</text>
            <text class="desc-content">孩子自然站立，从脚底到头顶的垂直距离。</text>
          </view>
        </view>
      </view>
      
      <!-- 胸围测量 -->
      <view class="measure-item">
        <view class="measure-header">
          <view class="measure-number">2</view>
          <text class="measure-title">胸围测量</text>
        </view>
        <view class="measure-content">
          <image class="measure-image" src="/static/images/chest-measure.png" mode="widthFix"></image>
          <view class="measure-desc">
            <text class="desc-title">测量方法：</text>
            <text class="desc-content">软尺环绕胸部最丰满处，保持水平。测量时应自然呼吸，不要绷紧或屏住呼吸。</text>
          </view>
        </view>
      </view>
      
      <!-- 腰围测量 -->
      <view class="measure-item">
        <view class="measure-header">
          <view class="measure-number">3</view>
          <text class="measure-title">腰围测量</text>
        </view>
        <view class="measure-content">
          <image class="measure-image" src="/static/images/waist-measure.png" mode="widthFix"></image>
          <view class="measure-desc">
            <text class="desc-title">测量方法：</text>
            <text class="desc-content">软尺环绕腰部最细处，通常在肚脐上方约2厘米处。测量时保持自然放松状态。</text>
          </view>
        </view>
      </view>
      
      <!-- 臀围测量 -->
      <view class="measure-item">
        <view class="measure-header">
          <view class="measure-number">4</view>
          <text class="measure-title">臀围测量</text>
        </view>
        <view class="measure-content">
          <image class="measure-image" src="/static/images/hip-measure.png" mode="widthFix"></image>
          <view class="measure-desc">
            <text class="desc-title">测量方法：</text>
            <text class="desc-content">软尺环绕臀部最丰满处，保持水平。测量时两脚并拢自然站立。</text>
          </view>
        </view>
      </view>
      
      <!-- 肩宽测量 -->
      <view class="measure-item">
        <view class="measure-header">
          <view class="measure-number">5</view>
          <text class="measure-title">肩宽测量</text>
        </view>
        <view class="measure-content">
          <image class="measure-image" src="/static/images/shoulder-measure.png" mode="widthFix"></image>
          <view class="measure-desc">
            <text class="desc-title">测量方法：</text>
            <text class="desc-content">测量左右肩峰点之间的直线距离。肩峰点是肩部最外侧的骨头突起。</text>
          </view>
        </view>
      </view>
      
      <!-- 袖长测量 -->
      <view class="measure-item">
        <view class="measure-header">
          <view class="measure-number">6</view>
          <text class="measure-title">袖长测量</text>
        </view>
        <view class="measure-content">
          <image class="measure-image" src="/static/images/sleeve-measure.png" mode="widthFix"></image>
          <view class="measure-desc">
            <text class="desc-title">测量方法：</text>
            <text class="desc-content">从肩峰点到手腕外侧的距离。测量时手臂自然弯曲呈90度。</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 常见问题 -->
    <view class="section faq-section">
      <view class="section-title">
        <text class="title-text">常见问题</text>
      </view>
      
      <view class="faq-list">
        <view class="faq-item" @click="toggleFaq(0)">
          <view class="faq-question">
            <text>孩子正在长身体，应该如何选择尺码？</text>
            <uni-icons :type="openedFaq === 0 ? 'top' : 'bottom'" size="18" color="#666"></uni-icons>
          </view>
          <view class="faq-answer" v-if="openedFaq === 0">
            <text>建议在实际测量的基础上选择大1-2厘米的尺码，避免校服很快就穿不下。但也不宜选择过大的尺码，以免影响孩子穿着体验。</text>
          </view>
        </view>
        
        <view class="faq-item" @click="toggleFaq(1)">
          <view class="faq-question">
            <text>男孩和女孩的测量方法有区别吗？</text>
            <uni-icons :type="openedFaq === 1 ? 'top' : 'bottom'" size="18" color="#666"></uni-icons>
          </view>
          <view class="faq-answer" v-if="openedFaq === 1">
            <text>测量方法基本相同，但由于身体发育特点不同，女孩在测量胸围时尤其需要注意测量的是胸部最丰满处的围度。</text>
          </view>
        </view>
        
        <view class="faq-item" @click="toggleFaq(2)">
          <view class="faq-question">
            <text>测量后发现孩子的数据在两个尺码之间怎么办？</text>
            <uni-icons :type="openedFaq === 2 ? 'top' : 'bottom'" size="18" color="#666"></uni-icons>
          </view>
          <view class="faq-answer" v-if="openedFaq === 2">
            <text>建议选择稍大一些的尺码，考虑到孩子的生长空间。夏季校服可以选择更贴身的尺码，冬季校服则可以选择稍宽松的尺码以便穿着内衣。</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 底部按钮 -->
    <view class="footer-actions">
      <button class="size-chart-btn" @click="goToSizeChart">查看尺码对照表</button>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      openedFaq: -1
    }
  },
  methods: {
    // 切换FAQ展开/收起状态
    toggleFaq(index) {
      if (this.openedFaq === index) {
        this.openedFaq = -1;
      } else {
        this.openedFaq = index;
      }
    },
    
    // 跳转到尺码对照表
    goToSizeChart() {
      uni.navigateTo({
        url: '/pages/uniform/size-chart'
      });
    }
  }
}
</script>

<style lang="scss">
.container {
  padding: 30rpx;
  padding-bottom: 150rpx;
}

// 顶部提示
.guide-header {
  display: flex;
  align-items: center;
  background-color: #EFF6FF;
  padding: 30rpx;
  border-radius: 12rpx;
  margin-bottom: 30rpx;
  
  .header-icon {
    margin-right: 20rpx;
  }
  
  .header-text {
    .header-title {
      font-size: 32rpx;
      color: #333;
      font-weight: bold;
      display: block;
      margin-bottom: 8rpx;
    }
    
    .header-desc {
      font-size: 26rpx;
      color: #666;
    }
  }
}

// 通用段落样式
.section {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  
  .section-title {
    margin-bottom: 30rpx;
    
    .title-text {
      font-size: 32rpx;
      color: #333;
      font-weight: bold;
      position: relative;
      padding-left: 20rpx;
      
      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 8rpx;
        height: 32rpx;
        background-color: #3B7AFF;
        border-radius: 4rpx;
      }
    }
  }
}

// 工具准备部分
.tool-section {
  .tools-list {
    display: flex;
    justify-content: space-around;
    margin-bottom: 30rpx;
    
    .tool-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      
      .tool-image {
        width: 120rpx;
        height: 120rpx;
        margin-bottom: 16rpx;
      }
      
      .tool-name {
        font-size: 28rpx;
        color: #333;
      }
    }
  }
  
  .tips-box {
    background-color: #f9f9f9;
    padding: 20rpx;
    border-radius: 8rpx;
    
    .tips-title {
      font-size: 28rpx;
      color: #333;
      font-weight: bold;
      margin-bottom: 10rpx;
      display: block;
    }
    
    .tips-content {
      font-size: 26rpx;
      color: #666;
      line-height: 1.5;
    }
  }
}

// 测量步骤部分
.measure-section {
  .measure-item {
    margin-bottom: 40rpx;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .measure-header {
      display: flex;
      align-items: center;
      margin-bottom: 20rpx;
      
      .measure-number {
        width: 40rpx;
        height: 40rpx;
        background-color: #3B7AFF;
        color: #fff;
        border-radius: 20rpx;
        display: flex;
        justify-content: center;
        align-items: center;
        font-size: 24rpx;
        margin-right: 16rpx;
      }
      
      .measure-title {
        font-size: 30rpx;
        color: #333;
        font-weight: 500;
      }
    }
    
    .measure-content {
      padding-left: 56rpx;
      
      .measure-image {
        width: 100%;
        border-radius: 8rpx;
        margin-bottom: 20rpx;
      }
      
      .measure-desc {
        .desc-title {
          font-size: 28rpx;
          color: #333;
          font-weight: 500;
          margin-bottom: 8rpx;
          display: block;
        }
        
        .desc-content {
          font-size: 26rpx;
          color: #666;
          line-height: 1.5;
        }
      }
    }
  }
}

// 常见问题部分
.faq-section {
  .faq-list {
    .faq-item {
      border-bottom: 1rpx solid #f0f0f0;
      padding-bottom: 20rpx;
      margin-bottom: 20rpx;
      
      &:last-child {
        border-bottom: none;
        margin-bottom: 0;
        padding-bottom: 0;
      }
      
      .faq-question {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 10rpx 0;
        
        text {
          font-size: 28rpx;
          color: #333;
          font-weight: 500;
          flex: 1;
        }
      }
      
      .faq-answer {
        padding: 20rpx 0 10rpx;
        
        text {
          font-size: 26rpx;
          color: #666;
          line-height: 1.5;
        }
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
  padding: 20rpx 30rpx;
  background-color: #fff;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
  
  .size-chart-btn {
    width: 100%;
    height: 90rpx;
    line-height: 90rpx;
    background-color: #3B7AFF;
    color: #fff;
    font-size: 30rpx;
    border-radius: 45rpx;
    
    &::after {
      border: none;
    }
  }
}
</style> 