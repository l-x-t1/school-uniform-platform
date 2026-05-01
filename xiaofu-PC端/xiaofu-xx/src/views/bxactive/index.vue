<template>
  <div class="app-container">
    <!-- 顶部标题和操作按钮 -->
    <el-button size="small" style="position: fixed;right: 30px;z-index: 100;" type="primary" icon="el-icon-plus"
      @click="handleAdd">
      发布比选要求
    </el-button>

    <!-- 标签页导航 -->
    <el-tabs v-model="activeTab" type="card" @tab-click="handleTabClick">
      <el-tab-pane label="全部" name="all"></el-tab-pane>
      <el-tab-pane label="制作投票信息" name="createVote"></el-tab-pane>
      <el-tab-pane label="邀请家长投票" name="inviteParent"></el-tab-pane>
      <el-tab-pane label="投票结果自动公示" name="autoPublish"></el-tab-pane>
    </el-tabs>

    <!-- 搜索表单 -->
    <div class="search-container">
      <el-form :inline="true" :model="queryParams" label-width="140px" class="search-form">
        <el-form-item label="投票活动名称：">
          <el-input v-model="queryParams.voteName" placeholder="请输入投票活动名称进行搜索" clearable
            style="width: 240px;"></el-input>
        </el-form-item>
        <el-form-item label="上传资料截止时间：">
          <el-date-picker v-model="queryParams.uploadDeadline" type="date" placeholder="请选择日期"
            value-format="yyyy-MM-dd HH:mm:ss" style="width: 240px;"></el-date-picker>
        </el-form-item>
        <el-form-item label="投票截止时间：">
          <el-date-picker v-model="queryParams.voteDeadline" type="date" placeholder="请选择日期"
            value-format="yyyy-MM-dd HH:mm:ss" style="width: 240px;"></el-date-picker>
        </el-form-item>
        <el-form-item label="项目状态：" v-if="activeTab === 'all'">
          <el-select v-model="queryParams.status" placeholder="全部" style="width: 240px;">
            <el-option label="全部" value=""></el-option>
            <el-option label="待发布比选要求" value="1"></el-option>
            <el-option label="投票中" value="2"></el-option>
            <el-option label="已结束" value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button size="small" type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="tableData" border style="width: 100%">
      <el-table-column prop="voteName" label="投票活动名称" min-width="180"></el-table-column>
      <el-table-column prop="uploadDeadline" label="上传资料截止时间" min-width="180"></el-table-column>
      <el-table-column prop="voteDeadline" label="投票截止时间" min-width="180"></el-table-column>
      <el-table-column prop="status" label="项目状态" min-width="150">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center">
        <template slot-scope="scope">
          <el-button size="small" type="text" @click="handleEdit(scope.row)">编辑比选要求</el-button>
          <el-button size="small" type="text" @click="handleDelete(scope.row)">删除比选要求</el-button>
        </template>
      </el-table-column>
    </el-table>


    <!-- 分页 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加/编辑比选要求弹窗 -->
    <el-dialog title="发布比选要求" :visible.sync="addDialogVisible" width="1100px" :close-on-click-modal="false"
      custom-class="bx-dialog" @close="handleClose">
      <div class="dialog-container">
        <!-- 步骤条 -->
        <div class="custom-steps-container">
          <div class="custom-steps">
            <div v-if="active > 0" class="step-line-success" :style="{ width: active > 1 ? '85%' : '35%' }"></div>
            <div v-if="active === 1" class="step-line-active" :style="{ width: '35%', left: '48%' }"></div>

            <div class="custom-step" :class="{ 'is-active': active === 0, 'is-success': active > 0 }">
              <div class="step-icon">
                <i class="el-icon-check" v-if="active > 0"></i>
                <span v-else>1</span>
              </div>
              <div class="step-title">基本信息</div>
            </div>

            <div class="custom-step" :class="{ 'is-active': active === 1, 'is-success': active > 1 }">
              <div class="step-icon">
                <i class="el-icon-check" v-if="active > 1"></i>
                <span v-else>2</span>
              </div>
              <div class="step-title">校服要求及报价</div>
            </div>

            <div class="custom-step" :class="{ 'is-active': active === 2, 'is-success': active > 2 }">
              <div class="step-icon">
                <i class="el-icon-check" v-if="active > 2"></i>
                <span v-else>3</span>
              </div>
              <div class="step-title">联系人信息</div>
            </div>
          </div>
        </div>

        <!-- 基本信息表单 -->
        <div v-show="active === 0" class="form-container">
          <el-form :model="baseInfoForm" :rules="baseInfoRules" ref="baseInfoForm" label-width="160px" class="form">
            <el-form-item label="投票活动名称：" prop="voteName" class="form-item-line">
              <el-input v-model="baseInfoForm.voteName" placeholder="请输入投票活动名称" style="width: 500px;"></el-input>
            </el-form-item>
            <el-form-item label="适用年级/院系/专业：" prop="applyScope" class="form-item-line">
              <el-input type="textarea" v-model="baseInfoForm.applyScope" placeholder="请输入适用年级/院系/专业"
                style="width: 500px;" :autosize="{ minRows: 2, maxRows: 4 }" maxlength="200" show-word-limit></el-input>
            </el-form-item>
            <el-form-item label="投票活动介绍：" prop="voteDesc" class="form-item-line">
              <el-input type="textarea" v-model="baseInfoForm.voteDesc" placeholder="请输入投票活动介绍" style="width: 850px;"
                :autosize="{ minRows: 5, maxRows: 8 }" maxlength="500" show-word-limit></el-input>
            </el-form-item>
            <el-form-item label="参与厂商：" prop="manufacturer" class="form-item-line">
              <div class="manufacturer-container">
                <el-button type="primary" size="small" @click="showManufacturerDialog">邀请</el-button>
                <span class="hint-text">邀请厂商须知：最多25家厂商</span>
                <div class="selected-manufacturers" v-if="selectedManufacturers.length > 0">
                  <el-tag v-for="item in selectedManufacturers" :key="item.id" closable
                    @close="removeSelectedManufacturer(item)" class="manufacturer-tag">
                    {{ item.name }}
                  </el-tag>
                </div>
              </div>
            </el-form-item>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="上传资料截止时间：" prop="uploadDeadline" class="form-item-line">
                  <el-date-picker v-model="baseInfoForm.uploadDeadline" type="datetime" placeholder="请选择上传资料截止时间"
                    style="width: 220px;" value-format="yyyy-MM-dd HH:mm:ss"></el-date-picker>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="交易时间：" prop="tradeTime" class="form-item-line">
                  <el-date-picker v-model="baseInfoForm.tradeTime" type="datetime" placeholder="请选择交易时间"
                    style="width: 220px;" value-format="yyyy-MM-dd HH:mm:ss"></el-date-picker>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="投票规则说明：" prop="voteRule" class="form-item-line">
              <el-input type="textarea" v-model="baseInfoForm.voteRule" placeholder="请输入投票规则说明" style="width: 850px;"
                :autosize="{ minRows: 3, maxRows: 5 }" maxlength="200" show-word-limit></el-input>
            </el-form-item>
            <el-form-item label="其他说明：" class="form-item-line">
              <el-input type="textarea" v-model="baseInfoForm.otherDesc" placeholder="请输入其他说明" style="width: 850px;"
                :autosize="{ minRows: 3, maxRows: 5 }" maxlength="200" show-word-limit></el-input>
            </el-form-item>
            <div class="dialog-footer">
              <el-button type="primary" @click="nextStep(0)">暂存并下一步</el-button>
            </div>
          </el-form>
        </div>

        <!-- 校服要求及报价表单 -->
        <div v-show="active === 1" class="form-container">
          <el-form :model="requirementForm" :rules="requirementRules" ref="requirementForm" label-width="140px"
            class="form">
            <div class="requirements-section">
              <div class="section-header">
                <span class="section-title">校服要求</span>
              </div>

              <!-- 校服要求表格布局 -->
              <div class="requirements-table">
                <div class="requirements-header">
                  <div class="req-col req-col-type">校服品类</div>
                  <div class="req-col req-col-style">校服款式</div>
                  <div class="req-col req-col-quantity">购买套数/人</div>
                  <div class="req-col req-col-fabric">面料成分要求</div>
                  <div class="req-col req-col-action">操作</div>
                </div>

                <div v-for="(item, index) in requirementForm.requirements" :key="index" class="requirements-row">
                  <div class="req-col req-col-type">
                    <el-form-item label-width="0" :prop="'requirements[' + index + '].category'"
                      :rules="{ required: true, message: '请选择校服品类', trigger: 'change' }" class="no-margin">
                      <el-select v-model="item.category" placeholder="请选择" size="small">
                        <el-option label="夏季校服" value="summer"></el-option>
                        <el-option label="冬季校服" value="winter"></el-option>
                        <el-option label="运动服" value="sport"></el-option>
                      </el-select>
                    </el-form-item>
                  </div>
                  <div class="req-col req-col-style">
                    <el-form-item label-width="0" :prop="'requirements[' + index + '].style'"
                      :rules="{ required: true, message: '请选择校服款式', trigger: 'change' }" class="no-margin">
                      <el-select v-model="item.style" placeholder="请选择" size="small">
                        <el-option label="T恤" value="tshirt"></el-option>
                        <el-option label="衬衫" value="shirt"></el-option>
                        <el-option label="外套" value="coat"></el-option>
                      </el-select>
                    </el-form-item>
                  </div>
                  <div class="req-col req-col-quantity">
                    <el-form-item label-width="0" :prop="'requirements[' + index + '].quantity'"
                      :rules="{ required: true, message: '请输入套数', trigger: 'blur' }" class="no-margin">
                      <el-input-number v-model="item.quantity" :min="1" :max="10" size="small"
                        controls-position="right"></el-input-number>
                    </el-form-item>
                  </div>
                  <div class="req-col req-col-fabric">
                    <el-form-item label-width="0" :prop="'requirements[' + index + '].fabricRequirement'"
                      :rules="{ required: true, message: '请输入面料要求', trigger: 'blur' }" class="no-margin">
                      <el-input type="textarea" v-model="item.fabricRequirement" placeholder="请输入面料成分要求"
                        :autosize="{ minRows: 3, maxRows: 5 }" size="small"></el-input>
                    </el-form-item>
                  </div>
                  <div class="req-col req-col-action">
                    <el-button type="primary" icon="el-icon-plus" circle size="mini" @click="addRequirement"
                      v-if="index === 0"></el-button>
                    <el-button type="danger" icon="el-icon-minus" circle size="mini" @click="removeRequirement(index)"
                      v-if="index > 0"></el-button>
                  </div>
                </div>
              </div>
            </div>

            <div class="price-section">
              <div class="section-header">
                <span class="section-title">校服报价要求</span>
              </div>

              <el-form-item label="订购模式：" prop="orderMode" class="form-item-line">
                <el-radio-group v-model="requirementForm.orderMode">
                  <el-radio label="1">家长自行填写模式 (校园加盟店)</el-radio>
                  <el-radio label="2">家长自助模式 (校内职员)</el-radio>
                  <el-radio label="3">财政统购模式</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-row :gutter="40">
                <el-col :span="8">
                  <el-form-item label="男款建议报价：" class="form-item-line">
                    <el-input v-model="requirementForm.malePriceRecommend" placeholder="建议报价">
                      <template slot="append">元/人</template>
                    </el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="女款建议报价：" class="form-item-line">
                    <el-input v-model="requirementForm.femalePriceRecommend" placeholder="建议报价">
                      <template slot="append">元/人</template>
                    </el-input>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-form-item label="价格说明：" prop="priceDesc" class="form-item-line">
                <el-input type="textarea" v-model="requirementForm.priceDesc" placeholder="请输入价格说明"
                  style="width: 850px;" :autosize="{ minRows: 3, maxRows: 5 }" maxlength="200"
                  show-word-limit></el-input>
              </el-form-item>
            </div>

            <el-form-item class="dialog-footer">
              <el-button @click="prevStep">上一步</el-button>
              <el-button type="primary" @click="nextStep(1)">暂存并下一步</el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 联系人信息表单 -->
        <div v-show="active === 2" class="form-container">
          <el-form :model="contactForm" :rules="contactRules" ref="contactForm" label-width="140px" class="form">
            <div class="section-title">联系人信息</div>
            <el-form-item label="联系人：" prop="contactName" class="form-item-line">
              <el-input v-model="contactForm.contactName" placeholder="请输入联系人" style="width: 350px;"></el-input>
            </el-form-item>
            <el-form-item label="联系电话：" prop="contactPhone" class="form-item-line">
              <el-input v-model="contactForm.contactPhone" placeholder="请输入联系电话" style="width: 350px;"></el-input>
            </el-form-item>
            <el-form-item class="dialog-footer">
              <el-button @click="prevStep">上一步</el-button>
              <el-button type="primary" @click="submitForm">提交发布</el-button>
              <el-button @click="saveForm">暂存</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </el-dialog>

    <!-- 厂商选择对话框 -->
    <el-dialog title="邀请参加厂商" :visible.sync="manufacturerDialogVisible" width="1000px" append-to-body
      custom-class="manufacturer-dialog">
      <div class="dialog-search">
        <el-form :inline="true" class="search-form">
          <el-form-item label="厂商名称：">
            <el-input placeholder="请输入厂商名称" v-model="searchParams.manufacturerName" style="width: 200px;"
              clearable></el-input>
          </el-form-item>
          <el-form-item label="注册资本：">
            <el-select v-model="searchParams.region" placeholder="请选择" style="width: 150px;" clearable>
              <el-option label="全部" value=""></el-option>
              <el-option label="0-100万" value="0-100"></el-option>
              <el-option label="100-500万" value="100-500"></el-option>
              <el-option label="500万以上" value="500-"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="厂商规模：">
            <el-select v-model="searchParams.type" placeholder="请选择" style="width: 150px;" clearable>
              <el-option label="全部" value=""></el-option>
              <el-option label="大型企业" value="large"></el-option>
              <el-option label="中型企业" value="medium"></el-option>
              <el-option label="小型企业" value="small"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label-width="0">
            <el-button type="primary" size="small" icon="el-icon-search" @click="searchManufacturers">查询</el-button>
            <el-button size="small" icon="el-icon-refresh" @click="resetSearchForm">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      <el-table :data="manufacturerList" border style="width: 100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55">
        </el-table-column>
        <el-table-column prop="name" label="厂商名称" min-width="180"></el-table-column>
        <el-table-column prop="employees" label="厂商规模(人)" width="120" align="center"></el-table-column>
        <el-table-column prop="capital" label="注册资本(万元)" width="140" align="center"></el-table-column>
        <el-table-column prop="establishTime" label="成立时间" width="120" align="center"></el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag type="info">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <!-- <el-table-column label="操作" width="100" align="center">
          <template slot-scope="scope">
            <el-button type="primary" size="small" @click="selectManufacturer(scope.row)">选择</el-button>
          </template>
        </el-table-column> -->
      </el-table>
      <div class="pagination-container">
        <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange"
          :current-page="pageParams.pageNum" :page-sizes="[10, 20, 30, 50]" :page-size="pageParams.pageSize"
          layout="prev, pager, next, jumper, total" :total="total">
        </el-pagination>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="manufacturerDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSelectManufacturers">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listData, getDicData } from "@/api/common";

