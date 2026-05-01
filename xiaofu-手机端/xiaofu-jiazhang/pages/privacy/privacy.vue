<template>
  <view class="privacy-container">
    <view class="privacy-header">
      <text class="privacy-title">用户服务协议及隐私政策</text>
    </view>
    
    <view class="privacy-content">
      <view class="section">
        <view class="section-title">一、引言</view>
        <view class="section-content">
          <view class="paragraph">尊敬的用户，欢迎您使用石家庄市校服管理平台！</view>
          <view class="paragraph">我们非常重视您的个人信息和隐私保护。为了更好地保障您的个人权益，在您使用我们的产品与服务前，请您务必审慎阅读、充分理解本《用户服务协议及隐私政策》各条款内容。</view>
        </view>
      </view>
      
      <view class="section">
        <view class="section-title">二、我们收集的信息</view>
        <view class="section-content">
          <view class="paragraph">1. 您提供的信息：当您注册账户、使用我们的服务时，我们会收集您提供的相关信息，如手机号码、学生信息等。</view>
          <view class="paragraph">2. 设备信息：我们可能会收集您使用我们服务的设备相关信息，如设备型号、操作系统版本、设备设置、唯一设备标识符等。</view>
          <view class="paragraph">3. 日志信息：当您使用我们的服务时，我们的服务器会自动记录一些信息，如访问日期和时间、IP地址等。</view>
        </view>
      </view>
      
      <view class="section">
        <view class="section-title">三、我们如何使用信息</view>
        <view class="section-content">
          <view class="paragraph">1. 提供、维护和改进我们的服务。</view>
          <view class="paragraph">2. 处理您的订单及提供客户服务。</view>
          <view class="paragraph">3. 发送服务通知，如校服发放通知、退换货通知等。</view>
          <view class="paragraph">4. 进行数据分析，以改进我们的服务。</view>
        </view>
      </view>
      
      <view class="section">
        <view class="section-title">四、信息共享</view>
        <view class="section-content">
          <view class="paragraph">除非获得您的明确同意，我们不会与任何第三方共享您的个人信息，但以下情况除外：</view>
          <view class="paragraph">1. 为了完成订单处理，我们可能会将您的部分信息共享给校服供应商。</view>
          <view class="paragraph">2. 应法律法规要求、法律程序、诉讼和/或公共和政府机构的要求。</view>
        </view>
      </view>
      
      <view class="section">
        <view class="section-title">五、信息安全</view>
        <view class="section-content">
          <view class="paragraph">我们采取适当的技术和组织措施来保护您的个人信息不被未经授权的访问、丢失或损坏。</view>
        </view>
      </view>
      
      <view class="section">
        <view class="section-title">六、您的权利</view>
        <view class="section-content">
          <view class="paragraph">您有权访问、更正或删除您的个人信息。如果您希望行使这些权利，请联系我们的客户服务。</view>
        </view>
      </view>
      
      <view class="section">
        <view class="section-title">七、政策更新</view>
        <view class="section-content">
          <view class="paragraph">我们可能会不时更新本隐私政策。更新后的政策将在平台上公布，并在公布后立即生效。</view>
        </view>
      </view>
      
      <view class="section">
        <view class="section-title">八、联系我们</view>
        <view class="section-content">
          <view class="paragraph">如果您对本隐私政策有任何疑问或建议，请联系我们：</view>
          <view class="paragraph">电子邮件：support@sjzxiaofuapp.com</view>
          <view class="paragraph">电话：0311-12345678</view>
        </view>
      </view>
    </view>
    
    <view class="privacy-footer">
      <button class="back-btn" @click="goBack">返回</button>
    </view>
  </view>
</template>

<script>
export default {
  methods: {
    goBack() {
      uni.navigateBack();
    }
  }
}
</script>

<style lang="scss">
.privacy-container {
  min-height: 100vh;
  background-color: #ffffff;
  padding: 40rpx;
}

.privacy-header {
  margin-bottom: 40rpx;
  
  .privacy-title {
    font-size: 36rpx;
    font-weight: 600;
    color: #333333;
  }
}

.privacy-content {
  .section {
    margin-bottom: 40rpx;
    
    .section-title {
      font-size: 32rpx;
      font-weight: 600;
      color: #333333;
      margin-bottom: 20rpx;
    }
    
    .section-content {
      .paragraph {
        font-size: 28rpx;
        color: #666666;
        line-height: 1.6;
        margin-bottom: 16rpx;
      }
    }
  }
}

.privacy-footer {
  margin-top: 60rpx;
  
  .back-btn {
    width: 100%;
    height: 90rpx;
    line-height: 90rpx;
    background-color: #3B7AFF;
    color: #ffffff;
    font-size: 32rpx;
    font-weight: 500;
    border-radius: 45rpx;
  }
}
</style> 