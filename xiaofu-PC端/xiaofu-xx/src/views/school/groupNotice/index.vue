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
          <el-col v-if="isAdmin" :span="24">
            <el-form-item label="可见供应商" prop="supplierIds">
              <el-select
                v-model="form.supplierIds"
                multiple
                filterable
                clearable
                collapse-tags
                placeholder="请选择可见供应商"
                style="width: 100%"
              >
                <el-option
                  v-for="supplier in supplierOptions"
                  :key="supplier.id"
                  :label="supplier.supplierName"
                  :value="supplier.id"
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
  addGroupNotice,
  updateGroupNotice,
  delGroupNotice
} from '@/api/school/groupNotice'
import {
  listSchoolMessage,
  getSchoolMessageByDeptId
} from '@/api/school/schoolMessage'
import { listSupplierMessage } from '@/api/school/supplierMessage'

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
        content: [{ required: true, message: '请输入公告正文', trigger: 'blur' }]
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
        this.loadSupplierOptions()
        return
      }
      const deptId = this.$store.getters.deptId
      if (!deptId) {
        return
      }
      getSchoolMessageByDeptId(deptId).then((res) => {
        this.currentSchool = res.data || {}
      })
    },
    loadSupplierOptions() {
      listSupplierMessage({ pageNum: 1, pageSize: 1000 }).then((res) => {
        this.supplierOptions = res.rows || []
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
      this.$refs.form && this.$refs.form.clearValidate()
    },
    handleAdd() {
      this.resetFormData()
      this.dialogTitle = '新增公告'
      this.open = true
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
        this.dialogTitle = '修改公告'
        this.open = true
      })
    },
    handleSchoolChange(schoolId) {
      const school = this.schoolOptions.find((item) => item.id === schoolId)
      if (!school) {
        this.form.deptId = null
        this.form.schoolName = null
        return
      }
      this.form.deptId = school.deptId
      this.form.schoolName = school.schoolName
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          return
        }
        if (this.isAdmin && (!this.form.supplierIds || !this.form.supplierIds.length)) {
          this.$message.error('请选择可见供应商')
          return
        }
        const payload = { ...this.form }
        payload.purchaseStartTime = payload.purchaseTimeRange[0]
        payload.purchaseEndTime = payload.purchaseTimeRange[1]
        delete payload.purchaseTimeRange
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
}
</style>
