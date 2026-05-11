<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="商品名称" prop="goodsName">
        <el-input
          v-model="queryParams.goodsName"
          placeholder="请输入商品名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品编码" prop="goodsCode">
        <el-input
          v-model="queryParams.goodsCode"
          placeholder="请输入商品编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="品类名称" prop="categoryName">
        <el-input
          v-model="queryParams.categoryName"
          placeholder="请输入品类名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="供应商" prop="supplierName">
        <el-input
          v-model="queryParams.supplierName"
          placeholder="请输入供应商名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="品牌名称" prop="brandName">
        <el-input
          v-model="queryParams.brandName"
          placeholder="请输入品牌名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="上架时间" prop="publishTime">
        <el-date-picker clearable
          v-model="queryParams.publishTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择上架时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="下架时间" prop="offShelfTime">
        <el-date-picker clearable
          v-model="queryParams.offShelfTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择下架时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="排序值" prop="sortNum">
        <el-input
          v-model="queryParams.sortNum"
          placeholder="请输入排序值"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['school:goods:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['school:goods:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['school:goods:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['school:goods:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="goodsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="商品名称" align="center" prop="goodsName" />
      <el-table-column label="商品编码" align="center" prop="goodsCode" />
      <el-table-column label="品类名称" align="center" prop="categoryName" />
      <el-table-column label="供应商" align="center" prop="supplierName" />
      <el-table-column label="品牌名称" align="center" prop="brandName" />
      <el-table-column label="主图路径或URL" align="center" prop="mainImage" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.mainImage" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="销售类型，1统购 2零售 3统购零售" align="center" prop="saleType" />
      <el-table-column label="状态，0草稿 1启用 2停用" align="center" prop="status" />
      <el-table-column label="上架状态，0未上架 1已上架 2已下架" align="center" prop="publishStatus" />
      <el-table-column label="上架时间" align="center" prop="publishTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.publishTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="下架时间" align="center" prop="offShelfTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.offShelfTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="排序值" align="center" prop="sortNum" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['school:goods:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['school:goods:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改商品信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="商品名称" prop="goodsName">
          <el-input v-model="form.goodsName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品编码" prop="goodsCode">
          <el-input v-model="form.goodsCode" placeholder="请输入商品编码" />
        </el-form-item>
        <el-form-item label="品类名称" prop="categoryName">
          <el-input v-model="form.categoryName" placeholder="请输入品类名称" />
        </el-form-item>
        <el-form-item label="供应商" prop="supplierId">
          <el-select v-model="form.supplierId" filterable placeholder="请选择供应商" @change="handleSupplierChange">
            <el-option
              v-for="item in supplierOptions"
              :key="item.id"
              :label="item.supplierName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="品牌名称" prop="brandName">
          <el-input v-model="form.brandName" placeholder="请输入品牌名称" />
        </el-form-item>
        <el-form-item label="主图路径或URL" prop="mainImage">
          <image-upload v-model="form.mainImage"/>
        </el-form-item>
        <el-form-item label="详情图片" prop="detailImagesJson">
          <el-input v-model="form.detailImagesJson" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="商品详情">
          <editor v-model="form.detailContent" :min-height="192"/>
        </el-form-item>
        <el-form-item label="销售类型" prop="saleType">
          <el-select v-model="form.saleType" placeholder="请选择销售类型">
            <el-option label="统购" value="1" />
            <el-option label="零售" value="2" />
            <el-option label="统购零售" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择商品状态">
            <el-option label="草稿" value="0" />
            <el-option label="启用" value="1" />
            <el-option label="停用" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="上架状态" prop="publishStatus">
          <el-select v-model="form.publishStatus" placeholder="请选择上架状态">
            <el-option label="未上架" value="0" />
            <el-option label="已上架" value="1" />
            <el-option label="已下架" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序值" prop="sortNum">
          <el-input v-model="form.sortNum" placeholder="请输入排序值" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-divider content-position="center">商品尺码信息</el-divider>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAddXfptGoodsSize">添加</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" icon="el-icon-delete" size="mini" @click="handleDeleteXfptGoodsSize">删除</el-button>
          </el-col>
        </el-row>
        <el-table :data="xfptGoodsSizeList" :row-class-name="rowXfptGoodsSizeIndex" @selection-change="handleXfptGoodsSizeSelectionChange" ref="xfptGoodsSize">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="序号" align="center" prop="index" width="50"/>
          <el-table-column label="尺码编码" prop="sizeCode" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.sizeCode" placeholder="请输入尺码编码" />
            </template>
          </el-table-column>
          <el-table-column label="排序字段，根据商品尺码依次增加，如 S=1 M=2 L=3 XL=4" prop="sortNum" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.sortNum" placeholder="请输入排序字段，根据商品尺码依次增加，如 S=1 M=2 L=3 XL=4" />
            </template>
          </el-table-column>
          <el-table-column label="性别类型，0通用 1男 2女" prop="genderType" width="150">
            <template slot-scope="scope">
              <el-select v-model="scope.row.genderType" placeholder="请选择性别类型，0通用 1男 2女">
                <el-option label="通用" value="0" />
                <el-option label="男" value="1" />
                <el-option label="女" value="2" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="零售价" prop="retailPrice" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.retailPrice" placeholder="请输入零售价" />
            </template>
          </el-table-column>
          <el-table-column label="统购价" prop="groupPrice" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.groupPrice" placeholder="请输入统购价" />
            </template>
          </el-table-column>
          <el-table-column label="库存" prop="stockNum" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.stockNum" placeholder="请输入库存" />
            </template>
          </el-table-column>
          <el-table-column label="状态，0停用 1启用" prop="status" width="150">
            <template slot-scope="scope">
              <el-select v-model="scope.row.status" placeholder="请选择状态，0停用 1启用">
                <el-option label="停用" value="0" />
                <el-option label="启用" value="1" />
              </el-select>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listGoods, getGoods, delGoods, addGoods, updateGoods } from "@/api/school/goods";
import { listSupplierMessage } from "@/api/school/manufacturers";

export default {
  name: "Goods",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 子表选中数据
      checkedXfptGoodsSize: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 商品信息表格数据
      goodsList: [],
      // 商品尺码表格数据
      xfptGoodsSizeList: [],
      // 供应商下拉数据
      supplierOptions: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        goodsName: null,
        goodsCode: null,
        categoryName: null,
        supplierId: null,
        supplierName: null,
        brandName: null,
        mainImage: null,
        saleType: null,
        status: null,
        publishStatus: null,
        publishTime: null,
        offShelfTime: null,
        sortNum: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        goodsName: [
          { required: true, message: "商品名称不能为空", trigger: "blur" }
        ],
        goodsCode: [
          { required: true, message: "商品编码不能为空", trigger: "blur" }
        ],
        categoryName: [
          { required: true, message: "品类名称不能为空", trigger: "blur" }
        ],
        supplierId: [
          { required: true, message: "供应商不能为空", trigger: "change" }
        ],
        saleType: [
          { required: true, message: "销售类型，1统购 2零售 3统购零售不能为空", trigger: "change" }
        ],
        status: [
          { required: true, message: "状态，0草稿 1启用 2停用不能为空", trigger: "change" }
        ],
        publishStatus: [
          { required: true, message: "上架状态，0未上架 1已上架 2已下架不能为空", trigger: "change" }
        ],
      }
    };
  },
  created() {
    this.getList();
    this.getSupplierOptions();
  },
  methods: {
    /** 查询商品信息列表 */
    getList() {
      this.loading = true;
      listGoods(this.queryParams).then(response => {
        this.goodsList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 查询供应商下拉数据 */
    getSupplierOptions() {
      listSupplierMessage({ pageNum: 1, pageSize: 1000 }).then(response => {
        this.supplierOptions = response.rows || [];
      });
    },
    /** 选择供应商后同步保存供应商名称快照 */
    handleSupplierChange(value) {
      const supplier = this.supplierOptions.find(item => String(item.id) === String(value));
      this.form.supplierName = supplier ? supplier.supplierName : null;
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        goodsName: null,
        goodsCode: null,
        categoryName: null,
        supplierId: null,
        supplierName: null,
        brandName: null,
        mainImage: null,
        detailImagesJson: null,
        detailContent: null,
        saleType: "3",
        status: "1",
        publishStatus: "0",
        publishTime: null,
        offShelfTime: null,
        sortNum: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null,
        delFlag: "0"
      };
      this.xfptGoodsSizeList = [];
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加商品信息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getGoods(id).then(response => {
        this.form = response.data;
        this.xfptGoodsSizeList = response.data.xfptGoodsSizeList;
        this.open = true;
        this.title = "修改商品信息";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.handleSupplierChange(this.form.supplierId);
          this.form.xfptGoodsSizeList = this.xfptGoodsSizeList;
          if (this.form.id != null) {
            updateGoods(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addGoods(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除商品信息编号为"' + ids + '"的数据项？').then(function() {
        return delGoods(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
	/** 商品尺码序号 */
    rowXfptGoodsSizeIndex({ row, rowIndex }) {
      row.index = rowIndex + 1;
    },
    /** 商品尺码添加按钮操作 */
    handleAddXfptGoodsSize() {
      let obj = {};
      obj.sizeCode = "";
      obj.sortNum = "";
      obj.genderType = "";
      obj.retailPrice = "";
      obj.groupPrice = "";
      obj.stockNum = "";
      obj.status = "";
      obj.remark = "";
      this.xfptGoodsSizeList.push(obj);
    },
    /** 商品尺码删除按钮操作 */
    handleDeleteXfptGoodsSize() {
      if (this.checkedXfptGoodsSize.length == 0) {
        this.$modal.msgError("请先选择要删除的商品尺码数据");
      } else {
        const xfptGoodsSizeList = this.xfptGoodsSizeList;
        const checkedXfptGoodsSize = this.checkedXfptGoodsSize;
        this.xfptGoodsSizeList = xfptGoodsSizeList.filter(function(item) {
          return checkedXfptGoodsSize.indexOf(item.index) == -1
        });
      }
    },
    /** 复选框选中数据 */
    handleXfptGoodsSizeSelectionChange(selection) {
      this.checkedXfptGoodsSize = selection.map(item => item.index)
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('school/goods/export', {
        ...this.queryParams
      }, `goods_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
