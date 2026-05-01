<template>
  <div class="detail-panel">
    <div class="header" style="display: flex; align-items: center">
      <h3>选用组织名单管理报备与公开</h3>
      <div style="margin-left: 20px">
        <el-button
          type="primary"
          icon="el-icon-plus"
          v-hasPermi="['xfpg:zzmdbbgk-add']"
          @click="handleAdd"
          v-if="discloseList.length == 0"
          >选用组织报备</el-button
        >
      </div>
    </div>


    <el-table
      v-loading="loading"
      :data="discloseList"
    >
      <el-table-column label="序号" align="center" type="index" />
      <el-table-column label="选用组织名单标题" align="center" prop="title" />
      <el-table-column label="总人数" align="center" prop="total" />
      <el-table-column label="创建时间" align="center" prop="createTime" />

      <el-table-column
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <el-button
            type="text"
            icon="el-icon-download"
            @click="handleDownload(scope.row)"
            >导出选用组织名单</el-button
          >
          <el-button
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            >查看</el-button
          >
          <el-button
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['xfpg:zzmdbbgk-edit']"
            >编辑</el-button
          >
          <el-button
            type="text"
            icon="el-icon-delete"
            v-hasPermi="['xfpg:zzmdbbgk-remove']"
            @click="handleDelete(scope.row)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table> 

    <!-- 添加或修改选用组织名单报备与公开对话框 -->
    <el-dialog
      :title="title"
      :visible.sync="open"
      width="1200px"
      append-to-body
    >
      <el-form ref="form" :model="form" :rules="rules">
        <!-- 选用组织名单标题 -->
        <el-form-item label="选用组织名单标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入选用组织名单标题" />
        </el-form-item>

        <div style="margin-bottom: 10px">
          <span style="margin-right: 10px">选用组织名单：</span>
          <el-button type="primary" size="small" @click.stop="importTemplate"
            >下载模板</el-button
          >
          <el-button type="primary" size="small" @click="importList"
            >导入选用组织名单</el-button
          >
        </div>

        <!-- 表格形式的成员列表 -->
        <div class="member-table">
          <el-table :data="form.sonDataList" border style="width: 100%">
            <el-table-column label="序号" width="60" align="center">
              <template slot-scope="scope">
                {{ scope.$index + 1 }}
              </template>
            </el-table-column>

            <el-table-column label="职务">
              <template slot-scope="scope">
                <el-form-item
                  :prop="'sonDataList.' + scope.$index + '.zw'"
                  :rules="rules.zw"
                  class="no-margin"
                >
                  <el-input
                    v-if="scope.row.zw == '2'"
                    value="组长"
                    disabled
                    placeholder="请输入职务"
                  />
                  <el-select
                    v-else
                    v-model="scope.row.zw"
                    placeholder="请选择职务"
                    style="width: 100%"
                    @change="handlePositionChange(scope.row, scope.$index)"
                  >
                    <el-option
                      v-for="item in dict.type.sc_zw.filter(
                        (item) => item.value != '2'
                      )"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column label="代表类别">
              <template slot="header">
                <span style="color: #f56c6c">*</span>代表类别
              </template>
              <template slot-scope="scope">
                <el-form-item
                  :prop="'sonDataList.' + scope.$index + '.dblb'"
                  :rules="rules.dblb"
                  class="no-margin"
                >
                  <el-select
                    placeholder="请选择代表类别"
                    v-model="scope.row.dblb"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="item in dict.type.sc_dble"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </template>
            </el-table-column>

            <el-table-column label="姓名">
              <template slot="header">
                <span style="color: #f56c6c">*</span>姓名
              </template>
              <template slot-scope="scope">
                <el-form-item
                  :prop="'sonDataList.' + scope.$index + '.xm'"
                  :rules="rules.xm"
                  class="no-margin"
                >
                  <el-input v-model="scope.row.xm" placeholder="请输入姓名" />
                </el-form-item>
              </template>
            </el-table-column>

            <el-table-column label="手机号">
              <template slot="header"> 手机号 </template>
              <template slot-scope="scope">
                <el-form-item class="no-margin">
                  <el-input
                    v-model="scope.row.phone"
                    placeholder="请输入手机号"
                  />
                </el-form-item>
              </template>
            </el-table-column>

            <el-table-column label="年级">
              <template slot-scope="scope">
                <el-form-item
                  :prop="'sonDataList.' + scope.$index + '.njZy'"
                  :rules="rules.njZy"
                  class="no-margin"
                >
                  <el-input
                    v-model="scope.row.njZy"
                    placeholder="请输入年级"
                  />
                </el-form-item>
              </template>
            </el-table-column>

            <el-table-column label="班级">
              <template slot-scope="scope">
                <el-form-item
                  :prop="'sonDataList.' + scope.$index + '.bj'"
                  :rules="rules.bj"
                  class="no-margin"
                >
                  <el-input v-model="scope.row.bj" placeholder="请输入班级" />
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column align="center" label="操作">
              <template slot-scope="scope">
                <div class="action-buttons">
                  <el-button
                    type="primary"
                    icon="el-icon-plus"
                    circle
                    size="mini"
                    v-if="scope.$index === form.sonDataList.length - 1"
                    @click="addMember"
                  ></el-button>
                  <el-button
                    :disabled="scope.row.zw == '2'"
                    type="danger"
                    icon="el-icon-minus"
                    circle
                    size="mini"
                    @click="removeMember(scope.$index, scope.row)"
                    v-show="form.sonDataList.length > 1"
                  ></el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- Summary Text -->
        <div
          class="summary-section"
          :class="{ warning: representativePercentage < 80 }"
        >
          本次选用组织总共{{
            totalsonDataList
          }}人，其中家长学生代表（含家长委员会代表、家长代表、学生代表）共{{
            representativesonDataList
          }}人，占选用组织名单总人数的{{ representativePercentage }}%
          <div v-if="representativePercentage < 80" class="warning-text">
            <i class="el-icon-warning"></i> 警告：家长学生代表占比不能低于80%
          </div>
        </div>
        <el-form-item label="附件" prop="file" style="margin-top: 10px">
          <FileUpload
            v-model="form.file"
            :fileType="[
              'pdf',
              'doc',
              'docx',
              'xls',
              'xlsx',
              'ppt',
              'pptx',
              'txt',
              'jpg',
              'jpeg',
              'png',
            ]"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 查看选用组织名单对话框 -->
    <el-dialog
      title="查看选用组织名单"
      :visible.sync="viewDialogVisible"
      width="1000px"
      append-to-body
    >
      <el-alert type="info" :closable="false" show-icon>
        <div style="line-height: 1.5; font-size: 14px">
          本次选用组织共{{
            viewData.length
          }}人，其中家长学生代表（含家长委员会代表、家长代表、学生代表）共{{
            representTotal(viewData)
          }}人，占选用组织总人数的{{ representPercentage(viewData) }}%
        </div>
      </el-alert>
      <el-table :data="viewData" border style="width: 100%; margin-top: 15px">
        <el-table-column label="序号" width="60" align="center">
          <template slot-scope="scope">
            {{ scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="职务" prop="zw" align="center">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.sc_zw" :value="scope.row.zw" />
          </template>
        </el-table-column>
        <el-table-column label="代表类别" align="center">
          <template slot-scope="scope">
            {{ getDictLabel(scope.row.dblb) }}
          </template>
        </el-table-column>
        <el-table-column label="姓名" prop="xm" align="center" />
        <el-table-column label="手机号" prop="phone" align="center" />
        <el-table-column label="年级" prop="njZy" align="center" />
        <el-table-column label="班级" prop="bj" align="center" />
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button @click="viewDialogVisible = false">关 闭</el-button>
      </div>
    </el-dialog>

    <el-dialog
      :title="upload.title"
      :visible.sync="upload.open"
      width="400px"
      append-to-body
    >
      <el-upload
        ref="upload"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip text-center" slot="tip">
          <div class="el-upload__tip" slot="tip">
            <el-checkbox
              v-model="upload.updateSupport"
            />是否更新已经存在的用户数据
          </div>
          <span>仅允许导入xls、xlsx格式文件。</span>
          <el-link
            type="primary"
            :underline="false"
            style="font-size: 12px; vertical-align: baseline"
            @click.stop="importTemplate"
            >下载模板</el-link
          >
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getToken } from "@/utils/auth";

import {
  listDisclose,
  getDisclose,
  delDisclose,
  addDisclose,
  updateDisclose,
  delSonData,
} from "@/api/cg/OrganizationList";

// Basic phone number validation (adjust regex as needed)
const validatePhone = (rule, value, callback) => {
  const reg = /^1[3-9]\d{9}$/;
  if (value && !reg.test(value)) {
    callback(new Error("手机格式不正确"));
  } else {
    callback();
  }
};

export default {
  name: "Disclose",
  dicts: ["sc_dble", "sc_zw"],
  data() {
    return {
      upload: {
        // 是否显示弹出层（用户导入）
        open: false,
        // 弹出层标题（用户导入）
        title: "",
        // 是否禁用上传
        isUploading: false,
        // 是否更新已经存在的用户数据
        updateSupport: 0,
        // 设置上传的请求头部
        headers: { Authorization: "Bearer " + getToken() },
        // 上传的地址
        url:
          process.env.VUE_APP_BASE_API +
          "/school/org/report/disclose/importTmpData",
      },
      // 遮罩层
      loading: true,

      // 总条数
      total: 0,
      // 选用组织名单报备与公开表格数据
      discloseList: [{}],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        // Removed old fields, adjust if list filtering needs them
        title: null,
        teamMeetingId: null,
      },


      form: {
        title: "",
        sonDataList: [], // Array to hold organization sonDataList
        teamMeetingId: null,
        file: "",
      },
      // 表单校验
      rules: {
        title: [
          {
            required: true,
            message: "选用组织名单标题不能为空",
            trigger: "blur",
          },
        ],
        zw: [{ required: true, message: "职务不能为空", trigger: "change" }],
        dblb: [
          { required: true, message: "代表类别不能为空", trigger: "change" },
        ],
        xm: [{ required: true, message: "姓名不能为空", trigger: "blur" }],
        phone: [
          { validator: validatePhone, trigger: "blur" },
        ],
        njZy: [],
        bj: [],
      },
      viewDialogVisible: false,
      viewData: [],
    };
  },
  props: {
    formId: {
      required: true,
    },
  },
  computed: {
    totalsonDataList() {
      return this.form.sonDataList ? this.form.sonDataList.length : 0;
    },
    representativesonDataList() {
      if (!this.form.sonDataList) return 0;
      const representativeCategories = [
        "0", // Assuming 1 for 家长委员会代表
        "5", // Assuming 2 for 家长代表
        "4", // Assuming 3 for 学生代表
      ];
      return this.form.sonDataList.filter((member) =>
        representativeCategories.includes(member.dblb)
      ).length;
    },
    representativePercentage() {
      if (this.totalsonDataList === 0) return 0;
      return (
        (this.representativesonDataList / this.totalsonDataList) *
        100
      ).toFixed(0); // Or toFixed(2) for decimals
    },
  },
  created() {
    this.getList();
  },
  methods: {
    representTotal(list) {
      if (!list) return 0;
      const representativeCategories = [
        "0", // Assuming 1 for 家长委员会代表
        "5", // Assuming 2 for 家长代表
        "4", // Assuming 3 for 学生代表
      ];
      return list.filter((member) =>
        representativeCategories.includes(member.dblb)
      ).length;
    },
    representPercentage(list) {
      if (!list.length) return 0;
      return ((this.representTotal(list) / list.length) * 100).toFixed(0); // Or toFixed(2) for decimals
    },
    handleDownload(row) {
      this.download(
        "/school/org/report/disclose/export",
        {
          id: row.id,
        },
        `选用组织名单_${row.title}_${new Date().getTime()}.xlsx`
      );
    },
    handleView(row) {
      getDisclose(row.id).then((response) => {
        this.viewData = response.data.sonDataList;
        this.viewDialogVisible = true;
      });
    },
    /** 查询选用组织名单报备与公开列表 */
    getList() {
      this.queryParams.teamMeetingId = this.formId;
      this.loading = true;
      // Adjust queryParams if needed for the API
      listDisclose(this.queryParams)
        .then((response) => {
          // Assuming response structure is { rows: [], total: N }
          // Modify if API returns data differently
          this.discloseList = response.rows;
          this.total = response.total;
          this.loading = false;
        })
        .catch(() => {
          this.loading = false; // Ensure loading is turned off on error
          this.$modal.msgError("加载列表失败");
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
        title: "",
        teamMeetingId: this.formId,
        file: "",
        sonDataList: [
          {
            zw: "2",
            dblb: "",
            xm: "",
            phone: "",
            njZy: "",
            bj: "",
          },
        ],
      };
      this.resetForm("form");
    },


    /** 添加成员行 */
    addMember() {
      this.form.sonDataList.push({
        zw: "", // Default to '成员' for new rows
        dblb: "",
        xm: "",
        phone: "",
        njZy: "",
        bj: "",
      });
    },
    /** 删除成员行 */
    removeMember(index, row) {
      if (this.form.sonDataList.length > 1) {
        // Prevent removing the last row
        if (row.id) {
          delSonData(row.id).then((response) => {
            this.$modal.msgSuccess("删除成功");
            this.form.sonDataList.splice(index, 1);
          });
        } else {
          this.form.sonDataList.splice(index, 1);
          this.$modal.msgSuccess("删除成功");
        }
      } else {
        this.$modal.msgWarning("至少需要保留一名成员");
      }
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "在线添加选用组织名单"; // Updated title
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;

      this.loading = true; // Show loading indicator
      getDisclose(id)
        .then((response) => {
          // IMPORTANT: Adapt this based on how the API returns the list and its sonDataList
          // Assuming response.data contains { id/tId, title, sonDataList: [...] }
          this.form = response.data;
          // Ensure sonDataList is an array, even if API returns null/undefined
          this.form.sonDataList = this.form.sonDataList || [
            {
              zw: "2",
              dblb: "",
              xm: "",
              phone: "",
              njZy: "",
              bj: "",
            },
          ];
          if (!this.form.sonDataList.length) {
            // Ensure at least one row if sonDataList array is empty
            this.addMember();
            this.form.sonDataList[0].zw = "2"; // Set first as leader if newly added
          }
          this.open = true;
          this.title = "修改选用组织名单"; // Updated title
          this.loading = false;
        })
        .catch(() => {
          this.loading = false;
          this.$modal.msgError("获取详情失败");
        });
    },
    /** 处理职务变更 */
    handlePositionChange(member, index) {
      // 如果当前选择的是组长，则检查其他成员是否已经是组长
      if (member.zw === "2") {
        // 查找除当前成员外是否有其他组长
        const otherLeaders = this.form.sonDataList.filter(
          (m, i) => i !== index && m.zw === "2"
        );

        if (otherLeaders.length > 0) {
          // 找到了其他组长，提示用户并恢复成员
          this.$message.warning("选用组织名单只能有一个职务是组长");

          // 将其他组长的职务修改为"成员"
          otherLeaders.forEach((leader) => {
            leader.zw = "成员";
          });
        }
      }
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          // Check if at least one member exists
          if (!this.form.sonDataList || this.form.sonDataList.length === 0) {
            this.$modal.msgError("请至少添加一名组织成员");
            return;
          }

          // Check if there's exactly one '组长' (Group Leader)
          const leaderCount = this.form.sonDataList.filter(
            (m) => m.zw == "2"
          ).length;
          if (leaderCount === 0) {
            this.$modal.msgError("必须有一名组长");
            return;
          } else if (leaderCount > 1) {
            this.$modal.msgError("选用组织名单只能有一个职务是组长");
            return;
          }

          // Check if representative percentage is at least 80%
          if (this.representativePercentage < 80) {
            this.$modal.msgError(
              `家长学生代表占比不能低于80%，当前占比为${this.representativePercentage}%`
            );
            return;
          }

          // Prepare payload for API
          // IMPORTANT: Adjust the payload structure based on what your API expects
          const payload = {
            ...this.form,
            // You might need to calculate 'total', 'jzxsDb', 'jzxsZb' here if the API requires them
            total: this.totalsonDataList,
            jzxsDb: this.representativesonDataList,
            jzxsZb: this.representativePercentage,
          };

          if (this.form.id != null) {
            // Using teamMeetingId instead of id
            updateDisclose(payload)
              .then((response) => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
                this.$emit("handleGetProcessMenu");
              })
              .catch(() => {
                // Optional: Add error handling for API call
              });
          } else {
            addDisclose(payload)
              .then((response) => {
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                this.getList();
                this.$emit("handleGetProcessMenu");
              })
              .catch(() => {
                // Optional: Add error handling for API call
              });
          }
        } else {
          this.$modal.msgError("请检查内容是否填写，或手机格式是否正确");
          return false;
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const tIds = row.id;
      this.$modal
        .confirm("是否确认删除选用的数据项？")
        .then(function () {
          return delDisclose(tIds);
        })
        .then(() => {
          this.getList();
          this.$emit("handleGetProcessMenu");
          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => {});
    },

    /** 下载模板操作 */
    importTemplate() {
      const link = document.createElement("a");
      link.href =
        process.env.VUE_APP_IMAGE_BASE_API +
        "/filestatics/download/校服选用组织名单（公开）.xlsx";
      link.download = "校服选用组织名单（公开）.xlsx";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
    /** 导入选用组织名单 */
    importList() {
      this.upload.open = true;
    },
    /** 获取字典标签 */
    getDictLabel(value) {
      const item = this.dict.type.sc_dble.find((dict) => dict.value === value);
      return item ? item.label : value;
    },
    // 提交上传文件
    submitFileForm() {
      this.$refs.upload.submit();
    },
    // 文件上传中处理
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true;
    },
    // 文件上传成功处理
    handleFileSuccess(response, file, fileList) {
      this.upload.open = false;
      this.upload.isUploading = false;


      this.form.sonDataList = response.data;
      this.$refs.upload.clearFiles();
      this.$alert(
        "<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" +
          response.msg +
          "</div>",
        "导入结果",
        { dangerouslyUseHTMLString: true }
      );
      this.getList();
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
.member-table {
  width: 100%;
  margin-bottom: 20px;
  background-color: #fff;
}

.no-margin {
  margin-bottom: 0;
}

.no-margin :deep(.el-form-item__content) {
  margin-left: 0 !important;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 5px;
}

/* Custom styling for the el-table */
.member-table :deep(.el-table) {
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.member-table :deep(.el-table__header-wrapper th) {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 500;
  padding: 12px 0;
  text-align: center;
}

.member-table :deep(.el-table td) {
  padding: 5px;
}

/* Input and select styles */
.member-table :deep(.el-input__inner),
.member-table :deep(.el-select .el-input__inner) {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  height: 36px;
  line-height: 36px;
  padding: 0 15px;
  background-color: transparent;
}

/* Make the select arrow more prominent */
.member-table :deep(.el-select .el-input .el-select__caret) {
  color: #909399;
}

.member-table :deep(.el-form-item) {
  margin-bottom: 0;
}

/* Fix for form validation */
.member-table :deep(.el-form-item.is-error .el-input__inner) {
  border-color: #f56c6c;
}

/* Style for the action buttons */
.member-table :deep(.el-button--mini) {
  padding: 5px;
  font-size: 12px;
}

/* Summary section */
.summary-section {
  margin-top: 20px;
  text-align: center;
  color: #606266;
  padding: 10px;
  background-color: #f8f8f8;
  border-radius: 4px;
}

.summary-section.warning {
  background-color: #fef0f0;
  border: 1px solid #fbc4c4;
  color: #f56c6c;
}

.warning-text {
  margin-top: 8px;
  font-size: 13px;
  color: #f56c6c;
  font-weight: 500;
}
</style>
