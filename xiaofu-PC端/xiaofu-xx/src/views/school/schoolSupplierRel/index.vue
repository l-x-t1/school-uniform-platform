<template>
  <div class="app-container school-supplier-rel-page">
    <el-form
      v-show="showSearch"
      ref="queryForm"
      :model="queryParams"
      size="small"
      :inline="true"
      label-width="84px"
      class="query-form"
    >
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
      <el-form-item label="合作状态" prop="cooperateStatus">
        <el-select
          v-model="queryParams.cooperateStatus"
          placeholder="请选择合作状态"
          clearable
          style="width: 220px"
        >
          <el-option
            v-for="item in cooperateStatusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="服务电话" prop="servicePhone">
        <el-input
          v-model="queryParams.servicePhone"
          placeholder="请输入服务电话"
          clearable
          style="width: 220px"
          @keyup.enter.native="handleQuery"
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
          v-hasPermi="['school:schoolSupplierRel:add']"
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['school:schoolSupplierRel:edit']"
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
          v-hasPermi="['school:schoolSupplierRel:remove']"
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
          v-hasPermi="['school:schoolSupplierRel:export']"
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
      :data="schoolSupplierRelList"
      border
      class="goods-table"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="合作双方" min-width="340">
        <template slot-scope="scope">
          <div class="rel-parties-cell">
            <div class="party-row">
              <span class="party-label">学校</span>
              <span class="party-name">{{ formatValue(scope.row.schoolName) }}</span>
            </div>
            <div class="party-row">
              <span class="party-label supplier">供应商</span>
              <span class="party-name">{{ formatValue(scope.row.supplierName) }}</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="合作状态" width="120" align="center">
        <template slot-scope="scope">
          <el-tag size="mini" :type="getCooperateStatusTag(scope.row.cooperateStatus)">
            {{ getOptionLabel(cooperateStatusOptions, scope.row.cooperateStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="服务电话" prop="servicePhone" width="150" show-overflow-tooltip>
        <template slot-scope="scope">{{ formatValue(scope.row.servicePhone) }}</template>
      </el-table-column>
      <el-table-column label="关联商品" width="110" align="center">
        <template slot-scope="scope">{{ formatGoodsRelCount(scope.row) }}</template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" min-width="180" show-overflow-tooltip>
        <template slot-scope="scope">{{ formatValue(scope.row.remark) }}</template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="160" align="center">
        <template slot-scope="scope">{{ formatDateTime(scope.row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="360" align="center" fixed="right" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >详情</el-button>
          <el-button
            v-hasPermi="['school:schoolSupplierRel:edit']"
            size="mini"
            type="text"
            icon="el-icon-goods"
            @click="handleRelGoods(scope.row)"
          >关联商品</el-button>
          <el-button
            v-hasPermi="['school:schoolSupplierRel:edit']"
            size="mini"
            type="text"
            icon="el-icon-phone"
            @click="handleServicePhone(scope.row)"
          >设置电话</el-button>
          <el-button
            v-if="canCancelCooperate(scope.row)"
            v-hasPermi="['school:schoolSupplierRel:edit']"
            size="mini"
            type="text"
            icon="el-icon-close"
            @click="handleCancelCooperate(scope.row)"
          >取消合作</el-button>
          <el-button
            v-hasPermi="['school:schoolSupplierRel:edit']"
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            v-hasPermi="['school:schoolSupplierRel:remove']"
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
      title="合作详情"
      :visible.sync="detailOpen"
      width="980px"
      top="5vh"
      append-to-body
      class="rel-detail-dialog"
      @closed="resetDetail"
    >
      <div v-loading="detailLoading" class="dialog-scroll">
        <div class="form-section detail-section">
          <div class="detail-parties">
            <div class="detail-party-card school">
              <div class="detail-party-label">学校</div>
              <div class="detail-party-name">{{ formatValue(detailForm.schoolName) }}</div>
            </div>
            <i class="el-icon-right detail-arrow" />
            <div class="detail-party-card supplier">
              <div class="detail-party-label">供应商</div>
              <div class="detail-party-name">{{ formatValue(detailForm.supplierName) }}</div>
            </div>
          </div>
          <div class="detail-status-line">
            <el-tag size="mini" :type="getCooperateStatusTag(detailForm.cooperateStatus)">
              {{ getOptionLabel(cooperateStatusOptions, detailForm.cooperateStatus) }}
            </el-tag>
            <span>服务电话：{{ formatValue(detailForm.servicePhone) }}</span>
          </div>
          <div class="detail-kv-grid">
            <div class="detail-kv">
              <span class="detail-label">创建时间</span>
              <span class="detail-value">{{ formatDateTime(detailForm.createTime) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">更新时间</span>
              <span class="detail-value">{{ formatDateTime(detailForm.updateTime) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">关联商品</span>
              <span class="detail-value">{{ detailGoodsRelList.length }} 个</span>
            </div>
          </div>
          <div class="detail-remark">
            <div class="detail-label">备注</div>
            <div class="remark-text">{{ formatValue(detailForm.remark) }}</div>
          </div>
        </div>

        <div class="form-section detail-section">
          <div class="section-head">
            <div class="section-title">关联商品</div>
            <div class="section-desc">当前合作关系下已配置的商品与启停状态</div>
          </div>
          <el-table :data="detailGoodsRelList" border size="mini" class="inner-table">
            <el-table-column label="序号" prop="index" width="70" align="center" />
            <el-table-column label="商品" min-width="220">
              <template slot-scope="scope">{{ getGoodsName(scope.row.goodsId) }}</template>
            </el-table-column>
            <el-table-column label="状态" width="110" align="center">
              <template slot-scope="scope">
                <el-tag size="mini" :type="getEnableStatusTag(scope.row.status)">
                  {{ getOptionLabel(enableStatusOptions, scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="备注" prop="remark" min-width="160" show-overflow-tooltip>
              <template slot-scope="scope">{{ formatValue(scope.row.remark) }}</template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <el-dialog
      :title="title"
      :visible.sync="open"
      width="960px"
      top="4vh"
      append-to-body
      :close-on-click-modal="false"
      class="rel-edit-dialog"
    >
      <div class="dialog-scroll">
        <el-form ref="form" :model="form" :rules="rules" label-width="96px" class="rel-form">
          <div class="form-section">
            <div class="section-head">
              <div class="section-title">合作主体</div>
              <div class="section-desc">选择学校与供应商，系统会自动保留对应内部编号</div>
            </div>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="学校" prop="schoolId">
                  <el-select
                    v-model="form.schoolId"
                    filterable
                    clearable
                    placeholder="请选择学校"
                    style="width: 100%"
                    @change="handleSchoolChange"
                  >
                    <el-option
                      v-for="item in schoolOptions"
                      :key="getSchoolId(item)"
                      :label="formatSchoolOption(item)"
                      :value="getSchoolId(item)"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="供应商" prop="supplierId">
                  <el-select
                    v-model="form.supplierId"
                    filterable
                    :clearable="!supplierScoped"
                    placeholder="请选择供应商"
                    style="width: 100%"
                    :disabled="supplierScoped"
                    @change="handleSupplierChange"
                  >
                    <el-option
                      v-for="item in supplierOptions"
                      :key="getSupplierId(item)"
                      :label="formatSupplierOption(item)"
                      :value="getSupplierId(item)"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
          </div>

          <div class="form-section">
            <div class="section-head">
              <div class="section-title">合作信息</div>
              <div class="section-desc">维护合作状态、服务电话与备注说明</div>
            </div>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="合作状态" prop="cooperateStatus">
                  <el-select v-model="form.cooperateStatus" placeholder="请选择合作状态" style="width: 100%">
                    <el-option
                      v-for="item in cooperateStatusOptions"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="服务电话" prop="servicePhone">
                  <el-input v-model="form.servicePhone" placeholder="请输入服务电话" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="备注" prop="remark">
                  <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
                </el-form-item>
              </el-col>
            </el-row>
          </div>

          <div class="form-section">
            <div class="section-head">
              <div class="section-title">合作商品</div>
              <div class="section-desc">配置该学校与供应商合作范围内可服务的商品</div>
            </div>
            <div class="size-toolbar">
              <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAddXfptSchoolSupplierGoodsRel">添加商品</el-button>
              <el-button type="danger" icon="el-icon-delete" size="mini" @click="handleDeleteXfptSchoolSupplierGoodsRel">删除商品</el-button>
            </div>
            <el-table
              ref="xfptSchoolSupplierGoodsRel"
              :data="xfptSchoolSupplierGoodsRelList"
              border
              size="mini"
              class="inner-table"
              :row-class-name="rowXfptSchoolSupplierGoodsRelIndex"
              @selection-change="handleXfptSchoolSupplierGoodsRelSelectionChange"
            >
              <el-table-column type="selection" width="50" align="center" />
              <el-table-column label="序号" align="center" prop="index" width="60" />
              <el-table-column label="商品" prop="goodsId" min-width="260">
                <template slot-scope="scope">
                  <el-select v-model="scope.row.goodsId" placeholder="请选择商品" filterable clearable style="width: 100%">
                    <el-option
                      v-for="item in goodsOptions"
                      :key="item.id"
                      :label="formatGoodsOption(item)"
                      :value="item.id"
                    />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="状态" prop="status" width="150">
                <template slot-scope="scope">
                  <el-select v-model="scope.row.status" placeholder="请选择状态" style="width: 100%">
                    <el-option
                      v-for="item in enableStatusOptions"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="备注" prop="remark" min-width="180">
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

    <el-dialog
      title="关联商品"
      :visible.sync="goodsRelOpen"
      width="860px"
      top="6vh"
      append-to-body
      class="goods-rel-dialog"
      @closed="resetGoodsRelDialog"
    >
      <div class="dialog-scroll">
        <div class="form-section compact-section">
          <div class="rel-summary">
            <div>
              <span class="summary-label">学校</span>
              <span class="summary-value">{{ formatValue(goodsRelForm.schoolName) }}</span>
            </div>
            <div>
              <span class="summary-label">供应商</span>
              <span class="summary-value">{{ formatValue(goodsRelForm.supplierName) }}</span>
            </div>
          </div>
        </div>
        <div class="form-section">
          <div class="section-head">
            <div class="section-title">合作商品</div>
            <div class="section-desc">单独维护当前合作关系下的商品范围</div>
          </div>
          <div class="size-toolbar">
            <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAddXfptSchoolSupplierGoodsRel">添加商品</el-button>
            <el-button type="danger" icon="el-icon-delete" size="mini" @click="handleDeleteXfptSchoolSupplierGoodsRel">删除商品</el-button>
          </div>
          <el-table
            :data="xfptSchoolSupplierGoodsRelList"
            border
            size="mini"
            class="inner-table"
            :row-class-name="rowXfptSchoolSupplierGoodsRelIndex"
            @selection-change="handleXfptSchoolSupplierGoodsRelSelectionChange"
          >
            <el-table-column type="selection" width="50" align="center" />
            <el-table-column label="序号" align="center" prop="index" width="60" />
            <el-table-column label="商品" prop="goodsId" min-width="260">
              <template slot-scope="scope">
                <el-select v-model="scope.row.goodsId" placeholder="请选择商品" filterable clearable style="width: 100%">
                  <el-option
                    v-for="item in goodsOptions"
                    :key="item.id"
                    :label="formatGoodsOption(item)"
                    :value="item.id"
                  />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="状态" prop="status" width="150">
              <template slot-scope="scope">
                <el-select v-model="scope.row.status" placeholder="请选择状态" style="width: 100%">
                  <el-option
                    v-for="item in enableStatusOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="备注" prop="remark" min-width="180">
              <template slot-scope="scope">
                <el-input v-model="scope.row.remark" placeholder="请输入备注" />
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitGoodsRelForm">确 定</el-button>
        <el-button @click="goodsRelOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="设置服务电话" :visible.sync="servicePhoneOpen" width="420px" append-to-body>
      <el-form ref="servicePhoneForm" :model="servicePhoneForm" :rules="servicePhoneRules" label-width="90px">
        <el-form-item label="服务电话" prop="servicePhone">
          <el-input v-model="servicePhoneForm.servicePhone" placeholder="请输入服务电话" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitServicePhoneForm">确 定</el-button>
        <el-button @click="servicePhoneOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listSchoolSupplierRel,
  delSchoolSupplierRel,
  addSchoolSupplierRel,
  updateSchoolSupplierRel,
  cancelSchoolSupplierRel,
  setSchoolSupplierServicePhone,
  replaceSchoolSupplierGoodsRel
} from '@/api/school/schoolSupplierRel'
import { listSchoolSupplierGoodsRel } from '@/api/school/schoolSupplierGoodsRel'
import { listGoods } from '@/api/school/goods'
import { listSupplierMessage } from '@/api/school/supplierMessage'
import { getSchoolListCompleteDetail } from '@/api/jiaoyuju/schoolManagement'
import { getCurrentSupplier } from '@/api/supplier/feature'

export default {
  name: 'SchoolSupplierRel',
  data() {
    return {
      loading: true,
      ids: [],
      selectedRows: [],
      checkedXfptSchoolSupplierGoodsRel: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      schoolSupplierRelList: [],
      xfptSchoolSupplierGoodsRelList: [],
      detailOpen: false,
      detailLoading: false,
      detailForm: {},
      detailGoodsRelList: [],
      schoolOptions: [],
      supplierOptions: [],
      goodsOptions: [],
      supplierScoped: false,
      currentSupplier: null,
      cooperateStatusOptions: [
        { value: '1', label: '合作中' },
        { value: '2', label: '已取消' },
        { value: '3', label: '历史合作' },
        { value: '4', label: '公告期内' }
      ],
      enableStatusOptions: [
        { value: '0', label: '停用' },
        { value: '1', label: '启用' }
      ],
      title: '',
      open: false,
      goodsRelOpen: false,
      goodsRelForm: {},
      servicePhoneOpen: false,
      servicePhoneForm: {
        id: null,
        servicePhone: null
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        schoolName: null,
        supplierName: null,
        cooperateStatus: null,
        servicePhone: null
      },
      form: {},
      rules: {
        schoolId: [
          { required: true, message: '请选择学校', trigger: 'change' }
        ],
        supplierId: [
          { required: true, message: '请选择供应商', trigger: 'change' }
        ],
        cooperateStatus: [
          { required: true, message: '请选择合作状态', trigger: 'change' }
        ],
        servicePhone: [
          { max: 50, message: '服务电话不能超过50个字符', trigger: 'blur' }
        ]
      },
      servicePhoneRules: {
        servicePhone: [
          { required: true, message: '服务电话不能为空', trigger: 'blur' },
          { max: 50, message: '服务电话不能超过50个字符', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.initializePage()
  },
  methods: {
    async initializePage() {
      await this.initSupplierScope()
      this.getSchoolOptions()
      this.getSupplierOptions()
      this.getGoodsOptions()
      this.getList()
    },
    async initSupplierScope() {
      const roles = (this.$store.getters.roles || []).map(item => String(item).toLowerCase())
      const isAdmin = roles.includes('admin')
      this.supplierScoped = !isAdmin && roles.some(item => item === 'gys' || item === 'ceshigongying' || item.includes('gys'))
      if (!this.supplierScoped) {
        this.currentSupplier = null
        return
      }
      try {
        const response = await getCurrentSupplier()
        this.currentSupplier = response.data || null
      } catch (error) {
        this.currentSupplier = null
      }
    },
    getDefaultForm() {
      return {
        id: null,
        schoolId: null,
        schoolName: null,
        supplierId: null,
        supplierName: null,
        noticeId: null,
        contractId: null,
        cooperateStatus: '1',
        servicePhone: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null,
        delFlag: '0'
      }
    },
    /** 查询学校供应商合作关系列表 */
    getList() {
      this.loading = true
      listSchoolSupplierRel(this.queryParams).then(response => {
        const rows = response.rows || []
        this.schoolSupplierRelList = rows
        this.total = response.total || 0
        this.fillGoodsRelCounts(rows)
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
      this.xfptSchoolSupplierGoodsRelList = []
      this.checkedXfptSchoolSupplierGoodsRel = []
      this.resetForm('form')
      this.applyCurrentSupplierToForm()
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
      this.title = '新增学校供应商合作关系'
    },
    handleView(row) {
      const data = row && row.id ? row : this.selectedRows[0]
      if (!data || !data.id) {
        this.$modal.msgError('请选择要查看的合作关系')
        return
      }
      this.detailOpen = true
      this.detailLoading = true
      this.detailForm = Object.assign(this.getDefaultForm(), data)
      this.loadGoodsRelListByRelId(data.id).then(list => {
        this.detailGoodsRelList = this.decorateGoodsRelList(list)
      }).finally(() => {
        this.detailLoading = false
      })
    },
    resetDetail() {
      this.detailForm = {}
      this.detailGoodsRelList = []
      this.detailLoading = false
    },
    handleUpdate(row) {
      this.reset()
      const data = row && row.id ? row : this.selectedRows[0]
      if (!data || !data.id) {
        this.$modal.msgError('请选择要修改的合作关系')
        return
      }
      this.form = Object.assign(this.getDefaultForm(), data)
      this.open = true
      this.title = '修改学校供应商合作关系'
      this.loadGoodsRelListByRelId(data.id).then(list => {
        this.xfptSchoolSupplierGoodsRelList = this.decorateGoodsRelList(list)
      })
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (!valid) {
          return
        }
        const message = this.validateGoodsRelList(this.form)
        if (message) {
          this.$modal.msgError(message)
          return
        }
        const payload = Object.assign({}, this.form, {
          delFlag: this.form.delFlag || '0',
          xfptSchoolSupplierGoodsRelList: this.normalizeGoodsRelList(this.form)
        })
        if (payload.id != null) {
          updateSchoolSupplierRel(payload).then(() => {
            this.$modal.msgSuccess('修改成功')
            this.open = false
            this.getList()
          })
          return
        }
        addSchoolSupplierRel(payload).then(() => {
          this.$modal.msgSuccess('新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const ids = row && row.id ? row.id : this.ids.join(',')
      this.$modal.confirm('是否确认删除选中的学校供应商合作关系？').then(() => {
        return delSchoolSupplierRel(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    rowXfptSchoolSupplierGoodsRelIndex({ row, rowIndex }) {
      row.index = rowIndex + 1
    },
    handleAddXfptSchoolSupplierGoodsRel() {
      const owner = this.goodsRelOpen ? this.goodsRelForm : this.form
      this.xfptSchoolSupplierGoodsRelList.push({
        schoolId: owner.schoolId || '',
        supplierId: owner.supplierId || '',
        goodsId: '',
        status: '1',
        remark: '',
        delFlag: '0'
      })
    },
    handleDeleteXfptSchoolSupplierGoodsRel() {
      if (this.checkedXfptSchoolSupplierGoodsRel.length === 0) {
        this.$modal.msgError('请先选择要删除的合作商品')
        return
      }
      const checkedRows = this.checkedXfptSchoolSupplierGoodsRel
      this.xfptSchoolSupplierGoodsRelList = this.xfptSchoolSupplierGoodsRelList.filter(item => {
        return checkedRows.indexOf(item.index) === -1
      })
      this.checkedXfptSchoolSupplierGoodsRel = []
    },
    handleXfptSchoolSupplierGoodsRelSelectionChange(selection) {
      this.checkedXfptSchoolSupplierGoodsRel = selection.map(item => item.index)
    },
    handleExport() {
      this.download('school/schoolSupplierRel/export', {
        ...this.queryParams
      }, `schoolSupplierRel_${new Date().getTime()}.xlsx`)
    },
    getSchoolOptions() {
      getSchoolListCompleteDetail({ pageNum: 1, pageSize: 1000 }).then(response => {
        this.schoolOptions = response.rows || response.data || []
      })
    },
    getSupplierOptions() {
      if (this.supplierScoped) {
        this.supplierOptions = this.currentSupplier ? [this.currentSupplier] : []
        this.applyCurrentSupplierToForm()
        return
      }
      listSupplierMessage({ pageNum: 1, pageSize: 1000 }).then(response => {
        this.supplierOptions = response.rows || []
      })
    },
    applyCurrentSupplierToForm() {
      if (!this.supplierScoped || !this.currentSupplier) {
        return
      }
      this.form.supplierId = this.getSupplierId(this.currentSupplier)
      this.form.supplierName = this.getSupplierName(this.currentSupplier)
    },
    getGoodsOptions() {
      listGoods({ pageNum: 1, pageSize: 1000, status: '1' }).then(response => {
        this.goodsOptions = response.rows || []
      })
    },
    handleSchoolChange(value) {
      const option = this.schoolOptions.find(item => String(this.getSchoolId(item)) === String(value))
      this.form.schoolId = value || null
      this.form.schoolName = option ? this.getSchoolName(option) : null
    },
    handleSupplierChange(value) {
      const option = this.supplierOptions.find(item => String(this.getSupplierId(item)) === String(value))
      this.form.supplierId = value || null
      this.form.supplierName = option ? this.getSupplierName(option) : null
    },
    formatSchoolOption(item) {
      return this.getSchoolName(item)
    },
    getSchoolName(item) {
      return item.schoolName || item.deptName || item.name || item.label || ''
    },
    getSchoolId(item) {
      if (item.schoolId != null) {
        return item.schoolId
      }
      if (item.deptId != null) {
        return item.deptId
      }
      return item.id
    },
    formatSupplierOption(item) {
      return this.getSupplierName(item)
    },
    getSupplierName(item) {
      return item.supplierName || item.companyName || item.name || ''
    },
    getSupplierId(item) {
      return item.id != null ? item.id : item.supplierId
    },
    formatGoodsOption(item) {
      return item.goodsCode ? `${item.goodsName}（${item.goodsCode}）` : item.goodsName
    },
    getGoodsName(goodsId) {
      const option = this.goodsOptions.find(item => String(item.id) === String(goodsId))
      if (option) {
        return this.formatGoodsOption(option)
      }
      return goodsId ? `未匹配商品（${goodsId}）` : '--'
    },
    getOptionLabel(options, value) {
      const option = options.find(item => item.value === String(value))
      return option ? option.label : this.formatValue(value)
    },
    getCooperateStatusTag(value) {
      const typeMap = {
        '1': 'success',
        '2': 'danger',
        '3': 'info',
        '4': 'warning'
      }
      return typeMap[String(value)] || 'info'
    },
    getEnableStatusTag(value) {
      return String(value) === '1' ? 'success' : 'info'
    },
    formatValue(value) {
      return value == null || value === '' ? '--' : value
    },
    formatDateTime(value) {
      return value ? this.parseTime(value, '{y}-{m}-{d} {h}:{i}') : '--'
    },
    formatGoodsRelCount(row) {
      if (row.goodsRelCount != null) {
        return `${row.goodsRelCount} 个`
      }
      if (Array.isArray(row.xfptSchoolSupplierGoodsRelList)) {
        return `${row.xfptSchoolSupplierGoodsRelList.length} 个`
      }
      return '--'
    },
    canCancelCooperate(row) {
      return String(row.cooperateStatus) !== '2'
    },
    decorateGoodsRelList(list) {
      return (list || []).map((item, index) => {
        const row = Object.assign({}, item)
        row.index = index + 1
        row.goodsId = row.goodsId == null ? '' : row.goodsId
        row.status = row.status == null || row.status === '' ? '1' : String(row.status)
        row.remark = row.remark == null ? '' : row.remark
        row.delFlag = row.delFlag || '0'
        return row
      })
    },
    normalizeGoodsRelList(owner) {
      return this.xfptSchoolSupplierGoodsRelList.map(item => {
        const row = Object.assign({}, item)
        delete row.index
        row.schoolId = owner.schoolId
        row.supplierId = owner.supplierId
        row.status = row.status || '1'
        row.remark = row.remark == null ? '' : row.remark
        row.delFlag = row.delFlag || '0'
        return row
      })
    },
    validateGoodsRelList(owner) {
      const ids = []
      for (let index = 0; index < this.xfptSchoolSupplierGoodsRelList.length; index++) {
        const row = this.xfptSchoolSupplierGoodsRelList[index]
        if (!row.goodsId) return `第 ${index + 1} 行商品不能为空`
        if (!row.status) return `第 ${index + 1} 行状态不能为空`
        const goodsId = String(row.goodsId)
        if (ids.includes(goodsId)) return `第 ${index + 1} 行商品重复，请勿重复关联`
        ids.push(goodsId)
      }
      if (this.xfptSchoolSupplierGoodsRelList.length > 0 && (!owner.schoolId || !owner.supplierId)) {
        return '请先选择学校和供应商'
      }
      return ''
    },
    fillGoodsRelCounts(rows) {
      rows.forEach(row => {
        if (Array.isArray(row.xfptSchoolSupplierGoodsRelList)) {
          this.$set(row, 'goodsRelCount', row.xfptSchoolSupplierGoodsRelList.length)
          return
        }
        if (!row.id) {
          this.$set(row, 'goodsRelCount', 0)
          return
        }
        listSchoolSupplierGoodsRel({ pageNum: 1, pageSize: 1, relId: row.id }).then(response => {
          this.$set(row, 'goodsRelCount', response.total || 0)
        }).catch(() => {
          this.$set(row, 'goodsRelCount', null)
        })
      })
    },
    handleRelGoods(row) {
      this.goodsRelForm = Object.assign(this.getDefaultForm(), row || {})
      this.checkedXfptSchoolSupplierGoodsRel = []
      this.goodsRelOpen = true
      this.loadGoodsRelListByRelId(row.id).then(list => {
        this.xfptSchoolSupplierGoodsRelList = this.decorateGoodsRelList(list)
        this.checkedXfptSchoolSupplierGoodsRel = []
      })
    },
    loadGoodsRelListByRelId(relId) {
      if (!relId) {
        return Promise.resolve([])
      }
      return listSchoolSupplierGoodsRel({ pageNum: 1, pageSize: 1000, relId }).then(response => response.rows || [])
    },
    resetGoodsRelDialog() {
      this.goodsRelForm = {}
      this.xfptSchoolSupplierGoodsRelList = []
      this.checkedXfptSchoolSupplierGoodsRel = []
    },
    submitGoodsRelForm() {
      const message = this.validateGoodsRelList(this.goodsRelForm)
      if (message) {
        this.$modal.msgError(message)
        return
      }
      replaceSchoolSupplierGoodsRel(this.goodsRelForm.id, this.normalizeGoodsRelList(this.goodsRelForm)).then(() => {
        this.$modal.msgSuccess('关联商品成功')
        this.goodsRelOpen = false
        this.getList()
      })
    },
    handleServicePhone(row) {
      this.servicePhoneForm = {
        id: row.id,
        servicePhone: row.servicePhone
      }
      this.servicePhoneOpen = true
    },
    submitServicePhoneForm() {
      this.$refs['servicePhoneForm'].validate(valid => {
        if (valid) {
          setSchoolSupplierServicePhone(this.servicePhoneForm.id, this.servicePhoneForm.servicePhone).then(() => {
            this.$modal.msgSuccess('服务电话设置成功')
            this.servicePhoneOpen = false
            this.getList()
          })
        }
      })
    },
    handleCancelCooperate(row) {
      if (!this.canCancelCooperate(row)) {
        this.$modal.msgError('该合作关系已取消')
        return
      }
      this.$modal.confirm('是否确认取消与"' + row.supplierName + '"的合作？').then(() => {
        return cancelSchoolSupplierRel(row.id)
      }).then(() => {
        this.$modal.msgSuccess('取消合作成功')
        this.getList()
      }).catch(() => {})
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

.compact-section {
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

.size-toolbar {
  display: flex;
  margin-bottom: 12px;
}

.size-toolbar .el-button + .el-button {
  margin-left: 8px;
}

.inner-table {
  background: #ffffff;
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

.detail-status-line {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  margin-top: 14px;
  color: #6b7785;
}

.detail-kv-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: 16px;
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

.rel-summary {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.summary-label {
  display: block;
  margin-bottom: 6px;
  font-size: 12px;
  color: #7a8798;
}

.summary-value {
  display: block;
  overflow: hidden;
  font-size: 15px;
  font-weight: 600;
  color: #243447;
  text-overflow: ellipsis;
  white-space: nowrap;
}

@media (max-width: 1200px) {
  .dialog-scroll {
    max-height: 64vh;
  }

  .detail-kv-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .detail-parties,
  .detail-kv-grid,
  .rel-summary {
    grid-template-columns: 1fr;
  }

  .detail-arrow {
    display: none;
  }
}
</style>
