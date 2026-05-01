<template>
  <view class="container">
    <uni-forms
      ref="form"
      label-position="top"
      :modelValue="formData"
      :rules="rules"
    >
      <uni-forms-item required label="学校" name="shcoolId">
        <!-- 远程加载数据 -->
          <lin-select
           :list="schoolList"
           :loading="loading"
           :type="searchType"
           loading-text="数据加载中"
           value-key="value"
           name-key="name"
           max-height="180"
           placeholder="输入学校名称可查询"
           @input="handleSchoolChange"
           @change="handleSchoolSelect"
           v-model="formData.shcoolId"
         />
      </uni-forms-item>
      <uni-forms-item required label="入学年份" name="grade">
        <uni-data-select
          v-model="formData.grade"
          :localdata="schoolYearList" 
          placeholder="请选择入学年份"
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
    <button type="primary" @click="submit">保 存</button>
  </view>
</template>

<script>
export default {
  data() {
    return {
      loading: false,
      schoolList: [],
      schoolYearList: [],
      classList: [],
      baseUrl: getApp().globalData.baseUrl,
      id: "",
      deptId: null,
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
      searchType:'search',
      sexRange: [
        { value: "0", text: "男" },
        { value: "1", text: "女" },
      ],
      // 防抖定时器
      schoolChangeTimer: null,
      // 当前学生的学校信息（用于编辑模式）
      currentStudentSchool: null,
      // 是否已经初始化完成，防止页面加载时自动搜索
      isInitialized: false,
      rules: {
        grade: {
          rules: [
            {
              required: true,
              errorMessage: "入学年份不能为空",
            },
          ],
        },
        classId: {
          rules: [
            {
              required: true,
              errorMessage: "班级不能为空",
            },
          ],
        },
        shcoolId: {
          rules: [
            {
              required: true,
              errorMessage: "学校不能为空",
            },
          ],
        },
        name: {
          rules: [
            {
              required: true,
              errorMessage: "学生姓名不能为空",
            },
          ],
        },
        sex: {
          rules: [
            {
              required: true,
              errorMessage: "性别不能为空",
            },
          ],
        },
        birthday: {
          rules: [
            {
              required: true,
              errorMessage: "出生年月不能为空",
            },
          ],
        },
      },
    };
  },
  onLoad(options) {
    // 初始化学年列表
    this.initSchoolYearList();

    if (options.id) {
      this.type = "edit";
      this.searchType = "edit";
      if (options.type === "edit") {
        this.getStudentInfo(options.id);
        // 编辑模式下，在 getStudentInfo 中设置 isInitialized
        return;
      }
    }
    
    // 新增模式下，设置初始化完成标志
    this.$nextTick(() => {
      this.isInitialized = true;
    });
  },
  onReady() {
    // 在页面渲染完成后，如果是编辑模式且已有学生信息，确保学校正确回显
    if (this.type === "edit" && this.currentStudentSchool && this.formData.shcoolId) {
      this.$nextTick(() => {
        // 再次确保学校列表包含当前学生的学校
        if (this.schoolList.length === 0 || !this.schoolList.find(school => school.value === this.currentStudentSchool.value)) {
          this.schoolList = [this.currentStudentSchool]; 
        }
        // 确保学校ID正确设置
        this.formData.shcoolId = this.currentStudentSchool.value;
      });
    }
  },
  onUnload() {
    // 清理定时器，避免内存泄漏
    if (this.schoolChangeTimer) {
      clearTimeout(this.schoolChangeTimer);
    }
  },
  methods: {
    // 获取默认出生日期（6年前的今天）
    getDefaultBirthday() {
      const date = new Date();
      date.setFullYear(date.getFullYear() - 6);
      return date.toISOString().split("T")[0];
    },
    // 获取开始日期（100年前）
    getStartDate() {
      const date = new Date();
      date.setFullYear(date.getFullYear() - 100);
      return date.toISOString().split("T")[0];
    },
    // 获取结束日期（今天）
    getEndDate() {
      return new Date().toISOString().split("T")[0];
    },
    // 初始化学年列表（生成近10年的学年选项）
    initSchoolYearList() {
      const currentYear = new Date().getFullYear();
      this.schoolYearList = [];

      // 生成前后5年的学年选项
      for (let i = currentYear - 5; i <= currentYear + 5; i++) {
        this.schoolYearList.push({
          value: i.toString(),
          text: `${i}年`,
        });
      }
    }, 
    //获取学生信息
    getStudentInfo(id) {
      this.loading = true;
      uni.request({
        url: `${this.baseUrl}/school/schoolStudentInfo/` + id,
        method: "GET",
        header: {
          Authorization: uni.getStorageSync("token"),
        },
        success: ({ data }) => {
          if (data.code == 200) {
            // 保存当前学生的学校信息
            this.currentStudentSchool = {
              name: data.data.sysDepts.deptName,
              value: Number(data.data.sysDepts.deptId),
            };
            
            // 先设置其他字段
            this.formData.name = data.data.name;
            this.formData.sex = data.data.sex;
            this.formData.birthday = data.data.birthday;
            this.formData.grade = data.data.grade;
            this.formData.classId = data.data.classId;
            this.deptId = Number(data.data.sysDepts.deptId);  
            this.id = data.data.id;
            
            // 设置初始学校列表，包含当前学生的学校
            
            
            // 立即设置学校ID
            this.formData.shcoolId = this.currentStudentSchool.value;
            
            // 确保在数据设置完成后才标记为初始化完成
            this.$nextTick(() => {
              this.isInitialized = true;
              this.schoolList = [this.currentStudentSchool];
              setTimeout(() => {
                this.searchType = "search";
              }, 500); 
            });
            
            this.loading = false;
          } else {
            uni.showToast({
              title: data.msg,
              icon: "none",
            });
            this.loading = false;
          }
        },
        fail: () => {
          this.loading = false;
          uni.showToast({
            title: "获取学生信息失败",
            icon: "none",
          });
        }
      });
    }, 
    handleSchoolChange(e) { 
      // 如果还没有初始化完成，不执行搜索
      if (!this.isInitialized) {
        return;
      }
      
      // 防抖处理：清除之前的定时器
      if (this.schoolChangeTimer) {
        clearTimeout(this.schoolChangeTimer);
      }
      
      // 设置新的定时器，延迟300ms执行
      this.schoolChangeTimer = setTimeout(() => {
        // 如果输入为空
        if (!e || e.trim() === '') {
          // 在编辑模式下，如果输入为空，显示当前学生的学校
          if (this.type === "edit" && this.currentStudentSchool) {
            this.schoolList = [this.currentStudentSchool];
            this.deptId = this.currentStudentSchool.value;
            // 保持当前选中的学校
            this.formData.shcoolId = this.currentStudentSchool.value;
          } else {
            this.schoolList = [];
            this.deptId = null;
          }
          return;
        } 
        
        // 在编辑模式下，如果用户开始输入，保持当前选中的学校直到用户做出选择
        if (this.type === "edit" && this.currentStudentSchool && !this.formData.shcoolId) {
          this.formData.shcoolId = this.currentStudentSchool.value;
        }
        
        // 用户开始搜索时，清空当前选中的学校ID，让用户重新选择
        if (this.type === "edit" && e.trim() !== '') {
          this.formData.shcoolId = null;
        }
        
        // 只进行搜索，不设置deptId，让用户手动选择
        this.getSchoolList(e); 
      }, 300);
    },
     
     // 处理学校选择
     handleSchoolSelect(value) {
      console.log(value);
       // 当用户选择了学校时，设置deptId
       if (value) {
         const selectedSchool = this.schoolList.find(school => school.value === Number(value));
         if (selectedSchool) {
           this.deptId = selectedSchool.value;
         }
       } else {
         this.deptId = null;
       }
     },
     
     //获取学校列表
    getSchoolList(e) {
      this.loading = true;
      uni.request({
        url: `${this.baseUrl}/system/open/link/getSchoolByName`,
        method: "GET",
        data: {
          name: e,
        },
        success: ({ data }) => {
          if (data.code == 200) {
            let searchResults = data.data.map((item) => ({
              name: item.deptName,
              value: Number(item.deptId),
            }));
            
            // 直接使用搜索结果，不再强制将当前学生的学校添加到列表开头
            // 让用户看到真实的搜索结果
            this.schoolList = searchResults;
            this.loading = false;
          } else {
            uni.showToast({
              title: data.msg,
              icon: "none",
            });
            this.loading = false;
          }
        },
        fail: () => {
          this.loading = false;
          uni.showToast({
            title: "网络请求失败",
            icon: "none",
          });
        }
      });
    },
    submit() {
      console.log(this.formData);
      this.$refs.form
        .validate()
        .then((res) => {
          if (this.type == "edit") {
            uni.request({
              url: `${this.baseUrl}/school/schoolStudentInfo`,
              method: "PUT",
              data: {
                ...res,
                simId: uni.getStorageSync("simId"),
                id: this.id,
              },
              header: {
                Authorization: uni.getStorageSync("token"),
                "Content-Type": "application/x-www-form-urlencoded",
              },
              success: (res) => {
                uni.showToast({
                  title: "修改成功",
                  icon: "success",
                });
                uni.navigateBack();
              },
            });
          } else {
            uni.request({
              url: `${this.baseUrl}/school/schoolStudentInfo`,
              method: "POST",
              data: {
                ...res,
                simId: uni.getStorageSync("simId"),
              },
              header: {
                Authorization: uni.getStorageSync("token"),
                "Content-Type": "application/x-www-form-urlencoded",
              },
              success: (res) => {
                uni.showToast({
                  title: "保存成功",
                  icon: "success",
                });
                uni.navigateBack();
              },
            });
          }
        })
        .catch((err) => {
          console.log("表单错误信息：", err);
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
