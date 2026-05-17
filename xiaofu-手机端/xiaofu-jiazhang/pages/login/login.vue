<template>
  <view class="login-container">
    <!-- 顶部图片区域 -->
    <view class="login-banner">
      <!-- <image src="https://ai-public.mastergo.com/ai/img_res/bd26cc8c732750796b54680304c36c5b.jpg" mode="aspectFill" class="banner-image"></image> -->
    </view>
    
    <!-- 标题与欢迎信息 -->
    <view class="login-header">
      <text class="login-title">石家庄市校服管理平台</text>
      <text class="welcome-text">欢迎登录家长端</text>
    </view>
    
    <!-- 登录表单 -->
    <view class="login-form">
        <view class="input-group">
        <view class="phone-input">
          <text class="area-code">+86</text>
          <input
            type="number"
            v-model="phone"
            maxlength="11"
            placeholder="请输入手机号"
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
          <uni-button
            :disabled="!isPhoneValid || counting"
            class="verify-btn" 
            @click="getVerificationCode"
          >
            {{ codeText }}
          </uni-button>
        </view>
      </view>
      
      <!-- 登录按钮 --> 
      <button 
        class="login-btn" 
        :class="{'btn-active': isFormValid}"
        @click="handleLogin"
      >
        登录
      </button>
      
      <!-- 微信一键登录 -->
      <view class="wechat-login">
        <view class="divider">
          <view class="line"></view>
          <text class="text">或</text>
          <view class="line"></view>
        </view>
        <button class="wechat-btn" type="success" open-type="getPhoneNumber" @getphonenumber="getPhoneNumber">
            <uni-icons type="weixin" style="margin-right: 10rpx;" color="#fff" size="30"></uni-icons>
          <text class="btn-text">微信一键登录</text>
        </button>
      </view>
    </view>
    
    <!-- 底部隐私说明 -->
    <view class="privacy-policy">
      <text class="privacy-text">还没有账号？ </text>
      <text class="register-link" @click="goToRegister">免费注册</text>
      <text class="privacy-text">  |  登录即表示您已同意</text>
      <text class="privacy-link" @click="showPrivacyPolicy">《用户服务协议及隐私政策》</text>
    </view>
  </view>
</template>

<script>
import config from '@/config'
import { mobileLogin, sendSmsCode, wechatPhoneLogin } from '@/api/auth'
import { setToken, setUserInfo } from '@/utils/auth'

