<template>
  <div class="app-container">
    <!-- -->

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
            {{ schoolGrades ? schoolGrades.length : 0
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
        <!-- <el-button size="mini" type="warning" plain icon="el-icon-setting"
          @click="showDeptSettingDialog">设置部门信息</el-button> -->
      </div>
    </div>

    <!-- <div style="text-align: center;margin-top: 10px;">
      <el-button type="primary" @click="saveClassInfo">保 存</el-button>
    </div> -->

    <!-- 部门信息设置对话框 -->
    <el-dialog
      title="设置部门信息"
      :visible.sync="deptSettingVisible"
      width="30%"
    >
      <el-form :model="deptForm" label-width="80px">
        <el-form-item label="部门ID">
          <el-input
            v-model="deptForm.deptId"
            placeholder="请输入部门ID"
          ></el-input>
        </el-form-item>
        <el-form-item label="部门名称">
          <el-input
            v-model="deptForm.deptName"
            placeholder="请输入部门名称"
          ></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deptSettingVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveDeptSetting">确 定</el-button>
      </span>
    </el-dialog>

    <el-table
      :data="schoolGrades || []"
      class="grade-table"
      row-key="id"
      :expand-row-keys="expandedRows"
    >
      <el-table-column type="expand" width="50">
        <template slot-scope="props">
          <div class="class-manage-expanded">
            <div class="class-header">
              <div class="class-name-header">
                <span class="required-field">*</span> 班级名称
              </div>
              <div class="class-number-header">
                <span class="required-field">*</span> 班级人数
              </div>
              <div class="class-number-header">状态</div>
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
                <el-select
                  v-model="cls.gradeClass"
                  placeholder="班级名称"
                  size="small"
                  style="width: 160px"
                >
                  <el-option
                    v-for="i in 30"
                    :key="i"
                    :label="`${i}班`"
                    :value="`${i}班`"
                  >
                  </el-option>
                </el-select>
              </div>
              <div class="class-number">
                <el-input
                  v-model="cls.number"
                  placeholder="请输入班级人数"
                  size="small"
                  style="width: 160px"
                ></el-input>
              </div>
              <div class="class-number">
                <el-switch
                  v-model="cls.status"
                  :active-value="'0'"
                  :inactive-value="'1'"
                  active-color="#13ce66"
                  inactive-color="#ff4949"
                >
                </el-switch>
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
            <div style="margin-right: 10px; width: 140px">
              <el-date-picker
                style="width: 140px"
                v-model="scope.row.gradeYear"
                type="year"
                format="yyyy"
                value-format="yyyy"
                placeholder="选择学年"
              >
              </el-date-picker>
            </div>
            <div>
              <el-select
                v-model="scope.row.gradeClass"
                placeholder="请选择"
                style="width: 160px"
              >
                <el-option
                  v-for="item in dict.type.xd"
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
            班级共计：{{ scope.row.classes ? scope.row.classes.length : 0 }}个，
            人数共计：{{ getTotalClassCount(scope.row) }}人
          </div>
        </template>
      </el-table-column>
      <el-table-column label="班级状态" align="center" min-width="20%">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            :active-value="'0'"
            :inactive-value="'1'"
            active-color="#13ce66"
            inactive-color="#ff4949"
          >
          </el-switch>
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
    <div style="text-align: center; margin-top: 20px">
      <el-button type="primary" size="medium" @click="saveClassInfo"
        >保存年级与班级信息</el-button
      >
    </div>
  </div>
</template>

<script>
import { getInfo } from "@/api/login";
import {
  listQuestionnaire,
  gradeList,
  addGradeClass,
  updateGradeClass,
  delGradeClass,
} from "@/api/school/son";
export default {
  name: "RuoyiXxIndex",
  dicts: ["xd"],
  data() {
    return {
      dialogVisible: false,
      classManageVisible: false,
      addGradeDialogVisible: false,
      deptSettingVisible: false,
      deptForm: {
        deptId: "",
        deptName: "",
      },
      totalStudents: 0,
      expandRowKeys: [],
      expandedRows: [],
      isAllExpanded: false,
      gradeData: [],
      schoolGrades: [],
      newGrade: {
        name: "",
        alias: "",
        classes: [],
      },
      totalStudentCount: 0,
      totalClassCount: 0,
      addInfo: {
        deptId: "",
        deptName: "",
        gradeClass: "",
        classes: [],
      },
      _warnedAboutDeptInfo: false,
    };
  },

  created() {
    // 初始化数据，防止页面渲染错误
    this.schoolGrades = [];
    this.gradeData = [];
    this.totalStudentCount = 0;
    this.totalClassCount = 0;
  },

  mounted() {
    // 先获取用户信息，然后再加载年级列表
    this.getUserInfo();
  },

  methods: {
    getUserInfo() {
      // 首先尝试从localStorage获取部门信息
      const cachedDeptInfo = localStorage.getItem("deptInfo");
      if (cachedDeptInfo) {
        try {
          const deptInfo = JSON.parse(cachedDeptInfo);
          this.addInfo.deptId = deptInfo.deptId;
          this.addInfo.deptName = deptInfo.deptName;
          console.log("从缓存获取的部门信息:", this.addInfo);

          // 使用缓存的部门信息加载年级列表
          this.gradeList();
          return;
        } catch (e) {
          console.error("解析缓存的部门信息失败:", e);
          // 继续从接口获取
        }
      }

      // 从接口获取用户信息
      getInfo()
        .then((res) => {
          if (res && res.user) {
            // 根据实际API响应结构获取部门信息
            // 可能是res.user.dept或直接在res.user中
            if (res.user.dept) {
              this.addInfo.deptId = res.user.dept.deptId;
              this.addInfo.deptName = res.user.dept.deptName;
            } else {
              this.addInfo.deptId = res.user.deptId;
              this.addInfo.deptName = res.user.deptName;
            }

            console.log("获取到的部门信息:", this.addInfo);

            // 将部门信息缓存到localStorage
            if (this.addInfo.deptId && this.addInfo.deptName) {
              localStorage.setItem(
                "deptInfo",
                JSON.stringify({
                  deptId: this.addInfo.deptId,
                  deptName: this.addInfo.deptName,
                })
              );
            }

            // 获取到用户信息后再加载年级列表
            this.gradeList();
          } else {
            console.error("未获取到用户部门信息");
            this.$message.warning("未获取到部门信息，可能影响保存");
            // 即使没有获取到部门信息，也尝试加载年级列表
            this.gradeList();
          }
        })
        .catch((error) => {
          console.error("获取用户信息失败:", error);
          this.$message.error("获取用户信息失败，请刷新页面重试");
          // 出错时也尝试加载年级列表
          this.gradeList();
        });
    },
    gradeList() {
      gradeList({
        deptId: this.$store.state.user.deptId,
      })
        .then((res) => {
          console.log(res);
          // 确保数据不为null
          this.gradeData = res.data || [];
          this.schoolGrades = res.data || [];

          // 数据加载完成后计算总数
          this.$nextTick(() => {
            this.calculateTotals();
          });
        })
        .catch((error) => {
          console.error("获取年级列表失败:", error);
          this.$message.error("获取年级列表失败");
          // 确保在出错时也初始化为空数组
          this.gradeData = [];
          this.schoolGrades = [];

          // 即使出错也需要计算总数(清零)
          this.calculateTotals();
        });
    },
    handleExpand(row) {
      if (row.expanded) {
        // 收起行
        this.expandRowKeys = this.expandRowKeys.filter((key) => key !== row.id);
      } else {
        // 展开行
        this.expandRowKeys.push(row.id);
      }
      row.expanded = !row.expanded;
    },
    handleGradeSelect(row) {
      // 如果是年级，同时选中所有班级
      if (row.children && row.children.length > 0) {
        row.children.forEach((child) => {
          child.selected = row.selected;
        });
      }
      this.calculateTotal();
    },
    calculateTotal() {
      let total = 0;
      this.gradeData.forEach((grade) => {
        if (grade.selected) {
          total += grade.studentCount;
        } else if (grade.children) {
          grade.children.forEach((cls) => {
            if (cls.selected) {
              total += cls.studentCount;
            }
          });
        }
      });
      this.totalStudents = total;
    },
    showClassManageDialog() {
      this.classManageVisible = true;
      this.calculateTotals();
    },
    addGradeDirectly() {
      // 获取当前部门信息，如不存在则使用空字符串
      const deptId = this.addInfo.deptId || "";
      const deptName = this.addInfo.deptName || "";

      // 创建一个临时ID，用于自动展开
      const tempId = "new_" + Date.now();

      // 添加新年级
      const newGrade = {
        id: tempId, // 使用临时ID
        deptId: deptId,
        deptName: deptName,
        gradeClass: "",
        gradeYear: "",
        status: "0",
        classes: [
          // 默认添加一条班级信息
          { gradeClass: "1班", number: "", status: "0" },
        ],
      };

      this.schoolGrades.push(newGrade);

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

      // 确保grade有部门信息
      if (!grade.deptId) {
        grade.deptId = this.addInfo.deptId;
      }
      if (!grade.deptName) {
        grade.deptName = this.addInfo.deptName;
      }

      grade.classes.push({
        gradeClass: "",
        number: "",
        status: "0",
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
      if (!this.schoolGrades || this.schoolGrades.length === 0) {
        this.isAllExpanded = false;
        return;
      }

      // 检查是否所有年级都已展开
      this.isAllExpanded = this.schoolGrades.every(
        (grade) => grade.id && this.expandedRows.includes(grade.id)
      );
    },
    expandAll() {
      // 确保schoolGrades存在
      if (!this.schoolGrades || this.schoolGrades.length === 0) {
        return;
      }
      this.expandedRows = this.schoolGrades.map((grade) => grade.id);
      this.isAllExpanded = true;
    },
    collapseAll() {
      this.expandedRows = [];
      this.isAllExpanded = false;
    },
    calculateTotals() {
      // 确保schoolGrades不为null
      if (!this.schoolGrades) {
        this.totalClassCount = 0;
        this.totalStudentCount = 0;
        return;
      }

      this.totalClassCount = this.schoolGrades.reduce((total, grade) => {
        return total + (grade.classes?.length || 0);
      }, 0);

      this.totalStudentCount = this.schoolGrades.reduce((total, grade) => {
        return total + this.getTotalClassCount(grade);
      }, 0);

      // 更新页面显示
      this.$nextTick(() => {
        console.log("统计数据已更新:", {
          totalClassCount: this.totalClassCount,
          totalStudentCount: this.totalStudentCount,
        });
      });
    },

    deleteGrade(index) {
      const grade = this.schoolGrades[index];
      if (!grade) return;

      // 如果是新添加的年级，没有id，直接从数组中删除
      if (!grade.id || grade.id.toString().startsWith("new_")) {
        this.schoolGrades.splice(index, 1);
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
          // 调用删除接口
          delGradeClass(grade.id)
            .then((res) => {
              if (res.code === 200) {
                this.schoolGrades.splice(index, 1);
                this.$message.success("删除成功");
                // 重新计算总数
                this.calculateTotals();
              } else {
                this.$message.error(res.msg || "删除失败");
              }
            })
            .catch((err) => {
              console.error("删除年级失败:", err);
              this.$message.error("删除年级失败");
            });
        })
        .catch(() => {
          // 取消删除操作
          this.$message.info("已取消删除");
        });
    },
    addClass(grade) {
      grade.classes.push({
        gradeClass: "",
        number: "",
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
        `确认删除班级【${grade.gradeClass}${cls.gradeClass}】吗？删除后将无法恢复`,
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      )
        .then(() => {
          // 调用删除班级接口
          // 注意：如果后端没有单独的删除班级接口，可以使用更新年级信息接口，将不需要的班级从列表中删除后提交
          grade.classes.splice(index, 1);

          // 准备年级数据
          const gradeData = {
            deptId: grade.deptId || "",
            deptName: grade.deptName || "",
            id: grade.id,
            gradeClass: grade.gradeClass,
            gradeYear: grade.gradeYear,
            status: grade.status,
            classes: grade.classes.map((c) => ({
              id: c.id,
              gradeClass: c.gradeClass,
              number: c.number,
              status: c.status,
            })),
          };

          if (grade.id) {
            // 有id，调用编辑接口
            updateGradeClass(gradeData)
              .then((res) => {
                if (res.code === 200) {
                  this.$message.success("删除班级成功");
                  // 重新计算总数
                  this.calculateTotals();
                } else {
                  // 如果更新失败，恢复删除的班级
                  grade.classes.splice(index, 0, cls);
                  this.$message.error(res.msg || "删除班级失败");
                }
              })
              .catch((err) => {
                // 如果出错，恢复删除的班级
                grade.classes.splice(index, 0, cls);
                console.error("删除班级失败:", err);
                this.$message.error("删除班级失败");
              });
          } else {
            // 没有id，调用新增接口
            addGradeClass(gradeData)
              .then((res) => {
                if (res.code === 200) {
                  this.$message.success(`${grade.gradeClass}信息添加成功`);
                } else {
                  this.$message.error(res.msg || "添加失败");
                }
              })
              .catch((err) => {
                console.error(err);
                this.$message.error("添加年级信息失败");
              });
          }
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
    saveClassInfo() {
      // 保存前检查部门信息
      if (!this.addInfo.deptId || !this.addInfo.deptName) {
        this.$confirm("未设置部门信息，是否现在设置?", "提示", {
          confirmButtonText: "是",
          cancelButtonText: "否",
          type: "warning",
        })
          .then(() => {
            // 打开部门设置对话框
            this.showDeptSettingDialog();
          })
          .catch(() => {
            // 用户选择不设置，继续保存
            this.doSaveClassInfo();
          });
      } else {
        // 有部门信息，直接保存
        this.doSaveClassInfo();
      }
    },

    // 实际执行保存操作的方法
    doSaveClassInfo() {
      // 保存班级信息
      const validGrades = this.validateGradeInfo();
      if (!validGrades) return;

      // 更新年级班级数据
      this.updateGradeData();

      // 获取并记录部门信息
      console.log("保存前的部门信息:", this.addInfo);

      // 遍历每个年级，根据id判断是新增还是编辑
      for (const grade of this.schoolGrades) {
        const hasId =
          grade.id !== undefined && grade.id !== null && grade.id !== "";

        // 确保grade的deptId和deptName不为undefined
        if (!grade.deptId) grade.deptId = "";
        if (!grade.deptName) grade.deptName = "";

        // 准备当前年级的数据
        const gradeData = {
          deptId: this.$store.state.user.deptId,
          deptName: grade.deptName,
          gradeClass: grade.gradeClass,
          gradeYear: grade.gradeYear,
          status: grade.status,
          id:
            typeof grade.id === "string" && grade.id.includes("new_")
              ? null
              : grade.id,
          classes: grade.classes.map((cls) => ({
            id: cls.id, // 如果是新增，这里可能是undefined
            gradeClass: cls.gradeClass,
            number: cls.number,
            status: cls.status,
          })),
        };
        console.log("准备发送的数据:", gradeData);
        // 没有id，调用新增接口
        addGradeClass(gradeData)
          .then((res) => {
            if (res.code === 200) {
              this.$message.success(`${grade.gradeClass}信息添加成功`);
            } else {
              this.$message.error(res.msg || "添加失败");
            }
          })
          .catch((err) => {
            console.error(err);
            this.$message.error("添加年级信息失败");
          });
      }
      // 操作完成后重新加载列表
      setTimeout(() => {
        this.gradeList();
      }, 500);
    },
    validateGradeInfo() {
      // 验证年级信息
      if (!this.schoolGrades || this.schoolGrades.length === 0) {
        this.$message.error("没有年级信息可保存");
        return false;
      }

      // 检查deptId和deptName是否已获取，如果没有，使用默认值或空字符串
      const deptId = this.addInfo.deptId || "";
      const deptName = this.addInfo.deptName || "";

      // 只在保存前第一次检查时记录警告，避免多次提示
      if ((!deptId || !deptName) && !this._warnedAboutDeptInfo) {
        this.$message.warning("部门信息未完全获取，可能影响数据保存");
        console.warn("部门信息不完整：", { deptId, deptName });
        this._warnedAboutDeptInfo = true;
      }

      for (const grade of this.schoolGrades) {
        // 确保每个年级都有部门信息
        grade.deptId = deptId;
        grade.deptName = deptName;
        if (
          grade.gradeYear == null ||
          grade.gradeYear == undefined ||
          grade.gradeYear == ""
        ) {
          this.$message.error("请选择学年");
          return false;
        }

        if (!grade.gradeClass) {
          this.$message.error("请选择年级名称");
          return false;
        }

        // 确保grade.classes存在
        if (!grade.classes || grade.classes.length === 0) {
          this.$message.error(`${grade.gradeClass}没有班级信息`);
          return false;
        }

        for (const cls of grade.classes) {
          if (!cls.gradeClass) {
            this.$message.error("请选择班级名称");
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
    updateGradeData() {
      // 从schoolGrades更新到gradeData
      if (!this.schoolGrades || this.schoolGrades.length === 0) {
        this.gradeData = [];
        return;
      }

      this.gradeData = this.schoolGrades.map((grade) => {
        const totalCount = this.getTotalClassCount(grade);
        return {
          id: grade.id,
          selected: false,
          gradeName: grade.gradeClass,
          studentCount: totalCount,
          expanded: false,
          children: grade.classes
            ? grade.classes.map((cls, index) => ({
                id: grade.id * 100 + index,
                selected: false,
                gradeName: grade.gradeClass + cls.gradeClass,
                studentCount: parseInt(cls.number) || 0,
              }))
            : [],
        };
      });

      this.calculateTotal();
    },
    // 显示部门设置对话框
    showDeptSettingDialog() {
      // 将当前部门信息填充到表单
      this.deptForm.deptId = this.addInfo.deptId || "";
      this.deptForm.deptName = this.addInfo.deptName || "";
      this.deptSettingVisible = true;
    },

    // 保存部门设置
    saveDeptSetting() {
      if (!this.deptForm.deptId || !this.deptForm.deptName) {
        this.$message.warning("部门ID和部门名称不能为空");
        return;
      }

      // 更新部门信息
      this.addInfo.deptId = this.deptForm.deptId;
      this.addInfo.deptName = this.deptForm.deptName;

      // 缓存到localStorage
      localStorage.setItem(
        "deptInfo",
        JSON.stringify({
          deptId: this.addInfo.deptId,
          deptName: this.addInfo.deptName,
        })
      );

      this.$message.success("部门信息设置成功");
      this.deptSettingVisible = false;

      // 重置警告标记，允许下次保存时不再显示警告
      this._warnedAboutDeptInfo = false;
    },
  },
};
</script>
<style scoped>
.detail-panel {
  padding: 20px 0;
}

.detail-panel h3 {
  margin-top: 0;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.survey-questions {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 10px;
  max-height: 300px;
  overflow-y: auto;
}

.question-item {
  margin-bottom: 15px;
}

.question {
  font-weight: bold;
  margin-bottom: 5px;
}

.options {
  display: flex;
  flex-wrap: wrap;
}

.option {
  margin-right: 20px;
  margin-bottom: 5px;
}

.grade-summary {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  color: #606266;
}

.update-tip {
  color: #409eff;
  cursor: pointer;
}

.expandable-label {
  cursor: pointer;
  display: flex;
  align-items: center;
}

.class-list {
  padding-left: 30px;
  margin-bottom: 10px;
}

.child-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.student-number {
  margin-right: 20px;
}

.class-manage-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

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
  grid-template-columns: 20fr 60fr 18fr 120px;
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
  background-color: #f5f7fa;
  padding: 20px;
  min-height: calc(100vh - 100px);
}
</style>
