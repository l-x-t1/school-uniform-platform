<template>
  <div class="detail-panel">
    <div class="header" style="display: flex; align-items: center">
      <h3>产品质量检验报告报备与公开</h3>
      <div style="margin-left: 20px">
        <el-button
          type="primary"
          icon="el-icon-plus"
          v-hasPermi="['xfpt:zljy:addgs']"
          @click="addHezuo"
          v-if="reportList.length == 0"
          >添加厂商</el-button
        >
      </div>
    </div>
    <el-table :data="reportList" style="width: 100%; margin-top: 10px">
      <el-table-column label="序号" align="center" type="index" />
      <el-table-column label="厂商名称" prop="supplierName" />
      <el-table-column label="法定代表人" prop="legalRepresentative" />
      <el-table-column
        label="联系电话"
        width="120"
        align="center"
        prop="supplierPhone"
      />
      <el-table-column align="center" label="操作">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="seeZJ(scope.row)"
            >查看质量检测报告</el-button
          >
          <el-button
            type="text"
            size="small"
            v-hasPermi="['school:zjbg:upload']"
            v-if="zzType != 2"
            @click="uploadZJ(scope.row)"
            > {{ zzType == 0 ? '上传质量检测报告' : '上传二次质量检测报告' }}</el-button
          >
          <el-button type="text" size="small" @click="seeInfo(scope.row)"
            >查看厂商信息</el-button
          >
          <el-button
            type="text"
            size="small"
            v-hasPermi="['xfpt:zljy:remove']"
            @click="delInfo(scope.row)"
            >删除厂商</el-button
          >
        </template>
      </el-table-column>
    </el-table>
    <el-dialog
      title="新增合作厂商"
      :visible.sync="manufacturerDialogVisible"
      width="900px"
      append-to-body
    >
      <div>
        <el-form inline>
          <el-form-item label="">
            <el-input
              v-model="manufacturerQuery"
              placeholder="请输入厂商名称"
              style="width: 300px"
              class="filter-item"
              @keyup.enter.native="searchManufacturer"
            ></el-input
          ></el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchManufacturer"
              >查询</el-button
            >
          </el-form-item>
        </el-form>
      </div>
      <el-table :data="manufacturerList" border style="width: 100%">
        <el-table-column label="厂商名称" prop="supplierName" />

        <el-table-column label="操作" align="center" width="100">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              @click="addManufacturer(scope.row)"
              >选择</el-button
            >
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 30, 50]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        ></el-pagination>
        <span class="page-info">共{{ totalPages }}页/{{ total }}条</span>
      </div>
    </el-dialog>
    <el-dialog
      title="厂商详情"
      :visible.sync="dialogChangshang"
      width="1000px"
      append-to-body
      custom-class="manufacturer-detail-dialog"
    >
      <div style="height: 700px; overflow: auto">
        <div class="manufacturer-header">
          <div class="manufacturer-logo">
            <el-avatar
              :size="80"
              icon="el-icon-s-custom"
              style="background-color: #409eff"
            ></el-avatar>
          </div>
          <div class="manufacturer-title">
            <h2>{{ csInfo.supplierName }}</h2>
            <div class="manufacturer-subtitle">
            </div>
          </div>
        </div>

        <el-divider><i class="el-icon-s-cooperation"></i> 基本信息</el-divider>

        <div class="info-grid">
          <div class="info-item">
            <span class="info-label"
              ><i class="el-icon-user"></i> 法人代表：</span
            >
            <span class="info-value">{{ csInfo.legalRepresentative }}</span>
          </div>
          <div class="info-item">
            <span class="info-label"
              ><i class="el-icon-money"></i> 注册资金(万元)：</span
            >
            <span class="info-value">{{ csInfo.registeredCapital }} </span>
          </div>
          <div class="info-item">
            <span class="info-label"
              ><i class="el-icon-s-custom"></i> 职工人数：</span
            >
            <span class="info-value">{{ csInfo.staffNumber }}</span>
          </div>
          <div class="info-item">
            <span class="info-label"
              ><i class="el-icon-postcard"></i> 企业类型：</span
            >
            <span class="info-value">{{ csInfo.supplierType }}</span>
          </div>
          <div class="info-item">
            <span class="info-label"
              ><i class="el-icon-date"></i> 成立时间：</span
            >
            <span class="info-value">
              {{ csInfo.establishedTime }}
            </span>
          </div>
          <div class="info-item">
            <span class="info-label"
              ><i class="el-icon-date"></i> 更新时间：</span
            >
            <span class="info-value">{{ csInfo.turnoverTime }}</span>
          </div>
          <div class="info-item info-item-full">
            <span class="info-label"
              ><i class="el-icon-location"></i> 厂商地址：</span
            >
            <span class="info-value">{{ csInfo.supplierAddress }}</span>
          </div>
        </div>
        <el-divider><i class="el-icon-document"></i> 厂商简介</el-divider>
        <div class="manufacturer-description" v-html="csInfo.supplierIntroduce">
           
        </div>

        <el-divider><i class="el-icon-picture"></i> 企业资质</el-divider>
        <div class="manufacturer-qualification">
          <FileUpload :disabled="true" v-model="csInfo.businessLicense" />
        </div>

        <el-divider v-if="csInfo.xypj != null"><i class="el-icon-star-on"></i> 信用评级</el-divider>
        <div class="manufacturer-qualification">
          <FileUpload :disabled="true" v-model="csInfo.xypj" />
        </div>
       
      </div>

      <div class="dialog-footer" slot="footer">
        <el-button type="primary" @click="dialogChangshang = false"
          >关 闭</el-button
        >
      </div>
    </el-dialog>
    <el-dialog
      :title="titleInfoZL"
      append-to-body
      :visible.sync="dialogZJbg"
      width="800px"
    >
      <el-form
        inline
        :rules="rulesForm"
        :model="addSupplierHetong"
        ref="addSupplierHetongRef"
        label-width="150px"
      >
        <el-form-item label="质量检验报告标题：" prop="inspectionReportTitle">
          <el-input
            style="width: 350px"
            placeholder="请输入质量检验报告标题"
            v-model="addSupplierHetong.inspectionReportTitle"
          ></el-input>
        </el-form-item>
        <el-form-item label="报告文件：" prop="url">
          <file-upload v-model="addSupplierHetong.url" :fileType="['pdf']" :limit="1">
            <!-- <el-button slot="template" type="text" @click="importTemplate"
              >下载模板</el-button
            > -->
          </file-upload>

          
        </el-form-item>

      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogZJbg = false">取 消</el-button>
        <el-button type="primary" @click="addConfirm">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 查看质量检测报告弹窗 -->
    <el-dialog
      title="质量检测报告"
      append-to-body
      :visible.sync="dialogZJList"
      width="50%"
    >
      <el-table :data="reportDetailList" style="width: 100%">
        <el-table-column prop="inspectionReportTitle" label="报告标题">
        </el-table-column>
        <el-table-column prop="inspectionReportType" label="报告类型" width="200">
          <template slot-scope="scope">
            <dict-tag
              v-if="
                scope.row.inspectionReportType != null &&
                String(scope.row.inspectionReportType) !== ''
              "
              :options="dict.type.sc_zljy_bglx"
              :value="scope.row.inspectionReportType"
            />
            <span v-else-if="scope.$index === 0">一次质量检测报告</span>
            <span v-else-if="scope.$index === 1">二次质量检测报告</span>
            <span v-else>—</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="上传时间" width="180">
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              @click="handlePreviewReport(scope.row)"
              >预览</el-button
            >
            <el-button
              type="text"
              size="small"
              @click="handleViewReport(scope.row)"
              >下载</el-button
            >
            <el-button
              type="text"
              size="small"
              v-hasPermi="['school:zjbg:upload']"
              @click="handleBianjiReport(scope.row)"
              >编辑</el-button
            > 
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog
      title="新增厂商"
      :visible.sync="supplierDialog"
      width="1000px"
      append-to-body
    >
      <el-form
        ref="supplierForm"
        :model="supplierForm"
        :rules="supplierRules"
        label-width="100px"
      >
        <el-row>
          <el-col :span="12">
            <el-form-item label="厂商名称" prop="supplierName">
              <el-input
                v-model="supplierForm.supplierName"
                placeholder="请输入厂商名称"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="supplierPhone">
              <el-input
                v-model="supplierForm.supplierPhone"
                placeholder="请输入联系电话"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="法定代表人" prop="legalRepresentative">
              <el-input
                v-model="supplierForm.legalRepresentative"
                placeholder="请输入法定代表人"
              />
            </el-form-item>
          </el-col> 
          <el-col :span="12">
            <el-form-item label="职工人数" prop="staffNumber">
              <el-input
                v-model="supplierForm.staffNumber"
                placeholder="请输入职工人数"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="厂商地址" prop="supplierAddress">
              <el-input
                v-model="supplierForm.supplierAddress"
                placeholder="请输入厂商地址"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="注册资金(万元)" prop="registeredCapital">
              <el-input
                v-model="supplierForm.registeredCapital"
                placeholder="请输入注册资金，单位（万元）"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="成立日期" prop="establishedTime">
              <el-date-picker
                style="width: 100%"
                clearable
                v-model="supplierForm.establishedTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择成立日期"
              >
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="企业类型" prop="supplierType">
              <el-input
                v-model="supplierForm.supplierType"
                placeholder="请输入企业类型"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="厂商介绍" prop="supplierIntroduce">
              <editor
                v-model="supplierForm.supplierIntroduce"
                :min-height="192"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="厂商资质" prop="businessLicense">
              <FileUpload
                v-model="supplierForm.businessLicense"
                :file-list="supplierForm.businessLicenseList"
                :limit="1"
                :file-type="['jpg', 'png', 'jpeg', 'pdf']"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="信用评级" prop="xypj">
              <FileUpload
                v-model="supplierForm.xypj"
                :file-list="supplierForm.xypjList"
                :limit="1"
                :file-type="['jpg', 'png', 'jpeg', 'pdf']"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="supplierSubmitForm">确 定</el-button>
        <el-button @click="supplierCancel">取 消</el-button>
      </div>
    </el-dialog>
    <!-- 文件预览弹窗 -->
    <el-dialog
      fullscreen
      title="预览"
      :visible.sync="previewVisible"
      :before-close="handlePreviewClose"
      append-to-body
    >
      <div class="preview-container" style="height: calc(100vh - 140px)">
        <iframe
          v-if="previewUrl"
          :src="previewUrl"
          width="100%"
          height="100%"
          frameborder="0"
          style="border: none"
        ></iframe>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="previewVisible = false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { addSupplierMessage } from "@/api/school/manufacturers";
