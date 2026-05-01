<template>
  <view class="page">
    <view class="process-banner" v-if="processHead">
      <text class="process-banner-name">{{
        processHead.purchaseName || "采购流程"
      }}</text>
      <text class="process-banner-meta" v-if="processBannerSub">{{
        processBannerSub
      }}</text>
    </view>
    <view class="card">
      <view class="card-title">流程名称</view>
      <view class="step-list">
        <view
          v-for="(item, index) in displaySteps"
          :key="item.pcType"
          class="step-row"
          :class="{ 'step-row--active': selectedIndex === index }"
          @click="onSelectStep(index)"
        >
          <view v-if="selectedIndex === index" class="step-accent"></view>
          <text
            class="step-index"
            :class="{ 'step-index--active': selectedIndex === index }"
            >{{ index + 1 }}</text
          >
          <view class="step-dot" :class="dotClass(index)"></view>
          <text
            class="step-label"
            :class="{ 'step-label--active': selectedIndex === index }"
            >{{ item.title }}</text
          >
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import {
  DEFAULT_STATS_DEPT_ID,
  DEFAULT_TEAM_MEETING_TEAM_ID,
  fetchCglcList,
  fetchCgType,
  fetchProcInfoDetail,
  fetchProcessMenu,
  fetchStatsTeamMeetingList,
  getStatsFormTeamId,
  setStatsFormTeamId,
  setStatsTeamMeetingId,
} from "./qr-open-api.js";

/** 与 PC `cg/_component/index.vue` 中 `menu` / `detailMenu` 的 title、type 顺序一致 */
const PROCEDURE_MENU_FULL = [
  { pcType: 0, title: "班子会议纪要报备与公开" },
  { pcType: 1, title: "家长购买意向征集管理" },
  { pcType: 2, title: "选用组织名单报备与公开" },
  { pcType: 3, title: "选用组织确定选购模式报备与公开" },
  { pcType: 4, title: "发布公告" },
  { pcType: 5, title: "备选公告展示" },
  { pcType: 6, title: "组建评定小组投票确定厂商" },
  { pcType: 7, title: "选用组织选购结果公示报备与公开" },
  { pcType: 8, title: "学校采购合同报备与公开" },
  { pcType: 9, title: "产品质量检验报告报备与公开" },
  { pcType: 10, title: "选用组织家长满意度总结报告报备与公开" },
  { pcType: 11, title: "采购信息报备" },
  { pcType: 12, title: "学校校服采购工作总结" },
];

