<template>
  <view class="container">
    <!-- 欢迎信息 -->
    <view class="home-hero">
      <view>
        <text class="hero-title">校服管理平台</text>
        <text class="hero-subtitle">{{ welcomeText }}</text>
      </view>
      <button class="hero-btn" @click="navigateTo('/pages/uniform/uniform-list')">去选购</button>
    </view>

    <!-- 轮播图区域 -->
    <swiper
      class="banner-swiper"
      circular
      :indicator-dots="true"
      :autoplay="true"
      :interval="3000"
      :duration="500"
    >
      <swiper-item v-for="(item, index) in banners" :key="index">
        <image
          :src="item.image"
          mode="aspectFill"
          class="banner-image"
          @click="onBannerClick(item)"
        ></image>
      </swiper-item>
    </swiper>

    <!-- 购买提醒 -->
    <view class="reminder-card">
      <view class="reminder-item" @click="navigateTo('/pages/user/student-info')">
        <text class="reminder-number">{{ studentCount }}</text>
        <text class="reminder-label">已绑定学生</text>
      </view>
      <view class="reminder-divider"></view>
      <view class="reminder-item" @click="navigateTo('/pages/order/order-list')">
        <text class="reminder-number">{{ unpaidCount }}</text>
        <text class="reminder-label">待处理订单</text>
      </view>
      <view class="reminder-divider"></view>
      <view class="reminder-item" @click="navigateTo('/pages/cart/cart')">
        <text class="reminder-number">{{ cartCount }}</text>
        <text class="reminder-label">购物车商品</text>
      </view>
    </view>

    <!-- 功能导航区 -->
    <view class="nav-section">
      <view
        class="nav-item"
        v-for="(item, index) in navItems"
        :key="index"
        @click="navigateTo(item.url)"
      >
        <view class="nav-icon" :style="{ backgroundColor: item.bgColor }">
          <uni-icons :type="item.icon" size="24" color="#fff"></uni-icons>
        </view>
        <text class="nav-text">{{ item.title }}</text>
      </view>
    </view>

    <!-- 流程公开 -->
    <view class="section process-section">
      <view class="section-header">
        <text class="section-title">采购流程公开</text>
        <view class="more-link" @click="navigateTo('/pages/stats/stats')">
          <text>查看全部</text>
          <uni-icons type="right" size="14" color="#999"></uni-icons>
        </view>
      </view>
      <view class="process-grid">
        <view class="process-item" v-for="item in processItems" :key="item.type" @click="viewProcessDetail(item.type)">
          <view class="process-icon">
            <uni-icons :type="item.icon" size="20" color="#30A3FF"></uni-icons>
          </view>
          <text class="process-text">{{ item.title }}</text>
        </view>
      </view>
    </view>

    <!-- 公告区域 -->
    <view class="section notice-section"> 
      
             <!-- 顶部选项卡 -->
       <view class="notice-tabs">
         <view
           class="tab-item"
           :class="{ active: currentNoticeTab === 0 }"
           @click="switchNoticeTab(0)"
         >
           采购公告
         </view>
         <view
           class="tab-item"
           :class="{ active: currentNoticeTab === 1 }"
           @click="switchNoticeTab(1)"
         >
           成交公告
         </view>
         <!-- <view
           class="tab-item"
           :class="{ active: currentNoticeTab === 2 }"
           @click="switchNoticeTab(2)"
         >
           备选公告
         </view> -->
       </view>
      
      <view class="notice-list">
        <view
          class="notice-item"
          v-for="(item, index) in notices"
          :key="index"
          @click="viewNotice(item)"
        >
          <view class="notice-content">
            <text class="notice-title">{{ item.isBc == 1?item.title+'（补充）':item.title }}</text>
            <view class="notice-info"> 
              <text class="notice-source">{{ item.schoolName || item.deptname || '校服管理平台' }}</text>
              <text class="notice-time">{{ formatNoticeTime(item) }}</text>
            </view>
          </view>
          <uni-icons type="right" size="16" color="#CCCCCC"></uni-icons>
        </view>
        <view class="empty-state" v-if="notices.length == 0">
          <view class="empty-icon">📋</view>
          <text class="empty-text">暂无公告</text> 
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { listOrders } from '@/api/order'
import { listAfterSale } from '@/api/afterSale'

