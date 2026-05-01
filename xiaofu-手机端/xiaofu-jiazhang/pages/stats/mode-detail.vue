<template>
  <view class="page" v-if="item">
    <view class="block">
      <text class="block-title">基本信息</text>
      <view class="kv">
        <text class="k">会议名称</text>
        <text class="v multiline">{{ item.title }}</text>
      </view>
      <view class="kv">
        <text class="k">选购模式</text>
        <view class="v">
          <text
            v-if="item.purchaseMode && item.purchaseMode !== '—'"
            class="mode-tag"
            >{{ item.purchaseMode }}</text
          >
          <text v-else class="value-muted">—</text>
        </view>
      </view>
    </view>

    <view class="block" v-if="item.resultFile && item.resultFile.name">
      <text class="block-title">报备表</text>
      <view class="file-row">
        <uni-icons type="paperclip" size="22" color="#30a3ff"></uni-icons>
        <text class="file-name">{{ item.resultFile.name }}</text>
      </view>
      <view class="file-actions">
        <text class="link" @click="onPreview">预览</text>
        <text class="link-gap"></text>
        <text class="link" @click="onDownload">下载</text>
      </view>
    </view>

    <view class="block" v-else-if="item.fjPlainText">
      <text class="block-title">报备表</text>
      <text class="plain-body">{{ item.fjPlainText }}</text>
    </view>

    <view class="block" v-else>
      <text class="block-title">报备表</text>
      <text class="plain-body muted">暂无报备表</text>
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
  fetchCgmslxDictMaps,
  fetchQdxgmsDetail,
  mapQdxgmsToDetailView,
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
      title: "详情",
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
          fetchQdxgmsDetail(id),
          fetchCgmslxDictMaps(),
        ]);
        this.item = mapQdxgmsToDetailView(raw, dictMaps);
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

.value-muted {
  font-size: 28rpx;
  color: #bbb;
}

.mode-tag {
  display: inline-block;
  font-size: 26rpx;
  font-weight: 600;
  color: #30a3ff;
  background-color: rgba(48, 163, 255, 0.12);
  padding: 8rpx 20rpx;
  border-radius: 8rpx;
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

.plain-body.muted {
  color: #bbb;
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
