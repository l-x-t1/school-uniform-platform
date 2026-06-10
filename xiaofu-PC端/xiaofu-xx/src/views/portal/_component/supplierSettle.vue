<template>
  <div class="supplier-settle-container">
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
          <el-form-item label="地区" prop="area">
            <el-select v-model="queryParams.area" placeholder="请选择地区">
              <el-option
                v-for="item of areaList"
                :key="item.deptId"
                :value="item.deptId"
                :label="item.deptName"
              ></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="标题" prop="title">
            <el-input
              v-model="queryParams.title"
              placeholder="请输入标题"
              clearable
              style="width: 240px"
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

    <!-- 供应商入驻公告内容区域 -->
    <div class="announcements-section">
      <div class="section-header">
        <h2 class="section-title">供应商入驻公告</h2> 
      </div>
      
      <div v-loading="loading" class="announcements-content">
        <div v-if="articleList.length > 0" class="announcements-list">
          <div
            class="announcement-card"
            v-for="item in articleList"
            :key="item.id"
            :class="{ 'empty-item': item.isEmpty }"
            @click="handleAnnouncementDetails(item)"
          >
            <div class="announcement-header">
              <h3 class="announcement-title">{{ item.isBc == 1? item.title +'（补充）':item.title }}</h3>
            </div>
            <div class="announcement-meta">
              <span class="meta-item">地区：{{ item.areaname || '未知' }}</span>
              <span class="meta-item">发布人：{{ item.createBy || '未知' }}</span>
             <span v-if="isExpired(item)" class="meta-item expired-tag">已过期</span>
            </div>
            <div class="announcement-actions">
              <el-button
                size="mini"
                type="primary"
                plain
                :disabled="isExpired(item)"
                icon="el-icon-s-check"
                @click.stop="handleApply(item)"
              >{{ isExpired(item) ? '已过期' : '申请入驻' }}</el-button>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无数据"></el-empty>
      </div>
    </div>

    <!-- 分页组件 -->
    <div class="pagination-section" v-show="total > 0">
      <pagination
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        :pageSizes="[queryParams.pageSize]"
        @pagination="getArticleList"
      />
    </div>

    <!-- 公告详情对话框 -->
    <el-dialog
      :visible.sync="announcementDialogVisible"
      width="1200px"
      append-to-body
      custom-class="supplier-settle-detail-dialog"
    >
      <div class="dialog-container" v-if="announcementDialogVisible">
        <div class="dialog-header">
          <h2 class="policy-dialog-title">{{ selectedAnnouncement.isBc == 1? selectedAnnouncement.title +'（补充）':selectedAnnouncement.title }}</h2>
          <div class="policy-dialog-meta">
            <span v-if="selectedAnnouncement.endTime"
              >截止时间：{{ selectedAnnouncement.endTime }}</span
            >
            <span v-if="selectedAnnouncement.createBy"
              >发布人：{{ selectedAnnouncement.createBy }}</span
            >
            <span v-if="selectedAnnouncement.readCount !== undefined"
              >阅读量：{{ selectedAnnouncement.readCount }}</span
            >
          </div>
        </div>
        <div class="dialog-divider"></div>
        <div class="dialog-body">
          <div
            class="policy-dialog-content"
            v-html="selectedAnnouncement.content"
          ></div>
          <iframe
            v-if="previewUrl"
            :src="previewUrl"
            width="100%"
            height="100%"
            frameborder="0"
            style="border: none"
          ></iframe>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
<el-button type="primary" icon="el-icon-s-check" :disabled="isExpired(selectedAnnouncement)" @click="handleApply(selectedAnnouncement)">
          {{ isExpired(selectedAnnouncement) ? '已过期' : '申请入驻' }}
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getArticleList, getDeptList, addReadCount, getArticleDetail } from "@/api/publicIndex";
import { getPublicEntryContext } from "@/api/supplierSettle";
import { Base64 } from "js-base64";

