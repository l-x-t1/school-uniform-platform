<template>
  <div class="detail-panel">
    <div class="header" style="display: flex; align-items: center">
      <h3>多校联合、一校为主模式(二)</h3>
      <div style="margin-left: 20px">
        <el-button
          icon="el-icon-upload"
          type="primary"
          v-hasPermi="['xfpt:mse-add']"
          @click="handleAdd"
          >发布选购信息</el-button
        >
      </div>
    </div>
    <el-form
      :model="queryParams"
      ref="queryForm"
      size="small"
      :inline="true"
      v-show="showSearch"
      label-width="68px"
    >
      <el-form-item label="标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否发布" prop="isRelease">
        <el-input
          v-model="queryParams.isRelease"
          placeholder="请输入是否发布"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="选用企业模式" prop="type">
        <el-select
          v-model="queryParams.type"
          placeholder="请选择选用企业模式"
          clearable
        >
          <el-option
            v-for="dict in dict.type.ggxzms"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
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
      :data="yxwzmodeList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center" type="index" />
      <el-table-column label="标题" align="center" prop="title" />
      <el-table-column label=" 类型" align="center" prop="modeType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.zblx" :value="scope.row.modeType" />
        </template>
      </el-table-column>
      <!-- <el-table-column label="是否提供样衣" align="center" prop="isSamplesPic">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isSamplesPic === 1" type="success">是</el-tag>
          <el-tag v-else type="danger">否</el-tag>
        </template>
      </el-table-column> -->
      <el-table-column label="是否发布" align="center" prop="isRelease">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isRelease == 1" type="success">已发布</el-tag>
          <el-tag v-else-if="scope.row.isRelease == 2" type="danger"
            >已撤回</el-tag
          >
          <el-tag v-else type="danger">未发布</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="添加时间" align="center" prop="createTime">
        <template slot-scope="scope">
          {{ parseTime(scope.row.createTime, "{y}-{m}-{d} {h}:{i}:{s}") }}
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
            v-hasPermi="['xfpt:mse-ch']"
            >撤回</el-button
          >
          <el-button
            v-else
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['xfpt:mse-edit']"
            >修改</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            >查看</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['xfpt:mse-remove']"
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

    <!-- 添加或修改一校为主模式（模式二）对话框 -->
    <el-dialog
      :title="title"
      :visible.sync="open"
      width="1000px"
      append-to-body
    >
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="采购模式" prop="modeType">
          <el-radio-group v-model="form.modeType" v-if="!readOnly">
            <el-radio
              v-for="dict in dict.type.zblx"
              :key="dict.value"
              :label="dict.value"
              >{{ dict.label }}</el-radio
            >
          </el-radio-group>
          <dict-tag :options="dict.type.zblx" :value="form.modeType" v-else />
        </el-form-item>
        <el-form-item
          label="选用企业模式"
          prop="type"
          v-if="form.modeType === '1'"
        >
          <el-radio-group v-model="form.type" v-if="!readOnly">
            <el-radio
              v-for="dict in dict.type.ggxzms"
              :key="dict.value"
              :label="dict.value"
              >{{ dict.label }}</el-radio
            >
          </el-radio-group>
          <dict-tag :options="dict.type.ggxzms" :value="form.type" v-else />
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input
            v-model="form.title"
            placeholder="请输入标题"
            :readonly="readOnly"
          />
        </el-form-item>
        <!-- <el-form-item label="是否提供样衣图" prop="isSamplesPic">
          <el-radio-group v-model="form.isSamplesPic" v-if="!readOnly">
            <el-radio :label="'1'">是</el-radio>
            <el-radio :label="'0'">否</el-radio>
          </el-radio-group>
          <div v-else>
            <el-tag v-if="form.isSamplesPic == 1" type="success">是</el-tag>
            <el-tag v-else type="danger">否</el-tag>
          </div>
        </el-form-item> -->
        <el-form-item
          label="样衣图"
          prop="samplesPic"
          v-if="form.isSamplesPic == 1"
        >
          <image-upload
            v-model="form.samplesPic"
            :baseUrl="baseUrl"
            v-if="!readOnly"
          />
          <div v-else>
            <image-preview
              v-for="(item, index) in form.samplesPic"
              :key="index"
              :src="item"
              :width="100"
              :height="100"
              style="margin-right: 10px"
            />
          </div>
        </el-form-item>
        <el-form-item label="采购公告" prop="noticeFile">
          <file-upload v-model="form.noticeFile" :disabled="readOnly">
            <el-button slot="template" type="text" @click.stop="importTemplate"
              >下载模板</el-button
            >
          </file-upload>
        </el-form-item>
        <el-form-item
          label="其他文件"
          prop="otherFile"
          v-if="form.id == null || form.otherFile != null"
        >
          <file-upload v-model="form.otherFile" :disabled="readOnly" />
        </el-form-item>
        <el-form-item label="联合学校名单" prop="schoolName">
          <el-input
            type="textarea"
            v-model="form.schoolName"
            placeholder="请输入联合学校名单"
            :readonly="readOnly"
          />
        </el-form-item>
        <div>
          <!-- <span
            style="
              width: 120px;
              text-align: right;
              display: inline-block;
              font-size: 14px;
              padding-right: 10px;
              font-weight: 500;
              color: #303133;
              box-sizing: border-box;
            "
            >厂商信息</span
          >
          <el-button
            type="primary"
            size="mini"
            @click="addEnterprise"
            v-if="!readOnly"
            >添加一行</el-button
          > -->
          <el-table :data="form.supplierList" border style="margin-top: 10px">
            <el-table-column label="厂商名称" prop="name">
              <template slot-scope="scope">
                <el-form-item
                  label-width="0"
                  :prop="'supplierList.' + scope.$index + '.name'"
                  :rules="{ required: true, message: '请输入厂商名称' }"
                >
                  <el-input
                    v-model="scope.row.name"
                    placeholder="请输入厂商名称"
                    :readonly="readOnly"
                  />
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column label="附件">
              <template slot-scope="scope">
                <el-form-item
                  label-width="0"
                  :prop="'supplierList.' + scope.$index + '.file'"
                  :rules="{ required: true, message: '请上传附件' }"
                >
                  <file-upload
                    v-model="scope.row.file"
                    :fileList="scope.row.file"
                    :disabled="readOnly"
                  />
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column
              label="操作"
              prop="operate"
              width="100px"
              align="center"
            >
              <template slot-scope="scope">
                <el-button
                  type="primary"
                  icon="el-icon-plus"
                  circle
                  size="mini"
                  v-if="scope.$index === form.supplierList.length - 1"
                  @click="addEnterprise"
                ></el-button>
                <el-button
                  :disabled="scope.row.zw == '2'"
                  type="danger"
                  icon="el-icon-minus"
                  circle
                  size="mini"
                  @click="deleteEnterprise(scope.$index, scope.row)"
                  v-show="form.supplierList.length > 1"
                ></el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <el-form-item label="是否发布" prop="isRelease">
          <el-radio-group v-model="form.isRelease" v-if="!readOnly">
            <el-radio :label="'1'">是</el-radio>
            <el-radio :label="'0'">否</el-radio>
          </el-radio-group>
          <div v-else>
            <el-tag v-if="form.isRelease == 1" type="success">已发布</el-tag>
            <el-tag v-else type="danger">未发布</el-tag>
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" v-if="!readOnly"
          >确 定</el-button
        >
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listYxwzmode,
  getYxwzmode,
  delYxwzmode,
  addYxwzmode,
  updateYxwzmode,
  withdrawYxwzmode,
} from "@/api/cg/yxwzmode";

