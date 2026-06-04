<template>
  <div class="settle-page">
    <div class="settle-shell">
      <section class="settle-aside">
        <div>
          <div class="brand">石家庄市校服管理平台</div>
          <h1>供应商入驻申请</h1>
          <p>请使用已有账号登录，系统将自动查询您绑定的厂商信息。</p>
        </div>
      </section>

      <main class="settle-main">
        <div class="login-panel">
          <h2 class="login-title">账号登录</h2>
          <el-form ref="loginForm" :model="form" :rules="rules" label-width="80px" class="login-form">
            <el-form-item label="用户名" prop="username">
              <el-input v-model.trim="form.username" placeholder="请输入用户名" maxlength="20" />
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password maxlength="20" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="loading" @click="handleLogin" style="width: 100%">
                登录并查询厂商信息
              </el-button>
            </el-form-item>
          </el-form>

          <div class="login-divider"><span>或</span></div>

          <el-button type="text" icon="el-icon-user" @click="goRegister" style="width: 100%; font-size: 15px;">
            没有账号？注册新账号
          </el-button>
        </div>
      </main>
    </div>
  </div>
</template>

<script>
import { checkBinding } from "@/api/supplierSettle";

export default {
  name: "SupplierSettleLogin",
  props: {
    noticeId: { type: [String, Number], default: null },
  },
  data() {
    return {
      loading: false,
      form: {
        username: "",
        password: "",
      },
      rules: {
        username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
        password: [{ required: true, message: "请输入密码", trigger: "blur" }],
      },
    };
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate((valid) => {
        if (!valid) return;
        this.loading = true;
        checkBinding(this.form)
          .then((res) => {
            const data = res.data;
            if (data && data.canSubmit) {
              const loginUser = data.loginUser || {};
              this.$emit("go-supplier-info", {
                accountInfo: {
                  username: loginUser.userName || this.form.username,
                  password: this.form.password,
                  phone: loginUser.phonenumber || loginUser.phone || "",
                  email: loginUser.email || "",
                },
                supplierData: data.supplier,
              });
            } else {
              this.$message.warning(data.message || "该账号未绑定厂商信息，请先注册");
            }
          })
          .catch(() => {
            this.$message.error("校验失败，请稍后重试");
          })
          .finally(() => {
            this.loading = false;
          });
      });
    },
    goRegister() {
      this.$emit("go-register");
    },
  },
};
</script>

<style lang="scss" scoped>
.settle-page {
  min-height: 100vh;
  background: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.settle-shell {
  display: flex;
  max-width: 1000px;
  width: 100%;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.12);
  overflow: hidden;
}

.settle-aside {
  flex: 0 0 340px;
  background: linear-gradient(135deg, #1e3a5f 0%, #2d5a87 100%);
  color: #fff;
  padding: 50px 36px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;

  .brand {
    font-size: 14px;
    opacity: 0.75;
    margin-bottom: 20px;
  }

  h1 {
    font-size: 26px;
    margin: 0 0 16px;
    font-weight: 600;
  }

  p {
    font-size: 14px;
    line-height: 1.7;
    opacity: 0.85;
  }
}

.settle-main {
  flex: 1;
  padding: 50px 48px;
  display: flex;
  align-items: center;
}

.login-panel {
  width: 100%;
  max-width: 380px;
  margin: 0 auto;
}

.login-title {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 32px;
  text-align: center;
}

.login-form {
  .el-form-item {
    margin-bottom: 22px;
  }
}

.login-divider {
  display: flex;
  align-items: center;
  margin: 24px 0;
  color: #c0c4cc;

  &::before,
  &::after {
    content: "";
    flex: 1;
    height: 1px;
    background: #e4e7ed;
  }

  span {
    padding: 0 16px;
    font-size: 13px;
  }
}
</style>
