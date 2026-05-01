<template>
  <!-- 数据来自 GET /school/summary/{id}，与 PC Caigoubaobei 字段一致 -->
  <view class="page" v-if="item">
    <view class="form-card">
      <!-- 1 学校 -->
      <view class="form-row">
        <text class="form-label">学校</text>
        <view class="form-value-wrap">
          <text class="form-value text-val">{{ item.schoolName }}</text>
        </view>
      </view>

      <!-- 2 采购公告 -->
      <view class="form-row form-row--file">
        <text class="form-label">采购公告</text>
        <view class="form-value-wrap">
          <template v-if="item.procurementNoticeFile && item.procurementNoticeFile.name">
            <view class="file-line">
              <uni-icons type="paperclip" size="20" color="#409eff"></uni-icons>
              <text class="file-name">{{ item.procurementNoticeFile.name }}</text>
            </view>
            <view class="file-links">
              <text class="link" @click="onPreview(item.procurementNoticeFile)">预览</text>
              <text class="link-sep"></text>
              <text class="link" @click="onDownload(item.procurementNoticeFile)">下载</text>
            </view>
          </template>
          <text v-else-if="item.cgNoticePlain" class="form-value plain-val">{{ item.cgNoticePlain }}</text>
          <text v-else class="form-value placeholder">暂无采购公告附件</text>
        </view>
      </view>

      <!-- 3 成交公告 -->
      <view class="form-row form-row--file">
        <text class="form-label">成交公告</text>
        <view class="form-value-wrap">
          <template v-if="item.dealNoticeFile && item.dealNoticeFile.name">
            <view class="file-line">
              <uni-icons type="paperclip" size="20" color="#409eff"></uni-icons>
              <text class="file-name">{{ item.dealNoticeFile.name }}</text>
            </view>
            <view class="file-links">
              <text class="link" @click="onPreview(item.dealNoticeFile)">预览</text>
              <text class="link-sep"></text>
              <text class="link" @click="onDownload(item.dealNoticeFile)">下载</text>
            </view>
          </template>
          <text v-else-if="item.dealNoticePlain" class="form-value plain-val">{{ item.dealNoticePlain }}</text>
          <text v-else class="form-value placeholder">暂无成交公告附件</text>
        </view>
      </view>

      <!-- 4 成交企业 -->
      <view class="form-row">
        <text class="form-label">成交企业</text>
        <view class="form-value-wrap">
          <text class="form-value text-val">{{ item.winningEnterprise }}</text>
        </view>
      </view>

      <!-- 5 单价（元） -->
      <view class="form-row">
        <text class="form-label">单价（元）</text>
        <view class="form-value-wrap">
          <text class="form-value text-val">{{ item.unitPrice }}</text>
        </view>
      </view>

      <!-- 6 订购人数 -->
      <view class="form-row">
        <text class="form-label">订购人数</text>
        <view class="form-value-wrap">
          <text class="form-value text-val">{{ item.orderCount }}</text>
        </view>
      </view>

      <!-- 7 总金额（元） -->
      <view class="form-row">
        <text class="form-label">总金额（元）</text>
        <view class="form-value-wrap">
          <text class="form-value text-val">{{ item.totalAmount }}</text>
        </view>
      </view>

      <!-- 8 采购日期 -->
      <view class="form-row form-row--last">
        <text class="form-label">采购日期</text>
        <view class="form-value-wrap form-value-wrap--date">
          <uni-icons type="calendar" size="18" color="#909399"></uni-icons>
          <text class="form-value text-val">{{ item.purchaseDate }}</text>
        </view>
      </view>
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
import { fetchProcInfoDetail, mapProcInfoToDetailView } from "./qr-open-api.js";

export default {
  data() {
    return {
      item: null,
      loading: true,
    };
  },
  onLoad(options) {
    uni.setNavigationBarTitle({
      title: "采购信息报备",
    });
    const id =
      options.id != null && options.id !== ""
        ? options.id
        : options.teamId != null && options.teamId !== ""
          ? options.teamId
          : "";
    if (!id) {
      this.loading = false;
      this.item = null;
      return;
    }
    this.loadDetail(id);
  },
  methods: {
    /** GET `/school/summary/{id}` */
    async loadDetail(id) {
      this.loading = true;
      try {
        const raw = await fetchProcInfoDetail(id);
        this.item = mapProcInfoToDetailView(raw);
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
    onPreview(file) {
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
    onDownload(file) {
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
  padding: 24rpx 24rpx 48rpx;
  box-sizing: border-box;
  background-color: #f0f2f5;
}

/* 与 PC el-form 只读详情类似的单列表单 */
.form-card {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 0 28rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
  border: 1rpx solid #ebeef5;
}

.form-row {
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  padding: 28rpx 0;
  border-bottom: 1rpx solid #ebeef5;
}

.form-row--last {
  border-bottom: none;
}

.form-label {
  width: 200rpx;
  flex-shrink: 0;
  font-size: 28rpx;
  color: #606266;
  line-height: 44rpx;
  padding-top: 4rpx;
}

.form-value-wrap {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  align-items: stretch;
}

.form-value-wrap--date {
  flex-direction: row;
  align-items: center;
  gap: 12rpx;
}

.form-value {
  font-size: 28rpx;
  line-height: 44rpx;
}

.text-val {
  color: #303133;
  word-break: break-all;
}

.plain-val {
  color: #303133;
  white-space: pre-wrap;
  word-break: break-all;
}

.placeholder {
  color: #c0c4cc;
}

.file-line {
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  gap: 10rpx;
  margin-bottom: 12rpx;
}

.file-name {
  flex: 1;
  font-size: 28rpx;
  color: #303133;
  line-height: 44rpx;
  word-break: break-all;
}

.file-links {
  display: flex;
  flex-direction: row;
  align-items: center;
}

.link {
  font-size: 28rpx;
  color: #409eff;
}

.link-sep {
  width: 36rpx;
  display: inline-block;
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
