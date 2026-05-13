<template>
  <div class="app-container">
    <el-form
      ref="queryForm"
      :model="queryParams"
      size="small"
      :inline="true"
      v-show="showSearch"
      label-width="100px"
    >
      <el-form-item label="售后单号" prop="afterSaleNo">
        <el-input
          v-model="queryParams.afterSaleNo"
          placeholder="请输入售后单号"
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
          <el-option label="统购" value="1" />
          <el-option label="零售" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="申请类型" prop="applyType">
        <el-select v-model="queryParams.applyType" placeholder="请选择申请类型" clearable>
          <el-option label="仅退款" value="1" />
          <el-option label="退货退款" value="2" />
          <el-option label="换货" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="售后状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择售后状态" clearable>
          <el-option label="待审核" value="0" />
          <el-option label="已通过" value="1" />
          <el-option label="已驳回" value="2" />
          <el-option label="处理中" value="3" />
          <el-option label="已完成" value="4" />
          <el-option label="已关闭" value="5" />
        </el-select>
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

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['school:afterSale:add']"
        >
          新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['school:afterSale:edit']"
        >
          修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['school:afterSale:remove']"
        >
          删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['school:afterSale:export']"
        >
          导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="afterSaleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="售后单号" align="center" prop="afterSaleNo" />
      <el-table-column label="订单主表ID" align="center" prop="orderId" />
      <el-table-column label="订单明细ID" align="center" prop="orderItemId" />
      <el-table-column label="订单编号" align="center" prop="orderNo" />
      <el-table-column label="订单类型" align="center" prop="orderType" />
      <el-table-column label="学校ID" align="center" prop="schoolId" />
      <el-table-column label="供应商ID" align="center" prop="supplierId" />
      <el-table-column label="发起用户ID" align="center" prop="userId" />
      <el-table-column label="发起用户名" align="center" prop="userName" />
      <el-table-column label="学生姓名" align="center" prop="studentName" />
      <el-table-column label="申请类型" align="center" prop="applyType" />
      <el-table-column label="换货目标尺码ID" align="center" prop="targetSizeId" />
      <el-table-column label="换货目标尺码名称" align="center" prop="targetSizeName" />
      <el-table-column label="申请原因" align="center" prop="reason" />
      <el-table-column label="申请数量" align="center" prop="applyNum" />
      <el-table-column label="申请金额" align="center" prop="applyAmount" />
      <el-table-column label="实际退款金额" align="center" prop="actualRefundAmount" />
      <el-table-column label="退款状态" align="center" prop="refundStatus" />
      <el-table-column label="退款完成时间" align="center" prop="refundTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.refundTime, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column label="售后状态" align="center" prop="status" />
      <el-table-column label="审核人" align="center" prop="auditBy" />
      <el-table-column label="审核时间" align="center" prop="auditTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.auditTime, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column label="处理结果" align="center" prop="handleResult" />
      <el-table-column label="处理备注" align="center" prop="handleRemark" />
      <el-table-column label="完成时间" align="center" prop="finishTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.finishTime, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column label="部门ID" align="center" prop="deptId" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['school:afterSale:edit']"
          >
            修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['school:afterSale:remove']"
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

    <el-dialog :title="title" :visible.sync="open" width="560px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="售后单号" prop="afterSaleNo">
          <el-input v-model="form.afterSaleNo" placeholder="请输入售后单号" />
        </el-form-item>
        <el-form-item label="订单主表ID" prop="orderId">
          <el-input v-model="form.orderId" placeholder="请输入订单主表ID" />
        </el-form-item>
        <el-form-item label="订单明细ID" prop="orderItemId">
          <el-input v-model="form.orderItemId" placeholder="请输入订单明细ID" />
        </el-form-item>
        <el-form-item label="订单编号" prop="orderNo">
          <el-input v-model="form.orderNo" placeholder="请输入订单编号" />
        </el-form-item>
        <el-form-item label="订单类型" prop="orderType">
          <el-select v-model="form.orderType" placeholder="请选择订单类型" style="width: 100%">
            <el-option label="统购" value="1" />
            <el-option label="零售" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="学校ID" prop="schoolId">
          <el-input v-model="form.schoolId" placeholder="请输入学校ID" />
        </el-form-item>
        <el-form-item label="供应商ID" prop="supplierId">
          <el-input v-model="form.supplierId" placeholder="请输入供应商ID" />
        </el-form-item>
        <el-form-item label="发起用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入发起用户ID" />
        </el-form-item>
        <el-form-item label="发起用户名" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入发起用户名" />
        </el-form-item>
        <el-form-item label="学生姓名" prop="studentName">
          <el-input v-model="form.studentName" placeholder="请输入学生姓名" />
        </el-form-item>
        <el-form-item label="申请类型" prop="applyType">
          <el-select v-model="form.applyType" placeholder="请选择申请类型" style="width: 100%">
            <el-option label="仅退款" value="1" />
            <el-option label="退货退款" value="2" />
            <el-option label="换货" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="换货目标尺码ID" prop="targetSizeId">
          <el-input v-model="form.targetSizeId" placeholder="请输入换货目标尺码ID" />
        </el-form-item>
        <el-form-item label="换货目标尺码名称" prop="targetSizeName">
          <el-input v-model="form.targetSizeName" placeholder="请输入换货目标尺码名称" />
        </el-form-item>
        <el-form-item label="申请原因" prop="reason">
          <el-input v-model="form.reason" type="textarea" placeholder="请输入申请原因" />
        </el-form-item>
        <el-form-item label="图片凭证JSON" prop="imagesJson">
          <el-input v-model="form.imagesJson" type="textarea" placeholder="请输入图片凭证JSON" />
        </el-form-item>
        <el-form-item label="申请数量" prop="applyNum">
          <el-input v-model="form.applyNum" placeholder="请输入申请数量" />
        </el-form-item>
        <el-form-item label="申请金额" prop="applyAmount">
          <el-input v-model="form.applyAmount" placeholder="请输入申请金额" />
        </el-form-item>
        <el-form-item label="实际退款金额" prop="actualRefundAmount">
          <el-input v-model="form.actualRefundAmount" placeholder="请输入实际退款金额" />
        </el-form-item>
        <el-form-item label="退款状态" prop="refundStatus">
          <el-select v-model="form.refundStatus" placeholder="请选择退款状态" clearable style="width: 100%">
            <el-option label="未退款" value="0" />
            <el-option label="退款中" value="1" />
            <el-option label="退款成功" value="2" />
            <el-option label="退款失败" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="退款完成时间" prop="refundTime">
          <el-date-picker
            clearable
            v-model="form.refundTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择退款完成时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="审核人" prop="auditBy">
          <el-input v-model="form.auditBy" placeholder="请输入审核人" />
        </el-form-item>
        <el-form-item label="审核时间" prop="auditTime">
          <el-date-picker
            clearable
            v-model="form.auditTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择审核时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="处理结果" prop="handleResult">
          <el-input v-model="form.handleResult" type="textarea" placeholder="请输入处理结果" />
        </el-form-item>
        <el-form-item label="处理备注" prop="handleRemark">
          <el-input v-model="form.handleRemark" type="textarea" placeholder="请输入处理备注" />
        </el-form-item>
        <el-form-item label="售后状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择售后状态" style="width: 100%">
            <el-option label="待审核" value="0" />
            <el-option label="已通过" value="1" />
            <el-option label="已驳回" value="2" />
            <el-option label="处理中" value="3" />
            <el-option label="已完成" value="4" />
            <el-option label="已关闭" value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="完成时间" prop="finishTime">
          <el-date-picker
            clearable
            v-model="form.finishTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择完成时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="部门ID" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入部门ID" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
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
  listAfterSale,
  getAfterSale,
  delAfterSale,
  addAfterSale,
  updateAfterSale,
} from "@/api/school/afterSale";