export default {
  data() {
    return {
      baseUrl: getApp().globalData.baseUrl,
      banners: [
        { image: "../../static/xf2.png" },
        { image: "../../static/xf1.png" },
      ],
      navItems: [
        {
          title: "校服选购",
          icon: "shop",
          url: "/pages/uniform/uniform-list",
          bgColor: "#30A3FF",
        },
        {
          title: "我的订单",
          icon: "paperplane",
          url: "/pages/order/order-list",
          bgColor: "#FF9F1C",
        },
        {
          title: "购物车",
          icon: "cart",
          url: "/pages/cart/cart",
          bgColor: "#12B886",
        },
        {
          title: "公告查看",
          icon: "list",
          url: "/pages/notice/notice-list",
          bgColor: "#FF7D3B",
        },
        {
          title: "质检报告",
          icon: "list",
          url: "/pages/zjbg/zjbg",
          bgColor: "#53c21b",
        },
        {
          title: "问卷调查",
          icon: "help",
          url: "/pages/survey/survey-list",
          bgColor: "#33CCCC",
        },
        {
          title: "意见反馈",
          icon: "chat",
          url: "/pages/feedback/feedback",
          bgColor: "#7D3BFF",
        },
        {
          title: "我的",
          icon: "person",
          url: "/pages/user/user",
          bgColor: "#007AFF",
        }
        
      ],
      notices: [],
      currentNoticeTab: 0, // 0: 采购公告, 1: 中标公告, 2: 备选公告
      data:{
        status: '1',
        noticeType: '1',
        pageNum: 1,
        pageSize: 10,
        simId: uni.getStorageSync("simId"),
      },
      studentCount: 0,
      unpaidCount: 0,
      cartCount: 0,
      processItems: [
        { title: '会议纪要', type: 'meeting', icon: 'calendar' },
        { title: '意见征集', type: 'survey', icon: 'chat' },
        { title: '选用结果', type: 'result', icon: 'checkbox' },
        { title: '质检报告', type: 'quality', icon: 'paperclip' }
      ]
    };
  }, 
  computed: {
    welcomeText() {
      const user = uni.getStorageSync('userInfo') || {};
      const phone = uni.getStorageSync('phone') || user.phone || user.parm1 || '';
      return phone ? `欢迎您，${phone}` : '欢迎使用家长端';
    }
  },
  onLoad() {
    if (!this.checkLogin()) return;
    this.getNoticeList();
    this.refreshHomeStats();
  },
  onShow() {
    this.refreshHomeStats();
  },
  onPullDownRefresh() {
    this.getNoticeList();
  },
  methods: {
    // 检查登录状态
    checkLogin() {
      const isLoggedIn = uni.getStorageSync("isLoggedIn");
      if (!isLoggedIn) {
        uni.reLaunch({
          url: "/pages/login/login",
        });
        return false;
      }
      return true;
    }, 

    // 导航到指定页面
    navigateTo(url) {
      const pureUrl = String(url || '').split('?')[0];
      const tabPages = [
        '/pages/index/index',
        '/pages/notice/notice-list',
        '/pages/survey/survey-list',
        '/pages/user/user',
      ];
      if (tabPages.includes(pureUrl)) {
        uni.switchTab({ url: pureUrl });
        return;
      }
      uni.navigateTo({ url });
    },

    // 查看通知详情
    viewNotice(notice) {
      uni.navigateTo({
        url: `/pages/notice/notice-detail?id=${notice.id}`,
      });
    },

    // 查看校服详情
    viewUniform(uniform) {
      uni.navigateTo({
        url: `/pages/uniform/uniform-detail?id=${uniform.id}`,
      });
    },

    // 处理待办事项
    handleTodo(todo) {
      uni.navigateTo({
        url: todo.url,
      });
    },

    // 查看最新质检报告
    viewLatestReport() {
      uni.navigateTo({
        url: "/pages/report/report-detail?id=1",
      });
    },

    // 点击轮播图
    onBannerClick(banner) {
      uni.navigateTo({
        url: banner.url,
      });
    },

    // 跳转到消息中心
    goToMessages() {
      uni.navigateTo({
        url: "/pages/message/message-list",
      });
    }, 
    getNoticeList() {
      let that = this;
      uni.request({
        url: `${this.baseUrl}/school/groupNotice/list`,
        method: "GET",
        data: that.data,
        header: {
          Authorization: uni.getStorageSync("token"),
        },
        success: ({ data }) => {
           const rows = data && data.rows ? data.rows : [];
           this.notices = rows;
           uni.stopPullDownRefresh();
         },
        fail: () => {
          this.notices = [];
          uni.stopPullDownRefresh();
        },
      });
    },
    formatNoticeTime(item) {
      const value = item.publishTime || item.resultPublishTime || item.createTime;
      return value ? String(value).split(' ')[0] : '暂无';
    },
    async refreshHomeStats() {
      this.studentCount = (uni.getStorageSync('studentList') || []).length;
      this.cartCount = (uni.getStorageSync('cartList') || []).reduce((sum, item) => sum + Number(item.quantity || 1), 0);
      try {
        const [unpaidRes, unshippedRes, shippedRes, afterSaleRes] = await Promise.all([
          listOrders({ status: 'unpaid', pageNum: 1, pageSize: 1 }),
          listOrders({ status: 'unshipped', pageNum: 1, pageSize: 1 }),
          listOrders({ status: 'shipped', pageNum: 1, pageSize: 1 }),
          listAfterSale({ pageNum: 1, pageSize: 200 })
        ]);

        const afterSaleCount = (afterSaleRes.rows || []).filter((item) =>
          ['0', '1', '3'].includes(String(item.status || '0'))
        ).length;

        this.unpaidCount =
          Number(unpaidRes.total || 0) +
          Number(unshippedRes.total || 0) +
          Number(shippedRes.total || 0) +
          afterSaleCount;
      } catch (error) {
        const localOrders = uni.getStorageSync('devOrders') || {};
        const pendingStatuses = ['unpaid', 'unshipped', 'shipped', 'afterSale'];
        this.unpaidCount = Object.keys(localOrders).filter((key) => pendingStatuses.includes(localOrders[key].status)).length;
      }
    },
    
    // 切换公告标签
    switchNoticeTab(index) {
      this.currentNoticeTab = index;
      this.data.noticeType = index === 0 ? '1' : '2';
      this.data.status = index === 0 ? '1' : '2';
      this.data.pageNum = 1;
      this.data.pageSize = 10;
      this.getNoticeList();
    },
    // 查看选购流程详情
    viewProcessDetail(step) {
      // 根据不同步骤跳转到对应的详情页
      const stepTitles = {
        meeting: "学校班子会议纪要",
        survey: "家长意见征集表决",
        team: "选用组织名单",
        method: "选购模式确定",
        mode1: "自主确定企业模式",
        mode2: "家长投票确定企业模式",
        mode3: "招标机构确定企业模式",
        result: "选购结果公示",
        contract: "采购合同",
        quality: "质量检验报告",
      };

      uni.navigateTo({
        url: `/pages/process/process-list?type=${step}&title=${encodeURIComponent(stepTitles[step] || '流程公开')}`,
      });
    },
  },
};
</script>

