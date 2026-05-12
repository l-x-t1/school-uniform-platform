<template>
  <view class="container">
    <!-- 学生列表 -->
    <view class="student-list">
      <view
        class="student-card"
        v-for="(student, index) in studentList"
        :key="index"
      >
        <view class="student-header">
          <view class="avatar-wrapper">
            <image
              v-if="student.gender === '0'"
              class="student-avatar"
              src="/static/nansheng.png"
              mode="aspectFill"
            ></image>
            <image
              v-else
              class="student-avatar"
              src="/static/nvsheng.png"
              mode="aspectFill"
            ></image>
          </view>
          <view class="student-basic">
            <view class="name-row">
              <text class="student-name">{{ student.name }}</text>
              <text
                class="student-gender"
                :class="student.gender === '0' ? 'male' : 'female'"
                >{{ student.gender === "0" ? "男" : "女" }}</text
              >
            </view>
          </view>
        </view>

        <view class="student-info">
          <view class="info-row">
            <text class="info-label">所属学校</text>
            <text class="info-value">{{ student.school }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">年级班级</text>
            <text class="info-value"> {{ student.className }}</text>
          </view>
        </view>

        <view class="student-actions">
          <button class="action-btn" @click="editStudent(student)">
            <uni-icons type="compose" size="18" color="#3B7AFF"></uni-icons>
            <text>编辑</text>
          </button>
          <button class="action-btn" @click="deleteStudent(student, index)">
            <uni-icons type="trash" size="18" color="#ff5a5f"></uni-icons>
            <text>删除</text>
          </button>
        </view>
      </view>

      <!-- 添加学生卡片 -->
      <view class="add-card" @click="addStudent">
        <uni-icons type="plusempty" size="32" color="#3B7AFF"></uni-icons>
        <text class="add-text">添加学生</text>
      </view>
    </view>
    <view class="btn-wrapper" v-if="isImprove == 0">
      <button type="primary" @click="complete">完 成</button>
    </view>
  </view>
</template>

<script>
import config from '@/config'
import { listMyStudents, deleteStudent as deleteStudentApi } from '@/api/student'

export default {
  data() {
    return {
      baseUrl: getApp().globalData.baseUrl,
      studentList: [],
      isImprove: uni.getStorageSync("isImprove"),
    };
  },
  created() {
    this.getStudentList();
    console.log(this.isImprove);
  },
  onShow() {
    this.getStudentList();
  },
  methods: {
    normalizeStudent(item = {}) {
      return {
        id: item.id || item.studentId,
        name: item.name || item.studentName || '',
        gender: item.gender || item.sex || '0',
        grade: item.grade || item.gradeName || '',
        className: item.className || item.class || (item.grade && item.classId ? `${item.grade}级${item.classId}班` : ''),
        school: item.school || item.schoolName || (item.sysDepts && item.sysDepts.deptName) || ''
      };
    },
    getLocalStudents() {
      return uni.getStorageSync('studentList') || [];
    },
    saveLocalStudents(list) {
      uni.setStorageSync('studentList', list);
    },
    //获取学生列表
    getStudentList() {
      listMyStudents({ simId: uni.getStorageSync("simId") })
        .then((res) => {
          const rows = res.rows || res.data || res.list || [];
          if (rows.length) {
            this.studentList = rows.map(this.normalizeStudent);
            this.saveLocalStudents(this.studentList);
            return;
          }
          this.studentList = config.enableMockFallback ? this.getLocalStudents().map(this.normalizeStudent) : [];
        })
        .catch((error) => {
          if (config.enableMockFallback) {
            this.studentList = this.getLocalStudents().map(this.normalizeStudent);
            return;
          }
          uni.showToast({
            title: error.message || '学生信息加载失败',
            icon: 'none'
          });
        });
    },
    // 编辑学生信息
    editStudent(student) {
      uni.navigateTo({
        url: `/pages/user/student-edit?id=${student.id}`,
      });
    },

    // 删除学生信息
    deleteStudent(student, index) {
      console.log(student);

      uni.showModal({
        title: "确认删除",
        content: `确定要删除${student.name}的信息吗？`,
        success: (res) => {
            deleteStudentApi(student.id)
              .catch((error) => {
                if (!config.enableMockFallback) return Promise.reject(error);
                const nextList = this.getLocalStudents().filter((item) => String(item.id || item.studentId) !== String(student.id));
                this.saveLocalStudents(nextList);
              })
              .then(() => {
                uni.showToast({
                  title: "删除成功",
                  icon: "success",
                });
                this.getStudentList();
              })
              .catch((error) => {
                uni.showToast({
                  title: error.message || '删除失败',
                  icon: 'none'
                });
              });
        },
      });
    },

    // 添加学生
    addStudent() {
      if (this.studentList.length >= 5) {
        uni.showModal({
          title: "提示",
          content: "最多只能添加5个学生，如需添加更多请联系班主任",
          showCancel: false,
        });
        return;
      }

      uni.navigateTo({
        url: "/pages/user/student-edit",
      });
    },
    complete(){
      if(this.studentList.length == 0){
        uni.showToast({
          title: "请先添加学生",
          icon: "none",
        });
        return;
      }
      uni.setStorageSync("isImprove", 1);
      uni.redirectTo({
        url: '/pages/index/index'
      })
    }
  },
};
</script>

<style lang="scss">
.container {
  padding: 30rpx;
}

// 学生列表
.student-list {
  .student-card {
    background-color: #fff;
    border-radius: 12rpx;
    margin-bottom: 30rpx;
    overflow: hidden;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);

    .student-header {
      display: flex;
      align-items: center;
      padding: 30rpx;
      border-bottom: 1rpx solid #f5f5f5;

      .avatar-wrapper {
        width: 100rpx;
        height: 100rpx;
        border-radius: 50%;
        overflow: hidden;
        margin-right: 20rpx;

        .student-avatar {
          width: 100%;
          height: 100%;
        }
      }

      .student-basic {
        flex: 1;

        .name-row {
          display: flex;
          align-items: center;
          margin-bottom: 10rpx;

          .student-name {
            font-size: 32rpx;
            font-weight: 500;
            color: #333;
            margin-right: 20rpx;
          }

          .student-gender {
            padding: 2rpx 16rpx;
            font-size: 24rpx;
            border-radius: 30rpx;

            &.male {
              background-color: rgba(59, 122, 255, 0.1);
              color: #3b7aff;
            }

            &.female {
              background-color: rgba(255, 90, 95, 0.1);
              color: #ff5a5f;
            }
          }
        }

        .student-id {
          font-size: 26rpx;
          color: #666;
        }
      }
    }

    .student-info {
      padding: 20rpx 30rpx;

      .info-row {
        display: flex;
        margin-bottom: 16rpx;

        &:last-child {
          margin-bottom: 0;
        }

        .info-label {
          width: 140rpx;
          font-size: 28rpx;
          color: #666;
        }

        .info-value {
          flex: 1;
          font-size: 28rpx;
          color: #333;
        }
      }
    }

    .student-actions {
      display: flex;
      padding: 20rpx 30rpx;
      border-top: 1rpx solid #f5f5f5;

      .action-btn {
        flex: 1;
        display: flex;
        justify-content: center;
        align-items: center;
        background-color: transparent;
        height: 80rpx;
        font-size: 28rpx;

        &::after {
          border: none;
        }

        uni-icons {
          margin-right: 10rpx;
        }

        &:first-child {
          color: #3b7aff;
          border-right: 1rpx solid #f5f5f5;
        }

        &:last-child {
          color: #ff5a5f;
        }
      }
    }
  }

  // 添加学生卡片
  .add-card {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 200rpx;
    background-color: #fff;
    border-radius: 12rpx;
    margin-bottom: 30rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);

    .add-text {
      font-size: 28rpx;
      color: #3b7aff;
      margin-top: 10rpx;
    }
  }
}

// 提示区域
.tips-section {
  background-color: rgba(59, 122, 255, 0.05);
  border-radius: 12rpx;
  padding: 20rpx 30rpx;
  margin-bottom: 30rpx;

  .tips-header {
    display: flex;
    align-items: center;
    margin-bottom: 16rpx;

    .tips-title {
      font-size: 28rpx;
      color: #3b7aff;
      margin-left: 10rpx;
      font-weight: 500;
    }
  }

  .tips-content {
    .tip-item {
      font-size: 26rpx;
      color: #666;
      line-height: 1.6;
      display: block;
    }
  }
}
.btn-wrapper{
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx;
  background-color: #fff;
  z-index: 1000;
}
</style>
