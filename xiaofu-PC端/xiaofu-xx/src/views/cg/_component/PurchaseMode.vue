<template>
  <div class="detail-panel">
    <div class="header" style="display: flex; align-items: center">
      <h3>选用组织确定选购模式报备与公开</h3>
      <div style="margin-left: 20px">
        <el-button
          type="primary"
          icon="el-icon-plus"
          @click="handleAdd"
          v-if="repmodeList.length == 0"
          >新增选用组织确定选购模式</el-button
        >
      </div>
    </div>


    <el-table
      v-loading="loading"
      :data="repmodeList"
    >
      <el-table-column label="序号" align="center" type="index" />
      <el-table-column label="标题" align="center" prop="title" />
      <el-table-column label="选购模式" align="center" prop="purchaseMode">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.cgmslx"
            :value="scope.row.purchaseMode"
          />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" />

      <el-table-column
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <el-button
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            >查看</el-button
          >
          <el-button
            v-hasPermi="['xfpt:xgmsbbgk-edit']"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            >修改</el-button
          >
          <el-button
            v-hasPermi="['xfpt:xgmsbbgk-remove']"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table> 

    <!-- 添加或修改选用组织确定选购模式会议对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="会议名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入会议名称" />
        </el-form-item>
        <el-form-item label="选购模式" prop="purchaseMode">
          <el-select
            style="width: 100%"
            v-model="form.purchaseMode"
            placeholder="请选择选购模式"
          >
            <el-option
              v-for="item in dict.type.cgmslx"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="报备表" prop="reportForm">
          <FileUpload v-model="form.reportForm" :gaizhang="true" :fileType="['pdf']">
            <el-button slot="template" type="text" @click.stop="importTemplate"
              >下载模板</el-button
            >
          </FileUpload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    <!-- 查看选用组织确定选购模式会议详情对话框 -->
    <el-dialog
      title="查看详情"
      :visible.sync="viewOpen"
      width="700px"
      append-to-body
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="会议名称" :labelStyle="{textAlign: 'center'}">{{
          viewData.title
        }}</el-descriptions-item>
        <el-descriptions-item label="选购模式" :labelStyle="{textAlign: 'center'}">
          <dict-tag
            :options="dict.type.cgmslx"
            :value="viewData.purchaseMode"
          />
        </el-descriptions-item>
        <el-descriptions-item label="报备表" :labelStyle="{textAlign: 'center'}">
          <span v-if="viewData.reportForm">
            <FileUpload :value="viewData.reportForm" disabled />
          </span>
          <span v-else>无</span>
        </el-descriptions-item>
        <el-descriptions-item label="备注" v-if="viewData.remark">{{
          viewData.remark
        }}</el-descriptions-item>
      </el-descriptions> 

      <!-- <div class="update-records-title">更新记录</div>
      <el-table :data="updateRecords" style="width: 100%; margin-top: 15px">
        <el-table-column prop="czr" label="操作人" align="center" />
        <el-table-column prop="createTime" label="添加时间" align="center" />
      </el-table> -->

      <div slot="footer" class="dialog-footer">
        <el-button @click="viewOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listRepmode,
  getRepmode,
  delRepmode,
  addRepmode,
  updateRepmode,
} from "@/api/cg/repmode";

export default {
  dicts: ["cgmslx"],
  name: "Repmode",
  data() {
    return {
      // 遮罩层
      loading: true,

      // 总条数
      total: 0,
      // 选用组织确定选购模式会议表格数据
      repmodeList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示查看弹出层
      viewOpen: false,
      // 查看的记录详情
      viewData: {},
      // 更新记录列表
      updateRecords: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        teamId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        title: [
          { required: true, message: "会议名称不能为空", trigger: "blur" },
        ],
        purchaseMode: [
          { required: true, message: "选购模式不能为空", trigger: "blur" },
        ],
        reportForm: [
          { required: true, message: "报备表不能为空", trigger: "blur" },
        ],
      },
    };
  },
  props: {
    formId: {},
  },
  created() {
    this.queryParams.teamId = this.formId;
    this.getList();
  },
  methods: {
    importTemplate() {
      const link = document.createElement("a");
      link.href =
        process.env.VUE_APP_IMAGE_BASE_API +
        "/filestatics/download/校服选购模式报备表（公开）.xlsx";
      link.download = "校服选购模式报备表（公开）.xlsx";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
    handleView(row) {
      this.viewData = {};
      this.updateRecords = [];
      const id = row.id;
      getRepmode(id).then((response) => {
        this.viewData = response.data.xfptReportMode;
        this.updateRecords = response.data.operList;
        this.viewOpen = true;
      });
    },
    /** 查询选用组织确定选购模式会议列表 */
    getList() {
      this.loading = true;
      listRepmode(this.queryParams).then((response) => {
        this.repmodeList = response.rows;
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
        purchaseMode: null,
        reportForm: null,
        teamId: this.formId,
      };
      this.resetForm("form");
    },


    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加选用组织确定选购模式会议";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getRepmode(id).then((response) => {
        this.form = response.data.xfptReportMode;
        this.open = true;
        this.title = "修改选用组织确定选购模式会议";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.id != null) {
            updateRepmode(this.form).then((response) => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
              this.$emit("handleGetProcessMenu",this.form.purchaseMode);
            });
          } else {
            addRepmode(this.form).then((response) => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
              this.$emit("handleGetProcessMenu", this.form.purchaseMode);
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id;
      this.$modal
        .confirm("是否确认删除选中的数据项？")
        .then(function () {
          return delRepmode(ids);
        })
        .then(() => {
          this.getList();
          this.$emit("handlegetModeByTeamId");
          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => {});
    },

  },
};
</script>

<style scoped>
.header {
  margin-top: 0;
  margin-bottom: 15px;
  padding-bottom: 5px;
  border-bottom: 1px solid #ebeef5;
}
.detail-panel {
  padding: 20px 0;
}

.detail-panel h3 {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}
.update-records-title {
  margin-top: 20px;
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 10px;
}
</style>
