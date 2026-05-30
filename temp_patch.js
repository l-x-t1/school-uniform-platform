const fs = require('fs');
const path = 'D:/校服项目/xiaofu-PC端/xiaofu-xx/src/views/jiaoyuju/supplierSettleAudit/index.vue';
let content = fs.readFileSync(path, 'utf-8');
let replaced = 0;
// Step 1
if(content.indexOf(`        <el-descriptions-item label="厂商简介" :span="2">
          {{ currentRow.supplierIntroduce || "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="审核状态">`) >= 0){ content = content.replace(`        <el-descriptions-item label="厂商简介" :span="2">
          {{ currentRow.supplierIntroduce || "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="审核状态">`,`        <el-descriptions-item label="厂商简介" :span="2">
          <div v-html="currentRow.supplierIntroduce || '-'" style="max-height:300px;overflow-y:auto"></div>
        </el-descriptions-item>
        <el-descriptions-item label="厂商资质" :span="2">
          <template v-if="businessLicenseFiles.length > 0">
            <div v-for="(file, idx) in businessLicenseFiles" :key="idx" class="attachment-item">
              <i :class="getFileIcon(file)"></i>
              <span class="attachment-name">{{ getFileName(file) }}</span>
              <el-button type="text" size="mini" icon="el-icon-download" @click="downloadFile(file)">下载</el-button>
              <el-button
                v-if="isPreviewable(file)"
                type="text"
                size="mini"
                icon="el-icon-view"
                @click="handlePreview(file)"
              >预览</el-button>
            </div>
          </template>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="信用评价" :span="2">
          {{ currentRow.xypj || "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="审核状态">`); replaced++; console.log('Step 1 OK'); } else { console.error('Step 1 NOT FOUND'); }
