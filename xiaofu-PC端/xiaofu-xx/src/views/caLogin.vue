<template>
  <div class="login">
    <div class="login-header">
      <div class="title" @click="goToHome" style="cursor: pointer;">石家庄市校服管理平台</div>
    </div>
    <div class="login-container"> 
      <div>
        <el-form
          ref="caLoginForm"
          :model="caLoginForm"
          class="login-form"
        >
          <div class="form-header1">
            <h3 class="title" style="font-size: 24px;font-weight: bold;text-align: center;">CA证书登录</h3>
          </div>

          <!-- 证书登录按钮 -->
          <el-form-item style="width: 100%; margin-top: 80px;">
            <el-button
              :loading="loading"
              size="medium"
              type="primary"
              style="width: 100%"
              @click.native.prevent="handleCaLogin"
            >
              <span v-if="!loading">UKey登录</span>
              <span v-else>正在登录中...</span>
            </el-button>
          </el-form-item>

          <!-- 提示信息 -->
          <div class="tips">
            <p style="color: #909399; font-size: 14px; text-align: center; margin: 10px 0;">
              <i class="el-icon-info"></i> 
              请确保已安装河北CA数字证书助手并插入USB Key
            </p>
          </div>

          <!-- <el-form-item style="text-align: center; margin-bottom: 0; margin-top: 10px;">
            <router-link class="link-type" to="/login">账号密码登录</router-link>
            <span style="margin: 0 10px; color: #dcdfe6;">|</span>
            <router-link class="link-type" to="/phone-login">手机号登录</router-link>
          </el-form-item>  -->
        </el-form>
      </div> 
    </div>
    <div class="login-footer">
      <div>版权所有: 石家庄市教育局</div>
      <div style="margin-top: 10px;">技术支持: 石家庄开发区亿网科技发展有限公司</div>
    </div>
  </div>
</template>
  
<script>
import { getCaRandom, caLogin } from "@/api/caLogin";
import { setToken, setExpiresIn } from "@/utils/auth";

export default {
  name: "CaLogin",
  data() {
    return {
      title: process.env.VUE_APP_TITLE,
      caLoginForm: {},
      loading: false,
      redirect: undefined,
      middleware: null, // 使用HebcaMiddleware（与demo一致）
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
    this.initHebcaMiddleware();
  },
  methods: {
    // 初始化河北CA证书中间件
    initHebcaMiddleware() {
      try {
        // 加载HebcaMiddleware（与demo保持一致）
        if (typeof HebcaMiddleware === "undefined") {
          this.$message.error("河北CA证书中间件加载失败，请刷新页面重试");
          return;
        }

        // 设置调试模式
        HebcaMiddleware.setDebug(true);

        // 创建Middleware对象
        this.middleware = new HebcaMiddleware.Middleware({
          success: () => {
            console.log("河北CA证书中间件初始化成功");
          },
          fail: (error) => {
            console.error("河北CA证书中间件初始化失败:", error);
            this.$message.error(
              "证书助手初始化失败: " + error.message
            );
          },
        });
      } catch (error) {
        console.error("初始化河北CA证书中间件异常:", error);
        this.$message.error("证书助手加载异常，请联系管理员");
      }
    },

    // CA证书登录
    async handleCaLogin() {
      if (!this.middleware) {
        this.$message.error("证书助手未初始化，请刷新页面重试");
        return;
      }

      this.loading = true;

      try {
        // 1. 获取服务器随机数
        const randomRes = await getCaRandom();
        const randomData = randomRes;
        
        console.log("获取到随机数:", randomData);

        // 2. 使用middleware.sign方法进行签名（与demo一致）
        const signResult = await this.doSign(randomData);
        if (!signResult) {
          this.loading = false;
          return;
        }

        console.log("签名成功");
        console.log("证书:", signResult.cert);
        console.log("签名:", signResult.signature);

        // 3. 提交签名数据到后端验证登录
        const loginData = {
            cert: signResult.cert,      // sign方法返回的证书
            sign: signResult.signature,  // sign方法返回的签名
            random: randomData,
        };

        const loginRes = await caLogin(loginData);
        
        // 4. 登录成功，保存token
        const data = loginRes.data;
        setToken(data.access_token);
        setExpiresIn(data.expires_in);
        
        this.$store.commit("SET_TOKEN", data.access_token);
        this.$store.commit("SET_EXPIRES_IN", data.expires_in);

        // 5. 获取用户信息并跳转
        this.$store.dispatch("GetInfo").then((res) => {
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
      } catch (error) {
        console.error("CA证书登录失败:", error);
        this.loading = false;
        this.$message.error(error.message || "证书登录失败，请重试");
      }
    },

    // 使用middleware.sign方法进行签名（与demo保持一致）
    doSign(data) {
      return new Promise((resolve, reject) => {
        this.middleware.sign({
          data: data,
          success: (result) => {
            // result: { cert: 证书base64, signature: 签名值 }
            resolve(result);
          },
          fail: (error) => {
            console.error("签名失败:", error);
            this.$message.error("签名失败: " + error.message);
            resolve(null);
          },
        });
      });
    },

    goToHome() {
      this.$router.push("/publicIndex");
    },
  },
};
</script>
  
<style scoped lang="scss">
.login-header {
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

.login-container {
  height: calc(100vh - 200px);
  background-image: url("../assets/images/loginbg-1.jpg");
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat; 
  width: 100%;
  position: relative;
  
  .login-form {
    width: 440px; 
    padding: 20px 40px 40px 40px;
    background-color: #fff;
    border-radius: 10px;
    position: absolute;
    top: 12%;
    right: 12%;
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

.link-type {
  color: #409eff;
  text-decoration: none;
  font-size: 14px;
  
  &:hover {
    text-decoration: underline;
  }
}

.tips {
  margin-top: 10px;
  
  p {
    display: flex;
    align-items: center;
    justify-content: center;
    
    i {
      margin-right: 5px;
    }
  }
}
</style>
  