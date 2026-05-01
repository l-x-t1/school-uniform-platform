<template>
  <div class="public-index">
    <!-- 面包屑导航 -->
    <div class="breadcrumb">
      <span>当前位置：</span>
      <span @click="goHome">首页</span> &gt; <span>{{ columnName }}</span>
    </div>
    <div class="main-content" :class="{ 'has-category': hasSubCategories }">
      <!-- 左侧分类 -->
      <div v-if="hasSubCategories" class="category-list">
        <div
          v-for="item in categoryList"
          :key="item.id"
          :class="['category-item', { active: item.id === activeCategory }]"
          @click="handleCategoryChange(item)"
        >
          <span class="category-name">{{ item.name }}</span>
        </div>
      </div>
      <!-- 文章列表 -->
      <el-form
        v-if="bs == 'CGGG'"
        :model="CGGGQueryParamas"
        ref="queryForm"
        size="small"
        :inline="true"
        label-width="68px"
      >
        <el-form-item label="地区" prop="area">
          <el-select v-model="CGGGQueryParamas.area" placeholder="请选择地区">
            <el-option
              v-for="item of areaList"
              :key="item.deptId"
              :value="item.deptId"
              :label="item.deptName"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="采购人" prop="deptName">
          <el-input
            v-model="CGGGQueryParamas.deptName"
            placeholder="请输入采购人"
            clearable
            style="width: 240px"
          />
        </el-form-item>

        <el-form-item label="标题" prop="title">
          <el-input
            v-model="CGGGQueryParamas.title"
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

      <div
        class="article-list"
        :style="hasSubCategories ? 'width: calc(100% - 180px);' : ''"
      >
        <div v-if="bs != 'CGGG'">
          <div
            v-for="item in articleList"
            :key="item.id"
            class="article-item"
            @click="goDetail(item)"
          >
            <span class="article-title">
              <span class="article-dot" />
              {{ item.title }}
            </span>
            <span class="date"> {{ item.createTime.split(" ")[0] }}</span>
          </div>
        </div>

        <div v-else>
          <div
            v-for="item in articleList"
            :key="item.id"
            class="article-item-cggg"
            @click="goDetail(item)"
          >
            <div class="article-title-cggg">
              <span style="font-weight: 600" class="article-icon-cggg" />
              {{ item.title }}
            </div>
            <div class="article-meta">
              <span class="creator"
                >发布时间：<span class="meta-value">{{
                  item.createTime.split(" ")[0]
                }}</span></span
              >
              <span class="creator" v-if="item.areaname"
                >地区：<span class="meta-value">{{ item.areaname }}</span></span
              >
              <span class="creator" v-if="item.createBy"
                >采购人：<span class="meta-value">{{
                  item.createBy
                }}</span></span
              >
            </div>
          </div>
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
import {
  getArticleList,
  getNoticeColumnList,
  getDeptList,
} from "@/api/publicIndex";
export default {
  name: "cgList",
  props: {
    bs: {
      type: String,
      default: null,
    },
  },
  data() {
    return {
      queryParams: {
        bs: this.bs,
        pageNum: 1,
        pageSize: 20,
      },
      CGGGQueryParamas: {
        area: null,
        deptName: null,
        title: null,
      },
      total: 0,
      areaList: [],
      articleList: [],
      columnName: "",
      categoryList: [],
      activeCategory: null,
      hasSubCategories: false,
    };
  },
  watch: {
    bs: {
      handler(val) {
        this.queryParams.bs = val;
        this.getNoticeColumnList();
      },
      immediate: true,
    },
  },
  created() {
    getDeptList().then((res) => {
      this.areaList = res.data;
    });
    this.getNoticeColumnList();
  },
  methods: {
    handleQuery() {
      this.CGGGQueryParamas.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.CGGGQueryParamas.deptId = undefined;
      this.handleQuery();
    },
    async getNoticeColumnList() {
      const res = await getNoticeColumnList(this.queryParams);
      res.data.forEach((item) => {
        if (item.bs === this.bs) {
          this.columnName = item.name;
          if (item.children && item.children.length > 0) {
            this.hasSubCategories = true;
            this.categoryList = item.children;
            this.activeCategory = this.categoryList[0].id;
            this.queryParams.bs = this.categoryList[0].bs;
          } else {
            this.hasSubCategories = false;
            this.queryParams.bs = this.bs;
          }
          this.getList();
        }
      });
    },
    async getList() {
      const res = await getArticleList({
        ...this.queryParams,
        ...this.CGGGQueryParamas,
        type: 0,
      });
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
    goDetail(item) {
      window.open(this.$route.path + "?id=" + item.id + "&bs=" + this.bs);
    },
  },
};
</script>

<style scoped>
.public-index {
  padding: 24px;
  background: #f8f9fa;
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
}

.main-content.has-category {
  gap: 32px;
}

.category-list {
  width: 180px;
  background: #fafbfc;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.03);
  padding: 16px 0;
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

.category-name {
  flex: 1;
}

.article-list {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  flex: 1;
}

.article-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 14px;
  color: #333;
  transition: all 0.3s ease;
  cursor: pointer;
}

.article-item:last-child {
  border-bottom: none;
}

.article-item:hover {
  background: #f8f9fa;
  transform: translateX(4px);
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

.article-item-cggg {
  padding: 10px 5px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.article-item-cggg:last-child {
  border-bottom: none;
}

.article-item-cggg:hover {
  background: #f8f9fa;
}

.article-title-cggg {
  display: flex;
  align-items: center;
  font-size: 16px;
  color: #333;
  margin-bottom: 12px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.article-meta {
  display: flex;
  align-items: center;
  font-size: 13px;
  gap: 24px;
  background: #eef5ff;
  padding: 12px 16px;
  border-radius: 4px;
}

.creator {
  color: black;
}

.meta-value {
  color: #2457a7;
  margin-left: 4px;
  font-weight: bold;
}

.article-icon-cggg {
  margin-left: 4px;
  font-weight: bold;
  display: inline-block;
  width: 8px;
  height: 8px;
  background-color: #2457a7;
  border-radius: 50%;
  margin-right: 10px;
  flex-shrink: 0;
}
</style>
