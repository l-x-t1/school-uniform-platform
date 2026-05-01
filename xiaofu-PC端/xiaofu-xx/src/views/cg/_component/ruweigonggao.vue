<template>
    <div class="detail-panel">
        <div class="header" style="display: flex; align-items: center">
            <h3>备选公告展示</h3>
            <div style="margin-left: 20px">
                <el-button icon="el-icon-upload" type="primary" v-hasPermi="['school:rwggzs:add']" v-if="rwggzsList.length === 0"
                    @click="handleAdd">发布备选公告</el-button>
            </div>
        </div>

        <el-table v-loading="loading" :data="rwggzsList">
            <el-table-column type="index" label="序号" width="55" align="center" />
            <el-table-column label="名称" align="center" prop="name" />
            <el-table-column label="是否发布" align="center" prop="isRelease">
                <template slot-scope="scope">
                    <el-tag v-if="scope.row.isRelease === '1'" type="success">已发布</el-tag>
                    <el-tag v-else type="danger">未发布</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="创建时间" align="center" prop="createTime" />

            <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                    <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                        v-if="scope.row.isRelease === '0'" v-hasPermi="['school:rwggzs:edit']">修改</el-button>
                    <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                        v-if="scope.row.isRelease === '0'" v-hasPermi="['school:rwggzs:remove']">删除</el-button>
                    <el-button size="mini" type="text" icon="el-icon-view" @click="getBcList(scope.row)"
                        v-if="scope.row.isRelease === '1'" v-hasPermi="['school:rwggzs:bc']">补充</el-button>
                    <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)"
                        v-if="scope.row.isRelease === '1'">详情</el-button>
                </template>
            </el-table-column>
        </el-table> 

        <!-- 添加或修改企业备选公告对话框 -->
        <el-dialog :title="title" :visible.sync="open" width="1300px" append-to-body>
            <el-form ref="form" :model="form" :rules="rules" label-width="80px">
                <el-form-item label="名称" prop="name">
                    <el-input v-model="form.name" placeholder="请输入名称" :readonly="!xgType" />
                </el-form-item>
                <el-form-item label="是否发布" prop="isRelease">
                    <el-radio-group v-model="form.isRelease" :disabled="!xgType">
                        <el-radio :label="'1'">是</el-radio>
                        <el-radio :label="'0'">否</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="备选公告" prop="file">
                    <file-upload v-model="form.file" :fileType="['pdf']" :gaizhang="true" :disabled="!xgType" />
                </el-form-item>
                <!-- 企业信息表格 -->
                <div style="margin-top: 20px" class="qyTable">
                    <el-table :data="form.supplierList" border>
                        <el-table-column prop="name">
                            <template slot="header" slot-scope="scope">
                                <div style="display: flex; align-items: center;">
                                    <span style="color: #ff4949;">*</span> 
                                    <span style="margin-left: 5px;">企业名称</span>
                                </div>
                            </template>
                            <template slot-scope="scope">
                                <el-form-item label-width="0" :prop="'supplierList.' + scope.$index + '.name'"
                                    :rules="{ required: true, message: '请输入企业名称' }">
                                    <el-input v-model="scope.row.name" placeholder="请输入企业名称" :readonly="!xgType" />
                                </el-form-item>
                            </template>
                        </el-table-column>
                        <el-table-column label="企业介绍链接">
                            <template slot-scope="scope">
                                <el-form-item label-width="0">
                                    <el-input v-model="scope.row.url" type="textarea"
                                        placeholder="请输入链接(多个请用逗号分隔)，需要添加上 http:// 或 https:// 前缀" :readonly="!xgType" />
                                </el-form-item>
                            </template>
                        </el-table-column>
                        <el-table-column> 
                            <template slot="header" slot-scope="scope">
                                <div style="display: flex; align-items: center;"> 
                                    <span style="">企业介绍视频</span>
                                    <span style="color: #ff4949;">（上传MP4格式，不超过500M）</span>
                                </div>
                            </template>
                            <template slot-scope="scope">
                                <el-form-item label-width="0">
                                    <file-upload v-model="scope.row.videoUrl" :fileType="['mp4']" :fileSize="500"
                                    :isShowTip="false" :fileList="scope.row.url" :disabled="!xgType" />
                                </el-form-item>
                            </template>
                        </el-table-column>
                        <el-table-column >
                            <template slot="header" slot-scope="scope">
                                <div style="display: flex; align-items: center;"> 
                                    <span style="color: #ff4949;">*</span> 
                                    <span style="">企业介绍文件</span>
                                    <span style="color: #ff4949;">（上传PDF）</span>
                                </div>
                            </template>
                            <template slot-scope="scope">
                                <el-form-item label-width="0" :prop="'supplierList.' + scope.$index + '.file'"
                                    :rules="{ required: true, message: '请上传文件' }">
                                    <file-upload v-model="scope.row.file" :fileType="['pdf']" :fileList="scope.row.file" :isShowTip="false"
                                        :disabled="!xgType" />
                                </el-form-item>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" prop="operate" width="100px" align="center" v-if="xgType">
                            <template slot-scope="scope">
                                <el-button type="primary" icon="el-icon-plus" circle size="mini"
                                    v-if="scope.$index === form.supplierList.length - 1 && xgType"
                                    @click="addEnterprise"></el-button>
                                <el-button type="danger" icon="el-icon-minus" circle size="mini"
                                    @click="deleteEnterprise(scope.$index)"
                                    v-show="form.supplierList.length > 1 && xgType"></el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </div>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" v-if="xgType" @click="submitForm">确 定</el-button>
                <el-button @click="cancel" v-if="xgType">取 消</el-button>
            </div>
        </el-dialog>
        <!-- 补充列表 -->
        <el-dialog title="补充列表" :visible.sync="bcListShow" width="1000px" append-to-body>
            <el-button type="primary" size="mini" icon="el-icon-plus" @click="handleBcAdd" style="margin-bottom: 10px;" v-hasPermi="['school:rwggzs:add']">新增</el-button>
            <el-table :data="bcList" border>
                <el-table-column label="标题"  prop="title" align="center" />
                <el-table-column label="创建时间" prop="createTime" align="center" />
                <el-table-column label="操作" align="center" >
                    <template slot-scope="scope">
                        <el-button type="primary" size="mini" icon="el-icon-edit" @click="handleBcUpdate(scope.row)">查看</el-button> 
                    </template>
                </el-table-column>
            </el-table>
        </el-dialog>
        <!-- 补充弹窗 -->
        <el-dialog title="补充公告" :visible.sync="bcShow" width="1300px" append-to-body >
            <el-form ref="bcForm" :model="bcForm" :rules="bcRules" label-width="80px">
                <el-form-item label="标题" prop="title">
                    <el-input v-model="bcForm.title" placeholder="请输入标题" :readonly="!bcXgType" />
                </el-form-item> 
                <el-form-item label="附件" prop="file">
                    <file-upload v-model="bcForm.file" :fileType="['pdf']" :fileList="bcForm.file" :limit="1" :disabled="!bcXgType" />
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" v-if="bcXgType" @click="bcSubmitForm">确 定</el-button>
                <el-button @click="bcCancel">取 消</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { listRwggzs, getRwggzs, delRwggzs, addRwggzs, updateRwggzs, buchong, getBcList } from "@/api/cg/rwggzs";
