<template>
  <div class="app-container group-notice-page">
    <el-form
      ref="queryForm"
      :model="queryParams"
      size="small"
      :inline="true"
      label-width="82px"
      class="query-form"
    >
      <el-form-item label="公告标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入公告标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="公告编号" prop="noticeCode">
        <el-input
          v-model="queryParams.noticeCode"
          placeholder="请输入公告编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item v-if="isAdmin" label="所属学校" prop="schoolId">
        <el-select
          v-model="queryParams.schoolId"
          placeholder="请选择学校"
          clearable
          filterable
        >
          <el-option
            v-for="school in schoolOptions"
            :key="school.id"
            :label="school.schoolName"
            :value="school.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item v-else label="所属学校">
        <el-input :value="currentSchool.schoolName || '-'" disabled />
      </el-form-item>
      <el-form-item label="公告类型" prop="noticeType">
        <el-select v-model="queryParams.noticeType" placeholder="请选择公告类型" clearable>
          <el-option
            v-for="item in noticeTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="公告状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择公告状态" clearable>
          <el-option
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">
          搜索
        </el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['school:groupNotice:add']"
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >
          新增公告
        </el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="groupNoticeList" border stripe>
      <el-table-column label="公告编号" prop="noticeCode" min-width="140" />
      <el-table-column label="公告标题" prop="title" min-width="220" show-overflow-tooltip />
      <el-table-column label="学校名称" prop="schoolName" min-width="180" show-overflow-tooltip />
      <el-table-column label="公告类型" min-width="110">
        <template slot-scope="scope">
          <el-tag size="mini" :type="scope.row.noticeType === '2' ? 'success' : 'primary'">
            {{ formatOption(noticeTypeOptions, scope.row.noticeType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="公告状态" min-width="110">
        <template slot-scope="scope">
          <el-tag size="mini" :type="getStatusTagType(scope.row.status)">
            {{ formatOption(statusOptions, scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="报名状态" min-width="110">
        <template slot-scope="scope">
          {{ formatOption(applyStatusOptions, scope.row.applyStatus) }}
        </template>
      </el-table-column>
      <el-table-column label="公告开始时间" min-width="120">
        <template slot-scope="scope">{{ formatDate(scope.row.startTime) }}</template>
      </el-table-column>
      <el-table-column label="公告截止时间" min-width="120">
        <template slot-scope="scope">{{ formatDate(scope.row.endTime) }}</template>
      </el-table-column>
      <el-table-column label="统购时间" min-width="260">
        <template slot-scope="scope">
          {{ formatDateTimeRange(scope.row.purchaseStartTime, scope.row.purchaseEndTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="220" fixed="right">
        <template slot-scope="scope">
          <el-button
            v-hasPermi="['school:groupNotice:query']"
            type="text"
            size="mini"
            @click="handleView(scope.row)"
          >
            详情
          </el-button>
          <el-button
            v-hasPermi="['school:groupNotice:edit']"
            type="text"
            size="mini"
            @click="handleUpdate(scope.row)"
          >
            修改
          </el-button>
          <el-button
            v-hasPermi="['school:groupNotice:remove']"
            type="text"
            size="mini"
            @click="handleDelete(scope.row)"
          >
            删除
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

    <el-dialog :title="dialogTitle" :visible.sync="open" width="920px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="96px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="所属学校" prop="schoolId">
              <el-select
                v-if="isAdmin"
                v-model="form.schoolId"
                placeholder="请选择学校"
                filterable
                clearable
                @change="handleSchoolChange"
              >
                <el-option
                  v-for="school in schoolOptions"
                  :key="school.id"
                  :label="school.schoolName"
                  :value="school.id"
                />
              </el-select>
              <el-input v-else :value="currentSchool.schoolName || '-'" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="发布供应商" prop="supplierIds">
              <el-select
                v-model="form.supplierIds"
                multiple
                filterable
                clearable
                collapse-tags
                placeholder="请选择要接收公告的合作供应商"
                style="width: 100%"
                @change="handleSupplierChange"
              >
                <el-option
                  v-for="supplier in supplierOptions"
                  :key="supplier.supplierId"
                  :label="formatSupplierOption(supplier)"
                  :value="supplier.supplierId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公告编号" prop="noticeCode">
              <el-input v-model="form.noticeCode" placeholder="可自动生成或手动填写" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公告标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入公告标题" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公告类型" prop="noticeType">
              <el-select v-model="form.noticeType" placeholder="请选择公告类型">
                <el-option
                  v-for="item in noticeTypeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公告开始" prop="startTime">
              <el-date-picker
                v-model="form.startTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择开始时间"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公告截止" prop="endTime">
              <el-date-picker
                v-model="form.endTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择截止时间"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="统购时间" prop="purchaseTimeRange">
              <el-date-picker
                v-model="form.purchaseTimeRange"
                type="datetimerange"
                value-format="yyyy-MM-dd HH:mm:ss"
                range-separator="至"
                start-placeholder="统购开始时间"
                end-placeholder="统购结束时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公告状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择公告状态">
                <el-option
                  v-for="item in statusOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="报名状态" prop="applyStatus">
              <el-select v-model="form.applyStatus" placeholder="请选择报名状态">
                <el-option
                  v-for="item in applyStatusOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="适用年级" prop="gradeNames">
              <el-input v-model="form.gradeNames" placeholder="请输入适用年级" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="公告正文" prop="content">
              <el-input
                v-model="form.content"
                type="textarea"
                :rows="5"
                placeholder="请输入公告正文"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关联商品">
              <el-select
                :value="getSelectedGoodsIds()"
                multiple
                filterable
                clearable
                collapse-tags
                placeholder="请选择该公告可下单的合作商品"
                style="width: 100%"
                @change="handleNoticeGoodsChange"
              >
                <el-option
                  v-for="goods in goodsOptions"
                  :key="goods.id"
                  :label="formatGoodsOption(goods)"
                  :value="goods.id"
                />
              </el-select>
              <el-card
                v-if="form.xfptGroupNoticeItemList && form.xfptGroupNoticeItemList.length"
                shadow="never"
                class="goods-config-card"
              >
                <el-table
                  :data="form.xfptGroupNoticeItemList"
                  size="mini"
                  border
                >
                  <el-table-column label="商品" min-width="240">
                    <template slot-scope="scope">
                      <div class="goods-detail-cell">
                        <div>{{ scope.row.goodsName || '-' }}</div>
                      </div>
                    </template>
                  </el-table-column>
                  <el-table-column label="尺码与数量" min-width="360">
                    <template slot-scope="scope">
                      <div class="size-config-cell">
                        <el-select
                          v-if="hasGoodsSizeOptions(scope.row.goodsId)"
                          v-model="scope.row.selectedSizeIds"
                          multiple
                          clearable
                          collapse-tags
                          size="mini"
                          placeholder="请选择商品尺码"
                          style="width: 100%"
                          @change="handleNoticeItemSizeChange(scope.row)"
                        >
                          <el-option
                            v-for="size in getGoodsSizeOptions(scope.row.goodsId)"
                            :key="size.id"
                            :label="getGoodsSizeLabel(size)"
                            :value="size.id"
                          />
                        </el-select>
                        <div v-if="scope.row.sizeConfigs && scope.row.sizeConfigs.length" class="size-config-list">
                          <div
                            v-for="size in scope.row.sizeConfigs"
                            :key="size.id"
                            class="size-config-row"
                          >
                            <span class="size-config-name">{{ getSizeConfigLabel(size) }}</span>
                            <el-input-number
                              v-model="size.buyNum"
                              :min="1"
                              :max="99999"
                              controls-position="right"
                              size="mini"
                              @change="handleNoticeItemSizeQtyChange(scope.row)"
                            />
                          </div>
                        </div>
                        <div
                          v-else-if="hasGoodsSizeOptions(scope.row.goodsId)"
                          class="size-config-empty"
                        >
                          请选择尺码并填写每个尺码的数量
                        </div>
                        <div v-else class="size-config-empty">
                          当前商品未配置尺码，可直接填写采购总数量
                        </div>
                      </div>
                    </template>
                  </el-table-column>
                  <el-table-column label="采购总数量" width="120">
                    <template slot-scope="scope">
                      <el-input-number
                        v-if="!hasGoodsSizeOptions(scope.row.goodsId)"
                        v-model="scope.row.buyNum"
                        :min="1"
                        :max="99999"
                        controls-position="right"
                        size="mini"
                      />
                      <span v-else>{{ scope.row.buyNum || 0 }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="适用年级" min-width="180">
                    <template slot-scope="scope">
                      <el-input
                        v-model="scope.row.gradeNames"
                        size="mini"
                        placeholder="默认沿用公告适用年级"
                      />
                    </template>
                  </el-table-column>
                  <el-table-column label="价格说明" min-width="220">
                    <template slot-scope="scope">
                      <el-input
                        v-model="scope.row.priceDesc"
                        size="mini"
                        placeholder="默认自动带出统购价"
                      />
                    </template>
                  </el-table-column>
                </el-table>
              </el-card>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确定</el-button>
        <el-button @click="open = false">取消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="公告详情" :visible.sync="detailOpen" width="980px" append-to-body>
      <div v-loading="detailLoading">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="公告标题">{{ detail.title || '-' }}</el-descriptions-item>
          <el-descriptions-item label="公告编号">{{ detail.noticeCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="学校名称">{{ detail.schoolName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="公告类型">
            {{ formatOption(noticeTypeOptions, detail.noticeType) }}
          </el-descriptions-item>
          <el-descriptions-item label="公告状态">
            {{ formatOption(statusOptions, detail.status) }}
          </el-descriptions-item>
          <el-descriptions-item label="报名状态">
            {{ formatOption(applyStatusOptions, detail.applyStatus) }}
          </el-descriptions-item>
          <el-descriptions-item label="公告开始时间">{{ formatDate(detail.startTime) }}</el-descriptions-item>
          <el-descriptions-item label="公告截止时间">{{ formatDate(detail.endTime) }}</el-descriptions-item>
          <el-descriptions-item label="统购开始时间">{{ formatDateTime(detail.purchaseStartTime) }}</el-descriptions-item>
          <el-descriptions-item label="统购结束时间">{{ formatDateTime(detail.purchaseEndTime) }}</el-descriptions-item>
          <el-descriptions-item label="适用年级">{{ detail.gradeNames || '-' }}</el-descriptions-item>
          <el-descriptions-item label="发布时间">{{ formatDate(detail.publishTime) }}</el-descriptions-item>
        </el-descriptions>
        <el-card shadow="never" class="detail-section">
          <div slot="header">发布供应商</div>
          <div class="notice-content">{{ (detail.supplierIds || []).length ? (detail.supplierIds || []).join('、') : '暂无发布供应商' }}</div>
        </el-card>
        <el-card shadow="never" class="detail-section">
          <div slot="header">关联商品</div>
          <div v-if="detail.xfptGroupNoticeItemList && detail.xfptGroupNoticeItemList.length">
            <el-table :data="detail.xfptGroupNoticeItemList" size="mini" border>
              <el-table-column label="商品名称" prop="goodsName" min-width="220" />
              <el-table-column label="供应商" prop="supplierName" min-width="180" />
              <el-table-column label="尺码与数量" min-width="220">
                <template slot-scope="scope">
                  {{ formatSizeRuleJson(scope.row.sizeRuleJson) }}
                </template>
              </el-table-column>
              <el-table-column label="采购数量" prop="buyNum" width="100" />
              <el-table-column label="价格说明" prop="priceDesc" min-width="180" />
            </el-table>
          </div>
          <div v-else class="notice-content">暂无关联商品</div>
        </el-card>
        <el-card shadow="never" class="detail-section">
          <div slot="header">公告正文</div>
          <div class="notice-content">{{ detail.content || '暂无公告正文' }}</div>
        </el-card>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listGroupNotice,
  getGroupNotice,
  listGroupNoticeGoodsOptions,
  listGroupNoticeGoodsSizeOptions,
  listGroupNoticeSupplierOptions,
  addGroupNotice,
  updateGroupNotice,
  delGroupNotice
} from '@/api/school/groupNotice'
import {
  listSchoolMessage,
  getSchoolMessageByDeptId
} from '@/api/school/schoolMessage'

const getDefaultForm = () => ({
  id: null,
  schoolId: null,
  deptId: null,
  schoolName: null,
  noticeCode: null,
  title: null,
  noticeType: '1',
  content: null,
  gradeIds: null,
  gradeNames: null,
  startTime: null,
  endTime: null,
  purchaseStartTime: null,
  purchaseEndTime: null,
  purchaseTimeRange: [],
  status: '0',
  applyStatus: '0',
  supplierIds: [],
  xfptGroupNoticeItemList: []
})

export default {
  name: 'GroupNotice',
  data() {
    return {
      loading: false,
      detailLoading: false,
      total: 0,
      groupNoticeList: [],
      schoolOptions: [],
      supplierOptions: [],
      goodsOptions: [],
      goodsSizeOptions: [],
      currentSchool: {},
      open: false,
      detailOpen: false,
      dialogTitle: '新增公告',
      form: getDefaultForm(),
      detail: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        noticeCode: null,
        schoolId: null,
        noticeType: null,
        status: null
      },
      noticeTypeOptions: [
        { label: '选购公告', value: '1' },
        { label: '结果公告', value: '2' }
      ],
      statusOptions: [
        { label: '草稿', value: '0' },
        { label: '已发布', value: '1' },
        { label: '已结束', value: '2' },
        { label: '已撤回', value: '3' }
      ],
      applyStatusOptions: [
        { label: '未开始', value: '0' },
        { label: '报名中', value: '1' },
        { label: '报名结束', value: '2' }
      ],
      rules: {
        schoolId: [{ required: true, message: '请选择所属学校', trigger: 'change' }],
        title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
        noticeType: [{ required: true, message: '请选择公告类型', trigger: 'change' }],
        status: [{ required: true, message: '请选择公告状态', trigger: 'change' }],
        applyStatus: [{ required: true, message: '请选择报名状态', trigger: 'change' }],
        purchaseTimeRange: [{ required: true, message: '请选择统购时间范围', trigger: 'change' }],
        content: [{ required: true, message: '请输入公告正文', trigger: 'blur' }],
        supplierIds: [{ required: true, message: '请选择发布供应商', trigger: 'change' }]
      }
    }
  },
  computed: {
    isAdmin() {
      const roles = this.$store.getters.roles || []
      return roles.includes('admin')
    }
  },
  created() {
    this.loadSchoolContext()
    this.getList()
  },
  methods: {
    loadSchoolContext() {
      if (this.isAdmin) {
        listSchoolMessage({ pageNum: 1, pageSize: 1000 }).then((res) => {
          this.schoolOptions = res.rows || []
        })
        return
      }
      const deptId = this.$store.getters.deptId
      if (!deptId) {
        return
      }
      getSchoolMessageByDeptId(deptId).then((res) => {
        this.currentSchool = res.data || {}
        this.form.schoolId = this.currentSchool.id || null
        this.form.deptId = this.currentSchool.deptId || null
        this.form.schoolName = this.currentSchool.schoolName || null
        this.loadNoticeSupplierOptions()
      })
    },
    loadNoticeSupplierOptions() {
      const schoolId = this.isAdmin ? this.form.schoolId : this.currentSchool.id
      if (!schoolId) {
        this.supplierOptions = []
        this.goodsOptions = []
        return Promise.resolve([])
      }
      return listGroupNoticeSupplierOptions({ schoolId }).then((res) => {
        this.supplierOptions = res.data || []
        return this.supplierOptions
      })
    },
    loadGoodsSizeOptions() {
      const schoolId = this.isAdmin ? this.form.schoolId : this.currentSchool.id
      const supplierIds = Array.isArray(this.form.supplierIds) ? this.form.supplierIds : []
      if (!schoolId || !supplierIds.length) {
        this.goodsSizeOptions = []
        this.syncNoticeItemSizeSelections()
        return Promise.resolve([])
      }
      return listGroupNoticeGoodsSizeOptions({
        schoolId,
        supplierIds: supplierIds.join(',')
      }).then((res) => {
        const rows = Array.isArray(res.rows) ? res.rows : []
        this.goodsSizeOptions = rows
          .filter((item) => item && item.goodsId != null)
          .map((item) => ({
            ...item,
            id: item.id == null ? null : Number(item.id),
            goodsId: item.goodsId == null ? null : Number(item.goodsId)
          }))
          .filter((item) => item.id != null && item.goodsId != null)
        this.syncNoticeItemSizeSelections()
        return this.goodsSizeOptions
      })
    },
    loadNoticeGoodsOptions() {
      const schoolId = this.isAdmin ? this.form.schoolId : this.currentSchool.id
      const supplierIds = Array.isArray(this.form.supplierIds) ? this.form.supplierIds : []
      if (!schoolId || !supplierIds.length) {
        this.goodsOptions = []
        this.goodsSizeOptions = []
        this.cleanupNoticeItems()
        return Promise.resolve([])
      }
      return listGroupNoticeGoodsOptions({
        schoolId,
        supplierIds: supplierIds.join(',')
      }).then((res) => {
        this.goodsOptions = Array.isArray(res.data) ? res.data : []
        this.cleanupNoticeItems()
        return this.loadGoodsSizeOptions().then(() => this.goodsOptions)
      })
    },
    getList() {
      this.loading = true
      listGroupNotice(this.queryParams)
        .then((response) => {
          this.groupNoticeList = response.rows || []
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
    resetFormData() {
      this.form = {
        ...getDefaultForm(),
        id: null,
        schoolId: this.isAdmin ? null : this.currentSchool.id,
        deptId: this.isAdmin ? null : this.currentSchool.deptId,
        schoolName: this.isAdmin ? null : this.currentSchool.schoolName
      }
      this.goodsOptions = []
      this.goodsSizeOptions = []
      this.$refs.form && this.$refs.form.clearValidate()
    },
    handleAdd() {
      this.resetFormData()
      this.dialogTitle = '新增公告'
      this.open = true
      this.loadNoticeSupplierOptions().then(() => this.loadGoodsSizeOptions())
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
    },
    handleUpdate(row) {
      if (!row.id) {
        this.$message.error('当前公告缺少主键，无法修改')
        return
      }
      this.resetFormData()
      getGroupNotice(row.id).then((response) => {
        this.form = this.normalizeFormData(response.data || {})
        this.loadNoticeSupplierOptions().then(() => this.loadNoticeGoodsOptions())
        this.dialogTitle = '修改公告'
        this.open = true
      })
    },
    handleSchoolChange(schoolId) {
      const school = this.schoolOptions.find((item) => item.id === schoolId)
      this.form.supplierIds = []
      this.form.xfptGroupNoticeItemList = []
      if (!school) {
        this.form.deptId = null
        this.form.schoolName = null
        this.supplierOptions = []
        this.goodsOptions = []
        this.goodsSizeOptions = []
        return
      }
      this.form.deptId = school.deptId
      this.form.schoolName = school.schoolName
      this.loadNoticeSupplierOptions()
    },
    handleSupplierChange() {
      this.loadNoticeGoodsOptions()
    },
    handleNoticeGoodsChange(goodsIds) {
      const ids = Array.isArray(goodsIds) ? goodsIds : []
      const selectedRows = ids.map((id) => this.goodsOptions.find((item) => String(item.id) === String(id))).filter(Boolean)
      const oldItemMap = (this.form.xfptGroupNoticeItemList || []).reduce((map, item) => {
        map[String(item.goodsId)] = item
        return map
      }, {})
      this.form.xfptGroupNoticeItemList = selectedRows.map((goods) => {
        const existing = oldItemMap[String(goods.id)] || {}
        return {
          goodsId: goods.id,
          goodsName: goods.goodsName,
          supplierId: goods.supplierId,
          supplierName: goods.supplierName,
          goodsCode: goods.goodsCode,
          unitName: existing.unitName || null,
          buyNum: existing.buyNum || 0,
          priceDesc: existing.priceDesc || null,
          gradeIds: existing.gradeIds || this.form.gradeIds || null,
          gradeNames: existing.gradeNames || this.form.gradeNames || null,
          deptId: this.form.deptId || null,
          delFlag: existing.delFlag || '0',
          selectedSizeIds: Array.isArray(existing.selectedSizeIds)
            ? this.normalizeIdArray(existing.selectedSizeIds)
            : this.normalizeIdArray(this.parseSizeRuleJson(existing.sizeRuleJson, existing.buyNum).map((size) => size.id)),
          sizeConfigs: this.parseSizeRuleJson(existing.sizeRuleJson, existing.buyNum),
          sizeRuleJson: existing.sizeRuleJson || null
        }
      })
      this.syncNoticeItemSizeSelections()
    },
    cleanupNoticeItems() {
      const optionMap = this.goodsOptions.reduce((map, item) => {
        map[String(item.id)] = item
        return map
      }, {})
      const existingItems = Array.isArray(this.form.xfptGroupNoticeItemList)
        ? this.form.xfptGroupNoticeItemList
        : []
      this.form.xfptGroupNoticeItemList = existingItems
        .filter((item) => optionMap[String(item.goodsId)])
        .map((item) => ({
          ...item,
          goodsName: optionMap[String(item.goodsId)].goodsName,
          supplierId: optionMap[String(item.goodsId)].supplierId,
          supplierName: optionMap[String(item.goodsId)].supplierName,
          goodsCode: optionMap[String(item.goodsId)].goodsCode,
          selectedSizeIds: this.normalizeIdArray(item.selectedSizeIds)
        }))
      this.syncNoticeItemSizeSelections()
    },
    getSelectedGoodsIds() {
      return (this.form.xfptGroupNoticeItemList || [])
        .map((item) => item.goodsId)
        .filter((id) => id !== null && id !== undefined)
    },
    getGoodsSizeOptions(goodsId) {
      const currentGoodsId = goodsId == null ? null : Number(goodsId)
      return this.goodsSizeOptions.filter((item) => item.goodsId === currentGoodsId)
    },
    hasGoodsSizeOptions(goodsId) {
      return this.getGoodsSizeOptions(goodsId).length > 0
    },
    getGoodsSizeLabel(size) {
      if (!size) {
        return '-'
      }
      const labelParts = [size.sizeCode || `尺码${size.id || ''}`]
      if (size.groupPrice !== null && size.groupPrice !== undefined && size.groupPrice !== '') {
        labelParts.push(`统购价${size.groupPrice}元`)
      }
      return labelParts.join(' / ')
    },
    getSizeConfigLabel(size) {
      if (!size) {
        return '-'
      }
      const labelParts = [size.sizeCode || `尺码${size.id || ''}`]
      if (size.groupPrice !== null && size.groupPrice !== undefined && size.groupPrice !== '') {
        labelParts.push(`统购价${size.groupPrice}元`)
      }
      return labelParts.join(' / ')
    },
    handleNoticeItemSizeChange(row) {
      if (!row) {
        return
      }
      row.selectedSizeIds = this.normalizeIdArray(row.selectedSizeIds)
      row.sizeConfigs = this.buildSizeConfigsFromSelection(
        row.goodsId,
        row.selectedSizeIds,
        row.sizeConfigs,
        row.buyNum
      )
      this.syncNoticeItemDerivedFields(row)
      if (!row.priceDesc) {
        row.priceDesc = this.buildPriceDescBySelection(row.goodsId, row.selectedSizeIds)
      }
    },
    handleNoticeItemSizeQtyChange(row) {
      if (!row) {
        return
      }
      row.sizeConfigs = this.normalizeSizeConfigs(row.sizeConfigs, row.buyNum)
      this.syncNoticeItemDerivedFields(row)
    },
    syncNoticeItemSizeSelections() {
      if (!Array.isArray(this.form.xfptGroupNoticeItemList)) {
        return
      }
      this.form.xfptGroupNoticeItemList = this.form.xfptGroupNoticeItemList.map((item) => {
        const selectedSizeIds = this.resolveSelectedSizeIds(item)
        const sizeConfigs = this.buildSizeConfigsFromSelection(
          item.goodsId,
          selectedSizeIds,
          item.sizeConfigs || this.parseSizeRuleJson(item.sizeRuleJson, item.buyNum),
          item.buyNum
        )
        const buyNum = this.hasGoodsSizeOptions(item.goodsId)
          ? this.sumSizeBuyNum(sizeConfigs)
          : this.normalizePositiveInt(item.buyNum, 1)
        return {
          ...item,
          selectedSizeIds,
          sizeConfigs,
          buyNum,
          sizeRuleJson: this.buildSizeRuleJsonByConfigs(sizeConfigs) ||
            item.sizeRuleJson ||
            null
        }
      })
    },
    resolveSelectedSizeIds(item) {
      if (item && Array.isArray(item.selectedSizeIds)) {
        return this.normalizeIdArray(item.selectedSizeIds)
      }
      const sizeConfigs = Array.isArray(item && item.sizeConfigs) ? item.sizeConfigs : []
      if (sizeConfigs.length) {
        return this.normalizeIdArray(sizeConfigs.map((rule) => rule && rule.id))
      }
      const parsedRules = this.parseSizeRuleJson(item && item.sizeRuleJson, item && item.buyNum)
      return this.normalizeIdArray(parsedRules.map((rule) => rule && rule.id))
    },
    parseSizeRuleJson(sizeRuleJson, fallbackBuyNum) {
      if (!sizeRuleJson) {
        return []
      }
      try {
        const parsed = JSON.parse(sizeRuleJson)
        if (!Array.isArray(parsed)) {
          return []
        }
        const singleRuleFallback = parsed.length === 1 ? this.normalizePositiveInt(fallbackBuyNum, null) : null
        return parsed
          .map((item) => ({
            id: item && item.id != null ? Number(item.id) : null,
            sizeCode: item && item.sizeCode ? item.sizeCode : null,
            genderType: item && item.genderType ? item.genderType : null,
            measureJson: item && item.measureJson ? item.measureJson : null,
            groupPrice: item && item.groupPrice !== undefined ? item.groupPrice : null,
            retailPrice: item && item.retailPrice !== undefined ? item.retailPrice : null,
            buyNum: this.resolveSizeBuyNum(item && item.buyNum, singleRuleFallback)
          }))
          .filter((item) => item.id != null && !Number.isNaN(item.id))
      } catch (error) {
        return []
      }
    },
    normalizePositiveInt(value, defaultValue = 1) {
      const num = Number(value)
      if (!Number.isNaN(num) && num > 0) {
        return Math.floor(num)
      }
      return defaultValue
    },
    resolveSizeBuyNum(value, fallbackValue = null) {
      const resolved = this.normalizePositiveInt(value, null)
      return resolved == null ? fallbackValue : resolved
    },
    normalizeIdArray(values) {
      if (!Array.isArray(values)) {
        return []
      }
      const uniqueIds = []
      values.forEach((value) => {
        const num = Number(value)
        if (!Number.isNaN(num) && uniqueIds.indexOf(num) === -1) {
          uniqueIds.push(num)
        }
      })
      return uniqueIds
    },
    normalizeSizeConfigs(sizeConfigs) {
      if (!Array.isArray(sizeConfigs)) {
        return []
      }
      const uniqueMap = {}
      sizeConfigs.forEach((item) => {
        const id = item && item.id != null ? Number(item.id) : null
        if (id == null || Number.isNaN(id)) {
          return
        }
        uniqueMap[id] = {
          ...item,
          id,
          buyNum: this.resolveSizeBuyNum(item.buyNum, null)
        }
      })
      return Object.keys(uniqueMap)
        .map((key) => uniqueMap[key])
    },
    buildSizeConfigsFromSelection(goodsId, selectedSizeIds, existingSizeConfigs, fallbackBuyNum) {
      const selectedIds = this.normalizeIdArray(selectedSizeIds)
      const optionMap = this.getGoodsSizeOptions(goodsId).reduce((map, item) => {
        map[item.id] = item
        return map
      }, {})
      const normalizedExisting = this.normalizeSizeConfigs(existingSizeConfigs)
      const existingMap = normalizedExisting.reduce((map, item) => {
        map[item.id] = item
        return map
      }, {})
      const targetIds = selectedIds.length
        ? selectedIds
        : normalizedExisting.map((item) => item.id)
      if (!targetIds.length) {
        return []
      }
      return targetIds
        .map((id) => {
          const option = optionMap[id] || {}
          const existing = existingMap[id] || {}
          const resolvedId = option.id != null ? option.id : existing.id
          if (resolvedId == null || Number.isNaN(Number(resolvedId))) {
            return null
          }
          return {
            id: Number(resolvedId),
            sizeCode: option.sizeCode || existing.sizeCode || `尺码${resolvedId}`,
            genderType: option.genderType != null ? option.genderType : existing.genderType,
            measureJson: option.measureJson != null ? option.measureJson : existing.measureJson,
            groupPrice: option.groupPrice != null ? option.groupPrice : existing.groupPrice,
            retailPrice: option.retailPrice != null ? option.retailPrice : existing.retailPrice,
            buyNum: existing.id != null
              ? this.resolveSizeBuyNum(existing.buyNum, null)
              : this.normalizePositiveInt(fallbackBuyNum, 1)
          }
        })
        .filter(Boolean)
    },
    sumSizeBuyNum(sizeConfigs) {
      return this.normalizeSizeConfigs(sizeConfigs).reduce((total, item) => {
        return total + this.normalizePositiveInt(item.buyNum, 0)
      }, 0)
    },
    buildSizeRuleJsonBySelection(goodsId, selectedSizeIds) {
      const sizeConfigs = this.buildSizeConfigsFromSelection(goodsId, selectedSizeIds, [], 1)
      return this.buildSizeRuleJsonByConfigs(sizeConfigs)
    },
    buildSizeRuleJsonByConfigs(sizeConfigs) {
      const normalizedConfigs = this.normalizeSizeConfigs(sizeConfigs)
      if (!normalizedConfigs.length) {
        return null
      }
      const selectedRules = normalizedConfigs.map((size) => ({
        id: size.id,
        sizeCode: size.sizeCode,
        genderType: size.genderType,
        measureJson: size.measureJson,
        groupPrice: size.groupPrice,
        retailPrice: size.retailPrice,
        buyNum: this.normalizePositiveInt(size.buyNum, 1)
      }))
      return selectedRules.length ? JSON.stringify(selectedRules) : null
    },
    syncNoticeItemDerivedFields(row) {
      if (!row) {
        return
      }
      row.sizeConfigs = this.normalizeSizeConfigs(row.sizeConfigs, row.buyNum)
      row.selectedSizeIds = this.normalizeIdArray(row.sizeConfigs.map((item) => item.id))
      row.buyNum = this.hasGoodsSizeOptions(row.goodsId)
        ? this.sumSizeBuyNum(row.sizeConfigs)
        : this.normalizePositiveInt(row.buyNum, 1)
      row.sizeRuleJson = this.buildSizeRuleJsonByConfigs(row.sizeConfigs)
    },
    buildPriceDescBySelection(goodsId, selectedSizeIds) {
      const selectedIds = this.normalizeIdArray(selectedSizeIds)
      if (!selectedIds.length) {
        return null
      }
      const selectedSizes = this.getGoodsSizeOptions(goodsId)
        .filter((item) => selectedIds.indexOf(item.id) !== -1)
      const prices = selectedSizes
        .map((item) => item.groupPrice)
        .filter((value) => value !== null && value !== undefined && value !== '')
        .map((value) => Number(value))
        .filter((value) => !Number.isNaN(value))
      if (!prices.length) {
        return null
      }
      const minPrice = Math.min(...prices)
      const maxPrice = Math.max(...prices)
      if (minPrice === maxPrice) {
        return `统购价：${this.formatPriceValue(minPrice)}元`
      }
      return `统购价：${this.formatPriceValue(minPrice)}-${this.formatPriceValue(maxPrice)}元`
    },
    formatPriceValue(value) {
      const numberValue = Number(value)
      if (Number.isNaN(numberValue)) {
        return value
      }
      return Number.isInteger(numberValue) ? String(numberValue) : String(numberValue)
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          return
        }
        if (!this.form.supplierIds || !this.form.supplierIds.length) {
          this.$message.error('请选择发布供应商')
          return
        }
        if (String(this.form.noticeType || '1') !== '2' && (!this.form.xfptGroupNoticeItemList || !this.form.xfptGroupNoticeItemList.length)) {
          this.$message.error('请选择关联商品')
          return
        }
        const invalidNoticeItem = (this.form.xfptGroupNoticeItemList || []).find((item) => {
          if (this.hasGoodsSizeOptions(item.goodsId)) {
            return !item.sizeConfigs ||
              !item.sizeConfigs.length ||
              item.sizeConfigs.some((size) => this.normalizePositiveInt(size && size.buyNum, 0) <= 0)
          }
          return this.normalizePositiveInt(item.buyNum, 0) <= 0
        })
        if (invalidNoticeItem) {
          this.$message.error(`请完善商品“${invalidNoticeItem.goodsName || ''}”的尺码数量`)
          return
        }
        const payload = { ...this.form }
        payload.purchaseStartTime = payload.purchaseTimeRange[0]
        payload.purchaseEndTime = payload.purchaseTimeRange[1]
        delete payload.purchaseTimeRange
        payload.xfptGroupNoticeItemList = (this.form.xfptGroupNoticeItemList || []).map((item) => ({
          id: item.id,
          noticeId: item.noticeId,
          goodsId: item.goodsId,
          goodsName: item.goodsName,
          supplierId: item.supplierId,
          supplierName: item.supplierName,
          goodsCode: item.goodsCode,
          sizeRuleJson: this.buildSizeRuleJsonByConfigs(item.sizeConfigs),
          unitName: item.unitName,
          buyNum: this.hasGoodsSizeOptions(item.goodsId)
            ? this.sumSizeBuyNum(item.sizeConfigs)
            : this.normalizePositiveInt(item.buyNum, 1),
          priceDesc: item.priceDesc || this.buildPriceDescBySelection(item.goodsId, item.selectedSizeIds),
          gradeIds: item.gradeIds || this.form.gradeIds || null,
          gradeNames: item.gradeNames || this.form.gradeNames || null,
          deptId: this.form.deptId,
          remark: item.remark,
          delFlag: item.delFlag || '0'
        }))
        if (!this.isAdmin) {
          payload.schoolId = this.currentSchool.id
          payload.deptId = this.currentSchool.deptId
          payload.schoolName = this.currentSchool.schoolName
        }
        const request = payload.id ? updateGroupNotice(payload) : addGroupNotice(payload)
        request.then(() => {
          this.$modal.msgSuccess(payload.id ? '修改成功' : '新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      if (!row.id) {
        this.$message.error('当前公告缺少主键，无法删除')
        return
      }
      this.$modal
        .confirm(`是否确认删除公告编号为“${row.noticeCode || row.id}”的数据项？`)
        .then(() => delGroupNotice(row.id))
        .then(() => {
          this.getList()
          this.$modal.msgSuccess('删除成功')
        })
        .catch(() => {})
    },
    handleView(row) {
      if (!row.id) {
        this.$message.error('当前公告缺少主键，无法查看详情')
        return
      }
      this.detailOpen = true
      this.detailLoading = true
      getGroupNotice(row.id)
        .then((response) => {
          this.detail = response.data || {}
        })
        .finally(() => {
          this.detailLoading = false
        })
    },
    normalizeFormData(data) {
      const form = {
        ...getDefaultForm(),
        ...data
      }
      form.startTime = this.normalizeDateValue(form.startTime)
      form.endTime = this.normalizeDateValue(form.endTime)
      form.purchaseStartTime = this.normalizeDateTimeValue(form.purchaseStartTime)
      form.purchaseEndTime = this.normalizeDateTimeValue(form.purchaseEndTime)
      form.purchaseTimeRange = form.purchaseStartTime && form.purchaseEndTime
        ? [form.purchaseStartTime, form.purchaseEndTime]
        : []
      form.noticeType = form.noticeType ? String(form.noticeType) : '1'
      form.status = form.status ? String(form.status) : '0'
      form.applyStatus = form.applyStatus ? String(form.applyStatus) : '0'
      form.supplierIds = Array.isArray(form.supplierIds)
        ? form.supplierIds.map((id) => Number(id)).filter((id) => !Number.isNaN(id))
        : []
      form.xfptGroupNoticeItemList = Array.isArray(form.xfptGroupNoticeItemList)
        ? form.xfptGroupNoticeItemList.map((item) => {
          const goodsId = Number(item.goodsId)
          const supplierId = item.supplierId == null ? null : Number(item.supplierId)
          const rawBuyNum = item.buyNum == null ? 1 : Number(item.buyNum)
          const sizeConfigs = this.parseSizeRuleJson(item.sizeRuleJson, rawBuyNum)
          return {
            ...item,
            goodsId,
            supplierId,
            sizeConfigs,
            buyNum: sizeConfigs.length ? this.sumSizeBuyNum(sizeConfigs) : rawBuyNum,
            selectedSizeIds: this.normalizeIdArray(sizeConfigs.map((size) => size.id))
          }
        }).filter((item) => !Number.isNaN(item.goodsId))
        : []
      return form
    },
    normalizeDateValue(value) {
      if (!value) {
        return null
      }
      return String(value).slice(0, 10)
    },
    normalizeDateTimeValue(value) {
      if (!value) {
        return null
      }
      const text = String(value).replace('T', ' ')
      return text.length >= 19 ? text.slice(0, 19) : text
    },
    formatOption(options, value) {
      const match = options.find((item) => String(item.value) === String(value))
      return match ? match.label : value || '-'
    },
    getStatusTagType(value) {
      const map = {
        0: 'info',
        1: 'success',
        2: 'warning',
        3: 'danger'
      }
      return map[value] || 'info'
    },
    formatDate(value) {
      if (!value) {
        return '-'
      }
      return this.parseTime(value, '{y}-{m}-{d}')
    },
    formatDateTime(value) {
      if (!value) {
        return '-'
      }
      return this.parseTime(value, '{y}-{m}-{d} {h}:{i}:{s}')
    },
    formatDateTimeRange(start, end) {
      if (!start && !end) {
        return '-'
      }
      return `${this.formatDateTime(start)} ~ ${this.formatDateTime(end)}`
    },
    formatSupplierOption(item) {
      if (!item) {
        return '-'
      }
      return item.supplierName || `供应商${item.supplierId || ''}`
    },
    formatGoodsOption(item) {
      if (!item) {
        return '-'
      }
      const code = item.goodsCode ? `（${item.goodsCode}）` : ''
      return `${item.goodsName || '未命名商品'}${code}`
    },
    formatSizeRuleJson(sizeRuleJson) {
      const rules = this.parseSizeRuleJson(sizeRuleJson)
      if (!rules.length) {
        return '-'
      }
      return rules
        .map((item) => {
          if (!item) {
            return null
          }
          const sizeName = item.sizeCode || `尺码${item.id || ''}`
          const buyNum = this.normalizePositiveInt(item.buyNum, 0)
          return buyNum > 0 ? `${sizeName} x ${buyNum}` : sizeName
        })
        .filter((item) => item)
        .join('、') || '-'
    }
  }
}
</script>

<style lang="scss" scoped>
.group-notice-page {
  .query-form {
    padding: 18px 20px 2px;
    margin-bottom: 16px;
    background: #fff;
    border: 1px solid #ebeef5;
    border-radius: 4px;
  }

  .detail-section {
    margin-top: 16px;
  }

  .notice-content {
    min-height: 80px;
    color: #303133;
    line-height: 1.8;
    white-space: pre-wrap;
    word-break: break-word;
  }

  .goods-config-card {
    margin-top: 12px;
    border: 1px solid #ebeef5;
  }

  .goods-detail-cell {
    line-height: 1.6;
  }

  .size-config-cell {
    .size-config-list {
      margin-top: 8px;
    }

    .size-config-row {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 6px 0;
      border-bottom: 1px dashed #ebeef5;
      gap: 12px;
    }

    .size-config-row:last-child {
      border-bottom: none;
      padding-bottom: 0;
    }

    .size-config-name {
      flex: 1;
      color: #606266;
      line-height: 1.5;
      word-break: break-word;
    }

    .size-config-empty {
      margin-top: 8px;
      color: #909399;
      line-height: 1.5;
    }
  }
}
</style>