export default {
  name: "BxActive",
  components: {},
  data() {
    return {
      // 遮罩层
      loading: false,
      // 选中数组
      ids: [],
      // 表格数据
      tableData: [],
      // 总条数
      total: 0,
      // 当前激活的标签页
      activeTab: "all",
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        voteName: undefined,
        uploadDeadline: undefined,
        voteDeadline: undefined,
        status: undefined
      },
      // 添加/编辑弹窗可见性
      addDialogVisible: false,
      // 当前步骤
      active: 0,
      // 厂商弹窗
      manufacturerDialogVisible: false,
      // 厂商列表
      manufacturerList: [],
      // 选中的厂商
      selectedManufacturers: [],
      // 厂商查询参数
      searchParams: {
        manufacturerName: '',
        region: '',
        type: ''
      },
      // 厂商分页参数
      pageParams: {
        pageNum: 1,
        pageSize: 10
      },
      // 基本信息表单
      baseInfoForm: {
        voteName: '',
        applyScope: '',
        voteDesc: '校服承载着学校文化的深刻内涵，同时又承载着学校文化的浓厚底蕴。在校园里，校服是一道亮丽的风景线。在外，校服整齐个性的学生形象，彰显了学校的教学水平，提升了学校的形象。所以，我们将学校校服采购的决定权交给家长委员会，家长委员会通过参观考察、实物对比等形式对各企业进行了解调查，从企业规模、产品质量、业态能力等，综合服务水平，社会形象，从中挑选出若干家企业进行投票选型。希望各位家长参与投票，可结合家庭情况，帮助完成校服采购决策。',
        uploadDeadline: '',
        tradeTime: '',
        voteRule: '',
        otherDesc: ''
      },
      // 基本信息校验规则
      baseInfoRules: {
        voteName: [
          { required: true, message: '请输入投票活动名称', trigger: 'blur' },
          { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
        ],
        applyScope: [
          { required: true, message: '请输入适用年级/院系/专业', trigger: 'blur' }
        ],
        voteDesc: [
          { required: true, message: '请输入投票活动介绍', trigger: 'blur' }
        ],
        uploadDeadline: [
          { required: true, message: '请选择上传资料截止时间', trigger: 'change' }
        ],
        tradeTime: [
          { required: true, message: '请选择交易时间', trigger: 'change' }
        ],
        voteRule: [
          { required: true, message: '请输入投票规则说明', trigger: 'blur' }
        ]
      },
      // 校服要求表单
      requirementForm: {
        requirements: [
          {
            category: '',
            style: '',
            quantity: 1,
            fabricRequirement: '高档精梳棉混纺面料，舒适型好，透气性面料。(棉：兰精70%PCLF，兰精30％。面料密度：点击密度40S)，面料含：涤纶棉混纺面料 - 50%棉 50%聚酯纤维，运动面料 聚'
          }
        ],
        orderMode: '1',
        malePriceRecommend: '',
        femalePriceRecommend: '',
        priceDesc: ''
      },
      // 校服要求校验规则
      requirementRules: {
        orderMode: [
          { required: true, message: '请选择订购模式', trigger: 'change' }
        ],
        priceDesc: [
          { required: true, message: '请输入价格说明', trigger: 'blur' }
        ]
      },
      // 联系人表单
      contactForm: {
        contactName: '',
        contactPhone: ''
      },
      // 联系人校验规则
      contactRules: {
        contactName: [
          { required: true, message: '请输入联系人', trigger: 'blur' }
        ],
        contactPhone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ]
      }
    };
  },
  created() {
    this.getList();
    this.getDictData();
  },
  methods: {
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 正常情况下应该调用后端API
      // listData(this.queryParams, '/bxactive/list').then(response => {
      //   this.tableData = response.rows;
      //   this.total = response.total;
      //   this.loading = false;
      // });

      // 此处使用模拟数据
      setTimeout(() => {
        if (this.activeTab === 'all') {
          this.tableData = [
            {
              id: 1,
              voteName: "2023年秋季校服投票活动",
              uploadDeadline: "2023-08-15 23:59:59",
              voteDeadline: "2023-08-30 23:59:59",
              status: "1" // 待发布
            },
            {
              id: 2,
              voteName: "2023年冬季校服投票活动",
              uploadDeadline: "2023-10-15 23:59:59",
              voteDeadline: "2023-10-30 23:59:59",
              status: "2" // 投票中
            },
            {
              id: 3,
              voteName: "2024年春季校服投票活动",
              uploadDeadline: "2023-12-15 23:59:59",
              voteDeadline: "2023-12-30 23:59:59",
              status: "3" // 已结束
            }
          ];
          this.total = 3;
        } else if (this.activeTab === 'createVote') {
          this.tableData = [
            {
              id: 1,
              voteName: "2023年秋季校服投票活动",
              uploadDeadline: "2023-08-15 23:59:59",
              voteDeadline: "2023-08-30 23:59:59",
              status: "1" // 待发布
            }
          ];
          this.total = 1;
        } else if (this.activeTab === 'inviteParent') {
          this.tableData = [
            {
              id: 2,
              voteName: "2023年冬季校服投票活动",
              uploadDeadline: "2023-10-15 23:59:59",
              voteDeadline: "2023-10-30 23:59:59",
              status: "2" // 投票中
            }
          ];
          this.total = 1;
        } else if (this.activeTab === 'autoPublish') {
          this.tableData = [
            {
              id: 3,
              voteName: "2024年春季校服投票活动",
              uploadDeadline: "2023-12-15 23:59:59",
              voteDeadline: "2023-12-30 23:59:59",
              status: "3" // 已结束
            }
          ];
          this.total = 1;
        }
        this.loading = false;
      }, 300);
    },
    /** 获取字典数据 */
    getDictData() {
      // 正常情况下应该调用后端API获取字典数据
      // getDicData("school_uniform_type").then(response => {
      //   this.uniformTypeOptions = response.data;
      // });
      // getDicData("order_mode").then(response => {
      //   this.orderModeOptions = response.data;
      // });
    },
    /** 状态转换 */
    getStatusText(status) {
      switch (status) {
        case "1": return "待发布比选要求";
        case "2": return "投票中";
        case "3": return "已结束";
        default: return "未知状态";
      }
    },
    /** 状态类型转换 */
    getStatusType(status) {
      switch (status) {
        case "1": return "warning";
        case "2": return "primary";
        case "3": return "success";
        default: return "info";
      }
    },
    /** 标签页点击事件 */
    handleTabClick(tab) {
      this.activeTab = tab.name;
      if (this.activeTab === 'all') {
        this.queryParams.status = '';
      } else if (this.activeTab === 'createVote') {
        this.queryParams.status = '1';
      } else if (this.activeTab === 'inviteParent') {
        this.queryParams.status = '2';
      } else if (this.activeTab === 'autoPublish') {
        this.queryParams.status = '3';
      }
      this.getList();
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 添加按钮操作 */
    handleAdd() {
      this.resetForm();
      this.addDialogVisible = true;
      this.active = 0;
    },
    /** 编辑按钮操作 */
    handleEdit(row) {
      // 模拟加载数据
      this.resetForm();
      this.baseInfoForm = {
        voteName: row.voteName,
        applyScope: '全校学生',
        voteDesc: '校服承载着学校文化的深刻内涵，同时又承载着学校文化的浓厚底蕴。在校园里，校服是一道亮丽的风景线。在外，校服整齐个性的学生形象，彰显了学校的教学水平，提升了学校的形象。所以，我们将学校校服采购的决定权交给家长委员会，家长委员会通过参观考察、实物对比等形式对各企业进行了解调查，从企业规模、产品质量、业态能力等，综合服务水平，社会形象，从中挑选出若干家企业进行投票选型。希望各位家长参与投票，可结合家庭情况，帮助完成校服采购决策。',
        uploadDeadline: row.uploadDeadline,
        tradeTime: '2023-09-01 10:00:00',
        voteRule: '每个家长只能投票一次，投票结果将作为最终校服选择的重要依据',
        otherDesc: '请各位家长积极参与'
      };
      this.requirementForm = {
        requirements: [
          {
            category: 'summer',
            style: 'tshirt',
            quantity: 2,
            fabricRequirement: '高档精梳棉混纺面料，舒适型好，透气性面料。(棉：兰精70%PCLF，兰精30％。面料密度：点击密度40S)，面料含：涤纶棉混纺面料 - 50%棉 50%聚酯纤维，运动面料 聚'
          }
        ],
        orderMode: '1',
        malePriceRecommend: '150',
        femalePriceRecommend: '150',
        priceDesc: '价格包含所有校服款式的费用，不再额外收费'
      };
      this.contactForm = {
        contactName: '张老师',
        contactPhone: '13800138000'
      };
      this.addDialogVisible = true;
      this.active = 0;
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      this.$confirm('是否确认删除名称为"' + row.voteName + '"的比选要求?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        // 正常情况下应该调用后端API
        // 此处使用模拟删除
        this.$message({
          type: "success",
          message: "删除成功"
        });
        this.getList();
      }).catch(() => { });
    },
    /** 重置表单数据 */
    resetForm() {
      if (this.$refs.baseInfoForm) {
        this.$refs.baseInfoForm.resetFields();
      }
      if (this.$refs.requirementForm) {
        this.$refs.requirementForm.resetFields();
      }
      if (this.$refs.contactForm) {
        this.$refs.contactForm.resetFields();
      }
      this.baseInfoForm = {
        voteName: '',
        applyScope: '',
        voteDesc: '校服承载着学校文化的深刻内涵，同时又承载着学校文化的浓厚底蕴。在校园里，校服是一道亮丽的风景线。在外，校服整齐个性的学生形象，彰显了学校的教学水平，提升了学校的形象。所以，我们将学校校服采购的决定权交给家长委员会，家长委员会通过参观考察、实物对比等形式对各企业进行了解调查，从企业规模、产品质量、业态能力等，综合服务水平，社会形象，从中挑选出若干家企业进行投票选型。希望各位家长参与投票，可结合家庭情况，帮助完成校服采购决策。',
        uploadDeadline: '',
        tradeTime: '',
        voteRule: '',
        otherDesc: ''
      };
      this.requirementForm = {
        requirements: [
          {
            category: '',
            style: '',
            quantity: 1,
            fabricRequirement: '高档精梳棉混纺面料，舒适型好，透气性面料。(棉：兰精70%PCLF，兰精30％。面料密度：点击密度40S)，面料含：涤纶棉混纺面料 - 50%棉 50%聚酯纤维，运动面料 聚'
          }
        ],
        orderMode: '1',
        malePriceRecommend: '',
        femalePriceRecommend: '',
        priceDesc: ''
      };
      this.contactForm = {
        contactName: '',
        contactPhone: ''
      };
      this.selectedManufacturers = [];
    },
    /** 关闭弹窗 */
    handleClose() {
      this.addDialogVisible = false;
      this.resetForm();
    },
    /** 上一步 */
    prevStep() {
      if (this.active > 0) {
        this.active--;
      }
    },
    /** 下一步 */
    nextStep(step) {
      if (step === 0) {
        this.$refs.baseInfoForm.validate(valid => {
          if (valid) {
            this.active++;
          }
        });
      } else if (step === 1) {
        this.$refs.requirementForm.validate(valid => {
          if (valid) {
            this.active++;
          }
        });
      }
    },
    /** 提交表单 */
    submitForm() {
      this.$refs.contactForm.validate(valid => {
        if (valid) {
          // 正常情况下应该调用后端API提交数据
          // const data = {
          //   ...this.baseInfoForm,
          //   ...this.requirementForm,
          //   ...this.contactForm
          // };
          // request({
          //   url: '/bxactive/add',
          //   method: 'post',
          //   data: data
          // }).then(response => {
          //   this.$message({
          //     type: "success",
          //     message: "提交成功"
          //   });
          //   this.addDialogVisible = false;
          //   this.getList();
          // });

          // 此处模拟提交成功
          this.$message({
            type: "success",
            message: "提交成功"
          });
          this.addDialogVisible = false;
          this.getList();
        }
      });
    },
    /** 暂存表单 */
    saveForm() {
      // 模拟暂存成功
      this.$message({
        type: "success",
        message: "暂存成功"
      });
    },
    /** 显示厂商选择对话框 */
    showManufacturerDialog() {
      this.manufacturerDialogVisible = true;
      this.searchManufacturers();
    },
    /** 搜索厂商 */
    searchManufacturers() {
      // 模拟后端数据
      this.manufacturerList = [
        {
          id: 1,
          name: "武汉市汉口区复兴制服儿童装品部",
          employees: 100,
          capital: 10.00,
          establishTime: "2018-07-23",
          status: "未选择"
        },
        {
          id: 2,
          name: "湖北智梦纺织科技设备有限公司",
          employees: 50,
          capital: 518.00,
          establishTime: "2011-06-03",
          status: "未选择"
        },
        {
          id: 3,
          name: "宝鸡市纺织服装服饰厂",
          employees: 27,
          capital: 10.00,
          establishTime: "2013-04-01",
          status: "未选择"
        },
        {
          id: 4,
          name: "正荣服饰服装加工厂",
          employees: 3,
          capital: 5.00,
          establishTime: "2019-02-19",
          status: "未选择"
        },
        {
          id: 5,
          name: "山西阿西服装有限公司",
          employees: 80,
          capital: 1000.00,
          establishTime: "2018-02-07",
          status: "未选择"
        },
        {
          id: 6,
          name: "成都神龙服装有限公司",
          employees: 190,
          capital: 2100.00,
          establishTime: "2006-10-24",
          status: "未选择"
        },
        {
          id: 7,
          name: "河南荣金服饰有限公司",
          employees: 100,
          capital: 500.00,
          establishTime: "2016-12-28",
          status: "未选择"
        },
        {
          id: 8,
          name: "济宁文景服装股份有限公司",
          employees: 300,
          capital: 101.00,
          establishTime: "2010-12-15",
          status: "未选择"
        },
        {
          id: 9,
          name: "陕西口上服饰有限公司",
          employees: 80,
          capital: 1030.00,
          establishTime: "2008-02-02",
          status: "未选择"
        },
        {
          id: 10,
          name: "桂林华服饰制衣有限公司",
          employees: 45,
          capital: 300.00,
          establishTime: "2006-09-07",
          status: "未选择"
        }
      ];
      this.total = 327;
    },
    /** 选择厂商变更 */
    handleSelectionChange(selection) {
      this.selectedManufacturers = selection;
    },
    /** 单独选择厂商 */
    selectManufacturer(row) {
      // 检查是否已经选择了该厂商
      const index = this.selectedManufacturers.findIndex(item => item.id === row.id);
      if (index === -1) {
        this.selectedManufacturers.push(row);
        this.$message({
          type: "success",
          message: `已选择 ${row.name}`
        });
      } else {
        this.selectedManufacturers.splice(index, 1);
        this.$message({
          type: "info",
          message: `已取消选择 ${row.name}`
        });
      }
    },
    /** 确认选择厂商 */
    confirmSelectManufacturers() {
      if (this.selectedManufacturers.length === 0) {
        this.$message({
          type: "warning",
          message: "请至少选择一家厂商"
        });
        return;
      }
      this.$message({
        type: "success",
        message: `已选择 ${this.selectedManufacturers.length} 家厂商`
      });
      this.manufacturerDialogVisible = false;
    },
    /** 每页数量变更 */
    handleSizeChange(val) {
      this.pageParams.pageSize = val;
      this.searchManufacturers();
    },
    /** 当前页变更 */
    handleCurrentChange(val) {
      this.pageParams.pageNum = val;
      this.searchManufacturers();
    },
    /** 添加校服要求项 */
    addRequirement() {
      this.requirementForm.requirements.push({
        category: '',
        style: '',
        quantity: 1,
        fabricRequirement: ''
      });
    },
    /** 移除校服要求项 */
    removeRequirement(index) {
      this.requirementForm.requirements.splice(index, 1);
    },
    /** 重置搜索表单 */
    resetSearchForm() {
      this.searchParams = {
        manufacturerName: '',
        region: '',
        type: ''
      };
      this.searchManufacturers();
    },
    /** 移除选中的厂商 */
    removeSelectedManufacturer(item) {
      const index = this.selectedManufacturers.findIndex(i => i.id === item.id);
      if (index !== -1) {
        this.selectedManufacturers.splice(index, 1);
        this.$message({
          type: "info",
          message: `已取消选择 ${item.name}`
        });
      }
    }
  }
};
</script>

