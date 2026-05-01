<template>
  <div class="questionnaire-builder">
    <!-- 顶部工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
         
      </div>
      <div class="toolbar-right">
        <el-button type="primary" @click="saveToTemplate">保存</el-button>
      </div>
    </div>

    <div class="main-content">
      <!-- 左侧组件库 -->
      <div class="component-library">
        <h3>组件库</h3>
        <draggable :list="componentList" :group="{ name: 'components', pull: 'clone', put: false }" :sort="false"
          :clone="cloneComponent" class="component-list">
          <div v-for="component in componentList" :key="component.type" class="component-item">
            <i :class="component.icon"></i>
            <span>{{ component.label }}</span>
          </div>
        </draggable>
      </div>

      <!-- 中间设计区域 -->
      <div class="design-area">
        <div class="questionnaire-container" style="position: relative;"> 
          <div class="wj-bg" style="display: flex;flex-direction: column;align-items: center;position: absolute;top: 300px;left: 0;right: 0;bottom: 0;z-index: 1000;"
            v-if="questionnaireComponents.length === 0">
            <img src="@/assets/images/wj.png" alt="" style="width: 300px;">
            <div class="wj-bg-text" style="text-align: center;font-size: 18px;color: #1890ff;letter-spacing: 4px;">
              从左侧拖入组件进行表单设计
            </div>
          </div>  
          <draggable v-model="questionnaireComponents" group="components" class="design-container" handle=".drag-handle"
            @start="dragStart" @end="dragEnd">
            
            <div v-for="(component, index) in questionnaireComponents" :key="component.id" class="design-component"
              :class="{ 'active': activeComponentIndex === index }" @click="selectComponent(index)">
              <!-- 拖拽手柄 -->
              <div class="drag-handle" @click.stop>
                <div class="drag-dots">
                  <span></span>
                  <span></span>
                  <span></span>
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
              </div>

              <!-- 组件内容 -->
              <div class="component-main">
                <div class="component-label">
                  <span v-if="component.config.isRequired" class="required-mark">*</span>
                  <span class="label-text">{{ component.config.surveyContent || component.label }}</span>
                </div>

                <!-- 富文本编辑器工具栏 -->
                <div class="rich-text-toolbar" v-if="component.type === 'input'">
                  <el-input readonly :placeholder="component.config.sm" />
                </div>
                <div class="rich-text-toolbar" v-if="component.type === 'textarea'">
                  <el-input readonly type="textarea" :rows="2" :placeholder="component.config.sm" />
                </div>
                <div class="rich-text-toolbar" v-if="component.type === 'radio'">
                  <el-radio-group class="form-radio-group">
                    <el-radio v-for="(option, optionIndex) in component.config.options" :key="optionIndex"
                      :label="option" class="form-radio">
                      <div v-if="component.config.optionType === 'image' && option " >
                        <image-preview :src="imgUrl+option" width="140px" height="80px" />
                      </div>
                      <span v-else>{{ option }}</span>
                    </el-radio>
                  </el-radio-group>
                </div>
                <div class="rich-text-toolbar" v-if="component.type === 'checkbox'">
                  <el-checkbox-group disabled class="form-checkbox-group">
                    <el-checkbox v-for="(option, optionIndex) in component.config.options" :key="optionIndex"
                      :label="option">
                      <div v-if="component.config.optionType === 'image' && option" >
                        <image-preview :src="imgUrl+option" width="140px" height="80px" />
                      </div>
                      <span v-else>{{ option }}</span>
                    </el-checkbox>
                  </el-checkbox-group>
                </div>
              </div>

              <!-- 组件操作按钮 -->
              <div class="component-actions" @click.stop>
                <!-- <el-button type="text" size="mini" @click.stop="duplicateComponent(index)" class="action-btn">
                  <i class="el-icon-plus"></i>
                </el-button> -->
                <el-button type="text" size="mini" @click.stop="duplicateComponent(index)" class="action-btn">
                  <i class="el-icon-document-copy"></i>
                </el-button>
                <el-button type="text" size="mini" @click.stop="deleteComponent(index)" class="action-btn delete-btn">
                  <i class="el-icon-delete"></i>
                </el-button>
              </div>
            </div>
          </draggable>
        </div>
      </div>

      <!-- 右侧属性配置面板 -->
      <div class="property-panel">
        <h3>组件属性</h3>
        <div v-if="activeComponent" class="property-content">
          <el-form label-width="80px" size="small">  
            
            <el-form-item label="是否必填">
              <el-switch v-model="activeComponent.config.isRequired" />
            </el-form-item> 
            
            <el-form-item label="标题">
              <el-input v-model="activeComponent.config.surveyContent" />
            </el-form-item>
            
            <el-form-item label="占位文案" v-if="activeComponent.type === 'input' || activeComponent.type === 'textarea'">
              <el-input v-model="activeComponent.config.sm" />
            </el-form-item>

            <el-form-item label="选项类型" v-if="activeComponent.type === 'radio' || activeComponent.type === 'checkbox'">
              <el-radio-group v-model="activeComponent.config.optionType" @change="handleOptionTypeChange">
                <el-radio label="text">文本选项</el-radio>
                <el-radio label="image">图片选项</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="选项" v-if="activeComponent.type === 'radio' || activeComponent.type === 'checkbox'">
              <div class="options-tabs"> 
                <div class="options-list">
                  <draggable v-model="activeComponent.config.options" class="options-draggable">
                    <div v-for="(option, index) in activeComponent.config.options" :key="index" class="option-item">
                      <div class="drag-handle-small">
                        <span></span>
                        <span></span>
                        <span></span>
                      </div>
                      
                      <!-- 文本选项 -->
                      <el-input 
                        v-if="activeComponent.config.optionType === 'text'" 
                        v-model="activeComponent.config.options[index]" 
                        size="mini" 
                      />
                      
                      <!-- 图片选项 -->
                      <div v-else-if="activeComponent.config.optionType === 'image'" class="image-option">
                        <image-upload v-model="activeComponent.config.options[index]" :limit="1" />
                      </div>
                      
                      <el-button type="text" size="mini" @click="removeOption(index)"
                        :disabled="activeComponent.config.options.length <= 1" class="delete-option-btn">
                        <i class="el-icon-delete"></i>
                      </el-button>
                    </div>
                  </draggable>
                </div>
                <div class="options-actions">
                  <el-button type="text" size="mini" @click="addOption" class="action-link">
                    <i class="el-icon-plus"></i> 添加选项
                  </el-button>
                </div>
              </div>
            </el-form-item>
          </el-form>
        </div>
        <div v-else class="no-selection">
          请选择一个组件进行配置
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import draggable from 'vuedraggable'

