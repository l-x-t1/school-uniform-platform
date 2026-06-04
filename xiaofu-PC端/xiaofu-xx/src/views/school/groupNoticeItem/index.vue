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
      <el-form-item label="商品名称" prop="goodsName">
        <el-input
          v-model="queryParams.goodsName"
          placeholder="请输入商品名称"
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
      <el-form-item label="适用年级" prop="gradeNames">
        <el-input
          v-model="queryParams.gradeNames"
          placeholder="请输入适用年级"
          clearable
          style="width: 220px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="单位" prop="unitName">
        <el-input
          v-model="queryParams.unitName"
          placeholder="请输入单位"
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
          v-hasPermi="['school:groupNoticeItem:add']"
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['school:groupNoticeItem:edit']"
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
          v-hasPermi="['school:groupNoticeItem:remove']"
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
          v-hasPermi="['school:groupNoticeItem:export']"
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
      :data="groupNoticeItemList"
      border
      class="business-table"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="商品信息" min-width="260">
        <template slot-scope="scope">
          <div class="info-cell">
            <div class="info-title">{{ formatValue(scope.row.goodsName) }}</div>
            <div class="info-subline">单位：{{ formatValue(scope.row.unitName) }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="公告信息" min-width="280">
        <template slot-scope="scope">
          <div class="info-cell">
            <div class="info-title">{{ formatValue(scope.row.noticeTitle) }}</div>
            <div class="info-subline">公告编号：{{ formatValue(scope.row.noticeCode) }}</div>
            <div class="info-subline">学校名称：{{ formatValue(scope.row.schoolName) }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="适用年级" prop="gradeNames" min-width="160" show-overflow-tooltip />
      <el-table-column label="采购数量" prop="buyNum" width="110" align="center" />
      <el-table-column label="价格说明" prop="priceDesc" min-width="220" show-overflow-tooltip />
      <el-table-column label="操作" width="210" align="center" fixed="right" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)">详情</el-button>
          <el-button
            v-hasPermi="['school:groupNoticeItem:edit']"
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            v-hasPermi="['school:groupNoticeItem:remove']"
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
      title="统购公告商品详情"
      :visible.sync="detailOpen"
      width="960px"
      top="6vh"
      append-to-body
      @closed="resetDetail"
    >
      <div v-loading="detailLoading" class="dialog-scroll">
        <div class="form-section detail-section">
          <div class="section-head">
            <div class="section-title">基础信息</div>
            <div class="section-desc">展示商品、公告、学校和采购信息</div>
          </div>
          <div class="detail-kv-grid">
            <div class="detail-kv">
              <span class="detail-label">商品名称</span>
              <span class="detail-value">{{ formatValue(detailForm.goodsName) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">单位</span>
              <span class="detail-value">{{ formatValue(detailForm.unitName) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">采购数量</span>
              <span class="detail-value">{{ formatValue(detailForm.buyNum) }}</span>
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
            <div class="detail-kv detail-kv-wide">
              <span class="detail-label">适用年级</span>
              <span class="detail-value text-wrap">{{ formatValue(detailForm.gradeNames) }}</span>
            </div>
            <div class="detail-kv detail-kv-wide">
              <span class="detail-label">价格说明</span>
              <span class="detail-value text-wrap">{{ formatValue(detailForm.priceDesc) }}</span>
            </div>
          </div>
        </div>

        <div class="form-section detail-section">
          <div class="section-head">
            <div class="section-title">补充说明</div>
            <div class="section-desc">备注与尺码规则默认收纳到详情里</div>
          </div>
          <div class="detail-block">
            <div class="detail-block-title">可选尺码规则 JSON</div>
            <pre class="detail-pre">{{ formatMultiline(detailForm.sizeRuleJson) }}</pre>
          </div>
          <div class="detail-block">
            <div class="detail-block-title">备注</div>
            <div class="detail-text">{{ formatMultiline(detailForm.remark) }}</div>
          </div>
        </div>

        <div class="form-section detail-section">
          <div class="section-head">
            <div class="section-title">补充字段</div>
            <div class="section-desc">保留原始关联字段，主列表中默认不展示</div>
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
              <span class="detail-label">商品ID</span>
              <span class="detail-value">{{ formatValue(detailForm.goodsId) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">适用年级ID</span>
              <span class="detail-value text-wrap">{{ formatValue(detailForm.gradeIds) }}</span>
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
        <el-form-item label="商品ID" prop="goodsId">
          <el-input v-model="form.goodsId" placeholder="请输入商品ID" />
        </el-form-item>
        <el-form-item label="商品名称" prop="goodsName">
          <el-input v-model="form.goodsName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="可选尺码规则" prop="sizeRuleJson">
          <el-input v-model="form.sizeRuleJson" type="textarea" :rows="3" placeholder="请输入尺码规则 JSON" />
        </el-form-item>
        <el-form-item label="单位" prop="unitName">
          <el-input v-model="form.unitName" placeholder="请输入单位" />
        </el-form-item>
        <el-form-item label="采购数量" prop="buyNum">
          <el-input v-model="form.buyNum" placeholder="请输入采购数量" />
        </el-form-item>
        <el-form-item label="价格说明" prop="priceDesc">
          <el-input v-model="form.priceDesc" type="textarea" :rows="3" placeholder="请输入价格区间或价格说明" />
        </el-form-item>
        <el-form-item label="适用年级ID" prop="gradeIds">
          <el-input v-model="form.gradeIds" type="textarea" :rows="2" placeholder="请输入适用年级ID" />
        </el-form-item>
        <el-form-item label="适用年级名称" prop="gradeNames">
          <el-input v-model="form.gradeNames" type="textarea" :rows="2" placeholder="请输入适用年级名称" />
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
import { listGroupNoticeItem, getGroupNoticeItem, delGroupNoticeItem, addGroupNoticeItem, updateGroupNoticeItem } from '@/api/school/groupNoticeItem'

export default {
  name: 'GroupNoticeItem',
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      groupNoticeItemList: [],
      title: '',
      open: false,
      detailOpen: false,
      detailLoading: false,
      detailForm: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        goodsName: null,
        noticeCode: null,
        noticeTitle: null,
        schoolName: null,
        gradeNames: null,
        unitName: null,
        noticeId: null,
        goodsId: null,
        deptId: null,
        buyNum: null
      },
      form: {},
      rules: {
        noticeId: [
          { required: true, message: '公告ID不能为空', trigger: 'blur' }
        ],
        goodsId: [
          { required: true, message: '商品ID不能为空', trigger: 'blur' }
        ],
        goodsName: [
          { required: true, message: '商品名称不能为空', trigger: 'blur' }
        ],
        buyNum: [
          { required: true, message: '采购数量不能为空', trigger: 'blur' }
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
        goodsId: null,
        goodsName: null,
        sizeRuleJson: null,
        unitName: null,
        buyNum: null,
        priceDesc: null,
        gradeIds: null,
        gradeNames: null,
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
      listGroupNoticeItem(this.queryParams).then(response => {
        this.groupNoticeItemList = response.rows || []
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
      this.title = '添加统购公告商品'
    },
    handleView(row) {
      const id = row && row.id ? row.id : this.ids[0]
      if (!id) {
        this.$modal.msgError('请选择要查看的公告商品')
        return
      }
      this.detailOpen = true
      this.detailLoading = true
      getGroupNoticeItem(id).then(response => {
        this.detailForm = Object.assign(this.getDefaultForm(), response.data || {})
      }).finally(() => {
        this.detailLoading = false
      })
    },
    handleUpdate(row) {
      this.reset()
      const id = row && row.id ? row.id : this.ids[0]
      getGroupNoticeItem(id).then(response => {
        this.form = Object.assign(this.getDefaultForm(), response.data || {})
        this.open = true
        this.title = '修改统购公告商品'
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        const request = this.form.id != null ? updateGroupNoticeItem(this.form) : addGroupNoticeItem(this.form)
        request.then(() => {
          this.$modal.msgSuccess(this.form.id != null ? '修改成功' : '新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const ids = row && row.id ? row.id : this.ids.join(',')
      this.$modal.confirm('是否确认删除统购公告商品编号为"' + ids + '"的数据项？').then(() => {
        return delGroupNoticeItem(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    handleExport() {
      this.download('school/groupNoticeItem/export', {
        ...this.queryParams
      }, `groupNoticeItem_${new Date().getTime()}.xlsx`)
    },
    formatValue(value) {
      return value == null || value === '' ? '--' : value
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

  .detail-kv-wide {
    grid-column: span 1;
  }
}
</style>
