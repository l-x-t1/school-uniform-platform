<template>
  <div>
    <el-form
      v-if="showSearch"
      :model="queryParams"
      ref="queryForm"
      :inline="true"
    >
      <el-form-item label="学校类别" prop="schoolType">
        <el-select v-model="queryParams.schoolType">
          <el-option
            v-for="item of dict.type.sc_sc_type"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <!-- 学校名称 -->
      <el-form-item label="是否完善信息" prop="isComplete">
        <el-select v-model="queryParams.isComplete">
          <el-option label="是" :value="1" />
          <el-option label="否" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery"
          >搜索</el-button
        >
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="tableData" style="width: 100%">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center" type="index" />
      <el-table-column label="学校名称" prop="deptName"></el-table-column>
      <!-- <el-table-column label="学生数量（人）" prop="number"></el-table-column> -->
      <el-table-column label="学校类别" prop="schoolType">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.sc_sc_type"
            :value="scope.row.schoolType"
          />
        </template>
      </el-table-column>
      <el-table-column label="学校区域" prop="schoolArea">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.school_area"
            :value="scope.row.schoolArea"
          />
        </template>
      </el-table-column>
      <el-table-column label="学生数量（人）" prop="peopleCount"></el-table-column>
      <el-table-column label="是否完善信息" prop="isComplete">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isComplete == 1" type="success"
            >已完善</el-tag
          >
          <el-tag v-else type="danger">未完善</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="260">
        <template slot-scope="scope">
          <el-button type="text" v-if="scope.row.isComplete == 1" @click="handleDetail(scope.row)"
            >详细</el-button
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

    <!-- 学校详情弹窗 -->
    <el-dialog
      title="学校详情"
      :visible.sync="detailDialogVisible"
      width="800px"
      :before-close="handleCloseDetail"
      custom-class="school-detail-dialog"
      append-to-body
    >
      <div v-if="detailDialogVisible" class="school-detail-container">
        <div class="school-header">
          <div class="school-logo">
            <img 
              :src="schoolDetail.schoolBadge || require('@/assets/images/school.png')" 
              alt="学校校徽"
              class="logo-image"
            />
          </div>
          <div class="school-title">
            <h2>{{ schoolDetail.schoolName || '暂无学校名称' }}</h2> 
          </div>
        </div>

        <el-divider></el-divider>

        <div class="school-info-grid">
          <div class="info-section">
            <h3 class="section-title">
              <i class="el-icon-location"></i>
              基本信息
            </h3>
            <div class="info-list">
              <div class="info-item">
                <span class="label">学校名称：</span>
                <span class="value">{{ schoolDetail.schoolName || '暂无' }}</span>
              </div>
              <div class="info-item">
                <span class="label">学校类型：</span>
                <span class="value">
                  <dict-tag
                    :options="dict.type.sc_sc_type"
                    :value="schoolDetail.schoolType"
                  />
                </span>
              </div> 
            </div>
          </div>

          <div class="info-section">
            <h3 class="section-title">
              <i class="el-icon-user"></i>
              负责人信息
            </h3>
            <div class="info-list">
              <div class="info-item">
                <span class="label">校长姓名：</span>
                <span class="value">{{ schoolDetail.principalName || '暂无' }}</span>
              </div>
              <div class="info-item">
                <span class="label">联系电话：</span>
                <span class="value contact-phone">
                  <i class="el-icon-phone"></i>
                  {{ schoolDetail.principalPhone || '暂无' }}
                </span>
              </div>
            </div>
          </div>

          <div class="info-section full-width">
            <h3 class="section-title">
              <i class="el-icon-map-location"></i>
              地址信息
            </h3>
            <div class="info-list"> 
              <div class="info-item">
                <span class="label">所在区县：</span>
                <span class="value">{{ schoolDetail.regionCounty || '暂无' }}</span>
              </div>
              <div class="info-item">
                <span class="label">详细地址：</span>
                <span class="value">{{ schoolDetail.detailedAddress || '暂无' }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- <div slot="footer" class="dialog-footer">
        <el-button @click="handleCloseDetail">关闭</el-button>
        <el-button type="primary" @click="handleEdit" v-if="hasEditPermission">
          编辑信息
        </el-button>
      </div> -->
    </el-dialog>
  </div>
</template>
<script>
import { getSchoolListCompleteDetail , getSchoolDetail} from "@/api/jiaoyuju/schoolManagement";
export default {
  dicts: ["sc_sc_type", "school_area"],
  props: {
    deptId: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        parentId: "",
        schoolType: "",
        isComplete: "",
      },
      showSearch: true,
      tableData: [],
      loading: false,
      // 详情弹窗相关
      detailDialogVisible: false,
      detailLoading: false,
      schoolDetail: {},
    };
  },
  computed: {
    hasEditPermission() {
      // 根据实际权限控制显示编辑按钮
      return this.$store.getters.roles.includes('admin') || 
             this.$store.getters.roles.includes('school_manager');
    }
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.loading = true;
      this.queryParams.parentId = this.deptId;
      getSchoolListCompleteDetail(this.queryParams).then((res) => {
        this.tableData = res.rows;
        this.total = res.total;
        this.loading = false;
      });
    },
    resetQuery() {
      this.$refs["queryForm"].resetFields();
      this.getList();
    },
    handleQuery() {
      this.getList();
    },
    handleDetail(row) {
      this.detailDialogVisible = true;
      this.detailLoading = true;
      this.schoolDetail = {};
      
      getSchoolDetail(row.schoolId).then((res) => {
        if (res.code === 200) {
          this.schoolDetail = res.data;
        } else {
          this.$message.error(res.msg || '获取学校详情失败');
        }
        this.detailLoading = false;
      }).catch(() => {
        this.$message.error('获取学校详情失败');
        this.detailLoading = false;
      });
    },
    handleCloseDetail() {
      this.detailDialogVisible = false;
      this.schoolDetail = {};
    },
    handleEdit() {
      // 跳转到编辑页面或打开编辑弹窗
      this.$message.info('编辑功能待实现');
    }, 
    // 获取学校类型颜色
    getSchoolTypeColor(type) {
      const colorMap = {
        '1': 'success',
        '2': 'warning',
        '3': 'danger',
        '4': 'primary',
        '5': 'info'
      };
      return colorMap[type] || 'info';
    },
    // 获取学校区域名称
    getSchoolAreaName(area) {
      const areaMap = {
        '201': '城市',
        '202': '县城',
        '203': '农村'
      };
      return areaMap[area] || '未知区域';
    },
    // 获取学院类型名称
    getAcademyTypeName(type) {
      const typeMap = {
        '1': '公办',
        '2': '民办',
        '3': '其他'
      };
      return typeMap[type] || '未知';
    },
    // 格式化日期时间
    formatDateTime(dateTime) {
      if (!dateTime) return '暂无';
      return new Date(dateTime).toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      });
    }
  },
};
</script>
<style scoped>
.school-detail-container {
  padding: 20px 0;
}

