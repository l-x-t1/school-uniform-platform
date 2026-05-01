<template>
  <div class="feedback-container">
    <!-- 页面标题和提交按钮 -->
    <div class="feedback-header">
      <h2 class="feedback-title">意见反馈</h2>
      <div class="header-actions">
        <el-button type="primary" @click="showFeedbackDialog = true">
          提交意见反馈
        </el-button>
        <el-button @click="$emit('goHome')">返回首页</el-button>
      </div>
    </div>

    <!-- 意见列表 -->
    <div class="feedback-list">
      <div v-if="loading" class="loading-container">
        <el-loading-text>加载中...</el-loading-text>
      </div>
      <div v-else-if="feedbackList.length === 0" class="empty-container">
        <el-empty description="暂无意见反馈"></el-empty>
      </div>
      <div v-else>
        <div
          v-for="item in feedbackList"
          :key="item.id"
          class="feedback-item"
          @click="viewDetail(item)"
        >
          <div class="feedback-content">
            <h4 class="feedback-subject">问题：</h4>
            <p class="feedback-text">{{ item.feedbackContent }}</p>
            <div v-if="item.replyContent" class="reply-section">
              <h4 class="reply-subject">回复：</h4>
              <p class="reply-text">{{ item.replyContent }}</p>
            </div>
            <div class="feedback-meta">
              <span class="feedback-time">提交时间：{{ item.createTime }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="total > 0" class="pagination-container">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="queryParams.pageNum"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="queryParams.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      ></el-pagination>
    </div>

    <!-- 提交意见反馈弹框 -->
    <el-dialog
      title="提交意见反馈"
      :visible.sync="showFeedbackDialog"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        :model="feedbackForm"
        :rules="rules"
        ref="feedbackForm"
        label-width="100px"
        class="feedback-form"
      >
        <el-form-item label="区县" prop="region">
          <el-select v-model="feedbackForm.region" placeholder="请选择区县">
            <el-option
              v-for="item of shijiazhuangDistricts"
              :value="item.id"
              :label="item.label"
              :key="item.id"
            >
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="姓名" prop="userName">
          <el-input
            placeholder="请输入姓名"
            v-model="feedbackForm.userName"
          ></el-input>
        </el-form-item>

        <el-form-item label="联系方式" prop="lxfs">
          <el-input
            v-model="feedbackForm.lxfs"
            placeholder="请输入您的手机号或邮箱（可选）"
          ></el-input>
        </el-form-item>

        <el-form-item label="反馈内容" prop="feedbackContent">
          <el-input
            type="textarea"
            v-model="feedbackForm.feedbackContent"
            placeholder="请详细描述您的意见或建议，我们会认真处理每一条反馈..."
            :rows="6"
            show-word-limit
            maxlength="500"
          ></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeFeedbackDialog">取 消</el-button>
        <el-button type="primary" @click="submitFeedback" :loading="submitting">
          提 交
        </el-button>
      </span>
    </el-dialog>

    <!-- 意见详情弹框 -->
    <el-dialog title="意见详情" :visible.sync="showDetailDialog" width="600px">
      <div v-if="feedbackDetail" class="detail-content">
        <div class="detail-item">
          <label>姓名：</label>
          <span>{{ feedbackDetail.userName || "匿名用户" }}</span>
        </div>
        <div class="detail-item" v-if="feedbackDetail.lxfs">
          <label>联系方式：</label>
          <span>{{ feedbackDetail.lxfs }}</span>
        </div>
        <div class="detail-item">
          <label>提交时间：</label>
          <span>{{ formatTime(feedbackDetail.createTime) }}</span>
        </div>
        <div class="detail-item">
          <label>反馈内容：</label>
          <div class="detail-content-text">
            {{ feedbackDetail.feedbackContent }}
          </div>
        </div>
        <div class="detail-item" v-if="feedbackDetail.replyContent">
          <label>回复内容：</label>
          <div class="detail-content-text reply-content">
            {{ feedbackDetail.replyContent }}
          </div>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="showDetailDialog = false">关 闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  getFeedbackList,
  getFeedbackDetail,
  feedback,
} from "@/api/publicIndex";
import { shijiazhuangDistricts } from "@/utils/region";

