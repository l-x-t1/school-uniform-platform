<template>
  <div class="app-container">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="质检公示" name="announcement">
        <announcement></announcement>
      </el-tab-pane>
      <el-tab-pane label="质检统计" name="school">
        <school></school>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import announcement from "./_component/announcement.vue";
import school from "./_component/school.vue";
export default {
  components: {
    announcement,
    school,
  },
  data() {
    return {
      activeTab: "announcement",
    };
  },
};
</script>
<style scoped></style>
