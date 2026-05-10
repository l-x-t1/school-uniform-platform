<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="所属评论ID" prop="commentId">
        <el-input
          v-model="queryParams.commentId"
          placeholder="请输入所属评论ID"
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
      <el-form-item label="商品ID" prop="goodsId">
        <el-input
          v-model="queryParams.goodsId"
          placeholder="请输入商品ID"
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
      <el-form-item label="回复人ID" prop="fromUserId">
        <el-input
          v-model="queryParams.fromUserId"
          placeholder="请输入回复人ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="回复人姓名快照" prop="fromUserName">
        <el-input
          v-model="queryParams.fromUserName"
          placeholder="请输入回复人姓名快照"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="回复人角色标题快照" prop="fromRoleTitle">
        <el-input
          v-model="queryParams.fromRoleTitle"
          placeholder="请输入回复人角色标题快照"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="被回复人ID" prop="toUserId">
        <el-input
          v-model="queryParams.toUserId"
          placeholder="请输入被回复人ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="被回复人姓名快照" prop="toUserName">
        <el-input
          v-model="queryParams.toUserName"
          placeholder="请输入被回复人姓名快照"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="被回复人角色标题快照" prop="toRoleTitle">
        <el-input
          v-model="queryParams.toRoleTitle"
          placeholder="请输入被回复人角色标题快照"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="排序值" prop="sortNum">
        <el-input
          v-model="queryParams.sortNum"
          placeholder="请输入排序值"
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
          v-hasPermi="['school:commentReply:add']"
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
          v-hasPermi="['school:commentReply:edit']"
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
          v-hasPermi="['school:commentReply:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['school:commentReply:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="commentReplyList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="所属评论ID" align="center" prop="commentId" />
      <el-table-column label="订单ID" align="center" prop="orderId" />
      <el-table-column label="订单明细ID" align="center" prop="orderItemId" />
      <el-table-column label="商品ID" align="center" prop="goodsId" />
      <el-table-column label="供应商ID" align="center" prop="supplierId" />
      <el-table-column label="回复人ID" align="center" prop="fromUserId" />
      <el-table-column label="回复人姓名快照" align="center" prop="fromUserName" />
      <el-table-column label="回复人角色，1家长 2供应商" align="center" prop="fromRoleType" />
      <el-table-column label="回复人角色标题快照" align="center" prop="fromRoleTitle" />
      <el-table-column label="被回复人ID" align="center" prop="toUserId" />
      <el-table-column label="被回复人姓名快照" align="center" prop="toUserName" />
      <el-table-column label="被回复人角色，1家长 2供应商" align="center" prop="toRoleType" />
      <el-table-column label="被回复人角色标题快照" align="center" prop="toRoleTitle" />
      <el-table-column label="状态，0正常 1隐藏" align="center" prop="status" />
      <el-table-column label="排序值" align="center" prop="sortNum" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['school:commentReply:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['school:commentReply:remove']"
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

    <!-- 添加或修改商品评论回复对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="所属评论ID" prop="commentId">
          <el-input v-model="form.commentId" placeholder="请输入所属评论ID" />
        </el-form-item>
        <el-form-item label="订单ID" prop="orderId">
          <el-input v-model="form.orderId" placeholder="请输入订单ID" />
        </el-form-item>
        <el-form-item label="订单明细ID" prop="orderItemId">
          <el-input v-model="form.orderItemId" placeholder="请输入订单明细ID" />
        </el-form-item>
        <el-form-item label="商品ID" prop="goodsId">
          <el-input v-model="form.goodsId" placeholder="请输入商品ID" />
        </el-form-item>
        <el-form-item label="供应商ID" prop="supplierId">
          <el-input v-model="form.supplierId" placeholder="请输入供应商ID" />
        </el-form-item>
        <el-form-item label="回复人ID" prop="fromUserId">
          <el-input v-model="form.fromUserId" placeholder="请输入回复人ID" />
        </el-form-item>
        <el-form-item label="回复人姓名快照" prop="fromUserName">
          <el-input v-model="form.fromUserName" placeholder="请输入回复人姓名快照" />
        </el-form-item>
        <el-form-item label="回复人角色" prop="fromRoleType">
          <el-select v-model="form.fromRoleType" placeholder="请选择回复人角色">
            <el-option label="家长" value="1" />
            <el-option label="供应商" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="回复人角色标题快照" prop="fromRoleTitle">
          <el-input v-model="form.fromRoleTitle" placeholder="请输入回复人角色标题快照" />
        </el-form-item>
        <el-form-item label="被回复人ID" prop="toUserId">
          <el-input v-model="form.toUserId" placeholder="请输入被回复人ID" />
        </el-form-item>
        <el-form-item label="被回复人姓名快照" prop="toUserName">
          <el-input v-model="form.toUserName" placeholder="请输入被回复人姓名快照" />
        </el-form-item>
        <el-form-item label="被回复角色" prop="toRoleType">
          <el-select v-model="form.toRoleType" placeholder="请选择被回复人角色">
            <el-option label="家长" value="1" />
            <el-option label="供应商" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="被回复人角色标题快照" prop="toRoleTitle">
          <el-input v-model="form.toRoleTitle" placeholder="请输入被回复人角色标题快照" />
        </el-form-item>
        <el-form-item label="回复内容">
          <editor v-model="form.content" :min-height="192"/>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="正常" value="0" />
            <el-option label="隐藏" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序值" prop="sortNum">
          <el-input v-model="form.sortNum" placeholder="请输入排序值" />
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
import { listCommentReply, getCommentReply, delCommentReply, addCommentReply, updateCommentReply } from "@/api/school/commentReply";

export default {
  name: "CommentReply",
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
      // 商品评论回复表格数据
      commentReplyList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        commentId: null,
        orderId: null,
        orderItemId: null,
        goodsId: null,
        supplierId: null,
        fromUserId: null,
        fromUserName: null,
        fromRoleType: null,
        fromRoleTitle: null,
        toUserId: null,
        toUserName: null,
        toRoleType: null,
        toRoleTitle: null,
        status: null,
        sortNum: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        commentId: [
          { required: true, message: "所属评论ID不能为空", trigger: "blur" }
        ],
        fromUserId: [
          { required: true, message: "回复人ID不能为空", trigger: "blur" }
        ],
        fromUserName: [
          { required: true, message: "回复人姓名快照不能为空", trigger: "blur" }
        ],
        fromRoleType: [
          { required: true, message: "回复人角色，1家长 2供应商不能为空", trigger: "change" }
        ],
        fromRoleTitle: [
          { required: true, message: "回复人角色标题快照不能为空", trigger: "blur" }
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
    /** 查询商品评论回复列表 */
    getList() {
      this.loading = true;
      listCommentReply(this.queryParams).then(response => {
        this.commentReplyList = response.rows;
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
        commentId: null,
        orderId: null,
        orderItemId: null,
        goodsId: null,
        supplierId: null,
        fromUserId: null,
        fromUserName: null,
        fromRoleType: null,
        fromRoleTitle: null,
        toUserId: null,
        toUserName: null,
        toRoleType: null,
        toRoleTitle: null,
        content: null,
        status: "0",
        sortNum: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null,
        delFlag: "0"
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
      this.title = "添加商品评论回复";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getCommentReply(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改商品评论回复";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateCommentReply(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addCommentReply(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除商品评论回复编号为"' + ids + '"的数据项？').then(function() {
        return delCommentReply(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('school/commentReply/export', {
        ...this.queryParams
      }, `commentReply_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
