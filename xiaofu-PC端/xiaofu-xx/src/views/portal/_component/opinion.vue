<template>
  <div class="opinion-container">
    <!-- 意见反馈内容区域 -->
    <div class="opinion-section">
      <div class="section-header">
        <div class="header-left">
          <div class="header-content">
            <h2 class="section-title">意见反馈</h2>
          </div>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="showFeedbackDialog = true">
            提交意见反馈
          </el-button>
        </div>
      </div>
      
      <div v-loading="loading" class="opinion-content">
        <div v-if="feedbackList.length > 0" class="opinion-list">
          <div
            v-for="item in feedbackList"
            :key="item.id"
            class="opinion-item"
            @click="viewDetail(item)"
          >
            <div class="opinion-content-wrapper">
              <div class="question-section">
                <h4 class="question-title">问题：</h4>
                <p class="question-text">{{ item.feedbackContent }}</p>
                <div class="question-meta">
                  <span class="meta-time">提交时间：{{ parseTime(item.createTime) }}</span>
                </div>
              </div>
              <div v-if="item.replyContent" class="reply-section">
                <h4 class="reply-title">回复：</h4>
                <p class="reply-text">{{ item.replyContent }}</p>
                <div class="reply-meta">
                  <span class="meta-time">回复时间：{{ parseTime(item.replyTime) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无意见反馈数据"></el-empty>
      </div>
    </div>

    <!-- 分页组件 -->
    <div class="pagination-section" v-show="total > 0">
      <pagination
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        :pageSizes="[queryParams.pageSize]"
        @pagination="getFeedbackList"
      />
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
          <el-select
            style="width: 100%"
            v-model="feedbackForm.region"
            placeholder="请选择区县"
          >
            <el-option
              v-for="item of shijiazhuangDistricts"
              :value="item.name"
              :label="item.name"
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
          <span>{{ parseTime(feedbackDetail.createTime) }}</span>
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
        <div class="detail-item">
          <label>回复时间：</label>
          <span>{{ parseTime(feedbackDetail.replyTime) }}</span>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <!-- <el-button @click="showDetailDialog = false">关 闭</el-button> -->
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { shijiazhuangDistricts } from "@/utils/region";
import {
  getFeedbackList,
  getFeedbackDetail,
  feedback,
} from "@/api/publicIndex";

export default {
  name: "Opinion",
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
        region: [{ required: true, message: "请选择区县", trigger: "blur" }],
      },
    };
  },
  created() {
    this.getFeedbackList();
    console.log(this.shijiazhuangDistricts);
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

    // 格式化时间
    formatTime(time) {
      if (!time) return "";
      return new Date(time).toLocaleString("zh-CN");
    },
  },
};
</script>

<style scoped lang="scss">
.opinion-container {
  max-width: 1400px;
  width: 100%;
  margin: 0 auto;
  padding: 20px 20px 40px;
  background: #f8fafc;
}

