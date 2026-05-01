<template>
  <el-row :gutter="15" class="panel-group">
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('newOrders')">
        <div class="card-panel-icon-wrapper icon-people">
          <svg-icon icon-class="peoples" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            学生数
          </div>
          <count-to :start-val="0" :end-val="5286" :duration="2600" class="card-panel-num" />
        </div>
      </div>
    </el-col>
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('inventory')">
        <div class="card-panel-icon-wrapper icon-message">
          <svg-icon icon-class="documentation" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            订购金额
          </div>
          <count-to :start-val="0" :end-val="8562" :duration="3000" class="card-panel-num" />
        </div>
      </div>
    </el-col>
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('shipments')">
        <div class="card-panel-icon-wrapper icon-money">
          <svg-icon icon-class="money" class-name="card-panel-icon" />

        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            本月订购
          </div>
          <count-to :start-val="0" :end-val="786" :duration="3200" class="card-panel-num" />
        </div>
      </div>
    </el-col>
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('returns')">
        <div class="card-panel-icon-wrapper icon-shopping">
          <svg-icon icon-class="shopping" class-name="card-panel-icon" />

        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            本月订单
          </div>
          <count-to :start-val="0" :end-val="328" :duration="3600" class="card-panel-num" />
        </div>
      </div>
    </el-col>
  </el-row>
</template>

<script>
import CountTo from 'vue-count-to'

export default {
  components: {
    CountTo
  },
  methods: {
    handleSetLineChartData(type) {
      this.$emit('handleSetLineChartData', type)
    }
  }
}
</script>

<style lang="scss" scoped>
.panel-group {
  margin-top: 8px;

  .card-panel-col {
    margin-bottom: 18px;
  }

  .card-panel {
    height: 108px;
    cursor: pointer;
    font-size: 12px;
    position: relative;
    overflow: hidden;
    color: #666;
    background: #fff;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
    border: 1px solid rgba(230, 235, 245, 0.9);
    backdrop-filter: blur(10px);
    border-radius: 16px;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-5px) scale(1.01);
      box-shadow:
        0 15px 35px rgba(0, 0, 0, 0.1),
        0 0 0 1px rgba(255, 255, 255, 0.3),
        0 0 30px rgba(99, 102, 241, 0.2);
      border-color: rgba(99, 102, 241, 0.4);
    }

    .card-panel-icon-wrapper {
      float: left;
      margin: 14px 0 0 14px;
      padding: 16px;
      transition: all 0.38s ease-out;
      border-radius: 12px;
      background: linear-gradient(135deg, #6366f1 0%, #3b82f6 50%, #10b981 100%);
      box-shadow:
        0 5px 15px rgba(0, 0, 0, 0.1),
        0 0 0 1px rgba(255, 255, 255, 0.2);

      .card-panel-icon {
        float: left;
        font-size: 24px;
        color: white;
        filter: drop-shadow(0 2px 3px rgba(0, 0, 0, 0.2));
      }
    }

    .card-panel-description {
      // float: right;
      text-align: center;
      font-weight: bold;
      margin: 26px;
      margin-left: 0px;

      .card-panel-text {
        line-height: 18px;
        color: rgba(0, 0, 0, 0.45);
        font-size: 16px;
        margin-bottom: 12px;
      }

      .card-panel-num {
        font-size: 20px;
        color: #1e293b;
        font-weight: 600;
      }
    }
  }
}

@media (max-width:550px) {
  .card-panel {
    flex-direction: column;
    justify-content: center;
    height: auto !important;
    padding: 20px 0;

    .card-panel-icon-wrapper {
      margin: 0 auto 10px !important;
    }

    .card-panel-description {
      text-align: center !important;
      margin: 10px 0 0 !important;
    }
  }
}
</style>
