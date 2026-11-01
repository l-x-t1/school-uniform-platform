<template>
  <div class="detail-panel">
    <div class="header" style="display: flex; align-items: center">
      <h3>学校采购合同报备与公开</h3>
      <div style="margin-left: 20px">
        <el-button
          type="primary"
          icon="el-icon-plus"
          v-hasPermi="['xfpt:cghtbb-add']"
          @click="addHezuo"
          v-if="tableCG.length==0"
          >添加合作厂商</el-button
        >
      </div>
    </div>
    <el-table :data="tableCG" style="width: 100%; margin-top: 10px">
      <el-table-column label="序号" align="center" type="index" />
      <el-table-column
        label="厂商名称"
        align="center"
        prop="supplierName"
      ></el-table-column>
      <el-table-column
        label="法定代表人"
        align="center"
        prop="legalRepresentative"
      ></el-table-column>
      <el-table-column
        label="联系电话"
        align="center"
        prop="supplierPhone"
      ></el-table-column>
      <el-table-column align="center" label="操作">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="seeInfo(scope.row)"
            >查看厂商信息</el-button
          >
          <el-button type="text" size="small" @click="seeHetong(scope.row)"
            >查看合同</el-button
          >
          <el-button
            type="text"
            size="small"
            v-hasPermi="['xfpt:cghtbb-addht']"
            @click="handleEdit(scope.row)"
            v-if="contractList.length == 0"
            >添加采购合同</el-button
          >
          <el-button
            type="text"
            size="small"
            v-hasPermi="['xfpt:cghtbb-qxhz']"
            @click="handleCancel(scope.row)"
            >取消合作</el-button
          >
          <el-button
            type="text"
            size="small"
            v-hasPermi="['bc-gonggao']"
            @click="handleBcList(scope.row)"
            v-if="contractList.length > 0"
            >补充</el-button
          >
        </template>
      </el-table-column>
    </el-table>
    <!-- 合作厂商列表弹窗 -->
    <el-dialog
      title="新增合作厂商"
      :visible.sync="manufacturerDialogVisible"
      width="900px"
      append-to-body
    >
      <div >
        <el-form inline>
          <el-form-item label="">
            <el-input
              v-model="manufacturerQuery"
              placeholder="请输入厂商名称"
              style="width: 300px"
              class="filter-item"
              @keyup.enter.native="searchManufacturer"
            ></el-input
          ></el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchManufacturer"
              >查询</el-button
            >
            <el-button type="primary" @click="handleAddSuppier"
              >添加厂商</el-button
            >
          </el-form-item>
        </el-form>
      </div>
      <el-table :data="manufacturerList" border style="width: 100%">
        <el-table-column label="厂商名称" prop="supplierName" />
        <el-table-column label="注册资金(万元)" prop="registeredCapital">
          <template slot-scope="scope">
            {{ scope.row.registeredCapital }}
          </template>
        </el-table-column>
        <el-table-column label="联系电话" prop="supplierPhone">
          <template slot-scope="scope">
            {{ scope.row.supplierPhone.length > 7 ? scope.row.supplierPhone.slice(0, 3) + '****' + scope.row.supplierPhone.slice(7) : scope.row.supplierPhone.slice(0, 2) + '***' + scope.row.supplierPhone.slice(5) }}
          </template>
        </el-table-column>

        <el-table-column label="创建时间" prop="createTime" />
        <el-table-column label="账号绑定状态" align="center" width="180">
          <template slot-scope="scope">
            <el-tag :type="scope.row.hasUserBinding ? 'success' : 'info'" size="mini">
              {{ scope.row.hasUserBinding ? "已绑定" : "未绑定" }}
            </el-tag>
            <div
              v-if="scope.row.hasUserBinding && scope.row.bindUserNames"
              style="margin-top: 6px; color: #909399; font-size: 12px; line-height: 1.4"
            >
              {{ scope.row.bindUserNames }}
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" align="center" width="100">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              :disabled="!scope.row.hasUserBinding"
              @click="addManufacturer(scope.row)"
              >选择</el-button
            >
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 30, 50]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        ></el-pagination>
      </div>
    </el-dialog>
    <el-dialog
      title="厂商详情"
      :visible.sync="dialogChangshang"
      width="1000px"
      append-to-body
      custom-class="manufacturer-detail-dialog"
    >
      <div style="height: 700px; overflow: auto">
        <div class="manufacturer-header">
          <div class="manufacturer-logo">
            <el-avatar
              :size="80"
              icon="el-icon-s-custom"
              style="background-color: #409eff"
            ></el-avatar>
          </div>
          <div class="manufacturer-title">
            <h2>{{ csInfo.supplierName }}</h2>
            <div class="manufacturer-subtitle"></div>
          </div>
        </div>

        <el-divider><i class="el-icon-s-cooperation"></i> 基本信息</el-divider>

        <div class="info-grid">
          <div class="info-item">
            <span class="info-label"
              ><i class="el-icon-user"></i> 法定代表人：</span
            >
            <span class="info-value">{{ csInfo.legalRepresentative }}</span>
          </div>
          <div class="info-item">
            <span class="info-label"
              ><i class="el-icon-money"></i> 注册资金(万元)：</span
            >
            <span class="info-value">{{ csInfo.registeredCapital }} </span>
          </div>
          <div class="info-item">
            <span class="info-label"
              ><i class="el-icon-s-custom"></i> 职工人数：</span
            >
            <span class="info-value">{{ csInfo.staffNumber }}</span>
          </div>
          <div class="info-item">
            <span class="info-label"
              ><i class="el-icon-postcard"></i> 企业类型：</span
            >
            <span class="info-value">{{ csInfo.supplierType }}</span>
          </div>
          <div class="info-item">
            <span class="info-label"
              ><i class="el-icon-date"></i> 成立日期：</span
            >
            <span class="info-value">
              {{ csInfo.establishedTime }}
            </span>
          </div>

          <div class="info-item info-item-full">
            <span class="info-label"
              ><i class="el-icon-location"></i> 厂商地址：</span
            >
            <span class="info-value">{{ csInfo.supplierAddress }}</span>
          </div>
        </div>
        <el-divider><i class="el-icon-document"></i> 厂商简介</el-divider>
        <div class="manufacturer-description" style="padding: 10px;" v-html="csInfo.supplierIntroduce">
        </div>

        <el-divider><i class="el-icon-picture"></i> 厂商资质</el-divider>
        <div class="manufacturer-qualification">
          <FileUpload :disabled="true" v-model="csInfo.businessLicense" />
        </div>

        <el-divider v-if="csInfo.xypj != null"><i class="el-icon-star-on" ></i> 信用评级</el-divider>
        <div class="manufacturer-qualification">
          <FileUpload :disabled="true" v-model="csInfo.xypj" />
        </div>
      </div>

      <div class="dialog-footer" slot="footer">
        <el-button type="primary" @click="dialogChangshang = false"
          >关闭</el-button
        >
      </div>
    </el-dialog>
    <el-dialog
      title="采购合同报备"
      append-to-body
      :visible.sync="dialogVisibleHetong"
      width="60%"
    >
      <el-table :data="tableHetong" style="width: 100%">
        <el-table-column prop="contractName" label="合同名称">
        </el-table-column>
        <el-table-column
          prop="contractDeadline"
          label="合同服务期限"
          width="200"
        >
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              style="margin-right: 10px"
              @click="handlePreview(scope.row)"
              >预览</el-button
            >
            <a
              href="javascript:void(0);"
              style="
                color: #409eff;
                margin-right: 10px;
                text-decoration: none;
                font-size: 12px;
              "
              @click="handleView(scope.row)"
              >下载</a
            >
            <el-button type="text" size="small" @click="handleViewContract(scope.row)"
              >查看</el-button
            >
            <!-- <el-button type="text" size="small" @click="handleDelete(scope.row)"
              >删除</el-button
            > -->
          </template>
        </el-table-column>
      </el-table>

    </el-dialog>
    <el-dialog
      :title="dialogTitle"
      append-to-body
      :visible.sync="dialogVisibleHetongBB"
      width="1100px"
    >
      <el-form
        :rules="rulesForm"
        :model="addSupplierHetong"
        ref="addSupplierHetongRef"
        label-width="120px"
      >
        <el-row>
          <el-col :span="12">
            <el-form-item label="合同名称：" prop="contractName">
              <el-input
                style="width: 350px"
                placeholder="请输入合同名称"
                v-model="addSupplierHetong.contractName"
                :readonly="isViewMode"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="厂商负责人：" prop="fzr">
              <el-input
                style="width: 350px"
                placeholder="请输入厂商负责人"
                v-model="addSupplierHetong.fzr"
                :readonly="isViewMode"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="厂商联系方式：" prop="fzrdh">
              <el-input
                style="width: 350px"
                placeholder="请输入厂商联系方式"
                v-model="addSupplierHetong.fzrdh"
                :readonly="isViewMode"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="订购人数：" prop="count">
              <el-input
                type="number"
                style="width: 350px"
                placeholder="请输入订购人数"
                v-model="addSupplierHetong.count"
                :readonly="isViewMode"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="单价（元）：" prop="dj">
              <el-input
                type="number"
                style="width: 350px"
                placeholder="请输入单价"
                v-model="addSupplierHetong.dj"
                :readonly="isViewMode"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总价：" prop="zj">
              <el-input
                type="number"
                style="width: 350px"
                placeholder="请输入总价"
                v-model="addSupplierHetong.zj"
                :readonly="isViewMode"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="合同服务期限：" prop="contractDeadline">
              <el-date-picker
                style="width: 350px"
                @change="changeDate"
                v-model="addSupplierHetong.contractDeadline"
                type="daterange"
                value-format="yyyy-MM-dd"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                :readonly="isViewMode"
              >
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="采购年级（级）">
          <el-table
            border
            style="margin-top: 10px"
            :data="addSupplierHetong.sonDataList"
          >
            <el-table-column label="采购年级（级）" prop="content">
              <template slot="header">
                <span class="required-field">采购年级（级）</span>
              </template>
              <template slot-scope="scope">
                <el-form
                  :model="scope.row"
                  :ref="'cgnjForm' + scope.$index"
                  label-width="0px"
                >
                  <el-form-item
                    :rules="[
                      {
                        required: true,
                        message: '采购年级（级）不能为空',
                        trigger: 'blur',
                      },
                    ]"
                    prop="cgnj"
                  >
                    <el-select
                      disabled
                      @change="handleChangeCgnj(scope.row, scope.$index)"
                      v-model="scope.row.cgnj"
                      placeholder="请选择采购年级（级）"
                    >
                      <el-option
                        v-for="item in teamXGCQ"
                        :key="item.id"
                        :label="item.gradeClass"
                        :value="item.id"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </el-form>
              </template>
            </el-table-column>
            <el-table-column label="校服品类" prop="content">
              <template slot="header">
                <span class="required-field">校服品类</span>
              </template>
              <template slot-scope="scope">
                <el-form
                  :model="scope.row"
                  :ref="'xfplForm' + scope.$index"
                  label-width="0px"
                >
                  <el-form-item
                    prop="xfpl"
                    :rules="[
                      {
                        required: true,
                        message: '校服品类不能为空',
                        trigger: 'blur',
                      },
                    ]"
                  >
                    <el-select
                      disabled
                      placeholder="请选择校服品类"
                      v-model="scope.row.xfpl"
                      clearable
                    >
                      <el-option
                        v-for="item in dict.type.xfpl"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </el-form>
              </template>
            </el-table-column>
            <el-table-column label="每个学生购买套数" prop="content">
              <template slot="header">
                <span class="required-field">每个学生购买套数</span>
              </template>
              <template slot-scope="scope">
                <el-form
                  :model="scope.row"
                  :ref="'xsngmtsForm' + scope.$index"
                  label-width="0px"
                >
                  <el-form-item
                    :rules="[
                      {
                        required: true,
                        message: '每个学生购买套数不能为空',
                        trigger: 'blur',
                      },
                    ]"
                    prop="xsngmts"
                  >
                  <!-- calculateTotal -->
                    <el-input
                      readonly
                      placeholder="请输入学生购买套数"
                      v-model="scope.row.xsngmts"
                    ></el-input>
                  </el-form-item>
                </el-form>
              </template>
            </el-table-column>
            <el-table-column label="每套价格区间（元）" prop="content">
              <template slot="header">
                <span class="required-field">每套价格区间（元）</span>
              </template>
              <template slot-scope="scope">
                <el-form
                  :model="scope.row"
                  :ref="'jgqjForm' + scope.$index"
                  label-width="0px"
                >
                  <el-form-item
                    :rules="[
                      {
                        required: true,
                        message: '每套价格区间不能为空',
                        trigger: 'blur',
                      },
                    ]"
                    prop="jgqj"
                  >
                    <el-input
                      readonly
                      placeholder="请输入每套价格区间"
                      v-model="scope.row.jgqj"
                    ></el-input>
                  </el-form-item>
                </el-form>
              </template>
            </el-table-column>
            <el-table-column label="备注" prop="content">
              <template slot-scope="scope">
                <el-input
                  readonly
                  type="textarea"
                  placeholder="请填写备注"
                  v-model="scope.row.bz"
                ></el-input>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
        <el-form-item label="采购合同：" prop="url">

          <FileUpload :fileType="['pdf']" v-model="addSupplierHetong.url" :disabled="isViewMode">
            <el-button slot="template" type="text" @click.stop="importTemplate" v-if="!isViewMode"
              >下载模板</el-button
            >
          </FileUpload>
        </el-form-item>
        <el-form-item label="电子合同内容：" prop="electronicContent">
          <el-input
            type="textarea"
            :rows="6"
            placeholder="请输入电子合同内容"
            v-model="addSupplierHetong.electronicContent"
            :readonly="isViewMode"
          />
        </el-form-item>
        <el-form-item label="合同模板：" prop="templateFile">
          <FileUpload
            :fileType="['doc', 'docx', 'pdf']"
            v-model="addSupplierHetong.templateFile"
            :disabled="isViewMode"
          />
        </el-form-item>
        <el-form-item label="纸质合同图片：" prop="paperContractFiles">
          <FileUpload
            :fileType="['jpg', 'jpeg', 'png', 'pdf']"
            v-model="addSupplierHetong.paperContractFiles"
            :disabled="isViewMode"
          />
        </el-form-item>


      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisibleHetongBB = false">{{ isViewMode ? '关 闭' : '取 消' }}</el-button>
        <el-button type="primary" @click="addConfirm" v-if="!isViewMode">确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="新增厂商"
      :visible.sync="supplierDialog"
      width="1000px"
      append-to-body
    >
      <el-form
        ref="supplierForm"
        :model="supplierForm"
        :rules="supplierRules"
        label-width="140px"
      >
        <el-row>
          <el-col :span="12">
            <el-form-item label="厂商名称" prop="supplierName">
              <el-input
                v-model="supplierForm.supplierName"
                placeholder="请输入厂商名称"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="supplierPhone">
              <el-input
                v-model="supplierForm.supplierPhone"
                placeholder="请输入联系电话"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="法定代表人" prop="legalRepresentative">
              <el-input
                v-model="supplierForm.legalRepresentative"
                placeholder="请输入法定代表人"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职工人数" prop="staffNumber">
              <el-input
                v-model="supplierForm.staffNumber"
                placeholder="请输入职工人数"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="厂商地址" prop="supplierAddress">
              <el-input
                v-model="supplierForm.supplierAddress"
                placeholder="请输入厂商地址"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="注册资金(万元)" prop="registeredCapital">
              <el-input
                v-model="supplierForm.registeredCapital"
                placeholder="请输入注册资金，单位（万元）"
                @input="handleRegisteredCapitalInput"
                @blur="handleRegisteredCapitalBlur"
                maxlength="10"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="成立日期" prop="establishedTime">
              <el-date-picker
                style="width: 100%"
                clearable
                v-model="supplierForm.establishedTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择成立日期"
              >
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="企业类型" prop="supplierType">
              <el-input
                v-model="supplierForm.supplierType"
                placeholder="请输入企业类型"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="厂商介绍" prop="supplierIntroduce">
              <editor
                v-model="supplierForm.supplierIntroduce"
                :min-height="192"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="厂商资质" prop="businessLicense">
              <FileUpload
                v-model="supplierForm.businessLicense"
                :file-list="supplierForm.businessLicenseList"
                :limit="1"
                :file-type="['jpg', 'png', 'jpeg', 'pdf']"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="信用评级" prop="xypj">
              <FileUpload
                v-model="supplierForm.xypj"
                :file-list="supplierForm.xypjList"
                :limit="1"
                :file-type="['jpg', 'png', 'jpeg', 'pdf']"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="supplierSubmitForm">确 定</el-button>
        <el-button @click="supplierCancel">取 消</el-button>
      </div>
    </el-dialog>
    <!-- 文件预览弹窗 -->
    <el-dialog
      fullscreen
      title="预览"
      :visible.sync="previewVisible"
      :before-close="handlePreviewClose"
      append-to-body
    >
      <div class="preview-container" style="height: calc(100vh - 140px)">
        <iframe
          v-if="previewUrl && isImage == false"
          :src="previewUrl"
          width="100%"
          height="100%"
          frameborder="0"
          style="border: none"
        ></iframe>
        <div v-else>
          <image-preview
            v-for="v in previewUrl.split('&&&')"
            :src="baseUrl + v"
            :key="v"
            style="width: 100px; height: 100px; margin-right: 10px"
          ></image-preview>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="previewVisible = false">关 闭</el-button>
      </span>
    </el-dialog>

    <!-- 补充列表 -->
    <el-dialog
      title="补充列表"
      :visible.sync="bcListShow"
      width="1000px"
      append-to-body
    >
      <el-button
        type="primary"
        size="mini"
        icon="el-icon-plus"
        @click="handleBcAdd"
        v-hasPermi="['school:rwggzs:add']"
        style="margin-bottom: 10px"
        >新增</el-button
      >
      <el-table :data="bcList" border>
        <el-table-column label="标题" prop="title" align="center" />
        <el-table-column label="创建时间" prop="createTime" align="center" />
        <el-table-column label="操作" align="center">
          <template slot-scope="scope">
            <el-button
              type="primary"
              size="mini"
              icon="el-icon-edit"
              @click="handleBcUpdate(scope.row)"
              >查看</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 补充弹窗 -->
    <el-dialog
      title="补充公告"
      :visible.sync="bcShow"
      width="1300px"
      append-to-body
    >
      <el-form ref="bcForm" :model="bcForm" :rules="bcRules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input
            v-model="bcForm.title"
            placeholder="请输入标题"
            :readonly="!bcXgType"
          />
        </el-form-item>
        <el-form-item label="附件" prop="file">
          <file-upload
            v-model="bcForm.file"
            :fileList="bcForm.file"
            :disabled="!bcXgType"
            :limit="1"
            :fileType="['pdf']"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" v-if="bcXgType" @click="bcSubmitForm"
          >确 定</el-button
        >
        <el-button @click="bcCancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { download } from "@/utils/request";
