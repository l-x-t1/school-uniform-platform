<template>
  <view class="page" v-if="item">
    <view
      class="info-banner"
      :class="{ 'info-banner--warn': item.orgSummaryLowRepresentative }"
      v-if="item.detailBanner"
    >
      <uni-icons
        type="info-filled"
        size="20"
        :color="item.orgSummaryLowRepresentative ? '#e6a23c' : '#30a3ff'"
      ></uni-icons>
      <text class="info-banner-text">{{ item.detailBanner }}</text>
    </view>
    <view
      class="summary-warn-tip"
      v-if="item.detailBanner && item.orgSummaryLowRepresentative"
    >
      <text class="summary-warn-tip-text"
        >提示：家长学生代表占比不能低于 80%</text
      >
    </view>

    <view class="block table-block" v-if="item.members && item.members.length">
      <scroll-view scroll-x class="table-scroll" enable-flex>
        <view class="table">
          <view class="tr tr-head">
            <view class="td td-sn">序号</view>
            <view class="td td-zw">职务</view>
            <view class="td td-dblb">代表类别</view>
            <view class="td td-xm">姓名</view>
            <view class="td td-phone">手机号</view>
            <view class="td td-nj">年级</view>
            <view class="td td-bj">班级</view>
          </view>
          <view
            class="tr tr-body"
            v-for="(m, idx) in item.members"
            :key="idx"
          >
            <view class="td td-sn">{{ idx + 1 }}</view>
            <view class="td td-zw">{{ m.zw }}</view>
            <view class="td td-dblb">{{ m.dblb }}</view>
            <view class="td td-xm">{{ m.xm }}</view>
            <view class="td td-phone">{{ m.phone }}</view>
            <view class="td td-nj">{{ m.njZy }}</view>
            <view class="td td-bj">{{ m.bj }}</view>
          </view>
        </view>
      </scroll-view>
    </view>
    <view class="block empty-members" v-else>
      <text class="empty-t">暂无名单数据</text>
    </view>

    <view
      class="block"
      v-if="(item.resultFile && item.resultFile.name) || item.fjPlainText"
    >
      <text class="block-title block-title--accent">附件</text>
      <template v-if="item.resultFile && item.resultFile.name">
        <view class="file-row">
          <uni-icons type="paperclip" size="22" color="#30a3ff"></uni-icons>
          <text class="file-name">{{ item.resultFile.name }}</text>
        </view>
        <view class="file-actions">
          <text class="link" @click="onPreview">预览</text>
          <text class="link-gap"></text>
          <text class="link" @click="onDownload">下载</text>
        </view>
      </template>
      <text v-else-if="item.fjPlainText" class="plain-body">{{
        item.fjPlainText
      }}</text>
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
  fetchOrgDiscloseDisplayDictMaps,
  fetchXyzzDetail,
  mapXyzzToDetailView,
} from "./qr-open-api.js";

export default {
  data() {
    return {
      item: null,
      loading: true,
    };
  },
  onLoad(options) {
    uni.setNavigationBarTitle({
      title: "查看选用组织名单",
    });
    const id = options.id;
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
          fetchXyzzDetail(id),
          fetchOrgDiscloseDisplayDictMaps(),
        ]);
        this.item = mapXyzzToDetailView(raw, dictMaps);
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
      const file = this.item && this.item.resultFile;
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
      const file = this.item && this.item.resultFile;
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

.info-banner {
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  gap: 16rpx;
  background-color: #eef6ff;
  border-radius: 16rpx;
  padding: 24rpx 24rpx;
  margin-bottom: 24rpx;
  border: 1rpx solid rgba(48, 163, 255, 0.2);
}

.info-banner--warn {
  background-color: #fdf6ec;
  border-color: rgba(230, 162, 60, 0.35);
}

.summary-warn-tip {
  margin: -12rpx 0 24rpx;
  padding: 0 8rpx;
}

.summary-warn-tip-text {
  font-size: 24rpx;
  color: #e6a23c;
  line-height: 1.5;
}

.info-banner-text {
  flex: 1;
  font-size: 26rpx;
  color: #555;
  line-height: 1.55;
}

.block {
  background-color: #fff;
  border-radius: 20rpx;
  padding: 24rpx 0 24rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
}

.table-block {
  padding-left: 0;
  padding-right: 0;
  overflow: hidden;
}

.table-scroll {
  width: 100%;
  white-space: nowrap;
}

.table {
  display: inline-block;
  min-width: 100%;
  padding: 0 16rpx 8rpx;
  box-sizing: border-box;
}

.tr {
  display: flex;
  flex-direction: row;
  align-items: stretch;
  border-bottom: 1rpx solid #e8eaed;
}

.tr-head {
  background-color: #f5f6f8;
  border-bottom: 1rpx solid #dcdfe6;
}

.tr-body:last-child {
  border-bottom: none;
}

.td {
  flex-shrink: 0;
  padding: 22rpx 14rpx;
  font-size: 24rpx;
  color: #333;
  line-height: 1.35;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  box-sizing: border-box;
  border-right: 1rpx solid #e8eaed;
}

.tr-head .td {
  font-weight: 600;
  color: #333;
}

.td:last-child {
  border-right: none;
}

.td-sn {
  width: 72rpx;
}

.td-zw {
  width: 100rpx;
}

.td-dblb {
  width: 220rpx;
}

.td-xm {
  width: 120rpx;
}

.td-phone {
  width: 200rpx;
}

.td-nj {
  width: 88rpx;
}

.td-bj {
  width: 88rpx;
}

.empty-members {
  padding: 48rpx 32rpx;
}

.empty-t {
  display: block;
  text-align: center;
  font-size: 28rpx;
  color: #999;
}

.block-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 24rpx;
  padding: 0 28rpx 16rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.block-title--accent {
  border-bottom: none;
  padding-bottom: 20rpx;
  padding-left: 48rpx;
  margin-bottom: 20rpx;
  position: relative;
}

.block-title--accent::before {
  content: "";
  position: absolute;
  left: 28rpx;
  top: 6rpx;
  width: 6rpx;
  height: 28rpx;
  background-color: #30a3ff;
  border-radius: 3rpx;
}

.file-row {
  display: flex;
  align-items: flex-start;
  gap: 12rpx;
  margin: 0 28rpx 20rpx;
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
  padding: 8rpx 28rpx 0;
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
  padding: 0 28rpx;
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
