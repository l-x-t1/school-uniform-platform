<template>
  <view class="container">
    <view class="card">
      <view class="title">评价详情</view>
      <view class="row"><text class="label">订单编号</text><text class="value">{{ orderNo || '-' }}</text></view>
      <view class="row"><text class="label">订单商品</text><text class="value">{{ comment.goodsName || '-' }}</text></view>
      <view class="row"><text class="label">评分</text><text class="value">{{ comment.score || 0 }} 分</text></view>
      <view class="block">
        <text class="label">评价内容</text>
        <text class="content">{{ comment.content || '暂无评价内容' }}</text>
      </view>
      <view class="block" v-if="comment.replyContent">
        <text class="label">商家回复</text>
        <text class="content">{{ comment.replyContent }}</text>
      </view>
    </view>
  </view>
</template>

<script>
import { listComments } from '@/api/comment'

export default {
  data() {
    return {
      orderId: '',
      orderNo: '',
      comment: {}
    }
  },
  onLoad(options) {
    this.orderId = options.id || ''
    this.orderNo = options.orderNo ? decodeURIComponent(options.orderNo) : ''
    this.loadComment()
  },
  methods: {
    loadComment() {
      listComments({ orderId: this.orderId, pageNum: 1, pageSize: 20 }).then((res) => {
        const rows = res.rows || []
        this.comment = rows[0] || {}
      })
    }
  }
}
</script>

<style scoped>
.container { min-height: 100vh; background: #f5f7fb; padding: 24rpx; box-sizing: border-box; }
.card { background: #fff; border-radius: 16rpx; padding: 28rpx; }
.title { font-size: 34rpx; font-weight: 600; margin-bottom: 20rpx; }
.row { display: flex; justify-content: space-between; padding: 18rpx 0; border-bottom: 1rpx solid #f1f1f1; }
.block { padding-top: 22rpx; }
.label { color: #666; font-size: 28rpx; display: block; margin-bottom: 12rpx; }
.value, .content { color: #222; font-size: 28rpx; }
</style>
