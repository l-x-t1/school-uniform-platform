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
      <el-form-item label="订单明细ID" prop="orderItemId">
        <el-input
          v-model="queryParams.orderItemId"
          placeholder="请输入订单明细ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="订单编号快照" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单编号快照"
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
      <el-form-item label="评价用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入评价用户ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="评价用户名快照" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入评价用户名快照"
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
      <el-form-item label="评分，1-5分" prop="score">
        <el-input
          v-model="queryParams.score"
          placeholder="请输入评分，1-5分"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="历史商家回复时间" prop="replyTime">
        <el-date-picker clearable
          v-model="queryParams.replyTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择历史商家回复时间">
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
          v-hasPermi="['school:comment:add']"
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
          v-hasPermi="['school:comment:edit']"
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
          v-hasPermi="['school:comment:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['school:comment:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="commentList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="订单主表ID" align="center" prop="orderId" />
      <el-table-column label="订单明细ID" align="center" prop="orderItemId" />
      <el-table-column label="订单编号快照" align="center" prop="orderNo" />
      <el-table-column label="商品ID" align="center" prop="goodsId" />
      <el-table-column label="商品名称快照" align="center" prop="goodsName" />
      <el-table-column label="评价用户ID" align="center" prop="userId" />
      <el-table-column label="评价用户名快照" align="center" prop="userName" />
      <el-table-column label="供应商ID" align="center" prop="supplierId" />
      <el-table-column label="评分，1-5分" align="center" prop="score" />
      <el-table-column label="历史商家回复时间" align="center" prop="replyTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.replyTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="评价状态，0正常 1隐藏" align="center" prop="status" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['school:comment:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['school:comment:remove']"
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
        <el-form-item label="订单主表ID" prop="orderId">
          <el-input v-model="form.orderId" placeholder="请输入订单主表ID" />
        </el-form-item>
        <el-form-item label="订单明细ID" prop="orderItemId">
          <el-input v-model="form.orderItemId" placeholder="请输入订单明细ID" />
        </el-form-item>
        <el-form-item label="订单编号快照" prop="orderNo">
          <el-input v-model="form.orderNo" placeholder="请输入订单编号快照" />
        </el-form-item>
        <el-form-item label="商品ID" prop="goodsId">
          <el-input v-model="form.goodsId" placeholder="请输入商品ID" />
        </el-form-item>
        <el-form-item label="商品名称快照" prop="goodsName">
          <el-input v-model="form.goodsName" placeholder="请输入商品名称快照" />
        </el-form-item>
        <el-form-item label="评价用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入评价用户ID" />
        </el-form-item>
        <el-form-item label="评价用户名快照" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入评价用户名快照" />
        </el-form-item>
        <el-form-item label="供应商ID" prop="supplierId">
          <el-input v-model="form.supplierId" placeholder="请输入供应商ID" />
        </el-form-item>
        <el-form-item label="评分，1-5分" prop="score">
          <el-input v-model="form.score" placeholder="请输入评分，1-5分" />
        </el-form-item>
        <el-form-item label="评价内容">
          <editor v-model="form.content" :min-height="192"/>
        </el-form-item>
        <el-form-item label="评价图片JSON文本" prop="imagesJson">
          <el-input v-model="form.imagesJson" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="历史商家回复内容，后续回复使用xfpt_comment_reply">
          <editor v-model="form.replyContent" :min-height="192"/>
        </el-form-item>
        <el-form-item label="历史商家回复时间" prop="replyTime">
          <el-date-picker clearable
            v-model="form.replyTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择历史商家回复时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="删除标记，0正常 1删除" prop="delFlag">
          <el-input v-model="form.delFlag" placeholder="请输入删除标记，0正常 1删除" />
        </el-form-item>
        <el-divider content-position="center">商品评论回复信息</el-divider>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAddXfptCommentReply">添加</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" icon="el-icon-delete" size="mini" @click="handleDeleteXfptCommentReply">删除</el-button>
          </el-col>
        </el-row>
        <el-table :data="xfptCommentReplyList" :row-class-name="rowXfptCommentReplyIndex" @selection-change="handleXfptCommentReplySelectionChange" ref="xfptCommentReply">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="序号" align="center" prop="index" width="50"/>
          <el-table-column label="订单ID" prop="orderId" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.orderId" placeholder="请输入订单ID" />
            </template>
          </el-table-column>
          <el-table-column label="订单明细ID" prop="orderItemId" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.orderItemId" placeholder="请输入订单明细ID" />
            </template>
          </el-table-column>
          <el-table-column label="商品ID" prop="goodsId" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.goodsId" placeholder="请输入商品ID" />
            </template>
          </el-table-column>
          <el-table-column label="供应商ID" prop="supplierId" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.supplierId" placeholder="请输入供应商ID" />
            </template>
          </el-table-column>
          <el-table-column label="回复人ID" prop="fromUserId" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.fromUserId" placeholder="请输入回复人ID" />
            </template>
          </el-table-column>
          <el-table-column label="回复人姓名快照" prop="fromUserName" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.fromUserName" placeholder="请输入回复人姓名快照" />
            </template>
          </el-table-column>
          <el-table-column label="回复人角色，1家长 2供应商" prop="fromRoleType" width="150">
            <template slot-scope="scope">
              <el-select v-model="scope.row.fromRoleType" placeholder="请选择回复人角色，1家长 2供应商">
                <el-option label="请选择字典生成" value="" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="回复人角色标题快照" prop="fromRoleTitle" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.fromRoleTitle" placeholder="请输入回复人角色标题快照" />
            </template>
          </el-table-column>
          <el-table-column label="被回复人ID" prop="toUserId" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.toUserId" placeholder="请输入被回复人ID" />
            </template>
          </el-table-column>
          <el-table-column label="被回复人姓名快照" prop="toUserName" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.toUserName" placeholder="请输入被回复人姓名快照" />
            </template>
          </el-table-column>
          <el-table-column label="被回复人角色，1家长 2供应商" prop="toRoleType" width="150">
            <template slot-scope="scope">
              <el-select v-model="scope.row.toRoleType" placeholder="请选择被回复人角色，1家长 2供应商">
                <el-option label="请选择字典生成" value="" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="被回复人角色标题快照" prop="toRoleTitle" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.toRoleTitle" placeholder="请输入被回复人角色标题快照" />
            </template>
          </el-table-column>
          <el-table-column label="状态，0正常 1隐藏" prop="status" width="150">
            <template slot-scope="scope">
              <el-select v-model="scope.row.status" placeholder="请选择状态，0正常 1隐藏">
                <el-option label="请选择字典生成" value="" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="排序值" prop="sortNum" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.sortNum" placeholder="请输入排序值" />
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
import { listComment, getComment, delComment, addComment, updateComment } from "@/api/school/comment";

