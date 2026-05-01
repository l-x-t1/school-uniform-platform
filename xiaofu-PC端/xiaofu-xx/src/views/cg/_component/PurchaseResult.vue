<template>
  <div class="detail-panel">
    <div class="header" style="display: flex; align-items: center">
      <h3>选用组织选购结果公示报备与公开</h3>
      <div style="margin-left: 20px">
        <el-button
          type="primary"
          icon="el-icon-plus"
          v-hasPermi="['xfpt:xgjggk-add']"
          @click="handleAdd"
          v-if="resultsList.length == 0"
          >发布选购结果</el-button
        >
      </div>
    </div>

    <el-table
      v-loading="loading"
      :data="resultsList"
    >
      <el-table-column label="序号" align="center" type="index" />
      <el-table-column label="公示标题" align="center" prop="noticeTitle" />
      <el-table-column label="选购模式" align="center" prop="reportMode">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.cgmslx" :value="scope.row.reportMode" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime">
        <template slot-scope="scope">
          {{ parseTime(scope.row.createTime, "{y}-{m}-{d}") }}
        </template>
      </el-table-column>
      <el-table-column label="发布状态" align="center" prop="isRelease">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.notice_status"
            :value="scope.row.isRelease"
          />
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            >查看</el-button
          >
          <el-button
            v-if="scope.row.isRelease == 0"
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            >修改</el-button
          >
          <!-- <el-button
            v-if="scope.row.isRelease === '1'"
            size="mini"
            type="text"
            icon="el-icon-bottom"
            @click="handleCH(scope.row)"
            v-hasPermi="['xfpt:xgjggk-ch']"
            >补充公告</el-button
          > -->
          <el-button
            v-if="scope.row.isRelease === '0'"
            size="mini"
            type="text"
            icon="el-icon-top"
            @click="handleFB(scope.row)"
            v-hasPermi="['xfpt:xgjggk-fb']"
            >发布</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['xfpt:xgjggk-remove']"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table> 

    <!-- 添加或修改选用组织选购结果公示报备与公开对话框 -->
    <el-dialog
      :title="title"
      :visible.sync="open"
      width="1200px"
      append-to-body
    >
      <el-form ref="form" :model="form" :rules="rules" label-width="140px">
        <el-form-item label="成交公告名称" prop="noticeTitle">
          <el-input
            v-model="form.noticeTitle"
            placeholder="xx市xx区xx学校20xx-20xx学年学校服务类采购成交公告"
            :readonly="!xgType"
          />
        </el-form-item>

        <el-form-item label="成交公告" prop="noticeFile">
          <file-upload
            :fileType="['pdf']"
            v-model="form.noticeFile"
            :disabled="!xgType"
            :gaizhang="true"
          >
            <el-button slot="template" type="text" @click.stop="importTemplate"
              >下载模板</el-button
            ></file-upload
          >
          <span v-if="!form.noticeFile && !xgType">
            未上传
          </span>
        </el-form-item>
        <el-form-item label=" 成交企业名称" prop="transactionCompanyName">
          <el-input
            v-model="form.transactionCompanyName"
            placeholder="请输入企业名称或组织名称" 
            :readonly="!xgType"
          />
        </el-form-item>


        <el-form-item label="采购日期" prop="cgsj">
          <el-date-picker
            style="width: 100%"
            v-model="form.cgsj"
            type="date"
            placeholder="选择日期"
            value-format="yyyy-MM-dd"
            :readonly="!xgType"
          />
        </el-form-item>
        <el-form-item label="采购年级（级）">
          <el-table border style="margin-top: 10px" :data="form.sonDataList">
            <el-table-column label="采购年级（级）" prop="content">
              <template slot="header">
                <span class="required-field">采购年级（级）</span>
              </template>
              <template slot-scope="scope">
                <el-form
                  :model="scope.row"
                  :ref="'cgnjForm' + scope.$index"
                  label-width="0px"
                >
                  <el-form-item
                    :rules="[
                      {
                        required: true,
                        message: '采购年级（级）不能为空',
                        trigger: 'blur',
                      },
                    ]"
                    prop="cgnj"
                  >
                    <el-select
                      disabled
                      @change="handleChangeCgnj(scope.row, scope.$index)"
                      v-model="scope.row.cgnj"
                      placeholder="请选择采购年级（级）" 
                    >
                      <el-option
                        v-for="item in teamXGCQ"
                        :key="item.id"
                        :label="item.gradeClass"
                        :value="item.id"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </el-form>
              </template>
            </el-table-column>
            <el-table-column label="校服品类" prop="content">
              <template slot="header">
                <span class="required-field">校服品类</span>
              </template>
              <template slot-scope="scope">
                <el-form
                  :model="scope.row"
                  :ref="'xfplForm' + scope.$index"
                  label-width="0px"
                >
                  <el-form-item
                    prop="xfpl"
                    :rules="[
                      {
                        required: true,
                        message: '校服品类不能为空',
                        trigger: 'blur',
                      },
                    ]"
                  >
                    <el-select
                      disabled
                      placeholder="请选择校服品类"
                      v-model="scope.row.xfpl"
                      clearable 
                    >
                      <el-option
                        v-for="item in dict.type.xfpl"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </el-form>
              </template>
            </el-table-column>
            <el-table-column label="每个学生购买套数" prop="content">
              <template slot="header">
                <span class="required-field">每个学生购买套数</span>
              </template>
              <template slot-scope="scope">
                <el-form
                  :model="scope.row"
                  :ref="'gmtsForm' + scope.$index"
                  label-width="0px"
                >
                  <el-form-item
                    :rules="[
                      {
                        required: true,
                        message: '每个学生购买套数不能为空',
                        trigger: 'blur',
                      },
                    ]"
                    prop="xsngmts"
                  >
                    <el-input
                      readonly 
                      placeholder="请输入学生购买套数"
                      v-model="scope.row.xsngmts" 
                    ></el-input>
                  </el-form-item>
                </el-form>
              </template>
            </el-table-column>
            <el-table-column label="每套价格区间（拟）" prop="content">
              <template slot="header">
                <span class="required-field">每套价格区间（元）</span>
              </template>
              <template slot-scope="scope">
                <el-form
                  :model="scope.row"
                  :ref="'jgqjForm' + scope.$index"
                  label-width="0px"
                >
                  <el-form-item
                    :rules="[
                      {
                        required: true,
                        message: '每套价格区间不能为空',
                        trigger: 'blur',
                      },
                    ]"
                    prop="jgqj"
                  >
                    <el-input
                      readonly 
                      placeholder="请输入每套价格区间"
                      v-model="scope.row.jgqj" 
                    ></el-input>
                  </el-form-item>
                </el-form>
              </template>
            </el-table-column>
            <el-table-column label="备注" prop="content">
              <template slot-scope="scope">
                <el-input
                  readonly
                  type="textarea"
                  placeholder="请填写备注"
                  v-model="scope.row.bz" 
                ></el-input>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>

        <el-form-item label="其他档案文件" prop="otherFile">
          <file-upload v-model="form.otherFile" :disabled="!xgType" />
          <div class="upload-tip" v-if="xgType">
            注：如需上传除成交公告公示文件外的其他补充材料等，如：会议现场照片、项目公示图片、说明类文件等可上传至此处
          </div>
          <span v-if="!form.otherFile">
            未上传
          </span>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer" v-if="xgType">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>



    <!-- 查看选购结果详情对话框 -->
    <el-dialog
      title="详情"
      :visible.sync="viewOpen"
      width="1000px"
      append-to-body
    >
      <div class="result-detail-container">
        <div class="result-header">
          <h3 class="header-title">{{ viewForm.noticeTitle }}</h3>
          <div class="header-meta">
            <span
              ><i class="el-icon-time"></i>
              {{ parseTime(viewForm.createTime) }}</span
            >
            <span>{{ viewForm.deptName }}</span>
          </div>
        </div>

        <!-- 成交信息卡片 -->
        <!-- <div class="detail-card">
          <div class="card-header">
            <i class="el-icon-s-cooperation"></i>
            <span>成交企业信息</span>
          </div>
          <div class="card-body">
            <div class="info-row">
              <div class="info-item">
                <div class="info-label">企业名称</div>
                <div class="info-value">
                  {{ viewForm.transactionCompanyName }}
                </div>
              </div>
              <div class="info-item">
                <div class="info-label">成交价格</div>
                <div class="info-value highlight">
                  {{ viewForm.bidPrice }} 元/人
                </div>
              </div>

            </div>

          </div> -->
        <!-- </div> -->

        <!-- PDF预览卡片 -->
        <div v-if="viewForm.announcementFile" class="detail-card">
          <div class="card-header">
            <i class="el-icon-document"></i>
            <span>公告文件</span>

          </div>
          <div class="card-body">
            <div class="pdf-viewer">

              <div :style="{ transform: `scale(${zoomLevel / 100})` }">
                <img
                  v-if="isPdfImage"
                  :src="getFileUrl(viewForm.announcementFile)"
                  alt="公告文件"
                />
                <div class="card-body">
                  <div class="file-grid">
                    <div class="file-item">
                      <div class="file-icon">
                        <i class="el-icon-document"></i>
                      </div>
                      <div class="file-info">
                        <div class="file-name">
                          {{ getFileName(viewForm.announcementFile) }}
                        </div>
                        <el-button
                          type="text"
                          icon="el-icon-download"
                          @click="downloadFile(viewForm.announcementFile)"
                          >下载</el-button
                        >
                        <el-button
                          type="text"
                          icon="el-icon-download"
                          @click="previewFile(viewForm.announcementFile)"
                          >预览</el-button
                        >
                      </div>
                    </div>
                  </div>
                </div>

              </div>
            </div>
          </div>
        </div>

        <!-- 采购年级信息卡片 -->
        <div
          v-if="viewForm.sonDataList && viewForm.sonDataList.length > 0"
          class="detail-card"
        >
          <div class="card-header">
            <i class="el-icon-s-order"></i>
            <span>采购年级信息</span>
          </div>
          <div class="card-body">
            <el-table border :data="viewForm.sonDataList">
              <el-table-column label="采购年级（级）" prop="cgnj" />
              <el-table-column label="校服品类" prop="xfpl">
                <template slot-scope="scope">
                  <dict-tag :options="dict.type.xfpl" :value="scope.row.xfpl" />
                </template>
              </el-table-column>
              <el-table-column label="每个学生购买套数" prop="xsngmts" />
              <el-table-column label="每套价格区间（拟）" prop="jgqj" />
              <el-table-column label="备注" prop="bz" />
            </el-table>
          </div>
        </div>

        <!-- 其他文件卡片 -->
        <div
          v-if="viewForm.otherFile && otherFiles.length > 0"
          class="detail-card"
        >
          <div class="card-header">
            <i class="el-icon-folder-opened"></i>
            <span>其他相关文件</span>
          </div>
          <div class="card-body">
            <div class="file-grid">
              <div
                v-for="(file, index) in otherFiles"
                :key="index"
                class="file-item"
              >
                <div class="file-icon">
                  <i class="el-icon-document"></i>
                </div>
                <div class="file-info">
                  <div class="file-name">{{ getFileName(file) }}</div>
                  <el-button
                    type="text"
                    icon="el-icon-download"
                    @click="downloadFile(file)"
                    >下载</el-button
                  >
                  <el-button
                    type="text"
                    icon="el-icon-download"
                    @click="previewFile(file)"
                    >预览</el-button
                  >
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="viewOpen = false">关 闭</el-button>
      </div>
    </el-dialog>


  </div>
