<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="68px">
      <el-form-item label="订单号" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="学校名称" prop="schoolName">
        <el-input
          v-model="queryParams.schoolName"
          placeholder="请输入学校名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="供应商名称" prop="supplierName">
        <el-input
          v-model="queryParams.supplierName"
          placeholder="请输入供应商名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="下单用户名称" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入下单用户名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="学生姓名" prop="studentName">
        <el-input
          v-model="queryParams.studentName"
          placeholder="请输入学生姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="年级名称" prop="gradeName">
        <el-input
          v-model="queryParams.gradeName"
          placeholder="请输入年级名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="班级名称" prop="className">
        <el-input
          v-model="queryParams.className"
          placeholder="请输入班级名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="收货人姓名" prop="receiverName">
        <el-input
          v-model="queryParams.receiverName"
          placeholder="请输入收货人姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="收货人电话" prop="receiverPhone">
        <el-input
          v-model="queryParams.receiverPhone"
          placeholder="请输入收货人电话"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="订单总金额" prop="totalAmount">
        <el-input
          v-model="queryParams.totalAmount"
          placeholder="请输入订单总金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="实付金额" prop="payAmount">
        <el-input
          v-model="queryParams.payAmount"
          placeholder="请输入实付金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="已退款金额" prop="refundAmount">
        <el-input
          v-model="queryParams.refundAmount"
          placeholder="请输入已退款金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="支付时间" prop="payTime">
        <el-date-picker
          v-model="queryParams.payTime"
          clearable
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择支付时间"
        />
      </el-form-item>
      <el-form-item label="取消时间" prop="cancelTime">
        <el-date-picker
          v-model="queryParams.cancelTime"
          clearable
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择取消时间"
        />
      </el-form-item>
      <el-form-item label="完成时间" prop="finishTime">
        <el-date-picker
          v-model="queryParams.finishTime"
          clearable
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择完成时间"
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
          v-hasPermi="['school:orderInfo:add']"
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['school:orderInfo:edit']"
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
          v-hasPermi="['school:orderInfo:remove']"
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
          v-hasPermi="['school:orderInfo:export']"
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
        >导出</el-button>
      </el-col>
      <right-toolbar :show-search.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="orderInfoList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="订单号" align="center" prop="orderNo" />
      <el-table-column label="订单类型" align="center" prop="orderType">
        <template slot-scope="scope">
          <span>{{ formatOrderType(scope.row.orderType) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="公告标题" align="center" prop="noticeTitle" />
      <el-table-column label="学校名称" align="center" prop="schoolName" />
      <el-table-column label="供应商名称" align="center" prop="supplierName" />
      <el-table-column label="下单用户名称" align="center" prop="userName" />
      <el-table-column label="学生姓名" align="center" prop="studentName" />
      <el-table-column label="年级名称" align="center" prop="gradeName" />
      <el-table-column label="班级名称" align="center" prop="className" />
      <el-table-column label="收货类型" align="center" prop="receiveType">
        <template slot-scope="scope">
          <span>{{ formatReceiveType(scope.row.receiveType) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="收货人姓名" align="center" prop="receiverName" />
      <el-table-column label="收货人电话" align="center" prop="receiverPhone" />
      <el-table-column label="收货地址" align="center" prop="receiverAddress" />
      <el-table-column label="订单总金额" align="center" prop="totalAmount" />
      <el-table-column label="实付金额" align="center" prop="payAmount" />
      <el-table-column label="已退款金额" align="center" prop="refundAmount" />
      <el-table-column label="支付状态" align="center" prop="payStatus">
        <template slot-scope="scope">
          <span>{{ formatPayStatus(scope.row.payStatus) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="订单状态" align="center" prop="orderStatus">
        <template slot-scope="scope">
          <span>{{ formatOrderStatus(scope.row.orderStatus) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="支付时间" align="center" prop="payTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.payTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="取消时间" align="center" prop="cancelTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.cancelTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="完成时间" align="center" prop="finishTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.finishTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >详情</el-button>
          <el-button
            v-hasPermi="['school:orderInfo:edit']"
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            v-hasPermi="['school:orderInfo:remove']"
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

    <el-dialog title="订单详情" :visible.sync="detailOpen" width="1000px" append-to-body>
      <div v-loading="detailLoading" class="detail-wrap">
        <div class="detail-section">
          <div class="section-title">订单摘要</div>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单号">{{ formatValue(detailForm.orderNo) }}</el-descriptions-item>
            <el-descriptions-item label="订单类型">{{ formatOrderType(detailForm.orderType) }}</el-descriptions-item>
            <el-descriptions-item label="公告标题">{{ formatValue(detailForm.noticeTitle) }}</el-descriptions-item>
            <el-descriptions-item label="学校名称">{{ formatValue(detailForm.schoolName) }}</el-descriptions-item>
            <el-descriptions-item label="供应商名称">{{ formatValue(detailForm.supplierName) }}</el-descriptions-item>
            <el-descriptions-item label="下单用户">{{ formatValue(detailForm.userName) }}</el-descriptions-item>
            <el-descriptions-item label="学生姓名">{{ formatValue(detailForm.studentName) }}</el-descriptions-item>
            <el-descriptions-item label="收货方式">{{ formatReceiveType(detailForm.receiveType) }}</el-descriptions-item>
            <el-descriptions-item label="支付状态">{{ formatPayStatus(detailForm.payStatus) }}</el-descriptions-item>
            <el-descriptions-item label="订单状态">{{ formatOrderStatus(detailForm.orderStatus) }}</el-descriptions-item>
            <el-descriptions-item label="订单总金额">{{ formatAmount(detailForm.totalAmount) }}</el-descriptions-item>
            <el-descriptions-item label="实付金额">{{ formatAmount(detailForm.payAmount) }}</el-descriptions-item>
            <el-descriptions-item label="已退款金额">{{ formatAmount(detailForm.refundAmount) }}</el-descriptions-item>
            <el-descriptions-item label="支付时间">{{ formatDateTime(detailForm.payTime) }}</el-descriptions-item>
            <el-descriptions-item label="取消时间">{{ formatDateTime(detailForm.cancelTime) }}</el-descriptions-item>
            <el-descriptions-item label="完成时间">{{ formatDateTime(detailForm.finishTime) }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="detail-section">
          <div class="section-title">收货信息</div>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="收货人姓名">{{ formatValue(detailForm.receiverName) }}</el-descriptions-item>
            <el-descriptions-item label="收货人电话">{{ formatValue(detailForm.receiverPhone) }}</el-descriptions-item>
            <el-descriptions-item label="收货地址" :span="2">{{ formatValue(detailForm.receiverAddress) }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="detail-section">
          <div class="section-title">订单明细</div>
          <el-table :data="detailItemList" border class="detail-table">
            <el-table-column label="商品名称" prop="goodsName" min-width="180" show-overflow-tooltip />
            <el-table-column label="尺码名称" prop="sizeName" min-width="140" show-overflow-tooltip />
            <el-table-column label="销售类型" min-width="120">
              <template slot-scope="scope">
                <span>{{ formatSaleType(scope.row.saleType) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="单价" min-width="120">
              <template slot-scope="scope">
                <span>{{ formatAmount(scope.row.unitPrice) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="购买数量" prop="buyNum" width="110" align="center" />
            <el-table-column label="行金额" min-width="120">
              <template slot-scope="scope">
                <span>{{ formatAmount(scope.row.itemAmount) }}</span>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="!detailItemList.length" class="empty-text">暂无订单明细</div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 添加或修改订单信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="订单号" prop="orderNo">
          <el-input v-model="form.orderNo" placeholder="请输入订单号" />
        </el-form-item>
        <el-form-item label="订单类型" prop="orderType">
          <el-select v-model="form.orderType" placeholder="请选择订单类型" style="width: 100%">
            <el-option label="统购" value="1" />
            <el-option label="零售" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="统购公告ID" prop="noticeId">
          <el-input v-model="form.noticeId" placeholder="请输入统购公告ID" />
        </el-form-item>
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
        <el-form-item label="下单用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入下单用户ID" />
        </el-form-item>
        <el-form-item label="下单用户名称" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入下单用户名称" />
        </el-form-item>
        <el-form-item label="学生ID" prop="studentId">
          <el-input v-model="form.studentId" placeholder="请输入学生ID" />
        </el-form-item>
        <el-form-item label="学生姓名" prop="studentName">
          <el-input v-model="form.studentName" placeholder="请输入学生姓名" />
        </el-form-item>
        <el-form-item label="年级名称" prop="gradeName">
          <el-input v-model="form.gradeName" placeholder="请输入年级名称" />
        </el-form-item>
        <el-form-item label="班级名称" prop="className">
          <el-input v-model="form.className" placeholder="请输入班级名称" />
        </el-form-item>
        <el-form-item label="收货地址ID" prop="addressId">
          <el-input v-model="form.addressId" placeholder="请输入收货地址ID" />
        </el-form-item>
        <el-form-item label="收货类型" prop="receiveType">
          <el-select v-model="form.receiveType" placeholder="请选择收货类型" style="width: 100%">
            <el-option label="学校收货" value="1" />
            <el-option label="家庭收货" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="收货人姓名快照" prop="receiverName">
          <el-input v-model="form.receiverName" placeholder="请输入收货人姓名快照" />
        </el-form-item>
        <el-form-item label="收货人电话快照" prop="receiverPhone">
          <el-input v-model="form.receiverPhone" placeholder="请输入收货人电话快照" />
        </el-form-item>
        <el-form-item label="收货地址快照" prop="receiverAddress">
          <el-input v-model="form.receiverAddress" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="订单总金额" prop="totalAmount">
          <el-input v-model="form.totalAmount" placeholder="请输入订单总金额" />
        </el-form-item>
        <el-form-item label="实付金额" prop="payAmount">
          <el-input v-model="form.payAmount" placeholder="请输入实付金额" />
        </el-form-item>
        <el-form-item label="已退款金额" prop="refundAmount">
          <el-input v-model="form.refundAmount" placeholder="请输入已退款金额" />
        </el-form-item>
        <el-form-item label="支付状态" prop="payStatus">
          <el-select v-model="form.payStatus" placeholder="请选择支付状态" style="width: 100%">
            <el-option label="待支付" value="0" />
            <el-option label="已支付" value="1" />
            <el-option label="部分退款" value="2" />
            <el-option label="已退款" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="订单状态" prop="orderStatus">
          <el-select v-model="form.orderStatus" placeholder="请选择订单状态" style="width: 100%">
            <el-option label="待支付" value="0" />
            <el-option label="待生产" value="1" />
            <el-option label="待发货" value="2" />
            <el-option label="待收货" value="3" />
            <el-option label="已完成" value="4" />
            <el-option label="已取消" value="5" />
            <el-option label="售后中" value="6" />
          </el-select>
        </el-form-item>
        <el-form-item label="支付时间" prop="payTime">
          <el-date-picker
            v-model="form.payTime"
            clearable
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择支付时间"
          />
        </el-form-item>
        <el-form-item label="取消时间" prop="cancelTime">
          <el-date-picker
            v-model="form.cancelTime"
            clearable
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择取消时间"
          />
        </el-form-item>
        <el-form-item label="完成时间" prop="finishTime">
          <el-date-picker
            v-model="form.finishTime"
            clearable
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择完成时间"
          />
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
        <el-divider content-position="center">订单明细信息</el-divider>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAddXfptOrderItem">添加</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" icon="el-icon-delete" size="mini" @click="handleDeleteXfptOrderItem">删除</el-button>
          </el-col>
        </el-row>
        <el-table ref="xfptOrderItem" :data="xfptOrderItemList" :row-class-name="rowXfptOrderItemIndex" @selection-change="handleXfptOrderItemSelectionChange">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="序号" align="center" prop="index" width="50" />
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
          <el-table-column label="销售类型，1统购 2零售 3统购零售" prop="saleType" width="180">
            <template slot-scope="scope">
              <el-select v-model="scope.row.saleType" placeholder="请选择销售类型">
                <el-option label="统购" value="1" />
                <el-option label="零售" value="2" />
                <el-option label="统购零售" value="3" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="单价" prop="unitPrice" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.unitPrice" placeholder="请输入单价" />
            </template>
          </el-table-column>
          <el-table-column label="购买数量" prop="buyNum" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.buyNum" placeholder="请输入购买数量" />
            </template>
          </el-table-column>
          <el-table-column label="行金额" prop="itemAmount" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.itemAmount" placeholder="请输入行金额" />
            </template>
          </el-table-column>
          <el-table-column label="当前归属部门/学校" prop="deptId" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.deptId" placeholder="请输入当前归属部门/学校" />
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
import { listOrderInfo, getOrderInfo, delOrderInfo, addOrderInfo, updateOrderInfo } from '@/api/school/orderInfo'

export default {
  name: 'OrderInfo',
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 子表选中数据
      checkedXfptOrderItem: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 订单信息表格数据
      orderInfoList: [],
      // 订单明细表格数据
      xfptOrderItemList: [],
      // 订单详情
      detailOpen: false,
      detailLoading: false,
      detailForm: {},
      detailItemList: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderNo: null,
        orderType: null,
        noticeId: null,
        schoolId: null,
        schoolName: null,
        supplierId: null,
        supplierName: null,
        userId: null,
        userName: null,
        studentId: null,
        studentName: null,
        gradeName: null,
        className: null,
        addressId: null,
        receiveType: null,
        receiverName: null,
        receiverPhone: null,
        receiverAddress: null,
        totalAmount: null,
        payAmount: null,
        refundAmount: null,
        payStatus: null,
        orderStatus: null,
        payTime: null,
        cancelTime: null,
        finishTime: null,
        deptId: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        orderNo: [
          { required: true, message: '订单号不能为空', trigger: 'blur' }
        ],
        orderType: [
          { required: true, message: '订单类型，1统购 2零售不能为空', trigger: 'change' }
        ],
        supplierId: [
          { required: true, message: '供应商ID不能为空', trigger: 'blur' }
        ],
        supplierName: [
          { required: true, message: '供应商名称快照不能为空', trigger: 'blur' }
        ],
        userId: [
          { required: true, message: '下单用户ID不能为空', trigger: 'blur' }
        ],
        userName: [
          { required: true, message: '下单用户名称不能为空', trigger: 'blur' }
        ],
        receiveType: [
          { required: true, message: '收货类型，1学校 2家庭不能为空', trigger: 'change' }
        ],
        receiverName: [
          { required: true, message: '收货人姓名快照不能为空', trigger: 'blur' }
        ],
        receiverPhone: [
          { required: true, message: '收货人电话快照不能为空', trigger: 'blur' }
        ],
        receiverAddress: [
          { required: true, message: '收货地址快照不能为空', trigger: 'blur' }
        ],
        totalAmount: [
          { required: true, message: '订单总金额不能为空', trigger: 'blur' }
        ],
        payStatus: [
          { required: true, message: '支付状态，0待支付 1已支付 2部分退款 3已退款不能为空', trigger: 'change' }
        ],
        orderStatus: [
          { required: true, message: '订单状态，0待支付 1待生产 2待发货 3待收货 4已完成 5已取消 6售后中不能为空', trigger: 'change' }
        ],
        delFlag: [
          { required: true, message: '删除标记，0正常 1删除不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    formatOrderType(value) {
      const optionMap = {
        '1': '统购',
        '2': '零售'
      }
      return optionMap[String(value)] || '-'
    },
    formatReceiveType(value) {
      const optionMap = {
        '1': '学校收货',
        '2': '家庭收货'
      }
      return optionMap[String(value)] || '-'
    },
    formatPayStatus(value) {
      const optionMap = {
        '0': '待支付',
        '1': '已支付',
        '2': '部分退款',
        '3': '已退款'
      }
      return optionMap[String(value)] || '-'
    },
    formatOrderStatus(value) {
      const optionMap = {
        '0': '待支付',
        '1': '待生产',
        '2': '待发货',
        '3': '待收货',
        '4': '已完成',
        '5': '已取消',
        '6': '售后中'
      }
      return optionMap[String(value)] || '-'
    },
    formatSaleType(value) {
      const optionMap = {
        '1': '统购',
        '2': '零售',
        '3': '统购+零售'
      }
      return optionMap[String(value)] || '-'
    },
    formatValue(value) {
      return value === undefined || value === null || value === '' ? '--' : value
    },
    formatAmount(value) {
      if (value === undefined || value === null || value === '') {
        return '--'
      }
      const amount = Number(value)
      return Number.isNaN(amount) ? value : `￥${amount.toFixed(2)}`
    },
    formatDateTime(value) {
      return this.parseTime(value, '{y}-{m}-{d} {h}:{i}:{s}') || '--'
    },
    /** 查询订单信息列表 */
    getList() {
      this.loading = true
      listOrderInfo(this.queryParams).then(response => {
        this.orderInfoList = response.rows
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
        orderNo: null,
        orderType: null,
        noticeId: null,
        schoolId: null,
        schoolName: null,
        supplierId: null,
        supplierName: null,
        userId: null,
        userName: null,
        studentId: null,
        studentName: null,
        gradeName: null,
        className: null,
        addressId: null,
        receiveType: null,
        receiverName: null,
        receiverPhone: null,
        receiverAddress: null,
        totalAmount: null,
        payAmount: null,
        refundAmount: null,
        payStatus: null,
        orderStatus: null,
        payTime: null,
        cancelTime: null,
        finishTime: null,
        deptId: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null,
        delFlag: null
      }
      this.xfptOrderItemList = []
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
      this.title = '添加订单信息'
    },
    /** 详情按钮操作 */
    handleView(row) {
      const id = row.id || this.ids[0]
      this.detailOpen = true
      this.detailLoading = true
      getOrderInfo(id).then(response => {
        this.detailForm = response.data || {}
        this.detailItemList = (response.data && response.data.xfptOrderItemList) || []
      }).finally(() => {
        this.detailLoading = false
      })
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getOrderInfo(id).then(response => {
        this.form = response.data
        this.xfptOrderItemList = response.data.xfptOrderItemList
        this.open = true
        this.title = '修改订单信息'
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          this.form.xfptOrderItemList = this.xfptOrderItemList
          if (this.form.id != null) {
            updateOrderInfo(this.form).then(response => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addOrderInfo(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除订单信息编号为"' + ids + '"的数据项？').then(function() {
        return delOrderInfo(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    /** 订单明细序号 */
    rowXfptOrderItemIndex({ row, rowIndex }) {
      row.index = rowIndex + 1
    },
    /** 订单明细添加按钮操作 */
    handleAddXfptOrderItem() {
      const obj = {}
      obj.goodsId = ''
      obj.sizeId = ''
      obj.goodsName = ''
      obj.sizeName = ''
      obj.mainImage = ''
      obj.saleType = this.form.orderType || '1'
      obj.unitPrice = ''
      obj.buyNum = ''
      obj.itemAmount = ''
      obj.deptId = ''
      obj.remark = ''
      this.xfptOrderItemList.push(obj)
    },
    /** 订单明细删除按钮操作 */
    handleDeleteXfptOrderItem() {
      if (this.checkedXfptOrderItem.length === 0) {
        this.$modal.msgError('请先选择要删除的订单明细数据')
      } else {
        const xfptOrderItemList = this.xfptOrderItemList
        const checkedXfptOrderItem = this.checkedXfptOrderItem
        this.xfptOrderItemList = xfptOrderItemList.filter(function(item) {
          return checkedXfptOrderItem.indexOf(item.index) === -1
        })
      }
    },
    /** 复选框选中数据 */
    handleXfptOrderItemSelectionChange(selection) {
      this.checkedXfptOrderItem = selection.map(item => item.index)
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('school/orderInfo/export', {
        ...this.queryParams
      }, `orderInfo_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>

<style scoped>
.detail-wrap {
  min-height: 220px;
}

.detail-section + .detail-section {
  margin-top: 20px;
}

.section-title {
  margin-bottom: 12px;
  color: #303133;
  font-size: 15px;
  font-weight: 600;
}

.detail-table {
  width: 100%;
}

.empty-text {
  margin-top: 12px;
  color: #909399;
  font-size: 13px;
}
</style>
