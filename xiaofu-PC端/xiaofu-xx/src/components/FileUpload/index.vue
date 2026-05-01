<template>
  <div class="upload-file">
    <el-upload
      multiple
      :action="uploadFileUrl"
      :before-upload="handleBeforeUpload"
      :file-list="fileList"
      :limit="limit"
      :on-error="handleUploadError"
      :on-exceed="handleExceed"
      :on-success="handleUploadSuccess"
      :show-file-list="false"
      :headers="headers"
      class="upload-file-uploader"
      ref="fileUpload"
      v-if="!disabled"
    >
      <!-- 上传按钮 -->
      <el-button size="mini" type="primary">选择文件</el-button>
      <!-- 上传提示 -->
      <div
        style="display: inline-block; margin-left: 10px"
        class="el-upload__tip"
        slot="tip"
        v-if="showTip"
      >
        <slot name="template"></slot>

        上传
        <template v-if="fileType">
          <b style="color: #f56c6c">{{ fileType.join("/") }}</b>
        </template>
        文件
        <template v-if="gaizhang">
          <b style="color: #f56c6c">并加盖学校公章</b>
        </template>
        <template v-if="fileSize"
          > 大小不超过 <b style="color: #f56c6c">{{ fileSize }}MB</b>
        </template>
      </div>
    </el-upload>

    <!-- 文件列表 -->
    <transition-group
      class="upload-file-list el-upload-list el-upload-list--text"
      name="el-fade-in-linear"
      tag="ul"
    >
      <el-tooltip
        v-for="file in fileList"
        :key="file.url"
        :content="getFileName(file.name)"
        placement="top"
      >
        <li
          :key="file.url"
          class="el-upload-list__item ele-upload-list__item-content"
        >
          <div
            style="
              display: flex;
              align-items: center;
              justify-content: space-between;
              padding: 4px 10px;
            "
          >
            <span
              class="el-icon-document"
              style="
                max-width: 280px;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
              "
            >
              {{ getFileName(file.name) }}
            </span>
            <el-button
              style="margin-left: 20px"
              v-if="disabled && (getFileType(file) == 'pdf' || getFileType(file) == 'png' || getFileType(file) == 'jpg') "
              type="text"
              @click="handlePreview(file)"
              >预览</el-button
            >
            <el-button
              style="margin-left: 10px"
              v-if="disabled"
              type="text"
              @click="downloadFile(file.url)"
              >下载</el-button
            >
          </div>

          <div
            style="margin-right: 20px"
            class="ele-upload-list__item-content-action"
          >
            <el-link
              :underline="false"
              @click="handleDelete(index)"
              type="danger"
              v-if="!disabled"
              >删除</el-link
            >
          </div>
        </li>
      </el-tooltip>
    </transition-group>

    <!-- 文件预览弹窗 -->
    <el-dialog
      fullscreen
      title="文件预览"
      :visible.sync="previewVisible"
      width="80%"
      :before-close="handlePreviewClose"
      append-to-body
    >
      <div class="preview-container" style="height: 86vh">
        <iframe
          v-if="previewUrl"
          :src="previewUrl"
          width="100%"
          frameborder="0"
          style="border: none"
        ></iframe>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="previewVisible = false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { Base64 } from "js-base64";

import { getToken } from "@/utils/auth";

