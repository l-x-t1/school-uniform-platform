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
      <el-form-item label="订单号" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单号"
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
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="待生产" value="0" />
          <el-option label="生产中" value="1" />
          <el-option label="生产完成" value="2" />
        </el-select>
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
          v-hasPermi="['school:productionOrder:add']"
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
          v-hasPermi="['school:productionOrder:edit']"
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
          v-hasPermi="['school:productionOrder:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['school:productionOrder:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="productionOrderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="生产单号" align="center" prop="productionNo" min-width="180" show-overflow-tooltip />
      <el-table-column label="订单号" align="center" prop="orderNo" min-width="180" show-overflow-tooltip />
      <el-table-column label="订单类型" align="center" prop="orderType">
        <template slot-scope="scope">
          <span>{{ formatOrderType(scope.row.orderType) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <span>{{ formatStatus(scope.row.status) }}</span>
        </template>
      </el-table-column>
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
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['school:productionOrder:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['school:productionOrder:remove']"
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
        <el-form-item label="计划完成时间" prop="plannedFinishTime">
          <el-date-picker clearable
            v-model="form.plannedFinishTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择计划完成时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-divider content-position="center">生产单明细信息</el-divider>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAddXfptProductionOrderItem">添加</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" icon="el-icon-delete" size="mini" @click="handleDeleteXfptProductionOrderItem">删除</el-button>
          </el-col>
        </el-row>
        <el-table :data="xfptProductionOrderItemList" :row-class-name="rowXfptProductionOrderItemIndex" @selection-change="handleXfptProductionOrderItemSelectionChange" ref="xfptProductionOrderItem">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="序号" align="center" prop="index" width="50"/>
          <el-table-column label="商品名称" prop="goodsName" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.goodsName" placeholder="请输入商品名称" />
            </template>
          </el-table-column>
          <el-table-column label="尺码名称" prop="sizeName" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.sizeName" placeholder="请输入尺码名称" />
            </template>
          </el-table-column>
          <el-table-column label="应生产数量" prop="buyNum" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.buyNum" placeholder="请输入应生产数量" />
            </template>
          </el-table-column>
          <el-table-column label="状态，0待生产 1生产中 2生产完成" prop="status" width="150">
            <template slot-scope="scope">
              <el-select v-model="scope.row.status" placeholder="请选择状态，0待生产 1生产中 2生产完成">
                <el-option label="待生产" value="0" />
                <el-option label="生产中" value="1" />
                <el-option label="生产完成" value="2" />
              </el-select>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listProductionOrder, getProductionOrder, delProductionOrder, addProductionOrder, updateProductionOrder } from "@/api/school/productionOrder";

export default {
  name: "ProductionOrder",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 子表选中数据
      checkedXfptProductionOrderItem: [],
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
      // 生产单明细表格数据
      xfptProductionOrderItemList: [],
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
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        productionNo: [
          { required: true, message: "生产单号不能为空", trigger: "blur" }
        ],
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
    formatOrderType(orderType) {
      const orderTypeMap = {
        "1": "统购",
        "2": "零售"
      };
      return orderTypeMap[String(orderType)] || orderType || "-";
    },
    formatStatus(status) {
      const statusMap = {
        "0": "待生产",
        "1": "生产中",
        "2": "生产完成"
      };
      return statusMap[String(status)] || status || "-";
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
        orderType: "2",
        schoolId: null,
        supplierId: null,
        status: "0",
        plannedFinishTime: null,
        startTime: null,
        finishTime: null,
        deptId: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null,
        delFlag: "0"
      };
      this.xfptProductionOrderItemList = [];
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
        this.xfptProductionOrderItemList = response.data.xfptProductionOrderItemList;
        this.open = true;
        this.title = "修改生产单";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.form.xfptProductionOrderItemList = this.xfptProductionOrderItemList;
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
	/** 生产单明细序号 */
    rowXfptProductionOrderItemIndex({ row, rowIndex }) {
      row.index = rowIndex + 1;
    },
    /** 生产单明细添加按钮操作 */
    handleAddXfptProductionOrderItem() {
      let obj = {};
      obj.orderItemId = "";
      obj.goodsId = "";
      obj.sizeId = "";
      obj.goodsName = "";
      obj.sizeName = "";
      obj.buyNum = "";
      obj.finishNum = 0;
      obj.status = "0";
      obj.remark = "";
      obj.delFlag = "0";
      this.xfptProductionOrderItemList.push(obj);
    },
    /** 生产单明细删除按钮操作 */
    handleDeleteXfptProductionOrderItem() {
      if (this.checkedXfptProductionOrderItem.length == 0) {
        this.$modal.msgError("请先选择要删除的生产单明细数据");
      } else {
        const xfptProductionOrderItemList = this.xfptProductionOrderItemList;
        const checkedXfptProductionOrderItem = this.checkedXfptProductionOrderItem;
        this.xfptProductionOrderItemList = xfptProductionOrderItemList.filter(function(item) {
          return checkedXfptProductionOrderItem.indexOf(item.index) == -1
        });
      }
    },
    /** 复选框选中数据 */
    handleXfptProductionOrderItemSelectionChange(selection) {
      this.checkedXfptProductionOrderItem = selection.map(item => item.index)
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('school/productionOrder/export', {
        ...this.queryParams
      }, `productionOrder_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
