<template>
  <div class="settle-page">
    <div class="settle-shell">
      <section class="settle-aside">
        <div>
          <div class="brand">石家庄市校服管理平台</div>
          <h1>供应商入驻申请</h1>
          <p>提交账号与厂商资料后，申请将进入教育局待审流程。审核通过前不会创建可登录账号。</p>
        </div>
        <div class="aside-status">
          <span>当前状态</span>
          <strong>{{ submitted ? "待教育局审核" : "资料填写中" }}</strong>
        </div>
      </section>

      <main class="settle-main">
        <div v-if="submitted" class="success-panel">
          <i class="el-icon-circle-check"></i>
          <h2>申请已提交</h2>
          <p>请等待教育局审核，审核通过后将完成供应商账号注册和资料入库。</p>
        </div>

        <template v-else>
          <el-steps :active="activeStep" finish-status="success" align-center>
            <el-step title="账号信息" />
            <el-step title="供应商信息" />
          </el-steps>

          <el-form
            ref="settleForm"
            :model="form"
            :rules="rules"
            label-width="120px"
            class="settle-form"
          >
            <div v-show="activeStep === 0" class="form-section account-section">
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
                <el-input
                  v-model="form.password"
                  placeholder="请输入密码"
                  show-password
                  maxlength="20"
                />
              </el-form-item>
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input
                  v-model="form.confirmPassword"
                  placeholder="请再次输入密码"
                  show-password
                  maxlength="20"
                />
              </el-form-item>
            </div>

            <div v-show="activeStep === 1" class="form-section">
              <div class="form-grid">
                <el-form-item label="厂商名称" prop="supplierName">
                  <el-input v-model.trim="form.supplierName" placeholder="请输入厂商名称" />
                </el-form-item>
                <el-form-item label="联系电话" prop="supplierPhone">
                  <el-input v-model.trim="form.supplierPhone" placeholder="请输入联系电话" maxlength="11" />
                </el-form-item>
                <el-form-item label="法定代表人" prop="legalRepresentative">
                  <el-input v-model.trim="form.legalRepresentative" placeholder="请输入法定代表人" />
                </el-form-item>
                <el-form-item label="职工人数" prop="staffNumber">
                  <el-input v-model.trim="form.staffNumber" placeholder="请输入职工人数" />
                </el-form-item>
                <el-form-item label="厂商地址" prop="supplierAddress">
                  <el-input v-model.trim="form.supplierAddress" placeholder="请输入厂商地址" />
                </el-form-item>
                <el-form-item label="注册资金" prop="registeredCapital">
                  <el-input v-model.trim="form.registeredCapital" placeholder="请输入注册资金，单位（万元）" />
                </el-form-item>
                <el-form-item label="成立日期" prop="establishedTime">
                  <el-date-picker
                    v-model="form.establishedTime"
                    type="date"
                    value-format="yyyy-MM-dd"
                    placeholder="请选择成立日期"
                  />
                </el-form-item>
                <el-form-item label="企业类型" prop="supplierType">
                  <el-input v-model.trim="form.supplierType" placeholder="请输入企业类型" />
                </el-form-item>
              </div>

              <el-form-item label="厂商介绍" prop="supplierIntroduce" class="editor-item">
                <Editor v-model="form.supplierIntroduce" :min-height="220" type="base64" />
              </el-form-item>

              <el-form-item label="厂商资质" prop="businessLicense">
                <FileUpload
                  v-model="form.businessLicense"
                  :limit="1"
                  :file-size="20"
                  :file-type="uploadFileTypes"
                  :is-token="false"
                  upload-url="/file/settle/upload"
                />
              </el-form-item>
              <el-form-item label="信用评级" prop="xypj">
                <FileUpload
                  v-model="form.xypj"
                  :limit="1"
                  :file-size="20"
                  :file-type="uploadFileTypes"
                  :is-token="false"
                  upload-url="/file/settle/upload"
                />
              </el-form-item>
            </div>
          </el-form>

          <div class="settle-actions">
            <el-button v-if="activeStep === 1" @click="activeStep = 0">上一步</el-button>
            <el-button v-if="activeStep === 0" type="primary" @click="nextStep">下一步</el-button>
            <el-button
              v-if="activeStep === 1"
              type="primary"
              :loading="submitting"
              @click="submitApply"
            >
              提交申请
            </el-button>
          </div>
        </template>
      </main>
    </div>
  </div>
</template>

<script>
import Editor from "@/components/Editor";
import FileUpload from "@/components/FileUpload";
import { submitSupplierSettleApply } from "@/api/supplierSettle";

