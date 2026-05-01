<template>
  <view class="container">
    <!-- 顶部选项卡 -->
    <view class="tabs">
      <view
        class="tab-item"
        :class="{ active: currentTab === 0 }"
        @click="switchTab(0)"
      >
        全部
      </view>
      <view
        class="tab-item"
        :class="{ active: currentTab === 1 }"
        @click="switchTab(1)"
      >
        待回复
      </view>
      <view
        class="tab-item"
        :class="{ active: currentTab === 2 }"
        @click="switchTab(2)"
      >
        已回复
      </view>
    </view>

    <!-- 反馈列表 -->
    <scroll-view
      scroll-y
      class="feedback-list"
      @scrolltolower="loadMore"
      refresher-enabled
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onRefresh"
      :refresher-threshold="80"
      :refresher-default-style="'black'"
    >
      <view class="list-content" v-if="feedbackList.length > 0">
        <view
          class="feedback-item"
          v-for="(item, index) in feedbackList"
          :key="item.id || index"
          @click="goToFeedbackDetail(item)"
        >
          <!-- 反馈标题和状态 -->
          <view class="feedback-header">
            <text class="feedback-title">{{ item.feedbackContent }}</text>
            <view class="feedback-status" :class="getStatusClass(item.status)">
              {{ getStatusText(item.status) }}
            </view>
          </view>

          <!-- 反馈信息 -->
          <view class="feedback-info">
            <view class="feedback-source">
              <text class="source-text">{{ item.deptName }}</text>
            </view>
            <view class="feedback-time">
              <text class="time-label">提交时间:</text>
              <text class="time-value">{{ item.createTime }}</text>
            </view>
          </view>

          <!-- 操作按钮 -->
          <!-- <view class="feedback-action" v-if="item.status == 0">
            <button
              class="action-btn"
              @click.stop="handleAction(item)"
            >
              查看详情
            </button>
          </view> -->
        </view>
        
        <!-- 加载更多提示 -->
        <view class="loading-more" v-if="feedbackList.length > 0">
          <view v-if="isLoading" class="loading-content">
            <uni-load-more status="loading" :content-text="loadingText"></uni-load-more>
          </view>
          <!-- <view v-else-if="!hasMore && " class="loading-content">
            <uni-load-more status="noMore" :content-text="loadingText"></uni-load-more>
          </view> -->
        </view>
      </view>

      <!-- 空状态 -->
      <view class="empty-state" v-if="feedbackList.length == 0 && !isLoading">
        <view class="empty-icon">💬</view>
        <text class="empty-text">暂无反馈记录</text>
        <text class="empty-subtext">当前分类下没有反馈记录</text>
        <!-- <button class="submit-btn" @click="goToFeedback">提交反馈</button> -->
      </view>
      
      <!-- 错误状态 -->
      <view class="error-state" v-if="hasError && !isLoading">
        <view class="error-icon">⚠️</view>
        <text class="error-text">{{ errorMessage }}</text>
        <button class="retry-btn" @click="retryLoad">重新加载</button>
      </view>
    </scroll-view>

    <!-- 浮动按钮 -->
    <!-- <view class="float-btn" @click="goToFeedback">
      <uni-icons type="plus" size="24" color="#fff"></uni-icons>
    </view> -->
  </view>
</template>

