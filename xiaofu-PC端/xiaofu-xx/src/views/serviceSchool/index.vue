<template>
  <div class="app-container">
    <el-radio-group v-model="activeStatus" size="small" class="status-tabs" @change="handleStatusChange">
      <el-radio-button label="1">服务中学校列表</el-radio-button>
      <el-radio-button label="3">历史服务学校</el-radio-button>
      <el-radio-button label="4">公告期内学校</el-radio-button>
    </el-radio-group>

    <el-form
      v-show="showSearch"
      ref="queryForm"
      :model="queryParams"
      size="small"
      :inline="true"
      label-width="80px"
    >
      <el-form-item label="学校名称" prop="schoolName">
        <el-input
          v-model="queryParams.schoolName"
          placeholder="请输入学校名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="厂商名称" prop="supplierName">
        <el-input
          v-model="queryParams.supplierName"
          placeholder="请输入厂商名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="服务电话" prop="servicePhone">
        <el-input
          v-model="queryParams.servicePhone"
          placeholder="请输入服务电话"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar :show-search.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="schoolList">
      <el-table-column label="学校名称" align="center" prop="schoolName" />
      <el-table-column label="厂商名称" align="center" prop="supplierName" />
      <el-table-column label="服务电话" align="center" prop="servicePhone" />
      <el-table-column label="统购公告ID" align="center" prop="noticeId" />
      <el-table-column label="合同ID" align="center" prop="contractId" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />
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
import { listSchoolSupplierRel } from '@/api/school/schoolSupplierRel'

export default {
  name: 'ServiceSchool',
  data() {
    return {
      activeStatus: '1',
      loading: false,
      showSearch: true,
      total: 0,
      schoolList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        schoolName: null,
        supplierName: null,
        servicePhone: null,
        cooperateStatus: '1'
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listSchoolSupplierRel({
        ...this.queryParams,
        cooperateStatus: this.activeStatus
      }).then((response) => {
        this.schoolList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleStatusChange(value) {
      this.queryParams.pageNum = 1
      this.queryParams.cooperateStatus = value
      this.getList()
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.queryParams.cooperateStatus = this.activeStatus
      this.handleQuery()
    }
  }
}
</script>

<style scoped>
.status-tabs {
  margin-bottom: 16px;
}
</style>
