<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="公告ID" prop="noticeId">
        <el-input
          v-model="queryParams.noticeId"
          placeholder="请输入公告ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="学校ID" prop="schoolId">
        <el-input
          v-model="queryParams.schoolId"
          placeholder="请输入学校ID"
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
      <el-form-item label="报名单号" prop="applyNo">
        <el-input
          v-model="queryParams.applyNo"
          placeholder="请输入报名单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="报名时间" prop="applyTime">
        <el-date-picker clearable
          v-model="queryParams.applyTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择报名时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="审核时间" prop="auditTime">
        <el-date-picker clearable
          v-model="queryParams.auditTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择审核时间">
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
          v-hasPermi="['school:supplierApply:add']"
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
          v-hasPermi="['school:supplierApply:edit']"
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
          v-hasPermi="['school:supplierApply:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['school:supplierApply:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="supplierApplyList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="公告ID" align="center" prop="noticeId" />
      <el-table-column label="学校ID" align="center" prop="schoolId" />
      <el-table-column label="供应商ID" align="center" prop="supplierId" />
      <el-table-column label="供应商名称快照" align="center" prop="supplierName" />
      <el-table-column label="报名单号" align="center" prop="applyNo" />
      <el-table-column label="报名状态，0待提交 1已提交 2已撤回" align="center" prop="applyStatus" />
      <el-table-column label="资质审查状态，0待审查 1通过 2不通过" align="center" prop="qualificationStatus" />
      <el-table-column label="比选阶段状态，0未进入 1已进入 2已完成" align="center" prop="bidStageStatus" />
      <el-table-column label="比选结果状态，0未出结果 1已中选 2未中选" align="center" prop="bidResultStatus" />
      <el-table-column label="报名时间" align="center" prop="applyTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.applyTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审核时间" align="center" prop="auditTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.auditTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审核意见" align="center" prop="auditRemark" />
      <el-table-column label="当前归属部门/学校" align="center" prop="deptId" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['school:supplierApply:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['school:supplierApply:remove']"
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

    <!-- 添加或修改供应商报名对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="公告ID" prop="noticeId">
          <el-input v-model="form.noticeId" placeholder="请输入公告ID" />
        </el-form-item>
        <el-form-item label="学校ID" prop="schoolId">
          <el-input v-model="form.schoolId" placeholder="请输入学校ID" />
        </el-form-item>
        <el-form-item label="供应商ID" prop="supplierId">
          <el-input v-model="form.supplierId" placeholder="请输入供应商ID" />
        </el-form-item>
        <el-form-item label="供应商名称快照" prop="supplierName">
          <el-input v-model="form.supplierName" placeholder="请输入供应商名称快照" />
        </el-form-item>
        <el-form-item label="报名单号" prop="applyNo">
          <el-input v-model="form.applyNo" placeholder="请输入报名单号" />
        </el-form-item>
        <el-form-item label="报名状态" prop="applyStatus">
          <el-select v-model="form.applyStatus" placeholder="请选择报名状态">
            <el-option label="待提交" value="0" />
            <el-option label="已提交" value="1" />
            <el-option label="已撤回" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="资质审查状态" prop="qualificationStatus">
          <el-select v-model="form.qualificationStatus" placeholder="请选择资质审查状态">
            <el-option label="待审查" value="0" />
            <el-option label="通过" value="1" />
            <el-option label="不通过" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="比选阶段状态" prop="bidStageStatus">
          <el-select v-model="form.bidStageStatus" placeholder="请选择比选阶段状态" clearable>
            <el-option label="未进入" value="0" />
            <el-option label="已进入" value="1" />
            <el-option label="已完成" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="比选结果状态" prop="bidResultStatus">
          <el-select v-model="form.bidResultStatus" placeholder="请选择比选结果状态" clearable>
            <el-option label="未出结果" value="0" />
            <el-option label="已中选" value="1" />
            <el-option label="未中选" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="报名时间" prop="applyTime">
          <el-date-picker clearable
            v-model="form.applyTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择报名时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="审核时间" prop="auditTime">
          <el-date-picker clearable
            v-model="form.auditTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择审核时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="审核意见" prop="auditRemark">
          <el-input v-model="form.auditRemark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="报名附件JSON文本" prop="applyFilesJson">
          <el-input v-model="form.applyFilesJson" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="比选资料JSON文本" prop="compareFilesJson">
          <el-input v-model="form.compareFilesJson" type="textarea" placeholder="请输入内容" />
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
import { listSupplierApply, getSupplierApply, delSupplierApply, addSupplierApply, updateSupplierApply } from "@/api/school/supplierApply";

export default {
  name: "SupplierApply",
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
      // 供应商报名表格数据
      supplierApplyList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        noticeId: null,
        schoolId: null,
        supplierId: null,
        supplierName: null,
        applyNo: null,
        applyStatus: null,
        qualificationStatus: null,
        bidStageStatus: null,
        bidResultStatus: null,
        applyTime: null,
        auditTime: null,
        auditRemark: null,
        deptId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        noticeId: [
          { required: true, message: "公告ID不能为空", trigger: "blur" }
        ],
        schoolId: [
          { required: true, message: "学校ID不能为空", trigger: "blur" }
        ],
        supplierId: [
          { required: true, message: "供应商ID不能为空", trigger: "blur" }
        ],
        supplierName: [
          { required: true, message: "供应商名称快照不能为空", trigger: "blur" }
        ],
        applyNo: [
          { required: true, message: "报名单号不能为空", trigger: "blur" }
        ],
        applyStatus: [
          { required: true, message: "报名状态，0待提交 1已提交 2已撤回不能为空", trigger: "change" }
        ],
        qualificationStatus: [
          { required: true, message: "资质审查状态，0待审查 1通过 2不通过不能为空", trigger: "change" }
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
    /** 查询供应商报名列表 */
    getList() {
      this.loading = true;
      listSupplierApply(this.queryParams).then(response => {
        this.supplierApplyList = response.rows;
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
        noticeId: null,
        schoolId: null,
        supplierId: null,
        supplierName: null,
        applyNo: null,
        applyStatus: "0",
        qualificationStatus: "0",
        bidStageStatus: null,
        bidResultStatus: null,
        applyTime: null,
        auditTime: null,
        auditRemark: null,
        applyFilesJson: null,
        compareFilesJson: null,
        deptId: null,
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
      this.title = "添加供应商报名";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getSupplierApply(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改供应商报名";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateSupplierApply(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addSupplierApply(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除供应商报名编号为"' + ids + '"的数据项？').then(function() {
        return delSupplierApply(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('school/supplierApply/export', {
        ...this.queryParams
      }, `supplierApply_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
