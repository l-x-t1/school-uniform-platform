<template>
  <view class="container">
    <!-- 加载状态 -->
    <view class="loading-container" v-if="isLoading && !reportData">
      <uni-load-more status="loading" :content-text="loadingText"></uni-load-more>
    </view> 
    
    <!-- 报告详情内容 -->
    <view v-else-if="reportData">
      <!-- 报告头部 -->
      <view class="report-header">
        <view class="report-title-section">
          <text class="report-title">{{ reportData.ggTitle || '质检报告' }}</text> 
        </view>  
        <view class="report-org">
          <text>创建时间: {{ reportData.createTime.split(' ')[0] || '暂无' }}</text>
        </view>
      </view>
      
      <!-- 基本信息卡片 -->
      <view class="info-card">
        <view class="card-title">基本信息</view>
        <view class="info-item">
          <text class="item-label">学校名称:</text>
          <text class="item-value">{{ reportData.deptName || '暂无' }}</text>
        </view>
        <view class="info-item">
          <text class="item-label">供应商:</text>
          <text class="item-value">{{ reportData.supplierName || '暂无' }}</text>
        </view>
        <view class="info-item">
          <text class="item-label">联系电话:</text>
          <text class="item-value">{{ reportData.supplierPhone || '暂无' }}</text>
        </view>
        <view class="info-item">
          <text class="item-label">创建人:</text>
          <text class="item-value">{{ reportData.createBy || '暂无' }}</text>
        </view>
        <view class="info-item" v-if="reportData.remark">
          <text class="item-label">备注:</text>
          <text class="item-value">{{ reportData.remark }}</text>
        </view>
      </view>
      
      <!-- 质检报告文件 -->
      <view class="files-card">
        <view class="card-title">质检报告文件</view>
        
        <!-- 文件列表 -->
        <view class="file-list" v-if="reportFiles.length > 0">
          <view 
            class="file-item" 
            v-for="(file, index) in reportFiles" 
            :key="file.id || index"
            @click="viewFile(file)"
          >
            <view class="file-icon">
              <uni-icons type="paperclip" size="24" color="#3B7AFF"></uni-icons>
            </view>
            <view class="file-info">
              <text class="file-name">{{ file.inspectionReportTitle || '质检报告文件' }}</text> 
            </view>
            <view class="file-action">
              <uni-icons type="right" size="16" color="#999"></uni-icons>
            </view>
          </view>
        </view>
        
        <!-- 空状态 -->
        <view class="empty-files" v-else>
          <view class="empty-icon">📄</view>
          <text class="empty-text">暂无质检报告文件</text>
        </view>
      </view> 
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      baseUrl: getApp().globalData.baseUrl,
      reportId: '',
      reportData: null,
      reportFiles: [],
      isLoading: false,
      hasError: false,
      errorMessage: '',
      loadingText: {
        contentdown: '加载中...',
        contentrefresh: '加载中...',
        contentnomore: '没有更多了'
      }
    }
  },
  onLoad(options) {
    if (options.id) {
      this.reportId = options.id;
      this.getReportDetail();
      this.getReportDetailFile();
    } 
  },
  methods: {
    getReportDetail() {
      this.isLoading = true;
      this.hasError = false;
      
      uni.request({
        url: `${this.baseUrl}/school/suppliceInspectionReport/${this.reportId}`,
        method: "GET",
        header: {
          Authorization: uni.getStorageSync("token"),
        },
        success: ({ data }) => {
          this.isLoading = false;
          this.reportData = data.data;
        },
        fail: (err) => {
          this.isLoading = false;
          this.hasError = true;
          this.errorMessage = err.message || '获取报告详情失败';
        }
      });
    },
    getReportDetailFile() {
      uni.request({
        url: `${this.baseUrl}/school/suppliceInspectionReport/getNoticeInspectFile`,
        method: "GET",
        data: {
          id: this.reportId
        },
        header: {
          Authorization: uni.getStorageSync("token"),
        },
        success: ({ data }) => {
          this.reportFiles = data.data || [];
        },
        fail: (err) => {
          console.error('获取文件列表失败:', err);
        }
      });
    },
    
    retryLoad() {
      this.hasError = false;
      this.getReportDetail();
      this.getReportDetailFile();
    },
    
    getStatusClass(status) {
      if (status === 1 || status === '1') {
        return 'passed';
      } else if (status === 0 || status === '0') {
        return 'failed';
      }
      return 'unknown';
    },
    
    getStatusText(status) {
      if (status === 1 || status === '1') {
        return '合格';
      } else if (status === 0 || status === '0') {
        return '不合格';
      }
      return '未知';
    },
    
    viewFile(file) {
      // 直接下载文件，参考notice-detail.vue的实现
      if (file.fileUrl || file.url) {
        this.downloadFile(file);
      } else {
        uni.showToast({
          title: '文件链接不可用',
          icon: 'none'
        });
      }
    },
    
    
    downloadFile(file) {
      const fileUrl = 'http://localhost:8080' + file.url;
      
      if (fileUrl) {
        uni.showToast({
          title: "开始下载",
          icon: "none",
        });

        // 参考notice-detail.vue的实现
        uni.downloadFile({
          url: fileUrl,
          success: (res) => {
            uni.hideToast();
            // 使用a标签下载，参考notice-detail.vue
            const a = document.createElement("a");
            a.href = res.tempFilePath;
            a.download = file.inspectionReportTitle || file.url.split("/").pop();
            a.click();
          },
          fail: (err) => {
            console.log(err);
            uni.showToast({
              title: '文件下载失败',
              icon: 'none'
            });
          }
        });
      }
    },
    
    formatFileSize(size) {
      if (!size) return '未知大小';
      
      const units = ['B', 'KB', 'MB', 'GB'];
      let index = 0;
      let fileSize = parseInt(size);
      
      while (fileSize >= 1024 && index < units.length - 1) {
        fileSize /= 1024;
        index++;
      }
      
      return `${fileSize.toFixed(1)}${units[index]}`;
    },
    
    downloadAllFiles() {
      if (this.reportFiles.length === 0) {
        uni.showToast({
          title: '暂无文件可下载',
          icon: 'none'
        });
        return;
      }
      
      uni.showModal({
        title: '下载确认',
        content: `确定要下载全部 ${this.reportFiles.length} 个文件吗？`,
        success: (res) => {
          if (res.confirm) {
            this.reportFiles.forEach((file, index) => {
              setTimeout(() => {
                this.downloadFile(file);
              }, index * 1000); // 每个文件间隔1秒下载
            });
          }
        }
      });
    },
    
    shareReport() {
      if (!this.reportData) {
        uni.showToast({
          title: '报告信息不完整',
          icon: 'none'
        });
        return;
      }
      
      const shareContent = `质检报告：${this.reportData.ggTitle || '质检报告'}\n学校：${this.reportData.deptName || '未知'}\n供应商：${this.reportData.supplierName || '未知'}\n创建时间：${this.reportData.createTime || '未知'}`;
      
      uni.share({
        provider: 'weixin',
        scene: 'WXSceneSession',
        type: 0,
        href: '',
        title: '质检报告分享',
        summary: shareContent,
        imageUrl: '',
        success: () => {
          uni.showToast({
            title: '分享成功',
            icon: 'success'
          });
        },
        fail: (err) => {
          console.error('分享失败:', err);
          uni.showToast({
            title: '分享失败',
            icon: 'none'
          });
        }
      });
    }
  }
}
</script>

