<template>
  <div class="app-container">
    <el-row>
      <el-col :span="8">
        <el-button type="primary" icon="el-icon-download" @click="handleExport"
          >导出数据</el-button
        >
      </el-col>
    </el-row>

    <el-table style="margin-top: 20px" v-loading="loading" :data="tableData">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center" type="index" />
      <el-table-column
        align="center"
        label="区县名称"
        prop="deptName"
      >
      <template slot="header" slot-scope="scope">
        <span>区县名称</span> 
      </template>
    </el-table-column> 
      <el-table-column
        align="center"
        label="已完善信息学校数量（所）"
        prop="schoolComplete"
      >
      </el-table-column>
      <el-table-column
        align="center"
        label="未完善信息学校数量（所）"
        prop="schoolNotComplete"
      >
      </el-table-column>

      <el-table-column align="center" width="260" label="操作">
        <template slot-scope="scope">
          <el-button type="text" @click="handleDetail(scope.row)"
            >详细</el-button
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
      fullscreen
      title="详细信息"
      :visible.sync="detailDialogVisible"
      width="70%"
    >
      <div v-if="detailDialogVisible">
        <!-- <el-tabs v-model="activeName"> -->
          <!-- <el-tab-pane label="学校类别" name="first">
            <levelSystem :deptId="deptId"></levelSystem>
          </el-tab-pane>

          <el-tab-pane label="学校列表" name="third"> -->
            <schoolList  :deptId="deptId"></schoolList>
          <!-- </el-tab-pane> -->
        <!-- </el-tabs> -->
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关 闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import levelSystem from "./_component/levelSystem";
import schoolList from "./_component/schooList";
import { getSchoolList } from "@/api/jiaoyuju/schoolManagement";
import { getDeptListByParentDeptId } from "@/api/jiaoyuju/process";
export default {
  name: "School",
  components: {
    levelSystem,
    schoolList,
  },
  data() {
    return {
      activeName: "first",
      deptId: null,
      detailDialogVisible: false,
      loading: false,
      dateRange: [],
      deptList: [],
      showSearch: true,
      tableData: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
      },
    };
  },
  created() {
    this.getList();
    this.getDeptList();
  },
  methods: {
    //查看详细
    handleDetail(row) {
      console.log(row);
      this.deptId = row.deptId;
      this.detailDialogVisible = true;
    },
    handleExport() {
      this.download(
        "/system/dept/completeExport",
        {
          ...this.queryParams,
        },
        `schoolManagement_${new Date().getTime()}.xlsx`
      );
    },

    getList() {
      this.loading = true;
      getSchoolList(this.queryParams).then((res) => {
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
