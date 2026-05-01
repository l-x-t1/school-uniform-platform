<template>
  <div class="app-container">
    <!-- 头部标签 -->
    <el-tabs v-model="activeTab" type="card">
      <el-tab-pane name="purchase" label="采购报备信息"></el-tab-pane>
      <el-tab-pane name="noPurchase" label="无采购说明信息"></el-tab-pane>
    </el-tabs>

    <!-- 采购报备信息 -->
    <div v-show="activeTab === 'purchase'">
      <div class="top-button">
        <el-button size="small" type="primary" icon="el-icon-plus" @click="openAddPurchaseDialog">添加报备</el-button>
      </div>
      <!-- 搜索表单 -->
      <div class="search-container">
        <el-form :inline="true" :model="purchaseQuery" ref="purchaseQueryForm" label-width="150px" class="search-form">
          <el-form-item label="采购报备名称：">
            <el-input v-model="purchaseQuery.name" placeholder="请输入" clearable></el-input>
          </el-form-item>
          <el-form-item label="事前报备申请时间：">
            <el-date-picker v-model="purchaseQuery.beforeApplyTime" type="date" placeholder="请选择日期"
              value-format="yyyy-MM-dd"></el-date-picker>
          </el-form-item>
          <el-form-item label="事前报备申请状态：">
            <el-select v-model="purchaseQuery.beforeStatus" placeholder="全部">
              <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="事后报备申请时间：">
            <el-date-picker v-model="purchaseQuery.afterApplyTime" type="date" placeholder="请选择日期"
              value-format="yyyy-MM-dd"></el-date-picker>
          </el-form-item>
          <el-form-item label="事后报备申请状态：">
            <el-select v-model="purchaseQuery.afterStatus" placeholder="全部">
              <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchPurchase">查询</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 表格 -->
      <el-table :data="purchaseTableData" border style="width: 100%" v-loading="purchaseLoading">
        <el-table-column prop="name" label="采购报备名称" min-width="180"></el-table-column>
        <el-table-column prop="beforeApplyTime" label="事前报备申请时间" min-width="150"></el-table-column>
        <el-table-column prop="beforeStatus" label="事前报备申请状态" min-width="150">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.beforeStatus)">{{ scope.row.beforeStatus }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="afterApplyTime" label="事后报备申请时间" min-width="150"></el-table-column>
        <el-table-column prop="afterStatus" label="事后报备申请状态" min-width="150">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.afterStatus)">{{ scope.row.afterStatus }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="auditDept" label="审核单位" min-width="120"></el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="handleView(scope.row)">查看</el-button>
            <el-button size="mini" type="text" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="text" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination v-show="purchaseTotal > 0" :total="purchaseTotal" :page.sync="purchaseQuery.pageNum"
        :limit.sync="purchaseQuery.pageSize" @pagination="getPurchaseList" />

      <!-- 空数据提示 -->
      <div v-if="purchaseTableData.length === 0 && !purchaseLoading" class="empty-data">
        无数据记录
      </div>
    </div>

    <!-- 无采购说明信息 -->
    <div v-show="activeTab === 'noPurchase'">
      <div class="top-button">
        <el-button size="small" type="primary" icon="el-icon-plus" @click="openAddNoPurchaseDialog">发布无采购说明</el-button>
      </div>
      <!-- 搜索表单 -->
      <div class="search-container">
        <el-form :inline="true" :model="noPurchaseQuery" ref="noPurchaseQueryForm" class="search-form">
          <el-form-item label="无校服采购说明名称：">
            <el-input v-model="noPurchaseQuery.name" placeholder="请输入" clearable></el-input>
          </el-form-item>
          <el-form-item label="提交时间：">
            <el-date-picker v-model="noPurchaseQuery.timeRange" type="daterange" range-separator="至"
              start-placeholder="开始时间" end-placeholder="结束时间" value-format="yyyy-MM-dd"></el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchNoPurchase">查询</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 表格 -->
      <el-table :data="noPurchaseTableData" border style="width: 100%" v-loading="noPurchaseLoading">
        <el-table-column prop="name" label="无校服采购说明信息" min-width="180"></el-table-column>
        <el-table-column prop="planTime" label="无采购计划时间" min-width="150"></el-table-column>
        <el-table-column prop="submitTime" label="提交时间" min-width="150"></el-table-column>
        <el-table-column prop="status" label="状态" min-width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="handleViewNoPurchase(scope.row)">查看</el-button>
            <el-button size="mini" type="text" @click="handleEditNoPurchase(scope.row)">编辑</el-button>
            <el-button size="mini" type="text" @click="handleDeleteNoPurchase(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination v-show="noPurchaseTotal > 0" :total="noPurchaseTotal" :page.sync="noPurchaseQuery.pageNum"
        :limit.sync="noPurchaseQuery.pageSize" @pagination="getNoPurchaseList" />

      <!-- 空数据提示 -->
      <div v-if="noPurchaseTableData.length === 0 && !noPurchaseLoading" class="empty-data">
        无数据记录
      </div>
    </div>

    <!-- 添加报备弹窗 -->
    <el-dialog title="添加报备" :visible.sync="addPurchaseDialogVisible" width="800px" append-to-body>
      <div class="dialog-content">
        <el-form :model="purchaseForm" :rules="purchaseRules" ref="purchaseForm" label-width="140px">
          <el-form-item label="采购报备名称：" prop="name">
            <el-input v-model="purchaseForm.name" placeholder="请输入采购报备名称"></el-input>
          </el-form-item>

          <div class="section-title">采购报备文件</div>

          <!-- 重复文件模块区域 -->
          <div v-for="(fileModule, index) in purchaseForm.fileModules" :key="index" class="file-module">
            <div class="file-module-header">
              <span>文件模块 {{ index + 1 }}</span>
              <i v-if="purchaseForm.fileModules.length > 1" class="el-icon-close" @click="removeFileModule(index)"></i>
            </div>

            <el-form-item label="标题：">
              <el-input v-model="fileModule.title" placeholder="请输入标题"></el-input>
            </el-form-item>

            <el-form-item label="上传附件：">
              支持扩展名：.png、.jpg、.ipeg
              <el-upload :action="uploadUrl" list-type="picture-card"
                :on-preview="handlePictureCardPreview" :on-remove="handleRemove">
                <i class="el-icon-plus"></i>
              </el-upload>
              <el-dialog :visible.sync="dialogVisibleImage" class="image-dialog">
                <img width="100%" :src="dialogImageUrl" alt="" />
              </el-dialog>
              <el-upload style="margin-top: 10px" class="upload-demo" drag
                :action="uploadUrl" multiple>
                <div style="margin-top: 8px" class="el-upload__text">
                  将文件拖到此处，或<em>点击上传</em>
                </div>
                <div class="el-upload__tip" slot="tip">
                  支持扩展名：.doc .docx .pdf .xlsx .xls .zip .rar
                </div>
              </el-upload>
            </el-form-item>
          </div>

          <div class="separator"></div>
          <div class="add-new-section">
            <el-button type="text" icon="el-icon-plus" @click="addNewFileModule">新增</el-button>
          </div>

          <div class="dialog-footer">
            <el-button @click="saveAsDraft">暂存</el-button>
            <el-button type="primary" @click="submitPurchase">提交审核</el-button>
            <el-button @click="cancelAddPurchase">取消</el-button>
          </div>
        </el-form>
      </div>
    </el-dialog>

    <!-- 发布无采购说明弹窗 -->
    <el-dialog title="发布无采购说明" :visible.sync="addNoPurchaseDialogVisible" width="800px" append-to-body>
      <div class="dialog-content">
        <el-form :model="noPurchaseForm" :rules="noPurchaseRules" ref="noPurchaseForm" label-width="160px">
          <el-form-item label="无采购说明标题：" prop="title">
            <el-input v-model="noPurchaseForm.title" placeholder="河北兰集中学无采购计划说明"></el-input>
          </el-form-item>

          <el-form-item label="无采购计划时间：" prop="planTime" required>
            <div class="time-range">
              <span>2024-08-01 ~ 2025-07-31</span>
              <div class="time-buttons">
                <el-button size="mini" type="primary" plain>本学年</el-button>
                <el-button size="mini" plain>上学年</el-button>
                <el-button size="mini" plain>下学年</el-button>
              </div>
            </div>
          </el-form-item>

          <el-form-item label="备注说明：">
            <el-input type="textarea" v-model="noPurchaseForm.remark" placeholder="请输入备注说明" :rows="6"></el-input>
          </el-form-item>

          <el-form-item label="上传附件：">
            支持扩展名：.png、.jpg、.ipeg
            <el-upload :action="uploadUrl" list-type="picture-card"
              :on-preview="handlePictureCardPreview" :on-remove="handleRemove">
              <i class="el-icon-plus"></i>
            </el-upload>
            <el-dialog :visible.sync="dialogVisibleImage" class="image-dialog">
              <img width="100%" :src="dialogImageUrl" alt="" />
            </el-dialog>
            <el-upload style="margin-top: 10px" class="upload-demo" drag
              :action="uploadUrl" multiple>
              <div style="margin-top: 8px" class="el-upload__text">
                将文件拖到此处，或<em>点击上传</em>
              </div>
              <div class="el-upload__tip" slot="tip">
                支持扩展名：.doc .docx .pdf .xlsx .xls .zip .rar
              </div>
            </el-upload>
          </el-form-item>
          <div class="dialog-footer">
            <el-button type="primary" @click="submitNoPurchase">发布</el-button>
            <el-button @click="cancelAddNoPurchase">返回</el-button>
          </div>
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'CgbcIndex',
  data() {
    return {
      uploadUrl: process.env.VUE_APP_BASE_API + '/file/upload',
      // 当前激活的tab
      activeTab: 'purchase',

      // ===== 采购报备信息 =====
      // 采购报备搜索参数
      purchaseQuery: {
        name: '',
        beforeApplyTime: '',
        beforeStatus: '',
        afterApplyTime: '',
        afterStatus: '',
        pageNum: 1,
        pageSize: 10
      },
      // 采购报备表格数据
      purchaseTableData: [],
      // 采购报备加载状态
      purchaseLoading: false,
      // 采购报备总条数
      purchaseTotal: 0,
      // 添加报备弹窗可见性
      addPurchaseDialogVisible: false,
      // 添加报备表单
      purchaseForm: {
        name: '',
        fileModules: [
          {
            title: '',
            images: [],
            files: []
          }
        ]
      },
      // 添加报备表单校验规则
      purchaseRules: {
        name: [
          { required: true, message: '请输入采购报备名称', trigger: 'blur' }
        ]
      },
      dialogVisibleImage: false,
      dialogImageUrl: '',

      // ===== 无采购说明信息 =====
      // 无采购说明搜索参数
      noPurchaseQuery: {
        name: '',
        timeRange: [],
        pageNum: 1,
        pageSize: 10
      },
      // 无采购说明表格数据
      noPurchaseTableData: [],
      // 无采购说明加载状态
      noPurchaseLoading: false,
      // 无采购说明总条数
      noPurchaseTotal: 0,
      // 添加无采购说明弹窗可见性
      addNoPurchaseDialogVisible: false,
      // 添加无采购说明表单
      noPurchaseForm: {
        title: '河北兰集中学无采购计划说明',
        planTime: '2024-08-01 ~ 2025-07-31',
        remark: '',
        images: [],
        files: []
      },
      // 添加无采购说明表单校验规则
      noPurchaseRules: {
        title: [
          { required: true, message: '请输入无采购说明标题', trigger: 'blur' }
        ]
      },

      // 通用状态选项
      statusOptions: [
        { value: '', label: '全部' },
        { value: '待审核', label: '待审核' },
        { value: '已通过', label: '已通过' },
        { value: '已驳回', label: '已驳回' }
      ]
    };
  },
  created() {
    this.getPurchaseList();
  },
  methods: {
    // 根据状态获取标签类型
    getStatusType(status) {
      if (status === '已通过') return 'success';
      if (status === '已驳回') return 'danger';
      if (status === '待审核') return 'warning';
      return 'info';
    },

    // ===== 采购报备信息 =====
    // 获取采购报备列表
    getPurchaseList() {
      this.purchaseLoading = true;
      // 模拟接口请求
      setTimeout(() => {
        this.purchaseTableData = [];
        this.purchaseTotal = 0;
        this.purchaseLoading = false;
      }, 500);
    },
    // 搜索采购报备
    searchPurchase() {
      this.purchaseQuery.pageNum = 1;
      this.getPurchaseList();
    },
    // 打开添加报备弹窗
    openAddPurchaseDialog() {
      this.addPurchaseDialogVisible = true;
      this.resetPurchaseForm();
    },
    // 重置采购报备表单
    resetPurchaseForm() {
      this.purchaseForm = {
        name: '',
        fileModules: [
          {
            title: '',
            images: [],
            files: []
          }
        ]
      };
      if (this.$refs.purchaseForm) {
        this.$refs.purchaseForm.resetFields();
      }
    },
    // 添加新的文件模块
    addNewFileModule() {
      this.purchaseForm.fileModules.push({
        title: '',
        images: [],
        files: []
      });
    },

    // 删除文件模块
    removeFileModule(index) {
      this.purchaseForm.fileModules.splice(index, 1);
    },

    // 处理图片预览
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url;
      this.dialogVisibleImage = true;
    },

    // 处理文件移除
    handleRemove(file, fileList) {
      console.log(file, fileList);
    },
    // 暂存报备
    saveAsDraft() {
      this.$message.success('暂存成功');
      this.addPurchaseDialogVisible = false;
    },
    // 提交报备审核
    submitPurchase() {
      this.$refs.purchaseForm.validate(valid => {
        if (valid) {
          this.$message.success('提交审核成功');
          this.addPurchaseDialogVisible = false;
          this.getPurchaseList();
        }
      });
    },
    // 取消添加报备
    cancelAddPurchase() {
      this.addPurchaseDialogVisible = false;
    },
    // 查看采购报备
    handleView(row) {
      this.$message.info('查看采购报备：' + row.name);
    },
    // 编辑采购报备
    handleEdit(row) {
      this.$message.info('编辑采购报备：' + row.name);
    },
    // 删除采购报备
    handleDelete(row) {
      this.$confirm('确认删除该采购报备？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message.success('删除成功');
        this.getPurchaseList();
      }).catch(() => { });
    },

    // ===== 无采购说明信息 =====
    // 获取无采购说明列表
    getNoPurchaseList() {
      this.noPurchaseLoading = true;
      // 模拟接口请求
      setTimeout(() => {
        this.noPurchaseTableData = [];
        this.noPurchaseTotal = 0;
        this.noPurchaseLoading = false;
      }, 500);
    },
    // 搜索无采购说明
    searchNoPurchase() {
      this.noPurchaseQuery.pageNum = 1;
      this.getNoPurchaseList();
    },
    // 打开添加无采购说明弹窗
    openAddNoPurchaseDialog() {
      this.addNoPurchaseDialogVisible = true;
      this.resetNoPurchaseForm();
    },
    // 重置无采购说明表单
    resetNoPurchaseForm() {
      this.noPurchaseForm = {
        title: '河北兰集中学无采购计划说明',
        planTime: '2024-08-01 ~ 2025-07-31',
        remark: '',
        images: [],
        files: []
      };
      if (this.$refs.noPurchaseForm) {
        this.$refs.noPurchaseForm.resetFields();
      }
    },
    // 上传无采购说明图片
    handleUploadNoPurchaseImage() {
      this.$message.success('触发上传图片操作');
    },
    // 上传无采购说明文件
    handleUploadNoPurchaseFile() {
      this.$message.success('触发上传文件操作');
    },
    // 提交无采购说明
    submitNoPurchase() {
      this.$refs.noPurchaseForm.validate(valid => {
        if (valid) {
          this.$message.success('发布成功');
          this.addNoPurchaseDialogVisible = false;
          this.getNoPurchaseList();
        }
      });
    },
    // 取消添加无采购说明
    cancelAddNoPurchase() {
      this.addNoPurchaseDialogVisible = false;
    },
    // 查看无采购说明
    handleViewNoPurchase(row) {
      this.$message.info('查看无采购说明：' + row.name);
    },
    // 编辑无采购说明
    handleEditNoPurchase(row) {
      this.$message.info('编辑无采购说明：' + row.name);
    },
    // 删除无采购说明
    handleDeleteNoPurchase(row) {
      this.$confirm('确认删除该无采购说明？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message.success('删除成功');
        this.getNoPurchaseList();
      }).catch(() => { });
    }
  }
};
</script>