<style scoped>
/* 全局样式 */
.app-container {
  padding: 20px;
}

/* 表格样式 */
.el-table {
  margin-top: 15px;
}

/* 标签页样式 */
.el-tabs {
  margin-bottom: 15px;
}

/* 表单样式 */
.el-form-item {
  margin-bottom: 20px;
}

/* 步骤条样式 */
.custom-steps-container {
  margin-bottom: 30px;
  padding: 20px 0;
  background-color: #f8f8f8;
  border-radius: 4px;
  position: relative;
}

.custom-steps {
  display: flex;
  justify-content: space-around;
  align-items: center;
  position: relative;
  width: 75%;
  margin: 0 auto;
}

.custom-steps::before {
  content: '';
  position: absolute;
  top: 16px;
  left: 15%;
  right: 15%;
  height: 2px;
  background-color: #e4e7ed;
  z-index: 1;
}

.custom-step {
  position: relative;
  text-align: center;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 2;
}

.step-node {
  margin-bottom: 10px;
}

.step-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 16px;
  font-weight: bold;
  background-color: #f5f7fa;
  color: #909399;
  border: 2px solid #e4e7ed;
  transition: all 0.3s;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.step-title {
  font-size: 15px;
  font-weight: 500;
  color: #909399;
  transition: all 0.3s;
}