<script>
export default {
  data() {
    return {
      baseUrl: getApp().globalData.baseUrl,
      currentTab: 0,
      isRefreshing: false,
      isLoading: false,
      hasMore: true,
      hasError: false,
      errorMessage: '',
      feedbackList: [],
      // 分页参数
      pageNum: 1,
      pageSize: 10,
      total: 0,
      // 加载更多文本配置
      loadingText: {
        contentdown: '上拉显示更多',
        contentrefresh: '正在加载...',
        contentnomore: '没有更多数据了'
      },
      // 防抖定时器
      loadMoreTimer: null
    };
  }, 
  onLoad(options) {
    if (options.status) {
      this.currentTab = Number(options.status);
    }
    this.getList();
  },
  onShow() {
    // 页面显示时刷新数据
    this.getList();
  },
  onUnload() {
    // 清理定时器
    if (this.loadMoreTimer) {
      clearTimeout(this.loadMoreTimer);
    }
  },
  methods: {
    switchTab(index) {
      if (this.currentTab === index) return;
      
      this.currentTab = index;
      // 切换标签时重置分页并重新加载
      this.resetPagination();
      this.getList();
    },
    
    // 重置分页参数
    resetPagination() {
      this.pageNum = 1;
      this.hasMore = true;
      this.feedbackList = [];
      this.hasError = false;
      this.errorMessage = '';
    },
    
    // 获取反馈列表
    getList(isLoadMore = false) {
      if (this.isLoading) return;
      
      this.isLoading = true;
      this.hasError = false;
      
      // 如果不是加载更多，重置分页
      if (!isLoadMore) {
        this.resetPagination();
      }
      
      // 构建请求参数
      const requestData = {
        userId: uni.getStorageSync("simId"),
        pageNum: this.pageNum,
        pageSize: this.pageSize,
      };

      // 根据筛选条件添加状态参数
      if (this.currentTab === 1) {
        requestData.replyState = 0; // 待回复
      } else if (this.currentTab === 2) {
        requestData.replyState = 1; // 已回复
      }
      // 'all' 不添加状态参数，获取全部
      
      uni.request({
        url: `${this.baseUrl}/school/parentsFeedback/list2`,
        method: "GET",
        header: {
          Authorization: uni.getStorageSync("token"),
          "Content-Type": "application/json",
        },
        data: requestData,
        success: (res) => {
          this.isLoading = false;
          
          if (res.statusCode === 200 && res.data.code === 200) {
            const { rows, total } = res.data;
            
            // 处理数据
            const processedData = (rows || []).map((item) => {
              console.log('原始数据项:', item);
              console.log('replyState 原始值:', item.replyState, '类型:', typeof item.replyState);
              
              return {
                id: item.id,
                feedbackContent: this.truncateText(item.feedbackContent || "暂无内容", 50),
                deptName: item.deptName || item.schoolName || "未知学校",
                status: parseInt(item.replyState) || 0,
                createTime: this.formatTime(item.createTime || item.createDate),
              };
            });
            
            // 更新数据
            if (isLoadMore) {
              this.feedbackList = [...this.feedbackList, ...processedData];
            } else {
              this.feedbackList = processedData;
            }
            
            this.total = total || 0;
            
            // 判断是否还有更多数据
            this.hasMore = this.feedbackList.length < this.total;
            
            // 如果是加载更多且没有新数据，显示提示
            if (isLoadMore && processedData.length === 0) {
              uni.showToast({
                title: '没有更多数据了',
                icon: 'none'
              });
            }
          } else {
            this.handleError(res.data?.message || '获取数据失败');
          }
        },
        fail: (err) => {
          this.isLoading = false;
          this.handleError('网络请求失败，请检查网络连接');
          console.error('请求失败:', err);
        }
      });
    },

    // 下拉刷新
    onRefresh() {
      this.isRefreshing = true;
      
      // 重置分页参数
      this.resetPagination();
      
      // 重新获取数据
      this.getList();
      
      // 延迟关闭刷新状态
      setTimeout(() => {
        this.isRefreshing = false;
        if (!this.hasError) {
          uni.showToast({
            title: "刷新成功",
            icon: "success",
            duration: 1500
          });
        }
      }, 1000);
    },

    // 触底加载更多
    loadMore() {
      if (!this.hasMore || this.isLoading || this.isRefreshing) {
        return;
      }
      
      // 防抖处理
      if (this.loadMoreTimer) {
        clearTimeout(this.loadMoreTimer);
      }
      
      this.loadMoreTimer = setTimeout(() => {
        this.pageNum++;
        this.getList(true);
      }, 300);
    },
    
    // 处理错误
    handleError(message) {
      this.hasError = true;
      this.errorMessage = message;
      this.isLoading = false;
    },
    
    // 重试加载
    retryLoad() {
      this.hasError = false;
      this.errorMessage = '';
      this.getList();
    },

    goToFeedbackDetail(item) {
      uni.navigateTo({
        url: `/pages/feedback/feedback-detail?id=${item.id}`,
      });
    },

    handleAction(item) {
      // 跳转到反馈详情
      uni.navigateTo({
        url: `/pages/feedback/feedback-detail?id=${item.id}`,
      });
    },

    getStatusText(status) {
      console.log('状态值:', status, '类型:', typeof status);
      // 确保状态值是数字类型
      const statusNum = parseInt(status); 
      
      switch (statusNum) {
        case 0:
          return "待回复";
        case 1:
          return "已回复";
        default:
          return "未知状态";
      }
    },

    getStatusClass(status) {
      // 确保状态值是数字类型
      const statusNum = parseInt(status);
      
      switch (statusNum) {
        case 0:
          return "status-pending";
        case 1:
          return "status-replied";
        default:
          return "status-unknown";
      }
    },

    // 截断文本
    truncateText(text, maxLength) {
      if (!text) return "暂无内容";
      if (text.length <= maxLength) return text;
      return text.substring(0, maxLength) + "...";
    },

    // 格式化时间
    formatTime(time) {
      if (!time) return "未知时间";
      
      try {
        const date = new Date(time);
        const now = new Date();
        const diff = now - date;

        if (diff < 24 * 60 * 60 * 1000) {
          return "今天";
        } else if (diff < 2 * 24 * 60 * 60 * 1000) {
          return "昨天";
        } else {
          const month = String(date.getMonth() + 1).padStart(2, "0");
          const day = String(date.getDate()).padStart(2, "0");
          return `${month}-${day}`;
        }
      } catch (error) {
        console.error('时间格式化错误:', error);
        return "未知时间";
      }
    },

    // 跳转到反馈页面
    goToFeedback() {
      uni.navigateTo({
        url: "/pages/feedback/feedback",
      });
    },
  },
};
</script>

