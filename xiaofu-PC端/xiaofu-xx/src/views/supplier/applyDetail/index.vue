<template>
  <div v-loading="loading" class="app-container apply-detail-page">
    <div class="page-card header-card">
      <div class="header-main">
        <div>
          <div class="page-title">{{ notice.noticeTitle || '采购公告报名' }}</div>
          <div class="page-subtitle">
            <span>公告编号：{{ notice.noticeCode || '--' }}</span>
            <span>学校：{{ notice.schoolName || '--' }}</span>
            <span>截止时间：{{ formatDate(notice.bmjzTime) }}</span>
          </div>
        </div>
        <el-tag :type="getApplyStatusTag(statusText)" size="medium">{{ statusText }}</el-tag>
      </div>
      <el-alert
        v-if="expired"
        title="当前采购公告已截止报名，仅可查看历史材料。"
        type="warning"
        :closable="false"
        show-icon
        style="margin-top: 16px"
      />
    </div>

    <div class="page-card">
      <div class="section-title">公告资料</div>
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="field-label">采购公告文件</div>
          <file-upload :value="notice.noticeFile" disabled />
        </el-col>
        <el-col :span="12">
          <div class="field-label">补充附件</div>
          <file-upload :value="notice.otherFile" disabled />
        </el-col>
      </el-row>
      <el-row v-if="notice.samplesPic" :gutter="20" style="margin-top: 12px">
        <el-col :span="24">
          <div class="field-label">公告样衣参考</div>
          <image-upload :value="notice.samplesPic" :limit="6" disabled />
        </el-col>
      </el-row>
    </div>

    <div class="page-card">
      <div class="section-title">供应商信息</div>
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="info-box">
            <div class="info-label">供应商名称</div>
            <div class="info-value">{{ supplier.supplierName || '--' }}</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="info-box">
            <div class="info-label">联系电话</div>
            <div class="info-value">{{ supplier.supplierPhone || '--' }}</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="info-box">
            <div class="info-label">法人代表</div>
            <div class="info-value">{{ supplier.legalRepresentative || '--' }}</div>
          </div>
        </el-col>
      </el-row>
      <div style="margin-top: 16px">
        <div class="field-label">营业执照</div>
        <file-upload :value="form.businessLicense" disabled />
        <el-alert
          v-if="!form.businessLicense"
          title="当前供应商档案中缺少营业执照，请先完善入驻信息后再提交报名。"
          type="error"
          :closable="false"
          show-icon
          style="margin-top: 12px"
        />
      </div>
    </div>

    <div class="page-card">
      <div class="section-title">报名材料</div>
      <el-form ref="applyForm" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="生产许可" prop="productionLicenseUrls">
          <file-upload v-model="form.productionLicenseUrls" :limit="3" :disabled="expired" />
        </el-form-item>
        <el-form-item label="质检报告" prop="qualityReportUrls">
          <file-upload v-model="form.qualityReportUrls" :limit="6" :disabled="expired" />
        </el-form-item>
        <el-form-item v-if="sampleRequired" label="样衣图" prop="sampleImageUrls">
          <image-upload v-model="form.sampleImageUrls" :limit="6" :disabled="expired" />
        </el-form-item>
        <el-form-item label="补充附件" prop="attachmentUrls">
          <file-upload v-model="form.attachmentUrls" :limit="10" :disabled="expired" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="4"
            maxlength="300"
            show-word-limit
            placeholder="可填写材料说明、补充信息等"
            :disabled="expired"
          />
        </el-form-item>
      </el-form>
    </div>

    <div v-if="logs.length" class="page-card">
      <div class="section-title">报名记录</div>
      <el-timeline>
        <el-timeline-item
          v-for="item in logs"
          :key="item.id"
          :timestamp="formatDateTime(item.actionTime)"
          placement="top"
        >
          <div class="timeline-title">{{ actionText(item.actionType) }}</div>
          <div class="timeline-meta">版本 {{ item.versionNo }} / 操作人：{{ item.actionUser || '--' }}</div>
          <div class="timeline-remark">{{ item.actionRemark || '--' }}</div>
        </el-timeline-item>
      </el-timeline>
    </div>

    <div class="footer-actions">
      <el-button @click="goBack">返回</el-button>
      <el-button v-if="permissions.canSaveDraft" type="info" :loading="savingDraft" @click="handleSaveDraft">保存草稿</el-button>
      <el-button v-if="permissions.canWithdraw" type="warning" :loading="withdrawing" @click="handleWithdraw">撤回报名</el-button>
      <el-button v-if="permissions.canSubmit" type="primary" :loading="submitting" @click="handleSubmit">
        {{ statusText === '待审核' ? '更新提交' : '提交报名' }}
      </el-button>
      <el-button v-if="!supplier.supplierId" type="primary" @click="goSupplierSettle">先完成供应商入驻</el-button>
    </div>
  </div>