<style lang="scss">
.container {
  min-height: 100vh;
  background-color: #f6f6f6;
  padding-bottom: 40rpx;
}

.home-hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 34rpx 30rpx 28rpx;
  background: linear-gradient(135deg, #30A3FF 0%, #1677d2 100%);
  color: #fff;

  .hero-title {
    display: block;
    font-size: 38rpx;
    font-weight: 700;
    margin-bottom: 10rpx;
  }

  .hero-subtitle {
    display: block;
    font-size: 26rpx;
    opacity: 0.9;
  }

  .hero-btn {
    width: 150rpx;
    height: 64rpx;
    line-height: 64rpx;
    border-radius: 34rpx;
    background-color: #fff;
    color: #1677d2;
    font-size: 26rpx;
    margin: 0;

    &::after {
      border: none;
    }
  }
}

.status-bar {
  height: var(--status-bar-height);
  width: 100%;
  background-color: #ffffff;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 30rpx;
  background-color: #ffffff;
  border-bottom: 1rpx solid #f0f0f0;

  .school-info {
    display: flex;
    align-items: center;

    .school-name {
      font-size: 34rpx;
      font-weight: 600;
      color: #333333;
    }

    .role-tag {
      margin-left: 16rpx;
      font-size: 22rpx;
      color: #3b7aff;
      background-color: rgba(59, 122, 255, 0.1);
      padding: 4rpx 12rpx;
      border-radius: 10rpx;
    }
  }

  .header-right {
    padding: 10rpx;
  }
}

.banner-swiper {
  width: 100%;
  height: 320rpx;

  .banner-image {
    width: 100%;
    height: 100%;
  }
}