const phonePattern = /^1[3-9]\d{9}$/;
const invalidPasswordPattern = /[<>"'|\\]/;

export default {
  name: "SupplierSettle",
  components: { Editor, FileUpload },
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
        callback(new Error("密码不能包含 < > \" ' | \\ 等字符"));
      } else {
        callback();
      }
    };

    return {
      activeStep: 0,
      submitted: false,
      submitting: false,
      uploadFileTypes: ["jpg", "png", "jpeg", "pdf"],
      form: {
        phone: "",
        email: "",
        username: "",
        password: "",
        confirmPassword: "",
        supplierName: "",
        supplierPhone: "",
        legalRepresentative: "",
        staffNumber: "",
        supplierAddress: "",
        registeredCapital: "",
        establishedTime: "",
        supplierType: "",
        supplierIntroduce: "",
        businessLicense: "",
        xypj: "",
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
        supplierName: [{ required: true, message: "请输入厂商名称", trigger: "blur" }],
        supplierPhone: [{ required: true, validator: validatePhone, trigger: "blur" }],
        legalRepresentative: [{ required: true, message: "请输入法定代表人", trigger: "blur" }],
        staffNumber: [{ required: true, message: "请输入职工人数", trigger: "blur" }],
        supplierAddress: [{ required: true, message: "请输入厂商地址", trigger: "blur" }],
        registeredCapital: [{ required: true, message: "请输入注册资金", trigger: "blur" }],
        establishedTime: [{ required: true, message: "请选择成立日期", trigger: "change" }],
        supplierType: [{ required: true, message: "请输入企业类型", trigger: "blur" }],
        supplierIntroduce: [{ required: true, message: "请输入厂商介绍", trigger: "blur" }],
        businessLicense: [{ required: true, message: "请上传厂商资质", trigger: "change" }],
      },
    };
  },
  watch: {
    "form.phone"(value) {
      if (!this.form.supplierPhone) {
        this.form.supplierPhone = value;
      }
    },
  },
  methods: {
    validateFields(fields) {
      return new Promise((resolve) => {
        let valid = true;
        let count = 0;
        fields.forEach((field) => {
          this.$refs.settleForm.validateField(field, (message) => {
            if (message) {
              valid = false;
            }
            count++;
            if (count === fields.length) {
              resolve(valid);
            }
          });
        });
      });
    },
    async nextStep() {
      const valid = await this.validateFields(["phone", "email", "username", "password", "confirmPassword"]);
      if (valid) {
        this.activeStep = 1;
      }
    },
    submitApply() {
      if (this.submitting) {
        return;
      }
      this.$refs.settleForm.validate((valid) => {
        if (!valid) {
          return;
        }
        this.submitting = true;
        const payload = { ...this.form };
        delete payload.confirmPassword;
        submitSupplierSettleApply(payload)
          .then(() => {
            this.submitted = true;
            this.$message.success("申请已提交，请等待教育局审核");
          })
          .finally(() => {
            this.submitting = false;
          });
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.settle-page {
  min-height: 100vh;
  padding: 42px;
  background:
    linear-gradient(120deg, rgba(8, 31, 62, 0.84), rgba(33, 96, 105, 0.72)),
    url("~@/assets/images/bg1.jpg") center/cover no-repeat;
  color: #1f2d3d;
}

.settle-shell {
  display: grid;
  grid-template-columns: minmax(260px, 360px) minmax(0, 1fr);
  max-width: 1280px;
  min-height: calc(100vh - 84px);
  margin: 0 auto;
  background: rgba(255, 255, 255, 0.96);
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 18px 60px rgba(8, 20, 40, 0.28);
}

.settle-aside {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 42px 36px;
  background: linear-gradient(160deg, #12385f 0%, #195c68 58%, #28734f 100%);
  color: #fff;

  .brand {
    margin-bottom: 60px;
    font-size: 16px;
    font-weight: 600;
    opacity: 0.9;
  }

  h1 {
    margin: 0 0 20px;
    font-size: 34px;
    line-height: 1.25;
    font-weight: 700;
    letter-spacing: 0;
  }

  p {
    margin: 0;
    color: rgba(255, 255, 255, 0.78);
    font-size: 15px;
    line-height: 1.8;
  }
}

.aside-status {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 18px;
  padding-top: 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);

  span {
    color: rgba(255, 255, 255, 0.72);
  }

  strong {
    font-size: 16px;
  }
}

.settle-main {
  padding: 42px 44px 32px;
  overflow-y: auto;
}

.settle-form {
  margin-top: 34px;
}

.form-section {
  max-width: 1060px;
}

.account-section {
  max-width: 560px;
  margin: 42px auto 0;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(260px, 1fr));
  column-gap: 28px;
}

.editor-item ::v-deep .editor {
  line-height: normal;
}

.settle-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 26px;
}

.success-panel {
  display: flex;
  min-height: 520px;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;

  i {
    font-size: 76px;
    color: #21a67a;
  }

  h2 {
    margin: 24px 0 12px;
    font-size: 28px;
    color: #1f2d3d;
    letter-spacing: 0;
  }

  p {
    margin: 0;
    color: #607086;
    font-size: 15px;
  }
}

::v-deep .el-date-editor.el-input {
  width: 100%;
}

@media (max-width: 960px) {
  .settle-page {
    padding: 18px;
  }

  .settle-shell {
    grid-template-columns: 1fr;
  }

  .settle-aside {
    padding: 30px 24px;

    .brand {
      margin-bottom: 32px;
    }
  }

  .settle-main {
    padding: 30px 22px;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
