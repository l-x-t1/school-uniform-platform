<template>
  <div class="settle-page">
    <div class="settle-shell">
      <section class="settle-aside">
        <div>
          <div class="brand">石家庄市校服管理平台</div>
          <h1>供应商入驻申请</h1>
          <p v-if="isBound">系统已自动填充您绑定的厂商信息，请确认或修改后提交。</p>
          <p v-else>第二步：填写厂商信息并提交申请。</p>
        </div>
        <div class="aside-status">
          <span>步骤</span>
          <strong>{{ isBound ? "绑定厂商确认" : "2 / 2 — 厂商信息" }}</strong>
        </div>
      </section>

      <main class="settle-main">
        <div v-if="submitted" class="success-panel">
          <i class="el-icon-circle-check"></i>
          <h2>申请已提交</h2>
          <p>请等待教育局审核，审核通过后将完成供应商账号注册和资料入库。</p>
          <el-alert
            v-if="redirect"
            title="当前报名上下文已为您保留。待入驻审核通过后，可返回原采购公告继续报名。"
            type="info"
            :closable="false"
            show-icon
            style="margin-top: 20px"
          />
          <div v-if="redirect" class="success-actions">
            <el-button @click="openRedirectPage">查看原报名页</el-button>
          </div>
        </div>

        <div v-else class="info-panel">
          <h2 class="info-title">{{ isBound ? "确认厂商信息" : "填写厂商信息" }}</h2>
          <el-alert
            v-if="redirect"
            title="当前采购公告上下文已保留。入驻审核通过后，可回到原报名页继续提交材料。"
            type="info"
            :closable="false"
            show-icon
            style="margin-bottom: 20px"
          />
          <el-form ref="settleForm" :model="form" :rules="rules" label-width="120px" class="info-form">
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
                  style="width: 100%"
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
          </el-form>

          <div class="info-actions">
            <el-button @click="goBack">上一步</el-button>
            <el-button type="primary" :loading="submitting" @click="submitApply">
              提交申请
            </el-button>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script>
import Editor from "@/components/Editor";
import FileUpload from "@/components/FileUpload";
import { submitLoginBoundSupplierSettleApply, submitSupplierSettleApply } from "@/api/supplierSettle";

const phonePattern = /^1[3-9]\d{9}$/;

export default {
  name: "SupplierSettleInfo",
  components: { Editor, FileUpload },
  props: {
    noticeId: { type: [String, Number], default: null },
    redirect: { type: String, default: "" },
    accountInfo: { type: Object, default: null },
    supplierData: { type: Object, default: null },
  },
  data() {
    const validatePhone = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入联系电话"));
      } else if (!phonePattern.test(value)) {
        callback(new Error("手机号格式不正确"));
      } else {
        callback();
      }
    };

    return {
      submitting: false,
      submitted: false,
      isBound: false,
      uploadFileTypes: ["jpg", "png", "jpeg", "pdf"],
      form: {
        phone: "",
        email: "",
        username: "",
        password: "",
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
  created() {
    if (this.supplierData) {
      this.fillFromSupplier(this.supplierData);
      this.isBound = true;
    }
    if (this.accountInfo) {
      const account = this.accountInfo;
      this.form.phone = account.phone || "";
      this.form.email = account.email || "";
      this.form.username = account.username || "";
      this.form.password = account.password || "";
      this.form.supplierPhone = account.phone || "";
    }
  },
  methods: {
    fillFromSupplier(supplier) {
      if (!supplier) return;
      this.form.supplierName = supplier.supplierName || "";
      this.form.supplierPhone = supplier.supplierPhone || "";
      this.form.legalRepresentative = supplier.legalRepresentative || "";
      this.form.staffNumber = supplier.staffNumber || "";
      this.form.supplierAddress = supplier.supplierAddress || "";
      this.form.registeredCapital = supplier.registeredCapital || "";
      this.form.establishedTime = supplier.establishedTime || "";
      this.form.supplierType = supplier.supplierType || "";
      this.form.supplierIntroduce = supplier.supplierIntroduce || "";
      this.form.businessLicense = supplier.businessLicense || "";
      this.form.xypj = supplier.xypj || "";
    },
    goBack() {
      this.$emit("go-back");
    },
    openRedirectPage() {
      if (!this.redirect) {
        return;
      }
      const routeData = this.$router.resolve(this.redirect);
      window.open(routeData.href, "_blank");
    },
    submitApply() {
      if (this.submitting) return;
      this.$refs.settleForm.validate((valid) => {
        if (!valid) return;
        this.submitting = true;
        const payload = { ...this.form };
        delete payload.confirmPassword;
        payload.noticeId = this.noticeId;
        const submitAction = this.isBound
          ? submitLoginBoundSupplierSettleApply
          : submitSupplierSettleApply;
        submitAction(payload)
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
  background: #f0f2f5;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 40px 20px;
}

.settle-shell {
  display: flex;
  max-width: 1200px;
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

.info-panel {
  width: 100%;
}

.info-title {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 28px;
}

.info-form {
  .form-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 0 24px;
  }

  .editor-item {
    margin-top: 8px;
  }
}

.info-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #ebeef5;
}

.success-panel {
  text-align: center;
  padding: 60px 20px;

  .el-icon-circle-check {
    font-size: 64px;
    color: #67c23a;
    margin-bottom: 20px;
  }

  h2 {
    font-size: 22px;
    color: #303133;
    margin: 0 0 12px;
  }

  p {
    color: #909399;
    font-size: 14px;
  }
}

.success-actions {
  margin-top: 20px;
}

@media (max-width: 900px) {
  .settle-shell {
    flex-direction: column;
  }
  .settle-aside {
    flex: 0 0 auto;
    padding: 30px 28px;
  }
  .settle-main {
    padding: 24px;
  }
  .info-form .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
