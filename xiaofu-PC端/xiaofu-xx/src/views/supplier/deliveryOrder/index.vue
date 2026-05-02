<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="发货单号" prop="deliveryNo">
        <el-input
          v-model="queryParams.deliveryNo"
          placeholder="请输入发货单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="统购公告ID" prop="noticeId">
        <el-input
          v-model="queryParams.noticeId"
          placeholder="请输入统购公告ID"
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
      <el-form-item label="订单号快照" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单号快照"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="生产单ID" prop="productionId">
        <el-input
          v-model="queryParams.productionId"
          placeholder="请输入生产单ID"
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
      <el-form-item label="收货人姓名快照" prop="receiverName">
        <el-input
          v-model="queryParams.receiverName"
          placeholder="请输入收货人姓名快照"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="收货人电话快照" prop="receiverPhone">
        <el-input
          v-model="queryParams.receiverPhone"
          placeholder="请输入收货人电话快照"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="物流公司" prop="logisticsCompany">
        <el-input
          v-model="queryParams.logisticsCompany"
          placeholder="请输入物流公司"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="物流单号" prop="logisticsNo">
        <el-input
          v-model="queryParams.logisticsNo"
          placeholder="请输入物流单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发货时间" prop="deliveryTime">
        <el-date-picker clearable
          v-model="queryParams.deliveryTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择发货时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="签收时间" prop="signTime">
        <el-date-picker clearable
          v-model="queryParams.signTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择签收时间">
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
          v-hasPermi="['supplier:deliveryOrder:add']"
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
          v-hasPermi="['supplier:deliveryOrder:edit']"
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
          v-hasPermi="['supplier:deliveryOrder:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['supplier:deliveryOrder:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="deliveryOrderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="发货单号" align="center" prop="deliveryNo" />
      <el-table-column label="统购公告ID" align="center" prop="noticeId" />
      <el-table-column label="订单ID" align="center" prop="orderId" />
      <el-table-column label="订单号快照" align="center" prop="orderNo" />
      <el-table-column label="生产单ID" align="center" prop="productionId" />
      <el-table-column label="学校ID" align="center" prop="schoolId" />
      <el-table-column label="供应商ID" align="center" prop="supplierId" />
      <el-table-column label="收货人姓名快照" align="center" prop="receiverName" />
      <el-table-column label="收货人电话快照" align="center" prop="receiverPhone" />
      <el-table-column label="收货地址快照" align="center" prop="receiverAddress" />
      <el-table-column label="物流公司" align="center" prop="logisticsCompany" />
      <el-table-column label="物流单号" align="center" prop="logisticsNo" />
      <el-table-column label="发货状态，0待发货 1已发货 2已签收" align="center" prop="deliveryStatus" />
      <el-table-column label="发货时间" align="center" prop="deliveryTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.deliveryTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="签收时间" align="center" prop="signTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.signTime, '{y}-{m}-{d}') }}</span>
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
            v-hasPermi="['supplier:deliveryOrder:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['supplier:deliveryOrder:remove']"
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

    <!-- 添加或修改发货单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="发货单号" prop="deliveryNo">
          <el-input v-model="form.deliveryNo" placeholder="请输入发货单号" />
        </el-form-item>
        <el-form-item label="统购公告ID" prop="noticeId">
          <el-input v-model="form.noticeId" placeholder="请输入统购公告ID" />
        </el-form-item>
        <el-form-item label="订单ID" prop="orderId">
          <el-input v-model="form.orderId" placeholder="请输入订单ID" />
        </el-form-item>
        <el-form-item label="订单号快照" prop="orderNo">
          <el-input v-model="form.orderNo" placeholder="请输入订单号快照" />
        </el-form-item>
        <el-form-item label="生产单ID" prop="productionId">
          <el-input v-model="form.productionId" placeholder="请输入生产单ID" />
        </el-form-item>
        <el-form-item label="学校ID" prop="schoolId">
          <el-input v-model="form.schoolId" placeholder="请输入学校ID" />
        </el-form-item>
        <el-form-item label="供应商ID" prop="supplierId">
          <el-input v-model="form.supplierId" placeholder="请输入供应商ID" />
        </el-form-item>
        <el-form-item label="收货人姓名快照" prop="receiverName">
          <el-input v-model="form.receiverName" placeholder="请输入收货人姓名快照" />
        </el-form-item>
        <el-form-item label="收货人电话快照" prop="receiverPhone">
          <el-input v-model="form.receiverPhone" placeholder="请输入收货人电话快照" />
        </el-form-item>
        <el-form-item label="收货地址快照" prop="receiverAddress">
          <el-input v-model="form.receiverAddress" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="物流公司" prop="logisticsCompany">
          <el-input v-model="form.logisticsCompany" placeholder="请输入物流公司" />
        </el-form-item>
        <el-form-item label="物流单号" prop="logisticsNo">
          <el-input v-model="form.logisticsNo" placeholder="请输入物流单号" />
        </el-form-item>
        <el-form-item label="发货时间" prop="deliveryTime">
          <el-date-picker clearable
            v-model="form.deliveryTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择发货时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="签收时间" prop="signTime">
          <el-date-picker clearable
            v-model="form.signTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择签收时间">
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
import { listDeliveryOrder, getDeliveryOrder, delDeliveryOrder, addDeliveryOrder, updateDeliveryOrder } from "@/api/supplier/deliveryOrder";

export default {
  name: "DeliveryOrder",
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
      // 发货单表格数据
      deliveryOrderList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        deliveryNo: null,
        noticeId: null,
        orderId: null,
        orderNo: null,
        productionId: null,
        schoolId: null,
        supplierId: null,
        receiverName: null,
        receiverPhone: null,
        receiverAddress: null,
        logisticsCompany: null,
        logisticsNo: null,
        deliveryStatus: null,
        deliveryTime: null,
        signTime: null,
        deptId: null,
        ancestors: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        deliveryNo: [
          { required: true, message: "发货单号不能为空", trigger: "blur" }
        ],
        supplierId: [
          { required: true, message: "供应商ID不能为空", trigger: "blur" }
        ],
        receiverName: [
          { required: true, message: "收货人姓名快照不能为空", trigger: "blur" }
        ],
        receiverPhone: [
          { required: true, message: "收货人电话快照不能为空", trigger: "blur" }
        ],
        receiverAddress: [
          { required: true, message: "收货地址快照不能为空", trigger: "blur" }
        ],
        deliveryStatus: [
          { required: true, message: "发货状态，0待发货 1已发货 2已签收不能为空", trigger: "change" }
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
    /** 查询发货单列表 */
    getList() {
      this.loading = true;
      listDeliveryOrder(this.queryParams).then(response => {
        this.deliveryOrderList = response.rows;
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
        deliveryNo: null,
        noticeId: null,
        orderId: null,
        orderNo: null,
        productionId: null,
        schoolId: null,
        supplierId: null,
        receiverName: null,
        receiverPhone: null,
        receiverAddress: null,
        logisticsCompany: null,
        logisticsNo: null,
        deliveryStatus: null,
        deliveryTime: null,
        signTime: null,
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
      this.title = "添加发货单";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getDeliveryOrder(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改发货单";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateDeliveryOrder(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addDeliveryOrder(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除发货单编号为"' + ids + '"的数据项？').then(function() {
        return delDeliveryOrder(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('supplier/deliveryOrder/export', {
        ...this.queryParams
      }, `deliveryOrder_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
