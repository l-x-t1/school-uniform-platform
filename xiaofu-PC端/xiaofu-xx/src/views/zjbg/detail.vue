<template>
  <div class="app-container">
    <div class="toolbar">
      <el-button icon="el-icon-back" size="small" @click="goBack">返回</el-button>
    </div>

    <el-card v-loading="loading" shadow="never">
      <div slot="header" class="card-title">质检报告详情</div>
      <el-row :gutter="24" class="detail-grid">
        <el-col :span="8"><span class="label">公告名称：</span>{{ detail.noticeName || detail.ggTitle || '-' }}</el-col>
        <el-col :span="8"><span class="label">公告状态：</span>{{ detail.noticeStatusText || noticeStatusText(detail.noticeStatus) }}</el-col>
        <el-col :span="8"><span class="label">学校名称：</span>{{ detail.deptName || '-' }}</el-col>
        <el-col :span="8"><span class="label">厂商名称：</span>{{ detail.manufacturerName || detail.supplierName || '-' }}</el-col>
        <el-col :span="8"><span class="label">联系电话：</span>{{ detail.supplierPhone || '-' }}</el-col>
        <el-col :span="8"><span class="label">法人代表：</span>{{ detail.legalRepresentative || '-' }}</el-col>
        <el-col :span="8"><span class="label">首次预检：</span>{{ inspectionText(detail.firstInspection) }}</el-col>
        <el-col :span="8"><span class="label">二次预检：</span>{{ inspectionText(detail.secondInspection) }}</el-col>
        <el-col :span="8"><span class="label">创建时间：</span>{{ detail.createTime || '-' }}</el-col>
      </el-row>
    </el-card>

    <el-card class="section-card" shadow="never">
      <div slot="header" class="card-title">报告附件</div>
      <el-table :data="reportFiles" style="width: 100%">
        <el-table-column label="报告标题" prop="inspectionReportTitle" min-width="180" />
        <el-table-column label="文件名称" prop="fileName" min-width="180" />
        <el-table-column label="报告类型" prop="type" width="120">
          <template slot-scope="scope">{{ reportTypeText(scope.row.type) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template slot-scope="scope">
            <el-button v-if="scope.row.url" type="text" size="mini" @click="openFile(scope.row.url)">查看文件</el-button>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { getSuppliceInspectionReport, getReportList } from '@/api/school/report'

export default {
  name: 'ZjbgDetail',
  data() {
    return {
      loading: false,
      detail: {},
      reportFiles: []
    }
  },
  created() {
    this.getDetail()
  },
  methods: {
    getDetail() {
      const id = this.$route.query.id
      if (!id) {
        this.$message.error('缺少质检报告ID')
        return
      }
      this.loading = true
      Promise.all([
        getSuppliceInspectionReport(id),
        getReportList({ reportId: id, pageNum: 1, pageSize: 100 })
      ]).then(([detailRes, filesRes]) => {
        this.detail = detailRes.data || {}
        this.reportFiles = filesRes.rows || filesRes.data || []
      }).finally(() => {
        this.loading = false
      })
    },
    noticeStatusText(value) {
      if (value === '1' || value === 1) return '已通过'
      if (value === '2' || value === 2) return '已拒绝'
      return '待审核'
    },
    inspectionText(value) {
      if (value === '1' || value === 1) return '合格'
      if (value === '0' || value === 0) return '不合格'
      return '-'
    },
    reportTypeText(value) {
      if (value === '1' || value === 1) return '首次预检'
      if (value === '2' || value === 2) return '二次预检'
      return value || '-'
    },
    openFile(url) {
      const fileUrl = /^https?:\/\//i.test(url)
        ? url
        : `${process.env.VUE_APP_IMAGE_BASE_API || ''}${url}`
      window.open(fileUrl, '_blank')
    },
    goBack() {
      this.$router.back()
    }
  }
}
</script>

<style scoped>
.toolbar {
  margin-bottom: 12px;
}

.section-card {
  margin-top: 14px;
}

.card-title {
  font-weight: 600;
  color: #303133;
}

.detail-grid .el-col {
  line-height: 34px;
  color: #303133;
}

.label {
  color: #606266;
}
</style>