export default {
  name: "wjBianji",
  props: {
    sonList: {
      type: Array,
      default: () => []
    },
    imgUrl: {
      type: String,
      default: process.env.VUE_APP_IMAGE_BASE_API
    }
  },
  components: {
    draggable,
    ComponentRenderer: {
      props: ['component', 'config', 'preview'],
      data() {
        return {
          inputValue: '',
          textareaValue: '',
          radioValue: '',
          checkboxValue: [],
          selectValue: '', 
        }
      },
      mounted() {
        // 为单选和多选组件设置默认选中值，以便立即看到效果
        if (this.component.type === 'radio' && this.config.options && this.config.options.length > 0) {
          this.radioValue = this.config.options[0];
        }
        if (this.component.type === 'checkbox' && this.config.options && this.config.options.length > 0) {
          // 根据组件在列表中的位置设置不同的选中状态，模拟真实使用场景
          const componentIndex = this.$parent.questionnaireComponents.findIndex(c => c.id === this.component.id);
          if (componentIndex % 2 === 0) {
            this.checkboxValue = [this.config.options[0]]; // 第一个选项
          } else {
            this.checkboxValue = [this.config.options[1]]; // 第二个选项
          }
        }
      },
      watch: {
        // 监听配置变化，更新选中状态
        'config.options': {
          handler(newOptions) {
            if (this.component.type === 'radio' && newOptions && newOptions.length > 0) {
              this.radioValue = newOptions[0];
            }
          },
          deep: true
        }
      }
    }
  },
  data() {
    return {
      questionnaireTitle: '测试',
      questionnaireDescription: '感谢您能抽出几分钟时间来参加本次表单填写,现在我们就马上开始吧!',
      componentList: [
        { type: 'input', label: '单行文本', icon: 'el-icon-edit' },
        { type: 'textarea', label: '多行文本', icon: 'el-icon-document' },
        { type: 'radio', label: '单选框组', icon: 'el-icon-circle-check' },
        { type: 'checkbox', label: '多选框组', icon: 'el-icon-check' },
      ],
      questionnaireComponents: [],
      activeComponentIndex: -1,
      previewVisible: false,
      componentCounter: 0,
      activeTab: 'static',
      uploadUrl: process.env.VUE_APP_BASE_API + '/common/upload'
    };
  },
  computed: {
    activeComponent() {
      if (this.activeComponentIndex >= 0 && this.activeComponentIndex < this.questionnaireComponents.length) {
        return this.questionnaireComponents[this.activeComponentIndex];
      }
      return null;
    }
  },
  watch: {
    // 监听 sonList 变化，自动回显
    sonList: {
      handler(newVal) {
        if (newVal && newVal.length > 0) {
          this.showQuestionnaire();
        }
      },
      deep: true,
      immediate: false
    }
  },
  mounted() {
    // 如果有 sonList 数据，进行回显
    if (this.sonList && this.sonList.length > 0) {
      this.showQuestionnaire();
    }
  },
  beforeDestroy() {

  },
  methods: {
    // 克隆组件
    cloneComponent(component) {
      this.componentCounter++;
      const clonedComponent = {
        ...component,
        id: `${component.type}${Date.now()}`,
        config: JSON.parse(JSON.stringify(this.getDefaultConfig(component.type)))
      };
      return clonedComponent;
    },
    // 获取默认配置
    getDefaultConfig(type) {
      const configs = {
        input: {
          surveyContent: '单行文本',
          sm: '请输入单行文本',
          isRequired: false, 
          answeringMethod: '3',
          id: '', 
        },
        textarea: {
          surveyContent: '多行文本',
          sm: '请输入内容',
          isRequired: false, 
          answeringMethod: '2',
          id: '', 
        },
        radio: {
          surveyContent: '单选框组',
          isRequired: false,  
          options: ['选项一', '选项二'],
          optionType: 'text',
          answeringMethod: '0',
          id: '', 
        },
        checkbox: {
          surveyContent: '多选框组',
          isRequired: false,  
          options: ['选项一', '选项二'],
          optionType: 'text',
          answeringMethod: '1',
          id: '', 
        },
      };
      return configs[type] || {};
    },
    // 选择组件
    selectComponent(index) {
      this.activeComponentIndex = index;
    },
    // 克隆组件
    duplicateComponent(index) {
      const component = this.questionnaireComponents[index];
      const duplicatedComponent = {
        ...component,
        id: `${component.type}${Date.now()}`,
        config: JSON.parse(JSON.stringify(component.config))
      };
      this.questionnaireComponents.splice(index + 1, 0, duplicatedComponent);
      this.activeComponentIndex = index + 1;
    },
    // 删除组件
    deleteComponent(index) {
      this.questionnaireComponents.splice(index, 1);
      if (this.activeComponentIndex === index) {
        this.activeComponentIndex = -1;
      } else if (this.activeComponentIndex > index) {
        this.activeComponentIndex--;
      }
    },
    // 更新组件配置
    updateComponentConfig(index, config) {
      this.questionnaireComponents[index].config = { ...config };
    },
    // 添加选项
    addOption() {
      if (this.activeComponent && this.activeComponent.config.options) {
        if (this.activeComponent.config.optionType === 'image') {
          this.activeComponent.config.options.push('');
        } else {
          this.activeComponent.config.options.push(`选项${this.activeComponent.config.options.length + 1}`);
        }
      }
    },
    // 删除选项
    removeOption(index) {
      if (this.activeComponent && this.activeComponent.config.options.length > 1) {
        this.activeComponent.config.options.splice(index, 1);
      }
    },
    // 设置默认值
    setDefaultValue() {
      this.$message.info('设置默认值功能');
    },
    // 清除默认值
    clearDefaultValue() {
      this.$message.info('清除默认值功能');
    },
    // 批量编辑
    batchEdit() {
      this.$message.info('批量编辑功能');
    },
    // 添加其他
    addOther() {
      if (this.activeComponent && this.activeComponent.config.options) {
        this.activeComponent.config.options.push('其他');
      }
    },
    
    // 设置排列方式
    setArrangement(type) {
      if (this.activeComponent) {
        this.activeComponent.config.arrangement = type;
      }
    },
    // 拖拽开始
    dragStart(evt) {
      console.log('拖拽开始', evt);
      // 拖拽开始时的处理
    },

    dragEnd(evt) {
      console.log('拖拽结束', evt);
      // 拖拽结束时的处理
    },
    // 保存至模板
    saveToTemplate() {
      if (this.questionnaireComponents.length > 0) {
        this.$emit('save', this.questionnaireComponents);
      } else {
        this.$message.warning('请完善问卷内容');
      }
    },
    //清空问卷
    clearQuestionnaire() {
      this.questionnaireComponents = [];
    },
    //回显问卷
    showQuestionnaire() {
      console.log('回显数据:', this.sonList);
      
      if (!this.sonList || this.sonList.length === 0) {
        return;
      }
      
      // 清空现有组件
      this.questionnaireComponents = [];
      
      // 遍历 sonList 数据，转换为组件格式
      this.sonList.forEach((item, index) => {
        const component = this.convertSonListToComponent(item, index);
        if (component) {
          this.questionnaireComponents.push(component);
        }
      });
      this.$forceUpdate();
      console.log('回显完成，组件数量:', this.questionnaireComponents);
    },
    
    // 将 sonList 数据转换为组件格式
    convertSonListToComponent(item, index) {
      console.log('转换数据项:', item, 'isRequired:', item.isRequired, '类型:', typeof item.isRequired);
      // 根据 answeringMethod 判断组件类型
      let componentType = '';
      let componentConfig = {};
      
      switch (item.answeringMethod) {
        case '0': // 单选框组
          componentType = 'radio';
          const radioOptions = this.parseOptions(item.surveyResult);
          componentConfig = {
            surveyContent: item.surveyContent || '单选框组',
            isRequired: Boolean(item.isRequired),
            options: radioOptions,
            optionType: this.detectOptionType(radioOptions, item.optionType),
            answeringMethod: item.answeringMethod,
            sm: item.sm,
            id: item.id
          };
          break;
          
        case '1': // 多选框组
          componentType = 'checkbox';
          const checkboxOptions = this.parseOptions(item.surveyResult);
          componentConfig = {
            surveyContent: item.surveyContent || '多选框组',
            isRequired: Boolean(item.isRequired),
            options: checkboxOptions,
            optionType: this.detectOptionType(checkboxOptions, item.optionType),
            answeringMethod: item.answeringMethod,
            sm: item.sm,
            id: item.id
          };
          break;
          
        case '2': // 多行文本
          componentType = 'textarea';
          componentConfig = {
            surveyContent: item.surveyContent || '多行文本',
            sm: item.sm || '请输入内容',
            isRequired: Boolean(item.isRequired),
            answeringMethod: item.answeringMethod, 
            id: item.id
          };
          break;
          
        case '3': // 单行文本
          componentType = 'input';
          componentConfig = {
            surveyContent: item.surveyContent || '单行文本',
            sm: item.sm || '请输入单行文本',
            isRequired: Boolean(item.isRequired),
            answeringMethod: item.answeringMethod, 
            id: item.id
          };
          break;
          
        default: 
          return null;
      }
      
      // 创建组件对象
      const component = {
        id: `${componentType}_${Date.now()}_${index}`,
        type: componentType,
        label: this.getComponentLabel(componentType),
        icon: this.getComponentIcon(componentType),
        config: componentConfig
      }; 
      return component;
    },
    
    // 解析选项字符串为数组
    parseOptions(optionsStr) {
      if (!optionsStr) {
        return ['选项一', '选项二'];
      }
      
      // 如果选项字符串包含"、"分隔符，按分隔符分割
      if (optionsStr.includes('、')) {
        return optionsStr.split('、').filter(option => option.trim());
      }
      
      // 如果选项字符串包含","分隔符，按分隔符分割
      if (optionsStr.includes(',')) {
        return optionsStr.split(',').filter(option => option.trim());
      }
      
      // 如果没有分隔符，返回原字符串作为单个选项
      return [optionsStr];
    },
    
    // 获取组件标签
    getComponentLabel(type) {
      const labels = {
        input: '单行文本',
        textarea: '多行文本',
        radio: '单选框组',
        checkbox: '多选框组'
      };
      return labels[type] || type;
    },
    
    // 获取组件图标
    getComponentIcon(type) {
      const icons = {
        input: 'el-icon-edit',
        textarea: 'el-icon-document',
        radio: 'el-icon-circle-check',
        checkbox: 'el-icon-check'
      };
      return icons[type] || 'el-icon-document';
    },

    // 处理选项类型变化
    handleOptionTypeChange(newType) {
      if (this.activeComponent && this.activeComponent.config.options) {
        // 转换现有选项格式
        const convertedOptions = this.activeComponent.config.options.map((option, index) => { 
          if (newType === 'image') {
            // 转换为图片选项格式
            return '';
          } else {
            // 转换为文本选项格式
            return `选项${index + 1}`;
          }
        }); 
        this.activeComponent.config.options = convertedOptions;
      }
    },
    // 获取选项值（用于表单绑定）
    getOptionValue(option, optionType) {
      if (optionType === 'image' && typeof option === 'object') {
        return option.image || option.text || '';
      }
      return option;
    },

    // 获取选项显示文本
    getOptionDisplayText(option, optionType) {
      if (optionType === 'image' && typeof option === 'object') {
        return option.text || '图片选项';
      }
      return option;
    },

    // 检测选项类型
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
    }
  },
};
</script>

