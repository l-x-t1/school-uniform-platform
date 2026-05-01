<template>
  <view class="page">
    <view v-for="(item, index) in list" :key="item.id" class="block">
      <view class="block-head">
        <text class="sn-label">序号</text>
        <text class="sn-val">{{ rowSerial(index) }}</text>
      </view>
      <text class="card-title-text">{{ item.title }}</text>
      <view class="row">
        <text class="row-label">总人数</text>
        <text class="row-value">{{ item.totalDisplay }}</text>
      </view>
      <view class="row">
        <text class="row-label">创建时间</text>
        <text class="row-value">{{ item.createTime }}</text>
      </view>
      <view class="actions">
        <view class="btn-detail" @click.stop="goDetail(item)">详情</view>
      </view>
    </view>
    <view v-if="!loading && list.length === 0" class="empty">暂无数据</view>
    <view v-if="loading && list.length === 0" class="empty muted">加载中…</view>
    <view v-if="loadingMore" class="footer-tip">加载更多…</view>
    <view v-else-if="!hasMore && list.length > 0" class="footer-tip">已加载全部</view>
  </view>
</template>

<script>
import {
  DEFAULT_STATS_DEPT_ID,
  fetchXyzzList,
  getStatsFormTeamId,
  getStatsTeamMeetingId,
  mapXyzzListRow,
} from "./qr-open-api.js";

export default {
  data() {
    return {
      deptId: DEFAULT_STATS_DEPT_ID,
      /** 班子会议列表首条 id，与 disclose/list 的 teamMeetingId、第二步意见征集一致 */
      teamMeetingId: "",
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
      title: "选用组织名单报备与公开",
    });
    if (options.deptId != null && options.deptId !== "") {
      this.deptId = String(options.deptId);
    }
    if (options.teamMeetingId != null && options.teamMeetingId !== "") {
      this.teamMeetingId = String(options.teamMeetingId);
    } else {
      const mid = getStatsTeamMeetingId();
      if (mid) {
        this.teamMeetingId = mid;
      } else {
        const tid = getStatsFormTeamId();
        if (tid) {
          this.teamMeetingId = tid;
        }
      }
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
      if (isFirst) {
        this.loading = true;
      } else {
        this.loadingMore = true;
      }
      try {
        const query = {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
        };
        if (this.deptId) {
          query.deptId = this.deptId;
        }
        if (this.teamMeetingId) {
          query.teamMeetingId = this.teamMeetingId;
        }
        const { total, rows } = await fetchXyzzList(query);
        this.total = total;
        const mapped = rows.map(mapXyzzListRow);
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
      uni.navigateTo({
        url: "/pages/stats/org-detail?id=" + item.id,
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  padding: 24rpx 30rpx 40rpx;
  box-sizing: border-box;
  background-color: #f0f2f5;
}

.block {
  background-color: #ffffff;
  border-radius: 20rpx;
  padding: 28rpx 28rpx 24rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
}

.block-head {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.sn-label {
  font-size: 24rpx;
  color: #999;
  margin-right: 12rpx;
}

.sn-val {
  font-size: 28rpx;
  font-weight: 600;
  color: #30a3ff;
}

.card-title-text {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
  line-height: 1.5;
  display: block;
  margin-bottom: 24rpx;
}

.row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16rpx;
  gap: 24rpx;
}

.row-label {
  font-size: 26rpx;
  color: #888;
  flex-shrink: 0;
}

.row-value {
  font-size: 26rpx;
  color: #333;
  text-align: right;
  flex: 1;
}

.actions {
  margin-top: 28rpx;
  padding-top: 24rpx;
  border-top: 1rpx solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
}

.btn-detail {
  min-width: 160rpx;
  padding: 16rpx 40rpx;
  text-align: center;
  font-size: 28rpx;
  color: #30a3ff;
  border: 2rpx solid #30a3ff;
  border-radius: 12rpx;
  background-color: rgba(48, 163, 255, 0.08);
}

.empty {
  text-align: center;
  padding: 120rpx 40rpx;
  font-size: 28rpx;
  color: #999;
}

.empty.muted {
  color: #bbb;
}

.footer-tip {
  text-align: center;
  padding: 24rpx 0 40rpx;
  font-size: 24rpx;
  color: #bbb;
}
</style>