export default {
  data() {
    return {
      /** 班子会议列表接口 deptId；扫码进入可带 ?deptId= 覆盖；默认 100 */
      deptId: DEFAULT_STATS_DEPT_ID,
      /** /school/cglcprocess/list 首条采购流程，用于页头展示 */
      processHead: null,
      /** 班子会议列表首条主键 id；与后续 teamMeetingId、teamId（getProcessMenu 等）一致 */
      teamMeetingId: "",
      /** 班子会议列表筛选用 teamId：优先路由 ?teamId=，否则 cglc 行 teamId，再否则默认 1102（见 DEFAULT_TEAM_MEETING_TEAM_ID） */
      routeTeamId: "",
      /** 与 PC `getCgType` / `res.msg` 一致：2 为招投标（侧栏去掉第 6、7 步），否则为完整 13 步中的对应关系 */
      cgTypeNum: null,
      /** PC getProcessMenu 返回并按招投标规则处理后的状态数组（1 组 0 等） */
      processStatesRaw: [],
      selectedIndex: 1,
    };
  },
  computed: {
    /** 与 PC 一致：招投标 `cgType===2` 时侧栏去掉第 6、7 步（下标 5、6），共 11 步；否则 13 步 */
    displaySteps() {
      if (this.cgTypeNum === 2) {
        const m = PROCEDURE_MENU_FULL.slice();
        m.splice(5, 2);
        return m;
      }
      return PROCEDURE_MENU_FULL;
    },
    processBannerSub() {
      const p = this.processHead;
      if (!p) return "";
      const parts = [];
      if (p.purchaseYear) parts.push(String(p.purchaseYear));
      if (p.cgTime) parts.push("采购时间 " + p.cgTime);
      return parts.join(" · ");
    },
  },
  watch: {
    displaySteps: {
      handler(steps) {
        if (!steps || !steps.length) return;
        if (this.selectedIndex > steps.length - 1) {
          this.selectedIndex = Math.max(0, steps.length - 1);
        }
      },
      immediate: true,
    },
  },
  onLoad(options) {
    uni.setNavigationBarTitle({
      title: "校服采购流程",
    });
    if (options.deptId != null && options.deptId !== "") {
      this.deptId = String(options.deptId);
    }
    if (options.teamId != null && options.teamId !== "") {
      this.routeTeamId = String(options.teamId);
    }
    const step =
      options.step != null ? parseInt(String(options.step), 10) : NaN;
    /** 最多 13 步；加载 getCgType 后若为招投标会由 watch 收敛到 11 步内 */
    if (!isNaN(step) && step >= 1 && step <= PROCEDURE_MENU_FULL.length) {
      this.selectedIndex = step - 1;
    }
    const boot = async () => {
      if (this.deptId) {
        await this.loadCglcHead();
      } else {
        setStatsFormTeamId("");
        await this.syncTeamMeetingIdFromList();
        await this.loadProcessDots();
      }
    };
    boot();
  },
  methods: {
    async loadCglcHead() {
      try {
        const { rows } = await fetchCglcList({
          deptId: this.deptId,
          pageNum: 1,
          pageSize: 1,
        });
        const row = rows && rows[0];
        /** cglc 行 teamId 仅作班子会议列表筛选兜底（第一步仍以路由 ?teamId= 为准） */
        this.processHead = row
          ? {
              purchaseName: row.purchaseName,
              purchaseYear: row.purchaseYear,
              cgTime: row.cgTime,
              cglcTeamId:
                row.teamId != null && row.teamId !== ""
                  ? String(row.teamId)
                  : "",
              id: row.id,
            }
          : null;
        setStatsFormTeamId("");
        await this.syncTeamMeetingIdFromList();
        await this.loadProcessDots();
      } catch (e) {
        this.processHead = null;
        this.teamMeetingId = "";
        setStatsTeamMeetingId("");
        setStatsFormTeamId("");
        uni.showToast({
          title: (e && e.message) || "流程信息加载失败",
          icon: "none",
        });
        await this.loadProcessDots();
      }
    },
    /**
     * PC 侧栏红点/绿点：GET getProcessMenu + getCgType，`processStatesRaw` 与当前 `displaySteps` 下标一一对应（招投标时已 splice 与 PC 一致）
     */
    async loadProcessDots() {
      const teamId = this.teamMeetingId || getStatsFormTeamId() || "";
      if (!teamId) {
        this.cgTypeNum = null;
        this.processStatesRaw = [];
        return;
      }
      try {
        const [cgType, states] = await Promise.all([
          fetchCgType(teamId),
          fetchProcessMenu(teamId),
        ]);
        this.cgTypeNum = cgType != null ? cgType : null;
        let arr = Array.isArray(states) ? states.slice() : [];
        /** 招投标：与 PC 一致去掉第 6、7 步，仅当接口仍返回 13 位时裁剪 */
        if (cgType === 2 && arr.length >= 13) {
          arr.splice(5, 2);
        }
        this.processStatesRaw = arr;
      } catch (e) {
        this.cgTypeNum = null;
        this.processStatesRaw = [];
      }
    },
    rawStateForMobileStep(mobileIndex) {
      const arr = this.processStatesRaw;
      if (!arr || mobileIndex < 0 || mobileIndex >= arr.length) {
        return undefined;
      }
      const v = arr[mobileIndex];
      if (v === 1 || v === "1") {
        return 1;
      }
      if (v === 0 || v === "0") {
        return 0;
      }
      return undefined;
    },
    /**
     * 班子会议列表首条主键 id → teamMeetingId、statsFormTeamId（与 PC getProcessMenu 的 teamId 一致）；
     * 列表筛选：deptId + teamId（优先路由 ?teamId=，否则 cglc 行 teamId）
     */
    async syncTeamMeetingIdFromList() {
      try {
        const listFilterTeamId =
          this.routeTeamId ||
          DEFAULT_TEAM_MEETING_TEAM_ID;
        const { rows } = await fetchStatsTeamMeetingList({
          deptId: this.deptId || undefined,
          teamId: listFilterTeamId,
          omitDefaultTeamId: true,
          pageNum: 1,
          pageSize: 1,
        });
        const first = rows && rows[0];
        const id =
          first && first.id != null && first.id !== "" ? String(first.id) : "";
        this.teamMeetingId = id;
        setStatsTeamMeetingId(id);
        setStatsFormTeamId(id);
      } catch (e) {
        this.teamMeetingId = "";
        setStatsTeamMeetingId("");
        setStatsFormTeamId("");
      }
    },
    /** 采购信息报备：与 PC Caigoubaobei 一致按 formId(teamId) 直接 getSummary(teamId) */
    async goProcInfoDetail() {
      const teamId = this.teamMeetingId || getStatsFormTeamId() || "";
      if (!teamId) {
        uni.showToast({
          title: "缺少班子会议标识",
          icon: "none",
        });
        return;
      }
      try {
        const detail = await fetchProcInfoDetail(teamId);
        if (!detail || Object.keys(detail).length === 0) {
          uni.showToast({
            title: "暂无采购信息报备记录",
            icon: "none",
          });
          return;
        }
        uni.navigateTo({
          url:
            "/pages/stats/proc-info-detail?id=" +
            encodeURIComponent(String(teamId)),
        });
      } catch (e) {
        uni.showToast({
          title: (e && e.message) || "加载失败",
          icon: "none",
        });
      }
    },
    onSelectStep(index) {
      const row = this.displaySteps[index];
      if (!row) return;
      const pcType = row.pcType;
      const deptQ =
        this.deptId !== "" ? "deptId=" + encodeURIComponent(this.deptId) : "";
      const meetingQ =
        this.teamMeetingId !== ""
          ? "teamMeetingId=" + encodeURIComponent(this.teamMeetingId)
          : "";
      const teamIdQ =
        this.teamMeetingId !== ""
          ? "teamId=" + encodeURIComponent(this.teamMeetingId)
          : "";
      /** 第一步班子会议：teamId 优先路由，其次 cglc 行，缺省 1102 */
      const firstStepFilterTeamId =
        this.routeTeamId ||
        (this.processHead && this.processHead.cglcTeamId) ||
        DEFAULT_TEAM_MEETING_TEAM_ID;
      const firstStepTeamQ =
        "teamId=" + encodeURIComponent(firstStepFilterTeamId);

      if (pcType === 0) {
        let url = "/pages/stats/meeting-minutes";
        const parts = [];
        if (deptQ) parts.push(deptQ);
        parts.push(firstStepTeamQ);
        if (parts.length) url += "?" + parts.join("&");
        uni.navigateTo({ url });
        return;
      }
      if (pcType === 1) {
        const parts = [];
        if (deptQ) parts.push(deptQ);
        if (meetingQ) parts.push(meetingQ);
        let url = "/pages/stats/opinion-list";
        if (parts.length) url += "?" + parts.join("&");
        uni.navigateTo({ url });
        return;
      }
      if (pcType === 2) {
        const parts = [];
        if (deptQ) parts.push(deptQ);
        if (meetingQ) parts.push(meetingQ);
        let url = "/pages/stats/org-list";
        if (parts.length) url += "?" + parts.join("&");
        uni.navigateTo({ url });
        return;
      }
      if (pcType === 3) {
        const parts = [];
        if (deptQ) parts.push(deptQ);
        if (teamIdQ) parts.push(teamIdQ);
        let url = "/pages/stats/mode-list";
        if (parts.length) url += "?" + parts.join("&");
        uni.navigateTo({ url });
        return;
      }
      if (pcType === 4) {
        const parts = [];
        if (deptQ) parts.push(deptQ);
        if (teamIdQ) parts.push(teamIdQ);
        let url = "/pages/stats/notice-pub-list";
        if (parts.length) url += "?" + parts.join("&");
        uni.navigateTo({ url });
        return;
      }
      if (pcType === 5) {
        const parts = [];
        if (deptQ) parts.push(deptQ);
        if (teamIdQ) parts.push(teamIdQ);
        let url = "/pages/stats/notice-pub-list";
        if (parts.length) url += "?" + parts.join("&");
        uni.navigateTo({ url });
        return;
      }
      if (pcType === 6) {
        const parts = [];
        if (deptQ) parts.push(deptQ);
        if (teamIdQ) parts.push(teamIdQ);
        let url = "/pages/stats/pdxz-vote-list";
        if (parts.length) url += "?" + parts.join("&");
        uni.navigateTo({ url });
        return;
      }
      if (pcType === 7) {
        const parts = [];
        if (deptQ) parts.push(deptQ);
        if (teamIdQ) parts.push(teamIdQ);
        let url = "/pages/stats/proc-result-list";
        if (parts.length) url += "?" + parts.join("&");
        uni.navigateTo({ url });
        return;
      }
      if (pcType === 8) {
        const parts = [];
        if (deptQ) parts.push(deptQ);
        if (meetingQ) parts.push(meetingQ);
        let url = "/pages/stats/contract-vendor-list";
        if (parts.length) url += "?" + parts.join("&");
        uni.navigateTo({ url });
        return;
      }
      if (pcType === 9) {
        const parts = [];
        if (deptQ) parts.push(deptQ);
        if (meetingQ) parts.push(meetingQ);
        let url = "/pages/stats/quality-vendor-list";
        if (parts.length) url += "?" + parts.join("&");
        uni.navigateTo({ url });
        return;
      }
      if (pcType === 10) {
        const parts = [];
        if (deptQ) parts.push(deptQ);
        if (meetingQ) parts.push(meetingQ);
        let url = "/pages/stats/satisfaction-list";
        if (parts.length) url += "?" + parts.join("&");
        uni.navigateTo({ url });
        return;
      }
      if (pcType === 11) {
        this.goProcInfoDetail();
        return;
      }
      if (pcType === 12) {
        const parts = [];
        if (deptQ) parts.push(deptQ);
        if (meetingQ) parts.push(meetingQ);
        let url = "/pages/stats/proc-work-summary-list";
        if (parts.length) url += "?" + parts.join("&");
        uni.navigateTo({ url });
        return;
      }
    },
    dotClass(index) {
      if (this.selectedIndex === index) {
        return "step-dot--current";
      }
      const s = this.rawStateForMobileStep(index);
      if (s === 1) {
        return "step-dot--ok";
      }
      if (s === 0) {
        return "step-dot--warn";
      }
      return "step-dot--placeholder";
    },
  },
};
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  padding: 24rpx 30rpx 40rpx;
  box-sizing: border-box;
  background-color: #f0f2f5;
}

