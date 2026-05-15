<template>
  <div class="app-container">
    <el-tabs v-model="activeType" @tab-click="handleTabClick">
      <el-tab-pane label="服务中学校列表" name="serving" />
      <el-tab-pane label="历史服务学校" name="history" />
      <el-tab-pane label="公示期内学校" name="notice" />
    </el-tabs>

    <el-form :model="queryParams" :inline="true" size="small">
      <el-form-item label="学校名称">
        <el-input
          v-model="queryParams.schoolName"
          placeholder="请输入学校名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="schoolList" border>
      <el-table-column label="学校名称" prop="schoolName" min-width="180" show-overflow-tooltip />
      <el-table-column label="供应商名称" prop="supplierName" min-width="160" show-overflow-tooltip />
      <el-table-column label="公告ID" prop="noticeId" width="120" align="center" />
      <el-table-column label="合同ID" prop="contractId" width="120" align="center" />
      <el-table-column label="状态" prop="cooperateStatus" width="120" align="center">
        <template slot-scope="scope">
          <el-tag :type="statusTagType(scope.row.cooperateStatus)">
            {{ statusText(scope.row.cooperateStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="160" align="center">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
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
  </div>
</template>

<script>
import { listSupplierServiceSchool } from '@/api/supplier/feature'

export default {
  name: 'SupplierServiceSchool',
  data() {
    return {
      activeType: 'serving',
      loading: false,
      total: 0,
      schoolList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        schoolName: ''
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listSupplierServiceSchool({
        ...this.queryParams,
        type: this.activeType
      }).then(response => {
        this.schoolList = response.rows || []
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
        schoolName: ''
      }
      this.getList()
    },
    handleTabClick() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    statusText(status) {
      const map = {
        1: '合作中',
        2: '已取消',
        3: '历史合作',
        4: '公告期内'
      }
      return map[String(status)] || '-'
    },
    statusTagType(status) {
      const map = {
        1: 'success',
        2: 'info',
        3: 'warning',
        4: 'primary'
      }
      return map[String(status)] || ''
    }
  }
}
</script>
