<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryForm"
      :inline="true"
      v-show="showSearch"
      label-width="68px"
    >
      <el-form-item label="公告名称" prop="noticeName">
        <el-input
          v-model="queryParams.noticeName"
          placeholder="请输入公告名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item label="发布时间">
        <el-date-picker
          clearable
          v-model="daterange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
          format="yyyy-MM-dd"
          placeholder="请选择发布时间"
        >
        </el-date-picker>
      </el-form-item>

      <el-form-item label="发布状态" prop="isRelease">
        <el-select
          v-model="queryParams.isRelease"
          placeholder="请选择发布状态"
          clearable
        >
          <el-option label="已发布" value="1"></el-option>
          <el-option label="已撤回" value="0"></el-option>
        </el-select>
      </el-form-item>

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

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd"
          >添加公告</el-button
        >
      </el-col>
      <right-toolbar
        :showSearch.sync="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>

    <el-table
      v-loading="loading"
      :data="modeList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="公告名称" align="center" prop="noticeName" />
      <el-table-column label="发布时间" align="center" prop="createTime" />

      <el-table-column label="是否发布" align="center" prop="isRelease">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isRelease == 1" type="success">已发布</el-tag>
          <el-tag v-else type="danger">已撤回</el-tag>
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
            type="text"
            icon="el-icon-download"
            @click="handleWithdraw(scope.row)"
            >撤回</el-button
          >
          <el-button
            v-if="scope.row.isRelease == 0"
            type="text"
            icon="el-icon-upload"
            @click="handleRelease(scope.row)"
            >发布</el-button
          >
          <el-button
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            >查看</el-button
          >
          <el-button
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            >修改</el-button
          >
          <!-- <el-button type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button> -->
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
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form label-width="120px" ref="form" :model="form" :rules="rules">
        <!-- <el-alert
          title="提示：24小时内可撤回公告，进行修改或删除，超过24小时后厂商可查看，不可撤回。"
          type="error"
          style="margin-bottom: 20px"
        >
        </el-alert> -->
        <el-form-item label="公告名称" prop="noticeName">
          <el-input v-model="form.noticeName" placeholder="请输入公告名称" />
        </el-form-item>
        <el-form-item label="采购公告" prop="noticeFile">
          <file-upload :fileType="doxcFileTypes" v-model="form.noticeFile" />
          <el-button type="text" @click="handleDownloadNoticeFile"
            >下载采购公告模板</el-button
          >
        </el-form-item>

        <el-form-item label="其他文件" prop="otherFile">
          <file-upload :fileType="doxcFileTypes" v-model="form.otherFile" />
          <el-alert
            title="(非必填)