import { getInfo } from "@/api/login";
import { getToken } from "@/utils/auth";
import { getTeamXGCQ, getCurrSchoolGradeList } from "@/api/cg/index";

import {
  supplierMessage,
  deleteSuppliceContractSon,
  editSuppliceContractSon,
  addSuppliceContractSon,
  cancelSupplierMessage,
  suppliceContractSon,
  addSupplieGYS,
  schoolSupplierBindList,
  getSupplierInfo,
} from "@/api/cg/cght";
import { addSupplierMessage } from "@/api/school/manufacturers";
import { buchong, getBcList } from "@/api/cg/rwggzs";
import { getXfptnotice } from "@/api/system/xfptnotice";
import { Base64 } from "js-base64";
export default {
  dicts: ["xfpl"],
  name: "PurchaseContract",
  props: {
    tableGS: {
      type: Array,
      default: () => [],
    },
    formId: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      teamXGCQ: [],
      // 合同列表数据
      contractList: [],

      baseUrl: process.env.VUE_APP_IMAGE_BASE_API,
      isImage: false,
      previewVisible: false,
      previewUrl: "",
      supplierDialog: false,
      // 表单参数
      supplierForm: {},
      // 表单校验
      supplierRules: {
        supplierName: [
          { required: true, message: "厂商名称不能为空", trigger: "blur" },
        ],
        supplierPhone: [
          { required: true, message: "联系电话不能为空", trigger: "blur" },
        ],
        legalRepresentative: [
          { required: true, message: "法定代表人不能为空", trigger: "blur" },
        ],
        supplierType: [
          { required: true, message: "企业类型不能为空", trigger: "blur" },
        ],
        staffNumber: [
          { required: true, message: "职工人数不能为空", trigger: "blur" },
        ],
        supplierAddress: [
          { required: true, message: "厂商地址不能为空", trigger: "blur" },
        ],
        registeredCapital: [
          { required: true, message: "注册资金不能为空", trigger: "blur" },
        ],
        establishedTime: [
          { required: true, message: "成立日期不能为空", trigger: "blur" },
        ],
        supplierIntroduce: [
          { required: true, message: "厂商介绍不能为空", trigger: "blur" },
        ],
        businessLicense: [
          { required: true, message: "厂商资质不能为空", trigger: "blur" },
        ]
      },
      rulesForm: {
        url: [
          {
            required: true,
            message: "请上传采购合同",
            trigger: "change",
          },
        ],
        contractName: [
          { required: true, message: "请输入合同名称", trigger: "blur" },
        ],
        contractDeadline: [
          { required: true, message: "请选择合同服务期限", trigger: "change" },
        ],


        fzr: [{ required: true, message: "请输入厂商负责人", trigger: "blur" }],
        fzrdh: [
          { required: true, message: "请输入厂商联系方式", trigger: "blur" },
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

              if (mobileRegex.test(value) || phoneRegex.test(value)) {
                callback();
              } else {
                callback(new Error('请输入正确的手机号或座机号'));
              }
            },
            trigger: "blur"
          },
        ],
        count: [{ required: true, message: "请输入订购人数", trigger: "blur" }],
        dj: [{ required: true, message: "请输入单价", trigger: "blur" }],
        zj: [{ required: true, message: "请输入总价", trigger: "blur" }],
      },
      fileListPic: [],
      upload: {
        // 设置上传的请求头部
        headers: { Authorization: "Bearer " + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/file/upload",
      },
      addSupplierHetong: {
        contractName: "",
        contractDeadline: null,
        contractDeadlineStr: "",
        fileType: 1,
        electronicContent: "",
        templateFile: "",
        paperContractFiles: "",
        count: "",
        dj: "",
        zj: "",
        sonDataList: [
          {
            cgnj: "",
            xfpl: "",
            xsngmts: "",
            jgqj: "",
            bz: "",
          },
        ],
      },
      radio: 1,
      tableHetong: [],
      formIdInfo: null,
      csInfo: {
        supplierName: "",
        supplierType: "",
        legalRepresentative: "",
        registeredCapital: "",
        staffNumber: "",
        establishedTime: "",
        turnoverTime: "",
      },
      tableCG: [],
      manufacturerDialogVisible: false,
      manufacturerList: [],
      manufacturerQuery: "",
      dialogChangshang: false,
      total: 0,
      currentPage: 1,
      pageSize: 10,
      pageNum: 1,
      totalPages: 0,
      formIdList: [],
      addSupplierInfo: {
        deptId: "",
        supplierId: "",
        supplierName: "",
        supplierPhone: "",
        deptName: "",
        teamMeetingId: "",
      },
      dialogVisibleHetongBB: false,
      dialogVisibleHetong: false,
      dialogTitle: "采购合同报备",
      isViewMode: false,

      // 补充公告相关数据
      bcList: [], // 补充列表
      bcShow: false, // 是否显示补充弹窗
      bcForm: {
        title: "",
        teamId: this.formId,
        catagory: "CGGG", // 采购合同补充
        file: "",
        id: null,
        type: "1",
      },
      bcRules: {
        title: [{ required: true, message: "请输入标题", trigger: "blur" }],
        file: [{ required: true, message: "请上传附件", trigger: "blur" }],
      },
      bcListShow: false, // 是否显示补充列表
      bcId: null, // 补充id
      bcXgType: true, // 是否修改补充
    };
  },
  watch: {
    dialogVisibleHetongBB(val) {
      if (!val) {
        // 关闭弹窗时重置表单和查看模式（延迟执行，避免与编辑逻辑冲突）
        this.$nextTick(() => {
          this.isViewMode = false;
          this.dialogTitle = "采购合同报备";
          this.resetForm();
        });
      }
    },
  },
  created() {
    this.schoolSupplierBindList();
    this.getTeamXGCQData();
    this.formIdInfo = this.formId;
    getTeamXGCQ(this.formId).then((response) => {
      this.formIdList = response.data;
    });
    // 查询合同列表
    this.getContractList();
  },

  methods: {
    // 查询合同列表
    getContractList() {
      // 查询所有合作厂商的合同列表
      if (this.tableCG && this.tableCG.length > 0) {
        // 查询所有合作厂商的合同，合并到一个列表中
        const contractPromises = this.tableCG.map(supplier => {
          return suppliceContractSon({
            suppliceContractId: supplier.id,
            deptId: supplier.deptId,
          }).then(res => res.rows || []).catch(() => []);
        });

        Promise.all(contractPromises).then(contractArrays => {
          // 合并所有合同列表
          this.contractList = contractArrays.flat();
        });
      } else {
        this.contractList = [];
      }
    },
    getTeamXGCQData() {
      getCurrSchoolGradeList().then((response) => {
        this.teamXGCQ = response.data;
      });
    },
    handleChangeCgnj(row, index) {
      // 选择器已经绑定了正确的值，这里不需要额外处理
    },

    importTemplate() {
      const link = document.createElement("a");
      link.href =
        process.env.VUE_APP_IMAGE_BASE_API +
        "/filestatics/download/合同模板.docx";
      link.download = "合同模板.docx";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
    // 取消按钮
    supplierCancel() {
      this.supplierDialog = false;
      this.supplierReset();
    },
    supplierSubmitForm() {
      this.$refs["supplierForm"].validate((valid) => {
        if (valid) {
          addSupplierMessage({
            ...this.supplierForm,
          }).then((response) => {
            this.$modal.msgSuccess("新增成功");
            this.supplierDialog = false;
            this.supplierMessage();
          });
        }
      });
    },
    // 表单重置
    supplierReset() {
      this.supplierForm = {
        teamMeetingId: this.formId,
        supplierName: null,
        supplierPhone: null,
        supplierType: null,
        legalRepresentative: null,
        turnoverTime: null,
        staffNumber: null,
        establishedTime: null,
        supplierAddress: null,
        supplierIntroduce: null,
        equipmentConditions: null,
        businessLicense: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null,
        registeredCapital: null,
        delFlag: null,
      };
      this.resetForm("supplierForm");
    },
    handleAddSuppier() {
      this.supplierReset()
      this.supplierDialog = true;

    },
    searchManufacturer() {
      this.supplierMessage();
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.supplierMessage();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.supplierMessage();
    },
    // 处理注册资金输入，只允许数字和小数点
    handleRegisteredCapitalInput(value) {
      // 只允许输入数字和小数点
      this.supplierForm.registeredCapital = value.replace(/[^\d.]/g, '');
    },
    // 处理注册资金失去焦点
    handleRegisteredCapitalBlur() {
      // 失去焦点时，如果输入为空，则清空
      if (this.supplierForm.registeredCapital === '') {
        this.supplierForm.registeredCapital = null;
      }
    },
    handleView(row) {
      download(
        "/school/suppliceContractSon/downloadZip",
        {
          url: row.url,
          fileName: row.fileName,
          contractName: row.contractName,
        },
        row.deptName + "_" + row.contractName + ".zip"
      );
    },


    handleViewContract(row) {
      // 查看合同信息
      this.isViewMode = true;
      this.dialogTitle = "查看采购合同";
      this.dialogVisibleHetongBB = true;
      // 先设置当前查看的合同数据，再重置表单（保持sonDataList）
      this.addSupplierHetong = { ...row };
      this.addSupplierHetong.sonDataList = [...row.teamMeetingSonList];
      this.addSupplierHetong.sonDataList.forEach((item,index) => {
        item.xsngmts = row.teamMeetingSonList[index].gmts;
      });
      this.resetFormForView();
    },

    handleDelete(row) {
      this.$confirm("确定要删除该合同记录吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          // 调用删除接口
          deleteSuppliceContractSon(row.id)
            .then((res) => {
              this.$message.success("删除成功");
              // 刷新合同列表
              this.seeHetong({
                id: row.suppliceContractId,
                deptId: row.deptId,
              });
            })
            .catch((error) => {
              this.$message.error("删除失败：" + (error.message || "未知错误"));
            });
        })
        .catch(() => {
          // 用户取消，不做操作
        });
    },
    // 日期范围选择
    changeDate(val) {
      if (val && val.length === 2) {
        // 使用value-format提供的格式化日期
        this.addSupplierHetong.contractDeadlineStr = val[0] + "至" + val[1];
      } else {
        this.addSupplierHetong.contractDeadlineStr = "";
      }
    },
    // 当前页变化
    handleCurrentChange(val) {
      this.pageNum = val;
      this.supplierMessage();
    },
    addConfirm() {
      // 设置文件类型
      this.addSupplierHetong.fileType = this.radio;

      this.$refs.addSupplierHetongRef.validate((valid) => {
        if (!valid) {
          this.$message.error("请完善表单信息");
          return;
        }

        // // 确认勾选
        // if (!this.addSupplierHetong.isPublic) {
        //   this.$message.error("请确认信息无误");
        //   return;
        // }

        // 确保contractDeadline字段有正确的值
        if (this.addSupplierHetong.contractDeadlineStr) {
          this.addSupplierHetong.contractDeadline =
            this.addSupplierHetong.contractDeadlineStr;
        }

        // 提交前处理采购年级数据，将ID转换为年级名称
        if (this.addSupplierHetong.sonDataList && this.addSupplierHetong.sonDataList.length > 0) {
          this.addSupplierHetong.sonDataList.forEach(item => {
            if (item.cgnj && typeof item.cgnj === 'number') {
              // 如果cgnj是数字（ID），则查找对应的年级名称
              const matchedGrade = this.teamXGCQ.find(grade => grade.id === item.cgnj);
              if (matchedGrade) {
                item.cgnj = matchedGrade.gradeClass;
              }
            }
          });
        }

        // 提交前重新计算总价
        // this.calculateTotal();



        // 判断是新增还是编辑
        const api = this.addSupplierHetong.id
          ? editSuppliceContractSon
          : addSuppliceContractSon;
        api(this.addSupplierHetong)
          .then((res) => {
            this.$message.success(
              this.addSupplierHetong.id ? "修改成功" : "添加成功"
            );
            this.dialogVisibleHetongBB = false;
            // 如果是在合同列表中编辑，则刷新合同列表
            if (this.dialogVisibleHetong) {
              this.seeHetong({
                id: this.addSupplierHetong.suppliceContractId,
                deptId: this.addSupplierHetong.deptId,
              });
            } else {
              this.schoolSupplierBindList();
            }
            this.dialogVisibleHetongBB = false;
          })
          .catch((error) => {
            this.$message.error(
              (this.addSupplierHetong.id ? "修改" : "添加") +
                "失败：" +
                (error.message || "未知错误")
            );
          });
      });
    },
    handleCancel(row) {
      this.$confirm("确定取消合作吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        cancelSupplierMessage(row.id).then(() => {
          this.$message.success("取消合作成功");
          this.$emit("handleGetProcessMenu");

          this.schoolSupplierBindList();
        });
      });
    },
    schoolSupplierBindList() {
      schoolSupplierBindList({
        teamMeetingId: this.formId,
      }).then((res) => {
        this.tableCG = res.rows;
        // 获取合作厂商列表后，重新查询合同列表
        this.getContractList();
      });
    },
    getInfo() {
      getInfo().then((res) => {
        this.addSupplierInfo.deptId = res.user.deptId;
        this.addSupplierInfo.deptName = res.user.dept.deptName;
      });
    },
    supplierMessage() {
      supplierMessage({
        pageNum: this.currentPage,
        pageSize: this.pageSize,
        supplierName: this.manufacturerQuery,
      }).then((res) => {
        this.manufacturerList = res.rows;
        this.total = res.total;
        this.totalPages = res.pages;
      });
    },
    addHezuo() {
      this.getInfo();
      this.manufacturerDialogVisible = true;
      this.supplierMessage();
    },
    /** 添加合作厂商 */
    addManufacturer(row) {
      if (!row.hasUserBinding) {
        this.$message.warning("该供应商未绑定账号，暂不可选择");
        return;
      }
      this.addSupplierInfo.supplierName = row.supplierName;
      this.addSupplierInfo.supplierPhone = row.supplierPhone;
      this.addSupplierInfo.supplierId = row.id;
      this.addSupplierInfo.teamMeetingId = this.formIdInfo;
      addSupplieGYS(this.addSupplierInfo).then(() => {
        this.$message.success("添加成功");
        this.schoolSupplierBindList();
        this.$emit("handleGetProcessMenu");
      });
      // 只关闭新增厂商弹窗，保持厂商列表弹窗打开
      this.manufacturerDialogVisible = false;
    },



    seeInfo(row) {
      this.dialogChangshang = true;
      getSupplierInfo(row.supplierId).then((res) => {
        this.csInfo = res.data;
        this.equipmentConditionsArray =
          this.csInfo.equipmentConditions.split(",");
      });
    },
    handleEdit(row) {
      // 编辑模式
      this.isViewMode = false;
      this.dialogTitle = "添加采购合同";
      // 重置表单
      this.resetForm();
      this.dialogVisibleHetongBB = true;
      this.addSupplierHetong.suppliceContractId = row.id;
      this.addSupplierHetong.title = row.supplierName;
    },
    // 重置表单
    resetForm() {
      this.$refs.addSupplierHetongRef &&
        this.$refs.addSupplierHetongRef.resetFields();
      this.addSupplierHetong = {
        suppliceContractId: this.addSupplierHetong
          ? this.addSupplierHetong.suppliceContractId
          : undefined,
        contractName: "",
        contractDeadline: null,
        contractDeadlineStr: "",
        isPublic: false,
        fileType: 1,
        electronicContent: "",
        templateFile: "",
        paperContractFiles: "",
        count: "",
        dj: "",
        zj: "",
        sonDataList: [
          ...this.formIdList.map((item) => ({
            cgnj: item.cgnj || "",
            xfpl: item.xfpl || "",
            xsngmts: item.xsngmts || "",
            jgqj: item.jgqj || "",
            bz: item.bz || "",
          })),
        ],
      };
    },
    // 编辑时的表单重置（保持sonDataList数据）
    resetFormForEdit() {
      this.$refs.addSupplierHetongRef &&
        this.$refs.addSupplierHetongRef.resetFields();
      // 编辑时不需要重新生成sonDataList，保持原有的数据
    },
    // 查看时的表单重置（保持sonDataList数据）
    resetFormForView() {
      this.$refs.addSupplierHetongRef &&
        this.$refs.addSupplierHetongRef.resetFields();
      // 查看时不需要重新生成sonDataList，保持原有的数据
    },
    seeHetong(row) {
      this.dialogVisibleHetong = true;
      suppliceContractSon({
        suppliceContractId: row.id,
        deptId: row.deptId,
      }).then((res) => {
        this.tableHetong = res.rows;
      });
    },
    //文件预览
    handlePreview(row) {

      this.previewUrl = process.env.VUE_APP_IMAGE_BASE_API + row.url
      this.previewVisible = true;
    },
    // 关闭预览弹窗
    handlePreviewClose() {
      this.previewUrl = "";
      this.previewVisible = false;
    },

    // 获取补充列表
    handleBcList(row) {
      let supplierId;
      if (row && row.id) {
        // 如果传入了具体的厂商信息
        supplierId = row.id;
      } else if (this.tableCG && this.tableCG.length > 0) {
        // 如果没有传入具体厂商，使用第一个合作厂商
        supplierId = this.tableCG[0].id;
      } else {
        this.$message.warning("没有可用的合作厂商");
        return;
      }

      const data = {
        id: supplierId,
        type: "1",
        catagory: "CGGG", // 采购合同补充
      };
      this.bcId = supplierId;
      getBcList(data).then((response) => {
        this.bcListShow = true;
        this.bcList = response.data;
      });
    },
    // 新增补充
    handleBcAdd() {
      this.bcReset();
      this.bcShow = true;
      this.bcXgType = true;
    },
    // 补充表单重置
    bcReset() {
      this.bcForm = {
        title: "",
        teamId: this.formId,
        catagory: "CGGG",
        file: "",
        id: this.bcId,
        type: "1",
      };
      this.resetForm("bcForm");
    },
    // 提交补充
    bcSubmitForm() {
      this.$refs["bcForm"].validate((valid) => {
        if (valid) {
          if (this.bcForm.id != null) {
            buchong(this.bcForm).then((response) => {
              this.$modal.msgSuccess("补充成功");
              this.bcShow = false;
              this.handleBcList({ id: this.bcId });
            });
          }
        }
      });
    },
    // 取消补充
    bcCancel() {
      this.bcShow = false;
      this.bcReset();
    },
    // 查看补充
    handleBcUpdate(row) {
      const id = row.id;
      this.bcReset();
      getXfptnotice(id).then((response) => {
        this.bcXgType = false;
        this.bcForm = response.data;
        this.bcForm.file = response.data.ggFile;
        this.bcShow = true;
      });
    },

  },
};
</script>

