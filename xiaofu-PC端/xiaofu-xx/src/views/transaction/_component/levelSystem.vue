<template>
  <div class="">
    <el-table :data="tableData" style="width: 100%">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center" type="index" />
      <el-table-column prop="schoolType" label="学校类别" width="180">
      </el-table-column>
      <el-table-column
        prop="number"
        label="已完善信息学校数量(所)"
        align="center"
      >
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { getSchoolSecondLevelList } from "@/api/jiaoyuju/schoolManagement";
export default {
  name: "LevelSystem",
  data() {
    return {
      selectedRegion: "市直", // Default selected region
      tableData: [],
    };
  },
  props: {
    deptId: {
      type: String,
      default: "",
    },
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      getSchoolSecondLevelList({
        deptId: this.deptId,
      }).then((res) => {
        this.tableData = res.data;
      });
    },
  },
};
</script>