export default {
  name: "AfterSale",
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      afterSaleList: [],
      title: "",
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        afterSaleNo: null,
        orderNo: null,
        orderType: null,
        applyType: null,
        status: null,
      },
      form: {},
      rules: {
        afterSaleNo: [{ required: true, message: "售后单号不能为空", trigger: "blur" }],
        orderId: [{ required: true, message: "订单主表ID不能为空", trigger: "blur" }],
        orderNo: [{ required: true, message: "订单编号不能为空", trigger: "blur" }],
        orderType: [{ required: true, message: "订单类型不能为空", trigger: "change" }],
        supplierId: [{ required: true, message: "供应商ID不能为空", trigger: "blur" }],
        userId: [{ required: true, message: "发起用户ID不能为空", trigger: "blur" }],
        userName: [{ required: true, message: "发起用户名不能为空", trigger: "blur" }],
        applyType: [{ required: true, message: "申请类型不能为空", trigger: "change" }],
        applyNum: [{ required: true, message: "申请数量不能为空", trigger: "blur" }],
        status: [{ required: true, message: "售后状态不能为空", trigger: "change" }],
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.loading = true;
      listAfterSale(this.queryParams).then((response) => {
        this.afterSaleList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    cancel() {
      this.open = false;
      this.reset();
    },
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
        actualRefundAmount: null,
        refundStatus: null,
        refundTime: null,
        status: null,
        auditBy: null,
        auditTime: null,
        handleResult: null,
        handleRemark: null,
        finishTime: null,
        deptId: this.$store.state.user.deptId || null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null,
        delFlag: "0",
      };
      this.resetForm("form");
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.id);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加售后主";
    },
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getAfterSale(id).then((response) => {
        this.form = {
          ...this.form,
          ...response.data,
          delFlag: response.data.delFlag || "0",
        };
        this.open = true;
        this.title = "修改售后主";
      });
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          return;
        }
        this.form.delFlag = this.form.delFlag || "0";
        if (this.form.id != null) {
          updateAfterSale(this.form).then(() => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
        } else {
          addAfterSale(this.form).then(() => {
            this.$modal.msgSuccess("新增成功");
            this.open = false;
            this.getList();
          });
        }
      });
    },
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal
        .confirm('是否确认删除售后主编号为"' + ids + '"的数据项？')
        .then(function () {
          return delAfterSale(ids);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => {});
    },
    handleExport() {
      this.download(
        "school/afterSale/export",
        {
          ...this.queryParams,
        },
        `afterSale_${new Date().getTime()}.xlsx`
      );
    },
  },
};
</script>