.custom-step.is-active .step-icon {
  background-color: #409EFF;
  color: #fff;
  border-color: #409EFF;
  box-shadow: 0 2px 10px rgba(64, 158, 255, 0.3);
}

.custom-step.is-active .step-title {
  color: #409EFF;
  font-weight: 700;
}

.custom-step.is-success .step-icon {
  background-color: #67c23a;
  color: #fff;
  border-color: #67c23a;
  box-shadow: 0 2px 10px rgba(103, 194, 58, 0.3);
}

.custom-step.is-success .step-title {
  color: #67c23a;
  font-weight: 700;
}

.custom-steps .step-line-success {
  position: absolute;
  top: 16px;
  left: 15%;
  height: 2px;
  background-color: #67c23a;
  z-index: 1;
  transition: width 0.8s ease-in-out;
}

.custom-steps .step-line-active {
  position: absolute;
  top: 16px;
  left: 15%;
  height: 2px;
  background-color: #409EFF;
  z-index: 1;
  transition: width 0.8s ease-in-out;
}

/* 按钮容器样式 */
.button-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #EBEEF5;
}

.button-container .el-button {
  margin-left: 10px;
}

/* 校服要求项样式 */
.requirements-section {
  margin-bottom: 30px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
}

.section-header {
  background-color: #f5f7fa;
  padding: 12px 15px;
  border-bottom: 1px solid #ebeef5;
}

