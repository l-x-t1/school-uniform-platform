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
      <el-form-item label="供应商名称" prop="supplierName">
        <el-input
          v-model="queryParams.supplierName"
          placeholder="请输入供应商名称"
          clearable
          style="width: 220px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="报名单号" prop="applyNo">
        <el-input
          v-model="queryParams.applyNo"
          placeholder="请输入报名单号"
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
      <el-form-item label="报名状态" prop="applyStatus">
        <el-select
          v-model="queryParams.applyStatus"
          placeholder="请选择报名状态"
          clearable
          style="width: 220px"
        >
          <el-option
            v-for="item in applyStatusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="资质审查" prop="qualificationStatus">
        <el-select
          v-model="queryParams.qualificationStatus"
          placeholder="请选择资质审查状态"
          clearable
          style="width: 220px"
        >
          <el-option
            v-for="item in qualificationStatusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="比选阶段" prop="bidStageStatus">
        <el-select
          v-model="queryParams.bidStageStatus"
          placeholder="请选择比选阶段"
          clearable
          style="width: 220px"
        >
          <el-option
            v-for="item in bidStageStatusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="比选结果" prop="bidResultStatus">
        <el-select
          v-model="queryParams.bidResultStatus"
          placeholder="请选择比选结果"
          clearable
          style="width: 220px"
        >
          <el-option
            v-for="item in bidResultStatusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="报名时间" prop="applyTime">
        <el-date-picker
          v-model="queryParams.applyTime"
          clearable
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择报名时间"
          style="width: 220px"
        />
      </el-form-item>
      <el-form-item label="审核时间" prop="auditTime">
        <el-date-picker
          v-model="queryParams.auditTime"
          clearable
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择审核时间"
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
          v-hasPermi="['school:supplierApply:add']"
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['school:supplierApply:edit']"
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
          v-hasPermi="['school:supplierApply:remove']"
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
          v-hasPermi="['school:supplierApply:export']"
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
      :data="supplierApplyList"
      border
      class="business-table"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="供应商信息" min-width="240">
        <template slot-scope="scope">
          <div class="info-cell">
            <div class="info-title">{{ formatValue(scope.row.supplierName) }}</div>
            <div class="info-subline">报名单号：{{ formatValue(scope.row.applyNo) }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="公告信息" min-width="260">
        <template slot-scope="scope">
          <div class="info-cell">
            <div class="info-title">{{ formatValue(scope.row.noticeTitle) }}</div>
            <div class="info-subline">公告编号：{{ formatValue(scope.row.noticeCode) }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="学校名称" prop="schoolName" min-width="180" show-overflow-tooltip />
      <el-table-column label="报名状态" width="110" align="center">
        <template slot-scope="scope">
          <el-tag size="mini" :type="getOptionTagType(applyStatusOptions, scope.row.applyStatus)">
            {{ getOptionLabel(applyStatusOptions, scope.row.applyStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="资质审查" width="110" align="center">
        <template slot-scope="scope">
          <el-tag size="mini" :type="getOptionTagType(qualificationStatusOptions, scope.row.qualificationStatus)">
            {{ getOptionLabel(qualificationStatusOptions, scope.row.qualificationStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="比选阶段" width="110" align="center">
        <template slot-scope="scope">
          <el-tag size="mini" :type="getOptionTagType(bidStageStatusOptions, scope.row.bidStageStatus)">
            {{ getOptionLabel(bidStageStatusOptions, scope.row.bidStageStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="比选结果" width="110" align="center">
        <template slot-scope="scope">
          <el-tag size="mini" :type="getOptionTagType(bidResultStatusOptions, scope.row.bidResultStatus)">
            {{ getOptionLabel(bidResultStatusOptions, scope.row.bidResultStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="报名时间" width="120" align="center">
        <template slot-scope="scope">{{ formatDate(scope.row.applyTime) }}</template>
      </el-table-column>
      <el-table-column label="审核时间" width="120" align="center">
        <template slot-scope="scope">{{ formatDate(scope.row.auditTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="210" align="center" fixed="right" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)">详情</el-button>
          <el-button
            v-hasPermi="['school:supplierApply:edit']"
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            v-hasPermi="['school:supplierApply:remove']"
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
      title="供应商报名详情"
      :visible.sync="detailOpen"
      width="980px"
      top="6vh"
      append-to-body
      @closed="resetDetail"
    >
      <div v-loading="detailLoading" class="dialog-scroll">
        <div class="form-section detail-section">
          <div class="section-head">
            <div class="section-title">基础信息</div>
            <div class="section-desc">展示供应商、公告、学校与状态信息</div>
          </div>
          <div class="detail-kv-grid">
            <div class="detail-kv">
              <span class="detail-label">供应商名称</span>
              <span class="detail-value">{{ formatValue(detailForm.supplierName) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">报名单号</span>
              <span class="detail-value">{{ formatValue(detailForm.applyNo) }}</span>
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
              <span class="detail-label">报名时间</span>
              <span class="detail-value">{{ formatDate(detailForm.applyTime) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">审核时间</span>
              <span class="detail-value">{{ formatDate(detailForm.auditTime) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">报名状态</span>
              <span class="detail-value">{{ getOptionLabel(applyStatusOptions, detailForm.applyStatus) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">资质审查</span>
              <span class="detail-value">{{ getOptionLabel(qualificationStatusOptions, detailForm.qualificationStatus) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">比选阶段</span>
              <span class="detail-value">{{ getOptionLabel(bidStageStatusOptions, detailForm.bidStageStatus) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">比选结果</span>
              <span class="detail-value">{{ getOptionLabel(bidResultStatusOptions, detailForm.bidResultStatus) }}</span>
            </div>
          </div>
        </div>

        <div class="form-section detail-section">
          <div class="section-head">
            <div class="section-title">补充说明</div>
            <div class="section-desc">审核意见、备注和报名资料默认收纳到详情中</div>
          </div>
          <div class="detail-block">
            <div class="detail-block-title">审核意见</div>
            <div class="detail-text">{{ formatMultiline(detailForm.auditRemark) }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-block-title">备注</div>
            <div class="detail-text">{{ formatMultiline(detailForm.remark) }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-block-title">报名附件 JSON</div>
            <pre class="detail-pre">{{ formatMultiline(detailForm.applyFilesJson) }}</pre>
          </div>
          <div class="detail-block">
            <div class="detail-block-title">比选资料 JSON</div>
            <pre class="detail-pre">{{ formatMultiline(detailForm.compareFilesJson) }}</pre>
          </div>
        </div>

        <div class="form-section detail-section">
          <div class="section-head">
            <div class="section-title">补充字段</div>
            <div class="section-desc">保留原始主键与关联 ID，主列表中默认不展示</div>
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
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="title" :visible.sync="open" width="560px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="公告ID" prop="noticeId">
          <el-input v-model="form.noticeId" placeholder="请输入公告ID" />
        </el-form-item>
        <el-form-item label="学校ID" prop="schoolId">
          <el-input v-model="form.schoolId" placeholder="请输入学校ID" />
        </el-form-item>
        <el-form-item label="供应商ID" prop="supplierId">
          <el-input v-model="form.supplierId" placeholder="请输入供应商ID" />
        </el-form-item>
        <el-form-item label="供应商名称" prop="supplierName">
          <el-input v-model="form.supplierName" placeholder="请输入供应商名称" />
        </el-form-item>
        <el-form-item label="报名单号" prop="applyNo">
          <el-input v-model="form.applyNo" placeholder="请输入报名单号" />
        </el-form-item>
        <el-form-item label="报名状态" prop="applyStatus">
          <el-select v-model="form.applyStatus" placeholder="请选择报名状态" style="width: 100%">
            <el-option
              v-for="item in applyStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="资质审查状态" prop="qualificationStatus">
          <el-select v-model="form.qualificationStatus" placeholder="请选择资质审查状态" style="width: 100%">
            <el-option
              v-for="item in qualificationStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="比选阶段状态" prop="bidStageStatus">
          <el-select v-model="form.bidStageStatus" placeholder="请选择比选阶段状态" clearable style="width: 100%">
            <el-option
              v-for="item in bidStageStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="比选结果状态" prop="bidResultStatus">
          <el-select v-model="form.bidResultStatus" placeholder="请选择比选结果状态" clearable style="width: 100%">
            <el-option
              v-for="item in bidResultStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="报名时间" prop="applyTime">
          <el-date-picker
            v-model="form.applyTime"
            clearable
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择报名时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="审核时间" prop="auditTime">
          <el-date-picker
            v-model="form.auditTime"
            clearable
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择审核时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="审核意见" prop="auditRemark">
          <el-input v-model="form.auditRemark" type="textarea" :rows="3" placeholder="请输入审核意见" />
        </el-form-item>
        <el-form-item label="报名附件JSON" prop="applyFilesJson">
          <el-input v-model="form.applyFilesJson" type="textarea" :rows="3" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="比选资料JSON" prop="compareFilesJson">
          <el-input v-model="form.compareFilesJson" type="textarea" :rows="3" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="部门/学校ID" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入当前归属部门/学校" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
        <el-form-item label="删除标记" prop="delFlag">
          <el-input v-model="form.delFlag" placeholder="请输入删除标记，0正常 1删除" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listSupplierApply, getSupplierApply, delSupplierApply, addSupplierApply, updateSupplierApply } from '@/api/school/supplierApply'

const APPLY_STATUS_OPTIONS = [
  { label: '待提交', value: '0', tagType: 'info' },
  { label: '已提交', value: '1', tagType: 'primary' },
  { label: '已撤回', value: '2', tagType: 'warning' }
]

const QUALIFICATION_STATUS_OPTIONS = [
  { label: '待审查', value: '0', tagType: 'info' },
  { label: '通过', value: '1', tagType: 'success' },
  { label: '不通过', value: '2', tagType: 'danger' }
]

const BID_STAGE_STATUS_OPTIONS = [
  { label: '未进入', value: '0', tagType: 'info' },
  { label: '已进入', value: '1', tagType: 'warning' },
  { label: '已完成', value: '2', tagType: 'success' }
]

const BID_RESULT_STATUS_OPTIONS = [
  { label: '未出结果', value: '0', tagType: 'info' },
  { label: '已中选', value: '1', tagType: 'success' },
  { label: '未中选', value: '2', tagType: 'danger' }
]

export default {
  name: 'SupplierApply',
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      supplierApplyList: [],
      title: '',
      open: false,
      detailOpen: false,
      detailLoading: false,
      detailForm: {},
      applyStatusOptions: APPLY_STATUS_OPTIONS,
      qualificationStatusOptions: QUALIFICATION_STATUS_OPTIONS,
      bidStageStatusOptions: BID_STAGE_STATUS_OPTIONS,
      bidResultStatusOptions: BID_RESULT_STATUS_OPTIONS,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        supplierName: null,
        applyNo: null,
        noticeCode: null,
        noticeTitle: null,
        schoolName: null,
        applyStatus: null,
        qualificationStatus: null,
        bidStageStatus: null,
        bidResultStatus: null,
        applyTime: null,
        auditTime: null,
        noticeId: null,
        schoolId: null,
        supplierId: null,
        deptId: null
      },
      form: {},
      rules: {
        noticeId: [
          { required: true, message: '公告ID不能为空', trigger: 'blur' }
        ],
        schoolId: [
          { required: true, message: '学校ID不能为空', trigger: 'blur' }
        ],
        supplierId: [
          { required: true, message: '供应商ID不能为空', trigger: 'blur' }
        ],
        supplierName: [
          { required: true, message: '供应商名称不能为空', trigger: 'blur' }
        ],
        applyNo: [
          { required: true, message: '报名单号不能为空', trigger: 'blur' }
        ],
        applyStatus: [
          { required: true, message: '报名状态不能为空', trigger: 'change' }
        ],
        qualificationStatus: [
          { required: true, message: '资质审查状态不能为空', trigger: 'change' }
        ],
        delFlag: [
          { required: true, message: '删除标记不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getDefaultForm() {
      return {
        id: null,
        noticeId: null,
        schoolId: null,
        supplierId: null,
        supplierName: null,
        applyNo: null,
        applyStatus: '0',
        qualificationStatus: '0',
        bidStageStatus: null,
        bidResultStatus: null,
        applyTime: null,
        auditTime: null,
        auditRemark: null,
        applyFilesJson: null,
        compareFilesJson: null,
        deptId: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null,
        delFlag: null,
        noticeTitle: null,
        noticeCode: null,
        schoolName: null
      }
    },
    getList() {
      this.loading = true
      listSupplierApply(this.queryParams).then(response => {
        this.supplierApplyList = response.rows || []
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
    resetDetail() {
      this.detailForm = {}
      this.detailLoading = false
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
      this.single = selection.length !== 1
      this.multiple = selection.length === 0
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '添加供应商报名'
    },
    handleView(row) {
      const id = row && row.id ? row.id : this.ids[0]
      if (!id) {
        this.$modal.msgError('请选择要查看的报名记录')
        return
      }
      this.detailOpen = true
      this.detailLoading = true
      getSupplierApply(id).then(response => {
        this.detailForm = Object.assign(this.getDefaultForm(), response.data || {})
      }).finally(() => {
        this.detailLoading = false
      })
    },
    handleUpdate(row) {
      this.reset()
      const id = row && row.id ? row.id : this.ids[0]
      getSupplierApply(id).then(response => {
        this.form = Object.assign(this.getDefaultForm(), response.data || {})
        this.open = true
        this.title = '修改供应商报名'
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        const request = this.form.id != null ? updateSupplierApply(this.form) : addSupplierApply(this.form)
        request.then(() => {
          this.$modal.msgSuccess(this.form.id != null ? '修改成功' : '新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const ids = row && row.id ? row.id : this.ids.join(',')
      this.$modal.confirm('是否确认删除供应商报名编号为"' + ids + '"的数据项？').then(() => {
        return delSupplierApply(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    handleExport() {
      this.download('school/supplierApply/export', {
        ...this.queryParams
      }, `supplierApply_${new Date().getTime()}.xlsx`)
    },
    getOptionLabel(options, value) {
      const option = options.find(item => String(item.value) === String(value))
      return option ? option.label : '--'
    },
    getOptionTagType(options, value) {
      const option = options.find(item => String(item.value) === String(value))
      return option ? option.tagType : 'info'
    },
    formatValue(value) {
      return value == null || value === '' ? '--' : value
    },
    formatDate(value) {
      return value ? this.parseTime(value, '{y}-{m}-{d}') : '--'
    },
    formatMultiline(value) {
      return value == null || value === '' ? '--' : value
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

.detail-block + .detail-block {
  margin-top: 18px;
}

.detail-block-title {
  margin-bottom: 10px;
  font-size: 14px;
  font-weight: 600;
  color: #243447;
}

.detail-text,
.detail-pre {
  min-height: 42px;
  padding: 12px 14px;
  margin: 0;
  overflow-x: auto;
  line-height: 1.6;
  color: #6b7785;
  white-space: pre-wrap;
  background: #ffffff;
  border: 1px solid #e7edf5;
  border-radius: 8px;
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
}
</style>
