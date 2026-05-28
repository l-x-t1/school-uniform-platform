<template>
  <div class="app-container account-balance-page">
    <el-row :gutter="16" class="summary-row">
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card blue">
          <div class="summary-label">收入金额</div>
          <div class="summary-value">¥{{ amountText(summary.incomeAmount) }}</div>
          <div class="summary-extra">收入笔数 {{ summary.incomeCount }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card green">
          <div class="summary-label">支出金额</div>
          <div class="summary-value">¥{{ amountText(summary.outcomeAmount) }}</div>
          <div class="summary-extra">支出笔数 {{ summary.outcomeCount }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card orange">
          <div class="summary-label">可提现金额</div>
          <div class="summary-value">¥{{ amountText(summary.withdrawableAmount) }}</div>
          <div class="summary-extra">可提现流水 {{ availableFinanceList.length }} 条</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card red">
          <div class="summary-label">待提现金额</div>
          <div class="summary-value">¥{{ amountText(summary.pendingWithdrawAmount) }}</div>
          <div class="summary-extra">待处理记录 {{ summary.pendingWithdrawCount }} 条</div>
        </div>
      </el-col>
    </el-row>

    <el-tabs v-model="activeTab" class="account-tabs">
      <el-tab-pane label="资金流水" name="finance">
        <el-form ref="financeQueryForm" :model="financeQueryParams" size="small" :inline="true" label-width="90px">
          <el-form-item label="流水单号" prop="flowNo">
            <el-input v-model="financeQueryParams.flowNo" placeholder="请输入流水单号" clearable @keyup.enter.native="handleFinanceQuery" />
          </el-form-item>
          <el-form-item label="供应商ID" prop="supplierId">
            <el-input v-model="financeQueryParams.supplierId" placeholder="请输入供应商ID" clearable @keyup.enter.native="handleFinanceQuery" />
          </el-form-item>
          <el-form-item label="用户ID" prop="userId">
            <el-input v-model="financeQueryParams.userId" placeholder="请输入用户ID" clearable @keyup.enter.native="handleFinanceQuery" />
          </el-form-item>
          <el-form-item label="收款方名称" prop="payeeName">
            <el-input v-model="financeQueryParams.payeeName" placeholder="请输入收款方名称" clearable @keyup.enter.native="handleFinanceQuery" />
          </el-form-item>
          <el-form-item label="流水方向" prop="flowDirection">
            <el-select v-model="financeQueryParams.flowDirection" placeholder="请选择流水方向" clearable>
              <el-option label="收入" value="1" />
              <el-option label="支出" value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="financeQueryParams.status" placeholder="请选择状态" clearable>
              <el-option label="未支付" value="0" />
              <el-option label="到账学校" value="1" />
              <el-option label="到账供应商" value="2" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleFinanceQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetFinanceQuery">重置</el-button>
            <el-button
              v-hasPermi="['school:financeFlow:export']"
              type="warning"
              plain
              icon="el-icon-download"
              size="mini"
              @click="exportFinanceList"
            >
              导出
            </el-button>
          </el-form-item>
        </el-form>

        <el-table v-loading="financeLoading" :data="financeFlowList" border>
          <el-table-column label="流水单号" prop="flowNo" min-width="180" show-overflow-tooltip />
          <el-table-column label="业务类型" min-width="100">
            <template slot-scope="scope">{{ businessTypeText(scope.row.businessType) }}</template>
          </el-table-column>
          <el-table-column label="流水方向" min-width="90">
            <template slot-scope="scope">{{ flowDirectionText(scope.row.flowDirection) }}</template>
          </el-table-column>
          <el-table-column label="订单编号" prop="orderNo" min-width="180" show-overflow-tooltip />
          <el-table-column label="供应商ID" prop="supplierId" width="100" />
          <el-table-column label="用户ID" prop="userId" width="110" />
          <el-table-column label="金额" width="110">
            <template slot-scope="scope">¥{{ amountText(scope.row.amount) }}</template>
          </el-table-column>
          <el-table-column label="支付渠道" prop="payChannel" min-width="120" show-overflow-tooltip />
          <el-table-column label="收款方" prop="payeeName" min-width="140" show-overflow-tooltip />
          <el-table-column label="状态" min-width="120">
            <template slot-scope="scope">{{ flowStatusText(scope.row.status) }}</template>
          </el-table-column>
          <el-table-column label="交易时间" width="170">
            <template slot-scope="scope">{{ parseTime(scope.row.tradeTime, "{y}-{m}-{d} {h}:{i}:{s}") }}</template>
          </el-table-column>
          <el-table-column label="备注" prop="remark" min-width="160" show-overflow-tooltip />
        </el-table>

        <pagination
          v-show="financeTotal > 0"
          :total="financeTotal"
          :page.sync="financeQueryParams.pageNum"
          :limit.sync="financeQueryParams.pageSize"
          @pagination="getFinanceList"
        />
      </el-tab-pane>

      <el-tab-pane label="可提现流水" name="withdrawable">
        <div class="toolbar-row">
          <el-alert title="仅支持对已到账供应商账户的收入流水发起提现申请。" type="info" :closable="false" show-icon />
          <div class="toolbar-actions">
            <el-button
              v-hasPermi="['school:withdrawApply:add']"
              type="primary"
              icon="el-icon-wallet"
              size="mini"
              @click="openWithdrawDialog()"
            >
              发起提现
            </el-button>
          </div>
        </div>

        <el-table :data="availableFinanceList" border @selection-change="handleAvailableSelectionChange">
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column label="流水单号" prop="flowNo" min-width="180" show-overflow-tooltip />
          <el-table-column label="订单编号" prop="orderNo" min-width="180" show-overflow-tooltip />
          <el-table-column label="供应商ID" prop="supplierId" width="100" />
          <el-table-column label="收款方名称" prop="payeeName" min-width="140" show-overflow-tooltip />
          <el-table-column label="金额" width="120">
            <template slot-scope="scope">¥{{ amountText(scope.row.amount) }}</template>
          </el-table-column>
          <el-table-column label="状态" min-width="120">
            <template slot-scope="scope">{{ flowStatusText(scope.row.status) }}</template>
          </el-table-column>
          <el-table-column label="交易时间" width="170">
            <template slot-scope="scope">{{ parseTime(scope.row.tradeTime, "{y}-{m}-{d} {h}:{i}:{s}") }}</template>
          </el-table-column>
          <el-table-column label="操作" width="140" align="center">
            <template slot-scope="scope">
              <el-button
                v-hasPermi="['school:withdrawApply:add']"
                type="text"
                size="mini"
                @click="openWithdrawDialog(scope.row)"
              >
                发起提现
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane v-if="canViewWithdrawList" label="提现记录" name="withdrawRecord">
        <el-form ref="withdrawQueryForm" :model="withdrawQueryParams" size="small" :inline="true" label-width="90px">
          <el-form-item label="提现单号" prop="withdrawNo">
            <el-input v-model="withdrawQueryParams.withdrawNo" placeholder="请输入提现单号" clearable @keyup.enter.native="handleWithdrawQuery" />
          </el-form-item>
          <el-form-item label="供应商ID" prop="supplierId">
            <el-input v-model="withdrawQueryParams.supplierId" placeholder="请输入供应商ID" clearable @keyup.enter.native="handleWithdrawQuery" />
          </el-form-item>
          <el-form-item label="供应商名称" prop="supplierName">
            <el-input v-model="withdrawQueryParams.supplierName" placeholder="请输入供应商名称" clearable @keyup.enter.native="handleWithdrawQuery" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="withdrawQueryParams.status" placeholder="请选择状态" clearable>
              <el-option label="待审核" value="0" />
              <el-option label="审核通过" value="1" />
              <el-option label="已拒绝" value="2" />
              <el-option label="已打款" value="3" />
              <el-option label="已取消" value="4" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleWithdrawQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetWithdrawQuery">重置</el-button>
            <el-button
              v-hasPermi="['school:withdrawApply:export']"
              type="warning"
              plain
              icon="el-icon-download"
              size="mini"
              @click="exportWithdrawList"
            >
              导出
            </el-button>
          </el-form-item>
        </el-form>

        <el-table v-loading="withdrawLoading" :data="withdrawApplyList" border>
          <el-table-column label="提现单号" prop="withdrawNo" min-width="180" show-overflow-tooltip />
          <el-table-column label="供应商ID" prop="supplierId" width="100" />
          <el-table-column label="供应商名称" prop="supplierName" min-width="140" show-overflow-tooltip />
          <el-table-column label="申请金额" width="120">
            <template slot-scope="scope">¥{{ amountText(scope.row.applyAmount) }}</template>
          </el-table-column>
          <el-table-column label="状态" min-width="100">
            <template slot-scope="scope">{{ withdrawStatusText(scope.row.status) }}</template>
          </el-table-column>
          <el-table-column label="申请时间" width="120">
            <template slot-scope="scope">{{ parseTime(scope.row.applyTime, "{y}-{m}-{d}") }}</template>
          </el-table-column>
          <el-table-column label="审核人" prop="auditUserName" min-width="120" />
          <el-table-column label="关联资金流水ID" prop="financeFlowId" width="120" />
          <el-table-column label="审核备注" prop="auditRemark" min-width="160" show-overflow-tooltip />
        </el-table>

        <pagination
          v-show="withdrawTotal > 0"
          :total="withdrawTotal"
          :page.sync="withdrawQueryParams.pageNum"
          :limit.sync="withdrawQueryParams.pageSize"
          @pagination="getWithdrawList"
        />
      </el-tab-pane>
    </el-tabs>

    <el-dialog title="发起提现" :visible.sync="withdrawDialogOpen" width="560px" append-to-body>
      <el-form ref="withdrawForm" :model="withdrawForm" :rules="withdrawRules" label-width="110px">
        <el-form-item label="提现单号">
          <el-input v-model="withdrawForm.withdrawNo" disabled />
        </el-form-item>
        <el-form-item label="供应商ID" prop="supplierId">
          <el-input v-model="withdrawForm.supplierId" placeholder="请输入供应商ID" />
        </el-form-item>
        <el-form-item label="供应商名称" prop="supplierName">
          <el-input v-model="withdrawForm.supplierName" placeholder="请输入供应商名称" />
        </el-form-item>
        <el-form-item label="申请金额" prop="applyAmount">
          <el-input-number v-model="withdrawForm.applyAmount" :min="0.01" :precision="2" :step="100" />
        </el-form-item>
        <el-form-item label="关联资金流水ID" prop="financeFlowId">
          <el-input v-model="withdrawForm.financeFlowId" placeholder="选填" />
        </el-form-item>
        <el-form-item label="银行卡信息JSON" prop="bankInfoJson">
          <el-input
            v-model="withdrawForm.bankInfoJson"
            type="textarea"
            :rows="4"
            placeholder='请输入如 {"accountName":"张三","bankName":"建设银行","bankNo":"6222..."} 的JSON'
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="withdrawForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="withdrawDialogOpen = false">取消</el-button>
        <el-button type="primary" :loading="withdrawSubmitting" @click="submitWithdraw">提交</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import auth from "@/plugins/auth";
import {
  createAccountBalanceWithdraw,
  listAccountBalanceFinance,
  listAccountBalanceWithdraw,
} from "@/api/school/accountBalance";

export default {
  name: "AccountBalance",
  data() {
    return {
      activeTab: "finance",
      financeLoading: false,
      withdrawLoading: false,
      withdrawSubmitting: false,
      withdrawDialogOpen: false,
      financeTotal: 0,
      withdrawTotal: 0,
      financeFlowList: [],
      withdrawApplyList: [],
      selectedAvailableRows: [],
      financeQueryParams: {
        pageNum: 1,
        pageSize: 10,
        flowNo: undefined,
        supplierId: undefined,
        userId: undefined,
        payeeName: undefined,
        flowDirection: undefined,
        status: undefined,
      },
      withdrawQueryParams: {
        pageNum: 1,
        pageSize: 10,
        withdrawNo: undefined,
        supplierId: undefined,
        supplierName: undefined,
        status: undefined,
      },
      withdrawForm: {
        withdrawNo: "",
        supplierId: undefined,
        supplierName: "",
        applyAmount: undefined,
        financeFlowId: undefined,
        bankInfoJson: "",
        remark: "",
      },
      withdrawRules: {
        supplierId: [{ required: true, message: "请输入供应商ID", trigger: "blur" }],
        supplierName: [{ required: true, message: "请输入供应商名称", trigger: "blur" }],
        applyAmount: [{ required: true, message: "请输入申请金额", trigger: "change" }],
      },
    };
  },
  computed: {
    canViewWithdrawList() {
      return auth.hasPermi("school:withdrawApply:list");
    },
    canCreateWithdraw() {
      return auth.hasPermi("school:withdrawApply:add");
    },
    availableFinanceList() {
      return this.financeFlowList.filter(this.isWithdrawableFlow);
    },
    summary() {
      const financeRows = this.financeFlowList || [];
      const withdrawRows = this.withdrawApplyList || [];
      const incomeRows = financeRows.filter((item) => String(item.flowDirection) === "1");
      const outcomeRows = financeRows.filter((item) => String(item.flowDirection) === "2");
      const pendingRows = withdrawRows.filter((item) => String(item.status) === "0");
      return {
        incomeAmount: this.sumAmount(incomeRows, "amount"),
        incomeCount: incomeRows.length,
        outcomeAmount: this.sumAmount(outcomeRows, "amount"),
        outcomeCount: outcomeRows.length,
        withdrawableAmount: this.sumAmount(this.availableFinanceList, "amount"),
        pendingWithdrawAmount: this.sumAmount(pendingRows, "applyAmount"),
        pendingWithdrawCount: pendingRows.length,
      };
    },
  },
  created() {
    this.getFinanceList();
    if (this.canViewWithdrawList) {
      this.getWithdrawList();
    }
  },
  methods: {
    getFinanceList() {
      this.financeLoading = true;
      listAccountBalanceFinance(this.financeQueryParams)
        .then((response) => {
          this.financeFlowList = response.rows || [];
          this.financeTotal = response.total || 0;
        })
        .finally(() => {
          this.financeLoading = false;
        });
    },
    getWithdrawList() {
      if (!this.canViewWithdrawList) {
        this.withdrawApplyList = [];
        this.withdrawTotal = 0;
        return;
      }
      this.withdrawLoading = true;
      listAccountBalanceWithdraw(this.withdrawQueryParams)
        .then((response) => {
          this.withdrawApplyList = response.rows || [];
          this.withdrawTotal = response.total || 0;
        })
        .finally(() => {
          this.withdrawLoading = false;
        });
    },
    handleFinanceQuery() {
      this.financeQueryParams.pageNum = 1;
      this.getFinanceList();
    },
    handleWithdrawQuery() {
      if (!this.canViewWithdrawList) {
        return;
      }
      this.withdrawQueryParams.pageNum = 1;
      this.getWithdrawList();
    },
    resetFinanceQuery() {
      this.resetForm("financeQueryForm");
      this.handleFinanceQuery();
    },
    resetWithdrawQuery() {
      if (!this.canViewWithdrawList) {
        return;
      }
      this.resetForm("withdrawQueryForm");
      this.handleWithdrawQuery();
    },
    exportFinanceList() {
      this.download("school/financeFlow/export", { ...this.financeQueryParams }, `financeFlow_${new Date().getTime()}.xlsx`);
    },
    exportWithdrawList() {
      if (!this.canViewWithdrawList) {
        return;
      }
      this.download("school/withdrawApply/export", { ...this.withdrawQueryParams }, `withdrawApply_${new Date().getTime()}.xlsx`);
    },
    handleAvailableSelectionChange(selection) {
      this.selectedAvailableRows = selection;
    },
    isWithdrawableFlow(row) {
      return String(row.flowDirection) === "1" && String(row.status) === "2" && (String(row.payeeType) === "2" || !!row.supplierId);
    },
    openWithdrawDialog(row) {
      if (!this.canCreateWithdraw) {
        return;
      }
      const rows = row ? [row] : this.selectedAvailableRows;
      const firstRow = rows[0] || {};
      this.withdrawForm = {
        withdrawNo: this.generateWithdrawNo(),
        supplierId: firstRow.supplierId,
        supplierName: firstRow.payeeName || firstRow.supplierName || "",
        applyAmount: rows.length ? this.sumAmount(rows, "amount") : undefined,
        financeFlowId: firstRow.id,
        bankInfoJson: "",
        remark: rows.length > 1 ? `关联 ${rows.length} 条资金流水` : "",
      };
      this.withdrawDialogOpen = true;
      this.$nextTick(() => {
        if (this.$refs.withdrawForm) {
          this.$refs.withdrawForm.clearValidate();
        }
      });
    },
    submitWithdraw() {
      if (!this.canCreateWithdraw) {
        return;
      }
      this.$refs.withdrawForm.validate((valid) => {
        if (!valid) return;
        const submitData = {
          ...this.withdrawForm,
          withdrawNo: this.withdrawForm.withdrawNo || this.generateWithdrawNo(),
          status: "0",
          applyTime: this.parseTime(new Date(), "{y}-{m}-{d}"),
        };
        this.withdrawSubmitting = true;
        createAccountBalanceWithdraw(submitData)
          .then(() => {
            this.$modal.msgSuccess("提交成功");
            this.withdrawDialogOpen = false;
            this.getWithdrawList();
          })
          .finally(() => {
            this.withdrawSubmitting = false;
          });
      });
    },
    sumAmount(list, field) {
      return (list || []).reduce((sum, item) => sum + Number(item[field] || 0), 0);
    },
    amountText(value) {
      return Number(value || 0).toFixed(2);
    },
    businessTypeText(value) {
      const map = { 1: "支付", 2: "退款", 3: "提现", 4: "结算" };
      return map[String(value)] || value || "-";
    },
    flowDirectionText(value) {
      const map = { 1: "收入", 2: "支出" };
      return map[String(value)] || value || "-";
    },
    flowStatusText(value) {
      const map = { 0: "未支付", 1: "到账学校", 2: "到账供应商" };
      return map[String(value)] || value || "-";
    },
    withdrawStatusText(value) {
      const map = { 0: "待审核", 1: "审核通过", 2: "已拒绝", 3: "已打款", 4: "已取消" };
      return map[String(value)] || value || "-";
    },
    generateWithdrawNo() {
      const now = new Date();
      const pad = (num) => String(num).padStart(2, "0");
      return `WD${now.getFullYear()}${pad(now.getMonth() + 1)}${pad(now.getDate())}${pad(now.getHours())}${pad(now.getMinutes())}${pad(now.getSeconds())}`;
    },
  },
};
</script>

<style scoped>
.summary-row {
  margin-bottom: 20px;
}

.summary-card {
  border-radius: 12px;
  padding: 18px 20px;
  color: #fff;
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.08);
  margin-bottom: 16px;
}

.summary-card.blue {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
}

.summary-card.green {
  background: linear-gradient(135deg, #22c55e, #16a34a);
}

.summary-card.orange {
  background: linear-gradient(135deg, #f59e0b, #ea580c);
}

.summary-card.red {
  background: linear-gradient(135deg, #ef4444, #dc2626);
}

.summary-label {
  font-size: 14px;
  opacity: 0.9;
}

.summary-value {
  font-size: 28px;
  font-weight: 700;
  margin: 10px 0 6px;
}

.summary-extra {
  font-size: 13px;
  opacity: 0.9;
}

.account-tabs {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
}

.toolbar-row {
  margin-bottom: 16px;
}

.toolbar-actions {
  margin-top: 12px;
  text-align: right;
}
</style>
