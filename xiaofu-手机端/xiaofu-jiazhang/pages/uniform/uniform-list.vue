<template>
  <view class="container">
    <!-- 搜索区域 -->
    <view class="search-section">
      <view class="search-bar">
        <uni-icons type="search" size="18" color="#999"></uni-icons>
        <input 
          class="search-input" 
          placeholder="搜索校服款式" 
          confirm-type="search"
          v-model="searchKeyword"
          @confirm="searchUniform"
        />
      </view>
      <view class="filter-btn" @click="showFilterDrawer = true">
        <uni-icons type="settings" size="20" color="#666"></uni-icons>
        <text>筛选</text>
      </view>
    </view>
    
    <!-- 分类选项卡 -->
    <scroll-view scroll-x class="category-tabs" :show-scrollbar="false">
      <view 
        class="tab-item" 
        v-for="(item, index) in categories" 
        :key="index"
        :class="{'active': currentCategory === item.id}"
        @click="switchCategory(item.id)"
      >
        <text class="tab-text">{{item.name}}</text>
      </view>
    </scroll-view>
    
    <!-- 校服列表 -->
    <scroll-view 
      scroll-y 
      class="uniform-list"
      @scrolltolower="loadMore"
      refresher-enabled
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onRefresh"
    >
      <view class="uniform-grid">
        <view 
          class="uniform-item" 
          v-for="(item, index) in uniformList" 
          :key="index"
          @click="goToDetail(item.id)"
        >
          <image class="uniform-image" :src="item.image" mode="aspectFill"></image>
          <view class="uniform-info">
            <text class="uniform-name">{{item.name}}</text>
            <view class="uniform-price-row">
              <text class="uniform-price">¥{{item.price}}</text>
              <text class="sales-count">{{item.salesCount}}人已购买</text>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 加载状态 -->
      <view class="loading-more" v-if="isLoadingMore">
        <uni-icons type="spinner-cycle" size="20" color="#999"></uni-icons>
        <text class="loading-text">加载中...</text>
      </view>
      
      <!-- 无数据提示 -->
      <view class="empty-state" v-if="uniformList.length === 0 && !isLoading">
        <image class="empty-image" src="/static/images/empty-box.png" mode="aspectFit"></image>
        <text class="empty-text">暂无相关校服</text>
      </view>
    </scroll-view>
    
    <!-- 筛选抽屉 -->
    <uni-drawer ref="filterDrawer" :visible="showFilterDrawer" mode="right" @close="showFilterDrawer = false">
      <view class="filter-drawer">
        <view class="drawer-header">
          <text class="drawer-title">筛选条件</text>
          <view class="drawer-actions">
            <button class="reset-btn" @click="resetFilter">重置</button>
            <button class="confirm-btn" @click="applyFilter">确定</button>
          </view>
        </view>
        
        <view class="filter-section">
          <text class="section-title">年级</text>
          <view class="filter-tags">
            <view 
              class="tag-item" 
              v-for="(item, index) in gradeOptions" 
              :key="index"
              :class="{'active': filters.grade === item.value}"
              @click="selectFilter('grade', item.value)"
            >
              {{item.label}}
            </view>
          </view>
        </view>
        
        <view class="filter-section">
          <text class="section-title">季节</text>
          <view class="filter-tags">
            <view 
              class="tag-item" 
              v-for="(item, index) in seasonOptions" 
              :key="index"
              :class="{'active': filters.season === item.value}"
              @click="selectFilter('season', item.value)"
            >
              {{item.label}}
            </view>
          </view>
        </view>
        
        <view class="filter-section">
          <text class="section-title">价格区间</text>
          <view class="price-range">
            <input 
              type="number" 
              class="price-input" 
              placeholder="最低价" 
              v-model="filters.minPrice"
            />
            <text class="price-separator">-</text>
            <input 
              type="number" 
              class="price-input" 
              placeholder="最高价" 
              v-model="filters.maxPrice"
            />
          </view>
        </view>
      </view>
    </uni-drawer>
  </view>
</template>

