<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryForm"
      size="small"
      :inline="true"
      v-show="showSearch"
      label-width="68px"
    >
      <el-form-item label="学校名称" prop="deptName">
        <el-input
          v-model="queryParams.deptName"
          placeholder="请输入学校名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="反馈状态" prop="replyState">
        <el-select
          v-model="queryParams.replyState"
          placeholder="请选择反馈状态"
          clearable
          @keyup.enter.native="handleQuery"
        >
          <el-option label="未回复" value="0"></el-option>
          <el-option label="已经回复" value="1"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="区县" prop="region">
        <el-select
          v-model="queryParams.region"
          placeholder="请选择区县"
          clearable
          @keyup.enter.native="handleQuery"
        >
          <el-option
            v-for="item of shijiazhuangDistricts"
            :value="item.id"
            :label="item.name"
            :key="item.id"
          >
          </el-option>
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

    <el-table
      v-loading="loading"
      :data="parentsFeedbackList" 
    >
      <!-- <el-table-column type="selection" width="55" align="center" /> -->
      <el-table-column label="序号" align="center" type="index" />
      <el-table-column width="200" label="区县" align="center" prop="region" />
      <el-table-column
        width="200"
        label="姓名"
        align="center"
        prop="userName"
      />
      <el-table-column
        width="200"
        label="联系方式"
        align="center"
        prop="lxfs"
      />

      <el-table-column
        width="200"
        label="学校名称"
        align="center"
        prop="deptName"
      />
      <el-table-column
        width="300"
        label="反馈内容"
        align="center"
        prop="feedbackContent"
      >
        <template slot-scope="scope">
          <el-tooltip
            effect="light"
            :content="scope.row.feedbackContent"
            placement="top"
          >
            <span class="text-ellipsis">{{ scope.row.feedbackContent }}</span>
            <div slot="content" style="width: 300px">
              {{ scope.row.feedbackContent }}
            </div>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column
        width="200"
        label="回复内容"
        align="center"
        prop="replyContent"
        show-overflow-tooltip
      />
      <el-table-column
        width="200"
        label="留言时间"
        align="center"
        prop="createTime"
        show-overflow-tooltip
      ></el-table-column>
      <el-table-column
        width="200"
        label="回复时间"
        align="center"
        prop="replyTime"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <span>{{
            parseTime(scope.row.replyTime, "{y}-{m}-{d} {h}:{i}:{s}")
          }}</span>
        </template>
      </el-table-column>
      <el-table-column
        width="200"
        label="反馈状态"
        align="center"
        prop="replyState"
      >
        <template slot-scope="scope">
          <el-tag v-if="scope.row.replyState == 0" type="danger">未回复</el-tag>
          <el-tag v-else type="success">已回复</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        width="200"
        label="是否公开"
        align="center"
        prop="isPublic"
      >
        <template slot-scope="scope">
          <!-- <el-tag v-if="scope.row.isPublic == 0" type="danger">不公开</el-tag>
          <el-tag v-if="scope.row.isPublic == 1" type="success">公开</el-tag> -->
          <el-switch
            :disabled="scope.row.replyContent == 0"
            v-model="scope.row.isPublic"
            active-value="1"
            inactive-value="0"
            active-text="公开"
            inactive-text="不公开"
            @change="handlePublicChange(scope.row)"
          >
          </el-switch>
        </template>
      </el-table-column>

      <el-table-column
        width="200"
        fixed="right"
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['school:parentsFeedback:edit']"
            >{{ scope.row.replyState == 0 ? "回复" : "编辑回复" }}</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['school:parentsFeedback:remove']"
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

    <!-- 回复意见对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="姓名">
          <el-input v-model="form.userName" readonly />
        </el-form-item>
        <el-form-item label="反馈时间">
          <el-input readonly v-model="form.createTime" type="text" />
        </el-form-item>
        <el-form-item label="反馈内容">
          <el-input
            readonly
            v-model="form.feedbackContent"
            type="textarea"
            :rows="5"
          />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="form.lxfs" readonly />
        </el-form-item>
        <el-form-item label="回复内容" prop="replyContent">
          <el-input
            v-model="form.replyContent"
            type="textarea"
            :rows="4"
            placeholder="请输入回复内容"
          />
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
import {
  listParentsFeedback,
  getParentsFeedback,
  delParentsFeedback,
  addParentsFeedback,
  updateParentsFeedback,
} from "@/api/opinion";
import { shijiazhuangDistricts } from "@/utils/region";
export default {
  name: "ParentsFeedback",
  data() {
    return {
      shijiazhuangDistricts: shijiazhuangDistricts,
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
      // 家长反馈表格数据
      parentsFeedbackList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        userName: null,
        deptId: null,
        deptName: null,
        feedbackContent: null,
        feedbackUrl: null,
        replyContent: null,
        replyUrl: null,
        replyState: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        replyContent: [
          { required: true, message: "回复内容不能为空", trigger: "blur" },
        ],
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    handlePublicChange(row) {
      console.log(row);
      updateParentsFeedback(row).then((res) => {
        this.$modal.msgSuccess("修改成功");
        this.getList();
      });
    },
    /** 查询家长反馈列表 */
    getList() {
      this.loading = true;
      listParentsFeedback({
        ...this.queryParams,
        deptId: this.$store.state.user.deptId,
      }).then((response) => {
        this.parentsFeedbackList = response.rows;
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
        userId: null,
        userName: null,
        deptId: null,
        deptName: null,
        feedbackContent: null,
        feedbackUrl: null,
        replyContent: null,
        replyUrl: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null,
        delFlag: null,
        replyState: null,
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
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getParentsFeedback(id).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = "回复意见";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          // 只更新回复相关字段
          const updateData = {
            id: this.form.id,
            replyContent: this.form.replyContent,
            replyState: 1, // 设置为已回复状态
          };

          updateParentsFeedback(updateData).then((response) => {
            this.$modal.msgSuccess("回复成功");
            this.open = false;
            this.getList();
          });
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal
        .confirm('是否确认删除，删除后无法恢复。')
        .then(function () {
          return delParentsFeedback(ids);
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
        "school/parentsFeedback/export",
        {
          ...this.queryParams,
        },
        `parentsFeedback_${new Date().getTime()}.xlsx`
      );
    },
  },
};
</script>

<style scoped>
/* 文本超出一行显示省略号 */
.text-ellipsis {
  display: inline-block;
  width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  vertical-align: top;
}

/* 自定义tooltip样式 */
::v-deep .el-tooltip__popper {
  max-width: 300px !important;
  width: auto !important;
  height: auto !important;
  word-wrap: break-word !important;
  word-break: break-all !important;
  white-space: normal !important;
  line-height: 1.5 !important;
}

/* 确保tooltip内容正确换行 */
::v-deep .el-tooltip__popper .popper__arrow {
  border-top-color: #303133 !important;
}
.text-ellipsis {
  width: 300px;
}
</style>
