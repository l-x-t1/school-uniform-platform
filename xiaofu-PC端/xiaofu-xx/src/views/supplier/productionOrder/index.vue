<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="生产单号" prop="productionNo">
        <el-input
          v-model="queryParams.productionNo"
          placeholder="请输入生产单号"
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
      <el-form-item label="计划完成时间" prop="plannedFinishTime">
        <el-date-picker clearable
          v-model="queryParams.plannedFinishTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择计划完成时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="开始生产时间" prop="startTime">
        <el-date-picker clearable
          v-model="queryParams.startTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择开始生产时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="生产完成时间" prop="finishTime">
        <el-date-picker clearable
          v-model="queryParams.finishTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择生产完成时间">
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
          v-hasPermi="['supplier:productionOrder:add']"
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
          v-hasPermi="['supplier:productionOrder:edit']"
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
          v-hasPermi="['supplier:productionOrder:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['supplier:productionOrder:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="productionOrderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="生产单号" align="center" prop="productionNo" />
      <el-table-column label="统购公告ID" align="center" prop="noticeId" />
      <el-table-column label="订单ID" align="center" prop="orderId" />
      <el-table-column label="订单号快照" align="center" prop="orderNo" />
      <el-table-column label="订单类型，1统购 2零售" align="center" prop="orderType" />
      <el-table-column label="学校ID" align="center" prop="schoolId" />
      <el-table-column label="供应商ID" align="center" prop="supplierId" />
      <el-table-column label="状态，0待生产 1生产中 2生产完成" align="center" prop="status" />
      <el-table-column label="计划完成时间" align="center" prop="plannedFinishTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.plannedFinishTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="开始生产时间" align="center" prop="startTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="生产完成时间" align="center" prop="finishTime" width="180">
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
            v-hasPermi="['supplier:productionOrder:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['supplier:productionOrder:remove']"
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

    <!-- 添加或修改生产单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="生产单号" prop="productionNo">
          <el-input v-model="form.productionNo" placeholder="请输入生产单号" />
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
        <el-form-item label="学校ID" prop="schoolId">
          <el-input v-model="form.schoolId" placeholder="请输入学校ID" />
        </el-form-item>
        <el-form-item label="供应商ID" prop="supplierId">
          <el-input v-model="form.supplierId" placeholder="请输入供应商ID" />
        </el-form-item>
        <el-form-item label="计划完成时间" prop="plannedFinishTime">
          <el-date-picker clearable
            v-model="form.plannedFinishTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择计划完成时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="开始生产时间" prop="startTime">
          <el-date-picker clearable
            v-model="form.startTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择开始生产时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="生产完成时间" prop="finishTime">
          <el-date-picker clearable
            v-model="form.finishTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择生产完成时间">
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
import { listProductionOrder, getProductionOrder, delProductionOrder, addProductionOrder, updateProductionOrder } from "@/api/supplier/productionOrder";

export default {
  name: "ProductionOrder",
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
      // 生产单表格数据
      productionOrderList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        productionNo: null,
        noticeId: null,
        orderId: null,
        orderNo: null,
        orderType: null,
        schoolId: null,
        supplierId: null,
        status: null,
        plannedFinishTime: null,
        startTime: null,
        finishTime: null,
        deptId: null,
        ancestors: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        productionNo: [
          { required: true, message: "生产单号不能为空", trigger: "blur" }
        ],
        orderType: [
          { required: true, message: "订单类型，1统购 2零售不能为空", trigger: "change" }
        ],
        supplierId: [
          { required: true, message: "供应商ID不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "状态，0待生产 1生产中 2生产完成不能为空", trigger: "change" }
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
    /** 查询生产单列表 */
    getList() {
      this.loading = true;
      listProductionOrder(this.queryParams).then(response => {
        this.productionOrderList = response.rows;
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
        productionNo: null,
        noticeId: null,
        orderId: null,
        orderNo: null,
        orderType: null,
        schoolId: null,
        supplierId: null,
        status: null,
        plannedFinishTime: null,
        startTime: null,
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
      this.title = "添加生产单";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getProductionOrder(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改生产单";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateProductionOrder(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addProductionOrder(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除生产单编号为"' + ids + '"的数据项？').then(function() {
        return delProductionOrder(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('supplier/productionOrder/export', {
        ...this.queryParams
      }, `productionOrder_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
