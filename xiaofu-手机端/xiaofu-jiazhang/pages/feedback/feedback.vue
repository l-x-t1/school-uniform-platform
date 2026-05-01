<template>
  <view class="container">
    <!-- 使用uni-forms进行表单布局和校验 -->
    <uni-forms
      label-position="top"
      label-width="200rpx"
      ref="form"
      :modelValue="formData"
      :rules="rules"
    >
      <!-- 家长姓名 -->
      <uni-forms-item label="家长姓名" name="userName" required>
        <uni-easyinput
          v-model="formData.userName"
          placeholder="请输入家长姓名"
          :maxlength="10"
        />
      </uni-forms-item>

      <!-- 区县选择 -->
      <!-- <uni-forms-item label="所属区县" name="region" required>
        <uni-data-select
          v-model="formData.region"
          :localdata="shijiazhuangDistricts"
          placeholder="请选择区县"
          @change="onDistrictChange"
        />
      </uni-forms-item> -->

      <!-- 学校选择 -->
      <uni-forms-item label="所属学校" name="deptId" required>
        <uni-data-select
          v-model="formData.deptId"
          :localdata="schoolList"
          placeholder="请选择学校"
          @change="onSchoolChange"
        />
      </uni-forms-item>
      <!-- 联系方式 -->
      <uni-forms-item label="联系电话" name="lxfs" required>
        <uni-easyinput
          v-model="formData.lxfs"
          type="number"
          placeholder="请输入联系电话"
          :maxlength="11"
        />
      </uni-forms-item>
      <!-- 反馈内容 -->
      <uni-forms-item label="反馈内容" name="feedbackContent" required>
        <uni-easyinput
          type="textarea"
          v-model="formData.feedbackContent"
          placeholder="请描述您遇到的问题或建议，我们将尽快回复您"
          :maxlength="500"
          :autoHeight="true"
        />
        <view class="word-count">
          <text>{{ formData.feedbackContent.length }}/500</text>
        </view>
      </uni-forms-item>
    </uni-forms>

    <!-- 提交按钮 -->
    <view class="submit-section">
      <uni-load-more
        v-if="isSubmitting"
        status="loading"
        :content-text="loadingText"
      />
      <button
        v-else
        class="submit-btn"
        @click="submitFeedback"
        :disabled="isSubmitting"
      >
        提 交
      </button>
    </view>
  </view>
</template>

