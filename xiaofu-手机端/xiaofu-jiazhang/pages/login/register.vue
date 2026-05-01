<template>
  <view class="register-container">
    <!-- 顶部标题 -->
    <view class="register-header">
      <text class="register-title">免费注册</text>
      <text class="register-subtitle">让校服更阳光美丽</text>
    </view>
    
    <!-- 注册表单 -->
    <view class="register-form">
      <view class="input-group">
        <view class="phone-input">
          <text class="area-code">+86</text>
          <input
            type="number"
            v-model="phone"
            maxlength="11"
            placeholder="请输入注册手机号"
            @input="validatePhone"
          />
        </view>
        <view class="verify-input">
          <input
            type="number"
            v-model="verifyCode"
            maxlength="6"
            placeholder="请输入验证码"
          />
          <button
            :disabled="!isPhoneValid || counting"
            class="verify-btn" 
            @click="getVerificationCode"
          >
            {{ codeText }}
          </button>
        </view>
        <view class="password-input">
          <input
            type="password"
            v-model="password"
            placeholder="请输入密码"
            maxlength="20"
          />
          <view class="password-tip">密码需8-20位字符，需包含字母/数字/符号3种组合</view>
        </view>
      </view>
      
      <!-- 注册按钮 -->
      <button 
        class="register-btn" 
        :class="{'btn-active': isFormValid}"
        @click="handleRegister"
      >
        注册
      </button>
      
      <!-- 已有账号 -->
      <view class="login-link" @click="goToLogin">
        <text>已有账号？</text>
        <text class="link">立即登录</text>
      </view>
    </view>
    
    <!-- 底部隐私说明 -->
    <view class="privacy-policy">
      <text class="privacy-text">注册即表示您已同意</text>
      <text class="privacy-link" @click="showPrivacyPolicy">《用户服务协议及隐私政策》</text>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      phone: '', // 手机号
      verifyCode: '', // 验证码
      password: '', // 密码
      counting: false, // 是否在倒计时
      counter: 60, // 倒计时秒数
      codeText: '获取验证码', // 验证码按钮文字
      timer: null, // 定时器
    }
  },
  computed: {
    // 验证手机号格式是否正确
    isPhoneValid() {
      return /^1[3-9]\d{9}$/.test(this.phone);
    },
    // 验证密码格式是否正确
    isPasswordValid() {
      const hasLetter = /[a-zA-Z]/.test(this.password);
      const hasNumber = /\d/.test(this.password);
      const hasSymbol = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(this.password);
      const validLength = this.password.length >= 8 && this.password.length <= 20;
      const typeCount = [hasLetter, hasNumber, hasSymbol].filter(Boolean).length;
      return validLength && typeCount >= 3;
    },
    // 表单是否有效
    isFormValid() {
      return this.isPhoneValid && this.verifyCode.length === 6 && this.isPasswordValid;
    }
  },
  methods: {
    // 获取验证码
    getVerificationCode() {
      if (this.counting || !this.isPhoneValid) return;
      
      uni.showLoading({
        title: '发送中...'
      });
      
      // 模拟发送验证码请求
      setTimeout(() => {
        uni.hideLoading();
        uni.showToast({
          title: '验证码已发送',
          icon: 'success'
        });
        
        this.startCountdown();
      }, 1500);
    },
    
    // 开始倒计时
    startCountdown() {
      this.counting = true;
      this.counter = 60;
      this.codeText = `${this.counter}秒后重新获取`;
      
      this.timer = setInterval(() => {
        this.counter--;
        this.codeText = `${this.counter}秒后重新获取`;
        
        if (this.counter <= 0) {
          clearInterval(this.timer);
          this.counting = false;
          this.codeText = '获取验证码';
        }
      }, 1000);
    },
    
    // 处理注册
    handleRegister() {
      if (!this.isFormValid) return;
      
      uni.showLoading({
        title: '注册中...'
      });
      
      // 模拟注册请求
      setTimeout(() => {
        uni.hideLoading();
        uni.showToast({
          title: '注册成功',
          icon: 'success'
        });
        
        // 注册成功后延迟跳转到登录页
        setTimeout(() => {
          uni.navigateBack();
        }, 1500);
      }, 1500);
    },
    
    // 跳转到登录页
    goToLogin() {
      uni.navigateBack();
    },
    
    // 显示隐私政策
    showPrivacyPolicy() {
      uni.navigateTo({
        url: '/pages/privacy/privacy'
      });
    }
  },
  // 清除定时器
  onUnload() {
    if (this.timer) {
      clearInterval(this.timer);
    }
  }
}
</script>

<style lang="scss">
.register-container {
  min-height: 100vh;
  background-color: #ffffff;
  position: relative;
  display: flex;
  flex-direction: column;
}

.register-header {
  padding: 40rpx 40rpx 20rpx;
  margin-top: 100rpx;
  
  .register-title {
    font-size: 40rpx;
    font-weight: 600;
    color: #333333;
    display: block;
    margin-bottom: 16rpx;
  }
  
  .register-subtitle {
    font-size: 28rpx;
    color: #666666;
    display: block;
  }
}

.register-form {
  padding: 40rpx;
  
  .input-group {
    margin-bottom: 48rpx;
  }
  
  .phone-input,
  .verify-input,
  .password-input {
    display: flex;
    align-items: center;
    height: 88rpx;
    border-bottom: 1px solid #e5e5e5;
    margin-bottom: 32rpx;
  }
  
  .area-code {
    font-size: 28rpx;
    color: #333333;
    margin-right: 24rpx;
  }
  
  input {
    flex: 1;
    height: 100%;
    font-size: 28rpx;
  }
  
  .verify-btn {
    width: 200rpx;
    height: 64rpx;
    line-height: 64rpx;
    font-size: 28rpx;
    color: #1989fa;
    background: none;
    padding: 0;
  }
  
  .verify-btn[disabled] {
    color: #999999;
    background: none;
  }
  
  .password-tip {
    font-size: 24rpx;
    color: #999999;
    margin-top: 8rpx;
    position: absolute;
    bottom: -30rpx;
  }
  
  .register-btn {
    width: 100%;
    height: 80rpx;
    line-height: 80rpx;
    background: linear-gradient(to right, #3B7AFF, #6BA1FF);
    color: #ffffff;
    font-size: 32rpx;
    font-weight: 500;
    border-radius: 50rpx;
    margin-top: 100rpx;
    opacity: 0.7;
  }
  
  .btn-active {
    opacity: 1;
  }
  
  .login-link {
    text-align: center;
    margin-top: 32rpx;
    font-size: 28rpx;
    color: #999999;
    
    .link {
      color: #3B7AFF;
      margin-left: 8rpx;
    }
  }
}

.privacy-policy {
  margin-top: auto;
  padding: 40rpx;
  text-align: center;
  
  .privacy-text {
    font-size: 24rpx;
    color: #999999;
  }
  
  .privacy-link {
    font-size: 24rpx;
    color: #3B7AFF;
  }
}
</style> 