.section-title {
  font-size: 15px;
  font-weight: bold;
  color: #303133;
  position: relative;
  padding-left: 10px;
}

.section-title:before {
  content: "";
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  height: 14px;
  width: 3px;
  background-color: #409EFF;
  border-radius: 1px;
}

.price-section {
  margin-bottom: 30px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
  padding-bottom: 15px;
}

.requirements-table {
  width: 100%;
}

.requirements-header {
  display: flex;
  background-color: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
  padding: 10px 0;
  font-weight: bold;
  font-size: 13px;
}

.requirements-row {
  display: flex;
  border-bottom: 1px solid #ebeef5;
  padding: 10px 0;
  align-items: center;
}

.requirements-row:last-child {
  border-bottom: none;
}

.req-col {
  padding: 0 10px;
  text-align: center;
}

.req-col-type {
  width: 20%;
}

.req-col-style {
  width: 20%;
}

.req-col-quantity {
  width: 15%;
}

.req-col-fabric {
  width: 35%;
}

.req-col-action {
  width: 10%;
  text-align: center;
}

/* 移除表单项底部边距 */
.no-margin {
  margin-bottom: 0 !important;
}

/* 步骤条图标 */
.custom-step-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 16px;
  font-weight: bold;
  background-color: #f5f7fa;
  color: #909399;
  border: 2px solid #e4e7ed;
  transition: all 0.3s;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.custom-step-icon.is-active {
  background-color: #409EFF;
  color: #fff;
  border-color: #409EFF;
  box-shadow: 0 2px 10px rgba(64, 158, 255, 0.3);
}

