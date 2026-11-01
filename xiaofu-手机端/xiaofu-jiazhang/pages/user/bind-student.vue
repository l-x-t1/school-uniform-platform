<template>
  <view class="container">
    <uni-forms
      ref="form"
      label-position="top"
      :modelValue="formData"
      :rules="rules"
    >
      <uni-forms-item required label="学校" name="shcoolId">
        <lin-select
          :list="schoolList"
          :loading="loadingSchools"
          :value="formData.shcoolId"
          value-key="value"
          name-key="name"
          max-height="220"
          placeholder="输入学校名称可查询"
          loading-text="学校加载中"
          @focus="openSchoolList"
          @input="handleSchoolKeywordChange"
          @change="handleSchoolSelect"
        />
      </uni-forms-item>
      <uni-forms-item required label="年级" name="grade">
        <uni-data-select
          v-model="formData.grade"
          :localdata="gradeOptions"
          placeholder="请选择年级"
        ></uni-data-select>
      </uni-forms-item>
      <uni-forms-item required label="班级" name="classId">
        <uni-easyinput type="number" v-model="formData.classId" placeholder="请输入班级" />
      </uni-forms-item>
      <uni-forms-item required label="学生姓名" name="name">
        <uni-easyinput
          type="text"
          v-model="formData.name"
          placeholder="请输入学生姓名"
        />
      </uni-forms-item>
      <uni-forms-item required label="性别" name="sex">
        <uni-data-select
          v-model="formData.sex"
          :localdata="sexRange"
          placeholder="请选择性别"
        ></uni-data-select>
      </uni-forms-item>
      <uni-forms-item required label="出生年月" name="birthday">
        <uni-datetime-picker
          type="date"
          :clear-icon="false"
          v-model="formData.birthday"
          placeholder="请选择出生年月"
          :start="startDate"
          :end="endDate"
        />
      </uni-forms-item>
    </uni-forms>
    <button type="primary" @click="submit">保存</button>
  </view>
</template>

<script>
import linSelect from '../../uni_modules/lin-select/components/lin-select/lin-select.vue'
import { listSchoolOptions } from '@/api/student'

