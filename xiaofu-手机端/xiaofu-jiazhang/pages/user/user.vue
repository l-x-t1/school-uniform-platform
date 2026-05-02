<template>
  <view class="user-container">
    <!-- 顶部用户信息区域 -->
    <!-- <view class="user-header-section">
      <view class="user-info">
        <view class="avatar-wrapper">
          <image
            class="avatar"
            :src="userInfo.avatar"
            mode="aspectFill"
          ></image>
        </view>
        <view class="user-detail"> 
        </view>
      </view> 
    </view> -->

    <!-- 学生信息卡片 -->
    <view class="student-card">
      <view class="card-header">
        <text class="card-title">学生信息</text>
        <view
          class="card-action"
          @click="navigateTo('/pages/user/student-info')"
        >
          <text>管理</text>
          <uni-icons type="right" size="14" color="#999"></uni-icons>
        </view>
      </view>
      <view class="student-list"> 
        <view
          class="student-item"
          v-for="(student, index) in studentList"
          :key="index"
        >
          <image
            v-if="student.sex == '0'"
            class="student-avatar"
            src="/static/nansheng.png"
            mode="aspectFill"
          ></image>
          <image
            v-else
            class="student-avatar"
            src="/static/nvsheng.png"
            mode="aspectFill"
          ></image>
          <view class="student-info">
            <text class="student-name">{{ student.name }}</text>
            <text class="student-class">{{ student.className }}</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 我的收藏卡片 -->
    <view class="favorite-card">
      <view class="card-header">
        <text class="card-title">我的收藏</text>
        <view
          class="card-action"
          @click="navigateTo('/pages/wdsc/wdsc')"
        >
          <text style="color: #999;">查看</text>
          <uni-icons type="right" size="14" color="#999"></uni-icons>
        </view>
      </view> 
    </view>
    <view class="favorite-card">
      <view class="card-header">
        <text class="card-title">校服回收</text>
        <view
          class="card-action"
          @click="navigateTo('/pages/xfhs/xfhs')"
        >
          <text style="color: #999;">查看</text>
          <uni-icons type="right" size="14" color="#999"></uni-icons>
        </view>
      </view> 
    </view>
    <!-- 反馈意见卡片 -->
    <view class="feedback-card">
      <view class="card-header">
        <text class="card-title">意见反馈</text>
        <view
          class="card-action"
          @click="navigateTo('/pages/feedback/feedback-list')"
        >
          <text>查看全部</text>
          <uni-icons type="right" size="14" color="#999"></uni-icons>
        </view>
      </view>

      <!-- 反馈统计 -->
      <view class="feedback-stats">  
        <view class="stat-item" @click="navigateTo('/pages/feedback/feedback-list?status=2')">
          <text class="stat-number">{{ feedbackStats.replyCount }}</text>
          <text class="stat-label">已回复</text>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item" @click="navigateTo('/pages/feedback/feedback-list?status=1')">
          <text class="stat-number">{{ feedbackStats.noReplyCount }}</text>
          <text class="stat-label">待回复</text>
        </view>
      </view>

      <!-- 快速反馈按钮 -->
      <view class="quick-feedback">
        <button
          class="feedback-btn"
          @click="navigateTo('/pages/feedback/feedback')"
        > 
          <text>我要反馈意见</text>
        </button>
      </view> 
    </view> 
    <!-- 底部版本信息 -->
    <!-- <view class="footer"> 
      <button class="logout-btn" @click="handleLogout">退出登录</button>
    </view> -->
  </view>
</template>