.custom-step-icon.is-success {
  background-color: #67c23a;
  color: #fff;
  border-color: #67c23a;
  box-shadow: 0 2px 10px rgba(103, 194, 58, 0.3);
}

/* 步骤条样式 */
.steps-bar {
  margin-bottom: 30px;
  padding: 20px 0;
  background-color: #f8f8f8;
  border-radius: 4px;
}

:deep(.el-step__main) {
  margin-top: 8px;
}

:deep(.el-step__title.is-process) {
  font-weight: 700;
  color: #409EFF;
}

:deep(.el-step__title.is-success) {
  color: #67c23a;
}

:deep(.el-step__head.is-process) {
  color: #409EFF;
  border-color: #409EFF;
}

:deep(.el-step__head.is-success) {
  color: #67c23a;
  border-color: #67c23a;
}

:deep(.el-step.is-horizontal .el-step__line) {
  height: 2px;
  top: 16px;
  left: 49%;
  right: -30%;
  width: 80%;
  margin: 0;
}

:deep(.el-step__line-inner) {
  border-width: 1px;
  transition: all 0.3s;
}

:deep(.el-step.is-process .el-step__line-inner) {
  border-color: #409EFF;
  width: 0 !important;
}

:deep(.el-step.is-success .el-step__line-inner) {
  border-color: #67c23a;
  width: 100% !important;
}

