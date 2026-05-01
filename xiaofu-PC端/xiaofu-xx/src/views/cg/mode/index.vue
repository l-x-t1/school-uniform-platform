<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryForm"
      size="small"
      :inline="true"
      v-show="showSearch"
      label-width="68px"
    >
      <el-form-item label="选购公告-公告名称" prop="noticeName">
        <el-input
          v-model="queryParams.noticeName"
          placeholder="请输入选购公告-公告名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="选购公告-是否提供样衣图" prop="isSamplesPic">
        <el-input
          v-model="queryParams.isSamplesPic"
          placeholder="请输入选购公告-是否提供样衣图"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="招标机构合作合同-会议名称" prop="meetingTitle">
        <el-input
          v-model="queryParams.meetingTitle"
          placeholder="请输入招标机构合作合同-会议名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="招标机构合作合同-招标公司" prop="tenderingCompany">
        <el-input
          v-model="queryParams.tenderingCompany"
          placeholder="请输入招标机构合作合同-招标公司"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item
        label="招标机构合作合同-招标代理合同签订时间"
        prop="signingTime"
      >
        <el-date-picker
          clearable
          v-model="queryParams.signingTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择招标机构合作合同-招标代理合同签订时间"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item
        label="招标机构合作合同-招标公司联系人姓名"
        prop="contactsName"
      >
        <el-input
          v-model="queryParams.contactsName"
          placeholder="请输入招标机构合作合同-招标公司联系人姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item
        label="招标机构合作合同-招标公司联系方式"
        prop="companyContactPhone"
      >
        <el-input
          v-model="queryParams.companyContactPhone"
          placeholder="请输入招标机构合作合同-招标公司联系方式"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item
        label="招标机构合作合同-招标公司联系地址"
        prop="companyContactAddress"
      >
        <el-input
          v-model="queryParams.companyContactAddress"
          placeholder="请输入招标机构合作合同-招标公司联系地址"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item
        label="招标机构合作合同-招标代理合同"
        prop="biddingAgencyContract"
      >
        <el-input
          v-model="queryParams.biddingAgencyContract"
          placeholder="请输入招标机构合作合同-招标代理合同"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item
        label="评估招标结果-评标结果会议名称"
        prop="bidEvaluationTitle"
      >
        <el-input
          v-model="queryParams.bidEvaluationTitle"
          placeholder="请输入评估招标结果-评标结果会议名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item
        label="评估招标结果-评标结果附件"
        prop="bidEvaluationResults"
      >
        <el-input
          v-model="queryParams.bidEvaluationResults"
          placeholder="请输入评估招标结果-评标结果附件"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否发布 0撤回 1发布" prop="isRelease">
        <el-input
          v-model="queryParams.isRelease"
          placeholder="请输入是否发布 0撤回 1发布"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="部门id" prop="deptId">
        <el-input
          v-model="queryParams.deptId"
          placeholder="请输入部门id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="祖籍id" prop="ancestors">
        <el-input
          v-model="queryParams.ancestors"
          placeholder="请输入祖籍id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          icon="el-icon-search"
          size="mini"
          @click="handleQuery"
          >搜索</el-button
        >
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery"
          >重置</el-button
        >
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['school:mode:add']"
          >新增</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['school:mode:edit']"
          >修改</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['school:mode:remove']"
          >删除</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['school:mode:export']"
          >导出</el-button
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
      <el-table-column label="${comment}" align="center" prop="id" />
      <el-table-column
        label="选购公告-公告名称"
        align="center"
        prop="noticeName"
      />
      <el-table-column
        label="选购公告-采购公告"
        align="center"
        prop="noticeFile"
      />
      <el-table-column
        label="选购公告-其他文件"
        align="center"
        prop="otherFile"
      />
      <el-table-column
        label="选购公告-是否提供样衣图"
        align="center"
        prop="isSamplesPic"
      />
      <el-table-column
        label="选购公告0  招标机构合作合同1  评估招标结果 2"
        align="center"
        prop="type"
      />
      <el-table-column
        label="招标机构合作合同-会议名称"
        align="center"
        prop="meetingTitle"
      />
      <el-table-column
        label="招标机构合作合同-招标公司"
        align="center"
        prop="tenderingCompany"
      />
      <el-table-column
        label="招标机构合作合同-招标代理合同签订时间"
        align="center"
        prop="signingTime"
        width="180"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.signingTime, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="招标机构合作合同-招标公司联系人姓名"
        align="center"
        prop="contactsName"
      />
      <el-table-column
        label="招标机构合作合同-招标公司联系方式"
        align="center"
        prop="companyContactPhone"
      />
      <el-table-column
        label="招标机构合作合同-招标公司联系地址"
        align="center"
        prop="companyContactAddress"
      />
      <el-table-column
        label="招标机构合作合同-执照"
        align="center"
        prop="licenseFile"
      />
      <el-table-column
        label="招标机构合作合同-招标代理合同"
        align="center"
        prop="biddingAgencyContract"
      />
      <el-table-column
        label="评估招标结果-评标结果会议名称"
        align="center"
        prop="bidEvaluationTitle"
      />
      <el-table-column
        label="评估招标结果-评标结果附件"
        align="center"
        prop="bidEvaluationResults"
      />
      <el-table-column label="${comment}" align="center" prop="remark" />
      <el-table-column
        label="是否发布 0撤回 1发布"
        align="center"
        prop="isRelease"
      />
      <el-table-column label="部门id" align="center" prop="deptId" />
      <el-table-column label="祖籍id" align="center" prop="ancestors" />
      <el-table-column
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
      >
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

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改选用组织委托招标机构确定企业模式对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="选购公告-公告名称" prop="noticeName">
          <el-input
            v-model="form.noticeName"
            placeholder="请输入选购公告-公告名称"
          />
        </el-form-item>
        <el-form-item label="选购公告-采购公告" prop="noticeFile">
          <file-upload v-model="form.noticeFile" />
        </el-form-item>
        <el-form-item label="选购公告-其他文件" prop="otherFile">
          <file-upload v-model="form.otherFile" />
        </el-form-item>
        <el-form-item label="选购公告-是否提供样衣图" prop="isSamplesPic">
          <el-input
            v-model="form.isSamplesPic"
            placeholder="请输入选购公告-是否提供样衣图"
          />
        </el-form-item>
        <el-form-item label="招标机构合作合同-会议名称" prop="meetingTitle">
          <el-input
            v-model="form.meetingTitle"
            placeholder="请输入招标机构合作合同-会议名称"
          />
        </el-form-item>
        <el-form-item label="招标机构合作合同-招标公司" prop="tenderingCompany">
          <el-input
            v-model="form.tenderingCompany"
            placeholder="请输入招标机构合作合同-招标公司"
          />
        </el-form-item>
        <el-form-item
          label="招标机构合作合同-招标代理合同签订时间"
          prop="signingTime"
        >
          <el-date-picker
            clearable
            v-model="form.signingTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择招标机构合作合同-招标代理合同签订时间"
          >
          </el-date-picker>
        </el-form-item>
        <el-form-item
          label="招标机构合作合同-招标公司联系人姓名"
          prop="contactsName"
        >
          <el-input
            v-model="form.contactsName"
            placeholder="请输入招标机构合作合同-招标公司联系人姓名"
          />
        </el-form-item>
        <el-form-item
          label="招标机构合作合同-招标公司联系方式"
          prop="companyContactPhone"
        >
          <el-input
            v-model="form.companyContactPhone"
            placeholder="请输入招标机构合作合同-招标公司联系方式"
          />
        </el-form-item>
        <el-form-item
          label="招标机构合作合同-招标公司联系地址"
          prop="companyContactAddress"
        >
          <el-input
            v-model="form.companyContactAddress"
            placeholder="请输入招标机构合作合同-招标公司联系地址"
          />
        </el-form-item>
        <el-form-item label="招标机构合作合同-执照" prop="licenseFile">
          <file-upload v-model="form.licenseFile" />
        </el-form-item>
        <el-form-item
          label="招标机构合作合同-招标代理合同"
          prop="biddingAgencyContract"
        >
          <el-input
            v-model="form.biddingAgencyContract"
            placeholder="请输入招标机构合作合同-招标代理合同"
          />
        </el-form-item>
        <el-form-item
          label="评估招标结果-评标结果会议名称"
          prop="bidEvaluationTitle"
        >
          <el-input
            v-model="form.bidEvaluationTitle"
            placeholder="请输入评估招标结果-评标结果会议名称"
          />
        </el-form-item>
        <el-form-item
          label="评估招标结果-评标结果附件"
          prop="bidEvaluationResults"
        >
          <el-input
            v-model="form.bidEvaluationResults"
            placeholder="请输入评估招标结果-评标结果附件"
          />
        </el-form-item>
        <el-form-item label="${comment}" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="delFlag">
          <el-input v-model="form.delFlag" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="是否发布 0撤回 1发布" prop="isRelease">
          <el-input
            v-model="form.isRelease"
            placeholder="请输入是否发布 0撤回 1发布"
          />
        </el-form-item>
        <el-form-item label="部门id" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入部门id" />
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
  listMode,
  getMode,
  delMode,
  addMode,
  updateMode,
} from "@/api/school/mode";

export default {
  name: "Mode",
  data() {
    return {
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
        type: null,
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
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {},
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询选用组织委托招标机构确定企业模式列表 */
    getList() {
      this.loading = true;
      listMode(this.queryParams).then((response) => {
        this.modeList = response.rows;
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
        id: null,
        noticeName: null,
        noticeFile: null,
        otherFile: null,
        isSamplesPic: null,
        type: null,
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
        remark: null,
        delFlag: null,
        createBy: null,
        updateTime: null,
        updateBy: null,
        createTime: null,
        isRelease: null,
        deptId: null,
        ancestors: null,
      };
      this.resetForm("form");
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
      this.title = "添加选用组织委托招标机构确定企业模式";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getMode(id).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = "修改选用组织委托招标机构确定企业模式";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.id != null) {
            updateMode(this.form).then((response) => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addMode(this.form).then((response) => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
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
  },
};
</script>
