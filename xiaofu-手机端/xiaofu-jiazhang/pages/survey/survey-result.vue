<template>
  <view class="container">
    <!-- 问卷基本信息 -->
    <view class="survey-header">
      <text class="survey-title">{{survey.title}}</text>
      <view class="survey-meta">
        <text class="survey-source">{{survey.source}}</text>
        <text class="survey-date">提交时间: {{survey.submitTime}}</text>
      </view>
      <view class="result-summary">
        <view class="summary-item">
          <text class="item-label">参与人数</text>
          <text class="item-value">{{survey.participantsCount}}人</text>
        </view>
        <view class="summary-item">
          <text class="item-label">问题数量</text>
          <text class="item-value">{{survey.questions.length}}题</text>
        </view>
      </view>
    </view>
    
    <!-- 问卷内容和答案 -->
    <view class="result-content">
      <view 
        class="question-item" 
        v-for="(question, qIndex) in survey.questions" 
        :key="qIndex"
      >
        <view class="question-header">
          <text class="question-number">{{qIndex + 1}}</text>
          <text class="question-title">{{question.title}}</text>
        </view>
        
        <!-- 单选题结果 -->
        <view class="result-display" v-if="question.type === 'radio'">
          <view 
            class="option-item" 
            v-for="(option, oIndex) in question.options" 
            :key="oIndex"
            :class="{'selected': question.answer === oIndex}"
          >
            <view class="option-content">
              <view class="select-indicator" v-if="question.answer === oIndex">
                <uni-icons type="checkmarkempty" size="14" color="#3B7AFF"></uni-icons>
              </view>
              <text class="option-text">{{option}}</text>
            </view>
            
            <view class="option-stats">
              <view class="progress-bar">
                <view 
                  class="progress-inner" 
                  :style="{width: getOptionPercentage(question, oIndex) + '%'}"
                ></view>
              </view>
              <text class="option-percentage">{{getOptionPercentage(question, oIndex)}}%</text>
            </view>
          </view>
        </view>
        
        <!-- 多选题结果 -->
        <view class="result-display" v-if="question.type === 'checkbox'">
          <view 
            class="option-item" 
            v-for="(option, oIndex) in question.options" 
            :key="oIndex"
            :class="{'selected': isOptionInAnswer(question.answer, oIndex)}"
          >
            <view class="option-content">
              <view class="select-indicator" v-if="isOptionInAnswer(question.answer, oIndex)">
                <uni-icons type="checkmarkempty" size="14" color="#3B7AFF"></uni-icons>
              </view>
              <text class="option-text">{{option}}</text>
            </view>
            
            <view class="option-stats">
              <view class="progress-bar">
                <view 
                  class="progress-inner" 
                  :style="{width: getOptionPercentage(question, oIndex) + '%'}"
                ></view>
              </view>
              <text class="option-percentage">{{getOptionPercentage(question, oIndex)}}%</text>
            </view>
          </view>
        </view>
        
        <!-- 填空题结果 -->
        <view class="result-display" v-if="question.type === 'text'">
          <view class="text-answer">
            <text class="text-content">{{question.answer || '未填写'}}</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 底部按钮 -->
    <view class="footer-actions">
      <button class="share-btn" open-type="share">
        <uni-icons type="redo" size="18" color="#fff"></uni-icons>
        <text>分享结果</text>
      </button>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      surveyId: '',
      survey: {
        id: '1',
        title: '2023秋季校服满意度调查',
        source: '教务处',
        submitTime: '2023-09-05 15:30',
        participantsCount: 256,
        questions: [
          {
            type: 'radio',
            title: '您对本学期校服的整体满意度如何？',
            options: [
              '非常满意',
              '比较满意',
              '一般',
              '不太满意',
              '非常不满意'
            ],
            answer: 1, // 选择了"比较满意"
            stats: [15, 45, 25, 10, 5] // 各选项的百分比
          },
          {
            type: 'checkbox',
            title: '您认为校服需要改进的方面有哪些？（可多选）',
            options: [
              '面料舒适度',
              '款式设计',
              '尺码合适度',
              '耐用性',
              '价格'
            ],
            answer: [1, 3], // 选择了"款式设计"和"耐用性"
            stats: [30, 50, 25, 40, 20] // 各选项的百分比
          },
          {
            type: 'text',
            title: '您对校服改进有什么具体建议？',
            answer: '希望春秋装使用更透气的面料，夏装可以考虑增加防晒功能，冬装希望能更轻薄保暖。'
          }
        ]
      }
    }
  },
  onLoad(options) {
    if (options.id) {
      this.surveyId = options.id;
      // 这里应该根据id从服务器获取调查结果
      // this.getSurveyResult(this.surveyId);
    }
  },
  methods: {
    // 获取问卷结果
    getSurveyResult(id) {
      // 模拟从服务器获取数据
      uni.showLoading({
        title: '加载中...'
      });
      
      setTimeout(() => {
        uni.hideLoading();
        // 实际项目中这里应该是调用API获取数据
      }, 1000);
    },
    
    // 计算选项百分比
    getOptionPercentage(question, index) {
      if (question.stats && question.stats[index] !== undefined) {
        return question.stats[index];
      }
      return 0;
    },
    
    // 检查多选题中选项是否被选中
    isOptionInAnswer(answer, index) {
      if (Array.isArray(answer)) {
        return answer.includes(index);
      }
      return false;
    }
  }
}
</script>

