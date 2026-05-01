<template>
  <view class="container">
    <!-- 质检报告列表 -->
    <scroll-view
      scroll-y
      class="report-list"
      @scrolltolower="loadMore"
      refresher-enabled
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onRefresh"
      :refresher-threshold="80"
      :refresher-default-style="'black'"
    >
      <view class="list-content" v-if="reports.length > 0">
        <view
          class="report-item"
          v-for="(item, index) in reports"
          :key="item.id || index"
          @click="viewReport(item)"
        >
          <!-- 报告标题 -->
          <view class="report-header">
            <text class="report-title">{{ item.ggTitle || '质检报告'}}</text> 
          </view>

          <!-- 报告信息 -->
          <view class="report-info"> 
            <view class="report-source">
              <text class="source-text">{{ item.deptName}}</text>
              <text class="source-text" style="margin-left: 10rpx;">{{ item.supplierName}}</text>
            </view>
            <view class="report-time" > 
              <text class="time-value">{{ item.createTime.split(' ')[0] || '暂无' }}</text>
            </view> 
          </view>

          <!-- 操作按钮 --> 
        </view>
        
        <!-- 加载更多提示 -->
        <view class="loading-more" v-if="reports.length > 0">
          <view v-if="isLoading" class="loading-content">
            <uni-load-more status="loading" :content-text="loadingText"></uni-load-more>
          </view>
          <view v-else-if="!hasMore" class="loading-content">
            <uni-load-more status="noMore" :content-text="loadingText"></uni-load-more>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view class="empty-state" v-if="reports.length == 0 && !isLoading">
        <view class="empty-icon">📋</view>
        <text class="empty-text">暂无质检报告</text>
        <text class="empty-subtext">当前没有质检报告数据</text>
      </view>
      
      <!-- 错误状态 -->
      <view class="error-state" v-if="hasError && !isLoading">
        <view class="error-icon">⚠️</view>
        <text class="error-text">{{ errorMessage }}</text>
        <button class="retry-btn" @click="retryLoad">重新加载</button>
      </view>
    </scroll-view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      baseUrl: getApp().globalData.baseUrl,
      reports: [],
      page: 1,
      limit: 10,
      isLoading: false,
      hasMore: true,
      isRefreshing: false,
      hasError: false,
      errorMessage: '',
      loadingText: {
        contentdown: '加载中...',
        contentrefresh: '加载中...',
        contentnomore: '没有更多了'
      }
    };
  }, 
  created() {
    this.getList();
  },
  onPullDownRefresh() { 
    this.getList();
  },
  methods: {
    getList() {
      this.isLoading = true;
      this.hasError = false;
      
      const requestData = {
        simId: uni.getStorageSync("simId"),
        pageNum: this.page,
        pageSize: this.limit,
      };
      
      uni.request({
        url: `${this.baseUrl}/school/suppliceInspectionReport/getNoticeInspect`,
        method: "GET",
        data: requestData,
        header: {
          Authorization: uni.getStorageSync("token"),
        },
        success: ({ data }) => {
          if (this.page === 1) {
            // 第一页时，替换数据
            this.reports = data.rows || [];
          } else {
            // 加载更多时，追加数据
            this.reports = [...this.reports, ...(data.rows || [])];
          }
          
          // 判断是否还有更多数据
          this.hasMore = this.reports.length < (data.total || 0);
          
          // 只有在成功获取数据后才递增页码
          if (data.rows && data.rows.length > 0) {
            this.page++;
          }
          
          this.isLoading = false;
          this.isRefreshing = false;
          uni.stopPullDownRefresh(); 
        },
        fail: (err) => {
          this.hasError = true;
          this.errorMessage = err.message || '加载失败';
          this.isLoading = false;
          this.isRefreshing = false;
          uni.stopPullDownRefresh();
        }
      });
    },
    loadMore() {
      if (this.isLoading || !this.hasMore) {
        return;
      }
      this.isLoading = true;
      this.getList();
    },
    onRefresh() {
      this.page = 1;
      this.hasMore = true;
      this.isLoading = false;
      this.isRefreshing = true;
      this.reports = [];
      this.getList();
    },
    retryLoad() {
      this.hasError = false;
      this.getList();
    },
    viewReport(report) {
      // 跳转到质检报告详情页面
      uni.navigateTo({
        url: `/pages/uniform/quality-report?id=${report.id}`,
      });
    },
    getStatusClass(status) {
      // 根据质检状态返回对应的样式类
      if (status === 1 || status === '1') {
        return 'status-qualified';
      } else if (status === 0 || status === '0') {
        return 'status-unqualified';
      }
      return 'status-unknown';
    },
    getStatusText(status) {
      // 根据质检状态返回对应的文本
      if (status === 1 || status === '1') {
        return '合格';
      } else if (status === 0 || status === '0') {
        return '不合格';
      }
      return '未知';
    }
  },
};
</script>

