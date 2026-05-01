<template>
  <div>
    <div style="border: 1px solid #ccc; margin-top: 10px">
      <!-- 工具栏 -->
      <Toolbar
        style="border-bottom: 1px solid #ccc"
        :editor="editor"
        :defaultConfig="toolbarConfig"
      />
      <!-- 编辑器 -->
      <Editor
        :style="styles"
        :defaultConfig="editorConfig"
        v-model="html"
        @onChange="onChange"
        @onCreated="onCreated"
      />
    </div>
  </div>
</template>

<script>
import { Editor, Toolbar } from "@wangeditor/editor-for-vue";
import { getToken } from "@/utils/auth";
import axios from "axios";
export default {
  name: "MyEditor",
  components: {
    Editor,
    Toolbar,
  },
  props: {
    /* 编辑器的内容 */
    value: {
      type: String,
    },
    /* 高度 */
    height: {
      type: Number,
      default: null,
    },
    /* 最小高度 */
    minHeight: {
      type: Number,
      default: null,
    },
    /* 只读 */
    readOnly: {
      type: Boolean,
      default: false,
    },
    // 上传文件大小限制(MB)
    fileSize: {
      type: Number,
      default: 400,
    },
    /* 类型（base64格式、url格式） */
    type: {
      type: String,
      default: "url",
    },
  },
  //监控传参，及时更新为新值
  watch: {
    value: {
      handler(val) {
        if (val !== this.html) {
          this.html = val === null ? "" : val;
        }
      },
      immediate: true,
    },
  },
  computed: {
    styles() {
      let style = {};
      if (this.minHeight) {
        style.minHeight = `${this.minHeight}px`;
      }
      if (this.height) {
        style.height = `${this.height}px`;
      }
      return style;
    },
  },
  data() {
    return {
      editor: null,
      currentValue: "",
      html: "",
      toolbarConfig: {
        // toolbarKeys: [ /* 显示哪些菜单，如何排序、分组 */ ],
        // excludeKeys: [ /* 隐藏哪些菜单 */ ],
      },
      showUpload: false, //是否显示上传对话框
      uploadUrl: process.env.VUE_APP_BASE_API + "/file/upload", // 上传的图片服务器地址

      headers: {
        Authorization: "Bearer " + getToken(),
        "Content-Type": "multipart/form-data",
      },
      editorConfig: {
        placeholder: "请输入内容你要上传的内容...",
        // 所有的菜单配置，都要在 MENU_CONF 属性下
        MENU_CONF: {
          // 配置上传图片
          uploadImage: {
            customUpload: this.update,
          },
          // 继续其他菜单配置...
          uploadVideo: {
            customUpload: this.update,
          },
        },
      },
    };
  },
  methods: {
    onCreated(editor) {
      this.editor = Object.seal(editor); // 【注意】一定要用 Object.seal() 否则会报错
    },
    onChange(editor) {
      this.$emit("input", this.html); // onChange 时获取编辑器最新内容
    },
    insertTextHandler() {
      const editor = this.editor;
      if (editor == null) return;
      editor.insertText(" hello ");
    },
    printEditorHtml() {
      const editor = this.editor;
      if (editor == null) return;
      console.log(editor.getHtml());
    },
    disableHandler() {
      const editor = this.editor;
      if (editor == null) return;
      editor.disable();
    },
    // 上传前校检格式和大小
    handleBeforeUpload(file) {
      console.log("上传前校验");
      // 校检文件大小
      if (this.fileSize) {
        const isLt = file.size / 1024 / 1024 < this.fileSize;
        if (!isLt) {
          this.$modal.msgError(`上传文件大小不能超过 ${this.fileSize} MB!`);
          return false;
        }
      }
      return true;
    },
    //自定义上传图片
    update(file, insertFn) {
      if (this.handleBeforeUpload(file)) {
        let formData = new FormData();
        formData.append("file", file);
        const loading = this.$loading({
          lock: true,
          text: "正在上传文件，请稍候...",
          spinner: "el-icon-loading",
          background: "rgba(0, 0, 0, 0.7)",
        });
        axios({
          method: "post",
          url: this.uploadUrl,
          data: formData,
          headers: this.headers,
        })
          .then((res) => {
            console.log(res);
            if (res.data.code == 200) {
              console.log("上传成功");
              let url = process.env.VUE_APP_IMAGE_BASE_API + res.data.data.url;
              insertFn(url);
            } else {
              this.$modal.msgError("插入失败");
            }
            loading.close();
          })
          .finally(() => {
            loading.close();
          });
      }
    },
  },
  mounted() {},
  beforeDestroy() {
    const editor = this.editor;
    if (editor == null) return;
    editor.destroy(); // 组件销毁时，及时销毁 editor ，重要！！！
  },
};
</script>

<style src="@wangeditor/editor/dist/css/style.css"></style>
