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

      <view class="info-card" v-if="hasBasicInfo">
        <view class="section-title">公告信息</view>
        <view class="info-grid">
          <view class="info-item" v-if="notice.noticeCode">
            <text class="info-label">公告编号</text>
            <text class="info-value">{{ notice.noticeCode }}</text>
          </view>
          <view class="info-item" v-if="notice.schoolName">
            <text class="info-label">发布学校</text>
            <text class="info-value">{{ notice.schoolName }}</text>
          </view>
          <view class="info-item" v-if="notice.startTime">
            <text class="info-label">开始时间</text>
            <text class="info-value">{{ formatDate(notice.startTime) }}</text>
          </view>
          <view class="info-item" v-if="notice.endTime">
            <text class="info-label">截止时间</text>
            <text class="info-value">{{ formatDate(notice.endTime) }}</text>
          </view>
          <view class="info-item" v-if="notice.publishTime">
            <text class="info-label">发布时间</text>
            <text class="info-value">{{ formatDate(notice.publishTime) }}</text>
          </view>
          <view class="info-item" v-if="notice.applyStatusText">
            <text class="info-label">报名状态</text>
            <text class="info-value">{{ notice.applyStatusText }}</text>
          </view>
        </view>
      </view>

      <view class="goods-card" v-if="notice.items && notice.items.length > 0">
        <view class="section-title">统购商品</view>
        <view
          class="goods-item"
          v-for="(item, index) in notice.items"
          :key="item.id || index"
          @click="goToGoodsDetail(item)"
        >
          <view class="goods-top">
            <text class="goods-name">{{ item.goodsName || '统购商品' }}</text>
            <view class="goods-action">
              <text class="goods-count" v-if="item.buyNum">拟采购 {{ item.buyNum }}{{ item.unitName || '件' }}</text>
              <uni-icons
                v-if="item.goodsId"
                type="right"
                size="16"
                color="#2f7df6"
              ></uni-icons>
            </view>
          </view>
          <view class="goods-meta" v-if="item.sizeText">
            可选尺码：{{ item.sizeText }}
          </view>
          <view class="goods-meta" v-if="item.priceDesc">
            价格说明：{{ item.priceDesc }}
          </view>
          <view class="goods-meta" v-if="item.gradeNames">
            适用年级：{{ item.gradeNames }}
          </view>
        </view>
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
import { getUniform } from "@/api/uniform";

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
        items: [],
      },
    };
  },
  computed: {
    hasBasicInfo() {
      return Boolean(
        this.notice.noticeCode ||
          this.notice.schoolName ||
          this.notice.startTime ||
          this.notice.endTime ||
          this.notice.publishTime ||
          this.notice.applyStatusText
      );
    },
  },
  onLoad(options) {
    this.id = options.id ? decodeURIComponent(options.id) : "";
    this.isCollected = this.getCollectedIds().indexOf(this.id) !== -1;
    if (this.id) {
      this.getNoticeDetail(this.id);
    } else {
      this.notice.content = "未找到公告";
    }
  },
  methods: {
    getNoticeDetail(id) {
      uni.request({
        url: `${this.baseUrl}/school/groupNotice/${encodeURIComponent(id)}`,
        method: "GET",
        data: {
          simId: uni.getStorageSync("simId"),
        },
        header: {
          Authorization: uni.getStorageSync("token"),
        },
        success: ({ data }) => {
          if (data && data.code === 200 && data.data) {
            this.notice = this.normalizeNotice(data.data);
          } else {
            this.notice.content = data && data.msg ? data.msg : "公告加载失败";
          }
        },
        fail: () => {
          this.notice.content = "公告加载失败，请稍后重试";
        },
      });
    },
    normalizeNotice(row) {
      return {
        id: row.id,
        title: row.title || "",
        source: row.schoolName || row.deptname || row.createBy || "校服管理平台",
        time: row.publishTime || row.resultPublishTime || row.createTime || "",
        noticeType: row.noticeType,
        type: row.noticeType,
        schoolName: row.schoolName || "",
        noticeCode: row.noticeCode || "",
        startTime: row.startTime || "",
        endTime: row.endTime || "",
        publishTime: row.publishTime || "",
        applyStatus: row.applyStatus || "",
        applyStatusText: this.getApplyStatusText(row.applyStatus),
        content: row.content || row.noticeContent || "暂无公告内容",
        attachments: this.parseAttachments(row.ggFile || row.attachments || row.fileUrl || row.remark),
        items: this.normalizeItems(row.xfptGroupNoticeItemList || []),
      };
    },
    normalizeItems(items) {
      return Array.isArray(items)
        ? items.map((item) => ({
            id: item.id,
            goodsId: item.goodsId,
            goodsName: item.goodsName || "",
            unitName: item.unitName || "",
            buyNum: item.buyNum || "",
            priceDesc: item.priceDesc || "",
            gradeNames: item.gradeNames || "",
            sizeText: this.parseSizeRule(item.sizeRuleJson),
          }))
        : [];
    },
    goToGoodsDetail(item) {
      const goodsId = item && item.goodsId ? Number(item.goodsId) : 0;
      if (!goodsId) {
        uni.showToast({
          title: "该统购商品未关联商品信息",
          icon: "none",
        });
        return;
      }

      getUniform(goodsId)
        .then((res) => {
          const goods = res.data || res;
          if (!goods || !goods.id) {
            throw new Error("商品不存在");
          }
          uni.navigateTo({
            url: `/pages/uniform/uniform-detail?id=${goods.id}`,
          });
        })
        .catch(() => {
          uni.showToast({
            title: "该商品已下架或不存在",
            icon: "none",
          });
        });
    },
    parseAttachments(value) {
      if (!value) return [];
      if (Array.isArray(value)) return value;
      return String(value)
        .split(",")
        .map((item) => item.trim())
        .filter(Boolean);
    },
    parseSizeRule(value) {
      if (!value) return "";
      const normalizeSizeText = (item) => {
        if (item === null || item === undefined) return "";
        if (typeof item === "string" || typeof item === "number") {
          return String(item).trim();
        }
        if (typeof item === "object") {
          return String(
            item.sizeCode ||
              item.sizeName ||
              item.name ||
              item.label ||
              item.value ||
              ""
          ).trim();
        }
        return "";
      };
      const buildSizeText = (list) =>
        list.map(normalizeSizeText).filter(Boolean).join("、");
      if (Array.isArray(value)) {
        return buildSizeText(value);
      }
      try {
        const parsed = JSON.parse(value);
        return Array.isArray(parsed) ? buildSizeText(parsed) : String(value);
      } catch (error) {
        return String(value);
      }
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
      return String(notice.noticeType || notice.type) === "2" ? "结果公告" : "公告";
    },
    getApplyStatusText(status) {
      const map = {
        "0": "未开放",
        "1": "报名中",
        "2": "报名结束",
      };
      return map[String(status)] || "";
    },
    formatDate(value) {
      if (!value) return "暂无时间";
      return String(value).split(" ")[0];
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

.info-card,
.goods-card {
  margin-bottom: 28rpx;
  padding: 28rpx 24rpx;
  background: linear-gradient(180deg, #f8fbff 0%, #ffffff 100%);
  border: 1rpx solid #e8eef8;
  border-radius: 16rpx;
}

.section-title {
  margin-bottom: 22rpx;
  font-size: 30rpx;
  font-weight: 600;
  color: #1f2530;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18rpx;
}

.info-item {
  padding: 18rpx 20rpx;
  background-color: #ffffff;
  border-radius: 12rpx;
  border: 1rpx solid #eef2f8;
}

.info-label {
  display: block;
  margin-bottom: 8rpx;
  font-size: 22rpx;
  color: #8a93a3;
}

.info-value {
  display: block;
  font-size: 26rpx;
  color: #243042;
  line-height: 1.5;
  word-break: break-all;
}

.goods-item {
  padding: 22rpx 0;
  border-bottom: 1rpx solid #eef2f8;
  cursor: pointer;
}

.goods-item:last-child {
  padding-bottom: 0;
  border-bottom: none;
}

.goods-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16rpx;
  margin-bottom: 12rpx;
}

.goods-action {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 10rpx;
}

.goods-name {
  flex: 1;
  font-size: 30rpx;
  font-weight: 600;
  color: #1f2530;
  line-height: 1.5;
}

.goods-count {
  flex-shrink: 0;
  padding: 6rpx 14rpx;
  background-color: #edf4ff;
  border-radius: 999rpx;
  font-size: 22rpx;
  color: #2f7df6;
}

.goods-meta {
  margin-top: 10rpx;
  font-size: 25rpx;
  line-height: 1.7;
  color: #5d6778;
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

@media screen and (max-width: 375px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>
