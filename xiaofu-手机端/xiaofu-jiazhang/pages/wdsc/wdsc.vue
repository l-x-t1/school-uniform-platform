<template>
  <view class="container">
    <!-- 收藏列表 -->
    <scroll-view
      scroll-y
      class="collect-list"
      @scrolltolower="loadMore"
      refresher-enabled
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onRefresh"
      :refresher-threshold="80"
      :refresher-default-style="'black'"
    >
      <view class="list-content" v-if="collectList.length > 0">
        <view
          class="collect-item"
          v-for="(item, index) in collectList"
          :key="item.id || index"
          @click="viewCollectItem(item)"
        >
          <!-- 收藏项头部 -->
          <view class="collect-header">
            <text class="collect-title">{{ item.title}}</text> 
			<view>
				<uni-icons type="heart-filled" size="20" color="#ff6b6b" @click.native.stop="removeCollect(item)"></uni-icons>
			</view>
			
          </view>

          <!-- 收藏项信息 -->
          <view class="collect-info">
            <view class="collect-source" v-if="item.deptName">
              <text class="source-text">{{ item.deptName }}</text>
            </view>
            <view class="collect-time">
              <text class="time-label">收藏时间:</text>
              <text class="time-value">{{ item.createTime ? item.createTime.split(' ')[0] : '暂无' }}</text>
            </view>
          </view>

          <!-- 收藏项描述 -->
          <view class="collect-description" v-if="item.description || item.content">
            <text class="description-text">{{ item.description || item.content }}</text>
          </view> 
        </view>
        
        <!-- 加载更多提示 -->
        <view class="loading-more" v-if="collectList.length > 0">
          <view v-if="isLoading" class="loading-content">
            <uni-load-more status="loading" :content-text="loadingText"></uni-load-more>
          </view>
          <view v-else-if="!hasMore" class="loading-content">
            <uni-load-more status="noMore" :content-text="loadingText"></uni-load-more>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view class="empty-state" v-if="collectList.length == 0 && !isLoading">
        <view class="empty-icon">📋</view>
        <text class="empty-text">暂无收藏内容</text> 
      </view> 
    </scroll-view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      baseUrl: getApp().globalData.baseUrl,
      collectList: [],
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
    this.getCollectList();
  },
  onPullDownRefresh() {
    this.getCollectList();
  },
  methods: {
    // 获取收藏列表
    getCollectList() {
      if (this.isLoading) return;
      
      this.isLoading = true;
      this.hasError = false;
      
      uni.request({
        url: `${this.baseUrl}/school/user/collectList`,
        method: "GET",
        data: {
          userId: uni.getStorageSync("simId"),
          pageNum: this.page,
          pageSize: this.limit
        },
        header: {
          Authorization: uni.getStorageSync("token"),
        },
        success: ({ data }) => {
          if (data.code === 200) {
            if (this.page === 1) {
              this.collectList = data.rows || [];
            } else {
              this.collectList = [...this.collectList, ...(data.rows || [])];
            }
            
            // 判断是否还有更多数据
            this.hasMore = data.rows && data.rows.length >= this.limit;
            this.page++;
          } else {
            this.hasError = true;
            this.errorMessage = data.message || '获取收藏列表失败';
          }
          uni.stopPullDownRefresh();
        },
        fail: (err) => {
          this.hasError = true;
          this.errorMessage = err.message || '网络请求失败';
          uni.stopPullDownRefresh();
        },
        complete: () => {
          this.isLoading = false;
          this.isRefreshing = false;
        }
      });
    },

    // 切换标签
    switchTab(index) {
      this.currentTab = index;
      this.page = 1;
      this.hasMore = true;
      this.isLoading = false;
      this.isRefreshing = false;
      this.collectList = [];
      this.getCollectList();
    },

    // 加载更多
    loadMore() {
      if (this.isLoading || !this.hasMore) {
        return;
      }
      this.getCollectList();
    },

    // 下拉刷新
    onRefresh() {
      this.page = 1;
      this.hasMore = true;
      this.isLoading = false;
      this.isRefreshing = true;
      this.collectList = [];
      this.getCollectList();
    },

    // 重试加载
    retryLoad() {
      this.hasError = false;
      this.getCollectList();
    },

    // 查看收藏项详情
    viewCollectItem(item) {
		uni.navigateTo({
			url: `/pages/notice/notice-detail?id=${item.id}`,
		});
    },

    // 取消收藏
    removeCollect(item) {
      uni.showModal({
        title: '提示',
        content: '确定要取消收藏吗？',
        success: (res) => {
          if (res.confirm) {
            this.handleRemoveCollect(item);
          }
        }
      });
    },

    // 处理取消收藏
    handleRemoveCollect(item) {
      uni.request({
        url: `${this.baseUrl}/school/user/removeCollect`,
        method: "GET",
        data: {
          noticId: item.id,
          userId: uni.getStorageSync("simId")
        },
        header: {
          Authorization: uni.getStorageSync("token"),
        },
        success: ({ data }) => {
          if (data.code === 200) {
            uni.showToast({
              title: '取消收藏成功',
              icon: 'success'
            });
            // 从列表中移除该项
            this.collectList = this.collectList.filter(collect => collect.id !== item.id);
          } else {
            uni.showToast({
              title: data.message || '取消收藏失败',
              icon: 'none'
            });
          }
        },
        fail: (err) => {
          uni.showToast({
            title: '网络请求失败',
            icon: 'none'
          });
        }
      });
    },

    // 去逛逛
    goToBrowse() {
      uni.switchTab({
        url: '/pages/index/index'
      });
    },

    // 获取类型样式类
    getTypeClass(type) {
      const typeMap = {
        'uniform': 'type-uniform',
        'notice': 'type-notice',
        'survey': 'type-survey',
        'default': 'type-default'
      };
      return typeMap[type] || typeMap['default'];
    },

    // 获取类型文本
    getTypeText(type) {
      const typeMap = {
        'uniform': '校服',
        'notice': '公告',
        'survey': '调研',
        'default': '其他'
      };
      return typeMap[type] || typeMap['default'];
    }
  }
};
</script>

