<template>
  <view class="container">
    <view class="content-wrapper">
      <view class="header">
        <text class="main-title">{{ detail.title }}</text>
        <view class="info-row">
          <text class="time">{{ detail.timeLabel }}：{{ detail.time }}</text>
        </view>
      </view>

      <view class="content">
        <view class="section" v-for="section in detail.sections" :key="section.title">
          <view class="section-title">{{ section.title }}</view>
          <view class="section-content">
            <text
              class="text-paragraph"
              v-for="(text, index) in section.paragraphs"
              :key="index"
            >
              {{ text }}
            </text>
          </view>
        </view>
      </view>

      <view class="footer">
        <text class="sign">{{ detail.sign }}</text>
        <text class="date">{{ detail.date }}</text>
      </view>
    </view>
  </view>
</template>

<script>
const DETAIL_MAP = {
  meeting: {
    title: "2026年春季校服采购工作会议纪要",
    timeLabel: "会议时间",
    time: "2026-04-18 09:30",
    sign: "石家庄市示例学校",
    date: "2026年4月18日",
    sections: [
      {
        title: "一、会议背景",
        paragraphs: [
          "为规范校服采购流程，保障学生和家长权益，学校召开校服采购专题会议，研究本年度校服选用工作安排。",
        ],
      },
      {
        title: "二、会议决议",
        paragraphs: [
          "成立校服选用组织，成员包含学校管理人员、教师代表、家长代表和学生代表。",
          "采购过程坚持公开、公平、公正原则，关键环节在平台向家长公开。",
        ],
      },
    ],
  },
  survey: {
    title: "家长购买意向征集结果公示",
    timeLabel: "征集时间",
    time: "2026-04-20 至 2026-04-24",
    sign: "校服选用组织",
    date: "2026年4月25日",
    sections: [
      {
        title: "一、征集范围",
        paragraphs: [
          "本次意见征集面向相关年级学生家长，主要收集校服款式、价格区间、尺码服务和售后需求。",
        ],
      },
      {
        title: "二、统计结果",
        paragraphs: [
          "大多数家长支持按照春秋装、夏装、冬季校服分批采购，并建议重点关注面料舒适度和尺码调换效率。",
        ],
      },
    ],
  },
  result: {
    title: "2026年春季校服选用结果公示",
    timeLabel: "公示时间",
    time: "2026-04-28",
    sign: "采购小组",
    date: "2026年4月28日",
    sections: [
      {
        title: "一、选用结果",
        paragraphs: [
          "经样衣展示、家长意见征集和选用组织评审，确定本次春季校服采购方案。",
        ],
      },
      {
        title: "二、后续安排",
        paragraphs: [
          "后续将组织家长确认订单信息、选择尺码并完成支付，学校同步跟进生产交付和质量验收。",
        ],
      },
    ],
  },
  quality: {
    title: "春秋装面料及成衣质量检验报告",
    timeLabel: "报告时间",
    time: "2026-04-30",
    sign: "质量验收小组",
    date: "2026年4月30日",
    sections: [
      {
        title: "一、检测项目",
        paragraphs: [
          "检测项目包括纤维含量、甲醛含量、pH值、色牢度、异味和外观质量等。",
        ],
      },
      {
        title: "二、检测结论",
        paragraphs: [
          "本批次样衣检测结果符合中小学生校服相关质量要求，后续批量生产仍需按批次提交检测材料。",
        ],
      },
    ],
  },
};

export default {
  data() {
    return {
      detail: DETAIL_MAP.meeting,
    };
  },
  onLoad(options) {
    const type = options.type || "meeting";
    const fallback = DETAIL_MAP[type] || DETAIL_MAP.meeting;
    this.detail = Object.assign({}, fallback, {
      title: options.title ? decodeURIComponent(options.title) : fallback.title,
    });
    uni.setNavigationBarTitle({
      title: "流程详情",
    });
  },
};
</script>

<style lang="scss">
.container {
  min-height: 100vh;
  background-color: #ffffff;
}

.content-wrapper {
  padding: 32rpx;
  min-height: 100vh;
  box-sizing: border-box;
}

.header {
  padding-bottom: 30rpx;
  border-bottom: 1rpx solid #eef0f4;
}

.main-title {
  display: block;
  font-size: 36rpx;
  font-weight: 600;
  color: #1f2530;
  line-height: 1.45;
  margin-bottom: 20rpx;
}

.info-row {
  display: flex;
  align-items: center;
}

.time {
  font-size: 26rpx;
  color: #7a8290;
}

.content {
  padding: 34rpx 0 12rpx;
}

.section {
  margin-bottom: 40rpx;
}

.section-title {
  font-size: 31rpx;
  font-weight: 600;
  color: #1f2530;
  margin-bottom: 18rpx;
}

.text-paragraph {
  display: block;
  font-size: 28rpx;
  color: #4a5361;
  line-height: 1.75;
  margin-bottom: 14rpx;
}

.footer {
  padding-top: 50rpx;
  text-align: right;
}

.sign {
  display: block;
  font-size: 28rpx;
  color: #303744;
  font-weight: 500;
  margin-bottom: 10rpx;
}

.date {
  font-size: 26rpx;
  color: #7a8290;
}
</style>