.reminder-card {
  display: flex;
  align-items: center;
  margin: 20rpx 30rpx;
  padding: 28rpx 0;
  background-color: #fff;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);

  .reminder-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .reminder-number {
    font-size: 40rpx;
    font-weight: 700;
    color: #30A3FF;
    margin-bottom: 8rpx;
  }

  .reminder-label {
    font-size: 24rpx;
    color: #666;
  }

  .reminder-divider {
    width: 1rpx;
    height: 54rpx;
    background-color: #edf0f5;
  }
}

.nav-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  padding: 30rpx 30rpx 20rpx;
  background-color: #ffffff;
  margin-bottom: 20rpx;

  .nav-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    min-width: 0;
    margin-bottom: 22rpx;

    .nav-icon {
      width: 90rpx;
      height: 90rpx;
      border-radius: 50%;
      display: flex;
      justify-content: center;
      align-items: center;
      margin-bottom: 12rpx;
    }

    .nav-text {
      font-size: 24rpx;
      color: #333333;
    }
  }
}

.process-section {
  padding: 30rpx;

  .section-header {
    margin-bottom: 24rpx;
  }
}

.process-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16rpx;
}

.process-item {
  min-width: 0;
  background-color: #f7fbff;
  border-radius: 12rpx;
  padding: 20rpx 10rpx;
  display: flex;
  flex-direction: column;
  align-items: center;

  .process-icon {
    width: 56rpx;
    height: 56rpx;
    border-radius: 28rpx;
    background-color: #eaf6ff;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 12rpx;
  }

  .process-text {
    font-size: 24rpx;
    color: #333;
    white-space: nowrap;
  }
}

.section {
  margin: 0 30rpx 20rpx;
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 0 0rpx 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;

    .section-title {
      font-size: 30rpx;
      font-weight: 600;
      color: #333333;
      position: relative;
      padding-left: 20rpx;

      &::before {
        content: "";
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 6rpx;
        height: 28rpx;
        background-color: #3b7aff;
        border-radius: 3rpx;
      }
    }

    .more-link {
      display: flex;
      align-items: center;
      font-size: 24rpx;
      color: #999999;
    }
  }
}

// 公告选项卡
.notice-tabs {
  display: flex;
  height: 88rpx;
  background-color: #fff;
  border-bottom: 1rpx solid #e8eaed;
  position: relative;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);

  .tab-item {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 28rpx;
    color: #666;
    position: relative;
    transition: all 0.3s ease;
    font-weight: 400;

    &.active {
      color: #3b7aff;
      font-weight: 600;

      &::after {
        content: "";
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translateX(-50%);
        width: 48rpx;
        height: 4rpx;
        background-color: #3b7aff;
        border-radius: 2rpx;
        transition: all 0.3s ease;
      }
    }

    &:active {
      opacity: 0.7;
    }
  }
}

.notice-list {
  padding: 0 30rpx;
  .notice-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20rpx 0;
    border-bottom: 1rpx solid #f0f0f0;

    &:last-child {
      border-bottom: none;
      padding-bottom: 0;
    }

    .notice-content {
      flex: 1;

      .notice-title {
        font-size: 28rpx;
        color: #333333;
        margin-bottom: 8rpx;
        display: block;
      }

      .notice-info {
        display: flex;
        align-items: center;
        gap: 20rpx;

        .notice-source {
          font-size: 24rpx;
          color: #3b7aff;
          background-color: rgba(59, 122, 255, 0.1);
          padding: 4rpx 12rpx;
          border-radius: 12rpx;
        }

        .notice-time {
          font-size: 24rpx;
          color: #999999;
        }
      }
    }
  }
}

.uniform-scroll {
  white-space: nowrap;

  .uniform-card {
    display: inline-block;
    width: 220rpx;
    margin-right: 20rpx;
    border-radius: 12rpx;
    overflow: hidden;

    &:last-child {
      margin-right: 0;
    }

    .uniform-image {
      width: 220rpx;
      height: 260rpx;
      background-color: #f5f5f5;
    }

    .uniform-info {
      padding: 12rpx 0;

      .uniform-name {
        font-size: 24rpx;
        color: #333333;
        display: block;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        margin-bottom: 6rpx;
      }

      .uniform-price {
        font-size: 24rpx;
        color: #e74c3c;
        font-weight: 500;
      }
    }
  }
}

