<template>
  <div class="detail-panel">
    <div class="header" style="display: flex; align-items: center">
      <h3>采购信息报备</h3>
      <div style="margin-left: 20px">
        <el-button
          icon="el-icon-upload"
          type="primary"
          v-hasPermi="['xfpt:cgxxbb']"
          @click="handleUpdate"
          >报备</el-button
        >
      </div>
    </div>
    <!-- 添加或修改采购汇总信息对话框 -->
    <el-form :model="baobeiInfo" label-width="120px">
      <el-form-item label="学校：" prop="schoolName">
        <el-input
          v-model="baobeiInfo.schoolName"
          :readonly="true"
          placeholder="请输入学校"
        />
      </el-form-item>
      <el-form-item label="采购公告：" prop="inviteFile">
        <div>
          <file-upload
            v-model="baobeiInfo.inviteFile"
            :disabled="true"
            v-if="baobeiInfo.inviteFile"
          />
          <div v-else>待上传</div>
        </div>
      </el-form-item>
      <el-form-item label="成交公告：" prop="winBidFile">
        <div>
          <file-upload
            v-model="baobeiInfo.winBidFile"
            :disabled="true"
            v-if="baobeiInfo.winBidFile"
          />
          <div v-else>待上传</div>
        </div>
      </el-form-item>
      <el-form-item label="成交企业：" prop="enterprise">
        <el-input
          v-model="baobeiInfo.enterprise"
          :readonly="true"
          placeholder="请输入成交企业"
        />
      </el-form-item>
      <el-form-item label="单价（元）：" prop="dj">
        <el-input
          v-model="baobeiInfo.dj"
          :readonly="true"
          placeholder="请输入单价"
        />
      </el-form-item>
      <el-form-item label="订购人数：" prop="count">
        <el-input
          v-model="baobeiInfo.count"
          :readonly="true"
          placeholder="请输入订购人数"
        />
      </el-form-item>
      <el-form-item label="总金额（元）：" prop="money">
        <el-input
          v-model="baobeiInfo.money"
          :readonly="true"
          placeholder="请输入总金额"
        />
      </el-form-item> 
      <el-form-item label="采购日期：" prop="buyTime">
        <el-date-picker
          clearable
          :readonly="true"
          v-model="baobeiInfo.buyTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择采购日期"
        >
        </el-date-picker>
      </el-form-item>
    </el-form>
    <el-dialog
      :title="title"
      :visible.sync="open"
      width="1000px"
      append-to-body
    >
      <el-form
        ref="form"
        :model="form"
        :rules="rules"
        label-width="120px"
        v-if="open"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="学校" prop="schoolName">
          <el-input v-model="form.schoolName" placeholder="请输入学校" />
        </el-form-item>
        <el-form-item label="采购公告" prop="inviteFile">
          <file-upload v-model="form.inviteFile" />
        </el-form-item>
        <el-form-item label="成交公告" prop="winBidFile">
          <file-upload v-model="form.winBidFile" />
        </el-form-item>
        <el-form-item label="成交企业" prop="enterprise">
          <el-input v-model="form.enterprise" placeholder="请输入成交企业" />
        </el-form-item>
        <el-form-item label="单价（元）" prop="dj">
          <el-input v-model="form.dj" placeholder="请输入单价" />
        </el-form-item>
        <el-form-item label="订购人数" prop="count">
          <el-input v-model="form.count" placeholder="请输入订购人数" />
        </el-form-item>
        <el-form-item label="总金额（元）" prop="money">
          <el-input v-model="form.money" placeholder="请输入总金额" />
        </el-form-item> 
        <el-form-item label="采购日期" prop="buyTime">
          <el-date-picker
            clearable
            v-model="form.buyTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择采购日期"
          >
          </el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listSummary,
  getSummary,
  delSummary,
  addSummary,
  updateSummary,
} from "@/api/school/summary";

export default {
  name: "Caigoubaobei",
  props: {
    formId: {
      type: [String, Number],
      required: true,
    },
  },
  data() {
    return {
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 采购信息报备信息
      baobeiInfo: {},
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        title: [{ required: true, message: "请输入标题", trigger: "blur" }],
        schoolName: [
          { required: true, message: "请输入学校", trigger: "blur" },
        ],
        money: [{ required: true, message: "请输入采购金额", trigger: "blur" }],
        inviteFile: [
          { required: true, message: "请上传招标文件", trigger: "blur" },
        ],
        winBidFile: [
          { required: true, message: "请上传中标文件", trigger: "blur" },
        ],
        enterprise: [
          { required: true, message: "请输入厂商", trigger: "blur" },
        ],
        count: [{ required: true, message: "请输入采购件数", trigger: "blur" }],
        buyTime: [
          { required: true, message: "请选择采购日期", trigger: "blur" },
        ],
      },
      baseUrl: process.env.VUE_APP_IMAGE_BASE_API,
    };
  },
  created() {
    this.getInfo();
  },
  methods: {
    /** 查询采购汇总信息列表 */
    getInfo() {
      getSummary(this.formId).then((response) => {
        if (response.data != undefined) {
          this.baobeiInfo = response.data;
          if (
            this.baobeiInfo.schoolUniformFile != null &&
            this.baobeiInfo.schoolUniformFile != ""
          ) {
            console.log("获取采购汇总信息内容", response.data);

            this.baobeiInfo.schoolUniformFile =
              this.baobeiInfo.schoolUniformFile
                .split(",")
                .map((item) => this.baseUrl + item);
          }
        }
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
        money: null,
        inviteFile: null,
        winBidFile: null,
        enterprise: null,
        count: null,
        schoolUniformFile: null,
        schoolName: null,
        deptId: null,
        ancestors: null,
        remark: null,
        createBy: null,
        createTime: null,
        updateTime: null,
        updateBy: null,
        buyTime: null,
        delFlag: null,
        teamId: this.formId,
      };
      this.resetForm("form");
    },

    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },

    // 修改按钮操作
    handleUpdate() {
      this.reset();
      getSummary(this.formId).then((response) => {
        if (response.data != undefined) {
          this.form = response.data;
          if (
            this.form.schoolUniformFile != null &&
            this.form.schoolUniformFile != ""
          ) {
            this.form.schoolUniformFile = this.form.schoolUniformFile
              .split(",")
              .map((item) => this.baseUrl + item);
          }
        }
        this.$emit("handleGetProcessMenu");

        console.log(this.form);
        this.open = true;
        this.title = "采购信息报备";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (
            this.form.schoolUniformFile != null &&
            this.form.schoolUniformFile != ""
          ) {
            this.form.schoolUniformFile = this.form.schoolUniformFile
              .map((item) => item.split(this.baseUrl)[1])
              .join(",");
          }
          updateSummary(this.form).then((response) => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getInfo();
          });
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal
        .confirm('是否确认删除采购汇总信息编号为"' + ids + '"的数据项？')
        .then(function () {
          return delSummary(ids);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download(
        "school/summary/export",
        {
          ...this.queryParams,
        },
        `summary_${new Date().getTime()}.xlsx`
      );
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
