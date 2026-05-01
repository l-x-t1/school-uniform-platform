<template>
  <view class="container">  
    <!-- 顶部选项卡 -->
    <view class="tabs">
      <view
        class="tab-item"
        :class="{ active: currentTab === 0 }"
        @click="switchTab(0)"
      >
        待审核
      </view>
      <view
        class="tab-item"
        :class="{ active: currentTab === 1 }"
        @click="switchTab(1)"
      >
        已通过
      </view>
      <view
        class="tab-item"
        :class="{ active: currentTab === 2 }"
        @click="switchTab(2)"
      >
        未通过
      </view>
    </view>

    <!-- 校服回收列表 -->
    <scroll-view
      scroll-y
      class="recycle-list"
      @scrolltolower="loadMore"
      refresher-enabled
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onRefresh"
      :refresher-threshold="80"
      :refresher-default-style="'black'"
    >
      <view class="list-content" v-if="recycleList.length > 0">
        <view
          class="recycle-item"
          v-for="(item, index) in recycleList"
          :key="item.id || index"
          @click="viewRecycleDetail(item)"
        >
          <!-- 校服信息 -->
          <view class="recycle-header">
            <text class="recycle-title">{{ item.schoolName || '校服名称' }}</text> 
          </view>

          <!-- 详细信息 -->
          <view class="recycle-info">
            <view class="info-row">
              <text class="info-label">学生姓名:</text>
              <text class="info-value">{{ item.name || '暂无' }}</text>
            </view>
            <view class="info-row">
              <text class="info-label">学生班级:</text>
              <text class="info-value">{{ item.className || '暂无' }}</text>
            </view>
            <view class="info-row">
              <text class="info-label">提交时间:</text>
              <text class="info-value">{{ item.createTime || '暂无' }}</text>
            </view> 
          </view> 
        </view>
        
        <!-- 加载更多提示 -->
        <view class="loading-more" v-if="recycleList.length > 0">
          <view v-if="isLoading" class="loading-content">
            <uni-load-more status="loading" :content-text="loadingText"></uni-load-more>
          </view>
          <view v-else-if="!hasMore" class="loading-content">
            <uni-load-more status="noMore" :content-text="loadingText"></uni-load-more>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view class="empty-state" v-if="recycleList.length == 0 && !isLoading">
        <view class="empty-icon">👕</view>
        <text class="empty-text">暂无校服回收记录</text>  
      </view> 
    </scroll-view>

    <!-- 浮动按钮 -->
    <view class="float-btn" @click="goToForm">
      <uni-icons type="plus" size="24" color="#fff"></uni-icons>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      baseUrl: getApp().globalData.baseUrl,
      recycleList: [],
      currentTab: 0, // 0: 待审核, 1: 已通过, 2: 未通过
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
  onShow() {
	this.page = 1;
	this.hasMore = true;
	this.isLoading = false;
	this.isRefreshing = false;
	this.recycleList = [];
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
		status: this.currentTab == 0?2:this.currentTab == 1?1:0
      };
      
      uni.request({
        url: `${this.baseUrl}/school/ljhs/list`,
        method: "GET",
        data: requestData,
        header: {
          Authorization: uni.getStorageSync("token"),
        },
        success: ({ data }) => {
          if (this.page === 1) {
            // 第一页或搜索时，替换数据
            this.recycleList = data.rows || [];
          } else {
            // 加载更多时，追加数据
            this.recycleList = [...this.recycleList, ...(data.rows || [])];
          }
          
          // 判断是否还有更多数据
          this.hasMore = this.recycleList.length < (data.total || 0);
          
          // 页码已在请求前递增，这里不需要再递增
          
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
      this.recycleList = []; // 清空列表
      this.getList();
    },
    loadMore() {
      if (this.isLoading || !this.hasMore) {
        return;
      }
      this.page++;
      this.isLoading = true;
      this.getList();
    },
    onRefresh() {
      this.page = 1;
      this.hasMore = true;
      this.isLoading = false;
      this.isRefreshing = true; // 设置下拉刷新状态
      this.recycleList = [];
      this.getList();
    },
    retryLoad() {
      this.hasError = false;
      this.getList();
    },
    viewRecycleDetail(item) {
      uni.navigateTo({
        url: `/pages/xfhs/detial?id=${item.id}`,
      });
    },
    getStatusClass(status) {
      switch (status) {
        case 0:
          return 'status-pending';
        case 1:
          return 'status-completed';
        default:
          return 'status-unknown';
      }
    },
    getStatusText(status) {
      switch (status) {
        case 0:
          return '待回收';
        case 1:
          return '已回收';
        default:
          return '未知状态';
      }
    },
    // 跳转到表单页面
    goToForm() {
      uni.navigateTo({
        url: '/pages/xfhs/form'
      });
    }
  },
};
</script>

<style lang="scss">
.container { 
  background-color: #f8f9fa;
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

// 校服回收列表
.recycle-list { 
  background-color: #f8f9fa;
  height: calc(100vh - 180rpx);

  .list-content {
    padding: 24rpx;
  }

  .recycle-item {
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

    .recycle-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 24rpx;

      .recycle-title {
        flex: 1;
        font-size: 32rpx;
        color: #1a1a1a;
        font-weight: 600;
        line-height: 1.4;
        margin-right: 20rpx;
      }

    }

    .recycle-info {
      margin-bottom: 28rpx;

      .info-row {
        display: flex;
        align-items: center;
        margin-bottom: 12rpx;

        .info-label {
          font-size: 26rpx;
          color: #999;
          margin-right: 16rpx;
          min-width: 140rpx;
        }

        .info-value {
          font-size: 26rpx;
          color: #666;
          font-weight: 500;
          flex: 1;
        }
      }
    }

    .recycle-action {
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
