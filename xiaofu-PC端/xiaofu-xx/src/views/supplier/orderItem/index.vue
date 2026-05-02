<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="订单主表ID" prop="orderId">
        <el-input
          v-model="queryParams.orderId"
          placeholder="请输入订单主表ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品ID" prop="goodsId">
        <el-input
          v-model="queryParams.goodsId"
          placeholder="请输入商品ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="尺码ID" prop="sizeId">
        <el-input
          v-model="queryParams.sizeId"
          placeholder="请输入尺码ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品名称快照" prop="goodsName">
        <el-input
          v-model="queryParams.goodsName"
          placeholder="请输入商品名称快照"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="尺码名称快照" prop="sizeName">
        <el-input
          v-model="queryParams.sizeName"
          placeholder="请输入尺码名称快照"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="单价" prop="unitPrice">
        <el-input
          v-model="queryParams.unitPrice"
          placeholder="请输入单价"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="行金额" prop="itemAmount">
        <el-input
          v-model="queryParams.itemAmount"
          placeholder="请输入行金额"
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
          v-hasPermi="['supplier:orderItem:add']"
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
          v-hasPermi="['supplier:orderItem:edit']"
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
          v-hasPermi="['supplier:orderItem:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['supplier:orderItem:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderItemList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="订单主表ID" align="center" prop="orderId" />
      <el-table-column label="商品ID" align="center" prop="goodsId" />
      <el-table-column label="尺码ID" align="center" prop="sizeId" />
      <el-table-column label="商品名称快照" align="center" prop="goodsName" />
      <el-table-column label="尺码名称快照" align="center" prop="sizeName" />
      <el-table-column label="商品主图快照" align="center" prop="mainImage" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.mainImage" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="销售类型，1统购 2零售" align="center" prop="saleType" />
      <el-table-column label="单价" align="center" prop="unitPrice" />
      <el-table-column label="购买数量" align="center" prop="buyNum" />
      <el-table-column label="行金额" align="center" prop="itemAmount" />
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
            v-hasPermi="['supplier:orderItem:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['supplier:orderItem:remove']"
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

    <!-- 添加或修改订单明细对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="订单主表ID" prop="orderId">
          <el-input v-model="form.orderId" placeholder="请输入订单主表ID" />
        </el-form-item>
        <el-form-item label="商品ID" prop="goodsId">
          <el-input v-model="form.goodsId" placeholder="请输入商品ID" />
        </el-form-item>
        <el-form-item label="尺码ID" prop="sizeId">
          <el-input v-model="form.sizeId" placeholder="请输入尺码ID" />
        </el-form-item>
        <el-form-item label="商品名称快照" prop="goodsName">
          <el-input v-model="form.goodsName" placeholder="请输入商品名称快照" />
        </el-form-item>
        <el-form-item label="尺码名称快照" prop="sizeName">
          <el-input v-model="form.sizeName" placeholder="请输入尺码名称快照" />
        </el-form-item>
        <el-form-item label="商品主图快照" prop="mainImage">
          <image-upload v-model="form.mainImage"/>
        </el-form-item>
        <el-form-item label="单价" prop="unitPrice">
          <el-input v-model="form.unitPrice" placeholder="请输入单价" />
        </el-form-item>
        <el-form-item label="行金额" prop="itemAmount">
          <el-input v-model="form.itemAmount" placeholder="请输入行金额" />
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
import { listOrderItem, getOrderItem, delOrderItem, addOrderItem, updateOrderItem } from "@/api/supplier/orderItem";

export default {
  name: "OrderItem",
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
      // 订单明细表格数据
      orderItemList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderId: null,
        goodsId: null,
        sizeId: null,
        goodsName: null,
        sizeName: null,
        mainImage: null,
        saleType: null,
        unitPrice: null,
        buyNum: null,
        itemAmount: null,
        deptId: null,
        ancestors: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        orderId: [
          { required: true, message: "订单主表ID不能为空", trigger: "blur" }
        ],
        goodsId: [
          { required: true, message: "商品ID不能为空", trigger: "blur" }
        ],
        goodsName: [
          { required: true, message: "商品名称快照不能为空", trigger: "blur" }
        ],
        saleType: [
          { required: true, message: "销售类型，1统购 2零售不能为空", trigger: "change" }
        ],
        unitPrice: [
          { required: true, message: "单价不能为空", trigger: "blur" }
        ],
        buyNum: [
          { required: true, message: "购买数量不能为空", trigger: "blur" }
        ],
        itemAmount: [
          { required: true, message: "行金额不能为空", trigger: "blur" }
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
    /** 查询订单明细列表 */
    getList() {
      this.loading = true;
      listOrderItem(this.queryParams).then(response => {
        this.orderItemList = response.rows;
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
        orderId: null,
        goodsId: null,
        sizeId: null,
        goodsName: null,
        sizeName: null,
        mainImage: null,
        saleType: null,
        unitPrice: null,
        buyNum: null,
        itemAmount: null,
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
      this.title = "添加订单明细";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getOrderItem(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改订单明细";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateOrderItem(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addOrderItem(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除订单明细编号为"' + ids + '"的数据项？').then(function() {
        return delOrderItem(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('supplier/orderItem/export', {
        ...this.queryParams
      }, `orderItem_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
