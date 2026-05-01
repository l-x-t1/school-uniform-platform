<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <div>
      <el-form
        :model="queryParams"
        ref="queryForm"
        :inline="true"
        v-show="showSearch"
      >
        <el-form-item label="会议名称" prop="bidEvaluationTitle">
          <el-input
            v-model="queryParams.bidEvaluationTitle"
            placeholder="请输入会议名称进行搜索"
            clearable
            style="width: 240px"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>

        <el-form-item label="时间搜索" prop="timeRange">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            style="width: 240px"
          />
        </el-form-item>

        <!-- <el-form-item label="教育局查阅状态" prop="readStatus">
          <el-select v-model="queryParams.readStatus" placeholder="请选择" clearable style="width: 170px">
            <el-option label="全部" value=""></el-option>
            <el-option label="已查阅" value="1"></el-option>
            <el-option label="未查阅" value="0"></el-option>
            <el-option label="学校已撤回" value="2"></el-option>
          </el-select>
        </el-form-item> -->
        <el-form-item>
          <el-button
            size="small"
            type="primary"
            icon="el-icon-search"
            @click="handleQuery"
            >搜索</el-button
          >
          <el-button size="small" icon="el-icon-refresh" @click="resetQuery"
            >重置</el-button
          >
        </el-form-item>
      </el-form>
      <el-button type="primary" icon="el-icon-plus" @click="openAddDialog"
        >评标结果备案</el-button
      >
    </div>
    <!-- 表格 -->
    <el-table
      v-loading="loading"
      :data="modeList"
      style="width: 100%; margin-top: 10px"
    >
      <el-table-column
        label="评标结果会议名称"
        prop="bidEvaluationTitle"
        align="center"
        min-width="180"
      ></el-table-column>
      <el-table-column
        label="提交时间"
        prop="createTime"
        align="center"
        width="180"
      ></el-table-column>
      <el-table-column label="操作" align="center" width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['school:mode:edit']"
            >修改</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['school:mode:remove']"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加评标结果弹窗 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="750px"
      append-to-body
    >
      <el-form ref="form" :model="form" :rules="rules" label-width="140px">
        <el-form-item label="评标结果会议名称" prop="bidEvaluationTitle">
          <el-input
            v-model="form.bidEvaluationTitle"
            placeholder="请输入评标结果会议名称"
          />
        </el-form-item>

        <el-form-item label="上传评标结果" prop="fileType">
          <el-radio-group v-model="radio" @change="handleRadioChange">
            <el-radio :label="1">文档类（支持.pdf格式）</el-radio>
            <el-radio :label="2">图片类（支持.png、.jpg、.jpeg格式）</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item style="display: flex; align-items: center" label="">
          <el-upload
            accept=".pdf"
            style=""
            :headers="upload.headers"
            :on-success="handleUploadSuccess"
            v-if="radio == 1"
            class="upload-demo"
            drag
            :action="upload.url"
            multiple
            :file-list="fileList"
          >
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
          </el-upload>
          <el-upload
            :limit="10"
            accept=".png,.jpg,.jpeg"
            style=""
            v-if="radio == 2"
            class="upload-demo"
            :headers="upload.headers"
            :action="upload.url"
            list-type="picture-card"
            :on-preview="handlePictureCardPreview"
            :on-success="handlePictureSuccess"
            :on-remove="handleRemove"
            :file-list="fileListPic"
          >
            <i class="el-icon-plus"></i>
          </el-upload>
          <!-- <el-dialog append-to-body :visible.sync="dialogVisible">
            <img width="100%" :src="dialogImageUrl" alt="">
          </el-dialog> -->
        </el-form-item>

        <el-form-item>
          <el-button type="text" @click="downloadTemplate"
            >评标结果模板下载</el-button
          >
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">提交</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getToken } from "@/utils/auth";
import { addBidEvaluationResults } from "@/api/school/results";
import axios from "axios";
import {
  listMode,
  getMode,
  delMode,
  addMode,
  updateMode,
} from "@/api/school/mode";

