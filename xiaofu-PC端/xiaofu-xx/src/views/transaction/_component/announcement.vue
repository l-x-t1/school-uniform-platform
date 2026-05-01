<template>
  <div>
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
      <!-- 学校名称 -->
      <el-form-item label="学校名称" prop="schoolName">
        <el-input
          placeholder="请输入学校名称"
          v-model="queryParams.schoolName"
        />
      </el-form-item>

      <el-form-item label="公告发布日期" prop="title">
        <el-date-picker
          clearable
          v-model="dateRange"
          type="daterange"
          value-format="yyyy-MM-dd"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item label="厂商名称" prop="csName">
        <el-input
          v-model="queryParams.csName"
          placeholder="请输入厂商名称"
        ></el-input>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery"
          >搜索</el-button
        >
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="tableData">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column
        type="index"
        align="center"
        label="序号"
      ></el-table-column>
      <el-table-column
        align="center"
        label="公告名称"
        prop="ggName"
      ></el-table-column>

      <el-table-column
        align="center"
        label="学校名称"
        prop="schoolName"
      ></el-table-column>
      <el-table-column
        align="center"
        label="厂商名称"
        prop="csName"
      ></el-table-column>
      <el-table-column
        align="center"
        label="发布日期"
        prop="fbDate"
      ></el-table-column>
      <el-table-column align="center" label="首次质检">
        <template slot-scope="scope">
          <div style="display: flex;align-items: center;justify-content: center;">
            <el-tag v-if="scope.row.firstzj == '1'" type="success">
              有质检报告
            </el-tag>
            <el-tag v-else type="info">无质检报告</el-tag>
            <div v-if="scope.row.firstzj == '1'" style="margin-left: 5px">
              <el-button
                type="text"
                size="mini"
                @click="handlePreviewFirst(scope.row)"
                >预览</el-button
              >
            </div>
          </div> 
        </template>
      </el-table-column>
      <el-table-column align="center" label="二次质检">
        <template slot-scope="scope">
          <div style="display: flex;align-items: center;justify-content: center;">
            <el-tag v-if="scope.row.secondzj == '1'" type="success">
              有质检报告
            </el-tag>
            <el-tag v-else type="info">无质检报告</el-tag>
            <div v-if="scope.row.secondzj == '1'" style="margin-left: 5px">
              <el-button
                type="text"
                size="mini"
                @click="handlePreviewSecond(scope.row)"
                >预览</el-button
              >
            </div>
          </div>
          
        </template>
      </el-table-column>
      <!-- <el-table-column align="center" label="操作">
        <template slot-scope="scope">
          <el-button type="text" @click="handleView(scope.row)">查看</el-button>
        </template>
      </el-table-column> -->
    </el-table>
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
    <!-- 质检报告附件对话框 -->
    <el-dialog title="质检报告附件" :visible.sync="dialogVisible" width="50%">
      <div>
        <el-form>
          <el-form-item v-if="firstFile" label="首次质检报告">
            <FileUpload disabled v-model="firstFile"></FileUpload>
          </el-form-item>
          <el-form-item v-if="secondFile" label="二次质检报告">
            <FileUpload disabled v-model="secondFile"></FileUpload>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>

    <!-- 文件预览对话框 -->
    <el-dialog
      title="文件预览"
      :visible.sync="previewDialogVisible"
      width="80%"
      :close-on-click-modal="false"
      top="5vh"
    >
      <div class="preview-container">
        <iframe
          v-if="previewUrl"
          :src="previewUrl"
          class="preview-iframe"
          frameborder="0"
        ></iframe>
        <div v-else class="preview-loading">
          <i class="el-icon-loading"></i>
          <span>正在加载预览...</span>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="previewDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import {
  getDeptListByParentDeptId,
  getNoticeStatisticsDataList,
  getNoticeInspectSonFiles,
} from "@/api/jiaoyuju/process";
import { Base64 } from "js-base64";
export default {
  name: "Announcement",
  data() {
    return {
      loading: false,
      dateRange: [],
      deptList: [],
      showSearch: true,
      tableData: [],
      total: 0,
      firstFile: "",
      secondFile: "",
      dialogVisible: false,
      previewDialogVisible: false,
      previewUrl: "",
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        parentDeptId: null,
        schoolName: "",
        schoolType: "",
        selectType: "",
        bblbValue: "",
        startDate: "",
        endDate: "",
        csName: "",
      },
    };
  },
  created() {
    this.queryParams.parentDeptId = this.$store.state.user.deptId;
    this.getList();
    this.getDeptList();
  },
  methods: {
    getList() {
      this.loading = true;
      getNoticeStatisticsDataList(
        this.addDateRange({ ...this.queryParams }, this.dateRange)
      ).then((res) => {
        this.tableData = res.rows;
        this.total = res.total;
        this.loading = false;
      });
    },
    getDeptList() {
      getDeptListByParentDeptId({
        parentDeptId: this.$store.state.user.deptId,
      }).then((res) => {
        this.deptList = res.data;
      });
    },
    handleQuery() {
      this.getList();
    },
    resetQuery() {
      this.$refs.queryForm.resetFields();
      this.queryParams.parentDeptId = this.$store.state.user.deptId;
      this.dateRange = [];
      this.getList();
    },
    handleView(row) {
      // 处理查看操作，可能需要查看质检报告
      getNoticeInspectSonFiles({
        teamMeetingId: row.teamMeetingId,
      }).then((res) => {
        this.firstFile = "";
        this.secondFile = "";
        if (res.data.one) {
          this.firstFile = res.data.one[0];
        }
        if (res.data.two) {
          this.secondFile = res.data.two[0];
        }
        this.dialogVisible = true;
      });
      // 这里可以添加跳转或弹窗逻辑查看质检报告
    },
    // 预览首次质检报告
    handlePreviewFirst(row) {
      getNoticeInspectSonFiles({
        teamMeetingId: row.teamMeetingId,
      })
        .then((res) => {
          if (res.data.one && res.data.one[0]) {
            this.previewFile(res.data.one[0]);
          } else {
            this.$modal.msgWarning("首次质检报告文件不存在");
          }
        })
        .catch(() => {
          this.$modal.msgError("获取质检报告失败");
        });
    },
    // 预览二次质检报告
    handlePreviewSecond(row) {
      getNoticeInspectSonFiles({
        teamMeetingId: row.teamMeetingId,
      })
        .then((res) => {
          if (res.data.two && res.data.two[0]) {
            this.previewFile(res.data.two[0]);
          } else {
            this.$modal.msgWarning("二次质检报告文件不存在");
          }
        })
        .catch(() => {
          this.$modal.msgError("获取质检报告失败");
        });
    },
    // 文件预览方法
    previewFile(fileUrl) {
      if (!fileUrl) {
        this.$modal.msgWarning("文件路径不存在");
        return;
      }

      try {
        // 构建完整的文件URL
        let fullUrl = fileUrl;
        if (!fullUrl.startsWith("http://") && !fullUrl.startsWith("https://")) {
          fullUrl = process.env.VUE_APP_IMAGE_BASE_API + fileUrl;
        }

        // 使用Base64编码URL
        //const b64Encoded = Base64.encode(fullUrl);
        const previewUrl = fullUrl
          //process.env.VUE_APP_KKFILE2 + encodeURIComponent(b64Encoded);

        // 设置预览URL并显示对话框
        this.previewUrl = previewUrl;
        this.previewDialogVisible = true;
      } catch (error) {
        console.error("预览文件失败:", error);
        this.$modal.msgError("预览文件失败");
      }
    },
  },
};
</script>
<style scoped>
.preview-container {
  width: 100%;
  height: 70vh;
  position: relative;
}

.preview-iframe {
  width: 100%;
  height: 100%;
  border: none;
}

.preview-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #999;
  font-size: 14px;
}

.preview-loading i {
  font-size: 24px;
  margin-bottom: 10px;
}
</style>