<style lang="scss">
.container {
  padding: 30rpx;
  padding-bottom: 150rpx;
  background-color: #f8f9fa;
  min-height: 100vh;
}

// 加载状态
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 120rpx 40rpx;
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

// 报告头部
.report-header {
  background: linear-gradient(135deg, #3B7AFF, #6BA1FF);
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(59, 122, 255, 0.2);
  
  .report-title-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;
    
    .report-title {
      font-size: 36rpx;
      color: #fff;
      font-weight: 600;
    }
    
    .report-status {
      display: flex;
      align-items: center;
      padding: 4rpx 16rpx;
      border-radius: 20rpx;
      font-size: 24rpx;
      
      &.passed {
        background-color: rgba(46, 204, 113, 0.8);
        color: #fff;
      }
      
      &.failed {
        background-color: rgba(231, 76, 60, 0.8);
        color: #fff;
      }
      
      uni-icons {
        margin-right: 6rpx;
      }
    }
  }
  
  .report-meta, .report-org {
    font-size: 26rpx;
    color: rgba(255, 255, 255, 0.9);
    margin-bottom: 10rpx;
    
    .report-number {
      margin-right: 20rpx;
    }
  }
}

// 卡片通用样式
.uniform-info-card, .test-results-card, .report-notes-card, .org-info-card, .info-card, .files-card {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  
  .card-title {
    font-size: 32rpx;
    color: #333;
    font-weight: 600;
    margin-bottom: 20rpx;
    position: relative;
    padding-left: 20rpx;
    
    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 6rpx;
      width: 6rpx;
      height: 30rpx;
      background-color: #3B7AFF;
      border-radius: 3rpx;
    }
  }
}

// 基本信息卡片
.info-card {
  .info-item {
    display: flex;
    align-items: flex-start;
    margin-bottom: 16rpx;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .item-label {
      width: 160rpx;
      font-size: 28rpx;
      color: #666;
    }
    
    .item-value {
      flex: 1;
      font-size: 28rpx;
      color: #333;
    }
  }
}

