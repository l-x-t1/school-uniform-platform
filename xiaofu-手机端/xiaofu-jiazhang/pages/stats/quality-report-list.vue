<template>
  <view class="page">
    <view v-if="vendorHint" class="hint-block">
      <text class="hint-label">厂商</text>
      <text class="hint-value">{{ vendorHint }}</text>
    </view>

    <view v-for="(item, index) in list" :key="item.id || index" class="block">
      <text class="block-title">{{ item.reportTitle }}</text>
      <view class="kv">
        <text class="k">报告类型</text>
        <text class="v multiline">{{ item.reportType }}</text>
      </view>
      <view class="kv">
        <text class="k">上传时间</text>
        <text class="v">{{ item.uploadTime }}</text>
      </view>
      <view class="actions">
        <view class="btn-link" @click.stop="goDetail(item)">查看详情</view>
      </view>
    </view>

    <view v-if="!loading && list.length === 0" class="empty-block">
      <text class="empty-text">暂无质检报告</text>
    </view>
    <view v-if="loading && list.length === 0" class="empty-block">
      <text class="empty-text muted">加载中…</text>
    </view>
    <view v-if="loadingMore" class="footer-tip">加载更多…</view>
    <view v-else-if="!hasMore && list.length > 0" class="footer-tip">已加载全部</view>
  </view>
</template>

<script>
import {
  fetchQualityInspectionDictMaps,
  fetchQualityReportSonList,
  mapQualityReportListRow,
} from "./qr-open-api.js";

export default {
  data() {
    return {
      reportId: "",
      vendorHint: "",
      /** @type {{ sc_zljy_bglx?: Record<string, string> } | null} */
      dictMaps: null,
      list: [],
      pageNum: 1,
      pageSize: 20,
      total: 0,
      loading: false,
      loadingMore: false,
    };
  },
  computed: {
    hasMore() {
      return this.list.length < this.total;
    },
  },
  onLoad(options) {
    uni.setNavigationBarTitle({
      title: "质量检测报告",
    });
    if (options.reportId != null && options.reportId !== "") {
      this.reportId = String(options.reportId);
    }
    if (options.vendorName != null && options.vendorName !== "") {
      try {
        this.vendorHint = decodeURIComponent(String(options.vendorName));
      } catch (e) {
        this.vendorHint = String(options.vendorName);
      }
    }
    if (!this.reportId) {
      uni.showToast({ title: "缺少报告信息", icon: "none" });
      return;
    }
    this.reload();
  },
  onPullDownRefresh() {
    this.reload();
  },
  onReachBottom() {
    if (!this.hasMore || this.loading || this.loadingMore) return;
    this.loadMore();
  },
  methods: {
    async reload() {
      this.pageNum = 1;
      this.total = 0;
      this.list = [];
      await this.loadList(true);
      uni.stopPullDownRefresh();
    },
    async loadMore() {
      this.pageNum += 1;
      await this.loadList(false);
    },
    async loadList(isFirst) {
      if (!this.reportId) return;
      if (isFirst) {
        this.loading = true;
      } else {
        this.loadingMore = true;
      }
      try {
        const dictPromise =
          this.dictMaps != null
            ? Promise.resolve(this.dictMaps)
            : fetchQualityInspectionDictMaps().then((m) => {
                this.dictMaps = m;
                return m;
              });
        const [{ total, rows }, dm] = await Promise.all([
          fetchQualityReportSonList({
            pageNum: this.pageNum,
            pageSize: this.pageSize,
            reportId: this.reportId,
          }),
          dictPromise,
        ]);
        this.total = total;
        const baseIndex = isFirst ? 0 : this.list.length;
        const mapped = rows.map((row, j) =>
          mapQualityReportListRow(row, baseIndex + j, dm)
        );
        if (isFirst) {
          this.list = mapped;
        } else {
          this.list = this.list.concat(mapped);
        }
      } catch (e) {
        if (!isFirst && this.pageNum > 1) {
          this.pageNum -= 1;
        }
        uni.showToast({
          title: (e && e.message) || "加载失败",
          icon: "none",
        });
      } finally {
        this.loading = false;
        this.loadingMore = false;
      }
    },
    goDetail(item) {
      if (!item.id) return;
      uni.navigateTo({
        url:
          "/pages/stats/quality-report-detail?id=" +
          encodeURIComponent(String(item.id)),
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  padding: 24rpx 30rpx 48rpx;
  box-sizing: border-box;
  background-color: #f0f2f5;
}

.hint-block {
  background-color: #fff;
  border-radius: 20rpx;
  padding: 24rpx 28rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
}

.hint-label {
  font-size: 24rpx;
  color: #888;
  display: block;
  margin-bottom: 8rpx;
}

.hint-value {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
  line-height: 1.45;
  display: block;
}

.block {
  background-color: #fff;
  border-radius: 20rpx;
  padding: 28rpx 28rpx 32rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
}

.block-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 24rpx;
  padding-bottom: 16rpx;
  border-bottom: 1rpx solid #f0f0f0;
  line-height: 1.45;
}

.kv {
  margin-bottom: 20rpx;
}

.kv:last-of-type {
  margin-bottom: 0;
}

.k {
  font-size: 26rpx;
  color: #888;
  display: block;
  margin-bottom: 10rpx;
}

.v {
  font-size: 28rpx;
  color: #333;
  line-height: 1.55;
  display: block;
}

.v.multiline {
  word-break: break-all;
}

.actions {
  margin-top: 24rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
}

.btn-link {
  font-size: 28rpx;
  color: #30a3ff;
  line-height: 1.4;
}

.empty-block {
  background-color: #fff;
  border-radius: 20rpx;
  padding: 80rpx 40rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

.empty-text.muted {
  color: #bbb;
}

.footer-tip {
  text-align: center;
  padding: 24rpx 0 40rpx;
  font-size: 24rpx;
  color: #bbb;
}
</style>
