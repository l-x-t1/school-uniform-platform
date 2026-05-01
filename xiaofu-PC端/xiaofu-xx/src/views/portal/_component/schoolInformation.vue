<template>
  <div class="school-information-container">
    <!-- 搜索筛选区域 -->
    <div class="search-filter-section">
      <div class="search-filter-content">
        <el-form
          :model="queryParams"
          ref="queryForm"
          size="small"
          :inline="true"
          label-width="68px"
        >
          <el-form-item label="学校名称" prop="name">
            <el-input
              v-model="queryParams.name"
              placeholder="请输入学校名称"
              class="search-input"
              clearable
            />
          </el-form-item>
          <el-form-item label="所属区县" prop="area">
            <el-select v-model="queryParams.area" placeholder="请选择所属区县">
              <el-option v-for="item of areaList" :key="item.deptId" :label="item.deptName" :value="item.deptId"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="getSchoolList">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 学校信息内容区域 -->
    <div class="school-information-section">
      <div class="section-header">
        <div class="header-left">
          <div class="header-content">
            <h2 class="section-title">学校信息</h2> 
          </div>
        </div>
      </div>
      
      <div v-loading="loading" class="school-information-content">
        <div v-if="schoolList.length > 0" class="schools-grid">
          <div
            class="school-card"
            v-for="school in schoolList"
            :key="school.deptId"
            @click="showSchoolDetails(school)"
          >
            <div class="school-logo">
              <img 
                v-if="school.schoolImage"
                :src="baseUrl + school.schoolImage"
                :alt="school.deptName"
              />
              <img
                v-else
                src="@/assets/images/xuexiao.png"
                :alt="school.deptName"
              />
            </div>
            <div class="school-info">
              <div class="school-name" :title="school.deptName">{{ school.deptName }}</div>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无学校信息数据"></el-empty>
      </div>
    </div>

    <!-- 分页组件 -->
    <div class="pagination-section" v-show="total > 0">
      <pagination
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        :pageSizes="[queryParams.pageSize]"
        @pagination="getSchoolList"
      />
    </div>

    <!-- 学校详情对话框 -->
    <el-dialog
      :visible.sync="schoolDialogVisible"
      width="600px"
      :show-close="true"
      title="学校详情"
      append-to-body
    >
      <div v-if="selectedSchool" class="school-details-content">
        <div class="school-logo-wrapper">
          <img
            v-if="selectedSchool.schoolImage"
            :src="baseUrl + selectedSchool.schoolImage"
            alt="logo"
            class="school-logo"
          />
          <img
            v-else
            src="@/assets/images/xuexiao.png"
            alt="logo"
            class="school-logo"
          />
        </div>
        <h3 class="school-dialog-name">{{ selectedSchool.deptName }}</h3>
        <div class="school-info-list">
          <p>
            <strong>学校类别：</strong>
            {{
              selectedSchool.schoolType == 0
                ? "高级中学"
                : selectedSchool.schoolType == 1
                ? "初级中学"
                : selectedSchool.schoolType == 2
                ? "小学"
                : selectedSchool.schoolType == 3
                ? "幼儿园"
                : "其他"
            }}
          </p>
          <p>
            <strong>学校区域：</strong>
            {{ selectedSchool.schoolArea == 1 ? "乡镇学校" : "城市学校" }}
          </p>
          <p>
            <strong>学校地区：</strong>
            {{
              getSchoolArea(
                selectedSchool.regionProvince,
                selectedSchool.regionCity,
                selectedSchool.regionCounty
              ) || "未知"
            }}
          </p>
          <p>
            <strong>详细地址：</strong>
            {{ selectedSchool.detailedAddress || "未提供" }}
          </p>
          <p>
            <strong>学校简介：</strong>
            {{ selectedSchool.remark || "未提供" }}
          </p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getSchoolList, getDeptList } from "@/api/publicIndex";
import Pagination from "@/components/Pagination";

export default {
  dicts: ["sc_sc_type"],
  name: "SchoolInformation",
  components: {
    Pagination,
  },
  props: {},
  data() {
    return {
      baseUrl: process.env.VUE_APP_IMAGE_BASE_API,
      loading: false,
      areaList: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 20, // 每页显示数量
        name: "", // 搜索用
        area: null, // 所属区县
      },
      schoolList: [],
      schoolDialogVisible: false,
      selectedSchool: null,
    };
  },
  watch: {},
  computed: {},
  created() {
    getDeptList().then((res) => {
      this.areaList = res.data;
    });
    this.getSchoolList();
  },
  mounted() {},
  methods: {
    resetQuery() {
      this.resetForm("queryForm");
      this.getSchoolList();
    },
    showSchoolDetails(school) {
      this.selectedSchool = school;
      this.schoolDialogVisible = true;
    },
    async getSchoolList() {
      this.loading = true;
      const res = await getSchoolList(this.queryParams);
      this.schoolList = res.rows;
      this.total = res.total;
      this.loading = false;
    },
    getSchoolArea(province, city, county) {
      const areas = [];
      if (province) areas.push(province);
      if (city && city !== province) areas.push(city);
      if (county && county !== city) areas.push(county);
      return areas.join(" - ");
    },
  },
};
</script>

