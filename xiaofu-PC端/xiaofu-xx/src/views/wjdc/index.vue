<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="问卷名称" prop="title">
        <el-input v-model="queryParams.title" placeholder="请输入问卷名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="问卷类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择问卷类型" clearable>
          <el-option v-for="item in dict.type.lclx" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['school:survey:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single || !canManageSelected" @click="handleUpdate"
          v-hasPermi="['school:survey:edit']">修改</el-button>
      </el-col> 
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>
    <el-table v-loading="loading" :data="surveyList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="50" align="center">
        <template slot-scope="scope">
          <span>{{
            (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1
          }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建学校" align="center" prop="createBy">
      </el-table-column>
      <el-table-column label="问卷名称" align="center" prop="title">
        <template slot-scope="scope">
          <div style="white-space: pre-line; max-height: 200px; overflow-y: auto">
            {{ scope.row.title }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="问卷类型" align="center" prop="surveyType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.lclx" :value="scope.row.type" />
        </template>
      </el-table-column> 

      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-if="canManage(scope.row)" size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button v-if="scope.row.fbStatus == 1 && scope.row.category != 1" size="mini" type="text"
            icon="el-icon-edit" @click="handleQrCode(scope.row)">查看问卷二维码</el-button>
          <el-button v-if="canManage(scope.row)" size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
          <el-button v-if="scope.row.category != 1" size="mini" type="text" icon="el-icon-view"
            @click="handleStatistics(scope.row)">统计</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
    <!-- 添加或修改问卷调查库对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1200px" append-to-body>
      <el-form ref="formAddRef" :model="formAdd" :rules="formAddRules" label-width="150px">
        <el-form-item label="问卷名称：" prop="title">
          <el-input style="width: 400px" v-model="formAdd.title" placeholder="请输入问卷名称"></el-input>
        </el-form-item> 
        <el-form-item label="问卷类型：" prop="type">
          <el-radio-group v-model="formAdd.type">
            <el-radio v-for="item in dict.type.lclx" :key="item.value" :label="item.value">{{ item.label
              }}</el-radio>
          </el-radio-group>
        </el-form-item> 
        <el-form-item label="问卷内容：" prop="sonList">
          <div class="table-operations">
            <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAddRow" v-if="formAdd.sonList.length == 0">添加问卷内容</el-button>
            <el-button type="warning" icon="el-icon-edit" size="mini" @click="handleAddRow" v-else>编辑问卷内容</el-button>
          </div> 
        </el-form-item>
        <el-form-item 
              label="投票起止时间"
              prop="tpqzsj"
            >
              <el-date-picker
                v-model="formAdd.tpqzsj"
                type="datetimerange"
                value-format="yyyy-MM-dd HH:mm:ss"
                format="yyyy-MM-dd HH:mm:ss"
                range-separator="至"
                start-placeholder="开始日期时间"
                end-placeholder="结束日期时间"
                :picker-options="{
                  format: 'yyyy-MM-dd HH:mm:ss',
                }"
                style="width: 100%"
              >
              </el-date-picker>
            </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>
    <el-dialog :visible.sync="wjbj" fullscreen append-to-body @close="wjClose">
      <wjBianji :visible.sync="wjbj" ref="wjBianjiRef" :sonList="formAdd.sonList" @save="wjSave" />
    </el-dialog>
    <!-- 问卷统计弹窗 -->
    <survey-statistics :visible.sync="statisticsVisible" :selectedSurveyId="selectedSurveyId" />
  </div>
</template>

<script>
import {
  listSurvey,
  getSurvey,
  delSurvey,
  addSurvey,
  updateSurvey,
  getSurveyStatistics,
} from "@/api/school/survey";
import { getDept } from "@/api/system/dept";
import SurveyStatistics from "./tongji";
import QRCode from "qrcode";
import wjBianji from "./wjBianji";
export default {
  dicts: ["zdfs", "sc_wjzt", "sc_type",'lclx'],
  name: "Survey",
  components: {
    SurveyStatistics,
    wjBianji,
  },
  data() {
    return {
      formAdd: {
        title: "",
        sonList: [],
      },
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      selectedRows: [],
      // 非单个禁用
      single: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 问卷调查库表格数据
      surveyList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      wjbj: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        type: null,
      },
      // 问卷表单校验规则
      formAddRules: {
        title: [
          { required: true, message: "问卷名称不能为空", trigger: "blur" },
        ],
        surveyTime: [
          {
            required: true,
            message: "请选择问卷调查起止时间",
            trigger: "blur",
          },
        ],
        type: [
          { required: true, message: "请选择问卷类型", trigger: "blur" },
        ],
        sonList: [
          {
            type: "array",
            required: true,
            message: "至少添加一个问卷问题",
            trigger: "change",
          },
        ],
        tpqzsj: [
          { required: true, message: "投票起止时间不能为空", trigger: "blur" },
        ],
      },
      // 统计相关数据
      statisticsVisible: false,
      selectedSurveyId: null,
      currentDeptInfo: null,
    };
  },
  created() {
    this.loadCurrentDeptInfo();
    this.getList();
  },
  computed: {
    canManageSelected() {
      return this.selectedRows.length === 1 && this.canManage(this.selectedRows[0]);
    },
  },
  methods: {
    //生成二维码
    handleQrCode(row) {
      if (!row || !row.id) {
        this.$message.error("问卷标识缺失，无法生成二维码");
        return;
      }
      // 显示加载中，防止用户重复点击
      const loading = this.$loading({
        lock: true,
        text: "正在生成二维码...",
        spinner: "el-icon-loading",
        background: "rgba(0, 0, 0, 0.7)",
      });

      // 构建问卷URL
      const url = process.env.VUE_APP_QR_CODE_URL + `?id=${encodeURIComponent(row.id)}`;

      // 使用QRCode生成二维码图片URL
      QRCode.toDataURL(url, {
        width: 240,
        margin: 2,
        color: {
          dark: "#000000",
          light: "#ffffff",
        },
        errorCorrectionLevel: "H",
      })
        .then((dataUrl) => {
          // 关闭加载提示
          loading.close();

          // 构建HTML内容
          const content = `
          <div style="text-align: center; padding: 15px 0;">
            <img src="${dataUrl}" style="width: 240px; height: 240px;">
            <p style="font-size: 14px; color: #666; margin-top: 10px;">扫描上方二维码访问问卷</p>
            <button id="saveQrCodeBtn" style="
              margin-top: 15px;
              background-color: #409EFF;
              color: white;
              border: none;
              padding: 8px 15px;
              border-radius: 4px;
              cursor: pointer;
              font-size: 14px;">
              保存二维码
            </button>
          </div>
        `;
          const handleSaveClick = () => {
            this.downloadImage(dataUrl, `问卷二维码_${row.id}.png`);
          };
          // 显示二维码对话框
          this.$alert(content, `${row.title} - 问卷二维码`, {
            dangerouslyUseHTMLString: true,
            center: true,
            showConfirmButton: false,
            showClose: true,
            closeOnClickModal: true,
            callback: () => {
              // 清理事件监听
              const saveBtn = document.getElementById("saveQrCodeBtn");
              if (saveBtn) {
                saveBtn.removeEventListener("click", handleSaveClick);
              }
            },
          });

          // 添加点击事件处理函数
          setTimeout(() => {
            const saveBtn = document.getElementById("saveQrCodeBtn");
            if (saveBtn) {
              saveBtn.addEventListener("click", handleSaveClick);
            }
          }, 100);
        })
        .catch((err) => {
          loading.close();
          this.$message.error("二维码生成失败");
          console.error(err);
        });
    },

    // 下载图片
    downloadImage(dataUrl, fileName) {
      const link = document.createElement("a");
      link.download = fileName;
      link.href = dataUrl;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },

    handleStatistics(row) {
      getSurveyStatistics({ id: row.id }).then((res) => {
        this.statisticsVisible = true;
        this.selectedSurveyId = row.id;
      });
    },
    loadCurrentDeptInfo() {
      const userInfo = this.$store.state.user.userInfo || {};
      if (userInfo.dept) {
        this.currentDeptInfo = userInfo.dept;
        return;
      }
      const deptId = this.$store.state.user.deptId;
      if (!deptId) {
        return;
      }
      getDept(deptId).then((response) => {
        this.currentDeptInfo = response.data;
      }).catch(() => {});
    },
    canManage(row) {
      if (!row) {
        return false;
      }
      const currentDeptId = this.$store.state.user.deptId;
      if (String(row.deptId) === String(currentDeptId)) {
        return true;
      }
      const currentDept = this.currentDeptInfo || (this.$store.state.user.userInfo || {}).dept;
      if (!currentDept || String(currentDept.isSchool) === "1") {
        return false;
      }
      return String(row.ancestors || "")
        .split(",")
        .includes(String(currentDeptId));
    },
    // 检查是否为文本类型
    isTextType(type) {
      // 根据字典值判断是否为文本输入类型
      return (
        type === "文本" ||
        type === "输入框" ||
        type === "2" ||
        type === 2 ||
        type === "3" ||
        type === 3
      );
    },
    /** 查询问卷调查库列表 */
    getList() {
      this.loading = true;
      listSurvey({
        ...this.queryParams,
      }).then((response) => {
        this.surveyList = response.rows;
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
      let startDate = new Date();
      let endDate = new Date();
      endDate.setDate(endDate.getDate() + 7); // 一周后 
      this.formAdd = {
        id: null,
        type: null,
        tpqzsj: [
          startDate.toISOString().slice(0, 19).replace("T", " "),
          endDate.toISOString().slice(0, 19).replace("T", " "),
        ],
        title: "",
        sonList: [],
      };
      this.resetForm("formAddRef");
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
      this.selectedRows = selection;
      this.ids = selection.map((item) => item.id);
      this.single = selection.length !== 1;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      // 重置formAdd 
      this.open = true;
      this.title = "添加问卷调查";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const selectedRow = row && row.id ? row : this.selectedRows[0];
      if (selectedRow && !this.canManage(selectedRow)) {
        this.$message.warning("无权限修改该问卷");
        return;
      }
      const id = selectedRow ? selectedRow.id : (this.ids.length === 1 ? this.ids[0] : null);
      if (!id) {
        this.$message.warning("请选择要修改的问卷");
        return;
      }
      getSurvey(id).then((response) => { 
        if (!this.canManage(response.data)) {
          this.$message.warning("无权限修改该问卷");
          return;
        }
        this.formAdd = response.data; 
        this.formAdd.tpqzsj = [
          response.data.beginTime,
          response.data.endTime,
        ];
        this.open = true;
        this.title = "修改问卷调查";
      });
    },
    /** 添加问卷行 */
    handleAddRow() { 
      this.wjbj = true;
      this.$nextTick(() => {
        this.$refs.wjBianjiRef.showQuestionnaire();
      });
    },
    //保存问卷
    wjSave(data) {
      console.log(data);
      this.formAdd.sonList = data.map(item => {
        return {
          surveyContent: item.config.surveyContent,
          answeringMethod: item.config.answeringMethod,
          isRequired: item.config.isRequired,
          sm: item.config.sm,
          surveyResult: item.config.answeringMethod == 0 || item.config.answeringMethod == 1 ? item.config.options.join('、') : '',
          id:item.config.id
        }
      });
      console.log(this.formAdd.sonList);
      this.wjbj = false;
    },
    wjClose(){
      this.$refs.wjBianjiRef.clearQuestionnaire();
    },

    /** 提交按钮 */
    submitForm() {
      this.$refs["formAddRef"].validate((valid) => {
        if (valid) {
          if(this.formAdd.sonList.length == 0){
            this.$message.error("请完善问卷内容");
            return;
          }

          // 创建提交的数据，直接使用整个formAdd对象
          const submitData = {
            id: this.formAdd.id,
            ...this.formAdd,
            beginTime: this.formAdd.tpqzsj[0],
            endTime: this.formAdd.tpqzsj[1],
          };

          if (this.formAdd.id != null) {
            updateSurvey(submitData).then((response) => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            submitData.fbStatus = 1;
            addSurvey({
              ...submitData,
              deptId: this.$store.state.user.deptId,
            }).then((response) => {
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
      if (row && !this.canManage(row)) {
        this.$message.warning("无权限删除该问卷");
        return;
      }
      if (!row && this.selectedRows.some(item => !this.canManage(item))) {
        this.$message.warning("所选问卷中包含无权限删除的数据");
        return;
      }
      const ids = row ? row.id : (this.ids.length > 0 ? this.ids : null);
      if (!ids) {
        this.$message.warning("请选择要删除的问卷");
        return;
      }
      this.$modal
        .confirm("是否确认删除该问卷？")
        .then(function () {
          return delSurvey(ids);
        })
        .then(() => {
          this.$modal.msgSuccess("删除成功");
          this.getList();
        })
        .catch(() => { });
    },
  },
};
</script>

<style scoped>
.el-form-item__tip {
  color: #909399;
  font-size: 12px;
  line-height: 1.4;
  margin-top: 4px;
}

.table-operations {
  margin-bottom: 10px;
}

.no-options-text {
  color: #909399;
  font-size: 14px;
}
</style>
