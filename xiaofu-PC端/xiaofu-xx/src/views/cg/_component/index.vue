<template>
  <div class="detail-container">
    <div class="detail-sidebar">
      <h3 class="detail-sidebar-title">流程名称</h3>
      <div class="detail-menu">
        <div
          v-for="(item, index) in detailMenu"
          :key="index"
          :class="[
            'menu-item',
            activeMenu === item.type ? 'active' : '', 
          ]"

          @click="handleMenuClick(item, index)"
        >
          <span class="xuhao">{{ index + 1 }}</span>
          <div
            class="process-state"
            style="margin-left: 5px"
            v-if="processStates[index] == 1"
          >
            <div
              style="
                background-color: #51ec7b;
                width: 12px;
                height: 12px;
                border-radius: 50%;
              "
            ></div>
          </div>
          <div
            class="process-state"
            style="margin-left: 5px"
            v-else-if="processStates[index] == 0"
          >
            <div
              style="
                background-color: #f56c6c;
                width: 12px;
                height: 12px;
                border-radius: 50%;
              "
            ></div>
          </div>
          <span
            class="menu-title"  
            >{{ item.title }}</span
          > 
        </div>
      </div>
    </div>
    <div class="detail-content">
      <!-- 1. 班子会议纪要报备与公开 -->
      <!-- <meeting-info v-if="activeMenu === 0" :formId="formId" /> -->
      <banzihuiyi
        v-if="activeMenu === 0"
        :teamId="teamId"
        @success="handleSuccess"
      />

      <!-- 2. 发布采购公告 -->
      <enterprise-mode-one
        @handleGetProcessMenu="handleGetProcessMenu"
        :formId="formId"
        v-if="activeMenu === 1"
        :active-xs-name="activeXSName"
        :table-data="tableData"
        @add-xuagou="$emit('add-xuagou')"
        @handle-tab-click="$emit('handle-tab-click')"
      /> 


      <!-- 3. 备选公告展示 -->
      <Ruweigonggao
        @handleGetProcessMenu="handleGetProcessMenu"
        :formId="formId"
        v-if="activeMenu === 2"
      />
      <!-- 4. 家长投票 -->
      <Zuzhi
        @handleGetProcessMenu="handleGetProcessMenu"
        :formId="formId"
        v-if="activeMenu === 3"
      />

      <!-- 5. 投票结果公示 -->
      <purchase-result
        @handleGetProcessMenu="handleGetProcessMenu"
        :formId="formId"
        v-if="activeMenu === 4"
        stage-type="1"
        step-title="投票结果公示"
        add-button-text="新增投票结果公示"
        publish-button-text="手动公示"
        :table-gs="tableGS"
        @add-xuangou="$emit('add-xuangou')"
      />

      <!-- 6. 签订合同 -->
      <purchase-contract
        @handleGetProcessMenu="handleGetProcessMenu"
        :formId="formId"
        v-if="activeMenu === 5"
        :table-gs="tableGS"
        @add-hezuo="$emit('add-hezuo')"
        @see-info="$emit('see-info', $event)"
        @see-hetong="$emit('see-hetong', $event)"
        @handle-edit="$emit('handle-edit', $event)"
      />

      <!-- 7. 发采购结果公告让家长购买 -->
      <purchase-result
        @handleGetProcessMenu="handleGetProcessMenu"
        :formId="formId"
        v-if="activeMenu === 6"
        stage-type="2"
        step-title="采购结果公告"
        add-button-text="发布采购结果公告"
        publish-button-text="发布采购结果公告"
        :table-gs="tableGS"
        @add-xuangou="$emit('add-xuangou')"
      />

      <!-- 8. 产品质量检验报告报备与公开 -->
      <quality-report
        @handleGetProcessMenu="handleGetProcessMenu"
        :formId="formId"
        v-if="activeMenu === 7"
        :table-gs="tableGS"
        @add-cs="$emit('add-cs')"
        @see-zj="$emit('see-zj', $event)"
        @upload-zj="$emit('upload-zj', $event)"
        @see-info="$emit('see-info', $event)"
      />

      <!-- 9. 选用组织家长满意度总结报告报备与公开 -->
      <satisfaction-report
        @handleGetProcessMenu="handleGetProcessMenu"
        :formId="formId"
        v-if="activeMenu === 8"
        :table-gs="tableGS"
        @add-zjbg="$emit('add-zjbg')"
        @see-yijian="$emit('see-yijian', $event)"
        @edit-dc="$emit('edit-dc', $event)"
      />

      <!-- 10. 采购信息报备 -->
      <Caigoubaobei
        @handleGetProcessMenu="handleGetProcessMenu"
        :formId="formId"
        v-if="activeMenu === 9"
      />
      <!-- 11. 学校校服采购工作总结 -->
      <procurementWork
        @handleGetProcessMenu="handleGetProcessMenu"
        :formId="formId"
        v-if="activeMenu === 10"
      ></procurementWork>
    </div>
  </div>
