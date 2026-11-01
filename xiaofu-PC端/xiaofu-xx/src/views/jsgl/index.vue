<template>
  <SupplierWithdrawRecordPage
    hero-title="教育局端申请结算列表"
    hero-subtitle="教育局端查看供应商提交的申请结算记录，不执行学校审核动作。"
    detail-dialog-title="教育局端申请结算详情"
  />
</template>

<script>
import SupplierWithdrawRecordPage from '@/views/school/supplierWithdrawRecord/index'

export default {
  name: 'EducationSettlementApplyAdminPage',
  components: {
    SupplierWithdrawRecordPage
  }
}
</script>
