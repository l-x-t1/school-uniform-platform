<template>
  <div class="phone-login">
    <div class="phone-login-header">
      <div class="title" @click="goToHome" style="cursor: pointer;">石家庄市校服管理平台</div>
    </div>
    <div class="phone-login-container">
      <div>
        <el-form
          ref="phoneLoginForm"
          :model="phoneLoginForm"
          :rules="phoneLoginRules"
          class="phone-login-form"
        >
          <div class="form-header1">
            <div class="login-tabs">
              <div class="tab-item" @click="switchToLogin">
                密码登录
              </div>
              <div class="tab-item active" @click="switchToPhoneLogin">
                短信登录
              </div>
            </div>
          </div>

          <el-form-item prop="phone">
            <el-input
              v-model="phoneLoginForm.phone"
              type="text"
              auto-complete="off"
              placeholder="请输入手机号"
              @keyup.enter.native="handleSendCode"
            >
              <svg-icon
                slot="prefix"
                icon-class="phone"
                class="el-input__icon input-icon"
              />
            </el-input>
          </el-form-item>

          <el-form-item prop="code">
            <el-input
              v-model="phoneLoginForm.code"
              auto-complete="off"
              placeholder="请输入验证码"
              style="width: 63%"
              @keyup.enter.native="handleLogin"
            >
              <svg-icon
                slot="prefix"
                icon-class="validCode"
                class="el-input__icon input-icon"
              />
            </el-input>
            <el-button
              :disabled="countdown > 0"
              @click="handleSendCode"
              class="code-button"
            >
              {{ countdown > 0 ? `${countdown}秒后重试` : '获取验证码' }}
            </el-button>
          </el-form-item>

          <el-form-item style="width: 100%">
            <el-button
              :loading="loading"
              size="medium"
              type="primary"
              style="width: 100%"
              @click.native.prevent="handleLogin"
            >
              <span v-if="!loading">登 录</span>
              <span v-else>登 录 中...</span>
            </el-button>
          </el-form-item>

          <el-form-item style="text-align: center; margin-bottom: 0;">
            <!-- <router-link class="link-type" to="/login">账号登录</router-link>
            <span style="margin: 0 10px; color: #dcdfe6;">|</span> -->
            <router-link class="link-type" to="/forget-password">忘记密码？</router-link>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <div class="phone-login-footer">
      <div>版权所有: 石家庄市教育局</div>
      <div style="margin-top: 10px;">技术支持: 石家庄开发区亿网科技发展有限公司</div>
    </div>
  </div>
</template>

<script>
import { getPhoneCode, loginByCode } from "@/api/login";
import { validMobile } from "@/utils/validate";
import { setToken, setExpiresIn } from "@/utils/auth";

