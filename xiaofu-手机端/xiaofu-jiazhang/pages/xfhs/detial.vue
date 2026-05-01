<template>
  <view class="container">
    <!-- 加载状态 -->
    <view v-if="isLoading" class="loading-container">
      <uni-load-more status="loading" :content-text="loadingText"></uni-load-more>
    </view>

    <!-- 错误状态 -->
    <view v-else-if="hasError" class="error-container">
      <view class="error-icon">⚠️</view>
      <text class="error-text">{{ errorMessage }}</text>
      <button class="retry-btn" @click="loadDetail">重新加载</button>
    </view>

    <!-- 详情内容 -->
    <view v-else-if="detailData" class="detail-content">
      <!-- 状态卡片 -->
      <view class="status-card">
        <view class="status-header">
          <text class="status-title">审核状态</text>
          <view class="status-badge" :class="getStatusClass(detailData.status)">
            {{ getStatusText(detailData.status) }}
          </view>
        </view> 
      </view>

      <!-- 基本信息 -->
      <view class="info-card">
        <view class="card-header">
          <text class="card-title">基本信息</text>
        </view>
        <view class="info-list">
        <view class="info-item">
            <text class="info-label">学校名称</text>
            <text class="info-value">{{ detailData.schoolName || '暂无' }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">学生姓名</text>
            <text class="info-value">{{ detailData.name || '暂无' }}</text>
          </view> 
          <view class="info-item">
            <text class="info-label">班级名称</text>
            <text class="info-value">{{ detailData.className || '暂无' }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">提交时间</text>
            <text class="info-value">{{ detailData.createTime.split(' ')[0] || '暂无' }}</text>
          </view> 
        </view>
      </view>

      <!-- 校服图片 -->
      <view class="image-card" v-if="imageList.length > 0">
        <view class="card-header">
          <text class="card-title">校服图片</text>
          <text class="image-count">({{ imageList.length }}张)</text>
        </view>
        <view class="image-gallery">
          <view 
            class="image-item" 
            v-for="(image, index) in imageList" 
            :key="index"
            @click="previewImage(index)"
          >
            <image 
              :src="image" 
              mode="aspectFill" 
              class="gallery-image"
              :lazy-load="true"
            />
            <view class="image-overlay">
              <uni-icons type="eye" size="20" color="#fff"></uni-icons>
            </view>
          </view>
        </view>
      </view>  
    </view>

    <!-- 空状态 -->
    <view v-else class="empty-container">
      <view class="empty-icon">📄</view>
      <text class="empty-text">暂无详情信息</text>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      baseUrl: getApp().globalData.baseUrl,
      detailData: null,
      imageList: [],
      isLoading: true,
      hasError: false,
      errorMessage: '',
      loadingText: {
        contentdown: '加载中...',
        contentrefresh: '加载中...',
        contentnomore: '加载完成'
      },
      imagUrl:'http://localhost:8080',
    };
  },
  onLoad(options) {
    if (options.id) {
      this.loadDetail(options.id);
    } else {
      this.hasError = true;
      this.errorMessage = '缺少必要参数';
      this.isLoading = false;
    }
  },
  methods: {
    // 加载详情数据
    loadDetail(id) {
      this.isLoading = true;
      this.hasError = false;
      
      uni.request({
        url: `${this.baseUrl}/school/ljhs/${id}`,
        method: 'GET',
        header: {
          Authorization: uni.getStorageSync('token')
        },
        success: (res) => {
          if (res.data.code === 200) {
            this.detailData = res.data.data;
            this.parseImages();
          } else {
            this.hasError = true;
            this.errorMessage = res.data.message || '获取详情失败';
          }
          this.isLoading = false;
        },
        fail: (err) => {
          this.hasError = true;
          this.errorMessage = '网络错误，请重试';
          this.isLoading = false;
          console.error('获取详情失败：', err);
        }
      });
    },

    // 解析图片列表
    parseImages() {
      if (this.detailData && this.detailData.images) {
        const images = this.detailData.images;
        if (typeof images === 'string') {
          // 如果是字符串，按逗号分割 
          this.imageList = images.split(',').filter(img => img.trim()).map(img => this.imagUrl+img);
        } else if (Array.isArray(images)) {
          // 如果是数组，直接使用
          this.imageList = images.filter(img => this.imagUrl+img);
        } 
      }
    },

    // 预览图片
    previewImage(index) {
      uni.previewImage({
        current: index,
        urls: this.imageList
      });
    },

    // 确认回收
    handleConfirm() {
      uni.showModal({
        title: '确认回收',
        content: `确认回收学生 ${this.detailData.name} 的校服吗？`,
        success: (res) => {
          if (res.confirm) {
            this.updateStatus(1, '确认回收');
          }
        }
      });
    },

    // 拒绝回收
    handleReject() {
      uni.showModal({
        title: '拒绝回收',
        content: '请输入拒绝原因',
        editable: true,
        placeholderText: '请输入拒绝原因',
        success: (res) => {
          if (res.confirm && res.content) {
            this.updateStatus(2, res.content);
          } else if (res.confirm && !res.content) {
            uni.showToast({
              title: '请输入拒绝原因',
              icon: 'none'
            });
          }
        }
      });
    },

    // 更新状态
    updateStatus(status, remark = '') {
      uni.showLoading({
        title: '处理中...',
        mask: true
      });

      const updateData = {
        id: this.detailData.id,
        status: status,
        auditRemark: remark,
        simId: uni.getStorageSync('simId')
      };

      uni.request({
        url: `${this.baseUrl}/school/ljhs`,
        method: 'PUT',
        data: updateData,
        header: {
          Authorization: uni.getStorageSync('token'),
          'Content-Type': 'application/json'
        },
        success: (res) => {
          uni.hideLoading();
          if (res.data.code === 200) {
            uni.showToast({
              title: status === 1 ? '确认成功' : '拒绝成功',
              icon: 'success'
            });
            // 刷新详情数据
            this.loadDetail(this.detailData.id);
          } else {
            uni.showToast({
              title: res.data.message || '操作失败',
              icon: 'none'
            });
          }
        },
        fail: (err) => {
          uni.hideLoading();
          uni.showToast({
            title: '网络错误',
            icon: 'none'
          });
          console.error('更新状态失败：', err);
        }
      });
    },

    // 获取状态样式类
    getStatusClass(status) {
      switch (status) {
        case '0':
          return 'status-rejected';
        case '1':
          return 'status-approved';
        case '2':
          return 'status-pending'; 
      }
    },

    // 获取状态文本
    getStatusText(status) {
      switch (status) {
        case '0':
          return '未通过';
        case '1':
          return '已通过';
        case '2':
          return '待审核'; 
      }
    },

    // 获取状态描述
    getStatusDesc(status) {
      switch (status) {
        case 0:
          return '您的校服回收申请已提交，等待审核中';
        case 1:
          return '您的校服回收申请已通过审核';
        case 2:
          return '您的校服回收申请未通过审核';
        default:
          return '状态未知';
      }
    },

    // 格式化时间
    formatTime(timeStr) {
      if (!timeStr) return '暂无';
      const date = new Date(timeStr);
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    }
  }
};
</script>

