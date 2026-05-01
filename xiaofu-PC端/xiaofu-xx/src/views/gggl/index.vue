<template>
  <div class="app-container">
    <el-form inline>
      <el-form-item label="公告状态：">
        <el-select v-model="queryParams.status" placeholder="全部">
          <el-option label="全部" value="" />
          <el-option label="已发布" value="1" />
          <el-option label="未发布" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="small" @click="handleQuery">查询</el-button>
        <el-button type="primary" size="small" @click="handleEvaluate">评价管理</el-button>
      </el-form-item>
    </el-form>
    <el-button type="primary" icon="el-icon-plus" size="small" @click="handleAdd">添加公告</el-button>
    <el-table v-loading="loading" :data="noticeList" style="margin-top: 10px;">
      <el-table-column label="公告标题" prop="title" />
      <el-table-column label="添加时间" prop="createTime" width="180" />
      <el-table-column label="付款截止日期" prop="paymentDeadline" width="180" />
      <el-table-column label="厂商名称" prop="manufacturerName" />
      <el-table-column label="配送方式" prop="deliveryMethod" width="120" />
      <el-table-column label="公告状态" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '1' ? 'success' : 'info'">
            {{ scope.row.status === '1' ? '已发布' : '未发布' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="150">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="mini" type="text" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑公告弹框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="1200px" append-to-body>
      <el-form ref="form" :model="form" style="height: 700px;overflow: auto;" :rules="rules" label-width="140px">
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <el-input type="textarea" v-model="form.content" placeholder="请输入公告内容" :rows="4"></el-input>
        </el-form-item>
        <el-form-item label="厂商名称" prop="manufacturerName">
          <el-select v-model="form.manufacturerName" placeholder="请选择" style="width: 100%">
            <el-option label="请选择" value="" />
          </el-select>
          <div class="manufacturer-link">
            <a href="javascript:;" @click="handleAddManufacturer">暂无合适厂商，去添加 ></a>
          </div>
        </el-form-item>
        <el-form-item label="选择厂商理由" prop="reason">
          <el-input type="textarea" v-model="form.reason" placeholder="请输入厂商理由" :rows="4" />
        </el-form-item>
        <el-form-item label="售后须知">
          <el-button type="primary" icon="el-icon-plus" size="small" @click="handleAddAfterSale">添加</el-button>
        </el-form-item>
        <el-form-item label="商品总价">
          <el-col :span="11">
            <el-form-item prop="malePriceTotal">
              <el-input v-model="form.malePriceTotal" placeholder="男生商品总价">
                <template slot="append">(包含男女款男女同款)</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="2" class="text-center"><span class="el-icon-minus"></span></el-col>
          <el-col :span="11">
            <el-form-item prop="femalePriceTotal">
              <el-input v-model="form.femalePriceTotal" placeholder="女生商品总价">
                <template slot="append">(包含男女款男女同款)</template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-form-item>
        <el-form-item label="适用年级">
          <el-checkbox v-model="form.gradeAll">高一</el-checkbox>
        </el-form-item>
        <el-form-item label="配送方式">
          <el-radio-group v-model="form.deliveryMethod">
            <el-radio label="1">自主配送至学校</el-radio>
            <el-radio label="2">物流配送到家</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="参考运费">
          <span class="reference-fee">家长无需支付运费</span>
        </el-form-item>
        <el-form-item label="付款截止日期" prop="paymentDeadline">
          <el-date-picker v-model="form.paymentDeadline" type="date" placeholder="选择日期" value-format="yyyy-MM-dd" />
        </el-form-item>
        <el-form-item label="预计到货日期" prop="expectedArrivalDate">
          <el-date-picker v-model="form.expectedArrivalDate" type="date" placeholder="选择日期" value-format="yyyy-MM-dd" />
        </el-form-item>
        <el-form-item label="已选择商品">
          <el-button type="primary" @click="handleSelectProducts">选择商品</el-button>
          <el-table :data="selectedProducts" border style="margin-top: 10px;">
            <el-table-column label="款式" prop="style" />
            <el-table-column label="商品图片" prop="image">
              <template slot-scope="scope">
                <el-image style="width: 50px; height: 50px" :src="scope.row.image"
                  :preview-src-list="[scope.row.image]">
                </el-image>
              </template>
            </el-table-column>
            <el-table-column label="商品名称" prop="name" />
            <el-table-column label="商品款号" prop="code" />
            <el-table-column label="操作" width="120">
              <template slot-scope="scope">
                <el-button size="mini" type="text" @click="handleRemoveProduct(scope.$index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="handlePreview">预览公告</el-button>
        <el-button @click="handleSaveDraft">暂存公告</el-button>
        <el-button type="primary" @click="handlePublish">发布公告</el-button>
      </div>
    </el-dialog>

    <!-- 评价管理弹框 -->
    <el-dialog title="评价管理" :visible.sync="evaluateVisible" width="900px" append-to-body>
      <el-tabs v-model="evaluateActiveName">
        <el-tab-pane label="未评价" name="unrated">
          <div class="filter-container">
            <el-form :inline="true" :model="evaluateQuery">
              <el-form-item label="公告名称：">
                <el-input v-model="evaluateQuery.title" placeholder="请输入公告名称" />
              </el-form-item>
              <el-form-item label="厂商名称：">
                <el-input v-model="evaluateQuery.manufacturerName" placeholder="请输入厂商名称" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" size="small" @click="handleEvaluateQuery">查询</el-button>
              </el-form-item>
            </el-form>
          </div>
          <el-table :data="unratedList" border>
            <el-table-column label="公告标题" prop="title" />
            <el-table-column label="添加日期" prop="createTime" width="180" />
            <el-table-column label="付款截止日期" prop="paymentDeadline" width="180" />
            <el-table-column label="厂商名称" prop="manufacturerName" />
            <el-table-column label="配送方式" prop="deliveryMethod" />
            <el-table-column label="操作" width="120" align="center">
              <template slot-scope="scope">
                <el-button size="mini" type="text" @click="handleRate(scope.row)">评价</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="已评价" name="rated">
          <div class="filter-container">
            <el-form :inline="true" :model="evaluateQuery">
              <el-form-item label="公告名称：">
                <el-input v-model="evaluateQuery.title" placeholder="请输入公告名称" />
              </el-form-item>
              <el-form-item label="厂商名称：">
                <el-input v-model="evaluateQuery.manufacturerName" placeholder="请输入厂商名称" />
              </el-form-item>
              <el-form-item label="评价时间：">
                <el-date-picker v-model="evaluateQuery.rateTime" type="date" placeholder="请选择评价时间"
                  value-format="yyyy-MM-dd" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" size="small" @click="handleEvaluateQuery">查询</el-button>
              </el-form-item>
            </el-form>
          </div>
          <el-table :data="ratedList" border>
            <el-table-column label="公告标题" prop="title" />
            <el-table-column label="添加日期" prop="createTime" width="180" />
            <el-table-column label="付款截止日期" prop="paymentDeadline" width="180" />
            <el-table-column label="厂商名称" prop="manufacturerName" />
            <el-table-column label="评价星级" prop="rating" width="120" />
            <el-table-column label="评价时间" prop="rateTime" width="180" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>

    <!-- 选择厂商理由弹窗 -->
    <el-dialog title="选择厂商理由" :visible.sync="reasonDialogVisible" width="600px" append-to-body>
      <el-radio-group v-model="selectedReason">
        <el-radio label="1">经过本学校学生家长委员会讨论的形式，该企业校服的质量、款式、价格得到学生和家长认可。</el-radio>
        <el-radio label="2">根据本学校校服公开招标的形式，该企业校服的质量、款式、价格得到学生和家长认可。</el-radio>
        <el-radio label="3">经过本学校学生、学生代表等讨论的形式，该企业校服的质量、款式、价格得到学生和家长认可。</el-radio>
        <el-radio label="4">根据文件精神，并征求家长、学生代表的一致意见，该企业校服的质量、款式、价格得到学生和家长认可。</el-radio>
      </el-radio-group>
      <div slot="footer" class="dialog-footer">
        <el-button @click="reasonDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReason">确定</el-button>
      </div>
    </el-dialog>

    <!-- 售后须知弹窗 -->
    <el-dialog title="售后须知" :visible.sync="afterSaleDialogVisible" width="1100px" append-to-body>
      <div class="after-sale-form">
        是否支持售后退换货？
        <p class="tip-text">*请根据与厂商沟通的结果选择是否编辑售后须知!</p>
        <el-radio-group v-model="afterSaleSupport">
          <el-radio label="1">支持</el-radio>
          <el-radio label="0">不支持</el-radio>
        </el-radio-group>
        <el-button type="primary" size="small" class="template-btn" @click="selectTemplate">选择模板</el-button>
        <el-input type="textarea" v-model="afterSaleContent" :rows="6" placeholder="请输入售后须知" :maxlength="500"
          show-word-limit></el-input>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="afterSaleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAfterSale">确定</el-button>
      </div>
    </el-dialog>

    <!-- 支持退换货模板弹窗 -->
    <el-dialog title="支持退换货模板" :visible.sync="supportTemplateVisible" width="600px" append-to-body>
      <el-radio-group v-model="selectedSupportTemplate" class="template-radio-group">
        <el-radio label="1">
          1. 关于退货:<br>
          (1) 统购商品公告截止日期之前的订单可申请退款。<br>
          (2) 统购商品公告截止日期之后，商品处于生产阶段，若退货可现订订单中厂家电话进行协商。<br>
          【注: 特殊体型（包含特殊定制）为定制商品，付款截止后统一按照订单信息量身定生产，不支持无理由退换货，请谨慎选购。】<br>
          2. 关于换货尺码: 可在"阳光智园"APP，"我的订单"中找到该订单进入订单详情, 选择需更换的商品点击"申请换货"进行申请。(若不显示"申请换货"，表示厂商暂未开启线上退换货，可按订订单中厂家电话进行协商。)
        </el-radio>
      </el-radio-group>
      <div slot="footer" class="dialog-footer">
        <el-button @click="supportTemplateVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSupportTemplate">确定</el-button>
      </div>
    </el-dialog>

    <!-- 不支持退换货模板弹窗 -->
    <el-dialog title="不支持退换货模板" :visible.sync="notSupportTemplateVisible" width="600px" append-to-body>
      <el-radio-group v-model="selectedNotSupportTemplate" class="template-radio-group">
        <el-radio label="1">
          公告付款截止后退换货: 校服为定制商品，付款截止后厂商统一按照订单信息量生产，属于定制，暂不支持退换货，请谨慎选购。
        </el-radio>
      </el-radio-group>
      <div slot="footer" class="dialog-footer">
        <el-button @click="notSupportTemplateVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmNotSupportTemplate">确定</el-button>
      </div>
    </el-dialog>

    <!-- 选择商品弹窗 -->
    <el-dialog title="选择商品" :visible.sync="productDialogVisible" width="900px" append-to-body>
      <div class="filter-container">
        <el-form :inline="true" :model="productQuery">
          <el-form-item label="商品品类：">
            <el-select v-model="productQuery.category" placeholder="全部">
              <el-option label="全部" value="" />
            </el-select>
          </el-form-item>
          <el-form-item label="商品名称：">
            <el-input v-model="productQuery.name" placeholder="请输入商品名称" />
          </el-form-item>
          <el-form-item label="商品款号：">
            <el-input v-model="productQuery.code" placeholder="请输入商品款号" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleProductQuery">查询</el-button>
          </el-form-item>
        </el-form>
      </div>
      <el-table :data="productList" border @selection-change="handleProductSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column label="商品图片" prop="image">
          <template slot-scope="scope">
            <el-image style="width: 50px; height: 50px" :src="scope.row.image" :preview-src-list="[scope.row.image]">
            </el-image>
          </template>
        </el-table-column>
        <el-table-column label="商品名称" prop="name" />
        <el-table-column label="商品款号" prop="code" />
        <el-table-column label="商品品类" prop="category" />
        <el-table-column label="款式" prop="style" />
      </el-table>
      <div class="no-data-tip" v-if="productList.length === 0">
        暂无商品，请联系厂商添加商品（厂商服务电话：18333111113）
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="productDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmProducts">确定</el-button>
      </div>
    </el-dialog>

    <!-- 合作厂商列表弹窗 -->
    <el-dialog title="新增合作厂商" :visible.sync="manufacturerDialogVisible" width="900px" append-to-body>
      <div>
        <el-form inline>
          <el-form-item label=""> <el-input v-model="manufacturerQuery" placeholder="请输入厂商名称" style="width: 300px;"
              class="filter-item" @keyup.enter.native="searchManufacturer"></el-input></el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchManufacturer">查询</el-button>
          </el-form-item>
        </el-form>
      </div>
      <el-table :data="manufacturerList" border style="width: 100%">
        <el-table-column label="厂商名称" prop="name" />
        <el-table-column label="操作" align="center" width="100">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="addManufacturer(scope.row)">添加</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange"
          :current-page="currentPage" :page-sizes="[10, 20, 30, 50]" :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper" :total="total"></el-pagination>
        <span class="page-info">共{{ totalPages }}页/{{ total }}条</span>
      </div>
    </el-dialog>

    <!-- 添加合作厂商信息弹窗 -->
    <el-dialog title="添加合作厂商" :visible.sync="addManufacturerDialogVisible" width="600px" append-to-body>
      <el-form ref="manufacturerForm" :model="manufacturerForm" :rules="manufacturerRules" label-width="100px">
        <el-form-item label="厂商名称" prop="name">
          <el-input v-model="manufacturerForm.name" placeholder="请输入厂商名称" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="manufacturerForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="联系人" prop="contact">
          <el-input v-model="manufacturerForm.contact" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="manufacturerForm.address" placeholder="请输入地址" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="addManufacturerDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitManufacturer">确定</el-button>
      </div>
    </el-dialog>

    <!-- 厂商合同表格弹窗 -->
    <el-dialog title="厂商列表" :visible.sync="manufacturerTableVisible" width="1000px" append-to-body>
      <el-button type="primary" icon="el-icon-plus" @click="openManufacturerDialog">添加合作厂商</el-button>
      <el-table :data="cooperationManufacturerList" style="width: 100%;margin-top: 10px;">
        <el-table-column label="厂商名称" prop="name" />
        <el-table-column label="联系电话" prop="phone" width="120" />
        <el-table-column label="操作" align="center" width="350">
          <template slot-scope="scope">
            <el-button type="text" @click="seeInfo(scope.row)">查看厂商信息</el-button>
            <el-button type="text" @click="viewContract(scope.row)">查看合同</el-button>
            <el-button type="text" @click="addContract(scope.row)">添加采购合同</el-button>
            <el-button type="text" @click="cancelCooperation(scope.row)">取消合作</el-button>
          </template>
        </el-table-column>
      </el-table>

    </el-dialog>
    <el-dialog title="厂商详情" :visible.sync="dialogChangshang" width="1000px" custom-class="manufacturer-detail-dialog">
      <div style="height: 700px;overflow: auto;">

        <div class="manufacturer-header">
          <div class="manufacturer-logo">
            <el-avatar :size="80" icon="el-icon-s-custom" style="background-color: #409EFF;">金鑫</el-avatar>
          </div>
          <div class="manufacturer-title">
            <h2>河北金鑫服装制造有限公司</h2>
            <div class="manufacturer-subtitle">
              <el-tag type="success">企业用户</el-tag>
              <span class="cert-tag"><i class="el-icon-circle-check"></i> 已认证</span>
            </div>
          </div>
        </div>

        <el-divider><i class="el-icon-s-cooperation"></i> 基本信息</el-divider>

        <div class="info-grid">
          <div class="info-item">
            <span class="info-label"><i class="el-icon-user"></i> 法人代表：</span>
            <span class="info-value">李志为</span>
          </div>
          <div class="info-item">
            <span class="info-label"><i class="el-icon-money"></i> 注册资金：</span>
            <span class="info-value">500万</span>
          </div>
          <div class="info-item">
            <span class="info-label"><i class="el-icon-s-custom"></i> 职工人数：</span>
            <span class="info-value">96人</span>
          </div>
          <div class="info-item">
            <span class="info-label"><i class="el-icon-date"></i> 成立时间：</span>
            <span class="info-value">2014-10-08</span>
          </div>
          <div class="info-item">
            <span class="info-label"><i class="el-icon-date"></i> 更新时间：</span>
            <span class="info-value">2024-08-26</span>
          </div>
          <div class="info-item info-item-full">
            <span class="info-label"><i class="el-icon-location"></i> 厂商地址：</span>
            <span class="info-value">河北省 保定市 蠡县 大曲堤乡 东韩村</span>
          </div>
        </div>

        <el-divider><i class="el-icon-document"></i> 厂商简介</el-divider>

        <div class="manufacturer-description">
          <p>
            河北金鑫服装制造有限公司是一家从事校服制造的专业公司，成立于2014年10月08日，公司坐落在河北省保定市蠡县大曲堤乡东韩村。经国家认证授权信息公示系统查询得知，河北金鑫服装制造有限公司的信用代码/注册号为911306293198098566，法人是李志为，注册资本为500万元人民币，企业的经营范围为：纺织服装、服饰、纺织品的加工、制造、销售；皮塑和代理各种商品和技术的进出口业务和直接出口商品和国家实行配额、许可证管理的出口商品以外的其他各类货物的进出口业务（依法须经批准的项目，经相关部门批准后方可开展经营活动）。
          </p>
        </div>

        <el-divider><i class="el-icon-set-up"></i> 设备条件</el-divider>

        <div class="manufacturer-equipment">
          <el-tag size="medium" effect="plain">自动化流水线</el-tag>
          <el-tag size="medium" effect="plain">缝纫设备</el-tag>
          <el-tag size="medium" effect="plain">裁剪设备</el-tag>
          <el-tag size="medium" effect="plain">品质检测设备</el-tag>
        </div>

        <el-divider><i class="el-icon-picture"></i> 企业资质</el-divider>

        <div class="manufacturer-qualification">
          <el-image style="width: 300px; height: 400px; border: 1px solid #eee; padding: 5px;"
            src="/images/demo/license.jpg" fit="contain">
            <div slot="error" class="image-slot">
              <i class="el-icon-picture-outline"></i> 营业执照
            </div>
          </el-image>
        </div>
      </div>

      <div class="dialog-footer" slot="footer">
        <el-button type="primary" @click="dialogChangshang = false">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'NoticeManagement',
  components: {
    Editor: () => import('@/components/Editor')
  },
  data() {
    return {
      dialogChangshang: false,
      // 遮罩层
      loading: false,
      // 查询参数
      queryParams: {
        status: ''
      },
      // 公告列表数据
      noticeList: [],
      // 弹出层标题
      dialogTitle: '',
      // 是否显示弹出层
      dialogVisible: false,
      // 表单参数
      form: {
        title: undefined,
        content: '尊敬的家长，您好！学校校服是学校形象和学生身份的识别标识，是培育校园文化、保障学生安全的重要载体，也是培养学生团队意识、减少攀比心理、传播平等思想的有益方式。为了进一步加强学生的思想品德教育，增强学生的集体荣誉感和组织纪律性，体现我校学生的精神风貌和整体意识，规范学生的日常行为。根据相关文件精神，学校拟开展校服服务工作。本次学生校服学校统一款式、面料、标志，家长通过阳光智园平台，在线选择商品，参考身高、体重信息，选择合适的尺码，确定订单并支付，厂商通过平台导出订购数据进行生产及发货',
        manufacturerName: undefined,
        reason: undefined,
        malePriceTotal: undefined,
        femalePriceTotal: undefined,
        gradeAll: false,
        deliveryMethod: '1',
        paymentDeadline: undefined,
        expectedArrivalDate: undefined
      },
      // 表单校验
      rules: {
        title: [
          { required: true, message: '请输入公告标题', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入公告内容', trigger: 'blur' }
        ],
        manufacturerName: [
          { required: true, message: '请选择厂商名称', trigger: 'change' }
        ],
        reason: [
          { required: true, message: '请输入选择厂商理由', trigger: 'blur' }
        ],
        paymentDeadline: [
          { required: true, message: '请选择付款截止日期', trigger: 'change' }
        ],
        expectedArrivalDate: [
          { required: true, message: '请选择预计到货日期', trigger: 'change' }
        ]
      },
      // 已选择的商品列表
      selectedProducts: [],
      // 评价管理
      evaluateVisible: false,
      evaluateActiveName: 'unrated',
      evaluateQuery: {
        title: undefined,
        manufacturerName: undefined,
        rateTime: undefined
      },
      unratedList: [],
      ratedList: [],
      // 选择厂商理由弹窗
      reasonDialogVisible: false,
      selectedReason: '',
      // 售后须知弹窗
      afterSaleDialogVisible: false,
      afterSaleSupport: '1',
      afterSaleContent: '',
      // 支持退换货模板弹窗
      supportTemplateVisible: false,
      selectedSupportTemplate: '1',
      // 不支持退换货模板弹窗
      notSupportTemplateVisible: false,
      selectedNotSupportTemplate: '1',
      // 选择商品弹窗
      productDialogVisible: false,
      productQuery: {
        category: '',
        name: '',
        code: ''
      },
      productList: [],
      // 合作厂商列表弹窗
      manufacturerDialogVisible: false,
      manufacturerQuery: '',
      manufacturerList: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      totalPages: 0,

      // 添加合作厂商信息弹窗
      addManufacturerDialogVisible: false,
      manufacturerForm: {
        name: '',
        phone: '',
        contact: '',
        address: ''
      },
      manufacturerRules: {
        name: [
          { required: true, message: '请输入厂商名称', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' }
        ]
      },

      // 厂商合同表格弹窗
      manufacturerTableVisible: false,
      cooperationManufacturerList: [
        {
          name: '河北鑫全盏服装制造有限公司',
          phone: '18333111113'
        }
      ]
    }
  },
  created() {
    this.getList()
  },
  methods: {
    seeInfo() {
      this.dialogChangshang = true;
    },
    /** 查询公告列表 */
    getList() {
      this.loading = true
      // 这里添加获取公告列表的接口调用
      setTimeout(() => {
        this.loading = false
        this.noticeList = []
      }, 300)
    },
    /** 查询按钮操作 */
    handleQuery() {
      this.getList()
    },
    /** 添加按钮操作 */
    handleAdd() {
      this.dialogTitle = '添加公告'
      this.dialogVisible = true
      this.reset()
    },
    /** 编辑按钮操作 */
    handleEdit(row) {
      this.dialogTitle = '编辑公告'
      this.dialogVisible = true
      this.form = { ...row }
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      this.$confirm('是否确认删除该公告？', '警告', {
        type: 'warning'
      }).then(() => {
        // 这里添加删除公告的接口调用
      }).catch(() => { })
    },
    /** 表单重置 */
    reset() {
      this.form = {
        title: undefined,
        content: '',
        manufacturerName: undefined,
        reason: undefined,
        malePriceTotal: undefined,
        femalePriceTotal: undefined,
        gradeAll: false,
        deliveryMethod: '1',
        paymentDeadline: undefined,
        expectedArrivalDate: undefined
      }
      this.selectedProducts = []
    },
    /** 添加厂商 */
    handleAddManufacturer() {
      this.manufacturerTableVisible = true
    },
    /** 添加售后须知 */
    handleAddAfterSale() {
      this.afterSaleDialogVisible = true
      this.afterSaleSupport = '1'
      this.afterSaleContent = ''
    },
    /** 选择商品 */
    handleSelectProducts() {
      this.productDialogVisible = true
      this.getProductList()
    },
    /** 移除商品 */
    handleRemoveProduct(index) {
      this.selectedProducts.splice(index, 1)
    },
    /** 预览公告 */
    handlePreview() {
      // 预览公告逻辑
    },
    /** 暂存公告 */
    handleSaveDraft() {
      this.$refs.form.validate(valid => {
        if (valid) {
          // 这里添加暂存公告的接口调用
        }
      })
    },
    /** 发布公告 */
    handlePublish() {
      this.$refs.form.validate(valid => {
        if (valid) {
          // 这里添加发布公告的接口调用
        }
      })
    },
    /** 评价管理 */
    handleEvaluate() {
      this.evaluateVisible = true
      this.getEvaluateList()
    },
    /** 获取评价列表 */
    getEvaluateList() {
      // 这里添加获取评价列表的接口调用
    },
    /** 评价查询 */
    handleEvaluateQuery() {
      this.getEvaluateList()
    },
    /** 评价操作 */
    handleRate(row) {
      // 评价操作逻辑
    },
    /** 打开选择厂商理由弹窗 */
    openReasonDialog() {
      this.reasonDialogVisible = true
      this.selectedReason = ''
    },
    /** 确认选择厂商理由 */
    confirmReason() {
      const reasonMap = {
        '1': '经过本学校学生家长委员会讨论的形式，该企业校服的质量、款式、价格得到学生和家长认可。',
        '2': '根据本学校校服公开招标的形式，该企业校服的质量、款式、价格得到学生和家长认可。',
        '3': '经过本学校学生、学生代表等讨论的形式，该企业校服的质量、款式、价格得到学生和家长认可。',
        '4': '根据文件精神，并征求家长、学生代表的一致意见，该企业校服的质量、款式、价格得到学生和家长认可。'
      }
      this.form.reason = reasonMap[this.selectedReason]
      this.reasonDialogVisible = false
    },
    /** 选择售后须知模板 */
    selectTemplate() {
      if (this.afterSaleSupport === '1') {
        this.supportTemplateVisible = true
        this.selectedSupportTemplate = '1'
      } else {
        this.notSupportTemplateVisible = true
        this.selectedNotSupportTemplate = '1'
      }
    },
    /** 确认售后须知 */
    confirmAfterSale() {
      this.form.afterSale = {
        support: this.afterSaleSupport === '1',
        content: this.afterSaleContent
      }
      this.afterSaleDialogVisible = false
    },
    /** 确认支持退换货模板 */
    confirmSupportTemplate() {
      const supportTemplateMap = {
        '1': '1. 关于退货:\n' +
          '(1) 统购商品公告截止日期之前的订单可申请退款。\n' +
          '(2) 统购商品公告截止日期之后，商品处于生产阶段，若退货可现订订单中厂家电话进行协商。\n' +
          '【注: 特殊体型（包含特殊定制）为定制商品，付款截止后统一按照订单信息量身定生产，不支持无理由退换货，请谨慎选购。】\n' +
          '2. 关于换货尺码: 可在"阳光智园"APP，"我的订单"中找到该订单进入订单详情, 选择需更换的商品点击"申请换货"进行申请。(若不显示"申请换货"，表示厂商暂未开启线上退换货，可按订订单中厂家电话进行协商。)'
      }

      this.afterSaleContent = supportTemplateMap[this.selectedSupportTemplate]
      this.supportTemplateVisible = false
    },
    /** 确认不支持退换货模板 */
    confirmNotSupportTemplate() {
      const notSupportTemplateMap = {
        '1': '公告付款截止后退换货: 校服为定制商品，付款截止后厂商统一按照订单信息量生产，属于定制，暂不支持退换货，请谨慎选购。'
      }

      this.afterSaleContent = notSupportTemplateMap[this.selectedNotSupportTemplate]
      this.notSupportTemplateVisible = false
    },
    /** 获取商品列表 */
    getProductList() {
      // 这里添加获取商品列表的接口调用
    },
    /** 商品查询 */
    handleProductQuery() {
      this.getProductList()
    },
    /** 商品多选 */
    handleProductSelectionChange(selection) {
      this.selectedProducts = selection
    },
    /** 确认选择商品 */
    confirmProducts() {
      this.form.products = this.selectedProducts
      this.productDialogVisible = false
    },
    /** 打开厂商列表弹窗 */
    openManufacturerDialog() {
      this.manufacturerDialogVisible = true
      this.getManufacturerList()
    },

    /** 获取厂商列表 */
    getManufacturerList() {
      // 模拟数据
      this.manufacturerList = [
        { name: '武汉市汇汉区童星幼儿用品经营部' },
        { name: '蒲城荟格乐科教设备有限公司' },
        { name: '宝鸡市扶风县美姿服装厂' },
        { name: '正定县依诺服装加工厂' },
        { name: '山西顺贝达服饰有限公司' },
        { name: '成都锐达服装有限公司' },
        { name: '河南辰逸服饰有限公司' },
        { name: '济宁文美服装服饰有限公司' },
        { name: '陕西仁翔服饰有限公司' },
        { name: '桂林丰源服饰有限责任公司' }
      ]
      this.total = 3265
      this.totalPages = Math.ceil(this.total / this.pageSize)
    },

    /** 查询厂商 */
    searchManufacturer() {
      this.currentPage = 1
      this.getManufacturerList()
    },

    /** 添加合作厂商 */
    addManufacturer(row) {
      // 直接添加选中的厂商到合作厂商列表
      this.cooperationManufacturerList.push({
        name: row.name,
        phone: '暂无',  // 假设搜索结果没有电话，设置默认值
      });

      // 更新选中的厂商名称到主表单
      this.form.manufacturerName = row.name;

      // 只关闭新增厂商弹窗，保持厂商列表弹窗打开
      this.manufacturerDialogVisible = false;

      this.$message.success(`已添加厂商：${row.name}`);
    },

    /** 查看厂商信息 */
    viewManufacturer(row) {
      // 查看厂商信息
    },

    /** 查看合同 */
    viewContract(row) {
      // 查看合同
    },

    /** 添加采购合同 */
    addContract(row) {
      // 添加采购合同
    },

    /** 取消合作 */
    cancelCooperation(row) {
      this.$confirm('确认取消与该厂商的合作吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 这里添加取消合作的接口调用
        this.$message.success('取消合作成功')
        const index = this.cooperationManufacturerList.findIndex(item => item.name === row.name)
        if (index !== -1) {
          this.cooperationManufacturerList.splice(index, 1)
        }
      }).catch(() => { })
    },

    /** 每页数量变化 */
    handleSizeChange(val) {
      this.pageSize = val
      this.getManufacturerList()
    },

    /** 页码变化 */
    handleCurrentChange(val) {
      this.currentPage = val
      this.getManufacturerList()
    }
  }
}
</script>

<style scoped>
.search-container {
  margin-bottom: 20px;
}

.title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 18px;
  font-weight: bold;
}

.filter-container {
  display: flex;
  align-items: center;
  gap: 10px;
}

.manufacturer-link {
  margin-top: 5px;
  font-size: 13px;
}

.manufacturer-link a {
  color: #409EFF;
  text-decoration: none;
}

.reference-fee {
  color: #67C23A;
}

.text-center {
  text-align: center;
  line-height: 32px;
}

.dialog-footer {
  text-align: center;
  margin-top: 20px;
}

.el-dialog__body {
  padding: 20px 40px;
}

.after-sale-form {
  padding: 0 20px;
}

.tip-text {
  color: #ff4949;
  font-size: 13px;
  margin-bottom: 10px;
}

.template-btn {
  margin: 10px;
}

.no-data-tip {
  text-align: center;
  color: #909399;
  padding: 30px 0;
}

.template-radio-group {
  display: flex;
  flex-direction: column;
  margin: 20px 0;
}

.template-radio-group .el-radio {
  margin-left: 0;
  margin-bottom: 20px;
  white-space: normal;
  line-height: 1.5;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-info {
  font-size: 13px;
  color: #606266;
}

.manufacturer-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.manufacturer-logo {
  margin-right: 20px;
}

.manufacturer-title h2 {
  margin-top: 0;
  margin-bottom: 10px;
}

.manufacturer-subtitle {
  margin-top: 5px;
}

.cert-tag {
  margin-left: 10px;
  color: #67C23A;
}

.cert-tag i {
  margin-right: 4px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
  margin-bottom: 20px;
}

.info-item {
  margin-bottom: 10px;
}

.info-item-full {
  grid-column: span 3;
}

.info-label {
  color: #606266;
  margin-right: 10px;
  font-weight: 500;
}

.info-value {
  color: #303133;
}

.manufacturer-description {
  line-height: 1.8;
  margin-bottom: 20px;
  color: #606266;
  text-indent: 2em;
  padding: 0 10px;
}

.manufacturer-equipment {
  margin-bottom: 20px;
  padding: 0 10px;
}

.manufacturer-equipment .el-tag {
  margin-right: 10px;
  margin-bottom: 10px;
}

.manufacturer-qualification {
  margin-bottom: 20px;
  display: flex;
  justify-content: center;
}

.image-slot {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background-color: #f5f7fa;
  color: #909399;
}

.image-slot i {
  font-size: 28px;
  margin-bottom: 10px;
}

.manufacturer-detail-dialog .el-divider__text {
  background-color: #f5f7fa;
  color: #409EFF;
  font-weight: bold;
}
</style>