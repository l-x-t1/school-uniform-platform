<template>
  <view class="page" v-if="item">
    <view class="block">
      <text class="block-title">基本信息</text>
      <view class="kv">
        <text class="k">会议名称</text>
        <text class="v multiline">{{ item.title }}</text>
      </view>
      <view class="kv">
        <text class="k">会议时间</text>
        <text class="v">{{ meetingTime }}</text>
      </view>
      <view class="kv">
        <text class="k">参会人员</text>
        <text class="v multiline">{{ item.participants || "—" }}</text>
      </view>
      <view class="kv" v-if="item.createTime && item.createTime !== '—'">
        <text class="k">创建时间</text>
        <text class="v">{{ item.createTime }}</text>
      </view>
    </view>

    <view class="block" v-if="item.proposedLeader">
      <text class="block-title">拟担任选用组织组长</text>
      <view class="leader-card">
        <view class="kv-row">
          <text class="k2">职务</text>
          <text class="v2">{{ item.proposedLeader.position }}</text>
        </view>
        <view class="kv-row">
          <text class="k2">代表类别</text>
          <text class="v2">{{ item.proposedLeader.category }}</text>
        </view>
        <view class="kv-row last">
          <text class="k2">姓名</text>
          <text class="v2">{{ item.proposedLeader.name }}</text>
        </view>
      </view>
    </view>

    <view class="block" v-if="item.selectionRequirements && item.selectionRequirements.length">
      <text class="block-title">选购需求</text>
      <view
        class="req-card"
        v-for="(row, idx) in item.selectionRequirements"
        :key="idx"
      >
        <text class="req-tag">{{ row.uniformCategory }}</text>
        <view class="kv-row">
          <text class="k2">采购年级（级）</text>
          <text class="v2">{{ row.grade }}</text>
        </view>
        <view class="kv-row">
          <text class="k2">每个学生拟购买套数</text>
          <text class="v2">{{ row.setsPerStudent }}</text>
        </view>
        <view class="kv-row">
          <text class="k2">每套价格区间（拟）（元）</text>
          <text class="v2">{{ row.priceRange }}</text>
        </view>
        <view class="kv-row last" v-if="row.remark">
          <text class="k2">备注</text>
          <text class="v2">{{ row.remark }}</text>
        </view>
        <view class="kv-row last" v-else>
          <text class="k2">备注</text>
          <text class="v2 muted">—</text>
        </view>
      </view>
    </view>

    <view class="block" v-if="item.minutesFile && item.minutesFile.name">
      <text class="block-title">会议纪要</text>
      <view class="file-row">
        <uni-icons type="paperclip" size="22" color="#30a3ff"></uni-icons>
        <text class="file-name">{{ item.minutesFile.name }}</text>
      </view>
      <view class="file-actions">
        <text class="link" @click="onPreview">预览</text>
        <text class="link-gap"></text>
        <text class="link" @click="onDownload">下载</text>
      </view>
    </view>

    <view class="block" v-if="item.hyjyPlainText">
      <text class="block-title">会议纪要</text>
      <text class="plain-body">{{ item.hyjyPlainText }}</text>
    </view>
  </view>
  <view class="page miss" v-else-if="!loading">
    <text class="miss-text">未找到该记录</text>
  </view>
  <view class="page miss" v-else>
    <text class="miss-text">加载中…</text>
  </view>
</template>

<script>
import {
  fetchStatsTeamMeetingDetail,
  fetchTeamMeetingDetailDictMaps,
  mapTeamMeetingToDetailView,
  DEFAULT_TEAM_MEETING_DEPT_ID,
} from "./qr-open-api.js";

