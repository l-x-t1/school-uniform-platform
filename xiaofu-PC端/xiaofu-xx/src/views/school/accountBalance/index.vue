<template>
  <div class="app-container account-balance-page">
    <el-row :gutter="16" class="summary-row">
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card blue">
          <div class="summary-label">Income Amount</div>
          <div class="summary-value">¥{{ amountText(summary.incomeAmount) }}</div>
          <div class="summary-extra">Income Count {{ summary.incomeCount }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card green">
          <div class="summary-label">Outcome Amount</div>
          <div class="summary-value">¥{{ amountText(summary.outcomeAmount) }}</div>
          <div class="summary-extra">Outcome Count {{ summary.outcomeCount }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card orange">
          <div class="summary-label">Withdrawable Amount</div>
          <div class="summary-value">¥{{ amountText(summary.withdrawableAmount) }}</div>
          <div class="summary-extra">Withdrawable Rows {{ availableFinanceList.length }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card red">
          <div class="summary-label">Pending Withdraw Amount</div>
          <div class="summary-value">¥{{ amountText(summary.pendingWithdrawAmount) }}</div>
          <div class="summary-extra">Pending Rows {{ summary.pendingWithdrawCount }}</div>
        </div>
      </el-col>
    </el-row>

    <el-tabs v-model="activeTab" class="account-tabs">
      <el-tab-pane label="Finance Flow" name="finance">
        <el-form ref="financeQueryForm" :model="financeQueryParams" size="small" :inline="true" label-width="90px">
          <el-form-item label="Flow No" prop="flowNo">
            <el-input v-model="financeQueryParams.flowNo" placeholder="Input flow no" clearable @keyup.enter.native="handleFinanceQuery" />
          </el-form-item>
          <el-form-item label="Supplier ID" prop="supplierId">
            <el-input v-model="financeQueryParams.supplierId" placeholder="Input supplier id" clearable @keyup.enter.native="handleFinanceQuery" />
          </el-form-item>
          <el-form-item label="User ID" prop="userId">
            <el-input v-model="financeQueryParams.userId" placeholder="Input user id" clearable @keyup.enter.native="handleFinanceQuery" />
          </el-form-item>
          <el-form-item label="Payee Name" prop="payeeName">
            <el-input v-model="financeQueryParams.payeeName" placeholder="Input payee name" clearable @keyup.enter.native="handleFinanceQuery" />
          </el-form-item>
          <el-form-item label="Direction" prop="flowDirection">
            <el-select v-model="financeQueryParams.flowDirection" placeholder="Select" clearable>
              <el-option label="Income" value="1" />
              <el-option label="Outcome" value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="Status" prop="status">
            <el-select v-model="financeQueryParams.status" placeholder="Select" clearable>
              <el-option label="Unpaid" value="0" />
              <el-option label="Arrived School" value="1" />
              <el-option label="Arrived Supplier" value="2" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleFinanceQuery">Search</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetFinanceQuery">Reset</el-button>
            <el-button
              v-hasPermi="['school:financeFlow:export']"
              type="warning"
              plain
              icon="el-icon-download"
              size="mini"
              @click="exportFinanceList"
            >
              Export
            </el-button>
          </el-form-item>
        </el-form>

        <el-table v-loading="financeLoading" :data="financeFlowList" border>
          <el-table-column label="Flow No" prop="flowNo" min-width="180" show-overflow-tooltip />
          <el-table-column label="Biz Type" min-width="100">
            <template slot-scope="scope">{{ businessTypeText(scope.row.businessType) }}</template>
          </el-table-column>
          <el-table-column label="Direction" min-width="90">
            <template slot-scope="scope">{{ flowDirectionText(scope.row.flowDirection) }}</template>
          </el-table-column>
          <el-table-column label="Order No" prop="orderNo" min-width="180" show-overflow-tooltip />
          <el-table-column label="Supplier ID" prop="supplierId" width="100" />
          <el-table-column label="User ID" prop="userId" width="110" />
          <el-table-column label="Amount" width="110">
            <template slot-scope="scope">¥{{ amountText(scope.row.amount) }}</template>
          </el-table-column>
          <el-table-column label="Pay Channel" prop="payChannel" min-width="120" show-overflow-tooltip />
          <el-table-column label="Payee" prop="payeeName" min-width="140" show-overflow-tooltip />
          <el-table-column label="Status" min-width="120">
            <template slot-scope="scope">{{ flowStatusText(scope.row.status) }}</template>
          </el-table-column>
          <el-table-column label="Trade Time" width="170">
            <template slot-scope="scope">{{ parseTime(scope.row.tradeTime, "{y}-{m}-{d} {h}:{i}:{s}") }}</template>
          </el-table-column>
          <el-table-column label="Remark" prop="remark" min-width="160" show-overflow-tooltip />
        </el-table>

        <pagination
          v-show="financeTotal > 0"
          :total="financeTotal"
          :page.sync="financeQueryParams.pageNum"
          :limit.sync="financeQueryParams.pageSize"
          @pagination="getFinanceList"
        />
      </el-tab-pane>

      <el-tab-pane label="Withdrawable" name="withdrawable">
        <div class="toolbar-row">
          <el-alert title="Only income rows that arrived at supplier account can be withdrawn." type="info" :closable="false" show-icon />
          <div class="toolbar-actions">
            <el-button
              v-hasPermi="['school:withdrawApply:add']"
              type="primary"
              icon="el-icon-wallet"
              size="mini"
              @click="openWithdrawDialog()"
            >
              Create Withdraw
            </el-button>
          </div>
        </div>

        <el-table :data="availableFinanceList" border @selection-change="handleAvailableSelectionChange">
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column label="Flow No" prop="flowNo" min-width="180" show-overflow-tooltip />
          <el-table-column label="Order No" prop="orderNo" min-width="180" show-overflow-tooltip />
          <el-table-column label="Supplier ID" prop="supplierId" width="100" />
          <el-table-column label="Payee Name" prop="payeeName" min-width="140" show-overflow-tooltip />
          <el-table-column label="Amount" width="120">
            <template slot-scope="scope">¥{{ amountText(scope.row.amount) }}</template>
          </el-table-column>
          <el-table-column label="Status" min-width="120">
            <template slot-scope="scope">{{ flowStatusText(scope.row.status) }}</template>
          </el-table-column>
          <el-table-column label="Trade Time" width="170">
            <template slot-scope="scope">{{ parseTime(scope.row.tradeTime, "{y}-{m}-{d} {h}:{i}:{s}") }}</template>
          </el-table-column>
          <el-table-column label="Action" width="140" align="center">
            <template slot-scope="scope">
              <el-button
                v-hasPermi="['school:withdrawApply:add']"
                type="text"
                size="mini"
                @click="openWithdrawDialog(scope.row)"
              >
                Create Withdraw
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="Withdraw Records" name="withdrawRecord">
        <el-form ref="withdrawQueryForm" :model="withdrawQueryParams" size="small" :inline="true" label-width="90px">
          <el-form-item label="Withdraw No" prop="withdrawNo">
            <el-input v-model="withdrawQueryParams.withdrawNo" placeholder="Input withdraw no" clearable @keyup.enter.native="handleWithdrawQuery" />
          </el-form-item>
          <el-form-item label="Supplier ID" prop="supplierId">
            <el-input v-model="withdrawQueryParams.supplierId" placeholder="Input supplier id" clearable @keyup.enter.native="handleWithdrawQuery" />
          </el-form-item>
          <el-form-item label="Supplier Name" prop="supplierName">
            <el-input v-model="withdrawQueryParams.supplierName" placeholder="Input supplier name" clearable @keyup.enter.native="handleWithdrawQuery" />
          </el-form-item>
          <el-form-item label="Status" prop="status">
            <el-select v-model="withdrawQueryParams.status" placeholder="Select" clearable>
              <el-option label="Pending" value="0" />
              <el-option label="Approved" value="1" />
              <el-option label="Rejected" value="2" />
              <el-option label="Paid" value="3" />
              <el-option label="Canceled" value="4" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleWithdrawQuery">Search</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetWithdrawQuery">Reset</el-button>
            <el-button
              v-hasPermi="['school:withdrawApply:export']"
              type="warning"
              plain
              icon="el-icon-download"
              size="mini"
              @click="exportWithdrawList"
            >
              Export
            </el-button>
          </el-form-item>
        </el-form>

        <el-table v-loading="withdrawLoading" :data="withdrawApplyList" border>
          <el-table-column label="Withdraw No" prop="withdrawNo" min-width="180" show-overflow-tooltip />
          <el-table-column label="Supplier ID" prop="supplierId" width="100" />
          <el-table-column label="Supplier Name" prop="supplierName" min-width="140" show-overflow-tooltip />
          <el-table-column label="Apply Amount" width="120">
            <template slot-scope="scope">¥{{ amountText(scope.row.applyAmount) }}</template>
          </el-table-column>
          <el-table-column label="Status" min-width="100">
            <template slot-scope="scope">{{ withdrawStatusText(scope.row.status) }}</template>
          </el-table-column>
          <el-table-column label="Apply Time" width="120">
            <template slot-scope="scope">{{ parseTime(scope.row.applyTime, "{y}-{m}-{d}") }}</template>
          </el-table-column>
          <el-table-column label="Audit User" prop="auditUserName" min-width="120" />
          <el-table-column label="Finance Flow ID" prop="financeFlowId" width="120" />
          <el-table-column label="Audit Remark" prop="auditRemark" min-width="160" show-overflow-tooltip />
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

    <el-dialog title="Create Withdraw" :visible.sync="withdrawDialogOpen" width="560px" append-to-body>
      <el-form ref="withdrawForm" :model="withdrawForm" :rules="withdrawRules" label-width="110px">
        <el-form-item label="Withdraw No">
          <el-input v-model="withdrawForm.withdrawNo" disabled />
        </el-form-item>
        <el-form-item label="Supplier ID" prop="supplierId">
          <el-input v-model="withdrawForm.supplierId" placeholder="Input supplier id" />
        </el-form-item>
        <el-form-item label="Supplier Name" prop="supplierName">
          <el-input v-model="withdrawForm.supplierName" placeholder="Input supplier name" />
        </el-form-item>
        <el-form-item label="Apply Amount" prop="applyAmount">
          <el-input-number v-model="withdrawForm.applyAmount" :min="0.01" :precision="2" :step="100" />
        </el-form-item>
        <el-form-item label="Finance Flow ID" prop="financeFlowId">
          <el-input v-model="withdrawForm.financeFlowId" placeholder="Optional" />
        </el-form-item>
        <el-form-item label="Bank Info JSON" prop="bankInfoJson">
          <el-input
            v-model="withdrawForm.bankInfoJson"
            type="textarea"
            :rows="4"
            placeholder='JSON like {"accountName":"Zhang","bankName":"CCB","bankNo":"6222..."}'
          />
        </el-form-item>
        <el-form-item label="Remark" prop="remark">
          <el-input v-model="withdrawForm.remark" type="textarea" :rows="3" placeholder="Input remark" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="withdrawDialogOpen = false">Cancel</el-button>
        <el-button type="primary" :loading="withdrawSubmitting" @click="submitWithdraw">Submit</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
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
        supplierId: [{ required: true, message: "Input supplier id", trigger: "blur" }],
        supplierName: [{ required: true, message: "Input supplier name", trigger: "blur" }],
        applyAmount: [{ required: true, message: "Input apply amount", trigger: "change" }],
      },
    };
  },
  computed: {
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
    this.getWithdrawList();
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
      this.withdrawQueryParams.pageNum = 1;
      this.getWithdrawList();
    },
    resetFinanceQuery() {
      this.resetForm("financeQueryForm");
      this.handleFinanceQuery();
    },
    resetWithdrawQuery() {
      this.resetForm("withdrawQueryForm");
      this.handleWithdrawQuery();
    },
    exportFinanceList() {
      this.download("school/financeFlow/export", { ...this.financeQueryParams }, `financeFlow_${new Date().getTime()}.xlsx`);
    },
    exportWithdrawList() {
      this.download("school/withdrawApply/export", { ...this.withdrawQueryParams }, `withdrawApply_${new Date().getTime()}.xlsx`);
    },
    handleAvailableSelectionChange(selection) {
      this.selectedAvailableRows = selection;
    },
    isWithdrawableFlow(row) {
      return String(row.flowDirection) === "1" && String(row.status) === "2" && (String(row.payeeType) === "2" || !!row.supplierId);
    },
    openWithdrawDialog(row) {
      const rows = row ? [row] : this.selectedAvailableRows;
      const firstRow = rows[0] || {};
      this.withdrawForm = {
        withdrawNo: this.generateWithdrawNo(),
        supplierId: firstRow.supplierId,
        supplierName: firstRow.payeeName || firstRow.supplierName || "",
        applyAmount: rows.length ? this.sumAmount(rows, "amount") : undefined,
        financeFlowId: firstRow.id,
        bankInfoJson: "",
        remark: rows.length > 1 ? `linked ${rows.length} rows` : "",
      };
      this.withdrawDialogOpen = true;
      this.$nextTick(() => {
        if (this.$refs.withdrawForm) {
          this.$refs.withdrawForm.clearValidate();
        }
      });
    },
    submitWithdraw() {
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
            this.$modal.msgSuccess("submit success");
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
      const map = { 1: "Pay", 2: "Refund", 3: "Withdraw", 4: "Settlement" };
      return map[String(value)] || value || "-";
    },
    flowDirectionText(value) {
      const map = { 1: "Income", 2: "Outcome" };
      return map[String(value)] || value || "-";
    },
    flowStatusText(value) {
      const map = { 0: "Unpaid", 1: "Arrived School", 2: "Arrived Supplier" };
      return map[String(value)] || value || "-";
    },
    withdrawStatusText(value) {
      const map = { 0: "Pending", 1: "Approved", 2: "Rejected", 3: "Paid", 4: "Canceled" };
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
