<template>
  <div class="app-container evaluation-page">
    <el-card shadow="never" class="evaluation-card">
      <div slot="header" class="evaluation-card__header">
        <div>
          <div class="evaluation-card__title">评价管理</div>
        </div>
      </div>

      <el-tabs v-model="activeOrderType" class="evaluation-tabs" @tab-click="handleTabClick">
        <el-tab-pane label="统购评价" name="1" />
        <el-tab-pane label="零购评价" name="2" />
      </el-tabs>

      <el-row :gutter="16" class="summary-row">
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="summary-card total">
            <div class="summary-label">当前页评价</div>
            <div class="summary-value">{{ pageSummary.totalCount }}</div>
            <div class="summary-extra">评价记录</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="summary-card score">
            <div class="summary-label">平均评分</div>
            <div class="summary-value">{{ pageSummary.averageScore }}</div>
            <div class="summary-extra">当前页评分均值</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="summary-card replied">
            <div class="summary-label">已回复</div>
            <div class="summary-value">{{ pageSummary.repliedCount }}</div>
            <div class="summary-extra">官方已处理</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="summary-card hidden">
            <div class="summary-label">已隐藏</div>
            <div class="summary-value">{{ pageSummary.hiddenCount }}</div>
            <div class="summary-extra">当前页隐藏评价</div>
          </div>
        </el-col>
      </el-row>

      <el-form
        v-show="showSearch"
        ref="queryForm"
        :model="queryParams"
        class="query-form"
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

      <el-table
        v-loading="loading"
        :data="evaluationList"
        border
        class="goods-table evaluation-table"
        empty-text="暂无评价数据"
      >
        <el-table-column label="订单号" align="center" prop="orderNo" min-width="150" show-overflow-tooltip />
        <el-table-column label="学校" align="center" prop="schoolName" min-width="150" show-overflow-tooltip />
        <el-table-column label="商品" align="center" prop="goodsName" min-width="160" show-overflow-tooltip />
        <el-table-column label="评分" align="center" prop="score" min-width="160" class-name="evaluation-score-column">
          <template slot-scope="scope">
            <el-rate
              class="evaluation-rate"
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

    <el-dialog title="评价详情" :visible.sync="detailOpen" width="920px" append-to-body>
      <div v-loading="detailLoading" class="dialog-scroll evaluation-detail">
        <div class="detail-hero">
          <div class="detail-main">
            <div class="detail-eyebrow">{{ formatOrderType(detail.orderType) }}评价</div>
            <div class="detail-title">{{ detail.goodsName || '-' }}</div>
            <div class="detail-meta">
              <span class="meta-pill">{{ detail.orderNo || '暂无订单号' }}</span>
              <el-tag :type="formatStatusType(detail.status)" size="small">
                {{ formatStatusText(detail.status) }}
              </el-tag>
              <el-tag :type="hasReply(detail) ? 'success' : 'info'" size="small">
                {{ hasReply(detail) ? '已回复' : '未回复' }}
              </el-tag>
            </div>
          </div>
          <div class="score-block">
            <div class="score-label">评价评分</div>
            <div class="score-value">{{ Number(detail.score || 0).toFixed(1) }}</div>
          </div>
        </div>

        <div class="detail-section">
          <div class="section-head">
            <div>
              <h4>评价信息</h4>
              <p>订单、学校、商品和评价状态</p>
            </div>
          </div>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">订单号</span>
              <span class="detail-value">{{ detail.orderNo || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">订单类型</span>
              <span class="detail-value">{{ formatOrderType(detail.orderType) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">学校</span>
              <span class="detail-value">{{ detail.schoolName || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">商品</span>
              <span class="detail-value">{{ detail.goodsName || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">评价人</span>
              <span class="detail-value">{{ detail.userName || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">评价时间</span>
              <span class="detail-value">{{ parseTime(detail.createTime) || '-' }}</span>
            </div>
            <div class="detail-item full">
              <span class="detail-label">评分</span>
              <span class="detail-value">
                <el-rate
                  class="evaluation-rate"
                  :value="Number(detail.score || 0)"
                  disabled
                  show-score
                  text-color="#ff9900"
                />
              </span>
            </div>
          </div>
        </div>

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
                class="evaluation-rate parent-comment__rate"
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
                >
                  <div slot="error" class="image-list__error">
                    <i class="el-icon-picture-outline" />
                    <span>图片加载失败</span>
                  </div>
                </el-image>
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
                    <el-tag
                      size="mini"
                      :type="formatReplyRoleTagType(reply.fromRoleType)"
                      :class="['reply-role-tag', 'reply-role-tag--' + formatReplyRoleClass(reply.fromRoleType)]"
                    >{{ formatReplyRoleText(reply) }}</el-tag>
                    <span v-if="reply.toUserName" class="reply-item__target">回复 @{{ reply.toUserName }}</span>
                    <span class="reply-item__time">{{ parseTime(reply.createTime) || '-' }}</span>
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
    pageSummary() {
      const rows = this.evaluationList || []
      const totalScore = rows.reduce((sum, item) => {
        const score = Number(item.score || 0)
        return sum + (Number.isNaN(score) ? 0 : score)
      }, 0)
      return {
        totalCount: rows.length,
        averageScore: rows.length ? (totalScore / rows.length).toFixed(1) : '0.0',
        repliedCount: rows.filter(item => this.hasReply(item)).length,
        hiddenCount: rows.filter(item => String(item.status) === '1').length
      }
    },
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
        return this.appendDemoImageVersion(src)
      }
      const imageBaseUrl = process.env.VUE_APP_IMAGE_BASE_API || ''
      const filePrefix = '/filestatics'
      const normalizedSrc = src.indexOf('/') === 0 ? src : '/' + src
      const imagePath = normalizedSrc.replace(/^\/(profile|statics)\//, filePrefix + '/')
      const imageUrl = imageBaseUrl ? imageBaseUrl + imagePath : (process.env.VUE_APP_BASE_API || '') + imagePath
      return this.appendDemoImageVersion(imageUrl)
    },
    appendDemoImageVersion(url) {
      if (url.indexOf('/upload/demo/') === -1 || url.indexOf('v=202605122045') !== -1) {
        return url
      }
      return url + (url.indexOf('?') === -1 ? '?' : '&') + 'v=202605122045'
    },
    formatReplyRoleText(reply) {
      const roleMap = {
        '1': '家长',
        '2': '供应商',
        '3': '学校',
        '4': '平台管理员'
      }
      const roleType = reply && reply.fromRoleType !== undefined && reply.fromRoleType !== null ? String(reply.fromRoleType) : ''
      return roleMap[roleType] || (reply && reply.fromRoleTitle) || '回复人'
    },
    formatReplyRoleTagType(roleType) {
      const tagTypeMap = {
        '2': 'warning',
        '3': 'success',
        '4': 'danger'
      }
      return tagTypeMap[String(roleType)] || (String(roleType) === '1' ? '' : 'info')
    },
    formatReplyRoleClass(roleType) {
      const classMap = {
        '1': 'parent',
        '2': 'supplier',
        '3': 'school',
        '4': 'admin'
      }
      return classMap[String(roleType)] || 'unknown'
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
    border-radius: 8px;
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

.evaluation-tabs {
  margin-bottom: 14px;
}

.summary-row {
  margin-bottom: 16px;
}

.summary-card {
  min-height: 112px;
  margin-bottom: 16px;
  padding: 18px 20px;
  border-radius: 8px;
  color: #fff;
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.08);
}

.summary-card.total {
  background: linear-gradient(135deg, #2563eb, #0f766e);
}

.summary-card.score {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.summary-card.replied {
  background: linear-gradient(135deg, #22c55e, #15803d);
}

.summary-card.hidden {
  background: linear-gradient(135deg, #64748b, #334155);
}

.summary-label {
  font-size: 14px;
  opacity: 0.9;
}

.summary-value {
  margin: 8px 0 4px;
  font-size: 28px;
  font-weight: 700;
  line-height: 36px;
}

.summary-extra {
  font-size: 13px;
  opacity: 0.86;
}

.query-form {
  margin-bottom: 16px;
  padding: 18px 18px 4px;
  border: 1px solid #e5edf7;
  border-radius: 8px;
  background: #f8fbff;
}

.goods-table {
  border-radius: 8px;
  overflow: hidden;
}

.goods-table ::v-deep .el-table__header th {
  background: #f8fafc;
  color: #334155;
}

.evaluation-detail {
  min-height: 260px;
}

.dialog-scroll {
  max-height: calc(88vh - 130px);
  overflow-y: auto;
  padding-right: 4px;
}

.detail-hero {
  display: flex;
  align-items: stretch;
  justify-content: space-between;
  gap: 16px;
  padding: 18px 20px;
  border: 1px solid #e5edf7;
  border-radius: 8px;
  background: #f8fbff;
}

.detail-main {
  min-width: 0;
}

.detail-eyebrow {
  color: #64748b;
  font-size: 13px;
  line-height: 20px;
}

.detail-title {
  margin-top: 6px;
  color: #0f172a;
  font-size: 20px;
  font-weight: 700;
  line-height: 28px;
  word-break: break-all;
}

.detail-meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.meta-pill {
  display: inline-flex;
  align-items: center;
  max-width: 100%;
  min-height: 24px;
  padding: 0 10px;
  border: 1px solid #bfdbfe;
  border-radius: 4px;
  color: #1d4ed8;
  background: #eff6ff;
  font-size: 12px;
  line-height: 22px;
  word-break: break-all;
}

.score-block {
  display: flex;
  flex: 0 0 150px;
  flex-direction: column;
  justify-content: center;
  align-items: flex-end;
  border-left: 1px solid #dbe7f3;
  padding-left: 18px;
}

.score-label {
  color: #64748b;
  font-size: 13px;
  line-height: 20px;
}

.score-value {
  margin-top: 8px;
  color: #f59e0b;
  font-size: 30px;
  font-weight: 700;
  line-height: 38px;
}

.detail-section {
  margin-top: 16px;
  border: 1px solid #e5edf7;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  border-bottom: 1px solid #e5edf7;
  background: #f8fafc;
}

.section-head h4 {
  margin: 0;
  color: #10233f;
  font-size: 16px;
  font-weight: 700;
  line-height: 24px;
}

.section-head p {
  margin: 2px 0 0;
  color: #64748b;
  font-size: 13px;
  line-height: 20px;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.detail-item {
  display: grid;
  grid-template-columns: 92px minmax(0, 1fr);
  gap: 12px;
  min-height: 48px;
  padding: 12px 16px;
  border-bottom: 1px solid #edf2f7;
}

.detail-item:nth-child(odd) {
  border-right: 1px solid #edf2f7;
}

.detail-item.full {
  grid-column: 1 / -1;
  border-right: 0;
}

.detail-item:last-child {
  border-bottom: 0;
}

.detail-label {
  color: #64748b;
  line-height: 24px;
}

.detail-value {
  min-width: 0;
  color: #1f2937;
  line-height: 24px;
  word-break: break-all;
}

.evaluation-page ::v-deep .evaluation-score-column .cell {
  overflow: visible;
  white-space: nowrap;
  text-overflow: clip;
}

.evaluation-rate {
  display: inline-flex;
  align-items: center;
  min-width: 136px;
  white-space: nowrap;
}

.evaluation-rate ::v-deep .el-rate__item {
  line-height: 1;
}

.evaluation-rate ::v-deep .el-rate__icon {
  margin-right: 4px;
  font-size: 16px;
}

.evaluation-rate ::v-deep .el-rate__text {
  margin-left: 4px;
  color: #ff9900 !important;
  font-size: 14px;
  line-height: 1;
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

.image-list__error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  color: #909399;
  font-size: 12px;
  background: #f5f7fa;
}

.image-list__error i {
  margin-bottom: 4px;
  font-size: 20px;
}

.comment-thread {
  margin-top: 16px;
  padding: 18px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
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
  border-radius: 8px;
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
  border-radius: 6px;
  background: #f8fafc;
}

.reply-thread {
  margin-top: 16px;
  padding: 14px 14px 14px 46px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
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

.reply-role-tag {
  font-weight: 600;
}

.reply-role-tag--parent {
  color: #1d70f2;
  border-color: #b9d6ff;
  background: #edf5ff;
}

.reply-role-tag--supplier {
  color: #b87300;
  border-color: #f3d19e;
  background: #fdf6ec;
}

.reply-role-tag--school {
  color: #2f8f4e;
  border-color: #b3e19d;
  background: #f0f9eb;
}

.reply-role-tag--admin {
  color: #cf3a3a;
  border-color: #fab6b6;
  background: #fef0f0;
}

.reply-role-tag--unknown {
  color: #606266;
  border-color: #d3d4d6;
  background: #f4f4f5;
}

.reply-item__target {
  color: #606266;
}

.reply-item__time {
  color: #909399;
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
  border-radius: 8px;
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

@media (max-width: 768px) {
  .detail-hero {
    flex-direction: column;
  }

  .score-block {
    align-items: flex-start;
    flex-basis: auto;
    border-left: 0;
    border-top: 1px solid #dbe7f3;
    padding-top: 14px;
    padding-left: 0;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }

  .detail-item,
  .detail-item:nth-child(odd),
  .detail-item.full {
    border-right: 0;
    border-bottom: 1px solid #edf2f7;
  }

  .detail-item:last-child {
    border-bottom: 0;
  }

  .parent-comment,
  .reply-item {
    flex-direction: column;
  }

  .reply-thread {
    padding-left: 14px;
  }
}
</style>