export default {
  name: "FeedbackForm",
  data() {
    return {
      shijiazhuangDistricts: shijiazhuangDistricts,
      loading: false,
      submitting: false,
      showFeedbackDialog: false,
      showDetailDialog: false,
      feedbackList: [],
      feedbackDetail: null,
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        isPublic: 1,
      },
      feedbackForm: {
        userName: "",
        lxfs: "",
        feedbackContent: "",
      },
      rules: {
        userName: [{ required: true, message: "请输入姓名", trigger: "blur" }],
        feedbackContent: [
          { required: true, message: "请输入反馈内容", trigger: "blur" },
          { min: 10, message: "反馈内容至少10个字符", trigger: "blur" },
        ],
        lxfs: [
          { 
            validator: (rule, value, callback) => {
              if (!value) {
                callback();
                return;
              }
              // 手机号正则：11位数字，以1开头
              const mobileRegex = /^1[3-9]\d{9}$/;
              // 座机号正则：区号+号码，支持多种格式
              const phoneRegex = /^(0\d{2,3}-?\d{7,8})|(400-?\d{3}-?\d{4})|(800-?\d{3}-?\d{4})$/;
              // 邮箱正则
              const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

              if (mobileRegex.test(value) || phoneRegex.test(value) || emailRegex.test(value)) {
                callback();
              } else {
                callback(new Error('请输入正确的手机号、座机号或邮箱'));
              }
            },
            trigger: "blur"
          }
        ],
      },
    };
  },
  created() {
    this.getFeedbackList();
  },
  methods: {
    // 获取意见反馈列表
    async getFeedbackList() {
      this.loading = true;
      try {
        const res = await getFeedbackList(this.queryParams);
        this.feedbackList = res.rows || res.data || [];
        this.total = res.total || 0;
      } catch (error) {
        this.$message.error("获取意见列表失败");
        console.error(error);
      } finally {
        this.loading = false;
      }
    },

    // 查看详情
    async viewDetail(item) {
      try {
        const res = await getFeedbackDetail(item.id);
        this.feedbackDetail = res.data;
        this.showDetailDialog = true;
      } catch (error) {
        this.$message.error("获取详情失败");
        console.error(error);
      }
    },

    // 提交意见反馈
    submitFeedback() {
      this.$refs.feedbackForm.validate(async (valid) => {
        if (valid) {
          this.submitting = true;
          try {
            await feedback(this.feedbackForm);
            this.$message.success("反馈提交成功，感谢您的意见和建议！");
            this.closeFeedbackDialog();
            this.getFeedbackList(); // 重新获取列表
          } catch (error) {
            this.$message.error("提交失败，请稍后重试");
            console.error(error);
          } finally {
            this.submitting = false;
          }
        } else {
          this.$message.error("请完善必填信息");
          return false;
        }
      });
    },

    // 关闭提交弹框
    closeFeedbackDialog() {
      this.showFeedbackDialog = false;
      this.resetForm();
    },

    // 重置表单
    resetForm() {
      this.$refs.feedbackForm && this.$refs.feedbackForm.resetFields();
      this.feedbackForm = {
        userName: "",
        lxfs: "",
        feedbackContent: "",
      };
    },

    // 分页相关
    handleSizeChange(val) {
      this.queryParams.pageSize = val;
      this.getFeedbackList();
    },

    handleCurrentChange(val) {
      this.queryParams.pageNum = val;
      this.getFeedbackList();
    },

    // 格式化时间
    formatTime(time) {
      if (!time) return "";
      return new Date(time).toLocaleString("zh-CN");
    },
  },
};
</script>

<style scoped>
.feedback-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  background: #fff;
}

.feedback-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f0f0f0;
}

.feedback-title {
  font-size: 28px;
  color: #2457a7;
  margin: 0;
  font-weight: bold;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.feedback-list {
  min-height: 400px;
}

.feedback-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px;
  margin-bottom: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #2457a7;
  cursor: pointer;
  transition: all 0.3s ease;
}

.feedback-item:hover {
  background: #e8f4fd;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.feedback-content {
  flex: 1;
}

.feedback-subject {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin: 0 0 10px 0;
}

.reply-section {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #e8e8e8;
}

.reply-subject {
  font-size: 16px;
  font-weight: bold;
  color: #2457a7;
  margin: 0 0 10px 0;
}

.reply-text {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 0 0 12px 0;
  background: #f0f9ff;
  padding: 12px;
  border-radius: 6px;
  border-left: 4px solid #2457a7;
}

.feedback-text {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 0 0 12px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.feedback-meta {
  display: flex;
  gap: 20px;
  font-size: 12px;
  color: #999;
}

.feedback-status {
  margin-left: 20px;
}

.loading-container,
.empty-container {
  text-align: center;
  padding: 60px 0;
}

.pagination-container {
  text-align: center;
  margin-top: 30px;
}

.feedback-form .el-form-item {
  margin-bottom: 20px;
}

.feedback-form .el-form-item__label {
  font-weight: 500;
  color: #333;
}

.feedback-form .el-input__inner,
.feedback-form .el-textarea__inner {
  border-radius: 6px;
  border: 1px solid #dcdfe6;
  transition: all 0.3s;
}

.feedback-form .el-input__inner:focus,
.feedback-form .el-textarea__inner:focus {
  border-color: #2457a7;
  box-shadow: 0 0 0 2px rgba(36, 87, 167, 0.1);
}

.detail-content {
  padding: 20px 0;
}

.detail-item {
  margin-bottom: 20px;
  display: flex;
  align-items: flex-start;
}

.detail-item label {
  font-weight: bold;
  color: #333;
  min-width: 80px;
  margin-right: 10px;
}

.detail-content-text {
  flex: 1;
  line-height: 1.6;
  color: #666;
  white-space: pre-wrap;
}

.reply-content {
  background: #f0f9ff;
  padding: 15px;
  border-radius: 6px;
  border-left: 4px solid #2457a7;
}

.dialog-footer {
  text-align: right;
}
</style>
