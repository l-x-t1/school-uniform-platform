<template>
  <div class="app-container supplier-message-page">
    <el-card shadow="never" class="search-card">
      <el-form
        ref="queryForm"
        :model="queryParams"
        size="small"
        :inline="true"
        label-width="92px"
      >
        <el-form-item label="供应商名称" prop="supplierName">
          <el-input
            v-model="queryParams.supplierName"
            placeholder="请输入供应商名称"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="联系电话" prop="supplierPhone">
          <el-input
            v-model="queryParams.supplierPhone"
            placeholder="请输入联系电话"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="法定代表人" prop="legalRepresentative">
          <el-input
            v-model="queryParams.legalRepresentative"
            placeholder="请输入法定代表人"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">
            搜索
          </el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <div class="table-header">
        <div class="table-title">供应商信息</div>
        <el-button type="primary" plain icon="el-icon-refresh" size="mini" @click="getList">
          刷新
        </el-button>
      </div>

      <el-table v-loading="loading" :data="supplierMessageList" border>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="供应商名称" prop="supplierName" min-width="180" show-overflow-tooltip />
        <el-table-column label="联系电话" prop="supplierPhone" width="130" />
        <el-table-column label="法定代表人" prop="legalRepresentative" width="140" />
        <el-table-column label="企业类型" prop="supplierType" width="140" show-overflow-tooltip />
        <el-table-column label="职工人数" prop="staffNumber" width="100" align="center" />
        <el-table-column label="注册资金(万)" prop="registeredCapital" width="120" align="center" />
        <el-table-column label="成立日期" prop="establishedTime" width="120" align="center">
          <template slot-scope="scope">
            {{ parseTime(scope.row.establishedTime, "{y}-{m}-{d}") || "-" }}
          </template>
        </el-table-column>
        <el-table-column label="厂商地址" prop="supplierAddress" min-width="220" show-overflow-tooltip />
        <el-table-column label="黑名单" width="90" align="center">
          <template slot-scope="scope">
            <el-tag :type="String(scope.row.blacklistStatus) === '1' ? 'danger' : 'success'" size="mini">
              {{ String(scope.row.blacklistStatus) === "1" ? "是" : "否" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" icon="el-icon-view" @click="handleView(scope.row)">
              查看
            </el-button>
            <el-button type="text" icon="el-icon-edit" @click="handleEdit(scope.row)">
              编辑
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
    </el-card>

    <el-dialog title="查看供应商信息" :visible.sync="detailOpen" width="980px" append-to-body>
      <div v-loading="detailLoading">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="供应商名称">
            {{ detailForm.supplierName || "-" }}
          </el-descriptions-item>
          <el-descriptions-item label="联系电话">
            {{ detailForm.supplierPhone || "-" }}
          </el-descriptions-item>
          <el-descriptions-item label="法定代表人">
            {{ detailForm.legalRepresentative || "-" }}
          </el-descriptions-item>
          <el-descriptions-item label="企业类型">
            {{ detailForm.supplierType || "-" }}
          </el-descriptions-item>
          <el-descriptions-item label="职工人数">
            {{ detailForm.staffNumber || "-" }}
          </el-descriptions-item>
          <el-descriptions-item label="注册资金(万)">
            {{ detailForm.registeredCapital || "-" }}
          </el-descriptions-item>
          <el-descriptions-item label="成立日期">
            {{ parseTime(detailForm.establishedTime, "{y}-{m}-{d}") || "-" }}
          </el-descriptions-item>
          <el-descriptions-item label="黑名单状态">
            {{ String(detailForm.blacklistStatus) === "1" ? "是" : "否" }}
          </el-descriptions-item>
          <el-descriptions-item label="厂商地址" :span="2">
            {{ detailForm.supplierAddress || "-" }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="section-block">
          <div class="section-title">厂商介绍</div>
          <div class="rich-content" v-html="detailForm.supplierIntroduce || '<p>暂无介绍</p>'"></div>
        </div>

        <div class="section-block">
          <div class="section-title">厂商资质</div>
          <FileUpload v-model="detailForm.businessLicense" :disabled="true" :limit="1" />
        </div>

        <div class="section-block">
          <div class="section-title">信用评级</div>
          <FileUpload v-if="detailForm.xypj" v-model="detailForm.xypj" :disabled="true" :limit="1" />
          <div v-else class="empty-tip">暂无信用评级</div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <el-dialog title="编辑供应商信息" :visible.sync="editOpen" width="980px" append-to-body @closed="editTab = 'info'">
      <el-tabs v-model="editTab">
        <el-tab-pane label="基本信息" name="info">
          <el-form ref="editForm" :model="editForm" :rules="rules" label-width="110px">
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="供应商名称" prop="supplierName">
                  <el-input v-model="editForm.supplierName" placeholder="请输入供应商名称" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="联系电话" prop="supplierPhone">
                  <el-input v-model="editForm.supplierPhone" placeholder="请输入联系电话" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="法定代表人" prop="legalRepresentative">
                  <el-input v-model="editForm.legalRepresentative" placeholder="请输入法定代表人" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="企业类型" prop="supplierType">
                  <el-input v-model="editForm.supplierType" placeholder="请输入企业类型" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="职工人数" prop="staffNumber">
                  <el-input v-model="editForm.staffNumber" placeholder="请输入职工人数" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="注册资金(万)" prop="registeredCapital">
                  <el-input v-model="editForm.registeredCapital" placeholder="请输入注册资金" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="成立日期" prop="establishedTime">
                  <el-date-picker v-model="editForm.establishedTime" type="date" value-format="yyyy-MM-dd" placeholder="请选择成立日期" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="厂商地址" prop="supplierAddress">
                  <el-input v-model="editForm.supplierAddress" placeholder="请输入厂商地址" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="厂商介绍" prop="supplierIntroduce">
              <Editor v-model="editForm.supplierIntroduce" :min-height="180" />
            </el-form-item>
            <el-form-item label="厂商资质" prop="businessLicense">
              <FileUpload v-model="editForm.businessLicense" :limit="1" :file-type="['jpg', 'png', 'jpeg', 'pdf']" />
            </el-form-item>
            <el-form-item label="信用评级" prop="xypj">
              <FileUpload v-model="editForm.xypj" :limit="1" :file-type="['jpg', 'png', 'jpeg', 'pdf']" />
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="用户管理" name="users">
          <div class="user-tab-content">
            <div class="user-tab-header">
              <span class="tab-hint">已绑定 {{ boundUsers.length }} 个用户</span>
              <el-button type="primary" size="small" icon="el-icon-plus" @click="handleBindUserOpen">绑定用户</el-button>
            </div>
            <el-table :data="boundUsers" border size="small" v-loading="userLoading">
              <el-table-column type="index" label="序号" width="50" align="center" />
              <el-table-column label="用户名" prop="userName" min-width="100" />
              <el-table-column label="昵称" prop="nickName" min-width="120" />
              <el-table-column label="手机号" prop="phonenumber" width="130" />
              <el-table-column label="操作" width="80" align="center">
                <template slot-scope="scope">
                  <el-button type="text" style="color: #f56c6c" @click="handleUnbindUser(scope.row)">解绑</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>
      </el-tabs>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editOpen = false">取 消</el-button>
        <el-button v-if="editTab === 'info'" type="primary" :loading="submitLoading" @click="submitEdit">保 存</el-button>
      </div>
    </el-dialog>

    <!-- 绑定用户选择弹窗 -->
    <el-dialog title="选择用户" :visible.sync="bindUserOpen" width="580px" append-to-body>
      <el-form :inline="true" size="small">
        <el-form-item>
          <el-input v-model="userSearchKeyword" placeholder="搜索用户名/昵称/手机号" clearable @keyup.enter.native="searchAvailableUsers" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="searchAvailableUsers">搜索</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="availableUserList" border size="small" v-loading="userSearchLoading" max-height="360">
        <el-table-column type="index" label="序号" width="50" align="center" />
        <el-table-column label="用户名" prop="userName" min-width="100" />
        <el-table-column label="昵称" prop="nickName" min-width="120" />
        <el-table-column label="手机号" prop="phonenumber" width="130" />
        <el-table-column label="操作" width="70" align="center">
          <template slot-scope="scope">
            <el-button type="text" style="color: #67c23a" :loading="scope.row._binding" @click="handleBindUser(scope.row)">绑定</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div slot="footer">
        <el-button @click="bindUserOpen = false">关 闭</el-button>
      </div>
    </el-dialog>>
  </div>
</template>

<script>
import {
  getSupplierMessage,
  listSupplierMessage,
  updateSupplierMessage,
} from "@/api/school/supplierMessage";

const createDefaultDetailForm = () => ({
  id: undefined,
  supplierName: "",
  supplierPhone: "",
  supplierType: "",
  legalRepresentative: "",
  staffNumber: "",
  establishedTime: "",
  supplierAddress: "",
  supplierIntroduce: "",
  businessLicense: "",
  registeredCapital: "",
  xypj: "",
  blacklistStatus: "0",
});

export default {
  name: "SchoolSupplierMessage",
  data() {
    return {
      loading: false,
      detailLoading: false,
      submitLoading: false,
      editingSupplierId: null,
      editTab: 'info',
      userLoading: false,
      boundUsers: [],
      bindUserOpen: false,
      userSearchKeyword: '',
      userSearchLoading: false,
      availableUserList: [],
      detailOpen: false,
      editOpen: false,
      total: 0,
      supplierMessageList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        supplierName: "",
        supplierPhone: "",
        legalRepresentative: "",
      },
      detailForm: createDefaultDetailForm(),
      editForm: createDefaultDetailForm(),
      rules: {
        supplierName: [
          { required: true, message: "请输入供应商名称", trigger: "blur" },
        ],
        supplierPhone: [
          { required: true, message: "请输入联系电话", trigger: "blur" },
          {
            pattern: /^1[3-9]\d{9}$/,
            message: "请输入正确的手机号",
            trigger: "blur",
          },
        ],
        legalRepresentative: [
          { required: true, message: "请输入法定代表人", trigger: "blur" },
        ],
        supplierType: [
          { required: true, message: "请输入企业类型", trigger: "blur" },
        ],
        staffNumber: [
          { required: true, message: "请输入职工人数", trigger: "blur" },
        ],
        establishedTime: [
          { required: true, message: "请选择成立日期", trigger: "change" },
        ],
        supplierAddress: [
          { required: true, message: "请输入厂商地址", trigger: "blur" },
        ],
        registeredCapital: [
          { required: true, message: "请输入注册资金", trigger: "blur" },
        ],
        supplierIntroduce: [
          { required: true, message: "请输入厂商介绍", trigger: "blur" },
        ],
        businessLicense: [
          { required: true, message: "请上传厂商资质", trigger: "change" },
        ],
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.loading = true;
      listSupplierMessage(this.queryParams)
        .then((response) => {
          this.supplierMessageList = response.rows || [];
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
      this.detailOpen = true;
      this.detailLoading = true;
      getSupplierMessage(row.id)
        .then((response) => {
          this.detailForm = {
            ...createDefaultDetailForm(),
            ...(response.data || {}),
          };
        })
        .finally(() => {
          this.detailLoading = false;
        });
    },
    handleEdit(row) {
      this.submitLoading = false;
      this.editTab = 'info';
      this.editingSupplierId = row.id;
      getSupplierMessage(row.id).then((response) => {
        this.editForm = {
          ...createDefaultDetailForm(),
          ...(response.data || {}),
        };
        this.editOpen = true;
        this.loadBoundUsers();
        this.$nextTick(() => {
          if (this.$refs.editForm) {
            this.$refs.editForm.clearValidate();
          }
        });
      });
    },
    submitEdit() {
      this.$refs.editForm.validate((valid) => {
        if (!valid) {
          return;
        }
        this.submitLoading = true;
        updateSupplierMessage(this.editForm)
          .then(() => {
            this.$modal.msgSuccess("保存成功");
            this.editOpen = false;
            this.getList();
          })
          .finally(() => {
            this.submitLoading = false;
          });
      });
    },
    loadBoundUsers() {
      if (!this.editingSupplierId) return;
      this.userLoading = true;
      getSupplierUsers(this.editingSupplierId)
        .then((response) => { this.boundUsers = response.data || []; })
        .finally(() => { this.userLoading = false; });
    },
    handleBindUserOpen() {
      this.userSearchKeyword = "";
      this.availableUserList = [];
      this.bindUserOpen = true;
      this.searchAvailableUsers();
    },
    searchAvailableUsers() {
      this.userSearchLoading = true;
      getAvailableUsers(this.userSearchKeyword || null)
        .then((response) => {
          this.availableUserList = (response.data || []).map((u) => ({ ...u, _binding: false }));
        })
        .finally(() => { this.userSearchLoading = false; });
    },
    handleBindUser(row) {
      this.$set(row, "_binding", true);
      bindUser({ supplierId: this.editingSupplierId, userId: row.userId })
        .then(() => {
          this.$message.success("绑定成功");
          this.loadBoundUsers();
          this.searchAvailableUsers();
        })
        .finally(() => { this.$set(row, "_binding", false); });
    },
    handleUnbindUser(row) {
      this.$modal.confirm("确认解除用户\u300c" + row.userName + "\u300d与当前厂商的绑定？")
        .then(() => unbindUser({ supplierId: this.editingSupplierId, userId: row.userId }))
        .then(() => { this.$message.success("解绑成功"); this.loadBoundUsers(); })
        .catch(() => {});
    },
  },
};
</script>

<style scoped>
.search-card {
  margin-bottom: 16px;
}

.table-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.table-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
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

.rich-content {
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  background: #fafafa;
  color: #606266;
  line-height: 1.8;
}

.empty-tip {
  padding: 12px 0;
  color: #909399;
  font-size: 14px;
}

.user-tab-content {
  min-height: 260px;
}

.user-tab-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.tab-hint {
  font-size: 13px;
  color: #909399;
}
</style>
