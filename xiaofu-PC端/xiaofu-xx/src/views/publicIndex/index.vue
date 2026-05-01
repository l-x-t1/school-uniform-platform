<template>
  <div class="public-index">
    <!-- 文章宫格区块 -->
    <div class="article-grid">
      <div v-for="(tab, idx) in wzList" :key="tab.id" class="article-section">
        <!-- 标题和更多 -->
        <div class="section-header">
          <span class="section-title">{{ tab.name }}</span>
          <span class="section-more" @click="goMore(tab.bs)">更多>></span>
        </div>
        <!-- 有子栏目 -->
        <div
          v-if="tab.children && tab.children.length"
          class="section-content with-sub"
        >
          <div class="sub-tabs">
            <span
              style="text-align: center"
              v-for="sub in tab.children"
              :key="sub.bs"
              :class="['sub-tab', { active: sub.bs === tab.activeSub }]"
              @click="changeSub(tab.bs, sub.bs)"
              >{{ sub.name }}</span
            >
          </div>
          <ul class="article-list">
            <li
              v-for="article in tab.list"
              :key="article.id"
              @click="goDetail(tab.bs, article.id)"
            >
              <span class="dot"></span>
              <span class="article-title">{{ article.title }}</span>
              <span class="article-date">{{
                parseTime(article.createTime, "{y}-{m}-{d}")
              }}</span>
            </li>
          </ul>
        </div>
        <!-- 无子栏目 -->
        <div v-else class="section-content">
          <ul class="article-list">
            <li
              v-for="article in tab.list"
              :key="article.id"
              @click="goDetail(tab.bs, article.id)"
            >
              <span class="dot"></span>
              <span class="article-title">{{ article.title }}</span>
              <span class="article-date">{{
                parseTime(article.createTime, "{y}-{m}-{d}")
              }}</span>
            </li>
          </ul>
        </div>
      </div>
    </div>
    <!-- 学校列表 -->
    <div class="school-section">
      <div class="section-header">
        <span class="section-title">学校信息</span>
        <span class="section-more" @click="goMore('XXXX')">更多&gt;</span>
      </div>
      <div class="school-list">
        <div
          class="school-item"
          v-for="school in schoolList"
          :key="school.deptId"
          @click="goSchoolDetail(school.deptId)"
        >
          <img
            v-if="school.schoolImage"
            class="school-logo"
            :src="baseUrl + school.schoolImage"
            :alt="school.deptName"
          />
          <img
            v-else
            class="school-logo"
            src="@/assets/images/xuexiao.png"
            alt=""
          />
          <div class="school-name" :title="school.deptName">
            {{ school.deptName }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {
  getNoticeColumnList,
  getArticleList,
  getSchoolList,
  getLbimageList,
} from "@/api/publicIndex";
export default {
  name: "pbIndex",
  data() {
    return {
      tabList: [], //栏目列表
      activeTab: null, //当前选中的栏目
      articleMap: {}, // { [栏目id或子栏目id]: [文章列表] }
      activeSub: {}, // { [栏目id]: 当前子栏目id }
      wzList: [], //文章列表
      activeNav: null, // 默认首页选中
      schoolList: [], //学校列表
      lbimageList: [], //轮播图列表
      baseUrl: "http://127.0.0.1:9300",
    };
  },
  created() {
    this.initTabs();
    this.fetchSchoolList();
    this.fetchLbimageList();
  },
  methods: {
    // 获取轮播图列表
    async fetchLbimageList() {
      const res = await getLbimageList();
      this.lbimageList = res.data;
    },
    // 获取栏目列表
    async initTabs() {
      // 获取栏目
      const res = await getNoticeColumnList();
      this.tabList = res.data.map((tab) => ({
        ...tab,
        bgColor: "#f8f9fa", // 可自定义每个栏目的浅色
      }));
      // 初始化子栏目选中
      this.tabList.forEach((tab, index) => {
        if (tab.bs !== "CGGG") {
          if (tab.children && tab.children.length > 0) {
            this.wzList.push({
              name: tab.name,
              list: [],
              children: tab.children,
              bs: tab.bs,
              activeSub: tab.children[0].bs,
            });
            this.fetchArticles(tab.bs, tab.children[0].bs);
          } else {
            this.wzList.push({
              name: tab.name,
              list: [],
              bs: tab.bs,
              activeSub: tab.bs,
            });
            this.fetchArticles(tab.bs);
          }
        }
      });
    },
    // 获取文章列表
    async fetchArticles(tabId, subId) {
      // 获取文章列表
      const params = subId
        ? { bs: subId, pageSize: 10, pageNum: 1 }
        : { bs: tabId, pageSize: 10, pageNum: 1 };
      console.log("asdsasdasds");

      const res = await getArticleList({ ...params, type: 0 });
      this.wzList.forEach((item) => {
        if (item.bs == tabId) {
          item.list = res.rows;
        } else {
          if (item.children) {
            item.children.forEach((child) => {
              if (child.bs == tabId) {
                item.list = res.rows;
              }
            });
          }
        }
      });
      console.log(this.wzList);
    },
    // 切换子栏目
    changeSub(tabId, subId) {
      this.wzList.forEach((item) => {
        if (item.bs == tabId) {
          item.activeSub = subId;
          this.fetchArticles(tabId, subId);
        }
      });
    },
    // 获取文章列表
    getArticles(tabId, subId) {
      return this.articleMap[subId || tabId] || [];
    },
    // 跳转更多
    goMore(tab) {
      console.log(tab);
      // 跳转更多
      this.$emit("goMore", tab);
    },
    // 获取学校列表
    async fetchSchoolList() {
      const res = await getSchoolList({ pageNum: 1, pageSize: 21 });
      this.schoolList = res.rows;
    },
    // 跳转详情
    goDetail(bs, id) {
      window.open(this.$route.path + "?bs=" + bs + "&id=" + id);
    },
    // 跳转学校详情
    goSchoolDetail(id) {
      window.open(this.$route.path + "?id=" + id + "&bs=XXXX");
    },
  },
};
</script>

<style scoped>
.public-index {
  width: 100%;
  background: #f8f9fa;
  min-height: 80vh;
}

.center-container {
  max-width: 1200px;
  margin: 0 auto;
  background: #fff;
}

.top-nav {
  display: flex;
  align-items: center;
  background: #2457a7;
  color: #fff;
  padding: 0 30px;
  height: 60px;
}
.logo {
  font-weight: bold;
  font-size: 22px;
  margin-right: 40px;
}
.nav-list {
  display: flex;
  gap: 0;
  background: #2457a7;
  height: 50px;
  align-items: flex-end;
  position: relative;
}
.nav-item {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 50px;
  padding: 0 28px;
  font-size: 20px;
  color: #fff;
  cursor: pointer;
  transition: background 0.2s;
}
.nav-item.active {
  background: #2d4e8a;
  color: #fff;
  font-weight: bold;
}
.nav-item.active .nav-triangle {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  bottom: -10px;
  width: 0;
  height: 0;
  border-left: 10px solid transparent;
  border-right: 10px solid transparent;
  border-top: 10px solid #2d4e8a;
  content: "";
  z-index: 2;
}
.tab-bar {
  display: flex;
  gap: 20px;
  background: #f5f5f5;
  padding: 10px 30px;
  border-bottom: 1px solid #e0e0e0;
}
.tab-item {
  cursor: pointer;
  padding: 6px 18px;
  border-radius: 4px 4px 0 0;
  background: #e0e0e0;
  color: #333;
}
.tab-item.active {
  background: #fff;
  color: #2457a7;
  border-bottom: 2px solid #2457a7;
  font-weight: bold;
}
.tab-content {
  padding: 20px 30px;
}
.el-carousel__item h3 {
  color: #475669;
  font-size: 14px;
  opacity: 0.75;
  line-height: 150px;
  margin: 0;
}
.article-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: 1fr 1fr;
  gap: 24px;
  margin-top: 24px;
  align-items: stretch;
}
.article-section {
  border-radius: 8px;
  background: #fff;
  padding: 24px;
  min-height: 320px;
  height: 100%;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}