.process-banner {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 24rpx 28rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
}

.process-banner-name {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
  line-height: 1.45;
  display: block;
  margin-bottom: 12rpx;
}

.process-banner-meta {
  font-size: 24rpx;
  color: #888;
  line-height: 1.4;
}

.card {
  background-color: #ffffff;
  border-radius: 24rpx;
  padding: 28rpx 0 8rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.06);
}

.card-title {
  padding: 0 32rpx 20rpx;
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
}

.step-list {
  padding-bottom: 12rpx;
}

.step-row {
  position: relative;
  display: flex;
  flex-direction: row;
  align-items: center;
  padding: 26rpx 28rpx 26rpx 24rpx;
  margin: 0 16rpx 8rpx;
  border-radius: 16rpx;
  overflow: hidden;
}

.step-row--active {
  background-color: rgba(48, 163, 255, 0.1);
}

.step-accent {
  position: absolute;
  left: 0;
  top: 12rpx;
  bottom: 12rpx;
  width: 8rpx;
  border-radius: 0 6rpx 6rpx 0;
  background-color: #30a3ff;
}

.step-index {
  width: 44rpx;
  font-size: 28rpx;
  color: #555555;
  font-weight: 500;
  text-align: center;
  flex-shrink: 0;
}

.step-index--active {
  color: #30a3ff;
}

.step-dot {
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  margin: 0 20rpx 0 12rpx;
  flex-shrink: 0;
}

.step-dot--ok {
  background-color: #34c759;
}

.step-dot--warn {
  background-color: #ff3b30;
}

.step-dot--current {
  background-color: #30a3ff;
}

/** 与 PC 一致：以 0/1 显示红绿，其余占位保持排布 */
.step-dot--placeholder {
  background-color: rgba(0, 0, 0, 0.06);
}

.step-label {
  flex: 1;
  font-size: 28rpx;
  color: #333333;
  line-height: 1.45;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.step-label--active {
  color: #30a3ff;
  font-weight: 500;
}
</style>
