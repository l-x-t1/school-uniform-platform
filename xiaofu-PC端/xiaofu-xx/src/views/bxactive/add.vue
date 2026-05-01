<template>
    <div class="app-container">
        <!-- 步骤条 -->
        <el-steps :active="active" finish-status="success" align-center>
            <el-step title="基本信息"></el-step>
            <el-step title="校服要求及报价"></el-step>
            <el-step title="联系人信息"></el-step>
        </el-steps>

        <!-- 基本信息表单 -->
        <div v-show="active === 0" class="form-container">
            <el-form :model="baseInfoForm" :rules="baseInfoRules" ref="baseInfoForm" label-width="120px" class="form">
                <el-form-item label="投票活动名称：" prop="voteName">
                    <el-input v-model="baseInfoForm.voteName" placeholder="请输入投票活动名称" style="width: 500px;"></el-input>
                </el-form-item>
                <el-form-item label="适用年级/院系/专业：" prop="applyScope">
                    <el-input type="textarea" v-model="baseInfoForm.applyScope" placeholder="请输入适用年级/院系/专业"
                        style="width: 500px;" :autosize="{ minRows: 2, maxRows: 4 }" maxlength="200"
                        show-word-limit></el-input>
                </el-form-item>
                <el-form-item label="投票活动介绍：" prop="voteDesc">
                    <el-input type="textarea" v-model="baseInfoForm.voteDesc" placeholder="请输入投票活动介绍"
                        style="width: 500px;" :autosize="{ minRows: 6, maxRows: 10 }" maxlength="500"
                        show-word-limit></el-input>
                </el-form-item>
                <el-form-item label="参与厂商：" prop="manufacturer">
                    <el-button type="primary" size="small" @click="showManufacturerDialog">邀请</el-button>
                    <span class="hint-text">邀请厂商须知：最多25家厂商</span>
                </el-form-item>
                <el-form-item label="上传资料截止时间：" prop="uploadDeadline">
                    <el-date-picker v-model="baseInfoForm.uploadDeadline" type="datetime" placeholder="请选择上传资料截止时间"
                        style="width: 220px;"></el-date-picker>
                </el-form-item>
                <el-form-item label="交易时间：" prop="tradeTime">
                    <el-date-picker v-model="baseInfoForm.tradeTime" type="datetime" placeholder="请选择交易时间"
                        style="width: 220px;"></el-date-picker>
                </el-form-item>
                <el-form-item label="投票规则说明：" prop="voteRule">
                    <el-input type="textarea" v-model="baseInfoForm.voteRule" placeholder="请输入投票规则说明"
                        style="width: 500px;" :autosize="{ minRows: 4, maxRows: 6 }" maxlength="200"
                        show-word-limit></el-input>
                </el-form-item>
                <el-form-item label="其他说明：">
                    <el-input type="textarea" v-model="baseInfoForm.otherDesc" placeholder="请输入其他说明"
                        style="width: 500px;" :autosize="{ minRows: 4, maxRows: 6 }" maxlength="200"
                        show-word-limit></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="nextStep(0)">暂存并下一步</el-button>
                </el-form-item>
            </el-form>
        </div>

        <!-- 校服要求及报价表单 -->
        <div v-show="active === 1" class="form-container">
            <el-form :model="requirementForm" :rules="requirementRules" ref="requirementForm" label-width="120px"
                class="form">
                <div class="section-title">校服要求</div>
                <div class="item-container">
                    <div v-for="(item, index) in requirementForm.requirements" :key="index" class="item-row">
                        <el-row :gutter="20">
                            <el-col :span="5">
                                <el-form-item :label="index === 0 ? '校服品类：' : ''"
                                    :prop="'requirements[' + index + '].category'"
                                    :rules="{ required: true, message: '请选择校服品类', trigger: 'change' }">
                                    <el-select v-model="item.category" placeholder="请选择">
                                        <el-option label="夏季校服" value="summer"></el-option>
                                        <el-option label="冬季校服" value="winter"></el-option>
                                        <el-option label="运动服" value="sport"></el-option>
                                    </el-select>
                                </el-form-item>
                            </el-col>
                            <el-col :span="5">
                                <el-form-item :label="index === 0 ? '校服款式：' : ''"
                                    :prop="'requirements[' + index + '].style'"
                                    :rules="{ required: true, message: '请选择校服款式', trigger: 'change' }">
                                    <el-select v-model="item.style" placeholder="请选择">
                                        <el-option label="T恤" value="tshirt"></el-option>
                                        <el-option label="衬衫" value="shirt"></el-option>
                                        <el-option label="外套" value="coat"></el-option>
                                    </el-select>
                                </el-form-item>
                            </el-col>
                            <el-col :span="3">
                                <el-form-item :label="index === 0 ? '购买套数/人：' : ''"
                                    :prop="'requirements[' + index + '].quantity'"
                                    :rules="{ required: true, message: '请输入套数', trigger: 'blur' }">
                                    <el-input-number v-model="item.quantity" :min="1" :max="10"
                                        size="small"></el-input-number>
                                </el-form-item>
                            </el-col>
                            <el-col :span="9">
                                <el-form-item :label="index === 0 ? '面料成分要求：' : ''"
                                    :prop="'requirements[' + index + '].fabricRequirement'"
                                    :rules="{ required: true, message: '请输入面料要求', trigger: 'blur' }">
                                    <el-input type="textarea" v-model="item.fabricRequirement" placeholder="请输入面料成分要求"
                                        :autosize="{ minRows: 2, maxRows: 4 }"></el-input>
                                </el-form-item>
                            </el-col>
                            <el-col :span="2" class="operation-buttons">
                                <el-button type="primary" icon="el-icon-plus" circle size="mini" @click="addRequirement"
                                    v-if="index === 0"></el-button>
                                <el-button type="danger" icon="el-icon-minus" circle size="mini"
                                    @click="removeRequirement(index)" v-if="index > 0"></el-button>
                            </el-col>
                        </el-row>
                    </div>
                </div>

                <div class="section-title">校服报价要求</div>
                <el-form-item label="订购模式：" prop="orderMode">
                    <el-radio-group v-model="requirementForm.orderMode">
                        <el-radio label="1">家长自行填写模式 (校园加盟店)</el-radio>
                        <el-radio label="2">家长自助模式 (校内职员)</el-radio>
                        <el-radio label="3">财政统购模式</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-row :gutter="20">
                    <el-col :span="6">
                        <el-form-item label="男款建议报价：">
                            <el-input v-model="requirementForm.malePriceRecommend" placeholder="建议报价">
                                <template slot="append">元/人</template>
                            </el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="6">
                        <el-form-item label="女款建议报价：">
                            <el-input v-model="requirementForm.femalePriceRecommend" placeholder="建议报价">
                                <template slot="append">元/人</template>
                            </el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item label="价格说明：" prop="priceDesc">
                    <el-input type="textarea" v-model="requirementForm.priceDesc" placeholder="请输入价格说明"
                        style="width: 500px;" :autosize="{ minRows: 4, maxRows: 6 }" maxlength="200"
                        show-word-limit></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button @click="prevStep">上一步</el-button>
                    <el-button type="primary" @click="nextStep(1)">暂存并下一步</el-button>
                </el-form-item>
            </el-form>
        </div>

        <!-- 联系人信息表单 -->
        <div v-show="active === 2" class="form-container">
            <el-form :model="contactForm" :rules="contactRules" ref="contactForm" label-width="120px" class="form">
                <div class="section-title">联系人信息</div>
                <el-form-item label="联系人：" prop="contactName">
                    <el-input v-model="contactForm.contactName" placeholder="请输入联系人" style="width: 350px;"></el-input>
                </el-form-item>
                <el-form-item label="联系电话：" prop="contactPhone">
                    <el-input v-model="contactForm.contactPhone" placeholder="请输入联系电话" style="width: 350px;"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button @click="prevStep">上一步</el-button>
                    <el-button type="primary" @click="submitForm">提交发布</el-button>
                    <el-button @click="saveForm">暂存</el-button>
                </el-form-item>
            </el-form>
        </div>

        <!-- 厂商选择对话框 -->
        <el-dialog title="邀请参加厂商" :visible.sync="manufacturerDialogVisible" width="60%">
            <div class="dialog-search">
                <el-input placeholder="厂商名称" v-model="searchParams.manufacturerName"
                    style="width: 200px; margin-right: 10px;"></el-input>
                <el-select v-model="searchParams.region" placeholder="注册资本" style="width: 150px; margin-right: 10px;">
                    <el-option label="全部" value=""></el-option>
                    <el-option label="0-100万" value="0-100"></el-option>
                    <el-option label="100-500万" value="100-500"></el-option>
                    <el-option label="500万以上" value="500-"></el-option>
                </el-select>
                <el-select v-model="searchParams.type" placeholder="厂商规模" style="width: 150px; margin-right: 10px;">
                    <el-option label="全部" value=""></el-option>
                    <el-option label="大型企业" value="large"></el-option>
                    <el-option label="中型企业" value="medium"></el-option>
                    <el-option label="小型企业" value="small"></el-option>
                </el-select>
                <el-button type="primary" size="small" @click="searchManufacturers">查询</el-button>
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
                <el-table-column label="操作" width="100" align="center">
                    <template slot-scope="scope">
                        <el-button type="primary" size="small" @click="selectManufacturer(scope.row)">选择</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div class="pagination-container">
                <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange"
                    :current-page="pageParams.pageNum" :page-sizes="[10, 20, 30, 50]" :page-size="pageParams.pageSize"
                    layout="prev, pager, next, jumper, total" :total="total">
                </el-pagination>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button @click="manufacturerDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="confirmSelectManufacturers">确定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