</template>

<script>
import { getApplyContext, saveApplyDraft, submitApply, withdrawApply } from '@/api/supplier/apply'
import { getApplyStatusTag } from '@/utils/supplierApply'

export default {
  name: 'SupplierApplyDetail',
  data() {
    const requireIfVisible = (fieldLabel) => {
      return (rule, value, callback) => {
        if (this.expired) {
          callback()
          return
        }
        if (!value) {
          callback(new Error(`请上传${fieldLabel}`))
          return
        }
        callback()
      }
    }
    return {
      loading: false,
      submitting: false,
      savingDraft: false,
      withdrawing: false,
      notice: {},
      supplier: {},
      currentApply: null,
      logs: [],
      statusText: '未报名',
      expired: false,
      permissions: {
        canSaveDraft: false,
        canSubmit: false,
        canWithdraw: false
      },
      form: {
        businessLicense: '',
        productionLicenseUrls: '',
        qualityReportUrls: '',
        sampleImageUrls: '',
        attachmentUrls: '',
        remark: ''
      },
      rules: {
        productionLicenseUrls: [{ validator: requireIfVisible('生产许可'), trigger: 'change' }],
        qualityReportUrls: [{ validator: requireIfVisible('质检报告'), trigger: 'change' }],
        sampleImageUrls: [{ validator: requireIfVisible('样衣图'), trigger: 'change' }]
      }
    }
  },
  computed: {
    noticeId() {
      return this.$route.query.noticeId
    },
    sampleRequired() {
      return String(this.notice.isSamplesPic) === '1'
    }
  },
  watch: {
    noticeId: {
      immediate: true,
      handler() {
        if (this.noticeId) {
          this.loadContext()
        }
      }
    }
  },
  methods: {
    getApplyStatusTag,
    loadContext() {
      this.loading = true
      getApplyContext(this.noticeId).then((response) => {
        const data = response.data || {}
        this.notice = data.notice || {}
        this.supplier = data.supplier || {}
        this.currentApply = data.apply || null
        this.logs = data.logs || []
        this.statusText = data.statusText || '未报名'
        this.expired = Boolean(data.expired)
        this.permissions = Object.assign({
          canSaveDraft: false,
          canSubmit: false,
          canWithdraw: false
        }, data.permissions || {})
        this.form = Object.assign({
          businessLicense: '',
          productionLicenseUrls: '',
          qualityReportUrls: '',
          sampleImageUrls: '',
          attachmentUrls: '',
          remark: ''
        }, data.form || {})
      }).catch(() => {
        this.notice = {}
      }).finally(() => {
        this.loading = false
      })
    },
    buildPayload() {
      return {
        noticeId: this.noticeId,
        productionLicenseUrls: this.form.productionLicenseUrls,
        qualityReportUrls: this.form.qualityReportUrls,
        sampleImageUrls: this.form.sampleImageUrls,
        attachmentUrls: this.form.attachmentUrls,
        remark: this.form.remark
      }
    },
    handleSaveDraft() {
      if (this.savingDraft) {
        return
      }
      if (this.$refs.applyForm) {
        this.$refs.applyForm.clearValidate()
      }
      this.savingDraft = true
      saveApplyDraft(this.buildPayload()).then(() => {
        this.$modal.msgSuccess('草稿已保存')
        this.loadContext()
      }).finally(() => {
        this.savingDraft = false
      })
    },
    handleSubmit() {
      if (!this.form.businessLicense) {
        this.$modal.msgError('请先完善供应商营业执照后再报名')
        return
      }
      this.$refs.applyForm.validate((valid) => {
        if (!valid) {
          return
        }
        this.submitting = true
        submitApply(this.buildPayload()).then(() => {
          this.$modal.msgSuccess('报名提交成功')
          this.loadContext()
        }).finally(() => {
          this.submitting = false
        })
      })
    },
    handleWithdraw() {
      this.$modal.confirm('确认撤回当前报名吗？撤回后截止前仍可再次提交。').then(() => {
        this.withdrawing = true
        return withdrawApply(this.noticeId)
      }).then(() => {
        this.$modal.msgSuccess('已撤回报名')
        this.loadContext()
      }).catch(() => {}).finally(() => {
        this.withdrawing = false
      })
    },
    goSupplierSettle() {
      const redirect = `/supplier/apply/detail?noticeId=${this.noticeId}`
      const routeData = this.$router.resolve({
        path: '/supplier-settle',
        query: { step: 'login', noticeId: this.noticeId, redirect }
      })
      window.open(routeData.href, '_blank')
    },
    goBack() {
      if (window.history.length > 1) {
        this.$router.back()
        return
      }
      this.$router.push('/index')
    },
    actionText(actionType) {
      const map = {
        SAVE_DRAFT: '保存草稿',
        SUBMIT: '提交报名',
        WITHDRAW: '撤回报名',
        AUDIT_PASS: '审核通过',
        AUDIT_REJECT: '审核驳回'
      }
      return map[actionType] || actionType || '--'
    },
    formatDate(value) {
      return value ? this.parseTime(value, '{y}-{m}-{d}') : '--'
    },
    formatDateTime(value) {
      return value ? this.parseTime(value, '{y}-{m}-{d} {h}:{i}:{s}') : '--'
    }
  }
}
</script>

