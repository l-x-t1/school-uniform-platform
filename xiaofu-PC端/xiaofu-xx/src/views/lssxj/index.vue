<template>
  <div class="app-container retail-goods-page">
    <el-form
      v-show="showSearch"
      ref="queryForm"
      :model="queryParams"
      :inline="true"
      size="small"
      label-width="88px"
      class="query-form"
    >
      <el-form-item label="商品名称" prop="goodsName">
        <el-input
          v-model="queryParams.goodsName"
          placeholder="请输入商品名称"
          clearable
          style="width: 220px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品款号" prop="goodsCode">
        <el-input
          v-model="queryParams.goodsCode"
          placeholder="请输入商品款号"
          clearable
          style="width: 220px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品品类" prop="categoryName">
        <el-input
          v-model="queryParams.categoryName"
          placeholder="请输入商品品类"
          clearable
          style="width: 220px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="厂商名称" prop="supplierName">
        <el-input
          v-model="queryParams.supplierName"
          placeholder="请输入厂商名称"
          clearable
          style="width: 220px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="上下架状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择" clearable style="width: 220px">
          <el-option label="上架中" value="1" />
          <el-option label="已下架" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-refresh" size="mini" @click="getList">刷新</el-button>
      </el-col>
      <right-toolbar :show-search.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="tableData" border>
      <el-table-column label="商品信息" min-width="300">
        <template slot-scope="scope">
          <div class="goods-cell">
            <image-preview :src="scope.row.mainImage" :width="56" :height="56" />
            <div class="goods-meta">
              <div class="goods-name">{{ scope.row.goodsName || '--' }}</div>
              <div class="goods-code">款号：{{ scope.row.goodsCode || '--' }}</div>
              <div class="goods-category">品类：{{ scope.row.categoryName || '--' }}</div>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="厂商名称" prop="supplierName" min-width="160" />
      <el-table-column label="零售价区间" min-width="150" align="center">
        <template slot-scope="scope">
          {{ formatPriceRange(scope.row) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="110" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '1' ? 'success' : 'info'">
            {{ scope.row.status === '1' ? '上架中' : '已下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作时间" width="180" align="center">
        <template slot-scope="scope">
          {{ parseTime(scope.row.operateTime, '{y}-{m}-{d} {h}:{i}') || '--' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="170" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button
            v-hasPermi="['school:retailGoods:query']"
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >查看</el-button>
          <el-button
            v-hasPermi="['school:retailGoods:edit']"
            size="mini"
            type="text"
            :icon="scope.row.status === '1' ? 'el-icon-bottom' : 'el-icon-top'"
            @click="handleToggleStatus(scope.row)"
          >{{ scope.row.status === '1' ? '下架' : '上架' }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div v-if="!loading && tableData.length === 0" class="empty-data">
      <span>暂无零售商品数据</span>
    </div>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-drawer
      title="零售商品详情"
      :visible.sync="detailVisible"
      :size="'620px'"
      direction="rtl"
      :destroy-on-close="true"
      @close="resetDetail"
    >
      <div v-loading="detailLoading" class="detail-panel">
        <template v-if="detailData">
          <div class="detail-header">
            <image-preview :src="detailData.mainImage" :width="96" :height="96" />
            <div class="detail-header-main">
              <h3 class="detail-title">{{ detailData.goodsName || '--' }}</h3>
              <div class="detail-subtitle">
                <span>款号：{{ detailData.goodsCode || '--' }}</span>
                <span>厂商：{{ detailData.supplierName || '--' }}</span>
              </div>
              <div class="detail-tags">
                <el-tag :type="detailData.status === '1' ? 'success' : 'info'">
                  {{ detailData.status === '1' ? '上架中' : '已下架' }}
                </el-tag>
                <el-tag type="warning">{{ getSaleTypeLabel(detailData.saleType) }}</el-tag>
                <el-tag type="info">{{ getPublishStatusLabel(detailData.publishStatus) }}</el-tag>
              </div>
            </div>
          </div>

          <div class="detail-grid">
            <div class="detail-item">
              <span class="label">商品品类</span>
              <span class="value">{{ detailData.categoryName || '--' }}</span>
            </div>
            <div class="detail-item">
              <span class="label">品牌名称</span>
              <span class="value">{{ detailData.brandName || '--' }}</span>
            </div>
            <div class="detail-item">
              <span class="label">零售价区间</span>
              <span class="value">{{ formatPriceRange(detailData) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">库存总数</span>
              <span class="value">{{ detailData.stockTotal != null ? detailData.stockTotal : '--' }}</span>
            </div>
            <div class="detail-item">
              <span class="label">操作人</span>
              <span class="value">{{ detailData.operateBy || '--' }}</span>
            </div>
            <div class="detail-item">
              <span class="label">操作时间</span>
              <span class="value">{{ parseTime(detailData.operateTime, '{y}-{m}-{d} {h}:{i}') || '--' }}</span>
            </div>
          </div>

          <div v-if="detailImageList.length" class="detail-section">
            <div class="section-title">详情图片</div>
            <div class="image-list">
              <image-preview
                v-for="image in detailImageList"
                :key="image"
                :src="image"
                :width="110"
                :height="110"
              />
            </div>
          </div>

          <div class="detail-section">
            <div class="section-title">商品详情</div>
            <div v-if="detailData.detailContent" class="html-content" v-html="detailData.detailContent" />
            <div v-else class="empty-text">暂无详情</div>
          </div>

          <div class="detail-section">
            <div class="section-title">尺码信息</div>
            <el-table :data="detailData.xfptGoodsSizeList || []" size="mini" border>
              <el-table-column label="尺码" prop="sizeCode" width="100" />
              <el-table-column label="零售价" min-width="120" align="center">
                <template slot-scope="scope">
                  ￥{{ formatMoney(scope.row.retailPrice) }}
                </template>
              </el-table-column>
              <el-table-column label="统购价" min-width="120" align="center">
                <template slot-scope="scope">
                  {{ scope.row.groupPrice != null ? '￥' + formatMoney(scope.row.groupPrice) : '--' }}
                </template>
              </el-table-column>
              <el-table-column label="库存" prop="stockNum" width="90" align="center" />
              <el-table-column label="状态" width="90" align="center">
                <template slot-scope="scope">
                  <el-tag :type="scope.row.status === '1' ? 'success' : 'info'">
                    {{ scope.row.status === '1' ? '启用' : '停用' }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
            <div v-if="!detailData.xfptGoodsSizeList || detailData.xfptGoodsSizeList.length === 0" class="empty-text">
              暂无尺码信息
            </div>
          </div>
        </template>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { getRetailGoods, listRetailGoods, updateRetailGoodsStatus } from '@/api/school/retailGoods'

export default {
  name: 'LssxjIndex',
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      tableData: [],
      detailVisible: false,
      detailLoading: false,
      detailData: null,
      detailImageList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        goodsName: '',
        goodsCode: '',
        categoryName: '',
        supplierName: '',
        status: ''
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listRetailGoods(this.queryParams)
        .then(response => {
          this.tableData = response.rows || []
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
      this.handleQuery()
    },
    handleView(row) {
      this.detailVisible = true
      this.loadDetail(row.id)
    },
    loadDetail(id) {
      this.detailLoading = true
      return getRetailGoods(id)
        .then(response => {
          this.detailData = response.data || null
          this.detailImageList = this.resolveImages(this.detailData && this.detailData.detailImagesJson)
        })
        .finally(() => {
          this.detailLoading = false
        })
    },
    handleToggleStatus(row) {
      const nextStatus = row.status === '1' ? '0' : '1'
      const actionText = nextStatus === '1' ? '上架' : '下架'
      this.$modal.confirm(`确认${actionText}该商品吗？`).then(() => {
        return updateRetailGoodsStatus(row.id, { status: nextStatus })
      }).then(() => {
        this.$modal.msgSuccess(`${actionText}成功`)
        this.getList()
        if (this.detailVisible && this.detailData && this.detailData.id === row.id) {
          this.loadDetail(row.id)
        }
      }).catch(() => {})
    },
    resetDetail() {
      this.detailData = null
      this.detailImageList = []
      this.detailLoading = false
    },
    resolveImages(detailImagesJson) {
      if (!detailImagesJson) {
        return []
      }
      if (Array.isArray(detailImagesJson)) {
        return detailImagesJson.filter(item => !!item)
      }
      try {
        const parsed = JSON.parse(detailImagesJson)
        return Array.isArray(parsed) ? parsed.filter(item => !!item) : []
      } catch (e) {
        return String(detailImagesJson)
          .split(',')
          .map(item => item.trim())
          .filter(item => !!item)
      }
    },
    formatMoney(value) {
      if (value === null || value === undefined || value === '') {
        return '--'
      }
      const number = Number(value)
      if (Number.isNaN(number)) {
        return '--'
      }
      return number.toFixed(2)
    },
    formatPriceRange(row) {
      if (!row) {
        return '--'
      }
      if (row.retailPriceRange) {
        return `￥${row.retailPriceRange}`
      }
      const min = this.formatMoney(row.retailMinPrice)
      const max = this.formatMoney(row.retailMaxPrice)
      if (min === '--' && max === '--') {
        return '--'
      }
      if (min === max) {
        return min === '--' ? '--' : `￥${min}`
      }
      return `￥${min} - ￥${max}`
    },
    getSaleTypeLabel(value) {
      const map = {
        '1': '统购商品',
        '2': '零售商品',
        '3': '统购零售'
      }
      return map[value] || '商品类型'
    },
    getPublishStatusLabel(value) {
      const map = {
        '0': '未上架',
        '1': '已上架',
        '2': '已下架'
      }
      return map[value] || '全局状态'
    }
  }
}
</script>

<style scoped>
.retail-goods-page {
  background: linear-gradient(180deg, #f8fbff 0%, #ffffff 36%);
}

.query-form {
  padding: 16px 18px 4px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid #e9eef6;
  border-radius: 14px;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.04);
  margin-bottom: 16px;
}

.mb8 {
  margin-bottom: 8px;
}

.goods-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.goods-meta {
  min-width: 0;
}

.goods-name {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 4px;
}

.goods-code,
.goods-category {
  font-size: 12px;
  color: #6b7280;
  line-height: 1.6;
}

.empty-data {
  text-align: center;
  color: #909399;
  padding: 28px 0 16px;
}

.detail-panel {
  padding: 20px 20px 24px;
}

.detail-header {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: linear-gradient(135deg, #f8fafc 0%, #eef4ff 100%);
  border-radius: 14px;
}

.detail-header-main {
  flex: 1;
  min-width: 0;
}

.detail-title {
  margin: 0 0 10px;
  font-size: 18px;
  color: #111827;
}

.detail-subtitle {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  font-size: 13px;
  color: #4b5563;
  margin-bottom: 10px;
}

.detail-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px 18px;
  margin: 18px 0 10px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 12px 14px;
  border-radius: 12px;
  background: #f8fafc;
}

.detail-item .label {
  font-size: 12px;
  color: #6b7280;
}

.detail-item .value {
  font-size: 14px;
  color: #111827;
  word-break: break-all;
}

.detail-section {
  margin-top: 18px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 12px;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.html-content {
  padding: 14px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  background: #fff;
  color: #374151;
  line-height: 1.8;
  overflow: hidden;
}

::v-deep .html-content img {
  max-width: 100%;
}

.empty-text {
  padding: 18px 0;
  color: #9ca3af;
  text-align: center;
}
</style>
