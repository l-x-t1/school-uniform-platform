<template>
  <div class="app-container">
    <el-form :model="queryParams" :inline="true" size="small">
      <el-form-item label="公告标题">
        <el-input
          v-model="queryParams.noticeTitle"
          placeholder="请输入公告标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="学校名称">
        <el-input
          v-model="queryParams.schoolName"
          placeholder="请输入学校名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="报名状态">
        <el-select v-model="statusFilter" placeholder="全部" clearable @change="handleQuery">
          <el-option label="待提交" value="待提交" />
          <el-option label="待审核" value="待审核" />
          <el-option label="已通过" value="已通过" />
          <el-option label="已驳回" value="已驳回" />
          <el-option label="已撤回" value="已撤回" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="filteredList" border>
      <el-table-column label="报名单号" prop="applyNo" min-width="210" show-overflow-tooltip />
      <el-table-column label="公告标题" prop="noticeTitle" min-width="240" show-overflow-tooltip />
      <el-table-column label="学校名称" prop="schoolName" min-width="180" show-overflow-tooltip />
      <el-table-column label="报名状态" width="110" align="center">
        <template slot-scope="scope">
          <el-tag :type="getApplyStatusTag(getApplyDisplayStatus(scope.row))">
            {{ getApplyDisplayStatus(scope.row) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="报名时间" width="120" align="center">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.applyTime, '{y}-{m}-{d}') || '--' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审核时间" width="120" align="center">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.auditTime, '{y}-{m}-{d}') || '--' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">查看详情</el-button>
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
import { listMyApply } from '@/api/supplier/apply'
import { getApplyDisplayStatus, getApplyStatusTag } from '@/utils/supplierApply'

export default {
  name: 'SupplierMyApply',
  data() {
    return {
      loading: false,
      total: 0,
      applyList: [],
      statusFilter: '',
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        noticeTitle: '',
        schoolName: ''
      }
    }
  },
  computed: {
    filteredList() {
      if (!this.statusFilter) {
        return this.applyList
      }
      return this.applyList.filter((item) => getApplyDisplayStatus(item) === this.statusFilter)
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getApplyDisplayStatus,
    getApplyStatusTag,
    getList() {
      this.loading = true
      listMyApply(this.queryParams).then((response) => {
        this.applyList = response.rows || []
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
      this.statusFilter = ''
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        noticeTitle: '',
        schoolName: ''
      }
      this.getList()
    },
    openDetail(row) {
      this.$router.push({
        path: '/supplier/apply/detail',
        query: { noticeId: row.noticeId }
      })
    }
  }
}
</script>
