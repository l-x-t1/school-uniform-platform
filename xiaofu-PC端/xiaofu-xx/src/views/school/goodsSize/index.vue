<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="68px">
      <el-form-item label="关联商品" prop="goodsId">
        <el-select v-model="queryParams.goodsId" placeholder="请选择商品" clearable filterable>
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
      <el-form-item label="排序字段，根据商品尺码依次增加，如 S=1 M=2 L=3 XL=4" prop="sortNum">
        <el-input
          v-model="queryParams.sortNum"
          placeholder="请输入排序字段，根据商品尺码依次增加，如 S=1 M=2 L=3 XL=4"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="零售价" prop="retailPrice">
        <el-input
          v-model="queryParams.retailPrice"
          placeholder="请输入零售价"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="统购价" prop="groupPrice">
        <el-input
          v-model="queryParams.groupPrice"
          placeholder="请输入统购价"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="库存" prop="stockNum">
        <el-input
          v-model="queryParams.stockNum"
          placeholder="请输入库存"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="性别类型" prop="genderType">
        <el-select v-model="queryParams.genderType" placeholder="请选择性别类型" clearable>
          <el-option
            v-for="item in genderTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
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

    <el-table v-loading="loading" :data="goodsSizeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="关联商品" align="center" prop="goodsId">
        <template slot-scope="scope">
          <span>{{ getGoodsName(scope.row.goodsId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="尺码编码" align="center" prop="sizeCode" />
      <el-table-column label="排序字段，根据商品尺码依次增加，如 S=1 M=2 L=3 XL=4" align="center" prop="sortNum" />
      <el-table-column label="性别类型" align="center" prop="genderType">
        <template slot-scope="scope">
          <span>{{ getOptionLabel(genderTypeOptions, scope.row.genderType) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="零售价" align="center" prop="retailPrice" />
      <el-table-column label="统购价" align="center" prop="groupPrice" />
      <el-table-column label="库存" align="center" prop="stockNum" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <el-tag size="mini" :type="scope.row.status === '1' ? 'success' : 'info'">
            {{ getOptionLabel(enableStatusOptions, scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
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
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="关联商品" prop="goodsId">
          <el-select v-model="form.goodsId" placeholder="请选择商品" clearable filterable>
            <el-option
              v-for="item in goodsOptions"
              :key="item.id"
              :label="formatGoodsOption(item)"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="尺码编码" prop="sizeCode">
          <el-input v-model="form.sizeCode" placeholder="请输入尺码编码" />
        </el-form-item>
        <el-form-item label="排序字段，根据商品尺码依次增加，如 S=1 M=2 L=3 XL=4" prop="sortNum">
          <el-input v-model="form.sortNum" placeholder="请输入排序字段，根据商品尺码依次增加，如 S=1 M=2 L=3 XL=4" />
        </el-form-item>
        <el-form-item label="量体规则JSON文本" prop="measureJson">
          <el-input v-model="form.measureJson" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="性别类型" prop="genderType">
          <el-select v-model="form.genderType" placeholder="请选择性别类型" clearable>
            <el-option
              v-for="item in genderTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="零售价" prop="retailPrice">
          <el-input v-model="form.retailPrice" placeholder="请输入零售价" />
        </el-form-item>
        <el-form-item label="统购价" prop="groupPrice">
          <el-input v-model="form.groupPrice" placeholder="请输入统购价" />
        </el-form-item>
        <el-form-item label="库存" prop="stockNum">
          <el-input v-model="form.stockNum" placeholder="请输入库存" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" clearable>
            <el-option
              v-for="item in enableStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
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
import { listGoodsSize, getGoodsSize, delGoodsSize, addGoodsSize, updateGoodsSize } from '@/api/school/goodsSize'
import { listGoods } from '@/api/school/goods'

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
      // 商品下拉数据
      goodsOptions: [],
      genderTypeOptions: [
        { value: '0', label: '通用' },
        { value: '1', label: '男' },
        { value: '2', label: '女' }
      ],
      enableStatusOptions: [
        { value: '0', label: '停用' },
        { value: '1', label: '启用' }
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
        sortNum: null,
        genderType: null,
        retailPrice: null,
        groupPrice: null,
        stockNum: null,
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
        sortNum: [
          { required: true, message: '排序字段，根据商品尺码依次增加，如 S=1 M=2 L=3 XL=4不能为空', trigger: 'blur' },
          { validator: validateNonNegativeInteger, trigger: 'blur' }
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
        this.goodsSizeList = response.rows
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
      const id = row.id || this.ids
      getGoodsSize(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = '修改商品尺码'
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          this.form.delFlag = this.form.delFlag || '0'
          if (this.form.id != null) {
            updateGoodsSize(this.form).then(response => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addGoodsSize(this.form).then(response => {
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
    }
  }
}
</script>
