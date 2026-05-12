<template>
  <view class="container">
    <view class="summary-card">
      <text class="summary-title">{{ title }}</text>
      <text class="summary-desc">{{ summaryText }}</text>
    </view>

    <view class="content-wrapper">
      <view
        class="process-item"
        v-for="item in filteredList"
        :key="item.id"
        @click="viewDetail(item)"
      >
        <view class="item-card">
          <text class="item-title">{{ item.title }}</text>
          <view class="item-info">
            <text class="info-label">{{ item.label }}</text>
            <text class="info-value">{{ item.time }}</text>
          </view>
        </view>
        <uni-icons type="right" size="16" color="#c8ccd4"></uni-icons>
      </view>

      <view class="empty-state" v-if="filteredList.length === 0">
        <image src="/static/uni.png" mode="aspectFit" class="empty-image"></image>
        <text class="empty-text">暂无相关内容</text>
      </view>
    </view>
  </view>
</template>

<script>
const PROCESS_LIST = [
  {
    id: "meeting-1",
    type: "meeting",
    title: "2026年春季校服采购工作会议纪要",
    label: "会议时间",
    time: "2026-04-18 09:30",
  },
  {
    id: "survey-1",
    type: "survey",
    title: "家长购买意向征集结果公示",
    label: "征集时间",
    time: "2026-04-20 至 2026-04-24",
  },
  {
    id: "result-1",
    type: "result",
    title: "2026年春季校服选用结果公示",
    label: "公示时间",
    time: "2026-04-28",
  },
  {
    id: "quality-1",
    type: "quality",
    title: "春秋装面料及成衣质量检验报告",
    label: "报告时间",
    time: "2026-04-30",
  },
];

export default {
  data() {
    return {
      title: "",
      type: "",
      processList: PROCESS_LIST,
    };
  },
  computed: {
    filteredList() {
      return this.processList.filter((item) => item.type === this.type);
    },
    summaryText() {
      const map = {
        meeting: "公开学校校服采购会议决议和工作安排。",
        survey: "公开家长意见征集范围、时间和统计结果。",
        result: "公开选用组织确认后的校服选购结果。",
        quality: "公开校服面料、成衣和检测报告信息。",
      };
      return map[this.type] || "公开校服采购过程中的关键资料。";
    },
  },
  onLoad(options) {
    this.title = options.title ? decodeURIComponent(options.title) : "流程公开";
    this.type = options.type || "";
    uni.setNavigationBarTitle({
      title: this.title,
    });
  },
  methods: {
    viewDetail(item) {
      uni.navigateTo({
        url:
          "/pages/process/process-detail?id=" +
          encodeURIComponent(item.id) +
          "&type=" +
          encodeURIComponent(item.type) +
          "&title=" +
          encodeURIComponent(item.title),
      });
    },
  },
};
</script>

<style lang="scss">
.container {
  min-height: 100vh;
  background-color: #f5f7fb;
}

.summary-card {
  padding: 32rpx 30rpx;
  background-color: #ffffff;
  border-bottom: 1rpx solid #eef0f4;
}

.summary-title {
  display: block;
  font-size: 34rpx;
  font-weight: 600;
  color: #1f2530;
  margin-bottom: 12rpx;
}

.summary-desc {
  display: block;
  font-size: 26rpx;
  color: #7a8290;
  line-height: 1.5;
}

.content-wrapper {
  padding: 24rpx;
}

.process-item {
  margin-bottom: 20rpx;
  background-color: #ffffff;
  border-radius: 12rpx;
  border: 1rpx solid #eef0f4;
  padding: 28rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 18rpx;
}

.item-card {
  flex: 1;
  min-width: 0;
}

.item-title {
  display: block;
  font-size: 31rpx;
  color: #1f2530;
  font-weight: 600;
  margin-bottom: 18rpx;
  line-height: 1.45;
}

.item-info {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.info-label,
.info-value {
  font-size: 25rpx;
  color: #7a8290;
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
