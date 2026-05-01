<template>
  <view class="container">
    <view class="form-content">
      <!-- 联系人信息 -->
      <view class="form-section">
        <view class="form-item">
          <text class="item-label required">收货人</text>
          <input 
            class="item-input" 
            type="text" 
            v-model="addressInfo.name" 
            placeholder="请输入收货人姓名"
            maxlength="15"
          />
        </view>
        
        <view class="form-item">
          <text class="item-label required">手机号码</text>
          <input 
            class="item-input" 
            type="number" 
            v-model="addressInfo.phone" 
            placeholder="请输入收货人手机号"
            maxlength="11"
          />
        </view>
      </view>
      
      <!-- 地址信息 -->
      <view class="form-section">
        <view class="form-item">
          <text class="item-label required">所在地区</text>
          <view class="region-picker" @click="showRegionPicker">
            <text class="region-text" :class="{'placeholder': !regionText}">{{regionText || '请选择省/市/区'}}</text>
            <uni-icons type="arrowdown" size="14" color="#999"></uni-icons>
          </view>
        </view>
        
        <view class="form-item text-area-item">
          <text class="item-label required">详细地址</text>
          <textarea 
            class="address-textarea" 
            v-model="addressInfo.detailAddress" 
            placeholder="请输入详细地址，如街道名称、门牌号等"
            maxlength="100"
          />
        </view>
        
        <view class="form-item">
          <text class="item-label">地址标签</text>
          <view class="tag-select">
            <view 
              class="tag-item" 
              :class="{'active': addressInfo.tag === '家'}"
              @click="selectTag('家')"
            >家</view>
            <view 
              class="tag-item" 
              :class="{'active': addressInfo.tag === '学校'}"
              @click="selectTag('学校')"
            >学校</view>
            <view 
              class="tag-item" 
              :class="{'active': addressInfo.tag === '公司'}"
              @click="selectTag('公司')"
            >公司</view>
            <view 
              class="tag-item custom-tag" 
              :class="{'active': customTagActive}"
              @click="showCustomTagInput"
            >
              <text v-if="!customTagActive">自定义</text>
              <input 
                v-else
                class="custom-tag-input" 
                type="text" 
                v-model="customTag" 
                placeholder="自定义标签"
                maxlength="4"
                @blur="onCustomTagBlur"
                focus
              />
            </view>
          </view>
        </view>
      </view>
      
      <!-- 默认设置 -->
      <view class="form-section">
        <view class="switch-item">
          <text class="switch-label">设为默认收货地址</text>
          <switch 
            :checked="addressInfo.isDefault" 
            color="#3B7AFF" 
            @change="onDefaultChange"
          />
        </view>
      </view>
    </view>
    
    <!-- 保存按钮 -->
    <view class="footer-btn-wrapper">
      <button class="save-btn" @click="saveAddress">保存</button>
    </view>
    
    <!-- 地区选择器弹窗 -->
    <uni-popup ref="regionPopup" type="bottom">
      <view class="region-picker-popup">
        <view class="picker-header">
          <text class="cancel-btn" @click="cancelRegionSelect">取消</text>
          <text class="title">选择地区</text>
          <text class="confirm-btn" @click="confirmRegionSelect">确定</text>
        </view>
        <view class="picker-body">
          <picker-view 
            class="region-picker-view" 
            :value="regionValue" 
            @change="onRegionChange"
          >
            <picker-view-column>
              <view class="picker-item" v-for="(item, index) in provinces" :key="index">{{item.name}}</view>
            </picker-view-column>
            <picker-view-column>
              <view class="picker-item" v-for="(item, index) in cities" :key="index">{{item.name}}</view>
            </picker-view-column>
            <picker-view-column>
              <view class="picker-item" v-for="(item, index) in districts" :key="index">{{item.name}}</view>
            </picker-view-column>
          </picker-view>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script>