<style scoped>
.app-container {
  padding: 20px;
}

/* Tab标签样式 */
.el-tabs {
  margin-bottom: 15px;
}

/* 顶部按钮 */
.top-button {
  margin-bottom: 15px;
}

/* 搜索表单容器 */
.search-container {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 20px;
}

/* 表单项样式 */
.search-form .el-form-item {
  margin-bottom: 10px;
  margin-right: 10px;
}

/* 表格底部的分页 */
.pagination-container {
  margin-top: 15px;
  text-align: right;
}

/* 空数据提示 */
.empty-data {
  text-align: center;
  color: #909399;
  font-size: 14px;
  padding: 50px 0;
  background-color: #fff;
}

/* 弹窗内容区 */
.dialog-content {
  padding: 0 20px;
}

/* 分隔标题 */
.section-title {
  font-size: 14px;
  font-weight: bold;
  margin: 20px 0 15px;
  position: relative;
  padding-left: 10px;
  border-left: 3px solid #409EFF;
}

/* 上传文件区域 */
.upload-list {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 15px;
}

.upload-item {
  margin-right: 20px;
  margin-bottom: 10px;
}

.upload-image,
.upload-file {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 180px;
  height: 120px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
}

.upload-image:hover,
.upload-file:hover {
  border-color: #409EFF;
}

