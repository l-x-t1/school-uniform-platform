<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="学生ID" prop="studentId">
        <el-input
          v-model="queryParams.studentId"
          placeholder="请输入学生ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="身高" prop="height">
        <el-input
          v-model="queryParams.height"
          placeholder="请输入身高"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="体重" prop="weight">
        <el-input
          v-model="queryParams.weight"
          placeholder="请输入体重"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="胸围" prop="chest">
        <el-input
          v-model="queryParams.chest"
          placeholder="请输入胸围"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="腰围" prop="waistline">
        <el-input
          v-model="queryParams.waistline"
          placeholder="请输入腰围"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="臀围" prop="hipline">
        <el-input
          v-model="queryParams.hipline"
          placeholder="请输入臀围"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="推荐尺码ID" prop="sizeId">
        <el-input
          v-model="queryParams.sizeId"
          placeholder="请输入推荐尺码ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="量体时间" prop="measureTime">
        <el-date-picker clearable
          v-model="queryParams.measureTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择量体时间">
        </el-date-picker>
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
          v-hasPermi="['supplier:studentSizeExt:add']"
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
          v-hasPermi="['supplier:studentSizeExt:edit']"
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
          v-hasPermi="['supplier:studentSizeExt:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['supplier:studentSizeExt:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="studentSizeExtList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="学生ID" align="center" prop="studentId" />
      <el-table-column label="身高" align="center" prop="height" />
      <el-table-column label="体重" align="center" prop="weight" />
      <el-table-column label="胸围" align="center" prop="chest" />
      <el-table-column label="腰围" align="center" prop="waistline" />
      <el-table-column label="臀围" align="center" prop="hipline" />
      <el-table-column label="推荐尺码ID" align="center" prop="sizeId" />
      <el-table-column label="量体时间" align="center" prop="measureTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.measureTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="量体备注" align="center" prop="measureRemark" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['supplier:studentSizeExt:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['supplier:studentSizeExt:remove']"
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

    <!-- 添加或修改学生尺码扩展对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="学生ID" prop="studentId">
          <el-input v-model="form.studentId" placeholder="请输入学生ID" />
        </el-form-item>
        <el-form-item label="身高" prop="height">
          <el-input v-model="form.height" placeholder="请输入身高" />
        </el-form-item>
        <el-form-item label="体重" prop="weight">
          <el-input v-model="form.weight" placeholder="请输入体重" />
        </el-form-item>
        <el-form-item label="胸围" prop="chest">
          <el-input v-model="form.chest" placeholder="请输入胸围" />
        </el-form-item>
        <el-form-item label="腰围" prop="waistline">
          <el-input v-model="form.waistline" placeholder="请输入腰围" />
        </el-form-item>
        <el-form-item label="臀围" prop="hipline">
          <el-input v-model="form.hipline" placeholder="请输入臀围" />
        </el-form-item>
        <el-form-item label="推荐尺码ID" prop="sizeId">
          <el-input v-model="form.sizeId" placeholder="请输入推荐尺码ID" />
        </el-form-item>
        <el-form-item label="量体时间" prop="measureTime">
          <el-date-picker clearable
            v-model="form.measureTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择量体时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="量体备注" prop="measureRemark">
          <el-input v-model="form.measureRemark" type="textarea" placeholder="请输入内容" />
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
import { listStudentSizeExt, getStudentSizeExt, delStudentSizeExt, addStudentSizeExt, updateStudentSizeExt } from "@/api/supplier/studentSizeExt";

export default {
  name: "StudentSizeExt",
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
      // 学生尺码扩展表格数据
      studentSizeExtList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        studentId: null,
        height: null,
        weight: null,
        chest: null,
        waistline: null,
        hipline: null,
        sizeId: null,
        measureTime: null,
        measureRemark: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        studentId: [
          { required: true, message: "学生ID不能为空", trigger: "blur" }
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
    /** 查询学生尺码扩展列表 */
    getList() {
      this.loading = true;
      listStudentSizeExt(this.queryParams).then(response => {
        this.studentSizeExtList = response.rows;
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
        studentId: null,
        height: null,
        weight: null,
        chest: null,
        waistline: null,
        hipline: null,
        sizeId: null,
        measureTime: null,
        measureRemark: null,
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
      this.title = "添加学生尺码扩展";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getStudentSizeExt(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改学生尺码扩展";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateStudentSizeExt(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addStudentSizeExt(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除学生尺码扩展编号为"' + ids + '"的数据项？').then(function() {
        return delStudentSizeExt(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('supplier/studentSizeExt/export', {
        ...this.queryParams
      }, `studentSizeExt_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
