<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="订单号" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单号"
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
      <el-form-item label="下单用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入下单用户ID"
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
      <el-form-item label="学生ID" prop="studentId">
        <el-input
          v-model="queryParams.studentId"
          placeholder="请输入学生ID"
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
      <el-form-item label="收货地址ID" prop="addressId">
        <el-input
          v-model="queryParams.addressId"
          placeholder="请输入收货地址ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="收货人姓名快照" prop="receiverName">
        <el-input
          v-model="queryParams.receiverName"
          placeholder="请输入收货人姓名快照"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="收货人电话快照" prop="receiverPhone">
        <el-input
          v-model="queryParams.receiverPhone"
          placeholder="请输入收货人电话快照"
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
      <el-form-item label="支付状态" prop="payStatus">
        <el-select v-model="queryParams.payStatus" placeholder="请选择支付状态" clearable>
          <el-option
            v-for="item in payStatusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="订单状态" prop="orderStatus">
        <el-select v-model="queryParams.orderStatus" placeholder="请选择订单状态" clearable>
          <el-option
            v-for="item in orderStatusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="支付时间" prop="payTime">
        <el-date-picker clearable
          v-model="queryParams.payTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择支付时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="取消时间" prop="cancelTime">
        <el-date-picker clearable
          v-model="queryParams.cancelTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择取消时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="完成时间" prop="finishTime">
        <el-date-picker clearable
          v-model="queryParams.finishTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择完成时间">
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

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['school:orderInfo:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['school:orderInfo:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['school:orderInfo:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['school:orderInfo:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderInfoList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="订单号" align="center" prop="orderNo" />
      <el-table-column label="订单类型，1统购 2零售" align="center" prop="orderType" />
      <el-table-column label="统购公告ID" align="center" prop="noticeId" />
      <el-table-column label="学校ID" align="center" prop="schoolId" />
      <el-table-column label="学校名称快照" align="center" prop="schoolName" />
      <el-table-column label="供应商ID" align="center" prop="supplierId" />
      <el-table-column label="供应商名称快照" align="center" prop="supplierName" />
      <el-table-column label="下单用户ID" align="center" prop="userId" />
      <el-table-column label="下单用户名称" align="center" prop="userName" />
      <el-table-column label="学生ID" align="center" prop="studentId" />
      <el-table-column label="学生姓名" align="center" prop="studentName" />
      <el-table-column label="年级名称" align="center" prop="gradeName" />
      <el-table-column label="班级名称" align="center" prop="className" />
      <el-table-column label="收货地址ID" align="center" prop="addressId" />
      <el-table-column label="收货类型，1学校 2家庭" align="center" prop="receiveType" />
      <el-table-column label="收货人姓名快照" align="center" prop="receiverName" />
      <el-table-column label="收货人电话快照" align="center" prop="receiverPhone" />
      <el-table-column label="收货地址快照" align="center" prop="receiverAddress" />
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
      <el-table-column label="当前归属部门/学校" align="center" prop="deptId" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['school:orderInfo:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['school:orderInfo:remove']"
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

    <!-- 添加或修改订单信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="订单号" prop="orderNo">
          <el-input v-model="form.orderNo" placeholder="请输入订单号" />
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
          <el-select v-model="form.payStatus" placeholder="请选择支付状态">
            <el-option
              v-for="item in payStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="订单状态" prop="orderStatus">
          <el-select v-model="form.orderStatus" placeholder="请选择订单状态">
            <el-option
              v-for="item in orderStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="支付时间" prop="payTime">
          <el-date-picker clearable
            v-model="form.payTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择支付时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="取消时间" prop="cancelTime">
          <el-date-picker clearable
            v-model="form.cancelTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择取消时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="完成时间" prop="finishTime">
          <el-date-picker clearable
            v-model="form.finishTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择完成时间">
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
        <el-divider content-position="center">订单明细信息</el-divider>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAddXfptOrderItem">添加</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" icon="el-icon-delete" size="mini" @click="handleDeleteXfptOrderItem">删除</el-button>
          </el-col>
        </el-row>
        <el-table :data="xfptOrderItemList" :row-class-name="rowXfptOrderItemIndex" @selection-change="handleXfptOrderItemSelectionChange" ref="xfptOrderItem">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="序号" align="center" prop="index" width="50"/>
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
import { listOrderInfo, getOrderInfo, delOrderInfo, addOrderInfo, updateOrderInfo } from "@/api/school/orderInfo";

export default {
  name: "OrderInfo",
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
      payStatusOptions: [
        { label: "待支付", value: "0" },
        { label: "已支付", value: "1" },
        { label: "部分退款", value: "2" },
        { label: "已退款", value: "3" }
      ],
      orderStatusOptions: [
        { label: "待付款", value: "0" },
        { label: "待生产", value: "1" },
        { label: "待发货", value: "2" },
        { label: "待收货", value: "3" },
        { label: "已完成", value: "4" },
        { label: "已取消", value: "5" },
        { label: "售后中", value: "6" }
      ],
      // 弹出层标题
      title: "",
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
        payStatus: "0",
        orderStatus: "0",
        payTime: null,
        cancelTime: null,
        finishTime: null,
        deptId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        orderNo: [
          { required: true, message: "订单号不能为空", trigger: "blur" }
        ],
        orderType: [
          { required: true, message: "订单类型，1统购 2零售不能为空", trigger: "change" }
        ],
        supplierId: [
          { required: true, message: "供应商ID不能为空", trigger: "blur" }
        ],
        supplierName: [
          { required: true, message: "供应商名称快照不能为空", trigger: "blur" }
        ],
        userId: [
          { required: true, message: "下单用户ID不能为空", trigger: "blur" }
        ],
        userName: [
          { required: true, message: "下单用户名称不能为空", trigger: "blur" }
        ],
        receiveType: [
          { required: true, message: "收货类型，1学校 2家庭不能为空", trigger: "change" }
        ],
        receiverName: [
          { required: true, message: "收货人姓名快照不能为空", trigger: "blur" }
        ],
        receiverPhone: [
          { required: true, message: "收货人电话快照不能为空", trigger: "blur" }
        ],
        receiverAddress: [
          { required: true, message: "收货地址快照不能为空", trigger: "blur" }
        ],
        totalAmount: [
          { required: true, message: "订单总金额不能为空", trigger: "blur" }
        ],
        payStatus: [
          { required: true, message: "支付状态，0待支付 1已支付 2部分退款 3已退款不能为空", trigger: "change" }
        ],
        orderStatus: [
          { required: true, message: "订单状态，0待支付 1待生产 2待发货 3待收货 4已完成 5已取消 6售后中不能为空", trigger: "change" }
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
    /** 查询订单信息列表 */
    getList() {
      this.loading = true;
      listOrderInfo(this.queryParams).then(response => {
        this.orderInfoList = response.rows;
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
        delFlag: "0"
      };
      this.xfptOrderItemList = [];
      this.resetForm("form");
    },
    formatPayStatus(status) {
      const item = this.payStatusOptions.find(item => item.value === String(status));
      return item ? item.label : status;
    },
    formatOrderStatus(status) {
      const item = this.orderStatusOptions.find(item => item.value === String(status));
      return item ? item.label : status;
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加订单信息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getOrderInfo(id).then(response => {
        this.form = response.data;
        this.xfptOrderItemList = response.data.xfptOrderItemList;
        this.open = true;
        this.title = "修改订单信息";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.form.xfptOrderItemList = this.xfptOrderItemList;
          if (this.form.id != null) {
            updateOrderInfo(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addOrderInfo(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除订单信息编号为"' + ids + '"的数据项？').then(function() {
        return delOrderInfo(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
	/** 订单明细序号 */
    rowXfptOrderItemIndex({ row, rowIndex }) {
      row.index = rowIndex + 1;
    },
    /** 订单明细添加按钮操作 */
    handleAddXfptOrderItem() {
      let obj = {};
      obj.goodsId = "";
      obj.sizeId = "";
      obj.goodsName = "";
      obj.sizeName = "";
      obj.mainImage = "";
      obj.saleType = this.form.orderType || "1";
      obj.unitPrice = "";
      obj.buyNum = "";
      obj.itemAmount = "";
      obj.deptId = "";
      obj.remark = "";
      this.xfptOrderItemList.push(obj);
    },
    /** 订单明细删除按钮操作 */
    handleDeleteXfptOrderItem() {
      if (this.checkedXfptOrderItem.length == 0) {
        this.$modal.msgError("请先选择要删除的订单明细数据");
      } else {
        const xfptOrderItemList = this.xfptOrderItemList;
        const checkedXfptOrderItem = this.checkedXfptOrderItem;
        this.xfptOrderItemList = xfptOrderItemList.filter(function(item) {
          return checkedXfptOrderItem.indexOf(item.index) == -1
        });
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
};
</script>
