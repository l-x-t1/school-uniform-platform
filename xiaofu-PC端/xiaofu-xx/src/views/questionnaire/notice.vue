<template>
  <div class="notice-container">
    <!-- 加载中状态 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-content">
        <i class="el-icon-loading loading-icon"></i>
        <p>加载中...</p>
      </div>
    </div>

    <!-- 调查须知内容 -->
    <div v-else class="page-content">
      <div class="header-bar">
        <div class="header-left">
          <!-- <i class="el-icon-arrow-left" @click="goBack"></i> -->
        </div>
        <div class="header-title">问卷须知</div>
        <div class="header-right"></div>
      </div>
      <div class="notice-body">
        <div v-if="votingNotice" class="notice-card" v-html="votingNotice" />
        <div
          class="notice-card"
          :style="votingNotice ? { marginTop: '16px' } : {}"
        >
          <el-form
            :model="form"
            ref="form"
            label-width="100px"
            label-position="top"
            :rules="rules"
          >
            <el-form-item :label="type == 4 ? '姓名' : '学生姓名'" prop="name">
              <el-select
                @hook:mounted="cancelReadOnly"
                @visible-change="cancelReadOnly"
                ref="selectInfo"
                style="width: 100%"
                v-model="form.name"
                filterable
                remote
                clearable
                reserve-keyword
                :placeholder="type == 4 ? '请输入姓名' : '请输入学生姓名'"
                :remote-method="searchName"
                :loading="nameLoading"
              >
                <el-option
                  v-for="item in options"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="手机号" prop="phone" v-if="type == 4">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-form>
        </div>
      </div>
      <div class="submit-container">
        <div style="margin-bottom: 10px" v-if="votingNotice">
          <el-checkbox v-model="hasRead" @change="onReadChange">
            <span class="agreement-text">我已阅读</span>
          </el-checkbox>
        </div>
        <el-button
          type="primary"
          @click="handleAgree"
          :disabled="!form.name || (votingNotice && !hasRead)"
          class="submit-button"
        >
          下一步
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { getQuestionnaireResult, getStudentName , getStudentNameAndPhone } from "@/api/questionnaire";

export default {
  name: "QuestionnaireNotice",
  data() {
    return {
      loading: true,
      votingNotice: "", // 调查须知内容
      hasRead: false,
      form: {
        name: "",
        phone: "",
      },
      options: [],
      nameLoading: false,
      rules: {
        name: [{ required: true, message: "请输入学生姓名", trigger: "blur" }],
        phone: [{ required: true, message: "请输入手机号", trigger: "blur" }],
      },
      type:null,//查询调查问卷  1意见征集   2满意度调查  4评定小组确定厂商
    };
  },
  computed: {
    questionnaireId() {
      return this.$route.query.id;
    },
  },
  created() {
    this.getNoticeContent();
  },
  methods: {
    cancelReadOnly(onOff) {
      this.$nextTick(() => {
        if (!onOff) {
          // ios 手机有延迟问题
          setTimeout(() => {
            const { selectInfo } = this.$refs;
            const input = selectInfo.$el.querySelector(".el-input__inner");
            input.removeAttribute("readonly");
          }, 200);
        }
      });
    },
    //查询学生姓名
    searchName(query) {
      if (query !== "") {
        this.nameLoading = true;
        getStudentName({
          name: query,
          questId: this.questionnaireId,
        }).then((res) => {
          this.options = res.data.map((item) => {
            return {
              value: item,
              label: item,
            };
          });
          this.nameLoading = false;
        });
      } else {
        this.options = [];
      }
    },
    // 获取调查须知内容
    async getNoticeContent() {
      try {
        this.loading = true;
        const response = await getQuestionnaireResult({
          id: this.questionnaireId,
        });
        if (
          response.data &&
          response.data.names != null &&
          response.data.names != "" &&
          response.data.names != undefined
        ) {
          this.votingNotice = response.data.votingNotice;
          this.type = response.data.type;
        } else {
          // 如果没有调查须知，直接跳转到问卷
          this.goToQuestionnaire();
        }
      } catch (error) {
        setTimeout(() => {
          this.$router.push("/cg");
        }, 1000);
        // 出错时也跳转到问卷页面
      } finally {
        this.loading = false;
      }
    },

    // 点击同意按钮
    handleAgree() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if(this.type == 4){
            this.getStudentNameAndPhone();
          }else{
            this.goToQuestionnaire();
          }
        }
      });
    },

    // 点击不同意按钮
    handleDisagree() {
      this.goBack();
    },
    //验证学生姓名和手机号
    getStudentNameAndPhone() {
      getStudentNameAndPhone({
        userName: this.form.name,
        phone: this.form.phone,
        id:this.questionnaireId,
      }).then((res) => {
        this.goToQuestionnaire();
      });
    },
    // 跳转到问卷页面
    goToQuestionnaire() {
      this.$router.push({
        path: "/questionnaire",
        query: {
          id: this.questionnaireId,
          name: this.form.name,
        },
      });
    },

    // 返回上一页
    goBack() {
      this.$router.push("/cg");
    },

    onReadChange() {
      // 处理勾选框变化后的逻辑
    },
  },
};
</script>

