<template>
  <view class="container">
    <view class="form-card">
      <!-- 头像上传 -->
      <view class="avatar-section">
        <view class="avatar-wrapper" @click="chooseAvatar">
          <image class="avatar" :src="studentInfo.avatar" mode="aspectFill"></image>
          <view class="upload-mask">
            <uni-icons type="camera" size="24" color="#fff"></uni-icons>
            <text class="upload-text">点击上传</text>
          </view>
        </view>
      </view>
      
      <!-- 基本信息 -->
      <view class="form-section">
        <view class="section-title">基本信息</view>
        <view class="form-item">
          <text class="item-label required">姓名</text>
          <input 
            class="item-input" 
            type="text" 
            v-model="studentInfo.name" 
            placeholder="请输入学生姓名"
            maxlength="10"
          />
        </view>
        
        <view class="form-item">
          <text class="item-label required">性别</text>
          <view class="gender-select">
            <view 
              class="gender-option" 
              :class="{'selected': studentInfo.gender === '男'}"
              @click="studentInfo.gender = '男'"
            >
              <view class="gender-icon male">
                <uni-icons type="person" size="20" color="#fff"></uni-icons>
              </view>
              <text class="gender-text">男</text>
            </view>
            <view 
              class="gender-option" 
              :class="{'selected': studentInfo.gender === '女'}"
              @click="studentInfo.gender = '女'"
            >
              <view class="gender-icon female">
                <uni-icons type="person" size="20" color="#fff"></uni-icons>
              </view>
              <text class="gender-text">女</text>
            </view>
          </view>
        </view>
        
        <view class="form-item">
          <text class="item-label required">学号</text>
          <input 
            class="item-input" 
            type="text" 
            v-model="studentInfo.studentId" 
            placeholder="请输入学号"
            maxlength="20"
          />
        </view>
      </view>
      
      <!-- 学校信息 -->
      <view class="form-section">
        <view class="section-title">学校信息</view>
        <view class="form-item">
          <text class="item-label required">学校</text>
          <picker 
            class="item-picker" 
            mode="selector"
            :range="schoolOptions"
            @change="onSchoolChange"
          >
            <view class="picker-value">
              <text>{{studentInfo.school || '请选择学校'}}</text>
              <uni-icons type="arrowdown" size="14" color="#999"></uni-icons>
            </view>
          </picker>
        </view>
        
        <view class="form-item">
          <text class="item-label required">年级</text>
          <picker 
            class="item-picker" 
            mode="selector"
            :range="gradeOptions"
            @change="onGradeChange"
          >
            <view class="picker-value">
              <text>{{studentInfo.grade || '请选择年级'}}</text>
              <uni-icons type="arrowdown" size="14" color="#999"></uni-icons>
            </view>
          </picker>
        </view>
        
        <view class="form-item">
          <text class="item-label required">班级</text>
          <picker 
            class="item-picker" 
            mode="selector"
            :range="classOptions"
            @change="onClassChange"
          >
            <view class="picker-value">
              <text>{{studentInfo.className || '请选择班级'}}</text>
              <uni-icons type="arrowdown" size="14" color="#999"></uni-icons>
            </view>
          </picker>
        </view>
      </view>
      
      <!-- 校服尺码 -->
      <view class="form-section">
        <view class="section-title">校服尺码</view>
        <view class="form-item">
          <text class="item-label">上衣尺码</text>
          <picker 
            class="item-picker" 
            mode="selector"
            :range="topSizeOptions"
            @change="onTopSizeChange"
          >
            <view class="picker-value">
              <text>{{studentInfo.topSize || '请选择上衣尺码'}}</text>
              <uni-icons type="arrowdown" size="14" color="#999"></uni-icons>
            </view>
          </picker>
        </view>
        
        <view class="form-item">
          <text class="item-label">裤子尺码</text>
          <picker 
            class="item-picker" 
            mode="selector"
            :range="bottomSizeOptions"
            @change="onBottomSizeChange"
          >
            <view class="picker-value">
              <text>{{studentInfo.bottomSize || '请选择裤子尺码'}}</text>
              <uni-icons type="arrowdown" size="14" color="#999"></uni-icons>
            </view>
          </picker>
        </view>
        
        <view class="form-item">
          <text class="item-label">鞋子尺码</text>
          <picker 
            class="item-picker" 
            mode="selector"
            :range="shoesSizeOptions"
            @change="onShoesSizeChange"
          >
            <view class="picker-value">
              <text>{{studentInfo.shoesSize || '请选择鞋子尺码'}}</text>
              <uni-icons type="arrowdown" size="14" color="#999"></uni-icons>
            </view>
          </picker>
        </view>
      </view>
      
      <!-- 提示 -->
      <view class="size-tip">
        <uni-icons type="info" size="14" color="#FF9900"></uni-icons>
        <text class="tip-text">建议参考校服尺码对照表选择合适尺码</text>
      </view>
      
      <!-- 保存按钮 -->
      <button class="save-btn" @click="saveStudentInfo">保存</button>
    </view>
  </view>
