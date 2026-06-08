<template>
  <view class="container">
    <view class="form-card">
      <view class="avatar-section">
        <view class="avatar-wrapper" @click="chooseAvatar">
          <image class="avatar" :src="studentInfo.avatar" mode="aspectFill"></image>
          <view class="upload-mask">
            <uni-icons type="camera" size="24" color="#fff"></uni-icons>
            <text class="upload-text">点击上传</text>
          </view>
        </view>
      </view>

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
              :class="{ selected: studentInfo.gender === '0' }"
              @click="studentInfo.gender = '0'"
            >
              <view class="gender-icon male">
                <uni-icons type="person" size="20" color="#fff"></uni-icons>
              </view>
              <text class="gender-text">男</text>
            </view>
            <view
              class="gender-option"
              :class="{ selected: studentInfo.gender === '1' }"
              @click="studentInfo.gender = '1'"
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
            v-model="studentInfo.studentNo"
            placeholder="请输入学号"
            maxlength="20"
          />
        </view>
      </view>

      <view class="form-section">
        <view class="section-title">学校信息</view>
        <view class="form-item school-field">
          <text class="item-label required">学校</text>
          <view class="item-input school-select">
            <lin-select
              :list="schoolList"
              :loading="loadingSchools"
              :value="studentInfo.shcoolId"
              value-key="value"
              name-key="name"
              max-height="220"
              placeholder="输入学校名称可查询"
              loading-text="学校加载中"
              @focus="openSchoolList"
              @input="handleSchoolKeywordChange"
              @change="handleSchoolSelect"
            />
          </view>
        </view>

        <view class="form-item">
          <text class="item-label required">年级</text>
          <picker class="item-picker" mode="selector" :range="gradeOptions" @change="onGradeChange">
            <view class="picker-value">
              <text>{{ studentInfo.grade || '请选择年级' }}</text>
              <uni-icons type="arrowdown" size="14" color="#999"></uni-icons>
            </view>
          </picker>
        </view>

        <view class="form-item">
          <text class="item-label required">班级</text>
          <picker class="item-picker" mode="selector" :range="classOptions" @change="onClassChange">
            <view class="picker-value">
              <text>{{ studentInfo.className || '请选择班级' }}</text>
              <uni-icons type="arrowdown" size="14" color="#999"></uni-icons>
            </view>
          </picker>
        </view>
      </view>

      <view class="form-section">
        <view class="section-title">校服尺码</view>
        <view class="form-item">
          <text class="item-label">上衣尺码</text>
          <picker class="item-picker" mode="selector" :range="topSizeOptions" @change="onTopSizeChange">
            <view class="picker-value">
              <text>{{ studentInfo.topSize || '请选择上衣尺码' }}</text>
              <uni-icons type="arrowdown" size="14" color="#999"></uni-icons>
            </view>
          </picker>
        </view>

        <view class="form-item">
          <text class="item-label">裤子尺码</text>
          <picker class="item-picker" mode="selector" :range="bottomSizeOptions" @change="onBottomSizeChange">
            <view class="picker-value">
              <text>{{ studentInfo.bottomSize || '请选择裤子尺码' }}</text>
              <uni-icons type="arrowdown" size="14" color="#999"></uni-icons>
            </view>
          </picker>
        </view>

        <view class="form-item">
          <text class="item-label">鞋子尺码</text>
          <picker class="item-picker" mode="selector" :range="shoesSizeOptions" @change="onShoesSizeChange">
            <view class="picker-value">
              <text>{{ studentInfo.shoesSize || '请选择鞋子尺码' }}</text>
              <uni-icons type="arrowdown" size="14" color="#999"></uni-icons>
            </view>
          </picker>
        </view>
      </view>

      <view class="size-tip">
        <uni-icons type="info" size="14" color="#FF9900"></uni-icons>
        <text class="tip-text">建议参考校服尺码对照表选择合适尺码</text>
      </view>

      <button class="save-btn" @click="saveStudentInfo">保存</button>
    </view>
  </view>
</template>

<script>
import config from '@/config'
import linSelect from '../../uni_modules/lin-select/components/lin-select/lin-select.vue'
import { getStudent, addStudent, updateStudent, listSchoolOptions } from '@/api/student'

