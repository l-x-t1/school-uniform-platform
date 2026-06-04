<template>
  <div class="app-container business-page">
    <el-form
      v-show="showSearch"
      ref="queryForm"
      :model="queryParams"
      :inline="true"
      size="small"
      label-width="96px"
      class="query-form"
    >
      <el-form-item label="生产单号" prop="productionNo">
        <el-input
          v-model="queryParams.productionNo"
          placeholder="请输入生产单号"
          clearable
          style="width: 220px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="订单号" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单号"
          clearable
          style="width: 220px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="公告编号" prop="noticeCode">
        <el-input
          v-model="queryParams.noticeCode"
          placeholder="请输入公告编号"
          clearable
          style="width: 220px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="公告标题" prop="noticeTitle">
        <el-input
          v-model="queryParams.noticeTitle"
          placeholder="请输入公告标题"
          clearable
          style="width: 220px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="学校名称" prop="schoolName">
        <el-input
          v-model="queryParams.schoolName"
          placeholder="请输入学校名称"
          clearable
          style="width: 220px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="供应商名称" prop="supplierName">
        <el-input
          v-model="queryParams.supplierName"
          placeholder="请输入供应商名称"
          clearable
          style="width: 220px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="生产状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择生产状态"
          clearable
          style="width: 220px"
        >
          <el-option
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="计划完成时间" prop="plannedFinishTime">
        <el-date-picker
          v-model="queryParams.plannedFinishTime"
          clearable
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择计划完成时间"
          style="width: 220px"
        />
      </el-form-item>
      <el-form-item label="开始生产时间" prop="startTime">
        <el-date-picker
          v-model="queryParams.startTime"
          clearable
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择开始生产时间"
          style="width: 220px"
        />
      </el-form-item>
      <el-form-item label="生产完成时间" prop="finishTime">
        <el-date-picker
          v-model="queryParams.finishTime"
          clearable
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择生产完成时间"
          style="width: 220px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          v-if="activeOrderType === '1'"
          v-hasPermi="['school:productionOrder:add']"
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['school:productionOrder:edit']"
          type="success"
          plain
          icon="el-icon-check"
          size="mini"
          :disabled="single || !selectedCanFinish"
          @click="handleFinishSelected"
        >生产完成</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['school:productionOrder:edit']"
          type="warning"
          plain
          icon="el-icon-truck"
          size="mini"
          :disabled="single || !selectedCanGenerateDelivery"
          @click="handleGenerateDeliverySelected"
        >生成发货单</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['school:productionOrder:remove']"
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
          v-hasPermi="['school:productionOrder:export']"
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
        >导出</el-button>
      </el-col>
      <right-toolbar :show-search.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-tabs v-model="activeOrderType" @tab-click="handleOrderTypeTabChange">
      <el-tab-pane label="统购" name="1" />
    </el-tabs>

    <el-table
      v-loading="loading"
      :data="productionOrderList"
      border
      class="business-table"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="生产单号" prop="productionNo" min-width="150" show-overflow-tooltip />
      <el-table-column label="公告信息" min-width="280">
        <template slot-scope="scope">
          <div class="info-cell">
            <div class="info-title">{{ formatValue(scope.row.noticeTitle) }}</div>
            <div class="info-subline">公告编号：{{ formatValue(scope.row.noticeCode) }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="订单号" prop="orderNo" min-width="160" show-overflow-tooltip />
      <el-table-column label="学校名称" prop="schoolName" min-width="180" show-overflow-tooltip />
      <el-table-column label="供应商名称" prop="supplierName" min-width="180" show-overflow-tooltip />
      <el-table-column label="状态" width="120" align="center">
        <template slot-scope="scope">
          <el-tag size="mini" :type="getOptionTagType(statusOptions, scope.row.status)">
            {{ getOptionLabel(statusOptions, scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="计划完成时间" width="120" align="center">
        <template slot-scope="scope">{{ formatDate(scope.row.plannedFinishTime) }}</template>
      </el-table-column>
      <el-table-column label="开始生产时间" width="120" align="center">
        <template slot-scope="scope">{{ formatDate(scope.row.startTime) }}</template>
      </el-table-column>
      <el-table-column label="生产完成时间" width="120" align="center">
        <template slot-scope="scope">{{ formatDate(scope.row.finishTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="280" align="center" fixed="right" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleDetail(scope.row)">详情</el-button>
          <el-button
            v-hasPermi="['school:productionOrder:edit']"
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            v-hasPermi="['school:productionOrder:edit']"
            size="mini"
            type="text"
            icon="el-icon-check"
            :disabled="!canFinish(scope.row)"
            @click="handleFinish(scope.row)"
          >生产完成</el-button>
          <el-button
            v-hasPermi="['school:productionOrder:edit']"
            size="mini"
            type="text"
            icon="el-icon-truck"
            :disabled="!canGenerateDelivery(scope.row)"
            @click="handleGenerateDelivery(scope.row)"
          >生成发货单</el-button>
          <el-button
            v-hasPermi="['school:productionOrder:remove']"
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
      title="生产单详情"
      :visible.sync="detailOpen"
      width="1080px"
      top="5vh"
      append-to-body
      @closed="resetDetail"
    >
      <div class="dialog-scroll">
        <div class="form-section detail-section">
          <div class="section-head">
            <div class="section-title">基础信息</div>
            <div class="section-desc">以业务字段为主展示生产单、公告、学校和供应商信息</div>
          </div>
          <div class="detail-kv-grid">
            <div class="detail-kv">
              <span class="detail-label">生产单号</span>
              <span class="detail-value">{{ formatValue(detailForm.productionNo) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">订单号</span>
              <span class="detail-value">{{ formatValue(detailForm.orderNo) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">订单类型</span>
              <span class="detail-value">{{ formatOrderType(detailForm.orderType) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">公告标题</span>
              <span class="detail-value">{{ formatValue(detailForm.noticeTitle) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">公告编号</span>
              <span class="detail-value">{{ formatValue(detailForm.noticeCode) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">学校名称</span>
              <span class="detail-value">{{ formatValue(detailForm.schoolName) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">供应商名称</span>
              <span class="detail-value">{{ formatValue(detailForm.supplierName) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">状态</span>
              <span class="detail-value">{{ getOptionLabel(statusOptions, detailForm.status) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">计划完成时间</span>
              <span class="detail-value">{{ formatDate(detailForm.plannedFinishTime) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">开始生产时间</span>
              <span class="detail-value">{{ formatDate(detailForm.startTime) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">生产完成时间</span>
              <span class="detail-value">{{ formatDate(detailForm.finishTime) }}</span>
            </div>
          </div>
        </div>

        <div class="form-section detail-section">
          <div class="section-head">
            <div class="section-title">生产单明细</div>
            <div class="section-desc">默认展示商品和尺码信息，不在列表中直出明细 ID</div>
          </div>
          <el-table :data="detailItemList" border size="mini" class="detail-table">
            <el-table-column label="商品名称" prop="goodsName" min-width="180" show-overflow-tooltip />
            <el-table-column label="尺码名称" prop="sizeName" min-width="120" show-overflow-tooltip />
            <el-table-column label="应生产数量" prop="buyNum" width="110" align="center" />
            <el-table-column label="已完成数量" prop="finishNum" width="110" align="center" />
            <el-table-column label="状态" width="110" align="center">
              <template slot-scope="scope">
                <el-tag size="mini" :type="getOptionTagType(itemStatusOptions, scope.row.status)">
                  {{ getOptionLabel(itemStatusOptions, scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="备注" prop="remark" min-width="180" show-overflow-tooltip />
          </el-table>
        </div>

        <div class="form-section detail-section">
          <div class="section-head">
            <div class="section-title">补充字段</div>
            <div class="section-desc">原始关联字段和备注收纳到详情中，便于排查但不影响主列表阅读</div>
          </div>
          <div class="detail-kv-grid">
            <div class="detail-kv">
              <span class="detail-label">主键</span>
              <span class="detail-value">{{ formatValue(detailForm.id) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">公告ID</span>
              <span class="detail-value">{{ formatValue(detailForm.noticeId) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">订单ID</span>
              <span class="detail-value">{{ formatValue(detailForm.orderId) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">学校ID</span>
              <span class="detail-value">{{ formatValue(detailForm.schoolId) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">供应商ID</span>
              <span class="detail-value">{{ formatValue(detailForm.supplierId) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">部门/学校ID</span>
              <span class="detail-value">{{ formatValue(detailForm.deptId) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">创建人</span>
              <span class="detail-value">{{ formatValue(detailForm.createBy) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">创建时间</span>
              <span class="detail-value">{{ formatValue(detailForm.createTime) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">更新人</span>
              <span class="detail-value">{{ formatValue(detailForm.updateBy) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">更新时间</span>
              <span class="detail-value">{{ formatValue(detailForm.updateTime) }}</span>
            </div>
            <div class="detail-kv detail-kv-wide">
              <span class="detail-label">备注</span>
              <span class="detail-value text-wrap">{{ formatValue(detailForm.remark) }}</span>
            </div>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="title" :visible.sync="open" width="980px" top="5vh" append-to-body :close-on-click-modal="false">
      <div class="dialog-scroll">
        <el-form ref="form" :model="form" :rules="rules" label-width="110px">
          <div class="form-section">
            <div class="section-head">
              <div class="section-title">基础信息</div>
              <div class="section-desc">新增/编辑时仍保留原有内部字段录入方式</div>
            </div>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="生产单号" prop="productionNo">
                  <el-input v-model="form.productionNo" placeholder="请输入生产单号" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="统购公告ID" prop="noticeId">
                  <el-input v-model="form.noticeId" placeholder="请输入统购公告ID" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="订单ID" prop="orderId">
                  <el-input v-model="form.orderId" placeholder="请输入订单ID" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="订单号" prop="orderNo">
                  <el-input v-model="form.orderNo" placeholder="请输入订单号" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="学校ID" prop="schoolId">
                  <el-input v-model="form.schoolId" placeholder="请输入学校ID" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="供应商ID" prop="supplierId">
                  <el-input v-model="form.supplierId" placeholder="请输入供应商ID" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="计划完成时间" prop="plannedFinishTime">
                  <el-date-picker
                    v-model="form.plannedFinishTime"
                    clearable
                    type="date"
                    value-format="yyyy-MM-dd"
                    placeholder="请选择计划完成时间"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="开始生产时间" prop="startTime">
                  <el-date-picker
                    v-model="form.startTime"
                    clearable
                    type="date"
                    value-format="yyyy-MM-dd"
                    placeholder="请选择开始生产时间"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="生产完成时间" prop="finishTime">
                  <el-date-picker
                    v-model="form.finishTime"
                    clearable
                    type="date"
                    value-format="yyyy-MM-dd"
                    placeholder="请选择生产完成时间"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="部门/学校ID" prop="deptId">
                  <el-input v-model="form.deptId" placeholder="请输入当前归属部门/学校" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="备注" prop="remark">
                  <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="删除标记" prop="delFlag">
                  <el-input v-model="form.delFlag" placeholder="请输入删除标记，0正常 1删除" />
                </el-form-item>
              </el-col>
            </el-row>
          </div>

          <div class="form-section">
            <div class="section-head">
              <div class="section-title">生产单明细</div>
              <div class="section-desc">保留内部维护方式，便于生成发货单时使用</div>
            </div>
            <div class="size-toolbar">
              <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAddXfptProductionOrderItem">添加明细</el-button>
              <el-button type="danger" icon="el-icon-delete" size="mini" @click="handleDeleteXfptProductionOrderItem">删除明细</el-button>
            </div>
            <el-table
              ref="xfptProductionOrderItem"
              :data="xfptProductionOrderItemList"
              border
              size="mini"
              :row-class-name="rowXfptProductionOrderItemIndex"
              @selection-change="handleXfptProductionOrderItemSelectionChange"
            >
              <el-table-column type="selection" width="50" align="center" />
              <el-table-column label="序号" align="center" prop="index" width="60" />
              <el-table-column label="订单明细ID" prop="orderItemId" width="140">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.orderItemId" placeholder="请输入订单明细ID" />
                </template>
              </el-table-column>
              <el-table-column label="商品ID" prop="goodsId" width="120">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.goodsId" placeholder="请输入商品ID" />
                </template>
              </el-table-column>
              <el-table-column label="尺码ID" prop="sizeId" width="120">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.sizeId" placeholder="请输入尺码ID" />
                </template>
              </el-table-column>
              <el-table-column label="商品名称" prop="goodsName" min-width="140">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.goodsName" placeholder="请输入商品名称" />
                </template>
              </el-table-column>
              <el-table-column label="尺码名称" prop="sizeName" min-width="120">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.sizeName" placeholder="请输入尺码名称" />
                </template>
              </el-table-column>
              <el-table-column label="应生产数量" prop="buyNum" width="120">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.buyNum" placeholder="请输入应生产数量" />
                </template>
              </el-table-column>
              <el-table-column label="已完成数量" prop="finishNum" width="120">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.finishNum" placeholder="请输入已完成数量" />
                </template>
              </el-table-column>
              <el-table-column label="状态" prop="status" width="120">
                <template slot-scope="scope">
                  <el-select v-model="scope.row.status" placeholder="请选择状态" style="width: 100%">
                    <el-option
                      v-for="item in itemStatusOptions"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="备注" prop="remark" min-width="140">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.remark" placeholder="请输入备注" />
                </template>
              </el-table-column>
            </el-table>
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
import { listProductionOrder, getProductionOrder, delProductionOrder, addProductionOrder, updateProductionOrder, finishProductionOrder, generateDeliveryOrder } from '@/api/school/productionOrder'

const STATUS_OPTIONS = [
  { label: '待生产', value: '0', tagType: 'info' },
  { label: '生产中', value: '1', tagType: 'warning' },
  { label: '生产完成', value: '2', tagType: 'success' },
  { label: '已生成发货单', value: '3', tagType: 'primary' }
]

const ITEM_STATUS_OPTIONS = [
  { label: '待生产', value: '0', tagType: 'info' },
  { label: '生产中', value: '1', tagType: 'warning' },
  { label: '生产完成', value: '2', tagType: 'success' }
]

export default {
  name: 'ProductionOrder',
  data() {
    return {
      loading: true,
      ids: [],
      checkedXfptProductionOrderItem: [],
      single: true,
      multiple: true,
      selectedRow: null,
      showSearch: true,
      total: 0,
      productionOrderList: [],
      xfptProductionOrderItemList: [],
      title: '',
      open: false,
      detailOpen: false,
      detailForm: {},
      detailItemList: [],
      activeOrderType: '1',
      statusOptions: STATUS_OPTIONS,
      itemStatusOptions: ITEM_STATUS_OPTIONS,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        productionNo: null,
        orderNo: null,
        noticeCode: null,
        noticeTitle: null,
        schoolName: null,
        supplierName: null,
        orderType: '1',
        status: null,
        plannedFinishTime: null,
        startTime: null,
        finishTime: null,
        noticeId: null,
        orderId: null,
        schoolId: null,
        supplierId: null,
        deptId: null
      },
      form: {},
      rules: {
        productionNo: [
          { required: true, message: '生产单号不能为空', trigger: 'blur' }
        ],
        orderType: [
          { required: true, message: '订单类型不能为空', trigger: 'change' }
        ],
        supplierId: [
          { required: true, message: '供应商ID不能为空', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '状态不能为空', trigger: 'change' }
        ],
        delFlag: [
          { required: true, message: '删除标记不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    selectedCanFinish() {
      return this.selectedRow ? this.canFinish(this.selectedRow) : false
    },
    selectedCanGenerateDelivery() {
      return this.selectedRow ? this.canGenerateDelivery(this.selectedRow) : false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getDefaultForm() {
      return {
        id: null,
        productionNo: null,
        noticeId: null,
        orderId: null,
        orderNo: null,
        orderType: '1',
        schoolId: null,
        supplierId: null,
        supplierName: null,
        noticeTitle: null,
        noticeCode: null,
        schoolName: null,
        status: '0',
        plannedFinishTime: null,
        startTime: null,
        finishTime: null,
        deptId: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null,
        delFlag: '0'
      }
    },
    getDefaultItemRow() {
      return {
        orderItemId: '',
        goodsId: '',
        sizeId: '',
        goodsName: '',
        sizeName: '',
        buyNum: '',
        finishNum: '',
        status: '0',
        remark: ''
      }
    },
    getList() {
      this.loading = true
      this.queryParams.orderType = this.activeOrderType
      listProductionOrder(this.queryParams).then(response => {
        this.productionOrderList = response.rows || []
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
      this.xfptProductionOrderItemList = []
      this.checkedXfptProductionOrderItem = []
      this.resetForm('form')
    },
    resetDetail() {
      this.detailForm = {}
      this.detailItemList = []
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
    handleOrderTypeTabChange() {
      this.queryParams.pageNum = 1
      this.queryParams.orderType = this.activeOrderType
      this.getList()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = selection.length === 0
      this.selectedRow = selection.length === 1 ? selection[0] : null
    },
    handleAdd() {
      this.reset()
      this.form.orderType = this.activeOrderType
      this.form.status = '0'
      this.open = true
      this.title = '添加生产单'
    },
    handleDetail(row) {
      const id = row && row.id ? row.id : this.ids[0]
      if (!id) {
        this.$modal.msgError('请选择要查看的生产单')
        return
      }
      getProductionOrder(id).then(response => {
        const data = response.data || {}
        this.detailForm = Object.assign(this.getDefaultForm(), data)
        this.detailItemList = data.xfptProductionOrderItemList || []
        this.detailOpen = true
      })
    },
    handleUpdate(row) {
      this.reset()
      const id = row && row.id ? row.id : this.ids[0]
      getProductionOrder(id).then(response => {
        const data = response.data || {}
        this.form = Object.assign(this.getDefaultForm(), data)
        this.xfptProductionOrderItemList = (data.xfptProductionOrderItemList || []).map(item => Object.assign({}, item))
        this.open = true
        this.title = '修改生产单'
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        this.form.xfptProductionOrderItemList = this.xfptProductionOrderItemList
        const request = this.form.id != null ? updateProductionOrder(this.form) : addProductionOrder(this.form)
        request.then(() => {
          this.$modal.msgSuccess(this.form.id != null ? '修改成功' : '新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const ids = row && row.id ? row.id : this.ids.join(',')
      this.$modal.confirm('是否确认删除生产单编号为"' + ids + '"的数据项？').then(() => {
        return delProductionOrder(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    handleFinishSelected() {
      if (this.selectedRow) {
        this.handleFinish(this.selectedRow)
      }
    },
    handleGenerateDeliverySelected() {
      if (this.selectedRow) {
        this.handleGenerateDelivery(this.selectedRow)
      }
    },
    handleFinish(row) {
      const id = row.id
      this.$modal.confirm('是否确认将该生产单标记为生产完成？').then(() => {
        return finishProductionOrder(id)
      }).then(() => {
        this.$modal.msgSuccess('生产完成')
        this.getList()
      }).catch(() => {})
    },
    handleGenerateDelivery(row) {
      const id = row.id
      this.$modal.confirm('是否确认根据该生产单生成发货单？').then(() => {
        return generateDeliveryOrder(id)
      }).then(() => {
        this.$modal.msgSuccess('生成发货单成功')
        this.getList()
      }).catch(() => {})
    },
    rowXfptProductionOrderItemIndex({ row, rowIndex }) {
      row.index = rowIndex + 1
    },
    handleAddXfptProductionOrderItem() {
      this.xfptProductionOrderItemList.push(this.getDefaultItemRow())
    },
    handleDeleteXfptProductionOrderItem() {
      if (this.checkedXfptProductionOrderItem.length === 0) {
        this.$modal.msgError('请先选择要删除的生产单明细数据')
        return
      }
      this.xfptProductionOrderItemList = this.xfptProductionOrderItemList.filter(item => {
        return this.checkedXfptProductionOrderItem.indexOf(item.index) === -1
      })
      this.checkedXfptProductionOrderItem = []
    },
    handleXfptProductionOrderItemSelectionChange(selection) {
      this.checkedXfptProductionOrderItem = selection.map(item => item.index)
    },
    handleExport() {
      this.download('school/productionOrder/export', {
        ...this.queryParams
      }, `productionOrder_${new Date().getTime()}.xlsx`)
    },
    canFinish(row) {
      return row && !['2', '3'].includes(String(row.status))
    },
    canGenerateDelivery(row) {
      return row && String(row.status) === '2'
    },
    getOptionLabel(options, value) {
      const option = options.find(item => String(item.value) === String(value))
      return option ? option.label : '--'
    },
    getOptionTagType(options, value) {
      const option = options.find(item => String(item.value) === String(value))
      return option ? option.tagType : 'info'
    },
    formatOrderType(value) {
      if (String(value) === '1') return '统购'
      if (String(value) === '2') return '零售'
      return '--'
    },
    formatValue(value) {
      return value == null || value === '' ? '--' : value
    },
    formatDate(value) {
      return value ? this.parseTime(value, '{y}-{m}-{d}') : '--'
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

.business-table {
  border-radius: 12px;
  overflow: hidden;
}

.info-cell {
  min-width: 0;
}

.info-title {
  margin-bottom: 6px;
  font-size: 14px;
  font-weight: 600;
  color: #243447;
}

.info-subline {
  font-size: 12px;
  line-height: 1.7;
  color: #6b7785;
}

.dialog-scroll {
  max-height: 70vh;
  overflow-y: auto;
  padding-right: 8px;
}

.form-section {
  margin-bottom: 18px;
  padding: 18px;
  background: #f8fafc;
  border: 1px solid #e7edf5;
  border-radius: 12px;
}

.detail-section {
  padding-bottom: 18px;
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

.detail-kv-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.detail-kv {
  min-width: 0;
  padding: 12px 14px;
  background: #ffffff;
  border: 1px solid #e7edf5;
  border-radius: 8px;
}

.detail-kv-wide {
  grid-column: span 2;
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

.text-wrap {
  white-space: pre-wrap;
}

.detail-table {
  background: #ffffff;
}

.size-toolbar {
  display: flex;
  margin-bottom: 12px;
}

.size-toolbar .el-button + .el-button {
  margin-left: 8px;
}

@media (max-width: 1200px) {
  .detail-kv-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .detail-kv-grid {
    grid-template-columns: 1fr;
  }

  .detail-kv-wide {
    grid-column: span 1;
  }
}
</style>
