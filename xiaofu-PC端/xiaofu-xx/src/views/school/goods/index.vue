<template>
  <div class="app-container goods-page">
    <el-form
      v-show="showSearch"
      ref="queryForm"
      :model="queryParams"
      :inline="true"
      size="small"
      label-width="84px"
      class="query-form"
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
      <el-form-item label="商品编码" prop="goodsCode">
        <el-input
          v-model="queryParams.goodsCode"
          placeholder="请输入商品编码"
          clearable
          style="width: 220px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="品类名称" prop="categoryName">
        <el-input
          v-model="queryParams.categoryName"
          placeholder="请输入品类名称"
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
      <el-form-item label="销售类型" prop="saleType">
        <el-select
          v-model="queryParams.saleType"
          placeholder="请选择销售类型"
          clearable
          style="width: 220px"
        >
          <el-option
            v-for="item in saleTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="商品状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择商品状态"
          clearable
          style="width: 220px"
        >
          <el-option
            v-for="item in goodsStatusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="上架状态" prop="publishStatus">
        <el-select
          v-model="queryParams.publishStatus"
          placeholder="请选择上架状态"
          clearable
          style="width: 220px"
        >
          <el-option
            v-for="item in publishStatusOptions"
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
          v-hasPermi="['school:goods:add']"
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['school:goods:edit']"
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
          v-hasPermi="['school:goods:remove']"
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
          v-hasPermi="['school:goods:export']"
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
        >导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['school:goods:import']"
          type="info"
          plain
          icon="el-icon-upload2"
          size="mini"
          @click="handleImport"
        >导入</el-button>
      </el-col>
      <right-toolbar :show-search.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table
      v-loading="loading"
      :data="goodsList"
      border
      class="goods-table"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="商品信息" min-width="320">
        <template slot-scope="scope">
          <div class="goods-info-cell">
            <image-preview :src="scope.row.mainImage" :width="56" :height="56" />
            <div class="goods-info-meta">
              <div class="goods-title">{{ scope.row.goodsName || "--" }}</div>
              <div class="goods-subline">
                <span>编码：{{ scope.row.goodsCode || "--" }}</span>
                <span>品类：{{ scope.row.categoryName || "--" }}</span>
              </div>
              <div class="goods-subline">
                <span>品牌：{{ scope.row.brandName || "--" }}</span>
              </div>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        label="供应商名称"
        prop="supplierName"
        min-width="180"
        show-overflow-tooltip
      />
      <el-table-column label="销售类型" width="110" align="center">
        <template slot-scope="scope">
          <el-tag :type="getOptionTagType(saleTypeOptions, scope.row.saleType)">
            {{ getOptionLabel(saleTypeOptions, scope.row.saleType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="商品状态" width="110" align="center">
        <template slot-scope="scope">
          <el-tag :type="getOptionTagType(goodsStatusOptions, scope.row.status)">
            {{ getOptionLabel(goodsStatusOptions, scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="上架状态" width="110" align="center">
        <template slot-scope="scope">
          <el-tag :type="getOptionTagType(publishStatusOptions, scope.row.publishStatus)">
            {{ getOptionLabel(publishStatusOptions, scope.row.publishStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="上架时间" prop="publishTime" width="140" align="center">
        <template slot-scope="scope">
          <span>{{ formatDate(scope.row.publishTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="下架时间" prop="offShelfTime" width="140" align="center">
        <template slot-scope="scope">
          <span>{{ formatDate(scope.row.offShelfTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140" align="center" fixed="right" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            v-hasPermi="['school:goods:edit']"
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            v-hasPermi="['school:goods:remove']"
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
      :title="title"
      :visible.sync="open"
      width="1080px"
      top="4vh"
      append-to-body
      :close-on-click-modal="false"
      class="goods-edit-dialog"
    >
      <div class="dialog-scroll">
        <el-form ref="form" :model="form" :rules="rules" label-width="96px" class="goods-form">
          <div class="form-section">
            <div class="section-head">
              <div class="section-title">基础信息</div>
              <div class="section-desc">维护商品基础资料与供应商信息</div>
            </div>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="商品名称" prop="goodsName">
                  <el-input v-model="form.goodsName" placeholder="请输入商品名称" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="商品编码" prop="goodsCode">
                  <el-input v-model="form.goodsCode" placeholder="请输入商品编码" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="品类名称" prop="categoryName">
                  <el-input v-model="form.categoryName" placeholder="请输入品类名称" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="品牌名称" prop="brandName">
                  <el-input v-model="form.brandName" placeholder="请输入品牌名称" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="供应商ID" prop="supplierId">
                  <el-input v-model="form.supplierId" placeholder="请输入供应商ID" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="供应商名称" prop="supplierName">
                  <el-input v-model="form.supplierName" placeholder="请输入供应商名称" />
                </el-form-item>
              </el-col>
            </el-row>
          </div>

          <div class="form-section">
            <div class="section-head">
              <div class="section-title">上架与展示</div>
              <div class="section-desc">维护商品销售方式、上下架状态与展示信息</div>
            </div>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="销售类型" prop="saleType">
                  <el-select v-model="form.saleType" placeholder="请选择销售类型" style="width: 100%">
                    <el-option
                      v-for="item in saleTypeOptions"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="商品状态" prop="status">
                  <el-select v-model="form.status" placeholder="请选择商品状态" style="width: 100%">
                    <el-option
                      v-for="item in goodsStatusOptions"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="上架状态" prop="publishStatus">
                  <el-select v-model="form.publishStatus" placeholder="请选择上架状态" style="width: 100%">
                    <el-option
                      v-for="item in publishStatusOptions"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="排序值" prop="sortNum">
                  <el-input v-model="form.sortNum" placeholder="请输入排序值" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="上架时间" prop="publishTime">
                  <el-date-picker
                    v-model="form.publishTime"
                    clearable
                    type="date"
                    value-format="yyyy-MM-dd"
                    placeholder="请选择上架时间"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="下架时间" prop="offShelfTime">
                  <el-date-picker
                    v-model="form.offShelfTime"
                    clearable
                    type="date"
                    value-format="yyyy-MM-dd"
                    placeholder="请选择下架时间"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="商品主图" prop="mainImage">
                  <image-upload v-model="form.mainImage" />
                </el-form-item>
              </el-col>
            </el-row>
          </div>

          <div class="form-section">
            <div class="section-head">
              <div class="section-title">商品详情</div>
              <div class="section-desc">商品详情优先维护富文本内容，扩展字段按需填写</div>
            </div>
            <el-form-item label="详情内容" prop="detailContent">
              <editor v-model="form.detailContent" :min-height="220" />
            </el-form-item>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="详情图配置" prop="detailImagesJson">
                  <el-input
                    v-model="form.detailImagesJson"
                    type="textarea"
                    :rows="4"
                    placeholder="选填，支持保存详情图 JSON 配置"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="补充说明" prop="remark">
                  <el-input
                    v-model="form.remark"
                    type="textarea"
                    :rows="4"
                    placeholder="选填，补充商品说明或备注"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </div>

          <div class="form-section">
            <div class="section-head">
              <div class="section-title">商品尺码</div>
              <div class="section-desc">在商品编辑流程中同步维护尺码、价格、库存与启停状态</div>
            </div>
            <div class="size-toolbar">
              <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAddXfptGoodsSize">添加尺码</el-button>
              <el-button type="danger" icon="el-icon-delete" size="mini" @click="handleDeleteXfptGoodsSize">删除尺码</el-button>
            </div>
            <el-table
              ref="xfptGoodsSize"
              :data="xfptGoodsSizeList"
              border
              size="mini"
              :row-class-name="rowXfptGoodsSizeIndex"
              @selection-change="handleXfptGoodsSizeSelectionChange"
            >
              <el-table-column type="selection" width="50" align="center" />
              <el-table-column label="序号" align="center" prop="index" width="60" />
              <el-table-column label="尺码" prop="sizeCode" min-width="120">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.sizeCode" placeholder="请输入尺码" />
                </template>
              </el-table-column>
              <el-table-column label="排序" prop="sortNum" width="100">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.sortNum" placeholder="如 1" />
                </template>
              </el-table-column>
              <el-table-column label="性别" prop="genderType" width="120">
                <template slot-scope="scope">
                  <el-select v-model="scope.row.genderType" placeholder="请选择" style="width: 100%">
                    <el-option
                      v-for="item in genderTypeOptions"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="零售价" prop="retailPrice" width="130">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.retailPrice" placeholder="请输入零售价" />
                </template>
              </el-table-column>
              <el-table-column label="统购价" prop="groupPrice" width="130">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.groupPrice" placeholder="请输入统购价" />
                </template>
              </el-table-column>
              <el-table-column label="库存" prop="stockNum" width="110">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.stockNum" placeholder="请输入库存" />
                </template>
              </el-table-column>
              <el-table-column label="状态" prop="status" width="120">
                <template slot-scope="scope">
                  <el-select v-model="scope.row.status" placeholder="请选择" style="width: 100%">
                    <el-option
                      v-for="item in sizeStatusOptions"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
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

    <el-dialog :title="upload.title" :visible.sync="upload.open" width="400px" append-to-body>
      <el-upload
        ref="upload"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :on-error="handleFileError"
        :auto-upload="false"
        drag
      >
        <i class="el-icon-upload" />
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div slot="tip" class="el-upload__tip text-center">
          <div slot="tip" class="el-upload__tip">
            <el-checkbox v-model="upload.updateSupport" />是否更新已经存在的商品数据
          </div>
          <span>仅允许导入 xls、xlsx 格式文件。</span>
          <el-link
            :underline="false"
            style="font-size: 12px; vertical-align: baseline"
            @click.stop="importTemplate"
          >下载模板</el-link>
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listGoods, getGoods, delGoods, addGoods, updateGoods } from '@/api/school/goods'
import { getToken } from '@/utils/auth'

const SALE_TYPE_OPTIONS = [
  { label: '统购', value: '1', tagType: 'warning' },
  { label: '零售', value: '2', tagType: 'success' },
  { label: '统购零售', value: '3', tagType: '' }
]

const GOODS_STATUS_OPTIONS = [
  { label: '草稿', value: '0', tagType: 'info' },
  { label: '启用', value: '1', tagType: 'success' },
  { label: '停用', value: '2', tagType: 'danger' }
]

const PUBLISH_STATUS_OPTIONS = [
  { label: '未上架', value: '0', tagType: 'info' },
  { label: '已上架', value: '1', tagType: 'success' },
  { label: '已下架', value: '2', tagType: 'warning' }
]

const GENDER_TYPE_OPTIONS = [
  { label: '通用', value: '0' },
  { label: '男', value: '1' },
  { label: '女', value: '2' }
]

const SIZE_STATUS_OPTIONS = [
  { label: '停用', value: '0' },
  { label: '启用', value: '1' }
]

export default {
  name: 'Goods',
  data() {
    return {
      loading: true,
      ids: [],
      checkedXfptGoodsSize: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      goodsList: [],
      xfptGoodsSizeList: [],
      title: '',
      open: false,
      pageActivated: false,
      saleTypeOptions: SALE_TYPE_OPTIONS,
      goodsStatusOptions: GOODS_STATUS_OPTIONS,
      publishStatusOptions: PUBLISH_STATUS_OPTIONS,
      genderTypeOptions: GENDER_TYPE_OPTIONS,
      sizeStatusOptions: SIZE_STATUS_OPTIONS,
      upload: {
        open: false,
        title: '',
        isUploading: false,
        updateSupport: false,
        headers: { Authorization: 'Bearer ' + getToken() },
        url: process.env.VUE_APP_BASE_API + '/school/goods/importData'
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        goodsName: null,
        goodsCode: null,
        categoryName: null,
        supplierName: null,
        saleType: null,
        status: null,
        publishStatus: null
      },
      form: {},
      rules: {
        goodsName: [
          { required: true, message: '商品名称不能为空', trigger: 'blur' }
        ],
        goodsCode: [
          { required: true, message: '商品编码不能为空', trigger: 'blur' }
        ],
        categoryName: [
          { required: true, message: '品类名称不能为空', trigger: 'blur' }
        ],
        supplierId: [
          { required: true, message: '供应商ID不能为空', trigger: 'blur' }
        ],
        supplierName: [
          { required: true, message: '供应商名称不能为空', trigger: 'blur' }
        ],
        saleType: [
          { required: true, message: '请选择销售类型', trigger: 'change' }
        ],
        status: [
          { required: true, message: '请选择商品状态', trigger: 'change' }
        ],
        publishStatus: [
          { required: true, message: '请选择上架状态', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  activated() {
    if (this.pageActivated) {
      this.getList()
      return
    }
    this.pageActivated = true
  },
  methods: {
    getDefaultForm() {
      return {
        id: null,
        goodsName: null,
        goodsCode: null,
        categoryName: null,
        supplierId: null,
        supplierName: null,
        brandName: null,
        mainImage: null,
        detailImagesJson: null,
        detailContent: null,
        saleType: null,
        status: null,
        publishStatus: null,
        publishTime: null,
        offShelfTime: null,
        sortNum: null,
        remark: null,
        delFlag: '0'
      }
    },
    getDefaultSizeRow() {
      return {
        sizeCode: '',
        sortNum: String(this.xfptGoodsSizeList.length + 1),
        genderType: '0',
        retailPrice: '',
        groupPrice: '',
        stockNum: '',
        status: '1',
        remark: ''
      }
    },
    /** 查询商品信息列表 */
    getList() {
      this.loading = true
      listGoods(this.queryParams).then(response => {
        this.goodsList = response.rows || []
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
      this.xfptGoodsSizeList = []
      this.checkedXfptGoodsSize = []
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
      this.multiple = selection.length === 0
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增商品'
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row && row.id ? row.id : this.ids[0]
      getGoods(id).then(response => {
        const data = response.data || {}
        this.form = Object.assign(this.getDefaultForm(), data)
        this.xfptGoodsSizeList = this.decorateSizeList(data.xfptGoodsSizeList || [])
        this.open = true
        this.title = '修改商品'
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (!valid) {
          return
        }
        const normalizedSizeList = this.normalizeSizeList(this.xfptGoodsSizeList)
        const sizeMessage = this.validateSizeList(normalizedSizeList)
        if (sizeMessage) {
          this.$modal.msgError(sizeMessage)
          return
        }
        this.xfptGoodsSizeList = this.decorateSizeList(normalizedSizeList)
        const payload = Object.assign({}, this.form, {
          xfptGoodsSizeList: normalizedSizeList
        })
        if (payload.id != null) {
          updateGoods(payload).then(() => {
            this.$modal.msgSuccess('修改成功')
            this.open = false
            this.getList()
          })
        } else {
          addGoods(payload).then(() => {
            this.$modal.msgSuccess('新增成功')
            this.open = false
            this.getList()
          })
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row && row.id ? row.id : this.ids.join(',')
      this.$modal.confirm('是否确认删除商品信息编号为"' + ids + '"的数据项？').then(() => {
        return delGoods(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    /** 商品尺码序号 */
    rowXfptGoodsSizeIndex({ row, rowIndex }) {
      row.index = rowIndex + 1
    },
    /** 商品尺码添加按钮操作 */
    handleAddXfptGoodsSize() {
      this.xfptGoodsSizeList.push(this.getDefaultSizeRow())
    },
    /** 商品尺码删除按钮操作 */
    handleDeleteXfptGoodsSize() {
      if (this.checkedXfptGoodsSize.length === 0) {
        this.$modal.msgError('请先选择要删除的商品尺码数据')
        return
      }
      this.xfptGoodsSizeList = this.xfptGoodsSizeList.filter(item => {
        return this.checkedXfptGoodsSize.indexOf(item.index) === -1
      })
      this.checkedXfptGoodsSize = []
    },
    /** 复选框选中数据 */
    handleXfptGoodsSizeSelectionChange(selection) {
      this.checkedXfptGoodsSize = selection.map(item => item.index)
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('school/goods/export', {
        ...this.queryParams
      }, `goods_${new Date().getTime()}.xlsx`)
    },
    /** 导入按钮操作 */
    handleImport() {
      this.upload.title = '商品信息导入'
      this.upload.open = true
      this.upload.isUploading = false
      this.upload.updateSupport = false
      this.$nextTick(() => {
        if (this.$refs.upload) {
          this.$refs.upload.clearFiles()
        }
      })
    },
    /** 下载模板操作 */
    importTemplate() {
      this.download('school/goods/importTemplate', {}, `goods_template_${new Date().getTime()}.xlsx`)
    },
    // 文件上传中处理
    handleFileUploadProgress() {
      this.upload.isUploading = true
    },
    // 文件上传成功处理
    handleFileSuccess(response) {
      this.upload.open = false
      this.upload.isUploading = false
      this.$refs.upload.clearFiles()
      if (response.code !== 200) {
        this.$modal.msgError(response.msg || '导入失败，请稍后重试')
        return
      }
      this.$alert(
        '<div style=\'overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;\'>' + response.msg + '</div>',
        '导入结果',
        { dangerouslyUseHTMLString: true }
      )
      this.getList()
    },
    // 文件上传失败处理
    handleFileError() {
      this.upload.isUploading = false
      this.$modal.msgError('导入失败，请检查文件后重试')
    },
    // 提交上传文件
    submitFileForm() {
      if (!this.$refs.upload || this.$refs.upload.uploadFiles.length === 0) {
        this.$modal.msgError('请先选择要导入的文件')
        return
      }
      this.$refs.upload.submit()
    },
    getOptionLabel(options, value) {
      const option = options.find(item => String(item.value) === String(value))
      return option ? option.label : '--'
    },
    getOptionTagType(options, value) {
      const option = options.find(item => String(item.value) === String(value))
      return option ? option.tagType : 'info'
    },
    formatDate(value) {
      return value ? this.parseTime(value, '{y}-{m}-{d}') : '--'
    },
    decorateSizeList(sizeList) {
      return (sizeList || []).map((item, index) => {
        return Object.assign({ index: index + 1 }, this.normalizeSizeRow(item))
      })
    },
    normalizeSizeList(sizeList) {
      return (sizeList || []).map(item => this.normalizeSizeRow(item))
    },
    normalizeSizeRow(item) {
      const row = Object.assign({}, item)
      delete row.index
      row.sizeCode = row.sizeCode == null ? '' : String(row.sizeCode).trim()
      row.sortNum = row.sortNum === '' ? null : row.sortNum
      row.genderType = row.genderType == null || row.genderType === '' ? '0' : String(row.genderType)
      row.retailPrice = row.retailPrice === '' ? null : row.retailPrice
      row.groupPrice = row.groupPrice === '' ? null : row.groupPrice
      row.stockNum = row.stockNum === '' ? null : row.stockNum
      row.status = row.status == null || row.status === '' ? '1' : String(row.status)
      row.remark = row.remark == null ? '' : row.remark
      return row
    },
    validateSizeList(sizeList) {
      const decimalPattern = /^(0|[1-9]\d*)(\.\d{1,2})?$/
      const integerPattern = /^(0|[1-9]\d*)$/
      const seen = new Set()
      for (let index = 0; index < sizeList.length; index++) {
        const row = sizeList[index]
        const rowNo = index + 1
        if (!row.sizeCode) {
          return `第 ${rowNo} 行尺码不能为空`
        }
        const sizeKey = row.sizeCode.toUpperCase()
        if (seen.has(sizeKey)) {
          return `第 ${rowNo} 行尺码重复，请调整后再保存`
        }
        seen.add(sizeKey)
        if (row.sortNum != null && row.sortNum !== '' && !integerPattern.test(String(row.sortNum))) {
          return `第 ${rowNo} 行排序值必须为非负整数`
        }
        if (row.retailPrice != null && row.retailPrice !== '' && !decimalPattern.test(String(row.retailPrice))) {
          return `第 ${rowNo} 行零售价格式不正确，最多保留两位小数`
        }
        if (row.groupPrice != null && row.groupPrice !== '' && !decimalPattern.test(String(row.groupPrice))) {
          return `第 ${rowNo} 行统购价格式不正确，最多保留两位小数`
        }
        if (row.stockNum != null && row.stockNum !== '' && !integerPattern.test(String(row.stockNum))) {
          return `第 ${rowNo} 行库存必须为非负整数`
        }
      }
      return ''
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
}

.goods-info-meta {
  min-width: 0;
  margin-left: 14px;
}

.goods-title {
  margin-bottom: 6px;
  font-size: 14px;
  font-weight: 600;
  color: #243447;
}

.goods-subline {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
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

.size-toolbar {
  display: flex;
  margin-bottom: 12px;
}

.size-toolbar .el-button + .el-button {
  margin-left: 8px;
}

@media (max-width: 1200px) {
  .dialog-scroll {
    max-height: 64vh;
  }

  .goods-subline {
    gap: 8px;
  }
}
</style>
