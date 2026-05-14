<template>
  <div class="app-container goods-size-page">
    <el-form
      v-show="showSearch"
      ref="queryForm"
      :model="queryParams"
      size="small"
      :inline="true"
      label-width="76px"
      class="query-form"
    >
      <el-form-item label="关联商品" prop="goodsId">
        <el-select
          v-model="queryParams.goodsId"
          placeholder="请选择商品"
          clearable
          filterable
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
      <el-form-item label="尺码编码" prop="sizeCode">
        <el-input
          v-model="queryParams.sizeCode"
          placeholder="请输入尺码编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="性别类型" prop="genderType">
        <el-select
          v-model="queryParams.genderType"
          placeholder="请选择性别类型"
          clearable
          style="width: 180px"
        >
          <el-option
            v-for="item in genderTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
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
          v-hasPermi="['school:goodsSize:add']"
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['school:goodsSize:edit']"
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
          v-hasPermi="['school:goodsSize:remove']"
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
          v-hasPermi="['school:goodsSize:export']"
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
      :data="goodsSizeList"
      border
      class="goods-table"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="关联商品" prop="goodsId" min-width="240" show-overflow-tooltip>
        <template slot-scope="scope">
          <div class="goods-info-cell">
            <div class="goods-title">{{ getGoodsName(scope.row.goodsId) }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="尺码编码" align="center" prop="sizeCode" min-width="120" />
      <el-table-column label="性别类型" align="center" prop="genderType" width="110">
        <template slot-scope="scope">
          <span>{{ getOptionLabel(genderTypeOptions, scope.row.genderType) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="零售价" align="center" prop="retailPrice" width="110" />
      <el-table-column label="统购价" align="center" prop="groupPrice" width="110" />
      <el-table-column label="库存" align="center" prop="stockNum" width="100" />
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag size="mini" :type="getOptionTagType(enableStatusOptions, scope.row.status)">
            {{ getOptionLabel(enableStatusOptions, scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" min-width="180" show-overflow-tooltip />
      <el-table-column label="操作" width="140" align="center" fixed="right" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            v-hasPermi="['school:goodsSize:edit']"
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            v-hasPermi="['school:goodsSize:remove']"
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

    <!-- 添加或修改商品尺码对话框 -->
    <el-dialog
      :title="title"
      :visible.sync="open"
      width="760px"
      top="6vh"
      append-to-body
      :close-on-click-modal="false"
      class="goods-size-edit-dialog"
    >
      <div class="dialog-scroll">
        <el-form ref="form" :model="form" :rules="rules" label-width="92px" class="goods-size-form">
          <div class="form-section">
            <div class="section-head">
              <div class="section-title">基础信息</div>
              <div class="section-desc">维护尺码所属商品、尺码编码、适用性别与启停状态</div>
            </div>
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="关联商品" prop="goodsId">
                  <el-select
                    v-model="form.goodsId"
                    placeholder="请选择商品"
                    clearable
                    filterable
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
                <el-form-item label="尺码编码" prop="sizeCode">
                  <el-input v-model="form.sizeCode" placeholder="请输入尺码编码，如 S、M、XL" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="性别类型" prop="genderType">
                  <el-select v-model="form.genderType" placeholder="请选择性别类型" clearable style="width: 100%">
                    <el-option
                      v-for="item in genderTypeOptions"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="状态" prop="status">
                  <el-select v-model="form.status" placeholder="请选择状态" clearable style="width: 100%">
                    <el-option
                      v-for="item in enableStatusOptions"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
          </div>

          <div class="form-section">
            <div class="section-head">
              <div class="section-title">价格库存</div>
              <div class="section-desc">维护该尺码的零售价、统购价与可售库存</div>
            </div>
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="零售价" prop="retailPrice">
                  <el-input v-model="form.retailPrice" placeholder="请输入零售价" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="统购价" prop="groupPrice">
                  <el-input v-model="form.groupPrice" placeholder="请输入统购价" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="库存" prop="stockNum">
                  <el-input v-model="form.stockNum" placeholder="请输入库存" />
                </el-form-item>
              </el-col>
            </el-row>
          </div>

          <div class="form-section">
            <div class="section-head">
              <div class="section-title">扩展信息</div>
              <div class="section-desc">按需补充量体信息与备注说明</div>
            </div>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="量体信息" prop="measureJson">
                  <div v-if="measureJsonRawMode" class="measure-editor">
                    <el-input
                      v-model="measureJsonRawValue"
                      type="textarea"
                      :rows="4"
                      placeholder="当前量体信息不是标准 JSON，将按原文保存"
                    />
                    <div class="measure-tip">当前内容无法拆分为量体项，系统会按原文保存。</div>
                  </div>
                  <div v-else class="measure-editor">
                    <template v-if="measureInfoList.length > 0">
                      <div
                        v-for="(item, index) in measureInfoList"
                        :key="index"
                        class="measure-row"
                      >
                        <el-input
                          v-model="item.label"
                          class="measure-label-input"
                          placeholder="项目"
                        />
                        <el-input
                          v-model="item.value"
                          class="measure-value-input"
                          placeholder="内容"
                        />
                        <el-button
                          type="text"
                          icon="el-icon-delete"
                          class="measure-delete"
                          @click="removeMeasureInfo(index)"
                        />
                      </div>
                    </template>
                    <div v-else class="measure-empty">暂无量体信息</div>
                    <el-button
                      type="primary"
                      plain
                      icon="el-icon-plus"
                      size="mini"
                      @click="addMeasureInfo"
                    >添加量体项</el-button>
                  </div>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="备注" prop="remark">
                  <el-input
                    v-model="form.remark"
                    type="textarea"
                    :rows="4"
                    placeholder="选填，补充尺码说明"
                  />
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
import { listGoodsSize, getGoodsSize, delGoodsSize, addGoodsSize, updateGoodsSize } from '@/api/school/goodsSize'
import { listGoods } from '@/api/school/goods'

const MEASURE_FIELD_LABELS = {
  height: '身高',
  weight: '体重',
  heightRange: '身高范围',
  weightRange: '体重范围',
  chest: '胸围',
  waist: '腰围',
  hip: '臀围',
  shoulder: '肩宽',
  sleeve: '袖长',
  length: '衣长'
}

const MEASURE_LABEL_FIELDS = Object.keys(MEASURE_FIELD_LABELS).reduce((labelFields, field) => {
  labelFields[MEASURE_FIELD_LABELS[field]] = field
  return labelFields
}, {})

export default {
  name: 'GoodsSize',
  data() {
    const validateNonNegativeInteger = (rule, value, callback) => {
      if (value === null || value === undefined || value === '') {
        callback()
      } else if (!/^\d+$/.test(String(value))) {
        callback(new Error('请输入非负整数'))
      } else {
        callback()
      }
    }
    const validateMoney = (rule, value, callback) => {
      if (value === null || value === undefined || value === '') {
        callback()
      } else if (!/^(0|[1-9]\d*)(\.\d{1,2})?$/.test(String(value))) {
        callback(new Error('请输入非负金额，最多两位小数'))
      } else {
        callback()
      }
    }
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 商品尺码表格数据
      goodsSizeList: [],
      // 量体信息编辑数据
      measureInfoList: [],
      measureJsonRawMode: false,
      measureJsonRawValue: '',
      // 商品下拉数据
      goodsOptions: [],
      genderTypeOptions: [
        { value: '0', label: '通用' },
        { value: '1', label: '男' },
        { value: '2', label: '女' }
      ],
      enableStatusOptions: [
        { value: '0', label: '停用', tagType: 'info' },
        { value: '1', label: '启用', tagType: 'success' }
      ],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        goodsId: null,
        sizeCode: null,
        genderType: null,
        status: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        goodsId: [
          { required: true, message: '关联商品不能为空', trigger: 'blur' }
        ],
        sizeCode: [
          { required: true, message: '尺码编码不能为空', trigger: 'blur' }
        ],
        genderType: [
          { required: true, message: '性别类型不能为空', trigger: 'change' }
        ],
        retailPrice: [
          { required: true, message: '零售价不能为空', trigger: 'blur' },
          { validator: validateMoney, trigger: 'blur' }
        ],
        groupPrice: [
          { validator: validateMoney, trigger: 'blur' }
        ],
        stockNum: [
          { required: true, message: '库存不能为空', trigger: 'blur' },
          { validator: validateNonNegativeInteger, trigger: 'blur' }
        ],
        status: [
          { required: true, message: '状态，0停用 1启用不能为空', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.getGoodsOptions()
    this.getList()
  },
  methods: {
    /** 查询商品尺码列表 */
    getList() {
      this.loading = true
      listGoodsSize(this.queryParams).then(response => {
        this.goodsSizeList = response.rows || []
        this.total = response.total || 0
      }).finally(() => {
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
      this.form = this.getDefaultForm()
      this.measureInfoList = []
      this.measureJsonRawMode = false
      this.measureJsonRawValue = ''
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
      this.title = '添加商品尺码'
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row && row.id ? row.id : this.ids[0]
      getGoodsSize(id).then(response => {
        this.form = Object.assign(this.getDefaultForm(), response.data || {})
        this.setMeasureInfo(this.form.measureJson)
        this.open = true
        this.title = '修改商品尺码'
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          const payload = Object.assign({}, this.form, {
            measureJson: this.stringifyMeasureInfo(),
            sortNum: this.calculateSizeSortNum(this.form.sizeCode),
            delFlag: this.form.delFlag || '0'
          })
          if (payload.id != null) {
            updateGoodsSize(payload).then(() => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addGoodsSize(payload).then(() => {
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
      const ids = row && row.id ? row.id : this.ids.join(',')
      this.$modal.confirm('是否确认删除商品尺码编号为"' + ids + '"的数据项？').then(function() {
        return delGoodsSize(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('school/goodsSize/export', {
        ...this.queryParams
      }, `goodsSize_${new Date().getTime()}.xlsx`)
    },
    getGoodsOptions() {
      listGoods({ pageNum: 1, pageSize: 1000, status: '1' }).then(response => {
        this.goodsOptions = response.rows || []
      })
    },
    formatGoodsOption(item) {
      return item.goodsCode ? `${item.goodsName}（${item.goodsCode}）` : item.goodsName
    },
    getGoodsName(goodsId) {
      const goods = this.goodsOptions.find(item => String(item.id) === String(goodsId))
      return goods ? this.formatGoodsOption(goods) : goodsId
    },
    getOptionLabel(options, value) {
      const option = options.find(item => item.value === String(value))
      return option ? option.label : value || '-'
    },
    getOptionTagType(options, value) {
      const option = options.find(item => item.value === String(value))
      return option ? option.tagType : 'info'
    },
    getDefaultForm() {
      return {
        id: null,
        goodsId: null,
        sizeCode: null,
        sortNum: null,
        genderType: '0',
        measureJson: null,
        retailPrice: null,
        groupPrice: null,
        stockNum: null,
        status: '1',
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null,
        delFlag: '0'
      }
    },
    calculateSizeSortNum(sizeCode) {
      const normalizedSize = String(sizeCode || '').trim().toUpperCase()
      if (/^\d+$/.test(normalizedSize)) {
        return Number(normalizedSize)
      }
      const sizeSortMap = {
        XS: 0,
        S: 1,
        M: 2,
        L: 3,
        XL: 4,
        XXL: 5,
        XXXL: 6
      }
      if (Object.prototype.hasOwnProperty.call(sizeSortMap, normalizedSize)) {
        return sizeSortMap[normalizedSize]
      }
      const numberedXlMatch = normalizedSize.match(/^(\d+)XL$/)
      if (numberedXlMatch) {
        return Number(numberedXlMatch[1]) + 3
      }
      const repeatedXlMatch = normalizedSize.match(/^(X{2,})L$/)
      if (repeatedXlMatch) {
        return repeatedXlMatch[1].length + 3
      }
      return 999
    },
    getMeasureLabel(field) {
      return MEASURE_FIELD_LABELS[field] || field
    },
    getMeasureField(item) {
      const label = String(item.label || '').trim()
      if (item.key && this.getMeasureLabel(item.key) === label) {
        return item.key
      }
      return MEASURE_LABEL_FIELDS[label] || label || item.key
    },
    formatMeasureValue(value) {
      if (value === null || value === undefined) {
        return ''
      }
      if (typeof value === 'object') {
        return JSON.stringify(value)
      }
      return String(value)
    },
    setMeasureInfo(measureJson) {
      this.measureInfoList = []
      this.measureJsonRawMode = false
      this.measureJsonRawValue = ''
      if (!measureJson) {
        return
      }
      try {
        const parsedMeasure = typeof measureJson === 'string' ? JSON.parse(measureJson) : measureJson
        if (!parsedMeasure || Array.isArray(parsedMeasure) || typeof parsedMeasure !== 'object') {
          throw new Error('量体信息不是对象格式')
        }
        this.measureInfoList = Object.keys(parsedMeasure).map(field => {
          return {
            key: field,
            label: this.getMeasureLabel(field),
            value: this.formatMeasureValue(parsedMeasure[field])
          }
        })
      } catch (error) {
        this.measureJsonRawMode = true
        this.measureJsonRawValue = String(measureJson)
      }
    },
    addMeasureInfo() {
      this.measureInfoList.push({
        key: '',
        label: '',
        value: ''
      })
    },
    removeMeasureInfo(index) {
      this.measureInfoList.splice(index, 1)
    },
    stringifyMeasureInfo() {
      if (this.measureJsonRawMode) {
        const rawValue = String(this.measureJsonRawValue || '').trim()
        return rawValue || null
      }
      const measureInfo = {}
      this.measureInfoList.forEach(item => {
        const field = this.getMeasureField(item)
        const value = String(item.value || '').trim()
        if (field && value) {
          measureInfo[field] = value
        }
      })
      return Object.keys(measureInfo).length > 0 ? JSON.stringify(measureInfo) : null
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

.goods-info-cell {
  display: flex;
  align-items: center;
  min-width: 0;
}

.goods-title {
  overflow: hidden;
  font-size: 14px;
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

.measure-editor {
  width: 100%;
}

.measure-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.measure-label-input {
  flex: 0 0 112px;
  margin-right: 8px;
}

.measure-value-input {
  flex: 1;
  min-width: 0;
}

.measure-delete {
  flex: 0 0 32px;
  margin-left: 4px;
  color: #f56c6c;
}

.measure-empty {
  margin-bottom: 8px;
  padding: 10px 12px;
  color: #7a8798;
  background: #fff;
  border: 1px dashed #d8e0ea;
  border-radius: 6px;
}

.measure-tip {
  margin-top: 6px;
  font-size: 12px;
  line-height: 1.5;
  color: #7a8798;
}

.goods-size-edit-dialog ::v-deep .el-dialog {
  max-width: calc(100vw - 32px);
}

@media (max-width: 768px) {
  .goods-size-form ::v-deep .el-col {
    width: 100%;
  }

  .dialog-scroll {
    max-height: 64vh;
  }

  .measure-row {
    flex-wrap: wrap;
  }

  .measure-label-input,
  .measure-value-input {
    flex: 0 0 100%;
    margin-right: 0;
  }

  .measure-delete {
    margin-left: 0;
  }
}
</style>
