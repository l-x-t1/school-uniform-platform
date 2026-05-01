<template>
  <div class="detail-panel">
    <div class="header" style="display: flex; align-items: center">
      <h3>家长购买意向征集管理</h3>
      <div style="margin-left: 20px">
        <el-button
          type="primary"
          v-hasPermi="['xfpt:jzgmyjzj-add']"
          icon="el-icon-plus"
          @click="handleAdd" v-if="opinionsList.length == 0"
          >添加家长购买意见征集活动</el-button
        >
      </div>
    </div>


    <el-table
      v-loading="loading"
      :data="opinionsList"
    >
      <el-table-column label="序号" align="center" type="index" />
      <el-table-column label="意见征集标题" align="center" prop="title" /> 
      <el-table-column label="意见征集类型" align="center" prop="zjlx">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sc_yjzhlx" :value="scope.row.zjlx" />
        </template>
      </el-table-column>
      <el-table-column label="意见征集人数" align="center" prop="votedCount" >
        <template slot-scope="scope">
          <span>{{scope.row.zjlx== 0 ? scope.row.votedCount : null}}</span> 
        </template>
      </el-table-column> 
      <el-table-column label="总人数" align="center" prop="count" >
        <template slot-scope="scope">
          <span>{{ scope.row.zjlx== 0 ? scope.row.count : null}}</span>
        </template>
      </el-table-column>
      <el-table-column label="投票率" align="center" prop="votingRate">
        <template slot-scope="scope">
          <span>{{scope.row.zjlx== 0 ? scope.row.votingRate+'%' : null}}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        width="180"
      >
      </el-table-column>
      <el-table-column label="意见征集情况" align="center" prop="yjzjqk">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sc_yjzjqk" :value="scope.row.yjzjqk" />
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <el-button
            v-hasPermi="['xfpt:jzgmyjzj-look']"
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handelViewDetails(scope.row)"
            >查看</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            v-hasPermi="['xfpt:jzgmyjzj-edit']"
            @click="handleUpdate(scope.row)"
            >修改</el-button
          >
          <el-dropdown>
            <el-button type="text" size="mini">
              更多<i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
            
            <el-dropdown-item v-if="scope.row.zjlx == 1"  >
              <el-button 
                type="text"
                size="mini"
                icon="el-icon-edit"
                v-hasPermi="['xfpt:jzgmyjzj-bjzjjg']"
                @click="handleEditResult(scope.row)"
                >编辑征集结果</el-button
              >
            </el-dropdown-item>
            <el-dropdown-item v-if="scope.row.zjlx == 0">
              <el-button 
                size="mini"
                type="text"
                icon="el-icon-view"
                @click="handleQrCode(scope.row)"
                >二维码</el-button
              >
            </el-dropdown-item>
            
            <el-dropdown-item v-if="scope.row.zjlx == 0">
              <el-button
                
                size="mini"
                type="text"
                icon="el-icon-view"
                @click="handleSignature(scope.row)"
                >投票名单</el-button
              >
            </el-dropdown-item>
            <el-dropdown-item v-if="scope.row.zjlx == 0">
              <el-button 
                size="mini"
                type="text"
                icon="el-icon-view"
                @click="handleStatistics(scope.row)"
                >统计</el-button
              >
            </el-dropdown-item>
            <el-dropdown-item v-hasPermi="['xfpt:jzgmyjzj-remove']">
              <el-button
                size="mini"
                type="text"
                icon="el-icon-delete"
                
                @click="handleDelete(scope.row)"
                >删除</el-button
              >
              </el-dropdown-item>
            </el-dropdown-menu> 
          </el-dropdown> 
        </template>
      </el-table-column>
    </el-table> 

    <!-- 添加或修改家长购买意见征集对话框 -->
    <el-dialog
      :title="title"
      :visible.sync="open"
      width="1100px"
      append-to-body
    >
      <el-form ref="form" :model="form" :rules="rules" label-width="140px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="意见征集标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入意见征集标题" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="意见征集类型" prop="zjlx">
              <el-select
                :disabled="isEdit"
                style="width: 100%"
                placeholder="请选择意见征集类型"
                v-model="form.zjlx"
                @change="handleChangeZjlx"
              >
                <el-option
                  v-for="dict of dict.type.sc_yjzhlx"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24" v-if="form.zjlx == 1">
            <el-form-item label="附件" prop="fj">
              <FileUpload
                :fileType="['pdf']"
                v-model="form.fj"
                :gaizhang="true"
              >
                <el-button slot="template" type="text" @click.stop="importTemplate"
                  >下载模板</el-button
                >
              </FileUpload>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.zjlx == 0">
          <el-col :span="24">
            <el-form-item label="意见征集内容" prop="configContent"> 

              <div style="margin: 10px 0">
                <editor 
                  v-model="form.configContent"
                  :min-height="192"
                />
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="form.zjlx == 0">
            <el-form-item label="人员名单上传" prop="ryfile">
              <el-upload
                class="upload-demo"
                :action="uploadUrl" 
                :on-remove="handleRemove"
                :on-success="handleSuccess"
                :on-error="handleError"
                :limit="1"
                :headers="headers"
                accept=".xls,.xlsx"
                :file-list="form.ryfile"
                ref="uploadRef"
                :on-exceed="handleExceed"
              >
                <el-button size="small" type="primary">点击上传</el-button>
                <el-button size="small" type="text" @click.stop="downLoadRenyuan"
                  >下载模板</el-button
                >
                <div slot="tip" class="el-upload__tip">
                  只能上传xls/xlsx文件
                </div>
              </el-upload>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="form.zjlx == 0">
            <el-form-item label="人员名单">
              <el-input
                v-model="form.names"
                type="textarea"
                :rows="3"
                readonly
                placeholder="上传人员名单后自动生成"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="form.zjlx == 0">
            <el-form-item label="问卷内容">
              <div class="table-operations">
                <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAddQuestionnaire" v-if="sonList.length == 0">添加问卷</el-button>
                <el-button type="warning" icon="el-icon-edit" size="mini" @click="handleEditQuestionnaire" v-else>编辑问卷</el-button>
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item
              v-if="form.zjlx == 0"
              label="投票起止时间"
              prop="tpqzsj"
            >
              <el-date-picker
                v-model="form.tpqzsj"
                type="datetimerange"
                value-format="yyyy-MM-dd HH:mm:ss"
                format="yyyy-MM-dd HH:mm:ss"
                range-separator="至"
                start-placeholder="开始日期时间"
                end-placeholder="结束日期时间"
                :picker-options="{
                  format: 'yyyy-MM-dd HH:mm:ss',
                }"
                style="width: 100%"
              >
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 选择内容模板对话框 -->
    <el-dialog
      title="选择内容模板"
      :visible.sync="templateDialog"
      width="800px"
      append-to-body
    >
      <el-form label-width="100px">
        <el-form-item label="模板标题：">
          <el-select v-model="templateType" placeholder="请选择模板标题">
            <el-option
              v-for="item of configList"
              :key="item.id"
              :label="item.category"
              :value="item.category"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="模板内容：">
          <editor v-model="configContent" :min-height="192" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="confirmTemplate">确 定</el-button>
        <el-button @click="templateDialog = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 编辑征集结果对话框 -->
    <el-dialog
      title="编辑征集结果"
      :visible.sync="resultDialog"
      width="800px"
      append-to-body
    >
      <el-form
        ref="resultForm"
        :model="resultForm"
        :rules="resultFormRules"
        label-width="100px"
      >
        <el-form-item label="附件" prop="fj">
          <FileUpload
            :fileType="[
              'pdf',
              'doc',
              'docx',
              'xls',
              'xlsx',
              'ppt',
              'pptx',
              'jpg',
              'jpeg',
              'png',
            ]"
            v-model="resultForm.fj"
          >
            <el-button slot="template" type="text" @click.stop="importTemplate2"
              >下载模板</el-button
            >
          </FileUpload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitResultForm">确 定</el-button>
        <el-button @click="cancelResult">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 查看意见征集结果对话框 -->
    <el-dialog
      title="查看意见征集结果"
      :visible.sync="viewDetilsDialog"
      width="1100px"
      append-to-body
    >
      <div class="view-details-content">
        <el-descriptions title="基本信息" :column="2" border>
          <el-descriptions-item label="意见征集标题">
            {{ viewForm.title }}
          </el-descriptions-item>
          <el-descriptions-item label="意见征集类型">
            <dict-tag :options="dict.type.sc_yjzhlx" :value="viewForm.zjlx" />
          </el-descriptions-item> 
        </el-descriptions>

        <template v-if="viewForm.configContent && viewForm.zjlx == 0">
          <div class="section-divider">
            <span class="section-title">意见征集内容</span>
          </div>
          <div class="content-box" v-html="viewForm.configContent"></div>
        </template>

        <template v-if="viewForm.zjlx == 0 && viewForm.fj">
          <div class="section-divider">
            <span class="section-title">人员名单</span>
          </div>
          <FileUpload v-model="viewForm.fj" disabled />
        </template>
        <template v-if="viewForm.zjlx == 1 && viewForm.fj">
          <div class="section-divider">
            <span class="section-title">征集结果</span>
          </div>
          <FileUpload v-model="viewForm.fj" disabled />
        </template>
      </div> 
    </el-dialog>
    <!-- 问卷统计弹窗 -->
    <survey-statistics
      :visible.sync="statisticsVisible"
      :selectedSurveyId="selectedSurveyId"
    />

    <!-- 投票名单弹窗 -->
    <el-dialog
      title="投票名单"
      :visible.sync="signatureDialog"
      width="1200px"
      append-to-body
    >
      <!-- 搜索表单 -->
      <el-form
        :model="signatureQueryParams"
        ref="signatureQueryForm"
        size="small"
        :inline="true"
        label-width="80px"
        style="margin-bottom: 15px"
      >
      </el-form>

      <el-table
        v-loading="signatureLoading"
        :data="signatureList"
        border
        height="350"
      > 
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
      <pagination
        v-show="signatureTotal > 0"
        :total="signatureTotal"
        :page.sync="signatureQueryParams.pageNum"
        :limit.sync="signatureQueryParams.pageSize"
        @pagination="getSignatureList"
        style="margin-top: 15px"
      />

      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="handleExport">导出投票结果</el-button>
      </div>
    </el-dialog>

    <!-- 签名预览对话框 -->
    <el-dialog
      title="家长签名预览"
      :visible.sync="signaturePreviewDialog"
      width="600px"
      append-to-body
      center
    >
      <div class="signature-preview-container">
        <img
          :src="signaturePreviewUrl"
          class="signature-preview-image"
          alt="家长签名预览"
        />
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="signaturePreviewDialog = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 问卷编辑对话框 -->
    <el-dialog :visible.sync="wjbj" fullscreen append-to-body @close="wjClose">
      <wjBianji :visible.sync="wjbj" ref="wjBianjiRef" :sonList="sonList" @save="wjSave" />
    </el-dialog>
  </div>