import {
  addsuppliceInspectionReportSon,
  getReportList,
  delSupplier,
  delsuppliceInspectionReportSon,
  editsuppliceInspectionReportSon,
  getSupplierInfo,
  suppliceInspectionReportList,
  suppliceInspectionReport,
} from "@/api/school/report";
import { getInfo } from "@/api/login";
import { download } from "@/utils/request";
import { getToken } from "@/utils/auth";
import { supplierMessage } from "@/api/cg/cght";
import { Base64 } from "js-base64";
export default {
  name: "QualityReport",
  dicts: ["sc_zljy_bglx"],
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
  data() {
    return {
      previewVisible: false,
      previewUrl: "",
      supplierDialog: false,
      rulesForm: {
        inspectionReportTitle: [
          {
            required: true,
            message: "请输入质量检验报告标题",
            trigger: "blur",
          },
        ],
        url: [
          {
            required: true,
            message: "请上传报告文件",
            trigger: "blur",
          },
        ],
      },
      upload: {
        // 设置上传的请求头部
        headers: { Authorization: "Bearer " + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/file/upload",
      },
      reportList: [],
      csInfo: {},
      dialogChangshang: false,
      manufacturerQuery: "",
      manufacturerDialogVisible: false,
      currentPage: 1,
      pageSize: 10,

      pageNum: 1,
      total: 0,
      totalPages: 0,
      manufacturerList: [],
      addSupplierInfo: {
        deptId: "",
        deptName: "",
        supplierName: "",
        supplierPhone: "",
        supplierAddress: "",
        supplierId: "",
        teamMeetingId: "",
      },
      // 表单参数
      supplierForm: {},
      // 表单校验
      supplierRules: {
        supplierName: [
          { required: true, message: "厂商名称不能为空", trigger: "blur" },
        ],
      supplierPhone: [
        { required: true, message: "联系电话不能为空", trigger: "blur" },
        { pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号码", trigger: "blur" }
      ],
      supplierType: [
        { required: true, message: "企业类型不能为空", trigger: "blur" },
      ],
      },
      formIdInfo: "",
      dialogZJbg: false,
      titleInfoZL: "",

      addSupplierHetong: {
        inspectionReportTitle: "",

        reportId: "",
      },
      dialogZJList: false,
      reportDetailList: [],
      currentSupplierId: null,
      zzType:0,//0 一检 1 二检 2 完成
    };
  },
  created() {
    this.getInfo();
    this.suppliceInspectionReportList();
    this.formIdInfo = this.formId;
  },
  methods: {
    importTemplate() {
      const link = document.createElement("a");
      link.href =
        process.env.VUE_APP_IMAGE_BASE_API + "/filestatics/download/质检报告.zip";
      link.download = "质检报告.zip";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
    // 取消按钮
    supplierCancel() {
      this.supplierDialog = false;
      this.supplierReset();
    },
    supplierSubmitForm() {
      this.$refs["supplierForm"].validate((valid) => {
        if (valid) {
          addSupplierMessage({
            ...this.supplierForm,
          }).then((response) => {
            this.$modal.msgSuccess("新增成功");
            this.supplierDialog = false;
            this.suppliceInspectionReportList();
          });
        }
      });
    },
    // 表单重置
    supplierReset() {
      this.supplierForm = {
        supplierName: null,
        supplierPhone: null,
        supplierType: null,
        legalRepresentative: null,
        turnoverTime: null,
        staffNumber: null,
        establishedTime: null,
        supplierAddress: null,
        supplierIntroduce: null,
        equipmentConditions: null,
        businessLicense: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null,
        registeredCapital: null,
        delFlag: null,
      };
      this.resetForm("supplierForm");
    },



    addConfirm() {
      this.$refs.addSupplierHetongRef.validate((valid) => {
        if (!valid) {
          this.$message.error("请完善表单信息");
          return;
        }

        // 判断是新增还是编辑
        const api = this.addSupplierHetong.id
          ? editsuppliceInspectionReportSon
          : addsuppliceInspectionReportSon;
        api(this.addSupplierHetong)
          .then((res) => {
            this.$message.success(
              this.addSupplierHetong.id ? "修改成功" : "添加成功"
            );
            this.dialogZJbg = false;

            // 如果当前有打开的报告列表，则刷新
            if (this.currentSupplierId) {
              this.loadReportDetails(this.currentSupplierId);
            } else {
              this.suppliceInspectionReportList();
            }
          })
          .catch((error) => {
            this.$message.error(
              (this.addSupplierHetong.id ? "修改" : "添加") +
                "失败：" +
                (error.message || "未知错误")
            );
          });
      });
    },



    /** 添加合作厂商 */
    addManufacturer(row) {
      this.addSupplierInfo.supplierName = row.supplierName;
      this.addSupplierInfo.supplierPhone = row.supplierPhone;
      this.addSupplierInfo.supplierId = row.id;
      this.addSupplierInfo.teamMeetingId = this.formIdInfo;
      suppliceInspectionReport(this.addSupplierInfo).then(() => {
        this.$message.success("添加成功");
        this.suppliceInspectionReportList();
        this.$emit("handleGetProcessMenu");
      });
      // 只关闭新增厂商弹窗，保持厂商列表弹窗打开
      this.manufacturerDialogVisible = false;
    },
    searchManufacturer() {
      this.supplierMessage();
    },
    supplierMessage() {
      supplierMessage({
        pageNum: this.currentPage,
        pageSize: this.pageSize,
        supplierName: this.manufacturerQuery,
      }).then((res) => {
        this.manufacturerList = res.rows;
        this.total = res.total;
        this.totalPages = res.pages;
      });
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.supplierMessage();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.supplierMessage();
    },
    getInfo() {
      getInfo().then((res) => {
        this.addSupplierInfo.deptId = res.user.deptId;
        this.addSupplierInfo.deptName = res.user.dept.deptName;
      });
    },
    addHezuo() {
      this.manufacturerDialogVisible = true;
      this.supplierMessage();
      // this.$emit('add-hezuo')
    },
    /** 获取质量检验报告列表 */
    suppliceInspectionReportList() {
      this.loading = true;
      suppliceInspectionReportList({
        teamMeetingId: this.formId,
      }).then((response) => {
        this.reportList = response.rows;
        if(this.reportList.length > 0){
          this.loadReportDetails(this.reportList[0].id)
        }
        this.total = response.total;
        this.loading = false;
      });
    },
    addCS() {
      this.$emit("add-cs");
    },
    seeZJ(row) {
      this.dialogZJList = true;
      this.currentSupplierId = row.id;
      this.loadReportDetails(row.id);
    },
    uploadZJ(row) {
      this.titleInfoZL = "上传质量检验报告";
      this.dialogZJbg = true;
      this.resetForm();
      // 正确设置两个ID
      this.addSupplierHetong.supplierQualityId = row.id;
      this.addSupplierHetong.reportId = row.id;
    },
    seeInfo(row) {
      this.dialogChangshang = true;
      getSupplierInfo(row.supplierId).then((res) => {
        this.csInfo = res.data;
      });
    },

    handleViewReport(row) {
      getInfo().then((res) => {
        download(
          "/school/suppliceContractSon/downloadZip",
          {
            url: row.url,
            fileName: row.fileName,
            inspectionReportTitle: row.inspectionReportTitle,
          },
          res.user.dept.deptName + "_" + row.inspectionReportTitle + ".zip"
        );
      });

    },
    handleBianjiReport(row) {
      this.titleInfoZL = "编辑质量检验报告";
      this.dialogZJbg = true;
      this.resetForm();
      
      this.addSupplierHetong = {
        id: row.id,
        supplierQualityId: row.supplierQualityId,
        inspectionReportTitle: row.inspectionReportTitle,
        url: row.url,
      };
    },
    /** 预览质量检测报告 */
    handlePreviewReport(row) {
      // var b64Encoded = Base64.encode(
      //   process.env.VUE_APP_IMAGE_BASE_API + row.url
      // );
      this.previewUrl = process.env.VUE_APP_IMAGE_BASE_API + row.url
        // process.env.VUE_APP_KKFILE2 + encodeURIComponent(b64Encoded);
      this.previewVisible = true;
    },
    // 关闭预览弹窗
    handlePreviewClose() {
      this.previewUrl = "";
      this.previewVisible = false;
    },
    /** 删除质量检测报告 */
    handleDeleteReport(row) {
      this.$confirm("确定要删除该质量检测报告吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          // 调用删除接口
          delsuppliceInspectionReportSon(row.id)
            .then((res) => {
              this.$message.success("删除成功");
              // 刷新报告列表
              // 使用正确的参数刷新列表
              if (row.supplierQualityId) {
                this.loadReportDetails(row.supplierQualityId);
              } else if (this.currentSupplierId) {
                // 如果没有supplierQualityId，尝试使用当前记录的ID
                this.loadReportDetails(this.currentSupplierId);
              } else {
                // 如果都没有，刷新全部列表
                this.suppliceInspectionReportList();
              }
            })
            .catch((error) => {
              this.$message.error("删除失败：" + (error.message || "未知错误"));
            });
        })
        .catch(() => {
          // 用户取消，不做操作
        });
    },
    // 加载报告详情
    loadReportDetails(id) {
      getReportList({
        reportId: id,
      })
        .then((res) => {
          this.reportDetailList = res.rows || [];
          if(this.reportDetailList.length == 0){
            this.zzType = 0
          }else if(this.reportDetailList.length == 1){
            this.zzType = 1
          }else{
            this.zzType = 2
          }
        })
        .catch((error) => {
          this.$message.error(
            "获取报告列表失败：" + (error.message || "未知错误")
          );
        });
    },
    // 重置表单
    resetForm() {
      this.$refs.addSupplierHetongRef &&
        this.$refs.addSupplierHetongRef.resetFields();
      this.addSupplierHetong = {
        supplierQualityId: this.addSupplierHetong
          ? this.addSupplierHetong.supplierQualityId
          : undefined,
        reportId: this.addSupplierHetong
          ? this.addSupplierHetong.reportId
          : undefined,
        inspectionReportTitle: "",
        url: "",
      };
    },
    // 文件下载方法
    downloadFile(url, fileName) {
      // 确保URL包含基础路径
      const fullUrl = url.startsWith("http")
        ? url
        : process.env.VUE_APP_IMAGE_BASE_API + url;

      // 处理文件类型
      let fileExtension = "";
      if (fileName.lastIndexOf(".") > -1) {
        fileExtension = fileName
          .substring(fileName.lastIndexOf(".") + 1)
          .toLowerCase();
      } else if (fullUrl.lastIndexOf(".") > -1) {
        // 如果文件名没有扩展名，尝试从URL提取
        fileExtension = fullUrl
          .substring(fullUrl.lastIndexOf(".") + 1)
          .split("?")[0]
          .toLowerCase();
      }

      // 设置不同类型文件的MIME类型
      let mimeType = "application/octet-stream"; // 默认二进制流
      if (["zip", "rar", "7z"].includes(fileExtension)) {
        // 压缩文件类型
        if (fileExtension === "zip") mimeType = "application/zip";
        else if (fileExtension === "rar")
          mimeType = "application/x-rar-compressed";
        else if (fileExtension === "7z")
          mimeType = "application/x-7z-compressed";
      } else if (["pdf"].includes(fileExtension)) {
        mimeType = "application/pdf";
      } else if (["doc", "docx"].includes(fileExtension)) {
        if (fileExtension === "doc") mimeType = "application/msword";
        else
          mimeType =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
      } else if (["jpg", "jpeg", "png"].includes(fileExtension)) {
        if (fileExtension === "jpg" || fileExtension === "jpeg")
          mimeType = "image/jpeg";
        else mimeType = "image/png";
      } else if (["xls", "xlsx"].includes(fileExtension)) {
        if (fileExtension === "xls") mimeType = "application/vnd.ms-excel";
        else
          mimeType =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
      }

      // 使用fetch API下载文件
      fetch(fullUrl)
        .then((response) => response.blob())
        .then((blob) => {
          // 创建blob链接
          const blobUrl = window.URL.createObjectURL(
            new Blob([blob], { type: mimeType })
          );
          const link = document.createElement("a");
          link.style.display = "none";
          link.href = blobUrl;

          // 确保文件名有正确的扩展名
          if (
            !fileName.toLowerCase().endsWith("." + fileExtension) &&
            fileExtension
          ) {
            fileName += "." + fileExtension;
          }

          link.setAttribute("download", fileName);
          document.body.appendChild(link);
          link.click();

          // 清理
          document.body.removeChild(link);
          window.URL.revokeObjectURL(blobUrl);
        })
        .catch((error) => {
          this.$message.error("下载文件失败，请稍后重试");

          // 使用直接链接作为备用方法
          window.open(fullUrl, "_blank");
        });
    },
    delInfo(row) {
      this.$confirm("确定要删除该厂商吗？此操作不可恢复", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          delSupplier(row.id)
            .then((res) => {
              this.$message.success("删除成功");
              // 刷新列表，使用正确的参数
              this.suppliceInspectionReportList();
            })
            .catch((error) => {
              this.$message.error("删除失败：" + (error.message || "未知错误"));
            });
        })
        .catch(() => {
          // 用户取消删除
        });
    },
    // 判断是否为图片文件
    isImageFile(fileUrl) {
      if (!fileUrl) return false;
      const imageExtensions = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp'];
      const extension = fileUrl.split('.').pop().toLowerCase();
      return imageExtensions.includes(extension);
    },
    // 判断是否为PDF文件
    isPdfFile(fileUrl) {
      if (!fileUrl) return false;
      const extension = fileUrl.split('.').pop().toLowerCase();
      return extension === 'pdf';
    },
    // 获取完整的图片URL
    getFullImageUrl(fileUrl) {
      if (!fileUrl) return '';
      if (fileUrl.startsWith('http')) {
        return fileUrl;
      }
      return process.env.VUE_APP_IMAGE_BASE_API + fileUrl;
    },
    // 预览PDF
    previewPdf(fileUrl) {
      const fullUrl = this.getFullImageUrl(fileUrl);
      this.previewUrl = fullUrl;
      this.previewVisible = true;
    },
  },
  watch: {
    dialogZJbg(val) {
      if (!val) {
        // 关闭弹窗时重置表单
        this.resetForm();
      }
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

.manufacturer-detail-dialog {
  border-radius: 10px;
  overflow: hidden;
}

.manufacturer-header {
  display: flex;
  align-items: center;
  padding: 20px;
  background-color: #f5f7fa;
}

.manufacturer-logo {
  margin-right: 20px;
}

.manufacturer-title h2 {
  margin: 0;
  font-size: 24px;
}

.manufacturer-subtitle {
  margin-top: 10px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  padding: 20px;
}

.info-item {
  display: flex;
  align-items: center;
}

.info-label {
  font-weight: bold;
  margin-right: 10px;
}

.info-value {
  color: #666;
}

.manufacturer-description {
  padding:10px !important;
}

.manufacturer-equipment {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 20px;
}

.manufacturer-qualification {
  padding: 10px;
}

.dialog-footer {
  text-align: right;
  padding: 20px;
}

::v-deep .el-dialog__body {
  padding: 10px 20px;
}

::v-deep .el-upload-dragger {
  width: 500px;
  height: 40px;
}

.pdf-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.pdf-preview {
  text-align: center;
  padding: 20px;
  border: 2px dashed #ddd;
  border-radius: 8px;
  background-color: #fafafa;
  min-width: 200px;
}

.no-qualification {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  text-align: center;
}
</style>