export default {
  data() {
    return {
      phone: '', // 手机号
      verifyCode: '', // 验证码
      counting: false, // 是否在倒计时
      counter: 60, // 倒计时秒数
      codeText: '获取验证码', // 验证码按钮文字
      timer: null // 定时器
    }
  },
  computed: {
    // 验证手机号格式是否正确
    isPhoneValid() {
      return /^1[3-9]\d{9}$/.test(this.phone);
    },
    // 表单是否有效（手机号和验证码都填写且格式正确）
    isFormValid() {
      return this.isPhoneValid && this.verifyCode.length === 6;
    }
  },
  methods: {
    validatePhone() {
      this.phone = String(this.phone || '').slice(0, 11);
    },

    // 获取验证码
    getVerificationCode() {
      if (this.counting || !this.isPhoneValid) return;
      
      // 显示加载提示
      uni.showLoading({
        title: '发送中...'
      });
      
      sendSmsCode({ phone: this.phone, scene: 'parent_login' })
        .then(() => {
          uni.hideLoading();
          uni.showToast({
            title: '验证码已发送',
            icon: 'success'
          });
          this.startCountdown();
        })
        .catch((error) => {
          if (config.enableMockFallback) {
            uni.hideLoading();
            uni.showToast({
              title: '开发环境验证码：123456',
              icon: 'none'
            });
            this.verifyCode = '123456';
            this.startCountdown();
            return;
          }

          uni.hideLoading();
          uni.showToast({
            title: error.message || '验证码发送失败',
            icon: 'none'
          });
        });
    },

    completeLogin(payload = {}) {
      uni.removeStorageSync('devOrders');
      uni.removeStorageSync('pendingCommentOrder');
      uni.removeStorageSync('selectedOrder');

      if (payload.token) {
        setToken(payload.token);
      }

      setUserInfo({
        phone: this.phone,
        userType: 'parent',
        ...(payload.user || payload.userInfo || {})
      });
      uni.setStorageSync('isLoggedIn', true);

      if (payload.simId) uni.setStorageSync('simId', payload.simId);
      if (payload.isImprove !== undefined) uni.setStorageSync('isImprove', payload.isImprove);
      if (this.phone) uni.setStorageSync('phone', this.phone);

      uni.reLaunch({
        url: payload.isImprove === false ? '/pages/user/student-info' : '/pages/index/index'
      });
    },
    
    mockLogin() {
      setToken('dev-token');
      this.completeLogin({
        token: 'dev-token',
        user: {
          phone: this.phone,
          userType: 'parent',
          nickName: '家长用户'
        },
        isImprove: true
      });
    },
    
    // 处理登录
    handleLogin() {
      if (!this.isFormValid) return;
      
      uni.showLoading({
        title: '登录中...'
      });

      mobileLogin({
        phone: this.phone,
        code: this.verifyCode
      })
        .then((res) => {
          uni.hideLoading();
          this.completeLogin(res.data || res);
        })
        .catch((error) => {
          if (config.enableMockFallback && this.verifyCode === '123456') {
            uni.hideLoading();
            this.mockLogin();
            return;
          }

          uni.hideLoading();
          uni.showToast({
            title: error.message || '登录失败',
            icon: 'none'
          });
        });
    },
    
    // 微信一键登录
    getPhoneNumber(e) {
      if (e.detail.errMsg === 'getPhoneNumber:ok') {
        uni.showLoading({
          title: '登录中...'
        });

        wechatPhoneLogin({
          code: e.detail.code,
          encryptedData: e.detail.encryptedData,
          iv: e.detail.iv
        })
          .then((res) => {
            uni.hideLoading();
            this.completeLogin(res.data || res);
          })
          .catch((error) => {
            if (config.enableMockFallback) {
              uni.hideLoading();
              this.completeLogin({
                token: 'dev-wechat-token',
                user: {
                  userType: 'parent',
                  isWechatLogin: true
                },
                isImprove: true
              });
              return;
            }

            uni.hideLoading();
            uni.showToast({
              title: error.message || '微信登录失败',
              icon: 'none'
            });
          });
      } else {
        uni.hideLoading();
        uni.showToast({
          title: '获取手机号失败',
          icon: 'none'
        });
      }
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
    // 显示隐私政策
    showPrivacyPolicy() {
      uni.navigateTo({
        url: '/pages/privacy/privacy'
      });
    },
    
    // 跳转到注册页面
    goToRegister() {
      uni.navigateTo({
        url: '/pages/login/register'
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
.login-container {
  min-height: 100vh;
  background-color: #ffffff;
  position: relative;
  display: flex;
  flex-direction: column;
}

.login-banner {
  width: 100%;
  height: 260rpx;
  overflow: hidden;
  
  .banner-image {
    width: 100%;
    height: 100%;
  }
}

.login-header {
  padding: 40rpx 40rpx 20rpx;
  margin-top: 100rpx;
  .login-title {
    font-size: 40rpx;
    font-weight: 600;
    color: #333333;
    display: block;
    margin-bottom: 16rpx;
  }
  
  .welcome-text {
    font-size: 28rpx;
    color: #666666;
    display: block;
  }
}

.login-form {
  padding: 40rpx;
  
//   .input-group {
//     display: flex;
//     align-items: center;
//     height: 100rpx;
//     background-color: #f8f8f8;
//     border-radius: 50rpx;
//     padding: 0 30rpx;
//     margin-bottom: 30rpx;
    
//     .iconfont {
//       font-size: 36rpx;
//       color: #999999;
//       margin-right: 20rpx;
//     }
    
//     .input {
//       flex: 1;
//       height: 100rpx;
//       line-height: 100rpx;
//       font-size: 28rpx;
//       color: #333333;
//     }
//   }
.input-group {
  margin-bottom: 48rpx;
}

.phone-input,
.verify-input {
  display: flex;
  align-items: center;
  height: 88rpx;
  border-bottom: 1px solid #e5e5e5;
  margin-bottom: 32rpx;
}

.area-code {
  font-size: 14px;
  color: #333333;
  margin-right: 24rpx;
}

input {
  flex: 1;
  height: 100%;
  font-size: 14px;
}

.verify-btn {
  width: 200rpx;
  height: 64rpx;
  line-height: 64rpx;
  font-size: 14px;
  color: #1989fa;
  background: none;
  padding: 0;

}

.verify-btn[disabled] {
  color: #999999;
  background: none;
}
  
  .login-btn {
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
  
  .wechat-login {
    margin-top: 40rpx;
    
    .divider {
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 40rpx;
      
      .line {
        width: 80rpx;
        height: 1px;
        background-color: #e5e5e5;
      }
      
      .text {
        margin: 0 20rpx;
        font-size: 24rpx;
        color: #999999;
      }
    }
    
    .wechat-btn {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 100%;
      height: 80rpx;
    //   background-color: #f8f8f8;
      background-color: #07c160;
      border-radius: 50rpx;  
      color: #fff;    
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

  .register-link {
    font-size: 24rpx;
    color: #3B7AFF;
    font-weight: bold;
  }
}

// 图标字体
@font-face {
  font-family: 'iconfont';
  src: url('data:font/woff2;charset=utf-8;base64,d09GMgABAAAAAARQAAsAAAAACOQAAAQDAAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHFQGYACDMgqEXIQ4ATYCJAMUCwwABCAFhGcHShvCB8gekqRjAQUKAMrp8fA1Vt/f7p4hxkuieCSaeCKZRBoiQSLRKREKnUro5Ol/rW0/YtjoJFQyE82ayXQ6YZo5HyaTr9MpzaHkQA8A4P+/n7vmI9Ig/AEkAwHTfmieJkpKkyg1FwfMQJRBrBpvEHDVejz1wPq/12oyNa1A8wNZoAAZxEhH953+8ilf0AH5d1wz/QMcEIBhKQRiWM6eYuCQXBOo1k2oAdiWmEzJFcEUGHOyZG4lCPuqcnIrcAD/fPzDiMDASJVEvffZRmEFTF6YTDOc/p+OZwLQOJ8L2ywSFgE5cZppXFGf4CK1fNXdAlVVMfr//+7+3ZPJ/8dLmkV0Vcx/eCRRZCAaJGo8ZvNjxgtY+VHihckPsEoM09vcB6qSgk4ydYA70AM2ga70LZFILSbTx3iSNMpQ3C9PShQrXlBecEjZU04HizZdP3/l3JJyQztPH9sxrIxcPzrnXrdcjrN7unbnGRf79P7Zrq5uZ5g3d+yZt507j+1vOBNP6Fxc2JV6qnOwMOMc2dmF0o8oRCLnpAR29XSnYsQGIf/08Q59x/Z37t9ZHNrhHCjlFc7GCx0pUQYdJsrC3VOEjtE80D2Zi9WpTSdaWpaXEwGH+HE6TpTtxyy7qBMThwAO5Z3LQYnpdCZS0L0cSyV6dmeIsmNu4b7uHdLAkCfRzuE+TlEq5MkXXmHXiZ5dZ/I62W5RXJt7prB3x5z9Z6c7zvF5O4ckO8+d2/1m2DsSBxd29A89e7bnhE9xJPnfyMSuLnFNY8PBtrSGQ1uyCnvaaqbWgkT2jd/t2gB1X5dFkQCwzzMbpnWMTYNpOL7MaMQkM3+ByXxEZBvAc2iWTTbx0L4dPERE9vJTsRHAc3iZSfZ+++qPsG9n/yKb+MgeSq+yr1hX2kA3lk0HJP0Q+huwfcYMcOxf50MbAU0bm7Fe4dZqc6gluvwvy8/VxD8qnNK/2l4K+Lcx9w9+KWbOUbdaOlRNSCZULVhMLvQ3Jb8vXlTpD6vV/F/1TVd+2bqsJRkyUwZGZQaSaXOQc1yEysgyVEZXoVpw4fCIsQTtkYJ54wjC2AbIRtdBMnYLOcdnqMy8Q2UcGKrLEDh3xvJBNIkREZgHqEXgwDSLqZZkqB2DW02DkkjH0GN0DfQBIY0aZBvVahgRqWHAPqDHdJGqCgFDw4SBbQ11gxMhz0ODBJZFYiVpoCBu0mCpndmDqcVUC5EQYDyANAmwwGhmYmokNKiVB25qa1ASFYGpZ+E10A9wP0UZathUc6FRhFQhAzdQj9GJKFUuBCbsVsKAWjOoNbARQrzV1UCCo/1JTCVSAQXyYCY16QrTFWWv127PQ1BJ35WixErURFNdDTXV1rNZVUDcUo9T5rGmamoJAAAA') format('woff2');
}

.iconfont {
  font-size: 16px;
  font-style: normal;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #999999 !important;
}

.icon-phone:before {
  content: "\e6dd";
}

.icon-safe:before {
  content: "\e6ce";
}

.icon-wechat:before {
  content: "\e637";
}
</style> 
