<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" label-width="90px">
      <el-form-item label="班级名称">
        <el-input
          v-model="queryParams.className"
          placeholder="请输入班级名称"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.progressStatus" placeholder="请选择状态" clearable style="width: 180px">
          <el-option label="待查看" value="0" />
          <el-option label="供应商已查看" value="1" />
          <el-option label="制作中" value="2" />
          <el-option label="已完成" value="3" />
          <el-option label="已发货" value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="订购单号">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订购单号"
          clearable
          style="width: 220px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
        <el-button size="small" icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="tableData" border style="width: 100%">
      <el-table-column label="学校名称" prop="schoolName" min-width="160" :show-overflow-tooltip="true" />
      <el-table-column label="班级名称" prop="className" min-width="120" :show-overflow-tooltip="true" />
      <el-table-column label="订购单号" prop="orderNo" min-width="170" :show-overflow-tooltip="true" />
      <el-table-column label="订购数量" prop="orderQuantity" align="center" width="100" />
      <el-table-column label="状态" prop="progressStatusName" align="center" width="130">
        <template slot-scope="scope">
          <el-tag :type="statusTagType(scope.row.progressStatus)">{{ scope.row.progressStatusName }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="物流单号" prop="logisticsNo" min-width="150" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <span>{{ scope.row.logisticsNo || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" align="center" min-width="160" />
      <el-table-column label="操作" align="center" width="280" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleDetail(scope.row)">查看</el-button>
          <el-button
            v-if="showSupplierActions"
            v-hasPermi="['school:tgjd:edit']"
            size="mini"
            type="text"
            @click="handleSupplierView(scope.row)"
          >供应商查看</el-button>
          <el-button
            v-if="showSupplierActions"
            v-hasPermi="['school:tgjd:edit']"
            size="mini"
            type="text"
            :disabled="scope.row.progressStatus !== '1'"
            @click="handleStart(scope.row)"
          >开始制作</el-button>
          <el-button
            v-if="showSupplierActions"
            v-hasPermi="['school:tgjd:edit']"
            size="mini"
            type="text"
            :disabled="scope.row.progressStatus !== '2'"
            @click="handleFinish(scope.row)"
          >制作完成</el-button>
          <el-button
            v-if="showSupplierActions"
            v-hasPermi="['school:tgjd:edit']"
            size="mini"
            type="text"
            :disabled="scope.row.progressStatus !== '3'"
            @click="handleLogistics(scope.row)"
          >填物流</el-button>
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

    <el-dialog title="订购明细" :visible.sync="detailVisible" width="900px" append-to-body>
      <el-descriptions :column="2" border class="order-summary">
        <el-descriptions-item label="学校名称">{{ currentRow.schoolName }}</el-descriptions-item>
        <el-descriptions-item label="班级名称">{{ currentRow.className }}</el-descriptions-item>
        <el-descriptions-item label="订购单号">{{ currentRow.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ currentRow.progressStatusName }}</el-descriptions-item>
        <el-descriptions-item label="物流单号">{{ currentRow.logisticsNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentRow.createTime }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="detailList" border style="width: 100%; margin-top: 16px">
        <el-table-column label="商品名称" prop="productName" :show-overflow-tooltip="true" />
        <el-table-column label="商品单价" prop="price" width="120" align="center" />
        <el-table-column label="订购数量" prop="quantity" width="120" align="center" />
        <el-table-column label="订购人" prop="orderPerson" width="140" align="center" />
        <el-table-column label="订购时间" prop="orderTime" width="180" align="center" />
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailVisible = false">关闭</el-button>
      </div>
    </el-dialog>

    <el-dialog title="填写物流单号" :visible.sync="logisticsVisible" width="420px" append-to-body>
      <el-form ref="logisticsForm" :model="logisticsForm" :rules="logisticsRules" label-width="90px">
        <el-form-item label="物流单号" prop="logisticsNo">
          <el-input v-model="logisticsForm.logisticsNo" placeholder="请输入物流单号" clearable />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="logisticsVisible = false">取消</el-button>
        <el-button type="primary" @click="submitLogistics">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import {
  listGroupNoticeProgress,
  getGroupNoticeProgressDetail,
  viewGroupNoticeProgressOrder,
  startGroupNoticeProgressOrder,
  finishGroupNoticeProgressOrder,
  fillGroupNoticeProgressLogistics
} from '@/api/school/groupNotice'

export default {
  name: 'TgjdIndex',
  data() {
    return {
      loading: false,
      total: 0,
      tableData: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        className: '',
        progressStatus: '',
        orderNo: ''
      },
      detailVisible: false,
      detailList: [],
      currentRow: {},
      logisticsVisible: false,
      logisticsForm: {
        orderId: null,
        logisticsNo: ''
      },
      logisticsRules: {
        logisticsNo: [
          { required: true, message: '物流单号不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    ...mapGetters([
      'roles'
    ]),
    isAdminUser() {
      return (this.roles || []).some(role => String(role).toLowerCase() === 'admin')
    },
    showSupplierActions() {
      return !this.isAdminUser
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listGroupNoticeProgress(this.queryParams).then(response => {
        this.tableData = response.rows || []
        this.total = response.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams.className = ''
      this.queryParams.progressStatus = ''
      this.queryParams.orderNo = ''
      this.handleQuery()
    },
    handleDetail(row) {
      this.currentRow = row
      this.detailVisible = true
      this.detailList = []
      getGroupNoticeProgressDetail(row.noticeId, row.supplierId).then(response => {
        this.detailList = response.data || []
      })
    },
    handleSupplierView(row) {
      viewGroupNoticeProgressOrder(row.id).then(() => {
        this.$modal.msgSuccess('已标记为供应商已查看')
        this.getList()
      })
    },
    handleStart(row) {
      this.$modal.confirm('确认开始制作该订购单吗？').then(() => {
        return startGroupNoticeProgressOrder(row.id)
      }).then(() => {
        this.$modal.msgSuccess('已开始制作')
        this.getList()
      }).catch(() => {})
    },
    handleFinish(row) {
      this.$modal.confirm('确认该订购单制作完成吗？').then(() => {
        return finishGroupNoticeProgressOrder(row.id)
      }).then(() => {
        this.$modal.msgSuccess('已标记制作完成')
        this.getList()
      }).catch(() => {})
    },
    handleLogistics(row) {
      this.logisticsForm = {
        orderId: row.id,
        logisticsNo: row.logisticsNo || ''
      }
      this.logisticsVisible = true
      this.$nextTick(() => {
        if (this.$refs.logisticsForm) {
          this.$refs.logisticsForm.clearValidate()
        }
      })
    },
    submitLogistics() {
      this.$refs.logisticsForm.validate(valid => {
        if (!valid) return
        fillGroupNoticeProgressLogistics(this.logisticsForm.orderId, this.logisticsForm.logisticsNo).then(() => {
          this.$modal.msgSuccess('物流单号已保存')
          this.logisticsVisible = false
          this.getList()
        })
      })
    },
    statusTagType(status) {
      const typeMap = {
        '1': 'info',
        '2': 'warning',
        '3': 'success',
        '4': 'success'
      }
      return typeMap[String(status || '0')] || ''
    }
  }
}
</script>

<style scoped>
.order-summary {
  margin-bottom: 8px;
}
</style>
