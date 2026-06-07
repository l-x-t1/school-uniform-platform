<template>
  <view class="container">
    <view class="search-container">
      <view class="search-box">
        <uni-icons type="search" size="20" color="#8a8f99"></uni-icons>
        <input
          class="search-input"
          type="text"
          placeholder="搜索公告名称"
          v-model="searchKeyword"
          @input="onSearchInput"
          @confirm="onSearchConfirm"
        />
        <uni-icons
          v-if="searchKeyword"
          type="clear"
          size="18"
          color="#b8bec8"
          @click="clearSearch"
        ></uni-icons>
      </view>
    </view>

    <scroll-view
      scroll-y
      class="notice-list"
      @scrolltolower="loadMore"
      refresher-enabled
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onRefresh"
    >
      <view class="list-content" v-if="notices.length > 0">
        <view
          class="notice-item"
          v-for="(item, index) in notices"
          :key="item.id || index"
          @click="viewNotice(item)"
        >
          <view class="notice-main">
            <view class="notice-tag">{{ getStatusText(item) }}</view>
            <text class="notice-title">{{ item.title }}</text>
            <view class="notice-info">
              <text class="notice-source">{{ item.schoolName || item.deptname || item.createBy || '校服管理平台' }}</text>
              <text class="notice-time">{{ formatDate(item.publishTime || item.resultPublishTime || item.createTime) }}</text>
            </view>
          </view>
          <uni-icons type="right" size="16" color="#c8ccd4"></uni-icons>
        </view>

        <view class="loading-more" v-if="isLoading || !hasMore">
          <uni-load-more
            :status="isLoading ? 'loading' : 'noMore'"
            :content-text="loadingText"
          ></uni-load-more>
        </view>
      </view>

      <view class="empty-state" v-if="notices.length === 0 && !isLoading">
        <image src="/static/uni.png" mode="aspectFit" class="empty-image"></image>
        <text class="empty-text">暂无公告</text>
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
      page: 1,
      limit: 10,
      isLoading: false,
      hasMore: true,
      isRefreshing: false,
      searchKeyword: "",
      searchTimer: null,
      loadingText: {
        contentdown: "加载更多",
        contentrefresh: "加载中",
        contentnomore: "没有更多了",
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.isLoading = true;

      const requestData = {
        simId: uni.getStorageSync("simId"),
        pageNum: this.page,
        pageSize: this.limit,
        noticeType: "2",
      };

      if (this.searchKeyword && this.searchKeyword.trim()) {
        requestData.title = this.searchKeyword.trim();
      }

      uni.request({
        url: `${this.baseUrl}/school/groupNotice/list`,
        method: "GET",
        data: requestData,
        header: {
          Authorization: uni.getStorageSync("token"),
        },
        success: ({ data }) => {
          const rows = data && Array.isArray(data.rows) ? data.rows : [];
          if (rows.length) {
            this.mergeRows(rows.map(this.normalizeNotice), data.total);
          } else {
            this.hasMore = false;
          }
          this.finishLoading();
        },
        fail: () => {
          this.notices = this.page === 1 ? [] : this.notices;
          this.hasMore = false;
          this.finishLoading();
        },
      });
    },
    normalizeNotice(row) {
      return {
        id: row.id,
        title: row.title || "",
        schoolName: row.schoolName || "",
        noticeType: row.noticeType,
        publishTime: row.publishTime,
        resultPublishTime: row.resultPublishTime,
        createTime: row.createTime,
      };
    },
    mergeRows(rows, total) {
      if (this.page === 1) {
        this.notices = rows;
      } else {
        this.notices = this.notices.concat(rows);
      }
      this.hasMore = this.notices.length < (total || rows.length);
      if (rows.length > 0) {
        this.page += 1;
      }
    },
    finishLoading() {
      this.isLoading = false;
      this.isRefreshing = false;
      uni.stopPullDownRefresh();
    },
    loadMore() {
      if (this.isLoading || !this.hasMore) return;
      this.getList();
    },
    onRefresh() {
      this.isRefreshing = true;
      this.resetList();
      this.getList();
    },
    resetList() {
      this.page = 1;
      this.hasMore = true;
      this.notices = [];
      this.isLoading = false;
    },
    viewNotice(notice) {
      uni.navigateTo({
        url: `/pages/notice/notice-detail?id=${encodeURIComponent(notice.id)}`,
      });
    },
    getStatusText(item) {
      return String(item.noticeType) === "2" ? "结果公告" : "公告";
    },
    formatDate(value) {
      if (!value) return "暂无时间";
      return String(value).split(" ")[0];
    },
    onSearchInput() {
      if (this.searchTimer) {
        clearTimeout(this.searchTimer);
      }
      this.searchTimer = setTimeout(() => {
        this.performSearch();
      }, 400);
    },
    onSearchConfirm() {
      if (this.searchTimer) {
        clearTimeout(this.searchTimer);
      }
      this.performSearch();
    },
    performSearch() {
      this.resetList();
      this.getList();
    },
    clearSearch() {
      this.searchKeyword = "";
      this.performSearch();
    },
  },
};
</script>

<style lang="scss">
.container {
  min-height: 100vh;
  background-color: #f5f7fb;
}

.search-container {
  background-color: #ffffff;
  padding: 24rpx 28rpx;
  border-bottom: 1rpx solid #eef0f4;
}

.search-box {
  height: 78rpx;
  padding: 0 24rpx;
  display: flex;
  align-items: center;
  gap: 14rpx;
  background-color: #f3f5f8;
  border-radius: 10rpx;
}

.search-input {
  flex: 1;
  height: 78rpx;
  font-size: 28rpx;
  color: #20242c;
}

.notice-list {
  height: calc(100vh - 88rpx);
}

.list-content {
  padding: 24rpx;
}

.notice-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
  padding: 28rpx 26rpx;
  margin-bottom: 20rpx;
  background-color: #ffffff;
  border-radius: 12rpx;
  border: 1rpx solid #eef0f4;
}

.notice-main {
  flex: 1;
  min-width: 0;
}

.notice-tag {
  display: inline-flex;
  align-items: center;
  height: 38rpx;
  padding: 0 14rpx;
  margin-bottom: 14rpx;
  border-radius: 6rpx;
  background-color: #edf4ff;
  color: #2f7df6;
  font-size: 22rpx;
}

.notice-title {
  display: block;
  font-size: 31rpx;
  line-height: 1.45;
  font-weight: 600;
  color: #1f2530;
}

.notice-info {
  display: flex;
  align-items: center;
  gap: 20rpx;
  margin-top: 18rpx;
}

.notice-source,
.notice-time {
  font-size: 24rpx;
  color: #7a8290;
}

.notice-source {
  max-width: 360rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.loading-more {
  padding: 12rpx 0 28rpx;
}

.empty-state {
  min-height: 520rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.empty-image {
  width: 180rpx;
  height: 180rpx;
  opacity: 0.55;
  margin-bottom: 20rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #8a8f99;
}
</style>