<script>
import linSelect from '../../uni_modules/lin-select/components/lin-select/lin-select.vue';
export default {
  components: { linSelect },
  data() {
    return {
      baseUrl: getApp().globalData.baseUrl,
      userInfo: { 
        avatar: "/static/usertx.png", 
      },
      studentList: [],
      // 反馈统计数据
      feedbackStats: {
        total: 0,
        replied: 0,
        pending: 0,
      }, 
    };
  },
  created() {
    
  },
  onShow() {
    this.ensureLogin(() => {
      this.getFeedbackStats();
      this.getStudentList();
    });
  },
  methods: {
    ensureLogin(callback) {
      if (uni.getStorageSync("token") && uni.getStorageSync("simId")) {
        callback && callback();
        return;
      }
      if (this.isLocalDev()) {
        this.devLogin(callback);
        return;
      }
      uni.reLaunch({
        url: "/pages/login/login",
      });
    },
    isLocalDev() {
      return typeof window !== "undefined" && ["localhost", "127.0.0.1"].includes(window.location.hostname);
    },
    devLogin(callback) {
      uni.showLoading({
        title: "本地登录中...",
        mask: true,
      });
      uni.request({
        url: `${this.baseUrl}/school/simOpen/devLogin`,
        method: "GET",
        success: ({ data }) => {
          uni.hideLoading();
          if (data.code === 200) {
            uni.setStorageSync("token", data.data.access_token);
            uni.setStorageSync("simId", data.data.user.id);
            uni.setStorageSync("isImprove", data.data.is_improve);
            uni.setStorageSync("phone", data.data.user.parm1);
            uni.setStorageSync("isLoggedIn", true);
            uni.setStorageSync("userInfo", data.data.user);
            callback && callback();
          } else {
            uni.showToast({
              title: data.msg || "本地登录失败",
              icon: "none",
            });
          }
        },
        fail: () => {
          uni.hideLoading();
          uni.showToast({
            title: "本地登录失败",
            icon: "none",
          });
        },
      });
    },
    //获取学生列表
    getStudentList() {
      uni.request({
        url: `${this.baseUrl}/school/schoolStudentInfo/list`,
        method: "GET",
        header: {
          Authorization: uni.getStorageSync("token"),
        },
        data: {
          simId: uni.getStorageSync("simId"),
        },
        success: ({ data }) => {
          this.studentList = (data.rows || []).map((item) => ({
            name: item.name,
            sex: item.sex,
            className:item.grade+'级'+item.classId+'班',
            schoolName: item.sysDepts.deptName,
          }));
        },
      });
    },

    // 获取反馈统计
    getFeedbackStats() {
      uni.request({
        url: `${this.baseUrl}/school/parentsFeedback/getCount`,
        method: "GET",
        header: {
          Authorization: uni.getStorageSync("token"),
          "Content-Type": "application/json",
        },
        data: {
          userId: uni.getStorageSync("simId"),
        },
        success: ({ data }) => {
          if (data.code === 200) {
            this.feedbackStats = data.data;
          } else {
            console.warn("获取反馈列表失败：", data.message);
          }
        },
        fail: (err) => {
          console.error("反馈列表接口请求失败：", err);
        },
      });
    },

    navigateTo(url) {
      uni.navigateTo({
        url,
      });
    },
    handleLogout() {
      uni.showModal({
        title: "提示",
        content: "确定要退出登录吗？",
        success: (res) => {
          if (res.confirm) {
            // 清除登录信息
            uni.removeStorageSync("token");
            uni.removeStorageSync("userInfo");
            // 跳转到登录页
            uni.reLaunch({
              url: "/pages/login/login",
            });
          }
        },
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.user-container {
  height: 100%;
  background-color: #f5f5f5;
  padding:40rpx 40rpx;
}

// 用户信息头部
.user-header-section {
  position: relative;
  background: #409eff;
  color: #fff;
  padding: 60rpx 40rpx 40rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .user-info {
    display: flex;
    align-items: center;

    .avatar-wrapper {
      width: 120rpx;
      height: 120rpx;
      border-radius: 60rpx;
      border: 4rpx solid rgba(255, 255, 255, 0.4);
      overflow: hidden;
      margin-right: 30rpx;
    }

    .avatar {
      width: 100%;
      height: 100%;
    }

    .user-detail {
      .user-name {
        font-size: 36rpx;
        font-weight: 600;
        margin-bottom: 10rpx;
      }

      .school-info {
        font-size: 24rpx;
        opacity: 0.85;

        .dot-divider {
          margin: 0 6rpx;
        }
      }
    }
  }

  .edit-btn {
    display: flex;
    align-items: center;
    font-size: 28rpx;
    background: rgba(255, 255, 255, 0.2);
    color: #fff;
    border-radius: 30rpx;
    padding: 10rpx 20rpx;
    line-height: 1;
    height: auto;

    &::after {
      border: none;
    }
  }
}

// 学生信息卡片
.student-card { 
  background-color: #fff;
  border-radius: 12rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30rpx;
    .card-title {
      font-size: 32rpx;
      font-weight: 600;
      color: #333;
    }

    .card-action {
      display: flex;
      align-items: center;
      font-size: 26rpx;
      color: #999;
    }
  }

  .student-list {
    margin-top: 30rpx;

    .student-item {
      display: flex;
      align-items: center;
      margin-bottom: 20rpx;

      &:last-child {
        margin-bottom: 0;
      }

      .student-avatar {
        width: 80rpx;
        height: 80rpx;
        border-radius: 40rpx;
        margin-right: 20rpx;
      }

      .student-info {
        .student-name {
          font-size: 28rpx;
          font-weight: 500;
          color: #333;
          margin-bottom: 4rpx;
        }

        .student-class {
          font-size: 24rpx;
          color: #999;
          margin-left: 20rpx;
        }
      }
    }
  }
}

// 我的收藏卡片
.favorite-card {
  margin-top: 30rpx; 
  border-radius: 16rpx;
  padding: 30rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.05);
  background: #fff;
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center; 
    .card-title {
      font-size: 32rpx;
      font-weight: 600;
      color: #333;
      position: relative; 
    }
  }

  .favorite-content {
    .favorite-item {
      display: flex;
      align-items: center;
      padding: 20rpx;
      background: rgba(255, 107, 107, 0.03);
      border-radius: 12rpx;
      transition: all 0.3s ease;

      &:active {
        transform: translateY(2rpx);
        background: rgba(255, 107, 107, 0.08);
      }

      .favorite-text {
        flex: 1;
        margin-left: 20rpx;
        font-size: 28rpx;
        color: #333;
        font-weight: 500;
      }
    }
  }
}

// 反馈意见卡片
.feedback-card { 
  margin-top: 30rpx;
  background: linear-gradient(135deg, #fff 0%, #f8fcff 100%);
  border-radius: 16rpx;
  padding: 30rpx;
  box-shadow: 0 8rpx 32rpx rgba(64, 158, 255, 0.1);
  border: 1rpx solid rgba(64, 158, 255, 0.08);

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30rpx;

    .card-title {
      font-size: 32rpx;
      font-weight: 600;
      color: #333;
      position: relative;

      // &::after {
      //   content: "";
      //   position: absolute;
      //   bottom: -8rpx;
      //   left: 0;
      //   width: 40rpx;
      //   height: 3rpx;
      //   background: linear-gradient(90deg, #409eff 0%, #66b3ff 100%);
      //   border-radius: 2rpx;
      // }
    }

    .card-action {
      display: flex;
      align-items: center;
      font-size: 26rpx;
      color: #999;
      transition: color 0.3s ease;

      &:active {
        color: #2e7bff;
      }
    }
  }

  .feedback-stats {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30rpx;
    background: rgba(64, 158, 255, 0.03);
    border-radius: 12rpx;
    padding: 20rpx;

    .stat-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      flex: 1;
      position: relative;

      .stat-number {
        font-size: 36rpx;
        font-weight: 700;
        background: linear-gradient(135deg, #409eff 0%, #66b3ff 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        margin-bottom: 8rpx;
      }

      .stat-label {
        font-size: 24rpx;
        color: #666;
        font-weight: 500;
      }
    }

    .stat-divider {
      width: 1rpx;
      height: 50rpx;
      background: linear-gradient(
        180deg,
        transparent 0%,
        rgba(64, 158, 255, 0.2) 50%,
        transparent 100%
      );
    }
  }

  .quick-feedback {
    display: flex;
    justify-content: center;
    margin-bottom: 30rpx;

    .feedback-btn {
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 28rpx;
      background: linear-gradient(135deg, #409eff 0%, #66b3ff 100%);
      color: #fff;
      border: none;
      border-radius: 50rpx;
      padding: 18rpx 40rpx;
      line-height: 1;
      height: auto;
      box-shadow: 0 6rpx 20rpx rgba(64, 158, 255, 0.3);
      transition: all 0.3s ease;

      &:active {
        transform: translateY(2rpx);
        box-shadow: 0 4rpx 16rpx rgba(64, 158, 255, 0.2);
      }

      &::after {
        border: none;
      }

      text {
        margin-left: 10rpx;
        font-weight: 500;
      }
    }
  }

  .recent-feedback {
    .recent-title {
      font-size: 28rpx;
      font-weight: 600;
      color: #333;
      margin-bottom: 20rpx;
      padding-bottom: 12rpx;
      border-bottom: 2rpx solid #f0f8ff;
      position: relative;

      &::after {
        content: "";
        position: absolute;
        bottom: -2rpx;
        left: 0;
        width: 60rpx;
        height: 2rpx;
        background: linear-gradient(90deg, #409eff 0%, #66b3ff 100%);
      }
    }

    .feedback-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 24rpx 20rpx;
      margin-bottom: 12rpx;
      background: rgba(255, 255, 255, 0.8);
      border-radius: 12rpx;
      border: 1rpx solid rgba(64, 158, 255, 0.05);
      transition: all 0.3s ease;

      &:last-child {
        margin-bottom: 0;
      }

      &:active {
        transform: translateY(2rpx);
        background: rgba(64, 158, 255, 0.02);
      }

      .feedback-content {
        flex: 1;
        margin-right: 20rpx;

        .content-text {
          font-size: 28rpx;
          color: #333;
          line-height: 1.5;
          margin-bottom: 10rpx;
          display: -webkit-box;
          -webkit-box-orient: vertical;
          -webkit-line-clamp: 2;
          overflow: hidden;
          font-weight: 500;
        }

        .feedback-meta {
          display: flex;
          justify-content: space-between;
          align-items: center;

          .create-time {
            font-size: 24rpx;
            color: #999;
            font-weight: 500;
          }

          .status-tag {
            padding: 6rpx 16rpx;
            border-radius: 16rpx;
            font-size: 22rpx;
            color: #fff;
            font-weight: 600;
            box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);

            &.replied {
              background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
            }

            &.pending {
              background: linear-gradient(135deg, #ff7875 0%, #ff9c9c 100%);
            }
          }
        }
      }
    }
  }
}

// 底部版本信息
.footer {
  text-align: center;
  padding: 40rpx 0;

  .version-text {
    font-size: 24rpx;
    color: #999;
    margin-bottom: 30rpx;
  }

  .logout-btn {
    width: 90%;
    height: 80rpx;
    line-height: 80rpx;
    background-color: #fff;
    color: #ff4d4f;
    font-size: 28rpx;
    border-radius: 40rpx;
    margin: 0 auto;
    border: none;

    &::after {
      border: none;
    }

    &:active {
      opacity: 0.8;
    }
  }
}
</style>
