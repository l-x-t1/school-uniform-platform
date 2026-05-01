<template>
  <div class="public-index">
    <!-- 面包屑导航 -->
    <div class="breadcrumb">
      <span>当前位置：</span>
      <span @click="goHome">首页</span> &gt; <span>政策法规</span>
    </div>
    <div class="main-content">
      <!-- 左侧分类 -->
      <div class="category-list">
        <div
          v-for="item in categoryList"
          :key="item.id"
          :class="['category-item', { active: item.id === activeCategory }]"
          @click="handleCategoryChange(item)"
        >
          <!-- <span class="category-dot" /> -->
          <span class="category-name">{{ item.name }}</span>
        </div>
      </div>
      <!-- 右侧文章列表 -->
      <div class="article-list" style="width: calc(100% - 180px)">
        <div
          v-for="item in articleList"
          :key="item.id"
          class="article-item"
          @click="goDetail(item.id)"
        >
          <span class="article-title">
            <span class="article-dot" />
            {{ item.title }}
          </span>
          <span class="date">{{ item.createTime.split(" ")[0] }}</span>
        </div>
        <!-- 分页 -->
        <pagination
          v-show="total > queryParams.pageSize"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { getArticleList, getNoticeColumnList } from "@/api/publicIndex";
import { parseTime } from "@/utils/index";
export default {
  name: "zcfgList",
  props: {
    bs: {
      type: String,
      default: null,
    },
  },
  data() {
    return {
      categoryList: [],
      activeCategory: null,
      queryParams: {
        bs: null,
        pageNum: 1,
        pageSize: 20,
      },
      articleList: [],
      total: 0,
    };
  },
  created() {
    this.getCategoryList();
  },
  methods: {
    async getCategoryList() {
      const res = await getNoticeColumnList();
      res.data.forEach((element) => {
        if (element.bs === this.bs) {
          this.categoryList = element.children;
          this.activeCategory = this.categoryList[0].id;
          this.queryParams.bs = this.categoryList[0].bs;
          this.getList();
        }
      });
    },
    async getList() {
      const res = await getArticleList({ ...this.queryParams, type: 0 });
      this.articleList = res.rows;
      this.total = res.total;
    },
    handleCategoryChange(item) {
      this.activeCategory = item.id;
      this.queryParams.bs = item.bs;
      this.queryParams.pageNum = 1;
      this.getList();
    },
    handlePageChange(page) {
      this.queryParams.pageNum = page;
      this.getList();
    },
    goHome() {
      this.$emit("goHome");
    },
    goDetail(id) {
      window.open(this.$route.path + "?id=" + id + "&bs=" + this.bs);
    },
  },
};
</script>

<style scoped>
.public-index {
  padding: 20px 0 0 0;
  min-height: 80vh;
}
.breadcrumb {
  margin-bottom: 24px;
  color: #888;
  font-size: 13px;
}

.breadcrumb span:nth-child(2) {
  color: #2457a7;
  cursor: pointer;
}

.breadcrumb span:nth-child(2):hover {
  text-decoration: underline;
}
.main-content {
  display: flex;
}
.category-list {
  width: 180px;
  background: #fafbfc;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.03);
  padding: 16px 0;
  margin-right: 32px;
}
.category-item {
  display: flex;
  align-items: center;
  padding: 10px 24px;
  font-size: 15px;
  color: #333;
  border-left: 3px solid transparent;
  cursor: pointer;
  transition: background 0.2s, color 0.2s, border-color 0.2s;
  position: relative;
}
.category-item:not(:last-child) {
  border-bottom: 1px solid #f0f0f0;
}
.category-item:hover {
  background: #f5f7fa;
  color: #2457a7;
}
.category-item.active {
  background: #fff;
  color: #2457a7;
  border-left: 3px solid #2457a7;
  font-weight: 600;
}
.category-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #2457a7;
  margin-right: 10px;
  opacity: 0.7;
}
.category-item.active .category-dot {
  background: #2457a7;
  opacity: 1;
}
.category-name {
  flex: 1;
}
.article-list {
  background: #fff;
  border-radius: 8px;
  /* box-shadow: 0 2px 12px rgba(0,0,0,0.05); */
  padding: 24px;
}
.article-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 14px;
  color: #333;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  border-radius: 4px;
}
.article-item:last-child {
  border-bottom: none;
}
.article-item:hover {
  background: #f8f9fa;
  color: #2457a7;
  box-shadow: 0 2px 8px rgba(36, 87, 167, 0.08);
}
.article-title {
  display: flex;
  align-items: center;
  flex: 1;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  font-size: 14px;
}
.article-dot {
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #2457a7;
  margin-right: 8px;
  flex-shrink: 0;
}
.date {
  color: #999;
  font-size: 14px;
  margin-left: 18px;
  flex-shrink: 0;
}
</style>
