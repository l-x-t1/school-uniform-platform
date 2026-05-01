<template>
  <div class="forget-password">
    <div class="forget-password-header">
      <div class="title" @click="goToHome" style="cursor: pointer;">石家庄市校服管理平台</div>
    </div>
    <div class="forget-password-container">
      <div>
        <el-form
          ref="forgetForm"
          :model="forgetForm"
          :rules="forgetRules"
          class="forget-form"
        >
          <div class="form-header1">
            <h3 class="title" style="font-size: 24px;font-weight: bold;text-align: center;">找回密码</h3>
          </div>

          <el-form-item prop="phone">
            <el-input
              v-model="forgetForm.phone"
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
              v-model="forgetForm.code"
              auto-complete="off"
              placeholder="请输入验证码"
              style="width: 63%"
              @keyup.enter.native="handleResetPassword"
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

          <el-form-item prop="password">
            <el-input
              v-model="forgetForm.password"
              type="password"
              auto-complete="off"
              placeholder="请输入新密码"
              @keyup.enter.native="handleResetPassword"
            >
              <svg-icon
                slot="prefix"
                icon-class="password"
                class="el-input__icon input-icon"
              />
            </el-input>
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="forgetForm.confirmPassword"
              type="password"
              auto-complete="off"
              placeholder="请再次输入新密码"
              @keyup.enter.native="handleResetPassword"
            >
              <svg-icon
                slot="prefix"
                icon-class="password"
                class="el-input__icon input-icon"
              />
            </el-input>
          </el-form-item>

          <el-form-item style="width: 100%">
            <el-button
              :loading="loading"
              size="medium"
              type="primary"
              style="width: 100%"
              @click.native.prevent="handleResetPassword"
            >
              <span v-if="!loading">重置密码</span>
              <span v-else>重置中...</span>
            </el-button>
          </el-form-item>

          <el-form-item style="text-align: center; margin-bottom: 0;">
            <router-link class="link-type" to="/login">返回登录</router-link>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <div class="forget-password-footer">
      <div>版权所有: 石家庄市教育局</div>
      <div style="margin-top: 10px;">技术支持: 石家庄开发区亿网科技发展有限公司</div>
    </div>
  </div>
</template>

<script>
import { getPhoneCode, resetPassword } from "@/api/login";
import { validMobile } from "@/utils/validate";

export default {
  name: "ForgetPassword",
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.forgetForm.password) {
        callback(new Error("两次输入的密码不一致"));
      } else {
        callback();
      }
    }; 
    const validatePassword = (rule, value, callback) => {
      // 检查长度
      if (value.length < 8 || value.length > 20) {
        callback(new Error("密码长度必须在8-20位之间"))
        return
      }

      // 检查是否包含字母
      if (!/[a-zA-Z]/.test(value)) {
        callback(new Error("密码必须包含至少一个字母"))
        return
      }

      // 检查是否包含数字
      if (!/\d/.test(value)) {
        callback(new Error("密码必须包含至少一个数字"))
        return
      } 

      //必须包含一个特殊字符
      if (!/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?~`]/.test(value)) {
        callback(new Error("密码必须包含至少一个特殊字符"))
        return
      } 

      callback()
    }
    return {
      forgetForm: {
        phone: "",
        code: "",
        password: "",
        confirmPassword: "",
      },
      forgetRules: {
        phone: [
          { required: true, trigger: "blur", message: "请输入手机号" },
          { validator: this.validatePhoneNumber, trigger: "blur" },
        ],
        code: [
          { required: true, trigger: "change", message: "请输入验证码" },
          { min: 6, max: 6, message: "验证码长度为6位", trigger: "blur" },
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入新密码" }, 
          { validator: validatePassword, trigger: ["blur", "change"] },
        ],
        confirmPassword: [
          { required: true, trigger: "blur", message: "请再次输入密码" },
          { validator: validateConfirmPassword, trigger: "blur" },
        ],
      },
      loading: false,
      countdown: 0,
      timer: null,
    };
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
      this.$refs.forgetForm.validateField("phone", (valid) => {
        if (valid) {
          return;
        }

        // 调用获取验证码接口
        getPhoneCode({ phone: this.forgetForm.phone })
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
    handleResetPassword() {
      this.$refs.forgetForm.validate((valid) => {
        if (valid) {
          this.loading = true;
          // 调用重置密码的API
          resetPassword({
            phone: this.forgetForm.phone,
            code: this.forgetForm.code,
            password: this.forgetForm.password
          }).then(() => {
            this.$message.success("密码重置成功");
            this.$router.push({ path: "/login" });
          }).catch(() => {
            this.loading = false;
          });
        }
      });
    },
    goToHome() {
      this.$router.push('/publicIndex');
    },
  },
};
</script>

<style scoped lang="scss">
.forget-password-header {
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

.forget-password-container {
  height: calc(100vh - 200px);
  background-image: url("../assets/images/loginbg-1.jpg");
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat; 
  width: 100%;
  position: relative;
  
  .forget-form {
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

.forget-password-footer {
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
</style>
