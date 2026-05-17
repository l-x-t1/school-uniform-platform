<template>
  <div class="app-container">
    <!-- 查询表单 -->
    <el-form :inline="true" :model="queryParams">
      <el-form-item label="公告创建时间：">
        <el-input v-model="queryParams.dateRange" placeholder="请选择日期范围"></el-input>
      </el-form-item>

      <el-form-item>
        <el-button-group class="semester-buttons">
          <el-button :class="{ 'is-active': queryParams.semester === 'upper' }"
            @click="selectSemester('upper')">上学期</el-button>
          <el-button :class="{ 'is-active': queryParams.semester === 'lower' }"
            @click="selectSemester('lower')">下学期</el-button>
          <el-button :class="{ 'is-active': queryParams.semester === 'current' }"
            @click="selectSemester('current')">本学年</el-button>
          <el-button :class="{ 'is-active': queryParams.semester === 'last' }"
            @click="selectSemester('last')">上学年</el-button>
        </el-button-group>
      </el-form-item>
      <br>

      <el-form-item label="公告名称：">
        <el-input v-model="queryParams.noticeName" placeholder="请输入公告名称" style="width: 250px;"></el-input>
      </el-form-item>

      <el-form-item label="厂商名称：">
        <el-input v-model="queryParams.manufacturerName" placeholder="请输入厂商名称" style="width: 250px;"></el-input>
      </el-form-item>


      <el-form-item label="首次预检：">
        <el-select v-model="queryParams.firstInspection" placeholder="全部" style="width: 200px;">
          <el-option label="全部" value=""></el-option>
          <el-option label="合格" value="1"></el-option>
          <el-option label="不合格" value="0"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="二次预检：">
        <el-select v-model="queryParams.secondInspection" placeholder="全部" style="width: 200px;">
          <el-option label="全部" value=""></el-option>
          <el-option label="合格" value="1"></el-option>
          <el-option label="不合格" value="0"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="公告状态：">
        <el-select v-model="queryParams.noticeStatus" placeholder="全部" style="width: 200px;">
          <el-option label="全部" value=""></el-option>
          <el-option label="待审核" value="0"></el-option>
          <el-option label="已通过" value="1"></el-option>
          <el-option label="已拒绝" value="2"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button size="small" type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
      </el-form-item>
    </el-form>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="tableData" style="width: 100%">
      <el-table-column label="公告名称" prop="noticeName" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column label="公告状态" prop="noticeStatus" width="100" align="center">
        <template slot-scope="scope">
          <el-tag
            :type="scope.row.noticeStatus === '1' ? 'success' : (scope.row.noticeStatus === '0' ? 'warning' : 'danger')">
            {{ scope.row.noticeStatusText }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="厂商名称" prop="manufacturerName" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column label="首次预检(款)" prop="firstInspection" width="120" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.firstInspection === '1' ? 'success' : 'danger'" v-if="scope.row.firstInspection">
            {{ scope.row.firstInspection === '1' ? '合格' : '不合格' }}
          </el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="二次预检(款)" prop="secondInspection" width="120" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.secondInspection === '1' ? 'success' : 'danger'" v-if="scope.row.secondInspection">
            {{ scope.row.secondInspection === '1' ? '合格' : '不合格' }}
          </el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" align="center">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)">查看</el-button>
          <el-button size="mini" type="text" icon="el-icon-upload2" @click="handleUpload(scope.row)">上传</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 上传质检报告 -->
    <el-dialog title="上传质检报告" :visible.sync="uploadOpen" width="560px" append-to-body>
      <el-form ref="uploadForm" :model="uploadForm" :rules="uploadRules" label-width="120px">
        <el-form-item label="报告标题：" prop="inspectionReportTitle">
          <el-input v-model="uploadForm.inspectionReportTitle" placeholder="请输入质量检验报告标题" />
        </el-form-item>
        <el-form-item label="报告文件：" prop="url">
          <file-upload v-model="uploadForm.url" :file-type="['pdf']" :limit="1" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancelUpload">取 消</el-button>
        <el-button type="primary" @click="submitUpload">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { suppliceInspectionReportList, addsuppliceInspectionReportSon } from '@/api/school/report'

export default {
  name: 'ZjbgIndex',
  data() {
    return {
      // 遮罩层
      loading: false,
      // 总条数
      total: 0,
      // 表格数据
      tableData: [],
      // 上传弹窗
      uploadOpen: false,
      // 上传表单
      uploadForm: {
        reportId: undefined,
        inspectionReportTitle: '',
        url: ''
      },
      // 上传校验
      uploadRules: {
        inspectionReportTitle: [
          { required: true, message: '请输入质量检验报告标题', trigger: 'blur' }
        ],
        url: [
          { required: true, message: '请上传报告文件', trigger: 'change' }
        ]
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        dateRange: '2024-08-01 - 2025-04-17',
        semester: 'current',
        noticeName: '',
        manufacturerName: '',
        firstInspection: '',
        secondInspection: '',
        noticeStatus: ''
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询列表 */
    getList() {
      this.loading = true;
      suppliceInspectionReportList(this.queryParams).then(response => {
        this.tableData = response.rows || [];
        this.total = response.total || 0;
      }).finally(() => {
        this.loading = false;
      });
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 选择学期 */
    selectSemester(semester) {
      this.queryParams.semester = semester;
      // 根据选择的学期设置日期范围
      switch (semester) {
        case 'upper':
          this.queryParams.dateRange = '2024-03-01 - 2024-07-31';
          break;
        case 'lower':
          this.queryParams.dateRange = '2024-09-01 - 2025-01-31';
          break;
        case 'current':
          this.queryParams.dateRange = '2024-08-01 - 2025-04-17';
          break;
        case 'last':
          this.queryParams.dateRange = '2023-08-01 - 2024-07-31';
          break;
      }
      this.handleQuery();
    },
    /** 查看详情 */
    handleView(row) {
      this.$router.push({ path: '/zjbg/detail', query: { id: row.id } });
    },
    /** 打开上传弹窗 */
    handleUpload(row) {
      this.uploadForm = {
        reportId: row.id,
        inspectionReportTitle: row.noticeName ? `${row.noticeName}质检报告` : '',
        url: ''
      }
      this.uploadOpen = true
      this.$nextTick(() => {
        if (this.$refs.uploadForm) {
          this.$refs.uploadForm.clearValidate()
        }
      })
    },
    /** 取消上传 */
    cancelUpload() {
      this.uploadOpen = false
      this.uploadForm = {
        reportId: undefined,
        inspectionReportTitle: '',
        url: ''
      }
    },
    /** 提交上传 */
    submitUpload() {
      this.$refs.uploadForm.validate(valid => {
        if (!valid) {
          this.$message.error('请完善表单信息')
          return
        }
        addsuppliceInspectionReportSon(this.uploadForm).then(() => {
          this.$message.success('上传成功')
          this.cancelUpload()
          this.getList()
        })
      })
    }
  }
};
</script>

<style scoped>
.search-form {
  background-color: #fff;
  padding: 15px 20px;
  margin-bottom: 15px;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.search-form .el-form-item {
  margin-bottom: 15px;
}

.form-line {
  width: 100%;
  height: 0;
  margin: 5px 0 15px;
  border-bottom: 1px dashed #ebeef5;
}

.semester-buttons {
  margin-left: 10px;
}

.semester-buttons .el-button {
  padding: 8px 15px;
}

.semester-buttons .is-active {
  color: #409EFF;
  background-color: #ecf5ff;
  border-color: #c6e2ff;
}

.date-range-input {
  width: 220px;
  cursor: default;
}

:deep(.el-tag) {
  padding: 0 8px;
  height: 24px;
  line-height: 22px;
}
</style>