.todo-list {
  .todo-item {
    display: flex;
    align-items: center;
    padding: 20rpx 0;
    border-bottom: 1rpx solid #f0f0f0;

    &:last-child {
      border-bottom: none;
      padding-bottom: 0;
    }

    .todo-tag {
      padding: 6rpx 16rpx;
      font-size: 22rpx;
      border-radius: 6rpx;
      margin-right: 20rpx;

      &.todo-tag-survey {
        background-color: rgba(52, 152, 219, 0.1);
        color: #3498db;
      }

      &.todo-tag-payment {
        background-color: rgba(231, 76, 60, 0.1);
        color: #e74c3c;
      }
    }

    .todo-content {
      flex: 1;

      .todo-title {
        font-size: 28rpx;
        color: #333333;
        margin-bottom: 6rpx;
        display: block;
      }

      .todo-desc {
        font-size: 24rpx;
        color: #999999;
      }
    }
  }
}

.report-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: rgba(46, 204, 113, 0.05);
  padding: 20rpx;
  border-radius: 12rpx;
  border: 1rpx solid rgba(46, 204, 113, 0.2);

  .report-info {
    .report-title {
      font-size: 28rpx;
      color: #333333;
      font-weight: 500;
      margin-bottom: 10rpx;
    }

    .report-desc {
      font-size: 24rpx;
      color: #666666;
      margin-bottom: 6rpx;
    }

    .report-tip {
      font-size: 24rpx;
      color: #2ecc71;
      font-weight: 500;
    }
  }

  .report-icon {
    margin-left: 20rpx;
    width: 80rpx;
    height: 80rpx;
    background-color: rgba(46, 204, 113, 0.1);
    border-radius: 50%;
    display: flex;
    justify-content: center;
    align-items: center;
  }
}

.floating-button {
  position: fixed;
  right: 30rpx;
  bottom: 100rpx;
  width: 100rpx;
  height: 100rpx;
  background: linear-gradient(to bottom right, #3b7aff, #6ba1ff);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 4rpx 12rpx rgba(59, 122, 255, 0.3);
  z-index: 999;
}

.process-section {
  .process-list {
    display: flex;
    flex-direction: column;
    gap: 20rpx;
    padding: 20rpx 0;

    .process-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 40rpx 30rpx;
      border-radius: 12rpx;
      transition: all 0.3s ease;
      position: relative;
      overflow: hidden;

      // 根据不同的步骤设置不同的背景色
      &:nth-child(1) {
        background: linear-gradient(135deg, #e8f5ff 0%, #c6e6ff 100%);
      }
      &:nth-child(2) {
        background: linear-gradient(135deg, #fff3e8 0%, #ffe4cc 100%);
      }
      &:nth-child(3) {
        background: linear-gradient(135deg, #e8fff1 0%, #ccffe4 100%);
      }
      &:nth-child(4) {
        background: linear-gradient(135deg, #f5e8ff 0%, #e6ccff 100%);
      }
      &:nth-child(5) {
        background: linear-gradient(135deg, #ffe8e8 0%, #ffcccc 100%);
      }
      &:nth-child(6) {
        background: linear-gradient(135deg, #e8fffa 0%, #ccfff2 100%);
      }
      &:nth-child(7) {
        background: linear-gradient(135deg, #fff8e8 0%, #ffe8cc 100%);
      }
      &:nth-child(8) {
        background: linear-gradient(135deg, #e8eeff 0%, #cce6ff 100%);
      }
      &:nth-child(9) {
        background: linear-gradient(135deg, #ffe8f5 0%, #ffcce6 100%);
      }
      &:nth-child(10) {
        background: linear-gradient(135deg, #f2ffe8 0%, #e6ffcc 100%);
      }

      &:active {
        transform: scale(0.98);
        opacity: 0.9;
      }

      .process-title {
        font-size: 28rpx;
        color: #333;
        flex: 1;
        font-weight: 500;
      }

      .process-icon {
        width: 80rpx;
        height: 80rpx;
        margin-left: 20rpx;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.5);
        padding: 10rpx;
      }
    }
  }
}
// 空状态
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 40rpx;

  .empty-icon {
    font-size: 120rpx;
    margin-bottom: 32rpx;
    opacity: 0.4;
  }

  .empty-text {
    font-size: 28rpx;
    color: #666;
    font-weight: 500;
    margin-bottom: 16rpx;
    margin-top: 20rpx;
  }

  .empty-subtext {
    font-size: 26rpx;
    color: #999;
    text-align: center;
    line-height: 1.5;
  }
}
</style>