export default {
  components: { linSelect },
  data() {
    return {
      loading: false,
      loadingSchools: false,
      allSchoolList: [],
      schoolList: [],
      gradeOptions: [],
      baseUrl: getApp().globalData.baseUrl,
      id: "",
      startDate: this.getStartDate(),
      endDate: this.getEndDate(),
      formData: {
        shcoolId: "",
        grade: "",
        classId: "",
        name: "",
        sex: null,
        birthday: null,
      },
      type: "",
      schoolKeyword: "",
      currentStudentSchool: null,
      sexRange: [
        { value: "0", text: "男" },
        { value: "1", text: "女" },
      ],
      rules: {
        grade: {
          rules: [{ required: true, errorMessage: "年级不能为空" }],
        },
        classId: {
          rules: [{ required: true, errorMessage: "班级不能为空" }],
        },
        shcoolId: {
          rules: [{ required: true, errorMessage: "学校不能为空" }],
        },
        name: {
          rules: [{ required: true, errorMessage: "学生姓名不能为空" }],
        },
        sex: {
          rules: [{ required: true, errorMessage: "性别不能为空" }],
        },
        birthday: {
          rules: [{ required: true, errorMessage: "出生年月不能为空" }],
        },
      },
    };
  },
  onLoad(options) {
    this.initGradeOptions();
    this.loadAllSchools();
    if (options.id) {
      this.type = "edit";
      this.getStudentInfo(options.id);
    }
  },
  methods: {
    getStartDate() {
      const date = new Date();
      date.setFullYear(date.getFullYear() - 100);
      return date.toISOString().split("T")[0];
    },
    getEndDate() {
      return new Date().toISOString().split("T")[0];
    },
    initGradeOptions() {
      this.gradeOptions = [
        { value: "一年级", text: "一年级" },
        { value: "二年级", text: "二年级" },
        { value: "三年级", text: "三年级" },
        { value: "四年级", text: "四年级" },
        { value: "五年级", text: "五年级" },
        { value: "六年级", text: "六年级" },
        { value: "初一", text: "初一" },
        { value: "初二", text: "初二" },
        { value: "初三", text: "初三" },
        { value: "高一", text: "高一" },
        { value: "高二", text: "高二" },
        { value: "高三", text: "高三" },
      ];
    },
    normalizeSchoolList(list = []) {
      return list
        .filter((item) => item && item.name && item.value)
        .map((item) => ({
          name: item.name,
          value: Number(item.value),
        }));
    },
    ensureCurrentSchoolVisible() {
      if (!this.currentStudentSchool) return;
      if (!this.schoolList.find((item) => item.value === this.currentStudentSchool.value)) {
        this.schoolList = [this.currentStudentSchool, ...this.schoolList];
      }
      if (!this.allSchoolList.find((item) => item.value === this.currentStudentSchool.value)) {
        this.allSchoolList = [this.currentStudentSchool, ...this.allSchoolList];
      }
    },
    loadAllSchools() {
      this.loadingSchools = true;
      listSchoolOptions("")
        .then((list) => {
          this.allSchoolList = this.normalizeSchoolList(list);
          this.schoolList = [...this.allSchoolList];
          this.ensureCurrentSchoolVisible();
        })
        .catch(() => {
          uni.showToast({
            title: "学校列表加载失败",
            icon: "none",
          });
        })
        .finally(() => {
          this.loadingSchools = false;
        });
    },
    filterSchoolList(keyword = "") {
      const trimmed = (keyword || "").trim();
      if (!trimmed) {
        this.schoolList = [...this.allSchoolList];
        this.ensureCurrentSchoolVisible();
        return;
      }
      this.schoolList = this.allSchoolList.filter((item) =>
        item.name.includes(trimmed)
      );
      this.ensureCurrentSchoolVisible();
    },
    openSchoolList() {
      if (!this.allSchoolList.length && !this.loadingSchools) {
        this.loadAllSchools();
        return;
      }
      this.filterSchoolList(this.schoolKeyword);
    },
    getStudentInfo(id) {
      this.loading = true;
      uni.request({
        url: `${this.baseUrl}/school/schoolStudentInfo/${id}`,
        method: "GET",
        header: {
          Authorization: uni.getStorageSync("token"),
        },
        success: ({ data }) => {
          if (data.code === 200) {
            const dept = data.data.sysDepts || {};
            this.currentStudentSchool = dept.deptId
              ? {
                  name: dept.deptName,
                  value: Number(dept.deptId),
                }
              : null;
            this.formData = {
              shcoolId: dept.deptId ? Number(dept.deptId) : "",
              grade: data.data.grade || "",
              classId: data.data.classId || "",
              name: data.data.name || "",
              sex: data.data.sex || "0",
              birthday: data.data.birthday || "",
            };
            this.id = data.data.id;
            this.ensureCurrentSchoolVisible();
          } else {
            uni.showToast({
              title: data.msg,
              icon: "none",
            });
          }
        },
        fail: () => {
          uni.showToast({
            title: "获取学生信息失败",
            icon: "none",
          });
        },
        complete: () => {
          this.loading = false;
        },
      });
    },
    handleSchoolKeywordChange(keyword) {
      this.schoolKeyword = typeof keyword === "string" ? keyword : "";
      if (!this.schoolKeyword.trim()) {
        this.formData.shcoolId = "";
      }
      this.filterSchoolList(this.schoolKeyword);
    },
    handleSchoolSelect(value) {
      const selectedSchool = this.schoolList.find((item) => item.value === Number(value));
      if (!selectedSchool) {
        this.formData.shcoolId = "";
        return;
      }
      this.formData.shcoolId = selectedSchool.value;
      this.currentStudentSchool = selectedSchool;
      this.schoolKeyword = selectedSchool.name;
      this.ensureCurrentSchoolVisible();
    },
    validateSchoolSelection() {
      return this.schoolList.some((item) => item.value === Number(this.formData.shcoolId)) ||
        this.allSchoolList.some((item) => item.value === Number(this.formData.shcoolId));
    },
    buildFormBody(payload) {
      return Object.keys(payload)
        .filter((key) => payload[key] !== undefined && payload[key] !== null && payload[key] !== "")
        .map((key) => `${encodeURIComponent(key)}=${encodeURIComponent(payload[key])}`)
        .join("&");
    },
    submit() {
      this.$refs.form
        .validate()
        .then((res) => {
          if (!this.validateSchoolSelection()) {
            uni.showToast({
              title: "请选择学校列表中的有效学校",
              icon: "none",
            });
            return Promise.reject(new Error("invalid_school"));
          }
          const payload = {
            ...res,
            shcoolId: this.formData.shcoolId,
            simId: uni.getStorageSync("simId"),
            id: this.id,
          };
          if (this.type === "edit") {
            return this.submitEdit(payload);
          }
          return this.submitCreate(payload);
        })
        .catch((err) => {
          if (err && err.message === "invalid_school") {
            return;
          }
          console.log("表单校验失败", err);
        });
    },
    submitCreate(payload) {
      uni.request({
        url: `${this.baseUrl}/school/schoolStudentInfo`,
        method: "POST",
        data: this.buildFormBody(payload),
        header: {
          Authorization: uni.getStorageSync("token"),
          "Content-Type": "application/x-www-form-urlencoded",
        },
        success: ({ data }) => {
          uni.showToast({
            title: data.code === 200 ? "保存成功" : data.msg || "保存失败",
            icon: data.code === 200 ? "success" : "none",
          });
          if (data.code === 200) {
            uni.navigateBack();
          }
        },
      });
    },
    submitEdit(payload) {
      uni.request({
        url: `${this.baseUrl}/school/schoolStudentInfo`,
        method: "PUT",
        data: payload,
        header: {
          Authorization: uni.getStorageSync("token"),
          "Content-Type": "application/json",
        },
        success: ({ data }) => {
          uni.showToast({
            title: data.code === 200 ? "修改成功" : data.msg || "修改失败",
            icon: data.code === 200 ? "success" : "none",
          });
          if (data.code === 200) {
            uni.navigateBack();
          }
        },
      });
    },
  },
};
</script>

<style lang="scss">
::v-deep .uni-forms-item__content {
  background: #fff;
}

.container {
  padding: 15px;
}

.uni-forms-item {
  margin-bottom: 10px;
}

button {
  margin-top: 15px;
}
</style>
