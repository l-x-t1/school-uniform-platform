<template>
  <div>
    <!-- 问卷统计弹窗 -->
    <el-dialog
      title="问卷统计"
      :visible.sync="dialogVisible"
      width="70%"
      :close-on-click-modal="false"
      append-to-body
    >
      <div v-loading="loading">
        <div>
          <h3 class="survey-title">{{ surveyData.title || "问卷调查统计" }}</h3>

          <!-- 统计概览卡片 -->
          <el-row :gutter="20" class="statistics-overview">
            <el-col :span="8">
              <el-card shadow="hover" class="stat-card">
                <div slot="header" class="stat-header">
                  <span>问题总数</span>
                </div>
                <div class="stat-value">{{ surveyData.sonList.length }}</div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card shadow="hover" class="stat-card">
                <div slot="header" class="stat-header">
                  <span>选择题数</span>
                </div>
                <div class="stat-value">{{ getChoiceQuestionsCount() }}</div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card shadow="hover" class="stat-card">
                <div slot="header" class="stat-header">
                  <span>文本题数</span>
                </div>
                <div class="stat-value">{{ getTextQuestionsCount() }}</div>
              </el-card>
            </el-col>
          </el-row>

          <!-- 问题列表 -->
          <el-collapse accordion class="questions-collapse">
            <el-collapse-item
              v-for="(item, index) in surveyData.sonList"
              :key="index"
              :name="index"
            >
              <template slot="title">
                <div class="collapse-title">
                  <span class="question-number">{{ index + 1 }}</span>
                  <span class="question-content">{{ item.surveyContent }}</span>
                  <el-tag
                    size="mini"
                    :type="getTagType(item.answeringMethod)"
                    class="question-type"
                  >
                    {{ getAnswerMethodLabel(item.answeringMethod) }}
                  </el-tag>
                </div>
              </template>

              <!-- 多选或单选统计 -->
              <div
                v-if="!isTextType(item.answeringMethod, item)"
                class="options-statistics"
              >
                <div class="options-list">
                  <div
                    v-for="(option, optIndex) in item.xfptQuestionnaireResults"
                    :key="optIndex"
                    class="option-row"
                  >
                    <div class="option-text">
                      <!-- 图片选项 -->
                      <div v-if="isImageOption(option.questionnaireResult)" class="image-option-display">
                        <image-preview 
                          :src="imgUrl + option.questionnaireResult" 
                          width="80px" 
                          height="60px" 
                        />
                      </div>
                      <!-- 文本选项 -->
                      <span v-else>{{ option.questionnaireResult }}</span>
                    </div>
                    <div class="progress-container">
                      <div
                        class="progress-bar"
                        :style="{
                          width: option.percentage,
                          backgroundColor: getRandomColor(index, optIndex),
                        }"
                      ></div>
                    </div>
                    <span class="option-value"
                      >{{ option.amount }} ({{ option.percentage }})</span
                    >
                  </div>
                </div>
              </div>

              <!-- 文本类型统计 -->
              <div v-else class="text-responses">
                <p v-if="getTextResponses(item).length == 0" class="no-data">
                  暂无回答
                </p>
                <div v-else>
                  <div class="text-response-list">
                    <div
                      v-for="(response, rIndex) in getTextResponses(item)"
                      :key="rIndex"
                      class="text-response-item"
                    >
                      <i class="el-icon-chat-dot-square"></i>
                      <span>{{ response.questionnaireResult }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </el-collapse-item>
          </el-collapse>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">关 闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getSurveyStatistics, getSurvey } from "@/api/school/survey";

export default {
  name: "SurveyStatistics",
  dicts: ["zdfs"],
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    selectedSurveyId: {
      type: [Number, String],
      default: null,
    },
    imgUrl: {
      type: String,
      default: process.env.VUE_APP_IMAGE_BASE_API
    }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      surveyData: {
        title: "",
        sonList: [],
      },
      statisticsData: {},
      chartColors: [
        "#409EFF",
        "#67C23A",
        "#E6A23C",
        "#F56C6C",
        "#909399",
        "#2ECDD1",
        "#FF9F7F",
        "#FFE180",
        "#8F82BC",
        "#7ECEF4",
      ],
    };
  },
  created() {
    this.loadStatisticsData();
  },
  watch: {
    visible(val) {
      this.dialogVisible = val;
    },
    dialogVisible(val) {
      if (!val) {
        this.$emit("update:visible", false);
      }
    },
    selectedSurveyId: {
      handler(val) {
        if (val) {
          this.loadStatisticsData();
        }
      },
      immediate: true,
    },
  },
  methods: {
    // 加载统计数据
    loadStatisticsData() {
      if (!this.selectedSurveyId) return;

      this.loading = true;
      this.dialogVisible = true;

      // 获取统计数据
      getSurveyStatistics({ id: this.selectedSurveyId }).then((response) => {
        // 检查是否有实际统计数据

        this.surveyData = response.data;
        this.loading = false;
      });
    },

    // 获取选择题数量
    getChoiceQuestionsCount() {
      if (!this.surveyData || !this.surveyData.sonList) return 0;
      return this.surveyData.sonList.filter(
        (item) => !this.isTextType(item.answeringMethod)
      ).length;
    },

    // 获取文本题数量
    getTextQuestionsCount() {
      if (!this.surveyData || !this.surveyData.sonList) return 0;
      return this.surveyData.sonList.filter((item) =>
        this.isTextType(item.answeringMethod)
      ).length;
    },

    // 根据题目类型获取标签类型
    getTagType(type) {
      if (type === 0 || type === "0") return "success";
      if (type === 1 || type === "1") return "primary";
      return "info";
    },

    // 获取作答方式标签
    getAnswerMethodLabel(value) {
      // 根据接口返回的数据格式调整
      if (value === "0" || value === 0) return "单选";
      if (value === "1" || value === 1) return "多选";
      if (value === "2" || value === 2) return "文本";

      const found = this.dict.type.zdfs?.find((item) => item.value == value);
      return found ? found.label : value;
    },

    // 判断是否为文本类型
    isTextType(type, item) {
      console.log(item);

      return type === "文本" || type == "2" || type == 2 || type == "3";
    },

    // 获取选项数据
    getOptionsData(item) {
      if (
        !item.surveyResult ||
        !this.statisticsData ||
        !this.statisticsData[item.id]
      ) {
        return [];
      }

      const optionsList = item.surveyResult.split("、");
      const stats = this.statisticsData[item.id] || {};
      const totalCount =
        Object.values(stats).reduce((sum, count) => sum + count, 0) || 1;

      return optionsList.map((option) => {
        const count = stats[option] || 0;
        const percentage = Math.round((count / totalCount) * 100);
        return { option, count, percentage };
      });
    },

    // 获取随机颜色
    getRandomColor(questionIndex, optionIndex) {
      const index = (questionIndex + optionIndex) % this.chartColors.length;
      return this.chartColors[index];
    },

    // 格式化百分比
    percentageFormat(percentage) {
      return percentage + "%";
    },

    // 获取文本回答
    getTextResponses(item) {
      return item.xfptQuestionnaireResults;
    },

    // 检测选项是否为图片类型
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

    // 判断选项是否为图片
    isImageOption(option) {
      if (!option || typeof option !== 'string') return false;
      
      const imageExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.bmp', '.webp', '.svg'];
      const optionStr = option.trim().toLowerCase();
      
      // 检查是否包含图片扩展名
      const hasImageExtension = imageExtensions.some(ext => optionStr.includes(ext));
      
      // 检查是否为纯文件名格式
      const isImageFileName = /^[^、，。！？\s\u4e00-\u9fa5]+\.(jpg|jpeg|png|gif|bmp|webp|svg)$/i.test(option.trim());
      
      return hasImageExtension || isImageFileName;
    },
  },
};
</script>

