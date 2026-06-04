<template>
  <div class="settle-page">
    <div class="settle-shell">
      <section class="settle-aside">
        <div>
          <div class="brand">石家庄市校服管理平台</div>
          <h1>供应商入驻申请</h1>
          <p>第一步：注册账号。完成后进入第二步填写厂商信息。</p>
        </div>
        <div class="aside-status">
          <span>步骤</span>
          <strong>1 / 2 — 账号注册</strong>
        </div>
      </section>

      <main class="settle-main">
        <div class="register-panel">
          <h2 class="register-title">账号注册</h2>
          <el-form ref="registerForm" :model="form" :rules="rules" label-width="100px" class="register-form">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model.trim="form.phone" placeholder="请输入手机号" maxlength="11" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model.trim="form.email" placeholder="请输入邮箱" maxlength="128" />
            </el-form-item>
            <el-form-item label="用户名" prop="username">
              <el-input v-model.trim="form.username" placeholder="请输入用户名" maxlength="20" />
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password maxlength="20" />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" show-password maxlength="20" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="medium" @click="nextStep" style="width: 100%">
                下一步 — 填写厂商信息
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </main>
    </div>
  </div>
</template>

<script>
const phonePattern = /^1[3-9]\d{9}$/;
const invalidPasswordPattern = /[<>"'|\\]/;

export default {
  name: "SupplierSettleRegister",
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请再次输入密码"));
      } else if (value !== this.form.password) {
        callback(new Error("两次输入的密码不一致"));
      } else {
        callback();
      }
    };
    const validatePhone = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入手机号"));
      } else if (!phonePattern.test(value)) {
        callback(new Error("手机号格式不正确"));
      } else {
        callback();
      }
    };
    const validatePassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入密码"));
      } else if (value.length < 5 || value.length > 20) {
        callback(new Error("密码长度应为 5 到 20 位"));
      } else if (invalidPasswordPattern.test(value)) {
        callback(new Error('密码不能包含 < > " \' | \\ 等字符'));
      } else {
        callback();
      }
    };

    return {
      noticeId: null,
      form: {
        phone: "",
        email: "",
        username: "",
        password: "",
        confirmPassword: "",
      },
      rules: {
        phone: [{ required: true, validator: validatePhone, trigger: "blur" }],
        email: [
          { required: true, message: "请输入邮箱", trigger: "blur" },
          { type: "email", message: "邮箱格式不正确", trigger: "blur" },
        ],
        username: [
          { required: true, message: "请输入用户名", trigger: "blur" },
          { min: 2, max: 20, message: "用户名长度应为 2 到 20 位", trigger: "blur" },
        ],
        password: [{ required: true, validator: validatePassword, trigger: "blur" }],
        confirmPassword: [{ required: true, validator: validateConfirmPassword, trigger: "blur" }],
      },
    };
  },
  props: {
    noticeId: { type: [String, Number], default: null },
  },
  methods: {
    nextStep() {
      this.$refs.registerForm.validate((valid) => {
        if (!valid) return;
        const accountInfo = {
          phone: this.form.phone,
          email: this.form.email,
          username: this.form.username,
          password: this.form.password,
        };
        this.$emit("go-supplier-info", {
          accountInfo: accountInfo,
        });
      });
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
  max-width: 1100px;
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

  .aside-status {
    margin-top: 30px;
    span {
      display: block;
      font-size: 12px;
      opacity: 0.65;
      margin-bottom: 4px;
    }
    strong {
      font-size: 15px;
    }
  }
}

.settle-main {
  flex: 1;
  padding: 40px 48px;
  overflow-y: auto;
}

.register-panel {
  width: 100%;
  max-width: 460px;
  margin: 0 auto;
}

.register-title {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 28px;
}

.register-form {
  .el-form-item {
    margin-bottom: 20px;
  }
}
</style>
