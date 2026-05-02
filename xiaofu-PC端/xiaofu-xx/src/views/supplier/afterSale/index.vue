<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="售后单号" prop="afterSaleNo">
        <el-input
          v-model="queryParams.afterSaleNo"
          placeholder="请输入售后单号"
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
      <el-form-item label="订单明细ID" prop="orderItemId">
        <el-input
          v-model="queryParams.orderItemId"
          placeholder="请输入订单明细ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="订单号快照" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单号快照"
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
      <el-form-item label="申请人用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入申请人用户ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申请人姓名" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入申请人姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="学生姓名快照" prop="studentName">
        <el-input
          v-model="queryParams.studentName"
          placeholder="请输入学生姓名快照"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="目标尺码ID" prop="targetSizeId">
        <el-input
          v-model="queryParams.targetSizeId"
          placeholder="请输入目标尺码ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="目标尺码名称快照" prop="targetSizeName">
        <el-input
          v-model="queryParams.targetSizeName"
          placeholder="请输入目标尺码名称快照"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申请金额" prop="applyAmount">
        <el-input
          v-model="queryParams.applyAmount"
          placeholder="请输入申请金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="审核人" prop="auditBy">
        <el-input
          v-model="queryParams.auditBy"
          placeholder="请输入审核人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="审核时间" prop="auditTime">
        <el-date-picker clearable
          v-model="queryParams.auditTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择审核时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="完成时间" prop="finishTime">
        <el-date-picker clearable
          v-model="queryParams.finishTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择完成时间">
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
          v-hasPermi="['supplier:afterSale:add']"
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
          v-hasPermi="['supplier:afterSale:edit']"
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
          v-hasPermi="['supplier:afterSale:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['supplier:afterSale:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="afterSaleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="售后单号" align="center" prop="afterSaleNo" />
      <el-table-column label="订单ID" align="center" prop="orderId" />
      <el-table-column label="订单明细ID" align="center" prop="orderItemId" />
      <el-table-column label="订单号快照" align="center" prop="orderNo" />
      <el-table-column label="订单类型，1统购 2零售" align="center" prop="orderType" />
      <el-table-column label="学校ID" align="center" prop="schoolId" />
      <el-table-column label="供应商ID" align="center" prop="supplierId" />
      <el-table-column label="申请人用户ID" align="center" prop="userId" />
      <el-table-column label="申请人姓名" align="center" prop="userName" />
      <el-table-column label="学生姓名快照" align="center" prop="studentName" />
      <el-table-column label="申请类型，1退货 2换货 3退款 4尺码调换" align="center" prop="applyType" />
      <el-table-column label="目标尺码ID" align="center" prop="targetSizeId" />
      <el-table-column label="目标尺码名称快照" align="center" prop="targetSizeName" />
      <el-table-column label="申请原因" align="center" prop="reason" />
      <el-table-column label="凭证图片JSON文本" align="center" prop="imagesJson" />
      <el-table-column label="申请数量" align="center" prop="applyNum" />
      <el-table-column label="申请金额" align="center" prop="applyAmount" />
      <el-table-column label="状态，0待审核 1通过 2驳回 3处理中 4已完成" align="center" prop="status" />
      <el-table-column label="审核人" align="center" prop="auditBy" />
      <el-table-column label="审核时间" align="center" prop="auditTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.auditTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="处理结果" align="center" prop="handleResult" />
      <el-table-column label="处理备注" align="center" prop="handleRemark" />
      <el-table-column label="完成时间" align="center" prop="finishTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.finishTime, '{y}-{m}-{d}') }}</span>
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
            v-hasPermi="['supplier:afterSale:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['supplier:afterSale:remove']"
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

    <!-- 添加或修改售后单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="售后单号" prop="afterSaleNo">
          <el-input v-model="form.afterSaleNo" placeholder="请输入售后单号" />
        </el-form-item>
        <el-form-item label="订单ID" prop="orderId">
          <el-input v-model="form.orderId" placeholder="请输入订单ID" />
        </el-form-item>
        <el-form-item label="订单明细ID" prop="orderItemId">
          <el-input v-model="form.orderItemId" placeholder="请输入订单明细ID" />
        </el-form-item>
        <el-form-item label="订单号快照" prop="orderNo">
          <el-input v-model="form.orderNo" placeholder="请输入订单号快照" />
        </el-form-item>
        <el-form-item label="学校ID" prop="schoolId">
          <el-input v-model="form.schoolId" placeholder="请输入学校ID" />
        </el-form-item>
        <el-form-item label="供应商ID" prop="supplierId">
          <el-input v-model="form.supplierId" placeholder="请输入供应商ID" />
        </el-form-item>
        <el-form-item label="申请人用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入申请人用户ID" />
        </el-form-item>
        <el-form-item label="申请人姓名" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入申请人姓名" />
        </el-form-item>
        <el-form-item label="学生姓名快照" prop="studentName">
          <el-input v-model="form.studentName" placeholder="请输入学生姓名快照" />
        </el-form-item>
        <el-form-item label="目标尺码ID" prop="targetSizeId">
          <el-input v-model="form.targetSizeId" placeholder="请输入目标尺码ID" />
        </el-form-item>
        <el-form-item label="目标尺码名称快照" prop="targetSizeName">
          <el-input v-model="form.targetSizeName" placeholder="请输入目标尺码名称快照" />
        </el-form-item>
        <el-form-item label="申请原因" prop="reason">
          <el-input v-model="form.reason" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="凭证图片JSON文本" prop="imagesJson">
          <el-input v-model="form.imagesJson" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="申请金额" prop="applyAmount">
          <el-input v-model="form.applyAmount" placeholder="请输入申请金额" />
        </el-form-item>
        <el-form-item label="审核人" prop="auditBy">
          <el-input v-model="form.auditBy" placeholder="请输入审核人" />
        </el-form-item>
        <el-form-item label="审核时间" prop="auditTime">
          <el-date-picker clearable
            v-model="form.auditTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择审核时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="处理结果" prop="handleResult">
          <el-input v-model="form.handleResult" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="处理备注" prop="handleRemark">
          <el-input v-model="form.handleRemark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="完成时间" prop="finishTime">
          <el-date-picker clearable
            v-model="form.finishTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择完成时间">
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
import { listAfterSale, getAfterSale, delAfterSale, addAfterSale, updateAfterSale } from "@/api/supplier/afterSale";