<script>
export default {
  data() {
    return {
      searchKeyword: '',
      currentCategory: 'all',
      isLoading: false,
      isLoadingMore: false,
      isRefreshing: false,
      page: 1,
      hasMore: true,
      showFilterDrawer: false,
      uniformList: [],
      categories: [
        { id: 'all', name: '全部' },
        { id: 'daily', name: '日常校服' },
        { id: 'sport', name: '运动校服' },
        { id: 'winter', name: '冬季校服' },
        { id: 'summer', name: '夏季校服' },
        { id: 'new', name: '新款上市' }
      ],
      filters: {
        grade: '',
        season: '',
        minPrice: '',
        maxPrice: ''
      },
      gradeOptions: [
        { value: 'primary', label: '小学' },
        { value: 'junior', label: '初中' },
        { value: 'senior', label: '高中' }
      ],
      seasonOptions: [
        { value: 'spring', label: '春季' },
        { value: 'summer', label: '夏季' },
        { value: 'autumn', label: '秋季' },
        { value: 'winter', label: '冬季' }
      ]
    }
  },
  onLoad() {
    this.fetchUniformList();
  },
  methods: {
    // 获取校服列表
    fetchUniformList(refresh = false) {
      if (refresh) {
        this.page = 1;
        this.uniformList = [];
        this.hasMore = true;
      }
      
      if (!this.hasMore || this.isLoading) return;
      
      this.isLoading = true;
      // 模拟API请求
      setTimeout(() => {
        // 这里应该是从API获取数据
        const mockData = Array(10).fill().map((_, index) => ({
          id: (this.page - 1) * 10 + index + 1,
          name: `${this.currentCategory === 'all' ? '' : this.getCategoryName() + ' '}校服${(this.page - 1) * 10 + index + 1}`,
          price: Math.floor(Math.random() * 150) + 50,
          salesCount: Math.floor(Math.random() * 500),
          image: index % 2 === 0 ? '/static/nanxf.png' : '/static/nvxf.png' 
        }));
        
        this.uniformList = [...this.uniformList, ...mockData];
        this.isLoading = false;
        this.isLoadingMore = false;
        this.isRefreshing = false;
        
        // 模拟没有更多数据的情况
        if (this.page >= 3) {
          this.hasMore = false;
        } else {
          this.page += 1;
        }
      }, 800);
    },
    
    // 获取分类名称
    getCategoryName() {
      const category = this.categories.find(item => item.id === this.currentCategory);
      return category ? category.name : '';
    },
    
    // 切换分类
    switchCategory(categoryId) {
      if (this.currentCategory === categoryId) return;
      this.currentCategory = categoryId;
      this.fetchUniformList(true);
    },
    
    // 搜索校服
    searchUniform() {
      if (!this.searchKeyword.trim()) return;
      // 实际开发中应该调用API搜索
      uni.showToast({
        title: `搜索关键词: ${this.searchKeyword}`,
        icon: 'none'
      });
      this.fetchUniformList(true);
    },
    
    // 加载更多
    loadMore() {
      if (!this.hasMore || this.isLoading) return;
      this.isLoadingMore = true;
      this.fetchUniformList();
    },
    
    // 下拉刷新
    onRefresh() {
      this.isRefreshing = true;
      this.fetchUniformList(true);
    },
    
    // 跳转到详情页
    goToDetail(id) {
      uni.navigateTo({
        url: `/pages/uniform/uniform-detail?id=${id}`
      });
    },
    
    // 选择筛选条件
    selectFilter(type, value) {
      if (this.filters[type] === value) {
        this.filters[type] = '';
      } else {
        this.filters[type] = value;
      }
    },
    
    // 重置筛选条件
    resetFilter() {
      this.filters = {
        grade: '',
        season: '',
        minPrice: '',
        maxPrice: ''
      };
    },
    
    // 应用筛选条件
    applyFilter() {
      this.showFilterDrawer = false;
      // 实际开发中应该调用API根据筛选条件查询
      console.log('应用筛选条件', this.filters);
      this.fetchUniformList(true);
    }
  }
}
</script>

<style lang="scss" scoped>
.container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f5f5;
}

// 搜索区域
.search-section {
  display: flex;
  align-items: center;
  padding: 20rpx 30rpx;
  background-color: #fff;
  position: sticky;
  top: 0;
  z-index: 10;
  
  .search-bar {
    flex: 1;
    display: flex;
    align-items: center;
    background-color: #f5f5f5;
    border-radius: 36rpx;
    height: 72rpx;
    padding: 0 20rpx;
    
    .search-input {
      flex: 1;
      height: 100%;
      font-size: 28rpx;
      margin-left: 10rpx;
    }
  }
  
  .filter-btn {
    display: flex;
    align-items: center;
    margin-left: 20rpx;
    font-size: 28rpx;
    color: #666;
    
    text {
      margin-left: 8rpx;
    }
  }
}