export default {
  name: "PhoneLogin",
  data() {
    return {
      phoneLoginForm: {
        phone: "",
        code: "",
      },
      phoneLoginRules: {
        phone: [
          { required: true, trigger: "blur", message: "请输入手机号" },
          { validator: this.validatePhoneNumber, trigger: "blur" },
        ],
        code: [
          { required: true, trigger: "change", message: "请输入验证码" },
          { min: 6, max: 6, message: "验证码长度为6位", trigger: "blur" },
        ],
      },
      loading: false,
      countdown: 0,
      timer: null,
      redirect: undefined,
    };
  },
  watch: {
    $route: {
      handler: function (route) {
        this.redirect = route.query && route.query.redirect;
      },
      immediate: true,
    },
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
    }
  },
  methods: {
    validatePhoneNumber(rule, value, callback) {
      if (!validMobile(value)) {
        callback(new Error("请输入正确的手机号码"));
      } else {
        callback();
      }
    },
    handleSendCode() {
      this.$refs.phoneLoginForm.validateField("phone", (valid) => {
        if (valid) {
          return;
        }

        // 调用获取验证码接口
        getPhoneCode({ phone: this.phoneLoginForm.phone })
          .then(() => {
            this.$message.success("验证码已发送");
            this.startCountdown();
          })
          .catch((error) => {
            
          });
      });
    },
    startCountdown() {
      this.countdown = 60;
      this.timer = setInterval(() => {
        if (this.countdown > 0) {
          this.countdown--;
        } else {
          clearInterval(this.timer);
          this.timer = null;
        }
      }, 1000);
    },
    handleLogin() {
      this.$refs.phoneLoginForm.validate((valid) => {
        if (valid) {
          this.loading = true;
          // 调用手机号登录接口
          loginByCode({
            phone: this.phoneLoginForm.phone,
            code: this.phoneLoginForm.code,
          })
            .then((res) => {
              let data = res.data || res;
              // 设置token
              if (data.access_token) {
                setToken(data.access_token);
                this.$store.commit("user/SET_TOKEN", data.access_token);
                if (data.expires_in) {
                  setExpiresIn(data.expires_in);
                  this.$store.commit("user/SET_EXPIRES_IN", data.expires_in);
                }
              }
              
              // 获取用户信息
              this.$store.dispatch("GetInfo").then((res) => {
                // 生成路由
                this.$store.dispatch("GenerateRoutes").then((accessRoutes) => {
                  this.$router.addRoutes(accessRoutes);
                  setTimeout(() => {
                    if (res.roles.includes("xuexiao")) {
                      this.$router.push({ path: "/schoolIndex" });
                    } else {
                      this.$router.push({ path: "/index" });
                    }
                  }, 500);
                });
              });
            })
            .catch(() => {
              this.loading = false;
            });
        }
      });
    },
    goToHome() {
      this.$router.push('/publicIndex');
    },
    switchToLogin() {
      this.$router.push('/login');
    },
    switchToPhoneLogin() {
      // 当前已经在短信登录页面，不需要跳转
    },
  },
};
</script>

<style scoped lang="scss">
.phone-login-header {
  height: 100px;
  background-color: #fff;
  color: #409eff;
  font-size: 36px;
  font-weight: bold;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  padding-left: 40px;
  
  .title {
    transition: all 0.3s ease;
    
    &:hover {
      color: #409eff;
      transform: scale(1.02);
    }
  }
}

.phone-login-container {
  height: calc(100vh - 200px);
  background-image: url("../assets/images/loginbg-1.jpg");
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat; 
  width: 100%;
  position: relative;
  
  .phone-login-form {
    width: 440px; 
    padding: 20px 40px 40px 40px;
    background-color: #fff;
    border-radius: 10px;
    position: absolute;
    top: 12%;
    right: 12%;
    
    .code-button {
      width: 35%;
      float: right;
      height: 40px;
      font-size: 14px;
    }
  }
}

.phone-login-footer {
  background-color: #fff;
  color: #909399;
  font-size: 16px; 
  text-align: center;
  height: 100px;
  box-sizing: border-box;
  padding-top: 30px;
}

.link-type {
  color: #409eff;
  text-decoration: none;
  
  &:hover {
    text-decoration: underline;
  }
}

::v-deep .el-input__inner {
  height: 40px;
}

.input-icon {
  color: #909399;
}

.el-form-item {
  margin-bottom: 22px;
}

.login-tabs {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
  border-bottom: 2px solid #f0f0f0;
  
  .tab-item {
    flex: 1;
    text-align: center;
    padding: 12px 0;
    font-size: 16px;
    font-weight: 500;
    color: #666;
    cursor: pointer;
    position: relative;
    transition: all 0.3s ease;
    
    &:hover {
      color: #409eff;
    }
    
    &.active {
      color: #1890ff;
      font-weight: bold;
      
      &::after {
        content: '';
        position: absolute;
        bottom: -2px;
        left: 0;
        right: 0;
        height: 2px;
        background-color: #1890ff;
      }
    }
  }
}
</style>
