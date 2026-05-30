<template>
  <div class="questionnaire-container">
    <div v-if="!isFish">
      <div v-if="invalidQuestion" class="expired-container">
        <div class="expired-content">
          <div class="expired-icon-wrapper">
            <svg
              class="expired-icon"
              viewBox="0 0 1024 1024"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path
                d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z"
                fill="#f56c6c"
              />
              <path
                d="M464 336a48 48 0 0 1 96 0v112a48 48 0 0 1-96 0V336zm32 272a48 48 0 1 1 96 0 48 48 0 0 1-96 0z"
                fill="#f56c6c"
              />
            </svg>
          </div>
          <h2 class="expired-title">二维码无效</h2>
          <div class="expired-message">
            <p>缺少问卷标识，无法打开对应问卷。</p>
            <p>请重新扫描正确的问卷二维码。</p>
          </div>
        </div>
      </div>
      <div v-else-if="question.state != 2">
        <!-- 顶部导航栏 -->
        <div class="header-bar">
          <div class="header-left">
            <!-- <i class="el-icon-arrow-left" @click="$router.go(-1)"></i> -->
          </div>
          <div class="header-title">问卷调查</div>
          <div class="header-right">
            <i class="el-icon-question" @click="showHint"></i>
          </div>
        </div>

        <!-- 进度条 -->
        <div class="progress-container">
          <div class="progress-bar">
            <div
              class="progress-fill"
              :style="{ width: progressPercentage + '%' }"
            ></div>
          </div>
          <div class="progress-text">
            {{ currentQuestion }}/{{ totalQuestions }}
          </div>
        </div>

        <div class="questionnaire-info" v-if="qyList.length > 0">
          <div class="info-card" v-for="(item, index) in qyList" :key="index">
            <div class="info-title">厂商名称</div>
            <div class="info-content">{{ item.name }}</div>
            <div class="info-title">厂商信息</div>
            <div class="info-content">
              <file-upload v-model="item.file" :disabled="true"></file-upload>
              <template v-if="item.videoUrl">
                <div
                  v-for="(video, vIdx) in item.videoUrl
                    .split(',')
                    .filter((v) => v.trim())"
                  :key="vIdx"
                  class="video-item"
                >
                  <el-button type="text" @click="playVideo(video.trim())"
                    >在线播放视频</el-button
                  >
                  <span class="video-name">{{
                    getVideoName(video.trim())
                  }}</span>
                </div>
              </template>
              <template v-if="item.url">
                <div
                  @click="handeOpenUrl(item.url)"
                  v-for="(url, uIdx) in item.url
                    .split(',')
                    .filter((u) => u.trim())"
                  :key="uIdx"
                  class="url-item"
                >
                  {{ url.trim() }}
                </div>
              </template>
            </div>
          </div>
        </div>

        <!-- 问卷内容 -->
        <el-form
          ref="questionnaireForm"
          :model="formData"
          v-loading="loading"
          label-position="top"
          class="questionnaire-form"
        >
          <div
            v-for="(item, index) in question.sonList"
            :key="index"
            class="question-card"
          >
            <el-form-item
              :prop="`answers.${index}`"
              :rules="getValidationRules(item)"
            >
              <div class="question-header">
                <div class="question-number">
                  {{ String(index + 1).padStart(2, "0") }}
                </div>
                <div class="question-content">
                  <span v-if="item.isRequired == 'true'" class="required-mark"
                    >*</span
                  >
                  <span>{{ item.surveyContent }}</span>
                </div>
                <div class="question-type">
                  {{ getQuestionTypeText(item.answeringMethod) }}
                </div>
              </div>

              <!-- 单选题 -->
              <div v-if="item.answeringMethod == 0" class="options-container">
                <el-radio-group
                  v-model="formData.answers[index]"
                  class="radio-group"
                >
                  <el-radio
                    v-for="(option, optIndex) in getOptions(item.surveyResult)"
                    :key="optIndex"
                    :label="option"
                    class="radio-option"
                  >
                    <div v-if="detectOptionType(getOptions(item.surveyResult), item.optionType) === 'image' && option">
                      <image-preview :src="imgUrl + option" width="140px" height="80px" />
                    </div>
                    <span v-else>{{ option }}</span>
                  </el-radio>
                </el-radio-group>
              </div>

              <!-- 多选题 -->
              <div
                v-else-if="item.answeringMethod == 1"
                class="options-container"
              >
                <el-checkbox-group
                  v-model="formData.answers[index]"
                  class="checkbox-group"
                >
                  <el-checkbox
                    v-for="(option, optIndex) in getOptions(item.surveyResult)"
                    :key="optIndex"
                    :label="option"
                    class="checkbox-option"
                  >
                    <div v-if="detectOptionType(getOptions(item.surveyResult), item.optionType) === 'image' && option">
                      <image-preview :src="imgUrl + option" width="140px" height="80px" />
                    </div>
                    <span v-else>{{ option }}</span>
                  </el-checkbox>
                </el-checkbox-group>
              </div>

              <!-- 文本输入框 -->
              <div
                v-else-if="item.answeringMethod == 2"
                class="text-input-container"
              >
                <el-input
                  type="textarea"
                  v-model="formData.answers[index]"
                  :rows="4"
                  placeholder="请写下你想对我们说的建议"
                  class="text-input"
                ></el-input>
              </div>
              <div
                v-else-if="item.answeringMethod == 3"
                class="text-input-container"
              >
                <el-input
                  v-model="formData.answers[index]"
                  placeholder="请输入您的回答"
                  class="text-input"
                ></el-input>
              </div>
            </el-form-item>
          </div>
        </el-form>

        <!-- 提交按钮 -->
        <div class="submit-container">
          <el-button
            type="primary"
            @click="showSignatureDialog"
            :loading="submitting"
            class="submit-button"
            >下一步</el-button
          >
        </div>
      </div>
      <!-- 问卷过期提示 -->
      <div v-else class="expired-container">
        <div class="expired-content">
          <div class="expired-icon-wrapper">
            <svg
              class="expired-icon"
              viewBox="0 0 1024 1024"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path
                d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z"
                fill="#f56c6c"
              />
              <path
                d="M464 336a48 48 0 0 1 96 0v112a48 48 0 0 1-96 0V336zm32 272a48 48 0 1 1 96 0 48 48 0 0 1-96 0z"
                fill="#f56c6c"
              />
            </svg>
          </div>
          <h2 class="expired-title">问卷已过期</h2>
          <div class="expired-message">
            <p>很抱歉，该问卷已经结束或尚未开始，无法填写。</p>
            <p>如有疑问，请联系管理员。</p>
          </div>
        </div>
      </div>
    </div>
    <div v-else class="finsishSuceess expired-container">
      <i class="el-icon-success expired-icon"></i>
      <h2>提交成功</h2>
    </div>
    <!-- 签名页面 -->
    <div v-show="signatureMode" class="signature-fullscreen-container">
      <div class="signature-header">
        <h3>请手写投票人姓名</h3>
      </div>
      <div class="signature-canvas-wrapper">
        <canvas ref="signatureCanvas"></canvas>
      </div>
      <div class="signature-footer">
        <el-button @click="clearSignature" size="large">清除重签</el-button>
        <el-button @click="cancelSignature" size="large">上一步</el-button>
        <el-button
          type="primary"
          @click="confirmSignature"
          :loading="submitting"
          size="large"
        >
          确定提交
        </el-button>
      </div>
    </div>
    <!-- 视频播放弹窗 -->
    <el-dialog
      fullscreen
      title="视频播放"
      :visible.sync="videoDialogVisible"
      width="60%"
      @close="videoUrl = ''"
    >
      <video
        v-if="videoUrl"
        :src="baseUrl + videoUrl"
        controls
        style="width: 100%"
      ></video>
    </el-dialog>
  </div>