<script>
import { shijiazhuangDistricts } from "./dicts";
export default {
  data() {
    return {
      shijiazhuangDistricts: shijiazhuangDistricts.map((item) => ({
        value: item.name,
        text: item.name,
      })),
      baseUrl: getApp().globalData.baseUrl,
      isSubmitting: false,
      loadingText: {
        contentdown: "提交中...",
        contentrefresh: "提交中...",
        contentnomore: "提交完成",
      },
      formData: {
        userId: "", // 家长id（从缓存获取）
        userName: "", // 家长姓名
        districtId: "", // 区县id
        districtName: "", // 区县名称
        deptId: "", // 学校id
        deptName: "", // 学校名称
        feedbackContent: "", // 反馈内容
        feedbackUrl: [], // 附件列表
        lxfs: uni.getStorageSync('phone'), // 联系电话
        contactEmail: "", // 邮箱地址
      },
      // 学校列表数据
      schoolList: [],
      // 区县列表数据
      districtList: [],
      // 表单校验规则
      rules: {
        userName: {
          rules: [
            {
              required: true,
              errorMessage: "请输入家长姓名",
            },
            {
              minLength: 2,
              maxLength: 10,
              errorMessage: "姓名长度应为2-10个字符",
            },
          ],
        },
        districtId: {
          rules: [
            {
              required: true,
              errorMessage: "请选择区县",
            },
          ],
        },
        deptId: {
          rules: [
            {
              required: true,
              errorMessage: "请选择学校",
            },
          ],
        },
        feedbackContent: {
          rules: [
            {
              required: true,
              errorMessage: "请输入反馈内容",
            },
            {
              minLength: 10,
              maxLength: 500,
              errorMessage: "反馈内容应为10-500个字符",
            },
          ],
        },
        lxfs: {
          rules: [
            {
              required: true,
              errorMessage: "请输入联系电话",
            },
            {
              pattern: /^1\d{10}$/,
              errorMessage: "请输入正确的手机号码",
            },
          ],
        },
        region: {
          rules: [
            {
              required: true,
              errorMessage: "请选择区县",
            },
          ],
        },
        contactEmail: {
          rules: [
            {
              pattern: /^$|^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/,
              errorMessage: "请输入正确的邮箱地址",
            },
          ],
        },
      },
    };
  },
  onLoad() {
    this.initUserInfo();
    this.loadDistrictList();
    this.loadSchoolList();
  },
  methods: {
    // 初始化用户信息
    initUserInfo() {
      const userInfo = uni.getStorageSync("userInfo");
      if (userInfo) {
        this.formData.userId = userInfo.id || "";
        this.formData.userName = userInfo.name || "";
        this.formData.lxfs = userInfo.phone || "";
      }
    },

    // 加载区县列表
    loadDistrictList() {
      // 这里可以调用接口获取区县列表
      uni.request({
        url: `${this.baseUrl}/system/district/list`,
        method: "GET",
        header: {
          Authorization: uni.getStorageSync("token"),
        },
        success: ({ data }) => {
          if (data.code === 200) {
            this.districtList = data.rows.map((item) => ({
              value: item.districtId,
              text: item.districtName,
            }));
          }
        },
        fail: (err) => {
          console.error("加载区县列表失败：", err);
          // 如果接口不存在，可以使用模拟数据
          this.districtList = [
            { value: "1", text: "朝阳区" },
            { value: "2", text: "海淀区" },
            { value: "3", text: "丰台区" },
            { value: "4", text: "石景山区" },
            { value: "5", text: "门头沟区" },
          ];
        },
      });
    },

    // 加载学校列表
    loadSchoolList() {
      // 这里可以调用接口获取学校列表
      uni.request({
        url: `${this.baseUrl}/school/schoolStudentInfo/list`,
        method: "GET",
        header: {
          Authorization: uni.getStorageSync("token"),
        },
        data: {
          simId: uni.getStorageSync("simId"),
        },
        success: ({ data }) => {
          this.schoolList = data.rows.map((item) => ({
            value: item.sysDepts.deptId,
            text: item.sysDepts.deptName,
          }));
		      this.formData.deptId = this.schoolList[0].value
		      this.formData.deptName = this.schoolList[0].text
        },
      });
    },

    // 根据区县加载学校列表
    loadSchoolListByDistrict(districtId) {
      uni.request({
        url: `${this.baseUrl}/school/schoolStudentInfo/list`,
        method: "GET",
        header: {
          Authorization: uni.getStorageSync("token"),
        },
        data: {
          simId: uni.getStorageSync("simId"),
          districtId: districtId,
        },
        success: ({ data }) => {
          this.schoolList = data.rows.map((item) => ({
            value: item.sysDepts.deptId,
            text: item.sysDepts.deptName,
          }));
        },
        fail: (err) => {
          console.error("根据区县加载学校列表失败：", err);
          // 如果接口不支持按区县筛选，则加载所有学校
          this.loadSchoolList();
        },
      });
    },

    // 学校选择变化
    onSchoolChange(e) {
      const selectedSchool = this.schoolList.find((item) => item.value === e);
      if (selectedSchool) {
        this.formData.deptName = selectedSchool.text;
      }
    },

    // 区县选择变化
    onDistrictChange(e) {
      const selectedDistrict = this.districtList.find(
        (item) => item.value === e
      );
      if (selectedDistrict) {
        this.formData.districtName = selectedDistrict.text;
        // 清空学校选择
        this.formData.deptId = "";
        this.formData.deptName = "";
        // 根据区县重新加载学校列表
        this.loadSchoolListByDistrict(e);
      }
    },

    // 文件选择
    onFileSelect(e) {
      console.log("文件选择：", e);
    },

    // 文件删除
    onFileDelete(e) {
      console.log("文件删除：", e);
    },

    // 提交反馈
    async submitFeedback() {
      try {
        // 表单校验
        console.log(this.formData);
        const valid = await this.$refs.form.validate();
        if (!valid) {
          return;
        }

        this.isSubmitting = true;

        // 处理附件上传
        const uploadedUrls = [];
        if (this.formData.feedbackUrl.length > 0) {
          for (let file of this.formData.feedbackUrl) {
            if (file.url) {
              uploadedUrls.push(file.url);
            } else if (file.path) {
              // 上传文件到服务器
              const uploadResult = await this.uploadFile(file.path);
              if (uploadResult) {
                uploadedUrls.push(uploadResult);
              }
            }
          }
        }

        // 提交数据
        const submitData = {
          ...this.formData,
          feedbackUrl: uploadedUrls.join(","),
        };

        uni.request({
          url: `${this.baseUrl}/school/parentsFeedback`,
          method: "POST",
          data: {
            ...submitData,
            userId: uni.getStorageSync("simId"),
          },
          header: {
            Authorization: uni.getStorageSync("token"),
            "Content-Type": "application/x-www-form-urlencoded",
          },
          success: (res) => {
            if (res.data.code === 200) {
              uni.showToast({
                title: "反馈提交成功",
                icon: "success",
              });
              // 返回上一页
              setTimeout(() => {
                uni.navigateBack();
              }, 1500);
            } else {
              uni.showToast({
                title: res.data.message || "提交失败",
                icon: "none",
              });
            }
          },
          fail: (err) => {
            console.error("提交失败：", err);
            uni.showToast({
              title: "网络错误，请重试",
              icon: "none",
            });
          },
          complete: () => {
            this.isSubmitting = false;
          },
        });
      } catch (error) {
        console.error("表单校验失败：", error);
        this.isSubmitting = false;
      }
    },

    // 上传文件
    uploadFile(filePath) {
      return new Promise((resolve, reject) => {
        uni.uploadFile({
          url: `${this.baseUrl}/upload/image`,
          filePath: filePath,
          name: "file",
          success: (res) => {
            try {
              const data = JSON.parse(res.data);
              if (data.code === 200) {
                resolve(data.data.url);
              } else {
                reject(data.message);
              }
            } catch (e) {
              reject("上传失败");
            }
          },
          fail: reject,
        });
      });
    },
  },
};
</script>

