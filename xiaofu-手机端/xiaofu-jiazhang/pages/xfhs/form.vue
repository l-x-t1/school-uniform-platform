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
        <!-- 姓名 -->
        <uni-forms-item label="姓名" name="name" required>
          <uni-easyinput
            v-model="formData.name"
            placeholder="请输入姓名"
            :maxlength="10"
          />
        </uni-forms-item> 
  
        <!-- 学校选择 -->
        <uni-forms-item label="学校名称" name="schoolId" required>
          <uni-data-select
            v-model="formData.schoolId"
            :localdata="schoolList"
            placeholder="请选择学校"
            @change="onSchoolChange"
          />
        </uni-forms-item>
        
        <!-- 班级名称 -->
        <uni-forms-item label="班级名称" name="className" required>
          <uni-easyinput
            v-model="formData.className"
            placeholder="请输入班级名称"
            :maxlength="20"
          />
        </uni-forms-item>
        
        <!-- 校服图片 -->
        <uni-forms-item label="校服图片" name="images" required>
          <uni-file-picker
            v-model="imgList"
            file-mediatype="image"
            mode="grid"
            :limit="9"
            @select="onFileSelect"
            @delete="onFileDelete"
          />
          <view class="upload-tip">
            <text>请至少上传1张图片，最多9张，支持jpg、png格式</text>
            <text v-if="formData.images && formData.images.length > 0" class="upload-status">
              已选择{{ formData.images.length }}张图片
            </text>
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
  export default {
    data() {
      return { 
        baseUrl: getApp().globalData.baseUrl,
        isSubmitting: false,
        loadingText: {
          contentdown: "提交中...",
          contentrefresh: "提交中...",
          contentnomore: "提交完成",
        },
        imgList:[],
        formData: {
          name: "", // 姓名
          schoolId: "", // 学校id
          schoolName: "", // 学校名称
          className: "", // 班级名称
          images: [], // 校服图片
        },
        // 学校列表数据
        schoolList: [],
        // 表单校验规则
        rules: {
          name: {
            rules: [
              {
                required: true,
                errorMessage: "请输入姓名",
              },
              {
                minLength: 2,
                maxLength: 10,
                errorMessage: "姓名长度应为2-10个字符",
              },
            ],
          },
          schoolId: {
            rules: [
              {
                required: true,
                errorMessage: "请选择学校",
              },
            ],
          },
          className: {
            rules: [
              {
                required: true,
                errorMessage: "请输入班级名称",
              },
              {
                minLength: 2,
                maxLength: 20,
                errorMessage: "班级名称长度应为2-20个字符",
              },
            ],
          },
          images: {
            rules: [
              {
                required: true,
                errorMessage: "请至少上传1张校服图片",
              },
              {
                validator: (rule, value, data, callback) => {
                  // 检查formData.images是否有上传成功的URL
                  if (!this.formData.images || this.formData.images.length === 0) {
                    callback("请至少上传1张校服图片");
                    return;
                  }
                  
                  // 检查URL是否有效
                  const validUrls = this.formData.images.filter(url => url && typeof url === 'string' && url.trim() !== '');
                  if (validUrls.length === 0) {
                    callback("请至少上传1张校服图片");
                    return;
                  }
                  
                  callback();
                },
                errorMessage: "请至少上传1张校服图片",
              },
            ],
          },
        },
      };
    },
    onLoad() {
      this.loadSchoolList();
    },
    methods: {
      // 公共方法：检查图片是否有效
      isValidImage(item) {
        return item && (item.url || item.path || item.tempFilePath || item.uploaded);
      },
      
      // 公共方法：获取有效图片列表
      getValidImages(images) {
        return images.filter(item => this.isValidImage(item));
      },
      
      // 公共方法：显示错误提示
      showError(title, content) {
        uni.showModal({
          title,
          content,
          showCancel: false,
          confirmText: "确定"
        });
      },
      
      // 公共方法：文件验证
      validateFile(file, index) {
        // 检查文件大小（限制5MB）
        if (file.size > 5 * 1024 * 1024) {
          this.showError("文件过大", `第${index + 1}张图片大小超过5MB，请选择较小的图片`);
          return false;
        }
        
        // 检查文件格式
        const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png'];
        if (!allowedTypes.includes(file.file.type)) {
          this.showError("格式不支持", `第${index + 1}张图片格式不支持，请选择jpg或png格式的图片`);
          return false;
        }
        
        return true;
      },
      
      // 公共方法：清除图片验证错误
      clearImageValidation() {
        setTimeout(() => {
          this.$refs.form.clearValidate(['images']);
        }, 100);
      },
      
      // 公共方法：格式化图片数据，确保uni-file-picker能正确显示
      formatImageData(file, uploadUrl) {
        return {
          ...file,
          url: uploadUrl,
          uploaded: true,
          name: file.name || `image_${Date.now()}.jpg`,
          extname: file.extname || '.jpg',
          size: file.size,
          // 确保有path属性用于显示
          path: file.path || file.tempFilePath || uploadUrl
        };
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
            if (this.schoolList.length > 0) {
              this.formData.schoolId = this.schoolList[0].value;
              this.formData.schoolName = this.schoolList[0].text;
            }
          },
        });
      },
  
      // 学校选择变化
      onSchoolChange(e) {
        const selectedSchool = this.schoolList.find((item) => item.value === e);
        if (selectedSchool) {
          this.formData.schoolName = selectedSchool.text;
        }
      },
  
      // 文件选择
      async onFileSelect(e) {
        console.log("文件选择：", e);
        const files = e.tempFiles || [];
        
        // 检查文件数量
        if (files.length > 9) {
          uni.showToast({
            title: "最多只能选择9张图片",
            icon: "none"
          });
          return;
        }
        
        // 使用公共方法验证文件
        for (let i = 0; i < files.length; i++) {
          if (!this.validateFile(files[i], i)) {
            return;
          }
        }
        
        // 所有检查通过，开始自动上传
        if (files.length > 0) {
          await this.autoUploadImages(files);
        }
        
        // 清除验证错误
        this.clearImageValidation();
      },
  
      // 自动上传图片
      async autoUploadImages(files) {
        try {
          uni.showLoading({
            title: "正在上传图片...",
            mask: true
          });
          
          const uploadPromises = files.map(async (file, index) => {
            try {
              const uploadResult = await this.uploadFile(file.path);
              return {
                url: uploadResult,
                uploaded: true
              };
            } catch (error) {
              console.error(`第${index + 1}张图片上传失败：`, error);
              return {
                uploadError: error.message || error,
                uploaded: false
              };
            }
          });
          
          const uploadResults = await Promise.all(uploadPromises);
          
          // 检查上传结果
          const successCount = uploadResults.filter(result => result.uploaded).length;
          const failCount = uploadResults.filter(result => !result.uploaded).length;
          
          // 提取上传成功的URL
          const successUrls = uploadResults
            .filter(result => result.uploaded)
            .map(result => result.url);
          
          // 将成功的URL添加到formData.images
          this.formData.images = [...this.formData.images, ...successUrls];
          
          if (failCount > 0) {
            uni.hideLoading();
            uni.showModal({
              title: "部分图片上传失败",
              content: `成功上传${successCount}张，失败${failCount}张。是否继续？`,
              success: (res) => {
                if (res.confirm) {
                  // 用户确认，保持已添加的URL
                  uni.showToast({
                    title: `已上传${successCount}张图片`,
                    icon: "success"
                  });
                } else {
                  // 用户取消，移除刚才添加的URL
                  this.formData.images = this.formData.images.slice(0, -successCount);
                }
              }
            });
          } else {
            // 全部上传成功，URL已经添加到formData.images中
            uni.hideLoading();
            uni.showToast({
              title: `已上传${successCount}张图片`,
              icon: "success",
              duration: 1500
            });
          }
          
        } catch (error) {
          console.error("自动上传失败：", error);
          uni.hideLoading();
          this.showError("上传失败", "图片上传失败，请重试");
        }
      },

      // 文件删除
      onFileDelete(e) { 
        this.formData.images.splice(e.index, 1); 
        // 清除验证错误
        this.clearImageValidation();
      },
  
      // 提交校服回收申请
      async submitFeedback() {
        try {
          // 表单校验（包含图片验证）
          const valid = await this.$refs.form.validate();
          if (!valid) {
            return;
          }
  
          this.isSubmitting = true;
  
          // formData.images现在直接就是URL数组
          const uploadedUrls = this.formData.images;
  
          // 提交数据
          const submitData = {
            ...this.formData,
            images: uploadedUrls.join(","),
            simId: uni.getStorageSync("simId"),
          };
  
          uni.request({
            url: `${this.baseUrl}/school/ljhs`,
            method: "POST",
            data: submitData,
            header: {
              Authorization: uni.getStorageSync("token"),
              "Content-Type": "application/json",
            },
            success: (res) => {
              if (res.data.code === 200) {
                uni.showToast({
                  title: "提交成功",
                  icon: "success",
                  duration: 2000
                });
                // 返回上一页
                setTimeout(() => {
                  uni.navigateBack();
                }, 2000);
              } else {
                this.showError("提交失败", res.data.message || "提交失败，请重试");
              }
            },
            fail: (err) => {
              console.error("提交失败：", err);
              this.showError("网络错误", "网络连接失败，请检查网络后重试");
            },
            complete: () => {
              this.isSubmitting = false;
            },
          });
        } catch (error) {
          console.error("提交失败：", error);
          this.isSubmitting = false;
        }
      },
  
      // 上传文件
      uploadFile(filePath) {
        return new Promise((resolve, reject) => {
          uni.uploadFile({
            url: `${this.baseUrl}/file/upload`,
            filePath: filePath,
            name: "file",
            header: {
              Authorization: uni.getStorageSync("token"),
            },
            success: (res) => {
              try {
                const data = JSON.parse(res.data);
                if (data.code === 200) {
                  // 根据实际返回的数据结构调整，优先使用url字段
                  const imageUrl = data.data?.url || data.data?.fileUrl || data.data;
                  if (imageUrl) {
                    resolve(imageUrl);
                  } else {
                    reject("上传成功但未返回图片地址");
                  }
                } else {
                  reject(data.message || "上传失败");
                }
              } catch (e) {
                console.error("解析上传响应失败：", e);
                reject("上传失败，响应格式错误");
              }
            },
            fail: (err) => {
              console.error("上传文件失败：", err);
              reject("网络错误，上传失败");
            },
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
    
    .upload-status {
      display: block;
      margin-top: 8rpx;
      color: #3b7aff;
      font-weight: 500;
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
    padding-bottom: 50rpx;
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
  
  