<style lang="scss">
.container {
  background-color: #f8f9fa;
  min-height: 100vh;
  padding: 24rpx;
}

// 加载状态
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400rpx;
}

// 错误状态
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 40rpx;

  .error-icon {
    font-size: 80rpx;
    margin-bottom: 24rpx;
    opacity: 0.6;
  }

  .error-text {
    font-size: 30rpx;
    color: #666;
    margin-bottom: 40rpx;
    text-align: center;
    line-height: 1.5;
  }

  .retry-btn {
    width: 240rpx;
    height: 72rpx;
    line-height: 72rpx;
    border-radius: 36rpx;
    font-size: 28rpx;
    font-weight: 500;
    background: linear-gradient(135deg, #3b7aff 0%, #5b8cff 100%);
    color: #fff;
    border: none;
    box-shadow: 0 4rpx 12rpx rgba(59, 122, 255, 0.3);
    transition: all 0.3s ease;

    &:active {
      transform: translateY(2rpx);
      box-shadow: 0 2rpx 8rpx rgba(59, 122, 255, 0.2);
    }

    &::after {
      border: none;
    }
  }
}

// 空状态
.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 40rpx;

  .empty-icon {
    font-size: 120rpx;
    margin-bottom: 32rpx;
    opacity: 0.4;
  }

  .empty-text {
    font-size: 28rpx;
    color: #666;
    font-weight: 500;
  }
}