export default {
  name: "FileUpload",
  props: {
    // 值
    value: [String, Object, Array],
    // 数量限制
    limit: {
      type: Number,
      default: 5,
    },
    // 大小限制(MB)
    fileSize: {
      type: Number,
      default: 20,
    },
    // 文件类型, 例如['png', 'jpg', 'jpeg']
    fileType: {
      type: Array,
      default: () => [
        "doc",
        "docx",
        "xls",
        "xlsx",
        "ppt",
        "pptx",
        "txt",
        "pdf",
      ],
    },
    // 是否显示提示
    isShowTip: {
      type: Boolean,
      default: true,
    },
    // 禁用组件（仅查看文件）
    disabled: {
      type: Boolean,
      default: false,
    },
    gaizhang: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      previewVisible: false,
      previewUrl: "",
      number: 0,
      uploadList: [],
      uploadFileUrl: process.env.VUE_APP_BASE_API + "/file/upload", // 上传文件服务器地址
      headers: {
        Authorization: "Bearer " + getToken(),
      },
      fileList: [],
    };
  },
  watch: {
    value: {
      handler(val) {
        if (val) {
          let temp = 1;
          // 首先将值转为数组
          const list = Array.isArray(val) ? val : this.value.split(",");
          // 然后将数组转为对象数组
          this.fileList = list.map((item) => {
            if (typeof item === "string") {
              item = { name: item, url: item };
            }
            item.uid = item.uid || new Date().getTime() + temp++;
            return item;
          });
        } else {
          this.fileList = [];
          return [];
        }
      },
      deep: true,
      immediate: true,
    },
  },
  computed: {
    // 是否显示提示
    showTip() {
      return this.isShowTip && (this.fileType || this.fileSize);
    },
  },
  methods: {
    getFileUrl(filePath) {
      if (!filePath) return "";
      // 检查是否已经是完整URL
      if (filePath.startsWith("http://") || filePath.startsWith("https://")) {
        return filePath;
      }
      return (
        process.env.VUE_APP_BASE_API +
        "/common/download?fileName=" +
        encodeURIComponent(filePath) +
        "&delete=false"
      );
    },
    getFileName(filePath) {
      if (!filePath) return "";
      const parts = filePath.split("/");
      return parts[parts.length - 1];
    },
    downloadFile(filePath) {
      console.log(filePath);

      // 创建一个临时的a元素用于下载
      const link = document.createElement("a");
      link.href = process.env.VUE_APP_IMAGE_BASE_API + filePath;
      link.setAttribute("download", this.getFileName(filePath)); // 设置下载文件名
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
    //预览
    handlePreview(file) {
      // var b64Encoded = Base64.encode(
      //   process.env.VUE_APP_IMAGE_BASE_API + file.url
      // );
      this.previewUrl = process.env.VUE_APP_IMAGE_BASE_API + file.url
        // process.env.VUE_APP_KKFILE2 + encodeURIComponent(b64Encoded);
      this.previewVisible = true;
    },
    // 上传前校检格式和大小
    handleBeforeUpload(file) {
      // 校检文件类型
      if (this.fileType) {
        const fileName = file.name.split(".");
        const fileExt = fileName[fileName.length - 1];
        const isTypeOk = this.fileType.indexOf(fileExt) >= 0;
        if (!isTypeOk) {
          this.$modal.msgError(
            `文件格式不正确，请上传${this.fileType.join("/")}格式文件!`
          );
          return false;
        }
      }
      // 校检文件名是否包含特殊字符
      if (file.name.includes(",")) {
        this.$modal.msgError("文件名不正确，不能包含英文逗号!");
        return false;
      }
      // 校检文件大小
      if (this.fileSize) {
        const isLt = file.size / 1024 / 1024 < this.fileSize;
        if (!isLt) {
          this.$modal.msgError(`上传文件大小不能超过 ${this.fileSize} MB!`);
          return false;
        }
      }
      this.$modal.loading("正在上传文件，请稍候...");
      this.number++;
      return true;
    },
    // 文件个数超出
    handleExceed() {
      this.$modal.msgError(`上传文件数量不能超过 ${this.limit} 个!`);
    },
    // 上传失败
    handleUploadError(err) {
      this.$modal.msgError("上传文件失败，请重试");
      this.$modal.closeLoading();
    },
    // 上传成功回调
    handleUploadSuccess(res, file) {
      if (res.code === 200) {
        this.uploadList.push({ name: res.data.url, url: res.data.url }); 
        this.uploadedSuccessfully();
      } else {
        this.number--;
        this.$modal.closeLoading();
        this.$modal.msgError(res.msg);
        this.$refs.fileUpload.handleRemove(file);
        this.uploadedSuccessfully();
      }
    },
    // 删除文件
    handleDelete(index) {
      this.fileList.splice(index, 1);
      this.$emit("input", this.listToString(this.fileList));
    },
    // 上传结束处理
    uploadedSuccessfully() {
      if (this.number > 0 && this.uploadList.length === this.number) {
        this.fileList = this.fileList.concat(this.uploadList);
        this.uploadList = [];
        this.number = 0;
        this.$emit("input", this.listToString(this.fileList));
        this.$modal.closeLoading();
      }
    },
    // 获取文件名称
    getFileName(name) {
      // 如果是url那么取最后的名字 如果不是直接返回
      if (name.lastIndexOf("/") > -1) {
        return name.slice(name.lastIndexOf("/") + 1);
      } else {
        return name;
      }
    },
    // 对象转成指定字符串分隔
    listToString(list, separator) {
      let strs = "";
      separator = separator || ",";
      for (let i in list) {
        strs += list[i].url + separator;
      }
      return strs != "" ? strs.substr(0, strs.length - 1) : "";
    },
    // 关闭预览弹窗
    handlePreviewClose() {
      this.previewUrl = "";
      this.previewVisible = false;
    },
    //获取文件类型
    getFileType(file) {
      const fileName = file.name.split(".");
      const fileExt = fileName[fileName.length - 1]; 
      return fileExt;
    },
  },
};
</script>

<style scoped lang="scss">
.upload-file-uploader {
  margin-bottom: 5px;
}
.upload-file-list .el-upload-list__item {
  border: 1px solid #e4e7ed;
  line-height: 1;
  margin-bottom: 10px;
  position: relative;
}
.upload-file-list .ele-upload-list__item-content {
  display: flex;
  // justify-content: space-between;
  align-items: center;
  color: inherit;
}
.ele-upload-list__item-content-action .el-link {
  margin-right: 10px;
}

.preview-container {
  width: 100%;
  height: 600px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
}

.preview-container iframe {
  width: 100%;
  height: 100%;
  border: none;
}
</style>
