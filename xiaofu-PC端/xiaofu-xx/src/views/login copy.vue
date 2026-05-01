<template>
  <div class="login">
    <div class="tech-particles"></div>
    <div class="floating-dots">
      <div class="dot" v-for="i in 10" :key="i"></div>
    </div>
    <div class="page-title"></div>
    <div class="login-container">
      <!-- <div class="login-left">
        <div class="system-info">
          <div class="system-subtitle">相关政策</div>
          <div class="feature-list">
            <div
              class="feature-item"
              v-for="(item, index) in noticeList"
              :key="item.id"
            >
              <div class="feature-icon">
                <i :class="getRandomIcon(index)"></i>
              </div>
              <span
                style="
                  width: 220px;
                  display: inline-block;
                  font-weight: normal;
                  font-size: 16px;
                "
                @click="showNoticeDetail(item)"
                :title="item.title"
                >{{ item.title }}</span
              >
              <span class="dateTime">{{ item.createTime.split(" ")[0] }}</span>
            </div>
          </div>
        </div>
      </div> -->
      <el-form
        ref="loginForm"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
      >
        <div class="form-header1">
          <h3 class="title">石家庄市校服管理平台</h3>
        </div>

        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            type="text"
            auto-complete="off"
            placeholder="用户名"
            @keyup.enter.native="handleLogin"
          >
            <svg-icon
              slot="prefix"
              icon-class="user"
              class="el-input__icon input-icon"
            />
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            auto-complete="off"
            placeholder="密码"
            @keyup.enter.native="handleLogin"
          >
            <svg-icon
              slot="prefix"
              icon-class="password"
              class="el-input__icon input-icon"
            />
          </el-input>
        </el-form-item>

        <el-form-item prop="code" v-if="captchaEnabled">
          <el-input
            v-model="loginForm.code"
            auto-complete="off"
            placeholder="验证码"
            style="width: 63%"
            @keyup.enter.native="handleLogin"
          >
            <svg-icon
              slot="prefix"
              icon-class="validCode"
              class="el-input__icon input-icon"
            />
          </el-input>
          <div class="login-code">
            <img :src="codeUrl" @click="getCode" class="login-code-img" />
          </div>
        </el-form-item>

        <!-- <div class="login-options">
          <el-checkbox v-model="loginForm.rememberMe">记住密码</el-checkbox>
          <a href="javascript:;" class="forget-pwd">忘记密码?</a>
        </div> -->

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
          <div style="float: right" v-if="register">
            <router-link class="link-type" :to="'/register'"
              >立即注册</router-link
            >
          </div>
        </el-form-item>

        <div class="tech-copyright">
          <div class="tech-line small"></div>
          <p>版权所有：石家庄市教育局</p>
        </div>
      </el-form>
    </div>

    <!-- 通知详情弹出框 -->
    <el-dialog
      :title="selectedNotice.title"
      :visible.sync="noticeDialogVisible"
      width="1000px"
      :before-close="handleCloseNoticeDialog"
      class="notice-dialog"
    >
      <div class="notice-content">
        <div class="notice-meta">
          <span class="notice-date"
            >发布时间：{{ formatDate(selectedNotice.createTime) }}</span
          >
        </div>
        <div
          class="notice-body"
          style="height: 600px; overflow-y: auto"
          v-html="selectedNotice.content || selectedNotice.noticeContent"
        ></div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="noticeDialogVisible = false">关 闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getCodeImg, getNoticeList } from "@/api/login";
import Cookies from "js-cookie";
import { encrypt, decrypt } from "@/utils/jsencrypt";