export default {
  name: "SupplierSettle",
  data() {
    return {
      loading: true,
      areaList: [],
      total: 0,
      announcementDialogVisible: false,
      selectedAnnouncement: {},
      previewUrl: "",
      queryParams: {
        area: null,
        deptName: null,
        title: null,
        pageNum: 1,
        pageSize: 10,
      },
      articleList: [],
      loading: false,
      pageSize: 10,
      selectedId: null,
    };
  },
  created() {
    getDeptList().then((res) => {
      this.areaList = res.data;
    });
    this.getArticleList();
  },
  methods: {
    async handleAnnouncementDetails(item) {
      if (item.isEmpty) return;
      try {
        await addReadCount(item.id);
        const res = await getArticleDetail(item.id);
        this.selectedAnnouncement = {
          ...item,
          ...res.data,
          readCount: res.data.readCount || 0
        };
        if (item.ggFile) { 
          this.previewUrl = process.env.VUE_APP_IMAGE_BASE_API + item.ggFile
        }else if (item.file) {
          this.previewUrl = process.env.VUE_APP_IMAGE_BASE_API + item.file
        }
        this.announcementDialogVisible = true;
      } catch (error) {
        console.error('获取详情失败:', error);
        this.$message.error('获取详情失败');
      }
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getArticleList();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.queryParams.deptId = undefined;
      this.handleQuery();
    },
    async getArticleList() {
      this.loading = true;
      try {
        const res = await getArticleList({
          bs: "GYSRZGG",
          type: 0,
          ...this.queryParams,
        });
        this.articleList = res.rows || [];
        this.total = res.total;
        this.loading = false;
      } catch (error) {
        this.loading = false;
      }
    },

    formatDate(dateString) {
      if (!dateString) return "";
      return dateString.substring(0, 10);
    },

    selectItem(id) {
      this.selectedId = id;
      this.goDetail(id);
    },

    async handleApply(item) {
      if (!item || !item.id) return;
      try {
        const res = await getPublicEntryContext(item.id);
        const data = res.data;
        if (!data || !data.canSubmit) {
          this.$message.warning(data && data.message ? data.message : "该公告当前不可提交入驻申请");
          return;
        }
        const routeData = this.$router.resolve({
          path: "/supplier-settle",
          query: { noticeId: item.id, step: "login" },
        });
        window.open(routeData.href, "_blank");
      } catch (e) {
        this.$message.error("校验公告失败，请稍后重试");
      }
    },

    goDetail(id) {
      this.$emit("goDetail", "GYSRZGG", id);
    },

    goMore() {
      this.$emit("goMore", "GYSRZGG");
    },

    goHome() {
      this.selectedId = null;
      this.$emit("goHome");
    },
    isExpired(item) {
      if (!item || !item.endTime) return false;
      return new Date(item.endTime) < new Date();
    },
  },
};
</script>

<style lang="scss" scoped>
.supplier-settle-container {
  max-width: 1400px;
  width: 100%;
  margin: 0 auto;
  padding: 40px 20px;
  box-sizing: border-box;
}

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
      align-items: center;
      gap: 10px;

      .el-form-item {
        margin-bottom: 0;
      }
    }
  }
}

.announcements-section {
  background: white;
  border-radius: 20px;
  padding: 30px;
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
  
  .section-title {
    font-size: 24px;
    font-weight: 600;
    color: #1e293b;
    margin: 0;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  
  .section-subtitle {
    font-size: 14px;
    color: #64748b;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}

.announcements-content {
  .announcements-list {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
  }
  
  .announcement-actions {
    margin-top: 12px;
    padding-top: 12px;
    border-top: 1px solid #e2e8f0;
    display: flex;
    justify-content: flex-end;
  }

  .announcement-card {
    background: #f8fafc;
    border-radius: 12px;
    padding: 20px;
    cursor: pointer;
    transition: all 0.3s ease;
    border: 1px solid #e2e8f0;
    
    &:hover {
      background: white;
      box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
      border-color: #3b82f6;
      transform: translateY(-2px);
    }
    
    &.empty-item {
      opacity: 0.6;
      cursor: not-allowed;
      
      &:hover {
        transform: none;
        box-shadow: none;
        border-color: #e2e8f0;
      }
    }
    
    .announcement-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 15px;
      
      .announcement-title {
        font-size: 16px;
        font-weight: 600;
        color: #1e293b;
        margin: 0;
        line-height: 1.4;
        flex: 1;
        margin-right: 15px;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
        text-overflow: ellipsis;
      }
      
      .announcement-date {
        font-size: 12px;
        color: #64748b;
        background: #e2e8f0;
        padding: 4px 8px;
        border-radius: 6px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }
    
    .announcement-meta {
      display: flex;
      flex-wrap: wrap;
      gap: 10px;
      
      .meta-item {
        font-size: 12px;
        color: #64748b;
        background: white;
        padding: 4px 8px;
        border-radius: 6px;
        border: 1px solid #e2e8f0;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }

      .expired-tag {
        color: #dc2626;
        background: #fef2f2;
        border-color: #fecaca;
        font-weight: 600;
      }
    }
  }
  
}

.pagination-section {
  background: white;
  border-radius: 20px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #f1f5f9;
  display: flex;
  justify-content: center;
}

::v-deep .supplier-settle-detail-dialog {
  border-radius: 12px;
  .el-dialog__body {
    padding: 0;
  }
}

.dialog-container { 
  display: flex;
  flex-direction: column;
  height: 80vh;
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

.policy-dialog-footer {
  text-align: center;
  margin-top: 30px;
  .el-button {
    padding: 12px 40px;
    font-size: 16px;
  }
}

@media (max-width: 1200px) {
  .announcements-content .announcements-list {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .supplier-settle-container {
    padding: 20px 15px;
  }
  
  .search-filter-section,
  .announcements-section {
    padding: 25px;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
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
  .supplier-settle-container {
    padding: 15px 10px;
  }
  
  .search-filter-section,
  .announcements-section {
    padding: 20px;
  }
  
  .section-header .section-title {
    font-size: 20px;
  }
  
  .announcement-card {
    padding: 15px;
    
    .announcement-header .announcement-title {
      font-size: 14px;
    }
    
    .announcement-meta .meta-item {
      font-size: 11px;
      padding: 3px 6px;
    }
  }
}
</style>