<style lang="scss" scoped>
.school-information-container {
  max-width: 1400px;
  width: 100%;
  margin: 0 auto;
  padding: 20px 20px 40px;
  background: #f8fafc;
}

/* 搜索筛选区域 */
.search-filter-section {
  background: white;
  border-radius: 20px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #f1f5f9;
  
  .search-filter-content {
    .el-form {
      display: flex;
      flex-wrap: wrap;
      gap: 20px;
      align-items: center;
      
      .el-form-item {
        margin-bottom: 0;
        
        .el-form-item__label {
          font-weight: 500;
          color: #374151;
        }
        
        .el-input,
        .el-select {
          .el-input__inner {
            border-radius: 8px;
            border: 1px solid #e2e8f0;
            
            &:focus {
              border-color: #3b82f6;
            }
          }
        }
        
        .el-button {
          border-radius: 8px;
          font-weight: 500;
          
          &.el-button--primary {
            background: linear-gradient(135deg, #3b82f6, #1d4ed8);
            border: none;
            
            &:hover {
              background: linear-gradient(135deg, #2563eb, #1e40af);
              transform: translateY(-1px);
            }
          }
          
          &.el-button--default {
            border: 1px solid #e2e8f0;
            color: #64748b;
            
            &:hover {
              border-color: #3b82f6;
              color: #3b82f6;
            }
          }
        }
      }
    }
  }
}

/* 学校信息内容区域 */
.school-information-section {
  background: white;
  border-radius: 20px;
  padding: 40px;
  margin-bottom: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #f1f5f9;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f1f5f9;
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 20px;
    
    .header-content {
      .section-title {
        font-size: 28px;
        font-weight: 600;
        color: #1e293b;
        margin: 0 0 5px 0;
      }
      
      .section-subtitle {
        font-size: 14px;
        color: #64748b;
        margin: 0;
      }
    }
  }
}

.school-information-content {
  .schools-grid {
    display: grid;
    grid-template-columns: repeat(5, minmax(0, 1fr));
    gap: 20px;
  }
  
  .school-card {
    background: #f8fafc;
    border-radius: 12px;
    padding: 20px;
    text-align: center;
    cursor: pointer;
    transition: all 0.3s ease;
    border: 1px solid #e2e8f0;
    
    &:hover {
      background: white;
      box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
      border-color: #3b82f6;
      transform: translateY(-3px);
    }
    
    .school-logo {
      width: 50px;
      height: 50px;
      margin: 0 auto 15px;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: contain;
        border-radius: 8px;
      }
    }
    
    .school-info {
      .school-name {
        font-size: 14px;
        font-weight: 500;
        color: #1e293b;
        margin: 0;
        line-height: 1.4;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }
}

/* 分页组件区域 */
.pagination-section {
  background: white;
  border-radius: 20px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #f1f5f9;
  display: flex;
  justify-content: center;
}

/* 对话框样式 */
.school-details-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.school-logo-wrapper {
  padding: 10px;
  border: 1px solid #eee;
  border-radius: 8px;
  background-color: #f9f9f9;
}

.school-logo {
  width: 60px;
  height: 60px;
  object-fit: contain;
}

.school-dialog-name {
  font-size: 22px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.school-info-list {
  align-self: flex-start;
  padding-left: 50px;
  p {
    font-size: 16px;
    color: #555;
    margin: 8px 0;
    strong {
      color: #333;
    }
  }
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .school-information-content .schools-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 768px) {
  .school-information-container {
    padding: 20px 15px;
  }
  
  .search-filter-section,
  .school-information-section {
    padding: 25px;
  }
  
  .section-header .header-left .header-content .section-title {
    font-size: 24px;
  }
  
  .school-information-content .schools-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .search-filter-content .el-form {
    flex-direction: column;
    align-items: stretch;
    
    .el-form-item {
      width: 100%;
      
      .el-input,
      .el-select {
        width: 100%;
      }
    }
  }
}

@media (max-width: 480px) {
  .school-information-container {
    padding: 15px 10px;
  }
  
  .search-filter-section,
  .school-information-section {
    padding: 20px;
  }
  
  .section-header .header-left .header-content .section-title {
    font-size: 20px;
  }
  
  .school-information-content .schools-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .school-information-content .school-card {
    padding: 15px;
    
    .school-logo {
      width: 40px;
      height: 40px;
      margin-bottom: 10px;
    }
    
    .school-info {
      .school-name {
        font-size: 13px;
      }
    }
  }
}
</style>
