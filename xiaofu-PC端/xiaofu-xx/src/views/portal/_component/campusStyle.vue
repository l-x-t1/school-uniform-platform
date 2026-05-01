<template>
  <div class="campus-style-container">
    <!-- 搜索筛选区域 -->
    <div class="search-filter-section">
      <div class="search-filter-content">
        <el-form
          :model="campusStyleParamas"
          ref="queryForm"
          size="small"
          :inline="true"
          label-width="68px"
        >
          <el-form-item label="标题" prop="title">
            <el-input
              v-model="campusStyleParamas.title"
              placeholder="请输入标题关键词"
              clearable
              style="width: 300px"
            />
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
      </div>
    </div>

    <!-- 校园风采内容区域 -->
    <div class="campus-style-section">
      <div class="section-header">
        <div class="header-left"> 
          <div class="header-content">
            <h2 class="section-title">校园风采</h2> 
          </div>
        </div>
      </div>
      
      <div v-loading="loading" class="campus-style-content">
        <div v-if="campusStyleList.length > 0" class="campus-style-grid">
          <div
            class="campus-style-card"
            v-for="item in campusStyleList"
            :key="item.id"
            @click="showCampusStyleDetails(item)"
          >
            <div class="card-image-wrapper">
              <img :src="baseUrl + item.fmImg" :alt="item.title" class="card-image" />
              <div class="card-overlay">
                <div class="overlay-content">
                  <svg-icon icon-class="eye" />
                  <span>查看详情</span>
                </div>
              </div>
            </div>
            <div class="card-content">
              <h3 class="card-title">{{ item.title }}</h3>
              <div class="card-meta">
                <span class="meta-date">{{ formatDate(item.createTime) }}</span>
                <span class="meta-tag">风采</span>
              </div>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无校园风采数据"></el-empty>
      </div>
    </div>

    <!-- 分页组件 -->
    <div class="pagination-section" v-show="total > 0">
      <pagination
        :total="total"
        :page.sync="campusStyleParamas.pageNum"
        :limit.sync="campusStyleParamas.pageSize"
        :pageSizes="[campusStyleParamas.pageSize]"
        @pagination="getCampusStyleData"
      />
    </div>

    <!-- 校园风采详情对话框 -->
    <el-dialog
      :visible.sync="campusStyleDialogVisible"
      width="1200px" 
      append-to-body
      custom-class="campus-style-detail-dialog"
    >
      <div class="dialog-container" v-if="campusStyleDialogVisible">
        <div class="dialog-header">
          <h2 class="campus-style-dialog-title">
            {{ selectedCampusStyle.title }}
          </h2>
          <div class="campus-style-dialog-meta">
            <span>发布时间：{{ selectedCampusStyle.createTime }}</span>
            <span v-if="selectedCampusStyle.createBy"
              >发布人：{{ selectedCampusStyle.createBy }}</span
            >
            <span v-if="selectedCampusStyle.readCount !== undefined"
              >阅读量：{{ selectedCampusStyle.readCount }}</span
            >
          </div>
        </div>
        <div class="dialog-divider"></div>
        <div class="dialog-body">
          <div
            class="campus-style-dialog-content"
            v-html="selectedCampusStyle.content"
          ></div>
          <div v-if="selectedCampusStyle.file != '' && selectedCampusStyle.file != null">
            <file-upload :value="selectedCampusStyle.file" :disabled="true" />
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <!-- <el-button @click="campusStyleDialogVisible = false">关 闭</el-button> -->
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getArticleList, addReadCount, getArticleDetail } from "@/api/publicIndex";
export default {
  name: "CampusStyle",

  components: {
    // 子组件
  },

  props: {
    // 接收的props
  },

  data() {
    return {
      loading: false,
      baseUrl: process.env.VUE_APP_IMAGE_BASE_API,
      campusStyleList: [], // For right side small images with text
      campusStyleDialogVisible: false,
      selectedCampusStyle: {},
      // 响应式数据
      campusStyleParamas: {
        title: null,
        createBy: null,
        pageNum: 1,
        pageSize: 9,
      },
      total: 0,
    };
  },

  computed: {
    // 计算属性
  },

  watch: {
    // 监听器
  },

  created() {
    this.getCampusStyleData();
  },

  mounted() {
    // 组件挂载后执行
  },

  methods: {
    handleQuery() {
      this.campusStyleParamas.pageNum = 1;
      this.getCampusStyleData();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    getCampusStyleData() {
      this.loading = true;
      getArticleList({
        bs: "XYFC",
        ...this.campusStyleParamas,
      }).then((res) => {
        this.campusStyleList = res.rows;
        this.total = res.total;
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    async showCampusStyleDetails(item) {
      try {
        // 先调用增加阅读数量接口
        await addReadCount(item.id);
        // 然后获取详情
        const res = await getArticleDetail(item.id);
        this.selectedCampusStyle = {
          ...item,
          ...res.data,
          readCount: res.data.readCount || 0
        };
        this.campusStyleDialogVisible = true;
      } catch (error) {
        console.error('获取详情失败:', error);
        this.$message.error('获取详情失败');
      }
    },
    formatDate(dateString) {
      if (!dateString) return "";
      return dateString.substring(0, 10);
    },
    // 初始化方法
    init() {
      console.log("CampusStyle component initialized");
    },
  },

  beforeDestroy() {
    // 组件销毁前执行清理工作
  },
};
</script>

<style lang="scss" scoped>
.campus-style-container {
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
        
        .el-input {
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

/* 校园风采内容区域 */
.campus-style-section {
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
    
    .header-icon {
      width: 60px;
      height: 60px;
      background: linear-gradient(135deg, #3b82f6, #1d4ed8);
      border-radius: 16px;
      display: flex;
      align-items: center;
      justify-content: center;
      
      .svg-icon {
        font-size: 28px;
        color: white;
      }
    }
    
    .header-content {
      .section-title {
        font-size: 28px;
        font-weight: 600;
        color: #1e293b;
        margin: 0 0 8px 0;
      }
      
      .section-subtitle {
        font-size: 16px;
        color: #64748b;
        margin: 0;
      }
    }
  }
}

.campus-style-content {
  .campus-style-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 25px;
  }
  
  .campus-style-card {
    background: white;
    border-radius: 16px;
    overflow: hidden;
    cursor: pointer;
    transition: all 0.3s ease;
    border: 1px solid #f1f5f9;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
      border-color: #3b82f6;
      
      .card-image-wrapper img {
        transform: scale(1.05);
      }
      
      .card-overlay {
        opacity: 1;
      }
    }
    
    .card-image-wrapper {
      position: relative;
      width: 100%;
      height: 220px;
      overflow: hidden;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: transform 0.3s ease;
      }
      
      .card-overlay {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0, 0, 0, 0.7);
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        opacity: 0;
        transition: opacity 0.3s ease;
        
        .overlay-content {
          text-align: center;
          
          .svg-icon {
            font-size: 32px;
            margin-bottom: 8px;
            display: block;
          }
          
          span {
            font-size: 14px;
            font-weight: 500;
          }
        }
      }
    }
    
    .card-content {
      padding: 20px;
      
      .card-title {
        font-size: 16px;
        font-weight: 600;
        color: #1e293b;
        margin: 0 0 12px 0;
        line-height: 1.4;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
        text-overflow: ellipsis;
      }
      
      .card-meta {
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        .meta-date {
          font-size: 13px;
          color: #9ca3af;
        }
        
        .meta-tag {
          font-size: 12px;
          color: #3b82f6;
          background: rgba(59, 130, 246, 0.1);
          padding: 4px 12px;
          border-radius: 12px;
          font-weight: 500;
        }
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
::v-deep .campus-style-detail-dialog {
  border-radius: 12px;
  // .el-dialog__header {
  //   display: none;
  // }
  .el-dialog__body {
    padding: 0;
  }
}

.dialog-container {
  height: 80vh;
  display: flex;
  flex-direction: column;
}

.dialog-header {
  padding: 30px 40px 20px 40px;
  background: #fff;
  border-bottom: 1px solid #e2e8f0;
  flex-shrink: 0;
}

.dialog-divider {
  height: 1px;
  background: linear-gradient(90deg, transparent, #e2e8f0, transparent);
  margin: 0 40px;
}

.dialog-body {
  flex: 1;
  overflow-y: auto;
  padding: 10px 40px 0px 40px;
}

.campus-style-dialog-title {
  text-align: center;
  font-size: 26px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
  margin-top: 0;
}

.campus-style-dialog-meta {
  text-align: center;
  font-size: 14px;
  color: #888;
  span {
    margin: 0 15px;
  }
}

.campus-style-dialog-content {
  line-height: 1.8;
  color: #333;
  img {
    max-width: 100%;
  }
  margin-top: 0;
}

.dialog-footer {
  text-align: center;
  margin-top: 30px;
  .el-button {
    padding: 12px 40px;
    font-size: 16px;
  }
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .campus-style-content .campus-style-grid {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  }
}

@media (max-width: 768px) {
  .campus-style-container {
    padding: 20px 15px;
  }
  
  .search-filter-section,
  .campus-style-section {
    padding: 25px;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
    
    .header-left {
      .header-icon {
        width: 50px;
        height: 50px;
        
        .svg-icon {
          font-size: 24px;
        }
      }
      
      .header-content {
        .section-title {
          font-size: 24px;
        }
        
        .section-subtitle {
          font-size: 14px;
        }
      }
    }
  }
  
  .campus-style-content .campus-style-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  }
  
  .search-filter-content .el-form {
    flex-direction: column;
    align-items: stretch;
    
    .el-form-item {
      width: 100%;
      
      .el-input {
        width: 100%;
      }
    }
  }
}

@media (max-width: 480px) {
  .campus-style-container {
    padding: 15px 10px;
  }
  
  .search-filter-section,
  .campus-style-section {
    padding: 20px;
  }
  
  .section-header .header-left .header-content .section-title {
    font-size: 20px;
  }
  
  .campus-style-content .campus-style-grid {
    grid-template-columns: 1fr;
  }
  
  .campus-style-content .campus-style-card {
    .card-image-wrapper {
      height: 180px;
    }
    
    .card-content {
      padding: 15px;
      
      .card-title {
        font-size: 14px;
      }
      
      .card-meta {
        .meta-date {
          font-size: 12px;
        }
        
        .meta-tag {
          font-size: 11px;
          padding: 3px 8px;
        }
      }
    }
  }
}
</style>
