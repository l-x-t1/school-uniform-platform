<script>
export default {
  globalData: {
    /** 班子会议列表首条 id，供意见征集等接口 teamMeetingId（流程页加载后写入） */
    statsTeamMeetingId: "1102",
    /** 与 `getProcessMenu` 等一致：班子会议列表首条主键 id（流程页 sync 后写入） */
    statsFormTeamId: "",
    baseUrl: "http://localhost:8080",
    webUrl: "http://localhost:8080/notice",
  },
  onLaunch: function () { 
  },
};
</script>

<style lang="scss">
/*每个页面公共css */
@import "@/uni_modules/uni-scss/index.scss";
/* #ifndef APP-NVUE */
@import "@/static/customicons.css";
// 设置整个项目的背景色
page {
  background-color: #f5f5f5;
}

/* #endif */
.example-info {
  font-size: 14px;
  color: #333;
  padding: 10px;
}
</style>
