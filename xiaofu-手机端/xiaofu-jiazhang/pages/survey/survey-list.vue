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
        未开始
      </view>
      <view
        class="tab-item"
        :class="{ active: currentTab === 2 }"
        @click="switchTab(2)"
      >
        进行中
      </view>
      <view
        class="tab-item"
        :class="{ active: currentTab === 3 }"
        @click="switchTab(3)"
      >
        已结束
      </view>
    </view>

    <!-- 问卷列表 -->
    <scroll-view
      scroll-y
      class="survey-list"
      @scrolltolower="loadMore"
      refresher-enabled
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onRefresh"
      :refresher-threshold="80"
      :refresher-default-style="'black'"
    >
      <view class="list-content" v-if="filteredSurveys.length > 0">
        <view
          class="survey-item"
          v-for="(item, index) in filteredSurveys"
          :key="item.id || index"
          @click="goToSurveyDetail(item)"
        >
          <!-- 问卷标题 -->
          <view class="survey-header">
            <text class="survey-title">{{ item.title }}</text>
            <view class="survey-status" :class="getStatusClass(item.wjStatus)">
              {{ getStatusText(item.wjStatus) }}
            </view>
          </view>

          <!-- 问卷信息 -->
          <view class="survey-info">
            <view class="survey-source">
              <text class="source-text">{{ item.source }}</text>
            </view>
            <view class="survey-deadline">
              <text class="deadline-label">截止日期:</text>
              <text class="deadline-value">{{ item.endDate ? item.endDate.split(' ')[0] : '暂无' }}</text>
            </view>
          </view>

          <!-- 操作按钮 -->
          <!-- <view class="survey-action" v-if="item.wjStatus == 1">
            <button
              class="action-btn btn-primary"
              @click.stop="handleAction(item)"
            >
              进行投票
            </button>
          </view> -->
        </view>
        
        <!-- 加载更多提示 -->
        <view class="loading-more" v-if="filteredSurveys.length > 0">
          <view v-if="isLoading" class="loading-content">
            <uni-load-more status="loading" :content-text="loadingText"></uni-load-more>
          </view>
          <view v-else-if="!hasMore" class="loading-content">
            <uni-load-more status="noMore" :content-text="loadingText"></uni-load-more>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view class="empty-state" v-if="filteredSurveys.length == 0 && !isLoading">
        <view class="empty-icon">📋</view>
        <text class="empty-text">暂无问卷调查</text> 
      </view> 
    </scroll-view>
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
      surveyList: [],
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
  computed: {
    filteredSurveys() {
      switch (this.currentTab) {
        case 0: // 全部
          return this.surveyList;
        case 1: // 未开始
          return this.surveyList.filter((item) => item.wjStatus == 0);
        case 2: // 进行中
          return this.surveyList.filter((item) => item.wjStatus == 1);
        case 3: // 已结束
          return this.surveyList.filter((item) => item.wjStatus == 2);
        default:
          return this.surveyList;
      }
    },
  },
  onLoad() {
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
      this.surveyList = [];
      this.hasError = false;
      this.errorMessage = '';
    },
    
    // 获取问卷列表
    getList(isLoadMore = false) {
      if (this.isLoading) return;
      
      this.isLoading = true;
      this.hasError = false;
      
      // 如果不是加载更多，重置分页
      if (!isLoadMore) {
        this.resetPagination();
      }
      
      uni.request({
        url: `${this.baseUrl}/school/question/config/list2`,
        data: { 
			id:uni.getStorageSync('simId'),
			pageNum: this.pageNum,
			pageSize: this.pageSize
        },
        header: {
          Authorization: uni.getStorageSync("token"),
        },
        success: (res) => {
          this.isLoading = false;
          
          if (res.statusCode === 200 && res.data.code === 200) {
            const { rows, total } = res.data;
            
            // 处理数据
            const processedData = rows.map((item) => {
              return {
                id: item.id,
                title: item.title,
                description: item.votingDescription,
                source: item.createBy || '未知来源',
                startDate: item.beginTime,
                endDate: item.endTime,
                wjStatus: parseInt(item.wjStatus),
              };
            });
            
            // 更新数据
            if (isLoadMore) {
              this.surveyList = [...this.surveyList, ...processedData];
            } else {
              this.surveyList = processedData;
            }
            
            this.total = total;
            
            // 判断是否还有更多数据
            this.hasMore = this.surveyList.length < total;
            
            // 如果是加载更多且没有新数据，显示提示
            if (isLoadMore && processedData.length === 0) {
              uni.showToast({
                title: '没有更多数据了',
                icon: 'none'
              });
            }
          } else {
            this.handleError('获取数据失败');
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

    goToSurveyDetail(item) {
      if(item.wjStatus == 1) {
        uni.navigateTo({
          url: `/pages/survey/survey-detail?id=${item.id}`,
        });
      }else if(item.wjStatus == 0) {
        uni.showToast({
          title: "问卷尚未开始",
          icon: "none",
        })
      }else if(item.wjStatus == 2) {
        uni.showToast({
          title: "问卷已结束",
          icon: "none",
        })
      }
    },

    handleAction(item) {
      // 只有进行中的问卷才会显示按钮，直接跳转到问卷详情
      uni.navigateTo({
        url: `/pages/survey/survey-detail?id=${item.id}`,
      });
    },

    getStatusText(wjStatus) {
      switch (wjStatus) {
        case 0:
          return "未开始";
        case 1:
          return "进行中";
        case 2:
          return "已结束";
        default:
          return "未知状态";
      }
    },

    getStatusClass(wjStatus) {
      switch (wjStatus) {
        case 0:
          return "status-pending";
        case 1:
          return "status-ongoing";
        case 2:
          return "status-expired";
        default:
          return "status-unknown";
      }
    },


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

// 问卷列表
.survey-list { 
  background-color: #f8f9fa;
  height: calc(100vh - 200rpx);

  .list-content {
    padding: 24rpx;
  }

  .survey-item {
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

    .survey-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 24rpx;

      .survey-title {
        flex: 1;
        font-size: 32rpx;
        color: #1a1a1a;
        font-weight: 600;
        line-height: 1.4;
        margin-right: 20rpx;
      }

      .survey-status {
        font-size: 22rpx;
        padding: 6rpx 16rpx;
        border-radius: 20rpx;
        font-weight: 500;
        white-space: nowrap;

        &.status-ongoing {
          background-color: rgba(59, 122, 255, 0.1);
          color: #3b7aff;
        }

        &.status-pending {
          background-color: rgba(255, 152, 0, 0.1);
          color: #ff9800;
        }

        &.status-expired {
          background-color: rgba(149, 165, 166, 0.1);
          color: #95a5a6;
        }

        &.status-unknown {
          background-color: rgba(158, 158, 158, 0.1);
          color: #9e9e9e;
        }
      }
    }

    .survey-info {
      margin-bottom: 28rpx;

      .survey-source {
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

      .survey-deadline {
        display: flex;
        align-items: center;

        .deadline-label {
          font-size: 26rpx;
          color: #999;
          margin-right: 8rpx;
        }

        .deadline-value {
          font-size: 26rpx;
          color: #666;
          font-weight: 500;
        }
      }
    }

    .survey-action {
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