<style lang="scss">
.container {
  padding-bottom: 120rpx;
}

.survey-header {
  background-color: #fff;
  padding: 30rpx;
  margin-bottom: 20rpx;
  
  .survey-title {
    font-size: 36rpx;
    font-weight: 600;
    color: #333;
    margin-bottom: 20rpx;
    display: block;
  }
  
  .survey-meta {
    display: flex;
    align-items: center;
    margin-bottom: 30rpx;
    
    .survey-source {
      font-size: 26rpx;
      color: #3B7AFF;
      background-color: rgba(59, 122, 255, 0.1);
      padding: 4rpx 16rpx;
      border-radius: 20rpx;
      margin-right: 20rpx;
    }
    
    .survey-date {
      font-size: 26rpx;
      color: #999;
    }
  }
  
  .result-summary {
    display: flex;
    background-color: #f9f9f9;
    border-radius: 12rpx;
    padding: 20rpx;
    
    .summary-item {
      flex: 1;
      display: flex;
      flex-direction: column;
      align-items: center;
      
      .item-label {
        font-size: 24rpx;
        color: #999;
        margin-bottom: 8rpx;
      }
      
      .item-value {
        font-size: 32rpx;
        color: #333;
        font-weight: 500;
      }
    }
  }
}

.result-content {
  background-color: #fff;
  padding: 30rpx;
  
  .question-item {
    margin-bottom: 40rpx;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .question-header {
      display: flex;
      margin-bottom: 20rpx;
      
      .question-number {
        width: 40rpx;
        height: 40rpx;
        border-radius: 20rpx;
        background-color: #3B7AFF;
        color: #fff;
        font-size: 24rpx;
        display: flex;
        justify-content: center;
        align-items: center;
        margin-right: 16rpx;
        flex-shrink: 0;
      }
      
      .question-title {
        font-size: 30rpx;
        color: #333;
        line-height: 1.5;
        font-weight: 500;
      }
    }
    
    .result-display {
      .option-item {
        margin-bottom: 20rpx;
        padding: 16rpx;
        border-radius: 8rpx;
        background-color: #f9f9f9;
        
        &.selected {
          background-color: rgba(59, 122, 255, 0.05);
          border: 1rpx solid #3B7AFF;
        }
        
        .option-content {
          display: flex;
          align-items: center;
          margin-bottom: 12rpx;
          
          .select-indicator {
            width: 30rpx;
            height: 30rpx;
            border-radius: 15rpx;
            background-color: #3B7AFF;
            display: flex;
            justify-content: center;
            align-items: center;
            margin-right: 12rpx;
          }
          
          .option-text {
            font-size: 28rpx;
            color: #333;
          }
        }
        
        .option-stats {
          display: flex;
          align-items: center;
          
          .progress-bar {
            flex: 1;
            height: 16rpx;
            background-color: #f0f0f0;
            border-radius: 8rpx;
            overflow: hidden;
            margin-right: 12rpx;
            
            .progress-inner {
              height: 100%;
              background-color: #3B7AFF;
              border-radius: 8rpx;
            }
          }
          
          .option-percentage {
            font-size: 24rpx;
            color: #666;
            width: 70rpx;
            text-align: right;
          }
        }
      }
      
      .text-answer {
        padding: 20rpx;
        background-color: #f9f9f9;
        border-radius: 8rpx;
        
        .text-content {
          font-size: 28rpx;
          color: #333;
          line-height: 1.5;
        }
      }
    }
  }
}

.footer-actions {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 20rpx 30rpx;
  background-color: #fff;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
  
  .share-btn {
    width: 100%;
    height: 90rpx;
    border-radius: 45rpx;
    background-color: #3B7AFF;
    color: #fff;
    font-size: 30rpx;
    display: flex;
    justify-content: center;
    align-items: center;
    
    uni-icons {
      margin-right: 10rpx;
    }
    
    &::after {
      border: none;
    }
  }
}
</style> 