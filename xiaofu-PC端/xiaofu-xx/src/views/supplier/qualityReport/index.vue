<template>
  <div class="app-container">
    <el-form ref="queryForm" :model="queryParams" :inline="true" size="small">
      <el-form-item label="公告名称">
        <el-input
          v-model="queryParams.noticeName"
          placeholder="请输入公告名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="学校名称">
        <el-input
          v-model="queryParams.deptName"
          placeholder="请输入学校名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="公告状态">
        <el-select v-model="queryParams.noticeStatus" placeholder="全部" clearable>
          <el-option label="待审核" value="0" />
          <el-option label="已通过" value="1" />
          <el-option label="已拒绝" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="reportList" border>
      <el-table-column label="公告名称" prop="noticeName" min-width="180" show-overflow-tooltip />
      <el-table-column label="学校名称" prop="deptName" min-width="160" show-overflow-tooltip />
      <el-table-column label="供应商名称" prop="supplierName" min-width="160" show-overflow-tooltip />
      <el-table-column label="首次质检" prop="firstInspection" width="110" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.firstInspection === '1'" type="success">已上传</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="二次质检" prop="secondInspection" width="110" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.secondInspection === '1'" type="success">已上传</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="公告状态" prop="noticeStatusText" width="110" align="center" />
      <el-table-column label="创建时间" prop="createTime" width="160" align="center">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button
            v-hasPermi="['supplier:qualityReport:add']"
            type="text"
            icon="el-icon-plus"
            @click="handleAdd(scope.row)"
          >新增</el-button>
          <el-button
            v-hasPermi="['supplier:qualityReport:query']"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
          >详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog title="新增质检报告" :visible.sync="addOpen" width="620px" append-to-body>
      <el-form ref="addForm" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="报告标题" prop="inspectionReportTitle">
          <el-input v-model="form.inspectionReportTitle" placeholder="请输入报告标题" />
        </el-form-item>
        <el-form-item label="报告文件" prop="url">
          <file-upload v-model="form.url" :file-type="['pdf']" :limit="1" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="addOpen = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">确定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="质检报告详情" :visible.sync="detailOpen" width="900px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="公告名称">{{ detailInfo.noticeName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="学校名称">{{ detailInfo.deptName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="供应商名称">{{ detailInfo.supplierName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="公告状态">{{ detailInfo.noticeStatusText || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(detailInfo.createTime, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ parseTime(detailInfo.updateTime, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
      </el-descriptions>

      <el-table :data="fileList" border style="margin-top: 16px;">
        <el-table-column label="报告标题" prop="inspectionReportTitle" min-width="180" show-overflow-tooltip />
        <el-table-column label="报告类型" prop="type" width="120" align="center">
          <template slot-scope="scope">
            <span>{{ reportTypeText(scope.row.type) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="上传时间" prop="createTime" width="160" align="center">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="附件" prop="url" min-width="220" show-overflow-tooltip />
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import {
  addSupplierQualityReport,
  getSupplierQualityReport,
  listSupplierQualityReport,
  listSupplierQualityReportFiles
} from '@/api/supplier/feature'

export default {
  name: 'SupplierQualityReport',
  data() {
    return {
      loading: false,
      total: 0,
      reportList: [],
      addOpen: false,
      detailOpen: false,
      currentRow: null,
      detailInfo: {},
      fileList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        noticeName: '',
        deptName: '',
        noticeStatus: ''
      },
      form: {
        reportId: '',
        inspectionReportTitle: '',
        url: ''
      },
      rules: {
        inspectionReportTitle: [{ required: true, message: '请输入报告标题', trigger: 'blur' }],
        url: [{ required: true, message: '请上传报告文件', trigger: 'change' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listSupplierQualityReport(this.queryParams).then(response => {
        this.reportList = response.rows || []
        this.total = response.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        noticeName: '',
        deptName: '',
        noticeStatus: ''
      }
      this.getList()
    },
    handleAdd(row) {
      this.currentRow = row
      this.form = {
        reportId: String(row.id),
        inspectionReportTitle: '',
        url: ''
      }
      this.addOpen = true
      this.$nextTick(() => this.$refs.addForm && this.$refs.addForm.clearValidate())
    },
    submitAdd() {
      this.$refs.addForm.validate(valid => {
        if (!valid) return
        addSupplierQualityReport({
          ...this.form,
          fileName: this.fileNameFromUrl(this.form.url)
        }).then(() => {
          this.$modal.msgSuccess('新增成功')
          this.addOpen = false
          this.getList()
        })
      })
    },
    handleDetail(row) {
      this.detailOpen = true
      this.detailInfo = row
      this.fileList = []
      getSupplierQualityReport(row.id).then(response => {
        this.detailInfo = response.data || row
      })
      listSupplierQualityReportFiles({ reportId: row.id, pageNum: 1, pageSize: 100 }).then(response => {
        this.fileList = response.rows || []
      })
    },
    reportTypeText(type) {
      if (String(type) === '1') return '首次质检'
      if (String(type) === '2') return '二次质检'
      return '-'
    },
    fileNameFromUrl(url) {
      if (!url) return ''
      const first = String(url).split(',')[0]
      return first.substring(first.lastIndexOf('/') + 1)
    }
  }
}
</script>
