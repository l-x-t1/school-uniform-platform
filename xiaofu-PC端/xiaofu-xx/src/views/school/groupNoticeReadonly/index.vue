<template>
  <div class="app-container group-notice-page">
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
      <el-form-item v-if="isAdmin" label="所属学校" prop="schoolId">
        <el-select
          v-model="queryParams.schoolId"
          placeholder="请选择学校"
          clearable
          filterable
        >
          <el-option
            v-for="school in schoolOptions"
            :key="school.id"
            :label="school.schoolName"
            :value="school.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item v-else label="所属学校">
        <el-input :value="currentSchool.schoolName || '-'" disabled />
      </el-form-item>
      <el-form-item label="公告类型" prop="noticeType">
        <el-select v-model="queryParams.noticeType" placeholder="请选择公告类型" clearable>
          <el-option
            v-for="item in noticeTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="公告状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择公告状态" clearable>
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

    <el-table v-loading="loading" :data="groupNoticeList" border stripe>
      <el-table-column label="公告编号" prop="noticeCode" min-width="140" />
      <el-table-column label="公告标题" prop="title" min-width="220" show-overflow-tooltip />
      <el-table-column label="学校名称" prop="schoolName" min-width="180" show-overflow-tooltip />
      <el-table-column label="公告类型" min-width="110">
        <template slot-scope="scope">
          <el-tag size="mini" :type="scope.row.noticeType === '2' ? 'success' : 'primary'">
            {{ formatOption(noticeTypeOptions, scope.row.noticeType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="公告状态" min-width="110">
        <template slot-scope="scope">
          <el-tag size="mini" :type="getStatusTagType(scope.row.status)">
            {{ formatOption(statusOptions, scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="报名状态" min-width="110">
        <template slot-scope="scope">
          {{ formatOption(applyStatusOptions, scope.row.applyStatus) }}
        </template>
      </el-table-column>
      <el-table-column label="公告开始时间" min-width="120">
        <template slot-scope="scope">{{ formatDate(scope.row.startTime) }}</template>
      </el-table-column>
      <el-table-column label="公告截止时间" min-width="120">
        <template slot-scope="scope">{{ formatDate(scope.row.endTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="150" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="mini" @click="handleView(scope.row)">
            详情
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

    <el-dialog title="公告详情" :visible.sync="detailOpen" width="980px" append-to-body>
      <div v-loading="detailLoading">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="公告标题">{{ detail.title || "-" }}</el-descriptions-item>
          <el-descriptions-item label="公告编号">{{ detail.noticeCode || "-" }}</el-descriptions-item>
          <el-descriptions-item label="学校名称">{{ detail.schoolName || "-" }}</el-descriptions-item>
          <el-descriptions-item label="公告类型">
            {{ formatOption(noticeTypeOptions, detail.noticeType) }}
          </el-descriptions-item>
          <el-descriptions-item label="公告状态">
            {{ formatOption(statusOptions, detail.status) }}
          </el-descriptions-item>
          <el-descriptions-item label="报名状态">
            {{ formatOption(applyStatusOptions, detail.applyStatus) }}
          </el-descriptions-item>
          <el-descriptions-item label="公告开始时间">{{ formatDate(detail.startTime) }}</el-descriptions-item>
          <el-descriptions-item label="公告截止时间">{{ formatDate(detail.endTime) }}</el-descriptions-item>
          <el-descriptions-item label="适用年级">{{ detail.gradeNames || "-" }}</el-descriptions-item>
          <el-descriptions-item label="发布时间">{{ formatDate(detail.publishTime) }}</el-descriptions-item>
        </el-descriptions>
        <el-card shadow="never" class="detail-section">
          <div slot="header">公告正文</div>
          <div class="notice-content">{{ detail.content || "暂无公告正文" }}</div>
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
  listGroupNotice,
  getGroupNotice,
  listSupplierGroupNotice,
  getSupplierGroupNotice,
} from "@/api/school/groupNotice";
import {
  listSchoolMessage,
  getSchoolMessageByDeptId,
} from "@/api/school/schoolMessage";

export default {
  name: "GroupNoticeReadonly",
  data() {
    return {
      loading: false,
      detailLoading: false,
      total: 0,
      groupNoticeList: [],
      schoolOptions: [],
      currentSchool: {},
      detailOpen: false,
      detail: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        noticeCode: null,
        schoolId: null,
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
  computed: {
    isAdmin() {
      const roles = this.$store.getters.roles || [];
      return roles.includes("admin");
    },
    isSupplier() {
      const roles = this.$store.getters.roles || [];
      return roles.some((role) => {
        const roleKey = (role || "").toLowerCase();
        return roleKey.includes("gys") || roleKey.includes("changshang");
      });
    },
  },
  created() {
    this.loadSchoolContext();
    this.getList();
  },
  methods: {
    loadSchoolContext() {
      if (this.isAdmin) {
        listSchoolMessage({ pageNum: 1, pageSize: 1000 }).then((res) => {
          this.schoolOptions = res.rows || [];
        });
        return;
      }
      if (this.isSupplier) {
        this.currentSchool = {};
        this.queryParams.schoolId = null;
        return;
      }
      const deptId = this.$store.getters.deptId;
      if (!deptId) {
        return;
      }
      getSchoolMessageByDeptId(deptId).then((res) => {
        this.currentSchool = res.data || {};
        if (this.currentSchool && this.currentSchool.id) {
          this.queryParams.schoolId = this.currentSchool.id;
        }
      });
    },
    getList() {
      this.loading = true;
      const request = this.isSupplier
        ? listSupplierGroupNotice(this.queryParams)
        : listGroupNotice(this.queryParams);
      request
        .then((response) => {
          this.groupNoticeList = response.rows || [];
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
      if (!this.isAdmin && !this.isSupplier && this.currentSchool && this.currentSchool.id) {
        this.queryParams.schoolId = this.currentSchool.id;
      }
      this.handleQuery();
    },
    handleView(row) {
      if (!row.id) {
        this.$message.error("当前公告缺少主键，无法查看详情");
        return;
      }
      this.detailOpen = true;
      this.detailLoading = true;
      const request = this.isSupplier
        ? getSupplierGroupNotice(row.id)
        : getGroupNotice(row.id);
      request
        .then((response) => {
          this.detail = response.data || {};
        })
        .finally(() => {
          this.detailLoading = false;
        });
    },
    formatOption(options, value) {
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
.group-notice-page {
  .query-form {
    padding: 18px 20px 2px;
    margin-bottom: 16px;
    background: #fff;
    border: 1px solid #ebeef5;
    border-radius: 4px;
  }

  .detail-section {
    margin-top: 16px;
  }

  .notice-content {
    min-height: 80px;
    color: #303133;
    line-height: 1.8;
    white-space: pre-wrap;
    word-break: break-word;
  }
}
</style>
