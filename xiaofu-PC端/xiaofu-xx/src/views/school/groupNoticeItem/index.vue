<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="公告ID" prop="noticeId">
        <el-input
          v-model="queryParams.noticeId"
          placeholder="请输入公告ID"
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
      <el-form-item label="单位" prop="unitName">
        <el-input
          v-model="queryParams.unitName"
          placeholder="请输入单位"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="采购数量" prop="buyNum">
        <el-input
          v-model="queryParams.buyNum"
          placeholder="请输入采购数量"
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
          v-hasPermi="['school:groupNoticeItem:add']"
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
          v-hasPermi="['school:groupNoticeItem:edit']"
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
          v-hasPermi="['school:groupNoticeItem:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['school:groupNoticeItem:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="groupNoticeItemList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="公告ID" align="center" prop="noticeId" />
      <el-table-column label="商品ID" align="center" prop="goodsId" />
      <el-table-column label="商品名称快照" align="center" prop="goodsName" />
      <el-table-column label="单位" align="center" prop="unitName" />
      <el-table-column label="采购数量" align="center" prop="buyNum" />
      <el-table-column label="价格区间或价格说明" align="center" prop="priceDesc" />
      <el-table-column label="适用年级ID" align="center" prop="gradeIds" />
      <el-table-column label="适用年级名称" align="center" prop="gradeNames" />
      <el-table-column label="当前归属部门/学校" align="center" prop="deptId" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['school:groupNoticeItem:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['school:groupNoticeItem:remove']"
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

    <!-- 添加或修改统购公告商品对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="公告ID" prop="noticeId">
          <el-input v-model="form.noticeId" placeholder="请输入公告ID" />
        </el-form-item>
        <el-form-item label="商品ID" prop="goodsId">
          <el-input v-model="form.goodsId" placeholder="请输入商品ID" />
        </el-form-item>
        <el-form-item label="商品名称快照" prop="goodsName">
          <el-input v-model="form.goodsName" placeholder="请输入商品名称快照" />
        </el-form-item>
        <el-form-item label="可选尺码规则JSON文本" prop="sizeRuleJson">
          <el-input v-model="form.sizeRuleJson" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="单位" prop="unitName">
          <el-input v-model="form.unitName" placeholder="请输入单位" />
        </el-form-item>
        <el-form-item label="采购数量" prop="buyNum">
          <el-input v-model="form.buyNum" placeholder="请输入采购数量" />
        </el-form-item>
        <el-form-item label="价格区间或价格说明" prop="priceDesc">
          <el-input v-model="form.priceDesc" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="适用年级ID" prop="gradeIds">
          <el-input v-model="form.gradeIds" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="适用年级名称" prop="gradeNames">
          <el-input v-model="form.gradeNames" type="textarea" placeholder="请输入内容" />
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
import { listGroupNoticeItem, getGroupNoticeItem, delGroupNoticeItem, addGroupNoticeItem, updateGroupNoticeItem } from "@/api/school/groupNoticeItem";

export default {
  name: "GroupNoticeItem",
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
      // 统购公告商品表格数据
      groupNoticeItemList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        noticeId: null,
        goodsId: null,
        goodsName: null,
        unitName: null,
        buyNum: null,
        priceDesc: null,
        gradeIds: null,
        gradeNames: null,
        deptId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        noticeId: [
          { required: true, message: "公告ID不能为空", trigger: "blur" }
        ],
        goodsId: [
          { required: true, message: "商品ID不能为空", trigger: "blur" }
        ],
        goodsName: [
          { required: true, message: "商品名称快照不能为空", trigger: "blur" }
        ],
        buyNum: [
          { required: true, message: "采购数量不能为空", trigger: "blur" }
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
    /** 查询统购公告商品列表 */
    getList() {
      this.loading = true;
      listGroupNoticeItem(this.queryParams).then(response => {
        this.groupNoticeItemList = response.rows;
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
        noticeId: null,
        goodsId: null,
        goodsName: null,
        sizeRuleJson: null,
        unitName: null,
        buyNum: null,
        priceDesc: null,
        gradeIds: null,
        gradeNames: null,
        deptId: null,
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
      this.title = "添加统购公告商品";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getGroupNoticeItem(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改统购公告商品";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateGroupNoticeItem(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addGroupNoticeItem(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除统购公告商品编号为"' + ids + '"的数据项？').then(function() {
        return delGroupNoticeItem(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('school/groupNoticeItem/export', {
        ...this.queryParams
      }, `groupNoticeItem_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