</template>

<script>
import procurementWork from "./procurementWork.vue";
import { getModeByTeamId, getProcessMenu , getCgType } from "@/api/cg";
import MeetingInfo from "./MeetingInfo.vue";
import ParentPurchase from "./ParentPurchase.vue";
import OrganizationList from "./OrganizationList.vue";
import PurchaseMode from "./PurchaseMode.vue"; 
import EnterpriseModeOne from "./EnterpriseModeOne.vue";
import EnterpriseModeTwo from "./EnterpriseModeTwo.vue";
import EnterpriseModeThree from "./EnterpriseModeThree.vue";
import PurchaseResult from "./PurchaseResult.vue";
import PurchaseContract from "./PurchaseContract.vue";
import QualityReport from "./QualityReport.vue";
import SatisfactionReport from "./SatisfactionReport.vue";
import Caigoubaobei from "./Caigoubaobei.vue";
import Zuzhi from "./zuzhi.vue";
import Ruweigonggao from "./ruweigonggao.vue";
import Banzihuiyi from "./banzihuiyi.vue";
export default {
  name: "DetailDialog",
  components: {
    Banzihuiyi,
    MeetingInfo,
    ParentPurchase,
    OrganizationList,
    PurchaseMode, 
    EnterpriseModeOne,
    EnterpriseModeTwo,
    EnterpriseModeThree,
    PurchaseResult,
    PurchaseContract,
    QualityReport,
    SatisfactionReport,
    Caigoubaobei,
    Zuzhi,
    procurementWork,
    Ruweigonggao,
  },
  props: {
    formId: {
      required: true,
      default: null,
    },
    teamId: {
      required: true,
    },
    visible: {
      type: Boolean,
      default: false,
    },
    organizationLeaders: {
      type: Array,
      default: () => [],
    },
    purchaseDetail: {
      type: Array,
      default: () => [],
    },
    parentPurchaseList: {
      type: Array,
      default: () => [],
    },
    zuzhiDetail: {
      type: Array,
      default: () => [],
    },
    tableDataZuzhi: {
      type: Array,
      default: () => [],
    },
    datetime: {
      type: Array,
      default: () => [],
    },
    tableData: {
      type: Array,
      default: () => [],
    },
    activeXSName: {
      type: String,
      default: "",
    },
    activeJiazhang: {
      type: String,
      default: "",
    },
    dataJiazhang: {
      type: Array,
      default: () => [],
    },
    activeJigou: {
      type: String,
      default: "",
    },
    tableDataJigou: {
      type: Array,
      default: () => [],
    },
    tableDataShenhe: {
      type: Array,
      default: () => [],
    },
    tableGS: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      processStates: [],
      modeProcess: null,
      activeMenu: 0,
      menu:[ 
        { title: "班子会议纪要报备与公开", type: 0 },
        { title: "发布采购公告", type: 1 },
        { title: "备选公告展示", type: 2 },
        { title: "家长投票", type: 3 },
        { title: "投票结果公示", type: 4 },
        { title: "签订合同", type: 5 },
        { title: "发采购结果公告让家长购买", type: 6 },
        { title: "产品质量检验报告报备与公开", type: 7 },
        { title: "选用组织家长满意度总结报告报备与公开", type: 8 },
        { title: "采购信息报备", type: 9 },
        { title: "学校校服采购工作总结", type: 10 },
      ],
      detailMenu: [
        { title: "班子会议纪要报备与公开", type: 0 },
        { title: "发布采购公告", type: 1 },
        { title: "备选公告展示", type: 2 },
        { title: "家长投票", type: 3 },
        { title: "投票结果公示", type: 4 },
        { title: "签订合同", type: 5 },
        { title: "发采购结果公告让家长购买", type: 6 },
        { title: "产品质量检验报告报备与公开", type: 7 },
        { title: "选用组织家长满意度总结报告报备与公开", type: 8 },
        { title: "采购信息报备", type: 9 },
        { title: "学校校服采购工作总结", type: 10 },
      ],
      cgType:null,//0 选用组织自主确定 2 招投标
    };
  },

  created() {
    this.getCgType();
    this.handlegetModeByTeamId();
  },
  methods: { 
    getCgType(){
      getCgType(this.formId == null ? "" : this.formId).then((res) => {
        this.cgType = res.msg;
        this.detailMenu = [...this.menu];
      })
    this.handleGetProcessMenu();
    },
    handleGetProcessMenu() { 
      getProcessMenu(this.formId == null ? "" : this.formId).then((res) => {
        this.processStates = res.data;
      });
    },
    handleMenuClick(item, index) {
      if (this.formId == null && index > 0) {
        return;
      }
      this.activeMenu = item.type;
    },
    handleSuccess(res) {
      this.formId = res;
      this.handleGetProcessMenu();
    },
    cgTypeChoose(res){
      this.cgType = res;
      this.detailMenu = [...this.menu];
      this.handleGetProcessMenu();
    },
    handlegetModeByTeamId() {
      getModeByTeamId(this.formId == null ? "" : this.formId).then((res) => {
        this.modeProcess = res.msg; 
      });
    },
  },
};
</script>

