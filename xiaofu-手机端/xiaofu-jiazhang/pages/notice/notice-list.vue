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
              <text class="notice-source">{{ item.deptname || item.createBy || '校服管理平台' }}</text>
              <text class="notice-time">{{ formatDate(item.createTime) }}</text>
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
const MOCK_NOTICES = [
  {
    id: "notice-1",
    title: "2026年春季校服采购公告",
    deptname: "石家庄市示例学校",
    createBy: "总务处",
    createTime: "2026-05-02 09:00:00",
    catagory: "CGGG",
    type: 0,
    content:
      "根据学校校服采购工作安排，现启动2026年春季校服采购流程。本次采购将按照公开、公平、公正原则开展，采购范围包括春秋装、夏装及冬季校服。欢迎符合条件的供应商按要求参与。",
    attachments: ["/profile/upload/校服采购需求说明.pdf"],
  },
  {
    id: "notice-2",
    title: "校服样衣展示及家长意见征集公告",
    deptname: "石家庄市示例学校",
    createBy: "校服选用组织",
    createTime: "2026-05-01 15:30:00",
    catagory: "CGGG",
    type: 0,
    content:
      "学校将对候选校服样衣进行集中展示，并同步征集家长意见。家长可在规定时间内查看样衣图片、面料说明和价格区间，并提交选购意见。",
    attachments: [],
  },
  {
    id: "notice-3",
    title: "2026年春季校服成交结果公告",
    deptname: "校服管理平台",
    createBy: "采购小组",
    createTime: "2026-04-28 10:20:00",
    catagory: "CGGG",
    type: 3,
    content:
      "经校服选用组织评议和家长代表确认，现将2026年春季校服采购成交结果予以公告。后续将按照合同约定组织尺码采集、订单确认和质量验收。",
    attachments: ["/profile/upload/成交结果公告.pdf"],
  },
];

export default {
  data() {
    return {
      baseUrl: getApp().globalData.baseUrl,
      notices: [],
      currentTab: 0,
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
        catagory: "CGGG",
        type: this.currentTab === 0 ? 0 : 3,
      };

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
          const rows = data && Array.isArray(data.rows) ? data.rows : [];
          if (rows.length) {
            this.mergeRows(rows, data.total);
          } else if (this.page === 1) {
            this.useMockRows();
          } else {
            this.hasMore = false;
          }
          this.finishLoading();
        },
        fail: () => {
          this.useMockRows();
          this.finishLoading();
        },
      });
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
    useMockRows() {
      const type = this.currentTab === 0 ? 0 : 3;
      const keyword = (this.searchKeyword || "").trim();
      let rows = MOCK_NOTICES.filter((item) => item.type === type);
      if (keyword) {
        rows = rows.filter((item) => item.title.indexOf(keyword) !== -1);
      }
      this.notices = rows;
      this.hasMore = false;
    },
    finishLoading() {
      this.isLoading = false;
      this.isRefreshing = false;
      uni.stopPullDownRefresh();
    },
    switchTab(index) {
      if (this.currentTab === index) return;
      this.currentTab = index;
      this.resetList();
      this.getList();
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
      return Number(item.type) === 3 ? "成交公告" : "采购公告";
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

.tabs {
  display: flex;
  height: 88rpx;
  background-color: #ffffff;
  border-bottom: 1rpx solid #eef0f4;
}

.tab-item {
  flex: 1;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  color: #5f6673;
}

.tab-item.active {
  color: #2f7df6;
  font-weight: 600;
}

.tab-item.active::after {
  content: "";
  position: absolute;
  left: 50%;
  bottom: 0;
  width: 52rpx;
  height: 5rpx;
  transform: translateX(-50%);
  background-color: #2f7df6;
  border-radius: 4rpx;
}

.notice-list {
  height: calc(100vh - 176rpx);
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
