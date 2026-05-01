<template>
  <view class="container">
    <!-- 搜索框 -->
    <view class="search-container">
      <view class="search-box">
        <input 
          class="search-input" 
          type="text" 
          placeholder="请输入公告名称" 
          v-model="searchKeyword" 
          @confirm="onSearchConfirm"
        />
        <view class="search-btn" @click="onSearchConfirm">
          <!-- <text class="search-icon">🔍</text> -->
          <uni-icons type="search" size="20" color="#999"></uni-icons>
        </view>
      </view>
    </view>

    <!-- 顶部选项卡 -->
    <view class="tabs">
      <view
        class="tab-item"
        :class="{ active: currentTab === 0 }"
        @click="switchTab(0)"
      >
        采购公告
      </view>
      <view
        class="tab-item"
        :class="{ active: currentTab === 1 }"
        @click="switchTab(1)"
      >
        成交公告
      </view>
      <!-- <view
        class="tab-item"
        :class="{ active: currentTab === 2 }"
        @click="switchTab(2)"
      >
        备选公告
      </view> -->
    </view>

    <!-- 公告列表 -->
    <scroll-view
      scroll-y
      class="notice-list"
      @scrolltolower="loadMore"
      refresher-enabled
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onRefresh"
      :refresher-threshold="80"
      :refresher-default-style="'black'"
    >
      <view class="list-content" v-if="notices.length > 0">
        <view
          class="notice-item"
          v-for="(item, index) in notices"
          :key="item.id || index"
          @click="viewNotice(item)"
        >
          <!-- 公告标题 -->
          <view class="notice-header">
            <text class="notice-title">{{ item.title }}</text> 
          </view>

          <!-- 公告信息 -->
          <view class="notice-info"> 
            <view class="notice-time"> 
              <text class="source-text">{{ item.deptname }}</text> 
              <!-- <text class="time-label">发布时间:</text> -->
              <text class="time-value">{{ item.createTime ? item.createTime.split(' ')[0] : '暂无' }}</text>
            </view>
          </view>

          <!-- 操作按钮 -->
          <!-- <view class="notice-action" v-if="item.catagory === 'CGGG' || item.catagory === 'QYRWGG'">
            <button
              class="action-btn btn-primary"
              @click.stop="handleAction(item)"
            >
              查看详情
            </button>
          </view> -->
        </view>
        
        <!-- 加载更多提示 -->
        <view class="loading-more" v-if="notices.length > 0">
          <view v-if="isLoading" class="loading-content">
            <uni-load-more status="loading" :content-text="loadingText"></uni-load-more>
          </view>
          <view v-else-if="!hasMore" class="loading-content">
            <uni-load-more status="noMore" :content-text="loadingText"></uni-load-more>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view class="empty-state" v-if="notices.length == 0 && !isLoading">
        <view class="empty-icon">📋</view>
        <text class="empty-text">暂无公告</text> 
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
      notices: [],
      currentTab: 0, // 0 for 采购公告, 1 for 备选公告
      page: 1,
      limit: 10,
      isLoading: false,
      hasMore: true,
      isRefreshing: false,
      hasError: false,
      errorMessage: '',
      searchKeyword: '', // 搜索关键词
      searchTimer: null, // 搜索防抖定时器
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
  methods: {
    getList() {
      this.isLoading = true;
      this.hasError = false;
      
      const requestData = {
        simId: uni.getStorageSync("simId"),
        pageNum: this.page,
        pageSize: this.limit,
        catagory: this.currentTab === 0 ? 'CGGG' : this.currentTab === 1 ? 'CGGG' : 'QYRWGG',
        type: this.currentTab === 0 ? 0 : this.currentTab === 1 ? 3 : null,
      };
      
      // 如果有搜索关键词，添加到请求参数中
      if (this.searchKeyword && this.searchKeyword.trim()) {
        requestData.title = this.searchKeyword.trim();
      }
      
      uni.request({
        url: `${this.baseUrl}/system/xfptnotice/appList`,
        method: "GET",
        data: requestData,
        header: {
          Authorization: uni.getStorageSync("token"),
        },
        success: ({ data }) => {
          if (this.page === 1) {
            // 第一页或搜索时，替换数据
            this.notices = data.rows || [];
          } else {
            // 加载更多时，追加数据
            this.notices = [...this.notices, ...(data.rows || [])];
          }
          
          // 判断是否还有更多数据 - 使用total字段正确判断
          this.hasMore = this.notices.length < (data.total || 0);
          
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
    switchTab(index) {
      this.currentTab = index;
      this.page = 1; // 切换标签时重置页码
      this.hasMore = true;
      this.isLoading = false;
      this.isRefreshing = false;
      this.notices = []; // 清空列表
      this.getList();
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
      this.notices = [];
      this.getList();
    },
    retryLoad() {
      this.hasError = false;
      this.getList();
    },
    viewNotice(notice) {
      uni.navigateTo({
        url: `/pages/notice/notice-detail?id=${notice.id}`,
      });
    },
    handleAction(notice) {
      this.viewNotice(notice);
    },
    getStatusClass(item) {
      if (item.catagory === 'CGGG' && item.type === 0) {
        return 'status-purchase';
      } else if (item.catagory === 'CGGG' && item.type === 1) {
        return 'status-bid';
      } else if (item.catagory === 'QYRWGG') {
        return 'status-alternative';
      }
      return 'status-unknown';
    },
    getStatusText(item) {
      if (item.catagory === 'CGGG' && item.type === 0) {
        return '采购公告';
      } else if (item.catagory === 'CGGG' && item.type === 1) {
        return '中标公告';
      } else if (item.catagory === 'QYRWGG') {
        return '备选公告';
      }
      return '未知状态';
    },
    // 搜索输入处理（防抖）
    onSearchInput() {
      // 清除之前的定时器
      if (this.searchTimer) {
        clearTimeout(this.searchTimer);
      }
      
      // 设置新的定时器，500ms后执行搜索
      this.searchTimer = setTimeout(() => {
        this.performSearch();
      }, 500);
    },
    // 搜索确认
    onSearchConfirm() {
      // 清除定时器，立即执行搜索
      if (this.searchTimer) {
        clearTimeout(this.searchTimer);
      }
      this.performSearch();
    },
    // 执行搜索
    performSearch() {
      this.page = 1;
      this.hasMore = true;
      this.isLoading = false;
      this.isRefreshing = false;
      this.notices = []; // 清空列表
      this.getList();
    },
    // 清空搜索
    clearSearch() {
      this.searchKeyword = '';
      this.performSearch();
    }
  },
};
</script>

<style lang="scss">
.container { 
  background-color: #f8f9fa;
}

// 搜索框样式
.search-container {
  background-color: #fff;
  padding: 24rpx;
  border-bottom: 1rpx solid #e8eaed;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}

.search-box {
  display: flex;
  align-items: center;
  background-color: #f5f5f5;
  border-radius: 48rpx;
  padding: 0 24rpx;
  height: 80rpx;
  border: 2rpx solid transparent;
  transition: all 0.3s ease;

  &:focus-within {
    border-color: #3b7aff;
    background-color: #fff;
    box-shadow: 0 0 0 4rpx rgba(59, 122, 255, 0.1);
  }
}

.search-input {
  flex: 1;
  height: 100%;
  font-size: 28rpx;
  color: #333;
  background: transparent;
  border: none;
  outline: none;
  
  &::placeholder {
    color: #999;
    font-size: 26rpx;
  }
}

.search-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60rpx;
  height: 60rpx;
  margin-left: 16rpx;
  // background-color: #3b7aff;
  border-radius: 50%;
  transition: all 0.3s ease;

  &:active {
    transform: scale(0.95);
    background-color: #2c5ce6;
  }
}

.search-icon {
  font-size: 28rpx;
  color: #fff;
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

// 公告列表
.notice-list { 
  background-color: #f8f9fa;
  height: calc(100vh - 320rpx);

  .list-content {
    padding: 24rpx;
  }

  .notice-item {
    background-color: #fff;
    border-radius: 16rpx;
    padding: 32rpx 32rpx 8rpx;
    margin-bottom: 24rpx;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
    transition: all 0.3s ease;
    border: 1rpx solid #f0f0f0;

    &:active {
      transform: scale(0.98);
      box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
    }

    .notice-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 24rpx;

      .notice-title {
        flex: 1;
        font-size: 32rpx;
        color: #1a1a1a;
        font-weight: 600;
        line-height: 1.4;
        margin-right: 20rpx;
      }

      .notice-status {
        font-size: 22rpx;
        padding: 6rpx 16rpx;
        border-radius: 20rpx;
        font-weight: 500;
        white-space: nowrap;

        &.status-purchase {
          background-color: rgba(59, 122, 255, 0.1);
          color: #3b7aff;
        }

        &.status-bid {
          background-color: rgba(76, 175, 80, 0.1);
          color: #4caf50;
        }

        &.status-alternative {
          background-color: rgba(255, 152, 0, 0.1);
          color: #ff9800;
        }

        &.status-unknown {
          background-color: rgba(158, 158, 158, 0.1);
          color: #9e9e9e;
        }
      }
    }

    .notice-info {
      margin-bottom: 28rpx;

      .notice-source {
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

      .notice-time {
        display: flex;
        align-items: center;
        
        .source-text {
          font-size: 26rpx;
          color: #3b7aff;
          background-color: rgba(59, 122, 255, 0.08);
          padding: 8rpx 20rpx;
          border-radius: 24rpx;
          font-weight: 500;
        }

        .time-label {
          font-size: 26rpx;
          color: #999;
          margin-right: 8rpx;
        }

        .time-value {
          font-size: 26rpx;
          color: #666;
          font-weight: 500; 
          margin-left: 20rpx;
        }
      }
    }

    .notice-action {
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