::v-deep .el-upload-dragger {
  background-color: #fff;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  width: 500px;
  height: 50px;
  text-align: center;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.upload-image i,
.upload-file i {
  font-size: 28px;
  color: #8c939d;
  margin-bottom: 5px;
}

.upload-text {
  font-size: 14px;
  color: #606266;
  margin-bottom: 5px;
}

.upload-tips {
  font-size: 12px;
  color: #909399;
}

/* 分隔线 */
.separator {
  height: 1px;
  background-color: #EBEEF5;
  margin: 20px 0 10px;
}

/* 新增区域按钮 */
.add-new-section {
  text-align: center;
  margin-bottom: 20px;
}

/* 弹窗底部按钮区域 */
.dialog-footer {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #EBEEF5;
}

/* 时间范围的样式 */
.time-range {
  display: flex;
  align-items: center;
}

.time-buttons {
  margin-left: 15px;
}

.time-buttons .el-button {
  padding: 5px 10px;
}

/* 文件模块样式 */
.file-module {
  position: relative;
  padding: 20px;
  border: 1px solid #EBEEF5;
  border-radius: 4px;
  margin-bottom: 20px;
  background-color: #fafafa;
}

.file-module-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  font-weight: bold;
  font-size: 14px;
  color: #606266;
}

.file-module-header .el-icon-close {
  color: #909399;
  cursor: pointer;
  padding: 5px;
  transition: all 0.3s;
}

.file-module-header .el-icon-close:hover {
  color: #F56C6C;
  transform: scale(1.2);
}
</style>