// 详情内容
.detail-content {
  .status-card,
  .info-card,
  .image-card,
  .audit-card {
    background-color: #fff;
    border-radius: 16rpx;
    margin-bottom: 24rpx;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
    overflow: hidden;
  }

  .card-header {
    display: flex;
    align-items: center;
    padding: 32rpx 32rpx 0;
    margin-bottom: 24rpx;

    .card-title {
      font-size: 32rpx;
      font-weight: 600;
      color: #1a1a1a;
    }

    .image-count {
      font-size: 24rpx;
      color: #999;
      margin-left: 12rpx;
    }
  }
}

// 状态卡片
.status-card {
  .status-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 32rpx;

    .status-title {
      font-size: 32rpx;
      font-weight: 600;
      color: #1a1a1a;
    }

    .status-badge {
      padding: 8rpx 20rpx;
      border-radius: 20rpx;
      font-size: 24rpx;
      font-weight: 500;

      &.status-pending {
        background-color: rgba(255, 152, 0, 0.1);
        color: #ff9800;
      }

      &.status-approved {
        background-color: rgba(76, 175, 80, 0.1);
        color: #4caf50;
      }

      &.status-rejected {
        background-color: rgba(244, 67, 54, 0.1);
        color: #f44336;
      }

      &.status-unknown {
        background-color: rgba(158, 158, 158, 0.1);
        color: #9e9e9e;
      }
    }
  }

  .status-info {
    padding: 0 32rpx 32rpx;

    .status-desc {
      font-size: 26rpx;
      color: #666;
      line-height: 1.5;
    }
  }
}

// 信息列表
.info-list,
.audit-info {
  padding: 0 32rpx 32rpx;

  .info-item {
    display: flex;
    align-items: flex-start;
    margin-bottom: 24rpx;

    &:last-child {
      margin-bottom: 0;
    }

    .info-label {
      font-size: 26rpx;
      color: #999;
      min-width: 140rpx;
      margin-right: 20rpx;
    }

    .info-value {
      font-size: 26rpx;
      color: #333;
      font-weight: 500;
      flex: 1;
      line-height: 1.5;
    }
  }
}

// 图片画廊
.image-gallery {
  display: flex;
  flex-wrap: wrap;
  padding: 0 32rpx 32rpx;
  gap: 16rpx;

  .image-item {
    position: relative;
    width: calc(33.333% - 12rpx);
    aspect-ratio: 1;
    border-radius: 12rpx;
    overflow: hidden;
    background-color: #f5f5f5;

    .gallery-image {
      width: 100%;
      height: 100%;
    }

    .image-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background-color: rgba(0, 0, 0, 0.3);
      display: flex;
      justify-content: center;
      align-items: center;
      opacity: 0;
      transition: opacity 0.3s ease;
    }

    &:active .image-overlay {
      opacity: 1;
    }
  }
}

// 操作按钮区域
.action-section {
  display: flex;
  gap: 24rpx;
  padding: 32rpx;
  background-color: #fff;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);

  .action-btn {
    flex: 1;
    height: 80rpx;
    line-height: 80rpx;
    border-radius: 40rpx;
    font-size: 28rpx;
    font-weight: 500;
    border: none;
    transition: all 0.3s ease;

    &::after {
      border: none;
    }

    &.btn-primary {
      background: linear-gradient(135deg, #3b7aff 0%, #5b8cff 100%);
      color: #fff;
      box-shadow: 0 4rpx 12rpx rgba(59, 122, 255, 0.3);

      &:active {
        transform: translateY(2rpx);
        box-shadow: 0 2rpx 8rpx rgba(59, 122, 255, 0.2);
      }
    }

    &.btn-secondary {
      background-color: #f5f5f5;
      color: #666;
      border: 2rpx solid #e0e0e0;

      &:active {
        background-color: #e8e8e8;
        transform: translateY(2rpx);
      }
    }
  }
}
</style>
