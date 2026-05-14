<template>
  <div class="app-container delivery-order-page">
    <el-card v-show="showSearch" class="search-card" shadow="never">
      <el-form
        ref="queryForm"
        :model="queryParams"
        size="small"
        :inline="true"
        label-width="86px"
      >
        <el-form-item label="发货单号" prop="deliveryNo">
          <el-input
            v-model="queryParams.deliveryNo"
            placeholder="请输入发货单号"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="订单编号" prop="orderNo">
          <el-input
            v-model="queryParams.orderNo"
            placeholder="请输入订单编号"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="收货人" prop="receiverName">
          <el-input
            v-model="queryParams.receiverName"
            placeholder="请输入收货人姓名"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="物流单号" prop="logisticsNo">
          <el-input
            v-model="queryParams.logisticsNo"
            placeholder="请输入物流单号"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="物流公司" prop="logisticsCompany">
          <el-input
            v-model="queryParams.logisticsCompany"
            placeholder="请输入物流公司"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="发货状态" prop="deliveryStatus">
          <el-select
            v-model="queryParams.deliveryStatus"
            placeholder="请选择发货状态"
            clearable
          >
            <el-option label="待发货" value="0" />
            <el-option label="已发货" value="1" />
            <el-option label="已签收" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
          <el-button
            type="warning"
            plain
            icon="el-icon-download"
            size="mini"
            @click="handleExport"
            v-hasPermi="['school:deliveryOrder:export']"
          >导出</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <div slot="header" class="table-header">
        <div>
          <div class="table-title">发货单处理</div>
          <div class="table-desc">查询统购和零售发货单，并查看发货、签收状态</div>
        </div>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
      </div>

      <el-tabs v-model="activeOrderType" class="order-type-tabs" @tab-click="handleOrderTypeTabClick">
        <el-tab-pane label="统购" name="1" />
        <el-tab-pane label="零售" name="2" />
      </el-tabs>

      <el-table
        v-loading="loading"
        :data="deliveryOrderList"
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column label="发货单号" align="center" prop="deliveryNo" min-width="150" />
        <el-table-column label="订单编号" align="center" prop="orderNo" min-width="150" />
        <el-table-column label="订单类型" align="center" prop="orderType" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.orderType === '2' ? 'success' : 'primary'" size="mini">
              {{ formatOrderType(scope.row.orderType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="学校名称" align="center" prop="schoolName" min-width="160">
          <template slot-scope="scope">
            <span>{{ scope.row.schoolName || scope.row.schoolId || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="供应商名称" align="center" prop="supplierName" min-width="180">
          <template slot-scope="scope">
            <span>{{ scope.row.supplierName || scope.row.supplierId || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="收货人" align="center" prop="receiverName" min-width="120" />
        <el-table-column label="收货电话" align="center" prop="receiverPhone" min-width="130" />
        <el-table-column label="物流公司" align="center" prop="logisticsCompany" min-width="130" />
        <el-table-column label="物流单号" align="center" prop="logisticsNo" min-width="150" />
        <el-table-column label="运费" align="center" prop="carriageFee" width="90" />
        <el-table-column label="发货状态" align="center" prop="deliveryStatus" width="100">
          <template slot-scope="scope">
            <el-tag :type="deliveryStatusTagType(scope.row.deliveryStatus)" size="mini">
              {{ formatDeliveryStatus(scope.row.deliveryStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发货时间" align="center" prop="deliveryTime" width="140">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.deliveryTime, '{y}-{m}-{d}') || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="签收时间" align="center" prop="signTime" width="140">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.signTime, '{y}-{m}-{d}') || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" fixed="right" width="110">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.deliveryStatus === '0' || scope.row.deliveryStatus === 0"
              size="mini"
              type="text"
              icon="el-icon-truck"
              @click="handleShip(scope.row)"
              v-hasPermi="['school:deliveryOrder:edit']"
            >发货</el-button>
            <span v-else class="muted-action">{{ formatDeliveryStatus(scope.row.deliveryStatus) }}</span>
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
    </el-card>

    <el-dialog
      title="填写发货信息"
      :visible.sync="shipOpen"
      width="520px"
      append-to-body
    >
      <el-form ref="shipForm" :model="shipForm" :rules="shipRules" label-width="90px" size="small">
        <el-form-item label="发货单号">
          <el-input v-model="shipForm.deliveryNo" disabled />
        </el-form-item>
        <el-form-item label="物流公司" prop="logisticsCompany">
          <el-input v-model="shipForm.logisticsCompany" placeholder="请输入物流公司" clearable />
        </el-form-item>
        <el-form-item label="物流单号" prop="logisticsNo">
          <el-input v-model="shipForm.logisticsNo" placeholder="请输入物流单号" clearable />
        </el-form-item>
        <el-form-item label="运费" prop="carriageFee">
          <el-input v-model="shipForm.carriageFee" placeholder="请输入运费" clearable />
        </el-form-item>
        <el-form-item label="发货时间" prop="deliveryTime">
          <el-date-picker
            v-model="shipForm.deliveryTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择发货时间"
            clearable
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" size="small" @click="submitShip">确 定</el-button>
        <el-button size="small" @click="shipOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listDeliveryOrder, updateDeliveryOrder } from "@/api/school/deliveryOrder";

export default {
  name: "DeliveryOrder",
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      deliveryOrderList: [],
      activeOrderType: "1",
      shipOpen: false,
      shipForm: {
        id: null,
        deliveryNo: null,
        logisticsCompany: null,
        logisticsNo: null,
        carriageFee: null,
        deliveryTime: null,
        deliveryStatus: "1"
      },
      shipRules: {
        logisticsCompany: [
          { required: true, message: "物流公司不能为空", trigger: "blur" }
        ],
        logisticsNo: [
          { required: true, message: "物流单号不能为空", trigger: "blur" }
        ],
        deliveryTime: [
          { required: true, message: "发货时间不能为空", trigger: "change" }
        ]
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderType: "1",
        deliveryNo: null,
        orderNo: null,
        receiverName: null,
        logisticsCompany: null,
        logisticsNo: null,
        deliveryStatus: null
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.loading = true;
      this.queryParams.orderType = this.activeOrderType;
      listDeliveryOrder(this.queryParams).then(response => {
        this.deliveryOrderList = response.rows || [];
        this.total = response.total || 0;
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    handleOrderTypeTabClick(tab) {
      this.activeOrderType = tab.name;
      this.queryParams.orderType = tab.name;
      this.handleQuery();
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.queryParams.orderType = this.activeOrderType;
      this.handleQuery();
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    handleShip(row) {
      this.shipForm = {
        id: row.id,
        deliveryNo: row.deliveryNo,
        logisticsCompany: row.logisticsCompany,
        logisticsNo: row.logisticsNo,
        carriageFee: row.carriageFee,
        deliveryTime: row.deliveryTime || this.parseTime(new Date(), "{y}-{m}-{d}"),
        deliveryStatus: "1"
      };
      this.shipOpen = true;
      this.$nextTick(() => {
        this.resetForm("shipForm");
      });
    },
    submitShip() {
      this.$refs.shipForm.validate(valid => {
        if (!valid) {
          return;
        }
        updateDeliveryOrder({
          id: this.shipForm.id,
          logisticsCompany: this.shipForm.logisticsCompany,
          logisticsNo: this.shipForm.logisticsNo,
          carriageFee: this.shipForm.carriageFee,
          deliveryTime: this.shipForm.deliveryTime,
          deliveryStatus: "1"
        }).then(() => {
          this.$modal.msgSuccess("发货成功");
          this.shipOpen = false;
          this.getList();
        });
      });
    },
    formatOrderType(orderType) {
      if (orderType === "1" || orderType === 1) {
        return "统购";
      }
      if (orderType === "2" || orderType === 2) {
        return "零售";
      }
      return "-";
    },
    formatDeliveryStatus(status) {
      if (status === "0" || status === 0) {
        return "待发货";
      }
      if (status === "1" || status === 1) {
        return "已发货";
      }
      if (status === "2" || status === 2) {
        return "已签收";
      }
      return "-";
    },
    deliveryStatusTagType(status) {
      if (status === "0" || status === 0) {
        return "info";
      }
      if (status === "1" || status === 1) {
        return "warning";
      }
      if (status === "2" || status === 2) {
        return "success";
      }
      return "";
    },
    handleExport() {
      this.download("school/deliveryOrder/export", {
        ...this.queryParams,
        orderType: this.activeOrderType
      }, `deliveryOrder_${new Date().getTime()}.xlsx`);
    }
  }
};
</script>

<style scoped>
.delivery-order-page .search-card,
.delivery-order-page .table-card {
  border: 1px solid #e6ebf5;
  border-radius: 4px;
}

.delivery-order-page .search-card {
  margin-bottom: 18px;
}

.delivery-order-page .table-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}

.delivery-order-page .table-title {
  color: #303133;
  font-size: 18px;
  font-weight: 600;
  line-height: 26px;
}

.delivery-order-page .table-desc {
  margin-top: 4px;
  color: #909399;
  font-size: 13px;
}

.delivery-order-page .order-type-tabs {
  margin-bottom: 14px;
}

.delivery-order-page .muted-action {
  color: #909399;
  font-size: 12px;
}
</style>