export default {
  components: { linSelect },
  data() {
    return {
      isEdit: false,
      studentId: '',
      loadingSchools: false,
      schoolKeyword: '',
      allSchoolList: [],
      schoolList: [],
      studentInfo: {
        avatar: '/static/nansheng.png',
        name: '',
        gender: '0',
        studentNo: '',
        school: '',
        schoolName: '',
        schoolId: '',
        shcoolId: '',
        deptId: '',
        grade: '',
        classId: '',
        className: '',
        topSize: '',
        bottomSize: '',
        shoesSize: ''
      },
      gradeOptions: ['一年级', '二年级', '三年级', '四年级', '五年级', '六年级', '初一', '初二', '初三', '高一', '高二', '高三'],
      classOptions: ['1班', '2班', '3班', '4班', '5班', '6班', '7班', '8班', '9班', '10班'],
      topSizeOptions: ['150/72A', '155/76A', '160/80A', '165/84A', '170/88A', '175/92A', '180/96A', '185/100A'],
      bottomSizeOptions: ['XS', 'S', 'M', 'L', 'XL', 'XXL', 'XXXL'],
      shoesSizeOptions: ['35码', '36码', '37码', '38码', '39码', '40码', '41码', '42码', '43码', '44码', '45码']
    }
  },
  onLoad(options) {
    this.loadAllSchools()
    if (options.id) {
      this.isEdit = true
      this.studentId = options.id
      this.fetchStudentInfo()
    }
  },
  methods: {
    normalizeStudent(item = {}) {
      const schoolId = item.schoolId || item.shcoolId || item.deptId || item.sysDepts?.deptId || ''
      const schoolName = item.school || item.schoolName || item.sysDepts?.deptName || ''
      const classValue = item.className || item.classId || item.class || ''
      return {
        avatar: item.avatar || item.avatarUrl || ((item.gender === '1' || item.sex === '1') ? '/static/nvsheng.png' : '/static/nansheng.png'),
        name: item.name || item.studentName || '',
        gender: item.gender || item.sex || '0',
        studentNo: item.studentNo || item.student_no || item.code || '',
        school: schoolName,
        schoolName,
        schoolId,
        shcoolId: schoolId,
        deptId: schoolId,
        grade: item.grade || item.gradeName || '',
        classId: item.classId || item.class_id || classValue || '',
        className: classValue,
        birthday: item.birthday || '',
        idNumber: item.idNumber || '',
        topSize: item.topSize || item.top_size || '',
        bottomSize: item.bottomSize || item.bottom_size || '',
        shoesSize: item.shoesSize || item.shoes_size || ''
      }
    },
    getLocalStudents() {
      return uni.getStorageSync('studentList') || []
    },
    saveLocalStudent(payload) {
      const list = this.getLocalStudents()
      const id = this.studentId || payload.id || `STU${Date.now()}`
      const nextStudent = {
        ...payload,
        id,
        studentId: id,
        studentNo: payload.studentNo || '',
        sex: payload.gender === '1' ? '1' : (payload.sex || '0'),
        gender: payload.gender === '1' ? '1' : '0'
      }
      const nextList = [nextStudent, ...list.filter((item) => String(item.id || item.studentId) !== String(id))]
      uni.setStorageSync('studentList', nextList)
      uni.setStorageSync('isImprove', 1)
      return nextStudent
    },
    chooseAvatar() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          this.studentInfo.avatar = res.tempFilePaths[0]
        }
      })
    },
    normalizeSchoolList(list = []) {
      return list
        .filter((item) => item && item.name && item.value)
        .map((item) => ({
          name: item.name,
          value: Number(item.value)
        }))
    },
    ensureSelectedSchoolVisible() {
      if (!this.studentInfo.shcoolId || !this.studentInfo.school) {
        return
      }
      const selected = {
        name: this.studentInfo.school,
        value: Number(this.studentInfo.shcoolId)
      }
      if (!this.allSchoolList.find((item) => item.value === selected.value)) {
        this.allSchoolList = [selected, ...this.allSchoolList]
      }
      if (!this.schoolList.find((item) => item.value === selected.value)) {
        this.schoolList = [selected, ...this.schoolList]
      }
    },
    loadAllSchools() {
      this.loadingSchools = true
      listSchoolOptions('')
        .then((list) => {
          this.allSchoolList = this.normalizeSchoolList(list)
          this.schoolList = [...this.allSchoolList]
          this.ensureSelectedSchoolVisible()
        })
        .catch(() => {
          uni.showToast({
            title: '学校列表加载失败',
            icon: 'none'
          })
        })
        .finally(() => {
          this.loadingSchools = false
        })
    },
    filterSchoolList(keyword = '') {
      const trimmed = keyword.trim()
      if (!trimmed) {
        this.schoolList = [...this.allSchoolList]
      } else {
        this.schoolList = this.allSchoolList.filter((item) => item.name.includes(trimmed))
      }
      this.ensureSelectedSchoolVisible()
    },
    openSchoolList() {
      if (!this.allSchoolList.length && !this.loadingSchools) {
        this.loadAllSchools()
        return
      }
      this.filterSchoolList(this.schoolKeyword)
    },
    fetchStudentInfo() {
      getStudent(this.studentId)
        .then((res) => {
          this.studentInfo = this.normalizeStudent(res.data || res)
          this.schoolKeyword = this.studentInfo.school || ''
          this.ensureSelectedSchoolVisible()
        })
        .catch((error) => {
          if (config.enableMockFallback) {
            const localStudent = this.getLocalStudents().find((item) => String(item.id || item.studentId) === String(this.studentId))
            if (localStudent) {
              this.studentInfo = this.normalizeStudent(localStudent)
              this.schoolKeyword = this.studentInfo.school || ''
              this.ensureSelectedSchoolVisible()
            }
            return
          }
          uni.showToast({
            title: error.message || '学生信息加载失败',
            icon: 'none'
          })
        })
    },
    handleSchoolKeywordChange(keyword) {
      this.schoolKeyword = typeof keyword === 'string' ? keyword : ''
      if (!this.schoolKeyword.trim()) {
        this.studentInfo.school = ''
        this.studentInfo.schoolName = ''
        this.studentInfo.schoolId = ''
        this.studentInfo.shcoolId = ''
        this.studentInfo.deptId = ''
      }
      this.filterSchoolList(this.schoolKeyword)
    },
    handleSchoolSelect(value) {
      const selectedSchool = this.schoolList.find((item) => item.value === Number(value))
      if (!selectedSchool) {
        return
      }
      this.studentInfo.school = selectedSchool.name
      this.studentInfo.schoolName = selectedSchool.name
      this.studentInfo.schoolId = selectedSchool.value
      this.studentInfo.shcoolId = selectedSchool.value
      this.studentInfo.deptId = selectedSchool.value
      this.schoolKeyword = selectedSchool.name
      this.ensureSelectedSchoolVisible()
    },
    onGradeChange(e) {
      const index = e.detail.value
      this.studentInfo.grade = this.gradeOptions[index]
    },
    onClassChange(e) {
      const index = e.detail.value
      this.studentInfo.className = this.classOptions[index]
      this.studentInfo.classId = this.classOptions[index]
    },
    onTopSizeChange(e) {
      this.studentInfo.topSize = this.topSizeOptions[e.detail.value]
    },
    onBottomSizeChange(e) {
      this.studentInfo.bottomSize = this.bottomSizeOptions[e.detail.value]
    },
    onShoesSizeChange(e) {
      this.studentInfo.shoesSize = this.shoesSizeOptions[e.detail.value]
    },
    validateSchoolSelection() {
      return this.allSchoolList.some((item) => item.value === Number(this.studentInfo.shcoolId))
    },
    saveStudentInfo() {
      if (!this.studentInfo.name) {
        uni.showToast({ title: '请输入学生姓名', icon: 'none' })
        return
      }
      if (!this.studentInfo.studentNo) {
        uni.showToast({ title: '请输入学号', icon: 'none' })
        return
      }
      if (!this.studentInfo.shcoolId || !this.validateSchoolSelection()) {
        uni.showToast({ title: '请选择学校列表中的有效学校', icon: 'none' })
        return
      }
      if (!this.studentInfo.grade) {
        uni.showToast({ title: '请选择年级', icon: 'none' })
        return
      }
      if (!this.studentInfo.className) {
        uni.showToast({ title: '请选择班级', icon: 'none' })
        return
      }

      uni.showLoading({ title: '保存中' })

      const payload = {
        ...this.studentInfo,
        id: this.studentId || undefined,
        sex: this.studentInfo.gender === '1' ? '1' : '0'
      }
      const request = this.isEdit ? updateStudent(payload) : addStudent(payload)

      request
        .catch((error) => {
          if (!config.enableMockFallback) return Promise.reject(error)
          return { data: this.saveLocalStudent(payload) }
        })
        .then((res) => {
          const saved = (res && (res.data || res)) || payload
          this.saveLocalStudent(Object.assign({}, payload, saved))
          uni.hideLoading()
          uni.showToast({
            title: this.isEdit ? '修改成功' : '添加成功',
            icon: 'success'
          })
          setTimeout(() => {
            uni.navigateBack()
          }, 800)
        })
        .catch((error) => {
          uni.hideLoading()
          uni.showToast({
            title: error.message || '保存失败',
            icon: 'none'
          })
        })
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

    .item-input,
    .item-picker {
      flex: 1;
      font-size: 28rpx;
      color: #333;
    }

    .picker-value {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

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

.school-field {
  align-items: flex-start;

  .school-select {
    padding-top: 6rpx;
  }
}

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
