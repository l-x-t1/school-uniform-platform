<template>
  <div class="app-container supplier-supervision-page">
    <el-card shadow="never">
      <div class="page-header">
        <div>
          <div class="page-title">供应商黑名单与投诉</div>
          <div class="page-subtitle">查看供应商黑名单记录和家长反馈投诉信息</div>
        </div>
        <el-button type="primary" plain icon="el-icon-refresh" size="mini" @click="refreshCurrentTab">
          刷新
        </el-button>
      </div>

      <el-tabs v-model="activeTab" @tab-click="handleTabChange">
        <el-tab-pane label="供应商黑名单" name="blacklist" />
        <el-tab-pane label="原因投诉列表" name="complaint" />
      </el-tabs>

      <div v-show="activeTab === 'blacklist'">
        <el-form
          ref="blacklistQueryForm"
          :model="blacklistQueryParams"
          size="small"
          :inline="true"
          label-width="100px"
          class="query-form"
        >
          <el-form-item label="厂商名称" prop="name">
            <el-input
              v-model="blacklistQueryParams.name"
              placeholder="请输入厂商名称"
              clearable
              @keyup.enter.native="handleBlacklistQuery"
            />
          </el-form-item>
          <el-form-item label="信用代码" prop="xydm">
            <el-input
              v-model="blacklistQueryParams.xydm"
              placeholder="请输入企业信用代码"
              clearable
              @keyup.enter.native="handleBlacklistQuery"
            />
          </el-form-item>
          <el-form-item label="法定代表人" prop="userName">
            <el-input
              v-model="blacklistQueryParams.userName"
              placeholder="请输入法定代表人"
              clearable
              @keyup.enter.native="handleBlacklistQuery"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleBlacklistQuery">
              搜索
            </el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetBlacklistQuery">
              重置
            </el-button>
          </el-form-item>
        </el-form>

        <el-table v-loading="blacklistLoading" :data="blacklistRows" border>
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column label="厂商名称" prop="name" min-width="180" show-overflow-tooltip />
          <el-table-column label="企业信用代码" prop="xydm" min-width="180" show-overflow-tooltip />
          <el-table-column label="法定代表人" prop="userName" width="140" align="center" />
          <el-table-column label="拉黑原因" prop="remark" min-width="260" show-overflow-tooltip>
            <template slot-scope="scope">
              {{ scope.row.remark || "-" }}
            </template>
          </el-table-column>
          <el-table-column label="创建时间" prop="createTime" width="170" align="center">
            <template slot-scope="scope">
              {{ parseTime(scope.row.createTime, "{y}-{m}-{d} {h}:{i}") || "-" }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" align="center" fixed="right">
            <template slot-scope="scope">
              <el-button type="text" icon="el-icon-view" @click="handleBlacklistDetail(scope.row)">
                详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <pagination
          v-show="blacklistTotal > 0"
          :total="blacklistTotal"
          :page.sync="blacklistQueryParams.pageNum"
          :limit.sync="blacklistQueryParams.pageSize"
          @pagination="getBlacklistList"
        />
      </div>

      <div v-show="activeTab === 'complaint'">
        <el-form
          ref="complaintQueryForm"
          :model="complaintQueryParams"
          size="small"
          :inline="true"
          label-width="92px"
          class="query-form"
        >
          <el-form-item label="学校名称" prop="deptName">
            <el-input
              v-model="complaintQueryParams.deptName"
              placeholder="请输入学校名称"
              clearable
              @keyup.enter.native="handleComplaintQuery"
            />
          </el-form-item>
          <el-form-item label="区县" prop="region">
            <el-select v-model="complaintQueryParams.region" placeholder="请选择区县" clearable>
              <el-option
                v-for="item in shijiazhuangDistricts"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="反馈状态" prop="replyState">
            <el-select v-model="complaintQueryParams.replyState" placeholder="请选择反馈状态" clearable>
              <el-option label="未回复" value="0" />
              <el-option label="已回复" value="1" />
            </el-select>
          </el-form-item>
          <el-form-item label="反馈人" prop="userName">
            <el-input
              v-model="complaintQueryParams.userName"
              placeholder="请输入反馈人姓名"
              clearable
              @keyup.enter.native="handleComplaintQuery"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleComplaintQuery">
              搜索
            </el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetComplaintQuery">
              重置
            </el-button>
          </el-form-item>
        </el-form>

        <el-table v-loading="complaintLoading" :data="complaintRows" border>
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column label="区县" prop="region" width="120" align="center" show-overflow-tooltip />
          <el-table-column label="反馈人" prop="userName" width="120" align="center">
            <template slot-scope="scope">
              {{ scope.row.userName || "匿名用户" }}
            </template>
          </el-table-column>
          <el-table-column label="联系方式" prop="lxfs" width="140" align="center">
            <template slot-scope="scope">
              {{ scope.row.lxfs || "-" }}
            </template>
          </el-table-column>
          <el-table-column label="学校名称" prop="deptName" min-width="170" show-overflow-tooltip>
            <template slot-scope="scope">
              {{ scope.row.deptName || "-" }}
            </template>
          </el-table-column>
          <el-table-column label="反馈内容" prop="feedbackContent" min-width="260" show-overflow-tooltip />
          <el-table-column label="回复内容" prop="replyContent" min-width="220" show-overflow-tooltip>
            <template slot-scope="scope">
              {{ scope.row.replyContent || "-" }}
            </template>
          </el-table-column>
          <el-table-column label="留言时间" prop="createTime" width="170" align="center">
            <template slot-scope="scope">
              {{ parseTime(scope.row.createTime, "{y}-{m}-{d} {h}:{i}") || "-" }}
            </template>
          </el-table-column>
          <el-table-column label="回复时间" prop="replyTime" width="170" align="center">
            <template slot-scope="scope">
              {{ parseTime(scope.row.replyTime, "{y}-{m}-{d} {h}:{i}") || "-" }}
            </template>
          </el-table-column>
          <el-table-column label="反馈状态" prop="replyState" width="100" align="center">
            <template slot-scope="scope">
              <el-tag :type="String(scope.row.replyState) === '1' ? 'success' : 'danger'" size="mini">
                {{ String(scope.row.replyState) === "1" ? "已回复" : "未回复" }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" align="center" fixed="right">
            <template slot-scope="scope">
              <el-button type="text" icon="el-icon-view" @click="handleComplaintDetail(scope.row)">
                详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <pagination
          v-show="complaintTotal > 0"
          :total="complaintTotal"
          :page.sync="complaintQueryParams.pageNum"
          :limit.sync="complaintQueryParams.pageSize"
          @pagination="getComplaintList"
        />
      </div>
    </el-card>

    <el-dialog title="黑名单详情" :visible.sync="blacklistDetailOpen" width="720px" append-to-body>
      <div v-loading="blacklistDetailLoading">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="厂商名称">{{ blacklistDetail.name || "-" }}</el-descriptions-item>
          <el-descriptions-item label="企业信用代码">{{ blacklistDetail.xydm || "-" }}</el-descriptions-item>
          <el-descriptions-item label="法定代表人">{{ blacklistDetail.userName || "-" }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ parseTime(blacklistDetail.createTime, "{y}-{m}-{d} {h}:{i}:{s}") || "-" }}
          </el-descriptions-item>
          <el-descriptions-item label="拉黑原因" :span="2">
            {{ blacklistDetail.remark || "-" }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="blacklistDetailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <el-dialog title="投诉详情" :visible.sync="complaintDetailOpen" width="820px" append-to-body>
      <div v-loading="complaintDetailLoading">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="反馈人">{{ complaintDetail.userName || "匿名用户" }}</el-descriptions-item>
          <el-descriptions-item label="联系方式">{{ complaintDetail.lxfs || "-" }}</el-descriptions-item>
          <el-descriptions-item label="区县">{{ complaintDetail.region || "-" }}</el-descriptions-item>
          <el-descriptions-item label="学校名称">{{ complaintDetail.deptName || "-" }}</el-descriptions-item>
          <el-descriptions-item label="反馈状态">
            {{ String(complaintDetail.replyState) === "1" ? "已回复" : "未回复" }}
          </el-descriptions-item>
          <el-descriptions-item label="是否公开">
            {{ String(complaintDetail.isPublic) === "1" ? "公开" : "不公开" }}
          </el-descriptions-item>
          <el-descriptions-item label="留言时间">
            {{ parseTime(complaintDetail.createTime, "{y}-{m}-{d} {h}:{i}:{s}") || "-" }}
          </el-descriptions-item>
          <el-descriptions-item label="回复时间">
            {{ parseTime(complaintDetail.replyTime, "{y}-{m}-{d} {h}:{i}:{s}") || "-" }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="section-block">
          <div class="section-title">反馈内容</div>
          <div class="plain-box">{{ complaintDetail.feedbackContent || "暂无反馈内容" }}</div>
        </div>

        <div class="section-block">
          <div class="section-title">回复内容</div>
          <div class="plain-box">{{ complaintDetail.replyContent || "暂无回复内容" }}</div>
        </div>

        <div v-if="complaintFiles.length" class="section-block">
          <div class="section-title">反馈附件</div>
          <div class="file-list">
            <el-link
              v-for="(item, index) in complaintFiles"
              :key="`${item}-${index}`"
              type="primary"
              icon="el-icon-link"
              :underline="false"
              @click="openFile(item)"
            >
              附件{{ index + 1 }}
            </el-link>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="complaintDetailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getHmd, listHmd } from "@/api/school/hmd";
import { getParentsFeedback, listParentsFeedback } from "@/api/opinion";
import { shijiazhuangDistricts } from "@/utils/region";

const defaultBlacklistDetail = () => ({
  id: undefined,
  name: "",
  xydm: "",
  userName: "",
  remark: "",
  createTime: undefined,
});

const defaultComplaintDetail = () => ({
  id: undefined,
  userName: "",
  lxfs: "",
  region: "",
  deptName: "",
  feedbackContent: "",
  feedbackUrl: "",
  replyContent: "",
  replyState: "0",
  isPublic: "0",
  createTime: undefined,
  replyTime: undefined,
});

export default {
  name: "CitySupplierSupervision",
  data() {
    return {
      activeTab: "blacklist",
      shijiazhuangDistricts,
      blacklistLoading: false,
      complaintLoading: false,
      blacklistDetailLoading: false,
      complaintDetailLoading: false,
      blacklistDetailOpen: false,
      complaintDetailOpen: false,
      blacklistRows: [],
      complaintRows: [],
      blacklistTotal: 0,
      complaintTotal: 0,
      blacklistQueryParams: {
        pageNum: 1,
        pageSize: 10,
        name: "",
        xydm: "",
        userName: "",
      },
      complaintQueryParams: {
        pageNum: 1,
        pageSize: 10,
        deptName: "",
        region: "",
        replyState: "",
        userName: "",
      },
      blacklistDetail: defaultBlacklistDetail(),
      complaintDetail: defaultComplaintDetail(),
    };
  },
  computed: {
    complaintFiles() {
      return this.parseFiles(this.complaintDetail.feedbackUrl);
    },
  },
  created() {
    this.getBlacklistList();
  },
  methods: {
    handleTabChange() {
      if (this.activeTab === "blacklist" && !this.blacklistRows.length) {
        this.getBlacklistList();
      }
      if (this.activeTab === "complaint" && !this.complaintRows.length) {
        this.getComplaintList();
      }
    },
    refreshCurrentTab() {
      if (this.activeTab === "blacklist") {
        this.getBlacklistList();
        return;
      }
      this.getComplaintList();
    },
    getBlacklistList() {
      this.blacklistLoading = true;
      listHmd(this.blacklistQueryParams)
        .then((response) => {
          this.blacklistRows = response.rows || [];
          this.blacklistTotal = response.total || 0;
        })
        .finally(() => {
          this.blacklistLoading = false;
        });
    },
    getComplaintList() {
      this.complaintLoading = true;
      listParentsFeedback(this.complaintQueryParams)
        .then((response) => {
          this.complaintRows = response.rows || [];
          this.complaintTotal = response.total || 0;
        })
        .finally(() => {
          this.complaintLoading = false;
        });
    },
    handleBlacklistQuery() {
      this.blacklistQueryParams.pageNum = 1;
      this.getBlacklistList();
    },
    handleComplaintQuery() {
      this.complaintQueryParams.pageNum = 1;
      this.getComplaintList();
    },
    resetBlacklistQuery() {
      this.resetForm("blacklistQueryForm");
      this.handleBlacklistQuery();
    },
    resetComplaintQuery() {
      this.resetForm("complaintQueryForm");
      this.handleComplaintQuery();
    },
    handleBlacklistDetail(row) {
      this.blacklistDetailOpen = true;
      this.blacklistDetailLoading = true;
      getHmd(row.id)
        .then((response) => {
          this.blacklistDetail = {
            ...defaultBlacklistDetail(),
            ...row,
            ...(response.data || {}),
          };
        })
        .finally(() => {
          this.blacklistDetailLoading = false;
        });
    },
    handleComplaintDetail(row) {
      this.complaintDetailOpen = true;
      this.complaintDetailLoading = true;
      getParentsFeedback(row.id)
        .then((response) => {
          this.complaintDetail = {
            ...defaultComplaintDetail(),
            ...row,
            ...(response.data || {}),
          };
        })
        .finally(() => {
          this.complaintDetailLoading = false;
        });
    },
    parseFiles(value) {
      if (!value) return [];
      if (Array.isArray(value)) return value.filter(Boolean);
      const raw = String(value).trim();
      if (!raw) return [];
      try {
        const parsed = JSON.parse(raw);
        if (Array.isArray(parsed)) {
          return parsed
            .map((item) => {
              if (typeof item === "string") return item;
              if (item && typeof item === "object") return item.url || item.name || "";
              return "";
            })
            .filter(Boolean);
        }
      } catch (error) {
        // Some legacy rows store file paths as comma-separated text.
      }
      return raw
        .split(",")
        .map((item) => item.trim())
        .filter(Boolean);
    },
    buildFileUrl(path) {
      if (!path) return "";
      if (/^https?:\/\//.test(path)) return path;
      return `${process.env.VUE_APP_IMAGE_BASE_API || ""}${path}`;
    },
    openFile(path) {
      const url = this.buildFileUrl(path);
      if (url) {
        window.open(url, "_blank");
      }
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

.section-block {
  margin-top: 20px;
}

.section-title {
  margin-bottom: 12px;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.plain-box {
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  background: #fafafa;
  color: #606266;
  line-height: 1.8;
  word-break: break-all;
}

.file-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
</style>
