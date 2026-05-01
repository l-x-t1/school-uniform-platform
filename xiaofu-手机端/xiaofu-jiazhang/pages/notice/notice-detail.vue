<template>
  <view class="container">
    <view class="notice-detail">
      <view class="notice-header">
        <text class="notice-title">{{ notice.title }}</text>
        <view class="notice-meta">
          <view>
            <text class="notice-source">{{ notice.source }}</text>
            <text class="notice-time">{{ notice.time }}</text>
          </view> 
          <view>
            <uni-icons type="heart" size="20" color="#999" @click="collectNotice"></uni-icons> 
          </view>
        </view>
      </view>
      <!-- <view class="notice-content">
        <rich-text :nodes="notice.content"></rich-text>
      </view> -->
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
          <uni-icons type="file" size="20" color="#3B7AFF"></uni-icons>
          <text class="attachment-name">{{ item.split("/").pop() }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      baseUrl: getApp().globalData.baseUrl,
      id: null,
      notice: {
        title: "",
        source: "",
        time: "",
        content: "",
        attachments: [],
      },
    };
  },
  onLoad(options) {
    if (options.id) {
      this.id = options.id;
      this.getNoticeDetail(this.id);
    }
  },
  methods: {
    fetchNoticeDetail() {
      // 模拟从API获取数据
      console.log("获取公告详情，ID：", this.id);
      // 实际开发中替换为API调用
    },
    downloadAttachment(attachment) {
      uni.showToast({
        title: "开始下载",
        icon: "none",
      });

      // 实际开发中实现文件下载功能
      uni.downloadFile({
        url: 'http://localhost:8080' + attachment,
        success: (res) => {
          uni.hideToast();
          //使用a标签下载
          const a = document.createElement("a");
          a.href = res.tempFilePath;
          a.download = attachment.split("/").pop();
          a.click();
        },
        fail: (err) => {
          console.log(err);
        },
      });
    },
    //获取公告详情
    getNoticeDetail(id) {
      uni.request({
        url: `${this.baseUrl}/system/xfptnotice/` + id,
        method: "GET",
        header: {
          Authorization: uni.getStorageSync("token"),
        }, 
        success: ({ data }) => {
          if (data.code == 200) {
            this.notice.title = data.data.title;
            this.notice.source = data.data.createBy;
            this.notice.time = data.data.createTime;
            this.notice.content = data.data.content;
            console.log(data.data.ggFile);
            if (data.data.ggFile) {
              this.notice.attachments = data.data.ggFile.split(",");
            }
          }
        },
      });
    },
    //收藏公告
    collectNotice() {
      uni.request({
        url: `${this.baseUrl}/school/user/addCollect?userId=${uni.getStorageSync("simId")}&noticId=${this.id}`,
        method: "GET",
        header: {
          Authorization: uni.getStorageSync("token"),
        },
        success: (res) => {
          if (res.data.code == 200) {
          uni.showToast({
              title: "收藏成功",
              icon: "success",
            });
          } else {
            uni.showToast({
              title: res.data.msg,
              icon: "none",
            });
          }
        },
      });
    },
  },
};
</script>

<style lang="scss">
.container {
  padding: 30rpx;
}

.notice-detail {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 30rpx;

  .notice-header {
    padding-bottom: 30rpx; 
    margin-bottom: 30rpx;

    .notice-title {
      font-size: 36rpx;
      color: #333;
      font-weight: 600;
      line-height: 1.4;
      margin-bottom: 20rpx;
      display: block;
    }

    .notice-meta {
      display: flex;
      align-items: center;
      justify-content: space-between;
      .notice-source {
        font-size: 24rpx;
        color: #3b7aff;
        margin-right: 20rpx;
      }

      .notice-time {
        font-size: 24rpx;
        color: #999;
      }
    }
  }

  .notice-content {
    font-size: 28rpx;
    color: #333;
    line-height: 1.6;
    margin-bottom: 30rpx;
  }

  .notice-attachments { 

    .attachment-title {
      font-size: 30rpx;
      color: #333;
      font-weight: 500;
      margin-bottom: 20rpx;
    }

    .attachment-item {
      display: flex;
      align-items: center;
      padding: 20rpx 0;
      border-bottom: 1rpx solid #f5f5f5;

      &:last-child {
        border-bottom: none;
      }

      .attachment-name {
        font-size: 26rpx;
        color: #3b7aff;
        margin-left: 10rpx;
      }
    }
  }
}
</style>
