<template>
  <view class="container">
    <view class="notice-detail">
      <view class="notice-header">
        <view class="notice-tag">{{ getStatusText(notice) }}</view>
        <text class="notice-title">{{ notice.title || '公告详情' }}</text>
        <view class="notice-meta">
          <view class="meta-left">
            <text class="notice-source">{{ notice.source || '校服管理平台' }}</text>
            <text class="notice-time">{{ notice.time || '暂无时间' }}</text>
          </view>
          <view class="collect-btn" @click="collectNotice">
            <uni-icons
              :type="isCollected ? 'heart-filled' : 'heart'"
              size="20"
              :color="isCollected ? '#ff6b6b' : '#8a8f99'"
            ></uni-icons>
          </view>
        </view>
      </view>

      <view class="notice-content" v-if="notice.content">
        <rich-text :nodes="notice.content"></rich-text>
      </view>

      <view
        class="notice-attachments"
        v-if="notice.attachments && notice.attachments.length > 0"
      >
        <view class="attachment-title">附件列表</view>
        <view
          class="attachment-item"
          v-for="(item, index) in notice.attachments"
          :key="index"
          @click="downloadAttachment(item)"
        >
          <uni-icons type="file" size="20" color="#2f7df6"></uni-icons>
          <text class="attachment-name">{{ getAttachmentName(item) }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
const MOCK_NOTICE_DETAILS = {
  "notice-1": {
    id: "notice-1",
    title: "2026年春季校服采购公告",
    source: "石家庄市示例学校",
    time: "2026-05-02 09:00:00",
    type: 0,
    content:
      "<p>根据学校校服采购工作安排，现启动2026年春季校服采购流程。本次采购将按照公开、公平、公正原则开展。</p><p>采购范围包括春秋装、夏装及冬季校服。供应商需按要求提交样衣、面料检测报告、价格说明和售后服务承诺。</p><p>学校将在家长代表、教师代表和选用组织共同参与下完成样衣展示、意见征集、评审和结果公示。</p>",
    attachments: ["/profile/upload/校服采购需求说明.pdf"],
  },
  "notice-2": {
    id: "notice-2",
    title: "校服样衣展示及家长意见征集公告",
    source: "校服选用组织",
    time: "2026-05-01 15:30:00",
    type: 0,
    content:
      "<p>学校将对候选校服样衣进行集中展示，并同步征集家长意见。家长可在规定时间内查看样衣图片、面料说明和价格区间。</p><p>征集结果将作为选用组织评审的重要参考，后续选用结果将在平台进行公示。</p>",
    attachments: [],
  },
  "notice-3": {
    id: "notice-3",
    title: "2026年春季校服成交结果公告",
    source: "采购小组",
    time: "2026-04-28 10:20:00",
    type: 3,
    content:
      "<p>经校服选用组织评议和家长代表确认，现将2026年春季校服采购成交结果予以公告。</p><p>后续将按照合同约定组织尺码采集、订单确认、生产交付和质量验收。家长可在订单页面查看自己的订购进度。</p>",
    attachments: ["/profile/upload/成交结果公告.pdf"],
  },
};

export default {
  data() {
    return {
      baseUrl: getApp().globalData.baseUrl,
      id: "",
      isCollected: false,
      notice: {
        title: "",
        source: "",
        time: "",
        type: 0,
        content: "",
        attachments: [],
      },
    };
  },
  onLoad(options) {
    this.id = options.id ? decodeURIComponent(options.id) : "";
    this.isCollected = this.getCollectedIds().indexOf(this.id) !== -1;
    if (this.id) {
      this.getNoticeDetail(this.id);
    } else {
      this.useMockDetail("notice-1");
    }
  },
  methods: {
    getNoticeDetail(id) {
      uni.request({
        url: `${this.baseUrl}/system/xfptnotice/${encodeURIComponent(id)}`,
        method: "GET",
        header: {
          Authorization: uni.getStorageSync("token"),
        },
        success: ({ data }) => {
          if (data && data.code === 200 && data.data) {
            this.notice = this.normalizeNotice(data.data);
          } else {
            this.useMockDetail(id);
          }
        },
        fail: () => {
          this.useMockDetail(id);
        },
      });
    },
    normalizeNotice(row) {
      return {
        id: row.id,
        title: row.title || "",
        source: row.deptname || row.createBy || "校服管理平台",
        time: row.createTime || "",
        type: row.type,
        content: row.content || row.noticeContent || "暂无公告内容",
        attachments: this.parseAttachments(row.ggFile || row.attachments || row.fileUrl),
      };
    },
    parseAttachments(value) {
      if (!value) return [];
      if (Array.isArray(value)) return value;
      return String(value)
        .split(",")
        .map((item) => item.trim())
        .filter(Boolean);
    },
    useMockDetail(id) {
      const detail = MOCK_NOTICE_DETAILS[id] || MOCK_NOTICE_DETAILS["notice-1"];
      this.notice = Object.assign({}, detail, {
        attachments: detail.attachments.slice(),
      });
    },
    downloadAttachment(attachment) {
      const url = this.resolveAttachmentUrl(attachment);
      if (!url) {
        uni.showToast({
          title: "附件地址无效",
          icon: "none",
        });
        return;
      }
      uni.showLoading({
        title: "正在打开",
      });
      uni.downloadFile({
        url,
        success: (res) => {
          uni.hideLoading();
          if (typeof document !== "undefined") {
            const a = document.createElement("a");
            a.href = res.tempFilePath;
            a.download = this.getAttachmentName(attachment);
            a.click();
            return;
          }
          uni.openDocument({
            filePath: res.tempFilePath,
          });
        },
        fail: () => {
          uni.hideLoading();
          uni.showToast({
            title: "附件暂时无法打开",
            icon: "none",
          });
        },
      });
    },
    resolveAttachmentUrl(attachment) {
      if (!attachment) return "";
      const path = String(attachment).trim();
      if (/^https?:\/\//i.test(path)) return path;
      return this.baseUrl.replace(/\/$/, "") + (path.indexOf("/") === 0 ? path : "/" + path);
    },
    getAttachmentName(attachment) {
      const path = String(attachment || "");
      const cleanPath = path.split("?")[0];
      return cleanPath.split("/").pop() || "附件";
    },
    collectNotice() {
      uni.request({
        url: `${this.baseUrl}/school/user/addCollect`,
        method: "GET",
        data: {
          userId: uni.getStorageSync("simId"),
          noticId: this.id,
        },
        header: {
          Authorization: uni.getStorageSync("token"),
        },
        success: (res) => {
          if (res.data && res.data.code === 200) {
            this.saveCollectState();
          } else {
            this.saveCollectState();
          }
        },
        fail: () => {
          this.saveCollectState();
        },
      });
    },
    saveCollectState() {
      const ids = this.getCollectedIds();
      if (ids.indexOf(this.id) === -1) {
        ids.push(this.id);
      }
      uni.setStorageSync("collectedNotices", ids);
      this.isCollected = true;
      uni.showToast({
        title: "收藏成功",
        icon: "success",
      });
    },
    getCollectedIds() {
      const ids = uni.getStorageSync("collectedNotices");
      return Array.isArray(ids) ? ids : [];
    },
    getStatusText(notice) {
      return Number(notice.type) === 3 ? "成交公告" : "采购公告";
    },
  },
};
</script>

<style lang="scss">
.container {
  min-height: 100vh;
  padding: 24rpx;
  box-sizing: border-box;
  background-color: #f5f7fb;
}

.notice-detail {
  background-color: #ffffff;
  border-radius: 12rpx;
  padding: 32rpx;
  border: 1rpx solid #eef0f4;
}

.notice-header {
  padding-bottom: 28rpx;
  border-bottom: 1rpx solid #eef0f4;
  margin-bottom: 28rpx;
}

.notice-tag {
  display: inline-flex;
  align-items: center;
  height: 40rpx;
  padding: 0 16rpx;
  margin-bottom: 18rpx;
  border-radius: 6rpx;
  background-color: #edf4ff;
  color: #2f7df6;
  font-size: 22rpx;
}

.notice-title {
  display: block;
  font-size: 36rpx;
  color: #1f2530;
  font-weight: 600;
  line-height: 1.45;
  margin-bottom: 22rpx;
}

.notice-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.meta-left {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 14rpx 20rpx;
}

.notice-source,
.notice-time {
  font-size: 24rpx;
  color: #7a8290;
}

.collect-btn {
  width: 58rpx;
  height: 58rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.notice-content {
  font-size: 29rpx;
  color: #303744;
  line-height: 1.8;
  margin-bottom: 34rpx;
}

.notice-attachments {
  padding-top: 28rpx;
  border-top: 1rpx solid #eef0f4;
}

.attachment-title {
  font-size: 30rpx;
  color: #1f2530;
  font-weight: 600;
  margin-bottom: 18rpx;
}

.attachment-item {
  display: flex;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f2f4f7;
}

.attachment-item:last-child {
  border-bottom: none;
}

.attachment-name {
  flex: 1;
  margin-left: 12rpx;
  font-size: 26rpx;
  color: #2f7df6;
  line-height: 1.4;
}
</style>
