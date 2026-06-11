<template>
  <div class="app-container after-sale-page">
    <el-card v-show="showSearch" shadow="never" class="search-card">
      <el-form
        ref="queryForm"
        :model="queryParams"
        size="small"
        :inline="true"
        label-width="96px"
      >
        <el-form-item label="售后单号" prop="afterSaleNo">
          <el-input
            v-model="queryParams.afterSaleNo"
            placeholder="请输入售后单号"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="订单编号" prop="orderNo">
          <el-input
            v-model="queryParams.orderNo"
            placeholder="请输入订单编号"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="退货物流单号" prop="returnTrackingNo">
          <el-input
            v-model="queryParams.returnTrackingNo"
            placeholder="请输入退货物流单号"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="申请用户" prop="userName">
          <el-input
            v-model="queryParams.userName"
            placeholder="请输入申请用户"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="学生姓名" prop="studentName">
          <el-input
            v-model="queryParams.studentName"
            placeholder="请输入学生姓名"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="订单类型" prop="orderType">
          <el-select v-model="queryParams.orderType" placeholder="请选择订单类型" clearable>
            <el-option label="统购" value="1" />
            <el-option label="零售" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="申请类型" prop="applyType">
          <el-select v-model="queryParams.applyType" placeholder="请选择申请类型" clearable>
            <el-option label="仅退款" value="1" />
            <el-option label="退货退款" value="2" />
            <el-option label="换货" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
          <el-button
            v-hasPermi="['school:afterSale:export']"
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
          <div class="page-title">售后单处理</div>
          <div class="page-subtitle">顾客提交退换货后系统自动处理完成，供应商仅查看售后单详情</div>
        </div>
        <right-toolbar :show-search.sync="showSearch" @queryTable="getList" />
      </div>

      <el-table v-loading="loading" :data="afterSaleList" border>
        <el-table-column label="售后单号" prop="afterSaleNo" min-width="180" show-overflow-tooltip />
        <el-table-column label="订单编号" prop="orderNo" min-width="170" show-overflow-tooltip />
        <el-table-column label="退货物流单号" prop="returnTrackingNo" min-width="150" show-overflow-tooltip>
          <template slot-scope="scope">
            {{ scope.row.returnTrackingNo || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="订单类型" width="90" align="center">
          <template slot-scope="scope">
            <el-tag size="mini" :type="scope.row.orderType === '1' ? 'warning' : 'success'">
              {{ formatOrderType(scope.row.orderType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申请类型" width="110" align="center">
          <template slot-scope="scope">
            <el-tag size="mini" :type="getApplyTagType(scope.row.applyType)">
              {{ formatApplyType(scope.row.applyType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申请用户" prop="userName" width="120" align="center" />
        <el-table-column label="学生姓名" prop="studentName" width="120" align="center" />
        <el-table-column label="目标尺码" prop="targetSizeName" width="110" align="center">
          <template slot-scope="scope">
            {{ scope.row.targetSizeName || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="申请数量" prop="applyNum" width="90" align="center" />
        <el-table-column label="申请金额" width="120" align="center">
          <template slot-scope="scope">
            {{ formatAmount(scope.row.applyAmount) }}
          </template>
        </el-table-column>
        <el-table-column label="申请原因" min-width="220" show-overflow-tooltip>
          <template slot-scope="scope">
            {{ scope.row.reason || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="申请时间" prop="createTime" width="160" align="center">
          <template slot-scope="scope">
            {{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="当前状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag size="mini" :type="getStatusTagType(scope.row.status)">
              {{ formatStatus(scope.row.status, scope.row) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="110" align="center" fixed="right">
          <template slot-scope="scope">
            <div class="action-buttons">
              <el-button type="text" icon="el-icon-view" @click="handleView(scope.row)">查看详情</el-button>
            </div>
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

    <el-dialog title="售后详情" :visible.sync="detailOpen" width="980px" append-to-body>
      <div v-loading="detailLoading" class="detail-wrap">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="售后单号">{{ detailForm.afterSaleNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="当前状态">{{ formatStatus(detailForm.status, detailForm) }}</el-descriptions-item>
          <el-descriptions-item label="订单编号">{{ detailForm.orderNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="订单类型">{{ formatOrderType(detailForm.orderType) }}</el-descriptions-item>
          <el-descriptions-item label="退货物流单号">{{ detailForm.returnTrackingNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="申请类型">{{ formatApplyType(detailForm.applyType) }}</el-descriptions-item>
          <el-descriptions-item label="申请用户">{{ detailForm.userName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="学生姓名">{{ detailForm.studentName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="目标尺码">{{ detailForm.targetSizeName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="申请数量">{{ detailForm.applyNum || '-' }}</el-descriptions-item>
          <el-descriptions-item label="申请金额">{{ formatAmount(detailForm.applyAmount) }}</el-descriptions-item>
          <el-descriptions-item label="实际退款金额">{{ formatAmount(detailForm.actualRefundAmount) }}</el-descriptions-item>
          <el-descriptions-item label="退款状态">{{ formatRefundStatus(detailForm.refundStatus) }}</el-descriptions-item>
          <el-descriptions-item label="申请时间">
            {{ parseTime(detailForm.createTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="审核时间">
            {{ parseTime(detailForm.auditTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="完成时间">
            {{ parseTime(detailForm.finishTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="审核人">{{ detailForm.auditBy || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="section-block">
          <div class="section-title">申请原因</div>
          <div class="plain-box">{{ detailForm.reason || '暂无申请原因' }}</div>
        </div>

        <div v-if="detailImages.length" class="section-block">
          <div class="section-title">凭证图片</div>
          <div class="image-list">
            <el-image
              v-for="(item, index) in detailImages"
              :key="`${item}-${index}`"
              :src="buildFileUrl(item)"
              :preview-src-list="detailPreviewImages"
              fit="cover"
              class="proof-image"
            />
          </div>
        </div>

        <div class="section-block">
          <div class="section-title">处理结果</div>
          <div class="plain-box">{{ detailForm.handleResult || '暂无处理结果' }}</div>
        </div>

        <div class="section-block">
          <div class="section-title">处理备注</div>
          <div class="plain-box">{{ detailForm.handleRemark || '暂无处理备注' }}</div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { getAfterSale, listAfterSale } from '@/api/school/afterSale'

const createDefaultAfterSale = () => ({
  id: undefined,
  afterSaleNo: '',
  orderId: undefined,
  orderItemId: undefined,
  orderNo: '',
  orderType: '',
  schoolId: undefined,
  supplierId: undefined,
  userId: undefined,
  userName: '',
  studentName: '',
  applyType: '',
  targetSizeId: undefined,
  targetSizeName: '',
  returnTrackingNo: '',
  reason: '',
  imagesJson: '',
  applyNum: undefined,
  applyAmount: undefined,
  actualRefundAmount: undefined,
  refundStatus: '',
  refundTime: undefined,
  status: '',
  auditBy: '',
  auditTime: undefined,
  handleResult: '',
  handleRemark: '',
  finishTime: undefined,
  deptId: undefined,
  remark: '',
  createTime: undefined,
  updateTime: undefined,
  delFlag: '0'
})

export default {
  name: 'AfterSale',
  data() {
    return {
      showSearch: true,
      loading: false,
      detailLoading: false,
      detailOpen: false,
      total: 0,
      afterSaleList: [],
      detailForm: createDefaultAfterSale(),
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        afterSaleNo: '',
        orderNo: '',
        returnTrackingNo: '',
        orderType: '',
        applyType: '',
        userName: '',
        studentName: ''
      }
    }
  },
  computed: {
    detailImages() {
      return this.parseImages(this.detailForm.imagesJson)
    },
    detailPreviewImages() {
      return this.detailImages.map((item) => this.buildFileUrl(item))
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listAfterSale({
        ...this.queryParams
      })
        .then((response) => {
          this.afterSaleList = response.rows || []
          this.total = response.total || 0
        })
        .finally(() => {
          this.loading = false
        })
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
        'school/afterSale/export',
        this.queryParams,
        `afterSale_${new Date().getTime()}.xlsx`
      )
    },
    handleView(row) {
      this.detailOpen = true
      this.loadAfterSaleDetail(row, 'detail')
    },
    loadAfterSaleDetail(row, target) {
      const loadingKey = 'detailLoading'
      this[loadingKey] = true
      getAfterSale(row.id)
        .then((response) => {
          const data = {
            ...createDefaultAfterSale(),
            ...row,
            ...(response.data || {})
          }
          this.detailForm = data
        })
        .finally(() => {
          this[loadingKey] = false
        })
    },
    formatOrderType(value) {
      if (String(value) === '1') return '统购'
      if (String(value) === '2') return '零售'
      return '-'
    },
    formatApplyType(value) {
      if (String(value) === '1') return '仅退款'
      if (String(value) === '2') return '退货退款'
      if (String(value) === '3') return '换货'
      return '-'
    },
    formatStatus(value, row = {}) {
      if (String(value) === '1') return '已通过'
      if (String(value) === '3') return '处理中'
      if (String(value) === '4') return '已完成'
      return '-'
    },
    formatRefundStatus(value) {
      if (String(value) === '0') return '未退款'
      if (String(value) === '1') return '退款中'
      if (String(value) === '2') return '退款成功'
      if (String(value) === '3') return '退款失败'
      return '-'
    },
    getApplyTagType(value) {
      if (String(value) === '1') return 'info'
      if (String(value) === '2') return 'warning'
      if (String(value) === '3') return 'success'
      return ''
    },
    getStatusTagType(value) {
      if (String(value) === '1') return 'info'
      if (String(value) === '3') return 'primary'
      if (String(value) === '4') return 'success'
      return 'info'
    },
    formatAmount(value) {
      if (value === undefined || value === null || value === '') {
        return '-'
      }
      return `¥${Number(value).toFixed(2)}`
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

.action-buttons {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  flex-wrap: nowrap;
  white-space: nowrap;
}

.action-buttons :deep(.el-button) {
  margin-left: 0;
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

.plain-box {
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  background: #fafafa;
  line-height: 1.8;
  color: #606266;
  word-break: break-all;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.proof-image {
  width: 120px;
  height: 120px;
  border-radius: 6px;
  border: 1px solid #ebeef5;
}

</style>
