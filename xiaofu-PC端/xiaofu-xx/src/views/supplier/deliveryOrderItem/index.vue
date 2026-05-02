<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="发货单主表ID" prop="deliveryId">
        <el-input
          v-model="queryParams.deliveryId"
          placeholder="请输入发货单主表ID"
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
          v-hasPermi="['supplier:deliveryOrderItem:add']"
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
          v-hasPermi="['supplier:deliveryOrderItem:edit']"
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
          v-hasPermi="['supplier:deliveryOrderItem:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['supplier:deliveryOrderItem:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="deliveryOrderItemList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="发货单主表ID" align="center" prop="deliveryId" />
      <el-table-column label="订单明细ID" align="center" prop="orderItemId" />
      <el-table-column label="商品ID" align="center" prop="goodsId" />
      <el-table-column label="尺码ID" align="center" prop="sizeId" />
      <el-table-column label="商品名称快照" align="center" prop="goodsName" />
      <el-table-column label="尺码名称快照" align="center" prop="sizeName" />
      <el-table-column label="发货数量" align="center" prop="deliveryNum" />
      <el-table-column label="签收数量" align="center" prop="signNum" />
      <el-table-column label="状态，0待发货 1已发货 2已签收" align="center" prop="status" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['supplier:deliveryOrderItem:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['supplier:deliveryOrderItem:remove']"
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

    <!-- 添加或修改发货单明细对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="发货单主表ID" prop="deliveryId">
          <el-input v-model="form.deliveryId" placeholder="请输入发货单主表ID" />
        </el-form-item>
        <el-form-item label="订单明细ID" prop="orderItemId">
          <el-input v-model="form.orderItemId" placeholder="请输入订单明细ID" />
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
import { listDeliveryOrderItem, getDeliveryOrderItem, delDeliveryOrderItem, addDeliveryOrderItem, updateDeliveryOrderItem } from "@/api/supplier/deliveryOrderItem";

export default {
  name: "DeliveryOrderItem",
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
      // 发货单明细表格数据
      deliveryOrderItemList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        deliveryId: null,
        orderItemId: null,
        goodsId: null,
        sizeId: null,
        goodsName: null,
        sizeName: null,
        deliveryNum: null,
        signNum: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        deliveryId: [
          { required: true, message: "发货单主表ID不能为空", trigger: "blur" }
        ],
        goodsId: [
          { required: true, message: "商品ID不能为空", trigger: "blur" }
        ],
        goodsName: [
          { required: true, message: "商品名称快照不能为空", trigger: "blur" }
        ],
        deliveryNum: [
          { required: true, message: "发货数量不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "状态，0待发货 1已发货 2已签收不能为空", trigger: "change" }
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
    /** 查询发货单明细列表 */
    getList() {
      this.loading = true;
      listDeliveryOrderItem(this.queryParams).then(response => {
        this.deliveryOrderItemList = response.rows;
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
        deliveryId: null,
        orderItemId: null,
        goodsId: null,
        sizeId: null,
        goodsName: null,
        sizeName: null,
        deliveryNum: null,
        signNum: null,
        status: null,
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
      this.title = "添加发货单明细";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getDeliveryOrderItem(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改发货单明细";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateDeliveryOrderItem(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addDeliveryOrderItem(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除发货单明细编号为"' + ids + '"的数据项？').then(function() {
        return delDeliveryOrderItem(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('supplier/deliveryOrderItem/export', {
        ...this.queryParams
      }, `deliveryOrderItem_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
