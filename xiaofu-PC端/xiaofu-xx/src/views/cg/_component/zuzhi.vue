<template>
  <div class="detail-panel">
    <div class="header" style="display: flex; align-items: center">
      <h3>组建评定小组投票确定厂商</h3>
      <div style="margin-left: 20px">
        <el-button icon="el-icon-upload" type="primary" v-hasPermi="['xfpg:pdxzqdcs-add']" @click="handleAdd"
          v-if="pdxzSupplierList.length == 0">组建评定小组</el-button>
      </div>
    </div>

    <el-table v-loading="loading" :data="pdxzSupplierList">
      <el-table-column label="序号" align="center" type="index" width="80" :index="indexMethod" />
      <el-table-column label="标题" align="center" prop="title" />
      <el-table-column label="投票类型" align="center">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sc_yjzhlx" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column label="投票人数" align="center" prop="votedCount" >
        <template slot-scope="scope">
          <span>{{scope.row.type== 0 ? scope.row.votedCount : null}}</span> 
        </template>
      </el-table-column> 
      <el-table-column label="总人数" align="center" prop="count" >
        <template slot-scope="scope">
          <span>{{ scope.row.type== 0 ? scope.row.count : null}}</span>
        </template>
      </el-table-column>
      <el-table-column label="投票率" align="center" prop="votingRate">
        <template slot-scope="scope">
          <span>{{scope.row.type== 0 ? scope.row.votingRate+'%' : null}}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime">
        <template slot-scope="scope">
          {{ parseTime(scope.row.createTime, "{y}-{m}-{d} {h}:{i}:{s}") }}
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['xfpg:pdxzqdcs-edit']">修改</el-button>
          <!-- <el-button size="mini" type="text" icon="el-icon-view" @click="showQuestionnaire(scope.row)">查看</el-button> -->
          <el-dropdown>
            <el-button size="mini" type="text">
              更多
              <i class="el-icon-arrow-down"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item v-if="scope.row.type == 1">
                <el-button  size="mini" type="text" icon="el-icon-view"
            @click="handleOfflineDetail(scope.row)">详情</el-button>
              </el-dropdown-item>
              <el-dropdown-item v-if="scope.row.type == 0">
                <el-button  size="mini" type="text" icon="el-icon-view"
            @click="handleQrCode(scope.row)">二维码</el-button>
              </el-dropdown-item>
              <el-dropdown-item v-if="scope.row.type == 0">
                <el-button  size="mini" type="text" icon="el-icon-view"
            @click="handleStatistics(scope.row)">统计</el-button>
              </el-dropdown-item>
              <el-dropdown-item v-if="scope.row.type == 0">
                <el-button  size="mini" type="text" icon="el-icon-view"
            @click="handleSignature(scope.row)">投票名单</el-button>
              </el-dropdown-item>
              <el-dropdown-item>
                <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['xfpg:pdxzqdcs-remove']">删除</el-button>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown> 
        </template>
      </el-table-column>
    </el-table> 

    <!-- 添加或修改评定小组确定厂商对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1000px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px" v-if="open == true">
        <el-form-item label="标题" prop="xfptPdxzSureSupplier.title">
          <el-input v-model="form.xfptPdxzSureSupplier.title" placeholder="请输入标题" :readonly="readonly" />
        </el-form-item>

        <el-form-item label="投票类型" prop="xfptPdxzSureSupplier.type">
          <el-radio-group v-model="form.xfptPdxzSureSupplier.type" v-if="!readonly">
            <el-radio v-for="item in dict.type.sc_yjzhlx" :key="item.value" :label="item.value">{{ item.label
              }}</el-radio>
          </el-radio-group>
          <dict-tag :options="dict.type.sc_yjzhlx" :value="form.xfptPdxzSureSupplier.type" v-else />
        </el-form-item>
        <el-form-item label="评定小组名单" v-if="form.xfptPdxzSureSupplier.type == 0" prop="xfptPdxzSureSupplier.file">
          <el-upload class="upload-demo" :action="uploadUrl" :on-remove="handleRemove" :on-success="handleSuccess"
            :on-error="handleError" :limit="1" :headers="headers" accept=".xls,.xlsx"
            :file-list="form.xfptPdxzSureSupplier.file" :disabled="readonly">
            <el-button size="small" type="primary" :disabled="readonly">点击上传</el-button>
            <el-button size="small" type="text" @click.stop="handleDownload">下载模板</el-button>
            <div slot="tip" class="el-upload__tip">只能上传xls/xlsx文件</div>
          </el-upload>
        </el-form-item>
        <el-form-item label="人员名单" v-if="form.xfptPdxzSureSupplier.type == 0">
          <el-input v-model="form.xfptPdxzSureSupplier.names" type="textarea" :rows="3" readonly
            placeholder="上传人员名单后自动生成" />
        </el-form-item>
        <el-form-item label="投票结果" prop="xfptPdxzSureSupplier.otherFile" v-if="form.xfptPdxzSureSupplier.type == 1">
          <file-upload v-model="form.xfptPdxzSureSupplier.otherFile" :gaizhang="true" :fileType="['pdf']" :disabled="readonly">
            <el-button slot="template" type="text" @click.stop="importTemplate">下载模板</el-button>
          </file-upload>
        </el-form-item>
        <div v-if="form.xfptPdxzSureSupplier.type == 0"> 
          <el-form-item label="问卷内容："> 
            <el-button type="warning" icon="el-icon-edit" size="mini" @click="handleEditQuestionnaire">编辑问卷</el-button>
          </el-form-item>
          <el-form-item label="投票起止时间：" prop="xfptQuestionnaireConfig.surveyTime">
            <el-date-picker @change="handleSurveyTimeChange" v-model="form.xfptQuestionnaireConfig.surveyTime"
              type="datetimerange" value-format="yyyy-MM-dd HH:mm:ss" range-separator="至" start-placeholder="开始时间"
              end-placeholder="结束时间" :disabled="readonly" style="width: 400px">
            </el-date-picker>
          </el-form-item>
        </div>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    <!-- 问卷统计弹窗 -->
    <survey-statistics :visible.sync="statisticsVisible" :selectedSurveyId="selectedSurveyId" />

    <!-- 投票名单弹窗 -->
    <el-dialog title="投票名单" :visible.sync="signatureDialog" width="1200px" append-to-body>
      <!-- 搜索表单 -->
      <el-form :model="signatureQueryParams" ref="signatureQueryForm" size="small" :inline="true" label-width="80px"
        style="margin-bottom: 15px">
      </el-form>

      <el-table v-loading="signatureLoading" :data="signatureList" border height="350">
        <el-table-column v-for="item in ziduanList" :fixed="item.value == 'name'" :key="item.id" :label="item.label" :prop="item.value" align="center">
          <template slot-scope="scope">
            <span v-if="item.value == 'signature'">
              <img
                :src="scope.row[item.value]"
                class="signature-image"
                @click="previewSignature(scope.row[item.value])"
                alt="家长签名"
              />
            </span>
            <span v-else>{{ scope.row[item.value] }}</span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <pagination v-show="signatureTotal > 0" :total="signatureTotal" :page.sync="signatureQueryParams.pageNum"
        :limit.sync="signatureQueryParams.pageSize" @pagination="getSignatureList" style="margin-top: 15px" />

      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="handleExport">导出投票结果</el-button>
      </div>
    </el-dialog>

    <!-- 签名预览对话框 -->
    <el-dialog title="家长签名预览" :visible.sync="signaturePreviewDialog" width="600px" append-to-body center>
      <div class="signature-preview-container">
        <img :src="signaturePreviewUrl" class="signature-preview-image" alt="家长签名预览" />
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="signaturePreviewDialog = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 线下投票详情弹窗 -->
    <el-dialog title="线下投票详情" :visible.sync="offlineDetailDialog" width="800px" append-to-body>
      <el-form label-width="120px" v-if="offlineDetailDialog">
        <el-form-item label="标题">
          <span>{{ offlineDetail.xfptPdxzSureSupplier && offlineDetail.xfptPdxzSureSupplier.title }}</span>
        </el-form-item> 
        <el-form-item label="创建时间">
          <span>{{ offlineDetail.xfptPdxzSureSupplier && offlineDetail.xfptPdxzSureSupplier.createTime ? parseTime(offlineDetail.xfptPdxzSureSupplier.createTime, "{y}-{m}-{d} {h}:{i}:{s}") : '' }}</span>
        </el-form-item>
        <el-form-item label="投票结果文件">
          <file-upload v-model="offlineDetail.xfptPdxzSureSupplier.otherFile" :disabled="true" ></file-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="offlineDetailDialog = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 问卷编辑 -->
    <el-dialog :visible.sync="wjbj" fullscreen append-to-body @close="wjClose">
      <wjBianji :v-if="wjbj" ref="wjBianjiRef" :sonList="form.xfptQuestionnaireConfig && form.xfptQuestionnaireConfig.sonList ? form.xfptQuestionnaireConfig.sonList : []" @save="wjSave" />
    </el-dialog>
  </div>