export default {
  data() {
    return {
      item: null,
      loading: true,
      deptId: DEFAULT_TEAM_MEETING_DEPT_ID,
      /** 第一步详情：与列表一致，路由 teamId（筛选条件），无则请求不传 teamId */
      teamId: "",
    };
  },
  computed: {
    meetingTime() {
      if (!this.item) return "";
      return this.item.meetingTime || this.item.meetingDate || "—";
    },
  },
  onLoad(options) {
    uni.setNavigationBarTitle({
      title: "详情",
    });
    const id = options.id;
    if (options.deptId != null && options.deptId !== "") {
      this.deptId = String(options.deptId);
    }
    if (options.teamId != null && options.teamId !== "") {
      this.teamId = String(options.teamId);
    }
    if (!id) {
      this.loading = false;
      this.item = null;
      return;
    }
    this.loadDetail(id);
  },
  methods: {
    async loadDetail(id) {
      this.loading = true;
      try {
        const [raw, dictMaps] = await Promise.all([
          fetchStatsTeamMeetingDetail(id, {
            deptId: this.deptId,
            teamId: this.teamId,
            omitDefaultTeamId:
              this.teamId == null || String(this.teamId).trim() === "",
          }),
          fetchTeamMeetingDetailDictMaps(),
        ]);
        this.item = mapTeamMeetingToDetailView(raw, dictMaps);
      } catch (e) {
        this.item = null;
        uni.showToast({
          title: (e && e.message) || "加载失败",
          icon: "none",
        });
      } finally {
        this.loading = false;
      }
    },
    onPreview() {
      const file = this.item && this.item.minutesFile;
      if (!file || !file.url) {
        uni.showToast({ title: "暂无文件地址", icon: "none" });
        return;
      }
      uni.showLoading({ title: "打开中..." });
      uni.downloadFile({
        url: file.url,
        success: (res) => {
          uni.hideLoading();
          if (res.statusCode === 200 && res.tempFilePath) {
            uni.openDocument({
              filePath: res.tempFilePath,
              showMenu: true,
            });
          } else {
            uni.showToast({ title: "预览失败", icon: "none" });
          }
        },
        fail: () => {
          uni.hideLoading();
          uni.showToast({ title: "下载失败", icon: "none" });
        },
      });
    },
    onDownload() {
      const file = this.item && this.item.minutesFile;
      if (!file || !file.url) {
        uni.showToast({ title: "暂无文件地址", icon: "none" });
        return;
      }
      uni.showLoading({ title: "下载中..." });
      uni.downloadFile({
        url: file.url,
        success: (res) => {
          uni.hideLoading();
          if (res.statusCode === 200 && res.tempFilePath) {
            uni.saveFile({
              tempFilePath: res.tempFilePath,
              success: () => {
                uni.showToast({ title: "已保存", icon: "success" });
              },
              fail: () => {
                uni.showToast({ title: "保存失败", icon: "none" });
              },
            });
          } else {
            uni.showToast({ title: "下载失败", icon: "none" });
          }
        },
        fail: () => {
          uni.hideLoading();
          uni.showToast({ title: "下载失败", icon: "none" });
        },
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

.leader-card {
  background-color: #f8fafc;
  border-radius: 16rpx;
  padding: 8rpx 20rpx;
}

.kv-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 18rpx 0;
  border-bottom: 1rpx solid #eef0f3;
  gap: 24rpx;
}

.kv-row.last {
  border-bottom: none;
}

.k2 {
  font-size: 26rpx;
  color: #888;
  flex-shrink: 0;
  max-width: 46%;
}

.v2 {
  font-size: 28rpx;
  color: #333;
  text-align: right;
  flex: 1;
  word-break: break-all;
}

.v2.muted {
  color: #bbb;
}

.req-card {
  background-color: #f8fafc;
  border-radius: 16rpx;
  padding: 20rpx 20rpx 8rpx;
  margin-bottom: 20rpx;
}

.req-card:last-child {
  margin-bottom: 0;
}

.req-tag {
  display: inline-block;
  font-size: 26rpx;
  font-weight: 600;
  color: #30a3ff;
  background-color: rgba(48, 163, 255, 0.12);
  padding: 8rpx 20rpx;
  border-radius: 8rpx;
  margin-bottom: 8rpx;
}

.file-row {
  display: flex;
  align-items: flex-start;
  gap: 12rpx;
  margin-bottom: 20rpx;
}

.file-name {
  flex: 1;
  font-size: 28rpx;
  color: #333;
  line-height: 1.45;
  word-break: break-all;
}

.file-actions {
  display: flex;
  align-items: center;
  padding-top: 8rpx;
}

.link {
  font-size: 28rpx;
  color: #30a3ff;
}

.link-gap {
  width: 48rpx;
}

.plain-body {
  font-size: 28rpx;
  color: #555;
  line-height: 1.65;
  white-space: pre-wrap;
  word-break: break-all;
}

.miss {
  display: flex;
  align-items: center;
  justify-content: center;
}

.miss-text {
  font-size: 28rpx;
  color: #999;
}
</style>