export default {
  name: "EnterpriseModeTwo",
  dicts: ["ggxzms", "zblx"],
  props: ["formId"],
  data() {
    return {
      readOnly: false,
      baseUrl: "http://127.0.0.1:9300",
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
      // 一校为主模式（模式二）表格数据
      yxwzmodeList: [],
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
        samplesPic: null,
        otherFile: null,
        bmjzTime: null,
        isRelease: null,
        teamId: this.formId,
        deptId: null,
        ancestors: null,
        modeType: null,
        schoolName: null,
        releaseTime: null,
        type: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        schoolName: [
          { required: true, message: "请输入联合学校名单", trigger: "blur" },
        ],
        title: [{ required: true, message: "请输入标题", trigger: "blur" }],
        isSamplesPic: [
          { required: true, message: "请输入是否提供样衣图", trigger: "blur" },
        ],
        isRelease: [
          { required: true, message: "请输入是否发布", trigger: "blur" },
        ],
        modeType: [
          { required: true, message: "请选择采购模式", trigger: "blur" },
        ],
        type: [
          { required: true, message: "请选择选用企业模式", trigger: "blur" },
        ],
        samplesPic: [
          { required: true, message: "请上传样衣图", trigger: "blur" },
        ],
        noticeFile: [
          { required: true, message: "请上传采购公告", trigger: "blur" },
        ],
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal
        .confirm("是否确认删除选中的数据项？")
        .then(function () {
          return delYxwzmode(ids);
        })
        .then(() => {
          this.getList();
          this.$emit("handleGetProcessMenu");
          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => {});
    },
    importTemplate() {
      const link = document.createElement("a");
      link.href =
        process.env.VUE_APP_IMAGE_BASE_API +
        "/filestatics/download/附件5模式三.zip";
      link.download = "附件5模式二.rar";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
    /** 查询一校为主模式（模式二）列表 */
    getList() {
      this.loading = true;
      listYxwzmode(this.queryParams).then((response) => {
        this.yxwzmodeList = response.rows;
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
        bmjzTime: null,
        isRelease: null,
        teamId: this.formId,
        deptId: null,
        ancestors: null,
        modeType: null,
        schoolName: null,
        releaseTime: null,
        type: null,
        supplierList: [
          {
            name: null,
            price: null,
            file: "",
          },
        ],
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
      this.readOnly = false;
      this.title = "添加一校为主模式（模式二）";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      this.readOnly = false;
      const id = row.id || this.ids;
      getYxwzmode(id).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = "修改一校为主模式（模式二）";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.id != null) {
            updateYxwzmode(this.form).then((response) => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
              this.$emit("handleGetProcessMenu");
            });
          } else {
            addYxwzmode(this.form).then((response) => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
              this.$emit("handleGetProcessMenu");
            });
          }
        }
      });
    },
    // 添加企业
    addEnterprise() {
      this.form.supplierList.push({
        name: null,
        price: null,
        file: null,
      });
    },
    // 删除企业
    deleteEnterprise(index) {
      this.form.supplierList.splice(index, 1);
    },
    //撤回
    handleWithdraw(row) {
      this.$modal
        .confirm("确定要撤回该选购信息吗？")
        .then(function () {
          return withdrawYxwzmode({
            id: row.id,
            isRelease: "2",
          });
        })
        .then(() => {
          this.$modal.msgSuccess("撤回成功");
          this.getList();
        })
        .catch(() => {});
    },
    handleView(row) {
      this.readOnly = true;
      getYxwzmode(row.id).then((response) => {
        this.form = response.data;
        if (this.form.isSamplesPic == 1) {
          this.form.samplesPic = this.form.samplesPic.split(",").map((item) => {
            return this.baseUrl + item;
          });
        }
        this.open = true;
        this.title = "查看一校为主模式（模式二）";
      });
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
</style>
