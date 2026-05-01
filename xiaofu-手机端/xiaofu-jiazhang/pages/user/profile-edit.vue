<template>
  <view class="container">
    <view class="profile-form">
      <!-- 头像选择 -->
      <view class="form-item avatar-item">
        <text class="item-label">头像</text>
        <view class="avatar-wrapper" @click="chooseAvatar">
          <image class="avatar" :src="userInfo.avatar" mode="aspectFill"></image>
          <view class="avatar-edit-icon">
            <uni-icons type="camera" size="20" color="#fff"></uni-icons>
          </view>
        </view>
      </view>
      
      <!-- 姓名 -->
      <view class="form-item">
        <text class="item-label">姓名</text>
        <input 
          class="item-input" 
          type="text" 
          v-model="userInfo.name" 
          placeholder="请输入您的姓名"
          maxlength="10"
        />
      </view>
      
      <!-- 手机号码 -->
      <view class="form-item">
        <text class="item-label">手机号码</text>
        <input 
          class="item-input" 
          type="number" 
          v-model="userInfo.phone" 
          placeholder="请输入手机号码"
          maxlength="11"
          disabled
        />
        <uni-icons type="locked" size="20" color="#999"></uni-icons>
      </view>
      
      <!-- 性别 -->
      <view class="form-item">
        <text class="item-label">性别</text>
        <picker 
          class="item-picker" 
          mode="selector"
          :range="genderOptions"
          @change="onGenderChange"
        >
          <view class="picker-value">
            <text>{{userInfo.gender || '请选择'}}</text>
            <uni-icons type="arrowdown" size="14" color="#999"></uni-icons>
          </view>
        </picker>
      </view>
      
      <!-- 关系 -->
      <view class="form-item">
        <text class="item-label">关系</text>
        <picker 
          class="item-picker" 
          mode="selector"
          :range="relationOptions"
          @change="onRelationChange"
        >
          <view class="picker-value">
            <text>{{userInfo.relation || '请选择'}}</text>
            <uni-icons type="arrowdown" size="14" color="#999"></uni-icons>
          </view>
        </picker>
      </view>
      
      <!-- 邮箱 -->
      <view class="form-item">
        <text class="item-label">邮箱</text>
        <input 
          class="item-input" 
          type="text" 
          v-model="userInfo.email" 
          placeholder="请输入邮箱地址"
        />
      </view>
      
      <!-- 地址 -->
      <view class="form-item">
        <text class="item-label">家庭住址</text>
        <textarea 
          class="item-textarea" 
          v-model="userInfo.address" 
          placeholder="请输入家庭住址"
          auto-height
        />
      </view>
    </view>
    
    <!-- 保存按钮 -->
    <button class="save-btn" @click="saveProfile">保存</button>
    
    <!-- 提示 -->
    <view class="tips">
      <text class="tip-text">* 手机号码登录后不可修改</text>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      userInfo: {
        avatar: 'https://pic4.zhimg.com/v2-abed1a8c04700ba7d72b45195223e0ff_r.jpg',
        name: '张三',
        phone: '13812345678',
        gender: '男',
        relation: '父亲',
        email: 'zhangsan@example.com',
        address: '河北省石家庄市裕华区体育南大街385号'
      },
      genderOptions: ['男', '女'],
      relationOptions: ['父亲', '母亲', '爷爷', '奶奶', '外公', '外婆', '其他']
    }
  },
  onLoad() {
    // 实际开发中在这里获取用户信息
  },
  methods: {
    // 选择头像
    chooseAvatar() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          const tempFilePaths = res.tempFilePaths;
          // 实际开发中这里应该上传图片到服务器
          this.userInfo.avatar = tempFilePaths[0];
        }
      });
    },
    
    // 性别选择变化
    onGenderChange(e) {
      const index = e.detail.value;
      this.userInfo.gender = this.genderOptions[index];
    },
    
    // 关系选择变化
    onRelationChange(e) {
      const index = e.detail.value;
      this.userInfo.relation = this.relationOptions[index];
    },
    
    // 保存个人资料
    saveProfile() {
      // 表单验证
      if (!this.userInfo.name) {
        uni.showToast({
          title: '请输入姓名',
          icon: 'none'
        });
        return;
      }
      
      if (!this.userInfo.gender) {
        uni.showToast({
          title: '请选择性别',
          icon: 'none'
        });
        return;
      }
      
      if (!this.userInfo.relation) {
        uni.showToast({
          title: '请选择与学生的关系',
          icon: 'none'
        });
        return;
      }
      
      // 实际开发中应该调用API保存数据
      uni.showLoading({
        title: '保存中'
      });
      
      setTimeout(() => {
        uni.hideLoading();
        uni.showToast({
          title: '保存成功',
          icon: 'success'
        });
        
        // 返回上一页
        setTimeout(() => {
          uni.navigateBack();
        }, 1500);
      }, 1000);
    }
  }
}
</script>

<style lang="scss">
.container {
  padding: 30rpx;
  padding-bottom: 100rpx;
}

.profile-form {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 20rpx;
  margin-bottom: 40rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
  
  .form-item {
    display: flex;
    align-items: center;
    padding: 30rpx 20rpx;
    border-bottom: 1rpx solid #f5f5f5;
    
    &:last-child {
      border-bottom: none;
    }
    
    .item-label {
      width: 180rpx;
      font-size: 28rpx;
      color: #333;
    }
    
    .item-input, .item-picker {
      flex: 1;
      font-size: 28rpx;
      color: #333;
    }
    
    .item-textarea {
      flex: 1;
      font-size: 28rpx;
      color: #333;
      min-height: 120rpx;
      line-height: 1.6;
    }
    
    .picker-value {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    
    &.avatar-item {
      align-items: center;
      
      .avatar-wrapper {
        width: 120rpx;
        height: 120rpx;
        border-radius: 60rpx;
        overflow: hidden;
        position: relative;
        margin-left: 30rpx;
        
        .avatar {
          width: 100%;
          height: 100%;
        }
        
        .avatar-edit-icon {
          position: absolute;
          bottom: 0;
          left: 0;
          right: 0;
          height: 40rpx;
          background-color: rgba(0, 0, 0, 0.5);
          display: flex;
          justify-content: center;
          align-items: center;
        }
      }
    }
  }
}

.save-btn {
  background-color: #3B7AFF;
  color: #fff;
  height: 90rpx;
  line-height: 90rpx;
  font-size: 32rpx;
  border-radius: 45rpx;
  margin: 0 20rpx;
  margin-top: 60rpx;
  
  &::after {
    border: none;
  }
}

.tips {
  margin-top: 30rpx;
  padding: 0 30rpx;
  
  .tip-text {
    font-size: 24rpx;
    color: #999;
  }
}
</style> 