<style scoped>
.page-content {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}
.notice-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 0;
  position: relative;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

.loading-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
}

.loading-content {
  text-align: center;
  color: #333;
  background: #fff;
  padding: 40px 30px;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  border: 1px solid #e8ecf0;
}

.loading-icon {
  font-size: 48px;
  color: #1890ff;
  margin-bottom: 20px;
  animation: rotate 2s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.loading-content p {
  margin: 0;
  font-size: 16px;
  color: #666;
  font-weight: 500;
}

.header-bar {
  background: #1890ff;
  color: white;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: sticky;
  top: 0;
  z-index: 99;
}

.header-left,
.header-right {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  flex: 1;
  text-align: center;
}

.notice-body {
  padding: 16px;
  flex: 1;
  /* overflow-y: auto; */
}

.notice-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  line-height: 1.8;
  font-size: 16px;
  color: #2c3e50;
}

.notice-card :deep(p) {
  margin-bottom: 16px;
  text-align: justify;
  text-indent: 2em;
}

.notice-card :deep(ul),
.notice-card :deep(ol) {
  padding-left: 24px;
  margin-bottom: 16px;
}

.notice-card :deep(li) {
  margin-bottom: 8px;
  color: #34495e;
}

.notice-card :deep(h1),
.notice-card :deep(h2),
.notice-card :deep(h3) {
  font-size: 18px;
  font-weight: 700;
  margin: 24px 0 12px 0;
  color: #2c3e50;
  padding-left: 12px;
  border-left: 4px solid #1890ff;
}

.submit-container {
  padding: 24px 16px;
  background: white;
  border-top: 1px solid #e8e8e8;
  text-align: center;
}

.agreement-text {
  font-size: 14px;
  color: #606266;
}

.submit-button {
  width: 100%;
  height: 48px;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  color: white;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
  transition: all 0.3s ease;
}

.submit-button:hover {
  background: linear-gradient(135deg, #096dd9 0%, #0050b3 100%);
  box-shadow: 0 6px 16px rgba(24, 144, 255, 0.4);
  transform: translateY(-1px);
}

.submit-button:disabled {
  background: #c0c4cc !important;
  color: #fff !important;
  cursor: not-allowed !important;
  opacity: 0.6 !important;
  box-shadow: none;
  transform: none;
}

::v-deep .el-select .el-input__inner {
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  padding: 12px 16px;
  height: 48px;
  font-size: 16px;
  background: white;
}
::v-deep .el-select .el-input__inner:focus {
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

/* 适配安全区域 */
@supports (bottom: env(safe-area-inset-bottom)) {
  .submit-container {
    padding-bottom: calc(24px + env(safe-area-inset-bottom));
  }
}
</style>