</template>

<script>
import {
  listResults,
  getResults,
  delResults,
  addResults,
  updateResults,
} from "@/api/school/results";
import { getTeamXGCQ, getCurrSchoolGradeList } from "@/api/cg/index";
import { getInfo } from "@/api/login";
import { Base64 } from "js-base64";
import { getToken } from "@/utils/auth";
import axios from "axios";
export default {
  name: "Results",
  dicts: ["cgmslx", "notice_status", "xfpl"],
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

      // 总条数
      total: 0,
      // 选用组织选购结果公示报备与公开表格数据
      resultsList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        teamId: null,
      },
      teamXGCQ: [],
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        noticeFile: [
          {
            required: true,
            message: "请上传成交公告文件",
            trigger: "change",
          },
        ],
        cgsj: [
          { required: true, message: "采购日期不能为空", trigger: "blur" },
        ],
        noticeTitle: [
          { required: true, message: "成交公告名称不能为空", trigger: "blur" },
        ],

        transactionCompanyName: [
          { required: true, message: "成交企业名称不能为空", trigger: "blur" },
        ],




      },

      // 查看详情
      viewOpen: false,
      viewForm: {},
      activeTab: "detail",
      zoomLevel: 100,

      isPdfImage: false,
      otherFiles: [],
      cgnj: null,
      formIdList: [],
      xgType: false,
    };
  },
  created() {

    this.form.teamId = this.formId;
    this.getList();
    this.getTeamXGCQData();
    getTeamXGCQ(this.formId).then((response) => {
      this.cgnj = response.data[0].cgnj;
      this.formIdList = response.data;
    });
  },
  methods: {
    importTemplate() {
      const link = document.createElement("a");
      link.href =
        process.env.VUE_APP_IMAGE_BASE_API +
        "/filestatics/download/采购结果公开.docx";
      link.download = "采购结果公开.docx";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
    // 获取班子会议选购需求
    getTeamXGCQData() {
      getCurrSchoolGradeList().then((response) => {
        this.teamXGCQ = response.data;
      });
    },

    previewFile(filePath) {
      var b64Encoded = Base64.encode(
        process.env.VUE_APP_IMAGE_BASE_API + filePath
      );
      window.open(
        process.env.VUE_APP_KKFILE2 + encodeURIComponent(b64Encoded),
        "_blank"
      );
    },
    getInfo() {
      getInfo().then((response) => {
        this.form.deptId = res.user.dept.deptId;
      });
    },
    getList() {
      this.loading = true;
      listResults({ ...this.queryParams, teamId: this.formId }).then(
        (response) => {
          this.resultsList = response.rows;
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
        id: null,
        noticeTitle: null,
        transactionCompanyName: null,
        noticeFile: null,
        otherFile: null,
        cgsj: null,
        teamId: this.formId,
        sonDataList: [...this.formIdList],
      };
      this.resetForm("form");
    },


    handleAdd() {
      this.reset();
      this.open = true;
      this.xgType = true
      this.title = "发布选购结果";
      this.getInfo();
    },
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getResults(id).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = "修改选用组织选购结果公示报备与公开";
      });
    },
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          this.formValidate()
            .then((res) => {
              if (this.form.id != null) {
                updateResults(this.form).then((response) => {
                  this.$modal.msgSuccess("修改成功");
                  this.open = false;
                  this.getList();
                  this.$emit("handleGetProcessMenu",response.data);
                });
              } else {
                addResults(this.form).then((response) => {
                  this.$modal.msgSuccess("新增成功");
                  this.open = false;
                  this.getList();
                  this.$emit("handleGetProcessMenu",response.data);
                });
              }
            })
            .catch(() => {
              // 表单验证失败，不需要额外处理，错误信息已经在 formValidate 中显示
            });
        }
      });
    },
    handleDelete(row) {
      const ids = row.id;
      this.$modal
        .confirm("是否确认删除选中的据项？")
        .then(function () {
          return delResults(ids);
        })
        .then(() => {
          this.getList();
          this.$emit("handleGetProcessMenu");

          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => {});
    },

    /** 查看详情按钮操作 */
    handleView(row) {
      this.open = true;
      this.xgType = false
      getResults(row.id).then((response) => {
        this.form = response.data;
        // // 检查文件类型
        // this.form.noticeFile = this.checkIsImage(this.form.noticeFile);

        // // 处理其他文件，将字符串转为数组
        // if (this.form.otherFile) {
        //   this.form.otherFile = this.form.otherFile.split(",");
        // } else {
        //   this.form.otherFile = [];
        // }
      });
    },

    // /** 撤回操作 */
    // handleCH(row) {
    //   this.$message.info('补充公告功能暂未实现');
    // },

    /** 发布操作 */
    handleFB(row) {
      this.$modal
        .confirm("确定要发布该公告吗？")
        .then(() => {
          const updateData = {
            id: row.id,
            isRelease: "1",
          };
          updateResults(updateData).then(() => {
            this.$modal.msgSuccess("发布成功");
            this.getList();
          });
        })
        .catch(() => {});
    },

    // 文件操作相关方法
    getFileUrl(filePath) {
      if (!filePath) return "";
      // 检查是否已经是完整URL
      if (filePath.startsWith("http://") || filePath.startsWith("https://")) {
        return filePath;
      }
      return (
        process.env.VUE_APP_BASE_API +
        "/common/download?fileName=" +
        encodeURIComponent(filePath) +
        "&delete=false"
      );
    },

    downloadFile(filePath) {
      if (!filePath) return;

      const url = this.getFileUrl(filePath);
      // 创建一个临时的a元素用于下载
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", this.getFileName(filePath)); // 设置下载文件名
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },

    getFileName(filePath) {
      if (!filePath) return "";
      const parts = filePath.split("/");
      return parts[parts.length - 1];
    },

    checkIsImage(filePath) {
      if (!filePath) return false;
      const ext = filePath.toLowerCase().split(".").pop();
      return ["jpg", "jpeg", "png", "gif", "bmp"].includes(ext);
    },

    // PDF预览工具方法
    zoomIn() {
      if (this.zoomLevel < 150) {
        this.zoomLevel += 10;
      }
    },

    zoomOut() {
      if (this.zoomLevel > 30) {
        this.zoomLevel -= 10;
      }
    },

    printPdf() {
      if (!this.viewForm.announcementFile) return;

      // 获取iframe或img元素
      let contentElement;
      if (this.isPdfImage) {
        contentElement = document.querySelector(".pdf-content img");
      } else {
        contentElement = document.querySelector(".pdf-content iframe");
      }

      if (contentElement) {
        if (this.isPdfImage) {
          // 如果是图片，创建新窗口显示并打印
          const printWindow = window.open("", "_blank");
          printWindow.document.write(`
            <html>
              <head><title>打印预览</title></head>
              <body style="margin:0;padding:0;display:flex;justify-content:center;align-items:center;">
                <img src="${contentElement.src}" style="max-width:100%;" />
              </body>
            </html>
          `);
          printWindow.document.close();
          printWindow.onload = function () {
            printWindow.print();
          };
        } else {
          // 如果是PDF，使用iframe中的内容打印
          try {
            contentElement.contentWindow.print();
          } catch (e) {
            // 如果直接打印失败（可能是跨域问题），则打开新窗口
            const printWindow = window.open(
              this.getFileUrl(this.viewForm.announcementFile),
              "_blank"
            );
            printWindow.onload = function () {
              setTimeout(function () {
                printWindow.print();
              }, 1000); // 给页面足够的加载时间
            };
          }
        }
      }
    },





    handleChangeCgnj(row, index) {
      this.form.sonDataList[index].cgnj = this.teamXGCQ.find(
        (item) => item.id == row.cgnj
      ).gradeClass;
    },

    // 表单验证方法
    formValidate() {
      const validateFormRef = (ref) => {
        return new Promise((resolve) => {
          if (this.$refs[ref]) {
            this.$refs[ref].validate((vaild, message) => {
              const messageList = Object.values(message)
                .flat(2)
                .map((item) => item.message)
                .join(",");

              resolve({
                vaild,
                message: messageList,
              });
            });
          } else {
            resolve(true);
          }
        });
      };

      // 采购年级表格校验
      const validateConfigTemplate = () => {
        const promises = [];
        this.form.sonDataList.forEach((_, index) => {
          promises.push(validateFormRef("cgnjForm" + index));
          promises.push(validateFormRef("xfplForm" + index));
          promises.push(validateFormRef("gmtsForm" + index));
          promises.push(validateFormRef("jgqjForm" + index));
        });
        return Promise.all(promises);
      };

      return new Promise((resolve, reject) => {
        validateConfigTemplate().then((results) => {
          const allResults = results.flat(2);

          const isValid = allResults.every((result) => result.vaild === true);
          const errorMessage = allResults.find((mes) => mes.vaild == false);
          if (!isValid && errorMessage.message) {
            this.$message.error(errorMessage.message);
            reject(false);
          } else {
            resolve(true);
          }
        });
      });
    },

    /** 添加行操作 */
    handleAddRow() {
      this.form.sonDataList.push({
        cgnj: "",
        xfpl: "",
        xsngmts: "",
        jgqj: "",
        bz: "",
      });
    },

    /** 删除行操作 */
    handleDeleteRow(index, row) {
      this.form.sonDataList.splice(index, 1);
      this.$modal.msgSuccess("删除成功");
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

.required-field:before {
  content: "*";
  color: #f56c6c;
  margin-right: 4px;
}
.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
  line-height: 1.4;
}

.selected-notice {
  margin-top: 0px;
  padding: 0px 12px;
  background-color: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  color: #606266;
}

.detail-panel {
  padding: 20px 0;
}

.selection-container {
  padding: 10px;
}

/* 全新的详情页样式 */
.result-detail-container {
  height: 700px;
  overflow-y: auto;
  background-color: #f5f7fa;
  border-radius: 4px;
  padding: 0;
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB",
    "Microsoft YaHei", Arial, sans-serif;
}

.result-header {
  /* background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%); */
  padding: 24px;
  border-radius: 4px 4px 0 0;
  color: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
  text-align: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.header-title {
  margin: 0 0 15px 0;
  font-size: 22px;
  font-weight: 500;
  line-height: 1.4;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.header-meta {
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 14px;
  opacity: 0.9;
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

.info-row {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  margin-bottom: 16px;
}

.info-row .info-item {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.info-item {
  margin-bottom: 16px;
}

.full-width {
  grid-column: 1 / -1;
}

.info-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.info-value {
  font-size: 15px;
  color: #303133;
  line-height: 1.6;
  word-break: break-all;
}

.highlight {
  color: #f56c6c;
  font-weight: 500;
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
  min-height: 500px;
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


</style>