</template>

<script>
import SmoothSignature from "smooth-signature";
import {
  getQuestionnaireContent,
  saveQuestionnaireResult,
} from "@/api/questionnaire";

export default {
  data() {
    return {
      baseUrl: process.env.VUE_APP_IMAGE_BASE_API,
      imgUrl: process.env.VUE_APP_IMAGE_BASE_API,
      isFish: false,
      form: {
        questionnaireSurveyId: null,
        patriarchName: "",
        studentName: "",
      },
      formData: {
        answers: [],
      },
      rules: {
        patriarchName: [
          { required: true, message: "请输入家长姓名", trigger: "blur" },
        ],
        studentName: [
          { required: true, message: "请输入学生姓名", trigger: "blur" },
        ],
      },
      question: {},
      loading: false,
      submitting: false,
      qyList: [], //企业列表
      signatureMode: false, // 签名模式
      signature: null, // 签名实例
      invalidQuestion: false,
      videoDialogVisible: false,
      videoUrl: "",
    };
  },
  created() {
    this.handleGetQuestion();
  },
  mounted() {
    // 移除原来的canvas初始化，改为在弹窗打开时初始化
  },
  computed: {
    questionsId() {
      return this.$route.query.id;
    },
    totalQuestions() {
      return this.question.sonList ? this.question.sonList.length : 0;
    },
    currentQuestion() {
      // 计算已填写的问题数量
      if (!this.formData.answers || this.totalQuestions === 0) return 0;

      let answeredCount = 0;
      this.formData.answers.forEach((answer, index) => {
        const question = this.question.sonList[index];
        if (question) {
          if (question.answeringMethod === 1) {
            // 多选题
            if (answer && Array.isArray(answer) && answer.length > 0) {
              answeredCount++;
            }
          } else {
            // 单选题或文本题
            if (answer && answer.toString().trim() !== "") {
              answeredCount++;
            }
          }
        }
      });

      return answeredCount;
    },
    progressPercentage() {
      return this.totalQuestions > 0
        ? (this.currentQuestion / this.totalQuestions) * 100
        : 0;
    },
  },
  methods: {
    showHint() {
      this.$alert("每个学生只能投票一次", "提示", {
        confirmButtonText: "确定",
        type: "info",
      });
    },
    handeOpenUrl(url) {
      window.open(url);
    },
    // 获取验证规则
    getValidationRules(item) {
      if (item.isRequired == "true") {
        let message = "";
        if (item.answeringMethod == 0) {
          message = "请选择答案";
        } else if (item.answeringMethod == 1) {
          message = "请至少选择一个选项";
        } else if (item.answeringMethod == 2 || item.answeringMethod == 3) {
          message = "请填写答案";
        }

        return [
          {
            message: message,
            trigger: item.answeringMethod <= 1 ? "change" : "blur",
            validator: (rule, value, callback) => {
              if (item.answeringMethod == 1) {
                // 多选题验证
                if (!value || !Array.isArray(value) || value.length === 0) {
                  callback(new Error(message));
                } else {
                  callback();
                }
              } else if (item.answeringMethod == 0) {
                // 单选题验证
                if (!value || value === "") {
                  callback(new Error(message));
                } else {
                  callback();
                }
              } else {
                // 文本题验证
                if (!value || value.trim() === "") {
                  callback(new Error(message));
                } else {
                  callback();
                }
              }
            },
          },
        ];
      }
      return [];
    },

    // 初始化签名画布
    initSignature() {
      this.$nextTick(() => {
        const canvas = this.$refs.signatureCanvas;
        if (canvas && !this.signature) {
          // 等待 DOM 完全渲染后再获取尺寸
          setTimeout(() => {
            const rect = canvas.getBoundingClientRect();
            const devicePixelRatio = window.devicePixelRatio || 1;
            
            // 设置 canvas 的实际尺寸
            const width = rect.width;
            const height = rect.height;
            
            // 设置 canvas 的内部尺寸以匹配设备像素比
            canvas.width = width * devicePixelRatio;
            canvas.height = height * devicePixelRatio;
            
            // 设置 canvas 的 CSS 尺寸
            canvas.style.width = width + 'px';
            canvas.style.height = height + 'px';
            
            this.signature = new SmoothSignature(canvas, {
              width: width,
              height: height,
              scale: devicePixelRatio,
              minWidth: 2,
              maxWidth: 6,
              color: "#1890ff",
              bgColor: "#fafafa",
              // 移动端优化配置
              openSmooth: true,
              minSpeed: 1.5,
              minDistance: 5,
            });
          }, 100);
        }
      });
    },

    // 显示签名页面
    showSignatureDialog() {
      // 使用 el-form 验证
      this.$refs.questionnaireForm.validate((valid) => {
        if (valid) {
          this.signatureMode = true;
          // 添加body类来禁止滚动
          document.body.classList.add("signature-active");
          this.initSignature();
          
          // 监听屏幕旋转和尺寸变化
          this.addResizeListener();
        } else {
          this.$message.error("请完成所有必填问题");
          return false;
        }
      });
    },

    // 添加尺寸变化监听器
    addResizeListener() {
      this.resizeHandler = () => {
        if (this.signatureMode && this.signature) {
          // 延迟重新初始化以确保 DOM 更新完成
          setTimeout(() => {
            this.reinitSignature();
          }, 200);
        }
      };
      
      window.addEventListener('resize', this.resizeHandler);
      window.addEventListener('orientationchange', this.resizeHandler);
    },

    // 重新初始化签名画布
    reinitSignature() {
      if (this.signature) {
        // 保存当前签名数据
        const currentSignature = this.signature.isEmpty() ? null : this.signature.getPNG();
        
        // 清除当前签名实例
        this.signature = null;
        
        // 重新初始化
        this.initSignature();
        
        // 如果有之前的签名数据，可以选择恢复（这里暂时不恢复，让用户重新签名）
      }
    },

    // 清除签名
    clearSignature() {
      if (this.signature) {
        this.signature.clear();
      }
    },

    // 取消签名
    cancelSignature() {
      this.signatureMode = false;
      // 移除body类恢复滚动
      document.body.classList.remove("signature-active");
      
      // 清理事件监听器
      this.removeResizeListener();
      
      if (this.signature) {
        this.signature.clear();
        this.signature = null;
      }
    },

    // 移除尺寸变化监听器
    removeResizeListener() {
      if (this.resizeHandler) {
        window.removeEventListener('resize', this.resizeHandler);
        window.removeEventListener('orientationchange', this.resizeHandler);
        this.resizeHandler = null;
      }
    },

    // 确认签名并提交
    confirmSignature() {
      if (!this.signature || this.signature.isEmpty()) {
        this.$message.error("请先进行签名");
        return;
      }

      // 获取签名数据
      const signatureData = this.signature.getPNG();
      
      // 清理事件监听器
      this.removeResizeListener();

      // 执行原来的提交逻辑
      this.handleSubmit(signatureData);
    },

    // 获取问卷内容
    handleGetQuestion() {
      const questionId = Number(this.questionsId);
      if (!this.questionsId || !Number.isFinite(questionId)) {
        this.invalidQuestion = true;
        this.$message.error("二维码无效，缺少问卷标识");
        return;
      }
      this.loading = true;
      getQuestionnaireContent({ id: questionId })
        .then((res) => {
          this.question = res.data || {};
          if (this.question.category == 3) {
            this.qyList = JSON.parse(this.question.votingDescription);
          }
          // 初始化问题的答案字段
          if (this.question.sonList && this.question.sonList.length) {
            this.formData.answers = this.question.sonList.map((item) => {
              if (item.answeringMethod == 0) {
                // 单选
                return "";
              } else if (item.answeringMethod == 1) {
                // 多选
                return [];
              } else if (
                item.answeringMethod == 2 ||
                item.answeringMethod == 3
              ) {
                // 文本
                return "";
              }
              return "";
            });
          }

          this.loading = false;
        })
        .catch(() => {
          this.$message.error("获取问卷失败");
          this.loading = false;
        });
    },

    // 解析选项字符串
    getOptions(optionStr) {
      if (!optionStr) return [];
      return optionStr.split("、").filter((item) => item.trim());
    },

    // 检测选项类型（图片或文本）
    detectOptionType(options, existingOptionType) {
      // 如果已经有明确的选项类型设置，优先使用
      if (existingOptionType) {
        return existingOptionType;
      }

      // 检测选项内容是否为图片
      if (options && options.length > 0) {
        // 检查是否有选项包含图片文件扩展名
        const imageExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.bmp', '.webp', '.svg'];
        
        for (let option of options) {
          if (typeof option === 'string' && option.trim()) {
            // 检查选项是否为图片文件名或包含图片扩展名
            const hasImageExtension = imageExtensions.some(ext => 
              option.toLowerCase().includes(ext)
            );
            
            // 或者检查选项是否为纯文件名（没有中文、空格等文本内容）
            const isImageFileName = /^[^、，。！？\s\u4e00-\u9fa5]+\.(jpg|jpeg|png|gif|bmp|webp|svg)$/i.test(option.trim());
            
            if (hasImageExtension || isImageFileName) {
              return 'image';
            }
          }
        }
      }

      // 默认为文本选项
      return 'text';
    },

    // 获取问题类型文本
    getQuestionTypeText(type) {
      const typeMap = {
        0: "单选",
        1: "多选",
        2: "填空",
        3: "填空",
      };
      return typeMap[type] || "";
    },

    // 获取问题标签类型
    getQuestionTagType(type) {
      const typeMap = {
        0: "success",
        1: "warning",
        2: "info",
      };
      return typeMap[type] || "";
    },

    // 提交问卷
    handleSubmit(signatureData) {
      this.submitting = true;
      let selectOptionsChecked = [];

      // 构建提交的数据
      const results = this.question.sonList
        .map((item, index) => {
          let result = "";
          const answer = this.formData.answers[index];

          if (item.answeringMethod == 0) {
            result = answer;
          } else if (item.answeringMethod == 2 || item.answeringMethod == 3) {
            result = answer;
          }

          if (item.answeringMethod == 1) {
            selectOptionsChecked = answer.map((itemCheck) => {
              return {
                surveyId: item.id,
                questionnaireResult: itemCheck,
              };
            });
          }
          console.log(selectOptionsChecked, "多选");

          return {
            surveyId: item.id,
            questionnaireResult: result,
          };
        })
        .concat(selectOptionsChecked)
        .filter((item) => item.questionnaireResult);

      const formData = results.map((item) => {
        return {
          questionnaireSurveyId: this.questionsId,
          surveyId: item.surveyId,
          questionnaireResult: item.questionnaireResult,
          patriarchName: this.form.patriarchName,
          studentName: this.form.studentName,
          name: this.$route.query.name,
        };
      });

      saveQuestionnaireResult({
        xfptQuestionnaireResult: formData,
        signature: signatureData,
      })
        .then((res) => {
          this.$message.success("问卷提交成功");
          this.signatureMode = false;
          // 移除body类恢复滚动
          document.body.classList.remove("signature-active");
          this.isFish = true;
          this.submitting = false;
        })
        .catch(() => {
          this.submitting = false;
        });
    },

    getVideoName(url) {
      if (!url) return "";
      return url.split("/").pop();
    },
    playVideo(url) {
      this.videoUrl = url;
      this.videoDialogVisible = true;
    },
  },
};
</script>