</template>

<script>
import {
  listOpinions,
  getOpinions,
  delOpinions,
  addOpinions,
  updateOpinions,
  getVoteSignatureList,
} from "@/api/cg/opinions";
import { getTeamXGCQ , getConfigByType , getOpinionsMoban , getZiduanList } from "@/api/cg/index";
import { listConfig, getClassesList } from "@/api/cg/config";

import { getCurrSchoolGradeList } from "@/api/cg/index";
import { getSurveyStatistics } from "@/api/school/survey";
import QRCode from "qrcode";
import SurveyStatistics from "@/views/wjdc/tongji";
import { getToken } from "@/utils/auth";
import wjBianji from "@/views/wjdc/wjBianji";
export default {
  name: "Opinions",
  dicts: ["sc_yjzhlx", "sc_yjzjqk", "sc_dble", "xfpl"],
  components: {
    SurveyStatistics,
    wjBianji,
  },
  data() {
    return {
      uploadUrl:
        process.env.VUE_APP_BASE_API + "/school/team/meeting/importUserName", // 上传文件服务器地址
      headers: {
        Authorization: "Bearer " + getToken(),
      },
      viewDetilsDialog: false,
      templateType: "",
      configContent: "",
      templateDialog: false,

      // 遮罩层
      loading: true,

      // 总条数
      total: 0,
      // 家长购买意见征集表格数据
      opinionsList: [],
      // 弹出层标题
      title: "",
      resultFormRules: {
        fj: [
          { required: true, message: "征集结果附件不能为空", trigger: "blur" },
        ],
      },
      isEdit: false,
      // 是否显示弹出层
      open: false,

      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        teamMeetingId: null,
        title: null,
        zjlx: null,
        xzrq: null,
        yjzjqk: null,
        zw: null,
        dblb: null,
        xm: null,
        tpqzsj: null,
        txxx: null,
      },
      // 表单参数
      form: {
        sonList: [], // 问卷内容列表
      },
      // 问卷配置列表
      configList: [],
      sonList: [],
      yjzjMoban:null,
      // 表单校验
      rules: {
        ryfile: [
          { required: true, message: "请上传人员名单", trigger: "blur" },
        ],
        title: [
          { required: true, message: "意见征集标题不能为空", trigger: "blur" },
        ],
        zjlx: [
          { required: true, message: "意见征集类型不能为空", trigger: "blur" },
        ],
        xzrq: [
          { required: true, message: "选择日期不能为空", trigger: "blur" },
        ],
        yjzjqk: [
          { required: true, message: "意见征集情况不能为空", trigger: "blur" },
        ],
        tpqzsj: [
          { required: true, message: "投票起止时间不能为空", trigger: "blur" },
        ],
        fwnx: [
          {
            required: true,
            message: "供应厂商服务年限不能为空",
            trigger: "blur",
          },
        ],
        txxx: [
          {
            required: true,
            message: "投票人需填写信息不能为空",
            trigger: "blur",
          },
        ],
        configContent: [
          {
            required: true,
            message: "意见征集内容不能为空",
            trigger: "blur",
          },
        ],
        questionConfigId: [
          {
            required: true,
            message: "问卷模板不能为空",
            trigger: "blur",
          },
        ],
        fj: [
          { required: true, message: "附件不能为空", trigger: "blur" },
        ],
      },
      teamXGCQ: [],
      classesList: [],

      resultDialog: false,
      resultForm: {
        id: null,
        resultFile: null,
      },
      viewForm: {
        title: "",
        zjlx: "",
        configContent: "",
        fj: "",
        fwnx: "",
        Proposed: [],
        sonDataList: [
          {
            cgnj: "",
            xfpl: "",
            xsngmts: "",
            jgqj: "",
            bz: "",
          },
        ],
      },

      // 统计相关数据
      statisticsVisible: false,
      selectedSurveyId: null,
      cgnj: null,
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
      wjbj: false, // 问卷编辑对话框的可见性
      wjBianjiRef: null, // wjBianji 组件的引用
      ziduanList: [],//表头
    };
  },
  props: {
    formId: {
      type: String,
      required: true,
    },
  },
  created() {
    this.getConfigByType();
    this.getOpinionsMoban();
    this.getList();
    this.getConfigList();
    this.handleGetClassesList();
    this.handlegetCurrSchoolGradeList();
    
    getTeamXGCQ(this.formId).then((response) => {
      this.cgnj = response.data[0].cgnj;
    });
  },
  computed: {},
  watch: {
    templateType: {
      handler(newVal) {
        this.configContent = this.configList.find(
          (item) => item.category === newVal
        ).content;
      },
    },
  },
  methods: {
    //获取意见征集模板
    getOpinionsMoban() {
      getOpinionsMoban().then((response) => {
        this.yjzjMoban = response.data.content; 
      });
    },
    //获取意见征集问卷
    getConfigByType() {
      getConfigByType(1).then((response) => {
        this.sonList = response.data.sonList; 
      });
    },
    downLoadRenyuan() {
      const link = document.createElement("a");
      link.href =
        process.env.VUE_APP_IMAGE_BASE_API + "/filestatics/download/姓名模板.xlsx";
      link.download = "姓名模板.xlsx";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
    importTemplate2() {
      const link = document.createElement("a");
      link.href =
        process.env.VUE_APP_IMAGE_BASE_API +
        "/filestatics/download/附件2.2xx学校校服选购家长意见征集结果（公开）.docx";
      link.download = "校校服选购家长意见征集结果（公开）.docx";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
    importTemplate() {
      const link = document.createElement("a");
      link.href =
        process.env.VUE_APP_IMAGE_BASE_API +
        "/filestatics/download/附件2.1家长意见征集模板.docx";
      link.download = "家长意见征集模板.docx";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
    // 获取班子会议选购需求
    handlegetCurrSchoolGradeList() {
      getCurrSchoolGradeList().then((response) => {
        this.teamXGCQ = response.data;

      });
    },
    handleChangeCgnj(row, index) {
      this.form.sonDataList[index].cgnj = this.teamXGCQ.find(
        (item) => item.id == row.cgnj
      ).gradeClass;
      
    },

    //查看意见征集结果
    handelViewDetails(row) {
      // 先清空之前的数据
            this.viewForm = {
        title: "",
        zjlx: "",
        configContent: "",
        fj: "",
        fwnx: "",
        Proposed: [],
        sonDataList: [],
      };

      // 获取最新数据
      getOpinions(row.id).then((response) => {
        this.viewForm = this.responseParseForm(response.data);
        this.viewDetilsDialog = true;
      });
    },
    //编辑意见征集结果
    handleEditResult(row) {
      getOpinions(row.id).then((response) => {
        this.resultForm = response.data;
        this.resultDialog = true;
      });
    }, 
    //获取班级列表
    handleGetClassesList() {
      return new Promise((resolve) => {
        getClassesList().then((response) => {
          this.classesList = response.data;
          resolve(this.classesList);
        });
      });
    },

    confirmTemplate() {
      this.templateDialog = false;
      this.form.templateId = this.configList.find(
        (item) => item.category == this.templateType
      ).id;
      this.form.configContent = this.configContent;
    },
    getConfigList() {
      listConfig().then((response) => {
        this.configList = response.rows;
      });
    },

    responseParseForm(formResponse) {
      const parseForm = {
        ...formResponse,
        tpqzsj: formResponse.tpqzsj && formResponse.tpqzsj.split("#"),
        Proposed: [
          {
            zw: formResponse.zw,
            dblb: formResponse.dblb,
            xm: formResponse.xm,
          },
        ],
      };
      return parseForm;
    },
    /** 查询家长购买意见征集列表 */
    getList() {
      this.loading = true;
      listOpinions({ ...this.queryParams, teamMeetingId: this.formId }).then(
        (response) => {
          this.opinionsList = response.rows;
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
        teamMeetingId: this.formId,
        count:null,
        title: null,
        zjlx: null,
        xzrq: null,
        yjzjqk: null,
        zw: null,
        dblb: null,
        xm: null,
        tpqzsj: null,
        txxx: null,
        createTime: null,
        createBy: null,
        updateBy: null,
        updateTime: null,
        delFlag: null,
        remark: null,
        fwnx: null,
        templateId: null,
        fj: null,
        configContent: null,
        templateId: null,
        sonDataList: [],
        contentList: [{}],
        sonList: [], // 问卷内容列表
        Proposed: [
          {
            zw: "组长",
            dblb: null,
            xm: null,
          },
        ],
        ryfile: null,
        names: null,
      };
      this.resetForm("form");

    },


    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加家长购买意见征集";
      this.isEdit = false;
      this.form.sonList = [...this.sonList]
      this.form.configContent = this.yjzjMoban;
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      this.isEdit = true;
      const id = row.id;
      getOpinions(id).then((response) => {
        this.form = this.responseParseForm(response.data);
        this.sonList = [...this.form.sonList]

        if (this.form.zjlx == 0) {
          this.form.ryfile = [
            {
              name: "人员名单",
              url: this.form.names,
              uid: new Date().getTime() + 1,
            },
          ];
        }

        this.open = true;
        this.title = "修改家长购买意见征集";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          // 如果是问卷类型且没有问卷内容，提示用户
          if (this.form.zjlx == 0 && this.sonList.length == 0) {
            this.$message.error("请完善问卷内容");
            return;
          }
          
          // this.formValidate().then((res) => {
            if (this.form.id != null) {
  
              updateOpinions(this.requestParseForm()).then((response) => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
                this.$emit("handleGetProcessMenu");
              });
            } else {
              addOpinions(this.requestParseForm()).then((response) => {
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                this.getList();
                this.$emit("handleGetProcessMenu");
              });
            }
          // });
        }
      });
    },
    requestParseForm() {
      const parseForm = {
        ...this.form,
        tpqzsj: this.form.tpqzsj ? this.form.tpqzsj.join("#") : null,
        zw: this.form.Proposed[0].zw,
        dblb: this.form.Proposed[0].dblb,
        xm: this.form.Proposed[0].xm,
        sonList: this.form.zjlx == 0 ? this.sonList : [],
        gradeClassList: [],
      };
      return parseForm;
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id;
      this.$modal
        .confirm("是否确认删除选中的数据项？")
        .then(function () {
          return delOpinions(ids);
        })
        .then(() => {
          this.getList();
          this.$emit("handleGetProcessMenu");
          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => {});
    },


    // 提交征集结果表单
    submitResultForm() {
      this.$refs["resultForm"].validate((valid) => {
        if (valid) {
          updateOpinions({
            ...this.resultForm,
          }).then((response) => {
            this.$modal.msgSuccess("保存成功");
            this.resultDialog = false;
            this.getList();
          });
        }
      });
    },
    // 取消征集结果编辑
    cancelResult() {
      this.resultDialog = false;
      this.resultForm = {
        id: null,
        resultFile: null,
        sonJsonData: [],
      };
    },

    // 关闭查看对话框
    closeViewDialog() {
      this.viewDetilsDialog = false;
    },
    handleChangeZjlx(value) {
      if (value == 0) {
        let startDate = new Date();
        let endDate = new Date();
        endDate.setDate(endDate.getDate() + 7); // 一周后

        this.form.tpqzsj = [
          startDate.toISOString().slice(0, 19).replace("T", " "),
          endDate.toISOString().slice(0, 19).replace("T", " "),
        ];
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
        process.env.VUE_APP_QR_CODE_URL + `?id=${row.questionBusinessId}`;

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
      getSurveyStatistics({ id: row.questionBusinessId }).then((res) => {
        this.statisticsVisible = true;
        this.selectedSurveyId = row.questionBusinessId;
      });
    }, 
    //人员名单上传成功
    handleSuccess(response, file, fileList) { 
      if(response.code == 200){
        this.form.ryfile = fileList;
        this.form.names = response.msg;
        this.form.count = response.msg.split(',').length;
      }else{
        this.$message.error('上传失败');
        this.$refs.uploadRef.clearFiles();
        this.form.ryfile = [];
        this.form.names = "";
        this.form.count = 0;
      }
    },
    handleExceed(files, fileList) {
      this.$message.warning(`最多上传1个文件`);
    },
    //人员名单移除文件
    handleRemove(file, fileList) {
      this.form.ryfile = fileList;
      this.form.names = "";
    },
    //人员名单上传失败
    handleError(err, file, fileList) {
      this.$message.error("上传失败");
    },
    /** 投票名单按钮操作 */
    handleSignature(row) {
      this.currentQuestionBusinessId = row.questionBusinessId;
      this.getZiduanList();
      this.getSignatureList();
      this.signatureDialog = true;

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



    /** 预览签名图片 */
    previewSignature(imageUrl) {
      this.signaturePreviewUrl = imageUrl;
      this.signaturePreviewDialog = true;
    },
    // 打开问卷编辑对话框
    handleWjEdit() {
       // 如果没有问卷数据，创建新的
       this.form.sonList = [];
       this.wjbj = true;
       this.$nextTick(() => {
         this.$refs.wjBianjiRef.clearQuestionnaire();
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
  },
};
</script>
<style lang="scss" scoped>
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

.view-details-content {
  padding: 10px;
}

.section-divider {
  margin: 25px 0 15px 0;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  padding-left: 10px;
  border-left: 4px solid #409eff;
  line-height: 1;
}

.content-box {
  border: 1px solid #e4e7ed;
  background-color: #ffffff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  margin-top: 10px;
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

  &:hover {
    border-color: #409eff;
    box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
    transform: scale(1.05);
  }
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

/* 问卷预览样式 */
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

.table-operations {
  margin-bottom: 10px;
}
</style>
