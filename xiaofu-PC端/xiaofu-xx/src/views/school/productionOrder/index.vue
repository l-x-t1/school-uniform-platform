<template>
  <div class="app-container">
    <div v-if="false">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="生产单号" prop="productionNo">
        <el-input
          v-model="queryParams.productionNo"
          placeholder="请输入生产单号"
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
      <el-form-item label="订单ID" prop="orderId">
        <el-input
          v-model="queryParams.orderId"
          placeholder="请输入订单ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="订单号快照" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单号快照"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="学校ID" prop="schoolId">
        <el-input
          v-model="queryParams.schoolId"
          placeholder="请输入学校ID"
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
      <el-form-item label="计划完成时间" prop="plannedFinishTime">
        <el-date-picker clearable
          v-model="queryParams.plannedFinishTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择计划完成时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="开始生产时间" prop="startTime">
        <el-date-picker clearable
          v-model="queryParams.startTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择开始生产时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="生产完成时间" prop="finishTime">
        <el-date-picker clearable
          v-model="queryParams.finishTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择生产完成时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="当前归属部门/学校" prop="deptId">
        <el-input
          v-model="queryParams.deptId"
          placeholder="请输入当前归属部门/学校"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    </div>

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="96px">
      <el-form-item label="生产单号" prop="productionNo">
        <el-input
          v-model="queryParams.productionNo"
          placeholder="请输入生产单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="订单号" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="生产状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择生产状态" clearable>
          <el-option label="待生产" value="0" />
          <el-option label="生产中" value="1" />
          <el-option label="生产完成" value="2" />
          <el-option label="已生成发货单" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="计划完成时间" prop="plannedFinishTime">
        <el-date-picker
          v-model="queryParams.plannedFinishTime"
          clearable
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择计划完成时间"
        />
      </el-form-item>
      <el-form-item label="开始生产时间" prop="startTime">
        <el-date-picker
          v-model="queryParams.startTime"
          clearable
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择开始生产时间"
        />
      </el-form-item>
      <el-form-item label="生产完成时间" prop="finishTime">
        <el-date-picker
          v-model="queryParams.finishTime"
          clearable
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择生产完成时间"
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
          v-if="activeOrderType === '1'"
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['school:productionOrder:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          v-if="false"
          @click="handleUpdate"
          v-hasPermi="['school:productionOrder:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-check"
          size="mini"
          :disabled="single || !selectedCanFinish"
          @click="handleFinishSelected"
          v-hasPermi="['school:productionOrder:edit']"
        >生产完成</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-truck"
          size="mini"
          :disabled="single || !selectedCanGenerateDelivery"
          @click="handleGenerateDeliverySelected"
          v-hasPermi="['school:productionOrder:edit']"
        >生成发货单</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['school:productionOrder:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['school:productionOrder:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-tabs v-model="activeOrderType" @tab-click="handleOrderTypeTabChange">
      <el-tab-pane label="统购" name="1" />
    </el-tabs>

    <el-table v-loading="loading" :data="productionOrderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="生产单号" align="center" prop="productionNo" />
      <el-table-column label="订单号快照" align="center" prop="orderNo" />
      <el-table-column label="订单类型" align="center" prop="orderType">
        <template slot-scope="scope">
          <el-tag size="mini" :type="String(scope.row.orderType) === '1' ? 'warning' : 'success'">
            {{ formatOrderType(scope.row.orderType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <el-tag size="mini" :type="getStatusTagType(scope.row.status)">
            {{ formatProductionStatus(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="计划完成时间" align="center" prop="plannedFinishTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.plannedFinishTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="开始生产时间" align="center" prop="startTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="生产完成时间" align="center" prop="finishTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.finishTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            v-if="false"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['school:productionOrder:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-check"
            :disabled="String(scope.row.status) === '2' || String(scope.row.status) === '3'"
            @click="handleFinish(scope.row)"
            v-hasPermi="['school:productionOrder:edit']"
          >生产完成</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-truck"
            :disabled="String(scope.row.status) !== '2'"
            @click="handleGenerateDelivery(scope.row)"
            v-hasPermi="['school:productionOrder:edit']"
          >生成发货单</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['school:productionOrder:remove']"
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

    <el-dialog title="生产单详情" :visible.sync="detailOpen" width="900px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="主键">{{ detailForm.id || '-' }}</el-descriptions-item>
        <el-descriptions-item label="生产单号">{{ detailForm.productionNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="统购公告ID">{{ detailForm.noticeId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="订单ID">{{ detailForm.orderId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="订单号">{{ detailForm.orderNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="订单类型">{{ formatOrderType(detailForm.orderType) }}</el-descriptions-item>
        <el-descriptions-item label="学校ID">{{ detailForm.schoolId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="供应商ID">{{ detailForm.supplierId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ formatProductionStatus(detailForm.status) }}</el-descriptions-item>
        <el-descriptions-item label="计划完成时间">{{ parseTime(detailForm.plannedFinishTime, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
        <el-descriptions-item label="开始生产时间">{{ parseTime(detailForm.startTime, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
        <el-descriptions-item label="生产完成时间">{{ parseTime(detailForm.finishTime, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
        <el-descriptions-item label="当前归属部门/学校">{{ detailForm.deptId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="删除标记">{{ detailForm.delFlag || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建人">{{ detailForm.createBy || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailForm.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="更新人">{{ detailForm.updateBy || '-' }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detailForm.updateTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailForm.remark || '-' }}</el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="center">生产单明细</el-divider>
      <el-table :data="detailItemList" border size="small">
        <el-table-column label="订单明细ID" align="center" prop="orderItemId" />
        <el-table-column label="商品ID" align="center" prop="goodsId" />
        <el-table-column label="尺码ID" align="center" prop="sizeId" />
        <el-table-column label="商品名称" align="center" prop="goodsName" />
        <el-table-column label="尺码名称" align="center" prop="sizeName" />
        <el-table-column label="应生产数量" align="center" prop="buyNum" />
        <el-table-column label="已完成数量" align="center" prop="finishNum" />
        <el-table-column label="状态" align="center" prop="status">
          <template slot-scope="scope">
            {{ formatProductionStatus(scope.row.status) }}
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" />
      </el-table>
    </el-dialog>

    <!-- 添加或修改生产单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="生产单号" prop="productionNo">
          <el-input v-model="form.productionNo" placeholder="请输入生产单号" />
        </el-form-item>
        <el-form-item label="统购公告ID" prop="noticeId">
          <el-input v-model="form.noticeId" placeholder="请输入统购公告ID" />
        </el-form-item>
        <el-form-item label="订单ID" prop="orderId">
          <el-input v-model="form.orderId" placeholder="请输入订单ID" />
        </el-form-item>
        <el-form-item label="订单号快照" prop="orderNo">
          <el-input v-model="form.orderNo" placeholder="请输入订单号快照" />
        </el-form-item>
        <el-form-item label="学校ID" prop="schoolId">
          <el-input v-model="form.schoolId" placeholder="请输入学校ID" />
        </el-form-item>
        <el-form-item label="供应商ID" prop="supplierId">
          <el-input v-model="form.supplierId" placeholder="请输入供应商ID" />
        </el-form-item>
        <el-form-item label="计划完成时间" prop="plannedFinishTime">
          <el-date-picker clearable
            v-model="form.plannedFinishTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择计划完成时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="开始生产时间" prop="startTime">
          <el-date-picker clearable
            v-model="form.startTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择开始生产时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="生产完成时间" prop="finishTime">
          <el-date-picker clearable
            v-model="form.finishTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择生产完成时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="当前归属部门/学校" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入当前归属部门/学校" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="删除标记，0正常 1删除" prop="delFlag">
          <el-input v-model="form.delFlag" placeholder="请输入删除标记，0正常 1删除" />
        </el-form-item>
        <el-divider content-position="center">生产单明细信息</el-divider>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAddXfptProductionOrderItem">添加</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" icon="el-icon-delete" size="mini" @click="handleDeleteXfptProductionOrderItem">删除</el-button>
          </el-col>
        </el-row>
        <el-table :data="xfptProductionOrderItemList" :row-class-name="rowXfptProductionOrderItemIndex" @selection-change="handleXfptProductionOrderItemSelectionChange" ref="xfptProductionOrderItem">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="序号" align="center" prop="index" width="50"/>
          <el-table-column label="订单明细ID" prop="orderItemId" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.orderItemId" placeholder="请输入订单明细ID" />
            </template>
          </el-table-column>
          <el-table-column label="商品ID" prop="goodsId" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.goodsId" placeholder="请输入商品ID" />
            </template>
          </el-table-column>
          <el-table-column label="尺码ID" prop="sizeId" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.sizeId" placeholder="请输入尺码ID" />
            </template>
          </el-table-column>
          <el-table-column label="商品名称快照" prop="goodsName" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.goodsName" placeholder="请输入商品名称快照" />
            </template>
          </el-table-column>
          <el-table-column label="尺码名称快照" prop="sizeName" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.sizeName" placeholder="请输入尺码名称快照" />
            </template>
          </el-table-column>
          <el-table-column label="应生产数量" prop="buyNum" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.buyNum" placeholder="请输入应生产数量" />
            </template>
          </el-table-column>
          <el-table-column label="已完成数量" prop="finishNum" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.finishNum" placeholder="请输入已完成数量" />
            </template>
          </el-table-column>
          <el-table-column label="状态，0待生产 1生产中 2生产完成" prop="status" width="150">
            <template slot-scope="scope">
              <el-select v-model="scope.row.status" placeholder="请选择状态，0待生产 1生产中 2生产完成">
                <el-option label="待生产" value="0" />
                <el-option label="生产中" value="1" />
                <el-option label="生产完成" value="2" />
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
  </div>
</template>

<script>
import { listProductionOrder, getProductionOrder, delProductionOrder, addProductionOrder, updateProductionOrder, finishProductionOrder, generateDeliveryOrder } from "@/api/school/productionOrder";

export default {
  name: "ProductionOrder",
  computed: {
    selectedCanFinish() {
      return this.selectedRow && !["2", "3"].includes(String(this.selectedRow.status));
    },
    selectedCanGenerateDelivery() {
      return this.selectedRow && String(this.selectedRow.status) === "2";
    }
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 子表选中数据
      checkedXfptProductionOrderItem: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      selectedRow: null,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 生产单表格数据
      productionOrderList: [],
      // 生产单明细表格数据
      xfptProductionOrderItemList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      detailOpen: false,
      detailForm: {},
      detailItemList: [],
      activeOrderType: "1",
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        productionNo: null,
        noticeId: null,
        orderId: null,
        orderNo: null,
        orderType: "1",
        schoolId: null,
        supplierId: null,
        status: null,
        plannedFinishTime: null,
        startTime: null,
        finishTime: null,
        deptId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        productionNo: [
          { required: true, message: "生产单号不能为空", trigger: "blur" }
        ],
        orderType: [
          { required: true, message: "订单类型，1统购 2零售不能为空", trigger: "change" }
        ],
        supplierId: [
          { required: true, message: "供应商ID不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "状态，0待生产 1生产中 2生产完成不能为空", trigger: "change" }
        ],
        delFlag: [
          { required: true, message: "删除标记，0正常 1删除不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询生产单列表 */
    getList() {
      this.loading = true;
      this.queryParams.orderType = this.activeOrderType;
      listProductionOrder(this.queryParams).then(response => {
        this.productionOrderList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        productionNo: null,
        noticeId: null,
        orderId: null,
        orderNo: null,
        orderType: "1",
        schoolId: null,
        supplierId: null,
        status: null,
        plannedFinishTime: null,
        startTime: null,
        finishTime: null,
        deptId: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null,
        delFlag: null
      };
      this.xfptProductionOrderItemList = [];
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.queryParams.orderType = this.activeOrderType;
      this.handleQuery();
    },
    handleOrderTypeTabChange() {
      this.queryParams.pageNum = 1;
      this.queryParams.orderType = this.activeOrderType;
      this.getList();
    },
    handleDetail(row) {
      const id = row.id || this.ids[0];
      getProductionOrder(id).then(response => {
        this.detailForm = response.data || {};
        this.detailItemList = this.detailForm.xfptProductionOrderItemList || [];
        this.detailOpen = true;
      });
    },
    formatOrderType(value) {
      if (String(value) === "1") return "统购";
      if (String(value) === "2") return "零售";
      return "-";
    },
    formatProductionStatus(value) {
      if (String(value) === "0") return "待生产";
      if (String(value) === "1") return "生产中";
      if (String(value) === "2") return "生产完成";
      if (String(value) === "3") return "已生成发货单";
      return "-";
    },
    getStatusTagType(value) {
      if (String(value) === "0") return "info";
      if (String(value) === "1") return "warning";
      if (String(value) === "2") return "success";
      if (String(value) === "3") return "primary";
      return "";
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
      this.selectedRow = selection.length === 1 ? selection[0] : null
    },
    /** 新增按钮操作 */
    handleAdd() {
      if (this.activeOrderType !== "1") {
        this.$modal.msgError("零售生产单不支持手动新增");
        return;
      }
      this.reset();
      this.form.orderType = "1";
      this.form.status = "0";
      this.open = true;
      this.title = "添加统购生产单";
    },
    handleFinishSelected() {
      this.handleFinish({ id: this.ids[0] });
    },
    handleGenerateDeliverySelected() {
      this.handleGenerateDelivery({ id: this.ids[0] });
    },
    handleFinish(row) {
      const id = row.id;
      this.$modal.confirm('是否确认将该生产单标记为生产完成？').then(() => {
        return finishProductionOrder(id);
      }).then(() => {
        this.$modal.msgSuccess("生产完成");
        this.getList();
      }).catch(() => {});
    },
    handleGenerateDelivery(row) {
      const id = row.id;
      this.$modal.confirm('是否确认根据该生产单生成发货单？').then(() => {
        return generateDeliveryOrder(id);
      }).then(() => {
        this.$modal.msgSuccess("生成发货单成功");
        this.getList();
      }).catch(() => {});
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getProductionOrder(id).then(response => {
        this.form = response.data;
        this.xfptProductionOrderItemList = response.data.xfptProductionOrderItemList;
        this.open = true;
        this.title = "修改生产单";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.form.xfptProductionOrderItemList = this.xfptProductionOrderItemList;
          if (this.form.id != null) {
            updateProductionOrder(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addProductionOrder(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除生产单编号为"' + ids + '"的数据项？').then(function() {
        return delProductionOrder(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
	/** 生产单明细序号 */
    rowXfptProductionOrderItemIndex({ row, rowIndex }) {
      row.index = rowIndex + 1;
    },
    /** 生产单明细添加按钮操作 */
    handleAddXfptProductionOrderItem() {
      let obj = {};
      obj.orderItemId = "";
      obj.goodsId = "";
      obj.sizeId = "";
      obj.goodsName = "";
      obj.sizeName = "";
      obj.buyNum = "";
      obj.finishNum = "";
      obj.status = "";
      obj.remark = "";
      this.xfptProductionOrderItemList.push(obj);
    },
    /** 生产单明细删除按钮操作 */
    handleDeleteXfptProductionOrderItem() {
      if (this.checkedXfptProductionOrderItem.length == 0) {
        this.$modal.msgError("请先选择要删除的生产单明细数据");
      } else {
        const xfptProductionOrderItemList = this.xfptProductionOrderItemList;
        const checkedXfptProductionOrderItem = this.checkedXfptProductionOrderItem;
        this.xfptProductionOrderItemList = xfptProductionOrderItemList.filter(function(item) {
          return checkedXfptProductionOrderItem.indexOf(item.index) == -1
        });
      }
    },
    /** 复选框选中数据 */
    handleXfptProductionOrderItemSelectionChange(selection) {
      this.checkedXfptProductionOrderItem = selection.map(item => item.index)
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('school/productionOrder/export', {
        ...this.queryParams
      }, `productionOrder_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
