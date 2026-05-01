<template>
    <div class="app-container">
        <div class="main-content">
            <!-- 搜索区域 -->
            <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
                <el-form-item label="物品名称" prop="name">
                    <el-input v-model="queryParams.name" placeholder="请输入物品名称" clearable style="width: 240px"
                        @keyup.enter.native="handleQuery" />
                </el-form-item>
                <el-form-item label="生产厂家" prop="manufacturer">
                    <el-input v-model="queryParams.manufacturer" placeholder="请输入生产厂家" clearable style="width: 240px"
                        @keyup.enter.native="handleQuery" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
                    <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
                </el-form-item>
            </el-form>

            <!-- 操作按钮区域 -->
            <el-row :gutter="10" class="mb8">
                <el-col :span="1.5">
                    <el-button type="primary" plain icon="el-icon-plus" @click="handleAdd">新增</el-button>
                </el-col>
                <el-col :span="1.5">
                    <el-button type="success" plain icon="el-icon-edit" :disabled="single"
                        @click="handleUpdate">修改</el-button>
                </el-col>
                <el-col :span="1.5">
                    <el-button type="danger" plain icon="el-icon-delete" :disabled="multiple"
                        @click="handleDelete">删除</el-button>
                </el-col>
                <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
            </el-row>

            <!-- 表格区域 -->
            <el-table v-loading="loading" :data="itemList" @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55" align="center" />
                <el-table-column label="物品编号" align="center" prop="code" />
                <el-table-column label="物品名称" align="center" prop="name" />
                <el-table-column label="物品类别" align="center" prop="category" />
                <el-table-column label="生产厂家" align="center" prop="manufacturer" />
                <el-table-column label="库存数量" align="center" prop="stock" />
                <el-table-column label="单价" align="center" prop="price">
                    <template slot-scope="scope">
                        ¥{{ scope.row.price }}
                    </template>
                </el-table-column>
                <el-table-column label="状态" align="center">
                    <template slot-scope="scope">
                        <el-tag :type="scope.row.status === '1' ? 'success' : 'info'">
                            {{ scope.row.status === '1' ? '在用' : '停用' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
                    <template slot-scope="scope">
                        <el-button size="mini" type="text" icon="el-icon-edit"
                            @click="handleUpdate(scope.row)">修改</el-button>
                        <el-button size="mini" type="text" icon="el-icon-delete"
                            @click="handleDelete(scope.row)">删除</el-button>
                        <el-button size="mini" type="text" icon="el-icon-view"
                            @click="handleView(scope.row)">查看</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 分页区域 -->
            <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum"
                :limit.sync="queryParams.pageSize" @pagination="getList" />
        </div>

        <!-- 右侧详情抽屉 -->
        <el-drawer title="物品详情" :visible.sync="drawer" :with-header="true" :size="500" :destroy-on-close="true"
            direction="rtl">
            <div class="drawer-container" v-if="detailData">
                <div class="drawer-item">
                    <div class="item-img">
                        <el-image
                            :src="detailData.image || require('@/assets/images/wj.png')"
                            fit="cover" style="width: 100%; height: 100%;">
                            <div slot="error" class="image-slot">
                                <i class="el-icon-picture-outline"></i>
                            </div>
                        </el-image>
                    </div>
                    <div class="item-info">
                        <h3>{{ detailData.name }}</h3>
                        <div class="info-row">
                            <span class="label">物品编号：</span>
                            <span class="value">{{ detailData.code }}</span>
                        </div>
                        <div class="info-row">
                            <span class="label">物品类别：</span>
                            <span class="value">{{ detailData.category }}</span>
                        </div>
                        <div class="info-row">
                            <span class="label">生产厂家：</span>
                            <span class="value">{{ detailData.manufacturer }}</span>
                        </div>
                        <div class="info-row">
                            <span class="label">库存数量：</span>
                            <span class="value">{{ detailData.stock }}</span>
                        </div>
                        <div class="info-row">
                            <span class="label">单价：</span>
                            <span class="value">¥{{ detailData.price }}</span>
                        </div>
                        <div class="info-row">
                            <span class="label">状态：</span>
                            <span class="value">
                                <el-tag :type="detailData.status === '1' ? 'success' : 'info'">
                                    {{ detailData.status === '1' ? '在用' : '停用' }}
                                </el-tag>
                            </span>
                        </div>
                    </div>
                </div>
                <el-divider></el-divider>
                <div class="drawer-item">
                    <h4>物品描述</h4>
                    <p class="description">{{ detailData.description || '暂无描述' }}</p>
                </div>
                <el-divider></el-divider>
                <div class="drawer-item">
                    <h4>入库记录</h4>
                    <el-table :data="detailData.records || []" style="width: 100%" size="small">
                        <el-table-column prop="date" label="入库日期" width="180" />
                        <el-table-column prop="quantity" label="入库数量" width="100" />
                        <el-table-column prop="operator" label="操作人" />
                    </el-table>
                    <div v-if="!detailData.records || detailData.records.length === 0" class="no-data">
                        暂无入库记录
                    </div>
                </div>
            </div>
        </el-drawer>

        <!-- 添加/修改对话框 -->
        <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
            <el-form ref="form" :model="form" :rules="rules" label-width="80px">
                <el-form-item label="物品名称" prop="name">
                    <el-input v-model="form.name" placeholder="请输入物品名称" />
                </el-form-item>
                <el-form-item label="物品类别" prop="category">
                    <el-select v-model="form.category" placeholder="请选择物品类别">
                        <el-option v-for="dict in categoryOptions" :key="dict.value" :label="dict.label"
                            :value="dict.value" />
                    </el-select>
                </el-form-item>
                <el-form-item label="生产厂家" prop="manufacturer">
                    <el-input v-model="form.manufacturer" placeholder="请输入生产厂家" />
                </el-form-item>
                <el-form-item label="库存数量" prop="stock">
                    <el-input-number v-model="form.stock" :min="0" :max="9999" />
                </el-form-item>
                <el-form-item label="单价" prop="price">
                    <el-input-number v-model="form.price" :min="0" :precision="2" :step="0.1" />
                </el-form-item>
                <el-form-item label="物品状态">
                    <el-radio-group v-model="form.status">
                        <el-radio label="1">在用</el-radio>
                        <el-radio label="0">停用</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="物品描述">
                    <el-input v-model="form.description" type="textarea" placeholder="请输入内容" />
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="cancel">取 消</el-button>
                <el-button type="primary" @click="submitForm">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
export default {
    name: "WpglIndex",
    data() {
        return {
            // 遮罩层
            loading: false,
            // 选中数组
            ids: [],
            // 非单个禁用
            single: true,
            // 非多个禁用
            multiple: true,
            // 显示搜索条件
            showSearch: true,
            // 总条数
            total: 0,
            // 物品表格数据
            itemList: [],
            // 弹出层标题
            title: "",
            // 是否显示弹出层
            open: false,
            // 侧边抽屉显示状态
            drawer: false,
            // 详情数据
            detailData: null,
            // 类别选项
            categoryOptions: [
                { value: '1', label: '办公用品' },
                { value: '2', label: '电子设备' },
                { value: '3', label: '生活用品' },
                { value: '4', label: '其他' }
            ],
            // 查询参数
            queryParams: {
                pageNum: 1,
                pageSize: 10,
                name: undefined,
                manufacturer: undefined
            },
            // 表单参数
            form: {
                id: undefined,
                name: undefined,
                code: undefined,
                category: undefined,
                manufacturer: undefined,
                stock: 0,
                price: 0,
                status: "1",
                description: undefined
            },
            // 表单校验
            rules: {
                name: [
                    { required: true, message: "物品名称不能为空", trigger: "blur" }
                ],
                category: [
                    { required: true, message: "物品类别不能为空", trigger: "change" }
                ],
                manufacturer: [
                    { required: true, message: "生产厂家不能为空", trigger: "blur" }
                ]
            }
        };
    },
    created() {
        this.getList();
    },
    methods: {
        /** 查询物品列表 */
        getList() {
            this.loading = true;
            // 模拟API请求
            setTimeout(() => {
                this.itemList = [
                    {
                        id: '1',
                        code: 'WP20230001',
                        name: '办公桌',
                        category: '1',
                        manufacturer: '某家具公司',
                        stock: 10,
                        price: 899.99,
                        status: '1',
                        description: '实木办公桌，环保材质，适合长时间办公使用',
                        records: [
                            { date: '2023-01-15', quantity: 5, operator: '张三' },
                            { date: '2023-03-20', quantity: 5, operator: '李四' }
                        ]
                    },
                    {
                        id: '2',
                        code: 'WP20230002',
                        name: '笔记本电脑',
                        category: '2',
                        manufacturer: '联想',
                        stock: 5,
                        price: 5999.00,
                        status: '1',
                        description: 'ThinkPad系列商务本，i5处理器，8GB内存，256GB固态硬盘'
                    },
                    {
                        id: '3',
                        code: 'WP20230003',
                        name: '打印纸',
                        category: '1',
                        manufacturer: '得力',
                        stock: 100,
                        price: 45.00,
                        status: '1',
                        description: 'A4规格，500张/包，适用于激光打印机和喷墨打印机'
                    }
                ];
                this.total = this.itemList.length;
                this.loading = false;
            }, 300);
        },
        // 表单重置
        reset() {
            this.form = {
                id: undefined,
                name: undefined,
                code: undefined,
                category: undefined,
                manufacturer: undefined,
                stock: 0,
                price: 0,
                status: "1",
                description: undefined
            };
            this.resetForm("form");
        },
        /** 搜索按钮操作 */
        handleQuery() {
            this.queryParams.pageNum = 1;
            this.getList();
        },
        /** 重置按钮操作 */
        resetQuery() {
            this.resetForm("queryForm");
            this.handleQuery();
        },
        // 多选框选中数据
        handleSelectionChange(selection) {
            this.ids = selection.map(item => item.id);
            this.single = selection.length !== 1;
            this.multiple = !selection.length;
        },
        /** 新增按钮操作 */
        handleAdd() {
            this.reset();
            this.open = true;
            this.title = "添加物品";
        },
        /** 修改按钮操作 */
        handleUpdate(row) {
            this.reset();
            const id = row.id || this.ids[0];
            // 通常这里会有一个获取详情的API
            const item = this.itemList.find(item => item.id === id);
            if (item) {
                this.form = { ...item };
                this.open = true;
                this.title = "修改物品";
            }
        },
        /** 查看详情 */
        handleView(row) {
            this.detailData = { ...row };
            this.drawer = true;
        },
        /** 提交按钮 */
        submitForm() {
            this.$refs["form"].validate(valid => {
                if (valid) {
                    if (this.form.id) {
                        // 修改操作
                        this.$modal.msgSuccess("修改成功");
                    } else {
                        // 添加操作
                        this.$modal.msgSuccess("新增成功");
                    }
                    this.open = false;
                    this.getList();
                }
            });
        },
        /** 删除按钮操作 */
        handleDelete(row) {
            const ids = row.id || this.ids;
            this.$modal.confirm('是否确认删除选中的数据项?').then(() => {
                this.$modal.msgSuccess("删除成功");
                this.getList();
            }).catch(() => { });
        },
        /** 取消按钮 */
        cancel() {
            this.open = false;
            this.reset();
        }
    }
};
</script>

<style scoped>
.main-content {
    margin-right: 0;
    transition: all 0.3s;
}

.drawer-container {
    padding: 20px;
}

.drawer-item {
    margin-bottom: 20px;
}

.item-img {
    width: 100%;
    height: 200px;
    background: #f5f7fa;
    border-radius: 4px;
    overflow: hidden;
    margin-bottom: 15px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.item-info h3 {
    margin-top: 0;
    margin-bottom: 15px;
    font-size: 18px;
    color: #303133;
}

.info-row {
    display: flex;
    margin-bottom: 10px;
}

.info-row .label {
    color: #909399;
    min-width: 80px;
}

.info-row .value {
    color: #606266;
    flex: 1;
}

h4 {
    font-size: 16px;
    margin-top: 0;
    margin-bottom: 15px;
    color: #303133;
}

.description {
    color: #606266;
    line-height: 1.5;
}

.no-data {
    color: #909399;
    text-align: center;
    padding: 20px 0;
}

.mb8 {
    margin-bottom: 8px;
}
</style>
