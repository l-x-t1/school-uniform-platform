<template>
  <div class="app-container withdraw-apply-page">
    <el-card v-show="showSearch" class="search-card" shadow="never">
      <el-form
        ref="queryForm"
        :model="queryParams"
        size="small"
        :inline="true"
        label-width="82px"
      >
        <el-form-item label="提现单号" prop="withdrawNo">
          <el-input
            v-model="queryParams.withdrawNo"
            placeholder="请输入提现单号"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="供应商名称" prop="supplierName">
          <el-input
            v-model="queryParams.supplierName"
            placeholder="请输入供应商名称"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="提现状态" prop="status">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择提现状态"
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
        <el-form-item label="申请时间" prop="applyTime">
          <el-date-picker
            v-model="queryParams.applyTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择申请时间"
            clearable
          />
        </el-form-item>
        <el-form-item label="审核时间" prop="auditTime">
          <el-date-picker
            v-model="queryParams.auditTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择审核时间"
            clearable
          />
        </el-form-item>
        <el-form-item label="打款时间" prop="payTime">
          <el-date-picker
            v-model="queryParams.payTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择打款时间"
            clearable
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <div slot="header" class="table-header">
        <div>
          <div class="table-title">提现申请</div>
          <div class="table-desc">按提现单号、供应商和时间查询申请记录，列表仅保留业务字段，补充信息在详情中查看。</div>
        </div>
        <right-toolbar :show-search.sync="showSearch" @queryTable="getList" />
      </div>

      <div class="toolbar-row">
        <el-button
          v-hasPermi="['school:withdrawApply:add']"
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >
          新增
        </el-button>
        <el-button
          v-hasPermi="['school:withdrawApply:edit']"
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
        >
          修改
        </el-button>
        <el-button
          v-hasPermi="['school:withdrawApply:remove']"
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
        >
          删除
        </el-button>
        <el-button
          v-hasPermi="['school:withdrawApply:export']"
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
        >
          导出
        </el-button>
      </div>

      <el-table v-loading="loading" :data="withdrawApplyList" border @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="提现单号" align="center" prop="withdrawNo" min-width="180" show-overflow-tooltip />
        <el-table-column label="供应商信息" align="center" min-width="180" show-overflow-tooltip>
          <template slot-scope="scope">
            <div class="supplier-cell">
              <div class="primary-text">{{ formatValue(scope.row.supplierName) }}</div>
              <div class="secondary-text">提现申请供应商</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="申请金额" align="center" prop="applyAmount" width="120">
          <template slot-scope="scope">
            <span>{{ formatAmount(scope.row.applyAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="status" width="110">
          <template slot-scope="scope">
            <el-tag :type="statusTagType(scope.row.status)" size="mini">
              {{ formatStatus(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申请时间" align="center" prop="applyTime" width="140">
          <template slot-scope="scope">
            <span>{{ formatDate(scope.row.applyTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="审核人" align="center" prop="auditUserName" min-width="120" show-overflow-tooltip>
          <template slot-scope="scope">
            <span>{{ formatValue(scope.row.auditUserName) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="审核时间" align="center" prop="auditTime" width="140">
          <template slot-scope="scope">
            <span>{{ formatDate(scope.row.auditTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="打款时间" align="center" prop="payTime" width="140">
          <template slot-scope="scope">
            <span>{{ formatDate(scope.row.payTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-view"
              @click="handleView(scope.row)"
            >
              详情
            </el-button>
            <el-button
              v-hasPermi="['school:withdrawApply:edit']"
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="handleUpdate(scope.row)"
            >
              修改
            </el-button>
            <el-button
              v-hasPermi="['school:withdrawApply:remove']"
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="handleDelete(scope.row)"
            >
              删除
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

    <el-dialog title="提现申请详情" :visible.sync="detailOpen" width="980px" append-to-body>
      <div v-loading="detailLoading" class="detail-wrap">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="提现单号">{{ formatValue(detailForm.withdrawNo) }}</el-descriptions-item>
          <el-descriptions-item label="提现状态">
            <el-tag :type="statusTagType(detailForm.status)" size="mini">
              {{ formatStatus(detailForm.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="供应商名称">{{ formatValue(detailForm.supplierName) }}</el-descriptions-item>
          <el-descriptions-item label="申请金额">{{ formatAmount(detailForm.applyAmount) }}</el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ formatDate(detailForm.applyTime) }}</el-descriptions-item>
          <el-descriptions-item label="审核人">{{ formatValue(detailForm.auditUserName) }}</el-descriptions-item>
          <el-descriptions-item label="审核时间">{{ formatDate(detailForm.auditTime) }}</el-descriptions-item>
          <el-descriptions-item label="打款时间">{{ formatDate(detailForm.payTime) }}</el-descriptions-item>
        </el-descriptions>

        <div class="detail-section">
          <div class="section-title">补充说明</div>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="审核备注" :span="2">{{ formatValue(detailForm.auditRemark) }}</el-descriptions-item>
            <el-descriptions-item label="备注" :span="2">{{ formatValue(detailForm.remark) }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="detail-section">
          <div class="section-title">银行信息快照</div>
          <pre class="json-box">{{ formatJsonText(detailForm.bankInfoJson) }}</pre>
        </div>

        <div class="detail-section">
          <div class="section-title">内部字段</div>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="主键ID">{{ formatValue(detailForm.id) }}</el-descriptions-item>
            <el-descriptions-item label="供应商ID">{{ formatValue(detailForm.supplierId) }}</el-descriptions-item>
            <el-descriptions-item label="审核人ID">{{ formatValue(detailForm.auditUserId) }}</el-descriptions-item>
            <el-descriptions-item label="资金流水ID">{{ formatValue(detailForm.financeFlowId) }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button size="small" @click="detailOpen = false">关闭</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="title" :visible.sync="open" width="560px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="96px">
        <el-form-item label="提现单号" prop="withdrawNo">
          <el-input v-model="form.withdrawNo" placeholder="请输入提现单号" />
        </el-form-item>
        <el-form-item label="供应商ID" prop="supplierId">
          <el-input v-model="form.supplierId" placeholder="请输入供应商ID" />
        </el-form-item>
        <el-form-item label="供应商名称" prop="supplierName">
          <el-input v-model="form.supplierName" placeholder="请输入供应商名称" />
        </el-form-item>
        <el-form-item label="申请金额" prop="applyAmount">
          <el-input-number
            v-model="form.applyAmount"
            :min="0"
            :precision="2"
            :step="100"
            controls-position="right"
            class="full-width"
          />
        </el-form-item>
        <el-form-item label="提现状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择提现状态">
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="申请时间" prop="applyTime">
          <el-date-picker
            v-model="form.applyTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择申请时间"
            clearable
          />
        </el-form-item>
        <el-form-item label="审核人ID" prop="auditUserId">
          <el-input v-model="form.auditUserId" placeholder="请输入审核人ID" />
        </el-form-item>
        <el-form-item label="审核人姓名" prop="auditUserName">
          <el-input v-model="form.auditUserName" placeholder="请输入审核人姓名" />
        </el-form-item>
        <el-form-item label="审核时间" prop="auditTime">
          <el-date-picker
            v-model="form.auditTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择审核时间"
            clearable
          />
        </el-form-item>
        <el-form-item label="打款时间" prop="payTime">
          <el-date-picker
            v-model="form.payTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择打款时间"
            clearable
          />
        </el-form-item>
        <el-form-item label="资金流水ID" prop="financeFlowId">
          <el-input v-model="form.financeFlowId" placeholder="请输入关联资金流水ID" />
        </el-form-item>
        <el-form-item label="审核备注" prop="auditRemark">
          <el-input v-model="form.auditRemark" type="textarea" :rows="3" placeholder="请输入审核备注" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
        <el-form-item label="银行信息JSON" prop="bankInfoJson">
          <el-input v-model="form.bankInfoJson" type="textarea" :rows="4" placeholder="请输入银行卡或收款账户信息 JSON" />
        </el-form-item>
        <el-form-item label="删除标记" prop="delFlag">
          <el-select v-model="form.delFlag" placeholder="请选择删除标记">
            <el-option label="正常" value="0" />
            <el-option label="删除" value="1" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确定</el-button>
        <el-button @click="cancel">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  addWithdrawApply,
  delWithdrawApply,
  getWithdrawApply,
  listWithdrawApply,
  updateWithdrawApply
} from '@/api/school/withdrawApply'

const statusOptions = [
  { label: '待审核', value: '0' },
  { label: '审核通过', value: '1' },
  { label: '审核拒绝', value: '2' },
  { label: '已打款', value: '3' },
  { label: '已取消', value: '4' }
]

const createDefaultForm = () => ({
  id: null,
  withdrawNo: null,
  supplierId: null,
  supplierName: null,
  applyAmount: null,
  bankInfoJson: null,
  status: '0',
  applyTime: null,
  auditUserId: null,
  auditUserName: null,
  auditTime: null,
  payTime: null,
  auditRemark: null,
  financeFlowId: null,
  createBy: null,
  createTime: null,
  updateBy: null,
  updateTime: null,
  remark: null,
  delFlag: '0'
})

export default {
  name: 'WithdrawApply',
  data() {
    const validateTimeOrder = (rule, value, callback) => {
      const applyTime = this.parseDateValue(this.form.applyTime)
      const auditTime = this.parseDateValue(this.form.auditTime)
      const payTime = this.parseDateValue(this.form.payTime)

      if (applyTime && auditTime && applyTime >= auditTime) {
        callback(new Error('申请时间必须早于审核时间'))
        return
      }
      if (auditTime && payTime && auditTime >= payTime) {
        callback(new Error('审核时间必须早于打款时间'))
        return
      }
      if (applyTime && payTime && applyTime >= payTime) {
        callback(new Error('申请时间必须早于打款时间'))
        return
      }
      callback()
    }

    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      withdrawApplyList: [],
      title: '',
      open: false,
      detailOpen: false,
      detailLoading: false,
      statusOptions,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        withdrawNo: null,
        supplierName: null,
        status: null,
        applyTime: null,
        auditTime: null,
        payTime: null
      },
      form: createDefaultForm(),
      detailForm: createDefaultForm(),
      rules: {
        withdrawNo: [{ required: true, message: '提现单号不能为空', trigger: 'blur' }],
        supplierId: [{ required: true, message: '供应商ID不能为空', trigger: 'blur' }],
        supplierName: [{ required: true, message: '供应商名称不能为空', trigger: 'blur' }],
        applyAmount: [{ required: true, message: '申请金额不能为空', trigger: 'change' }],
        status: [{ required: true, message: '提现状态不能为空', trigger: 'change' }],
        applyTime: [{ validator: validateTimeOrder, trigger: 'change' }],
        auditTime: [{ validator: validateTimeOrder, trigger: 'change' }],
        payTime: [{ validator: validateTimeOrder, trigger: 'change' }],
        delFlag: [
          { required: true, message: '删除标记不能为空', trigger: 'change' },
          { pattern: /^[01]$/, message: '删除标记只能选择正常或删除', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    parseDateValue(value) {
      if (!value) {
        return null
      }
      const date = value instanceof Date ? value : new Date(value)
      return Number.isNaN(date.getTime()) ? null : date
    },
    getList() {
      this.loading = true
      listWithdrawApply(this.queryParams)
        .then((response) => {
          this.withdrawApplyList = response.rows || []
          this.total = response.total || 0
        })
        .finally(() => {
          this.loading = false
        })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = createDefaultForm()
      this.resetForm('form')
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增提现申请'
    },
    handleUpdate(row) {
      this.reset()
      const id = row && row.id ? row.id : this.ids[0]
      getWithdrawApply(id).then((response) => {
        this.form = {
          ...createDefaultForm(),
          ...(response.data || {})
        }
        this.open = true
        this.title = '修改提现申请'
      })
    },
    handleView(row) {
      this.detailOpen = true
      this.detailLoading = true
      getWithdrawApply(row.id)
        .then((response) => {
          this.detailForm = {
            ...createDefaultForm(),
            ...row,
            ...(response.data || {})
          }
        })
        .finally(() => {
          this.detailLoading = false
        })
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          return
        }
        const action = this.form.id ? updateWithdrawApply : addWithdrawApply
        action(this.form).then(() => {
          this.$modal.msgSuccess(this.form.id ? '修改成功' : '新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const ids = row && row.id ? row.id : this.ids
      this.$modal
        .confirm(`是否确认删除提现申请编号为“${ids}”的数据项？`)
        .then(() => delWithdrawApply(ids))
        .then(() => {
          this.getList()
          this.$modal.msgSuccess('删除成功')
        })
        .catch(() => {})
    },
    formatStatus(status) {
      const matched = this.statusOptions.find((item) => item.value === String(status))
      return matched ? matched.label : '--'
    },
    statusTagType(status) {
      if (String(status) === '0') {
        return 'warning'
      }
      if (String(status) === '1') {
        return 'primary'
      }
      if (String(status) === '2') {
        return 'danger'
      }
      if (String(status) === '3') {
        return 'success'
      }
      if (String(status) === '4') {
        return 'info'
      }
      return ''
    },
    formatDate(value) {
      return this.parseTime(value, '{y}-{m}-{d}') || '--'
    },
    formatValue(value) {
      return value === undefined || value === null || value === '' ? '--' : value
    },
    formatAmount(value) {
      if (value === undefined || value === null || value === '') {
        return '--'
      }
      const amount = Number(value)
      if (Number.isNaN(amount)) {
        return value
      }
      return `￥${amount.toFixed(2)}`
    },
    formatJsonText(value) {
      if (!value) {
        return '暂无银行信息'
      }
      if (typeof value === 'object') {
        return JSON.stringify(value, null, 2)
      }
      const raw = String(value).trim()
      if (!raw) {
        return '暂无银行信息'
      }
      try {
        return JSON.stringify(JSON.parse(raw), null, 2)
      } catch (error) {
        return raw
      }
    },
    handleExport() {
      this.download(
        'school/withdrawApply/export',
        {
          ...this.queryParams
        },
        `withdrawApply_${new Date().getTime()}.xlsx`
      )
    }
  }
}
</script>

<style scoped>
.withdraw-apply-page .search-card,
.withdraw-apply-page .table-card {
  border: 1px solid #e6ebf5;
  border-radius: 4px;
}

.withdraw-apply-page .search-card {
  margin-bottom: 18px;
}

.table-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}

.table-title {
  color: #303133;
  font-size: 18px;
  font-weight: 600;
  line-height: 26px;
}

.table-desc {
  margin-top: 4px;
  color: #909399;
  font-size: 13px;
}

.toolbar-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 16px;
}

.supplier-cell {
  line-height: 1.5;
}

.primary-text {
  color: #303133;
  font-weight: 600;
}

.secondary-text {
  color: #909399;
  font-size: 12px;
}

.detail-wrap {
  min-height: 220px;
}

.detail-section {
  margin-top: 20px;
}

.section-title {
  margin-bottom: 12px;
  color: #303133;
  font-size: 15px;
  font-weight: 600;
}

.json-box {
  margin: 0;
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  background: #fafafa;
  color: #606266;
  font-size: 13px;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-all;
}

.full-width {
  width: 100%;
}
</style>