.school-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.school-logo {
  margin-right: 20px;
}

.logo-image {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #e6f7ff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.school-title h2 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.school-tags {
  display: flex;
  gap: 8px;
}

.school-info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.info-section {
  background: #fafafa;
  border-radius: 8px;
  padding: 20px;
  border: 1px solid #e8e8e8;
}

.info-section.full-width {
  grid-column: 1 / -1;
}

.section-title {
  margin: 0 0 16px 0;
  color: #409eff;
  font-size: 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title i {
  font-size: 18px;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  align-items: center;
  min-height: 24px;
}

.info-item .label { 
  color: #606266;
  font-weight: 500;
  margin-right: 12px;
}

.info-item .value {
  color: #303133;
  flex: 1;
}

.info-item .value.highlight {
  color: #409eff;
  font-weight: 600;
  font-size: 16px;
}

.contact-phone {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #67c23a;
}

.contact-phone i {
  font-size: 14px;
}

/* 弹窗样式优化 */
:deep(.school-detail-dialog) {
  border-radius: 12px;
  overflow: hidden;
}

:deep(.school-detail-dialog .el-dialog__header) {
  /* background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); */
  color: white;
  padding: 20px 24px;
}

:deep(.school-detail-dialog .el-dialog__title) {
  /* color: white; */
  font-size: 18px;
  font-weight: 600;
}

:deep(.school-detail-dialog .el-dialog__headerbtn .el-dialog__close) {
  color: white;
  font-size: 18px;
}

:deep(.school-detail-dialog .el-dialog__body) {
  padding: 0 24px 24px;
}

:deep(.school-detail-dialog .el-dialog__footer) {
  padding: 16px 24px;
  border-top: 1px solid #e8e8e8;
  background: #fafafa;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .school-info-grid {
    grid-template-columns: 1fr;
  }
  
  .school-header {
    flex-direction: column;
    text-align: center;
  }
  
  .school-logo {
    margin-right: 0;
    margin-bottom: 16px;
  }
  
  .info-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
  
  .info-item .label {
    min-width: auto;
  }
}
</style>
