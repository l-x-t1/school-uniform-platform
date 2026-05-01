<template>
  <div class="detail-panel">
    <div class="header" style="display: flex; align-items: center">
      <h3>委托招标机构确定入围企业(二)</h3>
      <div style="margin-left: 20px">
        <el-button
          icon="el-icon-upload"
          type="primary"
          v-hasPermi="['xfpt:mss-add']"
          @click="handleAdd"
          v-if="modeList.length == 0"
          >发布选购信息</el-button
        >
      </div>
    </div>
    <!-- <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="公告名称" prop="noticeName">
        <el-input v-model="queryParams.noticeName" placeholder="请输入公告名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>

      <el-form-item label="发布状态" prop="isRelease">
        <el-select v-model="queryParams.isRelease" placeholder="请选择发布状态" clearable>
          <el-option label="已发布" value="1"></el-option>
          <el-option label="已撤回" value="0"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button size="small" type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button size="small" icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form> -->
    <el-table
      v-loading="loading"
      :data="modeList"
      @selection-change="handleSelectionChange"
    >
      <!-- <el-table-column type="selection" width="55" align="center" /> -->
      <el-table-column label="序号" align="center" type="index" />
      <el-table-column label="公告名称" align="center" prop="noticeName" />
      <el-table-column label="发布时间" align="center" prop="createTime" />
      <el-table-column label="是否发布" align="center" prop="isRelease">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isRelease == 1" type="success">已发布</el-tag>
          <el-tag v-else-if="scope.row.isRelease == 2" type="danger"
            >已撤回</el-tag
          >
          <el-tag v-else type="danger">未发布</el-tag>
        </template>
      </el-table-column>

      <el-table-column
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <!-- <el-button
            v-if="scope.row.isRelease == 1"
            type="text"
            icon="el-icon-back"
            v-hasPermi="['xfpt:mss-ch']"
            @click="handleWithdraw(scope.row)"
            >撤回</el-button
          > -->
          <el-button
            v-if="scope.row.isRelease == 0"
            type="text"
            icon="el-icon-edit"
            v-hasPermi="['xfpt:mss-edit']"
            @click="handleUpdate(scope.row)"
            >修改</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="getBcList(scope.row)"
            v-hasPermi="['bc-gonggao']"
            v-if="scope.row.isRelease == 1"
            >补充</el-button
          >
          <el-button
            v-if="scope.row.isRelease == 1"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            >查看</el-button
          >

          <el-button
            v-if="scope.row.isRelease == 0"
            type="text"
            icon="el-icon-delete"
            v-hasPermi="['xfpt:mss-remove']"
            @click="handleDelete(scope.row)"
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

    <!-- 添加或修改选用组织委托招标机构确定企业模式对话框 -->
    <el-dialog
      :title="title"
      :visible.sync="open"
      width="1000px"
      append-to-body
    >
      <el-form label-width="120px" ref="form" :model="form" :rules="rules">
        <el-form-item label="公告名称" prop="noticeName">
          <el-input
            v-model="form.noticeName"
            placeholder="请输入公告名称"
            :readonly="readOnly"
          />
        </el-form-item>
        <el-form-item label="采购公告" prop="noticeFile">
          <file-upload
            :fileType="doxcFileTypes"
            v-model="form.noticeFile"
            :disabled="readOnly"
          >
            <el-button slot="template" type="text" @click.stop="importTemplate"
              >下载模板</el-button
            >
          </file-upload>
        </el-form-item>

        <el-form-item
          label="其他文件"
          prop="otherFile"
          v-if="readOnly == false || form.otherFile != null"
        >
          <file-upload
            :fileType="doxcFileTypes"
            v-model="form.otherFile"
            :disabled="readOnly"
          />
        </el-form-item>
        <el-form-item
          v-if="form.isSamplesPic == 1"
          label="样衣图"
          prop="samplesPic"
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
            />
          </div>
        </el-form-item>
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
        <el-button type="primary" @click="submitForm()" v-if="!readOnly"
          >确 定</el-button
        >
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    <!-- 补充列表 -->
    <el-dialog
      title="补充列表"
      :visible.sync="bcListShow"
      width="1000px"
      append-to-body
    >
      <el-button
        type="primary"
        size="mini"
        icon="el-icon-plus"
        @click="handleBcAdd"
        v-hasPermi="['school:rwggzs:add']"
        style="margin-bottom: 10px"
        >新增</el-button
      >
      <el-table :data="bcList" border>
        <el-table-column label="标题" prop="title" align="center" />
        <el-table-column label="补充时间" prop="createTime" align="center" />
        <el-table-column label="操作" align="center">
          <template slot-scope="scope">
            <el-button
              type="primary"
              size="mini"
              icon="el-icon-edit"
              @click="handleBcUpdate(scope.row)"
              >查看</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
    <!-- 补充弹窗 -->
    <el-dialog
      title="补充公告"
      :visible.sync="bcShow"
      width="1300px"
      append-to-body
    >
      <el-form ref="bcForm" :model="bcForm" :rules="bcRules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input
            v-model="bcForm.title"
            placeholder="请输入标题"
            :readonly="!bcXgType"
          />
        </el-form-item>
        <el-form-item label="附件" prop="file">
          <file-upload
            v-model="bcForm.file"
            :fileList="bcForm.file"
            :limit="1"
            :disabled="!bcXgType"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" v-if="bcXgType" @click="bcSubmitForm"
          >确 定</el-button
        >
        <el-button @click="bcCancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listMode,
  getMode,
  delMode,
  addMode,
  updateMode,
  withdrawMode,
} from "@/api/cg/mode";
import { getToken } from "@/utils/auth";
import axios from "axios";
import { readonly } from "vue";
import { getBcList, buchong } from "@/api/cg/rwggzs";
import { getXfptnotice } from "@/api/system/xfptnotice";
export default {
  name: "Mode",
  data() {
    return {
      readOnly: false,
      baseUrl: "http://127.0.0.1:9300",
      daterange: [],
      doxcFileTypes: [
        "docx",
        "doc",
        "xls",
        "xlsx",
        "ppt",
        "pptx",
        "txt",
        "pdf",
        "jpg",
        "png",
        "jpeg",
      ],
      imgFileTypes: ["jpg", "png", "jpeg"],
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
      // 选用组织委托招标机构确定企业模式表格数据
      modeList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        noticeName: null,
        noticeFile: null,
        otherFile: null,
        isSamplesPic: null,
        type: "0",
        meetingTitle: null,
        tenderingCompany: null,
        signingTime: null,
        contactsName: null,
        companyContactPhone: null,
        companyContactAddress: null,
        licenseFile: null,
        biddingAgencyContract: null,
        bidEvaluationTitle: null,
        bidEvaluationResults: null,
        isRelease: null,
        deptId: null,
        ancestors: null,
        createTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        noticeName: [
          { required: true, message: "公告名称不能为空", trigger: "blur" },
        ],
        noticeFile: [
          { required: true, message: "采购公告不能为空", trigger: "blur" },
        ],
        isSamplesPic: [
          {
            required: true,
            message: "是否提供样衣图不能为空",
            trigger: "blur",
          },
        ],
        samplesPic: [
          { required: true, message: "请上传样衣图", trigger: "blur" },
        ],
        isRelease: [
          { required: true, message: "请选择是否发布", trigger: "blur" },
        ],
      },
      upload: {
        headers: {
          Authorization: "Bearer " + getToken(),
        },
        url: process.env.VUE_APP_BASE_API + "/file/upload",
      },
      dialogVisibleImage: false,
      dialogImageUrl: "",
      fileListPic: [],
      fileList: [],
      viewDialogVisible: false,
      viewForm: {},
      zoomLevel: 100,
      currentPage: 1,
      totalPages: 1,
      isFileImage: true,
      previewDialogVisible: false,
      previewUrl: "",
      sampleFiles: [],
      otherFiles: [],
      // 补充公告相关
      bcListShow: false,
      bcList: [],
      bcShow: false,
      bcForm: {
        title: "",
        teamId: this.formId,
        catagory: "CGGG",
        file: "",
        id: null,
        type: "0",
      },
      bcRules: {
        title: [{ required: true, message: "请输入标题", trigger: "blur" }],
        file: [{ required: true, message: "请上传附件", trigger: "blur" }],
      },
      bcId: null,
      bcXgType: true,
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
        "/filestatics/download/附件5模式三.zip";
      link.download = "附件5模式三.zip";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
    handleView(row) {
      this.readOnly = true;
      // 获取最新数据
      getMode(row.id)
        .then((response) => {
          this.form = response.data;
          if (this.form.isSamplesPic == 1 && this.form.samplesPic != null) {
            this.form.samplesPic = this.form.samplesPic
              .split(",")
              .map((item) => {
                return this.baseUrl + item;
              });
          }
          this.open = true;
          this.title = "查看选购公告信息";
        })
        .catch((error) => {});
    },
    //撤回
    handleWithdraw(row) {
      this.$modal
        .confirm("确定要撤回该选购信息吗？")
        .then(function () {
          return withdrawMode({
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
    /** 查询选用组织委托招标机构确定企业模式列表 */
    getList() {
      this.loading = true;
      listMode(this.addDateRange(this.queryParams, this.daterange)).then(
        (response) => {
          this.modeList = response.rows;
          this.total = response.total;
          this.loading = false;
        }
      );
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        noticeName: null,
        noticeFile: null,
        otherFile: null,
        isSamplesPic: null,
        samplesPic: null,
        samplesPicUrls: "", // 添加样衣图URL字段
        samplesPicNames: "", // 添加样衣图名称字段
        type: "0",
        teamId: this.formId,
        isRelease: null,
      };
      this.resetForm("form");
      this.daterange = [];
      this.fileListPic = [];
      this.fileList = [];
    },
    bcReset() {
      this.bcForm = {
        title: "",
        teamId: this.formId,
        catagory: "CGGG",
        file: "",
        id: this.bcId, // 保证编辑时 id 正确
        type: "0",
      };
      this.resetForm("bcForm");
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
      this.readOnly = false;
      this.open = true;
      this.title = "发布选购公告信息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      this.readOnly = false;
      const id = row.id || this.ids;
      getMode(id).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = "修改选购公告信息";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          const formData = this.form;
          if (formData.id != null) {
            updateMode(formData).then((response) => {
              this.$modal.msgSuccess("修改成功");
              this.getList();
              this.$emit("handleGetProcessMenu");
              this.open = false;
            });
          } else {
            addMode(formData).then((response) => {
              this.$modal.msgSuccess("发布成功");
              this.getList();
              this.$emit("handleGetProcessMenu");
              this.open = false;
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal
        .confirm("是否确认删除选中的数据项？")
        .then(function () {
          return delMode(ids);
        })
        .then(() => {
          this.getList();
          this.$emit("handleGetProcessMenu");

          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => {});
    },
    /** 下载采购公告模板 */
    handleDownloadNoticeFile() {
      axios({
        method: "get",
        url:
          process.env.VUE_APP_IMAGE_BASE_API +
          "/statics/file/选购信息表模板-模式三.xlsx",
        responseType: "blob",
        headers: {
          Authorization: "Bearer " + getToken(),
        },
      }).then((response) => {
        const blob = new Blob([response.data], {
          type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        });
        const link = document.createElement("a");
        link.href = window.URL.createObjectURL(blob);
        link.download = "选购信息表模板-模式三.xlsx";
        link.click();
        window.URL.revokeObjectURL(link.href);
      });
    },
    /** 下载样衣图说明模板 */
    handleDownloadSampleTemplate() {
      axios({
        method: "get",
        url:
          process.env.VUE_APP_IMAGE_BASE_API +
          "/statics/file/样衣图说明模板.xlsx",
        responseType: "blob",
        headers: {
          Authorization: "Bearer " + getToken(),
        },
      }).then((response) => {
        const blob = new Blob([response.data], {
          type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        });
        const link = document.createElement("a");
        link.href = window.URL.createObjectURL(blob);
        link.download = "样衣图说明模板.xlsx";
        link.click();
        window.URL.revokeObjectURL(link.href);
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download(
        "school/mode/export",
        {
          ...this.queryParams,
        },
        `mode_${new Date().getTime()}.xlsx`
      );
    },
    /** 添加厂商 */
    addEnterprise() {
      // this.form.supplierList.push({
      //   name: "",
      //   file: [],
      // });
    },
    /** 删除厂商 */
    deleteEnterprise(index) {
      // this.form.supplierList.splice(index, 1);
    },
    // 补充公告相关方法（参考 ruweigonggao.vue 实现）
    bcReset() {
      this.bcForm = {
        title: "",
        teamId: this.formId,
        catagory: "CGGG",
        file: "",
        id: this.bcId, // 保证编辑时 id 正确
        type: "0",
      };
      this.resetForm("bcForm");
    },
    handleBcAdd() {
      this.bcReset();
      this.bcShow = true;
      this.bcXgType = true;
    },
    bcSubmitForm() {
      this.$refs["bcForm"].validate((valid) => {
        if (valid) {
          // 新增和编辑都调用 buchong，后端根据 id 是否存在区分
          buchong(this.bcForm).then((response) => {
            this.$modal.msgSuccess("补充成功");
            this.bcShow = false;
            this.getBcList(this.bcId);
          });
        }
      });
    },
    getBcList(row) {
      const data = {
        id: row.id ? row.id : row,
        type: "0",
        catagory: "CGGG",
      };
      this.bcId = row.id ? row.id : row;
      getBcList(data).then((response) => {
        this.bcList = response.data;
        this.bcListShow = true;
      });
    },
    bcCancel() {
      this.bcShow = false;
      this.bcReset();
    },
    handleBcUpdate(row) {
      const id = row.id;
      this.bcReset();
      getXfptnotice(id).then((response) => {
        this.bcXgType = false;
        this.bcForm = response.data;
        this.bcForm.file = response.data.ggFile;
        this.bcShow = true;
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

::v-deep .el-upload-dragger {
  background-color: #fff;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  width: 560px;
  height: 40px;
  text-align: center;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

::v-deep .el-upload-list--picture-card .el-upload-list__item {
  overflow: hidden;
  background-color: #fff;
  border: 1px solid #c0ccda;
  border-radius: 6px;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  width: 100px;
  height: 100px;
  margin: 0 8px 8px 0;
  display: inline-block;
}

::v-deep .el-upload--picture-card {
  width: 100px;
  height: 100px;
  line-height: 100px;
}

::v-deep .el-upload__text {
  color: #606266;
  font-size: 14px;
  text-align: center;
}

::v-deep .el-upload__text em {
  color: #409eff;
  font-style: normal;
}

/* 详情页样式 */
.result-detail-container {
  background-color: #f5f7fa;
  border-radius: 4px;
  padding: 0;
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB",
    "Microsoft YaHei", Arial, sans-serif;
}

.result-header {
  padding: 24px;
  border-radius: 4px 4px 0 0;
  color: #303133;
  text-align: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.header-title {
  margin: 0 0 15px 0;
  font-size: 22px;
  font-weight: 500;
  line-height: 1.4;
}

.header-meta {
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 14px;
  color: #606266;
}

.header-meta span {
  margin: 0 12px;
  display: flex;
  align-items: center;
}

.header-meta span i {
  margin-right: 5px;
}

.detail-card {
  margin: 16px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  background-color: #fafafa;
  border-bottom: 1px solid #f0f0f0;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.card-header i {
  font-size: 18px;
  margin-right: 8px;
  color: #1890ff;
}

.header-actions {
  margin-left: auto;
}

.card-body {
  padding: 20px;
}

.pdf-viewer {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
}

.pdf-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 15px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
}

.pdf-content {
  height: 500px;
  overflow: auto;
  display: flex;
  justify-content: center;
  transform-origin: center top;
  background-color: #e5e5e5;
}

.pdf-content iframe {
  width: 100%;
  height: 100%;
  border: none;
}

.pdf-content img {
  max-width: 100%;
  height: auto;
}

.file-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.file-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  transition: all 0.3s;
}

.file-item:hover {
  background-color: #f5f7fa;
  border-color: #d9ecff;
}

.file-icon {
  width: 40px;
  height: 40px;
  background-color: #f0f6ff;
  border-radius: 4px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 12px;
}

.file-icon i {
  font-size: 20px;
  color: #1890ff;
}

.file-info {
  flex: 1;
}

.file-name {
  font-size: 14px;
  color: #303133;
  margin-bottom: 5px;
  word-break: break-all;
}

.file-preview-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0;
  max-height: 80vh;
  overflow: auto;
}
</style>
