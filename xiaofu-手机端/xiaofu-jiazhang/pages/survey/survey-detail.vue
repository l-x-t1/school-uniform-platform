<template>
  <view class="container">
    <web-view :src="webUrl"></web-view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      id: null,
      webUrl: getApp().globalData.webUrl,
    };
  },
  onLoad(options) {
    this.id = options.id;
    this.webUrl = this.webUrl + "?id=" + this.id; 
  },
  methods: {},
};
</script>
