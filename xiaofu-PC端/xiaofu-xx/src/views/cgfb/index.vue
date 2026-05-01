<template>
  <div class="app-container">

    <!-- 头部按钮 -->

    <!-- 搜索表单 -->
    <div>
      <el-form :inline="true" :model="queryParams" class="search-form" label-width="140px" ref="queryForm">
        <el-form-item label="采购公告信息名称：">
          <el-input v-model="queryParams.name" placeholder="请输入采购公告信息名称" clearable style="width: 250px"></el-input>
        </el-form-item>
        <el-form-item label="发布时间：">
          <el-date-picker v-model="queryParams.publishTime" type="date" placeholder="请选择日期"
            value-format="yyyy-MM-dd"></el-date-picker>
        </el-form-item>
        <el-form-item label="选购模式：">
          <el-select v-model="queryParams.mode" placeholder="全部" clearable>
            <el-option v-for="item in modeOptions" :key="item.value" :label="item.label"
              :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="发布状态：">
          <el-select v-model="queryParams.status" placeholder="全部" clearable>
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label"
              :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="来源类型：">
          <el-select v-model="queryParams.sourceType" placeholder="全部" clearable>
            <el-option v-for="item in sourceTypeOptions" :key="item.value" :label="item.label"
              :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button size="small" type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
        </el-form-item>
      </el-form>
    </div>
    <el-button size="small" type="primary" icon="el-icon-plus" @click="openAddDialog">发布采购公告信息</el-button>

    <!-- 表格数据 -->
    <el-table v-loading="loading" :data="tableData" border style="width: 100%;margin-top: 10px;">
      <el-table-column prop="name" label="采购公告信息名称" min-width="220"></el-table-column>
      <el-table-column prop="mode" label="选购模式" min-width="150"></el-table-column>
      <el-table-column prop="publishTime" label="发布时间" min-width="180"></el-table-column>
      <el-table-column prop="status" label="发布状态" min-width="120">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '已发布' ? 'success' : 'info'" size="small">
            <i class="el-icon-circle-check" v-if="scope.row.status === '已发布'"></i>
            {{ scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="sourceType" label="来源类型" min-width="180">
        <template slot-scope="scope">
          {{ scope.row.sourceType }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" align="center">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleView(scope.row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 采购公告信息发布弹窗 -->
    <el-dialog title="发布采购公告信息" :visible.sync="dialogVisible" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="150px">
        <el-form-item label="采购公告名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入采购公告名称"></el-input>
        </el-form-item>
        <el-form-item label="选购模式" prop="mode">
          <el-select v-model="form.mode" placeholder="请选择选购模式">
            <el-option v-for="item in modeOptions" :key="item.value" :label="item.label"
              :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="公告正文" prop="content">
          <el-input type="textarea" v-model="form.content" :rows="5" placeholder="请输入公告正文"></el-input>
        </el-form-item>
        <el-form-item label="附件上传">
          <el-upload class="upload-demo" :action="uploadUrl" multiple :limit="5">
            <el-button size="small" type="primary">点击上传</el-button>
            <div slot="tip" class="el-upload__tip">支持上传doc, pdf, jpg等多种格式文件，且不超过10MB</div>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm">确定</el-button>
          <el-button @click="dialogVisible = false">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'CgfbIndex',
  data() {
    return {
      uploadUrl: process.env.VUE_APP_BASE_API + '/file/upload',
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: '',
        publishTime: '',
        mode: '',
        status: '',
        sourceType: ''
      },
      // 选购模式选项
      modeOptions: [
        { value: '', label: '全部' },
        { value: '委托招标机构确定企业', label: '委托招标机构确定企业' },
        { value: '招标', label: '招标' },
        { value: '询价', label: '询价' },
        { value: '直接选定', label: '直接选定' }
      ],
      // 发布状态选项
      statusOptions: [
        { value: '', label: '全部' },
        { value: '已发布', label: '已发布' },
        { value: '未发布', label: '未发布' }
      ],
      // 来源类型选项
      sourceTypeOptions: [
        { value: '', label: '全部' },
        { value: '校服选用采购规范流程支撑', label: '校服选用采购规范流程支撑' },
        { value: '校服零星采购', label: '校服零星采购' }
      ],
      // 加载状态
      loading: false,
      // 总条数
      total: 0,
      // 表格数据
      tableData: [],
      // 弹窗可见性
      dialogVisible: false,
      // 表单对象
      form: {
        name: '',
        mode: '',
        content: '',
        files: []
      },
      // 表单校验规则
      rules: {
        name: [
          { required: true, message: '请输入采购公告名称', trigger: 'blur' }
        ],
        mode: [
          { required: true, message: '请选择选购模式', trigger: 'change' }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    // 获取列表数据
    getList() {
      this.loading = true;
      // 模拟异步请求
      setTimeout(() => {
        this.tableData = [
          {
            name: '河北芊集中学2024-2025招标公告',
            mode: '委托招标机构确定企业',
            publishTime: '2024-10-23 20:38:20',
            status: '已发布',
            sourceType: '校服选用采购规范流程支撑'
          }
        ];
        this.total = this.tableData.length;
        this.loading = false;
      }, 300);
    },
    // 查询按钮点击事件
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    // 打开添加对话框
    openAddDialog() {
      this.form = {
        name: '',
        mode: '',
        content: '',
        files: []
      };
      this.$message.warning('暂无此功能权限，如有意向，可联系平台客服400-689-6600进行咨询');
      // this.dialogVisible = true;
    },
    // 提交表单
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.$message.success('发布成功');
          this.dialogVisible = false;
          this.getList();
        }
      });
    },
    // 查看详情
    handleView(row) {
      this.$message.info('查看：' + row.name);
    }
  }
};
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.page-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #303133;
}

.top-button {
  margin-bottom: 20px;
  text-align: right;
}

.search-container {
  background-color: #f5f7fa;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.search-form .el-form-item {
  margin-bottom: 10px;
  margin-right: 10px;
}

.pagination-container {
  text-align: right;
  margin-top: 15px;
}
</style>