// 文件列表卡片
.files-card {
  .file-list {
    .file-item {
      display: flex;
      align-items: center;
      padding: 20rpx 0;
      border-bottom: 1rpx solid #f5f5f5;
      transition: all 0.3s ease;
      
      &:last-child {
        border-bottom: none;
      }
      
      &:active {
        background-color: #f8f9fa;
        transform: scale(0.98);
      }
      
      .file-icon {
        width: 60rpx;
        height: 60rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        background-color: rgba(59, 122, 255, 0.1);
        border-radius: 12rpx;
        margin-right: 20rpx;
      }
      
      .file-info {
        flex: 1;
        
        .file-name {
          display: block;
          font-size: 28rpx;
          color: #333;
          font-weight: 500;
          margin-bottom: 8rpx;
        }
        
        .file-size {
          font-size: 24rpx;
          color: #999;
        }
      }
      
      .file-action {
        width: 40rpx;
        height: 40rpx;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }
  }
}

// 空状态
.empty-files {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80rpx 40rpx;
  background-color: #fff;
  border-radius: 16rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);

  .empty-icon {
    font-size: 80rpx;
    margin-bottom: 24rpx;
    opacity: 0.4;
  }

  .empty-text {
    font-size: 28rpx;
    color: #666;
    font-weight: 500;
  }
}

// 校服信息
.uniform-info-card {
  .info-item {
    display: flex;
    align-items: flex-start;
    margin-bottom: 16rpx;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .item-label {
      width: 160rpx;
      font-size: 28rpx;
      color: #666;
    }
    
    .item-value {
      flex: 1;
      font-size: 28rpx;
      color: #333;
    }
  }
}

// 检验结果
.test-results-card {
  .test-item {
    border-bottom: 1rpx solid #f5f5f5;
    padding-bottom: 30rpx;
    margin-bottom: 30rpx;
    
    &:last-child {
      border-bottom: none;
      padding-bottom: 0;
      margin-bottom: 0;
    }
    
    .test-item-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16rpx;
      
      .test-name {
        font-size: 30rpx;
        color: #333;
        font-weight: 500;
      }
      
      .test-result {
        font-size: 26rpx;
        padding: 4rpx 16rpx;
        border-radius: 20rpx;
        
        &.passed {
          background-color: rgba(46, 204, 113, 0.1);
          color: #2ecc71;
        }
        
        &.failed {
          background-color: rgba(231, 76, 60, 0.1);
          color: #e74c3c;
        }
      }
    }
    
    .test-details {
      .detail-item {
        display: flex;
        margin-bottom: 12rpx;
        
        &:last-child {
          margin-bottom: 0;
        }
        
        .detail-label {
          width: 160rpx;
          font-size: 26rpx;
          color: #999;
        }
        
        .detail-value {
          flex: 1;
          font-size: 26rpx;
          color: #666;
        }
      }
    }
  }
}

// 检验说明
.report-notes-card {
  .notes-content {
    .note-text {
      font-size: 26rpx;
      color: #666;
      line-height: 1.6;
      display: block;
      margin-bottom: 10rpx;
      
      &:last-child {
        margin-bottom: 0;
      }
    }
  }
}

// 检验机构信息
.org-info-card {
  .org-header {
    display: flex;
    align-items: center;
    margin-bottom: 20rpx;
    
    .org-logo {
      width: 100rpx;
      height: 100rpx;
      margin-right: 20rpx;
      border-radius: 50%;
      background-color: #f9f9f9;
    }
    
    .org-name {
      font-size: 28rpx;
      color: #333;
      font-weight: 500;
      
      .org-cert {
        font-size: 24rpx;
        color: #999;
        font-weight: normal;
        margin-top: 10rpx;
        display: block;
      }
    }
  }
  
  .org-contact {
    margin-bottom: 30rpx;
    
    .contact-item {
      display: flex;
      align-items: center;
      font-size: 26rpx;
      color: #666;
      margin-bottom: 10rpx;
      
      uni-icons {
        margin-right: 10rpx;
      }
    }
  }
  
  .qr-section {
    display: flex;
    flex-direction: column;
    align-items: center;
    
    .qr-code {
      width: 160rpx;
      height: 160rpx;
      margin-bottom: 10rpx;
      background-color: #f9f9f9;
    }
    
    .qr-tip {
      font-size: 24rpx;
      color: #999;
    }
  }
}

// 底部按钮
.footer-actions {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: space-between;
  padding: 20rpx 30rpx;
  background-color: #fff;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
  z-index: 100;
  
  .action-btn {
    flex: 1;
    height: 90rpx;
    border-radius: 45rpx;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 30rpx;
    font-weight: 500;
    transition: all 0.3s ease;
    
    &:first-child {
      margin-right: 20rpx;
    }
    
    &::after {
      border: none;
    }
    
    uni-icons {
      margin-right: 10rpx;
    }
    
    &:active {
      transform: scale(0.95);
    }
  }
  
  .download-btn {
    background-color: #fff;
    color: #3B7AFF;
    border: 2rpx solid #3B7AFF;
    
    &:active {
      background-color: rgba(59, 122, 255, 0.05);
    }
  }
  
  .share-btn {
    background: linear-gradient(135deg, #3B7AFF 0%, #5B8CFF 100%);
    color: #fff;
    border: none;
    box-shadow: 0 4rpx 12rpx rgba(59, 122, 255, 0.3);
    
    &:active {
      box-shadow: 0 2rpx 8rpx rgba(59, 122, 255, 0.2);
    }
  }
}
</style> 