<style lang="scss" scoped>
::v-deep .form-radio-group .el-radio{
  display: flex;
}
::v-deep .form-checkbox-group .el-checkbox{
  display: flex;
}
::v-deep .el-checkbox__input.is-disabled .el-checkbox__inner {
  background-color: #fff;
  border-color: #dcdfe6;
  cursor: pointer;
}

::v-deep .el-checkbox__input.is-disabled+span.el-checkbox__label {
  color: #606266;
  cursor: pointer;
}

::v-deep .el-button+.el-button {
  margin-left: 0;
}

::v-deep .el-form-item__label {
  font-weight: 500;
  color: #303133;
}

::v-deep .el-input__inner {
  border-radius: 6px;
}

::v-deep .el-switch__core {
  border-radius: 12px;
}

.form-radio-group,.el-checkbox-group{
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.questionnaire-builder {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;

  .toolbar {
    padding: 15px 20px;
    background: #fff;
    border-bottom: 1px solid #e4e7ed;
    display: flex;
    justify-content: space-between;
    align-items: center;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    .toolbar-left {
      display: flex;
      align-items: center;
      gap: 10px;
      
      .auto-save {
        display: flex;
        align-items: center;
        color: #67c23a;
        font-size: 14px;
        font-weight: 500;
        
        i {
          margin-right: 8px;
          font-size: 16px;
        }
      }
    }

    .toolbar-right {
      display: flex;
      gap: 10px;
    }
  }

  .main-content {
    flex: 1;
    display: flex;
    overflow: hidden;

    .component-library {
      width: 280px;
      background: #fff;
      border-right: 1px solid #e4e7ed;
      padding: 20px;
      box-shadow: 2px 0 4px rgba(0, 0, 0, 0.1);

      h3 {
        margin: 0 0 20px 0;
        color: #303133;
        font-size: 16px;
        font-weight: 600;
      }

      .component-list {
        .component-item {
          display: flex;
          align-items: center;
          padding: 15px 12px;
          margin-bottom: 10px;
          background: #fff;
          border: 1px solid #e4e7ed;
          border-radius: 8px;
          cursor: move;
          transition: all 0.3s;
          font-size: 14px;

          &:hover {
            border-color: #409eff;
            box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
            transform: translateY(-1px);
          }

          i {
            margin-right: 10px;
            color: #409eff;
            font-size: 16px;
          }
        }
      }
    }

    .design-area {
      flex: 1;
      background: #fff;
      display: flex;
      flex-direction: column;
      margin: 20px;
      border-radius: 12px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      overflow: hidden;

      .questionnaire-container {
        flex: 1;
        overflow-y: auto;
        padding: 30px;
        // background: #fafbfc;

        .questionnaire-header {
          margin-bottom: 40px;
          text-align: center;
          padding: 30px;
          background: #fff;
          border-radius: 12px;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

          .questionnaire-title {
            font-size: 28px;
            color: #303133;
            margin: 0 0 20px 0;
            font-weight: 600;
          }

          .questionnaire-description {
            color: #606266;
            font-size: 16px;
            margin: 0;
            line-height: 1.6;
          }
        }

        .design-container {
          min-height: 100%;
          user-select: none;

          .design-component {
            margin-bottom: 25px;
            border: 1px solid #e4e7ed;
            border-radius: 12px;
            background: #fff;
            transition: all 0.3s;
            display: flex;
            user-select: none;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

            &.active {
              border-color: #409eff;
              box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2), 0 4px 12px rgba(64, 158, 255, 0.15);
              background: #f0f9ff;
            }

            .drag-handle {
              width: 30px;
              display: flex;
              align-items: center;
              justify-content: center;
              background: #f5f7fa;
              border-right: 1px solid #e4e7ed;
              border-radius: 8px 0 0 8px;
              cursor: move;
              user-select: none;

              &:hover {
                background: #e4e7ed;
              }

              .drag-dots {
                display: flex;
                flex-direction: column;
                gap: 2px;

                span {
                  width: 4px;
                  height: 4px;
                  background: #909399;
                  border-radius: 50%;
                }
              }
            }

            .component-main {
              flex: 1;
              padding: 20px;
              user-select: none;

              .component-label {
                margin-bottom: 15px;
                display: flex;
                align-items: center;

                .required-mark {
                  color: #f56c6c;
                  margin-right: 5px;
                  font-weight: bold;
                }

                .label-text {
                  font-size: 16px;
                  color: #303133;
                  font-weight: 500;
                }
              }

              .component-content {
                margin-bottom: 15px;

                .component-renderer {

                  .input-component,
                  .textarea-component,
                  .select-component {

                    .el-input,
                    .el-select {
                      width: 100%;
                    }
                  }

                  .radio-component,
                  .checkbox-component {

                    .radio-group,
                    .checkbox-group {
                      &.horizontal {

                        .el-radio-group,
                        .el-checkbox-group {
                          display: flex;
                          gap: 20px;
                        }
                      }

                      &.vertical {

                        .el-radio-group,
                        .el-checkbox-group {
                          display: flex;
                          flex-direction: column;
                          gap: 10px;
                        }
                      }
                    }
                  }

                  // 表单组件样式优化
                  .form-input,
                  .form-textarea,
                  .form-select {
                    .el-input__inner {
                      border-radius: 6px;
                      border: 1px solid #dcdfe6;
                      padding: 12px 15px;
                      font-size: 14px;
                      transition: all 0.3s;

                      &:focus {
                        border-color: #409eff;
                        box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
                      }

                      &::placeholder {
                        color: #c0c4cc;
                      }
                    }
                  }

                  .form-radio-group,
                  .form-checkbox-group {
                    display: flex;
                    flex-direction: row;
                    flex-wrap: wrap;
                    gap: 15px;

                    .el-radio,
                    .el-checkbox {
                      margin: 0;
                      padding: 10px 15px;
                      border: 1px solid #e4e7ed;
                      border-radius: 8px;
                      background: #fff;
                      transition: all 0.3s;
                      min-width: 100px;
                      text-align: center;
                      display: flex;
                      align-items: center;
                      justify-content: center;

                      &:hover {
                        border-color: #409eff;
                        box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
                      }

                      .el-radio__input,
                      .el-checkbox__input {
                        margin-right: 0;

                        .el-radio__inner,
                        .el-checkbox__inner {
                          border: 2px solid #dcdfe6;
                          border-radius: 50%;
                          width: 18px;
                          height: 18px;
                          transition: all 0.3s;

                          &:hover {
                            border-color: #409eff;
                          }
                        }

                        &.is-checked {
                          .el-radio__inner {
                            border-color: #409eff;
                            background: #409eff;

                            &::after {
                              background: #fff;
                              width: 6px;
                              height: 6px;
                            }
                          }

                          .el-checkbox__inner {
                            border-color: #409eff;
                            background: #409eff;

                            &::after {
                              border-color: #fff;
                            }
                          }
                        }
                      }

                      .el-radio__label,
                      .el-checkbox__label {
                        font-size: 14px;
                        color: #303133;
                        padding-left: 8px;
                        font-weight: 500;
                      }
                      
                      &.is-checked {
                        border-color: #409eff;
                        background: #f0f9ff;
                        box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
                      }
                    }
                  }

                  // 单选和多选组件的容器样式
                  .radio-group,
                  .checkbox-group {
                    padding: 10px 0;

                    &.horizontal {

                      .el-radio-group,
                      .el-checkbox-group {
                        display: flex;
                        flex-wrap: wrap;
                        gap: 20px;

                        .el-radio,
                        .el-checkbox {
                          margin: 0;
                          padding: 8px 12px;
                          border: 1px solid #e4e7ed;
                          border-radius: 6px;
                          background: #fff;
                          transition: all 0.3s;
                          min-width: 80px;
                          text-align: center;

                          &:hover {
                            border-color: #409eff;
                            box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
                          }

                          &.is-checked {
                            border-color: #409eff;
                            background: #f0f9ff;
                          }

                          .el-radio__input,
                          .el-checkbox__input {
                            margin-right: 0;
                          }

                          .el-radio__label,
                          .el-checkbox__label {
                            padding-left: 0;
                            font-size: 14px;
                          }
                        }
                      }
                    }

                    &.vertical {

                      .el-radio-group,
                      .el-checkbox-group {
                        display: flex;
                        flex-direction: column;
                        gap: 12px;

                        .el-radio,
                        .el-checkbox {
                          margin: 0;
                          padding: 8px 12px;
                          border: 1px solid #e4e7ed;
                          border-radius: 6px;
                          background: #fff;
                          transition: all 0.3s;

                          &:hover {
                            border-color: #409eff;
                            box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
                          }

                          &.is-checked {
                            border-color: #409eff;
                            background: #f0f9ff;
                          }

                          .el-radio__input,
                          .el-checkbox__input {
                            margin-right: 0;
                          }

                          .el-radio__label,
                          .el-checkbox__label {
                            padding-left: 0;
                            font-size: 14px;
                          }
                        }
                      }
                    }
                  }

                  // 确保选中状态在预览模式下也能正常显示
                  .form-radio-group,
                  .form-checkbox-group {

                    .el-radio.is-checked,
                    .el-checkbox.is-checked {

                      .el-radio__input,
                      .el-checkbox__input {

                        .el-radio__inner,
                        .el-checkbox__inner {
                          border-color: #409eff !important;
                          background: #409eff !important;
                        }
                      }
                    }
                  }
                }
              }

              .rich-text-toolbar {
                display: flex;
                gap: 10px;
                padding: 15px;
                background: #fafbfc;
                border-radius: 8px;
                flex-wrap: wrap;
                border: 1px solid #e4e7ed;
                margin-top: 10px;

                .toolbar-group {
                  display: flex;
                  gap: 2px;
                }
                
                .el-input {
                  .el-input__inner {
                    border: 1px solid #e4e7ed;
                    background: #fff;
                    border-radius: 6px;
                    padding: 12px 15px;
                    font-size: 14px;
                    
                    &:focus {
                      border-color: #409eff;
                      box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
                    }
                    
                    &::placeholder {
                      color: #c0c4cc;
                    }
                  }
                }
                
                .el-textarea {
                  .el-textarea__inner {
                    border: 1px solid #e4e7ed;
                    background: #fff;
                    border-radius: 6px;
                    padding: 12px 15px;
                    font-size: 14px;
                    min-height: 80px;
                    
                    &:focus {
                      border-color: #409eff;
                      box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
                    }
                    
                    &::placeholder {
                      color: #c0c4cc;
                    }
                  }
                }
              }
            }

            .component-actions {
              display: flex;
              flex-direction: column;
              align-items: center;
              gap: 8px;
              padding: 15px 10px;
              background: #f8f9fa;
              border-left: 1px solid #e4e7ed;
              border-radius: 0 12px 12px 0;
              user-select: none;

              .action-btn {
                width: 32px;
                height: 32px;
                padding: 0;
                border-radius: 6px;
                transition: all 0.3s;
                
                &:hover {
                  background: #e6f7ff;
                  color: #409eff;
                }
                
                &.delete-btn:hover {
                  background: #fff2f0;
                  color: #f56c6c;
                }

                i {
                  font-size: 14px;
                }
              }
            }
          }
        }
      }
    }

    .property-panel {
      width: 450px;
      background: #fff;
      border-left: 1px solid #e4e7ed;
      padding: 25px;
      box-shadow: -2px 0 4px rgba(0, 0, 0, 0.1);

      h3 {
        margin: 0 0 25px 0;
        color: #303133;
        font-size: 18px;
        font-weight: 600;
      }

      .property-content {
        .el-form-item {
          margin-bottom: 15px;
        }

        .default-value {
          display: flex;
          gap: 10px;
        }

                  .default-value-group {
          display: flex;
          align-items: center;
          gap: 8px;
          flex-wrap: wrap;
          
          .el-input {
            flex: 1;
            min-width: 120px;
          }
        }

        .arrangement-buttons {
          display: flex;
          gap: 10px;
          
          .el-button {
            flex: 1;
            border-radius: 6px;
          }
        }

        .options-tabs {
          .tab-header {
            display: flex;
            gap: 10px;
            margin-bottom: 10px;
            border-bottom: 1px solid #e4e7ed;
            padding-bottom: 5px;

            .tab-item {
              padding: 5px 10px;
              border-radius: 4px;
              cursor: pointer;
              font-size: 14px;
              color: #606266;
              transition: all 0.3s;

              &.active {
                background-color: #409eff;
                color: #fff;
                font-weight: bold;
              }
            }
          }
                        .options-list {
              margin-bottom: 15px;
              background: #fafbfc;
              border-radius: 8px;
              padding: 15px;
              border: 1px solid #e4e7ed;

              .options-draggable {
                .option-item {
                  display: flex;
                  align-items: center;
                  margin-bottom: 10px;
                  gap: 10px;
                  padding: 10px;
                  background: #fff;
                  border-radius: 6px;
                  border: 1px solid #e4e7ed;
                  transition: all 0.3s;

                  &:hover {
                    border-color: #409eff;
                    box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
                  }

                  &:last-child {
                    margin-bottom: 0;
                  }

                  .drag-handle-small {
                    display: flex;
                    flex-direction: column;
                    gap: 1px;
                    cursor: move;
                    padding: 5px;
                    border-radius: 4px;
                    transition: all 0.3s;

                    &:hover {
                      background: #f0f0f0;
                    }

                    span {
                      width: 3px;
                      height: 3px;
                      background: #909399;
                      border-radius: 50%;
                    }
                  }

                  .el-input {
                    flex: 1;
                    
                    .el-input__inner {
                      border: 1px solid #e4e7ed;
                      border-radius: 6px;
                      padding: 8px 12px;
                      font-size: 13px;
                      
                      &:focus {
                        border-color: #409eff;
                        box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
                      }
                    }
                  }

                  .delete-option-btn {
                    margin-left: 5px;
                    color: #f56c6c;
                    
                    &:hover {
                      background: #fff2f0;
                      color: #f56c6c;
                    }
                  }
                }
              }
            }

          .options-actions {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
            margin-top: 15px;
            padding-top: 15px;
            border-top: 1px solid #f0f0f0;
            
            .action-link {
              color: #409eff;
              font-size: 13px;
              padding: 0;
              height: auto;
              
              &:hover {
                color: #66b1ff;
                background: transparent;
              }
              
              i {
                margin-right: 4px;
              }
            }
          }

          // 图片选项样式
          .image-option {
            width: 100%;
            
            .option-image-upload {
              width: 100%;
              
              .image-upload-content {
                width: 100%;
                height: 80px;
                border: 1px dashed #d9d9d9;
                border-radius: 6px;
                display: flex;
                align-items: center;
                justify-content: center;
                cursor: pointer;
                transition: border-color 0.3s;
                
                &:hover {
                  border-color: #409eff;
                }
                
                .option-image {
                  max-width: 100%;
                  max-height: 100%;
                  object-fit: cover;
                  border-radius: 4px;
                }
                
                .image-placeholder {
                  display: flex;
                  flex-direction: column;
                  align-items: center;
                  color: #999;
                  font-size: 12px;
                  
                  i {
                    font-size: 20px;
                    margin-bottom: 5px;
                  }
                }
              }
            }
          }

          // 预览区域的图片选项样式
          .option-with-image {
            display: flex;
            align-items: center;
            gap: 8px;
            
            .option-preview-image {
              width: 40px;
              height: 40px;
              object-fit: cover;
              border-radius: 4px;
              border: 1px solid #e4e7ed;
            }
            
            .option-text {
              font-size: 14px;
              color: #303133;
            }
          }
        }
      }

      .no-selection {
        color: #909399;
        text-align: center;
        padding: 40px 0;
      }
    }
  }

  .preview-content {
    .questionnaire-header {
      margin-bottom: 30px;
      text-align: center;

      .questionnaire-title {
        font-size: 24px;
        color: #303133;
        margin: 0 0 15px 0;
        font-weight: 500;
      }

      .questionnaire-description {
        color: #606266;
        font-size: 14px;
        margin: 0;
        line-height: 1.6;
      }
    }

    .preview-component {
      margin-bottom: 20px;
      padding: 20px;
      border: 1px solid #e4e7ed;
      border-radius: 8px;
      background: #fff;
    }
  }
}
</style>
