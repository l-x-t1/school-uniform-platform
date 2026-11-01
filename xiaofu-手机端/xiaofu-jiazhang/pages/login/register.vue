<template>
  <view class="register-container">
    <view class="register-header">
      <text class="register-title">{{ text.title }}</text>
      <text class="register-subtitle">{{ text.subtitle }}</text>
    </view>

    <view class="register-form">
      <view class="input-group">
        <view class="phone-input">
          <text class="area-code">+86</text>
          <input
            v-model="phone"
            type="number"
            maxlength="11"
            :placeholder="text.phonePlaceholder"
            @input="validatePhone"
          />
        </view>

        <view class="verify-input">
          <input
            v-model="verifyCode"
            type="number"
            maxlength="6"
            :placeholder="text.codePlaceholder"
          />
          <button
            class="verify-btn"
            :disabled="!isPhoneValid || counting"
            @click="getVerificationCode"
          >
            {{ codeText }}
          </button>
        </view>

        <view class="password-block">
          <view class="password-input">
            <input
              v-model="password"
              type="password"
              maxlength="20"
              :placeholder="text.passwordPlaceholder"
            />
          </view>
          <view class="password-tip">{{ text.passwordTip }}</view>
        </view>
      </view>

      <button
        class="register-btn"
        :class="{ 'btn-active': isFormValid }"
        @click="handleRegister"
      >
        {{ text.registerButton }}
      </button>

      <view class="login-link" @click="goToLogin">
        <text>{{ text.hasAccount }}</text>
        <text class="link">{{ text.loginNow }}</text>
      </view>
    </view>

    <view class="privacy-policy">
      <text class="privacy-text">{{ text.privacyPrefix }}</text>
      <text class="privacy-link" @click="showPrivacyPolicy">{{ text.privacyLink }}</text>
    </view>
  </view>
</template>

<script>
import { registerParent } from '@/api/auth'

const TEXT = {
  title: '\u514d\u8d39\u6ce8\u518c',
  subtitle: '\u8ba9\u6821\u670d\u66f4\u9633\u5149\u7f8e\u4e3d',
  phonePlaceholder: '\u8bf7\u8f93\u5165\u6ce8\u518c\u624b\u673a\u53f7',
  codePlaceholder: '\u8bf7\u8f93\u5165\u9a8c\u8bc1\u7801',
  passwordPlaceholder: '\u8bf7\u8f93\u5165\u5bc6\u7801',
  passwordTip: '\u5bc6\u7801\u97008-20\u4f4d\uff0c\u4e14\u5305\u542b\u5b57\u6bcd\u3001\u6570\u5b57\u3001\u7b26\u53f7',
  registerButton: '\u6ce8\u518c',
  hasAccount: '\u5df2\u6709\u8d26\u53f7\uff1f',
  loginNow: '\u7acb\u5373\u767b\u5f55',
  privacyPrefix: '\u6ce8\u518c\u5373\u8868\u793a\u60a8\u5df2\u540c\u610f',
  privacyLink: '\u300a\u7528\u6237\u670d\u52a1\u534f\u8bae\u53ca\u9690\u79c1\u653f\u7b56\u300b',
  codeText: '\u83b7\u53d6\u9a8c\u8bc1\u7801',
  devCodeToast: '\u5f00\u53d1\u9a8c\u8bc1\u7801\uff1a123456',
  countdownSuffix: '\u79d2\u540e\u91cd\u65b0\u83b7\u53d6',
  invalidForm: '\u8bf7\u5b8c\u5584\u6ce8\u518c\u4fe1\u606f',
  loading: '\u6ce8\u518c\u4e2d...',
  success: '\u6ce8\u518c\u6210\u529f',
  failed: '\u6ce8\u518c\u5931\u8d25'
}

export default {
  data() {
    return {
      text: TEXT,
      phone: '',
      verifyCode: '',
      password: '',
      counting: false,
      counter: 60,
      codeText: TEXT.codeText,
      timer: null
    }
  },
  computed: {
    isPhoneValid() {
      return /^1[3-9]\d{9}$/.test(this.phone)
    },
    isPasswordValid() {
      const hasLetter = /[a-zA-Z]/.test(this.password)
      const hasNumber = /\d/.test(this.password)
      const hasSymbol = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>/?]/.test(this.password)
      return this.password.length >= 8 && this.password.length <= 20 && hasLetter && hasNumber && hasSymbol
    },
    isFormValid() {
      return this.isPhoneValid && this.verifyCode.length === 6 && this.isPasswordValid
    }
  },
  methods: {
    validatePhone() {
      this.phone = String(this.phone || '').slice(0, 11)
    },
    getVerificationCode() {
      if (this.counting || !this.isPhoneValid) return

      this.verifyCode = '123456'
      uni.showToast({
        title: this.text.devCodeToast,
        icon: 'none'
      })
      this.startCountdown()
    },
    startCountdown() {
      this.counting = true
      this.counter = 60
      this.codeText = `${this.counter}${this.text.countdownSuffix}`

      this.timer = setInterval(() => {
        this.counter -= 1
        if (this.counter <= 0) {
          clearInterval(this.timer)
          this.timer = null
          this.counting = false
          this.codeText = this.text.codeText
          return
        }
        this.codeText = `${this.counter}${this.text.countdownSuffix}`
      }, 1000)
    },
    handleRegister() {
      if (!this.isFormValid) {
        uni.showToast({
          title: this.text.invalidForm,
          icon: 'none'
        })
        return
      }

      uni.showLoading({
        title: this.text.loading
      })

      registerParent({
        phone: this.phone,
        code: this.verifyCode,
        password: this.password
      })
        .then(() => {
          uni.hideLoading()
          uni.showToast({
            title: this.text.success,
            icon: 'success'
          })
          setTimeout(() => {
            uni.navigateBack()
          }, 1200)
        })
        .catch((error) => {
          uni.hideLoading()
          uni.showToast({
            title: error.message || this.text.failed,
            icon: 'none'
          })
        })
    },
    goToLogin() {
      uni.navigateBack()
    },
    showPrivacyPolicy() {
      uni.navigateTo({
        url: '/pages/privacy/privacy'
      })
    }
  },
  onUnload() {
    if (this.timer) {
      clearInterval(this.timer)
      this.timer = null
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

  .password-block {
    position: relative;
    padding-bottom: 28rpx;
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
    width: 220rpx;
    height: 64rpx;
    line-height: 64rpx;
    font-size: 26rpx;
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
    line-height: 1.5;
  }

  .register-btn {
    width: 100%;
    height: 80rpx;
    line-height: 80rpx;
    background: linear-gradient(to right, #3b7aff, #6ba1ff);
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
      color: #3b7aff;
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
    color: #3b7aff;
  }
}
</style>
