<template>
  <div class="detail-panel">
    <div class="header" style="display: flex; align-items: center">
      <h3>发布公告</h3>
      <div style="margin-left: 20px">
        <el-button
          icon="el-icon-upload"
          type="primary"
          v-hasPermi="['xfpt:msy-add']"
          v-if="total == 0"
          @click="addXuagou"
          >发布公告</el-button
        >
      </div>
    </div>
    <scene
      @handlegetModeByTeamId="handlegetModeByTeamId"
      ref="scene"
      :teamId="formId"
      @getDetail="getDetail"
      @chakan="chakan"
      @getBcList="getBcList"
      @getTotal="getTotal"
    />
    <!-- 添加或修改模式一组织确认选购模式对话框 -->
    <el-dialog
      :title="title"
      :visible.sync="open"
      width="1000px"
      append-to-body
    >
      <div style="">
        <el-form ref="form" :model="form" :rules="rules" label-width="120px"> 
          <el-form-item label="标题" prop="title">
            <el-input
              v-model="form.title"
              placeholder="请输入标题"
              :readonly="readOnly"
            />
          </el-form-item>
          <el-form-item label="采购公告" prop="noticeFile">
            <file-upload
              :disabled="readOnly"
              :fileType="['pdf']"
              :gaizhang="true"
              v-model="form.noticeFile"
              :limit="1"
            >
              <el-button slot="template" type="text" @click.stop="importTemplate"
                >下载模板</el-button
              ></file-upload
            >
          </el-form-item>
          <el-form-item
            label="其他文件"
            prop="otherFile"
            v-if="form.id == null || form.otherFile != null"
          >
            <file-upload
              :disabled="readOnly"
              :fileType="[
                'doc',
                'docx',
                'pdf',
                'xls',
                'xlsx',
                'zip',
                'rar',
                'png',
                'jpg',
                'jpeg',
              ]"
              v-model="form.otherFile"
            />
          </el-form-item>
          <el-form-item label="是否发布" prop="isRelease">
            <el-radio-group v-model="form.isRelease" v-if="!readOnly">
              <el-radio :label="'1'">是</el-radio>
              <el-radio :label="'0'">否</el-radio>
            </el-radio-group>
            <div v-else>
              <el-tag v-if="form.isRelease == 1" type="success">是</el-tag>
              <el-tag v-else type="danger">否</el-tag>
            </div>
          </el-form-item>
        </el-form>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" v-if="!readOnly"
          >确 定</el-button
        >
        <el-button @click="open = false">取 消</el-button>
      </div>
    </el-dialog>
    <!-- 补充列表弹窗 -->
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
        v-hasPermi="['school:rwggzs:add']"
        @click="handleBcAdd"
        style="margin-bottom: 10px"
        >新增</el-button
      >
      <el-table :data="bcList" border>
        <el-table-column label="标题" prop="title" align="center" />
        <el-table-column label="创建时间" prop="createTime" align="center" />
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
    <!-- 补充公告弹窗 -->
    <el-dialog
      title="补充公告"
      :visible.sync="bcShow"
      width="800px"
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
            :fileType="['pdf']"
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
  getOrganization,
  addOrganization,
  updateOrganization,
} from "@/api/cg/scene";
import { getBcList, buchong } from "@/api/cg/rwggzs";
import { getXfptnotice } from "@/api/system/xfptnotice";
import scene from "./modeOne/scene.vue";

export default {
  components: {
    scene,
  },
  dicts: ["ggxzms"],

  name: "EnterpriseModeOne",
  data() {
    return {
      baseUrl: "http://127.0.0.1:9300",
      readOnly: false,
      xgType: false,
      activeName: "scene",

      title: "",
      open: false,
      form: {},
      rules: {
        title: [{ required: true, message: "请输入标题", trigger: "blur" }],
        isSamplesPic: [
          { required: true, message: "请输入是否提样衣", trigger: "blur" },
        ],
        samplesPic: [
          { required: true, message: "请输入样衣图", trigger: "blur" },
        ],
        noticeFile: [
          { required: true, message: "请上传采购公告", trigger: "blur" },
        ],
        isRelease: [
          { required: true, message: "请选择是否发布", trigger: "blur" },
        ],
      },
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
      total: 0,
    };
  },
  props: {
    formId: {
      type: String,
      default: "",
    },
  },

  methods: {
    getTotal(total) {
      this.total = total;
    },
    importTemplate() {
      const link = document.createElement("a");
      link.href =
        process.env.VUE_APP_IMAGE_BASE_API +
        "/filestatics/download/公告模板.docx";
      link.download = "公告模板.docx";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
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
        noticeFile: null,
        isRelease: null,
      };
      this.resetForm("form");
    },

    addXuagou() {
      this.reset();
      this.readOnly = false;
      this.xgType = false;
      this.open = true;
      this.title = "发布选购信息";
    },
    handlegetModeByTeamId() {
      this.$emit("handleGetProcessMenu");
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.xgType) {
            updateOrganization({
              ...this.form,
              teamId: this.formId,
            }).then((response) => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.$refs[this.activeName].getList();
              this.$emit("handleGetProcessMenu");
            });
          } else {
            addOrganization({
              ...this.form,
              teamId: this.formId,
            }).then((response) => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.$refs[this.activeName].getList();
              this.$emit("handleGetProcessMenu");
            });
          }
        }
      });
    },
    //查询详情
    getDetail(id) {
      this.readOnly = false;
      getOrganization(id).then((response) => {
        this.xgType = true;
        this.form = response.data;
        this.open = true;
        this.title = "修改选购信息";
      });
    },
    chakan(id) {
      this.readOnly = true;
      getOrganization(id).then((response) => {
        this.form = response.data;
        if (this.form.isSamplesPic == 1 && this.form.samplesPic != null) {
          this.form.samplesPic = this.form.samplesPic.split(",").map((item) => {
            return this.baseUrl + item;
          });
        }
        this.open = true;
        this.title = "查看选购信息";
      });
    },
    // ====== 补充公告相关方法 ======
    bcReset() {
      this.bcForm = {
        title: "",
        teamId: this.formId,
        catagory: "CGGG",
        file: "",
        id: this.bcId, // 保证编辑时 id 正确
        type: "0",
      };
      this.resetForm && this.resetForm("bcForm");
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
            this.$emit("handleGetProcessMenu",response.data);
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
</style>