export default {
  name: "Results",
  props: {
    formId: {
      type: String,
      default: null,
    },
  },
  data() {
    return {
      // 显示搜索条件
      showSearch: true,
      // 加载状态
      loading: false,
      // 总条数
      total: 0,
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        bidEvaluationTitle: undefined,
        readStatus: undefined,
        type: "2",
      },
      // 评标结果列表
      modeList: [],
      // 对话框标题
      dialogTitle: "添加评标结果",
      // 对话框可见性
      dialogVisible: false,
      // 表单对象
      form: {
        bidEvaluationTitle: undefined,
        fileType: 1,
        bidEvaluationResults: "",
        type: 2,
      },
      // 表单校验规则
      rules: {
        bidEvaluationTitle: [
          {
            required: true,
            message: "请输入评标结果会议名称",
            trigger: "blur",
          },
        ],
        fileType: [
          { required: true, message: "请选择上传类型", trigger: "change" },
        ],
        isPublic: [
          {
            validator: (rule, value, callback) => {
              if (!value) {
                callback(new Error("请确认信息无误"));
              } else {
                callback();
              }
            },
            trigger: "change",
          },
        ],
      },
      // 上传URL
      upload: {
        // 设置上传的请求头部
        headers: { Authorization: "Bearer " + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/file/upload",
      },
      // 图片文件列表
      fileListPic: [],
      // 文档文件列表
      fileList: [],
      // 预览可见性
      dialogVisible: false,
      // 预览URL
      dialogImageUrl: "",
      // 当前选择的文件类型
      radio: 1,
    };
  },
  created() {
    this.getList();
    this.form.teamId = this.formId;
  },
  methods: {
    // 获取列表数据
    getList() {
      this.loading = true;
      listMode(this.queryParams).then((response) => {
        this.modeList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },

    // 搜索按钮操作
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },

    // 重置按钮操作
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    reset() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        bidEvaluationTitle: undefined,
        readStatus: undefined,
        type: "2",
      };
      this.dateRange = [];
      this.getList();
    },
    // 打开添加对话框
    openAddDialog() {
      this.dialogTitle = "添加评标结果";
      this.form = {
        bidEvaluationTitle: undefined,
        fileType: 1,
        bidEvaluationResults: "",
        type: 2,
        teamId: this.formId,
      };
      this.radio = 1;
      this.fileList = [];
      this.fileListPic = [];
      this.dialogVisible = true;
    },

    // 查看详情
    handleView(row) {
      console.log("查看", row);
      // 实现查看功能
    },

    // 编辑操作
    handleEdit(row) {
      console.log("编辑", row);
      this.dialogTitle = "编辑评标结果";
      this.dialogVisible = true;
      // 复制行数据到表单
      this.form = { ...row };

      // 设置文件类型
      this.radio = parseInt(row.fileType) || 1;

      // 处理文件显示
      if (row.bidEvaluationResults) {
        const urls = row.bidEvaluationResults.split("&&&");
        const names = row.bidEvaluationResultsName
          ? row.bidEvaluationResultsName.split("&&&")
          : urls.map((url) => url.split("/").pop());

        // 根据类型显示不同的文件列表
        if (this.radio === 1) {
          this.fileList = urls.map((url, index) => {
            return {
              name: names[index] || `文件${index + 1}`,
              url: this.getFullUrl(url),
              response: {
                code: 200,
                data: {
                  url: url,
                  name: names[index] || `文件${index + 1}`,
                },
              },
            };
          });
          this.fileListPic = [];
        } else {
          this.fileListPic = urls.map((url, index) => {
            return {
              name: names[index] || `图片${index + 1}`,
              url: this.getFullUrl(url),
              response: {
                code: 200,
                data: {
                  url: url,
                  name: names[index] || `图片${index + 1}`,
                },
              },
            };
          });
          this.fileList = [];
        }
      }
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getMode(id).then((response) => {
        this.form = response.data;
        this.dialogVisible = true;
        this.title = "修改评标结果";
      });
    },
    // 辅助方法：获取完整URL
    getFullUrl(url) {
      if (!url) return "";
      if (url.startsWith("http") || url.startsWith("https")) {
        return url;
      }
      return (
        process.env.VUE_APP_IMAGE_BASE_API +
        (url.startsWith("/") ? url : "/" + url)
      );
    },

    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal
        .confirm("确认删除该评标结果吗？")
        .then(function () {
          return delMode(ids);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => {});
    },

    // 处理文件类型切换
    handleRadioChange() {
      this.form.fileType = this.radio;
      // 不清空已有的文件列表
    },

    // 图片预览
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url || URL.createObjectURL(file.raw);
      this.dialogVisible = true;
    },

    // 移除文件
    handleRemove(file, fileList) {
      this.fileListPic = fileList;
    },

    // 图片上传成功
    handlePictureSuccess(response, file, fileList) {
      console.log("图片上传成功", response, file, fileList);
      // 处理上传后的图片列表
      if (response.code === 200) {
        // 确保每个文件都有正确的URL
        this.fileListPic = fileList.map((item) => {
          if (item.response && item.response.data) {
            const url = item.response.data.url;
            // 确保URL包含基础路径
            const fullUrl = url.startsWith("http")
              ? url
              : process.env.VUE_APP_IMAGE_BASE_API + url;
            return {
              name: item.response.data.name || item.name,
              url: fullUrl,
              response: {
                ...item.response,
                data: {
                  ...item.response.data,
                  url: fullUrl,
                },
              },
            };
          } else if (item.url) {
            // 处理已有的文件
            const fullUrl = item.url.startsWith("http")
              ? item.url
              : process.env.VUE_APP_IMAGE_BASE_API + item.url;
            return {
              ...item,
              url: fullUrl,
            };
          } else {
            return item;
          }
        });

        // 强制更新视图
        this.$forceUpdate();
      } else {
        this.fileListPic = fileList;
      }
    },

    // 文档上传成功
    handleUploadSuccess(res, file, fileList) {
      console.log("文档上传成功", fileList);

      // 处理上传后的文档列表
      if (res.code === 200) {
        // 确保每个文件都有正确的URL
        this.fileList = fileList.map((item) => {
          if (item.response && item.response.data) {
            const url = item.response.data.url;
            // 确保URL包含基础路径
            const fullUrl = url.startsWith("http")
              ? url
              : process.env.VUE_APP_IMAGE_BASE_API + url;
            return {
              name: item.response.data.name || item.name,
              url: fullUrl,
              response: {
                ...item.response,
                data: {
                  ...item.response.data,
                  url: fullUrl,
                },
              },
            };
          } else if (item.url) {
            // 处理已有的文件
            const fullUrl = item.url.startsWith("http")
              ? item.url
              : process.env.VUE_APP_IMAGE_BASE_API + item.url;
            return {
              ...item,
              url: fullUrl,
            };
          } else {
            return item;
          }
        });

        // 强制更新视图
        this.$forceUpdate();
      } else {
        this.fileList = fileList;
      }
    },

    // 处理文件URL
    processFileUrls() {
      // 初始化为空字符串而不是undefined
      this.form.bidEvaluationResults = "";

      // 添加调试日志
      console.log("处理文件URL开始", {
        radio: this.radio,
        fileList: this.fileList,
        fileListPic: this.fileListPic,
      });

      if (this.radio == 1 && this.fileList && this.fileList.length > 0) {
        const urls = [];
        const names = [];

        this.fileList.forEach((item) => {
          if (item.response && item.response.data) {
            // 从完整URL中提取相对路径部分
            let url = item.response.data.url;
            // 查找"/statics/"的位置，如果存在则只取该部分及之后的内容
            const staticsIndex = url.indexOf("/statics/");
            if (staticsIndex !== -1) {
              url = url.substring(staticsIndex);
            }
            urls.push(url);
            names.push(item.response.data.name || item.name);
          } else if (item.url) {
            // 如果是已有的URL，同样提取相对路径
            let url = item.url;
            const staticsIndex = url.indexOf("/statics/");
            if (staticsIndex !== -1) {
              url = url.substring(staticsIndex);
            }
            urls.push(url);
            names.push(item.name || "未命名文件");
          }
        });

        if (urls.length > 0) {
          this.form.bidEvaluationResults = urls.join("&&&");
        }
        console.log("文档处理完成:", {
          bidEvaluationResults: this.form.bidEvaluationResults,
        });
      } else if (
        this.radio == 2 &&
        this.fileListPic &&
        this.fileListPic.length > 0
      ) {
        const urls = [];
        const names = [];

        this.fileListPic.forEach((item) => {
          if (item.response && item.response.data) {
            // 从完整URL中提取相对路径部分
            let url = item.response.data.url;
            // 查找"/statics/"的位置，如果存在则只取该部分及之后的内容
            const staticsIndex = url.indexOf("/statics/");
            if (staticsIndex !== -1) {
              url = url.substring(staticsIndex);
            }
            urls.push(url);
            names.push(item.response.data.name || item.name);
          } else if (item.url) {
            // 如果是已有的URL，同样提取相对路径
            let url = item.url;
            const staticsIndex = url.indexOf("/statics/");
            if (staticsIndex !== -1) {
              url = url.substring(staticsIndex);
            }
            urls.push(url);
            names.push(item.name || "未命名图片");
          }
        });

        if (urls.length > 0) {
          this.form.bidEvaluationResults = urls.join("&&&");
        }
        console.log("图片处理完成:", {
          bidEvaluationResults: this.form.bidEvaluationResults,
        });
      }

      // 确保字段不为undefined
      if (!this.form.bidEvaluationResults) this.form.bidEvaluationResults = "";
    },

    // 验证文件上传
    validateFileUpload() {
      // 文档上传检查
      if (this.radio === 1) {
        if (!this.fileList || this.fileList.length === 0) {
          return "请上传合同文件";
        }
      }
      // 图片上传检查
      else if (this.radio === 2) {
        if (!this.fileListPic || this.fileListPic.length === 0) {
          return "请上传合同图片";
        }
      }
      return false; // 没有错误
    },

    // 下载模板
    downloadTemplate() {
      // 实现模板下载功能
      axios({
        method: "get",
        url:
          process.env.VUE_APP_IMAGE_BASE_API +
          "/statics/file/评标结果模板.docx",
        responseType: "blob",
        headers: {
          Authorization: "Bearer " + getToken(),
        },
      })
        .then((response) => {
          const blob = new Blob([response.data], {
            type: "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
          });
          const link = document.createElement("a");
          link.href = window.URL.createObjectURL(blob);
          link.download = "评标结果模板.docx";
          link.click();
          window.URL.revokeObjectURL(link.href);
        })
        .catch(() => {
          this.$message.error("模板下载失败");
        });
    },

    // 提交表单
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          // 设置文件类型
          this.form.fileType = this.radio;

          // 检查文件上传
          const fileUploadError = this.validateFileUpload();
          if (fileUploadError) {
            this.$message.error(fileUploadError);
            return;
          }

          // 处理文件URL
          this.processFileUrls();

          console.log("准备提交的数据:", this.form);
          if (this.form.id != null) {
            updateMode(this.form).then((response) => {
              this.$modal.msgSuccess("修改成功");
              this.dialogVisible = false;
              this.getList();
            });
          } else {
            // 在实际项目中调用保存接口
            addBidEvaluationResults(this.form).then((res) => {
              if (res.code === 200) {
                this.$message.success("提交成功");
                this.dialogVisible = false;
                this.getList();
              }
            });
          }
        }
      });
    },
  },
};
</script>

<style scoped>
.search-container {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
}

.tab-container {
  margin-top: 20px;
}

.el-upload__tip {
  line-height: 1.2;
  color: #909399;
  font-size: 12px;
}

.dialog-footer {
  text-align: center;
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

::v-deep .el-upload--picture-card i {
  font-size: 18px;
}

::v-deep .el-upload-dragger {
  width: 500px;
  height: 40px;
}
</style>