<style lang="scss">
.container {
  display: flex;
  flex-direction: column;
  height: 100%; 
}

// 顶部选项卡
.tabs {
  display: flex;
  height: 88rpx;
  background-color: #fff;
  border-bottom: 1rpx solid #e8eaed;
  position: relative;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);

  .tab-item {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 28rpx;
    color: #666;
    position: relative;
    transition: all 0.3s ease;
    font-weight: 400;

    &.active {
      color: #3b7aff;
      font-weight: 600;

      &::after {
        content: "";
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translateX(-50%);
        width: 48rpx;
        height: 4rpx;
        background-color: #3b7aff;
        border-radius: 2rpx;
        transition: all 0.3s ease;
      }
    }

    &:active {
      opacity: 0.7;
    }
  }
}

// 反馈列表
.feedback-list { 
  height: calc(100vh - 200rpx);

  .list-content {
    padding: 24rpx;
  }

  .feedback-item {
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

    .feedback-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 24rpx;

      .feedback-title {
        flex: 1;
        font-size: 30rpx;
        color: #1a1a1a;
        font-weight: 600;
        line-height: 1.4;
        margin-right: 20rpx;
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 2;
        overflow: hidden;
      }

      .feedback-status {
        font-size: 22rpx;
        padding: 6rpx 16rpx;
        border-radius: 20rpx;
        font-weight: 500;
        white-space: nowrap;

        &.status-replied {
          background-color: rgba(82, 196, 26, 0.1);
          color: #52c41a;
        }

        &.status-pending {
          background-color: rgba(255, 120, 117, 0.1);
          color: #ff7875;
        }

        &.status-unknown {
          background-color: rgba(158, 158, 158, 0.1);
          color: #9e9e9e;
        }
      }
    }

    .feedback-info {
      margin-bottom: 28rpx;

      .feedback-source {
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

      .feedback-time {
        display: flex;
        align-items: center;

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
    }

    .feedback-action {
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
    font-size: 32rpx;
    color: #666;
    font-weight: 500;
    margin-bottom: 16rpx;
  }

  .empty-subtext {
    font-size: 26rpx;
    color: #999;
    text-align: center;
    line-height: 1.5;
    margin-bottom: 40rpx;
  }

  .submit-btn {
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

// 浮动按钮
.float-btn {
  position: fixed;
  right: 40rpx;
  bottom: 40rpx;
  width: 100rpx;
  height: 100rpx;
  border-radius: 50rpx;
  background: linear-gradient(135deg, #3b7aff 0%, #5b8cff 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 8rpx 32rpx rgba(59, 122, 255, 0.4);
  z-index: 999;
  transition: all 0.3s ease;

  &:active {
    transform: scale(0.95) translateY(2rpx);
    box-shadow: 0 6rpx 24rpx rgba(59, 122, 255, 0.3);
  }
}
</style>