export default {
  name: "Login",
  data() {
    return {
      title: process.env.VUE_APP_TITLE,
      codeUrl: "",
      loginForm: {
        username: "",
        password: "",
        rememberMe: false,
        code: "",
        uuid: "",
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的用户名" },
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" },
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }],
      },
      loading: false,
      // 验证码开关
      captchaEnabled: true,
      // 注册开关
      register: false,
      redirect: undefined,
      noticeList: [],
      noticeDialogVisible: false,
      selectedNotice: {},
      iconList: [
        "el-icon-document",
        "el-icon-tickets",
        "el-icon-news",
        "el-icon-chat-dot-round",
        "el-icon-chat-line-round",
        "el-icon-notification",
      ],
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
  created() {
    this.getCode();
    this.getCookie();
    this.handleGetNoticeList();
  },
  methods: {
    handleGetNoticeList() {
      getNoticeList().then((res) => {
        this.noticeList = res.rows;
      });
    },
    getCode() {
      getCodeImg().then((res) => {
        this.captchaEnabled =
          res.captchaEnabled === undefined ? true : res.captchaEnabled;
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img;
          this.loginForm.uuid = res.uuid;
        }
      });
    },
    getCookie() {
      const username = Cookies.get("username");
      const password = Cookies.get("password");
      const rememberMe = Cookies.get("rememberMe");
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password:
          password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe),
      };
    },
    handleLogin() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          this.loading = true;
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 });
            Cookies.set("password", encrypt(this.loginForm.password), {
              expires: 30,
            });
            Cookies.set("rememberMe", this.loginForm.rememberMe, {
              expires: 30,
            });
          } else {
            Cookies.remove("username");
            Cookies.remove("password");
            Cookies.remove("rememberMe");
          }
          this.$store
            .dispatch("Login", this.loginForm)
            .then(() => {
              this.$store.dispatch("GetInfo").then((res) => {
                this.$router.push({ path: "/" });
              });
            })
            .catch(() => {
              this.loading = false;
              if (this.captchaEnabled) {
                this.getCode();
              }
            });
        }
      });
    },
    showNoticeDetail(notice) {
      this.selectedNotice = notice;
      this.noticeDialogVisible = true;
    },
    handleCloseNoticeDialog() {
      this.noticeDialogVisible = false;
      this.selectedNotice = {};
    },
    formatDate(dateString) {
      if (!dateString) return "";
      const date = new Date(dateString);
      return date.toLocaleString("zh-CN", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
      });
    },
    getRandomIcon(randomIndex) {
      return this.iconList[randomIndex];
    },
  },
};
</script>