<style scoped>
.menu-title {
  width: 230px;
  display: inline-block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.xuhao {
  font-weight: bold;
  display: inline-block;
  width: 20px;
  font-size: 14px;
}

.mode-item {
  padding-left: 40px;
}

.mode-title {
  margin-left: 0.5em;
}

.is-disabled {
  color: #999 !important;
  cursor: not-allowed;
  pointer-events: none;
  background-color: #f8fbff !important;
  border-left: 3px solid #999;
}
.detail-sidebar-title {
  font-weight: 600;
  padding-left: 20px;
}
.detail-container {
  display: flex;
  height: 80vh;
  overflow: hidden;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.detail-sidebar {
  width: 300px;
  border-right: 1px solid #ebeef5;
  overflow-y: auto;
  background-color: #f8f8f9;
}

.detail-menu {
  padding: 10px 0;
}
.process-state {
  /* position: absolute; */
  /* top: 50%; */
  /* transform: translateY(-50%); */
  /* right: 10px; */
  margin-right: 10px;
}
.menu-item {
  position: relative;
  padding: 12px 6px;
  cursor: pointer;
  font-size: 14px;
  color: #515a6e;
  transition: all 0.3s;
  border-left: 3px solid transparent;
  margin-bottom: 2px;
  display: flex;
  align-items: center;
}

.menu-item:hover {
  background-color: #eef1f6;
  color: #409eff;
}

.menu-item.active {
  background-color: #eef5fe;
  color: #409eff;
  font-weight: 600;
  border-left: 3px solid #409eff;
}

.detail-content {
  flex: 1;
  padding: 0 20px;
  overflow-y: auto;
  background-color: #fff;
}
</style>
