<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryForm"
      size="small"
      :inline="true"
      v-show="showSearch"
      label-width="80px"
    >
      <el-form-item label="采购名称" prop="purchaseName">
        <el-input
          v-model="queryParams.purchaseName"
          placeholder="请输入采购名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="采购年度" prop="purchaseYear">
        <el-date-picker
          clearable
          v-model="queryParams.purchaseYear"
          type="year"
          value-format="yyyy"
          placeholder="请选择采购年度"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item label="采购时间" prop="cgTime">
        <el-date-picker
          clearable
          v-model="queryParams.cgTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择采购时间"
        >
        </el-date-picker>
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
          v-hasPermi="['school:cglcprocess:add']"
          @click="handleAdd"
          >添加采购流程</el-button
        >
      </el-col>
      <right-toolbar
        :showSearch.sync="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>

    <el-table
      v-loading="loading"
      :data="purchaseList"
      @selection-change="handleSelectionChange"
      :row-class-name="tableRowClassName"
    >
      <el-table-column type="selection" width="55" align="center" />

      <el-table-column label="序号" align="center" type="index" />
      <el-table-column label="学校名称" align="center" prop="createBy" />
      <el-table-column label="采购名称" align="center" prop="purchaseName" />
      <el-table-column label="采购年度" align="center" prop="purchaseYear" />
      <el-table-column label="采购发起日期" align="center" prop="cgTime" />
      <el-table-column label="创建时间" align="center" prop="createTime">
      </el-table-column>
             <el-table-column
         label="操作"
         align="center"
         class-name="small-padding fixed-width"
       >
         <template slot-scope="scope"> 
           <el-button
             type="text"
             icon="el-icon-edit"
             @click="handleUpdate(scope.row)"
             v-hasPermi="['school:cglcprocess:edit']"
             >编辑</el-button
           >
           <el-button
             type="text"
             icon="el-icon-view"
             @click="handleView(scope.row)"
             >采购流程</el-button
           >
           <el-button
             type="text"
             icon="el-icon-delete"
             @click="handleDelete(scope.row)"
             v-hasPermi="['school:cglcprocess:remove']"
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

    <!-- 添加或修改采购流程对话框 -->
    <el-dialog
      top="5vh"
      :title="title"
      :visible.sync="open"
      width="1200px"
      append-to-body
    >
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="采购名称" prop="purchaseName">
              <el-input
                v-model="form.purchaseName"
                placeholder="请输入采购名称"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="采购年度" prop="purchaseYear">
              <el-date-picker
                style="width: 100%"
                clearable
                v-model="form.purchaseYear"
                type="year"
                value-format="yyyy"
                placeholder="请选择采购年度"
              >
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="采购发起日期" prop="cgTime">
              <el-date-picker
                style="width: 100%"
                clearable
                v-model="form.cgTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择采购发起日期"
              >
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 顶部统计和操作按钮区域 -->
        <div class="stats-action-container">
          <div class="statistics-panel">
            <div class="stat-item">
              <div class="stat-label">学生总人数</div>
              <div class="stat-value">
                {{ totalStudentCount }}<span class="stat-unit">人</span>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-label">年级总数</div>
              <div class="stat-value">
                {{ xfptSchoolGradeClass ? xfptSchoolGradeClass.length : 0
                }}<span class="stat-unit">个</span>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-label">班级总数</div>
              <div class="stat-value">
                {{ totalClassCount }}<span class="stat-unit">个</span>
              </div>
            </div>
          </div>

          <div class="action-buttons">
            <el-button
              size="mini"
              type="primary"
              plain
              icon="el-icon-arrow-up"
              v-if="isAllExpanded"
              @click="collapseAll"
              >全部收起</el-button
            >
            <el-button
              size="mini"
              type="primary"
              plain
              icon="el-icon-arrow-down"
              v-else
              @click="expandAll"
              >全部展开</el-button
            >
            <el-button
              size="mini"
              type="primary"
              plain
              icon="el-icon-plus"
              @click="addGradeDirectly"
              >添加年级</el-button
            >
          </div>
        </div>

        <div>
          <el-form-item label-width="0">
            <el-table
              :data="xfptSchoolGradeClass || []"
              class="grade-table"
              row-key="id"
              :expand-row-keys="expandedRows"
            >
              <el-table-column type="expand" width="50">
                <template slot-scope="props">
                  <div class="class-manage-expanded">
                    <div class="class-header">
                      <div class="class-name-header">
                        <span class="required-field"></span> 班级名称
                      </div>
                      <div class="class-number-header">
                        <span class="required-field"></span> 班级人数
                      </div>
                      <div class="class-action-header">
                        <el-button
                          type="primary"
                          plain
                          size="mini"
                          icon="el-icon-plus"
                          @click="addClassDirectly(props.row)"
                          >添加班级</el-button
                        >
                      </div>
                    </div>

                    <div
                      v-for="(cls, clsIndex) in props.row.classes || []"
                      :key="clsIndex"
                      class="class-row"
                    >
                      <div class="class-name">
                        <el-input
                          v-model="cls.gradeClass"
                          placeholder="请输入班级名称，如：1班、实验班"
                          size="small"
                          style="width: 220px"
                          clearable
                        />
                      </div>
                      <div class="class-number">
                        <el-input
                          v-model="cls.number"
                          placeholder="请输入班级人数"
                          size="small"
                          style="width: 160px"
                        ></el-input>
                      </div>
                      <div class="class-action">
                        <el-button
                          type="danger"
                          plain
                          size="mini"
                          icon="el-icon-delete"
                          @click="deleteClass(props.row, clsIndex)"
                          :disabled="
                            !props.row.classes || props.row.classes.length <= 1
                          "
                          >删除</el-button
                        >
                      </div>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="年级名称" min-width="25%">
                <template slot-scope="scope">
                  <div style="display: flex; align-items: center">
                    <div style="margin-right: 10px; width: 120px">
                      <el-date-picker
                        style="width: 140px"
                        v-model="scope.row.gradeYear"
                        type="year"
                        format="yyyy"
                        value-format="yyyy"
                        placeholder="选择年级（级）"
                      >
                      </el-date-picker>
                    </div> 
                    <div>
                      <el-select
                        v-model="scope.row.gradeClass"
                        placeholder="请选择年级"
                        style="width: 180px"
                        filterable
                        allow-create
                        default-first-option
                        clearable
                      >
                        <el-option
                          v-for="item in gradeOptions"
                          :key="item.value"
                          :label="item.label"
                          :value="item.value"
                        ></el-option>
                      </el-select>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="班级共计" align="center" min-width="40%">
                <template slot-scope="scope">
                  <div class="class-count-info">
                    班级共计：{{
                      scope.row.classes ? scope.row.classes.length : 0
                    }}个， 人数共计：{{ getTotalClassCount(scope.row) }}人
                  </div>
                </template>
              </el-table-column>

              <el-table-column label="操作" align="center" width="120">
                <template slot-scope="scope">
                  <el-button
                    type="danger"
                    plain
                    size="mini"
                    icon="el-icon-delete"
                    @click="deleteGrade(scope.$index)"
                    >删除</el-button
                  >
                </template>
              </el-table-column>
            </el-table>
          </el-form-item>
        </div>

        <div>
          <el-form-item label="采购备注" prop="remark">
            <el-input
              v-model="form.remark"
              placeholder="请输入采购备注"
              type="textarea"
              :rows="3"
            />
          </el-form-item>
        </div>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

         <!-- 查看采购流程对话框 -->
     <el-dialog
       title="采购流程详情"
       :visible.sync="viewOpen"
       fullscreen
       append-to-body
     >
       <div v-if="viewOpen" style="height: 100%">
         <hr
           style="
             border: none;
             border-top: 2px solid #e4e7ed;
             margin: 0px 0 20px 0;
           "
         />
         <TabsComponent :formId="formId" :teamId="teamId" />
       </div>
       <div slot="footer" class="dialog-footer">
         <el-button @click="viewOpen = false">关 闭</el-button>
       </div>
     </el-dialog>

     <!-- 详细查看对话框 -->
     <el-dialog
       title="采购流程详细信息"
       :visible.sync="detailOpen"
       width="1200px"
       append-to-body
     >
       <div v-if="detailOpen">
         <el-descriptions :column="2" border>
           <el-descriptions-item label="采购名称">
             {{ detailForm.purchaseName }}
           </el-descriptions-item>
           <el-descriptions-item label="采购年度">
             {{ detailForm.purchaseYear }}
           </el-descriptions-item>
           <el-descriptions-item label="采购时间">
             {{ detailForm.cgTime }}
           </el-descriptions-item>
           <el-descriptions-item label="创建时间">
             {{ detailForm.createTime }}
           </el-descriptions-item>
         </el-descriptions>

         <!-- 年级班级详细信息 -->
         <div style="margin-top: 20px;">
           <h3>年级班级信息</h3>
           <el-table
             :data="detailXfptSchoolGradeClass || []"
             class="grade-table"
             row-key="id"
           >
             <el-table-column type="expand" width="50">
               <template slot-scope="props">
                 <div class="class-manage-expanded">
                   <div class="class-header">
                     <div class="class-name-header">班级名称</div>
                     <div class="class-number-header">班级人数</div>
                   </div>

                   <div
                     v-for="(cls, clsIndex) in props.row.classes || []"
                     :key="clsIndex"
                     class="class-row"
                   >
                     <div class="class-name">{{ cls.gradeClass }}</div>
                     <div class="class-number">{{ cls.number }}人</div>
                   </div>
                 </div>
               </template>
             </el-table-column>
             <el-table-column label="年级名称" min-width="25%">
               <template slot-scope="scope">
                 <div style="display: flex; align-items: center">
                   <div style="margin-right: 10px; width: 120px">
                     学年：{{ scope.row.gradeYear }}
                   </div>
                   <div style="display: flex;align-items: center;">
                     年级：{{ getGradeLabel(scope.row.gradeClass) }}
                   </div>
                 </div>
               </template>
             </el-table-column>
             <el-table-column label="班级共计" align="center" min-width="40%">
               <template slot-scope="scope">
                 <div class="class-count-info">
                   班级共计：{{
                     scope.row.classes ? scope.row.classes.length : 0
                   }}个， 人数共计：{{ getTotalClassCount(scope.row) }}人
                 </div>
               </template>
             </el-table-column>
           </el-table>
         </div>
       </div>
       <div slot="footer" class="dialog-footer">
         <el-button @click="detailOpen = false">关 闭</el-button>
       </div>
     </el-dialog>
  </div>