<style scoped>
.login {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
  height: 100%;
  background: linear-gradient(125deg, #2563eb 0%, #3b82f6 40%, #60a5fa 100%);
  position: relative;
  overflow: hidden;
  min-height: 100vh;
  /* padding-top: 80px; */
}

.page-title {
  position: relative;
  z-index: 3;
  text-align: center;
  margin-bottom: 60px;
  animation: fadeIn 0.8s ease-out forwards;
}

.page-title h1 {
  font-size: 36px;
  font-weight: bold;
  color: #fff;
  text-shadow: 0 2px 15px rgba(0, 0, 0, 0.2);
  letter-spacing: 3px;
  background: linear-gradient(to right, #bfdbfe, #fff 50%, #f0f9ff);
  background-size: 200% auto;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: gradient-shift 6s infinite alternate;
  position: relative;
  margin: 0;
  padding: 0 20px;
}

.page-title h1::after {
  content: "";
  position: absolute;
  bottom: -8px;
  left: 50%;
  width: 80px;
  height: 3px;
  transform: translateX(-50%);
  background: linear-gradient(to right, transparent, #bfdbfe, transparent);
  opacity: 0.8;
  border-radius: 3px;
  filter: blur(1px);
  box-shadow: 0 0 10px rgba(191, 219, 254, 0.5);
}

.tech-particles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: radial-gradient(
      circle at 30% 20%,
      rgba(147, 197, 253, 0.15) 0%,
      transparent 50%
    ),
    radial-gradient(
      circle at 80% 80%,
      rgba(165, 180, 252, 0.15) 0%,
      transparent 50%
    ),
    radial-gradient(
      circle at 10% 70%,
      rgba(191, 219, 254, 0.12) 0%,
      transparent 40%
    ),
    radial-gradient(
      circle at 60% 30%,
      rgba(129, 140, 248, 0.12) 0%,
      transparent 45%
    );
  z-index: 1;
}

.tech-particles::before {
  content: "";
  position: absolute;
  width: 100%;
  height: 100%;
  background-image: url("data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cGF0dGVybiBpZD0iZ3JpZCIgd2lkdGg9IjEwMCIgaGVpZ2h0PSIxMDAiIHBhdHRlcm5Vbml0cz0idXNlclNwYWNlT25Vc2UiPjxwYXRoIGQ9Ik0gMTAwIDAgTCAwIDAgMCAxMDAiIGZpbGw9Im5vbmUiIHN0cm9rZT0icmdiYSgyNTUsIDI1NSwgMjU1LCAwLjEyKSIgc3Ryb2tlLXdpZHRoPSIxLjUiLz48L3BhdHRlcm4+PHJlY3Qgd2lkdGg9IjEwMCUiIGhlaWdodD0iMTAwJSIgZmlsbD0idXJsKCNncmlkKSIvPjwvc3ZnPg==");
  opacity: 0.8;
  animation: pulse-bg 10s infinite alternate;
}

@keyframes pulse-bg {
  0% {
    opacity: 0.5;
    transform: scale(1);
  }

  100% {
    opacity: 0.7;
    transform: scale(1.05);
  }
}

.tech-particles::after {
  content: "";
  position: absolute;
  width: 100%;
  height: 100%;
  background: url("data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA1NiA4MCI+PHBhdGggZD0iTSAyOCA4MCBMIDAgNDAgTCAyOCAwIEwgNTYgNDAgTCAyOCA4MCIgZmlsbD0ibm9uZSIgc3Ryb2tlPSJyZ2JhKDI1NSwgMjU1LCAyNDUsIDAuMTIpIiBzdHJva2Utd2lkdGg9IjEiLz48L3N2Zz4=");
  opacity: 0.7;
  animation: floatHex 60s infinite linear;
}

@keyframes floatHex {
  from {
    background-position: 0 0;
  }

  to {
    background-position: 100% 100%;
  }
}

.login-container {
  display: flex;
  /* width: 900px; */
  max-width: 95%;
  height: auto;
  min-height: 540px;
  max-height: none;
  position: relative;
  z-index: 2;
  border-radius: 24px;
  overflow: hidden;
  backdrop-filter: blur(10px);
  box-shadow: 0 25px 45px rgba(0, 0, 0, 0.15),
    0 0 0 1px rgba(255, 255, 255, 0.2), 0 0 40px rgba(96, 165, 250, 0.2),
    inset 0 0 0 1px rgba(255, 255, 255, 0.2);
  background: rgba(249, 250, 251, 0.9);
  animation: containerGlow 5s infinite alternate;
  margin-top: 0px;
  top: 50%;
  transform: translateY(-60%);
}

@keyframes containerGlow {
  0% {
    box-shadow: 0 25px 45px rgba(0, 0, 0, 0.15),
      0 0 0 1px rgba(255, 255, 255, 0.2), 0 0 40px rgba(96, 165, 250, 0.2),
      inset 0 0 0 1px rgba(255, 255, 255, 0.2);
  }

  100% {
    box-shadow: 0 25px 45px rgba(0, 0, 0, 0.15),
      0 0 0 1px rgba(255, 255, 255, 0.3), 0 0 50px rgba(59, 130, 246, 0.3),
      inset 0 0 0 1px rgba(255, 255, 255, 0.25);
  }
}

.login-left {
  width: 51%;
  background: linear-gradient(135deg, #2563eb 0%, #3b82f6 100%);
  padding: 30px 25px;
  color: white;
  display: flex;
  align-items: center;
  position: relative;
  overflow: visible;
  border-right: 1px solid rgba(255, 255, 255, 0.2);
}

.login-left::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(
      circle at 50% 100%,
      rgba(255, 255, 255, 0.25) 0%,
      transparent 50%
    ),
    linear-gradient(125deg, rgba(96, 165, 250, 0.3), rgba(129, 140, 248, 0.4));
  animation: gradientBreath 8s infinite alternate;
}

@keyframes gradientBreath {
  0% {
    opacity: 0.8;
    background-position: 0% 0%;
  }

  100% {
    opacity: 1;
    background-position: 100% 100%;
  }
}

.system-info {
  position: relative;
  z-index: 1;
  width: 100%;
}

.system-logo {
  margin: 0 auto 25px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 110px;
  height: 110px;
  border-radius: 50%;
  position: relative;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(5px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1), 0 0 0 1px rgba(255, 255, 255, 0.1),
    inset 0 0 0 1px rgba(255, 255, 255, 0.05);
  animation: float 6s infinite ease-in-out;
}

.system-logo::after {
  content: "";
  position: absolute;
  width: 140%;
  height: 140%;
  background: conic-gradient(
    transparent,
    rgba(96, 165, 250, 0.1),
    rgba(125, 211, 252, 0.2),
    rgba(147, 197, 253, 0.3),
    rgba(165, 180, 252, 0.2),
    rgba(96, 165, 250, 0.1),
    transparent
  );
  border-radius: 50%;
  animation: rotateConic 8s linear infinite;
}

@keyframes rotateConic {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}

.logo-glow {
  position: absolute;
  width: 115%;
  height: 115%;
  border-radius: 50%;
  background: radial-gradient(
    circle at 50% 50%,
    rgba(125, 211, 252, 0.7) 0%,
    rgba(79, 70, 229, 0.4) 40%,
    transparent 70%
  );
  filter: blur(15px);
  opacity: 0.5;
  animation: glow 4s infinite alternate;
}

@keyframes glow {
  0% {
    opacity: 0.3;
    transform: scale(0.95) rotate(0deg);
  }

  100% {
    opacity: 0.6;
    transform: scale(1.05) rotate(10deg);
  }
}

.big-logo {
  font-size: 48px;
  color: #fff;
  filter: drop-shadow(0 0 15px rgba(56, 189, 248, 0.8));
  position: relative;
  z-index: 1;
  animation: pulse 3s infinite alternate;
}

@keyframes pulse {
  0% {
    filter: drop-shadow(0 0 10px rgba(56, 189, 248, 0.5));
    transform: scale(0.98);
  }

  50% {
    filter: drop-shadow(0 0 20px rgba(124, 58, 237, 0.6));
    transform: scale(1);
  }

  100% {
    filter: drop-shadow(0 0 25px rgba(79, 70, 229, 0.8));
    transform: scale(1.02);
  }
}

.system-title {
  font-size: 32px;
  font-weight: bold;
  margin: 0 0 8px;
  text-align: center;
  color: #fff;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  letter-spacing: 3px;
  background: linear-gradient(to right, #bfdbfe, #fff 50%, #f0f9ff);
  background-size: 200% auto;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: gradient-shift 6s infinite alternate;
  position: relative;
}

@keyframes gradient-shift {
  0% {
    background-position: 0% 50%;
  }

  50% {
    background-position: 100% 50%;
  }

  100% {
    background-position: 0% 50%;
  }
}

.system-title::after {
  content: "";
  position: absolute;
  bottom: -5px;
  left: 50%;
  width: 60px;
  height: 2px;
  transform: translateX(-50%);
  background: linear-gradient(to right, transparent, #bfdbfe, transparent);
  opacity: 0.7;
  border-radius: 2px;
  filter: blur(1px);
}

.system-subtitle {
  font-size: 22px;
  margin-bottom: 12px;
  opacity: 1;
  text-align: center;
  font-weight: bold;
  background: linear-gradient(to right, #f0f9ff, #dbeafe);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  letter-spacing: 2px;
  text-shadow: 0 1px 5px rgba(0, 0, 0, 0.1);
}

.tech-line {
  height: 2px;
  background: linear-gradient(
    to right,
    transparent,
    rgba(191, 219, 254, 0.9),
    transparent
  );
  margin: 18px 0;
  position: relative;
  animation: line-glow 3s infinite alternate;
}

@keyframes line-glow {
  0% {
    box-shadow: 0 0 5px rgba(191, 219, 254, 0.4);
  }

  100% {
    box-shadow: 0 0 15px rgba(147, 197, 253, 0.7);
  }
}

.tech-line::before {
  content: "";
  position: absolute;
  width: 8px;
  height: 8px;
  background: rgba(147, 197, 253, 1);
  border-radius: 50%;
  top: -3px;
  left: 50%;
  transform: translateX(-50%);
  box-shadow: 0 0 15px 3px rgba(125, 211, 252, 0.8);
}

.tech-line.small {
  margin: 12px auto 15px;
  width: 80px;
  background: linear-gradient(
    to right,
    transparent,
    rgba(147, 197, 253, 0.5),
    transparent
  );
}

.feature-list {
  min-height: 300px;
  overflow-y: auto;
  margin-top: 20px;
  background: rgba(59, 130, 246, 0.15);
  border-radius: 16px;
  padding: 18px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.05),
    inset 0 0 0 1px rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(5px);
  overflow: visible;
  max-height: none;
}

.feature-item {
  display: flex;
  align-items: center;
  margin-bottom: 14px;
  padding: 8px;
  border-radius: 10px;
  transition: all 0.4s ease;
  animation: fadeIn 0.8s ease-out forwards;
  opacity: 0;
}

.feature-item:nth-child(1) {
  animation-delay: 0.1s;
}

.feature-item:nth-child(2) {
  animation-delay: 0.3s;
}

.feature-item:nth-child(3) {
  animation-delay: 0.5s;
}

.feature-item:nth-child(4) {
  animation-delay: 0.7s;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.feature-item:hover {
  background: rgba(96, 165, 250, 0.2);
  transform: translateX(5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.03), 0 0 0 1px rgba(96, 165, 250, 0.3);
}

.feature-item:last-child {
  margin-bottom: 0;
}

.feature-icon {
  margin-right: 14px;
  font-size: 14px;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(
    135deg,
    rgba(125, 211, 252, 0.4),
    rgba(147, 197, 253, 0.4)
  );
  border-radius: 8px;
  position: relative;
  border: 1px solid rgba(147, 197, 253, 0.5);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.03);
}

.feature-icon i {
  color: #fff;
  font-size: 16px;
  filter: drop-shadow(0 0 4px rgba(56, 189, 248, 0.6));
}

.feature-item span {
  font-size: 14px;
  font-weight: 600;
  background: linear-gradient(to right, #f0f9ff, #e0f2fe, #dbeafe);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  letter-spacing: 0.5px;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
  transition: all 0.3s ease;
}

.feature-item:hover span {
  background: linear-gradient(to right, #fff, #f0f9ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.login-form {
  /* width: 55%; */
  background: rgba(255, 255, 255, 0.95);
  padding: 35px 45px;
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  overflow: visible;
  max-height: none;
}

.login-form::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(
      circle at 20% 20%,
      rgba(96, 165, 250, 0.05) 0%,
      transparent 50%
    ),
    radial-gradient(
      circle at 80% 80%,
      rgba(129, 140, 248, 0.05) 0%,
      transparent 30%
    );
  z-index: -1;
}

.form-header1 {
  text-align: center;
  margin-bottom: 30px;
  animation: fadeIn 0.6s ease-out forwards;
}

.logo-container {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  background: linear-gradient(
    135deg,
    rgba(96, 165, 250, 0.9) 0%,
    rgba(129, 140, 248, 0.9) 100%
  );
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1), 0 0 0 1px rgba(96, 165, 250, 0.3),
    0 0 25px rgba(96, 165, 250, 0.2);
  transform: rotate(45deg);
  overflow: hidden;
}

.logo-container::before {
  content: "";
  position: absolute;
  width: 150%;
  height: 150%;
  background: linear-gradient(
    45deg,
    rgba(56, 189, 248, 0.5) 0%,
    rgba(79, 70, 229, 0.5) 25%,
    rgba(124, 58, 237, 0.5) 50%,
    rgba(79, 70, 229, 0.5) 75%,
    rgba(56, 189, 248, 0.5) 100%
  );
  animation: rotate 8s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}

.logo-ring {
  position: absolute;
  width: 115%;
  height: 115%;
  border: 2px solid rgba(255, 255, 255, 0.2);
  border-radius: 10px;
  animation: spin 12s linear infinite;
}

.logo-icon {
  font-size: 34px;
  color: #fff;
  filter: drop-shadow(0 0 5px rgba(255, 255, 255, 0.5));
  transform: rotate(-45deg);
  position: relative;
  z-index: 2;
}

.title {
  margin: 0 0 8px;
  font-size: 26px;
  font-weight: bold;
  background: #2563eb;
  background-size: 200% 200%;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  position: relative;
  display: inline-block;
  letter-spacing: 1.5px;
  padding-bottom: 8px;
  animation: gradient-title 4s ease infinite;
}

.title::before {
  content: "";
  position: absolute;
  top: -5px;
  left: -10px;
  right: -10px;
  bottom: -5px;
  /* background: radial-gradient(circle, rgba(96, 165, 250, 0.2) 0%, transparent 70%); */
  border-radius: 30px;
  z-index: -1;
  animation: titlePulse 3s infinite alternate;
}

@keyframes titlePulse {
  0% {
    opacity: 0.4;
    transform: scale(0.95);
  }

  100% {
    opacity: 0.8;
    transform: scale(1.05);
  }
}

.subtitle {
  color: #475569;
  font-size: 15px;
  margin: 12px 0 10px;
  font-weight: 500;
}

.el-form-item {
  margin-bottom: 22px;
  position: relative;
  opacity: 0;
  animation: fadeSlideIn 0.8s ease-out forwards;
}

.el-form-item:nth-child(1) {
  animation-delay: 0.3s;
}

.el-form-item:nth-child(2) {
  animation-delay: 0.5s;
}

.el-form-item:nth-child(3) {
  animation-delay: 0.7s;
}

@keyframes fadeSlideIn {
  from {
    opacity: 0;
    transform: translateY(15px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.el-input {
  height: 48px;
}

.el-input input {
  height: 48px;
  background: rgba(249, 250, 251, 0.8) !important;
  border: 1px solid rgba(96, 165, 250, 0.3) !important;
  border-radius: 12px !important;
  color: #2d3748 !important;
  font-size: 15px;
  padding-left: 18px;
  transition: all 0.3s ease;
  box-shadow: inset 0 2px 5px rgba(0, 0, 0, 0.01), 0 0 0 transparent;
}

.el-input input::placeholder {
  color: #a0aec0;
}

.el-input input:hover {
  border-color: rgba(96, 165, 250, 0.5) !important;
  box-shadow: 0 0 0 1px rgba(96, 165, 250, 0.1);
}

.el-input input:focus {
  border-color: rgba(96, 165, 250, 0.7) !important;
  background: rgba(255, 255, 255, 1) !important;
  box-shadow: 0 0 0 1px rgba(96, 165, 250, 0.2),
    0 0 15px rgba(96, 165, 250, 0.15) !important;
  animation: inputPulse 2s infinite alternate;
}

@keyframes inputPulse {
  0% {
    box-shadow: 0 0 0 1px rgba(96, 165, 250, 0.2),
      0 0 15px rgba(96, 165, 250, 0.15);
  }

  100% {
    box-shadow: 0 0 0 1px rgba(96, 165, 250, 0.3),
      0 0 20px rgba(96, 165, 250, 0.25);
  }
}

.input-icon {
  color: #3b82f6;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
  padding: 0 5px;
  opacity: 0;
  animation: fadeIn 0.6s ease-out 0.9s forwards;
}

.login-options .el-checkbox {
  color: #475569;
}

.login-options .el-checkbox__input.is-checked .el-checkbox__inner {
  background-color: #3b82f6;
  border-color: #3b82f6;
}

.login-options .el-checkbox__label {
  color: #475569;
}

.forget-pwd {
  color: #3b82f6;
  transition: all 0.3s ease;
}

.forget-pwd:after {
  background-color: #3b82f6;
}

.forget-pwd:hover {
  color: #2563eb;
  text-decoration: underline;
}

.login-code {
  width: 33%;
  height: 48px;
  float: right;
  position: relative;
  overflow: hidden;
  border-radius: 12px;
  box-shadow: 0 5px 15px rgba(59, 130, 246, 0.1);
}

.login-code::after {
  content: "";
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  background: linear-gradient(
    45deg,
    rgba(96, 165, 250, 0.5) 0%,
    rgba(125, 211, 252, 0.2) 25%,
    rgba(59, 130, 246, 0.3) 50%,
    rgba(99, 102, 241, 0.5) 75%,
    rgba(96, 165, 250, 0.5) 100%
  );
  border-radius: 14px;
  z-index: -1;
  opacity: 0;
  animation: captchaGlow 2s infinite alternate;
}

@keyframes captchaGlow {
  0% {
    opacity: 0;
  }

  100% {
    opacity: 0.5;
  }
}

.login-code img {
  cursor: pointer;
  height: 100%;
  width: 100%;
  object-fit: cover;
  border: 1px solid rgba(96, 165, 250, 0.3);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.login-code img:hover {
  transform: scale(1.03);
  border-color: rgba(96, 165, 250, 0.5);
  box-shadow: 0 5px 15px rgba(59, 130, 246, 0.2);
}

::v-deep .el-button--primary {
  height: 52px;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 1px;
  border-radius: 12px !important;
  background: #3b82f6;
  overflow: hidden !important;
}

.el-button--primary::after {
  content: "";
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(
    circle,
    rgba(255, 255, 255, 0) 60%,
    rgba(147, 197, 253, 0.3) 100%
  );
  opacity: 0;
  transform: scale(0.5);
  animation: buttonRipple 3s infinite;
  z-index: -1;
  pointer-events: none;
}

@keyframes buttonRipple {
  0% {
    opacity: 0;
    transform: scale(0.5);
  }

  30% {
    opacity: 0.4;
  }

  100% {
    opacity: 0;
    transform: scale(1.2);
  }
}

.tech-copyright {
  margin-top: auto;
  text-align: center;
  color: #64748b;
  font-size: 12px;
  letter-spacing: 0.8px;
  padding: 15px 0 5px;
  position: relative;
  opacity: 0;
  animation: fadeIn 0.6s ease-out 1.3s forwards;
}

.floating-dots {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  z-index: 1;
  overflow: hidden;
}

.dot {
  position: absolute;
  width: 6px;
  height: 6px;
  background: rgba(255, 255, 255, 0.4);
  border-radius: 50%;
  filter: blur(1px);
}

.dot:nth-child(1) {
  top: 10%;
  left: 15%;
  background: rgba(186, 230, 253, 0.5);
  animation: floatAnimation 20s infinite ease-in-out;
}

.dot:nth-child(2) {
  top: 25%;
  left: 80%;
  width: 8px;
  height: 8px;
  background: rgba(147, 197, 253, 0.5);
  animation: floatAnimation 15s infinite ease-in-out reverse;
}

.dot:nth-child(3) {
  top: 60%;
  left: 5%;
  width: 10px;
  height: 10px;
  background: rgba(191, 219, 254, 0.5);
  animation: floatAnimation 18s infinite ease-in-out 2s;
}

.dot:nth-child(4) {
  top: 75%;
  left: 85%;
  background: rgba(165, 180, 252, 0.5);
  animation: floatAnimation 22s infinite ease-in-out 1s;
}

.dot:nth-child(5) {
  top: 90%;
  left: 40%;
  width: 7px;
  height: 7px;
  background: rgba(129, 140, 248, 0.5);
  animation: floatAnimation 25s infinite ease-in-out;
}
.dateTime {
  font-size: 16px;
  font-width: normal;
}
.dot:nth-child(6) {
  top: 40%;
  left: 95%;
  width: 5px;
  height: 5px;
  animation: floatAnimation 17s infinite ease-in-out 3s;
}

.dot:nth-child(7) {
  top: 5%;
  left: 50%;
  width: 9px;
  height: 9px;
  animation: floatAnimation 21s infinite ease-in-out 2s;
}

.dot:nth-child(8) {
  top: 35%;
  left: 30%;
  width: 6px;
  height: 6px;
  animation: floatAnimation 19s infinite ease-in-out 4s;
}

.dot:nth-child(9) {
  top: 80%;
  left: 20%;
  width: 8px;
  height: 8px;
  animation: floatAnimation 23s infinite ease-in-out 1s;
}

.dot:nth-child(10) {
  top: 50%;
  left: 65%;
  width: 7px;
  height: 7px;
  animation: floatAnimation 16s infinite ease-in-out 5s;
}

@keyframes floatAnimation {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }

  25% {
    transform: translate(15px, 15px) scale(1.05);
  }

  50% {
    transform: translate(25px, -10px) scale(1.1);
  }

  75% {
    transform: translate(-10px, 15px) scale(1.05);
  }
}

@media (max-width: 768px) {
  .login {
    padding-top: 40px;
  }

  .page-title {
    margin-bottom: 40px;
  }

  .page-title h1 {
    font-size: 28px;
    letter-spacing: 2px;
    padding: 0 15px;
  }

  .login-container {
    min-height: 540px;
    flex-direction: column;
    width: 100%;
    max-width: 100%;
    border-radius: 0;
    margin-top: 0px;
  }

  .login-left {
    width: 100%;
    padding: 20px 15px;
    max-height: none;
    overflow: visible;
  }

  .system-logo {
    width: 80px;
    height: 80px;
    margin-bottom: 12px;
  }

  .big-logo {
    font-size: 38px;
  }

  .system-title {
    font-size: 26px;
  }

  .system-subtitle {
    font-size: 14px;
  }

  .tech-line {
    margin: 10px 0;
  }

  .feature-list {
    margin-top: 12px;
    padding: 12px;
    max-height: none;
    overflow: visible;
  }

  .feature-item {
    margin-bottom: 8px;
    padding: 6px;
  }

  .feature-icon {
    width: 28px;
    height: 28px;
    margin-right: 10px;
  }

  .feature-item span {
    font-size: 13px;
  }

  .login-form {
    width: 100%;
    padding: 20px 15px;
    max-height: none;
    overflow: visible;
  }

  .logo-container {
    width: 70px;
    height: 70px;
    margin-bottom: 15px;
  }

  .logo-icon {
    font-size: 30px;
  }

  .title {
    font-size: 24px;
  }

  .subtitle {
    font-size: 14px;
    margin: 8px 0;
  }

  .el-form-item {
    margin-bottom: 18px;
  }

  .el-input,
  .el-input input {
    height: 45px;
  }

  .el-input input {
    font-size: 14px;
    padding-left: 15px;
  }

  .login-code {
    height: 45px;
  }

  .login-options {
    margin-bottom: 22px;
  }

  .el-button--primary {
    height: 45px;
    font-size: 15px;
    overflow: hidden !important;
  }

  .tech-copyright {
    padding: 10px 0 5px;
  }
}

@keyframes float {
  0% {
    transform: translateY(0px);
  }

  50% {
    transform: translateY(-10px);
  }

  100% {
    transform: translateY(0px);
  }
}

@keyframes gradient-title {
  0% {
    background-position: 0% 50%;
  }

  50% {
    background-position: 100% 50%;
  }

  100% {
    background-position: 0% 50%;
  }
}

.title::after {
  content: "";
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 3px;
  background: linear-gradient(
    to right,
    rgba(59, 130, 246, 0.4),
    rgba(99, 102, 241, 0.4)
  );
  border-radius: 3px;
}

@keyframes buttonPulse {
  0% {
    box-shadow: 0 10px 20px rgba(59, 130, 246, 0.2),
      0 0 0 1px rgba(96, 165, 250, 0.3), 0 0 20px rgba(96, 165, 250, 0.15);
  }

  100% {
    box-shadow: 0 15px 25px rgba(59, 130, 246, 0.25),
      0 0 0 1px rgba(96, 165, 250, 0.4), 0 0 25px rgba(96, 165, 250, 0.25);
  }
}

.el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 15px 30px rgba(59, 130, 246, 0.25),
    0 0 0 1px rgba(96, 165, 250, 0.5), 0 0 30px rgba(96, 165, 250, 0.2) !important;
}

.el-button--primary:hover::before {
  left: 100%;
}

/* 通知详情弹出框样式 */
.notice-dialog .el-dialog {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.notice-dialog .el-dialog__header {
  background: linear-gradient(135deg, #3b82f6 0%, #6366f1 100%);
  color: white;
  padding: 20px 24px;
  border-bottom: none;
}

.notice-dialog .el-dialog__title {
  color: white;
  font-size: 18px;
  font-weight: 600;
  line-height: 1.4;
}

.notice-dialog .el-dialog__headerbtn .el-dialog__close {
  color: rgba(255, 255, 255, 0.8);
  font-size: 20px;
}

.notice-dialog .el-dialog__headerbtn .el-dialog__close:hover {
  color: white;
}

.notice-dialog .el-dialog__body {
  padding: 24px;
  background: #fafafa;
}

.notice-content {
  color: #333;
}

.notice-meta {
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e5e7eb;
}

.notice-date {
  color: #6b7280;
  font-size: 14px;
}

.notice-body {
  line-height: 1.6;
  font-size: 15px;
  color: #374151;
  word-wrap: break-word;
  white-space: pre-wrap;
}

.notice-body p {
  margin-bottom: 12px;
}

.notice-body img {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 8px 0;
}

.notice-dialog .el-dialog__footer {
  background: #f9fafb;
  padding: 16px 24px;
  border-top: 1px solid #e5e7eb;
}

.dialog-footer .el-button {
  border-radius: 8px;
  padding: 10px 24px;
  font-weight: 500;
}
</style>