<style scoped>
::v-deep .el-upload-list--picture-card .el-upload-list__item {
  overflow: hidden;
  background-color: #fff;
  border: 1px solid #c0ccda;
  border-radius: 6px;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  width: 100px;
  height: 100px;
  margin: 0 8px 8px 0;
  display: inline-block;
}



::v-deep .el-upload--picture-card {
  /* margin-left: 110px; */
  width: 100px;
  height: 100px;
  line-height: 100px;
}

::v-deep .el-upload--picture-card i {
  font-size: 18px;
}
.header {
  margin-top: 0;
  margin-bottom: 15px;
  padding-bottom: 5px;
  border-bottom: 1px solid #ebeef5;
}
.detail-panel {
  padding: 20px 0;
}

.detail-panel h3 {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.manufacturer-detail-dialog {
  border-radius: 10px;
  overflow: hidden;
}

.manufacturer-header {
  display: flex;
  align-items: center;
  padding: 20px;
  background-color: #f5f7fa;
}

.manufacturer-logo {
  margin-right: 20px;
}

.manufacturer-title h2 {
  margin: 0;
  font-size: 24px;
}

.manufacturer-subtitle {
  margin-top: 10px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  padding: 20px;
}

.info-item {
  display: flex;
  align-items: center;
}

.info-label {
  font-weight: bold;
  margin-right: 10px;
}

.info-value {
  color: #666;
}

.manufacturer-description {
  padding: 10px;
}

.manufacturer-equipment {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 20px;
}

.manufacturer-qualification {
  padding: 10px;
}

.dialog-footer {
  text-align: right;
  padding: 20px;
}

::v-deep .el-dialog__body {
  padding: 10px 20px;
}

::v-deep .el-upload-dragger {
  width: 500px;
  height: 40px;
  /* margin-left: 110px; */
}
</style>
