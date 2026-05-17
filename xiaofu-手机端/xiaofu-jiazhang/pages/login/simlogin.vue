<template>
  <view class="login-container">  
    <!-- {{params}}
    <view>
      <button @click="getToken(params.code)">授权登录</button>
    </view> -->
  </view>
</template>

<script>
export default {
  data() {
    return {
      url:'',
      params:{},
      fullPath:'',
      baseUrl: getApp().globalData.baseUrl,
      data:null
    }
  },
  onLoad(options) {
    const code = options && options.code;
    if (!code) {
      if (this.isLocalDev()) {
        this.devLogin();
        return;
      }
      uni.reLaunch({
        url: '/pages/stats/stats'
      });
      return;
    }
    uni.showLoading({
      title: '加载中...',
      mask: true
    });
    this.params = options;
    this.getToken(code);
  },
  methods: {
    //获取token
    getToken(code){
      let that = this
      uni.request({
        url: that.baseUrl + '/school/simOpen/redirecturi?code='+code,
        method: 'get', 
        success: (res) => { 
          if(res.data.code == 200){ 
            uni.hideLoading();
            this.data = res.data.data;
            uni.removeStorageSync('devOrders');
            uni.removeStorageSync('pendingCommentOrder');
            uni.removeStorageSync('selectedOrder');
            uni.setStorageSync('token', res.data.data.access_token);
            uni.setStorageSync('simId', res.data.data.user.id);
            uni.setStorageSync('isImprove', res.data.data.is_improve);
            uni.setStorageSync('phone', res.data.data.user.parm1);
            if(res.data.data.is_improve == 0){
              uni.redirectTo({
                url: '/pages/user/student-info'
              })
            }else{
              uni.redirectTo({
                url: '/pages/index/index'
              })
            }
          }else{
            uni.hideLoading();
            if (this.isLocalDev()) {
              this.devLogin();
              return;
            }
            uni.showToast({
              title: res.data.msg,
              icon: 'none'
            })
          }
        },
        fail: (err) => {
          uni.hideLoading();
          if (this.isLocalDev()) {
            this.devLogin();
            return;
          }
          uni.showToast({
            title: '登录失败',
            icon: 'none'
          })
        }
      }) 
    },
    isLocalDev() {
      return typeof window !== 'undefined' && ['localhost', '127.0.0.1'].includes(window.location.hostname);
    },
    devLogin() {
      uni.showLoading({
        title: '本地登录中...',
        mask: true
      });
      uni.request({
        url: this.baseUrl + '/school/simOpen/devLogin',
        method: 'get',
        success: (res) => {
          uni.hideLoading();
          if (res.data.code === 200) {
            this.data = res.data.data;
            uni.removeStorageSync('devOrders');
            uni.removeStorageSync('pendingCommentOrder');
            uni.removeStorageSync('selectedOrder');
            uni.setStorageSync('token', res.data.data.access_token);
            uni.setStorageSync('simId', res.data.data.user.id);
            uni.setStorageSync('isImprove', res.data.data.is_improve);
            uni.setStorageSync('phone', res.data.data.user.parm1);
            uni.redirectTo({
              url: res.data.data.is_improve == 0 ? '/pages/user/student-info' : '/pages/index/index'
            });
          } else {
            uni.showToast({
              title: res.data.msg || '本地登录失败',
              icon: 'none'
            });
          }
        },
        fail: () => {
          uni.hideLoading();
          uni.showToast({
            title: '本地登录失败',
            icon: 'none'
          });
        }
      });
    }
  }
}
</script>

<style lang="scss">

</style> 
