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
      <el-form-item label="供应商ID" prop="supplierId">
        <el-input
          v-model="queryParams.supplierId"
          placeholder="请输入供应商ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="供应商名称快照" prop="supplierName">
        <el-input
          v-model="queryParams.supplierName"
          placeholder="请输入供应商名称快照"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="服务电话" prop="servicePhone">
        <el-input
          v-model="queryParams.servicePhone"
          placeholder="请输入服务电话"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="统购公告ID" prop="noticeId">
        <el-input
          v-model="queryParams.noticeId"
          placeholder="请输入统购公告ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="合同ID" prop="contractId">
        <el-input
          v-model="queryParams.contractId"
          placeholder="请输入合同ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="合作开始时间" prop="startTime">
        <el-date-picker clearable
          v-model="queryParams.startTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择合作开始时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="合作结束时间" prop="endTime">
        <el-date-picker clearable
          v-model="queryParams.endTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择合作结束时间">
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
          v-hasPermi="['supplier:schoolSupplierRel:add']"
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
          v-hasPermi="['supplier:schoolSupplierRel:edit']"
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
          v-hasPermi="['supplier:schoolSupplierRel:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['supplier:schoolSupplierRel:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="schoolSupplierRelList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="学校ID" align="center" prop="schoolId" />
      <el-table-column label="学校名称快照" align="center" prop="schoolName" />
      <el-table-column label="供应商ID" align="center" prop="supplierId" />
      <el-table-column label="供应商名称快照" align="center" prop="supplierName" />
      <el-table-column label="服务电话" align="center" prop="servicePhone" />
      <el-table-column label="统购公告ID" align="center" prop="noticeId" />
      <el-table-column label="合同ID" align="center" prop="contractId" />
      <el-table-column label="合作状态，1合作中 2已取消 3历史合作 4公告期内" align="center" prop="cooperateStatus" />
      <el-table-column label="合作开始时间" align="center" prop="startTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="合作结束时间" align="center" prop="endTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="取消原因" align="center" prop="cancelReason" />
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
            v-hasPermi="['supplier:schoolSupplierRel:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['supplier:schoolSupplierRel:remove']"
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

    <!-- 添加或修改学校供应商合作关系对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="学校ID" prop="schoolId">
          <el-input v-model="form.schoolId" placeholder="请输入学校ID" />
        </el-form-item>
        <el-form-item label="学校名称快照" prop="schoolName">
          <el-input v-model="form.schoolName" placeholder="请输入学校名称快照" />
        </el-form-item>
        <el-form-item label="供应商ID" prop="supplierId">
          <el-input v-model="form.supplierId" placeholder="请输入供应商ID" />
        </el-form-item>
        <el-form-item label="供应商名称快照" prop="supplierName">
          <el-input v-model="form.supplierName" placeholder="请输入供应商名称快照" />
        </el-form-item>
        <el-form-item label="服务电话" prop="servicePhone">
          <el-input v-model="form.servicePhone" placeholder="请输入服务电话" />
        </el-form-item>
        <el-form-item label="统购公告ID" prop="noticeId">
          <el-input v-model="form.noticeId" placeholder="请输入统购公告ID" />
        </el-form-item>
        <el-form-item label="合同ID" prop="contractId">
          <el-input v-model="form.contractId" placeholder="请输入合同ID" />
        </el-form-item>
        <el-form-item label="合作开始时间" prop="startTime">
          <el-date-picker clearable
            v-model="form.startTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择合作开始时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="合作结束时间" prop="endTime">
          <el-date-picker clearable
            v-model="form.endTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择合作结束时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="取消原因" prop="cancelReason">
          <el-input v-model="form.cancelReason" type="textarea" placeholder="请输入内容" />
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
import { listSchoolSupplierRel, getSchoolSupplierRel, delSchoolSupplierRel, addSchoolSupplierRel, updateSchoolSupplierRel } from "@/api/supplier/schoolSupplierRel";

export default {
  name: "SchoolSupplierRel",
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
      // 学校供应商合作关系表格数据
      schoolSupplierRelList: [],
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
        supplierId: null,
        supplierName: null,
        servicePhone: null,
        noticeId: null,
        contractId: null,
        cooperateStatus: null,
        startTime: null,
        endTime: null,
        cancelReason: null,
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
        supplierId: [
          { required: true, message: "供应商ID不能为空", trigger: "blur" }
        ],
        supplierName: [
          { required: true, message: "供应商名称快照不能为空", trigger: "blur" }
        ],
        cooperateStatus: [
          { required: true, message: "合作状态，1合作中 2已取消 3历史合作 4公告期内不能为空", trigger: "change" }
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
    /** 查询学校供应商合作关系列表 */
    getList() {
      this.loading = true;
      listSchoolSupplierRel(this.queryParams).then(response => {
        this.schoolSupplierRelList = response.rows;
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
        supplierId: null,
        supplierName: null,
        servicePhone: null,
        noticeId: null,
        contractId: null,
        cooperateStatus: null,
        startTime: null,
        endTime: null,
        cancelReason: null,
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
      this.title = "添加学校供应商合作关系";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getSchoolSupplierRel(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改学校供应商合作关系";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateSchoolSupplierRel(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addSchoolSupplierRel(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除学校供应商合作关系编号为"' + ids + '"的数据项？').then(function() {
        return delSchoolSupplierRel(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('supplier/schoolSupplierRel/export', {
        ...this.queryParams
      }, `schoolSupplierRel_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
