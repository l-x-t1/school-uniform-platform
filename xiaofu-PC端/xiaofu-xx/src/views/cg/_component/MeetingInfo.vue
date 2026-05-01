<template>
  <div class="detail-panel">
    <!-- <h3>班子会议纪要报备与公开</h3> -->
    <el-form v-loading="loading">
      <el-form-item label="会议名称：">{{
        meetingData.title || "-"
      }}</el-form-item>
      <el-form-item label="会议时间：">{{
        formatDate(meetingData.meetingTime)
      }}</el-form-item>
      <el-form-item label="参会人员：">{{
        meetingData.meetingUser || "-"
      }}</el-form-item>
      <el-form-item label="拟担任选用组织组长：">
        <el-table :data="leaderData">
          <el-table-column label="职务" prop="zw" />
          <el-table-column label="代表类别" prop="dblb">
            <template slot-scope="scope">
              <span>{{ getDictLabel(scope.row.dblb, "sc_dble") }}</span>
            </template>
          </el-table-column>
          <el-table-column label="姓名" prop="zzName" />
        </el-table>
      </el-form-item>
      <el-form-item label="选购需求：">
        <el-table :data="purchaseData">
          <el-table-column label="采购年级（级）" prop="cgnj" />
          <el-table-column label="校服品类" prop="xfpl">
            <template slot-scope="scope">
              <span>{{ getDictLabel(scope.row.xfpl, "xfpl") }}</span>
            </template>
          </el-table-column>
          <el-table-column label="每个学生拟购买套数" prop="gmts" />
          <el-table-column label="每套价格区间（拟）(元)" prop="jgqj" />
          <el-table-column label="备注" prop="bz" />
        </el-table>
        <!-- <div class="service-years-info" style="margin-top: 15px">
          <span class="label">供应企业服务年限（拟）：</span>
          <span class="value">{{ meetingData.gyfwnx || "-" }}</span>
        </div> -->
      </el-form-item>
      <el-form-item label="会议纪要：">
        <FileUpload v-model="meetingData.hyjy" :disabled="true"></FileUpload>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { getMeeting } from "@/api/cg/index";

export default {
  name: "MeetingInfo",
  dicts: ["sc_dble", "xfpl"],
  props: {
    formId: {
      type: [String, Number],
      required: true,
    },
  },
  data() {
    return {
      loading: false,
      meetingData: {},
      leaderData: [],
      purchaseData: [],
    };
  },
  watch: {
    formId: {
      immediate: true,
      handler(val) {
        if (val) {
          this.fetchMeetingData();
        }
      },
    },
  },
  methods: {
    fetchMeetingData() {
      if (!this.formId) return;

      this.loading = true;
      getMeeting(this.formId)
        .then((res) => {
          this.meetingData = res.data || {};

          // 处理组织组长数据
          if (this.meetingData.dblb && this.meetingData.zzName) {
            this.leaderData = [
              {
                zw: "组长",
                dblb: this.meetingData.dblb,
                zzName: this.meetingData.zzName,
              },
            ];
          } else {
            this.leaderData = [];
          }

          // 处理选购需求数据
          if (this.meetingData.teamMeetingSonList) {
            this.purchaseData = this.meetingData.teamMeetingSonList;
          } else if (this.meetingData.sonJsonData) {
            try {
              this.purchaseData = JSON.parse(this.meetingData.sonJsonData);
            } catch (error) {
              this.purchaseData = [];
            }
          } else {
            this.purchaseData = [];
          }
        })
        .catch((error) => {
          console.error("获取会议数据失败:", error);
          this.$message.error("获取会议数据失败");
        })
        .finally(() => {
          this.loading = false;
        });
    },
    formatDate(date) {
      if (!date) return "-";
      return this.parseTime(date, "{y}-{m}-{d}");
    },
    getDictLabel(value, type) {
      const dictType = type;
      if (!value) return "-";
      const dict = this.dict.type[dictType];
      if (!dict) return value;

      const match = dict.find((item) => item.value == value);
      return match ? match.label : value;
    },
    handlePreview(fileInfo) {
      if (typeof fileInfo === "string") {
        try {
          const fileObj = JSON.parse(fileInfo);
          // 调用预览文件的方法或跳转到文件URL
          if (fileObj.url) {
            window.open(fileObj.url);
          }
        } catch (error) {
          console.error("文件预览失败:", error);
        }
      } else if (fileInfo && fileInfo.url) {
        window.open(fileInfo.url);
      }
    },
  },
};
</script>

<style scoped>
.detail-panel {
  padding: 20px 0;
}

.detail-panel h3 {
  margin-top: 0;
  margin-bottom: 9px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.service-years-info {
  margin: 20px 0;
  padding: 12px 15px;
  background: #f5f7fa;
  border-radius: 4px;
  border-left: 3px solid #409eff;
}

.service-years-info .label {
  font-weight: bold;
  margin-right: 10px;
  color: #303133;
}

.service-years-info .value {
  color: #606266;
}

/* Make form item labels bold */
::v-deep .el-form-item__label {
  font-weight: bold;
}

/* Make table headers bold */
::v-deep .el-table th {
  font-weight: bold;
}
::v-deep .el-form-item {
  margin-bottom: 0px;
}
</style>