注：如需上传除采购公告文件与样衣图外的其他材料备案，如：会议现场照片、项目公示照片、其他补充文件等信息可上传在此处"
            type="warning"
          >
          </el-alert>
        </el-form-item>
        <el-form-item label="提供样衣图" prop="isSamplesPic">
          <el-radio-group
            v-model="form.isSamplesPic"
            @change="handleSamplesPicChange"
          >
            <el-radio :label="0">不需要提供样衣图</el-radio>
            <el-radio :label="1">提供样衣图</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          v-if="form.isSamplesPic == 1"
          label="样衣图附件"
          prop="samplesPic"
        >
          <div style="margin-bottom: 10px">支持扩展名：.png .jpg .jpeg</div>
          <el-upload
            :limit="10"
            :headers="upload.headers"
            accept=".png,.jpg,.jpeg"
            class="upload-demo"
            :action="upload.url"
            list-type="picture-card"
            :on-preview="handlePictureCardPreview"
            :on-success="handlePictureSuccess"
            :on-remove="handleRemove"
            :file-list="fileListPic"
          >
            <i class="el-icon-plus"></i>
          </el-upload>
          <el-dialog append-to-body :visible.sync="dialogVisibleImage">
            <img width="100%" :src="dialogImageUrl" alt="" />
          </el-dialog>
          <el-upload
            accept=".doc,.docx,.pdf,.xlsx,.xls,"
            style="margin-top: 20px"
            :headers="upload.headers"
            :on-success="handleUploadSuccess"
            :on-remove="handleRemoveFile"
            class="upload-demo"
            drag
            :action="upload.url"
            multiple
            :file-list="fileList"
          >
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
              支持doc、docx、pdf、xlsx、xls格式
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item label="公告发布范围">
          <el-checkbox v-model="form.isRelease" true-label="1" false-label="0"
            >是否发布到"采购公告"栏目公示</el-checkbox
          >
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm()">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 查看采购公告详情弹窗 -->
    <el-dialog
      title="查看采购公告"
      :visible.sync="viewDialogVisible"
      width="1000px"
      append-to-body
    >
      <div class="result-detail-container">
        <div class="result-header">
          <h3 class="header-title">{{ viewForm.noticeName }}</h3>
          <div class="header-meta">
            <span><i class="el-icon-time"></i> {{ viewForm.createTime }}</span>
            <span><i class="el-icon-school"></i> {{ viewForm.deptName }}</span>
          </div>
        </div>

        <!-- 公告文件卡片 -->
        <div v-if="viewForm.noticeFile" class="detail-card">
          <div class="card-header">
            <i class="el-icon-document"></i>
            <span>采购公告文件</span>
            <div class="header-actions">
              <el-button
                size="mini"
                type="primary"
                icon="el-icon-download"
                @click="downloadFile(viewForm.noticeFile)"
                >下载</el-button
              >
              <el-button size="mini" icon="el-icon-printer" @click="printFile"
                >打印</el-button
              >
            </div>
          </div>
          <div class="card-body">
            <div class="pdf-viewer">
              <div class="pdf-toolbar">
                <el-button-group>
                  <el-button
                    size="mini"
                    icon="el-icon-minus"
                    @click="zoomOut"
                  ></el-button>
                  <el-button size="mini">{{ zoomLevel }}%</el-button>
                  <el-button
                    size="mini"
                    icon="el-icon-plus"
                    @click="zoomIn"
                  ></el-button>
                </el-button-group>
                <div class="pdf-page-info">
                  <span>{{ currentPage }} / {{ totalPages }}</span>
                </div>
              </div>
              <div
                class="pdf-content"
                :style="{ transform: `scale(${zoomLevel / 100})` }"
              >
                <img
                  v-if="isFileImage"
                  :src="getFileUrl(viewForm.noticeFile)"
                  alt="公告文件"
                />
                <iframe
                  v-else
                  :src="getFileViewerUrl(viewForm.noticeFile)"
                  frameborder="0"
                ></iframe>
              </div>
            </div>
          </div>
        </div>

        <!-- 样衣图附件卡片 -->
        <div
          v-if="viewForm.isSamplesPic == 1 && viewForm.samplesPicUrls"
          class="detail-card"
        >
          <div class="card-header">
            <i class="el-icon-picture-outline"></i>
            <span>样衣图附件</span>
          </div>
          <div class="card-body">
            <div class="file-grid">
              <div
                v-for="(file, index) in sampleFiles"
                :key="index"
                class="file-item"
              >
                <div class="file-icon">
                  <i :class="getFileIconClass(file)"></i>
                </div>
                <div class="file-info">
                  <div class="file-name">{{ getFileName(file) }}</div>
                  <el-button
                    type="text"
                    icon="el-icon-view"
                    @click="previewFile(file)"
                    >预览</el-button
                  >
                  <el-button
                    type="text"
                    icon="el-icon-download"
                    @click="downloadFile(file)"
                    >下载</el-button
                  >
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 其他文件卡片 -->
        <!-- <div v-if="viewForm.otherFile" class="detail-card">
          <div class="card-header">
            <i class="el-icon-folder-opened"></i>
            <span>其他文件</span>
          </div>
          <div class="card-body">
            <div class="file-grid">
              <div v-for="(file, index) in otherFiles" :key="index" class="file-item">
                <div class="file-icon">
                  <i :class="getFileIconClass(file)"></i>
                </div>
                <div class="file-info">
                  <div class="file-name">{{ getFileName(file) }}</div>
                  <el-button type="text" icon="el-icon-view" @click="previewFile(file)">预览</el-button>
                  <el-button type="text" icon="el-icon-download" @click="downloadFile(file)">下载</el-button>
                </div>
              </div>
            </div>
          </div>
        </div> -->
      </div>
    </el-dialog>

    <!-- 文件预览弹窗 -->
    <el-dialog
      title="文件预览"
      :visible.sync="previewDialogVisible"
      width="80%"
      append-to-body
    >
      <div class="file-preview-container">
        <img
          v-if="isPreviewImage"
          :src="previewUrl"
          alt="文件预览"
          style="max-width: 100%"
        />
        <iframe
          v-else
          :src="previewUrl"
          frameborder="0"
          style="width: 100%; height: 600px"
        ></iframe>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listMode, getMode, delMode, addMode, updateMode } from "@/api/cg/mode";
