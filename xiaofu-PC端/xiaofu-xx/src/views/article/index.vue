<template>
  <div class="app-container">
    <!-- -->
    <el-tabs
      v-model="activeName"
      type="card"
      @tab-click="handleClick"
      class="article-tabs"
    >
      <el-tab-pane label="教育行政部门发布的文章" name="first">
        <div class="filter-container">
          <el-form class="search-container" :inline="true" :model="queryParams">
            <el-form-item label="标题：">
              <el-input
                v-model="queryParams.title"
                placeholder="请输入标题"
                clearable
                class="filter-item"
                @keyup.enter.native="handleQuery"
              ></el-input>
            </el-form-item>
            <el-form-item label="发布时间：">
              <el-date-picker
                v-model="queryParams.publishTime"
                type="datetime"
                placeholder="请选择发布时间"
                class="filter-item"
                value-format="yyyy-MM-dd HH:mm:ss"
              ></el-date-picker>
            </el-form-item>
            <el-form-item label="发布单位：">
              <el-select
                v-model="queryParams.publishUnit"
                placeholder="全部"
                clearable
                class="filter-item"
              >
                <el-option label="全部" value=""></el-option>
                <el-option label="教育局" value="1"></el-option>
                <el-option label="学校" value="2"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="文章类型：">
              <el-select
                v-model="queryParams.articleType"
                placeholder="全部"
                clearable
                class="filter-item"
              >
                <el-option label="全部" value=""></el-option>
                <el-option label="通知" value="1"></el-option>
                <el-option label="新闻" value="2"></el-option>
                <el-option label="公告" value="3"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                icon="el-icon-search"
                size="small"
                @click="handleQuery"
                >查询</el-button
              >
              <el-button icon="el-icon-refresh" size="small" @click="resetQuery"
                >重置</el-button
              >
            </el-form-item>
          </el-form>
        </div>

        <el-table
          v-loading="loading"
          :data="articleList"
          border
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column
            type="selection"
            width="55"
            align="center"
          ></el-table-column>
          <el-table-column
            label="文章标题"
            prop="title"
            :show-overflow-tooltip="true"
          ></el-table-column>
          <el-table-column
            label="文章类型"
            prop="articleType"
            width="100"
            align="center"
          ></el-table-column>
          <el-table-column
            label="内容摘要"
            prop="summary"
            :show-overflow-tooltip="true"
          ></el-table-column>
          <el-table-column
            label="发布单位"
            prop="publishUnit"
            width="120"
            align="center"
          ></el-table-column>
          <el-table-column
            label="发布时间"
            prop="publishTime"
            width="160"
            align="center"
          ></el-table-column>
          <el-table-column label="操作" align="center" width="150">
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="text"
                icon="el-icon-view"
                @click="handleView(scope.row)"
                >查看</el-button
              >
              <el-button
                size="mini"
                type="text"
                icon="el-icon-edit"
                @click="handleUpdate(scope.row)"
                >编辑</el-button
              >
              <el-button
                size="mini"
                type="text"
                icon="el-icon-delete"
                @click="handleDelete(scope.row)"
                >删除</el-button
              >
            </template>
          </el-table-column>
        </el-table>

        <pagination
          v-show="total > 0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getList"
        ></pagination>
      </el-tab-pane>
      <el-tab-pane label="我发布的文章" name="second">
        <div>
          <el-form :inline="true" :model="myQueryParams">
            <el-form-item label="标题：">
              <el-input
                v-model="myQueryParams.title"
                placeholder="请输入标题"
                clearable
                class="filter-item"
                @keyup.enter.native="handleMyQuery"
              ></el-input>
            </el-form-item>
            <el-form-item label="发布时间：">
              <el-date-picker
                v-model="myQueryParams.publishTime"
                type="datetime"
                placeholder="请选择发布时间"
                class="filter-item"
                value-format="yyyy-MM-dd HH:mm:ss"
              ></el-date-picker>
            </el-form-item>
            <el-form-item label="文章类型：">
              <el-select
                v-model="myQueryParams.articleType"
                placeholder="全部"
                clearable
                class="filter-item"
              >
                <el-option label="全部" value=""></el-option>
                <el-option label="通知" value="1"></el-option>
                <el-option label="新闻" value="2"></el-option>
                <el-option label="公告" value="3"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                icon="el-icon-search"
                @click="handleMyQuery"
                >查询</el-button
              >
              <el-button icon="el-icon-refresh" @click="resetMyQuery"
                >重置</el-button
              >
            </el-form-item>
          </el-form>
        </div>
        <el-button
          type="primary"
          size="small"
          icon="el-icon-plus"
          @click="handleAdd"
          >新建文章</el-button
        >
        <el-table
          v-loading="loading"
          :data="myArticleList"
          border
          style="width: 100%; margin-top: 10px"
        >
          <el-table-column
            label="文章标题"
            prop="title"
            :show-overflow-tooltip="true"
          ></el-table-column>
          <el-table-column
            label="文章类型"
            prop="articleType"
            width="100"
            align="center"
          ></el-table-column>
          <el-table-column
            label="内容摘要"
            prop="summary"
            :show-overflow-tooltip="true"
          ></el-table-column>
          <el-table-column
            label="状态"
            prop="status"
            width="100"
            align="center"
          >
            <template slot-scope="scope">
              <el-tag :type="scope.row.status === '1' ? 'success' : 'info'">
                {{ scope.row.status === "1" ? "已发布" : "草稿" }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column
            label="发布时间"
            prop="publishTime"
            width="160"
            align="center"
          ></el-table-column>
          <el-table-column label="操作" align="center" width="150">
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="text"
                icon="el-icon-view"
                @click="handleView(scope.row)"
                >查看</el-button
              >
              <el-button
                size="mini"
                type="text"
                icon="el-icon-edit"
                @click="handleUpdate(scope.row)"
                >编辑</el-button
              >
              <el-button
                size="mini"
                type="text"
                icon="el-icon-delete"
                @click="handleDelete(scope.row)"
                >删除</el-button
              >
            </template>
          </el-table-column>
        </el-table>

        <pagination
          v-show="myTotal > 0"
          :total="myTotal"
          :page.sync="myQueryParams.pageNum"
          :limit.sync="myQueryParams.pageSize"
          @pagination="getMyList"
        ></pagination>
      </el-tab-pane>
    </el-tabs>

    <!-- 添加文章弹框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="1100px"
      append-to-body
    >
      <el-form ref="form" :model="form" :rules="rules" label-width="160px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题"></el-input>
        </el-form-item>
        <el-form-item label="文章类型" prop="articleType">
          <el-select v-model="form.articleType" placeholder="请选择文章类型">
            <el-option label="通知" value="1"></el-option>
            <el-option label="新闻" value="2"></el-option>
            <el-option label="公告" value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="封面图片">
          <el-upload
            class="avatar-uploader"
            action="#"
            :show-file-list="false"
            :before-upload="beforeAvatarUpload"
          >
            <img v-if="form.coverUrl" :src="form.coverUrl" class="avatar" />
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            <div class="el-upload__tip" slot="tip">（非必填）</div>
          </el-upload>
        </el-form-item>
        <el-form-item label="上级行政单位查看权限" prop="adminAuth">
          <el-checkbox-group v-model="form.adminAuth">
            <el-checkbox label="1">省教育厅可查</el-checkbox>
            <el-checkbox label="2">市教育局可查</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-form-item>
          <el-alert
            title="说明：对应行政单位如暂未在本平台开通账户，则不进行显示并无法选择。"
            type="warning"
            show-icon
            :closable="false"
          >
          </el-alert>
        </el-form-item>

        <el-form-item label="内容" prop="content">
          <editor v-model="form.content" :min-height="192"></editor>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">发布</el-button>
        <el-button @click="saveAsDraft">保存</el-button>
        <el-button @click="cancel">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "RuoyiXxIndex",
  data() {
    return {
      isupEditor: true,
      formData: {
        articleText: "",
        isupEditor: true,
      },
      // tab页签
      activeName: "first",
      // 遮罩层
      loading: false,
      // 选中数组
      ids: [],
      // 总条数
      total: 0,
      myTotal: 0,
      // 文章表格数据
      articleList: [],
      myArticleList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: undefined,
        publishTime: undefined,
        publishUnit: undefined,
        articleType: undefined,
      },
      // 我的文章查询参数
      myQueryParams: {
        pageNum: 1,
        pageSize: 10,
        title: undefined,
        publishTime: undefined,
        articleType: undefined,
      },
      // 弹框显示
      dialogVisible: false,
      dialogTitle: "发布文章",
      // 表单参数
      form: {
        title: undefined,
        articleType: undefined,
        coverUrl: undefined,
        adminAuth: [],
        content: "",
      },
      // 表单校验
      rules: {
        title: [{ required: true, message: "请输入标题", trigger: "blur" }],
        articleType: [
          { required: true, message: "请选择文章类型", trigger: "change" },
        ],
        content: [{ required: true, message: "请输入内容", trigger: "blur" }],
      },
    };
  },

  created() {
    this.getList();
  },

  methods: {
    updateContent(val, strLength, flag) {
      this.strLength = strLength;
      this.formData.articleText = val;
    },
    /** 查询文章列表 */
    getList() {
      this.loading = true;
      // 这里添加获取文章列表的接口调用
      setTimeout(() => {
        this.loading = false;
        this.articleList = [];
        this.total = 0;
      }, 300);
    },

    /** 查询我的文章列表 */
    getMyList() {
      this.loading = true;
      // 这里添加获取我的文章列表的接口调用
      setTimeout(() => {
        this.loading = false;
        this.myArticleList = [];
        this.myTotal = 0;
      }, 300);
    },

    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },

    /** 重置按钮操作 */
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        title: undefined,
        publishTime: undefined,
        publishUnit: undefined,
        articleType: undefined,
      };
      this.handleQuery();
    },

    /** 搜索按钮操作 - 我的文章 */
    handleMyQuery() {
      this.myQueryParams.pageNum = 1;
      this.getMyList();
    },

    /** 重置按钮操作 - 我的文章 */
    resetMyQuery() {
      this.myQueryParams = {
        pageNum: 1,
        pageSize: 10,
        title: undefined,
        publishTime: undefined,
        articleType: undefined,
      };
      this.handleMyQuery();
    },

    /** 新增按钮操作 */
    handleAdd() {
      this.dialogTitle = "发布文章";
      this.dialogVisible = true;
      this.reset();
    },

    /** 修改按钮操作 */
    handleUpdate(row) {
      this.$router.push({ path: "/article/edit", query: { id: row.id } });
    },

    /** 查看按钮操作 */
    handleView(row) {
      this.$router.push({ path: "/article/detail", query: { id: row.id } });
    },

    /** 删除按钮操作 */
    handleDelete(row) {
      this.$modal
        .confirm("是否确认删除该文章？")
        .then(() => {
          // 这里添加删除文章的接口调用
        })
        .catch(() => {});
    },

    /** 多选框选中数据 */
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.id);
    },

    /** tab切换 */
    handleClick(tab) {
      if (tab.name === "first") {
        this.getList();
      } else {
        this.getMyList();
      }
    },

    /** 表单重置 */
    reset() {
      this.form = {
        title: undefined,
        articleType: undefined,
        coverUrl: undefined,
        adminAuth: [],
        content: "",
      };
      if (this.$refs.form) {
        this.$refs.form.resetFields();
      }
    },

    /** 上传前校验 */
    beforeAvatarUpload(file) {
      const isJPG = file.type === "image/jpeg" || file.type === "image/png";
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isJPG) {
        this.$message.error("上传封面图片只能是 JPG/PNG 格式!");
      }
      if (!isLt2M) {
        this.$message.error("上传封面图片大小不能超过 2MB!");
      }
      return isJPG && isLt2M;
    },

    /** 发布按钮 */
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          // 这里添加发布文章的接口调用
          this.dialogVisible = false;
          this.$message.success("发布成功");
          this.getMyList();
        }
      });
    },

    /** 保存草稿 */
    saveAsDraft() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          // 这里添加保存草稿的接口调用
          this.dialogVisible = false;
          this.$message.success("保存成功");
          this.getMyList();
        }
      });
    },

    /** 取消按钮 */
    cancel() {
      this.dialogVisible = false;
      this.reset();
    },
  },
};
</script>

<style scoped>
.article-tabs {
  margin-top: 10px;
}

.filter-container {
  /* padding-bottom: 10px; */
  /* margin-bottom: 20px; */
  /* border-bottom: 1px solid #eee; */
}

.filter-item {
  margin-right: 10px;
  min-width: 200px;
}

.search-form {
  /* margin-top: 20px; */
}

:deep(.el-table) {
  margin-top: 15px;
}

.el-tag {
  margin-right: 5px;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 178px;
  height: 178px;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
}

.dialog-footer {
  text-align: center;
}
</style>
