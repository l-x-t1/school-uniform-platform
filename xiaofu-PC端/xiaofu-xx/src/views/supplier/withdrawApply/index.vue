<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="提现单号" prop="withdrawNo">
        <el-input
          v-model="queryParams.withdrawNo"
          placeholder="请输入提现单号"
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
      <el-form-item label="申请提现金额" prop="applyAmount">
        <el-input
          v-model="queryParams.applyAmount"
          placeholder="请输入申请提现金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申请时间" prop="applyTime">
        <el-date-picker clearable
          v-model="queryParams.applyTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择申请时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="审核时间" prop="auditTime">
        <el-date-picker clearable
          v-model="queryParams.auditTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择审核时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="打款时间" prop="payTime">
        <el-date-picker clearable
          v-model="queryParams.payTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择打款时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="财务流水ID" prop="financeFlowId">
        <el-input
          v-model="queryParams.financeFlowId"
          placeholder="请输入财务流水ID"
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
          v-hasPermi="['supplier:withdrawApply:add']"
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
          v-hasPermi="['supplier:withdrawApply:edit']"
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
          v-hasPermi="['supplier:withdrawApply:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['supplier:withdrawApply:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="withdrawApplyList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="提现单号" align="center" prop="withdrawNo" />
      <el-table-column label="供应商ID" align="center" prop="supplierId" />
      <el-table-column label="申请提现金额" align="center" prop="applyAmount" />
      <el-table-column label="银行账户信息JSON文本" align="center" prop="bankInfoJson" />
      <el-table-column label="状态，0待审核 1已通过 2已驳回 3已打款" align="center" prop="status" />
      <el-table-column label="申请时间" align="center" prop="applyTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.applyTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审核时间" align="center" prop="auditTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.auditTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="打款时间" align="center" prop="payTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.payTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审核意见" align="center" prop="auditRemark" />
      <el-table-column label="财务流水ID" align="center" prop="financeFlowId" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['supplier:withdrawApply:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['supplier:withdrawApply:remove']"
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

    <!-- 添加或修改提现申请对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="提现单号" prop="withdrawNo">
          <el-input v-model="form.withdrawNo" placeholder="请输入提现单号" />
        </el-form-item>
        <el-form-item label="供应商ID" prop="supplierId">
          <el-input v-model="form.supplierId" placeholder="请输入供应商ID" />
        </el-form-item>
        <el-form-item label="申请提现金额" prop="applyAmount">
          <el-input v-model="form.applyAmount" placeholder="请输入申请提现金额" />
        </el-form-item>
        <el-form-item label="银行账户信息JSON文本" prop="bankInfoJson">
          <el-input v-model="form.bankInfoJson" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="申请时间" prop="applyTime">
          <el-date-picker clearable
            v-model="form.applyTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择申请时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="审核时间" prop="auditTime">
          <el-date-picker clearable
            v-model="form.auditTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择审核时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="打款时间" prop="payTime">
          <el-date-picker clearable
            v-model="form.payTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择打款时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="审核意见" prop="auditRemark">
          <el-input v-model="form.auditRemark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="财务流水ID" prop="financeFlowId">
          <el-input v-model="form.financeFlowId" placeholder="请输入财务流水ID" />
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
import { listWithdrawApply, getWithdrawApply, delWithdrawApply, addWithdrawApply, updateWithdrawApply } from "@/api/supplier/withdrawApply";

export default {
  name: "WithdrawApply",
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
      // 提现申请表格数据
      withdrawApplyList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        withdrawNo: null,
        supplierId: null,
        applyAmount: null,
        bankInfoJson: null,
        status: null,
        applyTime: null,
        auditTime: null,
        payTime: null,
        auditRemark: null,
        financeFlowId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        withdrawNo: [
          { required: true, message: "提现单号不能为空", trigger: "blur" }
        ],
        supplierId: [
          { required: true, message: "供应商ID不能为空", trigger: "blur" }
        ],
        applyAmount: [
          { required: true, message: "申请提现金额不能为空", trigger: "blur" }
        ],
        bankInfoJson: [
          { required: true, message: "银行账户信息JSON文本不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "状态，0待审核 1已通过 2已驳回 3已打款不能为空", trigger: "change" }
        ],
        applyTime: [
          { required: true, message: "申请时间不能为空", trigger: "blur" }
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
    /** 查询提现申请列表 */
    getList() {
      this.loading = true;
      listWithdrawApply(this.queryParams).then(response => {
        this.withdrawApplyList = response.rows;
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
        withdrawNo: null,
        supplierId: null,
        applyAmount: null,
        bankInfoJson: null,
        status: null,
        applyTime: null,
        auditTime: null,
        payTime: null,
        auditRemark: null,
        financeFlowId: null,
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
      this.title = "添加提现申请";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getWithdrawApply(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改提现申请";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateWithdrawApply(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addWithdrawApply(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除提现申请编号为"' + ids + '"的数据项？').then(function() {
        return delWithdrawApply(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('supplier/withdrawApply/export', {
        ...this.queryParams
      }, `withdrawApply_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
