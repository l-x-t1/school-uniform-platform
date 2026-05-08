<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="68px">
      <el-form-item label="学校ID" prop="schoolId">
        <el-input
          v-model="queryParams.schoolId"
          placeholder="请输入学校ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="学校名称快照" prop="schoolName">
        <el-input
          v-model="queryParams.schoolName"
          placeholder="请输入学校名称快照"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="供应商ID" prop="supplierId">
        <el-input
          v-model="queryParams.supplierId"
          placeholder="请输入供应商ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="供应商名称快照" prop="supplierName">
        <el-input
          v-model="queryParams.supplierName"
          placeholder="请输入供应商名称快照"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="统购公告ID" prop="noticeId">
        <el-input
          v-model="queryParams.noticeId"
          placeholder="请输入统购公告ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="合同ID" prop="contractId">
        <el-input
          v-model="queryParams.contractId"
          placeholder="请输入合同ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="合作状态" prop="cooperateStatus">
        <el-select v-model="queryParams.cooperateStatus" placeholder="请选择合作状态" clearable>
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

    <el-table v-loading="loading" :data="schoolSupplierRelList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="学校ID" align="center" prop="schoolId" />
      <el-table-column label="学校名称快照" align="center" prop="schoolName" />
      <el-table-column label="供应商ID" align="center" prop="supplierId" />
      <el-table-column label="供应商名称快照" align="center" prop="supplierName" />
      <el-table-column label="统购公告ID" align="center" prop="noticeId" />
      <el-table-column label="合同ID" align="center" prop="contractId" />
      <el-table-column label="合作状态" align="center" prop="cooperateStatus">
        <template slot-scope="scope">
          <el-tag size="mini" :type="getCooperateStatusTag(scope.row.cooperateStatus)">
            {{ getOptionLabel(cooperateStatusOptions, scope.row.cooperateStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="服务电话" align="center" prop="servicePhone" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
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
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改学校供应商合作关系对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="900px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="学校ID" prop="schoolId">
          <el-input v-model="form.schoolId" placeholder="请输入学校ID" />
        </el-form-item>
        <el-form-item label="学校名称快照" prop="schoolName">
          <el-input v-model="form.schoolName" placeholder="请输入学校名称快照" />
        </el-form-item>
        <el-form-item label="供应商ID" prop="supplierId">
          <el-input v-model="form.supplierId" placeholder="请输入供应商ID" />
        </el-form-item>
        <el-form-item label="供应商名称快照" prop="supplierName">
          <el-input v-model="form.supplierName" placeholder="请输入供应商名称快照" />
        </el-form-item>
        <el-form-item label="统购公告ID" prop="noticeId">
          <el-input v-model="form.noticeId" placeholder="请输入统购公告ID" />
        </el-form-item>
        <el-form-item label="合同ID" prop="contractId">
          <el-input v-model="form.contractId" placeholder="请输入合同ID" />
        </el-form-item>
        <el-form-item label="合作状态" prop="cooperateStatus">
          <el-select v-model="form.cooperateStatus" placeholder="请选择合作状态" clearable>
            <el-option
              v-for="item in cooperateStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="服务电话" prop="servicePhone">
          <el-input v-model="form.servicePhone" placeholder="请输入服务电话" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-divider content-position="center">学校供应商合作商品信息</el-divider>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAddXfptSchoolSupplierGoodsRel">添加</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" icon="el-icon-delete" size="mini" @click="handleDeleteXfptSchoolSupplierGoodsRel">删除</el-button>
          </el-col>
        </el-row>
        <el-table ref="xfptSchoolSupplierGoodsRel" :data="xfptSchoolSupplierGoodsRelList" :row-class-name="rowXfptSchoolSupplierGoodsRelIndex" @selection-change="handleXfptSchoolSupplierGoodsRelSelectionChange">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="序号" align="center" prop="index" width="50" />
          <el-table-column label="商品" prop="goodsId" width="260">
            <template slot-scope="scope">
              <el-select v-model="scope.row.goodsId" placeholder="请选择商品" filterable clearable>
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
              <el-select v-model="scope.row.status" placeholder="请选择状态">
                <el-option
                  v-for="item in enableStatusOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 关联商品对话框 -->
    <el-dialog title="关联商品" :visible.sync="goodsRelOpen" width="780px" append-to-body>
      <el-form :model="goodsRelForm" label-width="90px" size="small">
        <el-form-item label="学校">
          <span>{{ goodsRelForm.schoolName }}（{{ goodsRelForm.schoolId }}）</span>
        </el-form-item>
        <el-form-item label="供应商">
          <span>{{ goodsRelForm.supplierName }}（{{ goodsRelForm.supplierId }}）</span>
        </el-form-item>
      </el-form>
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAddXfptSchoolSupplierGoodsRel">添加</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="danger" icon="el-icon-delete" size="mini" @click="handleDeleteXfptSchoolSupplierGoodsRel">删除</el-button>
        </el-col>
      </el-row>
      <el-table :data="xfptSchoolSupplierGoodsRelList" :row-class-name="rowXfptSchoolSupplierGoodsRelIndex" @selection-change="handleXfptSchoolSupplierGoodsRelSelectionChange">
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column label="序号" align="center" prop="index" width="50" />
        <el-table-column label="商品" prop="goodsId">
          <template slot-scope="scope">
            <el-select v-model="scope.row.goodsId" placeholder="请选择商品" filterable clearable>
              <el-option
                v-for="item in goodsOptions"
                :key="item.id"
                :label="formatGoodsOption(item)"
                :value="item.id"
              />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="160">
          <template slot-scope="scope">
            <el-select v-model="scope.row.status" placeholder="请选择状态">
              <el-option
                v-for="item in enableStatusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </template>
        </el-table-column>
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitGoodsRelForm">确 定</el-button>
        <el-button @click="goodsRelOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 设置服务电话对话框 -->
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
  getSchoolSupplierRel,
  delSchoolSupplierRel,
  addSchoolSupplierRel,
  updateSchoolSupplierRel,
  cancelSchoolSupplierRel,
  setSchoolSupplierServicePhone,
  replaceSchoolSupplierGoodsRel
} from '@/api/school/schoolSupplierRel'
import { listGoods } from '@/api/school/goods'

export default {
  name: 'SchoolSupplierRel',
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 子表选中数据
      checkedXfptSchoolSupplierGoodsRel: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 学校供应商合作关系表格数据
      schoolSupplierRelList: [],
      // 学校供应商合作商品表格数据
      xfptSchoolSupplierGoodsRelList: [],
      goodsOptions: [],
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
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      goodsRelOpen: false,
      goodsRelForm: {},
      servicePhoneOpen: false,
      servicePhoneForm: {
        id: null,
        servicePhone: null
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        schoolId: null,
        schoolName: null,
        supplierId: null,
        supplierName: null,
        noticeId: null,
        contractId: null,
        cooperateStatus: null,
        servicePhone: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        schoolId: [
          { required: true, message: '学校ID不能为空', trigger: 'blur' }
        ],
        schoolName: [
          { required: true, message: '学校名称快照不能为空', trigger: 'blur' }
        ],
        supplierId: [
          { required: true, message: '供应商ID不能为空', trigger: 'blur' }
        ],
        supplierName: [
          { required: true, message: '供应商名称快照不能为空', trigger: 'blur' }
        ],
        cooperateStatus: [
          { required: true, message: '合作状态，1合作中 2已取消 3历史合作 4公告期内不能为空', trigger: 'change' }
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
    this.getGoodsOptions()
    this.getList()
  },
  methods: {
    /** 查询学校供应商合作关系列表 */
    getList() {
      this.loading = true
      listSchoolSupplierRel(this.queryParams).then(response => {
        this.schoolSupplierRelList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
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
      this.xfptSchoolSupplierGoodsRelList = []
      this.resetForm('form')
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '添加学校供应商合作关系'
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getSchoolSupplierRel(id).then(response => {
        this.form = response.data
        this.xfptSchoolSupplierGoodsRelList = response.data.xfptSchoolSupplierGoodsRelList
        this.open = true
        this.title = '修改学校供应商合作关系'
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          const message = this.validateGoodsRelList(this.form)
          if (message) {
            this.$modal.msgError(message)
            return
          }
          this.form.delFlag = this.form.delFlag || '0'
          this.form.xfptSchoolSupplierGoodsRelList = this.normalizeGoodsRelList(this.form)
          if (this.form.id != null) {
            updateSchoolSupplierRel(this.form).then(response => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addSchoolSupplierRel(this.form).then(response => {
              this.$modal.msgSuccess('新增成功')
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除学校供应商合作关系编号为"' + ids + '"的数据项？').then(function() {
        return delSchoolSupplierRel(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    /** 学校供应商合作商品序号 */
    rowXfptSchoolSupplierGoodsRelIndex({ row, rowIndex }) {
      row.index = rowIndex + 1
    },
    /** 学校供应商合作商品添加按钮操作 */
    handleAddXfptSchoolSupplierGoodsRel() {
      const obj = {}
      const owner = this.goodsRelOpen ? this.goodsRelForm : this.form
      obj.schoolId = owner.schoolId || ''
      obj.supplierId = owner.supplierId || ''
      obj.goodsId = ''
      obj.status = '1'
      obj.remark = ''
      obj.delFlag = '0'
      this.xfptSchoolSupplierGoodsRelList.push(obj)
    },
    /** 学校供应商合作商品删除按钮操作 */
    handleDeleteXfptSchoolSupplierGoodsRel() {
      if (this.checkedXfptSchoolSupplierGoodsRel.length === 0) {
        this.$modal.msgError('请先选择要删除的学校供应商合作商品数据')
      } else {
        const xfptSchoolSupplierGoodsRelList = this.xfptSchoolSupplierGoodsRelList
        const checkedXfptSchoolSupplierGoodsRel = this.checkedXfptSchoolSupplierGoodsRel
        this.xfptSchoolSupplierGoodsRelList = xfptSchoolSupplierGoodsRelList.filter(function(item) {
          return checkedXfptSchoolSupplierGoodsRel.indexOf(item.index) === -1
        })
      }
    },
    /** 复选框选中数据 */
    handleXfptSchoolSupplierGoodsRelSelectionChange(selection) {
      this.checkedXfptSchoolSupplierGoodsRel = selection.map(item => item.index)
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('school/schoolSupplierRel/export', {
        ...this.queryParams
      }, `schoolSupplierRel_${new Date().getTime()}.xlsx`)
    },
    getGoodsOptions() {
      listGoods({ pageNum: 1, pageSize: 1000, status: '1' }).then(response => {
        this.goodsOptions = response.rows || []
      })
    },
    formatGoodsOption(item) {
      return item.goodsCode ? `${item.goodsName}（${item.goodsCode}）` : item.goodsName
    },
    getOptionLabel(options, value) {
      const option = options.find(item => item.value === String(value))
      return option ? option.label : value || '-'
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
    normalizeGoodsRelList(owner) {
      return this.xfptSchoolSupplierGoodsRelList.map(item => ({
        ...item,
        schoolId: owner.schoolId,
        supplierId: owner.supplierId,
        status: item.status || '1',
        delFlag: item.delFlag || '0'
      }))
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
        return '请先填写学校ID和供应商ID'
      }
      return ''
    },
    handleRelGoods(row) {
      getSchoolSupplierRel(row.id).then(response => {
        this.goodsRelForm = response.data || {}
        this.xfptSchoolSupplierGoodsRelList = this.goodsRelForm.xfptSchoolSupplierGoodsRelList || []
        this.checkedXfptSchoolSupplierGoodsRel = []
        this.goodsRelOpen = true
      })
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
