<template>
  <div>
    <div class="public-index">
      <!-- 面包屑导航 -->
      <div class="breadcrumb">
        <span>当前位置：</span>
        <span @click="goHome" style="cursor: pointer">首页</span> &gt;
        <span style="cursor: pointer" @click="goColumnName">{{
          columnName
        }}</span>
        &gt;
        <span>正文</span>
      </div>
      <!-- 标题 -->
      <h2 class="article-title">{{ article.title }}</h2>
      <!-- 文章信息 -->
      <div class="article-info">
        <span>发布时间：{{ article.createTime }}</span>
        <span style="margin-left: 20px">阅读量：{{ readCount }}</span>
      </div>
      <!-- 富文本内容 -->
      <div class="article-content" v-html="article.content"></div>
      <iframe
        v-if="previewUrl"
        :src="previewUrl"
        width="100%"
        height="800px"
        frameborder="0"
        style="border: none"
      ></iframe>
    </div>
    <div class="footer">
      <div class="footer-content">
        <span>版权所有：石家庄市教育局</span>
        <span>备案信息：冀ICP备案000</span>
      </div>
    </div>
  </div>
</template>

<script>
import { Base64 } from "js-base64";

import {
  getNoticeColumnList,
  getArticleDetail,
  addReadCount,
} from "@/api/publicIndex";
export default {
  name: "messageInfo",
  props: {
    bs: {
      type: String,
      default: null,
    },
    id: {
      type: String,
      default: null,
    },
  },
  data() {
    return {
      columnName: "",
      article: {},
      readCount: 0,
      previewUrl: "",
    };
  },
  created() {
    this.fetchArticleDetail();
    this.getColumnName();
  },
  methods: {
    // 根据栏目id获取栏目名称
    async getColumnName() {
      const res = await getNoticeColumnList();
      res.data.forEach((item) => {
        if (item.bs === this.bs) {
          this.columnName = item.name;
        }
      });
    },
    // 返回首页
    goHome() {
      this.$emit("goHome");
    },
    // 获取文章详情
    async fetchArticleDetail() {
      const res = await getArticleDetail(this.id);
      this.article = res.data;

      if (this.article.ggFile && this.bs == "CGGG") {
        var b64Encoded = Base64.encode(
          process.env.VUE_APP_IMAGE_BASE_API + this.article.ggFile
        );
        this.previewUrl =
          process.env.VUE_APP_KKFILE2 + encodeURIComponent(b64Encoded);
        console.log(this.previewUrl, "预览url");
      }
      if (this.article.file) {
        var b64Encoded = Base64.encode(
          process.env.VUE_APP_IMAGE_BASE_API + this.article.file
        );
        this.previewUrl =
          process.env.VUE_APP_KKFILE2 + encodeURIComponent(b64Encoded);
      }

      this.addReadCount();
    },
    // 增加阅读量
    addReadCount() {
      addReadCount(this.id).then((res) => {
        this.readCount = this.article.readCount + 1;
      });
    },
    goColumnName() {
      this.$emit("goColumnName", this.bs);
    },
  },
};
</script>

<style scoped>
.public-index {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  background: #fff;
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
.breadcrumb span:nth-child(3) {
  color: #2457a7;
  cursor: pointer;
}
.breadcrumb span:nth-child(3):hover {
  text-decoration: underline;
}
.article-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 12px;
  text-align: center;
}
.article-info {
  color: #999;
  font-size: 13px;
  margin-bottom: 24px;
}
.article-content {
  font-size: 16px;
  line-height: 1.8;
  color: #222;
}

.footer {
  margin-top: 40px;
  padding: 15px 0;
  background: #2196f3;
  text-align: center;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.footer-content span {
  color: white;
  font-size: 14px;
}
</style>
