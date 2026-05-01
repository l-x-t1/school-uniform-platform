<template>
  <div class="public-index">
    <!-- tab栏 -->
    <div class="center-container">
      <div class="tab-bar">
        <span style="font-size: 12px; color: #999" class="erji-title">
          <!-- <span v-if="userInfo.roles.includes('xuexiao')">学校中心</span>
          <span @click="handleIndex" v-else>教育局中心</span> -->
        </span>
        <span
          v-if="!isLogin"
          style="
            font-size: 12px;
            font-weight: bold;
            color: #333;
            cursor: pointer;
          "
          @click="goLogin"
        >
          请登录
        </span>
        <span
          v-else
          style="
            font-size: 12px;
            font-weight: bold;
            color: #333;
            cursor: pointer;
          "
          @click="handleIndex"
        >
          欢迎您，{{ userInfo.nickName }}
        </span>
      </div>
    </div>
    <div class="center-container">
      <!-- 顶部导航条 -->
      <div class="top-nav">
        <div class="logo">石家庄市校服管理平台</div>
        <div class="nav-list">
          <span
            class="nav-item"
            :class="{ active: activeNav === null }"
            @click="handleIndexShouye"
            >首页
            <span v-if="activeNav === null" class="nav-triangle"></span>
          </span>
          <span
            class="nav-item"
            v-for="(item, idx) in tabList"
            :key="item.id || idx"
            :class="{ active: item.bs === activeNav }"
            @click="changeNav(item)"
          >
            {{ item.name }}
            <span v-if="item.bs === activeNav" class="nav-triangle"></span>
          </span>
          <span
            class="nav-item"
            :class="{ active: activeNav === 'XXXX' }"
            @click="activeNav = 'XXXX'"
            >学校信息
            <span v-if="activeNav === 'XXXX'" class="nav-triangle"></span>
          </span>
          <span
            class="nav-item"
            :class="{ active: activeNav === 'FEEDBACK' }"
            @click="activeNav = 'FEEDBACK'"
            >意见反馈
            <span v-if="activeNav === 'FEEDBACK'" class="nav-triangle"></span>
          </span>
        </div>
      </div>

      <div>
        <el-carousel v-if="!isView && activeNav == null" height="400px">
          <el-carousel-item v-for="item in lbimageList" :key="item.id">
            <img
              :src="baseUrl + item.image"
              alt="轮播图"
              style="width: 100%; height: 100%"
            />
          </el-carousel-item>
        </el-carousel>
      </div>
      <div style="margin-top: 40px">
        <pbIndex v-if="activeNav === null" @goMore="goMore" />
        <feedbackForm v-else-if="activeNav === 'FEEDBACK'" @goHome="goHome" />
        <schoolList
          v-else-if="activeNav === 'XXXX' && wzId === null"
          :bs.sync="activeNav"
          @goHome="goHome"
        />
        <ortherList
          v-else-if="
            wzId == undefined &&
            wzId === null &&
            activeNav !== 'XXXX' &&
            activeNav !== 'FEEDBACK'
          "
          :bs.sync="activeNav"
          @goHome="goHome"
        />
        <messageInfo
          v-else-if="
            wzId != undefined &&
            wzId !== null &&
            activeNav !== 'XXXX' &&
            activeNav !== 'CGGG'
          "
          :id="wzId"
          :bs="activeNav"
          @goHome="goHome"
          @goColumnName="goColumnName"
        />
        <schoolInfo
          v-else-if="wzId != undefined && wzId !== null && activeNav === 'XXXX'"
          :id="wzId"
          :bs="activeNav"
          @goHome="goHome"
          @goColumnName="goColumnName"
        />
        <cgInfo
          v-else-if="wzId != undefined && wzId !== null && activeNav === 'CGGG'"
          :id="wzId"
          :bs="activeNav"
          @goHome="goHome"
        />
      </div>
    </div>

    <!-- 版权信息 -->
    <div class="footer-copyright">
      <div class="center-container">
        <div class="copyright-content">
          <span>版权所有：石家庄市教育局</span>
          <span>备案信息：冀ICP备案000</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getNoticeColumnList, getLbimageList } from "@/api/publicIndex";
import pbIndex from "./publicIndex/index.vue";
import zcfgList from "./publicIndex/zcfgList.vue";
import ortherList from "./publicIndex/ortherList.vue";
import schoolList from "./publicIndex/schoolList.vue";
import messageInfo from "./publicIndex/messageInfo.vue";
import schoolInfo from "./publicIndex/schoolInfo.vue";
import cgInfo from "./publicIndex/cgInfo.vue";
import feedbackForm from "./publicIndex/feedbackForm.vue";
import { getToken } from "@/utils/auth";