</template>

<script>
import config from '@/config'
import { getStudent, addStudent, updateStudent } from '@/api/student'

export default {
  data() {
    return {
      isEdit: false,
      studentId: '',
      studentInfo: {
        avatar: 'https://pic3.zhimg.com/v2-306cd8f07a20cba46873209739c6395d_r.jpg',
        name: '',
        gender: '男',
        studentId: '',
        school: '',
        grade: '',
        className: '',
        topSize: '',
        bottomSize: '',
        shoesSize: ''
      },
      schoolOptions: ['石家庄市第一中学', '石家庄市第二中学', '石家庄市第四十二中学'],
      gradeOptions: ['初一', '初二', '初三', '高一', '高二', '高三'],
      classOptions: ['1班', '2班', '3班', '4班', '5班', '6班', '7班', '8班', '9班', '10班'],
      topSizeOptions: ['150/72A', '155/76A', '160/80A', '165/84A', '170/88A', '175/92A', '180/96A', '185/100A'],
      bottomSizeOptions: ['XS', 'S', 'M', 'L', 'XL', 'XXL', 'XXXL'],
      shoesSizeOptions: ['35码', '36码', '37码', '38码', '39码', '40码', '41码', '42码', '43码', '44码', '45码']
    }
  },
  onLoad(options) {
    if (options.id) {
      this.isEdit = true;
      this.studentId = options.id;
      // 实际开发中应该从API获取学生信息
      this.fetchStudentInfo();
    }
  },
  methods: {
    normalizeStudent(item = {}) {
      return {
        avatar: item.avatar || item.avatarUrl || (item.gender === '女' || item.sex === '1' ? '/static/nvsheng.png' : '/static/nansheng.png'),
        name: item.name || item.studentName || '',
        gender: item.gender === '0' || item.sex === '0' ? '男' : (item.gender === '1' || item.sex === '1' ? '女' : (item.gender || '男')),
        studentId: item.studentId || item.studentNo || item.code || '',
        school: item.school || item.schoolName || '',
        grade: item.grade || item.gradeName || '',
        className: item.className || item.class || '',
        topSize: item.topSize || '',
        bottomSize: item.bottomSize || '',
        shoesSize: item.shoesSize || ''
      };
    },
    getLocalStudents() {
      return uni.getStorageSync('studentList') || [];
    },
    saveLocalStudent(payload) {
      const list = this.getLocalStudents();
      const id = this.studentId || payload.id || `STU${Date.now()}`;
      const nextStudent = {
        ...payload,
        id,
        gender: payload.gender === '男' ? '0' : '1'
      };
      const nextList = [nextStudent, ...list.filter((item) => String(item.id || item.studentId) !== String(id))];
      uni.setStorageSync('studentList', nextList);
      uni.setStorageSync('isImprove', 1);
      return nextStudent;
    },
    // 选择头像
    chooseAvatar() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          const tempFilePath = res.tempFilePaths[0];
          // 实际开发中这里应该上传图片到服务器
          this.studentInfo.avatar = tempFilePath;
        }
      });
    },
    
    // 获取学生信息
    fetchStudentInfo() {
      getStudent(this.studentId)
        .then((res) => {
          this.studentInfo = this.normalizeStudent(res.data || res);
        })
        .catch((error) => {
          if (config.enableMockFallback) {
            const localStudent = this.getLocalStudents().find((item) => String(item.id || item.studentId) === String(this.studentId));
            if (localStudent) this.studentInfo = this.normalizeStudent(localStudent);
            return;
          }
          uni.showToast({
            title: error.message || '学生信息加载失败',
            icon: 'none'
          });
        });
    },
    
    // 选择学校
    onSchoolChange(e) {
      const index = e.detail.value;
      this.studentInfo.school = this.schoolOptions[index];
    },
    
    // 选择年级
    onGradeChange(e) {
      const index = e.detail.value;
      this.studentInfo.grade = this.gradeOptions[index];
    },
    
    // 选择班级
    onClassChange(e) {
      const index = e.detail.value;
      this.studentInfo.className = this.classOptions[index];
    },
    
    // 选择上衣尺码
    onTopSizeChange(e) {
      const index = e.detail.value;
      this.studentInfo.topSize = this.topSizeOptions[index];
    },
    
    // 选择裤子尺码
    onBottomSizeChange(e) {
      const index = e.detail.value;
      this.studentInfo.bottomSize = this.bottomSizeOptions[index];
    },
    
    // 选择鞋子尺码
    onShoesSizeChange(e) {
      const index = e.detail.value;
      this.studentInfo.shoesSize = this.shoesSizeOptions[index];
    },
    
    // 保存学生信息
    saveStudentInfo() {
      // 表单验证
      if (!this.studentInfo.name) {
        uni.showToast({
          title: '请输入学生姓名',
          icon: 'none'
        });
        return;
      }
      
      if (!this.studentInfo.studentId) {
        uni.showToast({
          title: '请输入学号',
          icon: 'none'
        });
        return;
      }
      
      if (!this.studentInfo.school) {
        uni.showToast({
          title: '请选择学校',
          icon: 'none'
        });
        return;
      }
      
      if (!this.studentInfo.grade) {
        uni.showToast({
          title: '请选择年级',
          icon: 'none'
        });
        return;
      }
      
      if (!this.studentInfo.className) {
        uni.showToast({
          title: '请选择班级',
          icon: 'none'
        });
        return;
      }
      
      uni.showLoading({
        title: '保存中'
      });

      const payload = {
        ...this.studentInfo,
        id: this.studentId || undefined,
        sex: this.studentInfo.gender === '男' ? '0' : '1'
      };
      const request = this.isEdit ? updateStudent(payload) : addStudent(payload);

      request
        .catch((error) => {
          if (!config.enableMockFallback) return Promise.reject(error);
          return { data: this.saveLocalStudent(payload) };
        })
        .then((res) => {
          const saved = (res && (res.data || res)) || payload;
          this.saveLocalStudent(Object.assign({}, payload, saved));
          uni.hideLoading();
          uni.showToast({
            title: this.isEdit ? '修改成功' : '添加成功',
            icon: 'success'
          });
          setTimeout(() => {
            uni.navigateBack();
          }, 800);
        })
        .catch((error) => {
          uni.hideLoading();
          uni.showToast({
            title: error.message || '保存失败',
            icon: 'none'
          });
        });
    }
  }
}
</script>

