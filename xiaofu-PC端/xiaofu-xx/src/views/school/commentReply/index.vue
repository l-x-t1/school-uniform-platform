<template>
  <div class="app-container comment-reply-page">
    <el-card v-show="showSearch" shadow="never" class="search-card">
      <el-form
        ref="queryForm"
        :model="queryParams"
        size="small"
        :inline="true"
        label-width="96px"
      >
        <el-form-item label="订单编号" prop="orderNo">
          <el-input
            v-model="queryParams.orderNo"
            placeholder="请输入订单编号"
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
        <el-form-item label="评价用户" prop="userName">
          <el-input
            v-model="queryParams.userName"
            placeholder="请输入评价用户"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="评分" prop="score">
          <el-select v-model="queryParams.score" placeholder="请选择评分" clearable>
            <el-option v-for="score in [5, 4, 3, 2, 1]" :key="score" :label="`${score}分`" :value="score" />
          </el-select>
        </el-form-item>
        <el-form-item label="评价状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择评价状态" clearable>
            <el-option label="正常" value="0" />
            <el-option label="隐藏" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
          <el-button
            v-hasPermi="['school:comment:export']"
            type="warning"
            plain
            icon="el-icon-download"
            size="mini"
            @click="handleExport"
          >
            导出
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <div class="page-header">
        <div>
          <div class="page-title">订单评价回复</div>
          <div class="page-subtitle">按订单查看评价，区分统购与零售，并可查看详情与回复</div>
        </div>
        <right-toolbar :show-search.sync="showSearch" @queryTable="getList" />
      </div>

      <el-tabs v-model="activeTab" @tab-click="handleTabChange">
        <el-tab-pane label="统购评价" name="1" />
        <el-tab-pane label="零售评价" name="2" />
      </el-tabs>

      <el-table v-loading="loading" :data="commentList" border>
        <el-table-column label="订单编号" prop="orderNo" min-width="180" show-overflow-tooltip />
        <el-table-column label="订单类型" width="90" align="center">
          <template slot-scope="scope">
            <el-tag size="mini" :type="scope.row.orderType === '1' ? 'warning' : 'success'">
              {{ formatOrderType(scope.row.orderType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="商品名称" prop="goodsName" min-width="160" show-overflow-tooltip />
        <el-table-column label="评价用户" prop="userName" width="120" align="center" />
        <el-table-column label="评分" width="130" align="center">
          <template slot-scope="scope">
            <el-rate :value="Number(scope.row.score || 0)" disabled text-color="#ff9900" />
          </template>
        </el-table-column>
        <el-table-column label="评价内容" min-width="220" show-overflow-tooltip>
          <template slot-scope="scope">
            {{ getPlainText(scope.row.content) || "-" }}
          </template>
        </el-table-column>
        <el-table-column label="最近回复时间" prop="replyTime" width="120" align="center">
          <template slot-scope="scope">
            {{ parseTime(scope.row.replyTime, "{y}-{m}-{d}") || "-" }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template slot-scope="scope">
            <el-tag size="mini" :type="scope.row.status === '0' ? 'success' : 'info'">
              {{ scope.row.status === "0" ? "正常" : "隐藏" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" icon="el-icon-view" @click="handleView(scope.row)">查看详情</el-button>
            <el-button
              v-hasPermi="['school:commentReply:add']"
              type="text"
              icon="el-icon-chat-line-square"
              @click="handleReply(scope.row)"
            >
              回复
            </el-button>
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

    <el-dialog title="评价详情" :visible.sync="detailOpen" width="980px" append-to-body>
      <div v-loading="detailLoading" class="detail-wrap">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单编号">{{ detailForm.orderNo || "-" }}</el-descriptions-item>
          <el-descriptions-item label="订单类型">{{ formatOrderType(detailForm.orderType) }}</el-descriptions-item>
          <el-descriptions-item label="商品名称">{{ detailForm.goodsName || "-" }}</el-descriptions-item>
          <el-descriptions-item label="评价用户">{{ detailForm.userName || "-" }}</el-descriptions-item>
          <el-descriptions-item label="评分">
            <el-rate :value="Number(detailForm.score || 0)" disabled text-color="#ff9900" />
          </el-descriptions-item>
          <el-descriptions-item label="最近回复时间">
            {{ parseTime(detailForm.replyTime, "{y}-{m}-{d}") || "-" }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="section-block">
          <div class="section-title">评价内容</div>
          <div class="rich-box" v-html="detailForm.content || '<p>暂无评价内容</p>'" />
        </div>

        <div v-if="commentImages.length" class="section-block">
          <div class="section-title">评价图片</div>
          <div class="image-list">
            <el-image
              v-for="(item, index) in commentImages"
              :key="`${item}-${index}`"
              :src="buildFileUrl(item)"
              :preview-src-list="previewImageList"
              fit="cover"
              class="comment-image"
            />
          </div>
        </div>

        <div class="section-block">
          <div class="section-title">回复记录</div>
          <el-empty v-if="!replyList.length" description="暂无回复记录" :image-size="90" />
          <div v-else class="reply-list">
            <div v-for="item in replyList" :key="item.id" class="reply-item">
              <div class="reply-meta">
                <span class="reply-user">{{ item.fromUserName || "-" }}</span>
                <span class="reply-role">{{ item.fromRoleTitle || formatRoleType(item.fromRoleType) }}</span>
                <span class="reply-arrow">回复</span>
                <span class="reply-user">{{ item.toUserName || "-" }}</span>
                <span class="reply-role">{{ item.toRoleTitle || formatRoleType(item.toRoleType) }}</span>
              </div>
              <div class="reply-content" v-html="item.content || '<p>暂无回复内容</p>'" />
              <div class="reply-footer">
                <span>排序值：{{ item.sortNum || "-" }}</span>
                <span>更新时间：{{ parseTime(item.updateTime || item.createTime, "{y}-{m}-{d} {h}:{i}:{s}") || "-" }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <el-dialog title="回复评价" :visible.sync="replyOpen" width="720px" append-to-body>
      <div v-loading="replyLoading">
        <el-descriptions :column="2" border class="reply-comment-info">
          <el-descriptions-item label="订单编号">{{ replyComment.orderNo || "-" }}</el-descriptions-item>
          <el-descriptions-item label="订单类型">{{ formatOrderType(replyComment.orderType) }}</el-descriptions-item>
          <el-descriptions-item label="商品名称">{{ replyComment.goodsName || "-" }}</el-descriptions-item>
          <el-descriptions-item label="评价用户">{{ replyComment.userName || "-" }}</el-descriptions-item>
        </el-descriptions>
        <div class="section-block">
          <div class="section-title">评价内容</div>
          <div class="rich-box" v-html="replyComment.content || '<p>暂无评价内容</p>'" />
        </div>
        <el-form ref="replyForm" :model="replyForm" :rules="replyRules" label-width="90px">
          <el-form-item label="回复内容" prop="content">
            <el-input
              v-model="replyForm.content"
              type="textarea"
              :rows="6"
              maxlength="1000"
              show-word-limit
              placeholder="请输入回复内容"
            />
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input
              v-model="replyForm.remark"
              type="textarea"
              :rows="3"
              maxlength="200"
              show-word-limit
              placeholder="可选"
            />
          </el-form-item>
        </el-form>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="replyOpen = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitReply">提 交</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { addCommentReply } from '@/api/school/commentReply'
import { getComment, listComment } from '@/api/school/comment'

const createDefaultComment = () => ({
  id: undefined,
  orderId: undefined,
  orderItemId: undefined,
  orderNo: '',
  orderType: '1',
  goodsId: undefined,
  goodsName: '',
  userId: undefined,
  userName: '',
  supplierId: undefined,
  score: undefined,
  content: '',
  imagesJson: '',
  replyTime: undefined,
  status: '0',
  xfptCommentReplyList: []
})

const createDefaultReplyForm = () => ({
  content: '',
  remark: ''
})

export default {
  name: 'CommentReply',
  data() {
    return {
      activeTab: '1',
      showSearch: true,
      loading: false,
      detailLoading: false,
      replyLoading: false,
      submitLoading: false,
      detailOpen: false,
      replyOpen: false,
      total: 0,
      commentList: [],
      detailForm: createDefaultComment(),
      replyComment: createDefaultComment(),
      replyForm: createDefaultReplyForm(),
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderNo: '',
        goodsName: '',
        userName: '',
        score: undefined,
        status: undefined
      },
      replyRules: {
        content: [
          { required: true, message: '请输入回复内容', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    commentImages() {
      return this.parseImages(this.detailForm.imagesJson)
    },
    previewImageList() {
      return this.commentImages.map((item) => this.buildFileUrl(item))
    },
    replyList() {
      const list = this.detailForm.xfptCommentReplyList || []
      return [...list].sort((a, b) => Number(a.sortNum || 0) - Number(b.sortNum || 0))
    },
    currentUser() {
      return this.$store.getters.userInfo || {}
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listComment({
        ...this.queryParams,
        orderType: this.activeTab
      })
        .then((response) => {
          this.commentList = response.rows || []
          this.total = response.total || 0
        })
        .finally(() => {
          this.loading = false
        })
    },
    handleTabChange() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.queryParams.pageNum = 1
      this.getList()
    },
    handleExport() {
      this.download(
        'school/comment/export',
        {
          ...this.queryParams,
          orderType: this.activeTab
        },
        `comment_${new Date().getTime()}.xlsx`
      )
    },
    handleView(row) {
      this.detailOpen = true
      this.loadCommentDetail(row, 'detail')
    },
    handleReply(row) {
      this.replyOpen = true
      this.replyForm = createDefaultReplyForm()
      this.$nextTick(() => {
        if (this.$refs.replyForm) {
          this.$refs.replyForm.clearValidate()
        }
      })
      this.loadCommentDetail(row, 'reply')
    },
    loadCommentDetail(row, target) {
      const loadingKey = target === 'detail' ? 'detailLoading' : 'replyLoading'
      this[loadingKey] = true
      getComment(row.id)
        .then((response) => {
          const data = {
            ...createDefaultComment(),
            ...row,
            ...(response.data || {}),
            orderType: row.orderType || (response.data && response.data.orderType) || this.activeTab
          }
          if (target === 'detail') {
            this.detailForm = data
          } else {
            this.replyComment = data
          }
        })
        .finally(() => {
          this[loadingKey] = false
        })
    },
    submitReply() {
      this.$refs.replyForm.validate((valid) => {
        if (!valid) {
          return
        }
        const source = this.replyComment
        const detailReplies = source.xfptCommentReplyList || []
        const fromRoleType = this.getCurrentReplyRoleType()
        const payload = {
          commentId: source.id,
          orderId: source.orderId,
          orderItemId: source.orderItemId,
          goodsId: source.goodsId,
          supplierId: source.supplierId,
          fromUserId: this.currentUser.userId || this.$store.state.user.id,
          fromUserName: this.currentUser.nickName || this.currentUser.userName || this.$store.getters.nickName || this.$store.getters.name,
          fromRoleType,
          fromRoleTitle: this.getRoleTitle(fromRoleType),
          toUserId: source.userId,
          toUserName: source.userName,
          toRoleType: '1',
          toRoleTitle: '家长',
          content: this.replyForm.content,
          status: '0',
          sortNum: detailReplies.length + 1,
          remark: this.replyForm.remark,
          delFlag: '0'
        }
        this.submitLoading = true
        addCommentReply(payload)
          .then(() => {
            this.$modal.msgSuccess('回复成功')
            this.replyOpen = false
            this.getList()
            if (this.detailOpen && this.detailForm.id === source.id) {
              this.loadCommentDetail(source, 'detail')
            }
          })
          .finally(() => {
            this.submitLoading = false
          })
      })
    },
    getCurrentReplyRoleType() {
      const roles = this.$store.getters.roles || []
      if (roles.some((item) => String(item).includes('parent'))) {
        return '1'
      }
      return '2'
    },
    getRoleTitle(roleType) {
      return roleType === '1' ? '家长' : '供应商'
    },
    formatOrderType(value) {
      if (String(value) === '1') return '统购'
      if (String(value) === '2') return '零售'
      return '-'
    },
    formatRoleType(value) {
      return String(value) === '1' ? '家长' : String(value) === '2' ? '供应商' : '-'
    },
    getPlainText(content) {
      if (!content) return ''
      return String(content).replace(/<[^>]+>/g, '').replace(/\s+/g, ' ').trim()
    },
    parseImages(imagesJson) {
      if (!imagesJson) return []
      if (Array.isArray(imagesJson)) return imagesJson.filter(Boolean)
      const raw = String(imagesJson).trim()
      if (!raw) return []
      try {
        const parsed = JSON.parse(raw)
        if (Array.isArray(parsed)) {
          return parsed
            .map((item) => {
              if (typeof item === 'string') return item
              if (item && typeof item === 'object') return item.url || item.name || ''
              return ''
            })
            .filter(Boolean)
        }
      } catch (error) {
        // Fall back to comma-separated paths.
      }
      return raw.split(',').map((item) => item.trim()).filter(Boolean)
    },
    buildFileUrl(path) {
      if (!path) return ''
      if (/^https?:\/\//.test(path)) return path
      return `${process.env.VUE_APP_IMAGE_BASE_API}${path}`
    }
  }
}
</script>

<style scoped>
.search-card {
  margin-bottom: 16px;
}

.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 16px;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.page-subtitle {
  margin-top: 6px;
  font-size: 13px;
  color: #909399;
}

.detail-wrap {
  min-height: 220px;
}

.section-block {
  margin-top: 20px;
}

.section-title {
  margin-bottom: 12px;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.rich-box {
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  background: #fafafa;
  line-height: 1.8;
  color: #606266;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.comment-image {
  width: 120px;
  height: 120px;
  border-radius: 6px;
  border: 1px solid #ebeef5;
}

.reply-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.reply-item {
  padding: 14px 16px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: #fff;
}

.reply-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  margin-bottom: 10px;
  color: #606266;
  font-size: 13px;
}

.reply-user {
  font-weight: 600;
  color: #303133;
}

.reply-role {
  padding: 2px 8px;
  border-radius: 999px;
  background: #f4f4f5;
}

.reply-arrow {
  color: #909399;
}

.reply-content {
  line-height: 1.8;
  color: #606266;
}

.reply-footer {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 12px;
  font-size: 12px;
  color: #909399;
}

.reply-comment-info {
  margin-bottom: 20px;
}
</style>