export default {
  name: "AfterSale",
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
      // 售后单表格数据
      afterSaleList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        afterSaleNo: null,
        orderId: null,
        orderItemId: null,
        orderNo: null,
        orderType: null,
        schoolId: null,
        supplierId: null,
        userId: null,
        userName: null,
        studentName: null,
        applyType: null,
        targetSizeId: null,
        targetSizeName: null,
        reason: null,
        imagesJson: null,
        applyNum: null,
        applyAmount: null,
        status: null,
        auditBy: null,
        auditTime: null,
        handleResult: null,
        handleRemark: null,
        finishTime: null,
        deptId: null,
        ancestors: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        afterSaleNo: [
          { required: true, message: "售后单号不能为空", trigger: "blur" }
        ],
        orderId: [
          { required: true, message: "订单ID不能为空", trigger: "blur" }
        ],
        orderItemId: [
          { required: true, message: "订单明细ID不能为空", trigger: "blur" }
        ],
        orderNo: [
          { required: true, message: "订单号快照不能为空", trigger: "blur" }
        ],
        orderType: [
          { required: true, message: "订单类型，1统购 2零售不能为空", trigger: "change" }
        ],
        supplierId: [
          { required: true, message: "供应商ID不能为空", trigger: "blur" }
        ],
        userId: [
          { required: true, message: "申请人用户ID不能为空", trigger: "blur" }
        ],
        userName: [
          { required: true, message: "申请人姓名不能为空", trigger: "blur" }
        ],
        applyType: [
          { required: true, message: "申请类型，1退货 2换货 3退款 4尺码调换不能为空", trigger: "change" }
        ],
        status: [
          { required: true, message: "状态，0待审核 1通过 2驳回 3处理中 4已完成不能为空", trigger: "change" }
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
    /** 查询售后单列表 */
    getList() {
      this.loading = true;
      listAfterSale(this.queryParams).then(response => {
        this.afterSaleList = response.rows;
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
        afterSaleNo: null,
        orderId: null,
        orderItemId: null,
        orderNo: null,
        orderType: null,
        schoolId: null,
        supplierId: null,
        userId: null,
        userName: null,
        studentName: null,
        applyType: null,
        targetSizeId: null,
        targetSizeName: null,
        reason: null,
        imagesJson: null,
        applyNum: null,
        applyAmount: null,
        status: null,
        auditBy: null,
        auditTime: null,
        handleResult: null,
        handleRemark: null,
        finishTime: null,
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
      this.title = "添加售后单";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getAfterSale(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改售后单";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateAfterSale(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addAfterSale(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除售后单编号为"' + ids + '"的数据项？').then(function() {
        return delAfterSale(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('supplier/afterSale/export', {
        ...this.queryParams
      }, `afterSale_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