export default {
  name: "Comment",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 子表选中数据
      checkedXfptCommentReply: [],
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
      // 商品评论回复表格数据
      xfptCommentReplyList: [],
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
        replyTime: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        orderId: [
          { required: true, message: "订单主表ID不能为空", trigger: "blur" }
        ],
        orderNo: [
          { required: true, message: "订单编号快照不能为空", trigger: "blur" }
        ],
        goodsId: [
          { required: true, message: "商品ID不能为空", trigger: "blur" }
        ],
        goodsName: [
          { required: true, message: "商品名称快照不能为空", trigger: "blur" }
        ],
        userId: [
          { required: true, message: "评价用户ID不能为空", trigger: "blur" }
        ],
        userName: [
          { required: true, message: "评价用户名快照不能为空", trigger: "blur" }
        ],
        supplierId: [
          { required: true, message: "供应商ID不能为空", trigger: "blur" }
        ],
        score: [
          { required: true, message: "评分，1-5分不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "评价状态，0正常 1隐藏不能为空", trigger: "change" }
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
      this.xfptCommentReplyList = [];
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
        this.xfptCommentReplyList = response.data.xfptCommentReplyList;
        this.open = true;
        this.title = "修改商品评价";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.form.xfptCommentReplyList = this.xfptCommentReplyList;
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
	/** 商品评论回复序号 */
    rowXfptCommentReplyIndex({ row, rowIndex }) {
      row.index = rowIndex + 1;
    },
    /** 商品评论回复添加按钮操作 */
    handleAddXfptCommentReply() {
      let obj = {};
      obj.orderId = "";
      obj.orderItemId = "";
      obj.goodsId = "";
      obj.supplierId = "";
      obj.fromUserId = "";
      obj.fromUserName = "";
      obj.fromRoleType = "";
      obj.fromRoleTitle = "";
      obj.toUserId = "";
      obj.toUserName = "";
      obj.toRoleType = "";
      obj.toRoleTitle = "";
      obj.status = "";
      obj.sortNum = "";
      obj.remark = "";
      this.xfptCommentReplyList.push(obj);
    },
    /** 商品评论回复删除按钮操作 */
    handleDeleteXfptCommentReply() {
      if (this.checkedXfptCommentReply.length == 0) {
        this.$modal.msgError("请先选择要删除的商品评论回复数据");
      } else {
        const xfptCommentReplyList = this.xfptCommentReplyList;
        const checkedXfptCommentReply = this.checkedXfptCommentReply;
        this.xfptCommentReplyList = xfptCommentReplyList.filter(function(item) {
          return checkedXfptCommentReply.indexOf(item.index) == -1
        });
      }
    },
    /** 复选框选中数据 */
    handleXfptCommentReplySelectionChange(selection) {
      this.checkedXfptCommentReply = selection.map(item => item.index)
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('school/comment/export', {
        ...this.queryParams
      }, `comment_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
