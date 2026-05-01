<template>
  <view class="detail-container">
    <!-- 状态指示器 -->
    <view class="status-indicator" :class="feedbackInfo.status == 1 ? 'replied' : 'pending'">
      <view class="status-icon">
        <uni-icons :type="feedbackInfo.status == 1 ? 'checkmarkempty' : 'clock'" size="18" color="#fff"></uni-icons>
      </view>
      <text class="status-text">{{ feedbackInfo.status == 1 ? "已回复" : "待回复" }}</text>
    </view>

    <!-- 反馈信息卡片 -->
    <view class="feedback-card">
      <view class="card-header">
        <text class="card-title">反馈详情</text>
        <text class="submit-time">{{ feedbackInfo.createTime }}</text>
      </view>

      <view class="feedback-content">
        <view class="content-section">
          <view class="section-header">
            <uni-icons type="chat" size="16" color="#409EFF"></uni-icons>
            <text class="section-title">反馈内容</text>
          </view>
          <view class="content-box">
            <text class="content-text">{{ feedbackInfo.feedbackContent }}</text>
          </view>
        </view>

        <!-- 学校信息 -->
        <view class="school-section">
          <view class="section-header">
            <uni-icons type="home" size="16" color="#409EFF"></uni-icons>
            <text class="section-title">反馈学校</text>
          </view>
          <view class="school-box">
            <text class="school-name">{{ feedbackInfo.deptName || "未知学校" }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 官方回复卡片 -->
    <view class="reply-card" v-if="feedbackInfo.status == 1">
      <view class="reply-header">
        <view class="reply-badge">
          <uni-icons type="staff" size="14" color="#fff"></uni-icons>
          <text>官方回复</text>
        </view>
        <text class="reply-time">{{ feedbackInfo.replyTime }}</text>
      </view>

      <view class="reply-content">
        <view class="reply-box">
          <text class="reply-text">{{ feedbackInfo.replyContent }}</text>
        </view>
      </view>

      <view class="reply-footer" v-if="feedbackInfo.replyBy">
        <text class="reply-author">回复人：{{ feedbackInfo.replyBy }}</text>
      </view>
    </view>

    <!-- 操作按钮 -->
    <!-- <view class="action-section">
      <button class="action-btn secondary" @click="goBack">
        <uni-icons type="left" size="16" color="#666"></uni-icons>
        <text>返回</text>
      </button>
      <button class="action-btn primary" @click="submitNewFeedback">
        <uni-icons type="plus" size="16" color="#fff"></uni-icons>
        <text>提交新反馈</text>
      </button>
    </view> -->
  </view>
</template>

<script>
export default {
  data() {
    return {
      baseUrl: getApp().globalData.baseUrl,
      feedbackId: "",
      feedbackInfo: {
        id: "",
        feedbackContent: "",
        userName: "",
        deptName: "",
        contactPhone: "",
        contactEmail: "",
        attachments: [],
        status: 0, // 0: 待回复, 1: 已回复
        createTime: "",
        replyContent: "",
        replyTime: "",
        replyBy: "",
      },
    };
  },
  onLoad(options) {
    this.feedbackId = options.id;
    this.getFeedbackDetail();
  },
  methods: {
    // 获取反馈详情
    getFeedbackDetail() {
      uni.showLoading({
        title: "加载中...",
      });

      uni.request({
        url: `${this.baseUrl}/school/parentsFeedback/${this.feedbackId}`,
        method: "GET",
        header: {
          Authorization: uni.getStorageSync("token"),
          "Content-Type": "application/json",
        },
        data: {
          userId: uni.getStorageSync("simId"), // 增加用户ID验证
        },
        success: ({ data }) => {
          if (data.code === 200 && data.data) {
            this.feedbackInfo = {
              ...data.data,
              status: data.data.replyState || 0, // 使用replyState字段
              deptName:
                data.data.deptName || data.data.schoolName || "未知学校", // 添加学校名称
              attachments: data.data.feedbackUrl
                ? data.data.feedbackUrl.split(",").filter((url) => url.trim())
                : [],
              createTime: this.formatDateTime(
                data.data.createTime || data.data.createDate
              ),
              replyTime: data.data.replyTime
                ? this.formatDateTime(
                    data.data.replyTime || data.data.replyDate
                  )
                : "",
            };
          } else {
            uni.showToast({
              title: data.message || "获取详情失败",
              icon: "none",
            });
          }
        },
        fail: (err) => {
          console.error("获取反馈详情失败：", err);
          uni.showToast({
            title: "网络错误",
            icon: "none",
          });
        },
        complete: () => {
          uni.hideLoading();
        },
      });
    },

    // 格式化日期时间
    formatDateTime(time) {
      if (!time) return "";
      const date = new Date(time);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, "0");
      const day = String(date.getDate()).padStart(2, "0");
      const hours = String(date.getHours()).padStart(2, "0");
      const minutes = String(date.getMinutes()).padStart(2, "0");
      return `${year}-${month}-${day} ${hours}:${minutes}`;
    },

    // 预览图片
    previewImage(current) {
      uni.previewImage({
        current: current,
        urls: this.feedbackInfo.attachments,
      });
    },

    // 返回
    goBack() {
      uni.navigateBack();
    },

    // 提交新反馈
    submitNewFeedback() {
      uni.navigateTo({
        url: "/pages/feedback/feedback",
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.detail-container {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 24rpx 20rpx;
  padding-bottom: 120rpx;
}

// 状态指示器
.status-indicator {
  display: flex;
  align-items: center;
  background-color: #409eff;
  border-radius: 8rpx;
  padding: 16rpx 20rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(64, 158, 255, 0.2);

  .status-icon {
    margin-right: 10rpx;
  }

  .status-text {
    font-size: 26rpx;
    color: #fff;
    font-weight: 500;
  }

  &.replied {
    background-color: #52c41a;
    box-shadow: 0 2rpx 8rpx rgba(82, 196, 26, 0.2);
  }

  &.pending {
    background-color: #ff7875;
    box-shadow: 0 2rpx 8rpx rgba(255, 120, 117, 0.2);
  }
}

// 反馈信息卡片
.feedback-card {
  background: #fff;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
  border-left: 4rpx solid #409eff;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24rpx;
    padding-bottom: 16rpx;
    border-bottom: 1rpx solid #f0f0f0;

    .card-title {
      font-size: 30rpx;
      font-weight: 600;
      color: #333;
    }

    .submit-time {
      font-size: 24rpx;
      color: #999;
    }
  }

  .feedback-content {
    .section-header {
      display: flex;
      align-items: center;
      margin-bottom: 12rpx;

      .uni-icons {
        margin-right: 8rpx;
      }

      .section-title {
        font-size: 28rpx;
        font-weight: 500;
        color: #333;
      }
    }

    .content-section {
      margin-bottom: 24rpx;

      .content-box {
        background-color: #f8f9fa;
        padding: 16rpx 20rpx;
        border-radius: 8rpx;
        border-left: 3rpx solid #409eff;

        .content-text {
          font-size: 26rpx;
          color: #333;
          line-height: 1.6;
          display: block;
        }
      }
    }



    .school-section {
      .school-box {
        background-color: #f8f9fa;
        padding: 16rpx 20rpx;
        border-radius: 8rpx;
        border-left: 3rpx solid #409eff;

        .school-name {
          font-size: 26rpx;
          color: #333;
          font-weight: 500;
        }
      }
    }
  }
}

// 官方回复卡片
.reply-card {
  background: #fff;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
  border-left: 4rpx solid #52c41a;

  .reply-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16rpx;

    .reply-badge {
      display: flex;
      align-items: center;
      padding: 6rpx 16rpx;
      border-radius: 6rpx;
      font-size: 22rpx;
      color: #fff;
      background-color: #409eff;
      font-weight: 500;

      .uni-icons {
        margin-right: 6rpx;
      }
    }

    .reply-time {
      font-size: 24rpx;
      color: #999;
    }
  }

  .reply-content {
    margin-bottom: 16rpx;

    .reply-box {
      background-color: #f6ffed;
      padding: 16rpx 20rpx;
      border-radius: 8rpx;
      border-left: 3rpx solid #52c41a;

      .reply-text {
        font-size: 26rpx;
        color: #333;
        line-height: 1.6;
        display: block;
      }
    }
  }

  .reply-footer {
    text-align: right;

    .reply-author {
      font-size: 22rpx;
      color: #999;
      font-style: italic;
    }
  }
}



// 操作按钮
.action-section {
  display: flex;
  gap: 16rpx;
  padding: 20rpx;
  margin-top: 32rpx;

  .action-btn {
    flex: 1;
    height: 80rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 8rpx;
    font-size: 26rpx;
    border: none;
    font-weight: 500;
    transition: all 0.2s ease;
    gap: 6rpx;

    &::after {
      border: none;
    }

    &.primary {
      background-color: #409eff;
      color: #fff;
      box-shadow: 0 2rpx 8rpx rgba(64, 158, 255, 0.2);

      &:active {
        background-color: #337ecc;
        transform: translateY(1rpx);
      }
    }

    &.secondary {
      background-color: #fff;
      color: #666;
      border: 1rpx solid #e0e0e0;
      box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);

      &:active {
        background-color: #f5f5f5;
        transform: translateY(1rpx);
      }
    }
  }
}
</style>