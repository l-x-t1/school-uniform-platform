<template>
  <div class="app-container delivery-order-page">
    <el-card v-show="showSearch" class="search-card" shadow="never">
      <el-form
        ref="queryForm"
        :model="queryParams"
        size="small"
        :inline="true"
        label-width="86px"
      >
        <el-form-item label="发货单号" prop="deliveryNo">
          <el-input
            v-model="queryParams.deliveryNo"
            placeholder="请输入发货单号"
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
        <el-form-item label="收货人" prop="receiverName">
          <el-input
            v-model="queryParams.receiverName"
            placeholder="请输入收货人姓名"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="物流单号" prop="logisticsNo">
          <el-input
            v-model="queryParams.logisticsNo"
            placeholder="请输入物流单号"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="物流公司" prop="logisticsCompany">
          <el-input
            v-model="queryParams.logisticsCompany"
            placeholder="请输入物流公司"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="发货状态" prop="deliveryStatus">
          <el-select
            v-model="queryParams.deliveryStatus"
            placeholder="请选择发货状态"
            clearable
          >
            <el-option label="待发货" value="0" />
            <el-option label="已发货" value="1" />
            <el-option label="已签收" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
          <el-button
            v-hasPermi="['school:deliveryOrder:add']"
            type="primary"
            plain
            icon="el-icon-plus"
            size="mini"
            @click="handleAdd"
          >
            新增
          </el-button>
          <el-button
            v-hasPermi="['school:deliveryOrder:export']"
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

    <el-card class="table-card" shadow="never">
      <div slot="header" class="table-header">
        <div>
          <div class="table-title">发货单处理</div>
          <div class="table-desc">查询统购和零售发货单，查看发货详情，并维护发货状态。</div>
        </div>
        <right-toolbar :show-search.sync="showSearch" @queryTable="getList" />
      </div>

      <el-tabs v-model="activeOrderType" class="order-type-tabs" @tab-click="handleOrderTypeTabClick">
        <el-tab-pane label="统购" name="1" />
        <el-tab-pane label="零售" name="2" />
      </el-tabs>

      <el-table
        v-loading="loading"
        :data="deliveryOrderList"
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column label="发货单号" align="center" prop="deliveryNo" min-width="150" show-overflow-tooltip />
        <el-table-column label="订单编号" align="center" prop="orderNo" min-width="150" show-overflow-tooltip />
        <el-table-column label="订单类型" align="center" prop="orderType" width="100">
          <template slot-scope="scope">
            <el-tag :type="orderTypeTagType(scope.row.orderType)" size="mini">
              {{ formatOrderType(scope.row.orderType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="学校名称" align="center" prop="schoolName" min-width="160" show-overflow-tooltip>
          <template slot-scope="scope">
            <span>{{ formatValue(scope.row.schoolName) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="供应商名称" align="center" prop="supplierName" min-width="180" show-overflow-tooltip>
          <template slot-scope="scope">
            <span>{{ formatValue(scope.row.supplierName) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="收货人" align="center" prop="receiverName" min-width="120" />
        <el-table-column label="物流公司" align="center" prop="logisticsCompany" min-width="130" show-overflow-tooltip />
        <el-table-column label="物流单号" align="center" prop="logisticsNo" min-width="150" show-overflow-tooltip />
        <el-table-column label="运费" align="center" prop="carriageFee" width="100">
          <template slot-scope="scope">
            <span>{{ formatAmount(scope.row.carriageFee) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="发货状态" align="center" prop="deliveryStatus" width="100">
          <template slot-scope="scope">
            <el-tag :type="deliveryStatusTagType(scope.row.deliveryStatus)" size="mini">
              {{ formatDeliveryStatus(scope.row.deliveryStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发货时间" align="center" prop="deliveryTime" width="140">
          <template slot-scope="scope">
            <span>{{ formatDate(scope.row.deliveryTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="签收时间" align="center" prop="signTime" width="140">
          <template slot-scope="scope">
            <span>{{ formatDate(scope.row.signTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" fixed="right" width="170">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-view"
              @click="handleView(scope.row)"
            >
              详情
            </el-button>
            <el-button
              v-if="scope.row.deliveryStatus === '0' || scope.row.deliveryStatus === 0"
              v-hasPermi="['school:deliveryOrder:edit']"
              size="mini"
              type="text"
              icon="el-icon-truck"
              @click="handleShip(scope.row)"
            >
              发货
            </el-button>
            <span v-else class="muted-action">{{ formatDeliveryStatus(scope.row.deliveryStatus) }}</span>
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

    <el-dialog
      title="发货单详情"
      :visible.sync="detailOpen"
      width="1080px"
      append-to-body
    >
      <div v-loading="detailLoading" class="detail-wrap">
        <el-descriptions :column="2" border class="detail-descriptions">
          <el-descriptions-item label="发货单号">{{ formatValue(detailForm.deliveryNo) }}</el-descriptions-item>
          <el-descriptions-item label="订单编号">{{ formatValue(detailForm.orderNo) }}</el-descriptions-item>
          <el-descriptions-item label="订单类型">
            <el-tag :type="orderTypeTagType(detailForm.orderType)" size="mini">
              {{ formatOrderType(detailForm.orderType) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="发货状态">
            <el-tag :type="deliveryStatusTagType(detailForm.deliveryStatus)" size="mini">
              {{ formatDeliveryStatus(detailForm.deliveryStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="学校名称">{{ formatValue(detailForm.schoolName) }}</el-descriptions-item>
          <el-descriptions-item label="供应商名称">{{ formatValue(detailForm.supplierName) }}</el-descriptions-item>
          <el-descriptions-item label="收货人">{{ formatValue(detailForm.receiverName) }}</el-descriptions-item>
          <el-descriptions-item label="收货电话">{{ formatValue(detailForm.receiverPhone) }}</el-descriptions-item>
          <el-descriptions-item label="物流公司">{{ formatValue(detailForm.logisticsCompany) }}</el-descriptions-item>
          <el-descriptions-item label="物流单号">{{ formatValue(detailForm.logisticsNo) }}</el-descriptions-item>
          <el-descriptions-item label="运费">{{ formatAmount(detailForm.carriageFee) }}</el-descriptions-item>
          <el-descriptions-item label="发货时间">{{ formatDate(detailForm.deliveryTime) }}</el-descriptions-item>
          <el-descriptions-item label="签收时间">{{ formatDate(detailForm.signTime) }}</el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2">{{ formatValue(detailForm.receiverAddress) }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ formatValue(detailForm.remark) }}</el-descriptions-item>
        </el-descriptions>

        <div class="detail-section">
          <div class="section-title">发货明细</div>
          <el-table
            v-if="detailItemList.length"
            :data="detailItemList"
            border
            size="small"
          >
            <el-table-column label="商品名称" prop="goodsName" min-width="180" show-overflow-tooltip />
            <el-table-column label="尺码名称" prop="sizeName" min-width="120" show-overflow-tooltip />
            <el-table-column label="发货数量" prop="deliveryNum" width="100" align="center" />
            <el-table-column label="签收数量" prop="signNum" width="100" align="center" />
            <el-table-column label="状态" width="110" align="center">
              <template slot-scope="scope">
                <el-tag :type="deliveryItemStatusTagType(scope.row)" size="mini">
                  {{ formatDeliveryItemStatus(scope.row) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="备注" prop="remark" min-width="200" show-overflow-tooltip>
              <template slot-scope="scope">
                <span>{{ formatValue(scope.row.remark) }}</span>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-else description="暂无发货明细" :image-size="90" />
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button size="small" @click="detailOpen = false">关闭</el-button>
      </div>
    </el-dialog>

    <el-dialog
      title="填写发货信息"
      :visible.sync="shipOpen"
      width="520px"
      append-to-body
      @close="resetShipForm"
    >
      <el-form ref="shipForm" :model="shipForm" :rules="shipRules" label-width="90px" size="small">
        <el-form-item label="发货单号">
          <el-input v-model="shipForm.deliveryNo" disabled />
        </el-form-item>
        <el-form-item label="物流公司" prop="logisticsCompany">
          <el-input v-model="shipForm.logisticsCompany" placeholder="请输入物流公司" clearable />
        </el-form-item>
        <el-form-item label="物流单号" prop="logisticsNo">
          <el-input v-model="shipForm.logisticsNo" placeholder="请输入物流单号" clearable />
        </el-form-item>
        <el-form-item label="运费" prop="carriageFee">
          <el-input v-model="shipForm.carriageFee" placeholder="请输入运费" clearable />
        </el-form-item>
        <el-form-item label="发货时间" prop="deliveryTime">
          <el-date-picker
            v-model="shipForm.deliveryTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择发货时间"
            clearable
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" size="small" @click="submitShip">确定</el-button>
        <el-button size="small" @click="shipOpen = false">取消</el-button>
      </div>
    </el-dialog>

    <el-dialog
      :title="dialogTitle"
      :visible.sync="open"
      width="1080px"
      append-to-body
      :close-on-click-modal="false"
      @close="resetFormDialog"
    >
      <div class="dialog-scroll">
        <el-form ref="form" :model="form" :rules="rules" label-width="98px" size="small">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="发货单号" prop="deliveryNo">
                <el-input v-model="form.deliveryNo" placeholder="请输入发货单号" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="订单编号">
                <el-input v-model="form.orderNo" placeholder="自动带出" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="订单类型" prop="orderType">
                <el-select v-model="form.orderType" placeholder="请选择订单类型" style="width: 100%">
                  <el-option label="统购" value="1" />
                  <el-option label="零售" value="2" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="发货状态" prop="deliveryStatus">
                <el-select v-model="form.deliveryStatus" placeholder="请选择发货状态" style="width: 100%">
                  <el-option label="待发货" value="0" />
                  <el-option label="已发货" value="1" />
                  <el-option label="已签收" value="2" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="学校名称">
                <el-input v-model="form.schoolName" placeholder="自动带出" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="供应商名称">
                <el-input v-model="form.supplierName" placeholder="自动带出" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="收货人" prop="receiverName">
                <el-input v-model="form.receiverName" placeholder="请输入收货人" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="收货电话" prop="receiverPhone">
                <el-input v-model="form.receiverPhone" placeholder="请输入收货电话" />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="收货地址" prop="receiverAddress">
                <el-input v-model="form.receiverAddress" type="textarea" :rows="2" placeholder="请输入收货地址" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="物流公司">
                <el-input v-model="form.logisticsCompany" placeholder="请输入物流公司" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="物流单号">
                <el-input v-model="form.logisticsNo" placeholder="请输入物流单号" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="运费">
                <el-input v-model="form.carriageFee" placeholder="请输入运费" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="发货时间">
                <el-date-picker
                  v-model="form.deliveryTime"
                  type="date"
                  value-format="yyyy-MM-dd"
                  placeholder="请选择发货时间"
                  clearable
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="签收时间">
                <el-date-picker
                  v-model="form.signTime"
                  type="date"
                  value-format="yyyy-MM-dd"
                  placeholder="请选择签收时间"
                  clearable
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="备注">
                <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
              </el-form-item>
            </el-col>
          </el-row>

          <div class="detail-section">
            <div class="section-title">发货明细</div>
            <el-table :data="form.xfptDeliveryOrderItemList" border size="small">
              <el-table-column label="商品名称" prop="goodsName" min-width="180" show-overflow-tooltip />
              <el-table-column label="尺码名称" prop="sizeName" min-width="120" show-overflow-tooltip />
              <el-table-column label="发货数量" min-width="120">
                <template slot-scope="scope">
                  <el-input-number v-model="scope.row.deliveryNum" :min="0" :controls="false" style="width: 100%" />
                </template>
              </el-table-column>
              <el-table-column label="签收数量" min-width="120">
                <template slot-scope="scope">
                  <el-input-number v-model="scope.row.signNum" :min="0" :controls="false" style="width: 100%" />
                </template>
              </el-table-column>
              <el-table-column label="状态" min-width="120">
                <template slot-scope="scope">
                  <el-select v-model="scope.row.status" placeholder="请选择状态" style="width: 100%">
                    <el-option label="待发货" value="0" />
                    <el-option label="已发货" value="1" />
                    <el-option label="已签收" value="2" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="备注" min-width="180">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.remark" placeholder="请输入备注" />
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-form>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" size="small" @click="submitForm">确定</el-button>
        <el-button size="small" @click="open = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { addDeliveryOrder, getDeliveryOrder, listDeliveryOrder, updateDeliveryOrder } from '@/api/school/deliveryOrder'
import cache from '@/plugins/cache'

const DELIVERY_ORDER_DRAFT_CACHE_KEY = 'production_delivery_order_draft'

const createShipForm = () => ({
  id: null,
  deliveryNo: null,
  logisticsCompany: null,
  logisticsNo: null,
  carriageFee: null,
  deliveryTime: null,
  deliveryStatus: '1'
})

const createDetailForm = () => ({
  id: null,
  deliveryNo: null,
  orderNo: null,
  orderType: '1',
  schoolName: null,
  supplierName: null,
  receiverName: null,
  receiverPhone: null,
  receiverAddress: null,
  logisticsCompany: null,
  logisticsNo: null,
  carriageFee: null,
  deliveryStatus: null,
  deliveryTime: null,
  signTime: null,
  remark: null
})

const createDeliveryItem = () => ({
  id: null,
  deliveryId: null,
  orderItemId: null,
  goodsId: null,
  sizeId: null,
  goodsName: null,
  sizeName: null,
  deliveryNum: 0,
  signNum: 0,
  status: '0',
  remark: null,
  delFlag: '0'
})

const createForm = () => ({
  id: null,
  deliveryNo: null,
  noticeId: null,
  orderId: null,
  orderNo: null,
  orderType: '1',
  productionId: null,
  schoolId: null,
  schoolName: null,
  supplierId: null,
  supplierName: null,
  receiverName: null,
  receiverPhone: null,
  receiverAddress: null,
  logisticsCompany: null,
  logisticsNo: null,
  deliveryStatus: '0',
  deliveryTime: null,
  signTime: null,
  carriageFee: null,
  deptId: null,
  remark: null,
  delFlag: '0',
  xfptDeliveryOrderItemList: []
})

export default {
  name: 'DeliveryOrder',
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      deliveryOrderList: [],
      activeOrderType: '1',
      open: false,
      dialogTitle: '新增发货单',
      form: createForm(),
      rules: {
        deliveryNo: [
          { required: true, message: '发货单号不能为空', trigger: 'blur' }
        ],
        orderType: [
          { required: true, message: '订单类型不能为空', trigger: 'change' }
        ],
        receiverName: [
          { required: true, message: '收货人不能为空', trigger: 'blur' }
        ],
        receiverPhone: [
          { required: true, message: '收货电话不能为空', trigger: 'blur' }
        ],
        receiverAddress: [
          { required: true, message: '收货地址不能为空', trigger: 'blur' }
        ],
        deliveryStatus: [
          { required: true, message: '发货状态不能为空', trigger: 'change' }
        ]
      },
      detailOpen: false,
      detailLoading: false,
      detailForm: createDetailForm(),
      detailItemList: [],
      shipOpen: false,
      shipForm: createShipForm(),
      shipRules: {
        logisticsCompany: [
          { required: true, message: '物流公司不能为空', trigger: 'blur' }
        ],
        logisticsNo: [
          { required: true, message: '物流单号不能为空', trigger: 'blur' }
        ],
        deliveryTime: [
          { required: true, message: '发货时间不能为空', trigger: 'change' }
        ]
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderType: '1',
        deliveryNo: null,
        orderNo: null,
        receiverName: null,
        logisticsCompany: null,
        logisticsNo: null,
        deliveryStatus: null
      }
    }
  },
  watch: {
    '$route.query.autoOpenAdd': {
      handler() {
        this.tryOpenDraftFromRoute()
      }
    }
  },
  created() {
    this.getList()
    this.tryOpenDraftFromRoute()
  },
  methods: {
    tryOpenDraftFromRoute() {
      if (this.$route.query.autoOpenAdd !== '1') {
        return
      }
      const draft = cache.session.getJSON(DELIVERY_ORDER_DRAFT_CACHE_KEY)
      if (!draft) {
        return
      }
      cache.session.remove(DELIVERY_ORDER_DRAFT_CACHE_KEY)
      this.handleAdd(draft)
      const query = { ...this.$route.query }
      delete query.autoOpenAdd
      delete query.fromProductionOrder
      this.$router.replace({ path: this.$route.path, query }).catch(() => {})
    },
    handleAdd(draft) {
      this.dialogTitle = '新增发货单'
      this.open = true
      this.form = this.normalizeForm(draft)
      this.$nextTick(() => {
        if (this.$refs.form) {
          this.$refs.form.clearValidate()
        }
      })
    },
    normalizeForm(data) {
      const source = data || {}
      const itemList = Array.isArray(source.xfptDeliveryOrderItemList)
        ? source.xfptDeliveryOrderItemList.map(item => ({
          ...createDeliveryItem(),
          ...item,
          deliveryNum: Number(item && item.deliveryNum != null ? item.deliveryNum : 0),
          signNum: Number(item && item.signNum != null ? item.signNum : 0),
          status: item && item.status != null ? String(item.status) : '0'
        }))
        : []
      return {
        ...createForm(),
        ...source,
        orderType: source.orderType != null ? String(source.orderType) : '1',
        deliveryStatus: source.deliveryStatus != null ? String(source.deliveryStatus) : '0',
        xfptDeliveryOrderItemList: itemList
      }
    },
    resetFormDialog() {
      this.form = createForm()
      if (this.$refs.form) {
        this.$refs.form.clearValidate()
      }
    },
    getList() {
      this.loading = true
      this.queryParams.orderType = this.activeOrderType
      listDeliveryOrder(this.queryParams)
        .then((response) => {
          this.deliveryOrderList = response.rows || []
          this.total = response.total || 0
        })
        .finally(() => {
          this.loading = false
        })
    },
    handleOrderTypeTabClick(tab) {
      this.activeOrderType = tab.name
      this.queryParams.orderType = tab.name
      this.handleQuery()
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.queryParams.orderType = this.activeOrderType
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleView(row) {
      this.detailOpen = true
      this.detailLoading = true
      getDeliveryOrder(row.id)
        .then((response) => {
          const data = response.data || {}
          const mergedData = {
            ...data,
            orderType: data.orderType != null ? data.orderType : row.orderType,
            schoolName: data.schoolName || row.schoolName,
            supplierName: data.supplierName || row.supplierName
          }
          this.detailForm = {
            ...createDetailForm(),
            ...row,
            ...mergedData
          }
          this.detailItemList = mergedData.xfptDeliveryOrderItemList || []
        })
        .finally(() => {
          this.detailLoading = false
        })
    },
    handleShip(row) {
      this.shipOpen = true
      this.shipForm = {
        ...createShipForm(),
        id: row.id,
        deliveryNo: row.deliveryNo,
        logisticsCompany: row.logisticsCompany,
        logisticsNo: row.logisticsNo,
        carriageFee: row.carriageFee,
        deliveryTime: row.deliveryTime || this.parseTime(new Date(), '{y}-{m}-{d}'),
        deliveryStatus: '1'
      }
      this.$nextTick(() => {
        if (this.$refs.shipForm) {
          this.$refs.shipForm.clearValidate()
        }
      })
    },
    resetShipForm() {
      this.shipForm = createShipForm()
      if (this.$refs.shipForm) {
        this.$refs.shipForm.clearValidate()
      }
    },
    submitShip() {
      this.$refs.shipForm.validate((valid) => {
        if (!valid) {
          return
        }
        updateDeliveryOrder({
          id: this.shipForm.id,
          logisticsCompany: this.shipForm.logisticsCompany,
          logisticsNo: this.shipForm.logisticsNo,
          carriageFee: this.shipForm.carriageFee,
          deliveryTime: this.shipForm.deliveryTime,
          deliveryStatus: '1'
        }).then(() => {
          this.$modal.msgSuccess('发货成功')
          this.shipOpen = false
          this.getList()
        })
      })
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          return
        }
        if (!this.form.xfptDeliveryOrderItemList.length) {
          this.$modal.msgError('发货明细不能为空')
          return
        }
        const payload = {
          ...this.form,
          orderType: this.form.orderType,
          deliveryStatus: this.form.deliveryStatus,
          xfptDeliveryOrderItemList: this.form.xfptDeliveryOrderItemList.map(item => ({
            ...item,
            status: item.status != null ? String(item.status) : '0'
          }))
        }
        addDeliveryOrder(payload).then(() => {
          this.$modal.msgSuccess('新增发货单成功')
          this.open = false
          this.getList()
        })
      })
    },
    formatOrderType(orderType) {
      if (String(orderType) === '1') {
        return '统购'
      }
      if (String(orderType) === '2') {
        return '零售'
      }
      return '--'
    },
    orderTypeTagType(orderType) {
      return String(orderType) === '2' ? 'success' : 'primary'
    },
    formatDeliveryStatus(status) {
      if (String(status) === '0') {
        return '待发货'
      }
      if (String(status) === '1') {
        return '已发货'
      }
      if (String(status) === '2') {
        return '已签收'
      }
      return '--'
    },
    deliveryStatusTagType(status) {
      if (String(status) === '0') {
        return 'info'
      }
      if (String(status) === '1') {
        return 'warning'
      }
      if (String(status) === '2') {
        return 'success'
      }
      return ''
    },
    formatDeliveryItemStatus(row) {
      const deliveryNum = Number(row.deliveryNum || 0)
      const signNum = Number(row.signNum || 0)

      if (!deliveryNum) {
        return '待发货'
      }
      if (signNum >= deliveryNum) {
        return '已签收'
      }
      if (signNum > 0) {
        return '部分签收'
      }
      return '待签收'
    },
    deliveryItemStatusTagType(row) {
      const statusText = this.formatDeliveryItemStatus(row)
      if (statusText === '已签收') {
        return 'success'
      }
      if (statusText === '部分签收') {
        return 'warning'
      }
      if (statusText === '待发货') {
        return 'info'
      }
      return ''
    },
    formatDate(value) {
      return this.parseTime(value, '{y}-{m}-{d}') || '--'
    },
    formatValue(value) {
      return value === undefined || value === null || value === '' ? '--' : value
    },
    formatAmount(value) {
      if (value === undefined || value === null || value === '') {
        return '--'
      }
      const amount = Number(value)
      if (Number.isNaN(amount)) {
        return value
      }
      return `￥${amount.toFixed(2)}`
    },
    handleExport() {
      this.download(
        'school/deliveryOrder/export',
        {
          ...this.queryParams,
          orderType: this.activeOrderType
        },
        `deliveryOrder_${new Date().getTime()}.xlsx`
      )
    }
  }
}
</script>

<style scoped>
.delivery-order-page .search-card,
.delivery-order-page .table-card {
  border: 1px solid #e6ebf5;
  border-radius: 4px;
}

.delivery-order-page .search-card {
  margin-bottom: 18px;
}

.delivery-order-page .table-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}

.delivery-order-page .table-title {
  color: #303133;
  font-size: 18px;
  font-weight: 600;
  line-height: 26px;
}

.delivery-order-page .table-desc {
  margin-top: 4px;
  color: #909399;
  font-size: 13px;
}

.delivery-order-page .order-type-tabs {
  margin-bottom: 14px;
}

.detail-wrap {
  min-height: 260px;
}

.detail-section {
  margin-top: 20px;
}

.section-title {
  margin-bottom: 12px;
  color: #303133;
  font-size: 15px;
  font-weight: 600;
}

.muted-action {
  color: #909399;
  font-size: 12px;
}
</style>
