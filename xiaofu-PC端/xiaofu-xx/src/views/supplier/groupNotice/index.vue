<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="学校ID" prop="schoolId">
        <el-input
          v-model="queryParams.schoolId"
          placeholder="请输入学校ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="学校名称快照" prop="schoolName">
        <el-input
          v-model="queryParams.schoolName"
          placeholder="请输入学校名称快照"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="公告标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入公告标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="公告编号" prop="noticeCode">
        <el-input
          v-model="queryParams.noticeCode"
          placeholder="请输入公告编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="班子会议ID" prop="teamMeetingId">
        <el-input
          v-model="queryParams.teamMeetingId"
          placeholder="请输入班子会议ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="采购流程ID" prop="purchaseProcessId">
        <el-input
          v-model="queryParams.purchaseProcessId"
          placeholder="请输入采购流程ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="公告开始时间" prop="startTime">
        <el-date-picker clearable
          v-model="queryParams.startTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择公告开始时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="公告截止时间" prop="endTime">
        <el-date-picker clearable
          v-model="queryParams.endTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择公告截止时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="发布时间" prop="publishTime">
        <el-date-picker clearable
          v-model="queryParams.publishTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择发布时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="结果发布时间" prop="resultPublishTime">
        <el-date-picker clearable
          v-model="queryParams.resultPublishTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择结果发布时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="当前归属部门/学校" prop="deptId">
        <el-input
          v-model="queryParams.deptId"
          placeholder="请输入当前归属部门/学校"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
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
          v-hasPermi="['supplier:groupNotice:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['supplier:groupNotice:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['supplier:groupNotice:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['supplier:groupNotice:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="groupNoticeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="学校ID" align="center" prop="schoolId" />
      <el-table-column label="学校名称快照" align="center" prop="schoolName" />
      <el-table-column label="公告标题" align="center" prop="title" />
      <el-table-column label="公告编号" align="center" prop="noticeCode" />
      <el-table-column label="公告类型，1选购公告 2结果公告" align="center" prop="noticeType" />
      <el-table-column label="公告正文" align="center" prop="content" />
      <el-table-column label="年级ID集合" align="center" prop="gradeIds" />
      <el-table-column label="年级名称集合" align="center" prop="gradeNames" />
      <el-table-column label="班子会议ID" align="center" prop="teamMeetingId" />
      <el-table-column label="采购流程ID" align="center" prop="purchaseProcessId" />
      <el-table-column label="公告开始时间" align="center" prop="startTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="公告截止时间" align="center" prop="endTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态，0草稿 1已发布 2已结束 3已撤回" align="center" prop="status" />
      <el-table-column label="报名状态，0未开放 1报名中 2报名结束" align="center" prop="applyStatus" />
      <el-table-column label="发布时间" align="center" prop="publishTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.publishTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="结果发布时间" align="center" prop="resultPublishTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.resultPublishTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="当前归属部门/学校" align="center" prop="deptId" />
      <el-table-column label="组织祖级链路" align="center" prop="ancestors" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['supplier:groupNotice:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['supplier:groupNotice:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改统购公告对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="学校ID" prop="schoolId">
          <el-input v-model="form.schoolId" placeholder="请输入学校ID" />
        </el-form-item>
        <el-form-item label="学校名称快照" prop="schoolName">
          <el-input v-model="form.schoolName" placeholder="请输入学校名称快照" />
        </el-form-item>
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="公告编号" prop="noticeCode">
          <el-input v-model="form.noticeCode" placeholder="请输入公告编号" />
        </el-form-item>
        <el-form-item label="公告正文" prop="content">
          <el-input v-model="form.content" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="年级ID集合" prop="gradeIds">
          <el-input v-model="form.gradeIds" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="年级名称集合" prop="gradeNames">
          <el-input v-model="form.gradeNames" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="班子会议ID" prop="teamMeetingId">
          <el-input v-model="form.teamMeetingId" placeholder="请输入班子会议ID" />
        </el-form-item>
        <el-form-item label="采购流程ID" prop="purchaseProcessId">
          <el-input v-model="form.purchaseProcessId" placeholder="请输入采购流程ID" />
        </el-form-item>
        <el-form-item label="公告开始时间" prop="startTime">
          <el-date-picker clearable
            v-model="form.startTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择公告开始时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="公告截止时间" prop="endTime">
          <el-date-picker clearable
            v-model="form.endTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择公告截止时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="发布时间" prop="publishTime">
          <el-date-picker clearable
            v-model="form.publishTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择发布时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结果发布时间" prop="resultPublishTime">
          <el-date-picker clearable
            v-model="form.resultPublishTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择结果发布时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="当前归属部门/学校" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入当前归属部门/学校" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="删除标记，0正常 1删除" prop="delFlag">
          <el-input v-model="form.delFlag" placeholder="请输入删除标记，0正常 1删除" />
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
import { listGroupNotice, getGroupNotice, delGroupNotice, addGroupNotice, updateGroupNotice } from "@/api/supplier/groupNotice";

export default {
  name: "GroupNotice",
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
      // 统购公告表格数据
      groupNoticeList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        schoolId: null,
        schoolName: null,
        title: null,
        noticeCode: null,
        noticeType: null,
        content: null,
        gradeIds: null,
        gradeNames: null,
        teamMeetingId: null,
        purchaseProcessId: null,
        startTime: null,
        endTime: null,
        status: null,
        applyStatus: null,
        publishTime: null,
        resultPublishTime: null,
        deptId: null,
        ancestors: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        schoolId: [
          { required: true, message: "学校ID不能为空", trigger: "blur" }
        ],
        schoolName: [
          { required: true, message: "学校名称快照不能为空", trigger: "blur" }
        ],
        title: [
          { required: true, message: "公告标题不能为空", trigger: "blur" }
        ],
        noticeCode: [
          { required: true, message: "公告编号不能为空", trigger: "blur" }
        ],
        noticeType: [
          { required: true, message: "公告类型，1选购公告 2结果公告不能为空", trigger: "change" }
        ],
        status: [
          { required: true, message: "状态，0草稿 1已发布 2已结束 3已撤回不能为空", trigger: "change" }
        ],
        applyStatus: [
          { required: true, message: "报名状态，0未开放 1报名中 2报名结束不能为空", trigger: "change" }
        ],
        delFlag: [
          { required: true, message: "删除标记，0正常 1删除不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询统购公告列表 */
    getList() {
      this.loading = true;
      listGroupNotice(this.queryParams).then(response => {
        this.groupNoticeList = response.rows;
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
        schoolId: null,
        schoolName: null,
        title: null,
        noticeCode: null,
        noticeType: null,
        content: null,
        gradeIds: null,
        gradeNames: null,
        teamMeetingId: null,
        purchaseProcessId: null,
        startTime: null,
        endTime: null,
        status: null,
        applyStatus: null,
        publishTime: null,
        resultPublishTime: null,
        deptId: null,
        ancestors: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null,
        delFlag: null
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
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加统购公告";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getGroupNotice(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改统购公告";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateGroupNotice(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addGroupNotice(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除统购公告编号为"' + ids + '"的数据项？').then(function() {
        return delGroupNotice(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('supplier/groupNotice/export', {
        ...this.queryParams
      }, `groupNotice_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
