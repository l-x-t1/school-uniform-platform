<template>
  <div class="app-container supplier-group-notice-page">
    <el-card shadow="never" class="summary-card">
      <div class="summary-header">
        <div>
          <div class="summary-title">统购公告</div>
          <div class="summary-subtitle">
            供应商端仅用于查询学校发布的统购公告，并查看学校发送公告后确认的生产信息。
          </div>
        </div>
        <el-tag type="info" effect="plain">只读页面</el-tag>
      </div>
    </el-card>

    <el-form
      ref="queryForm"
      :model="queryParams"
      size="small"
      :inline="true"
      label-width="82px"
      class="query-form"
    >
      <el-form-item label="公告标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入公告标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="公告编号" prop="noticeCode">
        <el-input
          v-model="queryParams.noticeCode"
          placeholder="请输入公告编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="学校名称" prop="schoolName">
        <el-input
          v-model="queryParams.schoolName"
          placeholder="请输入学校名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="公告类型" prop="noticeType">
        <el-select
          v-model="queryParams.noticeType"
          placeholder="请选择公告类型"
          clearable
        >
          <el-option
            v-for="item in noticeTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="公告状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择公告状态"
          clearable
        >
          <el-option
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">
          搜索
        </el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table
      v-loading="loading"
      :data="noticeList"
      border
      stripe
      class="notice-table"
    >
      <el-table-column label="公告编号" prop="noticeCode" min-width="140" />
      <el-table-column label="公告标题" prop="title" min-width="220" show-overflow-tooltip />
      <el-table-column label="学校名称" prop="schoolName" min-width="180" show-overflow-tooltip />
      <el-table-column label="公告类型" min-width="110">
        <template slot-scope="scope">
          <el-tag size="mini" :type="scope.row.noticeType === '2' ? 'success' : 'primary'">
            {{ formatNoticeType(scope.row.noticeType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="公告状态" min-width="110">
        <template slot-scope="scope">
          <el-tag size="mini" :type="getStatusTagType(scope.row.status)">
            {{ formatStatus(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="报名状态" min-width="110">
        <template slot-scope="scope">
          {{ formatApplyStatus(scope.row.applyStatus) }}
        </template>
      </el-table-column>
      <el-table-column label="公告开始时间" min-width="120">
        <template slot-scope="scope">
          {{ formatDate(scope.row.startTime) }}
        </template>
      </el-table-column>
      <el-table-column label="公告截止时间" min-width="120">
        <template slot-scope="scope">
          {{ formatDate(scope.row.endTime) }}
        </template>
      </el-table-column>
      <el-table-column label="结果发布时间" min-width="120">
        <template slot-scope="scope">
          {{ formatDate(scope.row.resultPublishTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="120" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="mini" @click="handleView(scope.row)">
            查看详情
          </el-button>
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

    <el-dialog
      title="学校发送公告确定生产信息"
      :visible.sync="detailOpen"
      width="1080px"
      append-to-body
    >
      <div v-loading="detailLoading" class="detail-wrap">
        <el-descriptions :column="2" border class="base-info">
          <el-descriptions-item label="公告标题">
            {{ detail.title || "-" }}
          </el-descriptions-item>
          <el-descriptions-item label="公告编号">
            {{ detail.noticeCode || "-" }}
          </el-descriptions-item>
          <el-descriptions-item label="学校名称">
            {{ detail.schoolName || "-" }}
          </el-descriptions-item>
          <el-descriptions-item label="公告类型">
            {{ formatNoticeType(detail.noticeType) }}
          </el-descriptions-item>
          <el-descriptions-item label="公告状态">
            {{ formatStatus(detail.status) }}
          </el-descriptions-item>
          <el-descriptions-item label="报名状态">
            {{ formatApplyStatus(detail.applyStatus) }}
          </el-descriptions-item>
          <el-descriptions-item label="公告开始时间">
            {{ formatDate(detail.startTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="公告截止时间">
            {{ formatDate(detail.endTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="发布时间">
            {{ formatDate(detail.publishTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="结果发布时间">
            {{ formatDate(detail.resultPublishTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="适用年级">
            {{ detail.gradeNames || "-" }}
          </el-descriptions-item>
          <el-descriptions-item label="采购流程ID">
            {{ detail.purchaseProcessId || "-" }}
          </el-descriptions-item>
        </el-descriptions>

        <el-card shadow="never" class="detail-section">
          <div slot="header" class="section-title">公告正文</div>
          <div class="notice-content" v-html="detail.content || '<p>暂无公告正文</p>'"></div>
        </el-card>

        <el-card shadow="never" class="detail-section">
          <div slot="header" class="section-title">生产信息明细</div>
          <el-table
            :data="detailItems"
            border
            stripe
            empty-text="暂无生产信息"
          >
            <el-table-column label="商品ID" prop="goodsId" min-width="100" />
            <el-table-column label="商品名称" prop="goodsName" min-width="180" show-overflow-tooltip />
            <el-table-column label="单位" prop="unitName" min-width="90" />
            <el-table-column label="采购数量" prop="buyNum" min-width="100" />
            <el-table-column label="价格说明" prop="priceDesc" min-width="180" show-overflow-tooltip />
            <el-table-column label="适用年级" prop="gradeNames" min-width="180" show-overflow-tooltip />
            <el-table-column label="备注" prop="remark" min-width="160" show-overflow-tooltip />
          </el-table>
        </el-card>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getSupplierGroupNotice,
  listSupplierGroupNotice,
} from "@/api/supplier/groupNotice";

export default {
  name: "SupplierGroupNotice",
  data() {
    return {
      loading: false,
      detailLoading: false,
      total: 0,
      noticeList: [],
      detailOpen: false,
      detail: {},
      detailItems: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        noticeCode: null,
        schoolName: null,
        noticeType: null,
        status: null,
      },
      noticeTypeOptions: [
        { label: "选购公告", value: "1" },
        { label: "结果公告", value: "2" },
      ],
      statusOptions: [
        { label: "草稿", value: "0" },
        { label: "已发布", value: "1" },
        { label: "已结束", value: "2" },
        { label: "已撤回", value: "3" },
      ],
      applyStatusOptions: [
        { label: "未开放", value: "0" },
        { label: "报名中", value: "1" },
        { label: "报名结束", value: "2" },
      ],
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.loading = true;
      listSupplierGroupNotice(this.queryParams)
        .then((response) => {
          this.noticeList = response.rows || [];
          this.total = response.total || 0;
        })
        .finally(() => {
          this.loading = false;
        });
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleView(row) {
      const noticeId = row.id;
      if (!noticeId) {
        this.$message.error("当前公告缺少主键，无法查看详情");
        return;
      }
      this.detailLoading = true;
      this.detailOpen = true;
      getSupplierGroupNotice(noticeId)
        .then((response) => {
          const detailData = response.data || {};
          this.detail = detailData;
          this.detailItems = detailData.xfptGroupNoticeItemList || [];
        })
        .finally(() => {
          this.detailLoading = false;
        });
    },
    formatNoticeType(value) {
      return this.findOptionLabel(this.noticeTypeOptions, value);
    },
    formatStatus(value) {
      return this.findOptionLabel(this.statusOptions, value);
    },
    formatApplyStatus(value) {
      return this.findOptionLabel(this.applyStatusOptions, value);
    },
    findOptionLabel(options, value) {
      const match = options.find((item) => String(item.value) === String(value));
      return match ? match.label : value || "-";
    },
    getStatusTagType(value) {
      const map = {
        0: "info",
        1: "success",
        2: "warning",
        3: "danger",
      };
      return map[value] || "info";
    },
    formatDate(value) {
      if (!value) {
        return "-";
      }
      return this.parseTime(value, "{y}-{m}-{d}");
    },
  },
};
</script>

<style lang="scss" scoped>
.supplier-group-notice-page {
  .summary-card {
    margin-bottom: 16px;
  }

  .summary-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 16px;
  }

  .summary-title {
    font-size: 20px;
    font-weight: 600;
    color: #1f2d3d;
  }

  .summary-subtitle {
    margin-top: 8px;
    font-size: 13px;
    color: #6b7280;
    line-height: 1.6;
  }

  .query-form {
    padding: 18px 20px 2px;
    margin-bottom: 16px;
    background: #fff;
    border: 1px solid #ebeef5;
    border-radius: 4px;
  }

  .notice-table {
    margin-bottom: 16px;
  }

  .detail-wrap {
    min-height: 240px;
  }

  .base-info {
    margin-bottom: 16px;
  }

  .detail-section {
    margin-bottom: 16px;
  }

  .section-title {
    font-size: 15px;
    font-weight: 600;
    color: #1f2d3d;
  }

  .notice-content {
    min-height: 80px;
    color: #303133;
    line-height: 1.8;
    word-break: break-word;
  }
}
</style>
