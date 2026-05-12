<template>
  <div class="app-container evaluation-page">
    <el-card shadow="never" class="evaluation-card">
      <div slot="header" class="evaluation-card__header">
        <div>
          <div class="evaluation-card__title">评价管理</div>
          <div class="evaluation-card__desc">按统购、零购查看订单评价，处理官方回复与评价状态</div>
        </div>
      </div>

      <el-tabs v-model="activeOrderType" @tab-click="handleTabClick">
        <el-tab-pane label="统购评价" name="1" />
        <el-tab-pane label="零购评价" name="2" />
      </el-tabs>

      <el-form
        v-show="showSearch"
        ref="queryForm"
        :model="queryParams"
        size="small"
        :inline="true"
        label-width="78px"
      >
        <el-form-item label="订单号" prop="orderNo">
          <el-input
            v-model="queryParams.orderNo"
            placeholder="请输入订单号"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="学校名称" prop="schoolName">
          <el-input
            v-model="queryParams.schoolName"
            placeholder="请输入学校名称"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="商品名称" prop="goodsName">
          <el-input
            v-model="queryParams.goodsName"
            placeholder="请输入商品名称"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="评价人" prop="userName">
          <el-input
            v-model="queryParams.userName"
            placeholder="请输入评价人"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="评分" prop="score">
          <el-select v-model="queryParams.score" placeholder="请选择评分" clearable>
            <el-option v-for="score in scoreOptions" :key="score" :label="score + '星'" :value="score" />
          </el-select>
        </el-form-item>
        <el-form-item label="官方回复" prop="params.replyStatus">
          <el-select v-model="queryParams.params.replyStatus" placeholder="请选择状态" clearable>
            <el-option label="未回复" value="0" />
            <el-option label="已回复" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="评价状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="正常" value="0" />
            <el-option label="已隐藏" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-row :gutter="10" class="mb8">
        <right-toolbar :show-search.sync="showSearch" @queryTable="getList" />
      </el-row>

      <el-table v-loading="loading" :data="evaluationList" empty-text="暂无评价数据，可执行演示数据脚本后联调">
        <el-table-column label="订单号" align="center" prop="orderNo" min-width="150" show-overflow-tooltip />
        <el-table-column label="学校" align="center" prop="schoolName" min-width="150" show-overflow-tooltip />
        <el-table-column label="商品" align="center" prop="goodsName" min-width="160" show-overflow-tooltip />
        <el-table-column label="评分" align="center" prop="score" width="130">
          <template slot-scope="scope">
            <el-rate
              :value="Number(scope.row.score || 0)"
              disabled
              show-score
              text-color="#ff9900"
              score-template="{value}"
            />
          </template>
        </el-table-column>
        <el-table-column label="评价内容" align="left" prop="content" min-width="220" show-overflow-tooltip />
        <el-table-column label="评价人" align="center" prop="userName" width="120" show-overflow-tooltip />
        <el-table-column label="评价时间" align="center" prop="createTime" width="170">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="官方回复" align="center" width="110">
          <template slot-scope="scope">
            <el-tag :type="hasReply(scope.row) ? 'success' : 'info'">
              {{ hasReply(scope.row) ? '已回复' : '未回复' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="评价状态" align="center" width="110">
          <template slot-scope="scope">
            <el-tag :type="formatStatusType(scope.row.status)">
              {{ formatStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="最新官方回复" align="left" prop="replyContent" min-width="180" show-overflow-tooltip>
          <template slot-scope="scope">
            <span>{{ scope.row.replyContent || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="官方回复时间" align="center" prop="replyTime" width="170">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.replyTime) || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="220">
          <template slot-scope="scope">
            <el-button
              v-hasPermi="['school:evaluation:query']"
              size="mini"
              type="text"
              icon="el-icon-view"
              @click="handleDetail(scope.row)"
            >详情</el-button>
            <el-button
              v-hasPermi="['school:evaluation:reply']"
              size="mini"
              type="text"
              icon="el-icon-chat-dot-square"
              @click="handleReply(scope.row)"
            >官方回复</el-button>
            <el-button
              v-hasPermi="['school:evaluation:hide']"
              size="mini"
              type="text"
              :icon="scope.row.status === '1' ? 'el-icon-refresh-left' : 'el-icon-hide'"
              @click="handleToggleStatus(scope.row)"
            >{{ scope.row.status === '1' ? '恢复' : '隐藏' }}</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <el-dialog title="评价处理详情" :visible.sync="detailOpen" width="820px" append-to-body>
      <div v-loading="detailLoading" class="evaluation-detail">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="订单号">{{ detail.orderNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="订单类型">{{ formatOrderType(detail.orderType) }}</el-descriptions-item>
          <el-descriptions-item label="学校">{{ detail.schoolName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="商品">{{ detail.goodsName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="评价人">{{ detail.userName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="评分">
            <el-rate :value="Number(detail.score || 0)" disabled show-score text-color="#ff9900" />
          </el-descriptions-item>
          <el-descriptions-item label="评价时间">{{ parseTime(detail.createTime) || '-' }}</el-descriptions-item>
          <el-descriptions-item label="官方回复">
            <el-tag :type="hasReply(detail) ? 'success' : 'info'">{{ hasReply(detail) ? '已回复' : '未回复' }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="评价状态">
            <el-tag :type="formatStatusType(detail.status)">{{ formatStatusText(detail.status) }}</el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <div class="comment-thread">
          <div class="comment-thread__header">
            <div>
              <div class="comment-thread__title">评价回复区</div>
              <div class="comment-thread__desc">查看该评价下的回复记录，可添加官方回复</div>
            </div>
            <el-button
              v-hasPermi="['school:evaluation:reply']"
              type="primary"
              size="mini"
              icon="el-icon-chat-dot-square"
              :disabled="!detail.id"
              @click="handleReply(detail)"
            >官方回复</el-button>
          </div>

          <div class="parent-comment">
            <div class="comment-avatar comment-avatar--parent">
              <el-image class="comment-avatar__image" :src="formatAvatarUrl(detail.userAvatar)" fit="cover">
                <img slot="error" class="comment-avatar__fallback-image" :src="defaultAvatar" alt="">
              </el-image>
            </div>
            <div class="parent-comment__body">
              <div class="parent-comment__meta">
                <div>
                  <span class="parent-comment__name">{{ detail.userName || '评价人' }}</span>
                  <el-tag size="mini" type="warning">商品评价</el-tag>
                </div>
                <span class="parent-comment__time">{{ parseTime(detail.createTime) || '-' }}</span>
              </div>
              <div class="parent-comment__goods">
                {{ detail.goodsName || '-' }} · {{ formatOrderType(detail.orderType) }}
              </div>
              <el-rate
                class="parent-comment__rate"
                :value="Number(detail.score || 0)"
                disabled
                show-score
                text-color="#ff9900"
              />
              <div class="parent-comment__content">{{ detail.content || '暂无评价内容' }}</div>
              <div v-if="parseImages(detail.imagesJson).length" class="image-list parent-comment__images">
                <el-image
                  v-for="image in parseImages(detail.imagesJson)"
                  :key="image"
                  class="image-list__item"
                  :src="normalizeImageUrl(image)"
                  :preview-src-list="parseImages(detail.imagesJson).map(normalizeImageUrl)"
                  fit="cover"
                />
              </div>
              <div v-else class="empty-text parent-comment__empty-image">暂无评价图片</div>
            </div>
          </div>

          <div class="reply-thread">
            <div class="reply-thread__header">
              <span>回复记录</span>
              <el-tag size="mini" type="info">{{ replyList.length }} 条</el-tag>
            </div>
            <div v-if="replyList.length" class="reply-list">
              <div v-for="reply in replyList" :key="reply.id" class="reply-item">
                <div class="comment-avatar">
                  <el-image class="comment-avatar__image" :src="formatAvatarUrl(reply.fromUserAvatar)" fit="cover">
                    <img slot="error" class="comment-avatar__fallback-image" :src="defaultAvatar" alt="">
                  </el-image>
                </div>
                <div class="reply-item__body">
                  <div class="reply-item__meta">
                    <span class="reply-item__name">{{ reply.fromUserName || '-' }}</span>
                    <el-tag size="mini" type="info">{{ reply.fromRoleTitle || '回复人' }}</el-tag>
                    <span v-if="reply.toUserName">回复 @{{ reply.toUserName }}</span>
                    <span>{{ parseTime(reply.createTime) || '-' }}</span>
                  </div>
                  <div class="reply-item__content">{{ reply.content }}</div>
                </div>
              </div>
            </div>
            <div v-else class="reply-empty">
              <div class="reply-empty__title">暂无回复记录</div>
              <div class="reply-empty__desc">可以在这里对该评价添加一条官方回复。</div>
              <el-button
                v-hasPermi="['school:evaluation:reply']"
                type="primary"
                plain
                size="mini"
                icon="el-icon-chat-dot-square"
                :disabled="!detail.id"
                @click="handleReply(detail)"
              >添加官方回复</el-button>
            </div>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <el-dialog title="官方回复评价" :visible.sync="replyOpen" width="560px" append-to-body>
      <el-form ref="replyForm" :model="replyForm" :rules="replyRules" label-width="80px">
        <el-form-item label="回复内容" prop="content">
          <el-input
            v-model="replyForm.content"
            type="textarea"
            :rows="6"
            maxlength="500"
            show-word-limit
            placeholder="请输入 1-500 字官方回复内容"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" :loading="replySubmitting" @click="submitReply">确 定</el-button>
        <el-button @click="replyOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getEvaluationComment,
  listEvaluationComment,
  replyEvaluationComment,
  updateEvaluationCommentStatus
} from '@/api/school/comment'
import defaultAvatar from '@/assets/images/profile.jpg'

export default {
  name: 'SchoolEvaluation',
  data() {
    return {
      loading: false,
      detailLoading: false,
      replySubmitting: false,
      showSearch: true,
      activeOrderType: '1',
      total: 0,
      evaluationList: [],
      detailOpen: false,
      replyOpen: false,
      detail: {},
      replyForm: {
        commentId: null,
        content: ''
      },
      defaultAvatar,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderType: '1',
        orderNo: null,
        schoolName: null,
        goodsName: null,
        userName: null,
        score: null,
        status: null,
        params: {
          replyStatus: null
        }
      },
      scoreOptions: [5, 4, 3, 2, 1],
      replyRules: {
        content: [
          { required: true, message: '官方回复内容不能为空', trigger: 'blur' },
          { min: 1, max: 500, message: '官方回复内容长度为 1-500 字', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    replyList() {
      return this.detail.xfptCommentReplyList || []
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      this.queryParams.orderType = this.activeOrderType
      listEvaluationComment(this.queryParams).then(response => {
        this.evaluationList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        orderType: this.activeOrderType,
        orderNo: null,
        schoolName: null,
        goodsName: null,
        userName: null,
        score: null,
        status: null,
        params: {
          replyStatus: null
        }
      }
      this.$nextTick(() => {
        if (this.$refs.queryForm) {
          this.$refs.queryForm.clearValidate()
        }
      })
      this.getList()
    },
    handleTabClick() {
      this.queryParams.pageNum = 1
      this.queryParams.orderType = this.activeOrderType
      this.getList()
    },
    handleDetail(row) {
      this.detailOpen = true
      this.detailLoading = true
      this.detail = {}
      getEvaluationComment(row.id).then(response => {
        this.detail = response.data || {}
        this.detailLoading = false
      }).catch(() => {
        this.detailLoading = false
      })
    },
    handleReply(row) {
      if (!row || !row.id) {
        return
      }
      this.replyForm = {
        commentId: row.id,
        content: ''
      }
      this.replyOpen = true
      this.$nextTick(() => {
        if (this.$refs.replyForm) {
          this.$refs.replyForm.clearValidate()
        }
      })
    },
    submitReply() {
      this.$refs.replyForm.validate(valid => {
        if (!valid) {
          return
        }
        const content = (this.replyForm.content || '').trim()
        if (!content) {
          this.$message.warning('官方回复内容不能为空')
          return
        }
        this.replySubmitting = true
        replyEvaluationComment(this.replyForm.commentId, { content }).then(() => {
          this.$modal.msgSuccess('官方回复成功')
          this.replyOpen = false
          this.replySubmitting = false
          this.getList()
          if (this.detailOpen && this.detail.id === this.replyForm.commentId) {
            this.handleDetail({ id: this.replyForm.commentId })
          }
        }).catch(() => {
          this.replySubmitting = false
        })
      })
    },
    handleToggleStatus(row) {
      const nextStatus = row.status === '1' ? '0' : '1'
      const actionText = nextStatus === '1' ? '隐藏' : '恢复'
      this.$modal.confirm('是否确认' + actionText + '该评价？').then(() => {
        return updateEvaluationCommentStatus(row.id, { status: nextStatus })
      }).then(() => {
        this.$modal.msgSuccess(actionText + '成功')
        this.getList()
        if (this.detailOpen && this.detail.id === row.id) {
          this.handleDetail(row)
        }
      }).catch(() => {})
    },
    hasReply(row) {
      return !!(row && ((row.replyCount && Number(row.replyCount) > 0) || row.replyContent))
    },
    formatStatusText(status) {
      if (status === '1') {
        return '已隐藏'
      }
      return '正常'
    },
    formatStatusType(status) {
      if (status === '1') {
        return 'danger'
      }
      return 'success'
    },
    formatOrderType(orderType) {
      if (orderType === '1') {
        return '统购'
      }
      if (orderType === '2') {
        return '零购'
      }
      return '-'
    },
    parseImages(imagesJson) {
      if (!imagesJson) {
        return []
      }
      try {
        const parsed = JSON.parse(imagesJson)
        if (Array.isArray(parsed)) {
          return parsed.map(item => {
            if (typeof item === 'string') {
              return item
            }
            return item.url || item.path || item.src
          }).filter(Boolean)
        }
        if (typeof parsed === 'string') {
          return this.splitImageText(parsed)
        }
      } catch (e) {
        return this.splitImageText(imagesJson)
      }
      return []
    },
    splitImageText(text) {
      return String(text).split(',').map(item => item.trim()).filter(Boolean)
    },
    normalizeImageUrl(src) {
      if (!src) {
        return ''
      }
      if (/^(https?:)?\/\//.test(src) || /^data:image\//.test(src) || /^blob:/.test(src)) {
        return src
      }
      const baseUrl = process.env.VUE_APP_BASE_API || ''
      return src.indexOf('/') === 0 ? baseUrl + src : baseUrl + '/' + src
    },
    formatAvatarUrl(avatar) {
      if (!avatar) {
        return this.defaultAvatar
      }
      return this.normalizeImageUrl(avatar)
    }
  }
}
</script>

<style lang="scss" scoped>
.evaluation-page {
  .evaluation-card {
    border-radius: 10px;
  }

  .evaluation-card__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .evaluation-card__title {
    color: #1f2d3d;
    font-size: 18px;
    font-weight: 600;
  }

  .evaluation-card__desc {
    margin-top: 6px;
    color: #8492a6;
    font-size: 13px;
  }
}

.evaluation-detail {
  min-height: 240px;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.image-list__item {
  width: 96px;
  height: 96px;
  overflow: hidden;
  border: 1px solid #ebeef5;
  border-radius: 8px;
}

.comment-thread {
  margin-top: 18px;
  padding: 18px;
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
}

.comment-thread__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}

.comment-thread__title {
  color: #1f2d3d;
  font-size: 16px;
  font-weight: 600;
}

.comment-thread__desc {
  margin-top: 4px;
  color: #909399;
  font-size: 12px;
}

.comment-avatar {
  flex: 0 0 34px;
  width: 34px;
  height: 34px;
  overflow: hidden;
  border-radius: 50%;
  color: #fff;
  line-height: 34px;
  text-align: center;
  font-weight: 600;
  background: #909399;
}

.comment-avatar--parent {
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
}

.comment-avatar__image,
.comment-avatar__fallback-image {
  display: block;
  width: 100%;
  height: 100%;
  border-radius: 50%;
}

.parent-comment {
  display: flex;
  gap: 12px;
  padding: 16px;
  border: 1px solid #cfe6ff;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 6px 18px rgba(64, 158, 255, 0.08);
}

.parent-comment__body {
  flex: 1;
  min-width: 0;
}

.parent-comment__meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.parent-comment__name {
  margin-right: 8px;
  color: #303133;
  font-weight: 600;
}

.parent-comment__time,
.parent-comment__goods {
  color: #909399;
  font-size: 12px;
}

.parent-comment__goods,
.parent-comment__rate,
.parent-comment__content,
.parent-comment__images,
.parent-comment__empty-image {
  margin-top: 8px;
}

.parent-comment__content {
  padding: 12px;
  color: #303133;
  line-height: 1.8;
  white-space: pre-wrap;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: #f8fafc;
}

.reply-thread {
  margin-top: 16px;
  padding: 14px 14px 14px 46px;
  border: 1px solid #ebeef5;
  border-radius: 12px;
  background: #fff;
}

.reply-thread__header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  color: #1f2d3d;
  font-weight: 600;
}

.reply-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.reply-item {
  display: flex;
  gap: 10px;
}

.reply-item__body {
  flex: 1;
  min-width: 0;
  padding: 12px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: #f8fafc;
}

.reply-item__meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 8px;
  color: #909399;
  font-size: 12px;
}

.reply-item__name {
  color: #303133;
  font-weight: 600;
}

.reply-item__content {
  color: #606266;
  line-height: 1.7;
  white-space: pre-wrap;
}

.reply-empty {
  padding: 18px;
  color: #909399;
  text-align: center;
  border: 1px dashed #dcdfe6;
  border-radius: 10px;
  background: #f8fafc;
}

.reply-empty__title {
  color: #606266;
  font-weight: 600;
}

.reply-empty__desc {
  margin: 6px 0 10px;
  font-size: 12px;
}

.empty-text {
  color: #909399;
  font-size: 13px;
}
</style>
