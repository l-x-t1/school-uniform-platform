<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="结算单号" prop="settlementNo">
        <el-input
          v-model="queryParams.settlementNo"
          placeholder="请输入结算单号"
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
      <el-form-item label="学校名称快照" prop="schoolName">
        <el-input
          v-model="queryParams.schoolName"
          placeholder="请输入学校名称快照"
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
      <el-form-item label="供应商名称快照" prop="supplierName">
        <el-input
          v-model="queryParams.supplierName"
          placeholder="请输入供应商名称快照"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="结算开始时间" prop="settlementPeriodStart">
        <el-date-picker clearable
          v-model="queryParams.settlementPeriodStart"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择结算开始时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="结算结束时间" prop="settlementPeriodEnd">
        <el-date-picker clearable
          v-model="queryParams.settlementPeriodEnd"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择结算结束时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="结算金额" prop="settlementAmount">
        <el-input
          v-model="queryParams.settlementAmount"
          placeholder="请输入结算金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="税额" prop="taxAmount">
        <el-input
          v-model="queryParams.taxAmount"
          placeholder="请输入税额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="已付金额" prop="paidAmount">
        <el-input
          v-model="queryParams.paidAmount"
          placeholder="请输入已付金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="未付金额" prop="unpaidAmount">
        <el-input
          v-model="queryParams.unpaidAmount"
          placeholder="请输入未付金额"
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
      <el-form-item label="审核人" prop="auditorName">
        <el-input
          v-model="queryParams.auditorName"
          placeholder="请输入审核人"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
          v-hasPermi="['supplier:settlementOrder:add']"
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
          v-hasPermi="['supplier:settlementOrder:edit']"
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
          v-hasPermi="['supplier:settlementOrder:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['supplier:settlementOrder:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="settlementOrderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="结算单号" align="center" prop="settlementNo" />
      <el-table-column label="学校ID" align="center" prop="schoolId" />
      <el-table-column label="学校名称快照" align="center" prop="schoolName" />
      <el-table-column label="供应商ID" align="center" prop="supplierId" />
      <el-table-column label="供应商名称快照" align="center" prop="supplierName" />
      <el-table-column label="结算开始时间" align="center" prop="settlementPeriodStart" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.settlementPeriodStart, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="结算结束时间" align="center" prop="settlementPeriodEnd" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.settlementPeriodEnd, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="订单数" align="center" prop="orderCount" />
      <el-table-column label="结算金额" align="center" prop="settlementAmount" />
      <el-table-column label="税额" align="center" prop="taxAmount" />
      <el-table-column label="已付金额" align="center" prop="paidAmount" />
      <el-table-column label="未付金额" align="center" prop="unpaidAmount" />
      <el-table-column label="状态，0待申请 1待审核 2已通过 3已完成" align="center" prop="status" />
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
      <el-table-column label="审核人" align="center" prop="auditorName" />
      <el-table-column label="审核备注" align="center" prop="auditRemark" />
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
            v-hasPermi="['supplier:settlementOrder:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['supplier:settlementOrder:remove']"
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

    <!-- 添加或修改结算单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="结算单号" prop="settlementNo">
          <el-input v-model="form.settlementNo" placeholder="请输入结算单号" />
        </el-form-item>
        <el-form-item label="学校ID" prop="schoolId">
          <el-input v-model="form.schoolId" placeholder="请输入学校ID" />
        </el-form-item>
        <el-form-item label="学校名称快照" prop="schoolName">
          <el-input v-model="form.schoolName" placeholder="请输入学校名称快照" />
        </el-form-item>
        <el-form-item label="供应商ID" prop="supplierId">
          <el-input v-model="form.supplierId" placeholder="请输入供应商ID" />
        </el-form-item>
        <el-form-item label="供应商名称快照" prop="supplierName">
          <el-input v-model="form.supplierName" placeholder="请输入供应商名称快照" />
        </el-form-item>
        <el-form-item label="结算开始时间" prop="settlementPeriodStart">
          <el-date-picker clearable
            v-model="form.settlementPeriodStart"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择结算开始时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结算结束时间" prop="settlementPeriodEnd">
          <el-date-picker clearable
            v-model="form.settlementPeriodEnd"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择结算结束时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结算金额" prop="settlementAmount">
          <el-input v-model="form.settlementAmount" placeholder="请输入结算金额" />
        </el-form-item>
        <el-form-item label="税额" prop="taxAmount">
          <el-input v-model="form.taxAmount" placeholder="请输入税额" />
        </el-form-item>
        <el-form-item label="已付金额" prop="paidAmount">
          <el-input v-model="form.paidAmount" placeholder="请输入已付金额" />
        </el-form-item>
        <el-form-item label="未付金额" prop="unpaidAmount">
          <el-input v-model="form.unpaidAmount" placeholder="请输入未付金额" />
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
        <el-form-item label="审核人" prop="auditorName">
          <el-input v-model="form.auditorName" placeholder="请输入审核人" />
        </el-form-item>
        <el-form-item label="审核备注" prop="auditRemark">
          <el-input v-model="form.auditRemark" type="textarea" placeholder="请输入内容" />
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
import { listSettlementOrder, getSettlementOrder, delSettlementOrder, addSettlementOrder, updateSettlementOrder } from "@/api/supplier/settlementOrder";