:deep(.el-step.is-horizontal) {
  position: relative;
}

/* 价格推荐样式 */
.price-recommend {
  display: flex;
  align-items: center;
  margin-top: 15px;
}

.price-recommend .el-input {
  width: 150px;
  margin: 0 10px;
}

/* 厂商选择对话框样式 */
.manufacturer-dialog .el-dialog__body {
  padding: 10px 20px;
}

/* 搜索表单样式 */
.search-container {
  background-color: #f5f7fa;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 0;
}

.search-form .el-form-item {
  margin-bottom: 10px;
  margin-right: 10px;
}

/* 弹窗按钮居中 */
.dialog-footer {
  text-align: center;
  margin-top: 30px;
}

/* 卡片样式 */
.el-card {
  margin-bottom: 20px;
}

.el-card__header {
  padding: 15px 20px;
  font-weight: bold;
}

/* 列表空数据样式 */
.no-data {
  text-align: center;
  color: #909399;
  padding: 30px 0;
  background: #fff;
  border-radius: 4px;
  border: 1px solid #ebeef5;
}

/* 弹窗容器样式 */
.dialog-container {
  padding: 0 10px;
}

/* 表单容器样式 */
.form-container {
  background-color: #fff;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}

/* 表单行样式 */
.form-item-line {
  margin-bottom: 22px;
  padding: 0 15px;
}

/* 提示文本样式 */
.hint-text {
  color: #909399;
  font-size: 12px;
  margin-left: 10px;
}

/* 内容行样式 */
.item-row {
  margin-bottom: 15px;
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
}

/* 操作按钮区样式 */
.operation-buttons {
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 分页容器样式 */
.pagination-container {
  margin-top: 15px;
  text-align: right;
}

/* 厂商容器样式 */
.manufacturer-container {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

/* 已选厂商区域 */
.selected-manufacturers {
  margin-top: 10px;
  width: 100%;
  display: flex;
  flex-wrap: wrap;
}

/* 厂商标签样式 */
.manufacturer-tag {
  margin-right: 8px;
  margin-bottom: 8px;
}

/* 自定义弹窗样式 */
:deep(.bx-dialog .el-dialog__body) {
  padding: 20px 20px;
}

:deep(.manufacturer-dialog .el-dialog__body) {
  padding: 5px 20px 0;
}

/* 自定义样式 */
:deep(.el-select) {
  width: 100%;
}

:deep(.custom-tabs .el-tabs__item) {
  font-size: 15px;
  padding: 0 20px;
}

:deep(.el-date-editor.el-input) {
  width: 100%;
}

:deep(.el-tabs__nav) {
  margin-bottom: 15px;
}

:deep(.el-tabs__item) {
  font-size: 14px;
  padding: 0 20px;
}

:deep(.el-tabs__item.is-active) {
  color: #409EFF;
  font-weight: bold;
}

:deep(.el-textarea__inner) {
  font-family: Arial, sans-serif;
}

:deep(.el-dialog__body) {
  padding: 10px 20px;
  height: 800px;
}


:deep(.el-form-item__label) {
  font-weight: 500;
  color: #303133;
}

:deep(.el-input-number) {
  width: 100%;
}
</style>