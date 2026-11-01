<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="68px">
      <el-form-item label="流水单号" prop="flowNo">
        <el-input
          v-model="queryParams.flowNo"
          placeholder="请输入流水单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="收支方向" prop="flowDirection">
        <el-select
          v-model="queryParams.flowDirection"
          placeholder="请选择收支方向"
          clearable
        >
          <el-option label="收入" value="1" />
          <el-option label="支出" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="订单编号" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单编号"
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
      <el-form-item label="供应商名称" prop="supplierName">
        <el-input
          v-model="queryParams.supplierName"
          placeholder="请输入供应商名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户名称" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入用户名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="流水金额" prop="amount">
        <el-input
          v-model="queryParams.amount"
          placeholder="请输入流水金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="支付渠道" prop="payChannel">
        <el-input
          v-model="queryParams.payChannel"
          placeholder="请输入支付渠道"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="交易单号" prop="tradeNo">
        <el-input
          v-model="queryParams.tradeNo"
          placeholder="请输入交易单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="收款方名称" prop="payeeName">
        <el-input
          v-model="queryParams.payeeName"
          placeholder="请输入收款方名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="流水状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择流水状态"
          clearable
        >
          <el-option label="未支付" value="0" />
          <el-option label="已到达学校账号" value="1" />
          <el-option label="已到达供应商账号" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="交易发生时间" prop="tradeTime">
        <el-date-picker
          v-model="queryParams.tradeTime"
          clearable
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择交易发生时间"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['school:financeFlow:add']"
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['school:financeFlow:edit']"
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['school:financeFlow:remove']"
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['school:financeFlow:export']"
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
        >导出</el-button>
      </el-col>
      <right-toolbar :show-search.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="financeFlowList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="流水单号" align="center" prop="flowNo" />
      <el-table-column label="业务类型" align="center" prop="businessType">
        <template slot-scope="scope">
          {{ businessTypeText(scope.row.businessType) }}
        </template>
      </el-table-column>
      <el-table-column label="收支方向" align="center" prop="flowDirection">
        <template slot-scope="scope">
          {{ flowDirectionText(scope.row.flowDirection) }}
        </template>
      </el-table-column>
      <el-table-column label="订单编号" align="center" prop="orderNo" />
      <el-table-column label="学校名称" align="center" prop="schoolName" />
      <el-table-column label="供应商名称" align="center" prop="supplierName" />
      <el-table-column label="用户名称" align="center" prop="userName" />
      <el-table-column label="流水金额" align="center" prop="amount" />
      <el-table-column label="支付渠道" align="center" prop="payChannel" />
      <el-table-column label="收款方名称" align="center" prop="payeeName" />
      <el-table-column label="流水状态" align="center" prop="status">
        <template slot-scope="scope">
          {{ flowStatusText(scope.row.status) }}
        </template>
      </el-table-column>
      <el-table-column label="交易发生时间" align="center" prop="tradeTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.tradeTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >详情</el-button>
          <el-button
            v-hasPermi="['school:financeFlow:edit']"
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            v-hasPermi="['school:financeFlow:remove']"
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog title="资金流水详情" :visible.sync="detailOpen" width="980px" append-to-body>
      <div v-loading="detailLoading" class="detail-wrap">
        <div class="detail-section">
          <div class="section-title">业务信息</div>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="流水单号">{{ formatValue(detailForm.flowNo) }}</el-descriptions-item>
            <el-descriptions-item label="业务类型">{{ businessTypeText(detailForm.businessType) }}</el-descriptions-item>
            <el-descriptions-item label="收支方向">{{ flowDirectionText(detailForm.flowDirection) }}</el-descriptions-item>
            <el-descriptions-item label="订单编号">{{ formatValue(detailForm.orderNo) }}</el-descriptions-item>
            <el-descriptions-item label="学校名称">{{ formatValue(detailForm.schoolName) }}</el-descriptions-item>
            <el-descriptions-item label="供应商名称">{{ formatValue(detailForm.supplierName) }}</el-descriptions-item>
            <el-descriptions-item label="用户名称">{{ formatValue(detailForm.userName) }}</el-descriptions-item>
            <el-descriptions-item label="流水金额">{{ formatAmount(detailForm.amount) }}</el-descriptions-item>
            <el-descriptions-item label="支付渠道">{{ formatValue(detailForm.payChannel) }}</el-descriptions-item>
            <el-descriptions-item label="交易单号">{{ formatValue(detailForm.tradeNo) }}</el-descriptions-item>
            <el-descriptions-item label="收款方类型">{{ payeeTypeText(detailForm.payeeType) }}</el-descriptions-item>
            <el-descriptions-item label="收款方名称">{{ formatValue(detailForm.payeeName) }}</el-descriptions-item>
            <el-descriptions-item label="流水状态">{{ flowStatusText(detailForm.status) }}</el-descriptions-item>
            <el-descriptions-item label="交易时间">{{ formatDateTime(detailForm.tradeTime) }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="detail-section">
          <div class="section-title">内部字段</div>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单ID">{{ formatValue(detailForm.orderId) }}</el-descriptions-item>
            <el-descriptions-item label="售后单ID">{{ formatValue(detailForm.afterSaleId) }}</el-descriptions-item>
            <el-descriptions-item label="学校ID">{{ formatValue(detailForm.schoolId) }}</el-descriptions-item>
            <el-descriptions-item label="供应商ID">{{ formatValue(detailForm.supplierId) }}</el-descriptions-item>
            <el-descriptions-item label="用户ID">{{ formatValue(detailForm.userId) }}</el-descriptions-item>
            <el-descriptions-item label="收款方ID">{{ formatValue(detailForm.payeeId) }}</el-descriptions-item>
            <el-descriptions-item label="归属部门/学校">{{ formatValue(detailForm.deptId) }}</el-descriptions-item>
            <el-descriptions-item label="备注">{{ formatValue(detailForm.remark) }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="detail-section">
          <div class="section-title">银行账户快照</div>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="收款账户户名">{{ formatValue(detailForm.payeeAccountName) }}</el-descriptions-item>
            <el-descriptions-item label="收款银行">{{ formatValue(detailForm.payeeBankName) }}</el-descriptions-item>
            <el-descriptions-item label="收款银行卡号" :span="2">{{ formatValue(detailForm.payeeBankAccountNo) }}</el-descriptions-item>
            <el-descriptions-item label="收款码" :span="2">
              <image-preview v-if="detailForm.receiptQrCode" :src="detailForm.receiptQrCode" :width="120" :height="120" />
              <span v-else>--</span>
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 添加或修改资金流水对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="流水单号" prop="flowNo">
          <el-input v-model="form.flowNo" placeholder="请输入流水单号" />
        </el-form-item>
        <el-form-item label="业务类型" prop="businessType">
          <el-select v-model="form.businessType" placeholder="请选择业务类型" clearable>
            <el-option label="支付" value="1" />
            <el-option label="退款" value="2" />
            <el-option label="提现" value="3" />
            <el-option label="结算" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="收支方向" prop="flowDirection">
          <el-select v-model="form.flowDirection" placeholder="请选择收支方向" clearable>
            <el-option label="收入" value="1" />
            <el-option label="支出" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联订单ID" prop="orderId">
          <el-input v-model="form.orderId" placeholder="请输入关联订单ID" />
        </el-form-item>
        <el-form-item label="订单编号" prop="orderNo">
          <el-input v-model="form.orderNo" placeholder="请输入订单编号" />
        </el-form-item>
        <el-form-item label="订单类型" prop="orderType">
          <el-select v-model="form.orderType" placeholder="请选择订单类型" clearable>
            <el-option label="统购" value="1" />
            <el-option label="零售" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联售后单ID" prop="afterSaleId">
          <el-input v-model="form.afterSaleId" placeholder="请输入关联售后单ID" />
        </el-form-item>
        <el-form-item label="售后单号" prop="afterSaleNo">
          <el-input v-model="form.afterSaleNo" placeholder="售后单号" disabled />
        </el-form-item>
        <el-form-item label="学校ID" prop="schoolId">
          <el-input v-model="form.schoolId" placeholder="请输入学校ID" />
        </el-form-item>
        <el-form-item label="学校名称" prop="schoolName">
          <el-input v-model="form.schoolName" placeholder="学校名称" disabled />
        </el-form-item>
        <el-form-item label="供应商ID" prop="supplierId">
          <el-input v-model="form.supplierId" placeholder="请输入供应商ID" />
        </el-form-item>
        <el-form-item label="供应商名称" prop="supplierName">
          <el-input v-model="form.supplierName" placeholder="供应商名称" disabled />
        </el-form-item>
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="用户名称" prop="userName">
          <el-input v-model="form.userName" placeholder="用户名称" disabled />
        </el-form-item>
        <el-form-item label="流水金额" prop="amount">
          <el-input v-model="form.amount" placeholder="请输入流水金额" />
        </el-form-item>
        <el-form-item label="支付渠道" prop="payChannel">
          <el-input v-model="form.payChannel" placeholder="请输入支付渠道" />
        </el-form-item>
        <el-form-item label="交易单号" prop="tradeNo">
          <el-input v-model="form.tradeNo" placeholder="请输入交易单号" />
        </el-form-item>
        <el-form-item label="收款方类型" prop="payeeType">
          <el-select v-model="form.payeeType" placeholder="请选择收款方类型" clearable>
            <el-option label="学校" value="1" />
            <el-option label="供应商" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="收款方ID" prop="payeeId">
          <el-input v-model="form.payeeId" placeholder="请输入收款方ID" />
        </el-form-item>
        <el-form-item label="收款方名称快照" prop="payeeName">
          <el-input v-model="form.payeeName" placeholder="请输入收款方名称快照" />
        </el-form-item>
        <el-form-item label="收款账户户名快照" prop="payeeAccountName">
          <el-input v-model="form.payeeAccountName" placeholder="请输入收款账户户名快照" />
        </el-form-item>
        <el-form-item label="收款银行快照" prop="payeeBankName">
          <el-input v-model="form.payeeBankName" placeholder="请输入收款银行快照" />
        </el-form-item>
        <el-form-item label="收款银行卡号快照" prop="payeeBankAccountNo">
          <el-input v-model="form.payeeBankAccountNo" placeholder="请输入收款银行卡号快照" />
        </el-form-item>
        <el-form-item label="收款码快照；统购展示学校收款码，零售展示供应商收款码" prop="receiptQrCode">
          <image-upload v-model="form.receiptQrCode" />
        </el-form-item>
        <el-form-item label="流水状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择流水状态" clearable>
            <el-option label="未支付" value="0" />
            <el-option label="已到达学校账号" value="1" />
            <el-option label="已到达供应商账号" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="交易发生时间" prop="tradeTime">
          <el-date-picker
            v-model="form.tradeTime"
            clearable
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择交易发生时间"
          />
        </el-form-item>
        <el-form-item label="当前归属部门/学校" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入当前归属部门/学校" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listFinanceFlow, getFinanceFlow, delFinanceFlow, addFinanceFlow, updateFinanceFlow } from '@/api/school/financeFlow'

export default {
  name: 'FinanceFlow',
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 资金流水表格数据
      financeFlowList: [],
      // 详情
      detailOpen: false,
      detailLoading: false,
      detailForm: {},
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        flowNo: null,
        businessType: null,
        flowDirection: null,
        orderId: null,
        orderNo: null,
        orderType: null,
        afterSaleId: null,
        schoolId: null,
        schoolName: null,
        supplierId: null,
        supplierName: null,
        userId: null,
        userName: null,
        amount: null,
        payChannel: null,
        tradeNo: null,
        payeeType: null,
        payeeId: null,
        payeeName: null,
        status: undefined,
        tradeTime: null,
        deptId: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        flowNo: [
          { required: true, message: '流水单号不能为空', trigger: 'blur' }
        ],
        businessType: [
          { required: true, message: '业务类型不能为空', trigger: 'change' }
        ],
        flowDirection: [
          { required: true, message: '收支方向不能为空', trigger: 'change' }
        ],
        amount: [
          { required: true, message: '流水金额不能为空', trigger: 'blur' }
        ],
        payeeType: [
          { required: true, message: '收款方类型不能为空', trigger: 'change' }
        ],
        payeeId: [
          { required: true, message: '收款方ID不能为空', trigger: 'blur' }
        ],
        payeeName: [
          { required: true, message: '收款方名称快照不能为空', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '流水状态不能为空', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    formatValue(value) {
      return value === undefined || value === null || value === '' ? '--' : value
    },
    formatAmount(value) {
      if (value === undefined || value === null || value === '') {
        return '--'
      }
      const amount = Number(value)
      return Number.isNaN(amount) ? value : `￥${amount.toFixed(2)}`
    },
    formatDateTime(value) {
      return this.parseTime(value, '{y}-{m}-{d} {h}:{i}:{s}') || '--'
    },
    /** 查询资金流水列表 */
    getList() {
      this.loading = true
      listFinanceFlow(this.queryParams).then(response => {
        this.financeFlowList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        flowNo: null,
        businessType: null,
        flowDirection: null,
        orderId: null,
        orderNo: null,
        orderType: null,
        afterSaleId: null,
        afterSaleNo: null,
        schoolId: null,
        schoolName: null,
        supplierId: null,
        supplierName: null,
        userId: null,
        userName: null,
        amount: null,
        payChannel: null,
        tradeNo: null,
        payeeType: null,
        payeeId: null,
        payeeName: null,
        payeeAccountName: null,
        payeeBankName: null,
        payeeBankAccountNo: null,
        receiptQrCode: null,
        status: null,
        tradeTime: null,
        deptId: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null,
        delFlag: '0'
      }
      this.resetForm('form')
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '添加资金流水'
    },
    /** 详情按钮操作 */
    handleView(row) {
      const id = row.id || this.ids[0]
      this.detailOpen = true
      this.detailLoading = true
      getFinanceFlow(id).then(response => {
        this.detailForm = response.data || {}
      }).finally(() => {
        this.detailLoading = false
      })
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getFinanceFlow(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = '修改资金流水'
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateFinanceFlow(this.form).then(response => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addFinanceFlow(this.form).then(response => {
              this.$modal.msgSuccess('新增成功')
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除资金流水编号为"' + ids + '"的数据项？').then(function() {
        return delFinanceFlow(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    businessTypeText(value) {
      const map = { 1: '支付', 2: '退款', 3: '提现', 4: '结算' }
      return map[String(value)] || value || '-'
    },
    flowDirectionText(value) {
      const map = { 1: '收入', 2: '支出' }
      return map[String(value)] || value || '-'
    },
    orderTypeText(value) {
      const map = { 1: '统购', 2: '零售' }
      return map[String(value)] || value || '-'
    },
    payeeTypeText(value) {
      const map = { 1: '学校', 2: '供应商' }
      return map[String(value)] || value || '-'
    },
    flowStatusText(value) {
      const map = { 0: '未支付', 1: '已到达学校账号', 2: '已到达供应商账号' }
      return map[String(value)] || value || '-'
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('school/financeFlow/export', {
        ...this.queryParams
      }, `financeFlow_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>

<style scoped>
.detail-wrap {
  min-height: 220px;
}

.detail-section + .detail-section {
  margin-top: 20px;
}

.section-title {
  margin-bottom: 12px;
  color: #303133;
  font-size: 15px;
  font-weight: 600;
}
</style>
