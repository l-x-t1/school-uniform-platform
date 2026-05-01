<template>
  <div class="public-index">
    <div class="breadcrumb">
      <span>当前位置：</span>
      <span @click="goHome">首页</span> &gt; <span>学校信息</span>
    </div>
    <div class="search-bar">
      <el-form
        :model="queryParams"
        ref="queryForm"
        size="small"
        :inline="true"
        label-width="68px"
      >
        <el-form-item label="区县" prop="area">
          <el-select v-model="queryParams.area" placeholder="请选择区县">
            <el-option
              v-for="item of areaList"
              :key="item.deptId"
              :value="item.deptId"
              :label="item.deptName"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="学校分类" prop="schoolType">
          <el-select
            v-model="queryParams.schoolType"
            placeholder="请选择学校分类"
          >
            <el-option
              v-for="item of dict.type.sc_sc_type"
              :key="item.value"
              :value="item.value"
              :label="item.label"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="学校名称" prop="name">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入学校名称"
            class="search-input"
            clearable
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="fetchSchoolList">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
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
          :src="baseUrl + school.schoolImage"
          alt="logo"
          class="school-logo"
        />
        <img
          v-else
          src="@/assets/images/xuexiao.png"
          alt="logo"
          class="school-logo"
        />
        <div class="school-name" :title="school.deptName">
          {{ school.deptName }}
        </div>
      </div>
    </div>
    <pagination
      v-show="total > queryParams.pageSize"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="fetchSchoolList()"
    />
  </div>
</template>

<script>
import { getSchoolList, getDeptList } from "@/api/publicIndex";
export default {
  dicts: ["sc_sc_type"],
  name: "pbIndex",
  data() {
    return {
      baseUrl: "http://127.0.0.1:9300",
      schoolList: [],
      total: 0,
      areaList: [],

      queryParams: {
        pageNum: 1,
        pageSize: 42, // 每页显示数量
        name: "", // 搜索用
      },
    };
  },
  created() {
    getDeptList().then((res) => {
      this.areaList = res.data;
    });
    this.fetchSchoolList();
  },
  methods: {
    resetQuery() {
      this.resetForm("queryForm");
      this.fetchSchoolList();
    },
    goHome() {
      this.$emit("goHome");
    },
    fetchSchoolList() {
      getSchoolList(this.queryParams).then((res) => {
        this.schoolList = res.rows;
        this.total = res.total;
      });
    },
    goSchoolDetail(id) {
      window.open(this.$route.path + "?id=" + id + "&bs=XXXX");
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
  font-size: 13px;
  color: #888;
}

.breadcrumb span:nth-child(2) {
  color: #2457a7;
  cursor: pointer;
}

.breadcrumb span:nth-child(2):hover {
  text-decoration: underline;
}

.search-bar {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  /* box-shadow: 0 2px 12px rgba(0,0,0,0.05); */
}

.search-input {
  width: 240px;
  margin-right: 16px;
}

.school-list {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 24px 0;
  margin-bottom: 24px;
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  /* box-shadow: 0 2px 12px rgba(0,0,0,0.05); */
}

.school-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  text-align: center;
  margin-bottom: 16px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.school-item:hover {
  transform: translateY(-4px);
}

.school-logo {
  width: 80px;
  height: 80px;
  object-fit: contain;
  border-radius: 50%;
  background: #fff;
  border: 1px solid #eee;
  padding: 4px;
  transition: all 0.3s ease;
}

.school-item:hover .school-logo {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-color: #2457a7;
}

.school-name {
  margin-top: 8px;
  font-size: 14px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 90%;
  box-sizing: border-box;
}

.pagination {
  text-align: center;
  margin-top: 24px;
  background: #fff;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}
</style>