</template>

<script>
import {
  listPdxzSupplier,
  getPdxzSupplier,
  delPdxzSupplier,
  addPdxzSupplier,
  updatePdxzSupplier,
  getCompanyInfo,
} from "@/api/cg/pdxzSupplier";
import { getZiduanList } from "@/api/cg/index"
import { getSurveyStatistics } from "@/api/school/survey";
import { getVoteSignatureList } from "@/api/cg/opinions";
import QRCode from "qrcode";
import SurveyStatistics from "@/views/wjdc/tongji";
import { getToken } from "@/utils/auth";
import wjBianji from "@/views/wjdc/wjBianji";


export default {
  name: "Zuzhi",
  props: {
    formId: {
      type: String,
      default: "",
    },
  },
  dicts: ["zdfs", "sc_type", "sc_yjzhlx"],
  components: {
    SurveyStatistics,
    wjBianji
  },
  data() {
    return {
      uploadUrl:
        process.env.VUE_APP_BASE_API + "/school/team/meeting/importUserName", // 上传文件服务器地址
      headers: {
        Authorization: "Bearer " + getToken(),
      },
      readonly: false,
      // 遮罩层
      loading: true,

      // 总条数
      total: 0,
      // 评定小组确定厂商表格数据
      pdxzSupplierList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        teamId: this.formId,
      },
      // 表单参数
      form: {
        xfptPdxzSureSupplier: {
          id: null,
          teamId: this.formId,
          title: null,
          file: null,
          otherFile: null,
          names: null,
          type: null,
        },
        xfptQuestionnaireConfig: {
          votingDescription: null,
          title: null,
          sonList: [],
          beginTime: null,
          endTime: null,
          surveyTime: null,
        },
      },
      // 表单校验
      rules: {
        xfptPdxzSureSupplier: {
          title: [{ required: true, message: "请输入标题", trigger: "blur" }],
          file: [
            { required: true, message: "请上传评定小组名单", trigger: "blur" },
          ],
          type: [
            { required: true, message: "请选择投票类型", trigger: "blur" },
          ],
          otherFile: [
            { required: true, message: "请选上传票结果", trigger: "blur" },
          ],
        },
        xfptQuestionnaireConfig: {
          title: [
            { required: true, message: "请输入问卷名称", trigger: "blur" },
          ],
          surveyTime: [
            { required: true, message: "请选择投票起止时间", trigger: "blur" },
          ],
        },
      },
      // 厂商列表
      companyList: [],
      // 统计相关数据
      statisticsVisible: false,
      selectedSurveyId: null,
      // 投票名单相关数据
      signatureDialog: false,
      signatureLoading: false,
      signatureList: [],
      signatureTotal: 0,
      currentQuestionBusinessId: null,
      signatureQueryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        phone: null,
      },
      // 签名预览相关数据
      signaturePreviewDialog: false,
      signaturePreviewUrl: "",
      // 问卷编辑相关数据
      wjbj: false, 
      ziduanList: [],
      detailDialogVisible: false,
      // 线下投票详情
      offlineDetailDialog: false,
      offlineDetailLoading: false,
      offlineDetail: {},
      offlineOtherFiles: [],
    };
  },
  created() {
    this.getList();
  },
  methods: {
    importTemplate() {
      const link = document.createElement("a");
      link.href =
        process.env.VUE_APP_IMAGE_BASE_API +
        "/filestatics/download/附件6.1评定组成员意见及签字（公开）.xlsx";
      link.download = "附件6.1评定组成员意见及签字（公开）.xlsx";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
    /** 序号 */
    indexMethod(index) {
      return (
        (this.queryParams.pageNum - 1) * this.queryParams.pageSize + index + 1
      );
    },
    /** 查询评定小组确定厂商列表 */
    getList() {
      this.loading = true;
      listPdxzSupplier(this.queryParams).then((response) => {
        this.pdxzSupplierList = response.rows;
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
        xfptPdxzSureSupplier: {
          id: null,
          teamId: this.formId,
          title: null,
          file: null,
          otherFile: null,
          names: null,
          type: null,
        },
        xfptQuestionnaireConfig: {
          votingDescription: null,
          title: null,
          sonList: [],
          beginTime: null,
          endTime: null,
          surveyTime: null,
        },
      };
      this.resetForm("form");
    },


    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.readonly = false;

      // 设置默认投票起止时间（延后一周）
      const now = new Date();
      const startTime = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000); // 一周后开始
      const endTime = new Date(startTime.getTime() + 7 * 24 * 60 * 60 * 1000); // 再延后一周结束

      // 格式化时间字符串
      const formatDateTime = (date) => {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, "0");
        const day = String(date.getDate()).padStart(2, "0");
        const hours = String(date.getHours()).padStart(2, "0");
        const minutes = String(date.getMinutes()).padStart(2, "0");
        const seconds = String(date.getSeconds()).padStart(2, "0");
        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
      };

      const startTimeStr = formatDateTime(startTime);
      const endTimeStr = formatDateTime(endTime);

      this.form.xfptQuestionnaireConfig.beginTime = startTimeStr;
      this.form.xfptQuestionnaireConfig.endTime = endTimeStr;
      this.form.xfptQuestionnaireConfig.surveyTime = [startTimeStr, endTimeStr];

      getCompanyInfo(this.formId).then((response) => {
        this.companyList = response.data;
        this.form.xfptQuestionnaireConfig.votingDescription = JSON.stringify(
          response.data
        );
        this.handleAddRow();
        this.open = true;
        this.title = "添加评定小组确定厂商";
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      this.readonly = false;
      getPdxzSupplier(id).then((response) => {
        // 确保 form 对象结构完整
        this.form = {
          xfptPdxzSureSupplier: {
            ...this.form.xfptPdxzSureSupplier,
            ...response.data.xfptPdxzSureSupplier
          },
          xfptQuestionnaireConfig: {
            ...this.form.xfptQuestionnaireConfig,
            ...response.data.xfptQuestionnaireConfig
          }
        };
        if (this.form.xfptPdxzSureSupplier.type == 0) {
          this.form.xfptPdxzSureSupplier.file = [
            {
              name: "人员名单",
              url: this.form.xfptPdxzSureSupplier.names,
              uid: new Date().getTime() + 1,
            },
          ];

          this.form.xfptQuestionnaireConfig.surveyTime = [
            this.form.xfptQuestionnaireConfig.beginTime,
            this.form.xfptQuestionnaireConfig.endTime,
          ];
        }
        this.open = true;
        this.title = "修改评定小组确定厂商";
      });
    },
    /** 查看详情 */
    showQuestionnaire(row) {
      this.reset();
      const id = row.id;
      this.readonly = true;
      getPdxzSupplier(id).then((response) => {
        // 确保 form 对象结构完整
        this.form = {
          xfptPdxzSureSupplier: {
            ...this.form.xfptPdxzSureSupplier,
            ...response.data.xfptPdxzSureSupplier
          },
          xfptQuestionnaireConfig: {
            ...this.form.xfptQuestionnaireConfig,
            ...response.data.xfptQuestionnaireConfig
          }
        };
        if (this.form.xfptPdxzSureSupplier.type == 1) {
          this.form.xfptQuestionnaireConfig.surveyTime = [
            this.form.xfptQuestionnaireConfig.beginTime,
            this.form.xfptQuestionnaireConfig.endTime,
          ];
        }
        this.open = true;
        this.title = "查看评定小组确定厂商";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.xfptPdxzSureSupplier.id != null) {
            updatePdxzSupplier(this.form).then((response) => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
              this.$emit("handleGetProcessMenu");
            });
          } else {
            addPdxzSupplier(this.form).then((response) => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
              this.$emit("handleGetProcessMenu");
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
          return delPdxzSupplier(ids);
        })
        .then(() => {
          this.getList();
          this.$emit("handleGetProcessMenu");

          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => { });
    },

    /** 添加问卷行 */
    handleAddRow() {
      this.form.xfptQuestionnaireConfig.sonList.push({
        surveyContent: "选择厂商",
        answeringMethod:'0',
        surveyResult: this.companyList.map((item) => item.name).join("、"),
        isRequired: true,
        id:'',
      });
    },

    /** 投票起止时间 */
    handleSurveyTimeChange(val) {
      this.form.xfptQuestionnaireConfig.beginTime = val[0];
      this.form.xfptQuestionnaireConfig.endTime = val[1];
    },

    //生成二维码
    handleQrCode(row) {
      // 显示加载中，防止用户重复点击
      const loading = this.$loading({
        lock: true,
        text: "正在生成二维码...",
        spinner: "el-icon-loading",
        background: "rgba(0, 0, 0, 0.7)",
      });

      // 构建问卷URL
      const url =
        process.env.VUE_APP_QR_CODE_URL + `?id=${row.questId}`;

      // 使用QRCode生成二维码图片URL
      QRCode.toDataURL(url, {
        width: 240,
        margin: 2,
        color: {
          dark: "#000000",
          light: "#ffffff",
        },
        errorCorrectionLevel: "H",
      })
        .then((dataUrl) => {
          // 关闭加载提示
          loading.close();

          // 在二维码上添加文字
          this.addTextToImage(dataUrl, '备注：每人只需投票一次').then((imageWithText) => {
            // 构建HTML内容
            const content = `
            <div style="text-align: center; padding: 15px 0;">
              <img src="${imageWithText}" style="width: 240px; height: 280px;"> 
              <p style="font-size: 12px; color: #999;"></p>
              <button id="saveQrCodeBtn" style="
                margin-top: 15px;
                background-color: #409EFF;
                color: white;
                border: none;
                padding: 8px 15px;
                border-radius: 4px;
                cursor: pointer;
                font-size: 14px;">
                保存二维码
              </button>
            </div>
          `;
            const handleSaveClick = () => {
              this.downloadImage(
                imageWithText,
                `问卷二维码_${row.questionConfigId}.png`
              );
            };
            // 显示二维码对话框
            this.$alert(content, `${row.title}`, {
              dangerouslyUseHTMLString: true,
              center: true,
              showConfirmButton: false,
              showClose: true,
              closeOnClickModal: true,
              callback: () => {
                // 清理事件监听
                const saveBtn = document.getElementById("saveQrCodeBtn");
                if (saveBtn) {
                  saveBtn.removeEventListener("click", handleSaveClick);
                }
              },
            });

            // 添加点击事件处理函数
            setTimeout(() => {
              const saveBtn = document.getElementById("saveQrCodeBtn");
              if (saveBtn) {
                saveBtn.addEventListener("click", handleSaveClick);
              }
            }, 100);
          });
        })
        .catch((err) => {
          loading.close();
          this.$message.error("二维码生成失败");

        });
    },
    // 在图片上添加文字
    addTextToImage(imageDataUrl, text) {
      return new Promise((resolve) => {
        const canvas = document.createElement('canvas');
        const ctx = canvas.getContext('2d');
        const img = new Image();
        
        img.onload = () => {
          // 设置画布尺寸，为文字预留空间
          const qrWidth = 240;
          const qrHeight = 240;
          const textHeight = 40;
          canvas.width = qrWidth;
          canvas.height = qrHeight + textHeight;
          
          // 绘制二维码
          ctx.drawImage(img, 0, 0, qrWidth, qrHeight);
          
          // 绘制文字背景
          ctx.fillStyle = '#ffffff';
          ctx.fillRect(0, qrHeight, qrWidth, textHeight);
          
          // 绘制文字
          ctx.fillStyle = '#333333';
          ctx.font = '16px Arial, sans-serif';
          ctx.textAlign = 'center';
          ctx.textBaseline = 'middle';
          
          // 文字换行处理
          const maxWidth = qrWidth - 20;
          const words = text.split('');
          let line = '';
          let lines = [];
          
          for (let i = 0; i < words.length; i++) {
            const testLine = line + words[i];
            const metrics = ctx.measureText(testLine);
            const testWidth = metrics.width;
            
            if (testWidth > maxWidth && i > 0) {
              lines.push(line);
              line = words[i];
            } else {
              line = testLine;
            }
          }
          lines.push(line);
          
          // 绘制多行文字
          const lineHeight = 16;
          const startY = qrHeight + (textHeight - lines.length * lineHeight) / 2;
          
          lines.forEach((line, index) => {
            ctx.fillText(line, qrWidth / 2, startY + index * lineHeight);
          });
          
          // 返回带有文字的图片数据URL
          resolve(canvas.toDataURL('image/png'));
        };
        
        img.src = imageDataUrl;
      });
    },
    // 下载图片
    downloadImage(dataUrl, fileName) {
      const link = document.createElement("a");
      link.download = fileName;
      link.href = dataUrl;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
    /** 统计按钮操作 */
    handleStatistics(row) {
      getSurveyStatistics({ id: row.questId }).then((res) => {
        this.statisticsVisible = true;
        this.selectedSurveyId = row.questId;
      });
    },
    handleDownload() {
      // var name = "评定小组名单模板";
      // var url =
      //   process.env.VUE_APP_IMAGE_BASE_API +
      //   "/filestatics/download/评定小组名单.xlsx";
      // var suffix = url.substring(url.lastIndexOf("."), url.length);
      // const a = document.createElement("a");
      // a.setAttribute("download", name + suffix);
      // a.setAttribute("target", "_blank");
      // a.setAttribute("href", url);
      // a.click();
      const link = document.createElement("a");
      link.href =
        process.env.VUE_APP_IMAGE_BASE_API + "/filestatics/download/姓名模板姓名+电话.xlsx";
      link.download = "姓名模板姓名+电话.xlsx";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
    //人员名单上传成功
    handleSuccess(response, file, fileList) {
      this.form.xfptPdxzSureSupplier.file = fileList;
      this.form.xfptPdxzSureSupplier.names = response.msg;
      this.form.xfptPdxzSureSupplier.count = response.msg.split(',').length;
    },
    //人员名单移除文件
    handleRemove(file, fileList) {
      this.form.xfptPdxzSureSupplier.file = fileList;
      this.form.xfptPdxzSureSupplier.names = "";
    },
    //人员名单上传失败
    handleError(err, file, fileList) {
      this.$message.error("上传失败");
    },
    /** 投票名单按钮操作 */
    handleSignature(row) { 
      this.currentQuestionBusinessId = row.questId;
      this.signatureDialog = true;
      this.resetSignatureQuery();
      this.getZiduanList();
      this.getSignatureList();
    },
    //导出问卷结果表格
    handleExport() {
      this.download(
        "/school/questionnaireSurvey/exportResultExcel",
        {
          questionId:this.currentQuestionBusinessId
        },
        `问卷结果_${new Date().getTime()}.xlsx`
      );
    },
    //获取表头
    getZiduanList() {
      getZiduanList(this.currentQuestionBusinessId).then((response) => {
        this.ziduanList = response.data
      });
    },
    /** 获取投票名单列表 */
    getSignatureList() {
      this.signatureLoading = true;
      getVoteSignatureList({
        ...this.signatureQueryParams,
        questId: this.currentQuestionBusinessId,
      })
        .then((response) => {
          this.signatureList = response.rows || response.data || [];
          this.signatureTotal = response.total || 0;
          this.signatureLoading = false;
        })
        .catch(() => {
          this.signatureLoading = false;
          this.$message.error("获取投票名单失败");
        });
    },

    /** 投票名单搜索按钮操作 */
    handleSignatureQuery() {
      this.signatureQueryParams.pageNum = 1;
      this.getSignatureList();
    },

    /** 投票名单重置按钮操作 */
    resetSignatureQuery() {
      this.signatureQueryParams = {
        pageNum: 1,
        pageSize: 10,
        name: null,
        phone: null,
      };
      if (this.$refs.signatureQueryForm) {
        this.$refs.signatureQueryForm.resetFields();
      }
      if (this.signatureDialog) {
        this.getSignatureList();
      }
    },

    /** 预览签名图片 */
    previewSignature(imageUrl) {
      this.signaturePreviewUrl = imageUrl;
      this.signaturePreviewDialog = true;
    },
    // 线下投票详情
    handleOfflineDetail(row) {
      this.offlineDetailDialog = true;
      this.offlineDetailLoading = true;
      const id = row.id;
      getPdxzSupplier(id)
        .then((response) => {
          this.offlineDetail = response.data || {};
          const files = this.offlineDetail.xfptPdxzSureSupplier && this.offlineDetail.xfptPdxzSureSupplier.otherFile;
          this.offlineOtherFiles = this.normalizeOtherFiles(files);
          this.offlineDetailLoading = false;
        })
        .catch(() => {
          this.offlineDetailLoading = false;
        });
    },
    normalizeOtherFiles(otherFile) {
      if (!otherFile) return [];
      if (Array.isArray(otherFile)) {
        return otherFile.map((f) => ({
          name: f.name || (f.url ? f.url.split('/').pop() : '文件'),
          url: f.url || f.response?.url || '',
        })).filter(f => f.url);
      }
      if (typeof otherFile === 'string') {
        return otherFile.split(',').filter(Boolean).map((u) => ({ name: u.split('/').pop(), url: u }));
      }
      if (typeof otherFile === 'object' && otherFile.url) {
        return [{ name: otherFile.name || otherFile.url.split('/').pop(), url: otherFile.url }];
      }
      return [];
    },
    //问卷编辑
    handleEditQuestionnaire() {
      this.wjbj = true;
      this.$nextTick(() => {
        this.$refs.wjBianjiRef.showQuestionnaire();
      });
    },
    // 关闭问卷编辑对话框
    wjClose() {
       this.$refs.wjBianjiRef.clearQuestionnaire();
     },
     // 保存问卷
     wjSave(data) { 
       this.form.xfptQuestionnaireConfig.sonList = data.map(item => {
         return {
           surveyContent: item.config.surveyContent,
           answeringMethod: item.config.answeringMethod,
           isRequired: item.config.isRequired,
           sm: item.config.sm,
           surveyResult: item.config.answeringMethod == 0 || item.config.answeringMethod == 1 ? item.config.options.join('、') : '',
           id: item.config.id
         }
       });  
       this.wjbj = false; 
       this.$message.success("问卷保存成功");
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

/* 签名相关样式 */
.signature-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.signature-image {
  width: 60px;
  height: 40px;
  object-fit: contain;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.signature-image:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
  transform: scale(1.05);
}

.no-signature {
  color: #c0c4cc;
  font-size: 12px;
}

.signature-preview-container {
  text-align: center;
  padding: 20px;
}

.signature-preview-image {
  max-width: 100%;
  max-height: 400px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
</style>
