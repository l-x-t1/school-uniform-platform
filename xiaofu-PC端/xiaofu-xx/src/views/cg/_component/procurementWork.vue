<template>
  <div class="detail-panel">
    <div class="header" style="display: flex; align-items: center">
      <h3>学校校服采购工作总结</h3>
      <div style="margin-left: 20px">
        <el-button type="primary" v-hasPermi="['school:cglcgzzj:add']" icon="el-icon-plus" v-if="cglcgzzjList.length == 0" @click="handleAdd"
          >添加</el-button
        >
      </div>
    </div>
    <el-table :data="cglcgzzjList" style="width: 100%; margin-top: 10px">
      <el-table-column label="序号" align="center" type="index" />
      <el-table-column label="名称" align="center" prop="title" />
      <!-- <el-table-column label="附件" align="center">
        <template slot-scope="scope">
          <el-link
            v-if="scope.row.fileUrl"
            :href="scope.row.fileUrl"
            target="_blank"
            >下载</el-link
          >
          <span v-else>无</span>
        </template>
      </el-table-column> -->
      <el-table-column label="创建时间" align="center" prop="createTime" />
      <el-table-column align="center" label="操作">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleView(scope.row)"
            >查看</el-button
          >
          <el-button type="text" v-hasPermi="['school:cglcgzzj:edit']" size="small" @click="handleUpdate(scope.row)"
            >编辑</el-button
          >
          <el-button type="text" v-hasPermi="['school:cglcgzzj:remove']" size="small" @click="handleDelete(scope.row)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      :title="title"
      :visible.sync="open"
      width="600px"
      append-to-body
    >
      <el-form :model="form" ref="form" label-width="80px">
        <el-form-item
          label="名称"
          prop="title"
          :rules="[{ required: true, message: '请输入名称', trigger: 'blur' }]"
        >
          <el-input v-model="form.title" maxlength="50" :readonly="readType" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="附件" prop="file" :rules="[{ required: true, message: '请上传附件', trigger: 'blur' }]">
          <FileUpload v-model="form.file" :fileType="['pdf']" :gaizhang="true" :disabled="readType"></FileUpload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer" v-if="!readType">
        <el-button type="primary"  @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { listCglcgzzj, getCglcgzzj, delCglcgzzj, addCglcgzzj, updateCglcgzzj } from "@/api/cg/cglcgzzj";
export default {
  name: "ProcurementWork",
  props: {
    formId: {
      type: String,
      default: "",
    },
  },
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
      // 学校校服采购工作总结表格数据
      cglcgzzjList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        file: null,
        teamId: this.formId,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询学校校服采购工作总结列表 */
    getList() {
      this.loading = true;
      listCglcgzzj(this.queryParams).then(response => {
        this.cglcgzzjList = response.rows;
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
        file: null,
        remark: null,
        teamId: this.formId,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        deptId: null,
        delFlag: null,
        ancestors: null
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
      this.title = "添加学校校服采购工作总结";
      this.readType = false;
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getCglcgzzj(id).then(response => {
        this.form = response.data;
        this.readType = false;
        this.open = true;
        this.title = "修改学校校服采购工作总结";
      });
    },
    /** 查看按钮操作 */
    handleView(row) {
      this.reset();
      const id = row.id || this.ids
      getCglcgzzj(id).then(response => {
        this.form = response.data;
        this.readType = true;
        this.open = true;
        this.title = "查看学校校服采购工作总结";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateCglcgzzj(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
              this.$emit("handleGetProcessMenu",response.data);
            });
          } else {
            addCglcgzzj(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
              this.$emit("handleGetProcessMenu",response.data);
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除学校校服采购工作总结编号为"' + ids + '"的数据项？').then(function() {
        return delCglcgzzj(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('school/cglcgzzj/export', {
        ...this.queryParams
      }, `cglcgzzj_${new Date().getTime()}.xlsx`)
    }
  },
};
</script>
<style scoped lang="scss">
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
</style>
