<template>
  <div class="policy-container">
    <!-- 搜索筛选区域 -->
    <div class="search-filter-section">
      <div class="search-filter-content">
        <el-form
          :model="policyQueryParamas"
          ref="queryForm"
          size="small"
          :inline="true"
          label-width="68px"
        >
          <el-form-item label="标题" prop="title">
            <el-input
              v-model="policyQueryParamas.title"
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

    <!-- 政策法规内容区域 -->
    <div class="policy-section">
      <div class="section-header">
        <div class="header-left"> 
          <div class="header-content">
            <h2 class="section-title">政策法规</h2> 
          </div>
        </div>
      </div>
      
      <div v-loading="loading" class="policy-content">
        <div v-if="policyList.length > 0" class="policy-list">
          <div
            class="policy-item"
            v-for="item in policyList"
            :key="item.id"
            @click="handleDetails(item)"
          >
            <div class="policy-dot"></div>
            <div class="policy-info">
              <h3 class="policy-title">{{ item.title }}</h3>
              <div class="policy-meta">
                <span class="meta-item">
                  <svg-icon icon-class="user" />
                  {{ item.createBy || '未知' }}
                </span>
                <span class="meta-item">
                  <svg-icon icon-class="time" />
                  {{ formatDate(item.createTime) }}
                </span>
              </div>
            </div>
            <div class="policy-arrow">
              <svg-icon icon-class="arrow-right" />
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无政策法规数据"></el-empty>
      </div>
    </div>

    <!-- 分页组件 -->
    <div class="pagination-section" v-show="total > 0">
      <pagination 
        :total="total"
        :page.sync="policyQueryParamas.pageNum"
        :limit.sync="policyQueryParamas.pageSize"
        :pageSizes="[policyQueryParamas.pageSize]"
        @pagination="handleGetArticleList"
      />
    </div>

    <!-- 文章详情弹框 -->
    <el-dialog
      :visible.sync="dialogVisible"
      width="1200px" 
      append-to-body
      custom-class="policy-detail-dialog"
    >
      <div class="dialog-container" v-if="dialogVisible">
        <div class="dialog-header">
          <h2 class="policy-dialog-title">{{ dialogData.title }}</h2>
          <div class="policy-dialog-meta">
            <span>发布时间：{{ dialogData.createTime }}</span>
            <span v-if="dialogData.createBy">发布人：{{ dialogData.createBy }}</span>
            <span v-if="dialogData.readCount !== undefined">阅读量：{{ dialogData.readCount }}</span>
          </div>
        </div>
        <div class="dialog-divider"></div>
        <div class="dialog-body">
          <div class="policy-dialog-content" v-html="dialogData.content"></div>
          <div v-if="dialogData.file != '' && dialogData.file != null">
            <file-upload :value="dialogData.file" :disabled="true" />
          </div>
        </div>
        
      </div>
      <div slot="footer" class="dialog-footer">
        <!-- <el-button @click="handleClose">关 闭</el-button> -->
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getArticleList, addReadCount, getArticleDetail } from "@/api/publicIndex";
export default {
  name: "Policy",
  components: {},
  props: {},
  data() {
    return {
      loading: false,
      policyQueryParamas: {
        title: null,
        createBy: null,
        pageNum: 1,
        pageSize: 10,
      },
      total: 0,
      policyList: [],
      dialogVisible: false,
      dialogData: {
        title: "",
        createTime: "",
        createBy: "",
        content: "",
        readCount: 0,
      },
    };
  },
  watch: {},
  computed: {},
  created() {
    this.handleGetArticleList();
  },

  mounted() {},
  methods: {
    handleQuery() {
      this.policyQueryParamas.pageNum = 1;
      this.handleGetArticleList();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleGetArticleList() {
      this.loading = true;
      getArticleList({
        bs: "ZCFG",
        ...this.policyQueryParamas,
      }).then((res) => {
        this.policyList = res.rows;
        this.total = res.total;
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    async handleDetails(item) {
      try {
        // 先调用增加阅读数量接口
        await addReadCount(item.id);
        // 然后获取详情
        const res = await getArticleDetail(item.id);
        this.dialogData = {
          title: item.title || "",
          createTime: item.createTime || "",
          createBy: item.createBy || "",
          content: res.data.content || item.content || "",
          file: res.data.file || item.file || "",
          readCount: res.data.readCount || 0,
        };
        this.dialogVisible = true;
      } catch (error) {
        console.error('获取详情失败:', error);
        this.$message.error('获取详情失败');
      }
    },
    handleClose() {
      this.dialogVisible = false;
      this.dialogData = {
        title: "",
        createTime: "",
        createBy: "",
        content: "",
        file: "",
        readCount: 0,
      };
    },
    formatDate(dateString) {
      if (!dateString) return "";
      return dateString.substring(0, 10);
    },
  },
};
</script>

<style scoped lang="scss">
.policy-container {
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

/* 政策法规内容区域 */
.policy-section {
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

.policy-content {
  .policy-list {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }
  
  .policy-item {
    display: flex;
    align-items: center;
    padding: 20px;
    background: #f8fafc;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.3s ease;
    border: 1px solid #e2e8f0;
    
    &:hover {
      background: white;
      box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
      border-color: #3b82f6;
      transform: translateX(5px);
      
      .policy-title {
        color: #3b82f6;
      }
      
      .policy-arrow .svg-icon {
        color: #3b82f6;
        transform: translateX(3px);
      }
    }
    
    .policy-dot {
      width: 8px;
      height: 8px;
      background: #3b82f6;
      border-radius: 50%;
      margin-right: 20px;
      flex-shrink: 0;
    }
    
    .policy-info {
      flex: 1;
      
      .policy-title {
        font-size: 16px;
        font-weight: 600;
        color: #1e293b;
        margin: 0 0 10px 0;
        line-height: 1.4;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
        text-overflow: ellipsis;
        transition: color 0.3s ease;
      }
      
      .policy-meta {
        display: flex;
        gap: 20px;
        
        .meta-item {
          display: flex;
          align-items: center;
          gap: 6px;
          font-size: 13px;
          color: #64748b;
          
          .svg-icon {
            font-size: 14px;
            color: #3b82f6;
          }
        }
      }
    }
    
    .policy-arrow {
      margin-left: 20px;
      
      .svg-icon {
        font-size: 18px;
        color: #9ca3af;
        transition: all 0.3s ease;
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
::v-deep .policy-detail-dialog {
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

.policy-dialog-title {
  text-align: center;
  font-size: 26px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
  margin-top: 0;
}

.policy-dialog-meta {
  text-align: center;
  font-size: 14px;
  color: #888;
  span {
    margin: 0 15px;
  }
}

.policy-dialog-content {
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
@media (max-width: 768px) {
  .policy-container {
    padding: 20px 15px;
  }
  
  .search-filter-section,
  .policy-section {
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
  
  .policy-content .policy-item {
    padding: 15px;
    
    .policy-info {
      .policy-title {
        font-size: 14px;
      }
      
      .policy-meta {
        flex-direction: column;
        gap: 8px;
        
        .meta-item {
          font-size: 12px;
        }
      }
    }
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
  .policy-container {
    padding: 15px 10px;
  }
  
  .search-filter-section,
  .policy-section {
    padding: 20px;
  }
  
  .section-header .header-left .header-content .section-title {
    font-size: 20px;
  }
  
  .policy-content .policy-item {
    padding: 12px;
    
    .policy-dot {
      width: 6px;
      height: 6px;
      margin-right: 15px;
    }
    
    .policy-info {
      .policy-title {
        font-size: 13px;
      }
      
      .policy-meta .meta-item {
        font-size: 11px;
      }
    }
    
    .policy-arrow .svg-icon {
      font-size: 16px;
    }
  }
}
</style>
