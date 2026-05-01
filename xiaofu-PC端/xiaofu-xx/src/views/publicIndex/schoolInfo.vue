<template>
  <div class="public-index">
    <!-- 面包屑导航 -->
    <div class="breadcrumb">
      <span>当前位置：</span>
      <span @click="goHome" style="cursor: pointer">首页</span> &gt;
      <span @click="goColumnName" style="cursor: pointer">学校信息</span> &gt;
      <span>学校详情</span>
    </div>
    <div class="school-detail">
      <img
        :src="baseUrl + school.schoolBadge"
        alt="学校logo"
        class="school-logo"
        v-if="school.schoolBadge"
      />
      <img
        v-else
        src="@/assets/images/xuexiao.png"
        class="school-logo"
        alt=""
      />
      <div class="school-name">{{ school.schoolName }}</div>
      <div
        class="school-info"
        style="display: flex; flex-direction: column; align-items: center"
      >
        <div style="width: 30%; text-align: left">
          学校类别：{{
            school.schoolType == 0
              ? "高级中学"
              : school.schoolType == 1
              ? "初级中学"
              : school.schoolType == 2
              ? "小学"
              : school.schoolType == 3
              ? "幼儿园"
              : "其他"
          }}
        </div>
        <div style="width: 30%; text-align: left">
          学校区域：{{ school.schoolArea == 1 ? "乡镇学校" : "城市学校" }}
        </div>
        <div style="width: 30%; text-align: left">
          学校地区：{{
            getSchoolArea(
              school.regionProvince,
              school.regionCity,
              school.regionCounty
            )
          }}
        </div>
        <div style="width: 30%; text-align: left">
          详细地址：{{ school.detailedAddress }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getSchoolDetail } from "@/api/publicIndex";
export default {
  name: "schoolInfo",
  props: {
    id: {
      type: String,
      default: null,
    },
  },
  data() {
    return {
      school: {},
      baseUrl: "http://127.0.0.1:9300",
    };
  },
  created() {
    this.fetchSchoolDetail();
  },
  methods: {
    goHome() {
      this.$router.push("/");
    },
    goColumnName() {
      this.$emit("goColumnName", "XXXX");
    },
    async fetchSchoolDetail() {
      const res = await getSchoolDetail(this.id);
      this.school = res.data;
    },
    getSchoolArea(province, city, county) {
      let area = province == null ? "" : province;
      let area2 = city == null ? "" : city;
      let area3 = county == null ? "" : county;
      return area + area2 + area3;
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
.school-detail {
  text-align: center;
  margin-top: 40px;
}
.school-logo {
  width: 180px;
  height: 180px;
  object-fit: contain;
  margin-bottom: 20px;
}
.school-name {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 16px;
}
.school-info {
  font-size: 16px;
  color: #222;
  line-height: 2;
}
</style>
