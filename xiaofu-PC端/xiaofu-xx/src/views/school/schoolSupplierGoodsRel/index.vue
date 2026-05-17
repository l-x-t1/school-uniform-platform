<template>
  <div class="app-container school-supplier-goods-rel-page">
    <el-form
      v-show="showSearch"
      ref="queryForm"
      :model="queryParams"
      size="small"
      :inline="true"
      label-width="84px"
      class="query-form"
    >
      <el-form-item label="合作关系" prop="relId">
        <el-select
          v-model="queryParams.relId"
          placeholder="请选择合作关系"
          filterable
          clearable
          style="width: 260px"
        >
          <el-option
            v-for="item in relOptions"
            :key="item.id"
            :label="formatRelOption(item)"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="商品" prop="goodsId">
        <el-select
          v-model="queryParams.goodsId"
          placeholder="请选择商品"
          filterable
          clearable
          style="width: 240px"
        >
          <el-option
            v-for="item in goodsOptions"
            :key="item.id"
            :label="formatGoodsOption(item)"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择状态"
          clearable
          style="width: 180px"
        >
          <el-option
            v-for="item in enableStatusOptions"
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

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['school:schoolSupplierGoodsRel:add']"
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['school:schoolSupplierGoodsRel:edit']"
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['school:schoolSupplierGoodsRel:remove']"
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['school:schoolSupplierGoodsRel:export']"
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
        >导出</el-button>
      </el-col>
      <right-toolbar :show-search.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table
      v-loading="loading"
      :data="schoolSupplierGoodsRelList"
      border
      class="goods-table"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="合作关系" min-width="340">
        <template slot-scope="scope">
          <div class="rel-parties-cell">
            <div class="party-row">
              <span class="party-label">学校</span>
              <span class="party-name">{{ getRelSchoolName(scope.row.relId, scope.row.schoolId) }}</span>
            </div>
            <div class="party-row">
              <span class="party-label supplier">供应商</span>
              <span class="party-name">{{ getRelSupplierName(scope.row.relId, scope.row.supplierId) }}</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="商品" min-width="240" show-overflow-tooltip>
        <template slot-scope="scope">{{ getGoodsName(scope.row.goodsId) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="110" align="center">
        <template slot-scope="scope">
          <el-tag size="mini" :type="getEnableStatusTag(scope.row.status)">
            {{ getOptionLabel(enableStatusOptions, scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" min-width="180" show-overflow-tooltip>
        <template slot-scope="scope">{{ formatValue(scope.row.remark) }}</template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="160" align="center">
        <template slot-scope="scope">{{ formatDateTime(scope.row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="190" align="center" fixed="right" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >详情</el-button>
          <el-button
            v-hasPermi="['school:schoolSupplierGoodsRel:edit']"
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            v-hasPermi="['school:schoolSupplierGoodsRel:remove']"
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
          >删除</el-button>
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

    <el-dialog
      title="合作商品详情"
      :visible.sync="detailOpen"
      width="760px"
      append-to-body
      @closed="resetDetail"
    >
      <div v-loading="detailLoading" class="dialog-scroll">
        <div class="form-section detail-section">
          <div class="section-head">
            <div class="section-title">合作关系</div>
            <div class="section-desc">该商品所属的学校与供应商合作关系</div>
          </div>
          <div class="detail-parties">
            <div class="detail-party-card school">
              <div class="detail-party-label">学校</div>
              <div class="detail-party-name">{{ getRelSchoolName(detailForm.relId, detailForm.schoolId) }}</div>
            </div>
            <i class="el-icon-right detail-arrow" />
            <div class="detail-party-card supplier">
              <div class="detail-party-label">供应商</div>
              <div class="detail-party-name">{{ getRelSupplierName(detailForm.relId, detailForm.supplierId) }}</div>
            </div>
          </div>
        </div>
        <div class="form-section detail-section">
          <div class="section-head">
            <div class="section-title">商品信息</div>
            <div class="section-desc">展示合作商品与启停状态</div>
          </div>
          <div class="detail-kv-grid">
            <div class="detail-kv">
              <span class="detail-label">商品</span>
              <span class="detail-value">{{ getGoodsName(detailForm.goodsId) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">状态</span>
              <span class="detail-value">
                <el-tag size="mini" :type="getEnableStatusTag(detailForm.status)">
                  {{ getOptionLabel(enableStatusOptions, detailForm.status) }}
                </el-tag>
              </span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">创建时间</span>
              <span class="detail-value">{{ formatDateTime(detailForm.createTime) }}</span>
            </div>
          </div>
          <div class="detail-remark">
            <div class="detail-label">备注</div>
            <div class="remark-text">{{ formatValue(detailForm.remark) }}</div>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <el-dialog
      :title="title"
      :visible.sync="open"
      width="760px"
      top="6vh"
      append-to-body
      :close-on-click-modal="false"
      class="goods-rel-edit-dialog"
    >
      <div class="dialog-scroll">
        <el-form ref="form" :model="form" :rules="rules" label-width="96px" class="goods-rel-form">
          <div class="form-section">
            <div class="section-head">
              <div class="section-title">合作关系</div>
              <div class="section-desc">选择合作关系后会自动保留学校与供应商编号</div>
            </div>
            <el-form-item label="合作关系" prop="relId">
              <el-select
                v-model="form.relId"
                filterable
                clearable
                placeholder="请选择合作关系"
                style="width: 100%"
                @change="handleRelChange"
              >
                <el-option
                  v-for="item in relOptions"
                  :key="item.id"
                  :label="formatRelOption(item)"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </div>

          <div class="form-section">
            <div class="section-head">
              <div class="section-title">商品配置</div>
              <div class="section-desc">选择合作商品并维护启停状态</div>
            </div>
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="商品" prop="goodsId">
                  <el-select
                    v-model="form.goodsId"
                    filterable
                    clearable
                    placeholder="请选择商品"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="item in goodsOptions"
                      :key="item.id"
                      :label="formatGoodsOption(item)"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="状态" prop="status">
                  <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
                    <el-option
                      v-for="item in enableStatusOptions"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="备注" prop="remark">
                  <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
                </el-form-item>
              </el-col>
            </el-row>
          </div>
        </el-form>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listSchoolSupplierGoodsRel,
  delSchoolSupplierGoodsRel,
  addSchoolSupplierGoodsRel,
  updateSchoolSupplierGoodsRel
} from '@/api/school/schoolSupplierGoodsRel'
import { listSchoolSupplierRel } from '@/api/school/schoolSupplierRel'
import { listGoods } from '@/api/school/goods'

export default {
  name: 'SchoolSupplierGoodsRel',
  data() {
    return {
      loading: true,
      ids: [],
      selectedRows: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      schoolSupplierGoodsRelList: [],
      relOptions: [],
      goodsOptions: [],
      detailOpen: false,
      detailLoading: false,
      detailForm: {},
      enableStatusOptions: [
        { value: '0', label: '停用' },
        { value: '1', label: '启用' }
      ],
      title: '',
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        relId: null,
        goodsId: null,
        status: null
      },
      form: {},
      rules: {
        relId: [
          { required: true, message: '请选择合作关系', trigger: 'change' }
        ],
        goodsId: [
          { required: true, message: '请选择商品', trigger: 'change' }
        ],
        status: [
          { required: true, message: '请选择状态', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.getRelOptions()
    this.getGoodsOptions()
    this.getList()
  },
  methods: {
    getDefaultForm() {
      return {
        id: null,
        relId: null,
        schoolId: null,
        supplierId: null,
        goodsId: null,
        status: '1',
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null,
        delFlag: '0'
      }
    },
    /** 查询学校供应商合作商品列表 */
    getList() {
      this.loading = true
      listSchoolSupplierGoodsRel(this.queryParams).then(response => {
        this.schoolSupplierGoodsRelList = response.rows || []
        this.total = response.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = this.getDefaultForm()
      this.resetForm('form')
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.selectedRows = selection
      this.single = selection.length !== 1
      this.multiple = selection.length === 0
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增学校供应商合作商品'
    },
    handleView(row) {
      const data = row && row.id ? row : this.selectedRows[0]
      if (!data || !data.id) {
        this.$modal.msgError('请选择要查看的合作商品')
        return
      }
      this.detailForm = Object.assign(this.getDefaultForm(), data)
      this.detailOpen = true
      this.detailLoading = false
    },
    resetDetail() {
      this.detailForm = {}
      this.detailLoading = false
    },
    handleUpdate(row) {
      this.reset()
      const data = row && row.id ? row : this.selectedRows[0]
      if (!data || !data.id) {
        this.$modal.msgError('请选择要修改的合作商品')
        return
      }
      this.form = Object.assign(this.getDefaultForm(), data)
      this.open = true
      this.title = '修改学校供应商合作商品'
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (!valid) {
          return
        }
        const payload = Object.assign({}, this.form, {
          delFlag: this.form.delFlag || '0'
        })
        if (payload.id != null) {
          updateSchoolSupplierGoodsRel(payload).then(() => {
            this.$modal.msgSuccess('修改成功')
            this.open = false
            this.getList()
          })
          return
        }
        addSchoolSupplierGoodsRel(payload).then(() => {
          this.$modal.msgSuccess('新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const ids = row && row.id ? row.id : this.ids.join(',')
      this.$modal.confirm('是否确认删除选中的学校供应商合作商品？').then(() => {
        return delSchoolSupplierGoodsRel(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    handleExport() {
      this.download('school/schoolSupplierGoodsRel/export', {
        ...this.queryParams
      }, `schoolSupplierGoodsRel_${new Date().getTime()}.xlsx`)
    },
    getRelOptions() {
      listSchoolSupplierRel({ pageNum: 1, pageSize: 1000 }).then(response => {
        this.relOptions = response.rows || []
      })
    },
    getGoodsOptions() {
      listGoods({ pageNum: 1, pageSize: 1000, status: '1' }).then(response => {
        this.goodsOptions = response.rows || []
      })
    },
    handleRelChange(value) {
      const option = this.relOptions.find(item => String(item.id) === String(value))
      this.form.relId = value || null
      this.form.schoolId = option ? option.schoolId : null
      this.form.supplierId = option ? option.supplierId : null
    },
    formatRelOption(item) {
      return `${this.formatValue(item.schoolName)} / ${this.formatValue(item.supplierName)}`
    },
    getRelOption(relId) {
      return this.relOptions.find(item => String(item.id) === String(relId))
    },
    getRelSchoolName(relId, fallbackId) {
      const option = this.getRelOption(relId)
      return option ? this.formatValue(option.schoolName) : this.formatFallback('学校', fallbackId)
    },
    getRelSupplierName(relId, fallbackId) {
      const option = this.getRelOption(relId)
      return option ? this.formatValue(option.supplierName) : this.formatFallback('供应商', fallbackId)
    },
    formatGoodsOption(item) {
      return item.goodsCode ? `${item.goodsName}（${item.goodsCode}）` : item.goodsName
    },
    getGoodsName(goodsId) {
      const option = this.goodsOptions.find(item => String(item.id) === String(goodsId))
      return option ? this.formatGoodsOption(option) : this.formatFallback('商品', goodsId)
    },
    getOptionLabel(options, value) {
      const option = options.find(item => item.value === String(value))
      return option ? option.label : this.formatValue(value)
    },
    getEnableStatusTag(value) {
      return String(value) === '1' ? 'success' : 'info'
    },
    formatFallback(label, value) {
      return value == null || value === '' ? '--' : `未匹配${label}（${value}）`
    },
    formatValue(value) {
      return value == null || value === '' ? '--' : value
    },
    formatDateTime(value) {
      return value ? this.parseTime(value, '{y}-{m}-{d} {h}:{i}') : '--'
    }
  }
}
</script>

<style scoped>
.query-form {
  margin-bottom: 16px;
  padding: 18px 20px 6px;
  background: #f8fafc;
  border: 1px solid #e7edf5;
  border-radius: 12px;
}

.goods-table {
  border-radius: 12px;
  overflow: hidden;
}

.rel-parties-cell {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.party-row {
  display: flex;
  align-items: center;
  min-width: 0;
}

.party-label {
  flex: 0 0 auto;
  min-width: 44px;
  margin-right: 10px;
  padding: 2px 8px;
  font-size: 12px;
  color: #1d4ed8;
  text-align: center;
  background: #eff6ff;
  border: 1px solid #bfdbfe;
  border-radius: 6px;
}

.party-label.supplier {
  color: #047857;
  background: #ecfdf5;
  border-color: #bbf7d0;
}

.party-name {
  min-width: 0;
  overflow: hidden;
  font-weight: 600;
  color: #243447;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dialog-scroll {
  max-height: 70vh;
  overflow-y: auto;
  padding-right: 8px;
}

.form-section {
  margin-bottom: 18px;
  padding: 18px 18px 6px;
  background: #f8fafc;
  border: 1px solid #e7edf5;
  border-radius: 12px;
}

.section-head {
  margin-bottom: 16px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #243447;
}

.section-desc {
  margin-top: 4px;
  font-size: 12px;
  color: #7a8798;
}

.detail-section {
  padding-bottom: 18px;
}

.detail-parties {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 40px minmax(0, 1fr);
  gap: 12px;
  align-items: center;
}

.detail-party-card {
  min-width: 0;
  padding: 16px;
  background: #ffffff;
  border: 1px solid #e7edf5;
  border-radius: 10px;
}

.detail-party-card.school {
  border-left: 4px solid #3b82f6;
}

.detail-party-card.supplier {
  border-left: 4px solid #10b981;
}

.detail-party-label {
  margin-bottom: 8px;
  font-size: 12px;
  color: #7a8798;
}

.detail-party-name {
  overflow: hidden;
  font-size: 16px;
  font-weight: 600;
  color: #1f2d3d;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.detail-arrow {
  font-size: 22px;
  color: #8aa0b8;
  text-align: center;
}

.detail-kv-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 120px 160px;
  gap: 12px;
}

.detail-kv {
  min-width: 0;
  padding: 12px 14px;
  background: #ffffff;
  border: 1px solid #e7edf5;
  border-radius: 8px;
}

.detail-label {
  display: block;
  margin-bottom: 6px;
  font-size: 12px;
  color: #7a8798;
}

.detail-value {
  display: block;
  overflow: hidden;
  font-size: 14px;
  color: #243447;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.detail-remark {
  margin-top: 16px;
  padding: 12px 14px;
  color: #243447;
  background: #ffffff;
  border: 1px solid #e7edf5;
  border-radius: 8px;
}

.remark-text {
  line-height: 1.6;
  white-space: pre-wrap;
}

@media (max-width: 768px) {
  .detail-parties,
  .detail-kv-grid {
    grid-template-columns: 1fr;
  }

  .detail-arrow {
    display: none;
  }
}
</style>
