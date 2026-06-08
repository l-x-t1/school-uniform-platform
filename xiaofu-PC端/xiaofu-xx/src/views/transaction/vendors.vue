<template>
  <div class="app-container">
    <el-form
      v-if="showSearch"
      ref="queryForm"
      :model="queryParams"
      :inline="true"
    >
      <el-form-item label="区域" prop="parentDeptId">
        <el-select v-model="queryParams.parentDeptId">
          <el-option
            v-if="$store.state.user.deptId == 100"
            label="全部"
            :value="100"
          />
          <el-option
            v-for="item of deptList"
            :key="item.deptId"
            :label="item.deptName"
            :value="item.deptId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="厂商名称" prop="supplierName">
        <el-input
          v-model="queryParams.supplierName"
          placeholder="请输入厂商名称"
        />
      </el-form-item>
      <el-form-item label="是否是黑名单" prop="blacklistStatus">
        <el-select v-model="queryParams.blacklistStatus" placeholder="请选择">
          <el-option label="全部" value="" />
          <el-option label="是" :value="1" />
          <el-option label="否" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          size="mini"
          icon="el-icon-search"
          @click="handleQuery"
        >
          搜索
        </el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">
          重置
        </el-button>
        <el-button
          type="primary"
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
        >
          导出数据
        </el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" style="margin-top: 20px" :data="tableData">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column type="index" align="center" label="序号" />
      <el-table-column
        align="center"
        label="厂商名称"
        prop="supplierName"
      />
      <el-table-column
        align="center"
        label="厂商规模(人)"
        prop="staffNumber"
      />
      <el-table-column
        align="center"
        label="注册资本(万元)"
        prop="registeredCapital"
      />
      <el-table-column
        align="center"
        label="服务学校数量(所)"
        prop="serveSchoolNumber"
      />
      <el-table-column align="center" label="是否是黑名单">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.blacklistStatus == 1" type="danger">
            是
          </el-tag>
          <el-tag v-else type="success">否</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" width="420" label="操作">
        <template slot-scope="scope">
          <el-button
            icon="el-icon-view"
            type="text"
            @click="handleViewSchoolList(scope.row)"
          >
            查看服务学校列表
          </el-button>
          <el-button
            icon="el-icon-user"
            type="text"
            @click="handleUserManage(scope.row)"
          >
            用户管理
          </el-button>
          <el-button
            icon="el-icon-edit"
            type="text"
            @click="handleEdit(scope.row)"
          >
            修改
          </el-button>
          <el-button
            icon="el-icon-delete"
            type="text"
            @click="handleDelete(scope.row)"
          >
            删除
          </el-button>
          <el-button
            v-if="scope.row.blacklistStatus == 0"
            icon="el-icon-circle-plus"
            type="text"
            @click="handleAddBlackList(scope.row)"
          >
            添加黑名单
          </el-button>
          <el-button
            v-if="scope.row.blacklistStatus == 1"
            icon="el-icon-circle-close"
            type="text"
            @click="handleRemove(scope.row)"
          >
            移除黑名单
          </el-button>
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
      title="服务学校列表"
      :visible.sync="schoolListDialogVisible"
      width="70%"
    >
      <el-descriptions class="margin-top" :column="2">
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-user" />
            厂商名称
          </template>
          {{ vendorDetail.supplierName }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-office-building" />
            厂商规模(人)
          </template>
          {{ vendorDetail.staffNumber }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-money" />
            注册资本(万)
          </template>
          {{ vendorDetail.registeredCapital }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-s-custom" />
            法定代表人
          </template>
          {{ vendorDetail.legalRepresentative }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-office-building" />
            公司地址
          </template>
          {{ vendorDetail.supplierAddress }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-postcard" />
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
        <el-table-column prop="deptName" label="学校名称" />
        <el-table-column
          prop="studentNumber"
          label="学生数量(人)"
          align="center"
        />
      </el-table>
      <pagination
        v-show="schoolListTotal > 0"
        :total="schoolListTotal"
        :page.sync="serverSchoolListQueryParams.pageNum"
        :limit.sync="serverSchoolListQueryParams.pageSize"
        @pagination="handlegetSchoolList"
      />
      <span slot="footer" class="dialog-footer">
        <el-button @click="schoolListDialogVisible = false">关 闭</el-button>
      </span>
    </el-dialog>

    <el-dialog
      :title="editTitle"
      :visible.sync="editDialogVisible"
      width="780px"
      append-to-body
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="100px"
      >
        <el-row>
          <el-col :span="12">
            <el-form-item label="厂商名称" prop="supplierName">
              <el-input
                v-model="editForm.supplierName"
                placeholder="请输入厂商名称"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="supplierPhone">
              <el-input
                v-model="editForm.supplierPhone"
                placeholder="请输入联系电话"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="法定代表人" prop="legalRepresentative">
              <el-input
                v-model="editForm.legalRepresentative"
                placeholder="请输入法定代表人"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职工人数" prop="staffNumber">
              <el-input
                v-model="editForm.staffNumber"
                placeholder="请输入职工人数"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="厂商地址" prop="supplierAddress">
              <el-input
                v-model="editForm.supplierAddress"
                placeholder="请输入厂商地址"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="注册资金" prop="registeredCapital">
              <el-input
                v-model="editForm.registeredCapital"
                placeholder="请输入注册资金，单位（万元）"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="成立日期" prop="establishedTime">
              <el-date-picker
                v-model="editForm.establishedTime"
                style="width: 100%"
                clearable
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择成立日期"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="企业类型" prop="supplierType">
              <el-input
                v-model="editForm.supplierType"
                placeholder="请输入企业类型"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="厂商介绍" prop="supplierIntroduce">
              <el-input
                v-model="editForm.supplierIntroduce"
                type="textarea"
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
                v-model="editForm.businessLicense"
                type="textarea"
                placeholder="请输入厂商资质信息"
                :rows="3"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" :loading="submitLoading" @click="submitEdit">
          保 存
        </el-button>
        <el-button @click="editDialogVisible = false">取 消</el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="用户管理"
      :visible.sync="userManageDialogVisible"
      width="760px"
      append-to-body
    >
      <div class="user-manage-header">
        <span>当前厂商：{{ currentSupplierName || "-" }}</span>
        <el-button
          type="primary"
          size="small"
          icon="el-icon-plus"
          @click="handleBindUserOpen"
        >
          绑定用户
        </el-button>
      </div>

      <el-table
        v-loading="userLoading"
        :data="boundUserList"
        border
        size="small"
        empty-text="暂无绑定用户"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="用户名" prop="userName" min-width="120" />
        <el-table-column label="昵称" prop="nickName" min-width="120" />
        <el-table-column label="手机号" prop="phonenumber" width="140" />
        <el-table-column label="操作" width="100" align="center">
          <template slot-scope="scope">
            <el-button type="text" @click="handleUnbindUser(scope.row)">
              解绑
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <span slot="footer" class="dialog-footer">
        <el-button @click="userManageDialogVisible = false">关 闭</el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="选择用户"
      :visible.sync="bindUserOpen"
      width="620px"
      append-to-body
    >
      <el-form :inline="true" size="small">
        <el-form-item>
          <el-input
            v-model="userSearchKeyword"
            placeholder="搜索用户名/昵称/手机号"
            clearable
            @keyup.enter.native="searchAvailableUsers"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            icon="el-icon-search"
            @click="searchAvailableUsers"
          >
            搜索
          </el-button>
        </el-form-item>
      </el-form>

      <el-table
        v-loading="userSearchLoading"
        :data="availableUserList"
        border
        size="small"
        max-height="360"
        empty-text="暂无可绑定用户"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="用户名" prop="userName" min-width="120" />
        <el-table-column label="昵称" prop="nickName" min-width="120" />
        <el-table-column label="手机号" prop="phonenumber" width="140" />
        <el-table-column label="操作" width="90" align="center">
          <template slot-scope="scope">
            <el-button
              type="text"
              :loading="scope.row._binding"
              @click="handleBindUser(scope.row)"
            >
              绑定
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <span slot="footer" class="dialog-footer">
        <el-button @click="bindUserOpen = false">关 闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  getVendorsList,
  addOrRemoveBlackList,
  getSchoolList,
  getVendorDetail,
  updateSupplierMessage,
  deleteSupplierMessage,
  getSupplierUsers,
  getAvailableUsers,
  bindSupplierUser,
  unbindSupplierUser
} from '@/api/jiaoyuju/vendors'
import { getDeptListByParentDeptId } from '@/api/jiaoyuju/process'

function createEmptyEditForm() {
  return {
    id: null,
    supplierName: '',
    supplierPhone: '',
    supplierType: '',
    legalRepresentative: '',
    staffNumber: '',
    establishedTime: '',
    supplierAddress: '',
    supplierIntroduce: '',
    businessLicense: '',
    registeredCapital: '',
    blacklistStatus: 0
  }
}

export default {
  name: 'School',
  data() {
    return {
      vendorDetail: {},
      schoolListTotal: 0,
      serverSchoolListQueryParams: {
        id: null,
        pageNum: 1,
        pageSize: 10
      },
      loading: false,
      deptList: [],
      showSearch: true,
      tableData: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        parentDeptId: 100,
        supplierName: '',
        blacklistStatus: ''
      },
      schoolListDialogVisible: false,
      schoolListTableData: [],
      editDialogVisible: false,
      editTitle: '修改厂商',
      editForm: createEmptyEditForm(),
      submitLoading: false,
      userManageDialogVisible: false,
      currentSupplierId: null,
      currentSupplierName: '',
      boundUserList: [],
      userLoading: false,
      bindUserOpen: false,
      userSearchKeyword: '',
      availableUserList: [],
      userSearchLoading: false,
      editRules: {
        supplierName: [
          { required: true, message: '厂商名称不能为空', trigger: 'blur' }
        ],
        supplierPhone: [
          { required: true, message: '联系电话不能为空', trigger: 'blur' },
          {
            pattern: /^1[3-9]\d{9}$/,
            message: '请输入正确的手机号码',
            trigger: 'blur'
          }
        ],
        legalRepresentative: [
          { required: true, message: '法定代表人不能为空', trigger: 'blur' }
        ],
        supplierType: [
          { required: true, message: '企业类型不能为空', trigger: 'blur' }
        ],
        staffNumber: [
          { required: true, message: '职工人数不能为空', trigger: 'blur' }
        ],
        establishedTime: [
          { required: true, message: '成立日期不能为空', trigger: 'change' }
        ],
        supplierAddress: [
          { required: true, message: '厂商地址不能为空', trigger: 'blur' }
        ],
        registeredCapital: [
          { required: true, message: '注册资金不能为空', trigger: 'blur' }
        ],
        supplierIntroduce: [
          { required: true, message: '厂商介绍不能为空', trigger: 'blur' }
        ],
        businessLicense: [
          { required: true, message: '厂商资质不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
    this.getDeptList()
  },
  methods: {
    handleExport() {
      this.download(
        '/school/supplierMessage/export',
        {
          ...this.queryParams
        },
        `vendors_${new Date().getTime()}.xlsx`
      )
    },
    handleAddBlackList(row) {
      this.$confirm('是否确认添加黑名单？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        addOrRemoveBlackList({
          id: row.id,
          blacklistStatus: 1
        }).then(() => {
          this.$message.success('添加黑名单成功')
          this.getList()
        })
      })
    },
    handleRemove(row) {
      this.$confirm('是否确认移除黑名单？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        addOrRemoveBlackList({
          id: row.id,
          blacklistStatus: 0
        }).then(() => {
          this.$message.success('移除黑名单成功')
          this.getList()
        })
      })
    },
    handlegetSchoolList() {
      getSchoolList({
        id: this.serverSchoolListQueryParams.id,
        pageNum: this.serverSchoolListQueryParams.pageNum,
        pageSize: this.serverSchoolListQueryParams.pageSize
      }).then((res) => {
        this.schoolListTableData = res.rows
        this.schoolListTotal = res.total
      })
    },
    handleViewSchoolList(row) {
      this.serverSchoolListQueryParams.id = row.id
      this.serverSchoolListQueryParams.pageNum = 1
      this.handlegetSchoolList()
      getVendorDetail({
        id: row.id
      }).then((res) => {
        this.vendorDetail = res.data || {}
      })
      this.schoolListDialogVisible = true
    },
    getList() {
      this.loading = true
      getVendorsList(this.queryParams)
        .then((res) => {
          this.tableData = res.rows || []
          this.total = res.total || 0
        })
        .finally(() => {
          this.loading = false
        })
    },
    getDeptList() {
      getDeptListByParentDeptId({
        parentDeptId: ''
      }).then((res) => {
        this.deptList = res.data || []
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.$refs.queryForm.resetFields()
      this.queryParams.pageNum = 1
      this.queryParams.parentDeptId =
        this.$store.state.user.deptId === 100
          ? 100
          : this.$store.state.user.deptId
      this.getList()
    },
    resetEditForm() {
      this.editForm = createEmptyEditForm()
      if (this.$refs.editFormRef) {
        this.$refs.editFormRef.resetFields()
      }
    },
    handleEdit(row) {
      this.resetEditForm()
      getVendorDetail({ id: row.id }).then((res) => {
        this.editForm = {
          ...createEmptyEditForm(),
          ...(res.data || {})
        }
        this.editTitle = '修改厂商'
        this.editDialogVisible = true
      })
    },
    submitEdit() {
      this.$refs.editFormRef.validate((valid) => {
        if (!valid) {
          return
        }
        this.submitLoading = true
        updateSupplierMessage(this.editForm)
          .then(() => {
            this.$message.success('修改成功')
            this.editDialogVisible = false
            this.getList()
          })
          .finally(() => {
            this.submitLoading = false
          })
      })
    },
    handleDelete(row) {
      this.$confirm('是否确认删除该厂商？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteSupplierMessage(row.id).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      })
    },
    handleUserManage(row) {
      this.currentSupplierId = row.id
      this.currentSupplierName = row.supplierName
      this.userManageDialogVisible = true
      this.loadBoundUsers()
    },
    loadBoundUsers() {
      if (!this.currentSupplierId) {
        return
      }
      this.userLoading = true
      getSupplierUsers(this.currentSupplierId)
        .then((res) => {
          this.boundUserList = res.data || []
        })
        .finally(() => {
          this.userLoading = false
        })
    },
    handleBindUserOpen() {
      this.userSearchKeyword = ''
      this.availableUserList = []
      this.bindUserOpen = true
      this.searchAvailableUsers()
    },
    searchAvailableUsers() {
      this.userSearchLoading = true
      getAvailableUsers(this.userSearchKeyword || null)
        .then((res) => {
          this.availableUserList = res.data || []
        })
        .finally(() => {
          this.userSearchLoading = false
        })
    },
    handleBindUser(row) {
      this.$set(row, '_binding', true)
      bindSupplierUser({
        supplierId: this.currentSupplierId,
        userId: row.userId
      })
        .then(() => {
          this.$message.success('绑定成功')
          this.loadBoundUsers()
          this.searchAvailableUsers()
        })
        .finally(() => {
          this.$set(row, '_binding', false)
        })
    },
    handleUnbindUser(row) {
      this.$confirm('是否确认解绑该用户？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        unbindSupplierUser({
          supplierId: this.currentSupplierId,
          userId: row.userId
        }).then(() => {
          this.$message.success('解绑成功')
          this.loadBoundUsers()
        })
      })
    }
  }
}
</script>

<style scoped>
.el-range-separator {
  padding: 0 5px;
  line-height: 32px;
  width: auto;
}

.user-manage-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
</style>