<style lang="scss">
.container {
  padding: 20rpx;
  padding-bottom: 120rpx;
  background-color: #f5f5f5;
}

// 字数统计
.word-count {
  text-align: right;
  margin-top: 10rpx;

  text {
    font-size: 24rpx;
    color: #999;
  }
}

// 上传提示
.upload-tip {
  margin-top: 10rpx;

  text {
    font-size: 24rpx;
    color: #999;
  }
}

// 提交按钮区域
.submit-section {
  margin-top: 60rpx;
  padding: 0 30rpx;

  .submit-btn {
    width: 100%;
    height: 90rpx;
    line-height: 90rpx;
    background-color: #3b7aff;
    color: #fff;
    font-size: 32rpx;
    border-radius: 45rpx;
    border: none;

    &:disabled {
      background-color: #ccc;
    }

    &::after {
      border: none;
    }
  }
}

// 重写uni-forms的样式
::v-deep .uni-forms-item {
  margin-bottom: 30rpx;
  background-color: #fff;
  border-radius: 12rpx;
  padding: 20rpx;
}

::v-deep .uni-forms-item__label {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
}

::v-deep .uni-easyinput__content {
  background-color: #f9f9f9;
  border-radius: 8rpx;
  border: none;
}

::v-deep .uni-easyinput__content-input {
  font-size: 28rpx;
  color: #333;
}

::v-deep .uni-easyinput__content-textarea {
  font-size: 28rpx;
  color: #333;
  min-height: 200rpx;
}

::v-deep .uni-data-select {
  background-color: #f9f9f9;
  border-radius: 8rpx;
  border: none;
}

::v-deep .uni-file-picker__container {
  background-color: transparent;
}
</style>
