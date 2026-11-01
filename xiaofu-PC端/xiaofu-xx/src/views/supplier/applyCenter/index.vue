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
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="noticeList" border>
      <el-table-column label="公告信息" min-width="280">
        <template slot-scope="scope">
          <div class="info-cell">
            <div class="info-title">{{ scope.row.noticeTitle || '--' }}</div>
            <div class="info-subline">公告编号：{{ scope.row.noticeCode || '--' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="学校名称" prop="schoolName" min-width="180" show-overflow-tooltip />
      <el-table-column label="报名截止时间" width="130" align="center">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.bmjzTime, '{y}-{m}-{d}') || '--' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="报名上限" width="120" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.signupLimit || '不限' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="当前状态" width="110" align="center">
        <template slot-scope="scope">
          <el-tag :type="getApplyStatusTag(scope.row.statusText || '未报名')">
            {{ scope.row.statusText || '未报名' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">查看报名</el-button>
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
import { listApplyCenter } from '@/api/supplier/apply'
import { getApplyStatusTag } from '@/utils/supplierApply'

export default {
  name: 'SupplierApplyCenter',
  data() {
    return {
      loading: false,
      total: 0,
      noticeList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        noticeTitle: '',
        schoolName: ''
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getApplyStatusTag,
    getList() {
      this.loading = true
      listApplyCenter(this.queryParams).then((response) => {
        this.noticeList = response.rows || []
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

<style scoped>
.info-title {
  margin-bottom: 6px;
  font-size: 14px;
  font-weight: 600;
  color: #23324a;
}

.info-subline {
  font-size: 12px;
  color: #7b8899;
}
</style>
