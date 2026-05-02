<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
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
      <el-form-item label="商品ID" prop="goodsId">
        <el-input
          v-model="queryParams.goodsId"
          placeholder="请输入商品ID"
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
      <el-form-item label="评价人用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入评价人用户ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="评价人姓名" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入评价人姓名"
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
      <el-form-item label="回复时间" prop="replyTime">
        <el-date-picker clearable
          v-model="queryParams.replyTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择回复时间">
        </el-date-picker>
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
          v-hasPermi="['supplier:comment:add']"
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
          v-hasPermi="['supplier:comment:edit']"
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
          v-hasPermi="['supplier:comment:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['supplier:comment:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="commentList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="订单ID" align="center" prop="orderId" />
      <el-table-column label="订单明细ID" align="center" prop="orderItemId" />
      <el-table-column label="订单号快照" align="center" prop="orderNo" />
      <el-table-column label="商品ID" align="center" prop="goodsId" />
      <el-table-column label="商品名称快照" align="center" prop="goodsName" />
      <el-table-column label="评价人用户ID" align="center" prop="userId" />
      <el-table-column label="评价人姓名" align="center" prop="userName" />
      <el-table-column label="供应商ID" align="center" prop="supplierId" />
      <el-table-column label="评分" align="center" prop="score" />
      <el-table-column label="评价内容" align="center" prop="content" />
      <el-table-column label="评价图片JSON文本" align="center" prop="imagesJson" />
      <el-table-column label="供应商回复内容" align="center" prop="replyContent" />
      <el-table-column label="回复时间" align="center" prop="replyTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.replyTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态，0正常 1隐藏" align="center" prop="status" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['supplier:comment:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['supplier:comment:remove']"
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

    <!-- 添加或修改商品评价对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="订单ID" prop="orderId">
          <el-input v-model="form.orderId" placeholder="请输入订单ID" />
        </el-form-item>
        <el-form-item label="订单明细ID" prop="orderItemId">
          <el-input v-model="form.orderItemId" placeholder="请输入订单明细ID" />
        </el-form-item>
        <el-form-item label="订单号快照" prop="orderNo">
          <el-input v-model="form.orderNo" placeholder="请输入订单号快照" />
        </el-form-item>
        <el-form-item label="商品ID" prop="goodsId">
          <el-input v-model="form.goodsId" placeholder="请输入商品ID" />
        </el-form-item>
        <el-form-item label="商品名称快照" prop="goodsName">
          <el-input v-model="form.goodsName" placeholder="请输入商品名称快照" />
        </el-form-item>
        <el-form-item label="评价人用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入评价人用户ID" />
        </el-form-item>
        <el-form-item label="评价人姓名" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入评价人姓名" />
        </el-form-item>
        <el-form-item label="供应商ID" prop="supplierId">
          <el-input v-model="form.supplierId" placeholder="请输入供应商ID" />
        </el-form-item>
        <el-form-item label="评价内容" prop="content">
          <el-input v-model="form.content" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="评价图片JSON文本" prop="imagesJson">
          <el-input v-model="form.imagesJson" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="供应商回复内容" prop="replyContent">
          <el-input v-model="form.replyContent" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="回复时间" prop="replyTime">
          <el-date-picker clearable
            v-model="form.replyTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择回复时间">
          </el-date-picker>
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
import { listComment, getComment, delComment, addComment, updateComment } from "@/api/supplier/comment";

export default {
  name: "Comment",
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
      // 商品评价表格数据
      commentList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderId: null,
        orderItemId: null,
        orderNo: null,
        goodsId: null,
        goodsName: null,
        userId: null,
        userName: null,
        supplierId: null,
        score: null,
        content: null,
        imagesJson: null,
        replyContent: null,
        replyTime: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        orderId: [
          { required: true, message: "订单ID不能为空", trigger: "blur" }
        ],
        orderItemId: [
          { required: true, message: "订单明细ID不能为空", trigger: "blur" }
        ],
        orderNo: [
          { required: true, message: "订单号快照不能为空", trigger: "blur" }
        ],
        goodsId: [
          { required: true, message: "商品ID不能为空", trigger: "blur" }
        ],
        goodsName: [
          { required: true, message: "商品名称快照不能为空", trigger: "blur" }
        ],
        userId: [
          { required: true, message: "评价人用户ID不能为空", trigger: "blur" }
        ],
        userName: [
          { required: true, message: "评价人姓名不能为空", trigger: "blur" }
        ],
        supplierId: [
          { required: true, message: "供应商ID不能为空", trigger: "blur" }
        ],
        score: [
          { required: true, message: "评分不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "状态，0正常 1隐藏不能为空", trigger: "change" }
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
    /** 查询商品评价列表 */
    getList() {
      this.loading = true;
      listComment(this.queryParams).then(response => {
        this.commentList = response.rows;
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
        orderItemId: null,
        orderNo: null,
        goodsId: null,
        goodsName: null,
        userId: null,
        userName: null,
        supplierId: null,
        score: null,
        content: null,
        imagesJson: null,
        replyContent: null,
        replyTime: null,
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
      this.title = "添加商品评价";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getComment(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改商品评价";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateComment(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addComment(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除商品评价编号为"' + ids + '"的数据项？').then(function() {
        return delComment(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('supplier/comment/export', {
        ...this.queryParams
      }, `comment_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