<style scoped>
::v-deep .radio-option{
  display: flex;
}
::v-deep .checkbox-option{
  display: flex;
}
.questionnaire-container {
  background: #f5f5f5;
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

/* 顶部导航栏 */
.header-bar {
  background: #1890ff;
  color: white;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
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

/* 进度条 */
.progress-container {
  background: white;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  border-bottom: 1px solid #e8e8e8;
  position: sticky;
  top: 0;
  z-index: 99;
}

.progress-bar {
  flex: 1;
  height: 4px;
  background: #e8e8e8;
  border-radius: 2px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: #1890ff;
  border-radius: 2px;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 14px;
  color: #666;
  font-weight: 500;
  min-width: 40px;
}

/* 问卷信息卡片 */
.questionnaire-info {
  padding: 16px;
}

.info-card {
  background: white;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.info-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
  font-weight: 500;
}

.info-content {
  font-size: 16px;
  color: #333;
  line-height: 1.5;
}

.video-item,
.url-item {
  margin-top: 8px;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.video-name {
  margin-left: 8px;
  color: #888;
  font-size: 14px;
}

.url-item {
  color: #1890ff;
  word-break: break-all;
  cursor: pointer;
}

/* 问卷表单 */
.questionnaire-form {
  padding: 16px;
}

.question-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.question-header {
  display: flex;
  align-items: flex-start;
  margin-bottom: 16px;
  gap: 12px;
}

.question-number {
  background: #1890ff;
  color: white;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  flex-shrink: 0;
}

.question-content {
  flex: 1;
  font-size: 16px;
  color: #333;
  line-height: 1.5;
  font-weight: 500;
}

.required-mark {
  color: #ff4d4f;
  margin-right: 4px;
}

.question-type {
  background: #f0f8ff;
  color: #1890ff;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  flex-shrink: 0;
}

/* 选项容器 */
.options-container {
  margin-top: 16px;
}

.radio-group,
.checkbox-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.radio-option,
.checkbox-option {
  margin: 0 !important;
  padding: 12px 16px;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  background: white;
  transition: all 0.2s ease;
}

.radio-option:hover,
.checkbox-option:hover {
  border-color: #1890ff;
  background: #f0f8ff;
}

.radio-option.is-checked,
.checkbox-option.is-checked {
  border-color: #1890ff;
  background: #f0f8ff;
  color: #1890ff;
}

/* 文本输入 */
.text-input-container {
  margin-top: 16px;
}

.text-input {
  width: 100%;
}

.text-input .el-textarea__inner,
.text-input .el-input__inner {
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  padding: 12px 16px;
  font-size: 16px;
  background: white;
}

.text-input .el-textarea__inner:focus,
.text-input .el-input__inner:focus {
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

/* 提交按钮 */
.submit-container {
  padding: 24px 16px;
  background: white;
  border-top: 1px solid #e8e8e8; 
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

/* 过期提示样式 */
.expired-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  padding: 30px;
}

.expired-content {
  background: white;
  padding: 60px 40px;
  text-align: center;
  max-width: 400px;
  width: 100%;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.expired-icon-wrapper {
  margin-bottom: 24px;
}

.expired-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto;
  display: flex;
  justify-content: center;
}

.expired-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
}

.expired-message {
  margin-bottom: 24px;
}

.expired-message p {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 8px 0;
}

/* 成功提示样式 */
.finsishSuceess {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.finsishSuceess.expired-container {
  background: #f5f5f5;
}

.finsishSuceess .expired-icon {
  color: #52c41a;
  font-size: 60px;
  margin-bottom: 16px;
}

.finsishSuceess h2 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

/* 全屏签名页面样式 */
.signature-fullscreen-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: #f5f5f5;
  z-index: 9999;
  display: flex;
  flex-direction: column;
}

.signature-header {
  padding: 20px;
  text-align: center;
  background: white;
  border-bottom: 1px solid #e8e8e8;
  flex-shrink: 0;
}

.signature-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.signature-canvas-wrapper {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  background: white;
  min-height: 0;
}

.signature-canvas-wrapper canvas {
  width: 100% !important;
  height: 100% !important;
  max-height: calc(100vh - 200px);
  min-height: 300px;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  background: #fafafa;
  touch-action: none;
  cursor: crosshair;
  /* 移动端优化 */
  -webkit-touch-callout: none;
  -webkit-user-select: none;
  -khtml-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;
  /* 确保 canvas 正确缩放 */
  image-rendering: -webkit-optimize-contrast;
  image-rendering: -moz-crisp-edges;
  image-rendering: -o-crisp-edges;
  image-rendering: optimize-contrast;
}

.signature-footer {
  padding: 20px;
  background: white;
  border-top: 1px solid #e8e8e8;
  display: flex;
  justify-content: space-around;
  gap: 12px;
  flex-shrink: 0;
}

.signature-footer .el-button {
  flex: 1;
  height: 44px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 8px;
}

.signature-footer .el-button--primary {
  background: #1890ff;
  border: none;
  color: white;
}

.signature-footer .el-button--primary:hover {
  background: #096dd9;
}

/* 禁止页面滚动当签名模式激活时 */
body.signature-active {
  overflow: hidden;
  position: fixed;
  width: 100%;
  height: 100%;
}

/* Element UI 样式覆盖 */
::v-deep .el-radio__input.is-checked .el-radio__inner {
  background-color: #1890ff;
  border-color: #1890ff;
}

::v-deep .el-radio__input.is-checked + .el-radio__label {
  color: #1890ff;
}

::v-deep .el-checkbox__input.is-checked .el-checkbox__inner {
  background-color: #1890ff;
  border-color: #1890ff;
}

::v-deep .el-checkbox__input.is-checked + .el-checkbox__label {
  color: #1890ff;
}

::v-deep .el-form-item__error {
  color: #ff4d4f;
  font-size: 12px;
  margin-top: 4px;
}

/* 移动端签名优化 */
@media (max-width: 768px) {
  .signature-fullscreen-container {
    /* 确保在移动端全屏显示 */
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 9999;
  }
  
  .signature-canvas-wrapper {
    /* 移动端 canvas 容器优化 */
    padding: 10px;
  }
  
  .signature-canvas-wrapper canvas {
    min-height: 250px;
    max-height: calc(100vh - 150px);
    /* 移动端触摸优化 */
    -webkit-tap-highlight-color: transparent;
  }
  
  .signature-footer {
    padding: 15px 10px;
    gap: 8px;
  }
  
  .signature-footer .el-button {
    height: 40px;
    font-size: 13px;
  }
  
  .question-card {
    padding: 16px;
    margin-bottom: 12px;
  }

  .question-content {
    font-size: 15px;
  }

  .question-number {
    width: 28px;
    height: 28px;
    font-size: 12px;
  }

  .submit-button {
    height: 44px;
    font-size: 15px;
  }
}

@media (max-width: 375px) {
  .question-card {
    padding: 12px;
  }

  .question-content {
    font-size: 14px;
  }

  .radio-option,
  .checkbox-option {
    padding: 10px 12px;
    font-size: 14px;
  }

  .text-input .el-textarea__inner,
  .text-input .el-input__inner {
    padding: 10px 12px;
    font-size: 14px;
  }
}
</style>
