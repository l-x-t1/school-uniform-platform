<template>
  <div class="app-container">
    <areaList
      v-if="index == 1"
      @changeTime="changeTime"
      @area-click="handleAreaClick"
    ></areaList>
    <schoolList
      v-if="index == 2"
      :areaId="selectedArea"
      :dateArray="dateRange"
      @back="handleBack"
    ></schoolList>
  </div>
</template>

<script>
import areaList from "./areaList.vue";
import schoolList from "./schoolList.vue";
export default {
  name: "Dinggoutongji",
  components: {
    areaList,
    schoolList,
  },
  data() {
    return {
      index: 1,
      selectedArea: null,
      dateRange: [],
    };
  },
  created() {},
  methods: {
    handleAreaClick(area) {
      this.selectedArea = area.deptId;
      this.index = 2;
    },
    handleBack() {
      this.index = 1;
    },
    changeTime(e) {
      this.dateRange = e;
      console.log(this.dateRange);
    },
  },
};
</script>
