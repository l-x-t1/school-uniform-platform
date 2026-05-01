<template>
  <view class="container">
    <!-- 地址列表 -->
    <view class="address-list" v-if="addressList.length > 0">
      <view 
        class="address-item" 
        v-for="(item, index) in addressList" 
        :key="index"
        @click="selectAddress(item)"
      >
        <view class="address-content">
          <view class="contact-row">
            <text class="contact-name">{{item.name}}</text>
            <text class="contact-phone">{{item.phone}}</text>
            <view class="default-tag" v-if="item.isDefault">默认</view>
          </view>
          <view class="address-row">
            <text class="address-text">{{item.province + item.city + item.district + item.detailAddress}}</text>
          </view>
          <view class="tag-row" v-if="item.tag">
            <view class="address-tag">{{item.tag}}</view>
          </view>
        </view>
        <view class="address-actions">
          <view class="action-btn edit-btn" @click.stop="editAddress(item)">
            <uni-icons type="compose" size="18" color="#666"></uni-icons>
          </view>
          <view class="action-btn delete-btn" @click.stop="deleteAddress(item, index)">
            <uni-icons type="trash" size="18" color="#666"></uni-icons>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 空状态 -->
    <view class="empty-state" v-else>
      <image class="empty-image" src="/static/images/empty-address.png" mode="aspectFit"></image>
      <text class="empty-text">暂无收货地址</text>
      <text class="empty-desc">添加地址以便收到校服</text>
    </view>
    
    <!-- 底部按钮 -->
    <view class="footer-btn-wrapper">
      <button class="add-btn" @click="addAddress">
        <uni-icons type="plusempty" size="18" color="#fff"></uni-icons>
        <text>新增收货地址</text>
      </button>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      addressList: [
        {
          id: '1',
          name: '张先生',
          phone: '13812345678',
          province: '河北省',
          city: '石家庄市',
          district: '长安区',
          detailAddress: '和平路123号和平小区2号楼3单元501',
          isDefault: true,
          tag: '家'
        },
        {
          id: '2',
          name: '李女士',
          phone: '13987654321',
          province: '河北省',
          city: '石家庄市',
          district: '新华区',
          detailAddress: '中华北大街256号丰泽园小区5号楼2单元302',
          isDefault: false,
          tag: '学校'
        }
      ],
      // 是否从选择地址进入
      isChooseMode: false
    }
  },
  onLoad(options) {
    // 判断是否是从选择地址进入
    if (options.choose && options.choose === '1') {
      this.isChooseMode = true;
    }
    
    // 获取地址列表
    this.getAddressList();
  },
  methods: {
    // 获取地址列表
    getAddressList() {
      // TODO: 从API获取地址列表
      console.log('获取地址列表');
    },
    
    // 新增地址
    addAddress() {
      uni.navigateTo({
        url: '/pages/user/address-edit'
      });
    },
    
    // 编辑地址
    editAddress(item) {
      uni.navigateTo({
        url: `/pages/user/address-edit?id=${item.id}`
      });
    },
    
    // 删除地址
    deleteAddress(item, index) {
      uni.showModal({
        title: '提示',
        content: '确定要删除此地址吗？',
        success: (res) => {
          if (res.confirm) {
            // TODO: 调用API删除地址
            this.addressList.splice(index, 1);
            uni.showToast({
              title: '删除成功',
              icon: 'none'
            });
          }
        }
      });
    },
    
    // 选择地址（从订单确认页进入时）
    selectAddress(item) {
      if (this.isChooseMode) {
        // 将选中的地址返回给上一页
        let pages = getCurrentPages();
        let prevPage = pages[pages.length - 2];
        
        // 设置上一页的数据
        prevPage.$vm.selectedAddress = item;
        
        // 返回上一页
        uni.navigateBack();
      }
    },
    
    // 设为默认地址
    setAsDefault(item, index) {
      // 先将所有地址设为非默认
      this.addressList.forEach(address => {
        address.isDefault = false;
      });
      
      // 将当前地址设为默认
      this.addressList[index].isDefault = true;
      
      // TODO: 调用API更新默认地址
      uni.showToast({
        title: '设置成功',
        icon: 'none'
      });
    }
  }
}
</script>

<style lang="scss">
.container {
  padding-bottom: 140rpx;
}

.address-list {
  padding: 20rpx;
  
  .address-item {
    display: flex;
    background-color: #fff;
    border-radius: 12rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;
    box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
    
    .address-content {
      flex: 1;
      
      .contact-row {
        display: flex;
        align-items: center;
        margin-bottom: 15rpx;
        
        .contact-name {
          font-size: 32rpx;
          color: #333;
          font-weight: 500;
          margin-right: 20rpx;
        }
        
        .contact-phone {
          font-size: 28rpx;
          color: #666;
        }
        
        .default-tag {
          margin-left: auto;
          background-color: #3B7AFF;
          color: #fff;
          font-size: 22rpx;
          padding: 4rpx 12rpx;
          border-radius: 4rpx;
        }
      }
      
      .address-row {
        margin-bottom: 15rpx;
        
        .address-text {
          font-size: 28rpx;
          color: #333;
          line-height: 1.4;
        }
      }
      
      .tag-row {
        .address-tag {
          display: inline-block;
          font-size: 22rpx;
          color: #3B7AFF;
          background-color: rgba(59, 122, 255, 0.1);
          padding: 4rpx 16rpx;
          border-radius: 20rpx;
        }
      }
    }
    
    .address-actions {
      display: flex;
      flex-direction: column;
      justify-content: center;
      margin-left: 20rpx;
      
      .action-btn {
        width: 60rpx;
        height: 60rpx;
        border-radius: 30rpx;
        background-color: #f5f5f5;
        display: flex;
        justify-content: center;
        align-items: center;
        margin-bottom: 20rpx;
        
        &:last-child {
          margin-bottom: 0;
        }
      }
    }
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
  
  .empty-image {
    width: 240rpx;
    height: 240rpx;
    margin-bottom: 30rpx;
  }
  
  .empty-text {
    font-size: 32rpx;
    color: #333;
    margin-bottom: 10rpx;
  }
  
  .empty-desc {
    font-size: 26rpx;
    color: #999;
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
  
  .add-btn {
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #3B7AFF;
    color: #fff;
    height: 90rpx;
    border-radius: 45rpx;
    font-size: 30rpx;
    
    uni-icons {
      margin-right: 10rpx;
    }
    
    &::after {
      border: none;
    }
  }
}
</style> 