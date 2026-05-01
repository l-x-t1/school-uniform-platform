<template>
  <view class="page">
    <view v-if="vendorHint" class="hint-block">
      <text class="hint-label">厂商</text>
      <text class="hint-value">{{ vendorHint }}</text>
    </view>

    <view v-for="(item, index) in list" :key="item.id || index" class="block">
      <text class="block-title">合同 {{ rowSerial(index) }}</text>
      <view class="kv">
        <text class="k">合同名称</text>
        <text class="v multiline">{{ item.contractName }}</text>
      </view>
      <view class="kv">
        <text class="k">合同服务期限</text>
        <text class="v multiline">{{ item.servicePeriod }}</text>
      </view>
    </view>

    <view v-if="!loading && list.length === 0" class="empty-block">
      <text class="empty-text">暂无数据</text>
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
  DEFAULT_STATS_DEPT_ID,
  fetchProcurementContractList,
  mapProcurementContractListRow,
} from "./qr-open-api.js";

export default {
  data() {
    return {
      deptId: DEFAULT_STATS_DEPT_ID,
      vendorId: "",
      vendorHint: "",
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
      title: "采购合同报备",
    });
    if (options.deptId != null && options.deptId !== "") {
      this.deptId = String(options.deptId);
    }
    if (options.vendorId != null && options.vendorId !== "") {
      this.vendorId = String(options.vendorId);
    }
    if (options.vendorName != null && options.vendorName !== "") {
      try {
        this.vendorHint = decodeURIComponent(String(options.vendorName));
      } catch (e) {
        this.vendorHint = String(options.vendorName);
      }
    }
    if (!this.vendorId) {
      uni.showToast({ title: "缺少厂商信息", icon: "none" });
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
    rowSerial(index) {
      return index + 1;
    },
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
      if (!this.vendorId) return;
      if (isFirst) {
        this.loading = true;
      } else {
        this.loadingMore = true;
      }
      try {
        const query = {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          vendorId: this.vendorId,
        };
        if (this.deptId) {
          query.deptId = this.deptId;
        }
        const { total, rows } = await fetchProcurementContractList(query);
        this.total = total;
        const mapped = rows.map(mapProcurementContractListRow);
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
}

.kv {
  margin-bottom: 24rpx;
}

.kv:last-child {
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
