<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryForm"
      size="small"
      :inline="true"
      v-show="showSearch"
    >
      <el-form-item label="厂商名称" prop="supplierName">
        <el-input
          v-model="queryParams.supplierName"
          placeholder="请输入厂商名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="联系电话" prop="supplierPhone">
        <el-input
          v-model="queryParams.supplierPhone"
          placeholder="请输入联系电话"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="法定代表人" prop="legalRepresentative">
        <el-input
          v-model="queryParams.legalRepresentative"
          placeholder="请输入法定代表人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery"
          >搜索</el-button
        >
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          @click="handleAdd"
          v-hasPermi="['school:manufacturers:add']"
          >新增</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['school:manufacturers:edit']"
          >修改</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['school:manufacturers:remove']"
          >删除</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          @click="handleExport"
          v-hasPermi="['school:manufacturers:export']"
          >导出</el-button
        >
      </el-col>
      <right-toolbar
        :showSearch.sync="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>

    <el-table
      v-loading="loading"
      :data="supplierMessageList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column type="index" align="center" label="序号" />
      <el-table-column label="厂商名称" align="center" prop="supplierName" />
      <el-table-column label="联系电话" align="center" prop="supplierPhone" />
      <el-table-column
        label="法定代表人"
        align="center"
        prop="legalRepresentative"
      />

      <el-table-column label="职工人数" align="center" prop="staffNumber" />
      <el-table-column
        label="成立日期"
        align="center"
        prop="establishedTime"
        width="180"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.establishedTime, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column label="厂商地址" align="center" prop="supplierAddress" />
      <el-table-column align="center" label="是否是黑名单">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.blacklistStatus == 1" type="danger"
            >是</el-tag
          >
          <el-tag v-else type="success">否</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        fixed="right"
        label="操作"
        width="400"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <el-button
            v-hasPermi="['school:manufacturers:view']"
            icon="el-icon-view"
            type="text"
            @click="handleViewSchoolList(scope.row)"
            >查看服务学校列表</el-button
          >
          <el-button
            v-hasPermi="['school:manufacturers:hmd']"
            v-if="scope.row.blacklistStatus == 0"
            icon="el-icon-circle-plus"
            type="text"
            @click="handleAddBlackList(scope.row)"
            >添加黑名单</el-button
          >
          <el-button
            v-if="scope.row.blacklistStatus == 1"
            icon="el-icon-circle-close"
            type="text"
            @click="handleRemove(scope.row)"
            >移除黑名单</el-button
          >
          <el-button
            v-hasPermi="['school:manufacturers:userManage']"
            icon="el-icon-user"
            type="text"
            @click="handleUserManage(scope.row)"
            >用户管理</el-button
          >
          <el-button
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['school:supplierMessage:edit']"
            >修改</el-button
          >
          <el-button
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['school:supplierMessage:remove']"
            >删除</el-button
          >
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

    <!-- 添加或修改供应商信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="780px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="厂商名称" prop="supplierName">
              <el-input
                v-model="form.supplierName"
                placeholder="请输入厂商名称"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="supplierPhone">
              <el-input
                v-model="form.supplierPhone"
                placeholder="请输入联系电话"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="法定代表人" prop="legalRepresentative">
              <el-input
                v-model="form.legalRepresentative"
                placeholder="请输入法定代表人"
              />
            </el-form-item>
          </el-col> 
          <el-col :span="12">
            <el-form-item label="职工人数" prop="staffNumber">
              <el-input
                v-model="form.staffNumber"
                placeholder="请输入职工人数"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="厂商地址" prop="supplierAddress">
              <el-input
                v-model="form.supplierAddress"
                placeholder="请输入厂商地址"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="注册资金" prop="registeredCapital">
              <el-input
                v-model="form.registeredCapital"
                placeholder="请输入注册资金，单位（万元）"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="成立日期" prop="establishedTime">
              <el-date-picker
                style="width: 100%"
                clearable
                v-model="form.establishedTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择成立日期"
              >
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="企业类型" prop="supplierType">
              <el-input
                v-model="form.supplierType"
                placeholder="请输入企业类型"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="厂商介绍" prop="supplierIntroduce">
              <el-input
                type="textarea"
                v-model="form.supplierIntroduce"
                placeholder="请输入厂商介绍"
                :rows="4"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="厂商资质" prop="businessLicense">
              <el-input
                type="textarea"
                v-model="form.businessLicense"
                placeholder="请输入厂商资质信息"
                :rows="3"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 厂商详情与服务学校列表对话框 -->
    <el-dialog
      title="厂商详情"
      :visible.sync="schoolListDialogVisible"
      width="60%"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-office-building"></i>
            厂商名称
          </template>
          {{ vendorDetail.supplierName }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-phone"></i>
            联系方式
          </template>
          {{ vendorDetail.supplierPhone }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-office-building"></i>
            厂商规模(人)
          </template>
          {{ vendorDetail.staffNumber }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-money"></i>
            注册资本(万)
          </template>
          {{ vendorDetail.registeredCapital }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-s-custom"></i>
            法定代表人
          </template>
          {{ vendorDetail.legalRepresentative }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-office-building"></i>
            公司地址
          </template>
          {{ vendorDetail.supplierAddress }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-postcard"></i>
            企业类型
          </template>
          {{ vendorDetail.supplierType }}
        </el-descriptions-item>
      </el-descriptions>

      <el-table
        :data="schoolListTableData"
        empty-text="暂时没有数据~"
        style="margin-top: 20px"
      >
        <el-table-column prop="deptName" label="服务学校"></el-table-column>
        <el-table-column
          prop="studentNumber"
          label="学生数量(人)"
          align="center"
        ></el-table-column>
      </el-table>
      <pagination
        v-show="schoolListTotal > 0"
        :total="schoolListTotal"
        :page.sync="serverSchoolListQueryParams.pageNum"
        :limit.sync="serverSchoolListQueryParams.pageSize"
        @pagination="getSchoolList"
      />
      <span slot="footer" class="dialog-footer">
        <el-button @click="schoolListDialogVisible = false">关 闭</el-button>
      </span>
    </el-dialog>

    <!-- 用户管理对话框 -->
    <el-dialog
      title="用户管理"
      :visible.sync="userManageDialogVisible"
      width="650px"
      append-to-body
    >
      <el-divider content-position="left">已绑定用户</el-divider>
      <el-table :data="boundUserList" empty-text="暂无绑定用户">
        <el-table-column label="用户名" align="center" prop="userName" />
        <el-table-column label="昵称" align="center" prop="nickName" />
        <el-table-column label="手机号" align="center" prop="phonenumber" />
        <el-table-column label="操作" align="center" width="100">
          <template slot-scope="scope">
            <el-button
              type="text"
              icon="el-icon-delete"
              style="color: #f56c6c"
              @click="handleUnbindUser(scope.row)"
            >解除绑定</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-divider content-position="left">新增绑定</el-divider>
      <el-form :inline="true" size="small">
        <el-form-item label="选择用户">
          <el-select
            v-model="selectedUserId"
            filterable
            remote
            reserve-keyword
            placeholder="请输入用户名搜索"
            :remote-method="remoteSearchUsers"
            :loading="userSearchLoading"
            clearable
            style="width: 280px"
          >
            <el-option
              v-for="user in userSearchOptions"
              :key="user.userId"
              :label="user.userName + ' (' + user.nickName + ')'"
              :value="user.userId"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            icon="el-icon-plus"
            :disabled="!selectedUserId"
            @click="handleBindUser"
          >绑定</el-button>
        </el-form-item>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="userManageDialogVisible = false">关 闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  listSupplierMessage,
  getSupplierMessage,
  delSupplierMessage,
  addSupplierMessage,
  updateSupplierMessage,
  getSupplierUsers,
  bindSupplierUser,
  unbindSupplierUser,
} from "@/api/school/manufacturers";
import {
  getVendorsList,
  addOrRemoveBlackList,
  getSchoolList,
  getVendorDetail,
} from "@/api/jiaoyuju/vendors";
import { listUser } from "@/api/system/user";
export default {
  name: "SupplierMessage",
  data() {
    return {
      userType: "school",
      serverSchoolListQueryParams: {
        pageNum: 1,
        pageSize: 10,
      },
      schoolListDialogVisible: false,
      schoolListTableData: [],
      schoolListTotal: 0,
      vendorDetail: {},
      // 用户管理
      userManageDialogVisible: false,
      currentSupplierId: null,
      boundUserList: [],
      selectedUserId: null,
      userSearchOptions: [],
      userSearchLoading: false,
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
      // 供应商信息表格数据
      supplierMessageList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        supplierName: null,
        supplierPhone: null,
        supplierType: null,
        legalRepresentative: null,
        turnoverTime: null,
        staffNumber: null,
        establishedTime: null,
        supplierAddress: null,
        supplierIntroduce: null,
        equipmentConditions: null,
        businessLicense: null,
        registeredCapital: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        supplierName: [
          { required: true, message: "厂商名称不能为空", trigger: "blur" },
        ],
        supplierPhone: [
          { required: true, message: "联系电话不能为空", trigger: "blur" },
          { pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号码", trigger: "blur" }
        ],
        legalRepresentative: [
          { required: true, message: "法定代表人不能为空", trigger: "blur" },
        ],
        supplierType: [
          { required: true, message: "企业类型不能为空", trigger: "blur" },
        ],
        staffNumber: [
          { required: true, message: "职工人数不能为空", trigger: "blur" },
        ],
        establishedTime: [
          { required: true, message: "成立日期不能为空", trigger: "blur" },
        ],
        supplierAddress: [
          { required: true, message: "厂商地址不能为空", trigger: "blur" },
        ],
        registeredCapital: [
          { required: true, message: "注册资金不能为空", trigger: "blur" },
        ],
        supplierIntroduce: [
          { required: true, message: "厂商介绍不能为空", trigger: "blur" },
        ],
        businessLicense: [
          { required: true, message: "厂商资质不能为空", trigger: "blur" },
        ],
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    handlegetSchoolList() {
      getSchoolList({
        id: this.serverSchoolListQueryParams.id,
        pageNum: this.serverSchoolListQueryParams.pageNum,
        pageSize: this.serverSchoolListQueryParams.pageSize,
      }).then((res) => {
        this.schoolListTableData = res.rows;
        this.schoolListTotal = res.total;
      });
    },
    handleViewSchoolList(row) {
      this.serverSchoolListQueryParams.id = row.id;
      this.handlegetSchoolList();
      getVendorDetail({
        id: row.id,
      }).then((res) => {
        this.vendorDetail = res.data;
      });
      this.schoolListDialogVisible = true;
    },
    //添加黑名单
    handleAddBlackList(row) {
      this.$prompt('请输入黑名单原因', '添加黑名单', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValidator: (value) => {
          if (!value || value.trim() === '') {
            return '黑名单原因不能为空';
          }
          return true;
        },
        inputErrorMessage: '黑名单原因不能为空'
      }).then(({ value }) => {
        addOrRemoveBlackList({
          id: row.id,
          blacklistStatus: 1,
          violationReason: value.trim()
        }).then((res) => {
          this.$message.success("添加黑名单成功");
          this.getList();
        });
      }).catch(() => {
        // 用户取消操作
      });
    },
    //移除黑名单
    handleRemove(row) {
      //是否确认移除黑名单
      this.$confirm("是否确认移除黑名单？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        addOrRemoveBlackList({
          id: row.id,
          blacklistStatus: 0,
        }).then((res) => {
          this.$message.success("移除黑名单成功");
          this.getList();
        });
      });
    },
    // 用户管理
    handleUserManage(row) {
      this.currentSupplierId = row.id;
      this.selectedUserId = null;
      this.userSearchOptions = [];
      this.loadBoundUsers();
      this.userManageDialogVisible = true;
    },
    loadBoundUsers() {
      getSupplierUsers(this.currentSupplierId).then((res) => {
        this.boundUserList = res.data || [];
      });
    },
    handleBindUser() {
      if (!this.selectedUserId) return;
      // 去重校验
      const alreadyBound = this.boundUserList.some(
        (u) => u.userId === this.selectedUserId
      );
      if (alreadyBound) {
        this.$message.warning("该用户已绑定此厂商，请勿重复绑定");
        return;
      }
      bindSupplierUser({
        supplierId: this.currentSupplierId,
        userId: this.selectedUserId,
      }).then(() => {
        this.$message.success("绑定成功");
        this.selectedUserId = null;
        this.userSearchOptions = [];
        this.loadBoundUsers();
      });
    },
    handleUnbindUser(row) {
      this.$confirm("确定要解除该用户与当前厂商的绑定吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        unbindSupplierUser({
          supplierId: this.currentSupplierId,
          userId: row.userId,
        }).then(() => {
          this.$message.success("解除绑定成功");
          this.loadBoundUsers();
        });
      }).catch(() => {});
    },
    remoteSearchUsers(query) {
      if (query !== "") {
        this.userSearchLoading = true;
        listUser({ userName: query, pageNum: 1, pageSize: 20 }).then(
          (res) => {
            this.userSearchOptions = res.rows || [];
            this.userSearchLoading = false;
          }
        );
      } else {
        this.userSearchOptions = [];
      }
    },
    /** 查询供应商信息列表 */
    getList() {
      this.loading = true;
      listSupplierMessage({
        ...this.queryParams,
      }).then((response) => {
        this.supplierMessageList = response.rows;
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
        supplierName: null,
        supplierPhone: null,
        supplierType: null,
        legalRepresentative: null,
        turnoverTime: null,
        staffNumber: null,
        establishedTime: null,
        supplierAddress: null,
        supplierIntroduce: null,
        equipmentConditions: null,
        businessLicense: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null,
        registeredCapital: null,
        delFlag: null,
      };
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
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.id);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加供应商信息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getSupplierMessage(id).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = "修改供应商信息";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.id != null) {
            updateSupplierMessage({
              ...this.form,
            }).then((response) => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addSupplierMessage({
              ...this.form,
            }).then((response) => {
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
      this.$modal
        .confirm('是否确认删除供应商信息编号为"' + ids + '"的数据项？')
        .then(function () {
          return delSupplierMessage(ids);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download(
        "school/supplierMessage/export",
        {
          ...this.queryParams,
        },
        `supplierMessage_${new Date().getTime()}.xlsx`
      );
    },
  },
};
</script>