import { getXfptnotice } from "@/api/system/xfptnotice";
export default {
    name: "Rwggzs",
    props: {
        formId: {},
    },
    data() {
        return {
            // 遮罩层
            loading: true,

            // 总条数
            total: 0,
            // 企业备选公告表格数据
            rwggzsList: [],
            // 弹出层标题
            title: "",
            // 是否显示弹出层
            open: false,
            // 查询参数
            queryParams: {
                pageNum: 1,
                pageSize: 10,
                teamId: this.formId,
            },
            // 表单参数
            form: {
                id: null,
                name: null,
                isRelease: null,
                file: null,
                teamId: this.formId,
                supplierList: [
                    {
                        name: '',
                        file: '',
                        videoUrl: '',
                        url: ''
                    }
                ]
            },
            // 表单校验
            rules: {
                name: [{ required: true, message: "请输入名称", trigger: "blur" }],
                isRelease: [{ required: true, message: "请选择是否发布", trigger: "blur" }],
                file: [{ required: true, message: "请上传附件", trigger: "blur" }],
            },
            xgType: false,//是否修改,
            bcList: [],//补充列表
            bcShow: false,//是否显示补充列表
            bcForm:{
                title:'',
                teamId:this.formId,
                catagory:'QYRWGG',
                file:'',
                id:null,
                type:'2'
            },
            bcRules:{
                title: [{ required: true, message: "请输入标题", trigger: "blur" }],
                file: [{ required: true, message: "请上传附件", trigger: "blur" }],
            },
            bcListShow:false,//是否显示补充列表
            bcId:null,//补充id
            bcXgType:true,//是否修改补充
        };
    },
    created() {
        this.getList();
    },
    methods: {
        /** 查询企业备选公告列表 */
        getList() {
            this.loading = true;
            listRwggzs(this.queryParams).then(response => {
                this.rwggzsList = response.rows;
                this.total = response.total;
                this.loading = false;
            });
        },
        // 取消按钮
        cancel() {
            this.open = false;
            this.reset();
        },
        // 表单重置
        reset() {
            this.form = {
                id: null,
                name: null,
                isRelease: null,
                file: null,
                teamId: this.formId,
                supplierList: [
                    {
                        name: '',
                        file: '',
                        videoUrl: '',
                        url: ''
                    }
                ]
            };
            this.resetForm("form");
        },
        // 补充表单重置
        bcReset() {
            this.bcForm = {
                title:'',
                teamId:this.formId,
                catagory:'QYRWGG',
                file:'',
                id:this.bcId,
                type:'2'
            };
            this.resetForm("bcForm");
        },
        // 添加企业
        addEnterprise() {
            this.form.supplierList.push({
                name: '',
                file: '',
                videoUrl: '',
                url: ''
            });
        },
        // 删除企业
        deleteEnterprise(index) {
            this.form.supplierList.splice(index, 1);
        },


        /** 新增按钮操作 */
        handleAdd() {
            this.xgType = true;
            this.reset();
            this.open = true;
            this.title = "添加企业备选公告";
        },
        /** 修改按钮操作 */
        handleUpdate(row) {
            this.xgType = true;
            this.reset();
            const id = row.id;
            getRwggzs(id).then(response => {
                this.form = response.data;
                this.open = true;
                this.title = "修改企业备选公告";
            });
        },
        /** 提交按钮 */
        submitForm() {
            this.$refs["form"].validate(valid => {
                if (valid) {
                    if (this.form.id != null) {
                        updateRwggzs(this.form).then(response => {
                            this.$modal.msgSuccess("修改成功");
                            this.open = false;
                            this.getList();
                        });
                    } else {
                        addRwggzs(this.form).then(response => {
                            this.$modal.msgSuccess("新增成功");
                            this.open = false;
                            this.getList();
                            this.$emit('handleGetProcessMenu',response.data)
                        });
                    }
                }
            });
        },
        /** 删除按钮操作 */
        handleDelete(row) {
            const ids = row.id;
            this.$modal.confirm('是否确认删除企业备选公告编号为"' + ids + '"的数据项？').then(function () {
                return delRwggzs(ids);
            }).then(() => {
                this.getList();
                this.$modal.msgSuccess("删除成功");
            }).catch(() => { });
        },

        /** 查看按钮操作 */
        handleView(row) {
            this.xgType = false;
            this.reset();
            const id = row.id;
            getRwggzs(id).then(response => {
                this.form = response.data;
                this.open = true;
                this.title = "查看企业备选公告";
            });
        },
        // 获取补充列表
        getBcList(row) {
            this.bcId = row.id ? row.id : row;
            const data = {
                id:row.id ? row.id : row,
                type:'2',
                catagory:'QYRWGG', 
            } 
            getBcList(data).then(response => {
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
        // 修改补充
        bcSubmitForm() {
            this.$refs["bcForm"].validate(valid => {
                if (valid) {
                    buchong(this.bcForm).then(response => {
                        this.$modal.msgSuccess("补充成功");
                        this.bcShow = false;
                        this.getBcList(this.bcId);
                    });
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
            const id = row.id
            this.bcReset();
            getXfptnotice(id).then(response => {
                this.bcXgType = false;
                this.bcForm = response.data; 
                this.bcForm.file = response.data.ggFile;
                this.bcShow = true;
            });
        },
    }
};
</script>
<style lang="scss" scoped>
.detail-panel {
    padding: 20px 0;
}

.detail-panel h3 {
    font-size: 18px;
    font-weight: bold;
    color: #303133;
}

.header {
    margin-top: 0;
    margin-bottom: 15px;
    padding-bottom: 5px;
    border-bottom: 1px solid #ebeef5;
}

::v-deep .qyTable {
    .el-icon-document {
        max-width: 150px !important;
    }
}
</style>