export default {
  name: "SettlementOrder",
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
      // 结算单表格数据
      settlementOrderList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        settlementNo: null,
        schoolId: null,
        schoolName: null,
        supplierId: null,
        supplierName: null,
        settlementPeriodStart: null,
        settlementPeriodEnd: null,
        orderCount: null,
        settlementAmount: null,
        taxAmount: null,
        paidAmount: null,
        unpaidAmount: null,
        status: null,
        applyTime: null,
        auditTime: null,
        auditorName: null,
        auditRemark: null,
        deptId: null,
        ancestors: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        settlementNo: [
          { required: true, message: "结算单号不能为空", trigger: "blur" }
        ],
        schoolId: [
          { required: true, message: "学校ID不能为空", trigger: "blur" }
        ],
        schoolName: [
          { required: true, message: "学校名称快照不能为空", trigger: "blur" }
        ],
        supplierId: [
          { required: true, message: "供应商ID不能为空", trigger: "blur" }
        ],
        supplierName: [
          { required: true, message: "供应商名称快照不能为空", trigger: "blur" }
        ],
        settlementPeriodStart: [
          { required: true, message: "结算开始时间不能为空", trigger: "blur" }
        ],
        settlementPeriodEnd: [
          { required: true, message: "结算结束时间不能为空", trigger: "blur" }
        ],
        orderCount: [
          { required: true, message: "订单数不能为空", trigger: "blur" }
        ],
        settlementAmount: [
          { required: true, message: "结算金额不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "状态，0待申请 1待审核 2已通过 3已完成不能为空", trigger: "change" }
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
    /** 查询结算单列表 */
    getList() {
      this.loading = true;
      listSettlementOrder(this.queryParams).then(response => {
        this.settlementOrderList = response.rows;
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
        settlementNo: null,
        schoolId: null,
        schoolName: null,
        supplierId: null,
        supplierName: null,
        settlementPeriodStart: null,
        settlementPeriodEnd: null,
        orderCount: null,
        settlementAmount: null,
        taxAmount: null,
        paidAmount: null,
        unpaidAmount: null,
        status: null,
        applyTime: null,
        auditTime: null,
        auditorName: null,
        auditRemark: null,
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
      this.title = "添加结算单";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getSettlementOrder(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改结算单";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateSettlementOrder(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addSettlementOrder(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除结算单编号为"' + ids + '"的数据项？').then(function() {
        return delSettlementOrder(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('supplier/settlementOrder/export', {
        ...this.queryParams
      }, `settlementOrder_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