<style lang="scss">
.container {
  padding: 30rpx;
}

.form-card {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
}

// 头像上传区域
.avatar-section {
  display: flex;
  justify-content: center;
  padding: 20rpx 0 40rpx;
  
  .avatar-wrapper {
    width: 160rpx;
    height: 160rpx;
    border-radius: 80rpx;
    overflow: hidden;
    position: relative;
    
    .avatar {
      width: 100%;
      height: 100%;
    }
    
    .upload-mask {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      height: 60rpx;
      background-color: rgba(0, 0, 0, 0.5);
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      
      .upload-text {
        font-size: 20rpx;
        color: #fff;
        margin-top: 2rpx;
      }
    }
  }
}

// 表单区域
.form-section {
  margin-bottom: 40rpx;
  
  .section-title {
    font-size: 30rpx;
    font-weight: 500;
    color: #333;
    margin-bottom: 20rpx;
    position: relative;
    padding-left: 20rpx;
    
    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 6rpx;
      width: 6rpx;
      height: 28rpx;
      background-color: #3B7AFF;
      border-radius: 3rpx;
    }
  }
  
  .form-item {
    display: flex;
    align-items: center;
    padding: 20rpx 0;
    border-bottom: 1rpx solid #f5f5f5;
    
    &:last-child {
      border-bottom: none;
    }
    
    .item-label {
      width: 150rpx;
      font-size: 28rpx;
      color: #333;
      
      &.required::before {
        content: '*';
        color: #ff5a5f;
        margin-right: 5rpx;
      }
    }
    
    .item-input, .item-picker {
      flex: 1;
      font-size: 28rpx;
      color: #333;
    }
    
    .picker-value {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    
    // 性别选择
    .gender-select {
      flex: 1;
      display: flex;
      
      .gender-option {
        display: flex;
        align-items: center;
        margin-right: 60rpx;
        
        &.selected .gender-text {
          color: #3B7AFF;
        }
        
        .gender-icon {
          width: 60rpx;
          height: 60rpx;
          border-radius: 30rpx;
          display: flex;
          justify-content: center;
          align-items: center;
          margin-right: 10rpx;
          
          &.male {
            background-color: #3B7AFF;
          }
          
          &.female {
            background-color: #ff5a5f;
          }
        }
        
        .gender-text {
          font-size: 28rpx;
          color: #666;
        }
      }
    }
  }
}

// 尺码提示
.size-tip {
  display: flex;
  align-items: center;
  background-color: rgba(255, 153, 0, 0.1);
  padding: 20rpx;
  border-radius: 8rpx;
  margin: 30rpx 0;
  
  .tip-text {
    font-size: 24rpx;
    color: #FF9900;
    margin-left: 10rpx;
  }
}

// 保存按钮
.save-btn {
  background-color: #3B7AFF;
  color: #fff;
  height: 90rpx;
  line-height: 90rpx;
  font-size: 32rpx;
  border-radius: 45rpx;
  margin-top: 60rpx;
  
  &::after {
    border: none;
  }
}
</style> 
