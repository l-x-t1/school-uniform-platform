<template>
  <div>
    <div class="app-container">
      <!-- 查询表单 -->
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="公告名称：">
          <el-select v-model="queryParams.noticeId" placeholder="请选择" style="width: 240px;">
            <el-option v-for="item in noticeOptions" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>

      <!-- 无数据时的展示 -->
      <div v-if="!loading && isEmpty" class="empty-block">
        <div class="empty-icon">
          <el-icon class="icon-forbidden"></el-icon>
        </div>
        <div class="empty-text">暂无数据</div>
        <div class="empty-desc">抱歉，没有找到您当前的数据</div>
      </div>

      <!-- 有数据时的表格 -->
      <el-table v-if="!isEmpty" v-loading="loading" :data="tableData" style="width: 100%; margin-top: 15px;">
        <!-- 表格列将根据查询结果动态生成 -->
      </el-table>

      <!-- 分页组件 -->
      <pagination v-if="!isEmpty && total > 0" :total="total" :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize" @pagination="getList" />
    </div>
  </div>

</template>

<script>
export default {
  name: 'DgfkIndex',
  data() {
    return {
      // 遮罩层
      loading: false,
      // 是否为空数据
      isEmpty: true,
      // 总条数
      total: 0,
      // 表格数据
      tableData: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        noticeId: ''
      },
      // 公告选项
      noticeOptions: [
        { value: '1', label: '2023年春季校服统购' },
        { value: '2', label: '2023年秋季校服统购' },
        { value: '3', label: '2023年冬季校服统购' }
      ]
    };
  },
  watch: {
    'queryParams.noticeId': {
      handler(val) {
        if (val) {
          this.getList();
        } else {
          this.isEmpty = true;
          this.tableData = [];
        }
      },
      immediate: true
    }
  },
  methods: {
    /** 查询列表 */
    getList() {
      if (!this.queryParams.noticeId) {
        this.isEmpty = true;
        return;
      }
      this.loading = true;
      // 模拟接口调用
      setTimeout(() => {
        this.tableData = [];
        this.total = 0;
        this.isEmpty = true; // 保持为空状态以显示无数据界面
        this.loading = false;
      }, 300);
    }
  }
};
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.empty-block {
  width: 100%;
  margin: 100px 0;
  text-align: center;
}

.empty-icon {
  height: 80px;
  line-height: 80px;
  margin-bottom: 15px;
}

.icon-forbidden {
  font-size: 50px;
  color: #dcdfe6;
  display: inline-block;
  width: 80px;
  height: 80px;
  border-radius: 50%;
  border: 2px solid #dcdfe6;
  position: relative;
}

.icon-forbidden::before {
  content: "";
  position: absolute;
  width: 100%;
  height: 100%;
  left: 0;
  top: 0;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512"><path fill="%23dcdfe6" d="M256 8C119.034 8 8 119.033 8 256s111.034 248 248 248 248-111.034 248-248S392.967 8 256 8zm130.108 117.892c65.448 65.448 70 165.481 20.677 235.637L150.47 105.216c70.204-49.356 170.226-44.735 235.638 20.676zM125.892 386.108c-65.448-65.448-70-165.481-20.677-235.637L361.53 406.784c-70.203 49.356-170.226 44.736-235.638-20.676z"/></svg>');
  background-size: 55px 55px;
  background-position: center;
  background-repeat: no-repeat;
  opacity: 0.8;
}

.empty-text {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
}

.empty-desc {
  font-size: 14px;
  color: #909399;
}
</style>
