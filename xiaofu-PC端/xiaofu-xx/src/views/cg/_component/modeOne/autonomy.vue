<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryForm"
      size="small"
      :inline="true"
      v-show="showSearch"
    >
      <el-form-item label="标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item label="发布状态" prop="isRelease">
        <el-select
          v-model="queryParams.isRelease"
          placeholder="请选择发布状态"
          clearable
        >
          <el-option label="已发布" value="1" />
          <el-option label="未发布" value="0" />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button
          type="primary"
          icon="el-icon-search"
          size="mini"
          @click="handleQuery"
          >搜索</el-button
        >
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery"
          >重置</el-button
        >
      </el-form-item>
    </el-form>

    <!-- <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['school:organization:add']"
          >新增</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['school:organization:edit']"
          >修改</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['school:organization:remove']"
          >删除</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['school:organization:export']"
          >导出</el-button
        >
      </el-col>
      <right-toolbar
        :showSearch.sync="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row> -->

    <el-table
      v-loading="loading"
      :data="organizationList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center" type="index" />
      <el-table-column label="标题" align="center" prop="title" />
      <el-table-column label="是否提样衣" align="center" prop="isSamplesPic">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isSamplesPic === 1" type="success">是</el-tag>
          <el-tag v-else type="danger">否</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="采购公告模式选择" align="center" prop="type">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.ggxzms" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column
        label="报名截止日期"
        align="center"
        prop="bmjzTime"
        width="180"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.bmjzTime, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column label="发布状态" align="center" prop="isRelease">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isRelease == 1" type="success">已发布</el-tag>
          <el-tag v-else type="danger">未发布</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="添加时间" align="center" prop="createTime">
        <template slot-scope="scope">
          <span>{{
            parseTime(scope.row.createTime, "{y}-{m}-{d} {h}:{i}:{s}")
          }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <el-button
            v-if="scope.row.isRelease == 1"
            size="mini"
            type="text"
            icon="el-icon-back"
            @click="handleWithdraw(scope.row)"
            v-hasPermi="['school:organization:edit']"
            >撤回</el-button
          >
          <el-button
            v-else
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['school:organization:edit']"
            >修改</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['school:organization:remove']"
            >删除</el-button
          >
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
  </div>
</template>

<script>
import {
  listOrganization,
  getOrganization,
  delOrganization,
  addOrganization,
  updateOrganization,
} from "@/api/cg/scene";

export default {
  name: "Organization",
  dicts: ["ggxzms"],
  props: ["type"],
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
      // 模式一组织确认选购模式表格数据
      organizationList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        isSamplesPic: null,
        type: null,
        bmjzTime: null,
        isRelease: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {},
    };
  },
  created() {
    this.getList();
  },
  methods: {
    handleWithdraw(row) {
      this.$modal
        .confirm("确定要撤回该选购信息吗？")
        .then(function () {
          return updateOrganization({
            id: row.id,
            isRelease: "0",
          });
        })
        .then(() => {
          this.$modal.msgSuccess("撤回成功");
          this.getList();
        })
        .catch(() => {});
    },
    /** 查询模式一组织确认选购模式列表 */
    getList() {
      this.loading = true;
      listOrganization(
        this.addDateRange(
          { ...this.queryParams, type: this.type, teamId: this.formId },
          this.dateRange
        )
      ).then((response) => {
        this.organizationList = response.rows;
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
        title: null,
        isSamplesPic: null,
        samplesPic: null,
        updateTime: null,
        updateBy: null,
        createTime: null,
        createBy: null,
        delFlag: null,
        remark: null,
        otherFile: null,
        noticeFile: null,
        type: null,
        quesId: null,
        bmjzTime: null,
        isRelease: null,
        inviteFile: null,
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
      this.ids = selection.map((item) => item.id);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加模式一组织确认选购模式";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getOrganization(id).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = "修改模式一组织确认选购模式";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.id != null) {
            updateOrganization(this.form).then((response) => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addOrganization(this.form).then((response) => {
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
      this.$modal
        .confirm(
          '是否确认删除模式一组织确认选购模式编号为"' + ids + '"的数据项？'
        )
        .then(function () {
          return delOrganization(ids);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download(
        "school/organization/export",
        {
          ...this.queryParams,
        },
        `organization_${new Date().getTime()}.xlsx`
      );
    },
  },
};
</script>