import { getToken } from "@/utils/auth";
import axios from "axios";
export default {
  name: "Mode",
  data() {
    return {
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
          {
            validator: (rule, value, callback) => {
              // 只有当选择了提供样衣图时才进行校验
              if (this.form.isSamplesPic == 1) {
                // 判断是否已上传图片或文件
                if (
                  this.fileListPic.length === 0 &&
                  this.fileList.length === 0
                ) {
                  callback(new Error("样衣图附件不能为空"));
                } else {
                  callback();
                }
              } else {
                callback();
              }
            },
            trigger: "change",
          },
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
    handleView(row) {
      console.log("查看数据:", row);
      // 获取最新数据
      getMode(row.id)
        .then((response) => {
          if (response.code === 200) {
            this.viewForm = response.data;

            // 处理样衣图附件
            if (
              this.viewForm.isSamplesPic == 1 &&
              this.viewForm.samplesPicUrls
            ) {
              const urls = this.viewForm.samplesPicUrls.split(",");
              const names = this.viewForm.samplesPicNames
                ? this.viewForm.samplesPicNames.split(",")
                : urls.map((url) => url.split("/").pop());

              this.sampleFiles = [];
              for (let i = 0; i < urls.length; i++) {
                if (!urls[i]) continue;

                const name = names[i] || `文件${i + 1}`;
                let url = urls[i];

                // 确保URL包含完整路径
                if (
                  url &&
                  !url.startsWith("http") &&
                  !url.startsWith("https") &&
                  !url.startsWith("/")
                ) {
                  url = "/" + url;
                }

                if (
                  url &&
                  !url.startsWith("http") &&
                  !url.startsWith("https")
                ) {
                  url = process.env.VUE_APP_IMAGE_BASE_API + url;
                }

                this.sampleFiles.push({
                  name: name,
                  url: url,
                  originalUrl: urls[i],
                });
              }
            } else {
              this.sampleFiles = [];
            }

            // 处理其他文件
            if (this.viewForm.otherFile) {
              const otherFile = this.viewForm.otherFile;
              if (typeof otherFile === "string") {
                const url = otherFile;
                const name = url.split("/").pop();

                let fullUrl = url;
                if (
                  url &&
                  !url.startsWith("http") &&
                  !url.startsWith("https") &&
                  !url.startsWith("/")
                ) {
                  fullUrl = "/" + url;
                }

                if (
                  url &&
                  !url.startsWith("http") &&
                  !url.startsWith("https")
                ) {
                  fullUrl = process.env.VUE_APP_IMAGE_BASE_API + fullUrl;
                }

                this.otherFiles = [
                  {
                    name: name,
                    url: fullUrl,
                    originalUrl: url,
                  },
                ];
              } else {
                this.otherFiles = [];
              }
            } else {
              this.otherFiles = [];
            }

            // 检查采购公告文件类型
            if (this.viewForm.noticeFile) {
              this.isFileImage = this.isImageFile(this.viewForm.noticeFile);
            }

            // 打开弹窗
            this.viewDialogVisible = true;
          } else {
            this.$modal.msgError("获取详情失败");
          }
        })
        .catch((error) => {
          console.error("获取详情失败:", error);
          this.$modal.msgError("获取详情失败");
        });
    },

    // 判断是否为图片文件
    isImageFile(fileName) {
      if (!fileName) return false;
      const ext = fileName.toLowerCase().split(".").pop();
      return ["jpg", "jpeg", "png", "gif", "bmp"].includes(ext);
    },

    // 获取文件图标样式
    getFileIconClass(file) {
      if (!file || !file.name) return "el-icon-document";

      const ext = file.name.toLowerCase().split(".").pop();
      if (["jpg", "jpeg", "png", "gif", "bmp"].includes(ext)) {
        return "el-icon-picture-outline";
      } else if (["pdf"].includes(ext)) {
        return "el-icon-document-checked";
      } else if (["doc", "docx"].includes(ext)) {
        return "el-icon-document";
      } else if (["xls", "xlsx"].includes(ext)) {
        return "el-icon-document";
      } else {
        return "el-icon-document";
      }
    },

    // 获取文件名
    getFileName(file) {
      if (!file) return "";
      if (typeof file === "string") {
        return file.split("/").pop();
      }
      return file.name || "";
    },

    // 文件预览
    previewFile(file) {
      if (!file) return;

      let previewUrl = "";
      if (typeof file === "string") {
        previewUrl = this.getFileViewerUrl(file);
      } else {
        previewUrl = this.getFileViewerUrl(file.url);
      }

      // 图片直接预览，其他文件使用在线预览
      const fileName = typeof file === "string" ? file : file.name;
      this.isPreviewImage = this.isImageFile(fileName);

      this.previewUrl = previewUrl;
      this.previewDialogVisible = true;
    },

    // 获取文件预览URL
    getFileViewerUrl(fileUrl) {
      if (!fileUrl) return "";

      // 图片直接返回URL
      if (this.isImageFile(fileUrl)) {
        if (fileUrl.startsWith("http") || fileUrl.startsWith("https")) {
          return fileUrl;
        } else {
          return (
            process.env.VUE_APP_IMAGE_BASE_API +
            (fileUrl.startsWith("/") ? fileUrl : "/" + fileUrl)
          );
        }
      }

      // 其他文件使用在线预览
      const baseUrl = process.env.VUE_APP_BASE_API;
      const officeUrl =
        process.env.VUE_APP_KKFILE2 || "/kkFileView/onlinePreview";

      // 对于非图片文件，使用在线预览服务
      let fullUrl = fileUrl;
      if (!fileUrl.startsWith("http") && !fileUrl.startsWith("https")) {
        fullUrl = baseUrl + (fileUrl.startsWith("/") ? fileUrl : "/" + fileUrl);
      }

      // 编码文件路径
      const encodedUrl = window.btoa(fullUrl);
      return `${officeUrl}?url=${encodedUrl}`;
    },

    // 获取文件URL
    getFileUrl(fileUrl) {
      if (!fileUrl) return "";
      if (fileUrl.startsWith("http") || fileUrl.startsWith("https")) {
        return fileUrl;
      } else {
        return (
          process.env.VUE_APP_IMAGE_BASE_API +
          (fileUrl.startsWith("/") ? fileUrl : "/" + fileUrl)
        );
      }
    },

    // 下载文件
    downloadFile(file) {
      if (!file) return;

      let url = "";
      let fileName = "";

      if (typeof file === "string") {
        url = file;
        fileName = file.split("/").pop();
      } else {
        url = file.url || file.originalUrl;
        fileName = file.name || url.split("/").pop();
      }

      // 构建完整URL
      let fullUrl = url;
      if (!url.startsWith("http") && !url.startsWith("https")) {
        fullUrl =
          process.env.VUE_APP_IMAGE_BASE_API +
          (url.startsWith("/") ? url : "/" + url);
      }

      // 创建a标签进行下载
      const link = document.createElement("a");
      link.href = fullUrl;
      link.setAttribute("download", fileName);
      link.setAttribute("target", "_blank");
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },

    // 打印文件
    printFile() {
      if (!this.viewForm.noticeFile) return;

      // 如果是图片，创建新窗口打印
      if (this.isFileImage) {
        const imgUrl = this.getFileUrl(this.viewForm.noticeFile);
        const printWindow = window.open("", "_blank");
        printWindow.document.write(`
          <html>
            <head>
              <title>打印预览</title>
              <style>
                body {
                  margin: 0;
                  padding: 0;
                  display: flex;
                  justify-content: center;
                  align-items: center;
                }
                img {
                  max-width: 100%;
                  height: auto;
                }
              </style>
            </head>
            <body>
              <img src="${imgUrl}" />
            </body>
          </html>
        `);
        printWindow.document.close();
        printWindow.onload = function () {
          printWindow.focus();
          printWindow.print();
        };
      } else {
        // 对于非图片文件，打开新窗口
        const fileUrl = this.getFileViewerUrl(this.viewForm.noticeFile);
        const printWindow = window.open(fileUrl, "_blank");
        printWindow.onload = function () {
          setTimeout(function () {
            printWindow.focus();
            printWindow.print();
          }, 1000);
        };
      }
    },

    // 放大文件
    zoomIn() {
      this.zoomLevel = Math.min(200, this.zoomLevel + 10);
    },

    // 缩小文件
    zoomOut() {
      this.zoomLevel = Math.max(50, this.zoomLevel - 10);
    },
    //发布
    handleRelease(row) {
      this.$modal
        .confirm("是否确认发布？")
        .then(() => {
          const loading = this.$loading({
            lock: true,
            text: "正在发布...",
            spinner: "el-icon-loading",
            background: "rgba(0, 0, 0, 0.7)",
          });

          return updateMode({
            id: row.id,
            isRelease: 1,
          })
            .then((response) => {
              loading.close();
              if (response.code === 200) {
                this.getList();
                this.$modal.msgSuccess("已发布");
              } else {
                this.$modal.msgError(response.msg || "发布失败");
              }
            })
            .catch((error) => {
              loading.close();
              this.$modal.msgError("发布失败，请稍后重试");
              console.error("发布错误:", error);
            });
        })
        .catch(() => {
          // 用户取消操作，不做处理
        });
    },
    //撤回
    handleWithdraw(row) {
      this.$modal
        .confirm("是否确认撤回？")
        .then(() => {
          const loading = this.$loading({
            lock: true,
            text: "正在撤回...",
            spinner: "el-icon-loading",
            background: "rgba(0, 0, 0, 0.7)",
          });

          return updateMode({
            id: row.id,
            isRelease: 0,
          })
            .then((response) => {
              loading.close();
              if (response.code === 200) {
                this.getList();
                this.$modal.msgSuccess("已撤回");
              } else {
                this.$modal.msgError(response.msg || "撤回失败");
              }
            })
            .catch((error) => {
              loading.close();
              this.$modal.msgError("撤回失败，请稍后重试");
              console.error("撤回错误:", error);
            });
        })
        .catch(() => {
          // 用户取消操作，不做处理
        });
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
        isRelease: 0,
      };
      this.resetForm("form");
      this.daterange = [];
      this.fileListPic = [];
      this.fileList = [];
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
      this.title = "发布选购公告信息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getMode(id).then((response) => {
        this.form = response.data;
        // 确保isSamplesPic是数字类型
        if (
          this.form.isSamplesPic !== null &&
          this.form.isSamplesPic !== undefined
        ) {
          this.form.isSamplesPic = Number(this.form.isSamplesPic);
        }
        console.log("修改数据返回:", this.form); // 调试信息
        this.open = true;
        this.title = "修改选用组织委托招标机构确定企业模式";

        // 清空文件列表
        this.fileListPic = [];
        this.fileList = [];

        // 检查样衣图附件字段，可能的名称为samplesPic、samplesPicUrls或数据库实际字段名
        let attachmentUrls = "";
        let attachmentNames = "";

        // 尝试不同可能的字段名
        if (this.form.isSamplesPic == 1) {
          if (this.form.samplesPicUrls && this.form.samplesPicNames) {
            attachmentUrls = this.form.samplesPicUrls;
            attachmentNames = this.form.samplesPicNames;
          } else if (this.form.samplesPic) {
            // 可能样衣图信息存储在samplesPic字段中
            attachmentUrls = this.form.samplesPic;
            attachmentNames = this.form.samplesPic; // 名称可能需要从URL中提取
          } else {
            // 尝试查找其他可能的字段
            console.log("正在查找可能的样衣图字段:");
            for (const key in this.form) {
              if (
                key.toLowerCase().includes("sample") ||
                key.toLowerCase().includes("pic") ||
                key.toLowerCase().includes("image") ||
                key.toLowerCase().includes("file")
              ) {
                console.log(`可能的样衣图字段: ${key} = ${this.form[key]}`);
              }
            }
          }

          console.log("样衣图URLs:", attachmentUrls);
          console.log("样衣图Names:", attachmentNames);

          // 如果有附件信息，解析并显示
          if (attachmentUrls) {
            const urls = attachmentUrls.split(",");
            // 如果没有名称信息，从URL中提取
            const names = attachmentNames
              ? attachmentNames.split(",")
              : urls.map((url) => url.split("/").pop());

            // 确保两个数组长度一致
            const minLength = Math.min(urls.length, names.length);

            console.log("解析的URLs:", urls);
            console.log("解析的Names:", names);

            for (let i = 0; i < minLength; i++) {
              if (!urls[i]) continue;

              const name = names[i] || `文件${i + 1}`;
              let url = urls[i];

              // 确保URL包含完整路径
              if (
                url &&
                !url.startsWith("http") &&
                !url.startsWith("https") &&
                !url.startsWith("/")
              ) {
                // 如果不是完整URL，添加斜杠
                url = "/" + url;
              }

              if (url && !url.startsWith("http") && !url.startsWith("https")) {
                // 如果不是完整URL，添加基础路径
                url = process.env.VUE_APP_IMAGE_BASE_API + url;
              }

              console.log(`处理文件 ${i}: ${name}, URL: ${url}`);

              // 根据文件扩展名判断是图片还是文档
              const isImage = /\.(jpg|jpeg|png|gif)$/i.test(name.toLowerCase());

              // 创建文件对象，保留原始URL以便上传
              const fileItem = {
                name: name,
                url: url,
                originUrl: urls[i], // 保存原始URL，用于表单提交
              };

              console.log("添加文件项:", fileItem);

              // 根据文件类型添加到不同列表
              if (isImage) {
                this.fileListPic.push(fileItem);
              } else {
                this.fileList.push(fileItem);
              }
            }

            console.log("处理后的图片列表:", this.fileListPic);
            console.log("处理后的文件列表:", this.fileList);
          }
        }
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          // 处理样衣图附件，收集图片和文件的URL和名称
          if (this.form.isSamplesPic == 1) {
            // 收集图片文件URL和名称
            const imageUrls = this.fileListPic
              .map((item) => {
                // 优先使用response中的url
                if (
                  item.response &&
                  item.response.data &&
                  item.response.data.url
                ) {
                  return item.response.data.url;
                }
                // 然后尝试使用保存的原始URL
                if (item.originUrl) {
                  return item.originUrl;
                }
                // 如果都没有，使用显示用的url
                return item.url
                  ? item.url.replace(process.env.VUE_APP_BASE_API, "")
                  : "";
              })
              .filter((url) => url); // 过滤掉空值

            const imageNames = this.fileListPic
              .map((item) => {
                if (
                  item.response &&
                  item.response.data &&
                  item.response.data.name
                ) {
                  return item.response.data.name;
                }
                return item.name || "";
              })
              .filter((name) => name); // 过滤掉空值

            // 收集普通文件URL和名称，逻辑同上
            const fileUrls = this.fileList
              .map((item) => {
                if (
                  item.response &&
                  item.response.data &&
                  item.response.data.url
                ) {
                  return item.response.data.url;
                }
                if (item.originUrl) {
                  return item.originUrl;
                }
                return item.url
                  ? item.url.replace(process.env.VUE_APP_BASE_API, "")
                  : "";
              })
              .filter((url) => url);

            const fileNames = this.fileList
              .map((item) => {
                if (
                  item.response &&
                  item.response.data &&
                  item.response.data.name
                ) {
                  return item.response.data.name;
                }
                return item.name || "";
              })
              .filter((name) => name);

            // 组合所有文件URL和名称，使用逗号分隔
            this.form.samplesPicUrls = [...imageUrls, ...fileUrls].join(",");
            this.form.samplesPicNames = [...imageNames, ...fileNames].join(",");

            console.log("提交的样衣图URLs:", this.form.samplesPicUrls);
            console.log("提交的样衣图Names:", this.form.samplesPicNames);

            // 确保form.samplesPic不为空，可以设置为任意非空值
            if (imageUrls.length > 0 || fileUrls.length > 0) {
              this.form.samplesPic = this.form.samplesPicUrls;
            }
          } else {
            // 如果不提供样衣图，清空相关字段
            this.form.samplesPicUrls = "";
            this.form.samplesPicNames = "";
            this.form.samplesPic = null;
          }

          // 显示加载提示
          const loading = this.$loading({
            lock: true,
            text: this.form.id ? "正在保存修改..." : "正在添加...",
            spinner: "el-icon-loading",
            background: "rgba(0, 0, 0, 0.7)",
          });

          // 创建表单数据的副本，防止修改原始表单
          const formData = { ...this.form };

          if (formData.id != null) {
            updateMode(formData)
              .then((response) => {
                loading.close();
                if (response.code === 200) {
                  this.$modal.msgSuccess("修改成功");
                  this.open = false;
                  this.getList();
                } else {
                  this.$modal.msgError(response.msg || "修改失败");
                }
              })
              .catch((error) => {
                loading.close();
                this.$modal.msgError("系统错误，请稍后重试");
                console.error("修改错误:", error);
              });
          } else {
            addMode(formData)
              .then((response) => {
                loading.close();
                if (response.code === 200) {
                  this.$modal.msgSuccess("新增成功");
                  this.open = false;
                  this.getList();
                } else {
                  this.$modal.msgError(response.msg || "新增失败");
                }
              })
              .catch((error) => {
                loading.close();
                this.$modal.msgError("系统错误，请稍后重试");
                console.error("新增错误:", error);
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
          '是否确认删除选用组织委托招标机构确定企业模式编号为"' +
            ids +
            '"的数据项？'
        )
        .then(function () {
          return delMode(ids);
        })
        .then(() => {
          this.getList();
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
    handlePictureCardPreview(file) {
      // 尝试获取不同可能的URL格式
      this.dialogImageUrl =
        file.url || (file.response ? file.response.data.url : "");
      console.log("预览图片URL:", this.dialogImageUrl);
      this.dialogVisibleImage = true;
    },
    handlePictureSuccess(response, file, fileList) {
      this.fileListPic = fileList;
      this.$modal.msgSuccess("图片上传成功");
      // 触发表单校验
      this.$nextTick(() => {
        this.$refs.form.validateField("samplesPic");
      });
    },
    handleRemove(file, fileList) {
      this.fileListPic = fileList;
      // 触发表单校验
      this.$nextTick(() => {
        this.$refs.form.validateField("samplesPic");
      });
    },
    handleUploadSuccess(response, file, fileList) {
      this.fileList = fileList;
      this.$modal.msgSuccess("文件上传成功");
      // 触发表单校验
      this.$nextTick(() => {
        this.$refs.form.validateField("samplesPic");
      });
    },
    handleRemoveFile(file, fileList) {
      this.fileList = fileList;
      // 触发表单校验
      this.$nextTick(() => {
        this.$refs.form.validateField("samplesPic");
      });
    },
    handleSamplesPicChange(value) {
      if (value === 0) {
        // 清空相关字段
        this.fileListPic = [];
        this.fileList = [];
        this.form.samplesPic = null;
      } else {
        // 选择提供样衣图时，延迟校验以便用户上传
        this.$nextTick(() => {
          this.$refs.form.validateField("samplesPic");
        });
      }
    },
  },
};
</script>

<style scoped>
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
