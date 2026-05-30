<template>
  <div class="app-container tonggou-goods-page">
    <el-card shadow="never" class="search-card">
      <el-form
        v-show="showSearch"
        ref="queryForm"
        :model="queryParams"
        size="small"
        :inline="true"
        label-width="82px"
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
            <el-option
              v-for="item in shelfStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="10" class="mb8 action-row">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-refresh" size="mini" @click="getList">刷新</el-button>
      </el-col>
      <right-toolbar :show-search.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="tonggouGoodsList" border>
      <el-table-column label="商品信息" min-width="320">
        <template slot-scope="scope">
          <div class="goods-cell">
            <el-image
              v-if="scope.row.mainImage"
              class="goods-cover"
              :src="scope.row.mainImage"
              fit="cover"
              :preview-src-list="[scope.row.mainImage]"
            >
              <div slot="error" class="goods-cover-placeholder">无图</div>
            </el-image>
            <div v-else class="goods-cover goods-cover-placeholder">无图</div>
            <div class="goods-meta">
              <div class="goods-name">{{ scope.row.goodsName || '-' }}</div>
              <div class="goods-sub">款号：{{ scope.row.goodsCode || '-' }}</div>
              <div class="goods-sub">
                {{ scope.row.categoryName || '未分类' }}
                <span v-if="scope.row.brandName"> / {{ scope.row.brandName }}</span>
              </div>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="厂商名称" prop="supplierName" min-width="180" show-overflow-tooltip />
      <el-table-column label="销售类型" width="120" align="center">
        <template slot-scope="scope">
          <el-tag size="mini" type="warning">{{ getSaleTypeLabel(scope.row.saleType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="统购价区间" min-width="120" align="center">
        <template slot-scope="scope">
          <span>{{ formatGroupPrice(scope.row) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="库存" prop="stockTotal" width="90" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.stockTotal == null ? '-' : scope.row.stockTotal }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="110" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '1' ? 'success' : 'info'" size="mini">
            {{ getShelfStatusLabel(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作时间" width="160" align="center">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.operateTime, '{y}-{m}-{d} {h}:{i}') || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)">详情</el-button>
          <el-button
            v-if="scope.row.status !== '1'"
            v-hasPermi="['school:tonggouGoods:edit']"
            size="mini"
            type="text"
            icon="el-icon-top"
            @click="handleStatus(scope.row, '1')"
          >上架</el-button>
          <el-button
            v-if="scope.row.status === '1'"
            v-hasPermi="['school:tonggouGoods:edit']"
            size="mini"
            type="text"
            icon="el-icon-bottom"
            @click="handleStatus(scope.row, '0')"
          >下架</el-button>
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

    <el-drawer
      title="统购商品详情"
      :visible.sync="detailOpen"
      :size="560"
      :destroy-on-close="true"
      direction="rtl"
    >
      <div v-if="detail" v-loading="detailLoading" class="detail-drawer">
        <div class="detail-header">
          <el-image v-if="detail.mainImage" class="detail-cover" :src="detail.mainImage" fit="cover" />
          <div v-else class="detail-cover detail-cover-placeholder">无图</div>
          <div>
            <div class="detail-title">{{ detail.goodsName || '-' }}</div>
            <div class="detail-sub">款号：{{ detail.goodsCode || '-' }}</div>
            <el-tag :type="detail.status === '1' ? 'success' : 'info'" size="mini">
              {{ getShelfStatusLabel(detail.status) }}
            </el-tag>
          </div>
        </div>

        <el-descriptions :column="1" border size="small" class="detail-section">
          <el-descriptions-item label="厂商名称">{{ detail.supplierName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="商品品类">{{ detail.categoryName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="品牌名称">{{ detail.brandName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="销售类型">{{ getSaleTypeLabel(detail.saleType) }}</el-descriptions-item>
          <el-descriptions-item label="统购价区间">{{ formatGroupPrice(detail) }}</el-descriptions-item>
          <el-descriptions-item label="库存">{{ detail.stockTotal == null ? '-' : detail.stockTotal }}</el-descriptions-item>
          <el-descriptions-item label="最近操作">
            {{ detail.operateBy || '-' }} / {{ parseTime(detail.operateTime, '{y}-{m}-{d} {h}:{i}') || '-' }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="detail-section">
          <div class="section-title">商品尺码</div>
          <el-table :data="detail.xfptGoodsSizeList || []" size="mini" border>
            <el-table-column label="尺码" prop="sizeCode" align="center" />
            <el-table-column label="统购价" prop="groupPrice" align="center">
              <template slot-scope="scope">{{ scope.row.groupPrice == null ? '-' : '¥' + scope.row.groupPrice }}</template>
            </el-table-column>
            <el-table-column label="零售价" prop="retailPrice" align="center">
              <template slot-scope="scope">{{ scope.row.retailPrice == null ? '-' : '¥' + scope.row.retailPrice }}</template>
            </el-table-column>
            <el-table-column label="库存" prop="stockNum" align="center" />
            <el-table-column label="状态" prop="status" align="center">
              <template slot-scope="scope">
                <el-tag size="mini" :type="scope.row.status === '1' ? 'success' : 'info'">
                  {{ scope.row.status === '1' ? '启用' : '停用' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div v-if="detail.detailContent" class="detail-section">
          <div class="section-title">商品详情</div>
          <div class="detail-content" v-html="detail.detailContent" />
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { listTonggouGoods, getTonggouGoods, updateTonggouGoodsStatus } from '@/api/school/tonggouGoods'

export default {
  name: 'TonggouShelfIndex',
  data() {
    return {
      loading: false,
      detailLoading: false,
      showSearch: true,
      total: 0,
      tonggouGoodsList: [],
      detailOpen: false,
      detail: null,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        goodsName: null,
        goodsCode: null,
        categoryName: null,
        supplierName: null,
        status: null
      },
      shelfStatusOptions: [
        { value: '1', label: '已上架' },
        { value: '0', label: '已下架' }
      ],
      saleTypeOptions: [
        { value: '1', label: '统购' },
        { value: '3', label: '统购零售' }
      ],
      hasLoaded: false
    }
  },
  created() {
    this.getList()
  },
  activated() {
    if (this.hasLoaded) {
      this.getList()
    }
  },
  methods: {
    getList() {
      this.loading = true
      return listTonggouGoods(this.queryParams).then(response => {
        this.tonggouGoodsList = response.rows || []
        this.total = response.total || 0
        this.hasLoaded = true
      }).finally(() => {
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
      this.detailOpen = true
      this.detailLoading = true
      this.detail = null
      getTonggouGoods(row.id).then(response => {
        this.detail = response.data || {}
      }).finally(() => {
        this.detailLoading = false
      })
    },
    handleStatus(row, status) {
      const actionName = status === '1' ? '上架' : '下架'
      this.$modal.confirm('确认要' + actionName + '商品“' + row.goodsName + '”吗？').then(() => {
        return updateTonggouGoodsStatus(row.id, status)
      }).then(() => {
        this.$modal.msgSuccess(actionName + '成功')
        this.getList()
      }).catch(() => {})
    },
    getSaleTypeLabel(value) {
      const item = this.saleTypeOptions.find(option => option.value === value)
      return item ? item.label : '-'
    },
    getShelfStatusLabel(value) {
      const item = this.shelfStatusOptions.find(option => option.value === value)
      return item ? item.label : '-'
    },
    formatGroupPrice(row) {
      if (!row) {
        return '-'
      }
      if (row.groupPriceRange) {
        return '¥' + row.groupPriceRange
      }
      if (row.groupMinPrice != null) {
        return '¥' + row.groupMinPrice
      }
      return '-'
    }
  }
}
</script>

<style scoped>
.tonggou-goods-page {
  background: linear-gradient(180deg, #f7fbff 0%, #ffffff 180px);
}

.search-card {
  margin-bottom: 14px;
  border-radius: 12px;
}

.search-card ::v-deep .el-card__body {
  padding-bottom: 4px;
}

.action-row {
  display: flex;
  align-items: center;
}

.goods-cell {
  display: flex;
  align-items: center;
  min-height: 66px;
}

.goods-cover {
  flex: 0 0 auto;
  width: 56px;
  height: 56px;
  margin-right: 12px;
  border-radius: 10px;
  overflow: hidden;
  border: 1px solid #e8edf5;
  background: #f6f8fb;
}

.goods-cover-placeholder,
.detail-cover-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #a8b0bd;
  font-size: 12px;
}

.goods-meta {
  min-width: 0;
}

.goods-name {
  color: #1f2d3d;
  font-weight: 600;
  line-height: 22px;
}

.goods-sub {
  color: #7a869a;
  font-size: 12px;
  line-height: 20px;
}

.detail-drawer {
  padding: 0 22px 24px;
}

.detail-header {
  display: flex;
  align-items: center;
  padding: 4px 0 18px;
}

.detail-cover {
  width: 96px;
  height: 96px;
  margin-right: 16px;
  border-radius: 14px;
  overflow: hidden;
  border: 1px solid #e8edf5;
  background: #f6f8fb;
}

.detail-title {
  color: #1f2d3d;
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 8px;
}

.detail-sub {
  color: #7a869a;
  margin-bottom: 10px;
}

.detail-section {
  margin-top: 18px;
}

.section-title {
  margin-bottom: 10px;
  font-size: 14px;
  font-weight: 600;
  color: #1f2d3d;
}

.detail-content {
  line-height: 1.8;
  color: #4e5969;
  word-break: break-word;
}
</style>
