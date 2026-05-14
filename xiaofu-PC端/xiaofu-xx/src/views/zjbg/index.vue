<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams">
      <el-form-item label="公告创建时间：">
        <el-input v-model="queryParams.dateRange" placeholder="请选择日期范围" />
      </el-form-item>

      <el-form-item>
        <el-button-group class="semester-buttons">
          <el-button :class="{ 'is-active': queryParams.semester === 'upper' }" @click="selectSemester('upper')">上学期</el-button>
          <el-button :class="{ 'is-active': queryParams.semester === 'lower' }" @click="selectSemester('lower')">下学期</el-button>
          <el-button :class="{ 'is-active': queryParams.semester === 'current' }" @click="selectSemester('current')">本学年</el-button>
          <el-button :class="{ 'is-active': queryParams.semester === 'last' }" @click="selectSemester('last')">上学年</el-button>
        </el-button-group>
      </el-form-item>
      <br>

      <el-form-item label="公告名称：">
        <el-input v-model="queryParams.noticeName" placeholder="请输入公告名称" clearable style="width: 250px;" @keyup.enter.native="handleQuery" />
      </el-form-item>

      <el-form-item label="厂商名称：">
        <el-input v-model="queryParams.manufacturerName" placeholder="请输入厂商名称" clearable style="width: 250px;" @keyup.enter.native="handleQuery" />
      </el-form-item>

      <el-form-item label="首次预检：">
        <el-select v-model="queryParams.firstInspection" placeholder="全部" style="width: 200px;">
          <el-option label="全部" value="" />
          <el-option label="合格" value="1" />
          <el-option label="不合格" value="0" />
        </el-select>
      </el-form-item>

      <el-form-item label="二次预检：">
        <el-select v-model="queryParams.secondInspection" placeholder="全部" style="width: 200px;">
          <el-option label="全部" value="" />
          <el-option label="合格" value="1" />
          <el-option label="不合格" value="0" />
        </el-select>
      </el-form-item>

      <el-form-item label="公告状态：">
        <el-select v-model="queryParams.noticeStatus" placeholder="全部" style="width: 200px;">
          <el-option label="全部" value="" />
          <el-option label="待审核" value="0" />
          <el-option label="已通过" value="1" />
          <el-option label="已拒绝" value="2" />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button size="small" type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="tableData" style="width: 100%">
      <el-table-column label="公告名称" prop="noticeName" :show-overflow-tooltip="true" />
      <el-table-column label="公告状态" prop="noticeStatus" width="100" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.noticeStatus === '1' ? 'success' : (scope.row.noticeStatus === '0' ? 'warning' : 'danger')">
            {{ scope.row.noticeStatusText || '-' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="厂商名称" prop="manufacturerName" :show-overflow-tooltip="true" />
      <el-table-column label="首次预检(款)" prop="firstInspection" width="120" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.firstInspection" :type="scope.row.firstInspection === '1' ? 'success' : 'danger'">
            {{ scope.row.firstInspection === '1' ? '合格' : '不合格' }}
          </el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="二次预检(款)" prop="secondInspection" width="120" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.secondInspection" :type="scope.row.secondInspection === '1' ? 'success' : 'danger'">
            {{ scope.row.secondInspection === '1' ? '合格' : '不合格' }}
          </el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" align="center">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="质检报告详情" :visible.sync="detailOpen" width="900px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="公告名称">{{ detailInfo.noticeName || detailInfo.ggTitle || '-' }}</el-descriptions-item>
        <el-descriptions-item label="厂商名称">{{ detailInfo.manufacturerName || detailInfo.supplierName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="公告状态">{{ detailInfo.noticeStatusText || '-' }}</el-descriptions-item>
        <el-descriptions-item label="法定代表人">{{ detailInfo.legalRepresentative || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(detailInfo.createTime, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ parseTime(detailInfo.updateTime, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="reportFileList" border style="width: 100%; margin-top: 16px;">
        <el-table-column label="报告名称" prop="inspectionReportTitle" :show-overflow-tooltip="true" />
        <el-table-column label="报告类型" prop="type" width="120" align="center">
          <template slot-scope="scope">
            <span>{{ reportTypeText(scope.row.type) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="文件名" prop="fileName" :show-overflow-tooltip="true" />
        <el-table-column label="上传时间" prop="createTime" width="160" align="center">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') || '-' }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { getReportList, getSuppliceInspectionReport, suppliceInspectionReportList } from '@/api/school/report'

export default {
  name: 'ZjbgIndex',
  data() {
    return {
      loading: false,
      total: 0,
      tableData: [],
      detailOpen: false,
      detailInfo: {},
      reportFileList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        dateRange: '2024-08-01 - 2025-04-17',
        semester: 'current',
        noticeName: '',
        manufacturerName: '',
        firstInspection: '',
        secondInspection: '',
        noticeStatus: ''
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      suppliceInspectionReportList(this.queryParams).then(response => {
        this.tableData = response.rows || []
        this.total = response.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    selectSemester(semester) {
      this.queryParams.semester = semester
      switch (semester) {
        case 'upper':
          this.queryParams.dateRange = '2024-03-01 - 2024-07-31'
          break
        case 'lower':
          this.queryParams.dateRange = '2024-09-01 - 2025-01-31'
          break
        case 'current':
          this.queryParams.dateRange = '2024-08-01 - 2025-04-17'
          break
        case 'last':
          this.queryParams.dateRange = '2023-08-01 - 2024-07-31'
          break
      }
      this.handleQuery()
    },
    handleView(row) {
      this.detailOpen = true
      this.detailInfo = row
      this.reportFileList = []
      getSuppliceInspectionReport(row.id).then(response => {
        this.detailInfo = response.data || row
      })
      getReportList({ reportId: row.id, pageNum: 1, pageSize: 100 }).then(response => {
        this.reportFileList = response.rows || []
      })
    },
    reportTypeText(type) {
      if (type === '1' || type === 1) return '首次预检'
      if (type === '2' || type === 2) return '二次预检'
      return '-'
    }
  }
}
</script>

<style scoped>
.semester-buttons {
  margin-left: 10px;
}

.semester-buttons .el-button {
  padding: 8px 15px;
}

.semester-buttons .is-active {
  color: #409EFF;
  background-color: #ecf5ff;
  border-color: #c6e2ff;
}

:deep(.el-tag) {
  padding: 0 8px;
  height: 24px;
  line-height: 22px;
}
</style>
