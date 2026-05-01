<template>
  <div class="public-index">
    <!-- 面包屑导航 -->
    <div class="breadcrumb">
      <span @click="goHome" style="cursor: pointer">首页</span> &gt;
      <span @click="goColumnName">{{ columnName }}</span>
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
      ref="ifrname"
      v-if="previewUrl"
      :src="previewUrl"
      width="100%"
      height="800px"
      frameborder="0"
      style="border: none"
    ></iframe>
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
  name: "cgInfo",
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
      previewUrl: null,
    };
  },
  created() {
    this.fetchArticleDetail();
    this.getColumnName();
  },
  mounted() {},
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
      const res = await getArticleDetail(this.id, 0);
      this.article = res.data;
      console.log(this.article, "asdasd");

      if (this.article.ggFile) {
        var b64Encoded = Base64.encode(
          process.env.VUE_APP_IMAGE_BASE_API + this.article.ggFile
        );
        this.previewUrl =
          process.env.VUE_APP_KKFILE2 + encodeURIComponent(b64Encoded);
        console.log(this.previewUrl, "预览url");
        if (this.previewUrl) {
          // 等待DOM更新完成后执行滚动
          this.$nextTick(() => {
            // 获取iframe元素并滚动到顶部
            const iframe = this.$refs.ifrname;
            console.log(iframe);

            if (iframe) {
              iframe.scrollIntoView({ behavior: "smooth" });
            }
          });
        }
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
      this.$emit("goColumnName", this.columnName);
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

.breadcrumb span:first-child {
  color: #2457a7;
  cursor: pointer;
}

.breadcrumb span:first-child:hover {
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
</style>