<style scoped>
.survey-title {
  text-align: center;
  margin-bottom: 20px;
  font-size: 18px;
  font-weight: bold;
}

.statistics-overview {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
}

.stat-header {
  font-weight: bold;
  font-size: 16px;
}

.stat-value {
  font-size: 24px;
  color: #409eff;
  font-weight: bold;
  margin: 10px 0;
}

.questions-collapse {
  margin-bottom: 20px;
}

.collapse-title {
  display: flex;
  align-items: center;
}

.question-number {
  display: inline-block;
  width: 24px;
  height: 24px;
  line-height: 24px;
  text-align: center;
  background-color: #409eff;
  color: white;
  border-radius: 50%;
  margin-right: 10px;
  font-size: 12px;
}

.question-content {
  flex: 1;
  font-weight: 500;
}

.question-type {
  margin-left: 10px;
}

.options-statistics {
  padding: 10px 0;
}

.options-list {
  margin-top: 5px;
}

.option-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  padding: 6px 0;
}

.option-text {
  width: 20%;
  font-size: 14px;
  word-break: break-all;
  padding-right: 10px;
  display: flex;
  align-items: center;
}

.image-option-display {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 4px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background: #fafafa;
}

.progress-container {
  flex: 1;
  height: 24px;
  background-color: #f5f7fa;
  border-radius: 3px;
  overflow: hidden;
  margin: 0 10px;
}

.progress-bar {
  height: 100%;
  transition: width 0.3s ease;
}

.option-value {
  width: 15%;
  text-align: right;
  font-size: 14px;
  color: #606266;
}

.text-responses {
  padding: 5px 0;
}

.text-response-list {
  border-left: 2px solid #ebeef5;
  padding-left: 10px;
}

.text-response-item {
  padding: 8px 0;
  display: flex;
  align-items: flex-start;
  border-bottom: 1px dashed #ebeef5;
}

.text-response-item:last-child {
  border-bottom: none;
}

.text-response-item i {
  color: #409eff;
  margin-right: 8px;
  margin-top: 2px;
}

.no-data {
  text-align: center;
  color: #909399;
  padding: 20px 0;
}
</style>
