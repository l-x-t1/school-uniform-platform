<template>
  <div class="app-container supplier-settle-audit-page">
    <el-card shadow="never">
      <div class="page-header">
        <div>
          <div class="page-title">供应商入驻审核</div>
          <div class="page-subtitle">审核供应商提交的入驻申请，并查询自己发布公告下已审核通过的厂商信息</div>
        </div>
        <el-button type="primary" plain icon="el-icon-refresh" size="mini" @click="refreshList">
          刷新
        </el-button>
      </div>

      <el-tabs v-model="activeTab" @tab-click="handleTabChange">
        <el-tab-pane label="待审核申请" name="pending">
          <el-form
            ref="pendingQueryForm"
            :model="pendingQueryParams"
            size="small"
            :inline="true"
            label-width="100px"
            class="query-form"
          >
            <el-form-item label="厂商名称" prop="supplierName">
              <el-input
                v-model="pendingQueryParams.supplierName"
                placeholder="请输入厂商名称"
                clearable
                @keyup.enter.native="handlePendingQuery"
              />
            </el-form-item>
            <el-form-item label="联系电话" prop="supplierPhone">
              <el-input
                v-model="pendingQueryParams.supplierPhone"
                placeholder="请输入联系电话"
                clearable
                @keyup.enter.native="handlePendingQuery"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" size="mini" @click="handlePendingQuery">
                搜索
              </el-button>
              <el-button icon="el-icon-refresh" size="mini" @click="resetPendingQuery">
                重置
              </el-button>
            </el-form-item>
          </el-form>

          <el-table v-loading="pendingLoading" :data="pendingRows" border>
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column label="厂商名称" prop="supplierName" min-width="160" show-overflow-tooltip />
            <el-table-column label="联系电话" prop="supplierPhone" width="130" align="center" />
            <el-table-column label="入驻公告" prop="noticeTitle" min-width="180" show-overflow-tooltip />
            <el-table-column label="用户名" prop="username" width="120" align="center" />
            <el-table-column label="提交时间" prop="createTime" width="170" align="center">
              <template slot-scope="scope">
                {{ parseTime(scope.row.createTime, "{y}-{m}-{d} {h}:{i}") || "-" }}
              </template>
            </el-table-column>
            <el-table-column label="审核状态" width="100" align="center">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.auditStatus === '0'" type="warning" size="small">待审核</el-tag>
                <el-tag v-else-if="scope.row.auditStatus === '1'" type="success" size="small">已通过</el-tag>
                <el-tag v-else-if="scope.row.auditStatus === '2'" type="danger" size="small">已驳回</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" align="center" fixed="right">
              <template slot-scope="scope">
                <template v-if="scope.row.auditStatus === '0'">
                  <el-button type="text" icon="el-icon-check" style="color: #67c23a" @click="handleApprove(scope.row)">
                    通过
                  </el-button>
                  <el-button type="text" icon="el-icon-close" style="color: #f56c6c" @click="handleRejectOpen(scope.row)">
                    驳回
                  </el-button>
                </template>
                <el-button type="text" icon="el-icon-view" @click="openDetail(scope.row, 'pending')">
                  详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <pagination
            v-show="pendingTotal > 0"
            :total="pendingTotal"
            :page.sync="pendingQueryParams.pageNum"
            :limit.sync="pendingQueryParams.pageSize"
            @pagination="getPendingList"
          />
        </el-tab-pane>

        <el-tab-pane label="已通过厂商" name="approved">
          <el-form
            ref="approvedQueryForm"
            :model="approvedQueryParams"
            size="small"
            :inline="true"
            label-width="100px"
            class="query-form"
          >
            <el-form-item label="厂商名称" prop="supplierName">
              <el-input
                v-model="approvedQueryParams.supplierName"
                placeholder="请输入厂商名称"
                clearable
                @keyup.enter.native="handleApprovedQuery"
              />
            </el-form-item>
            <el-form-item label="公告标题" prop="noticeTitle">
              <el-input
                v-model="approvedQueryParams.noticeTitle"
                placeholder="请输入公告标题"
                clearable
                @keyup.enter.native="handleApprovedQuery"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" size="mini" @click="handleApprovedQuery">
                搜索
              </el-button>
              <el-button icon="el-icon-refresh" size="mini" @click="resetApprovedQuery">
                重置
              </el-button>
              <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleApprovedExport">
                导出
              </el-button>
            </el-form-item>
          </el-form>

          <el-table v-loading="approvedLoading" :data="approvedRows" border>
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column label="厂商名称" prop="supplierName" min-width="160" show-overflow-tooltip />
            <el-table-column label="联系电话" prop="supplierPhone" width="130" align="center" />
            <el-table-column label="入驻公告标题" prop="noticeTitle" min-width="220" show-overflow-tooltip />
            <el-table-column label="用户名" prop="username" width="120" align="center" />
            <el-table-column label="厂商类型" prop="supplierType" min-width="120" show-overflow-tooltip />
            <el-table-column label="法定代表人" prop="legalRepresentative" width="120" align="center" />
            <el-table-column label="审核时间" prop="auditTime" width="170" align="center">
              <template slot-scope="scope">
                {{ parseTime(scope.row.auditTime, "{y}-{m}-{d} {h}:{i}") || "-" }}
              </template>
            </el-table-column>
            <el-table-column label="审核人" prop="auditBy" width="120" align="center" />
            <el-table-column label="操作" width="100" align="center" fixed="right">
              <template slot-scope="scope">
                <el-button type="text" icon="el-icon-view" @click="openDetail(scope.row, 'approved')">
                  详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <pagination
            v-show="approvedTotal > 0"
            :total="approvedTotal"
            :page.sync="approvedQueryParams.pageNum"
            :limit.sync="approvedQueryParams.pageSize"
            @pagination="getApprovedList"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog title="驳回入驻申请" :visible.sync="rejectDialogOpen" width="520px" append-to-body>
      <el-form ref="rejectForm" :model="rejectForm" :rules="rejectRules" label-width="80px">
        <el-form-item label="厂商名称">
          <span>{{ currentRow.supplierName }}</span>
        </el-form-item>
        <el-form-item label="驳回备注" prop="remark">
          <el-input
            v-model="rejectForm.remark"
            type="textarea"
            :rows="4"
            placeholder="请输入驳回原因"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="rejectDialogOpen = false">取 消</el-button>
        <el-button type="danger" :loading="rejectLoading" @click="handleRejectSubmit">
          确 定 驳 回
        </el-button>
      </div>
    </el-dialog>

    <el-dialog :title="detailDialogTitle" :visible.sync="detailDialogOpen" width="720px" append-to-body>
      <el-descriptions v-if="currentRow.id" :column="2" border size="small">
        <el-descriptions-item label="厂商名称" :span="2">
          {{ currentRow.supplierName || "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="来源入驻公告" :span="2">
          {{ currentRow.noticeTitle || "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentRow.username || "-" }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentRow.phone || "-" }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentRow.supplierPhone || "-" }}</el-descriptions-item>
        <el-descriptions-item label="厂商类型">{{ currentRow.supplierType || "-" }}</el-descriptions-item>
        <el-descriptions-item label="法定代表人">{{ currentRow.legalRepresentative || "-" }}</el-descriptions-item>
        <el-descriptions-item label="职工人数">{{ currentRow.staffNumber || "-" }}</el-descriptions-item>
        <el-descriptions-item label="注册资金">{{ currentRow.registeredCapital || "-" }}</el-descriptions-item>
        <el-descriptions-item label="成立时间">
          {{ parseTime(currentRow.establishedTime, "{y}-{m}-{d}") || "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="提交时间">
          {{ parseTime(currentRow.createTime, "{y}-{m}-{d} {h}:{i}") || "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="审核时间">
          {{ parseTime(currentRow.auditTime, "{y}-{m}-{d} {h}:{i}") || "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="审核人">
          {{ currentRow.auditBy || currentRow.updateBy || "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="厂商地址" :span="2">
          {{ currentRow.supplierAddress || "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="厂商简介" :span="2">
          <div v-html="currentRow.supplierIntroduce || '-'" class="content-block"></div>
        </el-descriptions-item>
        <el-descriptions-item label="厂商资质" :span="2">
          <template v-if="businessLicenseFiles.length > 0">
            <div v-for="(file, idx) in businessLicenseFiles" :key="idx" class="attachment-item">
              <i :class="getFileIcon(file)"></i>
              <span class="attachment-name">{{ getFileName(file) }}</span>
              <el-button type="text" size="mini" icon="el-icon-download" @click="downloadFile(file)">下载</el-button>
              <el-button v-if="isPreviewable(file)" type="text" size="mini" icon="el-icon-view" @click="handlePreview(file)">预览</el-button>
            </div>
          </template>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="信用评价" :span="2">
          <template v-if="creditRatingFiles.length > 0">
            <div v-for="(file, idx) in creditRatingFiles" :key="idx" class="attachment-item">
              <i :class="getFileIcon(file)"></i>
              <span class="attachment-name">{{ getFileName(file) }}</span>
              <el-button type="text" size="mini" icon="el-icon-download" @click="downloadFile(file)">下载</el-button>
              <el-button v-if="isPreviewable(file)" type="text" size="mini" icon="el-icon-view" @click="handlePreview(file)">预览</el-button>
            </div>
          </template>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag v-if="currentRow.auditStatus === '0'" type="warning" size="small">待审核</el-tag>
          <el-tag v-else-if="currentRow.auditStatus === '1'" type="success" size="small">已通过</el-tag>
          <el-tag v-else-if="currentRow.auditStatus === '2'" type="danger" size="small">已驳回</el-tag>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="审核备注">
          {{ currentRow.auditRemark || "-" }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog title="文件预览" :visible.sync="previewVisible" width="80%" append-to-body :before-close="handlePreviewClose">
      <div class="preview-container">
        <iframe v-if="previewUrl" :src="previewUrl" width="100%" frameborder="0" class="preview-iframe"></iframe>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="previewVisible = false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  auditList,
  approvedList,
  approveApply,
  rejectApply,
  exportApprovedApply,
} from "@/api/school/supplierSettleAudit";

export default {
  name: "SupplierSettleAudit",
  data() {
    return {
      activeTab: "pending",
      pendingLoading: false,
      approvedLoading: false,
      rejectLoading: false,
      approvedLoaded: false,
      pendingTotal: 0,
      approvedTotal: 0,
      pendingRows: [],
      approvedRows: [],
      pendingQueryParams: {
        pageNum: 1,
        pageSize: 10,
        supplierName: "",
        supplierPhone: "",
      },
      approvedQueryParams: {
        pageNum: 1,
        pageSize: 10,
        supplierName: "",
        noticeTitle: "",
      },
      currentRow: {},
      detailMode: "pending",
      rejectDialogOpen: false,
      detailDialogOpen: false,
      previewVisible: false,
      previewUrl: "",
      rejectForm: {
        remark: "",
      },
      rejectRules: {
        remark: [{ required: true, message: "请输入驳回原因", trigger: "blur" }],
      },
    };
  },
  computed: {
    businessLicenseFiles() {
      if (!this.currentRow.businessLicense) return [];
      return this.currentRow.businessLicense.split(",").filter(function(file) {
        return file.trim() !== "";
      });
    },
    creditRatingFiles() {
      if (!this.currentRow.xypj) return [];
      return this.currentRow.xypj.split(",").filter(function(file) {
        return file.trim() !== "";
      });
    },
    detailDialogTitle() {
      return this.detailMode === "approved" ? "已通过入驻详情" : "入驻申请详情";
    },
  },
  created() {
    this.getPendingList();
  },
  methods: {
    getPendingList() {
      this.pendingLoading = true;
      auditList(this.pendingQueryParams)
        .then((response) => {
          this.pendingRows = response.rows || [];
          this.pendingTotal = response.total || 0;
        })
        .finally(() => {
          this.pendingLoading = false;
        });
    },
    getApprovedList() {
      this.approvedLoading = true;
      this.approvedLoaded = true;
      approvedList(this.approvedQueryParams)
        .then((response) => {
          this.approvedRows = response.rows || [];
          this.approvedTotal = response.total || 0;
        })
        .finally(() => {
          this.approvedLoading = false;
        });
    },
    handleTabChange() {
      if (this.activeTab === "approved" && !this.approvedLoaded) {
        this.getApprovedList();
      }
    },
    handlePendingQuery() {
      this.pendingQueryParams.pageNum = 1;
      this.getPendingList();
    },
    resetPendingQuery() {
      this.resetForm("pendingQueryForm");
      this.handlePendingQuery();
    },
    handleApprovedQuery() {
      this.approvedQueryParams.pageNum = 1;
      this.getApprovedList();
    },
    resetApprovedQuery() {
      this.resetForm("approvedQueryForm");
      this.handleApprovedQuery();
    },
    handleApprovedExport() {
      exportApprovedApply({
        ...this.approvedQueryParams,
      });
    },
    refreshList() {
      if (this.activeTab === "approved") {
        this.getApprovedList();
        return;
      }
      this.getPendingList();
    },
    handleApprove(row) {
      const name = row.supplierName || "该申请";
      this.$confirm(
        `确认通过 <strong>${name}</strong> 的入驻申请吗？通过后系统将自动创建厂商账号和厂商信息。`,
        "审核通过确认",
        {
          confirmButtonText: "确认通过",
          cancelButtonText: "取消",
          type: "success",
          dangerouslyUseHTMLString: true,
        }
      )
        .then(() => {
          return approveApply(row.id);
        })
        .then(() => {
          this.msgSuccess("审核通过，已创建供应商账号和厂商信息");
          if (this.approvedLoaded) {
            this.getApprovedList();
          }
        })
        .catch(() => {
          // 取消或无操作
        })
        .finally(() => {
          this.getPendingList();
        });
    },
    handleRejectOpen(row) {
      this.currentRow = row;
      this.rejectForm.remark = "";
      this.rejectDialogOpen = true;
      this.$nextTick(() => {
        this.$refs.rejectForm && this.$refs.rejectForm.clearValidate();
      });
    },
    handleRejectSubmit() {
      this.$refs.rejectForm.validate((valid) => {
        if (!valid) return;
        this.rejectLoading = true;
        rejectApply(this.currentRow.id, this.rejectForm.remark)
          .then(() => {
            this.msgSuccess("已驳回该入驻申请");
            this.rejectDialogOpen = false;
          })
          .finally(() => {
            this.getPendingList();
            this.rejectLoading = false;
          });
      });
    },
    openDetail(row, mode) {
      this.currentRow = { ...row };
      this.detailMode = mode;
      this.detailDialogOpen = true;
    },
    getFileName(filePath) {
      if (!filePath) return "";
      var parts = filePath.split("/");
      return parts[parts.length - 1];
    },
    getFileIcon(filePath) {
      var ext = this.getFileName(filePath).split(".").pop().toLowerCase();
      var iconMap = {
        pdf: "el-icon-document",
        doc: "el-icon-document",
        docx: "el-icon-document",
        xls: "el-icon-s-data",
        xlsx: "el-icon-s-data",
        ppt: "el-icon-s-data",
        pptx: "el-icon-s-data",
        png: "el-icon-picture",
        jpg: "el-icon-picture",
        jpeg: "el-icon-picture",
        gif: "el-icon-picture",
        txt: "el-icon-tickets",
      };
      return iconMap[ext] || "el-icon-document";
    },
    isPreviewable(filePath) {
      var ext = this.getFileName(filePath).split(".").pop().toLowerCase();
      return ["pdf", "png", "jpg", "jpeg"].indexOf(ext) >= 0;
    },
    downloadFile(filePath) {
      var link = document.createElement("a");
      link.href = process.env.VUE_APP_IMAGE_BASE_API + filePath;
      link.setAttribute("download", this.getFileName(filePath));
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
    handlePreview(filePath) {
      this.previewUrl = process.env.VUE_APP_IMAGE_BASE_API + filePath;
      this.previewVisible = true;
    },
    handlePreviewClose() {
      this.previewUrl = "";
      this.previewVisible = false;
    },
  },
};
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 16px;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.page-subtitle {
  margin-top: 6px;
  font-size: 13px;
  color: #909399;
}

.query-form {
  margin-bottom: 12px;
}

.content-block {
  max-height: 300px;
  overflow-y: auto;
}

.attachment-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 0;
  font-size: 13px;
}

.attachment-item + .attachment-item {
  border-top: 1px dashed #e4e7ed;
}

.attachment-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #606266;
}

.preview-container {
  width: 100%;
  height: 86vh;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
}

.preview-iframe {
  width: 100%;
  height: 100%;
  border: none;
}
</style>
