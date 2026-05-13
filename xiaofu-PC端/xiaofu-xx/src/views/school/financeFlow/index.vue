<template>
  <div class="app-container">
    <el-form
      v-show="showSearch"
      ref="queryForm"
      :model="queryParams"
      size="small"
      :inline="true"
      label-width="88px"
    >
      <el-form-item label="流水单号" prop="flowNo">
        <el-input
          v-model="queryParams.flowNo"
          placeholder="请输入流水单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="业务类型" prop="businessType">
        <el-select v-model="queryParams.businessType" placeholder="请选择业务类型" clearable>
          <el-option
            v-for="item in businessTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="收支方向" prop="flowDirection">
        <el-select v-model="queryParams.flowDirection" placeholder="请选择收支方向" clearable>
          <el-option
            v-for="item in flowDirectionOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="订单ID" prop="orderId">
        <el-input
          v-model="queryParams.orderId"
          placeholder="请输入关联订单ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="订单编号" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="订单类型" prop="orderType">
        <el-select v-model="queryParams.orderType" placeholder="请选择订单类型" clearable>
          <el-option
            v-for="item in orderTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="售后单ID" prop="afterSaleId">
        <el-input
          v-model="queryParams.afterSaleId"
          placeholder="请输入关联售后单ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="学校ID" prop="schoolId">
        <el-input
          v-model="queryParams.schoolId"
          placeholder="请输入学校ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="供应商ID" prop="supplierId">
        <el-input
          v-model="queryParams.supplierId"
          placeholder="请输入供应商ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入用户ID"
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
      <el-form-item label="交易号" prop="tradeNo">
        <el-input
          v-model="queryParams.tradeNo"
          placeholder="请输入外部交易号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="收款方类型" prop="payeeType">
        <el-select v-model="queryParams.payeeType" placeholder="请选择收款方类型" clearable>
          <el-option
            v-for="item in payeeTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="收款方ID" prop="payeeId">
        <el-input
          v-model="queryParams.payeeId"
          placeholder="请输入收款方ID"
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
      <el-form-item label="到账状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择到账状态" clearable>
          <el-option
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="交易时间" prop="tradeTime">
        <el-date-picker
          v-model="queryParams.tradeTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择交易时间"
          clearable
        />
      </el-form-item>
      <el-form-item label="归属部门" prop="deptId">
        <el-input
          v-model="queryParams.deptId"
          placeholder="请输入归属部门/学校"
          clearable
          @keyup.enter.native="handleQuery"
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
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="流水单号" align="center" prop="flowNo" />
      <el-table-column label="业务类型" align="center" prop="businessType">
        <template slot-scope="scope">
          <span>{{ getOptionLabel(businessTypeOptions, scope.row.businessType) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="收支方向" align="center" prop="flowDirection">
        <template slot-scope="scope">
          <span>{{ getOptionLabel(flowDirectionOptions, scope.row.flowDirection) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="订单ID" align="center" prop="orderId" />
      <el-table-column label="订单编号" align="center" prop="orderNo" />
      <el-table-column label="订单类型" align="center" prop="orderType">
        <template slot-scope="scope">
          <span>{{ getOptionLabel(orderTypeOptions, scope.row.orderType) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="售后单ID" align="center" prop="afterSaleId" />
      <el-table-column label="学校ID" align="center" prop="schoolId" />
      <el-table-column label="供应商ID" align="center" prop="supplierId" />
      <el-table-column label="用户ID" align="center" prop="userId" />
      <el-table-column label="流水金额" align="center" prop="amount" />
      <el-table-column label="支付渠道" align="center" prop="payChannel" />
      <el-table-column label="交易号" align="center" prop="tradeNo" />
      <el-table-column label="收款方类型" align="center" prop="payeeType">
        <template slot-scope="scope">
          <span>{{ getOptionLabel(payeeTypeOptions, scope.row.payeeType) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="收款方ID" align="center" prop="payeeId" />
      <el-table-column label="收款方名称" align="center" prop="payeeName" />
      <el-table-column label="收款账户名" align="center" prop="payeeAccountName" />
      <el-table-column label="收款银行" align="center" prop="payeeBankName" />
      <el-table-column label="到账状态" align="center" prop="status">
        <template slot-scope="scope">
          <span>{{ getOptionLabel(statusOptions, scope.row.status) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="交易时间" align="center" prop="tradeTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.tradeTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="归属部门" align="center" prop="deptId" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
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
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="title" :visible.sync="open" width="520px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="92px">
        <el-form-item label="流水单号" prop="flowNo">
          <el-input v-model="form.flowNo" placeholder="请输入流水单号" />
        </el-form-item>
        <el-form-item label="业务类型" prop="businessType">
          <el-select v-model="form.businessType" placeholder="请选择业务类型" clearable style="width: 100%">
            <el-option
              v-for="item in businessTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="收支方向" prop="flowDirection">
          <el-select v-model="form.flowDirection" placeholder="请选择收支方向" clearable style="width: 100%">
            <el-option
              v-for="item in flowDirectionOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="订单ID" prop="orderId">
          <el-input v-model="form.orderId" placeholder="请输入关联订单ID" />
        </el-form-item>
        <el-form-item label="订单编号" prop="orderNo">
          <el-input v-model="form.orderNo" placeholder="请输入订单编号" />
        </el-form-item>
        <el-form-item label="订单类型" prop="orderType">
          <el-select v-model="form.orderType" placeholder="请选择订单类型" clearable style="width: 100%">
            <el-option
              v-for="item in orderTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="售后单ID" prop="afterSaleId">
          <el-input v-model="form.afterSaleId" placeholder="请输入关联售后单ID" />
        </el-form-item>
        <el-form-item label="学校ID" prop="schoolId">
          <el-input v-model="form.schoolId" placeholder="请输入学校ID" />
        </el-form-item>
        <el-form-item label="供应商ID" prop="supplierId">
          <el-input v-model="form.supplierId" placeholder="请输入供应商ID" />
        </el-form-item>
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="流水金额" prop="amount">
          <el-input v-model="form.amount" placeholder="请输入流水金额" />
        </el-form-item>
        <el-form-item label="支付渠道" prop="payChannel">
          <el-input v-model="form.payChannel" placeholder="请输入支付渠道" />
        </el-form-item>
        <el-form-item label="交易号" prop="tradeNo">
          <el-input v-model="form.tradeNo" placeholder="请输入外部交易号" />
        </el-form-item>
        <el-form-item label="收款方类型" prop="payeeType">
          <el-select v-model="form.payeeType" placeholder="请选择收款方类型" clearable style="width: 100%">
            <el-option
              v-for="item in payeeTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="收款方ID" prop="payeeId">
          <el-input v-model="form.payeeId" placeholder="请输入收款方ID" />
        </el-form-item>
        <el-form-item label="收款方名称" prop="payeeName">
          <el-input v-model="form.payeeName" placeholder="请输入收款方名称" />
        </el-form-item>
        <el-form-item label="收款账户名" prop="payeeAccountName">
          <el-input v-model="form.payeeAccountName" placeholder="请输入收款账户名" />
        </el-form-item>
        <el-form-item label="收款银行" prop="payeeBankName">
          <el-input v-model="form.payeeBankName" placeholder="请输入收款银行" />
        </el-form-item>
        <el-form-item label="银行卡号" prop="payeeBankAccountNo">
          <el-input v-model="form.payeeBankAccountNo" placeholder="请输入银行卡号" />
        </el-form-item>
        <el-form-item label="收款码" prop="receiptQrCode">
          <image-upload v-model="form.receiptQrCode" />
        </el-form-item>
        <el-form-item label="到账状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择到账状态" clearable style="width: 100%">
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="交易时间" prop="tradeTime">
          <el-date-picker
            v-model="form.tradeTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择交易时间"
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="归属部门" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入归属部门/学校" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
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
import { addFinanceFlow, delFinanceFlow, getFinanceFlow, listFinanceFlow, updateFinanceFlow } from '@/api/school/financeFlow'

export default {
  name: 'FinanceFlow',
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      financeFlowList: [],
      title: '',
      open: false,
      businessTypeOptions: [
        { value: '1', label: '支付' },
        { value: '2', label: '退款' },
        { value: '3', label: '提现' },
        { value: '4', label: '结算' }
      ],
      flowDirectionOptions: [
        { value: '1', label: '收入' },
        { value: '2', label: '支出' }
      ],
      orderTypeOptions: [
        { value: '1', label: '统购' },
        { value: '2', label: '零售' }
      ],
      payeeTypeOptions: [
        { value: '1', label: '学校' },
        { value: '2', label: '供应商' }
      ],
      statusOptions: [
        { value: '0', label: '未支付' },
        { value: '1', label: '已到学校账号' },
        { value: '2', label: '已到供应商账号' }
      ],
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
        supplierId: null,
        userId: null,
        amount: null,
        payChannel: null,
        tradeNo: null,
        payeeType: null,
        payeeId: null,
        payeeName: null,
        status: null,
        tradeTime: null,
        deptId: null
      },
      form: {},
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
          { required: true, message: '收款方名称不能为空', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '到账状态不能为空', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listFinanceFlow(this.queryParams).then(response => {
        this.financeFlowList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
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
        schoolId: null,
        supplierId: null,
        userId: null,
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
        status: '0',
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
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '添加资金流水'
    },
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getFinanceFlow(id).then(response => {
        this.form = {
          ...this.form,
          ...response.data,
          businessType: response.data.businessType ? String(response.data.businessType) : null,
          flowDirection: response.data.flowDirection ? String(response.data.flowDirection) : null,
          orderType: response.data.orderType ? String(response.data.orderType) : null,
          payeeType: response.data.payeeType ? String(response.data.payeeType) : null,
          status: response.data.status != null ? String(response.data.status) : '0',
          delFlag: response.data.delFlag != null ? String(response.data.delFlag) : '0'
        }
        this.open = true
        this.title = '修改资金流水'
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        const submitData = {
          ...this.form,
          businessType: this.form.businessType ? String(this.form.businessType) : null,
          flowDirection: this.form.flowDirection ? String(this.form.flowDirection) : null,
          orderType: this.form.orderType ? String(this.form.orderType) : null,
          payeeType: this.form.payeeType ? String(this.form.payeeType) : null,
          status: this.form.status != null ? String(this.form.status) : null,
          delFlag: this.form.delFlag != null ? String(this.form.delFlag) : '0'
        }
        if (submitData.id != null) {
          updateFinanceFlow(submitData).then(() => {
            this.$modal.msgSuccess('修改成功')
            this.open = false
            this.getList()
          })
        } else {
          addFinanceFlow(submitData).then(() => {
            this.$modal.msgSuccess('新增成功')
            this.open = false
            this.getList()
          })
        }
      })
    },
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除资金流水编号为"' + ids + '"的数据项？').then(() => {
        return delFinanceFlow(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    handleExport() {
      this.download('school/financeFlow/export', {
        ...this.queryParams
      }, `financeFlow_${new Date().getTime()}.xlsx`)
    },
    getOptionLabel(options, value) {
      const target = options.find(item => item.value === String(value))
      return target ? target.label : value
    }
  }
}
</script>
