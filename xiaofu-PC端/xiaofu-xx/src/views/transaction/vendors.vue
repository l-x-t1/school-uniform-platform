<template>
  <div class="app-container">
    <el-form
      v-if="showSearch"
      :model="queryParams"
      ref="queryForm"
      :inline="true"
    >
      <el-form-item label="区域" prop="parentDeptId">
        <el-select v-model="queryParams.parentDeptId">
          <el-option
            label="全部"
            v-if="$store.state.user.deptId == 100"
            :value="100"
          ></el-option>
          <el-option
            v-for="item of deptList"
            :key="item.deptId"
            :label="item.deptName"
            :value="item.deptId"
          ></el-option>
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
        <el-button type="primary" size="mini" icon="el-icon-search" @click="handleQuery"
          >搜索</el-button
        >
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        <el-button type="primary" icon="el-icon-download" size="mini" @click="handleExport"
          >导出数据</el-button
        >
      </el-form-item>
    </el-form>

    <el-table style="margin-top: 20px" v-loading="loading" :data="tableData">
      <el-table-column type="selection" width="55" align="center" />

      <el-table-column
        type="index"
        align="center"
        label="序号"
      ></el-table-column>
      <el-table-column
        align="center"
        label="厂商名称"
        prop="supplierName"
      ></el-table-column>
      <el-table-column
        align="center"
        label="厂商规模(人)"
        prop="staffNumber"
      ></el-table-column>
      <el-table-column
        align="center"
        label="注册资本(万元)"
        prop="registeredCapital"
      >
      </el-table-column>
      <el-table-column
        align="center"
        label="服务学校数量(所)"
        prop="serveSchoolNumber"
      >
      </el-table-column>
      <el-table-column align="center" label="是否是黑名单">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.blacklistStatus == 1" type="danger"
            >是</el-tag
          >
          <el-tag v-else type="success">否</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" width="260" label="操作">
        <template slot-scope="scope">
          <el-button
            icon="el-icon-view"
            type="text"
            @click="handleViewSchoolList(scope.row)"
            >查看服务学校列表</el-button
          >
          <el-button
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
            <i class="el-icon-user"></i>
            厂商名称
          </template>
          {{ vendorDetail.supplierName }}
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
        <el-table-column prop="deptName" label="学校名称"></el-table-column>
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
  </div>
</template>
<script>
import {
  getVendorsList,
  addOrRemoveBlackList,
  getSchoolList,
  getVendorDetail,
} from "@/api/jiaoyuju/vendors";
import { getDeptListByParentDeptId } from "@/api/jiaoyuju/process";
export default {
  name: "School",
  data() {
    return {
      vendorDetail: {},
      schoolListTotal: 0,
      serverSchoolListQueryParams: {
        pageNum: 1,
        pageSize: 10,
      },
      loading: false,
      dateRange: [],
      deptList: [],
      showSearch: true,
      tableData: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        parentDeptId: 100,
      },
      blackListDialogVisible: false,
      blackListTableData: [],
      schoolListDialogVisible: false,
      schoolListTableData: [],
    };
  },
  created() {
    this.getList();
    this.getDeptList();
  },
  methods: {
    handleExport() {
      this.download(
        "/school/supplierMessage/export",
        {
          ...this.queryParams,
        },
        `vendors_${new Date().getTime()}.xlsx`
      );
    },
    //添加黑名单
    handleAddBlackList(row) {
      //是否确认添加黑名单
      this.$confirm("是否确认添加黑名单？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        addOrRemoveBlackList({
          id: row.id,
          blacklistStatus: 1,
        }).then((res) => {
          this.$message.success("添加黑名单成功");
          this.getList();
        });
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
    //厂商黑名单
    handleBlackList() {
      this.blackListDialogVisible = true;
    },
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
    getList() {
      this.loading = true;
      getVendorsList(this.queryParams).then((res) => {
        this.tableData = res.rows;
        this.total = res.total;
        this.loading = false;
      });
    },
    getDeptList() {
      getDeptListByParentDeptId({
        parentDeptId: "",
      }).then((res) => {
        this.deptList = res.data;
      });
    },
    handleQuery() {
      this.getList();
    },
    resetQuery() {
      this.$refs.queryForm.resetFields();
      this.getList();
    },
  },
};
</script>
<style scoped>
.el-range-separator {
  padding: 0 5px;
  line-height: 32px;
  width: auto;
}
</style>