</template>

<script>
import TabsComponent from "./_component/index.vue";
import {
  listCglcprocess,
  addCglcprocess,
  updateCglcprocess,
  delCglcprocess,
  getCglcprocess,
} from "@/api/cg/cglcprocess";

export default {
  name: "PurchaseProcess",
  components: {
    TabsComponent,
  },
  data() {
    return {
      isEdit: false,
      dateRange: [],
      formId: null,
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
      // 采购流程表格数据
      purchaseList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
             // 查看对话框显示状态
       viewOpen: false,
       // 详细对话框显示状态
       detailOpen: false,
       gradeOptions: [
        { label: "小班", value: "小班" },
        { label: "中班", value: "中班" },
        { label: "大班", value: "大班" },
        { label: "一年级", value: "一年级" },
        { label: "二年级", value: "二年级" },
        { label: "三年级", value: "三年级" },
        { label: "四年级", value: "四年级" },
        { label: "五年级", value: "五年级" },
        { label: "六年级", value: "六年级" },
        { label: "初一", value: "初一" },
        { label: "初二", value: "初二" },
        { label: "初三", value: "初三" },
        { label: "高一", value: "高一" },
        { label: "高二", value: "高二" },
        { label: "高三", value: "高三" },
        { label: "中专一年级", value: "中专一年级" },
        { label: "中专二年级", value: "中专二年级" },
        { label: "中专三年级", value: "中专三年级" },
        { label: "大专一年级", value: "大专一年级" },
        { label: "大专二年级", value: "大专二年级" },
        { label: "大专三年级", value: "大专三年级" },
      ],
      legacyGradeLabelMap: {
        "1": "幼儿园",
        "2": "小学",
        "3": "初中",
        "4": "高中",
        "5": "中专",
        "6": "大专",
      },
       // 年级班级相关数据
       expandedRows: [],
       isAllExpanded: false,
       xfptSchoolGradeClass: [],
       totalStudentCount: 0,
       totalClassCount: 0,
       // 详细查看相关数据
       detailForm: {},
       detailXfptSchoolGradeClass: [],
       teamId:null,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        purchaseName: null,
        purchaseYear: null,
        cgTime: null,
        deptId: null,
      },
      // 表单参数
      form: {
        purchaseName: null,
        purchaseYear: null,
        cgTime: null,
        deptId: null,
        ancestors: null,
        xfptSchoolGradeClass: [],
        remark: null,
      },
      // 表单校验
      rules: {
        purchaseName: [
          { required: true, message: "采购名称不能为空", trigger: "blur" },
        ],
        purchaseYear: [
          { required: true, message: "采购年度不能为空", trigger: "blur" },
        ],
        cgTime: [
          { required: true, message: "采购时间不能为空", trigger: "blur" },
        ],
      },
    };
  },
  created() {
    // 初始化数据，防止页面渲染错误
    this.xfptSchoolGradeClass = [];
    this.totalStudentCount = 0;
    this.totalClassCount = 0;
    if(this.$route.query.deptId){
      this.queryParams.deptId = this.$route.query.deptId;
    }
  },
  mounted() {
    // 直接加载列表
    this.getList();
  },
  methods: {
    /** 查询采购流程列表 */
    getList() {
      this.loading = true;
      listCglcprocess(this.queryParams)
        .then((response) => {
          this.purchaseList = response.rows;
          this.total = response.total;
          this.loading = false;
        })
        .catch(() => {
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
        purchaseName: null,
        purchaseYear: null,
        cgTime: null,
        deptId: null,
        ancestors: null,
        xfptSchoolGradeClass: [],
        remark: null,
      };
      this.xfptSchoolGradeClass = [];
      this.expandedRows = [];
      this.isAllExpanded = false;
      this.totalStudentCount = 0;
      this.totalClassCount = 0;
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
      this.isEdit = false;
      this.title = "添加采购流程";
    },
         /** 查看按钮操作 */
     handleView(row) {
       this.formId = row.teamId;
       this.teamId = row.id;
       this.viewOpen = true;
     },
     /** 详细按钮操作 */
    handleDetail(row) {
      const id = row.id;
      getCglcprocess(id).then((response) => {
        this.detailForm = response.data;
        // 如果有年级数据，需要解析
        if (this.detailForm.xfptSchoolGradeClass) {
          try {
            this.detailXfptSchoolGradeClass = this.normalizeGradeRows(
              this.detailForm.xfptSchoolGradeClass
            );
          } catch (error) {
            this.detailXfptSchoolGradeClass = [];
          }
        }
         this.detailOpen = true;
       });
     },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getCglcprocess(id).then((response) => {
        this.form = response.data;
        // 如果有年级数据，需要解析
        if (this.form.xfptSchoolGradeClass) {
          try {
            this.xfptSchoolGradeClass = this.normalizeGradeRows(
              this.form.xfptSchoolGradeClass
            );
          } catch (error) {
            this.xfptSchoolGradeClass = [];
          }
        }
        this.isEdit = true;
        this.open = true;
        this.title = "修改采购流程";
        // 计算统计数据
        this.$nextTick(() => {
          this.calculateTotals();
        });
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          // 验证年级班级信息
          const validGrades = this.validateGradeInfo();
          if (!validGrades) return;
          console.log(this.xfptSchoolGradeClass);
          // 准备提交数据
          const submitData = {
              ...this.form,
              xfptSchoolGradeClass: this.xfptSchoolGradeClass.map((item) => {
                return {
                  ...item,
                  id:
                    item.id && item.id.toString().includes("new_")
                      ? null
                      : item.id,
                  gradeClass: this.normalizeText(item.gradeClass),
                  classes: (item.classes || []).map((cls) => ({
                    ...cls,
                    gradeClass: this.normalizeText(cls.gradeClass),
                    number: cls.number,
                  })),
                };
              }),
            };
          if (this.form.id != null) { 
            updateCglcprocess(submitData).then((response) => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {  
            addCglcprocess(submitData).then((response) => {
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
        .confirm("是否确认删除选中的采购流程数据项？")
        .then(function () {
          return delCglcprocess(ids);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => {});
    },
    tableRowClassName({ row, rowIndex }) {
      // 检查当前行是否被选中
      const isSelected = this.ids.includes(row.id);
      if (isSelected) {
        return "selected-row";
      }
      return "";
    },
    // 年级班级相关方法
    normalizeText(value) {
      return value == null ? "" : String(value).trim();
    },
    getGradeLabel(value) {
      const normalizedValue = this.normalizeText(value);
      return this.legacyGradeLabelMap[normalizedValue] || normalizedValue;
    },
    normalizeGradeRows(rows) {
      return (rows || []).map((grade) => ({
        ...grade,
        gradeClass: this.getGradeLabel(grade.gradeClass),
        classes: (grade.classes || []).map((cls) => ({
          ...cls,
          gradeClass: this.normalizeText(cls.gradeClass),
        })),
      }));
    },
    addGradeDirectly() {
      // 创建一个临时ID，用于自动展开
      const tempId = "new_" + Date.now();

      // 添加新年级
      const newGrade = {
        id: tempId, // 使用临时ID
        gradeClass: "",
        gradeYear: "",
        classes: [
          // 默认添加一条班级信息
          { gradeClass: "", number: "" },
        ],
      };

      this.xfptSchoolGradeClass.push(newGrade);

      // 自动展开新添加的年级行
      this.$nextTick(() => {
        this.expandedRows.push(tempId);
        this.checkAllExpanded();
        // 更新统计数据
        this.calculateTotals();
      });
    },
    addClassDirectly(grade) {
      // 确保grade.classes存在
      if (!grade.classes) {
        grade.classes = [];
      }

      grade.classes.push({
        gradeClass: "",
        number: "",
      });

      // 自动展开包含该班级的年级行
      if (!this.expandedRows.includes(grade.id)) {
        this.expandedRows.push(grade.id);
      }

      // 更新统计数据
      this.calculateTotals();
    },
    // 展开年级
    handleExpandChange(row, expanded) {
      if (!row || !row.id) return;

      if (expanded) {
        this.expandedRows.push(row.id);
        // 检查是否所有行都已展开
        this.checkAllExpanded();
      } else {
        const index = this.expandedRows.indexOf(row.id);
        if (index !== -1) {
          this.expandedRows.splice(index, 1);
        }
        this.isAllExpanded = false;
      }
    },
    checkAllExpanded() {
      if (
        !this.xfptSchoolGradeClass ||
        this.xfptSchoolGradeClass.length === 0
      ) {
        this.isAllExpanded = false;
        return;
      }

      // 检查是否所有年级都已展开
      this.isAllExpanded = this.xfptSchoolGradeClass.every(
        (grade) => grade.id && this.expandedRows.includes(grade.id)
      );
    },
    expandAll() {
      // 确保xfptSchoolGradeClass存在
      if (
        !this.xfptSchoolGradeClass ||
        this.xfptSchoolGradeClass.length === 0
      ) {
        return;
      }
      this.expandedRows = this.xfptSchoolGradeClass.map((grade) => grade.id);
      this.isAllExpanded = true;
    },
    collapseAll() {
      this.expandedRows = [];
      this.isAllExpanded = false;
    },
    calculateTotals() {
      // 确保xfptSchoolGradeClass不为null
      if (!this.xfptSchoolGradeClass) {
        this.totalClassCount = 0;
        this.totalStudentCount = 0;
        return;
      }

      this.totalClassCount = this.xfptSchoolGradeClass.reduce(
        (total, grade) => {
          return total + (grade.classes?.length || 0);
        },
        0
      );

      this.totalStudentCount = this.xfptSchoolGradeClass.reduce(
        (total, grade) => {
          return total + this.getTotalClassCount(grade);
        },
        0
      );

      // 更新页面显示
      this.$nextTick(() => {
        console.log("统计数据已更新:", {
          totalClassCount: this.totalClassCount,
          totalStudentCount: this.totalStudentCount,
        });
      });
    },
    deleteGrade(index) {
      const grade = this.xfptSchoolGradeClass[index];
      if (!grade) return;

      // 如果是新添加的年级，没有id，直接从数组中删除
      if (!grade.id || grade.id.toString().startsWith("new_")) {
        this.xfptSchoolGradeClass.splice(index, 1);
        this.$message.success("删除成功");
        // 更新统计数据
        this.calculateTotals();
        return;
      }

      // 显示确认对话框
      this.$confirm(
        `确认删除年级【${grade.gradeClass}】吗？删除后将无法恢复`,
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      )
        .then(() => {
          this.xfptSchoolGradeClass.splice(index, 1);
          this.$message.success("删除成功");
          // 重新计算总数
          this.calculateTotals();
        })
        .catch(() => {
          // 取消删除操作
          this.$message.info("已取消删除");
        });
    },
    deleteClass(grade, index) {
      // 确保grade和grade.classes存在
      if (!grade || !grade.classes) {
        return;
      }

      // 确保至少保留一个班级
      if (grade.classes.length <= 1) {
        this.$message.warning("每个年级至少需要保留一个班级");
        return;
      }

      const cls = grade.classes[index];

      // 如果是新添加的班级，没有id，直接从数组中删除
      if (!cls.id) {
        grade.classes.splice(index, 1);
        this.$message.success("删除班级成功");
        // 更新统计数据
        this.calculateTotals();
        return;
      }

      // 显示确认对话框
      this.$confirm(
        `确认删除班级【${grade.gradeClass} ${cls.gradeClass}】吗？删除后将无法恢复`,
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      )
        .then(() => {
          grade.classes.splice(index, 1);
          this.$message.success("删除班级成功");
          // 重新计算总数
          this.calculateTotals();
        })
        .catch(() => {
          // 取消删除操作
          this.$message.info("已取消删除");
        });
    },
    getTotalClassCount(grade) {
      // 确保grade.classes存在
      if (!grade || !grade.classes) {
        return 0;
      }
      return grade.classes.reduce(
        (sum, cls) => sum + (parseInt(cls.number) || 0),
        0
      );
    },
    validateGradeInfo() {
      // 验证年级信息
      if (
        !this.xfptSchoolGradeClass ||
        this.xfptSchoolGradeClass.length === 0
      ) {
        this.$message.error("没有年级信息可保存");
        return false;
      }

      for (const grade of this.xfptSchoolGradeClass) {
        if (
          grade.gradeYear == null ||
          grade.gradeYear == undefined ||
          grade.gradeYear == ""
        ) {
          this.$message.error("请选择学年");
          return false;
        }

        if (!this.normalizeText(grade.gradeClass)) {
          this.$message.error("请选择年级名称");
          return false;
        }

        // 确保grade.classes存在
        if (!grade.classes || grade.classes.length === 0) {
          this.$message.error(`${grade.gradeClass}没有班级信息`);
          return false;
        }

        for (const cls of grade.classes) {
          if (!this.normalizeText(cls.gradeClass)) {
            this.$message.error("请输入班级名称");
            return false;
          }

          if (!cls.number) {
            this.$message.error("请输入班级人数");
            return false;
          }
        }
      }

      return true;
    },
  },
};
</script>
<style lang="scss" scoped>
::v-deep .el-dialog__header {
  padding-bottom: 0px !important;
  padding: 15px 20px 10px;
}
::v-deep .el-dialog__body {
  padding: 10px 20px;
}
::v-deep .pagination-container {
  background-color: none;
  background: none;
}
.form-label {
  display: inline-block;
  margin-bottom: 10px;
  font-size: 14px;
  color: #606266;
  line-height: 40px;
  padding: 0 12px 0 0;
  box-sizing: border-box;
  text-align: right;
}
.el-form-item__error {
  color: #f56c6c;
  font-size: 12px;
  line-height: 1;
  padding-top: 4px;
  position: absolute;
  left: 0;
  right: 0;
  text-align: center;
}
.required-field:before {
  content: "*";
  color: #f56c6c;
  margin-right: 4px;
}

// 选中行的样式
::v-deep .el-table .selected-row {
  background-color: #e6e6e6 !important;
}

::v-deep .el-table .selected-row:hover {
  background-color: #e6e6e6;
}

// 文本域字体样式
::v-deep .el-textarea__inner {
  font-family: "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
  font-weight: normal;
}

// 年级班级管理样式
.class-manage-expanded {
  padding: 10px 20px;
  background-color: #f5f7fa;
  border-left: 4px solid #ebeef5;
  margin: 0 50px 0 0;
  width: 100%;
}

.class-header,
.class-row {
  display: grid;
  grid-template-columns: 20fr 60fr 120px;
  gap: 10px;
  margin-bottom: 10px;
  align-items: center;
  width: 100%;
}

.class-name-header,
.class-number-header,
.class-action-header {
  text-align: center;
  padding: 0 10px;
  font-weight: bold;
}

.class-action-header {
  display: flex;
  justify-content: center;
}

.class-name,
.class-number,
.class-action {
  display: flex;
  justify-content: center;
  padding: 0 10px;
}

.required-field {
  color: #f56c6c;
  margin-right: 3px;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.class-header {
  font-weight: bold;
  color: #909399;
  font-size: 13px;
  margin-bottom: 15px;
  border-bottom: 1px dashed #d4d7de;
  padding-bottom: 8px;
}

.class-row {
  margin-bottom: 5px;
  border-bottom: 1px solid rgba(235, 238, 245, 0.3);
  padding: 5px 0;
}

.class-row:hover {
  background-color: rgba(240, 242, 247, 0.3);
  border-radius: 2px;
}

.class-name,
.class-number,
.class-action {
  display: flex;
  justify-content: center;
  padding: 0 10px;
}

.add-class-row {
  margin-top: 15px;
  text-align: center;
}

.class-count-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
}

.class-count-info div {
  margin: 3px 0;
  text-align: center;
  width: 100%;
}

.stats-action-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 15px 20px;
}

.statistics-panel {
  display: flex;
  gap: 40px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 15px;
  border-right: 1px solid #ebeef5;
}

.stat-item:last-child {
  border-right: none;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-unit {
  font-size: 14px;
  color: #909399;
  margin-left: 2px;
}

/* 美化表格样式 */
.el-table {
  margin-top: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

/* 底部保存按钮区域 */
.save-button-container {
  text-align: center;
  margin-top: 20px;
  padding: 15px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

/* 整体容器样式 */
.app-container {
  // background-color: #f5f7fa;
  padding: 20px;
  min-height: calc(100vh - 100px);
}
</style>