<style lang="scss">
.container { 
  background-color: #f8f9fa; 
}

// 质检报告列表
.report-list { 
  background-color: #f8f9fa;
  height: calc(100vh - 90rpx);

  .list-content {
    padding: 24rpx;
  }

  .report-item {
    background-color: #fff;
    border-radius: 16rpx;
    padding: 32rpx;
    margin-bottom: 24rpx;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
    transition: all 0.3s ease;
    border: 1rpx solid #f0f0f0;

    &:active {
      transform: scale(0.98);
      box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
    }

    .report-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 24rpx;

      .report-title {
        flex: 1;
        font-size: 32rpx;
        color: #1a1a1a;
        font-weight: 600;
        line-height: 1.4;
        margin-right: 20rpx;
      }

      .report-status {
        font-size: 22rpx;
        padding: 6rpx 16rpx;
        border-radius: 20rpx;
        font-weight: 500;
        white-space: nowrap;

        &.status-qualified {
          background-color: rgba(82, 196, 26, 0.1);
          color: #52c41a;
        }

        &.status-unqualified {
          background-color: rgba(255, 77, 79, 0.1);
          color: #ff4d4f;
        }

        &.status-unknown {
          background-color: rgba(158, 158, 158, 0.1);
          color: #9e9e9e;
        }
      }
    }

    .report-info {  
      .report-source {
        margin-bottom: 16rpx;

        .source-text {
          font-size: 26rpx;
          color: #3b7aff;
          background-color: rgba(59, 122, 255, 0.08);
          padding: 8rpx 20rpx;
          border-radius: 24rpx;
          font-weight: 500;
        }
      }

      .report-time {
        display: flex;
        align-items: center;
        margin-bottom: 12rpx;

        .time-label {
          font-size: 26rpx;
          color: #999;
          margin-right: 8rpx;
        }

        .time-value {
          font-size: 26rpx;
          color: #666;
          font-weight: 500;
        }
      }

      .report-details {
        display: flex;
        align-items: center;
        margin-bottom: 8rpx;

        .detail-label {
          font-size: 24rpx;
          color: #999;
          margin-right: 8rpx;
          min-width: 120rpx;
        }

        .detail-value {
          font-size: 24rpx;
          color: #666;
          font-weight: 500;
          flex: 1;
        }
      }
    }

    .report-action {
      display: flex;
      justify-content: center;

      .action-btn {
        width: 240rpx;
        height: 72rpx;
        line-height: 72rpx;
        border-radius: 36rpx;
        font-size: 28rpx;
        font-weight: 500;
        padding: 0;
        transition: all 0.3s ease;
        border: none;
        background: linear-gradient(135deg, #3b7aff 0%, #5b8cff 100%);
        color: #fff;
        box-shadow: 0 4rpx 12rpx rgba(59, 122, 255, 0.3);

        &::after {
          border: none;
        }

        &:active {
          transform: translateY(2rpx);
          box-shadow: 0 2rpx 8rpx rgba(59, 122, 255, 0.2);
        }
      }
    }
  }
}

// 加载更多
.loading-more {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40rpx 0;

  .loading-content {
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
  }
}

// 空状态
.empty-state {
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
    margin-bottom: 16rpx;
    margin-top: 20rpx;
  }

  .empty-subtext {
    font-size: 26rpx;
    color: #999;
    text-align: center;
    line-height: 1.5;
  }
}

// 错误状态
.error-state {
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
</style>