<style scoped lang="scss">
.apply-detail-page {
  padding-bottom: 90px;
}

.page-card {
  padding: 20px 22px;
  margin-bottom: 18px;
  background: #fff;
  border: 1px solid #e8eef5;
  border-radius: 14px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.05);
}

.header-main {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: #23324a;
}

.page-subtitle {
  margin-top: 10px;
  font-size: 13px;
  color: #7b8899;
}

.page-subtitle span + span {
  margin-left: 18px;
}

.section-title {
  margin-bottom: 16px;
  font-size: 16px;
  font-weight: 600;
  color: #23324a;
}

.field-label {
  margin-bottom: 10px;
  font-size: 13px;
  color: #6c7a8d;
}

.info-box {
  padding: 14px 16px;
  background: #f8fafc;
  border: 1px solid #e6edf5;
  border-radius: 10px;
}

.info-label {
  margin-bottom: 6px;
  font-size: 12px;
  color: #7b8899;
}

.info-value {
  font-size: 14px;
  color: #23324a;
}

.timeline-title {
  font-size: 14px;
  font-weight: 600;
  color: #23324a;
}

.timeline-meta {
  margin-top: 4px;
  font-size: 12px;
  color: #7b8899;
}

.timeline-remark {
  margin-top: 8px;
  color: #4b5a6d;
  line-height: 1.7;
}

.footer-actions {
  position: fixed;
  right: 24px;
  bottom: 20px;
  z-index: 10;
  display: flex;
  gap: 10px;
  padding: 14px 16px;
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid #e8eef5;
  border-radius: 999px;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.12);
}

@media (max-width: 768px) {
  .header-main {
    flex-direction: column;
  }

  .page-subtitle span {
    display: block;
    margin: 6px 0 0;
  }

  .footer-actions {
    left: 12px;
    right: 12px;
    bottom: 12px;
    justify-content: flex-end;
    padding: 12px;
    border-radius: 14px;
  }
}
</style>