export default {
  name: "publicIndex",
  components: {
    pbIndex,
    zcfgList,
    ortherList,
    schoolList,
    messageInfo,
    schoolInfo,
    cgInfo,
    feedbackForm,
  },
  data() {
    return {
      isLogin: false,
      userInfo: {
        roles: [],
      },
      isView: false,
      tabList: [], //栏目列表
      activeNav: null, //当前选中的栏目
      lbimageList: [], //轮播图列表
      baseUrl: process.env.VUE_APP_IMAGE_BASE_API,
      wzId: null, //文章id
    };
  },
  created() {
    console.log(this.baseUrl);
    this.initTabs();
    this.fetchLbimageList();
    //根据页面路径获取参数
    this.getParams();

    // 将页面滑动到底部
    this.isView = this.$router.currentRoute.query.bs;

    if (getToken()) {
      this.isLogin = true;
      this.$store.dispatch("GetInfo").then((res) => {
        this.$store.dispatch("GenerateRoutes").then((accessRoutes) => {
          this.$router.addRoutes(accessRoutes);
        });
        this.userInfo = this.$store.state.user;
        console.log(this.userInfo, "用户信息");
      });
    }
  },
  methods: {
    handleIndexShouye() {
      this.activeNav = null;
      this.isView = false;
    },
    handleIndex() {
      if (this.userInfo.roles.includes("xuexiao")) {
        this.$router.push({
          path: "/schoolIndex",
        });
      } else {
        this.$router.push({
          path: "/index",
        });
      }
    },
    //根据页面路径获取参数
    getParams() {
      const params = this.$route.query;
      if (params.id) {
        if (params.bs === "XXXX") {
          this.activeNav = "XXXX";
          this.wzId = params.id;
        } else {
          this.wzId = params.id;
          this.activeNav = params.bs;
        }
      }
    },
    async fetchLbimageList() {
      const res = await getLbimageList();
      this.lbimageList = res.data;
    },
    async initTabs() {
      // 获取栏目
      const res = await getNoticeColumnList();
      this.tabList = res.data
        .filter((item) => item.bs)
        .sort((a, b) => a.sort - b.sort);
    },
    // 切换栏目
    changeNav(item) {
      window.scrollTo({ top: 0, behavior: "smooth" });
      this.activeNav = item.bs;
      this.wzId = null;
      this.$router.replace({ path: "/publicIndex", query: null, params: null });
    },
    // 返回首页
    goHome() {
      this.activeNav = null;
      this.wzId = null;
      this.$router.replace({ path: "/publicIndex", query: null, params: null });
      this.isView = false;
    },
    // 跳转详情
    goDetail(id) {
      this.wzId = id;
    },
    goMore(bs) {
      window.scrollTo({ top: 0, behavior: "smooth" });
      this.activeNav = bs;
    },
    goLogin() {
      this.$router.push("/login");
    },
    goColumnName(bs) {
      this.$router.replace({ path: "/publicIndex", query: null, params: null });
      this.activeNav = bs;
      this.wzId = null;
    },
  },
};
</script>

<style scoped>
.erji-title {
  cursor: pointer;
}
.public-index {
  width: 100%;
  background: #fff;
}

.center-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

.top-nav {
  display: flex;
  align-items: center;
  background: #2457a7;
  color: #fff;
  padding: 0 30px;
  height: 60px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
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
  font-size: 18px;
  color: rgba(255, 255, 255, 0.85);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-bottom: 2px solid transparent;
}

.nav-item:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.08);
}

.nav-item.active {
  background: rgba(255, 255, 255, 0.12);
  color: #fff;
  font-weight: 500;
  border-bottom: 2px solid #fff;
}

.nav-item.active .nav-triangle {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  bottom: -8px;
  width: 0;
  height: 0;
  border-left: 8px solid transparent;
  border-right: 8px solid transparent;
  border-top: 8px solid #fff;
  content: "";
  z-index: 2;
  filter: drop-shadow(0 2px 2px rgba(0, 0, 0, 0.1));
}

.tab-bar {
  display: flex;
  gap: 20px;
  background: #f2f2f2;
  padding: 12px 30px;
  border-bottom: 1px solid #e0e0e0;
  justify-content: flex-end;
}

.tab-item {
  cursor: pointer;
  padding: 6px 18px;
  border-radius: 4px;
  color: #666;
  transition: all 0.3s ease;
}

.tab-item:hover {
  color: #2457a7;
  background: rgba(36, 87, 167, 0.1);
}

.tab-item.active {
  background: #2457a7;
  color: #fff;
  font-weight: bold;
}

.el-carousel {
  margin: 20px 0;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.el-carousel__item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.article-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: 1fr 1fr;
  gap: 32px;
  margin-top: 32px;
}
.article-section {
  border-radius: 8px;
  background: #f8f9fa;
  padding: 24px 24px 12px 24px;
  min-height: 340px; /* 可根据实际内容调整 */
  display: flex;
  flex-direction: column;
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 2px solid #e0e0e0;
  margin-bottom: 12px;
  padding-bottom: 4px;
}
.section-title {
  font-size: 22px;
  color: #b71c1c;
  font-weight: bold;
}
.section-more {
  color: #1976d2;
  cursor: pointer;
  font-size: 15px;
}
.section-content {
  margin-top: 10px;
  flex: 1;
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
  padding: 4px 10px;
  border-radius: 4px;
  color: #b71c1c;
  background: #fff;
  transition: background 0.2s;
}
.sub-tab.active {
  background: #e3eafc;
  color: #1976d2;
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
  margin-bottom: 8px;
}
.dot {
  width: 6px;
  height: 6px;
  background: #b71c1c;
  border-radius: 50%;
  margin-right: 8px;
}
.article-title {
  flex: 1;
  color: #222;
  font-size: 15px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.article-date {
  color: #888;
  font-size: 13px;
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
  padding: 24px 24px 12px 24px;
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
}
.school-logo {
  width: 80px;
  height: 80px;
  object-fit: contain;
  border-radius: 50%;
  background: #f5f5f5;
  border: 1px solid #eee;
  margin-bottom: 8px;
}
.school-name {
  font-size: 15px;
  color: #333;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 100%;
}
.school-logo {
  width: 80px;
  height: 80px;
  object-fit: contain;
  border-radius: 50%;
  background: #f5f5f5;
  border: 1px solid #eee;
  margin-bottom: 8px;
}
.school-name {
  font-size: 15px;
  color: #333;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 90%;
  box-sizing: border-box;
}

.footer-copyright {
  background: #2457a7;
  color: white;
  padding: 20px 0;
  margin-top: 40px;
}

.copyright-content {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 40px;
  font-size: 14px;
}

.copyright-content span {
  display: flex;
  align-items: center;
}

@media (max-width: 768px) {
  .copyright-content {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }
}
</style>
