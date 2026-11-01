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
      <el-form-item label="标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发布状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择发布状态"
          clearable
        >
          <el-option
            v-for="dict in dict.type.notice_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="分类标识" prop="catagory">
        <el-cascader
          v-model="queryParams.catagory"
          :options="categoryList"
          :props="{ checkStrictly: true, value: 'bs', label: 'name' }"
          clearable
          :show-all-levels="false"
          @change="handleCategoryChange"
        ></el-cascader>
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
          v-hasPermi="['system:xfptnotice:add']"
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
          v-hasPermi="['system:xfptnotice:edit']"
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
          v-hasPermi="['system:xfptnotice:remove']"
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
          v-hasPermi="['system:xfptnotice:export']"
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
      :data="xfptnoticeList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column
        label="序号"
        type="index"
        width="50"
        align="center"
        :index="indexMethod"
      />
      <el-table-column label="标题" align="center" prop="title" /> 
      
      <el-table-column label="阅读量" align="center" prop="readCount" />

      <el-table-column label="分类" align="center" prop="catagory">
        <template slot-scope="scope">
          {{ getCategoryName(scope.row.catagory) }}
        </template>
      </el-table-column>
      <el-table-column label="发布时间" align="center" prop="createTime" />
      <el-table-column label="截止时间" align="center" prop="endTime" />
      <el-table-column label="发布状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.notice_status"
            :value="scope.row.status"
          />
        </template>
      </el-table-column>
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
            v-hasPermi="['system:xfptnotice:edit']"
            >修改</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:xfptnotice:remove']"
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

    <!-- 添加或修改首页公告对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="70vw" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="分类标识" prop="catagory">
          <el-cascader
            v-model="form.catagory"
            :options="categoryList"
            :props="{ value: 'bs', label: 'name' }"
            clearable
            :show-all-levels="false"
            @change="catagoryChange"
          ></el-cascader>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容">
          <!-- <editor v-model="form.content" :min-height="192"/> -->
          <wangEditor v-model="form.content" :height="500" />
        </el-form-item>
        <el-form-item label="截止时间" prop="endTime">
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择截止时间"
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="发布状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in dict.type.notice_status"
              :key="dict.value"
              :label="dict.value"
              >{{ dict.label }}</el-radio
            >
          </el-radio-group>
        </el-form-item>
        <el-form-item label="封面图" prop="fmImg" v-if="isShowFmImg">
          <file-upload
            :fileType="['jpg', 'png', 'jpeg']"
            v-model="form.fmImg"
            :limit="1"
            :baseUrl="baseUrl"
          />
        </el-form-item>
        <el-form-item label="附件" prop="file">
          <file-upload v-model="form.file" />
        </el-form-item>

        <!-- <el-form-item label="封面图" prop="fmImg">
          <image-upload v-model="form.fmImg" :limit="1" :baseUrl="baseUrl" />
        </el-form-item> -->
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
  listCategory,
  listXfptnotice,
  getXfptnotice,
  delXfptnotice,
  addXfptnotice,
  updateXfptnotice,
} from "@/api/system/xfptnotice";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
export default {
  name: "Xfptnotice",
  dicts: ["notice_status"],
  components: {
    Treeselect,
  },
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
      // 首页公告表格数据
      xfptnoticeList: [],
      // 分类列表
      isShowFmImg: false,
      categoryList: [],
      categoryList2: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        status: null,
        catagory: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        title: [{ required: true, message: "标题不能为空", trigger: "blur" }],
        catagory: [
          { required: true, message: "分类不能为空", trigger: "blur" },
        ],
        status: [
          { required: true, message: "发布状态不能为空", trigger: "blur" },
        ],
        content: [{ required: true, message: "内容不能为空", trigger: "blur" }],
        fmImg: [{ required: true, message: "封面图不能为空", trigger: "blur" }],
      },
      baseUrl: "http://127.0.0.1:9300",
    };
  },
  created() {
    this.getCategoryList();
    this.getList();
  },
  methods: {
    /** 查询分类列表 */
    getCategoryList() {
      listCategory().then((response) => {
        this.categoryList2 = response.data;
        this.categoryList = this.handleTree(response.data, "id", "parentId");
        this.categoryList.forEach((element) => {
          if (element.children.length === 0) {
            element.children = null;
          } else {
            element.children.forEach((item) => {
              if (item.children.length === 0) {
                item.children = null;
              }
            });
          }
        });
      });
    },
    /** 查询首页公告列表 */
    getList() {
      this.loading = true;
      listXfptnotice(this.queryParams).then((response) => {
        this.xfptnoticeList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    /** 分类标识改变 */
    handleCategoryChange(value) {
      this.queryParams.catagory = value.join(",");
    },
    /** 分类标识改变 */
    catagoryChange(value) {
      const findCate = this.categoryList.find((item) => item.bs == value);
      this.isShowFmImg = findCate.isCoverImage == 1;

      this.form.catagory = value;
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        title: null,
        content: null,
        createTime: null,
        endTime: null,
        updateBy: null,
        updateTime: null,
        delFlag: null,
        type: null,
        status: null,
        xgType: null,
        file: null,
        createBy: null,
        ggFile: null,
        fileType: null,
        samplesPic: null,
        enteId: null,
        readCount: null,
        userId: null,
        catagory: null,
        fmImg: null,
        remark: null,
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
      this.isShowFmImg = false;
      this.open = true;
      this.title = "添加首页公告";

    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      this.isShowFmImg = false;
      const id = row.id || this.ids;
      getXfptnotice(id).then((response) => {
        this.form = response.data;
        this.form.fmImg = this.baseUrl + this.form.fmImg;
        this.form.catagory = this.getParentCategory(this.form.catagory);
        this.isShowFmImg = this.categoryList2.find((item) => item.bs == this.form.catagory).isCoverImage == 1;
        this.open = true;
        this.title = "修改首页公告";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.id != null) {
            this.form.catagory =
              this.form.catagory[this.form.catagory.length - 1];
            if (this.form.fmImg && this.form.fmImg.includes(this.baseUrl)) {
              this.form.fmImg = this.form.fmImg.replace(this.baseUrl, "");
            }
            updateXfptnotice(this.form).then((response) => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            this.form.catagory =
              this.form.catagory[this.form.catagory.length - 1];
            if (this.form.fmImg && this.form.fmImg.includes(this.baseUrl)) {
              this.form.fmImg = this.form.fmImg.replace(this.baseUrl, "");
            }
            addXfptnotice(this.form).then((response) => {
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
        .confirm('是否确认删除首页公告编号为"' + ids + '"的数据项？')
        .then(function () {
          return delXfptnotice(ids);
        })
        .then(() => {
          this.queryParams.pageNum = 1;
          this.getList();
          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download(
        "system/xfptnotice/export",
        {
          ...this.queryParams,
        },
        `xfptnotice_${new Date().getTime()}.xlsx`
      );
    },
    /** 转换栏目分类数据结构 */
    normalizer(node) {
      if (node.children && !node.children.length) {
        delete node.children;
      }
      return {
        id: node.id,
        label: node.name,
        children: node.children,
      };
    },
    //根据子分类查询父分类返回父子分类id数组
    getParentCategory(value) {
      let parentId = null;
      this.categoryList.forEach((element) => {
        if (element.bs == value) {
          parentId = element.bs;
        } else {
          if (element.children != null) {
            if (element.children.some((item) => item.bs == value)) {
              parentId = element.bs;
            }
          }
        }
      });
      if (parentId == value) {
        return [value];
      } else {
        return [parentId, value];
      }
    },
    //根据分类标识查询分类名称
    getCategoryName(value) {
      return this.categoryList2.find((item) => item.bs == value).name;
    },
    //序号
    indexMethod(index) {
      return (
        (this.queryParams.pageNum - 1) * this.queryParams.pageSize + index + 1
      );
    },
  },
};
</script>
