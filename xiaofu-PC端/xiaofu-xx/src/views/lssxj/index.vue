<template>
  <div class="app-container">
    <!-- 查询表单 -->
    <el-form :inline="true" :model="queryParams">
      <el-form-item label="厂商名称：">
        <el-select v-model="queryParams.manufacturerId" placeholder="请选择" style="width: 220px;">
          <el-option label="全部" value=""></el-option>
          <el-option v-for="item in manufacturerOptions" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="商品状态：">
        <el-select v-model="queryParams.productStatus" placeholder="请选择" style="width: 220px;">
          <el-option label="全部" value=""></el-option>
          <el-option label="上架中" value="1"></el-option>
          <el-option label="已下架" value="0"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="商品品类：">
        <el-select v-model="queryParams.productCategory" placeholder="请选择" style="width: 220px;">
          <el-option label="全部" value=""></el-option>
          <el-option label="校服" value="1"></el-option>
          <el-option label="运动服" value="2"></el-option>
          <el-option label="书包" value="3"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="商品款式：">
        <el-select v-model="queryParams.productStyle" placeholder="请选择" style="width: 220px;">
          <el-option label="全部" value=""></el-option>
          <el-option label="春秋款" value="1"></el-option>
          <el-option label="夏季款" value="2"></el-option>
          <el-option label="冬季款" value="3"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="商品名称：">
        <el-input v-model="queryParams.productName" placeholder="请输入商品名称" style="width: 220px;"></el-input>
      </el-form-item>
      <el-form-item label="商品款号：">
        <el-input v-model="queryParams.productCode" placeholder="请输入商品款号" style="width: 220px;"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
      </el-form-item>
    </el-form>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="tableData" style="width: 100%">
      <el-table-column label="商品信息" prop="productInfo" min-width="300">
        <template slot-scope="scope">
          <div class="product-info">
            <div class="product-img" v-if="scope.row.imageUrl">
              <img :src="scope.row.imageUrl" alt="商品图片">
            </div>
            <div class="product-details">
              <div class="product-name">{{ scope.row.productName }}</div>
              <div class="product-code">款号: {{ scope.row.productCode }}</div>
              <div class="product-category">品类: {{ scope.row.categoryName }}</div>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="厂商名称" prop="manufacturerName" min-width="150"></el-table-column>
      <el-table-column label="价格/时间" prop="price" min-width="150">
        <template slot-scope="scope">
          <div>￥{{ scope.row.price }}</div>
          <div class="publish-time">{{ scope.row.publishTime }}</div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" align="center">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleView(scope.row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 无数据提示 -->
    <div v-if="tableData.length === 0 && !loading" class="empty-data">
      <span>无数据记录</span>
    </div>

    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
  </div>
</template>

<script>
export default {
  name: 'LssxjIndex',
  data() {
    return {
      // 遮罩层
      loading: false,
      // 总条数
      total: 0,
      // 表格数据
      tableData: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        manufacturerId: '',
        productStatus: '',
        productCategory: '',
        productStyle: '',
        productName: '',
        productCode: ''
      },
      // 厂商选项
      manufacturerOptions: [
        { value: '1', label: '北京服装厂' },
        { value: '2', label: '上海制衣厂' },
        { value: '3', label: '广州校服有限公司' }
      ]
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 模拟接口调用
      setTimeout(() => {
        this.tableData = [];
        this.total = 0;
        this.loading = false;
      }, 300);
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 查看按钮操作 */
    handleView(row) {
      this.$router.push({ path: '/lssxj/detail', query: { id: row.id } });
    }
  }
};
</script>

<style scoped>
.search-form {
  background-color: #fff;
  padding: 15px 20px;
  margin-bottom: 15px;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.product-info {
  display: flex;
  align-items: center;
}

.product-img {
  width: 60px;
  height: 60px;
  margin-right: 10px;
  border: 1px solid #ebeef5;
  overflow: hidden;
}

.product-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-details {
  flex: 1;
}

.product-name {
  font-weight: 500;
  margin-bottom: 5px;
}

.product-code,
.product-category {
  font-size: 12px;
  color: #606266;
  margin-bottom: 3px;
}

.publish-time {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.empty-data {
  text-align: center;
  color: #909399;
  padding: 30px 0;
}
</style>
