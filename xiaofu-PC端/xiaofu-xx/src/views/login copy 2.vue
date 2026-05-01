<template>
  <div class="login">
    <div class="login-header">
      <div class="title">石家庄市校服管理平台</div>
    </div>
    <div class="login-container"> 
      <div>
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
        </el-form>
      </div> 
    </div>
    <div class="login-footer">
      <div>版权所有: 石家庄市教育局</div>
      <div>技术支持: 石家庄开发区亿网科技发展有限公司</div>
    </div>
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

<style scoped lang="scss">
.login-header {
  height: 100px;
  background-color: #fff;
  color: #8cdce0;
  font-size: 30px;
  font-weight: bold;
  box-sizing: border-box;
  padding: 20px; 
}
.login-container {
  height: calc(100vh - 200px);
  background-image: url("../assets/images/loginbg-1.jpg");
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat; 
  width: 100%;
  position: relative;
  .login-form {
    width: 400px; 
    padding: 20px;
    background-color: #fff;
    border-radius: 10px;
    position: absolute;
    top: 12%;
    right: 12%;
    .login-code {
      width: 33%;
      height: 40px;
      float: right;
      position: relative;
      overflow: hidden; 
    }
    .login-code img {
      cursor: pointer;
      height: 100%;
      width: 100%;
      object-fit: cover;  
      transition: all 0.3s ease;
    }
  }
}
.login-footer {
  background-color: #fff;
  color: #909399;
  font-size: 16px; 
  text-align: center;
  height: 100px;
  box-sizing: border-box;
  padding-top: 30px;
}
</style>
