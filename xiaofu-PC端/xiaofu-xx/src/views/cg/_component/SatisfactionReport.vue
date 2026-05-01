<template>
  <div class="detail-panel">
    <div class="header" style="display: flex; align-items: center">
      <h3>选用组织家长满意度总结报告报备与公开</h3>
      <div style="margin-left: 20px">
        <el-button type="primary" icon="el-icon-plus" v-hasPermi="['xfpt:jzmydzj:add']" @click="addZJBG"
          v-if="tableZJ.length == 0">添加总结报告</el-button>
      </div>
    </div>
    <el-table :data="tableZJ" style="width: 100%">
      <el-table-column label="序号" align="center" type="index" />
      <el-table-column align="center" prop="summarizeReportName" label="总结报告名称"></el-table-column>
      <el-table-column align="center" prop="summarizeReportType" label="问卷调查类型"></el-table-column>
      <el-table-column label="意见征集人数" align="center" prop="votedCount" >
        <template slot-scope="scope">
          <span>{{scope.row.summarizeReportType== '线上调查' ? scope.row.votedCount : null}}</span> 
        </template>
      </el-table-column> 
      <el-table-column label="总人数" align="center" prop="count" >
        <template slot-scope="scope">
          <span>{{ scope.row.summarizeReportType== '线上调查' ? scope.row.count : null}}</span>
        </template>
      </el-table-column>
      <el-table-column label="投票率" align="center" prop="votingRate">
        <template slot-scope="scope">
          <span>{{scope.row.summarizeReportType== '线上调查' ? scope.row.votingRate+'%' : null}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="问卷调查起止时间">
        <template slot-scope="scope">
          <div v-if="scope.row.summarizeReportType == '线上调查'">
            {{ scope.row.startTime }} 至 {{ scope.row.endTime }}
          </div> 
        </template>
      </el-table-column> 
      <el-table-column align="center" label="问卷调查状态">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sc_wjzt" :value="scope.row.state"
            v-if="scope.row.summarizeReportType == '线上调查'" />
          <span v-else>无</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作">
        <template slot-scope="scope"> 
          <el-button type="text" size="mini" icon="el-icon-view" v-hasPermi="['xfpt:jzmydzj:editjg']"
            @click="editDC(scope.row)" v-if="scope.row.summarizeReportType == '线下调查'">编辑调查结果</el-button>
          <el-button v-if="scope.row.summarizeReportType == '线上调查'" size="mini" type="text" icon="el-icon-view"
            @click="handleQrCode(scope.row)">二维码</el-button>
          <el-dropdown>
            <el-button type="text" size="mini">更多
              <i class="el-icon-arrow-down"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item v-if="scope.row.summarizeReportType == '线下调查'">
                <el-button size="mini" type="text" icon="el-icon-view"
            @click="handleOfflineDetail(scope.row)">详情</el-button>
              </el-dropdown-item>
              <el-dropdown-item v-if="scope.row.summarizeReportType == '线上调查'">
                <el-button  size="mini" type="text" icon="el-icon-view"
            @click="handleStatistics(scope.row)">统计</el-button>
              </el-dropdown-item>
              <el-dropdown-item v-if="scope.row.summarizeReportType == '线上调查'">
                <el-button  size="mini" type="text" icon="el-icon-view"
            @click="handleSignature(scope.row)">投票名单</el-button>
              </el-dropdown-item>
              <el-dropdown-item>
                <el-button size="mini" type="text" icon="el-icon-delete" v-hasPermi="['xfpt:jzmydzj:remove']"
                @click="handleDelete(scope.row)">删除</el-button>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown> 
        </template>
      </el-table-column>
    </el-table>
    <!-- 添加总结报告弹窗 -->
    <el-dialog title="添加总结报告" append-to-body :visible.sync="dialogVisible" width="1100px" :before-close="closeDialog">
      <el-form :model="form" label-width="150px" :rules="rules" ref="reportForm">
        <el-form-item label="总结报告名称" prop="summarizeReportName">
          <el-input v-model="form.summarizeReportName" placeholder="XX学校家长满意度调查问卷"></el-input>
        </el-form-item>
        <el-form-item label="总结报告类型" prop="summarizeReportType">
          <div class="custom-radio-group">
            <div class="custom-radio" :class="{
              'is-active': form.summarizeReportType === '线上调查',
            }" @click="selectReportType('线上调查')">
              <span class="radio-circle"></span>
              <span class="radio-label">线上调查</span>
            </div>
            <div class="custom-radio" :class="{
              'is-active': form.summarizeReportType === '线下调查',
            }" @click="selectReportType('线下调查')">
              <span class="radio-circle"></span>
              <span class="radio-label">线下调查</span>
            </div>
          </div>
        </el-form-item>
        <el-form-item v-if="form.summarizeReportType === '线上调查'" label="问卷调查截止时间" prop="contractDeadline">
          <el-date-picker style="width: 400px" @change="changeDate" v-model="form.contractDeadline" type="datetimerange"
            value-format="yyyy-MM-dd HH:mm:ss" range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item v-if="form.summarizeReportType == '线上调查'" label="问卷内容">
          <div class="table-operations">
            <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAddQuestionnaire"
              v-if="sonList.length == 0">添加问卷</el-button>
            <el-button type="warning" icon="el-icon-edit" size="mini" @click="handleEditQuestionnaire"
              v-else>编辑问卷</el-button>
          </div>
        </el-form-item>
        <el-form-item label="上传人员名单：" prop="file" v-if="form.summarizeReportType == '线上调查'">
          <el-upload class="upload-demo" :action="personUploadUrl" :on-remove="handlePersonRemove"
            :on-success="handlePersonSuccess" :on-error="handlePersonError" :limit="1" :headers="personHeaders"
            accept=".xls,.xlsx" :file-list="form.file" :disabled="false">
            <el-button size="small" type="primary">点击上传</el-button>
            <el-button size="small" type="text" @click.stop="downloadPersonTemplate">下载模板</el-button>
            <div slot="tip" class="el-upload__tip">只能上传xls/xlsx文件</div>
          </el-upload>
        </el-form-item>
        <el-form-item label="人员名单" v-if="form.summarizeReportType == '线上调查'">
          <el-input type="textarea" :rows="3" v-model="form.names" readonly placeholder="上传人员名单后自动生成" />
        </el-form-item>

        <!-- 新增上传图片和上传附件功能，仅在线下调查时显示 -->
        <template v-if="form.summarizeReportType === '线下调查'">
          <!-- 新增：上传人员名单 -->

          <el-form-item label="上传调查结果：">
            <FileUpload v-model="form.surveyFileUrl" :gaizhang="true" :fileType="['pdf']">
              <el-button slot="template" type="text" @click.stop="importTemplate">下载模板</el-button>
            </FileUpload>
          </el-form-item>
        </template>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReportInfo">确定</el-button>
      </div>
    </el-dialog>
    <el-dialog title="编辑调查结果" :visible.sync="dialogFileInfo" append-to-body width="700px">
      <el-form label-width="120">
        <el-form-item label="编辑调查结果：">
          <FileUpload v-model="surveyFileUrl"></FileUpload>
        </el-form-item>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogFileInfo = false">取 消</el-button>
        <el-button type="primary" @click="editSurveyResult">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 查看问卷详情弹窗 -->
    <el-dialog title="查看问卷详情" append-to-body :visible.sync="dialogDetailVisible" width="1100px"
      :before-close="closeDetailDialog">
      <el-form :model="detailForm" label-width="150px" ref="detailForm" disabled>
        <el-form-item label="总结报告名称">
          <el-input v-model="detailForm.summarizeReportName" readonly></el-input>
        </el-form-item>
        <el-form-item label="总结报告类型">
          <div class="custom-radio-group">
            <div class="custom-radio" :class="{
              'is-active': detailForm.summarizeReportType === '线上调查',
            }">
              <span class="radio-circle"></span>
              <span class="radio-label">线上调查</span>
            </div>
            <div class="custom-radio" :class="{
              'is-active': detailForm.summarizeReportType === '线下调查',
            }">
              <span class="radio-circle"></span>
              <span class="radio-label">线下调查</span>
            </div>
          </div>
        </el-form-item>
        <el-form-item v-if="detailForm.summarizeReportType === '线上调查'" label="问卷调查截止时间">
          <el-input v-model="detailForm.timeRange" readonly></el-input>
        </el-form-item>

        <el-form-item label="调查问卷：">
          <div class="selected-questionnaire">
            <span class="questionnaire-name">{{
              detailForm.questionnaireName
            }}</span>
          </div>
        </el-form-item>

        <el-form-item label="参与调查年级">
          <el-table :data="detailForm.schoolGrades || []" class="grade-table" row-key="id"
            :expand-row-keys="detailForm.expandedRows" ref="detailGradeTable">
            <el-table-column type="expand" width="50">
              <template slot-scope="props">
                <div class="class-manage-expanded">
                  <div class="class-header">
                    <div class="class-name-header">班级名称</div>
                    <div class="class-number-header" style="text-align: center">
                      班级人数
                    </div>
                  </div>
                  <div v-for="(cls, clsIndex) in props.row.classes || []" :key="clsIndex" class="class-row">
                    <div class="class-name">
                      <div class="class-name-flex">
                        <el-checkbox style="margin: 0 20px" v-model="cls.selected" disabled></el-checkbox>
                        {{ cls.gradeClass }}
                      </div>
                    </div>
                    <div class="class-number" style="text-align: center">
                      {{ cls.number }}人
                    </div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="年级名称" min-width="60%">
              <template slot-scope="scope">
                <div class="grade-name-flex">
                  <el-checkbox style="margin-right: 10px" disabled :value="true"></el-checkbox>
                  {{ scope.row.gradeClass }}
                </div>
              </template>
            </el-table-column>
            <el-table-column label="学生人数" align="center" min-width="40%">
              <template slot-scope="scope">
                <div class="class-count-info" style="text-align: center">
                  {{ getTotalClassCount(scope.row) }}人
                </div>
              </template>
            </el-table-column>
          </el-table>
          <div class="grade-summary">
            <div class="total">总人数: {{ detailForm.amount }} 人</div>
          </div>
        </el-form-item>

        <!-- 线下调查时显示上传的文件 -->
        <template v-if="detailForm.summarizeReportType === '线下调查'">
          <el-form-item label="调查结果文件">
            <FileUpload v-model="detailForm.surveyFileUrl" disabled></FileUpload>
          </el-form-item>
        </template>

        <el-form-item label="创建信息">
          <div class="create-info">
            <span>创建人：{{ detailForm.createBy }}</span>
            <span style="margin-left: 20px">创建时间：{{ detailForm.createTime }}</span>
            <span style="margin-left: 20px">所属部门：{{ detailForm.deptName }}</span>
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogDetailVisible = false">关闭</el-button>
      </div>
    </el-dialog>
    <!-- 问卷统计弹窗 -->
    <survey-statistics :visible.sync="statisticsVisible" :selectedSurveyId="selectedSurveyId" />

    <!-- 投票名单弹窗 -->
    <el-dialog title="投票名单" :visible.sync="signatureDialog" width="1200px" append-to-body> 

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

    <!-- 问卷编辑对话框 -->
    <el-dialog :visible.sync="wjbj" fullscreen append-to-body @close="wjClose">
      <wjBianji :visible.sync="wjbj" ref="wjBianjiRef" :sonList="sonList" @save="wjSave" />
    </el-dialog>

    <!-- 线下调查详情弹窗 -->
    <el-dialog title="线下调查详情" :visible.sync="offlineDetailDialog" width="800px" append-to-body>
      <el-form label-width="120px" v-if="offlineDetailDialog">
        <el-form-item label="总结报告名称">
          <span>{{ offlineDetail.summarizeReportName }}</span>
        </el-form-item> 
        <el-form-item label="创建时间">
          <span>{{ offlineDetail.createTime ? parseTime(offlineDetail.createTime, "{y}-{m}-{d} {h}:{i}:{s}") : '' }}</span>
        </el-form-item>
        <el-form-item label="调查结果文件">
          <file-upload v-model="offlineDetail.surveyFileUrl" :disabled="true"></file-upload>
        </el-form-item> 
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="offlineDetailDialog = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listQuestionnaire,
  summarizeReportList,
  gradeList,
  addSummarizeReport,
  editQuestionnaireSurvey,
  getSummarizeReportDetail,
  delSummarizeReport,
} from "@/api/school/son";
import { listSurveyNew } from "@/api/school/survey";
import { Base64 } from "js-base64";
import { getInfo } from "@/api/login";
import { getToken } from "@/utils/auth";
import { getSurveyStatistics } from "@/api/school/survey";
import QRCode from "qrcode";
import SurveyStatistics from "@/views/wjdc/tongji";
import { getVoteSignatureList } from "@/api/cg/opinions";
import { getConfigByType , getZiduanList } from "@/api/cg/index";
import wjBianji from "@/views/wjdc/wjBianji";
export default {
  name: "SatisfactionReport",
  dicts: ["sc_wjzt"],
  components: {
    SurveyStatistics,
    wjBianji,
  },
  props: {
    tableGS: {
      type: Array,
      default: () => [],
    },
    formId: {
      type: String,
      default: "",
    },
  },
  created() {
    // 初始化时直接设置teamMeetingId
    this.form.teamMeetingId = this.formId;
    this.searchParams.teamMeetingId = this.formId;
    
    this.getZJList();
    // 获取问卷配置
    this.getConfigByType();
  },
  mounted() {
    // 初始化时确保expandedRows为空数组
    this.expandedRows = [];

  },
  data() {
    // 线上调查时间验证
    const validateOnlineTime = (rule, value, callback) => {
      if (
        this.form.summarizeReportType === "线上调查" &&
        (!value || !this.form.startTime || !this.form.endTime)
      ) {
        callback(new Error("请选择问卷调查截止时间"));
      } else {
        callback();
      }
    };

    return {
      surveyFileUrl: null,
      tableZJ: [],
      // 搜索参数
      searchParams: {
        summarizeReportName: "", // 总结报告名称
        summarizeReportType: "all", // 问卷调查类型
        contractDeadline: "", // 线上截止日期
        summarizeReportStatus: "all", // 问卷调查状态
        pageNum: 1,
        pageSize: 10,
        teamMeetingId: null,
      },
      // 问卷调查库表格数据
      surveyList: [],
      loading: false,
      total: 0,
      // 选中的问卷
      selectedQuestionnaire: null,
      // 选中的问卷ID数组（表格多选用）
      selectedquestionnaireSurveyIds: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        reportName: "",
      },
      schoolGrades: [],
      myTotal: 0,
      // 问卷调查库表格数据
      questionnaireDialogVisible: false,

      upload: {
        // 设置上传的请求头部
        headers: { Authorization: "Bearer " + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/file/upload",
      },

      dialogVisible: false,
      classManageVisible: false,
      addGradeDialogVisible: false,
      totalStudents: 0,
      expandedRows: [],
      isAllExpanded: false,
      form: {
        summarizeReportName: "",
        summarizeReportType: "线上调查",
        contractDeadline: "",
        questionnaireSurveyId: "",
        questionnaireName: "",
        startTime: "",
        endTime: "",
        teamMeetingId: "",
      },


      rules: {
        file: [
          { required: true, message: "请上传人员名单", trigger: "change" },
        ],
        votingNotice: [
          { required: true, message: "请输入调查须知", trigger: "blur" },
        ],
        summarizeReportName: [
          { required: true, message: "请输入总结报告名称", trigger: "blur" },
        ],
        summarizeReportType: [
          { required: true, message: "请选择总结报告类型", trigger: "change" },
        ],
        questionnaireSurveyId: [
          { required: true, message: "请选择调查问卷", trigger: "change" },
        ],
        contractDeadline: [
          {
            required: true,
            message: "请选择问卷调查截止时间",
            trigger: "change",
          },
          { validator: validateOnlineTime, trigger: "change" },
        ],
      },
      dialogFileInfo: false,
      gradeData: [],
      schoolGrades: [],
      newGrade: {
        name: "",
        alias: "",
        classes: [],
      },
      totalStudentCount: 0,
      totalClassCount: 0,
      addInfo: {
        deptId: "",
        deptName: "",
        gradeClass: "",
        classes: [],
      },
      currentEditRow: null, // 当前编辑的行数据
      dialogDetailVisible: false, // 查看问卷详情弹窗
      detailForm: {
        summarizeReportName: "",
        summarizeReportType: "",
        timeRange: "",
        questionnaireName: "",
        gradeClassList: [],
        fileList: [],
      },
      // 统计相关数据
      statisticsVisible: false,
      selectedSurveyId: null,
      // 新增：人员名单相关
      personFileList: [],
      personNames: "",
      personUploadUrl:
        process.env.VUE_APP_BASE_API + "/school/team/meeting/importUserName",
      personHeaders: {
        Authorization: "Bearer " + getToken(),
      },
      // 投票名单相关
      signatureDialog: false,
      signatureLoading: false,
      signatureList: [],
      signatureTotal: 0,
      signatureQueryParams: {
        pageNum: 1,
        pageSize: 10,
        summarizeReportId: null,
      },
      signaturePreviewDialog: false,
      signaturePreviewUrl: "",
      // 问卷相关数据
      sonList: [],
      wjbj: false, // 问卷编辑对话框的可见性
      wjBianjiRef: null, // wjBianji 组件的引用
      ziduanList: [],
      detailDialogVisible: false,
      // 线下调查详情
      offlineDetailDialog: false,
      offlineDetail: {},
    };

  },
  watch: {

    // 监听formId变化，确保teamMeetingId始终保持同步
    formId: {
      handler(newVal) {
        if (newVal) {
          this.form.teamMeetingId = newVal;

        }
      },
      immediate: true,
    },
  },

  methods: {
    importTemplate() {
      const link = document.createElement("a");
      link.href =
        process.env.VUE_APP_IMAGE_BASE_API +
        "/filestatics/download/附件10.2校服选购问卷调查总结报告（公开）.docx";
      link.download = "附件10.2校服选购问卷调查总结报告（公开）.docx";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
    deleteDC(row) {
      this.$confirm("是否确认删除该调查结果?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        delSummarizeReport(row.id)
          .then((res) => {
            this.$message.success("删除调查结果成功");
            this.getZJList();
          })
          .catch((err) => {
            console.error("删除调查结果失败:", err);
            this.$message.error("删除调查结果失败");
          });
      });
    },
    previewFile(file) {
      var b64Encoded = Base64.encode(file.url);

      window.open(
        process.env.VUE_APP_KKFILE2 + encodeURIComponent(b64Encoded),
        "_blank"
      );

    },
    
    seeYijian(row) {

      this.dialogDetailVisible = true;

      getSummarizeReportDetail(row.id)
        .then((res) => {

          if (res.code === 200 && res.data) {
            const data = res.data;

            // 处理年级班级数据，转换为表格需要的结构
            let gradeClassTableData = [];
            if (
              data.xfptBuyGradeClassList &&
              data.xfptBuyGradeClassList.length > 0
            ) {
              // 先找出所有年级（parentId为0的项）
              const grades = data.xfptBuyGradeClassList.filter(
                (item) => item.parentId === 0 || item.isGrade
              );

              // 为每个年级创建表格行，并添加对应的班级
              grades.forEach((grade) => {
                // 找出属于该年级的班级
                const classes = data.xfptBuyGradeClassList.filter(
                  (item) =>
                    item.parentId === grade.gradeClassId &&
                    (!item.isGrade || item.isGrade === false)
                );

                // 添加年级行
                const gradeRow = {
                  ...grade,
                  id: grade.gradeClassId,
                  gradeClass: grade.gradeClassName,
                  classes: classes.map((cls) => ({
                    id: cls.gradeClassId,
                    gradeClass: cls.gradeClassName,
                    number: cls.amount,
                    selected: true, // 在详情中，视为已选中
                  })),
                };

                gradeClassTableData.push(gradeRow);
              });
            }

            this.detailForm = {
              summarizeReportName: data.summarizeReportName || "",
              summarizeReportType: data.summarizeReportType || "",
              timeRange:
                data.startTime && data.endTime
                  ? `${data.startTime}~${data.endTime}`
                  : "",
              questionnaireName: data.xfptQuestionnaireConfig?.title || "",
              gradeClassList: data.xfptBuyGradeClassList || [],
              schoolGrades: gradeClassTableData, // 表格数据
              expandedRows: gradeClassTableData.map((item) => item.id), // 默认展开所有行
              fileList: [],
              createBy: data.createBy || "",
              createTime: data.createTime || "",
              deptName: data.deptName || "",
              amount: data.amount || 0,
              surveyFileUrl: data.surveyFileUrl,
            };
          } else {
            this.$message.error(res.msg || "获取详情失败");
          }
        })
        .catch((err) => {

          this.$message.error("获取详情失败");
        });
    },
    editSurveyResult() {
      if (!this.currentEditRow) {
        this.$message.error("没有找到要编辑的数据");
        return;
      }

      editQuestionnaireSurvey({
        id: this.currentEditRow.id,
        surveyFileUrl: this.surveyFileUrl,
      }).then((res) => {
        this.$message.success("编辑调查结果成功");
        this.dialogFileInfo = false;
        this.getZJList(); // 刷新列表
        this.$emit("handleGetProcessMenu");
      });
    },
    getZJList() {
      // 构建查询参数
      const params = {
        ...this.searchParams,
      };

      // 移除值为 'all' 的参数
      Object.keys(params).forEach((key) => {
        if (params[key] === "all" || params[key] === "") {
          delete params[key];
        }
      });

      // 处理日期格式
      if (params.contractDeadline) {
        params.contractDeadline = this.formatDate(params.contractDeadline);
      }

      summarizeReportList(params)
        .then((res) => {
  
          if (res.code === 200) {
            this.tableZJ = res.rows || [];
            this.total = res.total || 0;
          } else {
            this.$message.error(res.msg || "获取总结报告列表失败");
            this.tableZJ = [];
            this.total = 0;
          }
        })
        .catch((err) => {

          this.$message.error("获取总结报告列表失败");
          this.tableZJ = [];
          this.total = 0;
        });
    },
    formatDate(date) {
      if (!date) return "";
      const d = new Date(date);
      const year = d.getFullYear();
      const month = String(d.getMonth() + 1).padStart(2, "0");
      const day = String(d.getDate()).padStart(2, "0");
      return `${year}-${month}-${day}`;
    },
    getTotalClassCount(grade) {
      // 确保grade.classes存在
      if (!grade || !grade.classes) {
        return 0;
      }
      return grade.classes.reduce(
        (sum, cls) => sum + (parseInt(cls.number) || 0),
        0
      );
    },
    submitReportInfo() {
      this.$refs.reportForm.validate((valid) => {
        if (!valid) {
          return this.$message.warning("请完善表单信息");
        }

        // 如果是线上调查且没有问卷内容，提示用户
        if (this.form.summarizeReportType === '线上调查' && this.sonList.length == 0) {
          this.$message.error("请完善问卷内容");
          return;
        }



        // 确保teamMeetingId已设置
        if (!this.form.teamMeetingId && this.formId) {
          this.form.teamMeetingId = this.formId;
        }

        // 添加问卷数据到表单
        this.form.sonList = this.sonList;

        addSummarizeReport(this.form)
          .then((res) => {

            if (res.code === 200) {
              this.$message.success("添加总结报告成功");
              this.dialogVisible = false;
              this.getZJList();
              this.$emit('handleGetProcessMenu',res.data)
            } else {
              this.$message.error("添加总结报告失败");
            }
          })
          .catch((err) => {
            this.$message.error("添加总结报告失败");

          });
      });
    },
    getInfo() {
      getInfo().then((res) => {
        this.form.deptId = res.user.dept.deptId;
        this.form.deptName = res.user.dept.deptName;
      });
    },
    // 删除总结报告
    handleDelete(row) {
      this.$confirm("确定删除该总结报告吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        delSummarizeReport(row.id)
          .then((res) => {
            this.$message.success("删除总结报告成功");
            this.getZJList();
          })
          .catch((err) => {
            this.$message.error("删除总结报告失败");
          });
      });
    },
    addQuestionSearch() {
      this.getList();
    },
    addQuestionnaire() {
      this.questionnaireDialogVisible = true;
    },

    // 新增：处理选择问卷变更
    handleSelectionChange(selection) {
      // 限制只能选择一个问卷
      if (selection.length > 1) {
        // 保留最后选择的那个
        const lastSelected = selection[selection.length - 1];
        this.$refs.surveyTable.clearSelection();
        this.$refs.surveyTable.toggleRowSelection(lastSelected, true);
        this.selectedquestionnaireSurveyIds = [lastSelected.id];
      } else {
        this.selectedquestionnaireSurveyIds = selection.map((item) => item.id);
      }
    },

    // 新增：确认问卷选择
    confirmQuestionnaire() {
      if (this.selectedquestionnaireSurveyIds.length === 0) {
        return this.$message.warning("请选择一个问卷");
      }

      // 从表格中找到选中的问卷
      const selectedQuestionnaire = this.surveyList.find(
        (item) => item.id === this.selectedquestionnaireSurveyIds[0]
      );

      if (selectedQuestionnaire) {
        this.selectedQuestionnaire = selectedQuestionnaire;
        this.form.questionnaireSurveyId = selectedQuestionnaire.id;
        this.form.questionnaireName = selectedQuestionnaire.title;
        this.questionnaireDialogVisible = false;
      }
    },

    // 新增：移除已选问卷
    removeQuestionnaire() {
      this.selectedQuestionnaire = null;
      this.form.questionnaireSurveyId = "";
      this.form.questionnaireName = "";
    },

    changeDate(val) {
      if (val && val.length === 2) {
        this.form.startTime = val[0];
        this.form.endTime = val[1];
      } else {
        this.form.startTime = "";
        this.form.endTime = "";
      }
    },
    listQuestionnaire() {
      listQuestionnaire().then((res) => {
        // 处理问卷列表数据
      });
    },
    addZJBG() {
      this.dialogVisible = true;

      // 获取默认时间范围
      const defaultTime = this.getDefaultTimeRange();

      this.form = {
        summarizeReportName: "",
        summarizeReportType: "线上调查",
        contractDeadline: defaultTime.timeRange, // 设置默认时间范围
        questionnaireSurveyId: "",
        questionnaireName: "",
        startTime: defaultTime.startTime, // 设置开始时间
        endTime: defaultTime.endTime, // 设置结束时间
        teamMeetingId: this.formId, // 设置teamMeetingId
        file: [],
        names: "",
        sonList: [], // 初始化问卷列表
        count: null,
      };
      this.selectedQuestionnaire = null;
      this.fileList = [];
      this.fileListPic = [];
      // 重置展开行状态
      this.expandedRows = [];
      this.gradeList();
      this.getList();
      this.getInfo();
      // 初始化问卷数据
      this.sonList = [...this.sonList];
    },
    getList() {
      this.loading = true;
      listSurveyNew().then((response) => {
        this.surveyList = response.data;
        this.loading = false;
      });
    },
    submitReport() {
      this.$refs.reportForm.validate((valid) => {
        if (valid) {
          // 提交表单逻辑


          // 如果是线下调查，添加图片和附件到表单
          if (this.form.summarizeReportType === "线下调查") {



            // 这里可以进一步处理文件上传逻辑
          }

          this.dialogVisible = false;
        }
      });
    },
    editDC(row) {
      this.currentEditRow = row;

      // 获取最新的调查结果文件和图片
      getSummarizeReportDetail(row.id)
        .then((res) => {

          if (res.code === 200 && res.data) {
            const data = res.data;
            this.surveyFileUrl = res.data.surveyFileUrl;
            // 显示对话框
            this.dialogFileInfo = true;
          } else {
            // 仍然尝试使用现有数据
            this.parseExistingFiles(row);
            this.dialogFileInfo = true;
          }
        })
        .catch((err) => {

          // 回退到使用行数据
          this.parseExistingFiles(row);
          this.dialogFileInfo = true;
        });
    },

    // 解析现有文件
    parseExistingFiles(row) {
      // 简化的文件解析逻辑
      if (row.surveyFileUrl) {
        // 简单处理，直接使用URL
      }
    },

    // 下载文件方法
    downloadFile(row) {
      const loading = this.$loading({
        lock: true,
        text: "正在下载文件，请稍候...",
        spinner: "el-icon-loading",
        background: "rgba(0, 0, 0, 0.7)",
      });

      try {
        // 创建FormData对象，使用multipart/form-data格式
        const formData = new FormData();
        formData.append("url", row.url || "");
        formData.append("fileName", row.fileName || "");
        formData.append("reportName", row.reportName || "满意度调查报告");

        // 创建下载链接
        const downloadUrl =
          process.env.VUE_APP_BASE_API +
          "/school/suppliceContractSon/downloadZip";

        // 保存this引用
        const _this = this;

        // 创建请求
        const xhr = new XMLHttpRequest();
        xhr.open("POST", downloadUrl, true);
        xhr.responseType = "blob";
        xhr.setRequestHeader("Authorization", "Bearer " + this.getToken());

        // 处理响应
        xhr.onload = function () {
          loading.close();
          if (this.status === 200) {
            const blob = new Blob([this.response], { type: "application/zip" });
            const link = document.createElement("a");
            link.href = window.URL.createObjectURL(blob);
            link.download = row.fileName || "满意度调查报告.zip";
            link.click();
            window.URL.revokeObjectURL(link.href);
            _this.$message.success("文件下载成功");
          } else {
            _this.$message.error("下载文件失败，请稍后重试");
          }
        };

        // 错误处理
        xhr.onerror = function () {
          loading.close();
          _this.$message.error("下载文件失败，请稍后重试");
        };

        // 发送请求
        xhr.send(formData);
      } catch (error) {

        this.$message.error("下载文件失败，请稍后重试");
        loading.close();
      }
    },

    // 获取Token
    getToken() {
      return this.$store.getters.token;
    },
    gradeList() {
      gradeList({ deptId: this.$store.state.user.deptId })
        .then((res) => {
  

          this.schoolGrades = res.data || [];

          // 确保每个班级对象都有selected属性
          if (this.schoolGrades && this.schoolGrades.length > 0) {
            this.schoolGrades.forEach((grade) => {
              if (grade.classes && grade.classes.length > 0) {
                grade.classes.forEach((cls) => {
                  if (cls.selected === undefined) {
                    this.$set(cls, "selected", false);
                  }
                });
              }
            });
          }

          // 数据加载完成后计算总数
          this.$nextTick(() => {
            this.calculateTotals();
          });
        })
        .catch((error) => {
          console.error("获取年级列表失败:", error);
          this.$message.error("获取年级列表失败");

          this.schoolGrades = [];

          // 即使出错也需要计算总数(清零)
          this.calculateTotals();
        });
    },
    // 新增方法：处理图片上传
    handleImageChange(file, fileList) {
      this.imageFileList = fileList;
    },

    // 新增方法：图片上传前的验证
    beforeImageUpload(file) {
      const isJPG = file.type === "image/jpeg";
      const isPNG = file.type === "image/png";
      const isJPEG = file.type === "image/jpeg";
      const isLt10M = file.size / 1024 / 1024 < 10;

      if (!isJPG && !isPNG && !isJPEG) {
        this.$message.error("上传图片只能是 JPG/JPEG/PNG 格式!");
        return false;
      }
      if (!isLt10M) {
        this.$message.error("上传图片大小不能超过 10MB!");
        return false;
      }
      return true;
    },

    // 新增方法：处理文件上传
    handleFileChange(file, fileList) {
      this.fileList = fileList;
    },

    // 新增方法：文件上传前的验证
    beforeFileUpload(file) {
      const isDoc = file.name.endsWith(".doc");
      const isDocx = file.name.endsWith(".docx");
      const isPdf = file.name.endsWith(".pdf");
      const isXlsx = file.name.endsWith(".xlsx");
      const isXls = file.name.endsWith(".xls");
      const isLt20M = file.size / 1024 / 1024 < 20;

      if (!isDoc && !isDocx && !isPdf && !isXlsx && !isXls) {
        this.$message.error("上传附件只能是 DOC/DOCX/PDF/XLSX/XLS 格式!");
        return false;
      }
      if (!isLt20M) {
        this.$message.error("上传附件大小不能超过 20MB!");
        return false;
      }
      return true;
    },

    // 获取默认的时间范围（当前时间到一周后）
    getDefaultTimeRange() {
      const now = new Date();
      const oneWeekLater = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000);

      // 格式化时间为 yyyy-MM-dd HH:mm:ss 格式
      const formatDateTime = (date) => {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, "0");
        const day = String(date.getDate()).padStart(2, "0");
        const hours = String(date.getHours()).padStart(2, "0");
        const minutes = String(date.getMinutes()).padStart(2, "0");
        const seconds = String(date.getSeconds()).padStart(2, "0");
        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
      };

      const startTime = formatDateTime(now);
      const endTime = formatDateTime(oneWeekLater);

      return { startTime, endTime, timeRange: [startTime, endTime] };
    },

    // 关闭对话框时重置表单
    closeDialog() {
      const defaultTime = this.getDefaultTimeRange();

      this.form.summarizeReportType = "线上调查";
      this.form.summarizeReportName = "";
      this.form.contractDeadline = defaultTime.timeRange; // 设置默认时间范围
      this.form.startTime = defaultTime.startTime;
      this.form.endTime = defaultTime.endTime;
      this.selectedQuestionnaire = null;
      this.form.questionnaireSurveyId = "";
      this.form.questionnaireName = "";
      this.imageFileList = [];
      this.fileList = [];
      this.fileListPic = [];
      this.dialogVisible = false;
      this.questionnaireDialogVisible = false;
      // 重置问卷数据
      this.sonList = [];
    },

    // 直接点击单选按钮时的处理方法
    selectReportType(type) {

      this.form.summarizeReportType = type;
      this.$forceUpdate(); // 强制更新视图
    },

    calculateTotal() {
      // 计算总人数
      this.totalStudents = 0;
      if (this.gradeData && this.gradeData.length > 0) {
        this.gradeData.forEach((grade) => {
          if (grade.studentCount) {
            this.totalStudents += parseInt(grade.studentCount);
          }
        });
      }
    },

    showClassManageDialog() {
      // 显示班级管理对话框
      this.classManageVisible = true;

    },

    // 文件上传成功的回调
    handleUploadSuccess(response, file, fileList) {
      this.fileList = fileList;
      this.$message.success("文件上传成功");

    },

    // 图片上传成功的回调
    handlePictureSuccess(response, file, fileList) {
      this.fileListPic = fileList;
      this.$message.success("图片上传成功");

    },

    // 移除图片
    handleRemove(file, fileList) {
      this.fileListPic = fileList;

    },

    // 移除文件
    handleRemoveFile(file, fileList) {
      this.fileList = fileList;

    },

    // 预览图片
    handlePictureCardPreview(file) {
      let previewUrl = file.url || file.response?.data?.url;

      // 确保URL是完整路径
      if (
        previewUrl &&
        !previewUrl.startsWith("http") &&
        !previewUrl.startsWith("https")
      ) {
        previewUrl = process.env.VUE_APP_IMAGE_BASE_API + previewUrl;
      }

      this.dialogImageUrl = previewUrl;
      this.dialogVisibleImage = true;
    },

    addClassDirectly(grade) {
      if (!grade.classes) {
        grade.classes = [];
      }
      grade.classes.push({
        gradeClass: "",
        number: "",
        selected: false,
      });
    },

    // 获取所有选中的班级
    getSelectedClasses() {
      let xfptBuyGradeClassList = [];
      if (this.schoolGrades && this.schoolGrades.length > 0) {
        this.schoolGrades.forEach((grade) => {
          if (grade.classes && grade.classes.length > 0) {
            const selectedInGrade = grade.classes.filter((cls) => cls.selected);
            if (selectedInGrade.length > 0) {
              // 添加年级数据，格式与班级一致
              xfptBuyGradeClassList.push({
                gradeClassId: grade.id || "",
                gradeClassName: grade.gradeClass || "",
                amount: 0, // 年级人数设为0，避免重复计算
                parentId: 0, // 年级作为顶级，parentId设为0
                isGrade: true, // 标记为年级
              });

              // 添加班级数据
              xfptBuyGradeClassList = [
                ...xfptBuyGradeClassList,
                ...selectedInGrade.map((cls) => ({
                  gradeClassId: cls.id || "",
                  gradeClassName: cls.gradeClass || "",
                  amount: Number(cls.number) || 0,
                  parentId: grade.id || "",
                  isGrade: false, // 标记为班级
                })),
              ];
            }
          }
        });
      }
      return xfptBuyGradeClassList;
    },

    // 获取所选班级数量和人数
    getSelectedClassesCount() {
      // 只统计班级，不统计年级
      const selectedClasses = this.getSelectedClasses().filter(
        (item) => !item.isGrade
      );
      const count = selectedClasses.length;
      const studentCount = selectedClasses.reduce(
        (sum, cls) => sum + (parseInt(cls.amount) || 0),
        0
      );
      return { count, studentCount };
    },

    // 处理班级选择状态变化
    handleClassSelectionChange(value) {
      // 重新计算已选班级数量
      this.$emit("class-selection-change", this.getSelectedClasses());

      // 更新年级的选择状态
      this.updateGradeSelectionState();
      // 强制更新视图
      this.$forceUpdate();
    },

    // 更新年级的选择状态
    updateGradeSelectionState() {
      // 现在不再需要通过方法修改年级勾选状态，因为年级勾选状态由班级全选状态决定
      // 只需要触发界面更新
      this.$forceUpdate();
    },

    // 处理展开行变化
    handleExpandChange(row, expanded) {
      console.log("展开状态变化:", row.id, expanded);

      if (expanded) {
        // 添加到展开行数组
        if (!this.expandedRows.includes(row.id)) {
          this.expandedRows.push(row.id);
        }
      } else {
        // 从展开行数组中移除
        const index = this.expandedRows.indexOf(row.id);
        if (index !== -1) {
          this.expandedRows.splice(index, 1);
        }
      }
      console.log("当前展开行:", this.expandedRows);
    },

    // 删除班级
    deleteClass(grade, index) {
      if (grade.classes && grade.classes.length > 1) {
        grade.classes.splice(index, 1);
        this.$message.success("删除班级成功");
      } else {
        this.$message.warning("至少需要保留一个班级");
      }
    },

    // 删除年级
    deleteGrade(index) {
      this.schoolGrades.splice(index, 1);
      this.$message.success("删除年级成功");
    },

    // 计算总数
    calculateTotals() {
      let totalStudents = 0;

      // 计算所有年级班级的总人数
      if (this.schoolGrades && this.schoolGrades.length > 0) {
        this.schoolGrades.forEach((grade) => {
          if (grade.classes && grade.classes.length > 0) {
            grade.classes.forEach((cls) => {
              const count = parseInt(cls.number) || 0;
              totalStudents += count;
            });
          }
        });
      }

      this.totalStudents = totalStudents;
    },

    // 检查年级下所有班级是否全部被选中
    checkAllClassesSelected(grade) {
      if (!grade.classes || grade.classes.length === 0) {
        return false;
      }
      return grade.classes.every((cls) => cls.selected);
    },

    // 切换年级选择状态
    toggleGradeSelection(grade, isSelected) {
      if (grade.classes && grade.classes.length > 0) {
        grade.classes.forEach((cls) => {
          cls.selected = isSelected;
        });
      }
      // 触发班级选择变化事件
      this.$emit("class-selection-change", this.getSelectedClasses());
      // 更新显示
      this.$forceUpdate();
    },

    // 关闭详情对话框时重置表单
    closeDetailDialog() {
      this.dialogDetailVisible = false;
    },

    // 计算详情中的总人数
    getTotalDetailStudents() {
      if (
        !this.detailForm.gradeClassList ||
        this.detailForm.gradeClassList.length === 0
      ) {
        return 0;
      }
      return this.detailForm.gradeClassList.reduce(
        (sum, cls) => sum + (parseInt(cls.amount) || 0),
        0
      );
    },

    // 判断是否为图片文件
    isImageFile(fileName) {
      return /\.(jpg|jpeg|png|gif)$/i.test(fileName);
    },

    // 预览图片
    previewImage(file) {
      let previewUrl = file.url;

      // 确保URL是完整路径
      if (
        previewUrl &&
        !previewUrl.startsWith("http") &&
        !previewUrl.startsWith("https")
      ) {
        previewUrl = process.env.VUE_APP_IMAGE_BASE_API + previewUrl;
      }

      this.dialogImageUrl = previewUrl;
      this.dialogVisibleImage = true;
    },

    // 下载详情页面的文件
    downloadDetailFile(file) {
      if (!file || !file.url) {
        return this.$message.error("文件地址不存在");
      }

      const loading = this.$loading({
        lock: true,
        text: "正在准备下载文件...",
        spinner: "el-icon-loading",
        background: "rgba(0, 0, 0, 0.7)",
      });

      try {
        // 确保URL是完整路径
        let downloadUrl = file.url;
        if (
          !downloadUrl.startsWith("http") &&
          !downloadUrl.startsWith("https")
        ) {
          downloadUrl = process.env.VUE_APP_IMAGE_BASE_API + downloadUrl;
        }

        window.open(downloadUrl);
        loading.close();
      } catch (error) {

        this.$message.error("下载文件失败，请稍后重试");
        loading.close();
      }
    },

    // 搜索方法
    handleSearch() {
      this.searchParams.pageNum = 1; // 重置页码
      this.getZJList();
    },

    // 重置搜索
    resetSearch() {
      this.searchParams = {
        summarizeReportName: "",
        summarizeReportType: "all",
        contractDeadline: "",
        summarizeReportStatus: "all",
        pageNum: 1,
        teamMeetingId: this.formId,
        pageSize: 10,
      };
      this.getZJList();
    },

    // 计算年级下所有班级的总人数
    getGradeTotal(grade) {
      if (!grade.children || grade.children.length === 0) {
        return 0;
      }
      return grade.children.reduce(
        (sum, cls) => sum + (parseInt(cls.amount) || 0),
        0
      );
    },

    // 开关所有行的展开/折叠状态
    toggleAllRowsExpand() {
      if (this.expandedRows.length > 0) {
        // 如果有展开的行，则全部折叠
        this.expandedRows = [];
      } else {
        // 如果没有展开的行，则全部展开
        this.expandedRows = this.schoolGrades.map((item) => item.id);
      }
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
        process.env.VUE_APP_QR_CODE_URL + `?id=${row.questionnaireSurveyId}`;

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
                下载二维码
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
            this.$alert(content, `${row.summarizeReportName}`, {
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
          console.error(err);
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
      getSurveyStatistics({ id: row.questionnaireSurveyId }).then((res) => {
        this.statisticsVisible = true;
        this.selectedSurveyId = row.questionnaireSurveyId;
      });
    },
    // 新增：人员名单上传成功
    handlePersonSuccess(response, file, fileList) {
      this.form.file = fileList;
      this.form.names = response.msg; // 假设后端返回名单内容在msg字段
      this.form.count = response.msg.split(',').length;
    },
    // 新增：人员名单移除
    handlePersonRemove(file, fileList) {
      this.form.file = fileList;
      this.form.names = "";
    },
    // 新增：人员名单上传失败
    handlePersonError(err, file, fileList) {
      this.$message.error("上传失败");
    },
    // 新增：下载人员名单模板
    downloadPersonTemplate() {
      const link = document.createElement("a");
      link.href =
        process.env.VUE_APP_IMAGE_BASE_API +
        "/filestatics/download/姓名模板.xlsx";
      link.download = "姓名模板.xlsx";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
    // 投票名单弹窗
    handleSignature(row) {
      this.signatureQueryParams.summarizeReportId = row.questionnaireSurveyId;
      this.getZiduanList();
      this.getSignatureList();
      this.signatureDialog = true;
    },
    //导出问卷结果表格
    handleExport() {
      this.download(
        "/school/questionnaireSurvey/exportResultExcel",
        {
          questionId: this.signatureQueryParams.summarizeReportId
        },
        `问卷结果_${new Date().getTime()}.xlsx`
      );
    },
    //获取表头
    getZiduanList() {
      getZiduanList( this.signatureQueryParams.summarizeReportId).then((response) => {
        this.ziduanList = response.data
      });
    },
    // 获取投票名单列表
    getSignatureList() {
      this.signatureLoading = true;
      const params = {
        ...this.signatureQueryParams,
        questId: this.signatureQueryParams.summarizeReportId,
      };
      getVoteSignatureList(params)
        .then((response) => {
          this.signatureList = response.rows || response.data || [];
          this.signatureTotal = response.total || 0;
          this.signatureLoading = false;
        })
        .catch(() => {
          this.signatureLoading = false;
          this.signatureList = [];
          this.signatureTotal = 0;
          this.$message.error("获取投票名单失败");
        });
    },
    // 预览签名图片
    previewSignature(url) {
      this.signaturePreviewUrl = url;
      this.signaturePreviewDialog = true;
    },
    // 获取问卷配置
    getConfigByType() {
      getConfigByType(2).then((response) => {
        this.sonList = response.data.sonList || [];
      });
    },
    // 添加问卷内容
    handleAddQuestionnaire() {
      this.wjbj = true;
      this.$nextTick(() => {
        this.$refs.wjBianjiRef.showQuestionnaire();
      });
    },
    // 编辑问卷内容
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
      this.sonList = data.map(item => {
        return {
          surveyContent: item.config.surveyContent,
          answeringMethod: item.config.answeringMethod,
          isRequired: item.config.isRequired,
          sm: item.config.sm,
          surveyResult: item.config.answeringMethod == 0 || item.config.answeringMethod == 1 ? item.config.options.join('、') : '',
          id: item.config.id
        }
      });
      this.form.sonList = [...this.sonList]
      this.wjbj = false;
      this.$message.success("问卷保存成功");
    },
    // 线下调查详情
    handleOfflineDetail(row) {
      this.offlineDetailDialog = true;
      getSummarizeReportDetail(row.id)
        .then((response) => {
          if (response.code === 200 && response.data) {
            this.offlineDetail = response.data;
          } else {
            this.$message.error(response.msg || "获取详情失败");
            this.offlineDetail = {};
          }
        })
        .catch((err) => {
          this.$message.error("获取详情失败");
          this.offlineDetail = {};
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

.survey-questions {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 10px;
  max-height: 300px;
  overflow-y: auto;
}

.question-item {
  margin-bottom: 15px;
}

.question {
  font-weight: bold;
  margin-bottom: 5px;
}

.options {
  display: flex;
  flex-wrap: wrap;
}

.option {
  margin-right: 20px;
  margin-bottom: 5px;
}

.grade-summary {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  color: #606266;
}

.update-tip {
  color: #409eff;
  cursor: pointer;
}

.expandable-label {
  cursor: pointer;
  display: flex;
  align-items: center;
}

.class-list {
  padding-left: 30px;
  margin-bottom: 10px;
}

.child-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.student-number {
  margin-right: 20px;
}

.class-manage-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.class-manage-expanded {
  padding: 10px 20px;
  background-color: #f5f7fa;
  border-left: 4px solid #ebeef5;
  margin: 0 20px;
}

.class-header,
.class-row {
  display: grid;
  grid-template-columns: 3fr 2fr;
  gap: 10px;
  margin-bottom: 10px;
  align-items: center;
}

.class-header {
  font-weight: bold;
  color: #909399;
  font-size: 13px;
  margin-bottom: 15px;
  border-bottom: 1px dashed #d4d7de;
  padding-bottom: 8px;
}

.class-row {
  margin-bottom: 12px;
}

.class-row:hover {
  background-color: #f0f2f7;
  border-radius: 4px;
}

.class-name,
.class-alias,
.class-number {
  padding: 5px;
}

.add-class-row {
  margin-top: 15px;
  text-align: center;
}

.required-field {
  color: #f56c6c;
  margin-right: 3px;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

/* 新增样式 */
.el-upload__tip {
  font-size: 12px;
  color: #606266;
  margin-top: 5px;
}

/* 自定义单选框样式 */
.custom-radio-group {
  display: flex;
  gap: 20px;
}

.custom-radio {
  display: flex;
  align-items: center;
  cursor: pointer;
  user-select: none;
}

.radio-circle {
  display: inline-block;
  width: 14px;
  height: 14px;
  border-radius: 50%;
  border: 1px solid #dcdfe6;
  margin-right: 8px;
  position: relative;
}

.is-active .radio-circle:after {
  content: "";
  position: absolute;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background-color: #409eff;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.is-active .radio-circle {
  border-color: #409eff;
}

.radio-label {
  color: #606266;
}

.is-active .radio-label {
  color: #409eff;
}

.class-selection {
  display: flex;
  align-items: center;
  justify-content: center;
}

.class-name-flex {
  display: flex;
  align-items: center;
}

.class-number {
  padding: 5px;
  color: #606266;
  font-size: 14px;
}

.class-count-info {
  text-align: center;
  color: #606266;
  font-size: 14px;
}

/* 已选问卷样式 */
.selected-questionnaire {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #f5f7fa;
  padding: 8px 15px;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
  width: 100%;
  max-width: 600px;
}

.questionnaire-name {
  color: #606266;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 15px;
}

/* 文件显示样式 */
.file-item {
  margin: 10px;
  display: inline-block;
  vertical-align: top;
}

.image-preview {
  width: 100px;
  height: 100px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.document-item {
  padding: 2px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  /* align-items: center; */
  width: 100px;
  height: 40px;
  cursor: pointer;
}

.document-item i {
  font-size: 36px;
  margin-bottom: 5px;
  /* color: #909399; */
}

.file-name {
  font-size: 12px;
  color: #606266;
  text-align: center;
  word-break: break-all;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
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

/* 问卷相关样式 */
.table-operations {
  margin-bottom: 10px;
}

.questionnaire-preview {
  margin-top: 10px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 15px;
  background-color: #fafafa;
}

.question-item {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #fff;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.question-item:last-child {
  margin-bottom: 0;
}

.question-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  font-weight: 500;
  color: #303133;
}

.question-number {
  color: #409eff;
  font-weight: bold;
  margin-right: 8px;
}

.question-title {
  flex: 1;
}

.required-mark {
  color: #f56c6c;
  margin-left: 4px;
  font-weight: bold;
}

.question-content {
  margin-left: 20px;
}

.options-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.option-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
}

.option-item i {
  color: #409eff;
  font-size: 16px;
}

.text-input {
  margin-top: 8px;
}
</style>