<style lang="scss">
.container {
  background-color: #f8f9fa;
  height: 100vh;
}

// 收藏列表
.collect-list {
  background-color: #f8f9fa;
  height: 100vh;

  .list-content {
    padding: 24rpx;
  }

  .collect-item {
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

    .collect-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 24rpx;

      .collect-title {
        flex: 1;
        font-size: 32rpx;
        color: #1a1a1a;
        font-weight: 600;
        line-height: 1.4;
        margin-right: 20rpx;
      }

      .collect-type {
        font-size: 22rpx;
        padding: 6rpx 16rpx;
        border-radius: 20rpx;
        font-weight: 500;
        white-space: nowrap;

        &.type-uniform {
          background-color: rgba(64, 158, 255, 0.1);
          color: #409eff;
        }

        &.type-notice {
          background-color: rgba(76, 175, 80, 0.1);
          color: #4caf50;
        }

        &.type-survey {
          background-color: rgba(255, 152, 0, 0.1);
          color: #ff9800;
        }

        &.type-default {
          background-color: rgba(158, 158, 158, 0.1);
          color: #9e9e9e;
        }
      }
    }

    .collect-info {
      margin-bottom: 20rpx;

      .collect-source {
        margin-bottom: 12rpx;

        .source-text {
          font-size: 26rpx;
          color: #409eff;
          background-color: rgba(64, 158, 255, 0.08);
          padding: 8rpx 20rpx;
          border-radius: 24rpx;
          font-weight: 500;
        }
      }

      .collect-time {
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

    .collect-description {
      margin-bottom: 24rpx;

      .description-text {
        font-size: 28rpx;
        color: #666;
        line-height: 1.5;
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 2;
        line-clamp: 2;
        overflow: hidden;
      }
    }

    .collect-action {
      display: flex;
      justify-content: space-between;
      gap: 20rpx;

      .action-btn {
        flex: 1;
        height: 72rpx;
        line-height: 72rpx;
        border-radius: 36rpx;
        font-size: 28rpx;
        font-weight: 500;
        padding: 0;
        transition: all 0.3s ease;
        border: none;

        &::after {
          border: none;
        }

        &.btn-primary {
          background: linear-gradient(135deg, #409eff 0%, #66b3ff 100%);
          color: #fff;
          box-shadow: 0 4rpx 12rpx rgba(64, 158, 255, 0.3);

          &:active {
            transform: translateY(2rpx);
            box-shadow: 0 2rpx 8rpx rgba(64, 158, 255, 0.2);
          }
        }

        &.btn-danger {
          background: linear-gradient(135deg, #ff7875 0%, #ff9c9c 100%);
          color: #fff;
          box-shadow: 0 4rpx 12rpx rgba(255, 120, 117, 0.3);

          &:active {
            transform: translateY(2rpx);
            box-shadow: 0 2rpx 8rpx rgba(255, 120, 117, 0.2);
          }
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
    margin-bottom: 40rpx;
  }

  .browse-btn {
    width: 240rpx;
    height: 72rpx;
    line-height: 72rpx;
    border-radius: 36rpx;
    font-size: 28rpx;
    font-weight: 500;
    background: linear-gradient(135deg, #409eff 0%, #66b3ff 100%);
    color: #fff;
    border: none;
    box-shadow: 0 4rpx 12rpx rgba(64, 158, 255, 0.3);
    transition: all 0.3s ease;

    &:active {
      transform: translateY(2rpx);
      box-shadow: 0 2rpx 8rpx rgba(64, 158, 255, 0.2);
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
    background: linear-gradient(135deg, #409eff 0%, #66b3ff 100%);
    color: #fff;
    border: none;
    box-shadow: 0 4rpx 12rpx rgba(64, 158, 255, 0.3);
    transition: all 0.3s ease;

    &:active {
      transform: translateY(2rpx);
      box-shadow: 0 2rpx 8rpx rgba(64, 158, 255, 0.2);
    }

    &::after {
      border: none;
    }
  }
}
</style>