// 分类选项卡
.category-tabs {
  display: flex;
  white-space: nowrap;
  background-color: #fff;
  padding: 0 20rpx;
  height: 80rpx;
  border-bottom: 1rpx solid #f0f0f0;
  
  .tab-item {
    display: inline-block;
    height: 80rpx;
    line-height: 80rpx;
    padding: 0 30rpx;
    font-size: 28rpx;
    color: #666;
    position: relative;
    
    &.active {
      color: #3B7AFF;
      font-weight: 500;
      
      &:after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translateX(-50%);
        width: 40rpx;
        height: 4rpx;
        background-color: #3B7AFF;
        border-radius: 2rpx;
      }
    }
  }
}

// 校服列表
.uniform-list {
  flex: 1;
  overflow: hidden;
  
  .uniform-grid {
    display: flex;
    flex-wrap: wrap;
    padding: 20rpx;
    
    .uniform-item {
      width: calc(50% - 20rpx);
      margin: 10rpx;
      background-color: #fff;
      border-radius: 12rpx;
      overflow: hidden;
      box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
      
      .uniform-image {
        width: 100%;
        height: 360rpx;
      }
      
      .uniform-info {
        padding: 20rpx;
        
        .uniform-name {
          font-size: 28rpx;
          color: #333;
          margin-bottom: 16rpx;
          display: -webkit-box;
          -webkit-box-orient: vertical;
          -webkit-line-clamp: 2;
          overflow: hidden;
        }
        
        .uniform-price-row {
          display: flex;
          justify-content: space-between;
          align-items: center;
          
          .uniform-price {
            font-size: 32rpx;
            font-weight: 500;
            color: #f44;
          }
          
          .sales-count {
            font-size: 22rpx;
            color: #999;
          }
        }
      }
    }
  }
  
  // 加载更多
  .loading-more {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100rpx;
    
    .loading-text {
      font-size: 24rpx;
      color: #999;
      margin-left: 10rpx;
    }
  }
  
  // 空状态
  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 100rpx 0;
    
    .empty-image {
      width: 200rpx;
      height: 200rpx;
      margin-bottom: 20rpx;
    }
    
    .empty-text {
      font-size: 28rpx;
      color: #999;
    }
  }
}

// 筛选抽屉
.filter-drawer {
  padding: 30rpx;
  
  .drawer-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 40rpx;
    
    .drawer-title {
      font-size: 32rpx;
      font-weight: 500;
      color: #333;
    }
    
    .drawer-actions {
      display: flex;
      
      .reset-btn, .confirm-btn {
        margin: 0;
        padding: 0 30rpx;
        font-size: 28rpx;
        height: 70rpx;
        line-height: 70rpx;
        border-radius: 35rpx;
      }
      
      .reset-btn {
        background-color: #f5f5f5;
        color: #666;
        margin-right: 20rpx;
        
        &::after {
          border: none;
        }
      }
      
      .confirm-btn {
        background-color: #3B7AFF;
        color: #fff;
        
        &::after {
          border: none;
        }
      }
    }
  }
  
  .filter-section {
    margin-bottom: 40rpx;
    
    .section-title {
      font-size: 28rpx;
      font-weight: 500;
      color: #333;
      margin-bottom: 20rpx;
      display: block;
    }
    
    .filter-tags {
      display: flex;
      flex-wrap: wrap;
      
      .tag-item {
        padding: 15rpx 30rpx;
        background-color: #f5f5f5;
        border-radius: 40rpx;
        font-size: 26rpx;
        color: #666;
        margin: 0 20rpx 20rpx 0;
        
        &.active {
          background-color: rgba(59, 122, 255, 0.1);
          color: #3B7AFF;
        }
      }
    }
    
    .price-range {
      display: flex;
      align-items: center;
      
      .price-input {
        flex: 1;
        height: 80rpx;
        background-color: #f5f5f5;
        border-radius: 40rpx;
        padding: 0 30rpx;
        font-size: 26rpx;
      }
      
      .price-separator {
        margin: 0 20rpx;
        color: #999;
      }
    }
  }
}
</style> 