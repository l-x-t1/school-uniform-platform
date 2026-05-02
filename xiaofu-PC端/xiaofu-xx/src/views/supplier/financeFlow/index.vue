<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="流水号" prop="flowNo">
        <el-input
          v-model="queryParams.flowNo"
          placeholder="请输入流水号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="订单ID" prop="orderId">
        <el-input
          v-model="queryParams.orderId"
          placeholder="请输入订单ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="售后ID" prop="afterSaleId">
        <el-input
          v-model="queryParams.afterSaleId"
          placeholder="请输入售后ID"
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
      <el-form-item label="金额" prop="amount">
        <el-input
          v-model="queryParams.amount"
          placeholder="请输入金额"
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
      <el-form-item label="第三方交易号" prop="tradeNo">
        <el-input
          v-model="queryParams.tradeNo"
          placeholder="请输入第三方交易号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="交易完成时间" prop="tradeTime">
        <el-date-picker clearable
          v-model="queryParams.tradeTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择交易完成时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="当前归属部门/学校" prop="deptId">
        <el-input
          v-model="queryParams.deptId"
          placeholder="请输入当前归属部门/学校"
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
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['supplier:financeFlow:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['supplier:financeFlow:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['supplier:financeFlow:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['supplier:financeFlow:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="financeFlowList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="流水号" align="center" prop="flowNo" />
      <el-table-column label="业务类型，1支付 2退款 3结算 4提现" align="center" prop="businessType" />
      <el-table-column label="订单ID" align="center" prop="orderId" />
      <el-table-column label="售后ID" align="center" prop="afterSaleId" />
      <el-table-column label="学校ID" align="center" prop="schoolId" />
      <el-table-column label="供应商ID" align="center" prop="supplierId" />
      <el-table-column label="用户ID" align="center" prop="userId" />
      <el-table-column label="金额" align="center" prop="amount" />
      <el-table-column label="支付渠道" align="center" prop="payChannel" />
      <el-table-column label="第三方交易号" align="center" prop="tradeNo" />
      <el-table-column label="状态，0待处理 1成功 2失败 3关闭" align="center" prop="status" />
      <el-table-column label="交易完成时间" align="center" prop="tradeTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.tradeTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="当前归属部门/学校" align="center" prop="deptId" />
      <el-table-column label="组织祖级链路" align="center" prop="ancestors" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['supplier:financeFlow:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['supplier:financeFlow:remove']"
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

    <!-- 添加或修改财务流水对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="流水号" prop="flowNo">
          <el-input v-model="form.flowNo" placeholder="请输入流水号" />
        </el-form-item>
        <el-form-item label="订单ID" prop="orderId">
          <el-input v-model="form.orderId" placeholder="请输入订单ID" />
        </el-form-item>
        <el-form-item label="售后ID" prop="afterSaleId">
          <el-input v-model="form.afterSaleId" placeholder="请输入售后ID" />
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
        <el-form-item label="金额" prop="amount">
          <el-input v-model="form.amount" placeholder="请输入金额" />
        </el-form-item>
        <el-form-item label="支付渠道" prop="payChannel">
          <el-input v-model="form.payChannel" placeholder="请输入支付渠道" />
        </el-form-item>
        <el-form-item label="第三方交易号" prop="tradeNo">
          <el-input v-model="form.tradeNo" placeholder="请输入第三方交易号" />
        </el-form-item>
        <el-form-item label="交易完成时间" prop="tradeTime">
          <el-date-picker clearable
            v-model="form.tradeTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择交易完成时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="当前归属部门/学校" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入当前归属部门/学校" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="删除标记，0正常 1删除" prop="delFlag">
          <el-input v-model="form.delFlag" placeholder="请输入删除标记，0正常 1删除" />
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
import { listFinanceFlow, getFinanceFlow, delFinanceFlow, addFinanceFlow, updateFinanceFlow } from "@/api/supplier/financeFlow";

export default {
  name: "FinanceFlow",
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
      // 财务流水表格数据
      financeFlowList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        flowNo: null,
        businessType: null,
        orderId: null,
        afterSaleId: null,
        schoolId: null,
        supplierId: null,
        userId: null,
        amount: null,
        payChannel: null,
        tradeNo: null,
        status: null,
        tradeTime: null,
        deptId: null,
        ancestors: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        flowNo: [
          { required: true, message: "流水号不能为空", trigger: "blur" }
        ],
        businessType: [
          { required: true, message: "业务类型，1支付 2退款 3结算 4提现不能为空", trigger: "change" }
        ],
        amount: [
          { required: true, message: "金额不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "状态，0待处理 1成功 2失败 3关闭不能为空", trigger: "change" }
        ],
        delFlag: [
          { required: true, message: "删除标记，0正常 1删除不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询财务流水列表 */
    getList() {
      this.loading = true;
      listFinanceFlow(this.queryParams).then(response => {
        this.financeFlowList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        flowNo: null,
        businessType: null,
        orderId: null,
        afterSaleId: null,
        schoolId: null,
        supplierId: null,
        userId: null,
        amount: null,
        payChannel: null,
        tradeNo: null,
        status: null,
        tradeTime: null,
        deptId: null,
        ancestors: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null,
        delFlag: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加财务流水";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getFinanceFlow(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改财务流水";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateFinanceFlow(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addFinanceFlow(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除财务流水编号为"' + ids + '"的数据项？').then(function() {
        return delFinanceFlow(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('supplier/financeFlow/export', {
        ...this.queryParams
      }, `financeFlow_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