export default {
    name: "BxactiveAdd",
    data() {
        return {
            active: 0, // 当前步骤
            // 基本信息表单
            baseInfoForm: {
                voteName: '',
                applyScope: '',
                voteDesc: '校服承载着学校文化的深刻内涵，同时又承载着学校文化的浓厚底蕴。在校园里，校服是一道亮丽的风景线。在外，校服整齐个性的学生形象，彰显了学校的教学水平，提升了学校的形象。所以，我们将学校校服采购的决定权交给家长委员会，家长委员会通过参观考察、实物对比等形式对各企业进行了解调查，从企业规模、产品质量、业态能力等，综合服务水平，社会形象，从中挑选出若干家企业进行投票选型。希望各位家长参与投票，可结合家庭情况，帮助完成校服采购决策。',
                manufacturer: [],
                uploadDeadline: '',
                tradeTime: '',
                voteRule: '',
                otherDesc: ''
            },
            // 校服要求及报价表单
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
            // 联系人信息表单
            contactForm: {
                contactName: '',
                contactPhone: ''
            },
            // 表单验证规则
            baseInfoRules: {
                voteName: [
                    { required: true, message: '请输入投票活动名称', trigger: 'blur' }
                ],
                applyScope: [
                    { required: true, message: '请输入适用年级/院系/专业', trigger: 'blur' }
                ],
                voteDesc: [
                    { required: true, message: '请输入投票活动介绍', trigger: 'blur' }
                ],
                manufacturer: [
                    { required: true, message: '请选择参与厂商', trigger: 'change' }
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
            requirementRules: {
                orderMode: [
                    { required: true, message: '请选择订购模式', trigger: 'change' }
                ]
            },
            contactRules: {
                contactName: [
                    { required: true, message: '请输入联系人', trigger: 'blur' }
                ],
                contactPhone: [
                    { required: true, message: '请输入联系电话', trigger: 'blur' },
                    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
                ]
            },
            // 厂商选择相关
            manufacturerDialogVisible: false,
            manufacturerList: [],
            selectedManufacturers: [],
            searchParams: {
                manufacturerName: '',
                region: '',
                type: ''
            },
            pageParams: {
                pageNum: 1,
                pageSize: 10
            },
            total: 0
        };
    },
    methods: {
        // 下一步
        nextStep(step) {
            const formNames = ['baseInfoForm', 'requirementForm', 'contactForm'];
            this.$refs[formNames[step]].validate(valid => {
                if (valid) {
                    this.active++;
                } else {
                    this.$message.error('请完善表单信息');
                    return false;
                }
            });
        },
        // 上一步
        prevStep() {
            this.active--;
        },
        // 添加校服要求
        addRequirement() {
            this.requirementForm.requirements.push({
                category: '',
                style: '',
                quantity: 1,
                fabricRequirement: ''
            });
        },
        // 删除校服要求
        removeRequirement(index) {
            this.requirementForm.requirements.splice(index, 1);
        },
        // 显示厂商选择对话框
        showManufacturerDialog() {
            this.manufacturerDialogVisible = true;
            this.getManufacturerList();
        },
        // 获取厂商列表
        getManufacturerList() {
            // 模拟获取厂商列表数据
            setTimeout(() => {
                this.manufacturerList = [
                    {
                        id: '1',
                        name: '武汉市汉口区复兴制服儿童装品部',
                        employees: 100,
                        capital: 10.00,
                        establishTime: '2018-07-23',
                        status: '未选择'
                    },
                    {
                        id: '2',
                        name: '湖北智梦纺织科技设备有限公司',
                        employees: 50,
                        capital: 518.00,
                        establishTime: '2011-06-03',
                        status: '未选择'
                    },
                    {
                        id: '3',
                        name: '宝鸡市纺织服装服饰厂',
                        employees: 27,
                        capital: 10.00,
                        establishTime: '2013-04-01',
                        status: '未选择'
                    },
                    {
                        id: '4',
                        name: '正荣服饰服装加工厂',
                        employees: 3,
                        capital: 5.00,
                        establishTime: '2019-02-19',
                        status: '未选择'
                    },
                    {
                        id: '5',
                        name: '山西阿西服装有限公司',
                        employees: 80,
                        capital: 1000.00,
                        establishTime: '2018-02-07',
                        status: '未选择'
                    },
                    {
                        id: '6',
                        name: '成都神龙服装有限公司',
                        employees: 190,
                        capital: 2100.00,
                        establishTime: '2006-10-24',
                        status: '未选择'
                    },
                    {
                        id: '7',
                        name: '河南荣金服饰有限公司',
                        employees: 100,
                        capital: 500.00,
                        establishTime: '2016-12-28',
                        status: '未选择'
                    },
                    {
                        id: '8',
                        name: '济宁文景服装股份有限公司',
                        employees: 300,
                        capital: 101.00,
                        establishTime: '2010-12-15',
                        status: '未选择'
                    },
                    {
                        id: '9',
                        name: '陕西口上服饰有限公司',
                        employees: 80,
                        capital: 1030.00,
                        establishTime: '2008-02-02',
                        status: '未选择'
                    },
                    {
                        id: '10',
                        name: '桂林华服饰制衣有限公司',
                        employees: 45,
                        capital: 300.00,
                        establishTime: '2006-09-07',
                        status: '未选择'
                    }
                ];
                this.total = 327;
            }, 300);
        },
        // 搜索厂商
        searchManufacturers() {
            this.pageParams.pageNum = 1;
            this.getManufacturerList();
        },
        // 选择厂商
        selectManufacturer(row) {
            const index = this.selectedManufacturers.findIndex(item => item.id === row.id);
            if (index === -1) {
                this.selectedManufacturers.push(row);
            } else {
                this.$message.warning('该厂商已被选择');
            }
        },
        // 表格多选变化事件
        handleSelectionChange(selection) {
            this.selectedManufacturers = selection;
        },
        // 确认选择厂商
        confirmSelectManufacturers() {
            if (this.selectedManufacturers.length === 0) {
                this.$message.warning('请至少选择一个厂商');
                return;
            }
            this.baseInfoForm.manufacturer = this.selectedManufacturers;
            this.manufacturerDialogVisible = false;
            this.$message.success(`已选择 ${this.selectedManufacturers.length} 家厂商`);
        },
        // 分页大小变化
        handleSizeChange(val) {
            this.pageParams.pageSize = val;
            this.getManufacturerList();
        },
        // 当前页变化
        handleCurrentChange(val) {
            this.pageParams.pageNum = val;
            this.getManufacturerList();
        },
        // 提交表单
        submitForm() {
            this.$refs.contactForm.validate(valid => {
                if (valid) {
                    // 提交所有表单数据
                    const formData = {
                        ...this.baseInfoForm,
                        ...this.requirementForm,
                        ...this.contactForm
                    };
                    console.log('提交的表单数据:', formData);

                    // 模拟提交成功
                    this.$modal.msgSuccess('提交成功');
                    this.$router.push('/bxactive');
                } else {
                    this.$message.error('请完善表单信息');
                    return false;
                }
            });
        },
        // 暂存表单
        saveForm() {
            // 暂存表单数据
            const formData = {
                ...this.baseInfoForm,
                ...this.requirementForm,
                ...this.contactForm
            };
            console.log('暂存的表单数据:', formData);

            // 模拟暂存成功
            this.$modal.msgSuccess('暂存成功');
        }
    }
};
</script>

<style scoped>
.app-container {
    padding: 20px;
}

.el-steps {
    margin-bottom: 30px;
}

.form-container {
    background-color: #f9f9f9;
    padding: 20px;
    border-radius: 4px;
}

.form {
    max-width: 800px;
    margin: 0 auto;
}

.section-title {
    font-size: 16px;
    font-weight: bold;
    margin: 20px 0 15px;
    padding-left: 10px;
    border-left: 3px solid #409EFF;
}

.item-container {
    margin-bottom: 20px;
}

.item-row {
    margin-bottom: 10px;
    background-color: #f5f7fa;
    padding: 15px;
    border-radius: 4px;
}

.operation-buttons {
    display: flex;
    justify-content: center;
    align-items: center;
}

.dialog-search {
    margin-bottom: 20px;
    display: flex;
    align-items: center;
}

.pagination-container {
    margin-top: 15px;
    text-align: right;
}

.hint-text {
    color: #909399;
    font-size: 12px;
    margin-left: 10px;
}

:deep(.el-textarea__inner) {
    font-family: Arial, sans-serif;
}
</style>