export default {
  data() {
    return {
      // 是否是编辑模式
      isEdit: false,
      // 地址ID
      addressId: '',
      
      // 地址信息
      addressInfo: {
        name: '',
        phone: '',
        province: '',
        city: '',
        district: '',
        detailAddress: '',
        isDefault: false,
        tag: ''
      },
      
      // 地区选择器相关
      regionValue: [0, 0, 0],
      provinces: [{name: '河北省'}, {name: '北京市'}],
      cities: [{name: '石家庄市'}, {name: '保定市'}],
      districts: [{name: '长安区'}, {name: '新华区'}, {name: '桥西区'}],
      
      // 自定义标签
      customTag: '',
      customTagActive: false
    }
  },
  computed: {
    // 地区文本
    regionText() {
      if (this.addressInfo.province && this.addressInfo.city && this.addressInfo.district) {
        return this.addressInfo.province + ' ' + this.addressInfo.city + ' ' + this.addressInfo.district;
      }
      return '';
    },
    
    // 表单是否有效
    isFormValid() {
      return this.addressInfo.name && 
             this.addressInfo.phone && 
             this.addressInfo.province && 
             this.addressInfo.city && 
             this.addressInfo.district && 
             this.addressInfo.detailAddress;
    }
  },
  onLoad(options) {
    if (options.id) {
      this.isEdit = true;
      this.addressId = options.id;
      // 获取地址详情
      this.getAddressDetail();
    }
    
    // 获取省市区数据
    this.getRegionData();
  },
  methods: {
    // 获取地址详情
    getAddressDetail() {
      // 模拟数据
      const addressDetail = {
        id: this.addressId,
        name: '张先生',
        phone: '13812345678',
        province: '河北省',
        city: '石家庄市',
        district: '长安区',
        detailAddress: '和平路123号和平小区2号楼3单元501',
        isDefault: true,
        tag: '家'
      };
      
      // 设置表单数据
      this.addressInfo = addressDetail;
      
      // TODO: 从API获取地址详情
    },
    
    // 获取省市区数据
    getRegionData() {
      // TODO: 从API获取省市区数据
    },
    
    // 显示地区选择器
    showRegionPicker() {
      this.$refs.regionPopup.open();
    },
    
    // 地区选择器变化
    onRegionChange(e) {
      this.regionValue = e.detail.value;
    },
    
    // 取消地区选择
    cancelRegionSelect() {
      this.$refs.regionPopup.close();
    },
    
    // 确认地区选择
    confirmRegionSelect() {
      const [provinceIndex, cityIndex, districtIndex] = this.regionValue;
      
      this.addressInfo.province = this.provinces[provinceIndex].name;
      this.addressInfo.city = this.cities[cityIndex].name;
      this.addressInfo.district = this.districts[districtIndex].name;
      
      this.$refs.regionPopup.close();
    },
    
    // 选择标签
    selectTag(tag) {
      this.addressInfo.tag = tag;
      this.customTagActive = false;
    },
    
    // 显示自定义标签输入框
    showCustomTagInput() {
      this.customTagActive = true;
      this.customTag = this.addressInfo.tag;
    },
    
    // 自定义标签失去焦点
    onCustomTagBlur() {
      if (this.customTag) {
        this.addressInfo.tag = this.customTag;
      } else {
        this.customTagActive = false;
      }
    },
    
    // 默认地址切换
    onDefaultChange(e) {
      this.addressInfo.isDefault = e.detail.value;
    },
    
    // 保存地址
    saveAddress() {
      // 表单验证
      if (!this.isFormValid) {
        uni.showToast({
          title: '请填写完整信息',
          icon: 'none'
        });
        return;
      }
      
      // 手机号验证
      const phoneReg = /^1[3-9]\d{9}$/;
      if (!phoneReg.test(this.addressInfo.phone)) {
        uni.showToast({
          title: '请输入正确的手机号',
          icon: 'none'
        });
        return;
      }
      
      // TODO: 调用API保存地址
      uni.showLoading({
        title: '保存中...'
      });
      
      setTimeout(() => {
        uni.hideLoading();
        uni.showToast({
          title: this.isEdit ? '修改成功' : '添加成功',
          icon: 'success'
        });
        
        // 返回地址列表页
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
  padding-bottom: 140rpx;
}

.form-content {
  padding: 20rpx;
}

.form-section {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 0 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  
  .form-item {
    display: flex;
    align-items: flex-start;
    padding: 30rpx 0;
    border-bottom: 1rpx solid #f5f5f5;
    
    &:last-child {
      border-bottom: none;
    }
    
    &.text-area-item {
      align-items: flex-start;
    }
    
    .item-label {
      width: 180rpx;
      font-size: 28rpx;
      color: #333;
      padding-top: 6rpx;
      
      &.required:before {
        content: '*';
        color: #ff5a5f;
        margin-right: 5rpx;
      }
    }
    
    .item-input {
      flex: 1;
      font-size: 28rpx;
      color: #333;
      height: 40rpx;
    }
    
    .region-picker {
      flex: 1;
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .region-text {
        font-size: 28rpx;
        color: #333;
        
        &.placeholder {
          color: #999;
        }
      }
    }
    
    .address-textarea {
      flex: 1;
      height: 160rpx;
      font-size: 28rpx;
      color: #333;
      line-height: 1.6;
    }
    
    .tag-select {
      flex: 1;
      display: flex;
      flex-wrap: wrap;
      
      .tag-item {
        min-width: 120rpx;
        height: 60rpx;
        line-height: 60rpx;
        text-align: center;
        background-color: #f5f5f5;
        border-radius: 30rpx;
        font-size: 26rpx;
        color: #666;
        margin-right: 20rpx;
        margin-bottom: 20rpx;
        padding: 0 20rpx;
        
        &.active {
          background-color: rgba(59, 122, 255, 0.1);
          color: #3B7AFF;
        }
        
        &.custom-tag {
          display: flex;
          justify-content: center;
          align-items: center;
          
          .custom-tag-input {
            width: 100rpx;
            height: 60rpx;
            font-size: 26rpx;
            text-align: center;
          }
        }
      }
    }
  }
  
  .switch-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 30rpx 0;
    
    .switch-label {
      font-size: 28rpx;
      color: #333;
    }
  }
}

.footer-btn-wrapper {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #fff;
  padding: 20rpx 30rpx;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
  
  .save-btn {
    background-color: #3B7AFF;
    color: #fff;
    height: 90rpx;
    line-height: 90rpx;
    border-radius: 45rpx;
    font-size: 30rpx;
    
    &::after {
      border: none;
    }
  }
}

// 地区选择器弹窗
.region-picker-popup {
  background-color: #fff;
  border-radius: 24rpx 24rpx 0 0;
  
  .picker-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 30rpx;
    border-bottom: 1rpx solid #f5f5f5;
    
    .title {
      font-size: 32rpx;
      color: #333;
      font-weight: 500;
    }
    
    .cancel-btn, .confirm-btn {
      font-size: 28rpx;
    }
    
    .cancel-btn {
      color: #999;
    }
    
    .confirm-btn {
      color: #3B7AFF;
    }
  }
  
  .picker-body {
    height: 500rpx;
    
    .region-picker-view {
      width: 100%;
      height: 100%;
      
      .picker-item {
        line-height: 70rpx;
        text-align: center;
        font-size: 28rpx;
      }
    }
  }
}
</style> 