/* 意见反馈内容区域 */
.opinion-section {
  background: white;
  border-radius: 20px;
  padding: 40px;
  margin-bottom: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #f1f5f9;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f1f5f9;
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 20px;
    
    .header-content {
      .section-title {
        font-size: 28px;
        font-weight: 600;
        color: #1e293b;
        margin: 0;
      }
    }
  }
  
  .header-actions {
    .el-button--primary {
      background: linear-gradient(135deg, #3b82f6, #1d4ed8);
      border: none;
      border-radius: 8px;
      font-weight: 500;
      
      &:hover {
        background: linear-gradient(135deg, #2563eb, #1e40af);
        transform: translateY(-1px);
      }
    }
  }
}

.opinion-content {
  .opinion-list {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }
  
  .opinion-item {
    background: #f8fafc;
    border-radius: 16px;
    padding: 25px;
    cursor: pointer;
    transition: all 0.3s ease;
    border: 1px solid #e2e8f0;
    
    &:hover {
      background: white;
      box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
      border-color: #3b82f6;
      transform: translateY(-2px);
    }
    
    .opinion-content-wrapper {
      .question-section {
        margin-bottom: 20px;
        
        .question-title {
          font-size: 16px;
          font-weight: 600;
          color: #1e293b;
          margin: 0 0 12px 0;
        }
        
        .question-text {
          font-size: 14px;
          color: #64748b;
          line-height: 1.6;
          margin: 0 0 15px 0;
          display: -webkit-box;
          -webkit-line-clamp: 3;
          -webkit-box-orient: vertical;
          overflow: hidden;
          text-overflow: ellipsis;
        }
        
        .question-meta {
          .meta-time {
            font-size: 12px;
            color: #9ca3af;
          }
        }
      }
      
      .reply-section {
        background: #f0f9ff;
        border-radius: 12px;
        padding: 20px;
        border-left: 4px solid #3b82f6;
        
        .reply-title {
          font-size: 16px;
          font-weight: 600;
          color: #3b82f6;
          margin: 0 0 12px 0;
        }
        
        .reply-text {
          font-size: 14px;
          color: #1e293b;
          line-height: 1.6;
          margin: 0 0 15px 0;
        }
        
        .reply-meta {
          .meta-time {
            font-size: 12px;
            color: #64748b;
          }
        }
      }
    }
  }
}

/* 分页组件区域 */
.pagination-section {
  background: white;
  border-radius: 20px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #f1f5f9;
  display: flex;
  justify-content: center;
}

/* 表单样式 */
.feedback-form {
  .el-form-item {
    margin-bottom: 20px;
    
    .el-form-item__label {
      font-weight: 500;
      color: #374151;
    }
    
    .el-input__inner,
    .el-textarea__inner,
    .el-select .el-input__inner {
      border-radius: 8px;
      border: 1px solid #e2e8f0;
      
      &:focus {
        border-color: #3b82f6;
        box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
      }
    }
  }
}

.dialog-footer {
  text-align: right;
  
  .el-button--primary {
    background: linear-gradient(135deg, #3b82f6, #1d4ed8);
    border: none;
    border-radius: 8px;
    font-weight: 500;
    
    &:hover {
      background: linear-gradient(135deg, #2563eb, #1e40af);
      transform: translateY(-1px);
    }
  }
}

/* 详情弹框样式 */
.detail-content {
  padding: 20px 0;
  
  .detail-item {
    margin-bottom: 20px;
    display: flex;
    align-items: flex-start;
    
    label {
      font-weight: 600;
      color: #374151;
      min-width: 80px;
      margin-right: 15px;
    }
    
    .detail-content-text {
      flex: 1;
      line-height: 1.6;
      color: #64748b;
      white-space: pre-wrap;
      
      &.reply-content {
        background: #f0f9ff;
        padding: 15px;
        border-radius: 8px;
        border-left: 4px solid #3b82f6;
        color: #1e293b;
      }
    }
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .opinion-container {
    padding: 20px 15px;
  }
  
  .opinion-section {
    padding: 25px;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
    
    .header-left .header-content .section-title {
      font-size: 24px;
    }
  }
  
  .opinion-content .opinion-item {
    padding: 20px;
    
    .opinion-content-wrapper {
      .question-section {
        .question-title,
        .reply-section .reply-title {
          font-size: 15px;
        }
        
        .question-text,
        .reply-section .reply-text {
          font-size: 13px;
        }
      }
      
      .reply-section {
        padding: 15px;
      }
    }
  }
}

@media (max-width: 480px) {
  .opinion-container {
    padding: 15px 10px;
  }
  
  .opinion-section {
    padding: 20px;
  }
  
  .section-header .header-left .header-content .section-title {
    font-size: 20px;
  }
  
  .opinion-content .opinion-item {
    padding: 15px;
    
    .opinion-content-wrapper {
      .question-section {
        .question-title,
        .reply-section .reply-title {
          font-size: 14px;
        }
        
        .question-text,
        .reply-section .reply-text {
          font-size: 12px;
        }
      }
      
      .reply-section {
        padding: 12px;
      }
    }
  }
}
</style>