.article-section:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 2px solid #e8e8e8;
  margin-bottom: 16px;
  padding-bottom: 8px;
}
.section-title {
  font-size: 20px;
  color: #2457a7;
  font-weight: bold;
}
.section-more {
  color: #666;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}
.section-more:hover {
  color: #2457a7;
}
.section-content {
  margin-top: 12px;
  flex: 1;
  overflow: hidden;
}
.with-sub {
  display: flex;
}
.sub-tabs {
  min-width: 120px;
  margin-right: 24px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.sub-tab {
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  color: #666;
  background: #f8f9fa;
  transition: all 0.3s ease;
  font-size: 14px;
}
.sub-tab:hover {
  background: #e8f0fe;
  color: #2457a7;
}
.sub-tab.active {
  background: #2457a7;
  color: #fff;
  font-weight: bold;
}
.article-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.article-list li {
  display: flex;
  align-items: center;
  /* margin-bottom: 12px; */
  padding: 8px;
  border-radius: 4px;
  transition: all 0.3s ease;
  font-size: 14px;
}
.article-list li:hover {
  background: #f8f9fa;
  cursor: pointer;
}
.dot {
  width: 6px;
  height: 6px;
  background: #2457a7;
  border-radius: 50%;
  margin-right: 8px;
  flex-shrink: 0;
}
.article-title {
  flex: 1;
  color: #333;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  white-space: normal;
  line-height: 1.5;
}
.article-date {
  color: #999;
  font-size: 14px;
  margin-left: 16px;
  white-space: nowrap;
}
.el-carousel__item:nth-child(2n) {
  background-color: #99a9bf;
}

.el-carousel__item:nth-child(2n + 1) {
  background-color: #d3dce6;
}

.school-section {
  margin-top: 40px;
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}
.school-list {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 24px 0;
  margin-top: 16px;
}
.school-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
  transition: all 0.3s ease;
}
.school-item:hover {
  transform: translateY(-2px);
}
.school-logo {
  width: 80px;
  height: 80px;
  object-fit: contain;
  border-radius: 50%;
  background: #fff;
  border: 1px solid #eee;
  margin-bottom: 8px;
  padding: 4px;
  transition: all 0.3s ease;
}
.school-item:hover .school-logo {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-color: #2457a7;
}
.school-name {
  font-size: 14px;
  color: #333;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 90%;
  box-sizing: border-box;
}
.breadcrumb {
  font-size: 13px;
  color: #888;
}
</style>
